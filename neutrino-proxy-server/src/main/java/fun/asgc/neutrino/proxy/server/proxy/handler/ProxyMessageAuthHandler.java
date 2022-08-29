/**
 * Copyright (c) 2022 aoshiguchen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package fun.asgc.neutrino.proxy.server.proxy.handler;

import fun.asgc.neutrino.core.annotation.Autowired;
import fun.asgc.neutrino.core.annotation.Component;
import fun.asgc.neutrino.core.annotation.Match;
import fun.asgc.neutrino.core.annotation.NonIntercept;
import fun.asgc.neutrino.core.util.CollectionUtil;
import fun.asgc.neutrino.core.util.StringUtil;
import fun.asgc.neutrino.proxy.core.*;
import fun.asgc.neutrino.proxy.server.base.proxy.ProxyConfig;
import fun.asgc.neutrino.proxy.server.base.proxy.ProxyServerConfig;
import fun.asgc.neutrino.proxy.server.base.rest.constant.EnableStatusEnum;
import fun.asgc.neutrino.proxy.server.proxy.core.BytesMetricsHandler;
import fun.asgc.neutrino.proxy.server.proxy.core.UserChannelHandler;
import fun.asgc.neutrino.proxy.server.dal.entity.LicenseDO;
import fun.asgc.neutrino.proxy.server.dal.entity.PortMappingDO;
import fun.asgc.neutrino.proxy.server.dal.entity.UserDO;
import fun.asgc.neutrino.proxy.server.service.LicenseService;
import fun.asgc.neutrino.proxy.server.service.PortMappingService;
import fun.asgc.neutrino.proxy.server.service.UserService;
import fun.asgc.neutrino.proxy.server.util.ProxyChannelManager;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.net.BindException;
import java.util.List;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/6/16
 */
@Slf4j
@NonIntercept
@Match(type = Constants.ProxyDataTypeName.AUTH)
@Component
public class ProxyMessageAuthHandler implements ProxyMessageHandler {
	@Autowired("serverBossGroup")
	private NioEventLoopGroup serverBossGroup;
	@Autowired("serverWorkerGroup")
	private NioEventLoopGroup serverWorkerGroup;
	@Autowired
	private ProxyConfig proxyConfig;
	@Autowired
	private LicenseService licenseService;
	@Autowired
	private UserService userService;
	@Autowired
	private PortMappingService portMappingService;

	@Override
	public void handle(ChannelHandlerContext ctx, ProxyMessage proxyMessage) {
		String licenseKey = proxyMessage.getInfo();
		if (StringUtil.isEmpty(licenseKey)) {
			ctx.channel().writeAndFlush(ProxyMessage.buildErrMessage(ExceptionEnum.AUTH_FAILED, "license不能为空!"));
			ctx.channel().close();
			return;
		}
		LicenseDO licenseDO = licenseService.findByKey(licenseKey);
		if (null == licenseDO) {
			ctx.channel().writeAndFlush(ProxyMessage.buildErrMessage(ExceptionEnum.AUTH_FAILED, "license不存在!"));
			ctx.channel().close();
			return;
		}
		if (EnableStatusEnum.DISABLE.getStatus().equals(licenseDO.getEnable())) {
			ctx.channel().writeAndFlush(ProxyMessage.buildErrMessage(ExceptionEnum.AUTH_FAILED, "当前license已被禁用!"));
			ctx.channel().close();
			return;
		}
		UserDO userDO = userService.findById(licenseDO.getId());
		if (null == userDO || EnableStatusEnum.DISABLE.getStatus().equals(userDO.getEnable())) {
			ctx.channel().writeAndFlush(ProxyMessage.buildErrMessage(ExceptionEnum.AUTH_FAILED, "当前license无效!"));
			ctx.channel().close();
			return;
		}
		List<PortMappingDO> portMappingList = portMappingService.findEnableListByLicenseId(licenseDO.getId());
		if (CollectionUtil.isEmpty(portMappingList)) {
			ctx.channel().writeAndFlush(ProxyMessage.buildErrMessage(ExceptionEnum.AUTH_FAILED, "当前license没有可用的端口映射!"));
			ctx.channel().close();
			return;
		}

		ProxyServerConfig.getInstance().addClientConfig(licenseKey, portMappingList);
		List<Integer> ports = ProxyServerConfig.getInstance().getClientInetPorts(licenseKey);
		if (ports == null) {
			ctx.channel().close();
			return;
		}

		Channel channel = ProxyChannelManager.getCmdChannel(licenseKey);
		if (channel != null) {
			ctx.channel().close();
			return;
		}

		ProxyChannelManager.addCmdChannel(ports, licenseKey, ctx.channel());

		startUserPortServer(ports);
	}

	@Override
	public String name() {
		return ProxyDataTypeEnum.AUTH.getDesc();
	}

	private void startUserPortServer(List<Integer> ports) {
		ServerBootstrap bootstrap = new ServerBootstrap();
		bootstrap.group(serverBossGroup, serverWorkerGroup)
			.channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<SocketChannel>() {
			@Override
			public void initChannel(SocketChannel ch) throws Exception {
				ch.pipeline().addFirst(new BytesMetricsHandler());
				ch.pipeline().addLast(new UserChannelHandler());
			}
		});

		for (int port : ports) {
			try {
				bootstrap.bind(port).get();
				log.info("绑定用户端口： {}", port);
			} catch (Exception ex) {
				// BindException表示该端口已经绑定过
				if (!(ex.getCause() instanceof BindException)) {
					throw new RuntimeException(ex);
				}
			}
		}
	}
}

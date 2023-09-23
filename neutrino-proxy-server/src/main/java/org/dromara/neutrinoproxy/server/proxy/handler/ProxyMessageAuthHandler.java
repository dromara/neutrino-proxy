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

package org.dromara.neutrinoproxy.server.proxy.handler;

import cn.hutool.core.util.StrUtil;
import org.dromara.neutrinoproxy.core.*;
import org.dromara.neutrinoproxy.core.*;
import org.dromara.neutrinoproxy.core.dispatcher.Match;
import org.dromara.neutrinoproxy.server.base.proxy.ProxyConfig;
import org.dromara.neutrinoproxy.server.constant.ClientConnectTypeEnum;
import org.dromara.neutrinoproxy.server.constant.EnableStatusEnum;
import org.dromara.neutrinoproxy.server.constant.OnlineStatusEnum;
import org.dromara.neutrinoproxy.server.constant.SuccessCodeEnum;
import org.dromara.neutrinoproxy.server.dal.LicenseMapper;
import org.dromara.neutrinoproxy.server.dal.entity.ClientConnectRecordDO;
import org.dromara.neutrinoproxy.server.dal.entity.LicenseDO;
import org.dromara.neutrinoproxy.server.dal.entity.UserDO;
import org.dromara.neutrinoproxy.server.service.*;
import org.dromara.neutrinoproxy.server.util.ProxyUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;

import java.net.InetSocketAddress;
import java.util.Date;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/6/16
 */
@Slf4j
@Match(type = Constants.ProxyDataTypeName.AUTH)
@Component
public class ProxyMessageAuthHandler implements ProxyMessageHandler {
	@Inject
	private ProxyConfig proxyConfig;
	@Inject
	private LicenseService licenseService;
	@Inject
	private UserService userService;
	@Inject
	private PortMappingService portMappingService;
	@Inject
	private ProxyMutualService proxyMutualService;
	@Inject
	private FlowReportService flowReportService;
	@Inject
	private ClientConnectRecordService clientConnectRecordService;
	@Inject
	private LicenseMapper licenseMapper;
	@Inject
	private VisitorChannelService visitorChannelService;

	@Override
	public void handle(ChannelHandlerContext ctx, ProxyMessage proxyMessage) {
		String ip = ((InetSocketAddress)ctx.channel().remoteAddress()).getAddress().getHostAddress();
		Date now = new Date();

		String info = proxyMessage.getInfo();
		String[] tmp = info.split(",");
		String licenseKey = proxyMessage.getInfo();
		String clientId = "";
		if (tmp.length == 2) {
			licenseKey = tmp[0];
			clientId = tmp[1];
		}

		if (StrUtil.isEmpty(licenseKey)) {
			log.warn("[client connection] license cannot empty info:{} ", info);
			ctx.channel().writeAndFlush(ProxyMessage.buildAuthResultMessage(ExceptionEnum.AUTH_FAILED.getCode(), "license不能为空!", licenseKey));
			ctx.channel().close();
			clientConnectRecordService.add(new ClientConnectRecordDO()
					.setIp(ip)
					.setType(ClientConnectTypeEnum.CONNECT.getType())
					.setMsg(licenseKey)
					.setCode(SuccessCodeEnum.FAIL.getCode())
					.setErr("license cannot empty!")
					.setCreateTime(now)
			);
			return;
		}
		LicenseDO licenseDO = licenseService.findByKey(licenseKey);
		if (null == licenseDO) {
			log.warn("[client connection] license notfound info:{} ", info);
			ctx.channel().writeAndFlush(ProxyMessage.buildAuthResultMessage(ExceptionEnum.AUTH_FAILED.getCode(), "license不存在!", licenseKey));
			ctx.channel().close();
			clientConnectRecordService.add(new ClientConnectRecordDO()
					.setIp(ip)
					.setType(ClientConnectTypeEnum.CONNECT.getType())
					.setMsg(licenseKey)
					.setCode(SuccessCodeEnum.FAIL.getCode())
					.setErr("license notfound!")
					.setCreateTime(now)
			);
			return;
		}
		if (EnableStatusEnum.DISABLE.getStatus().equals(licenseDO.getEnable())) {
			log.warn("[client connection] the license disabled info:{} ", info);
			ctx.channel().writeAndFlush(ProxyMessage.buildAuthResultMessage(ExceptionEnum.AUTH_FAILED.getCode(), "the license disabled!", licenseKey));
			ctx.channel().close();
			clientConnectRecordService.add(new ClientConnectRecordDO()
					.setIp(ip)
					.setLicenseId(licenseDO.getId())
					.setType(ClientConnectTypeEnum.CONNECT.getType())
					.setMsg(licenseKey)
					.setCode(SuccessCodeEnum.FAIL.getCode())
					.setErr("the license disabled!")
					.setCreateTime(now));
			return;
		}
		UserDO userDO = userService.findById(licenseDO.getUserId());
		if (null == userDO || EnableStatusEnum.DISABLE.getStatus().equals(userDO.getEnable())) {
			log.warn("[client connection] the license invalid info:{} ", info);
			ctx.channel().writeAndFlush(ProxyMessage.buildAuthResultMessage(ExceptionEnum.AUTH_FAILED.getCode(), "the license invalid!", licenseKey));
			ctx.channel().close();
			clientConnectRecordService.add(new ClientConnectRecordDO()
					.setIp(ip)
					.setLicenseId(licenseDO.getId())
					.setType(ClientConnectTypeEnum.CONNECT.getType())
					.setMsg(licenseKey)
					.setCode(SuccessCodeEnum.FAIL.getCode())
					.setErr("the license invalid!")
					.setCreateTime(now));
			return;
		}
		Channel cmdChannel = ProxyUtil.getCmdChannelByLicenseId(licenseDO.getId());
		if (null != cmdChannel) {
			String _clientId = ProxyUtil.getClientIdByLicenseId(licenseDO.getId());
			if (!clientId.equals(_clientId)) {
				log.warn("[client connection] the license on another no used info:{} _clientId:{}", info, _clientId);
				ctx.channel().writeAndFlush(ProxyMessage.buildAuthResultMessage(ExceptionEnum.AUTH_FAILED.getCode(), "the license on another no used!", licenseKey));
				ctx.channel().close();
				clientConnectRecordService.add(new ClientConnectRecordDO()
						.setIp(ip)
						.setLicenseId(licenseDO.getId())
						.setType(ClientConnectTypeEnum.CONNECT.getType())
						.setMsg(licenseKey)
						.setCode(SuccessCodeEnum.FAIL.getCode())
						.setErr("the license on another no used!")
						.setCreateTime(now));
				return;
			}
		}
		// 发送认证成功消息
		ctx.channel().writeAndFlush(ProxyMessage.buildAuthResultMessage(ExceptionEnum.SUCCESS.getCode(), "auth success!", licenseKey));

		clientConnectRecordService.add(new ClientConnectRecordDO()
				.setIp(ip)
				.setLicenseId(licenseDO.getId())
				.setType(ClientConnectTypeEnum.CONNECT.getType())
				.setMsg(info)
				.setCode(SuccessCodeEnum.SUCCESS.getCode())
				.setCreateTime(now));

		// 设置当前licenseId对应的客户端ID
		ProxyUtil.setLicenseIdToClientIdMap(licenseDO.getId(), clientId);

		log.warn("[client connection] auth success info:{} ", info);

		// 更新license在线状态
		licenseMapper.updateOnlineStatus(licenseDO.getId(), OnlineStatusEnum.ONLINE.getStatus(), now);
		// 初始化VisitorChannel
		visitorChannelService.initVisitorChannel(licenseDO.getId(), ctx.channel());
	}

	@Override
	public String name() {
		return ProxyDataTypeEnum.AUTH.getDesc();
	}
}

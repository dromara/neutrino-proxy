package org.dromara.neutrinoproxy.server.proxy.handler;

import cn.hutool.core.util.StrUtil;
import org.dromara.neutrinoproxy.core.*;
import org.dromara.neutrinoproxy.core.*;
import org.dromara.neutrinoproxy.core.dispatcher.Match;
import org.dromara.neutrinoproxy.server.constant.EnableStatusEnum;
import org.dromara.neutrinoproxy.server.dal.entity.LicenseDO;
import org.dromara.neutrinoproxy.server.dal.entity.UserDO;
import org.dromara.neutrinoproxy.server.proxy.domain.ProxyAttachment;
import org.dromara.neutrinoproxy.server.service.LicenseService;
import org.dromara.neutrinoproxy.server.service.UserService;
import org.dromara.neutrinoproxy.server.util.ProxyUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/6/16
 */
@Match(type = Constants.ProxyDataTypeName.CONNECT)
@Component
public class ProxyMessageConnectHandler implements ProxyMessageHandler {
	@Inject
	private LicenseService licenseService;
	@Inject
	private UserService userService;

	@Override
	public void handle(ChannelHandlerContext ctx, ProxyMessage proxyMessage) {
		String info = proxyMessage.getInfo();
		if (StrUtil.isEmpty(info)) {
			ctx.channel().writeAndFlush(ProxyMessage.buildErrMessage(ExceptionEnum.CONNECT_FAILED, "info不能为空!"));
			ctx.channel().close();
			return;
		}

		String[] tokens = info.split("@");
		if (tokens.length != 2) {
			ctx.channel().writeAndFlush(ProxyMessage.buildErrMessage(ExceptionEnum.CONNECT_FAILED, "info格式有误!"));
			ctx.channel().close();
			return;
		}
		String visitorId = tokens[0];
		String licenseKey = tokens[1];

		LicenseDO licenseDO = licenseService.findByKey(licenseKey);
		if (null == licenseDO) {
			ctx.channel().writeAndFlush(ProxyMessage.buildErrMessage(ExceptionEnum.CONNECT_FAILED, "license不存在!"));
			ctx.channel().close();
			return;
		}
		if (EnableStatusEnum.DISABLE.getStatus().equals(licenseDO.getEnable())) {
			ctx.channel().writeAndFlush(ProxyMessage.buildErrMessage(ExceptionEnum.CONNECT_FAILED, "当前license已被禁用!"));
			ctx.channel().close();
			return;
		}
		UserDO userDO = userService.findById(licenseDO.getUserId());
		if (null == userDO || EnableStatusEnum.DISABLE.getStatus().equals(userDO.getEnable())) {
			ctx.channel().writeAndFlush(ProxyMessage.buildErrMessage(ExceptionEnum.CONNECT_FAILED, "当前license无效!"));
			ctx.channel().close();
			return;
		}

		Channel cmdChannel = ProxyUtil.getCmdChannelByLicenseId(licenseDO.getId());

		if (null == cmdChannel) {
			ctx.channel().writeAndFlush(ProxyMessage.buildErrMessage(ExceptionEnum.CONNECT_FAILED, "服务端异常，指令通道不存在!"));
			ctx.channel().close();
			return;
		}

		Channel visitorChannel = ProxyUtil.getVisitorChannel(cmdChannel, visitorId);
		if (null == visitorChannel) {
			return;
		}
		ctx.channel().attr(Constants.VISITOR_ID).set(visitorId);
		ctx.channel().attr(Constants.LICENSE_ID).set(licenseDO.getId());
		ctx.channel().attr(Constants.NEXT_CHANNEL).set(visitorChannel);
		visitorChannel.attr(Constants.NEXT_CHANNEL).set(ctx.channel());
		// 代理客户端与后端服务器连接成功，修改用户连接为可读状态
		visitorChannel.config().setOption(ChannelOption.AUTO_READ, true);

		// 获取代理附加对象
		ProxyAttachment proxyAttachment = ProxyUtil.getProxyConnectAttachment(visitorId);
		if (null != proxyAttachment) {
			proxyAttachment.execute();
		}
	}

	@Override
	public String name() {
		return ProxyDataTypeEnum.CONNECT.getDesc();
	}
}

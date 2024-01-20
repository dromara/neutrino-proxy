package org.dromara.neutrinoproxy.client.sdk.handler;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import lombok.extern.slf4j.Slf4j;
import org.dromara.neutrinoproxy.client.sdk.config.ProxyConfig;
import org.dromara.neutrinoproxy.client.sdk.core.ProxyChannelBorrowListener;
import org.dromara.neutrinoproxy.client.sdk.util.ProxyUtil;
import org.dromara.neutrinoproxy.core.Constants;
import org.dromara.neutrinoproxy.core.ProxyDataTypeEnum;
import org.dromara.neutrinoproxy.core.ProxyMessage;
import org.dromara.neutrinoproxy.core.ProxyMessageHandler;
import org.dromara.neutrinoproxy.core.dispatcher.Match;

/**
 * 连接信息处理器
 * @author: aoshiguchen
 * @date: 2022/6/16
 */
@Slf4j
@Match(type = Constants.ProxyDataTypeName.CONNECT)
public class ProxyMessageConnectHandler implements ProxyMessageHandler {

	private Bootstrap tcpProxyTunnelBootstrap;
	private Bootstrap realServerBootstrap;
	private ProxyConfig proxyConfig;

    public ProxyMessageConnectHandler(Bootstrap tcpProxyTunnelBootstrap,Bootstrap realServerBootstrap,ProxyConfig proxyConfig){
        this.proxyConfig=proxyConfig;
        this.realServerBootstrap=realServerBootstrap;
        this.tcpProxyTunnelBootstrap=tcpProxyTunnelBootstrap;
    }

	@Override
	public void handle(ChannelHandlerContext ctx, ProxyMessage proxyMessage) {
		final Channel cmdChannel = ctx.channel();
		final String visitorId = proxyMessage.getInfo();
		String[] serverInfo = new String(proxyMessage.getData()).split(":");
		String ip = serverInfo[0];
		int port = Integer.parseInt(serverInfo[1]);
		// 连接真实的、被代理的服务
		realServerBootstrap.connect(ip, port).addListener(new ChannelFutureListener() {

			@Override
			public void operationComplete(ChannelFuture future) throws Exception {

				// 连接后端服务器成功
				if (future.isSuccess()) {
					final Channel realServerChannel = future.channel();

					realServerChannel.config().setOption(ChannelOption.AUTO_READ, false);

					// 获取连接
					ProxyUtil.borrowTcpProxyChanel(tcpProxyTunnelBootstrap, new ProxyChannelBorrowListener() {

						@Override
						public void success(Channel channel) {
							// 连接绑定
							channel.attr(Constants.NEXT_CHANNEL).set(realServerChannel);
							realServerChannel.attr(Constants.NEXT_CHANNEL).set(channel);

							// 远程绑定
							channel.writeAndFlush(ProxyMessage.buildConnectMessage(visitorId + "@" + proxyConfig.getTunnel().getLicenseKey()));

							realServerChannel.config().setOption(ChannelOption.AUTO_READ, true);
							ProxyUtil.addRealServerChannel(visitorId, realServerChannel);
							ProxyUtil.setRealServerChannelVisitorId(realServerChannel, visitorId);
						}

						@Override
						public void error(Throwable cause) {
							ProxyMessage proxyMessage = new ProxyMessage();
							proxyMessage.setType(ProxyMessage.TYPE_DISCONNECT);
							proxyMessage.setInfo(visitorId);
							cmdChannel.writeAndFlush(proxyMessage);
						}
					});

				} else {
					cmdChannel.writeAndFlush(ProxyMessage.buildDisconnectMessage(visitorId));
				}
			}
		});
	}

	@Override
	public String name() {
		return ProxyDataTypeEnum.CONNECT.getDesc();
	}

}

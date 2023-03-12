package fun.asgc.neutrino.proxy.client.handler;

import fun.asgc.neutrino.proxy.client.config.ProxyConfig;
import fun.asgc.neutrino.proxy.client.core.ProxyChannelBorrowListener;
import fun.asgc.neutrino.proxy.client.util.ProxyUtil;
import fun.asgc.neutrino.proxy.core.Constants;
import fun.asgc.neutrino.proxy.core.ProxyDataTypeEnum;
import fun.asgc.neutrino.proxy.core.ProxyMessage;
import fun.asgc.neutrino.proxy.core.ProxyMessageHandler;
import fun.asgc.neutrino.proxy.core.dispatcher.Match;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;

/**
 * 连接信息处理器
 * @author: aoshiguchen
 * @date: 2022/6/16
 */
@Match(type = Constants.ProxyDataTypeName.CONNECT)
@Component
public class ProxyMessageConnectHandler implements ProxyMessageHandler {
	@Inject("bootstrap")
	private Bootstrap bootstrap;
	@Inject("realServerBootstrap")
	private Bootstrap realServerBootstrap;
	@Inject
	private ProxyConfig proxyConfig;

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
					ProxyUtil.borrowProxyChanel(bootstrap, new ProxyChannelBorrowListener() {

						@Override
						public void success(Channel channel) {
							// 连接绑定
							channel.attr(Constants.NEXT_CHANNEL).set(realServerChannel);
							realServerChannel.attr(Constants.NEXT_CHANNEL).set(channel);

							// 远程绑定
							channel.writeAndFlush(ProxyMessage.buildConnectMessage(visitorId + "@" + proxyConfig.getClient().getLicenseKey()));

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

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
package fun.asgc.neutrino.core.web;

import fun.asgc.neutrino.core.annotation.Autowired;
import fun.asgc.neutrino.core.annotation.Component;
import fun.asgc.neutrino.core.annotation.Destroy;
import fun.asgc.neutrino.core.annotation.Init;
import fun.asgc.neutrino.core.context.ApplicationConfig;
import fun.asgc.neutrino.core.context.ApplicationRunner;
import fun.asgc.neutrino.core.util.NumberUtil;
import fun.asgc.neutrino.core.util.StringUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * web服务
 * @author: aoshiguchen
 * @date: 2022/7/9
 */
@Slf4j
@Component
public class WebApplicationServer implements ApplicationRunner {
	@Autowired
	private ApplicationConfig rootApplicationConfig;
	private EventLoopGroup bossGroup;
	private EventLoopGroup workerGroup;
	private ServerBootstrap serverBootstrap;
	private ChannelFuture channelFuture;
	private byte[] faviconBytes;

	@Init
	public void init() {
		ApplicationConfig.Http http = rootApplicationConfig.getHttp();
		if (StringUtil.notEmpty(http.getMaxContentLengthDesc()) && null == http.getMaxContentLength()) {
			http.setMaxContentLength(NumberUtil.descriptionToSize(http.getMaxContentLengthDesc(), 64 * 1024));
		}

		this.bossGroup = new NioEventLoopGroup();
		this.workerGroup = new NioEventLoopGroup();
		this.serverBootstrap = new ServerBootstrap();
	}

	@Override
	public void run(String[] args) throws Exception {
		ApplicationConfig.Http http = rootApplicationConfig.getHttp();
		this.serverBootstrap.group(bossGroup, workerGroup)
			.channel(NioServerSocketChannel.class)
			.option(ChannelOption.SO_BACKLOG, 1024)
			.childOption(ChannelOption.TCP_NODELAY, true)
			.childOption(ChannelOption.SO_KEEPALIVE, true)
			.childOption(ChannelOption.RCVBUF_ALLOCATOR, new AdaptiveRecvByteBufAllocator())
			.childHandler(new ChannelInitializer<SocketChannel>() {
			@Override
			protected void initChannel(SocketChannel ch) throws Exception {
				ChannelPipeline pipeline = ch.pipeline();
				pipeline.addLast(new HttpServerCodec());
				pipeline.addLast(new ChunkedWriteHandler());
				pipeline.addLast(new HttpObjectAggregator(http.getMaxContentLength().intValue()));
				pipeline.addLast(new SimpleChannelInboundHandler<FullHttpRequest>() {
					@Override
					protected void channelRead0(ChannelHandlerContext channelHandlerContext, FullHttpRequest request) throws Exception {
						String uri = request.uri();
						log.info("request: {}", uri);
					}
				});
			}
		});
		channelFuture = this.serverBootstrap.bind(http.getPort()).sync();
		log.info("HTTP服务启动，端口：{} context-path：{}", http.getPort(), http.getContextPath());
	}

	@Destroy
	public void destroy() {
		this.channelFuture.channel().close();
	}
}

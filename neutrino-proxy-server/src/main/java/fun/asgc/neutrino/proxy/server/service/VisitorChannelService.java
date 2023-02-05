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
package fun.asgc.neutrino.proxy.server.service;

import fun.asgc.neutrino.core.annotation.Autowired;
import fun.asgc.neutrino.core.annotation.Component;
import fun.asgc.neutrino.core.annotation.NonIntercept;
import fun.asgc.neutrino.core.util.CollectionUtil;
import fun.asgc.neutrino.proxy.server.dal.entity.PortMappingDO;
import fun.asgc.neutrino.proxy.server.proxy.core.BytesMetricsHandler;
import fun.asgc.neutrino.proxy.server.proxy.core.VisitorChannelHandler;
import fun.asgc.neutrino.proxy.server.proxy.domain.CmdChannelAttachInfo;
import fun.asgc.neutrino.proxy.server.proxy.domain.ProxyMapping;
import fun.asgc.neutrino.proxy.server.util.ProxyUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.net.BindException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 访问者通道服务
 * @author: aoshiguchen
 * @date: 2023/2/5
 */
@Slf4j
@NonIntercept
@Component
public class VisitorChannelService {
    @Autowired("serverBossGroup")
    private NioEventLoopGroup serverBossGroup;
    @Autowired("serverWorkerGroup")
    private NioEventLoopGroup serverWorkerGroup;
    @Autowired
    private PortMappingService portMappingService;
    @Autowired
    private ProxyMutualService proxyMutualService;

    /**
     * 初始化
     * @param licenseId
     */
    public void initVisitorChannel(Integer licenseId, Channel cmdChannel) {
        List<PortMappingDO> portMappingList = portMappingService.findEnableListByLicenseId(licenseId);
        // 没有端口映射仍然保持连接
        ProxyUtil.initProxyInfo(licenseId, ProxyMapping.buildList(portMappingList));

        ProxyUtil.addCmdChannel(licenseId, cmdChannel, portMappingList.stream().map(PortMappingDO::getServerPort).collect(Collectors.toSet()));

        startUserPortServer(ProxyUtil.getAttachInfo(cmdChannel), portMappingList);
    }

    /**
     * 更新
     * 触发时机：新增端口映射、修改端口映射、删除端口映射、禁用端口映射、启用端口映射、禁用license、启用license、禁用用户、启用用户
     * @param licenseId
     */
    public void UpdateVisitorChannel(Integer licenseId) {

    }

    private void startUserPortServer(CmdChannelAttachInfo cmdChannelAttachInfo, List<PortMappingDO> portMappingList) {
        if (CollectionUtil.isEmpty(portMappingList)) {
            return;
        }
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(serverBossGroup, serverWorkerGroup)
                .channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addFirst(new BytesMetricsHandler());
                        ch.pipeline().addLast(new VisitorChannelHandler());
                    }
                });

        for (PortMappingDO portMapping : portMappingList) {
            try {
                proxyMutualService.bindServerPort(cmdChannelAttachInfo, portMapping.getServerPort());
                bootstrap.bind(portMapping.getServerPort()).get();
                log.info("绑定用户端口： {}", portMapping.getServerPort());
            } catch (Exception ex) {
                // BindException表示该端口已经绑定过
                if (!(ex.getCause() instanceof BindException)) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }
}

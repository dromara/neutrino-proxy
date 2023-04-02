package org.dromara.neutrinoproxy.server.service;

import cn.hutool.core.collection.CollectionUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.dromara.neutrinoproxy.core.Constants;
import org.dromara.neutrinoproxy.server.constant.EnableStatusEnum;
import org.dromara.neutrinoproxy.server.dal.LicenseMapper;
import org.dromara.neutrinoproxy.server.dal.PortMappingMapper;
import org.dromara.neutrinoproxy.server.dal.PortPoolMapper;
import org.dromara.neutrinoproxy.server.dal.UserMapper;
import org.dromara.neutrinoproxy.server.dal.entity.LicenseDO;
import org.dromara.neutrinoproxy.server.dal.entity.PortMappingDO;
import org.dromara.neutrinoproxy.server.dal.entity.PortPoolDO;
import org.dromara.neutrinoproxy.server.dal.entity.UserDO;
import org.dromara.neutrinoproxy.server.proxy.core.BytesMetricsHandler;
import org.dromara.neutrinoproxy.server.proxy.core.VisitorChannelHandler;
import org.dromara.neutrinoproxy.server.proxy.domain.CmdChannelAttachInfo;
import org.dromara.neutrinoproxy.server.proxy.domain.ProxyMapping;
import org.dromara.neutrinoproxy.server.util.ProxyUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.solon.annotation.Db;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;

import java.net.BindException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 访问者通道服务
 * @author: aoshiguchen
 * @date: 2023/2/5
 */
@Slf4j
@Component
public class VisitorChannelService {
    @Inject("serverBossGroup")
    private NioEventLoopGroup serverBossGroup;
    @Inject("serverWorkerGroup")
    private NioEventLoopGroup serverWorkerGroup;
    @Inject
    private ProxyMutualService proxyMutualService;
    @Db
    private UserMapper userMapper;
    @Db
    private LicenseMapper licenseMapper;
    @Db
    private PortMappingMapper portMappingMapper;
    @Db
    private PortPoolMapper portPoolMapper;

    /**
     * 初始化
     * @param licenseId
     */
    public void initVisitorChannel(Integer licenseId, Channel cmdChannel) {
        List<PortMappingDO> portMappingList = portMappingMapper.findEnableListByLicenseId(licenseId);
        // 没有端口映射仍然保持连接
        ProxyUtil.initProxyInfo(licenseId, ProxyMapping.buildList(portMappingList));

        ProxyUtil.addCmdChannel(licenseId, cmdChannel, portMappingList.stream().map(PortMappingDO::getServerPort).collect(Collectors.toSet()));

        startUserPortServer(ProxyUtil.getAttachInfo(cmdChannel), portMappingList);
    }

    /**
     * 更新
     * 触发时机：删除端口池、禁用端口池、启用端口池
     * @param serverPort
     * @param enable
     */
    public void updateVisitorChannelByPortPool(Integer serverPort, Integer enable) {
        if (null == serverPort) {
            return;
        }
        List<PortMappingDO> portMappingDOList = portMappingMapper.findListByServerPort(serverPort);
        if (CollectionUtil.isEmpty(portMappingDOList)) {
            return;
        }
        EnableStatusEnum enableStatusEnum = EnableStatusEnum.of(enable);
        for (PortMappingDO portMappingDO : portMappingDOList) {
            if (EnableStatusEnum.DISABLE == enableStatusEnum) {
                removeVisitorChannelByPortMapping(portMappingDO);
            } else if (EnableStatusEnum.ENABLE == EnableStatusEnum.of(portMappingDO.getEnable())) {
                addVisitorChannelByPortMapping(portMappingDO);
            }
        }
    }

    /**
     * 更新
     * 触发时机：删除用户、禁用用户、启用用户 （新增、修改用户不涉及VisitorChannel的变更）
     * @param userId
     */
    public void updateVisitorChannelByUserId(Integer userId, Integer enable) {
        if (null == userId) {
            return;
        }
        List<LicenseDO> licenseDOList = licenseMapper.listByUserId(userId);
        if (CollectionUtil.isEmpty(licenseDOList)) {
            return;
        }
        for (LicenseDO licenseDO : licenseDOList) {
            updateVisitorChannelByLicenseId(licenseDO.getId(), enable);
        }
    }

    /**
     * 更新
     * 触发时机：删除license、禁用license、启用license （新增、修改license不涉及VisitorChannel的变更）
     * 重置licenseKey，不会立即影响已经连接成功的license，如果想要立即影响，请先进行禁用
     * @param licenseId
     */
    public void updateVisitorChannelByLicenseId(Integer licenseId, Integer enable) {
        if (null == licenseId) {
            return;
        }
        Channel cmdChannel = ProxyUtil.getCmdChannelByLicenseId(licenseId);
        if (null == cmdChannel) {
            // 如果不存在有效的cmdChannel，则无需更新VisitorChannel
            return;
        }
        EnableStatusEnum enableStatusEnum = EnableStatusEnum.of(enable);
        List<PortMappingDO> portMappingDOList = portMappingMapper.findListByLicenseId(licenseId);
        if (!CollectionUtil.isEmpty(portMappingDOList)) {
            for (PortMappingDO portMappingDO : portMappingDOList) {
                if (EnableStatusEnum.DISABLE == enableStatusEnum) {
                    removeVisitorChannelByPortMapping(portMappingDO);
                } else if (EnableStatusEnum.ENABLE == EnableStatusEnum.of(portMappingDO.getEnable())) {
                    addVisitorChannelByPortMapping(portMappingDO);
                }
            }
        }
    }

    /**
     * 更新
     * 触发时机：修改端口映射
     * @param oldPortMappingDO
     * @param newPortMappingDO
     */
    public void updateVisitorChannelByPortMapping(PortMappingDO oldPortMappingDO, PortMappingDO newPortMappingDO) {
        if (null == oldPortMappingDO || null == newPortMappingDO) {
            return;
        }
        removeVisitorChannelByPortMapping(oldPortMappingDO);
        addVisitorChannelByPortMapping(newPortMappingDO);
    }

    /**
     * 新增VisitorChannel
     * 触发时机：新增端口映射、启用端口映射
     * @param portMappingDO
     */
    public void addVisitorChannelByPortMapping(PortMappingDO portMappingDO) {
        if (null == portMappingDO) {
            return;
        }
        Channel cmdChannel = ProxyUtil.getCmdChannelByLicenseId(portMappingDO.getLicenseId());
        if (null == cmdChannel) {
            // 如果不存在有效的cmdChannel，则无需更新VisitorChannel
            return;
        }
        // 判断端口映射是否启用
        if (EnableStatusEnum.DISABLE != EnableStatusEnum.of(portMappingDO.getEnable())) {
            LicenseDO licenseDO = licenseMapper.findById(portMappingDO.getLicenseId());
            // 判断license是否启用
            if (null != licenseDO && EnableStatusEnum.ENABLE == EnableStatusEnum.of(licenseDO.getEnable())) {
                UserDO userDO = userMapper.findById(licenseDO.getUserId());
                // 判断用户是否启用
                if (null != userDO && EnableStatusEnum.ENABLE == EnableStatusEnum.of(userDO.getEnable())) {
                    PortPoolDO portPoolDO = portPoolMapper.findByPort(portMappingDO.getServerPort());
                    // 判断端口池是否启用
                    if (null != portPoolDO && EnableStatusEnum.ENABLE == EnableStatusEnum.of(portPoolDO.getEnable())) {
                        // 未删除且未禁用，则开启代理
                        ProxyUtil.addProxyInfo(portMappingDO.getLicenseId(), ProxyMapping.build(portMappingDO));
                        ProxyUtil.addCmdChannel(portMappingDO.getLicenseId(), cmdChannel, Sets.newHashSet(portMappingDO.getServerPort()));
                        startUserPortServer(ProxyUtil.getAttachInfo(cmdChannel), Lists.newArrayList(portMappingDO));
                    }
                }
            }
        }
    }

    /**
     * 删除VisitorChannel
     * 触发时机：删除端口映射、禁用端口映射
     * @param portMappingDO
     */
    public void removeVisitorChannelByPortMapping(PortMappingDO portMappingDO) {
        if (null == portMappingDO) {
            return;
        }
        Channel cmdChannel = ProxyUtil.getCmdChannelByLicenseId(portMappingDO.getLicenseId());
        if (null == cmdChannel) {
            // 如果不存在有效的cmdChannel，则无需更新VisitorChannel
            return;
        }
        Channel visitorChannel = ProxyUtil.getVisitorChannelByServerPort(portMappingDO.getServerPort());
        if (null != visitorChannel) {
            Channel proxyChannel = visitorChannel.attr(Constants.NEXT_CHANNEL).get();
            if (null != proxyChannel) {
                proxyChannel.close();
            }
            visitorChannel.close();
        }
        ProxyUtil.removeProxyInfo(portMappingDO.getServerPort());
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

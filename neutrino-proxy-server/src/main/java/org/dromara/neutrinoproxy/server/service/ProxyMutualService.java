package org.dromara.neutrinoproxy.server.service;

import org.dromara.neutrinoproxy.server.constant.OnlineStatusEnum;
import org.dromara.neutrinoproxy.server.dal.LicenseMapper;
import org.dromara.neutrinoproxy.server.dal.PortMappingMapper;
import org.dromara.neutrinoproxy.server.proxy.domain.CmdChannelAttachInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.solon.annotation.Db;
import org.noear.solon.annotation.Component;

import java.util.Date;

/**
 * 代理交互服务
 * @author: aoshiguchen
 * @date: 2022/9/3
 */
@Slf4j
@Component
public class ProxyMutualService {
	@Db
	private PortMappingMapper portMappingMapper;
	@Db
	private LicenseMapper licenseMapper;

	/**
	 * 绑定服务端端口处理
	 * @param attachInfo
	 * @param serverPort
	 */
	public void bindServerPort(CmdChannelAttachInfo attachInfo, Integer serverPort) {
		Date now = new Date();
		portMappingMapper.updateOnlineStatus(attachInfo.getLicenseId(), serverPort, OnlineStatusEnum.ONLINE.getStatus(), now);
		licenseMapper.updateOnlineStatus(attachInfo.getLicenseId(), OnlineStatusEnum.ONLINE.getStatus(), now);
		log.info("bind server port licenseId:{},ip:{},serverPort:{}", attachInfo.getLicenseId(), attachInfo.getIp(),  serverPort);
	}

	/**
	 * 客户端下线
	 * @param attachInfo
	 */
	public void offline(CmdChannelAttachInfo attachInfo) {
		Date now = new Date();
		portMappingMapper.updateOnlineStatus(attachInfo.getLicenseId(), OnlineStatusEnum.OFFLINE.getStatus(), now);
		licenseMapper.updateOnlineStatus(attachInfo.getLicenseId(), OnlineStatusEnum.OFFLINE.getStatus(), now);
		log.info("client offline licenseId:{},ip:{}", attachInfo.getLicenseId(), attachInfo.getIp());
	}

}

package org.dromara.neutrinoproxy.server.proxy.domain;

import cn.hutool.core.collection.CollectionUtil;
import org.dromara.neutrinoproxy.server.dal.entity.PortMappingDO;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/8/30
 */
@Accessors(chain = true)
@Data
public class ProxyMapping {
    /**
     * licenseId
     */
    private Integer licenseId;
	/**
	 * 服务端端口
	 */
	private Integer serverPort;
	/**
	 * 客户端信息 IP:port
	 */
	private String lanInfo;
    /**
     * 协议
     */
    private String protocal;


    public ProxyMapping() {
    }

    public ProxyMapping(Integer licenseId, Integer serverPort, String lanInfo, String protocal) {
        this.licenseId = licenseId;
        this.serverPort = serverPort;
        this.lanInfo = lanInfo;
        this.protocal = protocal;
    }

    public static List<ProxyMapping> buildList(List<PortMappingDO> portMappingList) {
		List<ProxyMapping> list = new ArrayList<>();
		if (CollectionUtil.isEmpty(portMappingList)) {
			return list;
		}
		for (PortMappingDO portMapping : portMappingList) {
			list.add(build(portMapping));
		}
		return list;
	}

	public static ProxyMapping build(PortMappingDO portMappingDO) {
		return new ProxyMapping()
            .setLicenseId(portMappingDO.getLicenseId())
            .setServerPort(portMappingDO.getServerPort())
            .setLanInfo(String.format("%s:%s", portMappingDO.getClientIp(), portMappingDO.getClientPort()))
            .setProtocal(portMappingDO.getProtocal());
	}



}


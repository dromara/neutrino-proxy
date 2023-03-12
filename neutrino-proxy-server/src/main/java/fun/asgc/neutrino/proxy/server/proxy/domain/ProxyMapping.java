package fun.asgc.neutrino.proxy.server.proxy.domain;

import cn.hutool.core.collection.CollectionUtil;
import fun.asgc.neutrino.proxy.server.dal.entity.PortMappingDO;
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
	 * 服务端端口
	 */
	private Integer serverPort;
	/**
	 * 客户端信息 IP:port
	 */
	private String lanInfo;

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
				.setServerPort(portMappingDO.getServerPort())
				.setLanInfo(String.format("%s:%s", portMappingDO.getClientIp(), portMappingDO.getClientPort()));
	}
}


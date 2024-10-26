package org.dromara.neutrinoproxy.server.proxy.domain;

import io.netty.channel.Channel;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Map;
import java.util.Set;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/8/30
 */
@Accessors(chain = true)
@Data
public class CmdChannelAttachInfo {
	/**
	 * 用户通道映射
	 */
	private Map<String, Channel> visitorChannelMap;
	/**
	 * 服务端端口集合
	 */
	private Set<Integer> serverPorts;
	/**
	 * licenseId
	 */
	private Integer licenseId;
	/**
	 * ip
	 */
	private String ip;
}

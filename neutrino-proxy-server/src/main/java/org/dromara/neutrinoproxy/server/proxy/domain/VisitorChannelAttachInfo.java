package org.dromara.neutrinoproxy.server.proxy.domain;

import lombok.Data;
import lombok.experimental.Accessors;
import org.dromara.neutrinoproxy.server.constant.NetworkProtocolEnum;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/8/30
 */
@Accessors(chain = true)
@Data
public class VisitorChannelAttachInfo {
	private NetworkProtocolEnum protocol;
	private String visitorId;
	private String lanInfo;
	private Integer serverPort;
	/**
	 * licenseId
	 */
	private Integer licenseId;
	/**
	 * ip地址
	 */
	private String ip;
}

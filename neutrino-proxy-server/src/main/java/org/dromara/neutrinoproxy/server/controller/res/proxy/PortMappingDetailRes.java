package org.dromara.neutrinoproxy.server.controller.res.proxy;

import org.dromara.neutrinoproxy.server.constant.EnableStatusEnum;
import org.dromara.neutrinoproxy.server.constant.OnlineStatusEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 端口映射详情响应
 * @author: aoshiguchen
 * @date: 2022/8/8
 */
@Accessors(chain = true)
@Data
public class PortMappingDetailRes {
	private Integer id;
	/**
	 * licenseId
	 */
	private Integer licenseId;
	/**
	 * license名称
	 */
	private String licenseName;
	/**
	 * 用户ID
	 */
	private Integer userId;
	/**
	 * 用户名称
	 */
	private String userName;
	/**
	 * 服务端端口
	 */
	private Integer serverPort;
	/**
	 * 客户端ip
	 */
	private String clientIp;
	/**
	 * 客户端端口
	 */
	private Integer clientPort;
	/**
	 * 是否在线
	 * {@link OnlineStatusEnum}
	 */
	private Integer isOnline;
	/**
	 * 代理响应数量（响应数据包数量，如果没有拆包则等于数据条数）
	 */
	private Integer proxyResponses;
	/**
	 * 代理超时时间
	 */
	private Long proxyTimeoutMs;
	/**
	 * 启用状态
	 * {@link EnableStatusEnum}
	 */
	private Integer enable;
	/**
	 * 创建时间
	 */
	private Date createTime;
	/**
	 * 更新时间
	 */
	private Date updateTime;
}

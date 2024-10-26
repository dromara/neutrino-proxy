package org.dromara.neutrinoproxy.server.controller.res.system;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 端口池列表响应
 * @author: aoshiguchen
 * @date: 2022/8/7
 */
@Accessors(chain = true)
@Data
public class PortPoolListRes {
	private Integer id;
	/**
	 * 端口
	 */
	private Integer port;
	/**
	 * 是否禁用
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

	/**
	 * 分组类型
	 */
	private Integer possessorType;

	/**
	 * 分组
	 */
	private String groupName;
	/**
	 * 分组ID
	 */
	private Integer groupId;
}

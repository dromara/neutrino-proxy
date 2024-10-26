package org.dromara.neutrinoproxy.server.base.rest;

import org.dromara.neutrinoproxy.server.dal.entity.UserDO;
import lombok.Data;
import lombok.experimental.Accessors;
import org.noear.solon.core.handle.Action;

import java.util.Date;

/**
 * 系统上下文
 * @author: aoshiguchen
 * @date: 2022/8/2
 */
@Accessors(chain = true)
@Data
public class SystemContext {
	/**
	 * 当前用户
	 */
	private UserDO user;
	/**
	 * 鉴权token
	 */
	private String token;
	/**
	 * 客户端ip
	 */
	private String ip;
	/**
	 * 接收请求时间
	 */
	private Date receiveTime;
	private Action action;
}

package org.dromara.neutrinoproxy.server.base.rest;

import org.dromara.neutrinoproxy.server.dal.entity.UserDO;

/**
 * 系统上下文持有者
 * @author: aoshiguchen
 * @date: 2022/8/2
 */
public class SystemContextHolder {
	private static final ThreadLocal<SystemContext> systemContextHolder = new ThreadLocal<>();

	public static void remove() {
		systemContextHolder.remove();
	}

	public static void set(SystemContext systemContext) {
		systemContextHolder.set(systemContext);
	}

	public static UserDO getUser() {
		SystemContext context = getContext();
		return (null == context) ? null : context.getUser();
	}

	public static Integer getUserId() {
		UserDO userDO = getUser();
		return (null == userDO) ? null : userDO.getId();
	}

	public static String getToken() {
		return systemContextHolder.get().getToken();
	}

	public static String getIp() {
		return systemContextHolder.get().getIp();
	}

	public static SystemContext getContext() {
		return systemContextHolder.get();
	}

	public static boolean isAdmin() {
		UserDO userDO = getUser();
		if (null != userDO && userDO.getLoginName().equals("admin")) {
			return true;
		}
		return false;
	}
}

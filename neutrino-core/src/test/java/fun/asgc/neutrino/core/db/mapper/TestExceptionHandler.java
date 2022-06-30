/**
 * Copyright (C) 2018-2022 Zeyi information technology (Shanghai) Co., Ltd.
 * <p>
 * All right reserved.
 * <p>
 * This software is the confidential and proprietary
 * information of Zeyi Company of China.
 * ("Confidential Information"). You shall not disclose
 * such Confidential Information and shall use it only
 * in accordance with the terms of the contract agreement
 * you entered into with Zeyi inc.
 */
package fun.asgc.neutrino.core.db.mapper;

import fun.asgc.neutrino.core.aop.interceptor.ExceptionHandler;
import lombok.extern.slf4j.Slf4j;

import java.sql.SQLException;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/6/30
 */
@Slf4j
public class TestExceptionHandler implements ExceptionHandler {

	/**
	 * 此处若拦截成功，则全局异常拦截不会执行
	 * @param e
	 * @return
	 */
	@Override
	public boolean support(Exception e) {
//		return e instanceof SQLException;
		return false;
	}

	@Override
	public Object handle(Exception e) {
		log.error("SQL执行异常", e);
		return null;
	}
}

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

import fun.asgc.neutrino.core.annotation.Component;
import fun.asgc.neutrino.core.aop.interceptor.ExceptionHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * 全局异常处理
 * @author: aoshiguchen
 * @date: 2022/6/30
 */
@Slf4j
@Component
public class TestGlobalExceptionHandler implements ExceptionHandler {

	@Override
	public boolean support(Exception e) {
		return true;
	}

	@Override
	public Object handle(Exception e) {
		log.error("全局异常 {}" + this.hashCode(), e);
		return null;
	}
}

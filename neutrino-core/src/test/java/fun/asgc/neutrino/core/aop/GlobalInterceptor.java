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
package fun.asgc.neutrino.core.aop;

import fun.asgc.neutrino.core.aop.interceptor.Interceptor;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author: wen.y
 * @date: 2022/6/27
 */
@Slf4j
public class GlobalInterceptor implements Interceptor {

	@Override
	public void intercept(Invocation inv) {
		log.info("全局拦截器1 class:{} method:{} args:{} before", inv.getTargetClass().getName(), inv.getTargetMethod().getName(), inv.getArgs());
		inv.invoke();
		log.info("全局拦截器1 class:{} method:{} args:{} after", inv.getTargetClass().getName(), inv.getTargetMethod().getName(), inv.getArgs());
	}

}

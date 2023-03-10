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
package fun.asgc.neutrino.proxy.server.base.quartz;

import fun.asgc.neutrino.core.exception.InternalException;

/**
 *
 * @author: wen.y
 * @date: 2022/9/4
 */
public class JobException extends InternalException {

	public JobException(String message) {
		super(message);
	}

	public JobException(String message, Throwable cause) {
		super(message, cause);
	}
}

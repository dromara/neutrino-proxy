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

/**
 *
 * @author: wen.y
 * @date: 2022/6/23
 */
@Intercept({DogInterceptor.class})
public class Dog {
	public Dog() {
		System.out.println("狗出生");
	}
	public void call() {
		System.out.println("汪汪汪");
	}

	public String say(String msg) {
		return "狗说:" + msg;
	}
}

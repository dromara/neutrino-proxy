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
 * @date: 2022/6/24
 */
public class Cat {
	public Cat() {
		System.out.println("猫出生");
	}

	public void climb() {
		System.out.println("猫在爬");
	}

	@Intercept(TestInterceptor2.class)
	public int calc(int x, int y) {
		System.out.println("猫在计算");
		return x + y;
	}
}

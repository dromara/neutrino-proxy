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

import org.junit.Test;

/**
 *
 * @author: wen.y
 * @date: 2022/6/24
 */
public class Test1 {

	@Test
	public void dogCall() {
		Dog dog = Aop.get(Dog.class);
		dog.call();
	}

	@Test
	public void dogSay() {
		Dog dog = Aop.get(Dog.class);
		System.out.println(dog.say("hello"));
	}
}

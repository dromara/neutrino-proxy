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
package fun.asgc.neutrino.core.util;

import org.junit.Test;

/**
 *
 * @author: wen.y
 * @date: 2022/6/20
 */
public class SystemUtilTest {

	@Test
	public void run() {
		ThreadUtil.run(() -> {
			for (int i = 0; i < 10; i ++) {
				try {
					System.out.println(i);
					Thread.sleep(1000);
				} catch (Exception e) {

				}
			}
		});

		SystemUtil.waitProcessDestroy(() -> System.out.println("进程销毁")).sync();
	}

}

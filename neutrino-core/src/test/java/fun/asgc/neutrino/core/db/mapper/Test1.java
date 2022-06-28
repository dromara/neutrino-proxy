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

import com.alibaba.fastjson.JSONObject;
import fun.asgc.neutrino.core.annotation.Autowired;
import fun.asgc.neutrino.core.annotation.Component;
import fun.asgc.neutrino.core.runner.ApplicationRunner;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/6/28
 */
@Slf4j
@Component
public class Test1 implements ApplicationRunner {
	@Autowired
	private UserMapper userMapper;

	@Override
	public void run(String[] args) {
		User user = userMapper.findOneById(1L);
		log.info("查询结果:{}", JSONObject.toJSONString(user));
	}
}

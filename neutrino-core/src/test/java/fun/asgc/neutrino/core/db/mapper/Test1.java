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
import fun.asgc.neutrino.core.annotation.Init;
import fun.asgc.neutrino.core.aop.interceptor.ExceptionHandler;
import fun.asgc.neutrino.core.aop.interceptor.InnerGlobalInterceptor;
import fun.asgc.neutrino.core.db.template.JdbcTemplateTest;
import fun.asgc.neutrino.core.runner.ApplicationRunner;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.List;

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
	@Autowired
	private TestGlobalExceptionHandler testGlobalExceptionHandler;

	public Test1() {
		System.out.println("aaa");
	}

	@Init
	public void init() {
		log.info("初始化，注册全局异常拦截器{}...", testGlobalExceptionHandler.hashCode());
		InnerGlobalInterceptor.registerExceptionHandler(testGlobalExceptionHandler);
	}

	@Override
	public void run(String[] args) {
		User user = userMapper.findOneById(1L);
		log.info("查询结果1:{}", JSONObject.toJSONString(user));
		List<User> userList = userMapper.findAll();
		log.info("查询结果2:{}", JSONObject.toJSONString(userList));
		log.info("查询结果3:{}", userMapper.count());

		User user2 = new User();
		user2.setId(6L);
		user2.setName("李八");
		user2.setAge(24);
		user2.setEmail("liba@qq.com");
		user2.setSex("男");
		user2.setCreateTime(new Date());
		try {
			System.out.println(userMapper.add(user2));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

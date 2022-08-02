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
package fun.asgc.neutrino.proxy.server.dal.entity;

import fun.asgc.neutrino.core.db.annotation.Id;
import fun.asgc.neutrino.core.db.annotation.Table;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/8/2
 */
@ToString
@Data
@Accessors(chain = true)
@Table("user_login_record")
public class UserLoginRecordDO {
	/**
	 * 类型 - 登录
	 */
	public static final Integer TYPE_LOGIN = 1;
	/**
	 * 类型 - 登出
	 */
	public static final Integer TYPE_LOGOUT = 2;

	@Id
	private Integer id;
	/**
	 * 用户ID
	 */
	private Integer userId;
	/**
	 * token
	 */
	private String token;
	/**
	 * ip
	 */
	private String ip;
	/**
	 * 类型
	 */
	private Integer type;
	/**
	 * 创建时间
	 */
	private Date createTime;
}

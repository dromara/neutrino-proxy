/**
 * Copyright (c) 2022 aoshiguchen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.dromara.neutrinoproxy.server.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 异常常量枚举
 * @author: aoshiguchen
 * @date: 2022/7/31
 */
@Getter
@AllArgsConstructor
public enum ExceptionConstant {
	SUCCESS(0, "成功"),
	USER_NOT_LOGIN(1, "用户未登录"),
	PARAMS_INVALID(2, "参数不正确"),
	USER_NAME_OR_PASSWORD_ERROR(3, "用户名或密码错误"),
	USER_DISABLE(4, "当前用户已被禁止登录"),
	NO_PERMISSION_VISIT(5, "当前用户无权访问该资源"),
	PARAMS_NOT_NULL(10, "参数[{}]不能为空"),
	PARAMS_NOT_EMPTY(11, "参数[{}]不能为空"),
	FILED_LENGTH_OUT(12 ,"{}不能超出长度{}"),
    BYTES_DESC_INVALID(13, "参数[{}]字节描述不合法"),

	// 用户管理(11000)
	// license管理(12000)
	LICENSE_NAME_CANNOT_REPEAT(12000, "license名称不能重复"),
	LICENSE_NOT_EXIST(12001, "license数据不存在"),
	ORIGIN_PASSWORD_CHECK_FAIL(12002, "原密码验证失败"),
	LOGIN_PASSWORD_LENGTH_CHECK_FAIL(12003, "登录密码不能小于6位数"),
	LOGIN_PASSWORD_NO_CHANGE_MODIFY_FAIL(12004, "密码没有变化，修改失败"),
	LICENSE_CANNOT_BE_DELETED(12005, "license下存在[{}]个端口映射!"),
	// 端口池管理(13000)
	PORT_CANNOT_REPEAT(13000,"端口不能重复"),
	PORT_NOT_EXIST(13001, "该端口在端口池中不存在"),
	PORT_RANGE_FAIL(13002, "端口值范围为MIN-MAX"),
	// 端口映射管理(14000)
	PORT_MAPPING_NOT_EXIST(14000, "端口映射记录不存在"),
	PORT_CANNOT_REPEAT_MAPPING(14001, "服务端口[{}]不能重复映射"),
	AN_UNSUPPORTED_PROTOCOL(14002, "不支持的协议[{}]！"),
	// 调度管理(15000)
	JOB_INFO_NOT_EXIST(15000, "调度管理记录不存在"),
	SYSTEM_ERROR(500, "系统异常"),

	PORT_GROUP_NAME_ALREADY_EXIST(16000,"端口分组名称[{}]已经存在"),
	PORT_GROUP_NAME_DOES_NOT_EXIST(16001,"端口分组不存在"),
	DEFAULT_GROUP_FORBID_DELETE(16002,"默认分组禁止删除"),

    // 安全组管理(17000)
    SECURITY_GROUP_NOT_EXIST(17000, "安全组不存在"),
	SECURITY_RULE_NOT_EXIST(17001, "安全规则不存在"),
    // 域名映射管理(18000)
    DOMAIN_NAME_CANNOT_REPEAT(18000, "域名不能重复"),
    DOMAIN_NAME_NOT_EXIST(18001, "域名不存在"),
    DOMAIN_NAME_IS_DISABLE(18002, "当前域名[{}]被禁用"),
    SUDOMAIN_NAME_CANNOT_REPEAT(18003, "子域名不能重复"),
    DOMAIN_NAME_IS_USED(18002, "当前域名正在使用"),
	;

	private int code;
	private String msg;
}

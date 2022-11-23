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
package fun.asgc.neutrino.proxy.server.constant;

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

	// 用户管理(11000)
	// license管理(12000)
	LICENSE_NAME_CANNOT_REPEAT(12000, "license名称不能重复"),
	LICENSE_NOT_EXIST(12001, "license数据不存在"),
	ORIGIN_PASSWORD_CHECK_FAIL(12002, "原密码验证失败"),
	LOGIN_PASSWORD_LENGTH_CHECK_FAIL(12003, "登录密码不能小于6位数"),
	LOGIN_PASSWORD_NO_CHANGE_MODIFY_FAIL(12004, "密码没有变化，修改失败"),
	// 端口池管理(13000)
	PORT_CANNOT_REPEAT(13000,"端口不能重复"),
	PORT_NOT_EXIST(13001, "该端口在端口池中不存在，不允许映射"),
	// 端口映射管理(14000)
	PORT_MAPPING_NOT_EXIST(14000, "端口映射记录不存在"),
	PORT_CANNOT_REPEAT_MAPPING(14001, "服务端口[{}]不能重复映射"),
	// 调度管理(15000)
	JOB_INFO_NOT_EXIST(15000, "调度管理记录不存在"),
	SYSTEM_ERROR(500, "系统异常"),
	;

	private int code;
	private String msg;
}

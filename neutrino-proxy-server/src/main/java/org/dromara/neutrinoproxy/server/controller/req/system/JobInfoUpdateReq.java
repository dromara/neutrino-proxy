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
package org.dromara.neutrinoproxy.server.controller.req.system;

import lombok.Data;
import org.dromara.neutrinoproxy.server.constant.EnableStatusEnum;

import java.util.Date;

/**
 * 调度管理更新请求
 * @author: zCans
 * @date: 2022/9/17
 */
@Data
public class JobInfoUpdateReq {
	private Integer id;
	/**
	 * 描述
	 */
	private String desc;
	/**
	 * 处理器
	 */
	private String handler;
	/**
	 * cron
	 */
	private String cron;
	/**
	 * 任务参数
	 */
	private String param;
	/**
	 * 任务报警邮箱
	 */
	private String alarmEmail;
	/**
	 * 任务报警钉钉
	 */
	private String alarmDing;
	/**
	 * 启用状态
	 * {@link EnableStatusEnum}
	 */
	private Integer enable;
	/**
	 * 创建时间
	 */
	private Date createTime;
	/**
	 * 更新时间
	 */
	private Date updateTime;
}

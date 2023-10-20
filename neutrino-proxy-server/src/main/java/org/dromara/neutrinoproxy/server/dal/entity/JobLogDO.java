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
package org.dromara.neutrinoproxy.server.dal.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.dromara.neutrinoproxy.server.controller.res.log.JobLogListRes;

import java.util.Date;

/**
 *
 * @author: wen.y
 * @date: 2022/9/4
 */
@ToString
@Accessors(chain = true)
@Data
@TableName("job_log")
public class JobLogDO {
	@TableId(type = IdType.AUTO)
	private Integer id;
	private Integer jobId;
	private String handler;
	private String param;
	private Integer code;
	private String msg;
	private Integer alarmStatus;
	/**
	 * 创建时间
	 */
	private Date createTime;

    public JobLogListRes toRes() {
        JobLogListRes res = new JobLogListRes();
        res.setId(id);
        res.setJobId(jobId);
        res.setHandler(handler);
        res.setParam(param);
        res.setCode(code);
        res.setMsg(msg);
        res.setAlarmStatus(alarmStatus);
        res.setCreateTime(createTime);
        return res;
    }
}

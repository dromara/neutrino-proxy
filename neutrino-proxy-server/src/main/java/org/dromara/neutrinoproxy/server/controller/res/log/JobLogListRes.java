package org.dromara.neutrinoproxy.server.controller.res.log;

import lombok.Data;
import org.dromara.neutrinoproxy.server.constant.AlarmStatusEnum;

import java.util.Date;

/**
 * 调度日志列表响应
 * @author: zCans
 * @date: 2022/9/12
 */
@Data
public class JobLogListRes {
	private Integer id;
	/**
	 * job_id
	 */
	private Integer jobId;
	/**
	 * 处理器
	 */
	private String handler;
	/**
	 * 任务参数
	 */
	private String param;
	/**
	 * code
	 */
	private Integer code;
	/**
	 * msg
	 */
	private String msg;
	/**
	 * 报警状态
	 * {@link AlarmStatusEnum}
	 */
	private Integer alarmStatus;
	/**
	 * 创建时间
	 */
	private Date createTime;
}

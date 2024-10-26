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

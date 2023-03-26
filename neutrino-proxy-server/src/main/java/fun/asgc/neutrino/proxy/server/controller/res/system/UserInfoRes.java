package fun.asgc.neutrino.proxy.server.controller.res.system;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 用户信息响应
 * @author: wen.y
 * @date: 2022/8/27
 */
@Accessors(chain = true)
@Data
public class UserInfoRes {
	private Integer id;
	/**
	 * 用户名
	 */
	private String name;
	/**
	 * 登录名
	 */
	private String loginName;
	/**
	 * 创建时间
	 */
	private Date createTime;
	/**
	 * 更新时间
	 */
	private Date updateTime;
}

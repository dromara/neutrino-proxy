package fun.asgc.neutrino.proxy.server.controller.res;

import fun.asgc.neutrino.proxy.server.constant.OnlineStatusEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/8/6
 */
@Accessors(chain = true)
@Data
public class LicenseListRes {
	private Integer id;
	/**
	 * 名称
	 */
	private String name;
	/**
	 * licenseKey
	 */
	private String key;
	/**
	 * 用户ID
	 */
	private Integer userId;
	/**
	 * 用户名
	 */
	private String userName;
	/**
	 * 是否在线
	 * {@link OnlineStatusEnum}
	 */
	private Integer isOnline;
	/**
	 * 启用状态
	 * {@link fun.asgc.neutrino.proxy.server.constant.EnableStatusEnum}
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

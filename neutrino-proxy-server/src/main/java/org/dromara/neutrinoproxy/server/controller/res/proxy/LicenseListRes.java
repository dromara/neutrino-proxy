package org.dromara.neutrinoproxy.server.controller.res.proxy;

import org.dromara.neutrinoproxy.server.constant.EnableStatusEnum;
import org.dromara.neutrinoproxy.server.constant.OnlineStatusEnum;
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
     * 上传限速
     */
    private String upLimitRate;
    /**
     * 下载限速
     */
    private String downLimitRate;
	/**
	 * 是否在线
	 * {@link OnlineStatusEnum}
	 */
	private Integer isOnline;
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

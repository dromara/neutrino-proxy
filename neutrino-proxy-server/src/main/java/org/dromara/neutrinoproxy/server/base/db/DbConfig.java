package org.dromara.neutrinoproxy.server.base.db;

import org.dromara.neutrinoproxy.server.constant.DbTypeEnum;
import lombok.Data;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;

/**
 * sqlite数据库配置
 * @author: aoshiguchen
 * @date: 2022/7/31
 */
@Data
@Component
public class DbConfig {
	/**
	 * 数据库类型
	 * {@link DbTypeEnum}
	 */
	@Inject("${neutrino.data.db.type}")
	private String type;
	/**
	 * 连接url
	 */
	@Inject("${neutrino.data.db.url}")
	private String url;

	/**
	 * 用户名
	 */
	@Inject("${neutrino.data.db.username}")
	private String username;
	/**
	 * 密码
	 */
	@Inject("${neutrino.data.db.password}")
	private String password;
}

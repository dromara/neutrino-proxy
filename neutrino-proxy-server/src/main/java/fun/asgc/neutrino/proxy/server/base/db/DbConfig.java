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
package fun.asgc.neutrino.proxy.server.base.db;

import fun.asgc.neutrino.proxy.server.constant.DbTypeEnum;
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
	 * 驱动类
	 */
	@Inject("${neutrino.data.db.driver-class}")
	private String driverClass;

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

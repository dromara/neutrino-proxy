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
package fun.asgc.neutrino.proxy.server.dal.entity;

import fun.asgc.neutrino.core.db.annotation.Table;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/8/31
 */
@ToString
@Data
@Table("user_connect_record")
public class UserConnectRecordDO {
	private Integer id;
	/**
	 * 服务端端口号
	 */
	private Integer serverPort;
	/**
	 * userIp
	 */
	private String userIp;
	/**
	 * 客户端IP
	 */
	private String clientIp;
	/**
	 * 客户端信息
	 */
	private String clientLanInfo;
	/**
	 * 用户ID
	 */
	private String userId;
	/**
	 * licenseId
	 */
	private Integer licenseId;
	/**
	 * licenseKey
	 */
	private String licenseKey;
	/**
	 * writeBytes
	 */
	private Integer writeBytes;
	/**
	 * readBytes
	 */
	private Integer readBytes;
	/**
	 * type
	 */
	private Integer type;
	/**
	 * 创建时间
	 */
	private Date createTime;
}

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
package fun.asgc.neutrino.proxy.server.proxy.domain;

import fun.asgc.neutrino.core.util.CollectionUtil;
import fun.asgc.neutrino.proxy.server.dal.entity.PortMappingDO;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/8/30
 */
@Accessors(chain = true)
@Data
public class ProxyMapping {
	/**
	 * 服务端端口
	 */
	private Integer serverPort;
	/**
	 * 客户端信息 IP:port
	 */
	private String lanInfo;

	public static List<ProxyMapping> buildList(List<PortMappingDO> portMappingList) {
		List<ProxyMapping> list = new ArrayList<>();
		if (CollectionUtil.isEmpty(portMappingList)) {
			return list;
		}
		for (PortMappingDO portMapping : portMappingList) {
			list.add(build(portMapping));
		}
		return list;
	}

	public static ProxyMapping build(PortMappingDO portMappingDO) {
		return new ProxyMapping()
				.setServerPort(portMappingDO.getServerPort())
				.setLanInfo(String.format("%s:%s", portMappingDO.getClientIp(), portMappingDO.getClientPort()));
	}
}


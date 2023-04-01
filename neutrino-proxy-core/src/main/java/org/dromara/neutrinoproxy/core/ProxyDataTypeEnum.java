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

package org.dromara.neutrinoproxy.core;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/6/16
 */
@Getter
@AllArgsConstructor
public enum ProxyDataTypeEnum {
	HEARTBEAT(0x01, Constants.ProxyDataTypeName.HEARTBEAT, "心跳"),
	AUTH(0x02, Constants.ProxyDataTypeName.AUTH,"认证"),
	CONNECT(0x03, Constants.ProxyDataTypeName.CONNECT,"连接"),
	DISCONNECT(0x04, Constants.ProxyDataTypeName.DISCONNECT,"断开连接"),
	TRANSFER(0x05, Constants.ProxyDataTypeName.TRANSFER,"数据传输"),
	ERROR(0x06, Constants.ProxyDataTypeName.ERROR,"异常"),
	PORT_MAPPING_SYNC(0x07, Constants.ProxyDataTypeName.PORT_MAPPING_SYNC, "端口映射同步");
	private static Map<Integer,ProxyDataTypeEnum> cache = Stream.of(values()).collect(Collectors.toMap(ProxyDataTypeEnum::getType, Function.identity()));

	private int type;
	private String name;
	private String desc;

	public static ProxyDataTypeEnum of(Integer type) {
		return cache.get(type);
	}
}

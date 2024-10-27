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
	HEARTBEAT(0x01, Constants.ProxyDataTypeName.HEARTBEAT, "HEARTBEAT"),
	AUTH(0x02, Constants.ProxyDataTypeName.AUTH,"AUTH"),
	CONNECT(0x03, Constants.ProxyDataTypeName.CONNECT,"CONNECT"),
	DISCONNECT(0x04, Constants.ProxyDataTypeName.DISCONNECT,"DISCONNECT"),
	TRANSFER(0x05, Constants.ProxyDataTypeName.TRANSFER,"TRANSFER"),
	ERROR(0x06, Constants.ProxyDataTypeName.ERROR,"ERROR"),
	PORT_MAPPING_SYNC(0x07, Constants.ProxyDataTypeName.PORT_MAPPING_SYNC, "PORT_MAPPING_SYNC"),
	UDP_CONNECT(0x08, Constants.ProxyDataTypeName.UDP_CONNECT,"UDP_CONNECT"),
	UDP_DISCONNECT(0x09, Constants.ProxyDataTypeName.UDP_DISCONNECT,"UDP_DISCONNECT"),
	UDP_TRANSFER(0x10, Constants.ProxyDataTypeName.UDP_TRANSFER,"UDP_TRANSFER");
	private static Map<Integer,ProxyDataTypeEnum> cache = Stream.of(values()).collect(Collectors.toMap(ProxyDataTypeEnum::getType, Function.identity()));

	private int type;
	private String name;
	private String desc;

	public static ProxyDataTypeEnum of(Integer type) {
		return cache.get(type);
	}
}

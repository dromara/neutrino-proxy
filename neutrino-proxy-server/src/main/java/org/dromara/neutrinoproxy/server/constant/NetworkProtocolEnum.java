package org.dromara.neutrinoproxy.server.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author: aoshiguchen
 * @date: 2023/4/2
 */
@Getter
@AllArgsConstructor
public enum NetworkProtocolEnum {
    TCP("TCP", "TCP"),
    UDP("UDP", "UDP"),
    HTTP("HTTP", "TCP"),
    ;
    private String desc;
    private String baseProtocol;
    private static final Map<String, NetworkProtocolEnum> map = Stream.of(NetworkProtocolEnum.values()).collect(Collectors.toMap(NetworkProtocolEnum::getDesc, Function.identity()));

    public static NetworkProtocolEnum of(String desc) {
        if (StringUtils.isBlank(desc)) {
            return null;
        }
        if (desc.startsWith("HTTP")) {
            return NetworkProtocolEnum.HTTP;
        }
        return map.get(desc);
    }
}

package org.dromara.neutrinoproxy.server.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 仅Https状态枚举
 * @author Mirac
 * @date 21/7/2024
 */
@Getter
@AllArgsConstructor
public enum HttpsStatusEnum {
    ONLY_HTTPS(1, "仅HTTPS"),
    DISABLE_ONLY_HTTPS(2, "关闭仅HTTPS");
    private static Map<Integer, HttpsStatusEnum> CACHE = Stream.of(HttpsStatusEnum.values()).collect(Collectors.toMap(HttpsStatusEnum::getStatus, Function.identity()));

    private Integer status;
    private String desc;

    public static HttpsStatusEnum of(Integer status) {
        return CACHE.get(status);
    }
}

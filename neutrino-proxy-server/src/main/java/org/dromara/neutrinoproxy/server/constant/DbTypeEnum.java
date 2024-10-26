package org.dromara.neutrinoproxy.server.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 数据库类型美剧
 * @author: aoshiguchen
 * @date: 2022/11/25
 */
@Getter
@AllArgsConstructor
public enum DbTypeEnum {
    H2("h2"),
    MYSQL("mysql"),
    MARIADB("mariadb"),
    ;

    private String type;

    private static final Map<String, DbTypeEnum> cache = Stream.of(DbTypeEnum.values()).collect(Collectors.toMap(DbTypeEnum::getType, Function.identity()));

    public static DbTypeEnum of(String type) {
        return cache.get(type);
    }
}

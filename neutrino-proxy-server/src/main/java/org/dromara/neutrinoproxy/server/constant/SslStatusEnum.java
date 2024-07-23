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
public enum SslStatusEnum {
    UPLOADED(1, "已上传"),
    NOT_UPLOADED(2, "未上传"),
    CERTIFIED(3, "已认证");//预留，用于证书验证

    private Integer status;
    private String desc;
}

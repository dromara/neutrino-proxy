package org.dromara.neutrinoproxy.server.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

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

    private Integer status;
    private String desc;
}

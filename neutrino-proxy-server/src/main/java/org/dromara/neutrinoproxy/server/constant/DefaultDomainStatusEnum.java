package org.dromara.neutrinoproxy.server.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 默认域名状态枚举
 * @author Mirac
 * @date 21/7/2024
 */
@Getter
@AllArgsConstructor
public enum DefaultDomainStatusEnum {
    ENABLE(1, "默认域名"),
    DISABLE(2, "关闭");

    private Integer status;
    private String desc;
}

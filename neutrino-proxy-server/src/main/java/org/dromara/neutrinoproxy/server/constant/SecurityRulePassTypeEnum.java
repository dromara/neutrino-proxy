package org.dromara.neutrinoproxy.server.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SecurityRulePassTypeEnum {
    DENY(-1, "DENY"),
    ALLOW(1, "allow"),
    NONE(0, "none")
    ;

    private final Integer code;
    private final String desc;
}

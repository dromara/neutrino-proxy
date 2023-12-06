package org.dromara.neutrinoproxy.server.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SecurityRulePassTypeEnum {
    DENY(0, "deny"),
    ALLOW(1, "allow"),
    NONE(-1, "none")
    ;

    private final Integer code;
    private final String desc;
}

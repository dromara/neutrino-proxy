package org.dromara.neutrinoproxy.server.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SecurityRulePassTypeEnum {
    REJECT(0, "reject"),
    ALLOW(1, "allow")
    ;

    private final Integer code;
    private final String desc;
}

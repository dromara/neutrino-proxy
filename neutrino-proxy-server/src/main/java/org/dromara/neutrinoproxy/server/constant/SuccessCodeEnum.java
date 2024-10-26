package org.dromara.neutrinoproxy.server.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author: aoshiguchen
 * @date: 2022/11/23
 */
@Getter
@AllArgsConstructor
public enum SuccessCodeEnum {
    SUCCESS(1, "成功"),
    FAIL(2, "失败");

    private Integer code;
    private String desc;
}

package org.dromara.neutrinoproxy.server.controller.req.proxy;

import lombok.Data;
import org.noear.solon.validation.annotation.Min;
import org.noear.solon.validation.annotation.NotNull;

/**
 * license删除请求
 * @author: aoshiguchen
 * @date: 2023/3/11
 */
@Data
public class LicenseDeleteReq {
    @NotNull
    @Min(value=2, message="服务端禁止删除")
    private Integer id;
}

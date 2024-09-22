package org.dromara.neutrinoproxy.server.controller.req.proxy;

import lombok.Data;
import org.dromara.neutrinoproxy.server.constant.DefaultDomainStatusEnum;

/**
 * @author Mirac
 * @date 23/7/2024
 */
@Data
public class DomainUpdateDefaultStatusReq {

    /**
     * id
     */
    private Integer id;

    /**
     * 启用状态 {@link DefaultDomainStatusEnum}
     */
    private Integer isDefault;
}

package org.dromara.neutrinoproxy.server.controller.req.proxy;

import lombok.Data;
import org.dromara.neutrinoproxy.server.constant.EnableStatusEnum;

/**
 * @author Mirac
 * @date 21/7/2024
 */
@Data
public class DomainListReq {
    /**
     * 用户ID
     */
    private Integer userId;
    /**
     * 启用状态
     * {@link EnableStatusEnum}
     */
    private Integer enable;
}

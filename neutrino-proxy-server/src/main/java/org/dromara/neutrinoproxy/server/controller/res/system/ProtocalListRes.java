package org.dromara.neutrinoproxy.server.controller.res.system;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 协议列表响应
 * @author: aoshiguchen
 * @date: 2023/4/2
 */
@Accessors(chain = true)
@Data
public class ProtocalListRes {
    /**
     * 协议名称
     */
    private String name;
    /**
     * 启用状态
     */
    private Boolean enable;
    /**
     * 备注
     */
    private String remark;
}

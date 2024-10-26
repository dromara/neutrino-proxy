package org.dromara.neutrinoproxy.server.controller.req.system;

import lombok.Data;

/**
 * 端口池列表请求
 * @author: aoshiguchen
 * @date: 2022/8/7
 */
@Data
public class PortPoolListReq {
    /**
     * 分组ID
     */
    private Integer groupId;
}

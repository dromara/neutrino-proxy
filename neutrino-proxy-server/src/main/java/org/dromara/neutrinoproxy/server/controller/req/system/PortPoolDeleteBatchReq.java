package org.dromara.neutrinoproxy.server.controller.req.system;

import lombok.Data;

import java.util.List;

/**
 * @author: Metal
 * @date: 2023/7/3
 */
@Data
public class PortPoolDeleteBatchReq {
    private List<Integer> ids;
}

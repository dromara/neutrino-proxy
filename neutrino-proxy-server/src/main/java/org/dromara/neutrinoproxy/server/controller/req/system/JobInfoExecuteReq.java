package org.dromara.neutrinoproxy.server.controller.req.system;

import lombok.Data;

/**
 * 调度管理执行请求
 * @author: zCans
 * @date: 2022/9/12
 */
@Data
public class JobInfoExecuteReq {
    /**
     * id
     */
    private Integer id;
    /**
     * 任务参数
     */
    private String param;
}

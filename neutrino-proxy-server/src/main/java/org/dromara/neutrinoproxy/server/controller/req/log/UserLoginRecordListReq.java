package org.dromara.neutrinoproxy.server.controller.req.log;

import lombok.Data;

/**
 * 用户登录日志列表请求
 * @author: aoshiguchen
 * @date: 2022/10/20
 */
@Data
public class UserLoginRecordListReq {
    /**
     * 用户ID
     */
    private Integer userId;
}

package org.dromara.neutrinoproxy.server.controller.req.system;

import lombok.Data;

/**
 * 用户列表请求
 * @author: aoshiguchen
 * @date: 2022/8/14
 */
@Data
public class UserListReq {

    /**
     * 用户名
     */
    private String name;

}

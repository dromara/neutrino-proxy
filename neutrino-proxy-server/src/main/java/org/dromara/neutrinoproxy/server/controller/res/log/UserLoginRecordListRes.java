package org.dromara.neutrinoproxy.server.controller.res.log;

import lombok.Data;

import java.util.Date;

/**
 * 用户登录日志列表响应
 * @author: aoshiguchen
 * @date: 2022/10/20
 */
@Data
public class UserLoginRecordListRes {
    private Integer id;
    /**
     * 用户ID
     */
    private Integer userId;
    /**
     * 用户名
     */
    private String userName;
    /**
     * ip
     */
    private String ip;
    /**
     * 类型
     */
    private Integer type;
    /**
     * 创建时间
     */
    private Date createTime;
}

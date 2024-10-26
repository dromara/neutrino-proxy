package org.dromara.neutrinoproxy.server.controller.res.log;

import lombok.Data;

import java.util.Date;

/**
 * @author: aoshiguchen
 * @date: 2022/11/26
 */
@Data
public class ClientConnectRecordListRes {
    private Integer id;
    private String ip;
    private Integer licenseId;
    private Integer type;
    private Integer userId;
    private String userName;
    private String licenseName;
    private String msg;
    /**
     * 1、成功
     * 2、失败
     */
    private Integer code;
    private String err;
    /**
     * 创建时间
     */
    private Date createTime;
}

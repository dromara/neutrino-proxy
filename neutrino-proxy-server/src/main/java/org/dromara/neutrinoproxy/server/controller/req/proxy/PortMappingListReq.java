package org.dromara.neutrinoproxy.server.controller.req.proxy;

import org.dromara.neutrinoproxy.server.constant.EnableStatusEnum;
import org.dromara.neutrinoproxy.server.constant.OnlineStatusEnum;
import lombok.Data;

/**
 * 端口映射列表请求
 * @author: aoshiguchen
 * @date: 2022/8/8
 */
@Data
public class PortMappingListReq {
    /**
     * 用户ID
     */
    private Integer userId;
    /**
     * 协议
     */
    private String protocal;
    /**
     * licenseId
     */
    private Integer licenseId;
    /**
     * 服务端口号
     */
    private Integer serverPort;
    /**
     * 描述
     */
    private String description;


    /**
     * 是否在线
     * {@link OnlineStatusEnum}
     */
    private Integer isOnline;
    /**
     * 启用状态
     * {@link EnableStatusEnum}
     */
    private Integer enable;
}

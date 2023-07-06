package org.dromara.neutrinoproxy.server.controller.req.system;

import lombok.Data;

/**
 * 获取可用端口请求
 */
@Data
public class AvailablePortListReq {

    /**
     * licenseId
     */
    private Integer licenseId;

    /**
     * 当前页
     */
    private int page = 1;
    /**
     * 分页大小
     */
    private int size = 10;

    /**
     * 搜索关键字
     */
    private String keyword;
}

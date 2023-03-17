package fun.asgc.neutrino.proxy.server.controller.req;

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
}

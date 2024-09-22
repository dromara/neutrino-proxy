package org.dromara.neutrinoproxy.server.controller.req.proxy;

import lombok.Data;

/**
 * @author Mirac
 * @date 21/7/2024
 */
@Data
public class DomainUpdateReq {
    /**
     * id
     */
    private Integer id;

    /**
     * 主域名
     */
    private String domain;

    /**
     * KeyStore密码
     */
    private String keyStorePassword;

    /**
     * 强制Https
     */
    private Integer forceHttps;
}

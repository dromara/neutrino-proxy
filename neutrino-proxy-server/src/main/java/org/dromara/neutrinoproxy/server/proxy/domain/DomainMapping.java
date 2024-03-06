package org.dromara.neutrinoproxy.server.proxy.domain;

import lombok.Data;

/**
 * @author xiaojie
 * @date
 */
@Data
public class DomainMapping {

    /**
     * domainMapping 主键id
     */
    private Integer id;
    /**
     * License
     */
    private Integer licenseId;
    /**
     * 客户端信息 IP:port
     */
    private String domain;
    /**
     * 目标地址（其中含有\n隔离多个 目标地址）
     */
    private String targetPath;


    public DomainMapping() {
    }

    public DomainMapping(Integer id, Integer licenseId, String domain, String targetPath) {
        this.id = id;
        this.licenseId = licenseId;
        this.domain = domain;
        this.targetPath = targetPath;
    }
}

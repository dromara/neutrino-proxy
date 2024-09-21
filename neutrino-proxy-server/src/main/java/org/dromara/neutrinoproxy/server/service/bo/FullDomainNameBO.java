package org.dromara.neutrinoproxy.server.service.bo;

import lombok.Data;
import lombok.experimental.Accessors;
import org.dromara.neutrinoproxy.server.constant.HttpsStatusEnum;

/**
 * 完整域名
 * @author: mirac
 * @date: 2024/8/8
 */
@Data
@Accessors(chain = true)
public class FullDomainNameBO {
    /**
     * 域名端口映射id
     */
    private Integer id;
    /**
     * 端口映射id
     */
    private Integer portMappingId;
    /**
     * 子域名
     */
    private String subdomain;
    /**
     * 域名id
     */
    private Integer domainNameId;
    /**
     * 域名
     */
    private String domain;
    /**
     * 强制使用HTTPS(1、是 2、否)
     * {@link HttpsStatusEnum}
     */
    private Integer forceHttps;
}

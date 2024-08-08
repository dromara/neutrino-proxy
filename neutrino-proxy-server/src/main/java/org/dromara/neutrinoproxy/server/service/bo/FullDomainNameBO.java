package org.dromara.neutrinoproxy.server.service.bo;

import lombok.Data;
import lombok.experimental.Accessors;
import org.dromara.neutrinoproxy.server.controller.res.report.HomeDataView;

import java.util.Date;

/**
 * 完整域名
 * @author: mirac
 * @date: 2024/8/8
 */
@Data
@Accessors(chain = true)
public class FullDomainNameBO {
    /**
     * 端口映射id
     */
    private Integer id;
    /**
     * 子域名
     */
    private String subdomain;
    /**
     * 域名
     */
    private String domain;

    /**
     * 域名id
     */
    private Integer domainId;
}

package org.dromara.neutrinoproxy.server.controller.res.proxy;


import lombok.Data;
import lombok.experimental.Accessors;
import org.dromara.neutrinoproxy.server.dal.entity.DomainMappingDO;

/**
 *
 * @author xiaojie
 * @date
 */
@Accessors(chain = true)
@Data
public class DomainMappingDto extends DomainMappingDO {

    /**
     * licenseId
     */
    private String licenseName;

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 用户名称
     */
    private String userName;
}

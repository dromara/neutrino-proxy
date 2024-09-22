package org.dromara.neutrinoproxy.server.controller.res.proxy;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.experimental.Accessors;
import org.dromara.neutrinoproxy.server.constant.DefaultDomainStatusEnum;
import org.dromara.neutrinoproxy.server.constant.EnableStatusEnum;
import org.dromara.neutrinoproxy.server.constant.HttpsStatusEnum;
import org.dromara.neutrinoproxy.server.constant.SslStatusEnum;

import java.util.Date;

/**
 * @author Mirac
 * @date 21/7/2024
 */
@Accessors(chain = true)
@Data
public class DomainListRes {
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 主域名
     */
    private String domain;

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 是否是默认域名(1、是 2、否)
     * {@link DefaultDomainStatusEnum}
     */
    private Integer isDefault;

    /**
     * 强制使用HTTPS(1、是 2、否)
     * {@link HttpsStatusEnum}
     */
    private Integer forceHttps;

    /**
     * 启用状态
     * {@link EnableStatusEnum}
     */
    private Integer enable;

    /**
     * SSLStatusEnum
     * {@link SslStatusEnum}
     */
    private Integer sslStatus;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}

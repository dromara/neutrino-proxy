package org.dromara.neutrinoproxy.server.controller.req.proxy;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import org.dromara.neutrinoproxy.server.constant.DefaultDomainStatusEnum;
import org.dromara.neutrinoproxy.server.constant.EnableStatusEnum;
import org.dromara.neutrinoproxy.server.constant.HttpsStatusEnum;
import org.noear.solon.core.handle.UploadedFile;
import org.noear.solon.lang.Nullable;

import java.util.Date;

/**
 * @author Mirac
 * @date 21/7/2024
 */
@Data
public class DomainCreateReq {
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

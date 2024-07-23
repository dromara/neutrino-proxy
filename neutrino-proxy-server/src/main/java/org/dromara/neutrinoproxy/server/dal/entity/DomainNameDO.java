/**
 * Copyright (c) 2022 aoshiguchen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.dromara.neutrinoproxy.server.dal.entity;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.dromara.neutrinoproxy.server.constant.*;
import org.dromara.neutrinoproxy.server.controller.res.proxy.DomainListRes;
import org.dromara.neutrinoproxy.server.controller.res.proxy.PortMappingListRes;

import java.util.Date;

/**
 * 域名
 * @author: Mirac
 * @date: 2024/7/21
 */
@ToString
@Accessors(chain = true)
@Data
@TableName("domain_name")
public class DomainNameDO {
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
     * SSL证书文件内容
     */
    private byte[] jks;

    /**
     * KeyStore密码
     */
    private String keyStorePassword;

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
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    public DomainListRes toRes() {
        DomainListRes res = new DomainListRes();
        BeanUtil.copyProperties(this, res);
        res.setSslStatus(this.getJks() == null ? SslStatusEnum.NOT_UPLOADED.getStatus() : SslStatusEnum.UPLOADED.getStatus());
        return res;
    }
}

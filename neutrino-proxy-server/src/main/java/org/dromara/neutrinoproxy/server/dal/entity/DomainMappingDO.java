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
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.dromara.neutrinoproxy.server.constant.EnableStatusEnum;
import org.dromara.neutrinoproxy.server.constant.OnlineStatusEnum;
import org.dromara.neutrinoproxy.server.controller.res.proxy.DomainMappingDto;
import org.noear.solon.validation.annotation.NotBlank;
import org.noear.solon.validation.annotation.NotNull;
import org.noear.solon.validation.annotation.Pattern;

import java.util.Date;

/**
 * 端口映射
 * @author: aoshiguchen
 * @date: 2022/8/8
 */
@ToString
@Accessors(chain = true)
@Data
@TableName("domain_mapping")
public class DomainMappingDO {
	@TableId(type = IdType.AUTO)
	private Integer id;
	/**
	 * licenseId
	 */
    @NotNull
	private Integer licenseId;
	/**
	 * 协议
	 */
	private String protocal;
	/**
	 * 域名 /^(\w+\.{1}\w+)$/g
	 */
    @NotBlank
    @Pattern("^(?=^.{3,255}$)[a-zA-Z0-9][-a-zA-Z0-9]{0,62}(\\.[a-zA-Z0-9][-a-zA-Z0-9]{0,62})+$")
	private String domain;
    /**
     * 描述
     */
    private String description;
	/**
	 * 目标地址（licenseId客户端，ip:port）
	 */
    @NotBlank
    private String targetPath;

    /**
     * 上传限速
     */
    private String upLimitRate;
    /**
     * 下载限速
     */
    private String downLimitRate;

	/**
	 * 是否在线
	 * {@link OnlineStatusEnum}
	 */
    @TableField(exist=false)
	private Integer isOnline;

	/**
	 * 启用状态
	 * {@link EnableStatusEnum}
	 */
	private Integer enable;

    /**
     * 安全组Id
     */
    private Integer securityGroupId = 0; // 设置为null不生效，不知道为啥

	/**
	 * 创建时间
	 */
	private Date createTime;
	/**
	 * 更新时间
	 */
	private Date updateTime;

    public DomainMappingDto toRes() {
        DomainMappingDto dto = new DomainMappingDto();
        BeanUtil.copyProperties(this, dto);
        return dto;
    }

}

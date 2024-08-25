package org.dromara.neutrinoproxy.server.controller;

import org.dromara.neutrinoproxy.server.base.page.PageInfo;
import org.dromara.neutrinoproxy.server.base.page.PageQuery;
import org.dromara.neutrinoproxy.server.constant.ExceptionConstant;
import org.dromara.neutrinoproxy.server.constant.NetworkProtocolEnum;
import org.dromara.neutrinoproxy.server.controller.req.proxy.*;
import org.dromara.neutrinoproxy.server.controller.res.proxy.*;
import org.dromara.neutrinoproxy.server.controller.req.proxy.*;
import org.dromara.neutrinoproxy.server.controller.res.proxy.*;
import org.dromara.neutrinoproxy.server.service.PortMappingService;
import org.dromara.neutrinoproxy.server.util.ParamCheckUtil;
import org.apache.commons.lang3.StringUtils;
import org.noear.solon.annotation.*;

import java.util.List;

/**
 * 端口映射
 * @author: aoshiguchen
 * @date: 2022/8/8
 */
@Mapping("/port-mapping")
@Controller
public class PortMappingController {
	@Inject
	private PortMappingService portMappingService;

	@Get
	@Mapping("/page")
	public PageInfo<PortMappingListRes> page(PageQuery pageQuery, PortMappingListReq req) {
		ParamCheckUtil.checkNotNull(pageQuery, "pageQuery");
		return portMappingService.page(pageQuery, req);
	}

    @Post
    @Mapping("/create")
    public PortMappingCreateRes create(PortMappingCreateReq req) {
        ParamCheckUtil.checkNotNull(req, "req");
        ParamCheckUtil.checkNotNull(req.getLicenseId(), "licenseId");
        ParamCheckUtil.checkNotNull(req.getServerPort(), "serverPort");
        ParamCheckUtil.checkNotNull(req.getClientPort(), "clientPort");
        ParamCheckUtil.checkNotEmpty(req.getProtocal(), "protocal");
		ParamCheckUtil.checkMaxLength(req.getDescription(), 50, "描述", "50");
        ParamCheckUtil.checkBytesDesc(req.getUpLimitRate(), "upLimitRate");
        ParamCheckUtil.checkBytesDesc(req.getDownLimitRate(), "downLimitRate");
        if (StringUtils.isBlank(req.getClientIp())) {
            // 没传客户端ip，默认为127.0.0.1
            req.setClientIp("127.0.0.1");
        }
        NetworkProtocolEnum networkProtocolEnum = NetworkProtocolEnum.of(req.getProtocal());
        ParamCheckUtil.checkNotNull(networkProtocolEnum, ExceptionConstant.AN_UNSUPPORTED_PROTOCOL, req.getProtocal());
        if (networkProtocolEnum != NetworkProtocolEnum.HTTP) {
            // 目前仅HTTP支持绑定域名
			req.setDomainMappings(null);
        }
		req.setProtocal(networkProtocolEnum.getDesc());
		if (null == req.getProxyResponses()) {
			req.setProxyResponses(0);
		}
		if (null == req.getProxyTimeoutMs()) {
			req.setProxyTimeoutMs(0L);
		}
        if (null == req.getSecurityGroupId()) {
            req.setSecurityGroupId(0);
        }

		return portMappingService.create(req);
	}

	@Post
	@Mapping("/update")
	public void update(PortMappingUpdateReq req) {
		ParamCheckUtil.checkNotNull(req, "req");
		ParamCheckUtil.checkNotNull(req.getLicenseId(), "licenseId");
		ParamCheckUtil.checkNotNull(req.getServerPort(), "serverPort");
		ParamCheckUtil.checkNotNull(req.getClientPort(), "clientPort");
		ParamCheckUtil.checkNotEmpty(req.getProtocal(), "protocal");
		ParamCheckUtil.checkMaxLength(req.getDescription(), 50, "描述", "50");
        ParamCheckUtil.checkBytesDesc(req.getUpLimitRate(), "upLimitRate");
        ParamCheckUtil.checkBytesDesc(req.getDownLimitRate(), "downLimitRate");
		if (StringUtils.isBlank(req.getClientIp())) {
			// 没传客户端ip，默认为127.0.0.1
			req.setClientIp("127.0.0.1");
		}
		NetworkProtocolEnum networkProtocolEnum = NetworkProtocolEnum.of(req.getProtocal());
		ParamCheckUtil.checkNotNull(networkProtocolEnum, ExceptionConstant.AN_UNSUPPORTED_PROTOCOL, req.getProtocal());
		if (networkProtocolEnum != NetworkProtocolEnum.HTTP) {
			// 目前仅HTTP支持绑定域名
			req.setDomainMappings(null);
		}
		req.setProtocal(networkProtocolEnum.getDesc());
		if (null == req.getProxyResponses()) {
			req.setProxyResponses(0);
		}
		if (null == req.getProxyTimeoutMs()) {
			req.setProxyTimeoutMs(0L);
		}
        if (null == req.getSecurityGroupId()) {
            req.setSecurityGroupId(0);
        }

        portMappingService.update(req);
	}

	@Get
	@Mapping("/detail")
	public PortMappingDetailRes detail(PortMappingDetailReq req) {
		ParamCheckUtil.checkNotNull(req, "req");
		ParamCheckUtil.checkNotNull(req.getId(), "id");

		return portMappingService.detail(req.getId());
	}

	@Post
	@Mapping("/update/enable-status")
	public PortMappingUpdateEnableStatusRes updateEnableStatus(PortMappingUpdateEnableStatusReq req) {
		ParamCheckUtil.checkNotNull(req, "req");
		ParamCheckUtil.checkNotNull(req.getId(), "id");
		ParamCheckUtil.checkNotNull(req.getEnable(), "enable");

		return portMappingService.updateEnableStatus(req);
	}

	@Post
	@Mapping("/delete")
	public void delete(PortMappingDeleteReq req) {
		ParamCheckUtil.checkNotNull(req, "req");
		ParamCheckUtil.checkNotNull(req.getId(), "id");

		portMappingService.delete(req.getId());
	}

    /**
     * 绑定安全组
     * @param req portMappingId和securityGroupId
     */
    @Post
    @Mapping("/bind/security-group")
    public void bindSecurityGroup(PortMappingBindSecurityGroupReq req) {
        portMappingService.portBindSecurityGroup(req.getId(), req.getSecurityGroupId());
    }

    /**
     * 安全组解绑
     * @param id 端口映射Id
     */
    @Post
    @Mapping("/unbind/security-group")
    public void unbindSecurityGroup(Integer id) {
        portMappingService.portUnbindSecurityGroup(id);
    }


}

package fun.asgc.neutrino.proxy.server.controller;

import fun.asgc.neutrino.proxy.server.base.page.PageInfo;
import fun.asgc.neutrino.proxy.server.base.page.PageQuery;
import fun.asgc.neutrino.proxy.server.controller.req.*;
import fun.asgc.neutrino.proxy.server.controller.res.*;
import fun.asgc.neutrino.proxy.server.service.PortMappingService;
import fun.asgc.neutrino.proxy.server.util.ParamCheckUtil;
import org.noear.solon.annotation.*;

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
		ParamCheckUtil.checkNotEmpty(req.getClientIp(), "clientIp");
		ParamCheckUtil.checkNotNull(req.getClientPort(), "clientPort");

		return portMappingService.create(req);
	}

	@Post
	@Mapping("/update")
	public PortMappingUpdateRes update(PortMappingUpdateReq req) {
		ParamCheckUtil.checkNotNull(req, "req");

		return portMappingService.update(req);
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
}

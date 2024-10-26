package org.dromara.neutrinoproxy.server.controller;

import org.dromara.neutrinoproxy.server.base.page.PageInfo;
import org.dromara.neutrinoproxy.server.base.page.PageQuery;
import org.dromara.neutrinoproxy.server.base.rest.Authorization;
import org.dromara.neutrinoproxy.server.controller.req.system.*;
import org.dromara.neutrinoproxy.server.controller.res.system.*;
import org.dromara.neutrinoproxy.server.service.PortPoolService;
import org.dromara.neutrinoproxy.server.util.ParamCheckUtil;
import org.dromara.neutrinoproxy.server.controller.req.system.*;
import org.dromara.neutrinoproxy.server.controller.res.system.*;
import org.noear.solon.annotation.*;

import java.util.List;

/**
 * 端口池
 * @author: aoshiguchen
 * @date: 2022/8/7
 */
@Mapping("/port-pool")
@Controller
public class PortPoolController {
	@Inject
	private PortPoolService portPoolService;

	@Get
	@Mapping("/page")
	public PageInfo<PortPoolListRes> page(PageQuery pageQuery, PortPoolListReq req) {
		ParamCheckUtil.checkNotNull(pageQuery, "pageQuery");

		return portPoolService.page(pageQuery, req);
	}

	@Post
	@Mapping("/update")
	public PortPoolUpdateRes update(PortPoolUpdateReq req) {
		ParamCheckUtil.checkNotNull(req, "req");
		ParamCheckUtil.checkNotNull(req.getId(), "id");
		ParamCheckUtil.checkNotNull(req.getGroupId(), "groupId");
		return portPoolService.update(req);
	}

	@Get
	@Mapping("/list")
	public List<PortPoolListRes> list(PortPoolListReq req) {
		return portPoolService.list(req);
	}

	@Post
	@Mapping("/create")
	@Authorization(onlyAdmin = true)
	public PortPoolCreateRes create(PortPoolCreateReq req) {
		ParamCheckUtil.checkNotNull(req, "req");
		ParamCheckUtil.checkNotEmpty(req.getPort(), "port");
		ParamCheckUtil.checkNotNull(req.getGroupId(), "groupId");

		return portPoolService.create(req);
	}

	@Post
	@Mapping("/update/enable-status")
	@Authorization(onlyAdmin = true)
	public PortPoolUpdateEnableStatusRes updateEnableStatus(PortPoolUpdateEnableStatusReq req) {
		ParamCheckUtil.checkNotNull(req, "req");
		ParamCheckUtil.checkNotNull(req.getId(), "id");
		ParamCheckUtil.checkNotNull(req.getEnable(), "enable");

		return portPoolService.updateEnableStatus(req);
	}

	@Post
	@Mapping("/delete")
	@Authorization(onlyAdmin = true)
	public void delete(PortPoolDeleteReq req) {
		ParamCheckUtil.checkNotNull(req, "req");
		ParamCheckUtil.checkNotNull(req.getId(), "id");

		portPoolService.delete(req.getId());
	}

	@Get
	@Mapping("/get-available-port-list")
	public PageInfo<PortPoolListRes> getAvailablePortList(AvailablePortListReq req) {
		ParamCheckUtil.checkNotNull(req, "req");
		ParamCheckUtil.checkNotNull(req.getLicenseId(), "licenseId");
		return portPoolService.getAvailablePortList(req);
	}


	@Get
	@Mapping("/get-by-group")
	public List<PortPoolListRes> portListByGroupId(String groupId) {
		return portPoolService.portListByGroupId(groupId);
	}

	@Put
	@Mapping("/update-group")
	public PortPoolUpdateGroupRes updateGroup(PortPoolUpdateGroupReq req) {
		ParamCheckUtil.checkNotNull(req, "req");
		ParamCheckUtil.checkNotNull(req.getGroupId(), "groupId");
		ParamCheckUtil.checkNotEmpty(req.getPortIdList(), "portIdList");
		return portPoolService.updateGroup(req);
	}

	@Post
	@Mapping("/delete-batch")
	@Authorization(onlyAdmin = true)
	public void deleteBatch(PortPoolDeleteBatchReq req) {
		ParamCheckUtil.checkNotNull(req, "req");
		ParamCheckUtil.checkNotNull(req.getIds(), "ids");

		portPoolService.deleteBatch(req.getIds());
	}

	@Get
	@Mapping("/port-available")
	public boolean portAvailable(Integer port, Integer portMappingId) {
		ParamCheckUtil.checkNotNull(port, "port");

		return portPoolService.portAvailable(port, portMappingId);
	}
}

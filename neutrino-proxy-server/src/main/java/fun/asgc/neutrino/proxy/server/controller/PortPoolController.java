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
package fun.asgc.neutrino.proxy.server.controller;

import fun.asgc.neutrino.proxy.server.base.page.PageInfo;
import fun.asgc.neutrino.proxy.server.base.page.PageQuery;
import fun.asgc.neutrino.proxy.server.base.rest.Authorization;
import fun.asgc.neutrino.proxy.server.controller.req.*;
import fun.asgc.neutrino.proxy.server.controller.res.*;
import fun.asgc.neutrino.proxy.server.service.PortPoolService;
import fun.asgc.neutrino.proxy.server.util.ParamCheckUtil;
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
		ParamCheckUtil.checkNotNull(req.getPort(), "port");
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
	public List<PortPoolListRes> getAvailablePortList(AvailablePortListReq req) {
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
}

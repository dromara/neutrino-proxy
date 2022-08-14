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

import fun.asgc.neutrino.core.annotation.Autowired;
import fun.asgc.neutrino.core.annotation.NonIntercept;
import fun.asgc.neutrino.core.db.page.Page;
import fun.asgc.neutrino.core.db.page.PageQuery;
import fun.asgc.neutrino.core.web.annotation.*;
import fun.asgc.neutrino.proxy.server.controller.req.PortPoolCreateReq;
import fun.asgc.neutrino.proxy.server.controller.req.PortPoolListReq;
import fun.asgc.neutrino.proxy.server.controller.req.PortPoolUpdateEnableStatusReq;
import fun.asgc.neutrino.proxy.server.controller.res.PortPoolCreateRes;
import fun.asgc.neutrino.proxy.server.controller.res.PortPoolListRes;
import fun.asgc.neutrino.proxy.server.controller.res.PortPoolUpdateEnableStatusRes;
import fun.asgc.neutrino.proxy.server.service.PortPoolService;
import fun.asgc.neutrino.proxy.server.util.ParamCheckUtil;

import java.util.List;

/**
 * 端口池
 * @author: aoshiguchen
 * @date: 2022/8/7
 */
@NonIntercept
@RequestMapping("port-pool")
@RestController
public class PortPoolController {
	@Autowired
	private PortPoolService portPoolService;

	@GetMapping("page")
	public Page<PortPoolListRes> page(PageQuery pageQuery, PortPoolListReq req) {
		ParamCheckUtil.checkNotNull(pageQuery, "pageQuery");

		return portPoolService.page(pageQuery, req);
	}

	@GetMapping("list")
	public List<PortPoolListRes> list(PortPoolListReq req) {
		return portPoolService.list(req);
	}

	@PostMapping("create")
	public PortPoolCreateRes create(@RequestBody PortPoolCreateReq req) {
		ParamCheckUtil.checkNotNull(req, "req");
		ParamCheckUtil.checkNotNull(req.getPort(), "port");

		return portPoolService.create(req);
	}

	@PostMapping("update/enable-status")
	public PortPoolUpdateEnableStatusRes updateEnableStatus(@RequestBody PortPoolUpdateEnableStatusReq req) {
		ParamCheckUtil.checkNotNull(req, "req");
		ParamCheckUtil.checkNotNull(req.getId(), "id");
		ParamCheckUtil.checkNotNull(req.getEnable(), "enable");

		return portPoolService.updateEnableStatus(req);
	}

	@PostMapping("delete")
	public void delete(@RequestParam("id") Integer id) {
		ParamCheckUtil.checkNotNull(id, "id");

		portPoolService.delete(id);
	}
}

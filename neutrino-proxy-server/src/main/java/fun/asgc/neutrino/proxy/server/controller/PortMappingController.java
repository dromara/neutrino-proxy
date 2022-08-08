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
import fun.asgc.neutrino.proxy.server.controller.req.PortMappingCreateReq;
import fun.asgc.neutrino.proxy.server.controller.req.PortMappingListReq;
import fun.asgc.neutrino.proxy.server.controller.req.PortMappingUpdateEnableStatusReq;
import fun.asgc.neutrino.proxy.server.controller.res.*;
import fun.asgc.neutrino.proxy.server.service.PortMappingService;

/**
 * 端口映射
 * @author: aoshiguchen
 * @date: 2022/8/8
 */
@NonIntercept
@RequestMapping("license")
@RestController
public class PortMappingController {
	@Autowired
	private PortMappingService portMappingService;

	@GetMapping("page")
	public Page<PortMappingListRes> page(PageQuery pageQuery, PortMappingListReq req) {
		// TODO 参数校验
		return portMappingService.page(pageQuery, req);
	}

	@PostMapping("create")
	public PortMappingCreateRes create(@RequestBody PortMappingCreateReq req) {
		// TODO 参数校验
		return portMappingService.create(req);
	}

	@PostMapping("update")
	public PortMappingUpdateRes update(@RequestBody PortMappingUpdateRes req) {
		// TODO 参数校验
		return portMappingService.update(req);
	}

	@GetMapping("detail")
	public PortMappingDetailRes detail(@RequestParam("id") Integer id) {
		// TODO 参数校验
		return portMappingService.detail(id);
	}

	@PostMapping("update/enable-status")
	public PortMappingUpdateEnableStatusRes updateEnableStatus(@RequestBody PortMappingUpdateEnableStatusReq req) {
		// TODO 参数校验
		return portMappingService.updateEnableStatus(req);
	}

	@PostMapping("delete")
	public void delete(@RequestParam("id") Integer id) {
		// TODO 参数校验
		portMappingService.delete(id);
	}
}

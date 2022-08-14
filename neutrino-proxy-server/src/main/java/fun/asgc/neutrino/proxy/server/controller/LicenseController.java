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
import fun.asgc.neutrino.proxy.server.base.rest.annotation.OnlyAdmin;
import fun.asgc.neutrino.proxy.server.controller.req.LicenseCreateReq;
import fun.asgc.neutrino.proxy.server.controller.req.LicenseListReq;
import fun.asgc.neutrino.proxy.server.controller.req.LicenseUpdateEnableStatusReq;
import fun.asgc.neutrino.proxy.server.controller.req.LicenseUpdateReq;
import fun.asgc.neutrino.proxy.server.controller.res.*;
import fun.asgc.neutrino.proxy.server.service.LicenseService;
import fun.asgc.neutrino.proxy.server.util.ParamCheckUtil;

import java.util.List;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/8/6
 */
@NonIntercept
@RequestMapping("license")
@RestController
public class LicenseController {

	@Autowired
	private LicenseService licenseService;

	@GetMapping("page")
	public Page<LicenseListRes> page(PageQuery pageQuery, LicenseListReq req) {
		ParamCheckUtil.checkNotNull(pageQuery, "pageQuery");

		return licenseService.page(pageQuery, req);
	}

	@GetMapping("list")
	public List<LicenseListRes> list(LicenseListReq req) {
		return licenseService.list(req);
	}

	@OnlyAdmin
	@PostMapping("create")
	public LicenseCreateRes create(@RequestBody LicenseCreateReq req) {
		ParamCheckUtil.checkNotNull(req, "req");
		ParamCheckUtil.checkNotEmpty(req.getName(), "name");
		ParamCheckUtil.checkNotNull(req.getUserId(), "userId");

		return licenseService.create(req);
	}

	@OnlyAdmin
	@PostMapping("update")
	public LicenseUpdateRes update(@RequestBody LicenseUpdateReq req) {
		ParamCheckUtil.checkNotNull(req, "req");
		ParamCheckUtil.checkNotNull(req.getId(), "id");
		ParamCheckUtil.checkNotEmpty(req.getName(), "name");

		return licenseService.update(req);
	}

	@GetMapping("detail")
	public LicenseDetailRes detail(@RequestParam("id") Integer id) {
		ParamCheckUtil.checkNotNull(id, "id");

		return licenseService.detail(id);
	}

	@OnlyAdmin
	@PostMapping("update/enable-status")
	public LicenseUpdateEnableStatusRes updateEnableStatus(@RequestBody LicenseUpdateEnableStatusReq req) {
		ParamCheckUtil.checkNotNull(req, "req");
		ParamCheckUtil.checkNotNull(req.getId(), "id");
		ParamCheckUtil.checkNotNull(req.getEnable(), "enable");

		return licenseService.updateEnableStatus(req);
	}

	@OnlyAdmin
	@PostMapping("delete")
	public void delete(@RequestParam("id") Integer id) {
		ParamCheckUtil.checkNotNull(id, "id");

		licenseService.delete(id);
	}

	@OnlyAdmin
	@PostMapping("reset")
	public void reset(@RequestParam("id") Integer id) {
		ParamCheckUtil.checkNotNull(id, "id");

		licenseService.reset(id);
	}

}

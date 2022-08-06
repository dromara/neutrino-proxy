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

/**
 *
 * @author: aoshiguchen
 * @date: 2022/8/6
 */

import fun.asgc.neutrino.core.annotation.Autowired;
import fun.asgc.neutrino.core.annotation.NonIntercept;
import fun.asgc.neutrino.core.db.page.Page;
import fun.asgc.neutrino.core.db.page.PageQuery;
import fun.asgc.neutrino.core.web.annotation.*;
import fun.asgc.neutrino.proxy.server.controller.req.LicenseCreateReq;
import fun.asgc.neutrino.proxy.server.controller.req.LicenseListReq;
import fun.asgc.neutrino.proxy.server.controller.req.LicenseUpdateEnableStatusReq;
import fun.asgc.neutrino.proxy.server.controller.req.LicenseUpdateReq;
import fun.asgc.neutrino.proxy.server.controller.res.*;
import fun.asgc.neutrino.proxy.server.service.LicenseService;

@NonIntercept
@RequestMapping("license")
@RestController
public class LicenseController {

	@Autowired
	private LicenseService licenseService;

	@GetMapping("page")
	public Page<LicenseListRes> page(PageQuery pageQuery, LicenseListReq req) {
		// TODO 参数校验
		return licenseService.page(pageQuery, req);
	}

	@PostMapping("create")
	public LicenseCreateRes create(@RequestBody LicenseCreateReq req) {
		// TODO 参数校验

		return licenseService.create(req);
	}

	@PostMapping("update")
	public LicenseUpdateRes update(@RequestBody LicenseUpdateReq req) {
		// TODO 参数校验
		return licenseService.update(req);
	}

	@GetMapping("detail")
	public LicenseDetailRes detail(@RequestParam("id") Integer id) {
		// TODO 参数校验
		return licenseService.detail(id);
	}

	@PostMapping("update/enable-status")
	public LicenseUpdateEnableStatusRes updateEnableStatus(@RequestBody LicenseUpdateEnableStatusReq req) {
		// TODO 参数校验

		return licenseService.updateEnableStatus(req);
	}

	@PostMapping("delete")
	public void delete(@RequestParam("id") Integer id) {
		// TODO 参数校验
		licenseService.delete(id);
	}

	@PostMapping("reset")
	public void reset(@RequestParam("id") Integer id) {
		// TODO 参数校验
		licenseService.reset(id);
	}

}

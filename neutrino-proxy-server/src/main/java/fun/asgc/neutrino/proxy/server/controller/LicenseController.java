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
import fun.asgc.neutrino.proxy.server.base.rest.annotation.OnlyAdmin;
import fun.asgc.neutrino.proxy.server.controller.req.LicenseCreateReq;
import fun.asgc.neutrino.proxy.server.controller.req.LicenseListReq;
import fun.asgc.neutrino.proxy.server.controller.req.LicenseUpdateEnableStatusReq;
import fun.asgc.neutrino.proxy.server.controller.req.LicenseUpdateReq;
import fun.asgc.neutrino.proxy.server.controller.res.*;
import fun.asgc.neutrino.proxy.server.service.LicenseService;
import fun.asgc.neutrino.proxy.server.util.ParamCheckUtil;
import org.noear.solon.annotation.*;

import java.util.List;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/8/6
 */
@Mapping("/license")
@Controller
public class LicenseController {
	@Inject
	private LicenseService licenseService;

	@Get
	@Mapping("/page")
	public PageInfo<LicenseListRes> page(PageQuery pageQuery, LicenseListReq req) {
		ParamCheckUtil.checkNotNull(pageQuery, "pageQuery");

		return licenseService.page(pageQuery, req);
	}

	@Mapping("/list")
	public List<LicenseListRes> list(LicenseListReq req) {
		return licenseService.list(req);
	}

	@OnlyAdmin
	@Post
	@Mapping("/create")
	public LicenseCreateRes create(LicenseCreateReq req) {
		ParamCheckUtil.checkNotNull(req, "req");
		ParamCheckUtil.checkNotEmpty(req.getName(), "name");
		ParamCheckUtil.checkNotNull(req.getUserId(), "userId");

		return licenseService.create(req);
	}

	@OnlyAdmin
	@Post
	@Mapping("/update")
	public LicenseUpdateRes update(LicenseUpdateReq req) {
		ParamCheckUtil.checkNotNull(req, "req");
		ParamCheckUtil.checkNotNull(req.getId(), "id");
		ParamCheckUtil.checkNotEmpty(req.getName(), "name");

		return licenseService.update(req);
	}

	@Post
	@Mapping("/detail")
	public LicenseDetailRes detail(Integer id) {
		ParamCheckUtil.checkNotNull(id, "id");

		return licenseService.detail(id);
	}

	@OnlyAdmin
	@Post
	@Mapping("/update/enable-status")
	public LicenseUpdateEnableStatusRes updateEnableStatus(LicenseUpdateEnableStatusReq req) {
		ParamCheckUtil.checkNotNull(req, "req");
		ParamCheckUtil.checkNotNull(req.getId(), "id");
		ParamCheckUtil.checkNotNull(req.getEnable(), "enable");

		return licenseService.updateEnableStatus(req);
	}

	@OnlyAdmin
	@Post
	@Mapping("/delete")
	public void delete(Integer id) {
		ParamCheckUtil.checkNotNull(id, "id");

		licenseService.delete(id);
	}

	@OnlyAdmin
	@Post
	@Mapping("/reset")
	public void reset(Integer id) {
		ParamCheckUtil.checkNotNull(id, "id");

		licenseService.reset(id);
	}

}

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
package org.dromara.neutrinoproxy.server.controller;

import org.dromara.neutrinoproxy.server.base.page.PageInfo;
import org.dromara.neutrinoproxy.server.base.page.PageQuery;
import org.dromara.neutrinoproxy.server.base.rest.Authorization;
import org.dromara.neutrinoproxy.server.controller.req.proxy.*;
import org.dromara.neutrinoproxy.server.controller.res.proxy.*;
import org.dromara.neutrinoproxy.server.controller.req.proxy.*;
import org.dromara.neutrinoproxy.server.controller.res.proxy.*;
import org.dromara.neutrinoproxy.server.service.LicenseService;
import org.dromara.neutrinoproxy.server.util.ParamCheckUtil;
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

	@Mapping("/auth-list")
	public List<LicenseListRes> queryCurUserLicense(LicenseListReq req) {
		return licenseService.queryCurUserLicense(req);
	}

	@Post
	@Mapping("/create")
	@Authorization(onlyAdmin = true)
	public LicenseCreateRes create(LicenseCreateReq req) {
		ParamCheckUtil.checkNotNull(req, "req");
		ParamCheckUtil.checkNotEmpty(req.getName(), "name");
		ParamCheckUtil.checkNotNull(req.getUserId(), "userId");
        ParamCheckUtil.checkBytesDesc(req.getUpLimitRate(), "upLimitRate");
        ParamCheckUtil.checkBytesDesc(req.getDownLimitRate(), "downLimitRate");

		return licenseService.create(req);
	}

	@Post
	@Mapping("/update")
	@Authorization(onlyAdmin = true)
	public LicenseUpdateRes update(LicenseUpdateReq req) {
		ParamCheckUtil.checkNotNull(req, "req");
		ParamCheckUtil.checkNotNull(req.getId(), "id");
		ParamCheckUtil.checkNotEmpty(req.getName(), "name");
        ParamCheckUtil.checkBytesDesc(req.getUpLimitRate(), "upLimitRate");
        ParamCheckUtil.checkBytesDesc(req.getDownLimitRate(), "downLimitRate");

		return licenseService.update(req);
	}

	@Post
	@Mapping("/detail")
	public LicenseDetailRes detail(LicenseDetailReq req) {
		ParamCheckUtil.checkNotNull(req, "req");
		ParamCheckUtil.checkNotNull(req.getId(), "id");

		return licenseService.detail(req.getId());
	}

	@Post
	@Mapping("/update/enable-status")
	@Authorization(onlyAdmin = true)
	public LicenseUpdateEnableStatusRes updateEnableStatus(LicenseUpdateEnableStatusReq req) {
		ParamCheckUtil.checkNotNull(req, "req");
		ParamCheckUtil.checkNotNull(req.getId(), "id");
		ParamCheckUtil.checkNotNull(req.getEnable(), "enable");

		return licenseService.updateEnableStatus(req);
	}

	@Post
	@Mapping("/delete")
	@Authorization(onlyAdmin = true)
	public void delete(LicenseDeleteReq req) {
		ParamCheckUtil.checkNotNull(req, "req");
		ParamCheckUtil.checkNotNull(req.getId(), "id");

		licenseService.delete(req.getId());
	}

	@Post
	@Mapping("/reset")
	@Authorization(onlyAdmin = true)
	public void reset(LicenseResetReq req) {
		ParamCheckUtil.checkNotNull(req, "req");
		ParamCheckUtil.checkNotNull(req.getId(), "id");

		licenseService.reset(req.getId());
	}

}

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
package fun.asgc.neutrino.proxy.server.service;

import fun.asgc.neutrino.core.annotation.Autowired;
import fun.asgc.neutrino.core.annotation.Component;
import fun.asgc.neutrino.core.web.annotation.RequestBody;
import fun.asgc.neutrino.proxy.server.base.rest.constant.EnableStatusEnum;
import fun.asgc.neutrino.proxy.server.base.rest.constant.OnlineStatusEnum;
import fun.asgc.neutrino.proxy.server.controller.req.LicenseCreateReq;
import fun.asgc.neutrino.proxy.server.controller.req.LicenseUpdateEnableStatusReq;
import fun.asgc.neutrino.proxy.server.controller.res.LicenseCreateRes;
import fun.asgc.neutrino.proxy.server.controller.res.LicenseUpdateEnableStatusRes;
import fun.asgc.neutrino.proxy.server.dal.LicenseMapper;
import fun.asgc.neutrino.proxy.server.dal.entity.LicenseDO;

import java.util.Date;
import java.util.UUID;

/**
 * license服务
 * @author: aoshiguchen
 * @date: 2022/8/6
 */
@Component
public class LicenseService {

	@Autowired
	private LicenseMapper licenseMapper;

	/**
	 * 创建license
	 * @param req
	 * @return
	 */
	public LicenseCreateRes create(LicenseCreateReq req) {
		String key = UUID.randomUUID().toString().replaceAll("-", "");
		Date now = new Date();

		licenseMapper.add(new LicenseDO()
			.setName(req.getName())
			.setKey(key)
			.setUserId(req.getUserId())
			.setIsOnline(OnlineStatusEnum.OFFLINE.getStatus())
			.setEnable(EnableStatusEnum.ENABLE.getStatus())
			.setCreateTime(now)
			.setUpdateTime(now)
		);
		return new LicenseCreateRes();
	}

	public LicenseUpdateEnableStatusRes updateEnableStatus(LicenseUpdateEnableStatusReq req) {
		licenseMapper.updateEnableStatus(req.getId(), req.getEnable());

		return new LicenseUpdateEnableStatusRes();
	}

}

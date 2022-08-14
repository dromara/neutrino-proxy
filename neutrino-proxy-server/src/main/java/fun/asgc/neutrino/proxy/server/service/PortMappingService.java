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
import fun.asgc.neutrino.core.db.page.Page;
import fun.asgc.neutrino.core.db.page.PageQuery;
import fun.asgc.neutrino.core.util.CollectionUtil;
import fun.asgc.neutrino.proxy.server.base.rest.constant.EnableStatusEnum;
import fun.asgc.neutrino.proxy.server.base.rest.constant.OnlineStatusEnum;
import fun.asgc.neutrino.proxy.server.controller.req.PortMappingCreateReq;
import fun.asgc.neutrino.proxy.server.controller.req.PortMappingListReq;
import fun.asgc.neutrino.proxy.server.controller.req.PortMappingUpdateEnableStatusReq;
import fun.asgc.neutrino.proxy.server.controller.req.PortMappingUpdateReq;
import fun.asgc.neutrino.proxy.server.controller.res.*;
import fun.asgc.neutrino.proxy.server.dal.LicenseMapper;
import fun.asgc.neutrino.proxy.server.dal.PortMappingMapper;
import fun.asgc.neutrino.proxy.server.dal.UserMapper;
import fun.asgc.neutrino.proxy.server.dal.entity.LicenseDO;
import fun.asgc.neutrino.proxy.server.dal.entity.PortMappingDO;
import fun.asgc.neutrino.proxy.server.dal.entity.UserDO;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/8/8
 */
@Component
public class PortMappingService {
	@Autowired
	private PortMappingMapper portMappingMapper;
	@Autowired
	private LicenseMapper licenseMapper;
	@Autowired
	private UserMapper userMapper;

	public Page<PortMappingListRes> page(PageQuery pageQuery, PortMappingListReq req) {
		Page<PortMappingListRes> page = Page.create(pageQuery);
		portMappingMapper.page(page, req);
		if (!CollectionUtil.isEmpty(page.getRecords())) {
			Set<Integer> licenseIds = page.getRecords().stream().map(PortMappingListRes::getLicenseId).collect(Collectors.toSet());
			List<LicenseDO> licenseList = licenseMapper.findByIds(licenseIds);
			Set<Integer> userIds = licenseList.stream().map(LicenseDO::getUserId).collect(Collectors.toSet());
			List<UserDO> userList = userMapper.findByIds(userIds);
			Map<Integer, LicenseDO> licenseMap = licenseList.stream().collect(Collectors.toMap(LicenseDO::getId, Function.identity()));
			Map<Integer, UserDO> userMap = userList.stream().collect(Collectors.toMap(UserDO::getId, Function.identity()));
			page.getRecords().forEach(item -> {
				LicenseDO license = licenseMap.get(item.getLicenseId());
				if (null == license) {
					return;
				}
				item.setLicenseName(license.getName());
				item.setUserId(license.getUserId());
				UserDO user = userMap.get(license.getUserId());
				if (null == user) {
					return;
				}
				item.setUserName(user.getName());
			});
		}
		return page;
	}

	public PortMappingCreateRes create(PortMappingCreateReq req) {
		Date now = new Date();
		PortMappingDO portMappingDO = new PortMappingDO();
		portMappingDO.setLicenseId(req.getLicenseId());
		portMappingDO.setServerPort(req.getServerPort());
		portMappingDO.setClientIp(req.getClientIp());
		portMappingDO.setClientPort(req.getClientPort());
		portMappingDO.setIsOnline(OnlineStatusEnum.OFFLINE.getStatus());
		portMappingDO.setEnable(EnableStatusEnum.ENABLE.getStatus());
		portMappingDO.setCreateTime(now);
		portMappingDO.setUpdateTime(now);
		portMappingMapper.add(portMappingDO);
		return new PortMappingCreateRes();
	}

	public PortMappingUpdateRes update(PortMappingUpdateReq req) {
		PortMappingDO portMappingDO = new PortMappingDO();
		portMappingDO.setId(req.getId());
		portMappingDO.setLicenseId(req.getLicenseId());
		portMappingDO.setServerPort(req.getServerPort());
		portMappingDO.setClientIp(req.getClientIp());
		portMappingDO.setClientPort(req.getClientPort());
		portMappingDO.setUpdateTime(new Date());
		portMappingMapper.update(portMappingDO);
		return new PortMappingUpdateRes();
	}

	public PortMappingDetailRes detail(Integer id) {
		PortMappingDO portMappingDO = portMappingMapper.findById(id);
		if (null == portMappingDO) {
			return null;
		}
		PortMappingDetailRes res = new PortMappingDetailRes()
			.setId(portMappingDO.getId())
			.setLicenseId(portMappingDO.getLicenseId())
			.setServerPort(portMappingDO.getServerPort())
			.setClientIp(portMappingDO.getClientIp())
			.setClientPort(portMappingDO.getClientPort())
			.setIsOnline(portMappingDO.getIsOnline())
			.setEnable(portMappingDO.getEnable())
			.setCreateTime(portMappingDO.getCreateTime())
			.setUpdateTime(portMappingDO.getUpdateTime());

		LicenseDO license = licenseMapper.findById(portMappingDO.getLicenseId());
		if (null != license) {
			res.setLicenseName(license.getName());
			res.setUserId(license.getUserId());
			UserDO user = userMapper.findById(license.getUserId());
			if (null != user) {
				res.setUserName(user.getName());
			}
		}

		return res;
	}

	public PortMappingUpdateEnableStatusRes updateEnableStatus(PortMappingUpdateEnableStatusReq req) {
		portMappingMapper.updateEnableStatus(req.getId(), req.getEnable());

		return new PortMappingUpdateEnableStatusRes();
	}

	public void delete(Integer id) {
		portMappingMapper.delete(id);
	}

}

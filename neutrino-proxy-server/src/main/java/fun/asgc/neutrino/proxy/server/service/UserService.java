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
import fun.asgc.neutrino.core.util.DateUtil;
import fun.asgc.neutrino.core.web.annotation.GetMapping;
import fun.asgc.neutrino.proxy.server.base.rest.constant.ExceptionConstant;
import fun.asgc.neutrino.proxy.server.base.rest.ServiceException;
import fun.asgc.neutrino.proxy.server.base.rest.SystemContextHolder;
import fun.asgc.neutrino.proxy.server.controller.req.LoginReq;
import fun.asgc.neutrino.proxy.server.controller.req.UserListReq;
import fun.asgc.neutrino.proxy.server.controller.res.LicenseListRes;
import fun.asgc.neutrino.proxy.server.controller.res.LoginRes;
import fun.asgc.neutrino.proxy.server.controller.res.UserInfoRes;
import fun.asgc.neutrino.proxy.server.controller.res.UserListRes;
import fun.asgc.neutrino.proxy.server.dal.UserLoginRecordMapper;
import fun.asgc.neutrino.proxy.server.dal.UserMapper;
import fun.asgc.neutrino.proxy.server.dal.UserTokenMapper;
import fun.asgc.neutrino.proxy.server.dal.entity.UserDO;
import fun.asgc.neutrino.proxy.server.dal.entity.UserLoginRecordDO;
import fun.asgc.neutrino.proxy.server.dal.entity.UserTokenDO;
import fun.asgc.neutrino.proxy.server.util.Md5Util;
import fun.asgc.neutrino.proxy.server.util.ParamCheckUtil;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/7/31
 */
@Component
public class UserService {
	@Autowired
	private UserMapper userMapper;
	@Autowired
	private UserTokenMapper userTokenMapper;
	@Autowired
	private UserLoginRecordMapper userLoginRecordMapper;

	public LoginRes login(LoginReq req) {
		UserDO userDO = userMapper.findByLoginName(req.getLoginName());
		if (null == userDO || !Md5Util.encode(req.getLoginPassword()).equals(userDO.getLoginPassword())) {
			throw ServiceException.create(ExceptionConstant.USER_NAME_OR_PASSWORD_ERROR);
		}
		String token = UUID.randomUUID().toString().replaceAll("-", "");

		Date now = new Date();
		Date expirationTime = DateUtil.addDate(now, Calendar.HOUR, 1);

		// 缓存token
		userTokenMapper.add(new UserTokenDO()
			.setToken(token)
			.setUserId(userDO.getId())
			.setExpirationTime(expirationTime)
			.setCreateTime(now)
			.setUpdateTime(now)
		);

		// 新增用户登录日志
		userLoginRecordMapper.add(new UserLoginRecordDO()
			.setUserId(userDO.getId())
			.setIp(SystemContextHolder.getIp())
			.setToken(token)
			.setType(UserLoginRecordDO.TYPE_LOGIN)
			.setCreateTime(now)
		);

		return new LoginRes()
			.setToken(token)
			.setUserId(userDO.getId())
			.setUserName(userDO.getName());
	}

	public void logout() {
		userTokenMapper.deleteByToken(SystemContextHolder.getToken());

		// 新增用户登录日志
		userLoginRecordMapper.add(new UserLoginRecordDO()
			.setUserId(SystemContextHolder.getUser().getId())
			.setIp(SystemContextHolder.getIp())
			.setToken(SystemContextHolder.getToken())
			.setType(UserLoginRecordDO.TYPE_LOGOUT)
			.setCreateTime(new Date())
		);
	}

	public UserDO findByToken(String token) {
		Date now = new Date();
		UserTokenDO userTokenDO = userTokenMapper.findByAvailableToken(token, now.getTime());
		if (null == userTokenDO) {
			return null;
		}
		return userMapper.findById(userTokenDO.getUserId());
	}

	public void updateTokenExpirationTime(String token) {
		Date now = new Date();
		Date expirationTime = DateUtil.addDate(now, Calendar.HOUR, 1);
		userTokenMapper.updateTokenExpirationTime(token, expirationTime);
	}

	public Page<UserListRes> page(PageQuery pageQuery, UserListReq req) {
		Page<UserListRes> page = Page.create(pageQuery);
		userMapper.page(page, req);
		return page;
	}

	public List<UserListRes> list(UserListReq req) {
		List<UserListRes> list = userMapper.list();
		return list;
	}

	public UserInfoRes info() {
		UserDO userDO = userMapper.findById(SystemContextHolder.getUser().getId());
		if (null == userDO) {
			return null;
		}
		return new UserInfoRes()
			.setId(userDO.getId())
			.setName(userDO.getName())
			.setLoginName(userDO.getLoginName())
			.setCreateTime(userDO.getCreateTime())
			.setUpdateTime(userDO.getUpdateTime());
	}
}

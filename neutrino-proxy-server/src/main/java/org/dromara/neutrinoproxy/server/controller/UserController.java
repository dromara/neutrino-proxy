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
import org.dromara.neutrinoproxy.server.base.rest.SystemContextHolder;
import org.dromara.neutrinoproxy.server.constant.ExceptionConstant;
import org.dromara.neutrinoproxy.server.controller.req.system.*;
import org.dromara.neutrinoproxy.server.controller.res.system.*;
import org.dromara.neutrinoproxy.server.controller.res.system.*;
import org.dromara.neutrinoproxy.server.dal.UserMapper;
import org.dromara.neutrinoproxy.server.dal.entity.UserDO;
import org.dromara.neutrinoproxy.server.service.UserService;
import org.dromara.neutrinoproxy.server.util.Md5Util;
import org.dromara.neutrinoproxy.server.util.ParamCheckUtil;
import org.dromara.neutrinoproxy.server.controller.req.system.*;
import org.noear.solon.annotation.*;

import java.util.List;

/**
 * 用户管理
 * @author: aoshiguchen
 * @date: 2022/7/31
 */
@Mapping("/user")
@Controller
public class UserController {
	@Inject
	private UserService userService;
	@Inject
	private UserMapper userMapper;

	@Get
	@Mapping("/page")
	public PageInfo<UserListRes> page(PageQuery pageQuery, UserListReq req) {
		ParamCheckUtil.checkNotNull(pageQuery, "pageQuery");

		return userService.page(pageQuery, req);
	}

	@Get
	@Mapping("/list")
	public List<UserListRes> list(UserListReq req) {

		return userService.list(req);
	}

	@Get
	@Mapping("/info")
	public UserInfoRes info(UserInfoReq req) {
		return userService.info(req);
	}

	@Post
	@Mapping("/update/enable-status")
	@Authorization(onlyAdmin = true)
	public UserUpdateEnableStatusRes updateEnableStatus(UserUpdateEnableStatusReq req) {
		ParamCheckUtil.checkNotNull(req, "req");
		ParamCheckUtil.checkNotNull(req.getId(), "id");
		ParamCheckUtil.checkNotNull(req.getEnable(), "enable");

		return userService.updateEnableStatus(req);
	}

	@Post
	@Mapping("/create")
	@Authorization(onlyAdmin = true)
	public UserCreateRes create(UserCreateReq req) {
		ParamCheckUtil.checkNotNull(req, "req");
		ParamCheckUtil.checkNotEmpty(req.getName(), "name");
		ParamCheckUtil.checkNotEmpty(req.getLoginName(), "loginName");

		return userService.create(req);
	}

	@Post
	@Mapping("/update")
	@Authorization(onlyAdmin = true)
	public UserUpdateRes update(UserUpdateReq req) {
		ParamCheckUtil.checkNotNull(req, "req");
		ParamCheckUtil.checkNotNull(req.getId(), "id");
		ParamCheckUtil.checkNotEmpty(req.getName(), "name");
		ParamCheckUtil.checkNotEmpty(req.getLoginName(), "loginName");

		return userService.update(req);
	}

	@Post
	@Mapping("/update/password")
	@Authorization(onlyAdmin = true)
	public UserUpdatePasswordRes updatePassword(UserUpdatePasswordReq req) {
		ParamCheckUtil.checkNotNull(req, "req");
		ParamCheckUtil.checkNotNull(req.getId(), "id");
		ParamCheckUtil.checkNotEmpty(req.getLoginPassword(), "loginPassword");
		ParamCheckUtil.checkExpression(req.getLoginPassword().length() >= 6, ExceptionConstant.LOGIN_PASSWORD_LENGTH_CHECK_FAIL);

		return userService.updatePassword(req);
	}

	@Post
	@Mapping("/current-user/update/password")
	public UserUpdatePasswordRes currentUserUpdatePassword(UserUpdatePasswordReq req) {
		ParamCheckUtil.checkNotNull(req, "req");
		ParamCheckUtil.checkNotEmpty(req.getOldLoginPassword(), "oldLoginPassword");
		ParamCheckUtil.checkNotEmpty(req.getLoginPassword(), "loginPassword");
		req.setId(SystemContextHolder.getUserId());
		ParamCheckUtil.checkExpression(req.getLoginPassword().length() >= 6, ExceptionConstant.LOGIN_PASSWORD_LENGTH_CHECK_FAIL);
		// 验证原密码
		Integer userId = req.getId();
		UserDO userDO = userMapper.findById(userId);
		ParamCheckUtil.checkExpression(Md5Util.encode(req.getOldLoginPassword()).equals(userDO.getLoginPassword()), ExceptionConstant.ORIGIN_PASSWORD_CHECK_FAIL);

		return userService.updatePassword(req);
	}

	@Post
	@Mapping("/delete")
	@Authorization(onlyAdmin = true)
	public void delete(UserDeleteReq req) {
		ParamCheckUtil.checkNotNull(req, "req");
		ParamCheckUtil.checkNotNull(req.getId(), "id");

		userService.delete(req.getId());
	}
}

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
import fun.asgc.neutrino.proxy.server.base.rest.SystemContextHolder;
import fun.asgc.neutrino.proxy.server.base.rest.annotation.OnlyAdmin;
import fun.asgc.neutrino.proxy.server.constant.ExceptionConstant;
import fun.asgc.neutrino.proxy.server.controller.req.*;
import fun.asgc.neutrino.proxy.server.controller.res.*;
import fun.asgc.neutrino.proxy.server.dal.UserMapper;
import fun.asgc.neutrino.proxy.server.dal.entity.UserDO;
import fun.asgc.neutrino.proxy.server.service.UserService;
import fun.asgc.neutrino.proxy.server.util.Md5Util;
import fun.asgc.neutrino.proxy.server.util.ParamCheckUtil;

import java.util.List;

/**
 * 用户管理
 * @author: aoshiguchen
 * @date: 2022/7/31
 */
@NonIntercept
@RequestMapping("user")
@RestController
public class UserController {
	@Autowired
	private UserService userService;
	@Autowired
	private UserMapper userMapper;

	@GetMapping("page")
	public Page<UserListRes> page(PageQuery pageQuery, UserListReq req) {
		ParamCheckUtil.checkNotNull(pageQuery, "pageQuery");

		return userService.page(pageQuery, req);
	}

	@GetMapping("list")
	public List<UserListRes> list(UserListReq req) {

		return userService.list(req);
	}

	@GetMapping("info")
	public UserInfoRes info(UserInfoReq req) {
		return userService.info(req);
	}

	@OnlyAdmin
	@PostMapping("update/enable-status")
	public UserUpdateEnableStatusRes updateEnableStatus(@RequestBody UserUpdateEnableStatusReq req) {
		ParamCheckUtil.checkNotNull(req, "req");
		ParamCheckUtil.checkNotNull(req.getId(), "id");
		ParamCheckUtil.checkNotNull(req.getEnable(), "enable");

		return userService.updateEnableStatus(req);
	}

	@OnlyAdmin
	@PostMapping("create")
	public UserCreateRes create(@RequestBody UserCreateReq req) {
		ParamCheckUtil.checkNotNull(req, "req");
		ParamCheckUtil.checkNotEmpty(req.getName(), "name");
		ParamCheckUtil.checkNotEmpty(req.getLoginName(), "loginName");

		return userService.create(req);
	}

	@OnlyAdmin
	@PostMapping("update")
	public UserUpdateRes update(@RequestBody UserUpdateReq req) {
		ParamCheckUtil.checkNotNull(req, "req");
		ParamCheckUtil.checkNotNull(req.getId(), "id");
		ParamCheckUtil.checkNotEmpty(req.getName(), "name");
		ParamCheckUtil.checkNotEmpty(req.getLoginName(), "loginName");

		return userService.update(req);
	}

	@OnlyAdmin
	@PostMapping("update/password")
	public UserUpdatePasswordRes updatePassword(@RequestBody UserUpdatePasswordReq req) {
		ParamCheckUtil.checkNotNull(req, "req");
		ParamCheckUtil.checkNotNull(req.getId(), "id");
		ParamCheckUtil.checkNotEmpty(req.getLoginPassword(), "loginPassword");
		ParamCheckUtil.checkExpression(req.getLoginPassword().length() >= 6, ExceptionConstant.LOGIN_PASSWORD_LENGTH_CHECK_FAIL);

		return userService.updatePassword(req);
	}

	@PostMapping("current-user/update/password")
	public UserUpdatePasswordRes currentUserUpdatePassword(@RequestBody UserUpdatePasswordReq req) {
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

	@OnlyAdmin
	@PostMapping("delete")
	public void delete(@RequestParam("id") Integer id) {
		ParamCheckUtil.checkNotNull(id, "id");

		userService.delete(id);
	}
}

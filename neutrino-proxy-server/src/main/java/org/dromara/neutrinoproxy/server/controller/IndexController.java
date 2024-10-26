package org.dromara.neutrinoproxy.server.controller;

import org.dromara.neutrinoproxy.server.base.rest.Authorization;
import org.dromara.neutrinoproxy.server.controller.req.system.LoginReq;
import org.dromara.neutrinoproxy.server.controller.res.system.LoginRes;
import org.dromara.neutrinoproxy.server.service.UserService;
import org.dromara.neutrinoproxy.server.util.ParamCheckUtil;
import org.noear.solon.annotation.*;
import org.noear.solon.core.handle.Context;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/7/31
 */
@Controller
public class IndexController {
	@Inject
	private UserService userService;

	@Authorization(login = false)
	@Get
	@Mapping("/")
	public void home(Context ctx) {
		ctx.forward("/index.html");
	}

	@Authorization(login = false)
	@Post
	@Mapping("/login")
	public LoginRes login(LoginReq req) {
		ParamCheckUtil.checkNotEmpty(req.getLoginName(), "loginName");
		ParamCheckUtil.checkNotEmpty(req.getLoginPassword(), "loginPassword");

		return userService.login(req);
	}

	@Post
	@Mapping("/logout")
	public void logout() {
		userService.logout();
	}
}

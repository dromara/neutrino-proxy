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

import fun.asgc.neutrino.proxy.server.base.rest.Authorization;
import fun.asgc.neutrino.proxy.server.controller.req.LoginReq;
import fun.asgc.neutrino.proxy.server.controller.res.LoginRes;
import fun.asgc.neutrino.proxy.server.service.UserService;
import fun.asgc.neutrino.proxy.server.util.ParamCheckUtil;
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

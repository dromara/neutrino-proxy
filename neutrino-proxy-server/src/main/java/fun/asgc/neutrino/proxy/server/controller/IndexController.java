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

import fun.asgc.neutrino.core.annotation.NonIntercept;
import fun.asgc.neutrino.core.web.annotation.PostMapping;
import fun.asgc.neutrino.core.web.annotation.RequestBody;
import fun.asgc.neutrino.core.web.annotation.RequestMapping;
import fun.asgc.neutrino.core.web.annotation.RestController;
import fun.asgc.neutrino.proxy.server.base.rest.ExceptionConstant;
import fun.asgc.neutrino.proxy.server.base.rest.ServiceException;
import fun.asgc.neutrino.proxy.server.controller.req.LoginReq;
import fun.asgc.neutrino.proxy.server.controller.res.LoginRes;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/7/31
 */
@NonIntercept
@RequestMapping
@RestController
public class IndexController {

	@PostMapping("login")
	public LoginRes login(@RequestBody LoginReq req) {
		if (null == req) {
			throw ServiceException.create(ExceptionConstant.PARAMS_INVALID);
		}
		return new LoginRes()
			.setToken("1111")
			.setUserId(1)
			.setUserName("张三");
	}

}

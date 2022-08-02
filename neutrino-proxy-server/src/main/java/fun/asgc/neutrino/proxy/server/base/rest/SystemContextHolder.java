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
package fun.asgc.neutrino.proxy.server.base.rest;

import fun.asgc.neutrino.proxy.server.dal.entity.UserDO;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/8/2
 */
public class SystemContextHolder {
	private static final ThreadLocal<UserDO> userHolder = new ThreadLocal<>();
	private static final ThreadLocal<String> tokenHolder = new ThreadLocal<>();

	public static void remove() {
		userHolder.remove();
		tokenHolder.remove();
	}

	public static void setUser(UserDO user) {
		userHolder.set(user);
	}

	public static UserDO getUser() {
		return userHolder.get();
	}

	public static void setToken(String token) {
		tokenHolder.set(token);
	}

	public static String getToken() {
		return tokenHolder.get();
	}
}

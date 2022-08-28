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
package fun.asgc.neutrino.proxy.server.dal;

import fun.asgc.neutrino.core.annotation.Component;
import fun.asgc.neutrino.core.annotation.Param;
import fun.asgc.neutrino.core.db.annotation.ResultType;
import fun.asgc.neutrino.core.db.annotation.Select;
import fun.asgc.neutrino.core.db.annotation.Update;
import fun.asgc.neutrino.core.db.mapper.SqlMapper;
import fun.asgc.neutrino.core.db.page.Page;
import fun.asgc.neutrino.proxy.server.controller.req.UserListReq;
import fun.asgc.neutrino.proxy.server.controller.res.UserListRes;
import fun.asgc.neutrino.proxy.server.dal.entity.UserDO;

import java.util.List;
import java.util.Set;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/8/1
 */
@Component
public interface UserMapper extends SqlMapper {

	/**
	 * 根据登录名查询用户记录
	 * @param loginName
	 * @return
	 */
	@Select("select * from user where login_name = ?")
	UserDO findByLoginName(String loginName);

	/**
	 * 根据id查询单条记录
	 * @param id
	 * @return
	 */
	@Select("select * from user where id = ?")
	UserDO findById(Integer id);

	@ResultType(UserDO.class)
	@Select("select * from user where id in (:ids)")
	List<UserDO> findByIds(@Param("ids") Set<Integer> ids);

	@ResultType(UserListRes.class)
	@Select("select * from user")
	void page(Page page, UserListReq req);

	@ResultType(UserListRes.class)
	@Select("select * from user where enable = 1")
	List<UserListRes> list();

	@Update("update `user` set enable = :enable where id = :id")
	void updateEnableStatus(@Param("id") Integer id, @Param("enable") Integer enable);
}

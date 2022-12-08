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
import fun.asgc.neutrino.core.aop.Intercept;
import fun.asgc.neutrino.core.db.annotation.Delete;
import fun.asgc.neutrino.core.db.annotation.Update;
import fun.asgc.neutrino.core.db.mapper.SqlMapper;
import fun.asgc.neutrino.proxy.server.dal.entity.UserTokenDO;

import java.util.Date;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/8/1
 */
@Intercept(ignoreGlobal = true)
@Component
public interface UserTokenMapper extends SqlMapper {
	/**
	 * 新增用户token
	 * 支持注解 + xml配置2种方式
	 * @param userToken
	 * @return
	 */
//	@Insert("insert into `user_token`(`token`,`user_id`,`expiration_time`,`create_time`,`update_time`) values (:token,:userId,:expirationTime,:createTime,:updateTime)")
	int  add(UserTokenDO userToken);

	/**
	 * 根据token查询单条记录
	 * 支持注解 + xml配置2种方式
	 * @param token
	 * @param time
	 * @return
	 */
//	@Select("select * from user_token where token = ? and expiration_time > ?")
	UserTokenDO findByAvailableToken(String token, Date date);

	/**
	 * 根据token删除记录
	 * @param token
	 */
	@Delete("delete from user_token where token = ?")
	void deleteByToken(String token);

	@Update("update user_token set expiration_time = :expirationTime where token = :token")
	void updateTokenExpirationTime(@Param("token") String token, @Param("expirationTime") Date expirationTime);

	/**
	 * 根据userId删除token
	 * @param userId
	 */
	@Delete("delete from user_token where user_id = ?")
	void deleteByUserId(Integer userId);
}

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
package org.dromara.neutrinoproxy.server.dal;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.dromara.neutrinoproxy.server.dal.entity.UserTokenDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Date;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/8/1
 */
@Mapper
public interface UserTokenMapper extends BaseMapper<UserTokenDO> {

	/**
	 * 根据token查询单条记录
	 * 支持注解 + xml配置2种方式
	 * @param token
	 * @param time
	 * @return
	 */
	default UserTokenDO findByAvailableToken(String token, Date date) {
		return selectOne(new LambdaQueryWrapper<UserTokenDO>()
				.eq(UserTokenDO::getToken, token)
				.gt(UserTokenDO::getExpirationTime, date)
		);
	}

	/**
	 * 根据token删除记录
	 * @param token
	 */
	default void deleteByToken(String token) {
		this.delete(new LambdaQueryWrapper<UserTokenDO>()
				.eq(UserTokenDO::getToken, token)
		);
	}

	default void updateTokenExpirationTime(String token, Date expirationTime) {
		this.update(null, new LambdaUpdateWrapper<UserTokenDO>()
				.eq(UserTokenDO::getToken, token)
				.set(UserTokenDO::getExpirationTime, expirationTime)
		);
	}

	/**
	 * 根据userId删除token
	 * @param userId
	 */
	default void deleteByUserId(Integer userId) {
		this.delete(new LambdaQueryWrapper<UserTokenDO>()
				.eq(UserTokenDO::getUserId, userId)
		);
	}
}

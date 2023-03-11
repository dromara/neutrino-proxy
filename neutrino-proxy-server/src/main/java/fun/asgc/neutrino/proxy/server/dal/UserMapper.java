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

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import fun.asgc.neutrino.core.db.annotation.Insert;
import fun.asgc.neutrino.proxy.server.dal.entity.UserDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/8/1
 */
@Mapper
public interface UserMapper extends BaseMapper<UserDO> {

	/**
	 * 根据登录名查询用户记录
	 * @param loginName
	 * @return
	 */
	default UserDO findByLoginName(String loginName) {
		return selectOne(new LambdaQueryWrapper<UserDO>()
				.eq(UserDO::getLoginName, loginName)
				.last("limit 1")
		);
	}

	/**
	 * 根据id查询单条记录
	 * @param id
	 * @return
	 */
	default UserDO findById(Integer id) {
		return selectById(id);
	}

	default List<UserDO> findByIds(Set<Integer> ids) {
		return selectBatchIds(ids);
	}

	default void updateEnableStatus(Integer id, Integer enable, Date updateTime) {
		this.update(null, new LambdaUpdateWrapper<UserDO>()
				.eq(UserDO::getId, id)
				.set(UserDO::getEnable, enable)
				.set(UserDO::getUpdateTime, updateTime)
		);
	}

	default void updateLoginPassword(Integer id, String loginPassword, Date updateTime) {
		this.update(null, new LambdaUpdateWrapper<UserDO>()
				.eq(UserDO::getId, id)
				.set(UserDO::getLoginPassword, loginPassword)
				.set(UserDO::getUpdateTime, updateTime)
		);
	}

	@Insert("insert into user(`name`,`login_name`,`login_password`,`enable`,`create_time`,`update_time`) values(:name,:loginName,:loginPassword,:enable,:createTime,:updateTime)")
	void add(UserDO user);
}

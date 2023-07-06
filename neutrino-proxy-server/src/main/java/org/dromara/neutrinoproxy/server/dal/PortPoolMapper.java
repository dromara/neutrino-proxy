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
import org.dromara.neutrinoproxy.server.controller.req.system.PortPoolListReq;
import org.dromara.neutrinoproxy.server.controller.res.system.PortPoolListRes;
import org.dromara.neutrinoproxy.server.dal.entity.PortPoolDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/8/7
 */
@Mapper
public interface PortPoolMapper extends BaseMapper<PortPoolDO> {
	default void updateEnableStatus(Integer id, Integer enable, Date updateTime) {
		this.update(null, new LambdaUpdateWrapper<PortPoolDO>()
				.eq(PortPoolDO::getId, id)
				.set(PortPoolDO::getEnable, enable)
				.set(PortPoolDO::getUpdateTime, updateTime)
		);
	}

	default PortPoolDO findByPort(Integer port) {
		return this.selectOne(new LambdaQueryWrapper<PortPoolDO>()
				.eq(PortPoolDO::getPort, port)
		);
	}

	default PortPoolDO findById(Integer id) {
		return this.selectById(id);
	}

	default List<PortPoolDO> getByGroupId(String groupId){
		return this.selectList(
				new LambdaQueryWrapper<PortPoolDO>()
						.eq(PortPoolDO::getGroupId, groupId)
		);
	}

	List<PortPoolListRes> selectResList(@Param("req") PortPoolListReq req);

    List<PortPoolListRes> getAvailablePortList(@Param("licenseId") Integer licenseId,@Param("userId") Integer userId, @Param("keyword") String keyword);
}

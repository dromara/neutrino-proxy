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
import fun.asgc.neutrino.core.util.CollectionUtil;
import fun.asgc.neutrino.proxy.server.constant.EnableStatusEnum;
import fun.asgc.neutrino.proxy.server.dal.entity.PortMappingDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/8/8
 */
@Mapper
public interface PortMappingMapper extends BaseMapper<PortMappingDO> {

	default PortMappingDO findById(Integer id) {
		return this.selectById(id);
	}

	default void updateEnableStatus(Integer id, Integer enable, Date updateTime) {
		this.update(null, new LambdaUpdateWrapper<PortMappingDO>()
				.eq(PortMappingDO::getId, id)
				.set(PortMappingDO::getEnable, enable)
				.set(PortMappingDO::getUpdateTime, updateTime)
		);
	}

	default PortMappingDO findByPort(Integer port, Set<Integer> excludeIds) {
		return this.selectOne(new LambdaQueryWrapper<PortMappingDO>()
				.eq(PortMappingDO::getServerPort, port)
				.notIn(!CollectionUtil.isEmpty(excludeIds), PortMappingDO::getId, excludeIds)
				.last("limit 1")
		);
	}

	default List<PortMappingDO> findEnableListByLicenseId(Integer licenseId) {
		return this.selectList(new LambdaQueryWrapper<PortMappingDO>()
				.eq(PortMappingDO::getLicenseId, licenseId)
				.eq(PortMappingDO::getEnable, EnableStatusEnum.ENABLE.getStatus())
		);
	}

	default List<PortMappingDO> findListByServerPort(Integer serverPort) {
		return this.selectList(new LambdaQueryWrapper<PortMappingDO>()
				.eq(PortMappingDO::getServerPort, serverPort)
		);
	}

	default List<PortMappingDO> findListByLicenseId(Integer licenseId) {
		return this.selectList(new LambdaQueryWrapper<PortMappingDO>()
				.eq(PortMappingDO::getLicenseId, licenseId)
		);
	}

	default void updateOnlineStatus(Integer licenseId,Integer serverPort, Integer isOnline, Date updateTime) {
		this.update(null, new LambdaUpdateWrapper<PortMappingDO>()
				.eq(PortMappingDO::getLicenseId, licenseId)
				.eq(PortMappingDO::getServerPort, serverPort)
				.set(PortMappingDO::getIsOnline, isOnline)
				.set(PortMappingDO::getUpdateTime, updateTime)
		);
	}

	default void updateOnlineStatus(Integer licenseId, Integer isOnline, Date updateTime) {
		this.update(null, new LambdaUpdateWrapper<PortMappingDO>()
				.eq(PortMappingDO::getLicenseId, licenseId)
				.set(PortMappingDO::getIsOnline, isOnline)
				.set(PortMappingDO::getUpdateTime, updateTime)
		);
	}

	default void updateOnlineStatus(Integer isOnline, Date updateTime) {
		this.update(null, new LambdaUpdateWrapper<PortMappingDO>()
				.set(PortMappingDO::getIsOnline, isOnline)
				.set(PortMappingDO::getUpdateTime, updateTime)
		);
	}
}

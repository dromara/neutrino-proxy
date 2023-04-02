package org.dromara.neutrinoproxy.server.dal;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.dromara.neutrinoproxy.server.constant.EnableStatusEnum;
import org.dromara.neutrinoproxy.server.controller.req.proxy.PortMappingListReq;
import org.dromara.neutrinoproxy.server.dal.entity.PortMappingDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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

	/**
	 * 校验子域名是否重复
	 * @param subdomain
	 * @return
	 */
	default Boolean checkRepeatBySubdomain(String subdomain, Set<Integer> excludeIds) {
		return this.selectCount(new LambdaQueryWrapper<PortMappingDO>()
				.eq(PortMappingDO::getSubdomain, subdomain)
				.notIn(!CollectionUtil.isEmpty(excludeIds), PortMappingDO::getId, excludeIds)
		).intValue() > 0;
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

	List<PortMappingDO> selectPortMappingByCondition(@Param("req") PortMappingListReq req);
}

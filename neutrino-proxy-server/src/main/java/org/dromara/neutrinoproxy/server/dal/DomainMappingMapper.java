package org.dromara.neutrinoproxy.server.dal;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.dromara.neutrinoproxy.server.constant.EnableStatusEnum;
import org.dromara.neutrinoproxy.server.controller.req.proxy.PortMappingListReq;
import org.dromara.neutrinoproxy.server.dal.entity.DomainMappingDO;
import org.dromara.neutrinoproxy.server.dal.entity.DomainMappingDO;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/8/8
 */
@Mapper
public interface DomainMappingMapper extends BaseMapper<DomainMappingDO> {

	default DomainMappingDO findById(Integer id) {
		return this.selectById(id);
	}

	default void updateEnableStatus(Integer id, Integer enable, Date updateTime) {
		this.update(null, new LambdaUpdateWrapper<DomainMappingDO>()
				.eq(DomainMappingDO::getId, id)
				.set(DomainMappingDO::getEnable, enable)
				.set(DomainMappingDO::getUpdateTime, updateTime)
		);
	}

	default DomainMappingDO findByDomain(String domain, Set<Integer> excludeIds) {
		return this.selectOne(new LambdaQueryWrapper<DomainMappingDO>()
				.eq(DomainMappingDO::getDomain, domain)
				.notIn(!CollectionUtil.isEmpty(excludeIds), DomainMappingDO::getId, excludeIds)
				.last("limit 1")
		);
	}

	/**
	 * 校验域名是否重复
	 * @param subdomain
	 * @return
	 */
	default Boolean checkRepeatByDomain(String subdomain, Set<Integer> excludeIds) {
		return StringUtils.isEmpty(subdomain)? Boolean.FALSE : this.selectCount(new LambdaQueryWrapper<DomainMappingDO>()
				.eq(DomainMappingDO::getDomain, subdomain)
				.notIn(!CollectionUtil.isEmpty(excludeIds), DomainMappingDO::getId, excludeIds)
		).intValue() > 0;
	}

	default List<DomainMappingDO> findEnableListByLicenseId(Integer licenseId) {
		return this.selectList(new LambdaQueryWrapper<DomainMappingDO>()
				.eq(DomainMappingDO::getLicenseId, licenseId)
				.eq(DomainMappingDO::getEnable, EnableStatusEnum.ENABLE.getStatus())
		);
	}

	default List<DomainMappingDO> findListByServerPort(Integer serverPort) {
		return this.selectList(new LambdaQueryWrapper<DomainMappingDO>()
				.eq(DomainMappingDO::getServerPort, serverPort)
		);
	}

	default List<DomainMappingDO> findListByLicenseId(Integer licenseId) {
		return this.selectList(new LambdaQueryWrapper<DomainMappingDO>()
				.eq(DomainMappingDO::getLicenseId, licenseId)
		);
	}

	default DomainMappingDO findByLicenseIdAndServerPort(Integer licenseId, Integer serverPort) {
		return this.selectOne(new LambdaQueryWrapper<DomainMappingDO>()
				.eq(DomainMappingDO::getLicenseId, licenseId)
				.eq(DomainMappingDO::getServerPort, serverPort)
				.last("limit 1")
		);
	}

	default void updateOnlineStatus(Integer licenseId,Integer serverPort, Integer isOnline, Date updateTime) {
		this.update(null, new LambdaUpdateWrapper<DomainMappingDO>()
				.eq(DomainMappingDO::getLicenseId, licenseId)
				.eq(DomainMappingDO::getServerPort, serverPort)
				.set(DomainMappingDO::getIsOnline, isOnline)
				.set(DomainMappingDO::getUpdateTime, updateTime)
		);
	}

	default void updateOnlineStatus(Integer licenseId, Integer isOnline, Date updateTime) {
		this.update(null, new LambdaUpdateWrapper<DomainMappingDO>()
				.eq(DomainMappingDO::getLicenseId, licenseId)
				.set(DomainMappingDO::getIsOnline, isOnline)
				.set(DomainMappingDO::getUpdateTime, updateTime)
		);
	}

	default void updateOnlineStatus(Integer isOnline, Date updateTime) {
		this.update(null, new LambdaUpdateWrapper<DomainMappingDO>()
				.set(DomainMappingDO::getIsOnline, isOnline)
				.set(DomainMappingDO::getUpdateTime, updateTime)
		);
	}

	List<DomainMappingDO> selectDomainMappingByCondition(IPage<DomainMappingDO> page, @Param("req") PortMappingListReq req);
}

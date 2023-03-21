package fun.asgc.neutrino.proxy.server.service;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Sets;
import fun.asgc.neutrino.proxy.server.base.page.PageInfo;
import fun.asgc.neutrino.proxy.server.base.page.PageQuery;
import fun.asgc.neutrino.proxy.server.base.rest.SystemContextHolder;
import fun.asgc.neutrino.proxy.server.constant.EnableStatusEnum;
import fun.asgc.neutrino.proxy.server.constant.ExceptionConstant;
import fun.asgc.neutrino.proxy.server.constant.OnlineStatusEnum;
import fun.asgc.neutrino.proxy.server.controller.req.*;
import fun.asgc.neutrino.proxy.server.controller.res.*;
import fun.asgc.neutrino.proxy.server.dal.LicenseMapper;
import fun.asgc.neutrino.proxy.server.dal.PortMappingMapper;
import fun.asgc.neutrino.proxy.server.dal.PortPoolMapper;
import fun.asgc.neutrino.proxy.server.dal.UserMapper;
import fun.asgc.neutrino.proxy.server.dal.entity.LicenseDO;
import fun.asgc.neutrino.proxy.server.dal.entity.PortMappingDO;
import fun.asgc.neutrino.proxy.server.dal.entity.PortPoolDO;
import fun.asgc.neutrino.proxy.server.dal.entity.UserDO;
import fun.asgc.neutrino.proxy.server.util.ParamCheckUtil;
import ma.glasnost.orika.MapperFacade;
import org.apache.ibatis.solon.annotation.Db;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.Lifecycle;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/8/8
 */
@Component
public class PortMappingService implements Lifecycle {
	@Inject
	private MapperFacade mapperFacade;
	@Db
	private PortMappingMapper portMappingMapper;
	@Db
	private LicenseMapper licenseMapper;
	@Db
	private UserMapper userMapper;
	@Db
	private PortPoolMapper portPoolMapper;
	@Inject
	private VisitorChannelService visitorChannelService;

	@Inject
	private PortPoolService portPoolService;

	public PageInfo<PortMappingListRes> page(PageQuery pageQuery, PortMappingListReq req) {
		Page<PortMappingListRes> result = PageHelper.startPage(pageQuery.getCurrent(), pageQuery.getSize());

		List<PortMappingDO> list = portMappingMapper.selectPortMappingByCondition(req);
		List<PortMappingListRes> respList = mapperFacade.mapAsList(list, PortMappingListRes.class);
		if (CollectionUtils.isEmpty(list)) {
			return PageInfo.of(respList, result.getTotal(), pageQuery.getCurrent(), pageQuery.getSize());
		}

		Set<Integer> licenseIds = respList.stream().map(PortMappingListRes::getLicenseId).collect(Collectors.toSet());
		List<LicenseDO> licenseList = licenseMapper.findByIds(licenseIds);
		if (CollectionUtil.isEmpty(licenseList)) {
			return PageInfo.of(respList, result.getTotal(), pageQuery.getCurrent(), pageQuery.getSize());
		}
		Set<Integer> userIds = licenseList.stream().map(LicenseDO::getUserId).collect(Collectors.toSet());
		List<UserDO> userList = userMapper.findByIds(userIds);
		Map<Integer, LicenseDO> licenseMap = licenseList.stream().collect(Collectors.toMap(LicenseDO::getId, Function.identity()));
		Map<Integer, UserDO> userMap = userList.stream().collect(Collectors.toMap(UserDO::getId, Function.identity()));

		respList.forEach(item -> {
			LicenseDO license = licenseMap.get(item.getLicenseId());
			if (null == license) {
				return;
			}
			item.setLicenseName(license.getName());
			item.setUserId(license.getUserId());
			UserDO user = userMap.get(license.getUserId());
			if (null == user) {
				return;
			}
			item.setUserName(user.getName());
		});
		//sorted [userId asc] [licenseId asc] [createTime asc]
		respList = respList.stream().sorted(Comparator.comparing(PortMappingListRes::getUserId)
				.thenComparing(PortMappingListRes::getLicenseId)
				.thenComparing(PortMappingListRes::getCreateTime))
		.collect(Collectors.toList());
		return PageInfo.of(respList, result.getTotal(), pageQuery.getCurrent(), pageQuery.getSize());
	}

	public PortMappingCreateRes create(PortMappingCreateReq req) {
		LicenseDO licenseDO = licenseMapper.findById(req.getLicenseId());
		ParamCheckUtil.checkNotNull(licenseDO, ExceptionConstant.LICENSE_NOT_EXIST);
		if (!SystemContextHolder.isAdmin()) {
			// 临时处理，如果当前用户不是管理员，则操作userId不能为1
			ParamCheckUtil.checkExpression(!licenseDO.getUserId().equals(1), ExceptionConstant.NO_PERMISSION_VISIT);
		}
		PortPoolDO portPoolDO = portPoolMapper.findByPort(req.getServerPort());
		ParamCheckUtil.checkNotNull(portPoolDO, ExceptionConstant.PORT_NOT_EXIST);
		ParamCheckUtil.checkExpression(null == portMappingMapper.findByPort(req.getServerPort(), null), ExceptionConstant.PORT_CANNOT_REPEAT_MAPPING, req.getServerPort());


		Date now = new Date();
		PortMappingDO portMappingDO = new PortMappingDO();
		portMappingDO.setLicenseId(req.getLicenseId());
		portMappingDO.setServerPort(req.getServerPort());
		portMappingDO.setClientIp(req.getClientIp());
		portMappingDO.setClientPort(req.getClientPort());
		portMappingDO.setIsOnline(OnlineStatusEnum.OFFLINE.getStatus());
		portMappingDO.setEnable(EnableStatusEnum.ENABLE.getStatus());
		portMappingDO.setCreateTime(now);
		portMappingDO.setUpdateTime(now);
		portMappingMapper.insert(portMappingDO);
		// 更新VisitorChannel
		visitorChannelService.addVisitorChannelByPortMapping(portMappingDO);
		return new PortMappingCreateRes();
	}

	public PortMappingUpdateRes update(PortMappingUpdateReq req) {
		LicenseDO licenseDO = licenseMapper.findById(req.getLicenseId());
		ParamCheckUtil.checkNotNull(licenseDO, ExceptionConstant.LICENSE_NOT_EXIST);
		if (!SystemContextHolder.isAdmin()) {
			// 临时处理，如果当前用户不是管理员，则操作userId不能为1
			ParamCheckUtil.checkExpression(!licenseDO.getUserId().equals(1), ExceptionConstant.NO_PERMISSION_VISIT);
		}
		PortPoolDO portPoolDO = portPoolMapper.findByPort(req.getServerPort());
		ParamCheckUtil.checkNotNull(portPoolDO, ExceptionConstant.PORT_NOT_EXIST);
		ParamCheckUtil.checkExpression(null == portMappingMapper.findByPort(req.getServerPort(), Sets.newHashSet(req.getId())), ExceptionConstant.PORT_CANNOT_REPEAT_MAPPING, req.getServerPort());

		// 查询原端口映射
		PortMappingDO oldPortMappingDO = portMappingMapper.findById(req.getId());
		ParamCheckUtil.checkNotNull(oldPortMappingDO, ExceptionConstant.PORT_MAPPING_NOT_EXIST);

		PortMappingDO portMappingDO = new PortMappingDO();
		portMappingDO.setId(req.getId());
		portMappingDO.setLicenseId(req.getLicenseId());
		portMappingDO.setServerPort(req.getServerPort());
		portMappingDO.setClientIp(req.getClientIp());
		portMappingDO.setClientPort(req.getClientPort());
		portMappingDO.setUpdateTime(new Date());
		portMappingDO.setEnable(EnableStatusEnum.ENABLE.getStatus());
		portMappingMapper.updateById(portMappingDO);
		// 更新VisitorChannel
		visitorChannelService.updateVisitorChannelByPortMapping(oldPortMappingDO, portMappingDO);
		return new PortMappingUpdateRes();
	}

	public PortMappingDetailRes detail(Integer id) {
		PortMappingDO portMappingDO = portMappingMapper.findById(id);
		if (null == portMappingDO) {
			return null;
		}
		PortMappingDetailRes res = new PortMappingDetailRes()
			.setId(portMappingDO.getId())
			.setLicenseId(portMappingDO.getLicenseId())
			.setServerPort(portMappingDO.getServerPort())
			.setClientIp(portMappingDO.getClientIp())
			.setClientPort(portMappingDO.getClientPort())
			.setIsOnline(portMappingDO.getIsOnline())
			.setEnable(portMappingDO.getEnable())
			.setCreateTime(portMappingDO.getCreateTime())
			.setUpdateTime(portMappingDO.getUpdateTime());

		LicenseDO license = licenseMapper.findById(portMappingDO.getLicenseId());
		if (null != license) {
			res.setLicenseName(license.getName());
			res.setUserId(license.getUserId());
			UserDO user = userMapper.findById(license.getUserId());
			if (null != user) {
				res.setUserName(user.getName());
			}
		}

		return res;
	}

	public PortMappingUpdateEnableStatusRes updateEnableStatus(PortMappingUpdateEnableStatusReq req) {
		PortMappingDO portMappingDO = portMappingMapper.findById(req.getId());
		ParamCheckUtil.checkNotNull(portMappingDO, ExceptionConstant.PORT_MAPPING_NOT_EXIST);

		LicenseDO licenseDO = licenseMapper.findById(portMappingDO.getLicenseId());
		ParamCheckUtil.checkNotNull(licenseDO, ExceptionConstant.LICENSE_NOT_EXIST);
		if (!SystemContextHolder.isAdmin()) {
			ParamCheckUtil.checkExpression(!licenseDO.getUserId().equals(1), ExceptionConstant.NO_PERMISSION_VISIT);
		}

		portMappingMapper.updateEnableStatus(req.getId(), req.getEnable(), new Date());

		// 更新VisitorChannel
		portMappingDO.setEnable(req.getEnable());
		if (EnableStatusEnum.ENABLE == EnableStatusEnum.of(req.getEnable())) {
			visitorChannelService.addVisitorChannelByPortMapping(portMappingDO);
		} else {
			visitorChannelService.removeVisitorChannelByPortMapping(portMappingDO);
		}

		return new PortMappingUpdateEnableStatusRes();
	}

	public void delete(Integer id) {
		PortMappingDO portMappingDO = portMappingMapper.findById(id);
		ParamCheckUtil.checkNotNull(portMappingDO, ExceptionConstant.PORT_MAPPING_NOT_EXIST);

		LicenseDO licenseDO = licenseMapper.findById(portMappingDO.getLicenseId());
		if (null != licenseDO && !SystemContextHolder.isAdmin()) {
			// 临时处理，如果当前用户不是管理员，则操作userId不能为1
			ParamCheckUtil.checkExpression(!licenseDO.getUserId().equals(1), ExceptionConstant.NO_PERMISSION_VISIT);
		}

		portMappingMapper.deleteById(id);

		// 更新VisitorChannel
		visitorChannelService.removeVisitorChannelByPortMapping(portMappingDO);
	}

	/**
	 * 根据license查询可用的端口映射列表
	 * @param licenseId
	 * @return
	 */
	public List<PortMappingDO> findEnableListByLicenseId(Integer licenseId) {
		return portMappingMapper.findEnableListByLicenseId(licenseId);
	}

	/**
	 * 服务端项目停止、启动时，更新在线状态为离线
	 */
	@Override
	public void start() throws Throwable {
		portMappingMapper.updateOnlineStatus(OnlineStatusEnum.OFFLINE.getStatus(), new Date());
	}

	/**
	 * 服务端项目停止、启动时，更新在线状态为离线
	 */
	@Override
	public void stop() throws Throwable {
		portMappingMapper.updateOnlineStatus(OnlineStatusEnum.OFFLINE.getStatus(), new Date());
	}
}

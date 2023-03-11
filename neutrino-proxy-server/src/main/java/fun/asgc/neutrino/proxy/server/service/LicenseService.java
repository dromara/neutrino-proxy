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
import fun.asgc.neutrino.proxy.server.controller.req.LicenseCreateReq;
import fun.asgc.neutrino.proxy.server.controller.req.LicenseListReq;
import fun.asgc.neutrino.proxy.server.controller.req.LicenseUpdateEnableStatusReq;
import fun.asgc.neutrino.proxy.server.controller.req.LicenseUpdateReq;
import fun.asgc.neutrino.proxy.server.controller.res.*;
import fun.asgc.neutrino.proxy.server.dal.LicenseMapper;
import fun.asgc.neutrino.proxy.server.dal.UserMapper;
import fun.asgc.neutrino.proxy.server.dal.entity.LicenseDO;
import fun.asgc.neutrino.proxy.server.dal.entity.UserDO;
import fun.asgc.neutrino.proxy.server.util.ParamCheckUtil;
import ma.glasnost.orika.MapperFactory;
import org.apache.ibatis.solon.annotation.Db;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.Lifecycle;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * license服务
 * @author: aoshiguchen
 * @date: 2022/8/6
 */
@Component
public class LicenseService implements Lifecycle {
	@Inject
	private MapperFactory mapperFactory;
	@Db
	private LicenseMapper licenseMapper;
	@Db
	private UserMapper userMapper;
	@Inject
	private VisitorChannelService visitorChannelService;

	public PageInfo<LicenseListRes> page(PageQuery pageQuery, LicenseListReq req) {
		Page<LicenseListRes> result = PageHelper.startPage(pageQuery.getCurrent(), pageQuery.getSize());
		List<LicenseDO> list = licenseMapper.selectList(new LambdaQueryWrapper<LicenseDO>()
				.orderByAsc(LicenseDO::getId)
		);
		List<LicenseListRes> respList = mapperFactory.getMapperFacade().mapAsList(list, LicenseListRes.class);
		if (CollectionUtils.isEmpty(list)) {
			return PageInfo.of(respList, result.getTotal(), pageQuery.getCurrent(), pageQuery.getSize());
		}
		if (!CollectionUtil.isEmpty(respList)) {
			Set<Integer> userIds = respList.stream().map(LicenseListRes::getUserId).collect(Collectors.toSet());
			List<UserDO> userList = userMapper.findByIds(userIds);
			Map<Integer, UserDO> userMap = userList.stream().collect(Collectors.toMap(UserDO::getId, Function.identity()));
			for (LicenseListRes item : respList) {
				UserDO userDO = userMap.get(item.getUserId());
				if (null != userDO) {
					item.setUserName(userDO.getName());
				}
				item.setKey(desensitization(item.getUserId(), item.getKey()));
			}
		}
		return PageInfo.of(respList, result.getTotal(), pageQuery.getCurrent(), pageQuery.getSize());
	}

	public List<LicenseListRes> list(LicenseListReq req) {
		List<LicenseDO> list = licenseMapper.selectList(new LambdaQueryWrapper<LicenseDO>()
				.eq(LicenseDO::getEnable, EnableStatusEnum.ENABLE.getStatus())
		);
		List<LicenseListRes> licenseList = mapperFactory.getMapperFacade().mapAsList(list, LicenseListRes.class);
		if (!CollectionUtil.isEmpty(licenseList)) {
			Set<Integer> userIds = licenseList.stream().map(LicenseListRes::getUserId).collect(Collectors.toSet());
			List<UserDO> userList = userMapper.findByIds(userIds);
			Map<Integer, UserDO> userMap = userList.stream().collect(Collectors.toMap(UserDO::getId, Function.identity()));
			for (LicenseListRes item : licenseList) {
				UserDO userDO = userMap.get(item.getUserId());
				if (null != userDO) {
					item.setUserName(userDO.getName());
				}
				item.setKey(desensitization(item.getUserId(), item.getKey()));
			}
		}
		return licenseList;
	}

	/**
	 * 创建license
	 * @param req
	 * @return
	 */
	public LicenseCreateRes create(LicenseCreateReq req) {
		LicenseDO licenseDO = licenseMapper.checkRepeat(req.getUserId(), req.getName());
		ParamCheckUtil.checkExpression(null == licenseDO, ExceptionConstant.LICENSE_NAME_CANNOT_REPEAT);

		String key = UUID.randomUUID().toString().replaceAll("-", "");
		Date now = new Date();

		licenseMapper.insert(new LicenseDO()
			.setName(req.getName())
			.setKey(key)
			.setUserId(req.getUserId())
			.setIsOnline(OnlineStatusEnum.OFFLINE.getStatus())
			.setEnable(EnableStatusEnum.ENABLE.getStatus())
			.setCreateTime(now)
			.setUpdateTime(now)
		);
		return new LicenseCreateRes();
	}

	public LicenseUpdateRes update(LicenseUpdateReq req) {
		LicenseDO oldLicenseDO = licenseMapper.findById(req.getId());
		ParamCheckUtil.checkNotNull(oldLicenseDO, ExceptionConstant.LICENSE_NOT_EXIST);

		LicenseDO licenseCheck = licenseMapper.checkRepeat(oldLicenseDO.getUserId(), req.getName(), Sets.newHashSet(oldLicenseDO.getId()));
		ParamCheckUtil.checkMustNull(licenseCheck, ExceptionConstant.LICENSE_NAME_CANNOT_REPEAT);

		licenseMapper.update(req.getId(), req.getName(), new Date());
		return new LicenseUpdateRes();
	}

	public LicenseDetailRes detail(Integer id) {
		LicenseDO licenseDO = licenseMapper.findById(id);
		if (null == licenseDO) {
			return null;
		}
		UserDO userDO = userMapper.findById(licenseDO.getUserId());
		String userName = "";
		if (null != userDO) {
			userName = userDO.getName();
		}
		return new LicenseDetailRes()
			.setId(licenseDO.getId())
			.setName(licenseDO.getName())
			.setKey(desensitization(licenseDO.getUserId(), licenseDO.getKey()))
			.setUserId(licenseDO.getUserId())
			.setUserName(userName)
			.setIsOnline(licenseDO.getIsOnline())
			.setEnable(licenseDO.getEnable())
			.setCreateTime(licenseDO.getCreateTime())
			.setUpdateTime(licenseDO.getUpdateTime())
		;
	}

	/**
	 * 更新license启用状态
	 * @param req
	 * @return
	 */
	public LicenseUpdateEnableStatusRes updateEnableStatus(LicenseUpdateEnableStatusReq req) {
		licenseMapper.updateEnableStatus(req.getId(), req.getEnable(), new Date());
		// 更新VisitorChannel
		visitorChannelService.updateVisitorChannelByLicenseId(req.getId(), req.getEnable());
		return new LicenseUpdateEnableStatusRes();
	}

	/**
	 * 删除license
	 * @param id
	 */
	public void delete(Integer id) {
		licenseMapper.deleteById(id);
		// 更新VisitorChannel
		visitorChannelService.updateVisitorChannelByLicenseId(id, EnableStatusEnum.DISABLE.getStatus());
	}

	/**
	 * 重置license
	 * @param id
	 */
	public void reset(Integer id) {
		String key = UUID.randomUUID().toString().replaceAll("-", "");
		Date now = new Date();

		licenseMapper.reset(id, key, now);
	}

	public LicenseDO findByKey(String license) {
		return licenseMapper.findByKey(license);
	}

	/**
	 * 脱敏处理
	 * 非当前登录人的license，一律脱敏
	 * @param userId
	 * @param licenseKey
	 * @return
	 */
	private String desensitization(Integer userId, String licenseKey) {
		Integer currentUserId = SystemContextHolder.getUser().getId();
		if (currentUserId.equals(userId)) {
			return licenseKey;
		}
		return licenseKey.substring(0, 10) + "****" + licenseKey.substring(licenseKey.length() - 10);
	}

	/**
	 * 服务端项目停止、启动时，更新在线状态为离线
	 */
	@Override
	public void start() throws Throwable {
		licenseMapper.updateOnlineStatus(OnlineStatusEnum.OFFLINE.getStatus(), new Date());
	}

	/**
	 * 服务端项目停止、启动时，更新在线状态为离线
	 */
	@Override
	public void stop() throws Throwable {
		licenseMapper.updateOnlineStatus(OnlineStatusEnum.OFFLINE.getStatus(), new Date());
	}
}

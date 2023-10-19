package org.dromara.neutrinoproxy.server.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.solon.plugins.pagination.Page;
import org.dromara.neutrinoproxy.core.util.DateUtil;
import org.dromara.neutrinoproxy.server.base.page.PageInfo;
import org.dromara.neutrinoproxy.server.base.page.PageQuery;
import org.dromara.neutrinoproxy.server.base.rest.ServiceException;
import org.dromara.neutrinoproxy.server.base.rest.SystemContextHolder;
import org.dromara.neutrinoproxy.server.constant.EnableStatusEnum;
import org.dromara.neutrinoproxy.server.constant.ExceptionConstant;
import org.dromara.neutrinoproxy.server.controller.req.system.*;
import org.dromara.neutrinoproxy.server.controller.res.system.*;
import org.dromara.neutrinoproxy.server.dal.UserLoginRecordMapper;
import org.dromara.neutrinoproxy.server.dal.UserMapper;
import org.dromara.neutrinoproxy.server.dal.UserTokenMapper;
import org.dromara.neutrinoproxy.server.dal.entity.UserDO;
import org.dromara.neutrinoproxy.server.dal.entity.UserLoginRecordDO;
import org.dromara.neutrinoproxy.server.dal.entity.UserTokenDO;
import org.dromara.neutrinoproxy.server.util.Md5Util;
import org.dromara.neutrinoproxy.server.util.ParamCheckUtil;
import ma.glasnost.orika.MapperFacade;
import org.apache.ibatis.solon.annotation.Db;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/7/31
 */
@Component
public class UserService {
	private static final String DEFAULT_PASSWORD = "123456";
	@Inject
	private MapperFacade mapperFacade;
	@Db
	private UserMapper userMapper;
	@Db
	private UserTokenMapper userTokenMapper;
	@Db
	private UserLoginRecordMapper userLoginRecordMapper;
	@Inject
	private VisitorChannelService visitorChannelService;

	public LoginRes login(LoginReq req) {
		UserDO userDO = userMapper.findByLoginName(req.getLoginName());
		if (null == userDO || !Md5Util.encode(req.getLoginPassword()).equals(userDO.getLoginPassword())) {
			throw ServiceException.create(ExceptionConstant.USER_NAME_OR_PASSWORD_ERROR);
		}
		if (EnableStatusEnum.DISABLE.getStatus().equals(userDO.getEnable())) {
			throw ServiceException.create(ExceptionConstant.USER_DISABLE);
		}
		String token = UUID.randomUUID().toString().replaceAll("-", "");

		Date now = new Date();
		Date expirationTime = DateUtil.addDate(now, Calendar.HOUR, 1);

		// 缓存token
		userTokenMapper.insert(new UserTokenDO()
			.setToken(token)
			.setUserId(userDO.getId())
			.setExpirationTime(expirationTime)
			.setCreateTime(now)
			.setUpdateTime(now)
		);

		// 新增用户登录日志
		userLoginRecordMapper.insert(new UserLoginRecordDO()
			.setUserId(userDO.getId())
			.setIp(SystemContextHolder.getIp())
			.setToken(token)
			.setType(UserLoginRecordDO.TYPE_LOGIN)
			.setCreateTime(now)
		);

		return new LoginRes()
			.setToken(token)
			.setUserId(userDO.getId())
			.setUserName(userDO.getName());
	}

	public void logout() {
		userTokenMapper.deleteByToken(SystemContextHolder.getToken());

		// 新增用户登录日志
		userLoginRecordMapper.insert(new UserLoginRecordDO()
			.setUserId(SystemContextHolder.getUser().getId())
			.setIp(SystemContextHolder.getIp())
			.setToken(SystemContextHolder.getToken())
			.setType(UserLoginRecordDO.TYPE_LOGOUT)
			.setCreateTime(new Date())
		);
	}

	public UserDO findByToken(String token) {
		Date now = new Date();
		UserTokenDO userTokenDO = userTokenMapper.findByAvailableToken(token, now);
		if (null == userTokenDO) {
			return null;
		}
		return userMapper.findById(userTokenDO.getUserId());
	}

	public UserDO findById(Integer id) {
		return userMapper.findById(id);
	}

	public void updateTokenExpirationTime(String token) {
		Date now = new Date();
		Date expirationTime = DateUtil.addDate(now, Calendar.HOUR, 1);
		userTokenMapper.updateTokenExpirationTime(token, expirationTime);
	}

	public PageInfo<UserListRes> page(PageQuery pageQuery, UserListReq req) {
        Page<UserDO> page = userMapper.selectPage(new Page<>(pageQuery.getCurrent(), pageQuery.getSize()), new LambdaQueryWrapper<UserDO>()
            .orderByAsc(UserDO::getId)
        );
        List<UserListRes> respList = mapperFacade.mapAsList(page.getRecords(), UserListRes.class);
		return PageInfo.of(respList, page);
	}

	public List<UserListRes> list(UserListReq req) {
		List<UserDO> userDOList = userMapper.selectList(new LambdaQueryWrapper<UserDO>()
				.eq(UserDO::getEnable, EnableStatusEnum.ENABLE.getStatus())
				.orderByAsc(UserDO::getId)
		);
		return mapperFacade.mapAsList(userDOList, UserListRes.class);
	}

	public UserInfoRes info(UserInfoReq req) {
		Integer userId = req.getId();
		if (null == userId) {
			userId = SystemContextHolder.getUser().getId();
		}
		UserDO userDO = userMapper.findById(userId);
		if (null == userDO) {
			return null;
		}
		return new UserInfoRes()
			.setId(userDO.getId())
			.setName(userDO.getName())
			.setLoginName(userDO.getLoginName())
			.setCreateTime(userDO.getCreateTime())
			.setUpdateTime(userDO.getUpdateTime());
	}

	public UserUpdateEnableStatusRes updateEnableStatus(UserUpdateEnableStatusReq req) {
		userMapper.updateEnableStatus(req.getId(), req.getEnable(), new Date());
		// 更新VisitorChannel
		visitorChannelService.updateVisitorChannelByUserId(req.getId(), req.getEnable());
		return new UserUpdateEnableStatusRes();
	}

	public UserCreateRes create(UserCreateReq req) {
		Date now = new Date();
		UserDO userDO = new UserDO();
		userDO.setName(req.getName());
		userDO.setLoginName(req.getLoginName());
		userDO.setLoginPassword(Md5Util.encode(DEFAULT_PASSWORD));
		userDO.setEnable(EnableStatusEnum.ENABLE.getStatus());
		userDO.setCreateTime(now);
		userDO.setUpdateTime(now);
		userMapper.insert(userDO);
		return new UserCreateRes();
	}

	public UserUpdateRes update(UserUpdateReq req) {
		userMapper.update(null, new LambdaUpdateWrapper<UserDO>()
				.eq(UserDO::getId, req.getId())
				.set(UserDO::getName, req.getName())
				.set(UserDO::getLoginName, req.getLoginName())
				.set(UserDO::getUpdateTime, new Date())
		);
		return new UserUpdateRes();
	}

	public UserUpdatePasswordRes updatePassword(UserUpdatePasswordReq req) {
		UserDO userDO = userMapper.findById(req.getId());
		// 更新密码
		String loginPassword = Md5Util.encode(req.getLoginPassword());
		ParamCheckUtil.checkExpression(!userDO.getLoginPassword().equals(loginPassword), ExceptionConstant.LOGIN_PASSWORD_NO_CHANGE_MODIFY_FAIL);

		userMapper.updateLoginPassword(req.getId(), loginPassword, new Date());

		// 删除该用户所有token
		userTokenMapper.deleteByUserId(req.getId());

		return new UserUpdatePasswordRes();
	}

	public void delete(Integer id) {
		userMapper.deleteById(id);
		// 更新VisitorChannel
		visitorChannelService.updateVisitorChannelByUserId(id, EnableStatusEnum.DISABLE.getStatus());
	}
}

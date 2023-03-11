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
package fun.asgc.neutrino.proxy.server.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import fun.asgc.neutrino.core.util.DateUtil;
import fun.asgc.neutrino.proxy.server.base.page.PageInfo;
import fun.asgc.neutrino.proxy.server.base.page.PageQuery;
import fun.asgc.neutrino.proxy.server.base.rest.ServiceException;
import fun.asgc.neutrino.proxy.server.base.rest.SystemContextHolder;
import fun.asgc.neutrino.proxy.server.constant.EnableStatusEnum;
import fun.asgc.neutrino.proxy.server.constant.ExceptionConstant;
import fun.asgc.neutrino.proxy.server.controller.req.*;
import fun.asgc.neutrino.proxy.server.controller.res.*;
import fun.asgc.neutrino.proxy.server.dal.UserLoginRecordMapper;
import fun.asgc.neutrino.proxy.server.dal.UserMapper;
import fun.asgc.neutrino.proxy.server.dal.UserTokenMapper;
import fun.asgc.neutrino.proxy.server.dal.entity.UserDO;
import fun.asgc.neutrino.proxy.server.dal.entity.UserLoginRecordDO;
import fun.asgc.neutrino.proxy.server.dal.entity.UserTokenDO;
import fun.asgc.neutrino.proxy.server.util.Md5Util;
import fun.asgc.neutrino.proxy.server.util.ParamCheckUtil;
import ma.glasnost.orika.MapperFactory;
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
	private MapperFactory mapperFactory;
	@Db
	private UserMapper userMapper;
	@Db
	private UserTokenMapper userTokenMapper;
	@Db
	private UserLoginRecordMapper userLoginRecordMapper;
	@Db
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
		userLoginRecordMapper.add(new UserLoginRecordDO()
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
		Page<UserListRes> result = PageHelper.startPage(pageQuery.getCurrent(), pageQuery.getSize());
		List<UserDO> list = userMapper.selectList(new LambdaQueryWrapper<UserDO>()
				.orderByAsc(UserDO::getId)
		);
		List<UserListRes> respList = mapperFactory.getMapperFacade().mapAsList(list, UserListRes.class);
		return PageInfo.of(respList, result.getTotal(), pageQuery.getCurrent(), pageQuery.getSize());
	}

	public List<UserListRes> list(UserListReq req) {
		List<UserDO> userDOList = userMapper.selectList(new LambdaQueryWrapper<UserDO>()
				.eq(UserDO::getEnable, EnableStatusEnum.ENABLE.getStatus())
				.orderByAsc(UserDO::getId)
		);
		return mapperFactory.getMapperFacade().mapAsList(userDOList, UserListRes.class);
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
		userMapper.add(userDO);

		return new UserCreateRes();
	}

	public UserUpdateRes update(UserUpdateReq req) {
		UserDO userDO = new UserDO();
		userDO.setId(req.getId());
		userDO.setName(req.getName());
		userDO.setLoginName(req.getLoginName());
		userDO.setUpdateTime(new Date());
		userMapper.update(userDO);
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
		userMapper.delete(id);
		// 更新VisitorChannel
		visitorChannelService.updateVisitorChannelByUserId(id, EnableStatusEnum.DISABLE.getStatus());
	}
}

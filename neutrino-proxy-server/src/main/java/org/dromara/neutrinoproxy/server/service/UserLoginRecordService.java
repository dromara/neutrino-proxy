package org.dromara.neutrinoproxy.server.service;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.solon.plugins.pagination.Page;
import org.dromara.neutrinoproxy.server.base.page.PageInfo;
import org.dromara.neutrinoproxy.server.base.page.PageQuery;
import org.dromara.neutrinoproxy.server.controller.req.log.UserLoginRecordListReq;
import org.dromara.neutrinoproxy.server.controller.res.log.UserLoginRecordListRes;
import org.dromara.neutrinoproxy.server.dal.UserLoginRecordMapper;
import org.dromara.neutrinoproxy.server.dal.UserMapper;
import org.dromara.neutrinoproxy.server.dal.entity.UserDO;
import org.dromara.neutrinoproxy.server.dal.entity.UserLoginRecordDO;
import ma.glasnost.orika.MapperFacade;
import org.apache.ibatis.solon.annotation.Db;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 用户登录日志
 * @author: aoshiguchen
 * @date: 2022/10/20
 */
@Component
public class UserLoginRecordService {
    @Inject
    private MapperFacade mapperFacade;
    @Db
    private UserLoginRecordMapper userLoginRecordMapper;
    @Db
    private UserMapper userMapper;

    public PageInfo<UserLoginRecordListRes> page(PageQuery pageQuery, UserLoginRecordListReq req) {
        Page<UserLoginRecordDO> page = userLoginRecordMapper.selectPage(new Page<>(pageQuery.getCurrent(), pageQuery.getSize()), new LambdaQueryWrapper<UserLoginRecordDO>()
            .eq(null != req.getUserId(), UserLoginRecordDO::getUserId, req.getUserId())
            .orderByDesc(UserLoginRecordDO::getCreateTime)
        );
        List<UserLoginRecordListRes> respList = mapperFacade.mapAsList(page.getRecords(), UserLoginRecordListRes.class);
        if (CollectionUtils.isEmpty(page.getRecords())) {
            return PageInfo.of(respList, page);
        }

        if (!CollectionUtil.isEmpty(respList)) {
            Set<Integer> userIds = respList.stream().map(UserLoginRecordListRes::getUserId).collect(Collectors.toSet());
            List<UserDO> userList = userMapper.findByIds(userIds);
            Map<Integer, UserDO> userMap = userList.stream().collect(Collectors.toMap(UserDO::getId, Function.identity()));
            for (UserLoginRecordListRes item : respList) {
                UserDO userDO = userMap.get(item.getUserId());
                if (null != userDO) {
                    item.setUserName(userDO.getName());
                }
            }
        }
        return PageInfo.of(respList, page);
    }
}

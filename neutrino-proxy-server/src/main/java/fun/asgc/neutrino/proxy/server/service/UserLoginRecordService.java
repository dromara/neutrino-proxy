package fun.asgc.neutrino.proxy.server.service;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import fun.asgc.neutrino.proxy.server.base.page.PageInfo;
import fun.asgc.neutrino.proxy.server.base.page.PageQuery;
import fun.asgc.neutrino.proxy.server.controller.req.UserLoginRecordListReq;
import fun.asgc.neutrino.proxy.server.controller.res.UserLoginRecordListRes;
import fun.asgc.neutrino.proxy.server.dal.UserLoginRecordMapper;
import fun.asgc.neutrino.proxy.server.dal.UserMapper;
import fun.asgc.neutrino.proxy.server.dal.entity.UserDO;
import fun.asgc.neutrino.proxy.server.dal.entity.UserLoginRecordDO;
import ma.glasnost.orika.MapperFactory;
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
    private MapperFactory mapperFactory;
    @Db
    private UserLoginRecordMapper userLoginRecordMapper;
    @Db
    private UserMapper userMapper;

    public PageInfo<UserLoginRecordListRes> page(PageQuery pageQuery, UserLoginRecordListReq req) {
        Page<UserLoginRecordListRes> result = PageHelper.startPage(pageQuery.getCurrent(), pageQuery.getSize());
        List<UserLoginRecordDO> list = userLoginRecordMapper.selectList(new LambdaQueryWrapper<UserLoginRecordDO>()
                .orderByDesc(UserLoginRecordDO::getCreateTime)
        );
        List<UserLoginRecordListRes> respList = mapperFactory.getMapperFacade().mapAsList(list, UserLoginRecordListRes.class);
        if (CollectionUtils.isEmpty(list)) {
            return PageInfo.of(respList, result.getTotal(), pageQuery.getCurrent(), pageQuery.getSize());
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
        return PageInfo.of(respList, result.getTotal(), pageQuery.getCurrent(), pageQuery.getSize());
    }
}

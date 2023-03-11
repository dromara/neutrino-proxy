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
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import fun.asgc.neutrino.core.util.CollectionUtil;
import fun.asgc.neutrino.proxy.server.base.page.PageInfo;
import fun.asgc.neutrino.proxy.server.base.page.PageQuery;
import fun.asgc.neutrino.proxy.server.base.rest.SystemContextHolder;
import fun.asgc.neutrino.proxy.server.controller.req.ClientConnectRecordListReq;
import fun.asgc.neutrino.proxy.server.controller.res.ClientConnectRecordListRes;
import fun.asgc.neutrino.proxy.server.dal.ClientConnectRecordMapper;
import fun.asgc.neutrino.proxy.server.dal.LicenseMapper;
import fun.asgc.neutrino.proxy.server.dal.UserMapper;
import fun.asgc.neutrino.proxy.server.dal.entity.ClientConnectRecordDO;
import fun.asgc.neutrino.proxy.server.dal.entity.LicenseDO;
import fun.asgc.neutrino.proxy.server.dal.entity.UserDO;
import lombok.extern.slf4j.Slf4j;
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
 * @author: aoshiguchen
 * @date: 2022/11/23
 */
@Slf4j
@Component
public class ClientConnectRecordService {
    @Inject
    private MapperFactory mapperFactory;
    @Db
    private ClientConnectRecordMapper clientConnectRecordMapper;
    @Db
    private LicenseMapper licenseMapper;
    @Db
    private UserMapper userMapper;

    public void add(ClientConnectRecordDO clientConnectRecordDO) {
        clientConnectRecordMapper.insert(clientConnectRecordDO);
    }

    public PageInfo<ClientConnectRecordListRes> page(PageQuery pageQuery, ClientConnectRecordListReq req) {
        Page<ClientConnectRecordListRes> result = PageHelper.startPage(pageQuery.getCurrent(), pageQuery.getSize());
        List<ClientConnectRecordDO> list = clientConnectRecordMapper.selectList(new LambdaQueryWrapper<ClientConnectRecordDO>()
                .orderByDesc(ClientConnectRecordDO::getId)
        );
        List<ClientConnectRecordListRes> respList = mapperFactory.getMapperFacade().mapAsList(list, ClientConnectRecordListRes.class);
        if (CollectionUtils.isEmpty(list)) {
            return PageInfo.of(respList, result.getTotal(), pageQuery.getCurrent(), pageQuery.getSize());
        }
        Set<Integer> licenseIds = respList.stream().map(ClientConnectRecordListRes::getLicenseId).collect(Collectors.toSet());
        List<LicenseDO> licenseList = licenseMapper.findByIds(licenseIds);
        if (CollectionUtil.isEmpty(licenseList)) {
            return PageInfo.of(respList, result.getTotal(), pageQuery.getCurrent(), pageQuery.getSize());
        }
        Set<Integer> userIds = licenseList.stream().map(LicenseDO::getUserId).collect(Collectors.toSet());
        List<UserDO> userList = userMapper.findByIds(userIds);
        Map<Integer, LicenseDO> licenseMap = licenseList.stream().collect(Collectors.toMap(LicenseDO::getId, Function.identity()));
        Map<Integer, UserDO> userMap = userList.stream().collect(Collectors.toMap(UserDO::getId, Function.identity()));
        boolean isAdmin = SystemContextHolder.isAdmin();
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
            if (!isAdmin) {
                // msg可能带有license等敏感信息，若登录者为游客，则不展示
                item.setMsg("******");
            }
        });
        return PageInfo.of(respList, result.getTotal(), pageQuery.getCurrent(), pageQuery.getSize());
    }
}

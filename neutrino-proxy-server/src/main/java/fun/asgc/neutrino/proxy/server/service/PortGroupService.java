package fun.asgc.neutrino.proxy.server.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import fun.asgc.neutrino.proxy.server.base.page.PageInfo;
import fun.asgc.neutrino.proxy.server.base.page.PageQuery;
import fun.asgc.neutrino.proxy.server.base.rest.ServiceException;
import fun.asgc.neutrino.proxy.server.base.rest.SystemContextHolder;
import fun.asgc.neutrino.proxy.server.constant.Constants;
import fun.asgc.neutrino.proxy.server.constant.EnableStatusEnum;
import fun.asgc.neutrino.proxy.server.constant.ExceptionConstant;
import fun.asgc.neutrino.proxy.server.controller.req.system.PortGroupCreateReq;
import fun.asgc.neutrino.proxy.server.controller.req.system.PortGroupListReq;
import fun.asgc.neutrino.proxy.server.controller.req.system.PortGroupUpdateEnableStatusReq;
import fun.asgc.neutrino.proxy.server.controller.res.system.PortGroupCreateRes;
import fun.asgc.neutrino.proxy.server.controller.res.system.PortGroupListRes;
import fun.asgc.neutrino.proxy.server.controller.res.system.PortGroupUpdateEnableStatusRes;
import fun.asgc.neutrino.proxy.server.dal.PortGroupMapper;
import fun.asgc.neutrino.proxy.server.dal.PortPoolMapper;
import fun.asgc.neutrino.proxy.server.dal.entity.PortGroupDO;
import fun.asgc.neutrino.proxy.server.dal.entity.PortPoolDO;
import fun.asgc.neutrino.proxy.server.util.ParamCheckUtil;
import ma.glasnost.orika.MapperFacade;
import org.apache.ibatis.solon.annotation.Db;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 端口分组服务
 */
@Component
public class PortGroupService {

    @Inject
    private MapperFacade mapperFacade;

    @Db
    private PortGroupMapper portGroupMapper;

    @Db
    private PortPoolMapper portPoolMapper;

    public PortGroupCreateRes create(PortGroupCreateReq req) {
        PortGroupDO portGroupDO = portGroupMapper.selectOne(Wrappers.lambdaQuery(PortGroupDO.class)
                .eq(PortGroupDO::getName, req.getName()));
        if (Objects.nonNull(portGroupDO)) {
            throw ServiceException.create(ExceptionConstant.PORT_GROUP_NAME_ALREADY_EXIST, req.getName());
        }
        Date now = new Date();
        portGroupDO = new PortGroupDO();
        portGroupDO.setName(req.getName());
        portGroupDO.setPossessorType(req.getPossessorType());
        portGroupDO.setPossessorId(req.getPossessorId());
        portGroupDO.setEnable(EnableStatusEnum.ENABLE.getStatus());
        portGroupDO.setCreateTime(now);
        portGroupDO.setUpdateTime(now);
        portGroupMapper.insert(portGroupDO);
        return new PortGroupCreateRes();
    }

    public PageInfo<PortGroupListRes> page(PageQuery pageQuery, PortGroupListReq req) {
        Page<PortGroupListRes> result = PageHelper.startPage(pageQuery.getCurrent(), pageQuery.getSize());
        List<PortGroupListRes> list = portGroupMapper.selectPortGroupListResList(req);

        return PageInfo.of(list, result.getTotal(), pageQuery.getCurrent(), pageQuery.getSize());
    }

    public List<PortGroupListRes> list(PortGroupListReq req) {
        List<PortGroupListRes> list = portGroupMapper.selectPortGroupListResList(req);
        return list;
    }


    public PortGroupUpdateEnableStatusRes updateEnableStatus(PortGroupUpdateEnableStatusReq req) {
        PortGroupDO portGroupDO = portGroupMapper.selectById(req.getId());
        ParamCheckUtil.checkNotNull(portGroupDO, ExceptionConstant.PORT_GROUP_NAME_DOES_NOT_EXIST);
        if (!SystemContextHolder.isAdmin()) {
            ParamCheckUtil.checkExpression(false, ExceptionConstant.NO_PERMISSION_VISIT);
        }
        portGroupMapper.updateEnableStatus(req.getId(), req.getEnable(), new Date());
        return new PortGroupUpdateEnableStatusRes();
    }

    public void delete(Integer id) {
        if (id == Constants.DEFAULT_PORT_GROUP_ID) {
            throw ServiceException.create(ExceptionConstant.DEFAULT_GROUP_FORBID_DELETE);
        }
        PortGroupDO portGroupDO = portGroupMapper.selectById(id);
        //检验分组是否存在
        ParamCheckUtil.checkNotNull(portGroupDO, ExceptionConstant.PORT_GROUP_NAME_DOES_NOT_EXIST);
        //删除
        portGroupMapper.deleteById(id);
        //修改绑定此分组的端口到默认分组
        portPoolMapper.update(null, Wrappers.lambdaUpdate(PortPoolDO.class)
                .eq(PortPoolDO::getGroupId, portGroupDO.getId())
                .set(PortPoolDO::getGroupId, Constants.DEFAULT_PORT_GROUP_ID)
                .set(PortPoolDO::getUpdateTime, new Date())
        );
    }
}

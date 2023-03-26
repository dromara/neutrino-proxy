package fun.asgc.neutrino.proxy.server.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import fun.asgc.neutrino.proxy.server.base.page.PageInfo;
import fun.asgc.neutrino.proxy.server.base.page.PageQuery;
import fun.asgc.neutrino.proxy.server.base.rest.ServiceException;
import fun.asgc.neutrino.proxy.server.constant.EnableStatusEnum;
import fun.asgc.neutrino.proxy.server.constant.ExceptionConstant;
import fun.asgc.neutrino.proxy.server.controller.req.system.*;
import fun.asgc.neutrino.proxy.server.controller.res.system.*;
import fun.asgc.neutrino.proxy.server.dal.LicenseMapper;
import fun.asgc.neutrino.proxy.server.dal.PortGroupMapper;
import fun.asgc.neutrino.proxy.server.dal.PortMappingMapper;
import fun.asgc.neutrino.proxy.server.dal.PortPoolMapper;
import fun.asgc.neutrino.proxy.server.dal.entity.*;
import fun.asgc.neutrino.proxy.server.util.ParamCheckUtil;
import ma.glasnost.orika.MapperFacade;
import org.apache.ibatis.solon.annotation.Db;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/8/7
 */
@Component
public class PortPoolService {
	@Inject
	private MapperFacade mapperFacade;
	@Db
	private PortPoolMapper portPoolMapper;
	@Inject
	private VisitorChannelService visitorChannelService;

    @Db
    private PortGroupMapper portGroupMapper;
    @Db
    private PortMappingMapper portMappingMapper;
    @Db
    private LicenseMapper licenseMapper;

    public PageInfo<PortPoolListRes> page(PageQuery pageQuery, PortPoolListReq req) {
        Page<PortPoolListRes> result = PageHelper.startPage(pageQuery.getCurrent(), pageQuery.getSize());

        List<PortPoolListRes> list = portPoolMapper.selectResList(req);
        return PageInfo.of(list, result.getTotal(), pageQuery.getCurrent(), pageQuery.getSize());
    }

    public List<PortPoolListRes> list(PortPoolListReq req) {
        List<PortPoolDO> list = portPoolMapper.selectList(new LambdaQueryWrapper<PortPoolDO>()
                .eq(PortPoolDO::getEnable, EnableStatusEnum.ENABLE.getStatus())
        );
        return mapperFacade.mapAsList(this.filterUsedPorts(list), PortPoolListRes.class);
    }

    private List<PortPoolDO> filterUsedPorts(List<PortPoolDO> list) {
        //Gets the used ports
        List<PortMappingDO> usePorts = portMappingMapper.selectList(new LambdaQueryWrapper<PortMappingDO>().orderByAsc(PortMappingDO::getId));

        List<Integer> serverPorts = usePorts.stream().map(item -> item.getServerPort()).collect(Collectors.toList());

        return list.stream().filter(item -> !serverPorts.contains(item.getPort())).collect(Collectors.toList());
    }

    public PortPoolUpdateRes update(PortPoolUpdateReq req) {
        portPoolMapper.update(null, new LambdaUpdateWrapper<PortPoolDO>()
                .eq(PortPoolDO::getId, req.getId())
                .set(PortPoolDO::getGroupId, req.getGroupId())
                .set(PortPoolDO::getUpdateTime, new Date())
        );

        return new PortPoolUpdateRes();
    }

    public PortPoolCreateRes create(PortPoolCreateReq req) {
        PortPoolDO oldPortPoolDO = portPoolMapper.findByPort(req.getPort());
        ParamCheckUtil.checkMustNull(oldPortPoolDO, ExceptionConstant.PORT_CANNOT_REPEAT);
        PortGroupDO portGroupDO = portGroupMapper.selectById(req.getGroupId());
        ParamCheckUtil.checkNotNull(portGroupDO, ExceptionConstant.PORT_GROUP_NAME_DOES_NOT_EXIST);

        Date now = new Date();

        portPoolMapper.insert(new PortPoolDO()
                .setPort(req.getPort())
                .setGroupId(req.getGroupId())
                .setEnable(EnableStatusEnum.ENABLE.getStatus())
                .setCreateTime(now)
                .setUpdateTime(now)
        );
        // 更新visitorChannel
        visitorChannelService.updateVisitorChannelByPortPool(req.getPort(), EnableStatusEnum.ENABLE.getStatus());

        return new PortPoolCreateRes();
    }

    public PortPoolUpdateEnableStatusRes updateEnableStatus(PortPoolUpdateEnableStatusReq req) {
        PortPoolDO portPoolDO = portPoolMapper.findById(req.getId());
        ParamCheckUtil.checkNotNull(portPoolDO, ExceptionConstant.PORT_NOT_EXIST);
        portPoolMapper.updateEnableStatus(req.getId(), req.getEnable(), new Date());

        // 更新visitorChannel
        visitorChannelService.updateVisitorChannelByPortPool(portPoolDO.getPort(), req.getEnable());

        return new PortPoolUpdateEnableStatusRes();
    }

    public void delete(Integer id) {
        PortPoolDO portPoolDO = portPoolMapper.findById(id);
        ParamCheckUtil.checkNotNull(portPoolDO, ExceptionConstant.PORT_NOT_EXIST);

        portPoolMapper.deleteById(id);

        // 更新visitorChannel
        visitorChannelService.updateVisitorChannelByPortPool(portPoolDO.getPort(), EnableStatusEnum.DISABLE.getStatus());
    }

    public List<PortPoolListRes> portListByGroupId(String groupId) {
        List<PortPoolDO> portPoolDOList = portPoolMapper.getByGroupId(groupId);
        List<PortPoolListRes> portPoolListReList = mapperFacade.mapAsList(portPoolDOList, PortPoolListRes.class);
        return portPoolListReList;
    }

    public PortPoolUpdateGroupRes updateGroup(PortPoolUpdateGroupReq req) {
        PortGroupDO portGroupDO = portGroupMapper.selectById(req.getGroupId());
        if (Objects.isNull(portGroupDO)) {
            throw ServiceException.create(ExceptionConstant.PARAMS_INVALID);
        }
        portPoolMapper.update(null, Wrappers.lambdaUpdate(PortPoolDO.class)
                .in(PortPoolDO::getId, req.getPortIdList())
                .set(PortPoolDO::getGroupId, req.getGroupId())
                .set(PortPoolDO::getUpdateTime, new Date())
        );
        return new PortPoolUpdateGroupRes();
    }

    /**
     * 管理员： 全局端口 + 当前选择的用户独占端口 + 当前选择license独占端口
     * 游客：全局端口 + 当前选择用户独占端口 + 当前选择license独占端口
     * 非管理员身份时：下拉选择license，只能选当前用户下的LICENSE
     */
    public List<PortPoolListRes> getAvailablePortList(AvailablePortListReq req) {
        LicenseDO licenseDO = licenseMapper.queryById(req.getLicenseId());
        return portPoolMapper.getAvailablePortList(req.getLicenseId(), licenseDO.getUserId());
    }
}

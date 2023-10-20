package org.dromara.neutrinoproxy.server.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.solon.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.solon.annotation.Db;
import org.dromara.neutrinoproxy.server.base.page.PageInfo;
import org.dromara.neutrinoproxy.server.base.page.PageQuery;
import org.dromara.neutrinoproxy.server.base.rest.ServiceException;
import org.dromara.neutrinoproxy.server.constant.EnableStatusEnum;
import org.dromara.neutrinoproxy.server.constant.ExceptionConstant;
import org.dromara.neutrinoproxy.server.controller.req.system.AvailablePortListReq;
import org.dromara.neutrinoproxy.server.controller.req.system.PortPoolCreateReq;
import org.dromara.neutrinoproxy.server.controller.req.system.PortPoolListReq;
import org.dromara.neutrinoproxy.server.controller.req.system.PortPoolUpdateEnableStatusReq;
import org.dromara.neutrinoproxy.server.controller.req.system.PortPoolUpdateGroupReq;
import org.dromara.neutrinoproxy.server.controller.req.system.PortPoolUpdateReq;
import org.dromara.neutrinoproxy.server.controller.res.system.PortPoolCreateRes;
import org.dromara.neutrinoproxy.server.controller.res.system.PortPoolListRes;
import org.dromara.neutrinoproxy.server.controller.res.system.PortPoolUpdateEnableStatusRes;
import org.dromara.neutrinoproxy.server.controller.res.system.PortPoolUpdateGroupRes;
import org.dromara.neutrinoproxy.server.controller.res.system.PortPoolUpdateRes;
import org.dromara.neutrinoproxy.server.dal.LicenseMapper;
import org.dromara.neutrinoproxy.server.dal.PortGroupMapper;
import org.dromara.neutrinoproxy.server.dal.PortMappingMapper;
import org.dromara.neutrinoproxy.server.dal.PortPoolMapper;
import org.dromara.neutrinoproxy.server.dal.entity.LicenseDO;
import org.dromara.neutrinoproxy.server.dal.entity.PortGroupDO;
import org.dromara.neutrinoproxy.server.dal.entity.PortMappingDO;
import org.dromara.neutrinoproxy.server.dal.entity.PortPoolDO;
import org.dromara.neutrinoproxy.server.util.ParamCheckUtil;
import org.dromara.neutrinoproxy.server.util.PortAvailableUtil;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static org.dromara.neutrinoproxy.server.constant.ExceptionConstant.PORT_CANNOT_REPEAT;
import static org.dromara.neutrinoproxy.server.constant.ExceptionConstant.PORT_GROUP_NAME_DOES_NOT_EXIST;
import static org.dromara.neutrinoproxy.server.constant.ExceptionConstant.PORT_RANGE_FAIL;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/8/7
 */
@Slf4j
@Component
public class PortPoolService {
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
        Page<PortPoolListRes> page = new Page<>(pageQuery.getCurrent(), pageQuery.getSize());
        List<PortPoolListRes> list = portPoolMapper.selectResList(page, req);
        return PageInfo.of(list, page.getTotal(), pageQuery.getCurrent(), pageQuery.getSize());
    }

    public List<PortPoolListRes> list(PortPoolListReq req) {
        List<PortPoolDO> list = portPoolMapper.selectList(new LambdaQueryWrapper<PortPoolDO>()
                .eq(PortPoolDO::getEnable, EnableStatusEnum.ENABLE.getStatus())
        );

        List<PortPoolDO> resultList = this.filterUsedPorts(list);
        return resultList.stream().map(PortPoolDO::toRes).collect(Collectors.toList());
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
        Consumer<Integer> consumer = port -> {
            PortPoolDO oldPortPoolDO = portPoolMapper.findByPort(port);
            ParamCheckUtil.checkMustNull(oldPortPoolDO, PORT_CANNOT_REPEAT);
            PortGroupDO portGroupDO = portGroupMapper.selectById(req.getGroupId());
            ParamCheckUtil.checkNotNull(portGroupDO, PORT_GROUP_NAME_DOES_NOT_EXIST);
            Date now = new Date();
            portPoolMapper.insert(new PortPoolDO()
                    .setPort(port)
                    .setGroupId(req.getGroupId())
                    .setEnable(EnableStatusEnum.ENABLE.getStatus())
                    .setCreateTime(now)
                    .setUpdateTime(now)
            );
            // 更新visitorChannel
            visitorChannelService.updateVisitorChannelByPortPool(port, EnableStatusEnum.ENABLE.getStatus());
        };
        String[] portArr = StringUtils.split(req.getPort(), "-");
        if(portArr.length == 1){
            Integer port = Integer.valueOf(portArr[0]);
            consumer.accept(port);
        }else if(portArr.length == 2){
            int min = Integer.parseInt(portArr[0]),max = Integer.parseInt(portArr[1]);
            for (int i = min; i <= max; i++) {
                try {
                    consumer.accept(i);
                } catch (Exception e) {
                    log.warn("bulk add port err:{}",e.getMessage());
                }
            }
        }else{
            throw ServiceException.create(PORT_RANGE_FAIL);
        }
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
        List<PortPoolListRes> portPoolListReList = portPoolDOList.stream().map(PortPoolDO::toRes).collect(Collectors.toList());
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
    public PageInfo<PortPoolListRes> getAvailablePortList(AvailablePortListReq req) {

        LicenseDO licenseDO = licenseMapper.queryById(req.getLicenseId());
        if(StringUtils.isNotEmpty(req.getKeyword())){
            req.setKeyword(req.getKeyword()+"%");
        }

        Page<PortPoolListRes> page = new Page<>(req.getPage(), req.getSize());
        List<PortPoolListRes> portList = portPoolMapper.getAvailablePortList(page, req.getLicenseId(), licenseDO.getUserId(), req.getKeyword());

        return PageInfo.of(portList, page.getTotal(), req.getPage(), req.getSize());
    }

    public void deleteBatch(List<Integer> ids) {
        List<PortPoolDO> portPoolDOList = portPoolMapper.selectBatchIds(ids);
        ParamCheckUtil.checkNotNull(portPoolDOList, ExceptionConstant.PORT_NOT_EXIST);

        portPoolMapper.deleteBatchIds(ids);
        // 更新visitorChannel
        portPoolDOList.stream().forEach(portPoolDO -> {
            visitorChannelService.updateVisitorChannelByPortPool(portPoolDO.getPort(), EnableStatusEnum.DISABLE.getStatus());
        });
    }

    /**
     * 检查端口是否被占用
     * 端口映射编辑时，如果端口号没有变动，则不验证。避免出现端口映射正在使用时，无法更新端口映射其他信息的问题
     * @param port
     * @param portMappingId
     * @return
     */
    public boolean portAvailable(Integer port, Integer portMappingId) {
        if (null != portMappingId) {
            PortMappingDO portMappingDO = portMappingMapper.findById(portMappingId);
            if (null != portMappingDO && portMappingDO.getServerPort().equals(port)) {
                return Boolean.TRUE;
            }
        }
        return PortAvailableUtil.isPortAvailable(port);
    }
}

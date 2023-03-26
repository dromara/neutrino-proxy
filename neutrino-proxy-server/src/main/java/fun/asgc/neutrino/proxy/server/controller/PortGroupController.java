package fun.asgc.neutrino.proxy.server.controller;

import fun.asgc.neutrino.proxy.server.base.page.PageInfo;
import fun.asgc.neutrino.proxy.server.base.page.PageQuery;
import fun.asgc.neutrino.proxy.server.base.rest.Authorization;
import fun.asgc.neutrino.proxy.server.controller.req.system.PortGroupCreateReq;
import fun.asgc.neutrino.proxy.server.controller.req.system.PortGroupDeleteReq;
import fun.asgc.neutrino.proxy.server.controller.req.system.PortGroupListReq;
import fun.asgc.neutrino.proxy.server.controller.req.system.PortGroupUpdateEnableStatusReq;
import fun.asgc.neutrino.proxy.server.controller.res.system.PortGroupCreateRes;
import fun.asgc.neutrino.proxy.server.controller.res.system.PortGroupListRes;
import fun.asgc.neutrino.proxy.server.controller.res.system.PortGroupUpdateEnableStatusRes;
import fun.asgc.neutrino.proxy.server.service.PortGroupService;
import fun.asgc.neutrino.proxy.server.util.ParamCheckUtil;
import org.noear.solon.annotation.*;

import java.util.List;

/**
 * 端口分组控制层
 */
@Mapping("/port-group")
@Controller
public class PortGroupController {

    @Inject
    private PortGroupService portGroupService;

    @Post
    @Mapping("/create")
    public PortGroupCreateRes create(PortGroupCreateReq req) {
        ParamCheckUtil.checkNotNull(req, "req");
        ParamCheckUtil.checkNotEmpty(req.getName(), "name");
        ParamCheckUtil.checkNotNull(req.getPossessorType(), "possessorType");
        ParamCheckUtil.checkNotNull(req.getPossessorId(), "possessorId");

        return portGroupService.create(req);
    }

    @Get
    @Mapping("/page")
    public PageInfo<PortGroupListRes> page(PageQuery pageQuery, PortGroupListReq req) {
        ParamCheckUtil.checkNotNull(pageQuery, "pageQuery");
        return portGroupService.page(pageQuery, req);
    }

    @Get
    @Mapping("/list")
    public List<PortGroupListRes> list(PortGroupListReq req) {
        return portGroupService.list(req);
    }

    @Post
    @Mapping("/update/enable-status")
    public PortGroupUpdateEnableStatusRes updateEnableStatus(PortGroupUpdateEnableStatusReq req) {
        ParamCheckUtil.checkNotNull(req, "req");
        ParamCheckUtil.checkNotNull(req.getId(), "id");
        ParamCheckUtil.checkNotNull(req.getEnable(), "enable");

        return portGroupService.updateEnableStatus(req);
    }

    @Post
    @Mapping("/delete")
    @Authorization(onlyAdmin = true)
    public void delete(PortGroupDeleteReq req) {
        ParamCheckUtil.checkNotNull(req, "req");
        ParamCheckUtil.checkNotNull(req.getId(), "id");

        portGroupService.delete(req.getId());
    }




}

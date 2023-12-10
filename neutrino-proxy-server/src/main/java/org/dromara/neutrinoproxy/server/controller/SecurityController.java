package org.dromara.neutrinoproxy.server.controller;

import org.dromara.neutrinoproxy.server.base.page.PageInfo;
import org.dromara.neutrinoproxy.server.base.page.PageQuery;
import org.dromara.neutrinoproxy.server.controller.req.system.*;
import org.dromara.neutrinoproxy.server.controller.res.system.*;
import org.dromara.neutrinoproxy.server.service.PortMappingService;
import org.dromara.neutrinoproxy.server.service.SecurityGroupService;
import org.dromara.neutrinoproxy.server.util.ParamCheckUtil;
import org.noear.solon.annotation.*;

import java.util.List;

@Controller
@Mapping("/security")
public class SecurityController {

    @Inject
    private SecurityGroupService groupService;

    @Inject
    private PortMappingService portMappingService;

    /**
     * 获取当前用户权限下的安全组
     */
    @Get
    @Mapping("/group/page")
    public PageInfo<SecurityGroupListRes> groupPage(PageQuery pageQuery, SecurityGroupListReq req) {
        ParamCheckUtil.checkNotNull(pageQuery, "pageQuery");

        return groupService.groupPage(pageQuery, req);
    }

    @Get
    @Mapping("/group/list")
    public List<SecurityGroupListRes> groupList() {
        return groupService.groupList();
    }

    @Get
    @Mapping("/group/detail")
    public SecurityGroupDetailRes groupDetail(SecurityGroupDetailReq req) {
        ParamCheckUtil.checkNotNull(req, "req");
        ParamCheckUtil.checkNotNull(req.getId(), "id");

        return groupService.groupDetail(req);
    }

    @Post
    @Mapping("/group/create")
    public void createGroup(SecurityGroupCreateReq req) {
        groupService.createGroup(req);
    }

    @Post
    @Mapping("/group/update")
    public void updateGroup(SecurityGroupUpdateReq req) {
        groupService.updateGroup(req);
    }

    /**
     * 将级联删除对应规则,并更新缓存
     * @param groupId 安全组Id
     */
    @Post
    @Mapping("/group/delete")
    public void deleteGroup(Integer groupId) {
        ParamCheckUtil.checkNotNull(groupId, "groupId");

        groupService.deleteGroup(groupId);
    }

    @Post
    @Mapping("/group/update/enable-status")
    public SecurityGroupUpdateEnableStatueRes updateGroupEnableStatueReq(SecurityGroupUpdateEnableStatueReq req) {
        ParamCheckUtil.checkNotNull(req, "req");
        ParamCheckUtil.checkNotNull(req.getId(), "id");
        ParamCheckUtil.checkNotNull(req.getEnable(), "enable");

        return groupService.updateGroupEnableStatueReq(req);
    }

    @Get
    @Mapping("/rule/page")
    public PageInfo<SecurityRuleListRes> rulePage(PageQuery pageQuery, SecurityRuleListReq req) {
        ParamCheckUtil.checkNotNull(pageQuery, "pageQuery");

        return groupService.rulePage(pageQuery, req);
    }

    @Get
    @Mapping("/rule/list")
    public List<SecurityRuleListRes> getRuleListByGroupId(SecurityRuleListReq req) {
        return groupService.ruleList(req);
    }

    @Post
    @Mapping("/rule/create")
    public void createRule(SecurityRuleCreateReq req) {
        groupService.createRule(req);
    }

    @Post
    @Mapping("/rule/update")
    public void updateRule(SecurityRuleUpdateReq req) {
        groupService.updateRule(req);
    }

    @Post
    @Mapping("/rule/delete")
    public void deleteRule(Integer ruleId) {
        groupService.deleteRule(ruleId);
    }


    @Post
    @Mapping("/rule/update/enable-status")
    public SecurityRuleUpdateEnableStatueRes updateRuleEnableStatueReq(SecurityRuleUpdateEnableStatueReq req) {
        ParamCheckUtil.checkNotNull(req, "req");
        ParamCheckUtil.checkNotNull(req.getId(), "id");
        ParamCheckUtil.checkNotNull(req.getEnable(), "enable");

        return groupService.updateRuleEnableStatueReq(req);
    }

}

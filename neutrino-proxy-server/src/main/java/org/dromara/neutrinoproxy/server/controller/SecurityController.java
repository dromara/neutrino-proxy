package org.dromara.neutrinoproxy.server.controller;

import org.dromara.neutrinoproxy.server.base.page.PageInfo;
import org.dromara.neutrinoproxy.server.controller.req.system.SecurityGroupCreateReq;
import org.dromara.neutrinoproxy.server.controller.req.system.SecurityGroupUpdateReq;
import org.dromara.neutrinoproxy.server.controller.res.system.SecurityGroupListReq;
import org.dromara.neutrinoproxy.server.controller.res.system.SecurityRuleListRes;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Get;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Post;

import java.util.List;

@Controller
@Mapping("/security")
public class SecurityController {


    /**
     * 获取当前用户权限下的安全组
     */
    @Get
    @Mapping("/group/s")
    public List<SecurityGroupListReq> getGroups() {

        return null;
    }

    @Post
    @Mapping("/group/create")
    public void createGroup(SecurityGroupCreateReq req) {

    }

    @Post
    @Mapping("/group/update")
    public void updateGroup(SecurityGroupUpdateReq req) {

    }

    /**
     * 将级联删除对应规则
     * @param groupId
     */
    @Post
    @Mapping("/group/delete")
    public void updateGroup(Integer groupId) {

    }

    @Post
    @Mapping("/group/enable")
    public void enableGroup(Integer groupId) {

    }

    @Post
    @Mapping("/group/disable")
    public void disableGroup(Integer groupId) {

    }

    @Post
    @Mapping("/port/bind/group")
    public void portBindGroup(Integer portId, Integer groupId) {

    }

    @Get
    @Mapping("/rule/s")
    public List<SecurityRuleListRes> getRulesByGroupId(Integer groupId) {

        return null;
    }

    @Post
    @Mapping("/rule/create")
    public void createRule() {

    }

    @Post
    @Mapping("/rule/update")
    public void updateRule() {

    }

    @Post
    @Mapping("/rule/delete")
    public void deleteRule(Integer ruleId) {

    }


    @Post
    @Mapping("/rule/enable")
    public void enableRule(Integer ruleId) {

    }

    @Post
    @Mapping("/rule/disable")
    public void disableRule(Integer ruleId) {

    }

}

package fun.asgc.neutrino.proxy.server.service;

import fun.asgc.neutrino.proxy.server.base.page.PageInfo;
import fun.asgc.neutrino.proxy.server.base.page.PageQuery;
import fun.asgc.neutrino.proxy.server.controller.req.LicenseFlowReportReq;
import fun.asgc.neutrino.proxy.server.controller.req.UserFlowReportReq;
import fun.asgc.neutrino.proxy.server.controller.res.LicenseFlowReportRes;
import fun.asgc.neutrino.proxy.server.controller.res.UserFlowReportRes;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.annotation.Component;

/**
 * @author: aoshiguchen
 * @date: 2022/12/23
 */
@Slf4j
@Component
public class ReportService {

    /**
     * 用户流量报表分页
     * @param pageQuery
     * @param req
     * @return
     */
    public PageInfo<UserFlowReportRes> userFlowReportPage(PageQuery pageQuery, UserFlowReportReq req) {
        // TODO
        return null;
    }

    /**
     * license流量报表分页
     * @param pageQuery
     * @param req
     * @return
     */
    public PageInfo<LicenseFlowReportRes> licenseFlowReportPage(PageQuery pageQuery, LicenseFlowReportReq req) {
        // TODO
        return null;
    }
}

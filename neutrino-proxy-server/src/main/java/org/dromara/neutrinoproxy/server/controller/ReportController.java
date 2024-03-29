package org.dromara.neutrinoproxy.server.controller;

import org.dromara.neutrinoproxy.server.base.page.PageInfo;
import org.dromara.neutrinoproxy.server.base.page.PageQuery;
import org.dromara.neutrinoproxy.server.controller.req.report.LicenseFlowMonthReportReq;
import org.dromara.neutrinoproxy.server.controller.req.report.LicenseFlowReportReq;
import org.dromara.neutrinoproxy.server.controller.req.report.UserFlowMonthReportReq;
import org.dromara.neutrinoproxy.server.controller.req.report.UserFlowReportReq;
import org.dromara.neutrinoproxy.server.controller.res.report.*;
import org.dromara.neutrinoproxy.server.controller.res.report.*;
import org.dromara.neutrinoproxy.server.service.ReportService;
import org.dromara.neutrinoproxy.server.util.ParamCheckUtil;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Get;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;

/**
 * 报表管理
 * @author: aoshiguchen
 * @date: 2022/9/12
 */
@Mapping("/report")
@Controller
public class ReportController {

    @Inject
    private ReportService reportService;

    /**
     * 首页数据一览
     * @return
     */
    @Get
    @Mapping("/home/data-view")
    public HomeDataView homeDataView() {
        return reportService.homeDataView();
    }

    /**
     * 用户流量报表分页
     * @param pageQuery
     * @param req
     * @return
     */
    @Get
    @Mapping("/user/flow-report/page")
    public PageInfo<UserFlowReportRes> userFlowReportPage(PageQuery pageQuery, UserFlowReportReq req) {
        ParamCheckUtil.checkNotNull(pageQuery, "pageQuery");

        return reportService.userFlowReportPage(pageQuery, req);
    }

    /**
     * license流量报表分页
     * @param pageQuery
     * @param req
     * @return
     */
    @Get
    @Mapping("/license/flow-report/page")
    public PageInfo<LicenseFlowReportRes> licenseFlowReportPage(PageQuery pageQuery, LicenseFlowReportReq req) {
        ParamCheckUtil.checkNotNull(pageQuery, "pageQuery");

        return reportService.licenseFlowReportPage(pageQuery, req);
    }

    /**
     * 用户流量报表月度明细
     * @param pageQuery
     * @param req
     * @return
     */
    @Get
    @Mapping("/user/flow-month-report/page")
    public PageInfo<UserFlowMonthReportRes> userFlowMonthReportPage(PageQuery pageQuery, UserFlowMonthReportReq req) {
        ParamCheckUtil.checkNotNull(pageQuery, "pageQuery");

        return reportService.userFlowMonthReportPage(pageQuery, req);
    }

    /**
     * license流量报表月度明细
     * @param pageQuery
     * @param req
     * @return
     */
    @Get
    @Mapping("/license/flow-month-report/page")
    public PageInfo<LicenseFlowMonthReportRes> licenseFlowMonthReportPage(PageQuery pageQuery, LicenseFlowMonthReportReq req) {
        ParamCheckUtil.checkNotNull(pageQuery, "pageQuery");

        return reportService.licenseFlowMonthReportPage(pageQuery, req);
    }
}

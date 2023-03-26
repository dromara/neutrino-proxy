package fun.asgc.neutrino.proxy.server.controller;

import fun.asgc.neutrino.proxy.server.base.page.PageInfo;
import fun.asgc.neutrino.proxy.server.base.page.PageQuery;
import fun.asgc.neutrino.proxy.server.controller.req.report.LicenseFlowMonthReportReq;
import fun.asgc.neutrino.proxy.server.controller.req.report.LicenseFlowReportReq;
import fun.asgc.neutrino.proxy.server.controller.req.report.UserFlowMonthReportReq;
import fun.asgc.neutrino.proxy.server.controller.req.report.UserFlowReportReq;
import fun.asgc.neutrino.proxy.server.controller.res.report.*;
import fun.asgc.neutrino.proxy.server.service.ReportService;
import fun.asgc.neutrino.proxy.server.util.ParamCheckUtil;
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
     * 数据一览：
     * 在线用户数 查询token有效的用户ID数
     * 在线Client数 查询license表，状态为在线的数量
     * 今日流量 上行/下行/总计
     * 历史流量 上行/下行/总计
     *
     * 1、点击在线用户数：下方展示在线的用户列表，带分页
     * 2、点击在线client数：下方展示在线的license列表，带分页
     * 3、点击今日流量：下方展示今日的流量折线图（小时）。 筛选项：全部/用户列表
     * 4、点击历史流量：下方展示历史流量折线图。筛选项：日/月 日期选择：日（展示最近30天，最多跨30日），月（展示最近12个月，最多跨12个月）
     * @return
     */
    @Get
    @Mapping("/data-view")
    public ReportDataViewRes dataView() {
        return new ReportDataViewRes()
                .setUserOnlineNumber(2).setEnableUserNumber(3).setUserNumber(5)
                .setLicenseNumber(3).setEnableLicenseNumber(6).setLicenseNumber(6)
                .setServerPortOnlineNumber(3).setEnableServerPortNumber(5).setServerPortNumber(6)
                .setTotalUpstreamFlow("23K").setTotalDownwardFlow("47M")
                ;
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

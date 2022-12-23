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
package fun.asgc.neutrino.proxy.server.controller;

import fun.asgc.neutrino.core.annotation.Autowired;
import fun.asgc.neutrino.core.annotation.NonIntercept;
import fun.asgc.neutrino.core.db.page.Page;
import fun.asgc.neutrino.core.db.page.PageQuery;
import fun.asgc.neutrino.core.web.annotation.GetMapping;
import fun.asgc.neutrino.core.web.annotation.RequestMapping;
import fun.asgc.neutrino.core.web.annotation.RestController;
import fun.asgc.neutrino.proxy.server.controller.req.LicenseFlowReportReq;
import fun.asgc.neutrino.proxy.server.controller.req.UserFlowReportReq;
import fun.asgc.neutrino.proxy.server.controller.res.LicenseFlowReportRes;
import fun.asgc.neutrino.proxy.server.controller.res.ReportDataViewRes;
import fun.asgc.neutrino.proxy.server.controller.res.UserFlowReportRes;
import fun.asgc.neutrino.proxy.server.service.ReportService;
import fun.asgc.neutrino.proxy.server.util.ParamCheckUtil;

/**
 * 报表管理
 * @author: aoshiguchen
 * @date: 2022/9/12
 */
@NonIntercept
@RequestMapping("report")
@RestController
public class ReportController {

    @Autowired
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
    @GetMapping("data-view")
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
    @GetMapping("user/flow-report/page")
    public Page<UserFlowReportRes> userFlowReportPage(PageQuery pageQuery, UserFlowReportReq req) {
        ParamCheckUtil.checkNotNull(pageQuery, "pageQuery");

        return reportService.userFlowReportPage(pageQuery, req);
    }

    /**
     * license流量报表分页
     * @param pageQuery
     * @param req
     * @return
     */
    @GetMapping("license/flow-report/page")
    public Page<LicenseFlowReportRes> licenseFlowReportPage(PageQuery pageQuery, LicenseFlowReportReq req) {
        ParamCheckUtil.checkNotNull(pageQuery, "pageQuery");

        return reportService.licenseFlowReportPage(pageQuery, req);
    }
}

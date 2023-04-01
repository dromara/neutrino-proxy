package org.dromara.neutrinoproxy.server.service;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import org.dromara.neutrinoproxy.core.util.DateUtil;
import org.dromara.neutrinoproxy.server.base.db.DbConfig;
import org.dromara.neutrinoproxy.server.base.page.PageInfo;
import org.dromara.neutrinoproxy.server.base.page.PageQuery;
import org.dromara.neutrinoproxy.server.constant.Constants;
import org.dromara.neutrinoproxy.server.constant.OnlineStatusEnum;
import org.dromara.neutrinoproxy.server.controller.req.report.LicenseFlowMonthReportReq;
import org.dromara.neutrinoproxy.server.controller.req.report.LicenseFlowReportReq;
import org.dromara.neutrinoproxy.server.controller.req.report.UserFlowMonthReportReq;
import org.dromara.neutrinoproxy.server.controller.req.report.UserFlowReportReq;
import org.dromara.neutrinoproxy.server.controller.res.report.*;
import org.dromara.neutrinoproxy.server.dal.LicenseMapper;
import org.dromara.neutrinoproxy.server.dal.PortMappingMapper;
import org.dromara.neutrinoproxy.server.dal.ReportMapper;
import org.dromara.neutrinoproxy.server.dal.entity.LicenseDO;
import org.dromara.neutrinoproxy.server.dal.entity.PortMappingDO;
import org.dromara.neutrinoproxy.server.service.bo.FlowBO;
import org.dromara.neutrinoproxy.server.service.bo.SingleDayFlowBO;
import org.dromara.neutrinoproxy.server.util.FormatUtil;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import org.apache.ibatis.solon.annotation.Db;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: aoshiguchen
 * @date: 2022/12/23
 */
@Slf4j
@Component
public class ReportService {
    @Inject
    private MapperFacade mapperFacade;
    @Db
    private ReportMapper reportMapper;
    @Db
    private LicenseMapper licenseMapper;
    @Db
    private PortMappingMapper portMappingMapper;
    @Inject
    private DbConfig dbConfig;

    /**
     * 首页图表
     * @return
     */
    public HomeDataView homeDataView() {
        HomeDataView homeDataView = new HomeDataView();
        homeDataView.setLicense(new HomeDataView.License().setTotalCount(0).setOnlineCount(0));
        homeDataView.setPortMapping(new HomeDataView.PortMapping().setTotalCount(0).setOnlineCount(0));
        homeDataView.setTodayFlow(new HomeDataView.TodayFlow().setUpFlowBytes(0L).setDownFlowBytes(0L));
        homeDataView.setTotalFlow(new HomeDataView.TotalFlow().setUpFlowBytes(0L).setDownFlowBytes(0L));

        // 查询license列表
        List<LicenseDO> licenseDOList = licenseMapper.listAll();
        if (CollectionUtil.isNotEmpty(licenseDOList)) {
            homeDataView.getLicense().setTotalCount(licenseDOList.size());
            homeDataView.getLicense().setOnlineCount((int)licenseDOList.stream().filter(e -> OnlineStatusEnum.ONLINE.getStatus().equals(e.getIsOnline())).count());
        }
        // 查询端口映射列表
        List<PortMappingDO> portMappingDOList = portMappingMapper.selectList(new LambdaQueryWrapper<>());
        if (CollectionUtil.isNotEmpty(portMappingDOList)) {
            homeDataView.getPortMapping().setTotalCount(portMappingDOList.size());
            homeDataView.getPortMapping().setOnlineCount((int)portMappingDOList.stream().filter(e -> OnlineStatusEnum.ONLINE.getStatus().equals(e.getIsOnline())).count());
        }
        // 今日流量
        Date now = new Date();
        FlowBO todayFlow = reportMapper.homeTodayFlow(DateUtil.getDayBegin(now), now);
        homeDataView.setTodayFlow(mapperFacade.map(todayFlow, HomeDataView.TodayFlow.class));

        // 总流量
        FlowBO totalFlow = reportMapper.homeTotalFlow(DateUtil.getMonthBegin(now), DateUtil.getDayBegin(now), now);
        homeDataView.setTotalFlow(mapperFacade.map(totalFlow, HomeDataView.TotalFlow.class));

        // 最近n日流量
        Integer days = Constants.HOME_FLOW_DAYS;
        List<SingleDayFlowBO> last7dFlowList = reportMapper.homeLast7dFlowList(DateUtil.getDayBegin(DateUtil.addDate(now, Calendar.DATE, -(days - 1))), DateUtil.getDayBegin(now), now);
        homeDataView.setLast7dFlow(new HomeDataView.Last7dFlow());
        homeDataView.getLast7dFlow().setDataList(mapperFacade.mapAsList(last7dFlowList, HomeDataView.SingleDayFlow.class));

        // 数据处理
        fillHomeDataView(homeDataView, now);
        return homeDataView;
    }

    /**
     * 用户流量报表分页
     * @param pageQuery
     * @param req
     * @return
     */
    public PageInfo<UserFlowReportRes> userFlowReportPage(PageQuery pageQuery, UserFlowReportReq req) {
        Page<UserFlowReportRes> result = PageHelper.startPage(pageQuery.getCurrent(), pageQuery.getSize());
        Date now = new Date();
        List<UserFlowReportRes> list = reportMapper.userFlowReportList(req.getUserId(), DateUtil.getMonthBegin(now), DateUtil.getDayBegin(now), now);
        fillUserFlowReport(list);
        return PageInfo.of(list, result.getTotal(), pageQuery.getCurrent(), pageQuery.getSize());
    }

    /**
     * license流量报表分页
     * @param pageQuery
     * @param req
     * @return
     */
    public PageInfo<LicenseFlowReportRes> licenseFlowReportPage(PageQuery pageQuery, LicenseFlowReportReq req) {
        Page<LicenseFlowReportRes> result = PageHelper.startPage(pageQuery.getCurrent(), pageQuery.getSize());
        Date now = new Date();
        List<LicenseFlowReportRes> list = reportMapper.licenseFLowReportList(req.getUserId(), DateUtil.getMonthBegin(now), DateUtil.getDayBegin(now), now);
        fillLicenseFlowReport(list);
        return PageInfo.of(list, result.getTotal(), pageQuery.getCurrent(), pageQuery.getSize());
    }

    /**
     * 用户流量月度明细
     * @param pageQuery
     * @param req
     * @return
     */
    public PageInfo<UserFlowMonthReportRes> userFlowMonthReportPage(PageQuery pageQuery, UserFlowMonthReportReq req) {
        Page<UserFlowMonthReportRes> result = PageHelper.startPage(pageQuery.getCurrent(), pageQuery.getSize());
        Date now = new Date();
        List<UserFlowMonthReportRes> list = reportMapper.userFlowMonthReportList(req.getUserId(), DateUtil.getMonthBegin(now), DateUtil.getDayBegin(now), now);
        fillUserFlowMonthReport(list);
        return PageInfo.of(list, result.getTotal(), pageQuery.getCurrent(), pageQuery.getSize());
    }

    /**
     * license流量月度明细
     * @param pageQuery
     * @param req
     * @return
     */
    public PageInfo<LicenseFlowMonthReportRes> licenseFlowMonthReportPage(PageQuery pageQuery, LicenseFlowMonthReportReq req) {
        Page<LicenseFlowMonthReportRes> result = PageHelper.startPage(pageQuery.getCurrent(), pageQuery.getSize());
        Date now = new Date();
        List<LicenseFlowMonthReportRes> list = reportMapper.licenseFLowMonthReportList(req.getUserId(), req.getLicenseId(), DateUtil.getMonthBegin(now), DateUtil.getDayBegin(now), now);
        fillLicenseFlowMonthReport(list);
        return PageInfo.of(list, result.getTotal(), pageQuery.getCurrent(), pageQuery.getSize());
    }

    private void fillUserFlowReport(List<UserFlowReportRes> list) {
        if (CollectionUtil.isEmpty(list)) {
            return;
        }
        for (UserFlowReportRes item : list) {
            long upFlowBytes = (null == item.getUpFlowBytes()) ? 0 : item.getUpFlowBytes();
            long downFlowBytes = (null == item.getDownFlowBytes()) ? 0 : item.getDownFlowBytes();
            long totalFlowBytes = upFlowBytes + downFlowBytes;
            item.setUpFlowBytes(upFlowBytes);
            item.setDownFlowBytes(downFlowBytes);
            item.setTotalFlowBytes(totalFlowBytes);
            item.setUpFlowDesc(FormatUtil.getSizeDescByByteCount(upFlowBytes));
            item.setDownFlowDesc(FormatUtil.getSizeDescByByteCount(downFlowBytes));
            item.setTotalFlowDesc(FormatUtil.getSizeDescByByteCount(totalFlowBytes));
        }
    }

    private void fillLicenseFlowReport(List<LicenseFlowReportRes> list) {
        if (CollectionUtil.isEmpty(list)) {
            return;
        }
        for (LicenseFlowReportRes item : list) {
            long upFlowBytes = (null == item.getUpFlowBytes()) ? 0 : item.getUpFlowBytes();
            long downFlowBytes = (null == item.getDownFlowBytes()) ? 0 : item.getDownFlowBytes();
            long totalFlowBytes = upFlowBytes + downFlowBytes;
            item.setUpFlowBytes(upFlowBytes);
            item.setDownFlowBytes(downFlowBytes);
            item.setTotalFlowBytes(totalFlowBytes);
            item.setUpFlowDesc(FormatUtil.getSizeDescByByteCount(upFlowBytes));
            item.setDownFlowDesc(FormatUtil.getSizeDescByByteCount(downFlowBytes));
            item.setTotalFlowDesc(FormatUtil.getSizeDescByByteCount(totalFlowBytes));
        }
    }

    private void fillUserFlowMonthReport(List<UserFlowMonthReportRes> list) {
        if (CollectionUtil.isEmpty(list)) {
            return;
        }
        for (UserFlowMonthReportRes item : list) {
            long upFlowBytes = (null == item.getUpFlowBytes()) ? 0 : item.getUpFlowBytes();
            long downFlowBytes = (null == item.getDownFlowBytes()) ? 0 : item.getDownFlowBytes();
            long totalFlowBytes = upFlowBytes + downFlowBytes;
            item.setUpFlowBytes(upFlowBytes);
            item.setDownFlowBytes(downFlowBytes);
            item.setTotalFlowBytes(totalFlowBytes);
            item.setUpFlowDesc(FormatUtil.getSizeDescByByteCount(upFlowBytes));
            item.setDownFlowDesc(FormatUtil.getSizeDescByByteCount(downFlowBytes));
            item.setTotalFlowDesc(FormatUtil.getSizeDescByByteCount(totalFlowBytes));
        }
    }

    private void fillLicenseFlowMonthReport(List<LicenseFlowMonthReportRes> list) {
        if (CollectionUtil.isEmpty(list)) {
            return;
        }
        for (LicenseFlowMonthReportRes item : list) {
            long upFlowBytes = (null == item.getUpFlowBytes()) ? 0 : item.getUpFlowBytes();
            long downFlowBytes = (null == item.getDownFlowBytes()) ? 0 : item.getDownFlowBytes();
            long totalFlowBytes = upFlowBytes + downFlowBytes;
            item.setUpFlowBytes(upFlowBytes);
            item.setDownFlowBytes(downFlowBytes);
            item.setTotalFlowBytes(totalFlowBytes);
            item.setUpFlowDesc(FormatUtil.getSizeDescByByteCount(upFlowBytes));
            item.setDownFlowDesc(FormatUtil.getSizeDescByByteCount(downFlowBytes));
            item.setTotalFlowDesc(FormatUtil.getSizeDescByByteCount(totalFlowBytes));
        }
    }

    private void fillHomeDataView(HomeDataView homeDataView, Date now) {
        if (null == homeDataView) {
            return;
        }
        // License
        if (null == homeDataView.getLicense()) {
            homeDataView.setLicense(new HomeDataView.License());
        }
        if (null == homeDataView.getLicense().getTotalCount()) {
            homeDataView.getLicense().setTotalCount(0);
        }
        if (null == homeDataView.getLicense().getOnlineCount()) {
            homeDataView.getLicense().setOnlineCount(0);
        }
        homeDataView.getLicense().setOfflineCount(homeDataView.getLicense().getTotalCount() - homeDataView.getLicense().getOnlineCount());

        // PortMapping
        if (null == homeDataView.getPortMapping()) {
            homeDataView.setPortMapping(new HomeDataView.PortMapping());
        }
        if (null == homeDataView.getPortMapping().getTotalCount()) {
            homeDataView.getPortMapping().setTotalCount(0);
        }
        if (null == homeDataView.getPortMapping().getOnlineCount()) {
            homeDataView.getPortMapping().setOnlineCount(0);
        }
        homeDataView.getPortMapping().setOfflineCount(homeDataView.getPortMapping().getTotalCount() - homeDataView.getPortMapping().getOnlineCount());

        // TodayFlow
        if (null == homeDataView.getTodayFlow()) {
            homeDataView.setTodayFlow(new HomeDataView.TodayFlow());
        }
        if (null == homeDataView.getTodayFlow().getUpFlowBytes()) {
            homeDataView.getTodayFlow().setUpFlowBytes(0L);
        }
        if (null == homeDataView.getTodayFlow().getDownFlowBytes()) {
            homeDataView.getTodayFlow().setDownFlowBytes(0L);
        }
        homeDataView.getTodayFlow().setTotalFlowBytes(homeDataView.getTodayFlow().getUpFlowBytes() + homeDataView.getTodayFlow().getDownFlowBytes());
        homeDataView.getTodayFlow().setUpFlowDesc(FormatUtil.getSizeDescByByteCount(homeDataView.getTodayFlow().getUpFlowBytes()));
        homeDataView.getTodayFlow().setDownFlowDesc(FormatUtil.getSizeDescByByteCount(homeDataView.getTodayFlow().getDownFlowBytes()));
        homeDataView.getTodayFlow().setTotalFlowDesc(FormatUtil.getSizeDescByByteCount(homeDataView.getTodayFlow().getTotalFlowBytes()));

        // TotalFlow
        if (null == homeDataView.getTotalFlow()) {
            homeDataView.setTotalFlow(new HomeDataView.TotalFlow());
        }
        if (null == homeDataView.getTotalFlow().getUpFlowBytes()) {
            homeDataView.getTotalFlow().setUpFlowBytes(0L);
        }
        if (null == homeDataView.getTotalFlow().getDownFlowBytes()) {
            homeDataView.getTotalFlow().setDownFlowBytes(0L);
        }
        homeDataView.getTotalFlow().setTotalFlowBytes(homeDataView.getTotalFlow().getUpFlowBytes() + homeDataView.getTotalFlow().getDownFlowBytes());
        homeDataView.getTotalFlow().setUpFlowDesc(FormatUtil.getSizeDescByByteCount(homeDataView.getTotalFlow().getUpFlowBytes()));
        homeDataView.getTotalFlow().setDownFlowDesc(FormatUtil.getSizeDescByByteCount(homeDataView.getTotalFlow().getDownFlowBytes()));
        homeDataView.getTotalFlow().setTotalFlowDesc(FormatUtil.getSizeDescByByteCount(homeDataView.getTotalFlow().getTotalFlowBytes()));

        // 最近N日流量
        Integer days = Constants.HOME_FLOW_DAYS;
        HomeDataView.Last7dFlow last7dFlow = homeDataView.getLast7dFlow();
        if (null == last7dFlow) {
            last7dFlow = new HomeDataView.Last7dFlow();
            homeDataView.setLast7dFlow(last7dFlow);
        }
        if (null == last7dFlow.getDataList()) {
            last7dFlow.setDataList(Collections.emptyList());
        }
        // 数据列表
        Set<String> existDateStrList = new HashSet<>();
        if (CollectionUtil.isNotEmpty(last7dFlow.getDataList())) {
            for (HomeDataView.SingleDayFlow singleDayFlow : last7dFlow.getDataList()) {
                if (null == singleDayFlow.getUpFlowBytes()) {
                    singleDayFlow.setUpFlowBytes(0L);
                }
                if (null == singleDayFlow.getDownFlowBytes()) {
                    singleDayFlow.setDownFlowBytes(0L);
                }
                singleDayFlow.setTotalFlowBytes(singleDayFlow.getUpFlowBytes() + singleDayFlow.getDownFlowBytes());
                singleDayFlow.setUpFlowDesc(FormatUtil.getSizeDescByByteCount(singleDayFlow.getUpFlowBytes()));
                singleDayFlow.setDownFlowDesc(FormatUtil.getSizeDescByByteCount(singleDayFlow.getDownFlowBytes()));
                singleDayFlow.setTotalFlowDesc(FormatUtil.getSizeDescByByteCount(singleDayFlow.getTotalFlowBytes()));
                singleDayFlow.setDateStr(DateUtil.format(singleDayFlow.getDate(), "yyyy-MM-dd"));
                existDateStrList.add(singleDayFlow.getDateStr());
            }
        }
        // 获取最近7天的日期字符串列表。防止因统计数据缺失，造成图表展示错误的问题，缺失的日期数据，自动填充0
        List<String> dateList = DateUtil.getBetweenTimes(DateUtil.format(DateUtil.addDate(now, Calendar.DATE, -(days - 1)), "yyyy-MM-dd"), DateUtil.format(now, "yyyy-MM-dd"));
        for (String dateStr : dateList) {
            if (existDateStrList.contains(dateStr)) {
                continue;
            }
            last7dFlow.getDataList().add(new HomeDataView.SingleDayFlow()
                    .setDate(DateUtil.parse(dateStr, "yyyy-MM-dd"))
                    .setDateStr(dateStr)
                    .setUpFlowBytes(0L)
                    .setUpFlowDesc("0B")
                    .setDownFlowBytes(0L)
                    .setDownFlowDesc("0B")
                    .setTotalFlowBytes(0L)
                    .setTotalFlowDesc("0B")
            );
        }

        // 按日期升序
        Collections.sort(last7dFlow.getDataList(), Comparator.comparing(HomeDataView.SingleDayFlow::getDate));
        // x轴日期
        last7dFlow.setXDate(last7dFlow.getDataList().stream().map(HomeDataView.SingleDayFlow::getDateStr).collect(Collectors.toList()));
        // 图例
        last7dFlow.setLegendData(Lists.newArrayList("上行流量", "下行流量", "总流量"));
        // 折线图
        List<HomeDataView.Series> seriesList = Lists.newArrayList();
        last7dFlow.setSeriesList(seriesList);
        // 上行流量折线
        seriesList.add(new HomeDataView.Series()
                .setSeriesType("line")
                .setSeriesName(last7dFlow.getLegendData().get(0))
                .setSeriesData(last7dFlow.getDataList().stream().map(HomeDataView.SingleDayFlow::getUpFlowBytes).collect(Collectors.toList()))
        );
        // 下行流量折线
        seriesList.add(new HomeDataView.Series()
                .setSeriesType("line")
                .setSeriesName(last7dFlow.getLegendData().get(1))
                .setSeriesData(last7dFlow.getDataList().stream().map(HomeDataView.SingleDayFlow::getDownFlowBytes).collect(Collectors.toList()))
        );
        // 总流量折线
        seriesList.add(new HomeDataView.Series()
                .setSeriesType("line")
                .setSeriesName(last7dFlow.getLegendData().get(2))
                .setSeriesData(last7dFlow.getDataList().stream().map(HomeDataView.SingleDayFlow::getTotalFlowBytes).collect(Collectors.toList()))
        );
    }
}

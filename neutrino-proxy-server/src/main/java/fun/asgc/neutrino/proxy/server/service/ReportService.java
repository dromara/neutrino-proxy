package fun.asgc.neutrino.proxy.server.service;

import cn.hutool.core.collection.CollectionUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import fun.asgc.neutrino.proxy.core.util.DateUtil;
import fun.asgc.neutrino.proxy.server.base.db.DbConfig;
import fun.asgc.neutrino.proxy.server.base.page.PageInfo;
import fun.asgc.neutrino.proxy.server.base.page.PageQuery;
import fun.asgc.neutrino.proxy.server.controller.req.report.LicenseFlowMonthReportReq;
import fun.asgc.neutrino.proxy.server.controller.req.report.LicenseFlowReportReq;
import fun.asgc.neutrino.proxy.server.controller.req.report.UserFlowMonthReportReq;
import fun.asgc.neutrino.proxy.server.controller.req.report.UserFlowReportReq;
import fun.asgc.neutrino.proxy.server.controller.res.report.LicenseFlowMonthReportRes;
import fun.asgc.neutrino.proxy.server.controller.res.report.LicenseFlowReportRes;
import fun.asgc.neutrino.proxy.server.controller.res.report.UserFlowMonthReportRes;
import fun.asgc.neutrino.proxy.server.controller.res.report.UserFlowReportRes;
import fun.asgc.neutrino.proxy.server.dal.ReportMapper;
import fun.asgc.neutrino.proxy.server.util.FormatUtil;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import org.apache.ibatis.solon.annotation.Db;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;

import java.util.Date;
import java.util.List;

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
    @Inject
    private DbConfig dbConfig;

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
}

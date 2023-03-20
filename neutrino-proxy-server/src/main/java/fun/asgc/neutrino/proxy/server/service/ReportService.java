package fun.asgc.neutrino.proxy.server.service;

import cn.hutool.core.collection.CollectionUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import fun.asgc.neutrino.proxy.server.base.page.PageInfo;
import fun.asgc.neutrino.proxy.server.base.page.PageQuery;
import fun.asgc.neutrino.proxy.server.controller.req.LicenseFlowReportReq;
import fun.asgc.neutrino.proxy.server.controller.req.UserFlowReportReq;
import fun.asgc.neutrino.proxy.server.controller.res.JobLogListRes;
import fun.asgc.neutrino.proxy.server.controller.res.LicenseFlowReportRes;
import fun.asgc.neutrino.proxy.server.controller.res.UserFlowReportRes;
import fun.asgc.neutrino.proxy.server.dal.ReportMapper;
import fun.asgc.neutrino.proxy.server.util.FormatUtil;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import org.apache.ibatis.solon.annotation.Db;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;

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

    /**
     * 用户流量报表分页
     * @param pageQuery
     * @param req
     * @return
     */
    public PageInfo<UserFlowReportRes> userFlowReportPage(PageQuery pageQuery, UserFlowReportReq req) {
        Page<UserFlowReportRes> result = PageHelper.startPage(pageQuery.getCurrent(), pageQuery.getSize());
        List<UserFlowReportRes> list = reportMapper.userFlowReportList(req);
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
        List<LicenseFlowReportRes> list = reportMapper.licenseFLowReportList(req);
        fillLicenseFlowReport(list);
        return PageInfo.of(list, 25L, pageQuery.getCurrent(), pageQuery.getSize());
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
}

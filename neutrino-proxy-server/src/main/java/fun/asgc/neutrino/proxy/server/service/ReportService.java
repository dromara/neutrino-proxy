package fun.asgc.neutrino.proxy.server.service;

import cn.hutool.core.collection.CollectionUtil;
import com.google.common.collect.Lists;
import fun.asgc.neutrino.proxy.server.base.page.PageInfo;
import fun.asgc.neutrino.proxy.server.base.page.PageQuery;
import fun.asgc.neutrino.proxy.server.controller.req.LicenseFlowReportReq;
import fun.asgc.neutrino.proxy.server.controller.req.UserFlowReportReq;
import fun.asgc.neutrino.proxy.server.controller.res.LicenseFlowReportRes;
import fun.asgc.neutrino.proxy.server.controller.res.UserFlowReportRes;
import fun.asgc.neutrino.proxy.server.util.FormatUtil;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.annotation.Component;

import java.util.List;

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
        List<UserFlowReportRes> all = Lists.newArrayList(
            new UserFlowReportRes().setUserId(1).setUserName("用户1").setUpFlowBytes(100L).setDownFlowBytes(200L),
                new UserFlowReportRes().setUserId(2).setUserName("用户2").setUpFlowBytes(400L).setDownFlowBytes(600L),
                new UserFlowReportRes().setUserId(3).setUserName("用户3").setUpFlowBytes(800L).setDownFlowBytes(1200L),
                new UserFlowReportRes().setUserId(4).setUserName("用户4").setUpFlowBytes(120L).setDownFlowBytes(1600L),
                new UserFlowReportRes().setUserId(5).setUserName("用户5").setUpFlowBytes(1600L).setDownFlowBytes(2000L),
                new UserFlowReportRes().setUserId(6).setUserName("用户6").setUpFlowBytes(2200L).setDownFlowBytes(2500L),
                new UserFlowReportRes().setUserId(7).setUserName("用户7").setUpFlowBytes(2800L).setDownFlowBytes(3200L),
                new UserFlowReportRes().setUserId(8).setUserName("用户8").setUpFlowBytes(41000L).setDownFlowBytes(52000L),
                new UserFlowReportRes().setUserId(9).setUserName("用户9").setUpFlowBytes(310000L).setDownFlowBytes(420000L),
                new UserFlowReportRes().setUserId(10).setUserName("用户10").setUpFlowBytes(51000L).setDownFlowBytes(620000L),
                new UserFlowReportRes().setUserId(11).setUserName("用户11").setUpFlowBytes(310000L).setDownFlowBytes(20000L),
                new UserFlowReportRes().setUserId(12).setUserName("用户12").setUpFlowBytes(23100L).setDownFlowBytes(245200L),
                new UserFlowReportRes().setUserId(13).setUserName("用户13").setUpFlowBytes(98100L).setDownFlowBytes(65200L),
                new UserFlowReportRes().setUserId(14).setUserName("用户14").setUpFlowBytes(234100L).setDownFlowBytes(7809200L),
                new UserFlowReportRes().setUserId(15).setUserName("用户15").setUpFlowBytes(23100L).setDownFlowBytes(4200L),
                new UserFlowReportRes().setUserId(16).setUserName("用户16").setUpFlowBytes(21003100L).setDownFlowBytes(4099200L),
                new UserFlowReportRes().setUserId(17).setUserName("用户17").setUpFlowBytes(23100L).setDownFlowBytes(4200L),
                new UserFlowReportRes().setUserId(18).setUserName("用户18").setUpFlowBytes(10023100L).setDownFlowBytes(20004200L),
                new UserFlowReportRes().setUserId(19).setUserName("用户19").setUpFlowBytes(300023100L).setDownFlowBytes(40004200L),
                new UserFlowReportRes().setUserId(20).setUserName("用户20").setUpFlowBytes(5000023100L).setDownFlowBytes(600004200L),
                new UserFlowReportRes().setUserId(21).setUserName("用户21").setUpFlowBytes(700023100L).setDownFlowBytes(80094200L),
                new UserFlowReportRes().setUserId(22).setUserName("用户22").setUpFlowBytes(40000023100L).setDownFlowBytes(6000004200L),
                new UserFlowReportRes().setUserId(23).setUserName("用户23").setUpFlowBytes(300000023100L).setDownFlowBytes(40000004200L),
                new UserFlowReportRes().setUserId(24).setUserName("用户24").setUpFlowBytes(300998723100L).setDownFlowBytes(2099877664200L),
                new UserFlowReportRes().setUserId(25).setUserName("用户25").setUpFlowBytes(50987668623100L).setDownFlowBytes(30987657864200L)
        );
        List<UserFlowReportRes> list = null;
        if (pageQuery.getCurrent() == 1) {
            list = all.subList(0, 10);
        } else if (pageQuery.getCurrent() == 2){
            list = all.subList(10, 20);
        } else {
            list = all.subList(20, all.size());
        }
        fillUserFlowReport(list);
        return PageInfo.of(list, 25L, pageQuery.getCurrent(), pageQuery.getSize());
    }

    /**
     * license流量报表分页
     * @param pageQuery
     * @param req
     * @return
     */
    public PageInfo<LicenseFlowReportRes> licenseFlowReportPage(PageQuery pageQuery, LicenseFlowReportReq req) {
        // TODO
        List<LicenseFlowReportRes> all = Lists.newArrayList(
                new LicenseFlowReportRes().setUserId(1).setUserName("用户1").setLicenseName("license1").setUpFlowBytes(100L).setDownFlowBytes(200L),
                new LicenseFlowReportRes().setUserId(2).setUserName("用户2").setLicenseName("license1").setUpFlowBytes(400L).setDownFlowBytes(600L),
                new LicenseFlowReportRes().setUserId(3).setUserName("用户3").setLicenseName("license1").setUpFlowBytes(800L).setDownFlowBytes(1200L),
                new LicenseFlowReportRes().setUserId(4).setUserName("用户4").setLicenseName("license1").setUpFlowBytes(120L).setDownFlowBytes(1600L),
                new LicenseFlowReportRes().setUserId(5).setUserName("用户5").setLicenseName("license1").setUpFlowBytes(1600L).setDownFlowBytes(2000L),
                new LicenseFlowReportRes().setUserId(6).setUserName("用户6").setLicenseName("license1").setUpFlowBytes(2200L).setDownFlowBytes(2500L),
                new LicenseFlowReportRes().setUserId(7).setUserName("用户7").setLicenseName("license1").setUpFlowBytes(2800L).setDownFlowBytes(3200L),
                new LicenseFlowReportRes().setUserId(8).setUserName("用户8").setLicenseName("license1").setUpFlowBytes(41000L).setDownFlowBytes(52000L),
                new LicenseFlowReportRes().setUserId(9).setUserName("用户9").setLicenseName("license1").setUpFlowBytes(310000L).setDownFlowBytes(420000L),
                new LicenseFlowReportRes().setUserId(10).setUserName("用户10").setLicenseName("license1").setUpFlowBytes(51000L).setDownFlowBytes(620000L),
                new LicenseFlowReportRes().setUserId(11).setUserName("用户11").setLicenseName("license1").setUpFlowBytes(310000L).setDownFlowBytes(20000L),
                new LicenseFlowReportRes().setUserId(12).setUserName("用户12").setLicenseName("license1").setUpFlowBytes(23100L).setDownFlowBytes(245200L),
                new LicenseFlowReportRes().setUserId(13).setUserName("用户13").setLicenseName("license1").setUpFlowBytes(98100L).setDownFlowBytes(65200L),
                new LicenseFlowReportRes().setUserId(14).setUserName("用户14").setLicenseName("license1").setUpFlowBytes(234100L).setDownFlowBytes(7809200L),
                new LicenseFlowReportRes().setUserId(15).setUserName("用户15").setLicenseName("license1").setUpFlowBytes(23100L).setDownFlowBytes(4200L),
                new LicenseFlowReportRes().setUserId(16).setUserName("用户16").setLicenseName("license1").setUpFlowBytes(21003100L).setDownFlowBytes(4099200L),
                new LicenseFlowReportRes().setUserId(17).setUserName("用户17").setLicenseName("license1").setUpFlowBytes(23100L).setDownFlowBytes(4200L),
                new LicenseFlowReportRes().setUserId(18).setUserName("用户18").setLicenseName("license1").setUpFlowBytes(10023100L).setDownFlowBytes(20004200L),
                new LicenseFlowReportRes().setUserId(19).setUserName("用户19").setLicenseName("license1").setUpFlowBytes(300023100L).setDownFlowBytes(40004200L),
                new LicenseFlowReportRes().setUserId(20).setUserName("用户20").setLicenseName("license1").setUpFlowBytes(5000023100L).setDownFlowBytes(600004200L),
                new LicenseFlowReportRes().setUserId(21).setUserName("用户21").setLicenseName("license1").setUpFlowBytes(700023100L).setDownFlowBytes(80094200L),
                new LicenseFlowReportRes().setUserId(22).setUserName("用户22").setLicenseName("license1").setUpFlowBytes(40000023100L).setDownFlowBytes(6000004200L),
                new LicenseFlowReportRes().setUserId(23).setUserName("用户23").setLicenseName("license1").setUpFlowBytes(300000023100L).setDownFlowBytes(40000004200L),
                new LicenseFlowReportRes().setUserId(24).setUserName("用户24").setLicenseName("license1").setUpFlowBytes(300998723100L).setDownFlowBytes(2099877664200L),
                new LicenseFlowReportRes().setUserId(25).setUserName("用户25").setLicenseName("license1").setUpFlowBytes(50987668623100L).setDownFlowBytes(30987657864200L)
        );
        List<LicenseFlowReportRes> list = null;
        if (pageQuery.getCurrent() == 1) {
            list = all.subList(0, 10);
        } else if (pageQuery.getCurrent() == 2){
            list = all.subList(10, 20);
        } else {
            list = all.subList(20, all.size());
        }
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

package fun.asgc.neutrino.proxy.server.dal;

import fun.asgc.neutrino.proxy.server.controller.res.report.LicenseFlowMonthReportRes;
import fun.asgc.neutrino.proxy.server.controller.res.report.LicenseFlowReportRes;
import fun.asgc.neutrino.proxy.server.controller.res.report.UserFlowMonthReportRes;
import fun.asgc.neutrino.proxy.server.controller.res.report.UserFlowReportRes;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * @author: aoshiguchen
 * @date: 2023/3/19
 */
@Mapper
public interface ReportMapper {
    /**
     * 基于用户维度的流量报表
     * @param userId
     * @return
     */
    List<UserFlowReportRes> userFlowReportList(@Param("userId") Integer userId, @Param("curMonthBeginDate") Date curMonthBeginDate, @Param("curDayBeginDate") Date curDayBeginDate, @Param("curDate") Date curDate);
    /**
     * 基于License维度的流量报表
     * @param userId
     * @return
     */
    List<LicenseFlowReportRes> licenseFLowReportList(@Param("userId") Integer userId, @Param("curMonthBeginDate") Date curMonthBeginDate, @Param("curDayBeginDate") Date curDayBeginDate, @Param("curDate") Date curDate);

    /**
     * 用户流量月度明细
     * @param userId
     * @return
     */
    List<UserFlowMonthReportRes> userFlowMonthReportList(@Param("userId") Integer userId, @Param("curMonthBeginDate") Date curMonthBeginDate, @Param("curDayBeginDate") Date curDayBeginDate, @Param("curDate") Date curDate);
    /**
     * License流量月度明细
     * @param userId
     * @return
     */
    List<LicenseFlowMonthReportRes> licenseFLowMonthReportList(@Param("userId") Integer userId, @Param("licenseId") Integer licenseId, @Param("curMonthBeginDate") Date curMonthBeginDate, @Param("curDayBeginDate") Date curDayBeginDate, @Param("curDate") Date curDate);
}

package fun.asgc.neutrino.proxy.server.dal;

import fun.asgc.neutrino.proxy.server.controller.res.report.*;
import fun.asgc.neutrino.proxy.server.service.bo.FlowBO;
import fun.asgc.neutrino.proxy.server.service.bo.SingleDayFlowBO;
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

    /**
     * 首页 - 今日流量
     * @param curDayBeginDate
     * @param curDate
     * @return
     */
    FlowBO homeTodayFlow(@Param("curDayBeginDate") Date curDayBeginDate, @Param("curDate") Date curDate);

    /**
     * 首页 - 总流量
     * @param curMonthBeginDate
     * @param curDayBeginDate
     * @param curDate
     * @return
     */
    FlowBO homeTotalFlow(@Param("curMonthBeginDate") Date curMonthBeginDate, @Param("curDayBeginDate") Date curDayBeginDate, @Param("curDate") Date curDate);

    /**
     * 首页最近7日流量
     * @param beginDate
     * @param curDayBeginDate
     * @param curDate
     * @return
     */
    List<SingleDayFlowBO> homeLast7dFlowList(@Param("beginDate") Date beginDate, @Param("curDayBeginDate") Date curDayBeginDate, @Param("curDate") Date curDate);
}

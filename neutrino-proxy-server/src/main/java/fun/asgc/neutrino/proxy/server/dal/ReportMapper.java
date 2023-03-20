package fun.asgc.neutrino.proxy.server.dal;

import fun.asgc.neutrino.proxy.server.controller.req.LicenseFlowReportReq;
import fun.asgc.neutrino.proxy.server.controller.req.UserFlowReportReq;
import fun.asgc.neutrino.proxy.server.controller.res.LicenseFlowReportRes;
import fun.asgc.neutrino.proxy.server.controller.res.UserFlowReportRes;
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
     * 基于用户维度的流量报表
     * @param userId
     * @return
     */
    List<LicenseFlowReportRes> licenseFLowReportList(@Param("userId") Integer userId, @Param("curMonthBeginDate") Date curMonthBeginDate, @Param("curDayBeginDate") Date curDayBeginDate, @Param("curDate") Date curDate);
}

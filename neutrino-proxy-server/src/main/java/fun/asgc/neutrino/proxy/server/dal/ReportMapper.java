package fun.asgc.neutrino.proxy.server.dal;

import fun.asgc.neutrino.proxy.server.controller.req.LicenseFlowReportReq;
import fun.asgc.neutrino.proxy.server.controller.req.UserFlowReportReq;
import fun.asgc.neutrino.proxy.server.controller.res.LicenseFlowReportRes;
import fun.asgc.neutrino.proxy.server.controller.res.UserFlowReportRes;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author: aoshiguchen
 * @date: 2023/3/19
 */
@Mapper
public interface ReportMapper {

    /**
     * 基于用户维度的流量报表
     * @param req
     * @return
     */
    List<UserFlowReportRes> userFlowReportList(@Param("req") UserFlowReportReq req);

    /**
     * 基于license维度的流量报表
     * @param req
     * @return
     */
    List<LicenseFlowReportRes> licenseFLowReportList(@Param("req")LicenseFlowReportReq req);
}

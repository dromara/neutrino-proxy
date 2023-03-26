package fun.asgc.neutrino.proxy.server.controller.req.report;

import lombok.Data;

/**
 * @author: aoshiguchen
 * @date: 2022/12/21
 */
@Data
public class LicenseFlowMonthReportReq {
    private Integer userId;
    private Integer licenseId;
}

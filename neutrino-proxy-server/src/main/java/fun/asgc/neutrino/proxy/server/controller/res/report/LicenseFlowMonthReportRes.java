package fun.asgc.neutrino.proxy.server.controller.res.report;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @author: aoshiguchen
 * @date: 2022/12/21
 */
@Accessors(chain = true)
@Data
public class LicenseFlowMonthReportRes {
    /**
     * 用户ID
     */
    private Integer userId;
    /**
     * 用户名称
     */
    private String userName;
    /**
     * licenseId
     */
    private Integer licenseId;
    /**
     * license名称
     */
    private String licenseName;
    /**
     * 上行流量字节数
     */
    private Long upFlowBytes;
    /**
     * 下行流量字节数
     */
    private Long downFlowBytes;
    /**
     * 总流量字节数
     */
    private Long totalFlowBytes;
    /**
     * 上行流量描述
     */
    private String upFlowDesc;
    /**
     * 下行流量描述
     */
    private String downFlowDesc;
    /**
     * 总流量描述
     */
    private String totalFlowDesc;
    /**
     * 日期
     */
    private Date date;
}

package fun.asgc.neutrino.proxy.server.controller.res.report;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author: aoshiguchen
 * @date: 2022/9/12
 */
@Accessors(chain = true)
@Data
public class ReportDataViewRes {
    /**
     * 在线用户数
     */
    private Integer userOnlineNumber;
    /**
     * 启用用户数
     */
    private Integer enableUserNumber;
    /**
     * 用户总数
     */
    private Integer userNumber;
    /**
     * 在线license数
     */
    private Integer licenseOnlineNumber;
    /**
     * 启用license数
     */
    private Integer enableLicenseNumber;
    /**
     * license总数
     */
    private Integer licenseNumber;
    /**
     * 服务端口在线数
     */
    private Integer serverPortOnlineNumber;
    /**
     * 启用服务端口数
     */
    private Integer enableServerPortNumber;
    /**
     * 总的服务端口数
     */
    private Integer serverPortNumber;
    /**
     * 累计上行流量
     */
    private String totalUpstreamFlow;
    /**
     * 累计下行流量
     */
    private String totalDownwardFlow;
}

package org.dromara.neutrinoproxy.server.service.bo;

import lombok.Data;
import org.dromara.neutrinoproxy.server.controller.res.report.HomeDataView;

import java.util.Date;

/**
 * 单日流量
 * @author: aoshiguchen
 * @date: 2023/3/26
 */
@Data
public class SingleDayFlowBO {
    /**
     * 日期
     */
    private Date date;
    /**
     * 上行流量字节数
     */
    private Long upFlowBytes;
    /**
     * 下行流量字节数
     */
    private Long downFlowBytes;

    public HomeDataView.SingleDayFlow toSingleDayFlow() {
        HomeDataView.SingleDayFlow singleDayFlow = new HomeDataView.SingleDayFlow();
        singleDayFlow.setDate(date);
        singleDayFlow.setUpFlowBytes(upFlowBytes);
        singleDayFlow.setDownFlowBytes(downFlowBytes);
        return singleDayFlow;
    }
}

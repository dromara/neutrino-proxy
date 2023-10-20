package org.dromara.neutrinoproxy.server.service.bo;

import lombok.Data;
import org.dromara.neutrinoproxy.server.controller.res.report.HomeDataView;

/**
 * @author: aoshiguchen
 * @date: 2023/3/26
 */
@Data
public class FlowBO {
    /**
     * 上行流量字节数
     */
    private Long upFlowBytes;
    /**
     * 下行流量字节数
     */
    private Long downFlowBytes;

    public HomeDataView.TodayFlow todayFlow() {
        HomeDataView.TodayFlow todayFlow = new HomeDataView.TodayFlow();
        todayFlow.setUpFlowBytes(upFlowBytes);
        todayFlow.setDownFlowBytes(downFlowBytes);
        return todayFlow;
    }

    public HomeDataView.TotalFlow totalFlow() {
        HomeDataView.TotalFlow totalFlow = new HomeDataView.TotalFlow();
        totalFlow.setUpFlowBytes(upFlowBytes);
        totalFlow.setDownFlowBytes(downFlowBytes);
        return totalFlow;
    }
}

package org.dromara.neutrinoproxy.server.controller.res.report;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

/**
 * @author: aoshiguchen
 * @date: 2023/3/26
 */
@Accessors(chain = true)
@Data
public class HomeDataView {

    /**
     * licenses数据
     */
    private License license;

    /**
     * 端口映射
     */
    private PortMapping portMapping;

    /**
     * 今日流量
     */
    private TodayFlow todayFlow;

    /**
     * 总流量
     */
    private TotalFlow totalFlow;

    /**
     * 最近7日流量
     */
    private Last7dFlow last7dFlow;

    @Accessors(chain = true)
    @Data
    public static class License {
        /**
         * 总数
         */
        private Integer totalCount;
        /**
         * 在线数
         */
        private Integer onlineCount;
        /**
         * 离线数
         */
        private Integer offlineCount;
    }

    @Accessors(chain = true)
    @Data
    public static class PortMapping {
        /**
         * 总数
         */
        private Integer totalCount;
        /**
         * 在线数
         */
        private Integer onlineCount;
        /**
         * 离线数
         */
        private Integer offlineCount;
    }

    @Accessors(chain = true)
    @Data
    public static class TodayFlow {
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
    }

    @Accessors(chain = true)
    @Data
    public static class TotalFlow {
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
    }

    @Accessors(chain = true)
    @Data
    public static class Last7dFlow {
        /**
         * 最近7日流量数据
         */
        private List<SingleDayFlow> dataList;
        /**
         * x轴日期
         */
        private List<String> xDate;
        /**
         * 图例（上行流量、下行流量、总流量）
         */
        private List<String> legendData;
        /**
         * 折线列表
         */
        private List<Series> seriesList;
    }

    @Accessors(chain = true)
    @Data
    public static class SingleDayFlow {
        /**
         * 日期字符串
         */
        private String dateStr;
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
    }

    @Accessors(chain = true)
    @Data
    public static class Series {
        /**
         * 此处目前固定为：line
         */
        private String seriesType;
        /**
         * 名称：上行流量、下行流量、总流量
         */
        private String seriesName;
        /**
         * y值序列
         */
        private List<Long> seriesData;
    }
}

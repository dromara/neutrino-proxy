package org.dromara.neutrinoproxy.server.service.bo;

import lombok.Data;

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
}

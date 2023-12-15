package org.dromara.neutrinoproxy.server.service.bo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author: aoshiguchen
 * @date: 2023/12/15
 */
@Accessors(chain = true)
@Data
public class FlowLimitBO {
    private Long upLimitRate;
    private Long downLimitRate;
}

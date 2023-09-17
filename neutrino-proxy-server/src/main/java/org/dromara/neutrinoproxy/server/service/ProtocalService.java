package org.dromara.neutrinoproxy.server.service;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.dromara.neutrinoproxy.server.constant.NetworkProtocolEnum;
import org.dromara.neutrinoproxy.server.controller.res.system.ProtocalListRes;
import org.noear.solon.annotation.Component;

import java.util.List;

/**
 * @author: aoshiguchen
 * @date: 2023/4/2
 */
@Slf4j
@Component
public class ProtocalService {

    /**
     * 获取协议列表
     * @return
     */
    public List<ProtocalListRes> list() {
        return Lists.newArrayList(
                new ProtocalListRes().setName("TCP").setEnable(Boolean.TRUE).setRemark("支持一切TCP之上的协议"),
                new ProtocalListRes().setName("HTTP(S)").setEnable(Boolean.TRUE).setRemark("支持绑定子域名，未绑定时等价于时使用TCP。 若配置了证书，则同时支持HTTPS。"),
                new ProtocalListRes().setName("UDP").setEnable(Boolean.TRUE).setRemark("暂不支持")
        );
    }

}

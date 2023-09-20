package org.dromara.neutrinoproxy.client.handler;

import com.alibaba.fastjson.JSONObject;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.dromara.neutrinoproxy.core.Constants;
import org.dromara.neutrinoproxy.core.ProxyDataTypeEnum;
import org.dromara.neutrinoproxy.core.ProxyMessage;
import org.dromara.neutrinoproxy.core.ProxyMessageHandler;
import org.dromara.neutrinoproxy.core.dispatcher.Match;
import org.noear.solon.annotation.Component;

/**
 * @author: aoshiguchen
 * @date: 2023/9/20
 */
@Slf4j
@Match(type = Constants.ProxyDataTypeName.UDP_TRANSFER)
@Component
public class UdpProxyMessageTransferHandler implements ProxyMessageHandler {


    @Override
    public void handle(ChannelHandlerContext ctx, ProxyMessage proxyMessage) {
        final ProxyMessage.UdpBaseInfo udpBaseInfo = JSONObject.parseObject(proxyMessage.getInfo(), ProxyMessage.UdpBaseInfo.class);
        log.info("[UDP transfer]info:{} data:{}", proxyMessage.getInfo(), new String(proxyMessage.getData()));
    }

    @Override
    public String name() {
        return ProxyDataTypeEnum.UDP_TRANSFER.getDesc();
    }
}

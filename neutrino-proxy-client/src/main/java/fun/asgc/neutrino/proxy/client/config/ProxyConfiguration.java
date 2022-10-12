/**
 * Copyright (c) 2022 aoshiguchen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package fun.asgc.neutrino.proxy.client.config;

import fun.asgc.neutrino.core.annotation.Bean;
import fun.asgc.neutrino.core.annotation.Component;
import fun.asgc.neutrino.core.annotation.Order;
import fun.asgc.neutrino.core.base.DefaultDispatcher;
import fun.asgc.neutrino.core.base.Dispatcher;
import fun.asgc.neutrino.core.base.Ordered;
import fun.asgc.neutrino.core.util.BeanManager;
import fun.asgc.neutrino.proxy.core.ProxyDataTypeEnum;
import fun.asgc.neutrino.proxy.core.ProxyMessage;
import fun.asgc.neutrino.proxy.core.ProxyMessageHandler;
import io.netty.channel.ChannelHandlerContext;

/**
 * 代理配置
 * @author: aoshiguchen
 * @date: 2022/10/8
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
public class ProxyConfiguration {

    @Bean
    public Dispatcher<ChannelHandlerContext, ProxyMessage> dispatcher() {
        return new DefaultDispatcher<>("消息调度器",
                BeanManager.getBeanListBySuperClass(ProxyMessageHandler.class),
                proxyMessage -> ProxyDataTypeEnum.of((int)proxyMessage.getType()) == null ? null : ProxyDataTypeEnum.of((int)proxyMessage.getType()).getName());
    }
}

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
package fun.asgc.neutrino.core.base.event;

import fun.asgc.neutrino.core.base.Channel;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author: wen.y
 * @date: 2022/9/28
 */
public interface EventContext {
    /**
     * 获取事件源
     * @return 事件源
     */
    <T> T source();
    /**
     * 事件主题
     * 用于订阅一级过滤
     * @return 主题
     */
    String topic();

    /**
     * 事件标签
     * 用于订阅二级过滤
     * @return 标签
     */
    Set<String> tags();
    /**
     * 附加数据
     * @return 附加数据
     */
    Map<String, Object> attachData();

    /**
     * 事件ID
     * @return 事件ID
     */
    String id();

    /**
     * 事件发生的时间
     * @return 事件发生的时间
     */
    Date happenTime();

    /**
     * channel列表
     * 1、如果为空，说明该事件未经过channel
     * 2、该list代表事件在channel中的广播顺序
     * @return channel列表
     */
    List<Channel> channelList();
}

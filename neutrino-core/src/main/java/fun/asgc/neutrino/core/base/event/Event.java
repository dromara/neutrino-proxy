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

import java.util.Map;
import java.util.Set;

/**
 * @author: aoshiguchen
 * @date: 2022/9/28
 */
public interface Event<C extends EventContext, S extends EventSource, D extends Object> {
    /**
     * 获取事件源
     * @return 事件源
     */
    S source();

    /**
     * 获取数据
     * @return 数据
     */
    D data();

    /**
     * 获取事件上下文
     * @return 上下文
     */
    C context();

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
}

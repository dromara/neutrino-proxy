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

import fun.asgc.neutrino.core.base.CustomThreadFactory;
import fun.asgc.neutrino.core.base.Dispatcher;
import fun.asgc.neutrino.core.util.Assert;
import fun.asgc.neutrino.core.util.CollectionUtil;
import fun.asgc.neutrino.core.util.StringUtil;
import fun.asgc.neutrino.core.web.AntPathMatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author: aoshiguchen
 * @date: 2022/9/29
 */
public class ApplicationEventChannel<D> implements EventChannel<D,ApplicationEventContext,ApplicationEvent<D>,ApplicationEventReceiver<D>,Dispatcher<ApplicationEventContext,ApplicationEvent<D>>> {
    private List<ApplicationEventReceiver<D>> receiverList;
    private Dispatcher<ApplicationEventContext,ApplicationEvent<D>> dispatcher;
    private ThreadPoolExecutor threadPoolExecutor;
    private static final AntPathMatcher antPathMatcher = new AntPathMatcher();

    public ApplicationEventChannel() {
        this.receiverList = new ArrayList<>();
        this.threadPoolExecutor = new ThreadPoolExecutor(5, 20, 10L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(), new CustomThreadFactory("ApplicationEventChannel"));
    }

    @Override
    public void registerReceiver(ApplicationEventReceiver<D> receiver) {
        Assert.notNull(receiver, "receiver不能为空!");
        if (this.receiverList.contains(receiver)) {
            return;
        }
        this.receiverList.add(receiver);
    }

    @Override
    public void unRegisterReceiver(ApplicationEventReceiver<D> receiver) {
        Assert.notNull(receiver, "receiver不能为空!");
        if (!this.receiverList.contains(receiver)) {
            return;
        }
        this.receiverList.remove(receiver);
    }

    @Override
    public void setDispatcher(Dispatcher<ApplicationEventContext, ApplicationEvent<D>> dispatcher) {
        Assert.notNull(dispatcher, "dispatcher不能为空!");
        this.dispatcher = dispatcher;
    }

    @Override
    public void publish(ApplicationEvent<D> msg) {
        this.receiverList.forEach(receiver -> {
            threadPoolExecutor.submit(() -> {
                if (match(msg, receiver)) {
                    receiver.receive(msg);
                }
            });
        });
    }

    /**
     * 判断指定消息和指定接受者是否匹配
     * @param msg 消息
     * @param receiver 接受者
     * @return 是否匹配
     */
    private boolean match(ApplicationEvent<D> msg, ApplicationEventReceiver<D> receiver) {
        if (null == msg || null == receiver) {
            return false;
        }
        return topicMatch(msg.context().topic(), receiver.getTopic()) && tagMatch(msg.context().tags(), receiver.getTags());
    }

    /**
     * topic匹配起
     * @param eventTopic 事件主题
     * @param subscriptionTopic 订阅的主题
     * @return 是否匹配
     */
    private boolean topicMatch(String eventTopic, String subscriptionTopic) {
        if (StringUtil.isEmpty(subscriptionTopic)) {
            return true;
        }
        return antPathMatcher.match(subscriptionTopic, eventTopic == null ? "" : eventTopic);
    }

    /**
     * 标签匹配
     * @param eventTags 事件标签
     * @param subscriptionTags 关注的标签
     * @return 是否匹配
     */
    private boolean tagMatch(Set<String> eventTags, Set<String> subscriptionTags) {
        if (CollectionUtil.isEmpty(subscriptionTags)) {
            return true;
        }
        for (String tag : eventTags) {
            if (subscriptionTags.contains(tag)) {
                return true;
            }
        }
        return false;
    }
}

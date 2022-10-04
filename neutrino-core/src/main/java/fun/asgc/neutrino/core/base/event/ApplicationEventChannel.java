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

import com.google.common.collect.Sets;
import fun.asgc.neutrino.core.base.CustomThreadFactory;
import fun.asgc.neutrino.core.base.Dispatcher;
import fun.asgc.neutrino.core.util.Assert;
import fun.asgc.neutrino.core.util.CollectionUtil;

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
                if (!receiver.topicMatch(msg.context().topic())) {
                    return;
                }
                Set<String> tags = msg.context().tags();
                if (CollectionUtil.isEmpty(tags)) {
                    tags = Sets.newHashSet("");
                }
                for (String tag : tags) {
                    if (receiver.tagMatch(tag)) {
                        receiver.receive(msg);
                        break;
                    }
                }
            });
        });
    }
}

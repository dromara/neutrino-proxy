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


import fun.asgc.neutrino.core.util.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: aoshiguchen
 * @date: 2022/9/29
 */
public class ApplicationEventPublisher<D> implements EventPublisher<D,ApplicationEventContext,ApplicationEvent<D>,ApplicationEventChannel<D>> {
    private List<ApplicationEventChannel<D>> channelList;

    public ApplicationEventPublisher() {
        this.channelList = new ArrayList<>();
    }

    @Override
    public void publish(ApplicationEvent<D> msg) {
        this.channelList.forEach(channel -> channel.publish(msg));
    }

    @Override
    public void registerChannel(ApplicationEventChannel<D> channel) {
        Assert.notNull(channel, "channel不能为空!");
        if (!this.channelList.contains(channel)) {
            this.channelList.add(channel);
        }
    }

    @Override
    public void unRegisterChannel(ApplicationEventChannel<D> channel) {
        Assert.notNull(channel, "channel不能为空!");
        this.channelList.remove(channel);
    }
}

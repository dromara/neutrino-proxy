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

import java.util.Set;

/**
 * 简单的应用事件管理器
 * @author: wen.u
 * @date: 2022/10/10
 */
public class SimpleApplicationEventManager<D> {
    private ApplicationEventChannel<D> channel;
    private ApplicationEventPublisher<D> publisher;
    private Object source;

    public SimpleApplicationEventManager() {
        this.channel = new ApplicationEventChannel<>();
        this.publisher = new ApplicationEventPublisher<>();
        this.publisher.bindChannel(channel);
    }

    public SimpleApplicationEventManager(Object source) {
        this.channel = new ApplicationEventChannel<>();
        this.publisher = new ApplicationEventPublisher<>();
        this.publisher.bindChannel(channel);
        this.source = source;
    }

    public void publish(D data) {
        this.publish(null, null, data);
    }

    public void publish(String topic, D data) {
        this.publish(topic, null, data);
    }

    public void publish(String topic, Set<String> tags, D data) {
        ApplicationEvent<D> event = new ApplicationEvent<>();
        event.setData(data);
        event.context().setSource(source);
        event.context().setTopic(topic);
        event.context().setTags(tags);
        this.publisher.publish(event);
    }

    public void registerReceiver(ApplicationEventReceiver<D> receiver) {
        this.channel.registerReceiver(receiver);
    }

    public ApplicationEventChannel<D> getChannel() {
        return channel;
    }

    public ApplicationEventPublisher<D> getPublisher() {
        return publisher;
    }

    public Object getSource() {
        return source;
    }
}

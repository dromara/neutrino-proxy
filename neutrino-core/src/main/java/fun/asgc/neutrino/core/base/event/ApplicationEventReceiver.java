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

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

/**
 * @author: aoshiguchen
 * @date: 2022/9/29
 */
@Slf4j
public class ApplicationEventReceiver<D> implements EventReceiver<ApplicationEventContext,D,ApplicationEvent<D>> {
    private String topic;
    private Set<String> tags;

    public ApplicationEventReceiver() {

    }

    public ApplicationEventReceiver(String topic) {
        this.topic = topic;
    }

    public ApplicationEventReceiver(String topic, Set<String> tags) {
        this.topic = topic;
        this.tags = tags;
    }

    @Override
    public void receive(ApplicationEvent<D> msg) {
        log.debug("ApplicationEventReceiver receive {}", JSONObject.toJSONString(msg));
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    public String getTopic() {
        return topic;
    }

    public Set<String> getTags() {
        return tags;
    }
}

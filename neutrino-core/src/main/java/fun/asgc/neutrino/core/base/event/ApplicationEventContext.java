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
import fun.asgc.neutrino.core.util.StringUtil;

import java.util.*;

/**
 * @author: aoshiguchen
 * @date: 2022/9/28
 */
public class ApplicationEventContext implements EventContext {

    private String id;
    private String topic;
    private Set<String> tags;
    private Object source;
    private Date happenTime;
    private Map<String, Object> attachData = new HashMap<>();
    private List<Channel> channelList;

    public ApplicationEventContext() {
        this.id = StringUtil.genUUID();
        this.happenTime = new Date();
        this.channelList = new ArrayList<>();
    }

    @Override
    public Map<String, Object> attachData() {
        return attachData;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public Date happenTime() {
        return happenTime;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public <S> S source() {
        return (S)source;
    }

    @Override
    public String topic() {
        return topic;
    }

    @Override
    public Set<String> tags() {
        return tags;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    public <S> void setSource(S source) {
        this.source = source;
    }

    @Override
    public List<Channel> channelList() {
        return this.channelList;
    }
}

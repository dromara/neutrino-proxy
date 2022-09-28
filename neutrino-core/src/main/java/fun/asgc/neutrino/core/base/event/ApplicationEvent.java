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
public class ApplicationEvent<D> implements Event<ApplicationEventContext, ApplicationEventSource, D> {

    @Override
    public ApplicationEventSource source() {
        return null;
    }

    @Override
    public D data() {
        return null;
    }

    @Override
    public ApplicationEventContext context() {
        return null;
    }

    @Override
    public String topic() {
        return null;
    }

    @Override
    public Set<String> tags() {
        return null;
    }

    @Override
    public Map<String, Object> attachData() {
        return null;
    }

    @Override
    public String id() {
        return null;
    }

}

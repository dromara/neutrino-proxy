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
package fun.asgc.neutrino.proxy.core;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

import java.util.HashMap;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/8/30
 */
public class ChannelAttribute extends HashMap<String, Object> {
	public static ChannelAttribute create() {
		return new ChannelAttribute();
	}

	public static ChannelAttribute of(String k, Object v) {
		ChannelAttribute channelAttr = create();
		channelAttr.put(k, v);

		return channelAttr;
	}

	public ChannelAttribute set(String k, Object v) {
		super.put(k, v);
		return this;
	}

	public <T> T get(String key) {
		return (T)super.get(key);
	}

	public String getString(String k) {
		return String.valueOf(this.get(k));
	}

	public Long getLong(String k) {
		return Long.valueOf(String.valueOf(this.get(k)));
	}
}

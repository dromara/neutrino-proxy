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

package fun.asgc.neutrino.core.base;

import com.alibaba.fastjson.JSONObject;
import fun.asgc.neutrino.core.annotation.Match;
import fun.asgc.neutrino.core.util.Assert;
import fun.asgc.neutrino.core.util.CollectionUtil;
import fun.asgc.neutrino.core.util.StringUtil;
import fun.asgc.neutrino.core.util.TypeUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/6/16
 */
@Slf4j
public class DefaultDispatcher<Context, Data> implements Dispatcher<Context, Data> {

	/**
	 * 调度器名称
	 */
	private String name;

	/**
	 * 处理器映射
	 */
	private Map<String, Handler<Context,Data>> handlerMap;
	/**
	 * 匹配器
	 */
	private Function<Data,String> matcher;

	public DefaultDispatcher(String name, List<? extends Handler<Context,Data>> handlerList, Function<Data,String> matcher) {
		Assert.notNull(name, "名称不能为空!");
		Assert.notNull(matcher, "匹配器不能为空!");
		if (CollectionUtil.isEmpty(handlerList)) {
			log.error("{} 处理器列表为空.", name);
			return;
		}
		this.name = name == null ? "" : name;
		this.handlerMap = new HashMap<>();
		this.matcher = matcher;

		for (Handler handler : handlerList) {
			Match match = handler.getClass().getAnnotation(Match.class);
			if (null == match) {
				log.warn("{} 类: {} 缺失Match注解", this.name, handler.getClass().getName());
				continue;
			}
			if (StringUtil.isEmpty(match.type())) {
				log.warn("{} 类: {} match注解缺失type参数！", this.name, handler.getClass().getName());
				continue;
			}
			if (handlerMap.containsKey(match.type())) {
				log.warn("{} 类: {} match注解type值{} 存在重复!", this.name, handler.getClass().getName(), match.type());
				continue;
			}
			handlerMap.put(match.type(), handler);
		}
		log.info("{} 处理器初始化完成", this.name);
	}

	@Override
	public void dispatch(Context context, Data data) {
		if (CollectionUtil.isEmpty(handlerMap)) {
			return;
		}
		String type = matcher.apply(data);
		if (null == type) {
			log.warn("{} 获取匹配类型失败 data:{}", this.name, JSONObject.toJSONString(data));
			return;
		}
		Handler<Context,Data> handler = handlerMap.get(type);
		if (null == handler) {
			log.warn("{} 找不到匹配的处理器 type:{}", this.name, type);
			return;
		}
		String handlerName = handler.name();
		if (StringUtil.isEmpty(handlerName)) {
			handlerName = TypeUtil.getSimpleName(handler.getClass());
		}
		log.debug("{} 处理器[{}]执行.", this.name, handlerName);
		handler.handle(context, data);
	}
}

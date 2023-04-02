package org.dromara.neutrinoproxy.core.dispatcher;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import org.dromara.neutrinoproxy.core.util.TypeUtil;
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
			if (StrUtil.isEmpty(match.type())) {
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
//			log.debug("{} 找不到匹配的处理器 type:{}", this.name, type);
			return;
		}
		String handlerName = handler.name();
		if (StrUtil.isEmpty(handlerName)) {
			handlerName = TypeUtil.getSimpleName(handler.getClass());
		}
		log.debug("{} 处理器[{}]执行.", this.name, handlerName);
		handler.handle(context, data);
	}
}

package org.dromara.neutrinoproxy.core.dispatcher;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.dromara.neutrinoproxy.core.util.TypeUtil;
import org.noear.snack.ONode;

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
		Assert.notNull(name, "name cannot empty!");
		Assert.notNull(matcher, "matcher cannot empty!");
		if (CollectionUtil.isEmpty(handlerList)) {
			log.error("{} handler list empty.", name);
			return;
		}
		this.name = name == null ? "" : name;
		this.handlerMap = new HashMap<>();
		this.matcher = matcher;

		for (Handler handler : handlerList) {
			Match match = handler.getClass().getAnnotation(Match.class);
			if (null == match) {
				log.warn("{} class: {} notfound Match annotation", this.name, handler.getClass().getName());
				continue;
			}
			if (StrUtil.isEmpty(match.type())) {
				log.warn("{} class: {} match annotation notfound type param！", this.name, handler.getClass().getName());
				continue;
			}
			if (handlerMap.containsKey(match.type())) {
				log.warn("{} class: {} match annotation type value {} repeat!", this.name, handler.getClass().getName(), match.type());
				continue;
			}
			handlerMap.put(match.type(), handler);
		}
		log.info("{} dispatcher init success", this.name);
	}

	@Override
	public void dispatch(Context context, Data data) {
		if (CollectionUtil.isEmpty(handlerMap)) {
			return;
		}
		String type = matcher.apply(data);
		if (null == type) {
			log.warn("{} get match type failed data:{}", this.name, ONode.serialize(data));
			return;
		}
		Handler<Context,Data> handler = handlerMap.get(type);
		if (null == handler) {
			return;
		}
		String handlerName = handler.name();
		if (StrUtil.isEmpty(handlerName)) {
			handlerName = TypeUtil.getSimpleName(handler.getClass());
		}
		log.debug("{} handler[{}]execute.", this.name, handlerName);
		handler.handle(context, data);
	}
}

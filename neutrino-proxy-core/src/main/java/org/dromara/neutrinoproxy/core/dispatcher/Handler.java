package org.dromara.neutrinoproxy.core.dispatcher;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/6/16
 */
public interface Handler<Context, Data> {

	/**
	 * 处理
	 * @param context
	 * @param data
	 */
	void handle(Context context, Data data);

	/**
	 * 名称
	 * @return
	 */
	default String name() {
		return "";
	};
}

package org.dromara.neutrinoproxy.core.dispatcher;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/6/16
 */
public interface Dispatcher<Context, Data> {

	/**
	 * 调度
	 * @param context
	 * @param data
	 */
	void dispatch(Context context, Data data);
}

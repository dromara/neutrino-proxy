package org.dromara.neutrinoproxy.core.base;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/6/16
 */
@FunctionalInterface
public interface CodeBlock {

	/**
	 * 执行
	 */
	void execute() throws Exception;
}

package org.dromara.neutrinoproxy.server.base.page;

import lombok.Data;

import java.io.Serializable;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/8/6
 */
@Data
public class PageQuery implements Serializable {
	/**
	 * 当前页
	 */
	private int current = 1;
	/**
	 * 分页大小
	 */
	private int size = 10;
}

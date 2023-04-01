

package org.dromara.neutrinoproxy.core.base;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/6/16
 */
public class InternalException extends RuntimeException {

	public InternalException(String message) {
		super(message);
	}

	public InternalException(String message, Throwable cause) {
		super(message, cause);
	}
}

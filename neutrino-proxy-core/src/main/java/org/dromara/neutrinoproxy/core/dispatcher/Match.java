package org.dromara.neutrinoproxy.core.dispatcher;

import java.lang.annotation.*;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/6/16
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Match {
	String type();
}

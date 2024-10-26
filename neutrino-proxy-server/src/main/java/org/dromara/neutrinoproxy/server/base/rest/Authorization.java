package org.dromara.neutrinoproxy.server.base.rest;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 鉴权注解
 * @author: aoshiguchen
 * @date: 2022/7/31
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Authorization {
	boolean login() default true;

	boolean onlyAdmin() default false;
}

package fun.asgc.solon.extend.job.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/9/4
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface JobHandler {
	String name();
	String desc() default "";
	String cron();
	String param() default "";
}

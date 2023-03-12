package fun.asgc.solon.extend.job.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author: aoshiguchen
 * @date: 2023/3/12
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface EnableJob {
    boolean value() default true;
}

package fun.asgc.neutrino.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author: aoshiguchen
 * @date: 2022/10/10
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Subscribe {
    boolean enable() default true;
    String topic() default "";
    String[] tags() default {};
}

package annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * LogInfo简介
 * 自定义的打印配置，暂无实现，已使用logback
 *
 * @author zengxin
 * @date 2021-03-04 16:00
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LogInfo {

    String msg() default "";
}

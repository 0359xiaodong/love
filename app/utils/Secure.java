package utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * 默认的权限控制
 * @author MrROY
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Secure {
	//当前操作需要管理员权限
	boolean admin() default false;
	//当前操作需要登录
	boolean login() default true;
}

package com.api.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Documented
@Target(ElementType.METHOD)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface WebServlet {

	// Servlet的访问URL
	String[] urlPatterns();
	
	// Servlet的请求方式
	String[] method() default {"get"};
	

	// Servlet的描述
	String description() default "";

	// Servlet的显示名称
	String displayName() default "";

	// Servlet的名称
	String name() default "";

	// Servlet的init参数
	WebInitParam[] initParams() default {};
}
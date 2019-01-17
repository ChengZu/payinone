package com.api.annotation;

import java.lang.reflect.Method;

public class WebServletInfo {

	public Class<?> clazz;
	public Method method;

	public WebServletInfo(Class<?> clazz, Method method) {
		this.clazz = clazz;
		this.method = method;
	}
}

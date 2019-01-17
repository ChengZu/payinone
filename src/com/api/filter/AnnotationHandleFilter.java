package com.api.filter;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.api.annotation.RequestInfo;
import com.api.annotation.WebServlet;
import com.api.annotation.WebServletInfo;
import com.api.utils.ScanClassUtil;

/**
 * @ClassName: AnnotationHandleFilter
 * @Description: 使用Filter作为注解的处理器
 * @author: 孤傲苍狼
 * @date: 2014-11-12 下午10:15:19
 * 
 */
public class AnnotationHandleFilter implements Filter {

	private ServletContext servletContext = null;

	/*
	 * 过滤器初始化时扫描指定的包下面使用了WebServlet注解的那些类
	 * 
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	public void init(FilterConfig filterConfig) throws ServletException {
		System.out.println("---AnnotationHandleFilter过滤器初始化开始---");
		servletContext = filterConfig.getServletContext();
		Map<String, WebServletInfo> classMap = new HashMap<String, WebServletInfo>();
		// 获取web.xml中配置的要扫描的包
		String basePackage = filterConfig.getInitParameter("servletPackage");;
		// 如果配置了多个包，例如：<param-value>me.gacl.web.controller,me.gacl.web.UI</param-value>
		if (basePackage.indexOf(",") > 0) {
			// 按逗号进行分隔
			String[] packageNameArr = basePackage.split(",");
			for (String packageName : packageNameArr) {
				addServletClassToServletContext(packageName, classMap);
			}
		} else {
			addServletClassToServletContext(basePackage, classMap);
		}
		System.out.println("----AnnotationHandleFilter过滤器初始化结束---");
	}

	/**
	 * @Method: addServletClassToServletContext
	 * @Description:添加ServletClass到ServletContext中
	 * @Anthor:孤傲苍狼
	 * 
	 * @param packageName
	 * @param classMap
	 */
	private void addServletClassToServletContext(String packageName,
			Map<String, WebServletInfo> classMap) {
		Set<Class<?>> setClasses = ScanClassUtil.getClasses(packageName);
		for (Class<?> clazz : setClasses) {

			for (Method method : clazz.getMethods()) {
				// checks if MethodInfo annotation is present for the method
				if (method.isAnnotationPresent(WebServlet.class)) {

					WebServlet methodAnno = method
							.getAnnotation(WebServlet.class);

					// String annotationAttrValue = methodAnno.value();
					WebServletInfo info = new WebServletInfo(clazz, method);

					// 获取Annotation的实例的urlPatterns属性的值
					String[] urlPatterns = methodAnno.urlPatterns();
					String[] methods = methodAnno.method();
					for (String urlPattern : urlPatterns) {
						if (urlPattern.length() > 0) {
							for (String m : methods) {

								RequestInfo reqInfo = new RequestInfo(
										urlPattern, m);
								classMap.put(reqInfo.toString(), info);
							}

						}
					}

					servletContext.setAttribute("servletClassMap", classMap);

				}
			}
		}
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {

		// 将ServletRequest强制转换成HttpServletRequest
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		@SuppressWarnings("unchecked")
		Map<String, WebServletInfo> classMap = (Map<String, WebServletInfo>) servletContext
				.getAttribute("servletClassMap");
		// 获取contextPath
		String contextPath = req.getContextPath();
		// 获取用户请求的URI资源

		String uri = req.getRequestURI();

		// 如果没有指明要调用Servlet类中的哪个方法

		// 获取用户使用的请求方式
		String reqMethod = req.getMethod().toLowerCase();

		// 获取要请求的servlet路径
		String requestServletName = uri.substring(contextPath.length(),
				uri.length());

		RequestInfo reqInfo = new RequestInfo(requestServletName, reqMethod);
		// 获取要使用的类
		WebServletInfo servletInfo = classMap.get(reqInfo.toString());

		if (servletInfo == null) {
			// System.out.println("null webservlet"+reqInfo.toString());
			chain.doFilter(request, response);
			return;
		}

		try {
			// 创建类的实例
			Object obj = servletInfo.clazz.newInstance();
			servletInfo.method.invoke(obj, req, res);
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}

	}

	public void destroy() {

	}
}
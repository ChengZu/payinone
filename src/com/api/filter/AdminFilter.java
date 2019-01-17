package com.api.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class AdminFilter implements Filter {
	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;

		String path = req.getRequestURI();
		if (path.indexOf("/login.html") > -1 || path.indexOf("/login.do") > -1
				|| path.indexOf(".css") > -1 || path.indexOf(".js") > -1
				|| path.indexOf(".jpg") > -1 || path.indexOf(".gif") > -1
				|| path.indexOf(".woff") > -1) {
			chain.doFilter(request, response);
			return;
		}
		
		HttpSession session = req.getSession();
		if (session.getAttribute("user") != null) {// 登录后才能访问
			chain.doFilter(request, response);
		} else {
			String contextPath = req.getContextPath();
			res.sendRedirect(contextPath + "/admin/login.html");
		}
		
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub

	}
}

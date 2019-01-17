package com.api.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.api.annotation.WebServlet;
import com.api.dao.table.User;
import com.api.dao.tablehelp.UserDBHelp;
import com.api.utils.Tools;

public class UserServlet {

	@WebServlet(name = "AddUserServlet", urlPatterns = { "/AddUser.admin" }, method = { "post" })
	public void AddUserServlet(HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		PrintWriter out = response.getWriter();

		User user = (User) Tools.requestParamsToObject(request, User.class);
		UserDBHelp.insertUser(user);
		out.print(user);

		out.flush();
		out.close();

	}

	@WebServlet(name = "DeleteUserServlet", urlPatterns = { "/DeleteUser.admin" }, method = { "post" })
	public void DeleteUserServlet(HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		PrintWriter out = response.getWriter();

		User user = (User) Tools.requestParamsToObject(request, User.class);
		UserDBHelp.deleteUser(user);
		out.print(user);

		out.flush();
		out.close();
	}

	@WebServlet(name = "GetUserServlet", urlPatterns = { "/GetUser.admin" }, method = { "post" })
	public void GetUserServlet(HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		PrintWriter out = response.getWriter();

		User user = (User) Tools.requestParamsToObject(request, User.class);
		out.print(UserDBHelp.getUser(user));

		out.flush();
		out.close();
	}

	@WebServlet(name = "GetUserListServlet", urlPatterns = { "/GetUserList.admin" }, method = { "post" })
	public void GetUserListServlet(HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		PrintWriter out = response.getWriter();

		int start = 0, num = 10;
		try {
			if (request.getParameter("start") != null)
				start = Integer.parseInt(request.getParameter("start"));
			if (request.getParameter("num") != null)
				num = Integer.parseInt(request.getParameter("num"));
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		out.println(UserDBHelp.getUserList(start, num));
		out.flush();
		out.close();
	}

	@WebServlet(name = "UpdateUserServlet", urlPatterns = { "/UpdateUser.admin" }, method = { "post" })
	public void UpdateUserServlet(HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		PrintWriter out = response.getWriter();

		User newUser = (User) Tools.requestParamsToObject(request, User.class);
		User oldUser = new User();
		oldUser.setId(newUser.getId());
		UserDBHelp.updateUser(oldUser, newUser);
		out.print(newUser);

		out.flush();
		out.close();
	}

	@WebServlet(name = "LoginServlet", urlPatterns = { "/Login.do" }, method = { "post" })
	public void LoginServlet(HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		/*
		 * PrintWriter out = response.getWriter(); String result = "[]"; User
		 * user = (User) Tools.requestParamsToObject(request, User.class); if
		 * ((user.getId() != null || user.getName() != null) &&
		 * user.getPassword() != null) {
		 * 
		 * result = UserDBHelp.getUser(user);
		 * 
		 * if (result.length() > 2) { HttpSession session =
		 * request.getSession(); session.setAttribute("user", user);
		 * 
		 * } } out.print(result);
		 * 
		 * out.flush(); out.close();
		 */

		PrintWriter out = response.getWriter();
		HttpSession session = request.getSession();

		String username = request.getParameter("username");
		String imagecode1 = request.getParameter("imagecode");
		boolean isEnd = false;
		boolean needImageCode = false;
		if (session.getAttribute("loginnum") != null) {
			int num = (int) session.getAttribute("loginnum");
			if (num > 1)
				needImageCode = true;
			session.setAttribute("loginnum", num + 1);
		} else {
			session.setAttribute("loginnum", 0);
		}

		if (needImageCode) {
			imagecode1 = imagecode1.toUpperCase();

			String imagecode2 = (String) session.getAttribute("imagecode");

			if (!imagecode1.equals(imagecode2)) {
				out.println("{\"result\":\"2\",\"imagecode\":\"1\"}");
				isEnd = true;
			}
		}
		String password ="";
		if(request.getParameter("password")!=null)
			password = request.getParameter("password").toUpperCase();
		if (!isEnd) {
			User user1 = new User();
			User user2 = new User();
			user1.setId(username);
			user1.setPassword(password);

			user2.setName(username);
			user2.setPassword(password);
			List<Object> list1 = UserDBHelp.getUser(user1);
			List<Object> list2 = UserDBHelp.getUser(user2);
			if (list1.size() > 0) {
				User user=(User) list1.get(0);
				session.setAttribute("user", list1.get(0));
				out.println("{\"result\":\"1\",\"imagecode\":\"0\",\"username\":\""+user.getName()+"\",\"userid\":\""+user.getId()+"\",\"password\":\""+user.getPassword()+"\"}");
				session.setAttribute("loginnum", 0);
			} else if (list2.size() > 0) {
				User user=(User) list2.get(0);
				session.setAttribute("user", list2.get(0));
				out.println("{\"result\":\"1\",\"imagecode\":\"0\",\"username\":\""+user.getName()+"\",\"userid\":\""+user.getId()+"\",\"password\":\""+user.getPassword()+"\"}");
				session.setAttribute("loginnum", 0);
			} else {

				if (needImageCode) {
					out.println("{\"result\":\"0\",\"imagecode\":\"1\"}");
				} else {
					out.println("{\"result\":\"0\",\"imagecode\":\"0\"}");
				}
			}
		}
		out.flush();
		out.close();

	}

	@WebServlet(name = "LogoutServlet", urlPatterns = { "/Logout.admin" }, method = { "post" })
	public void LogoutServlet(HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		PrintWriter out = response.getWriter();

		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		session.invalidate();

		out.print(user);
		out.flush();
		out.close();
	}
}
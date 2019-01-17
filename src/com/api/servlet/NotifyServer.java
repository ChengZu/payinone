package com.api.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.api.annotation.WebServlet;
import com.api.client.NotifySocket;

public class NotifyServer {
	@WebServlet(name = "NotifyServer", urlPatterns = {"/NotifyServer"}, method = {"get"})
	public void AddUserServlet(HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		PrintWriter out = response.getWriter();

		for (int i = 0; i < NotifySocket.CLIENTS.size(); i++) {
			out.println("client: "+NotifySocket.CLIENTS.get(i).getMid());
		}
		

		out.flush();
		out.close();

	}
	
	@WebServlet(name = "NotifyServer2", urlPatterns = {"/NotifySend"}, method = {"get"})
	public void AddUserServlet2(HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		PrintWriter out = response.getWriter();
		String mid = request.getParameter("mid");
		String msg = request.getParameter("msg");
		for (int i = 0; i < NotifySocket.CLIENTS.size(); i++) {
			if(NotifySocket.CLIENTS.get(i).getMid()!=null && NotifySocket.CLIENTS.get(i).getMid().equals(mid)){
				NotifySocket.CLIENTS.get(i).sendMessage(msg);
				out.println("send client "+NotifySocket.CLIENTS.get(i).getMid()+" msg: "+msg);
			}
			
		}
		

		out.flush();
		out.close();

	}
	
}

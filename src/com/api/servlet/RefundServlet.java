package com.api.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.api.annotation.WebServlet;
import com.api.dao.table.Refund;
import com.api.dao.tablehelp.RefundDBHelp;
import com.api.utils.Tools;

public class RefundServlet {

	@WebServlet(name = "AddRefundServlet", urlPatterns = {"/AddRefund.admin"})
	public void AddRefundServlet(HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		PrintWriter out = response.getWriter();

		Refund refund = (Refund) Tools.requestParamsToObject(request, Refund.class);
		RefundDBHelp.insertRefund(refund);
		out.print(refund);

		out.flush();
		out.close();

	}

	@WebServlet(name = "DeleteRefundServlet", urlPatterns = {"/DeleteRefund.admin"})
	public void DeleteRefundServlet(HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		PrintWriter out = response.getWriter();

		Refund refund = (Refund) Tools.requestParamsToObject(request, Refund.class);
		RefundDBHelp.deleteRefund(refund);
		out.print(refund);

		out.flush();
		out.close();
	}

	@WebServlet(name = "GetRefundServlet", urlPatterns = {"/GetRefund.admin"})
	public void GetRefundServlet(HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		PrintWriter out = response.getWriter();

		Refund refund = (Refund) Tools.requestParamsToObject(request, Refund.class);
		out.print(RefundDBHelp.getRefund(refund));

		out.flush();
		out.close();
	}
	
	@WebServlet(name = "GetRefundListServlet", urlPatterns = {"/GetRefundList.admin"})
	public void GetRefundListServlet(HttpServletRequest request,
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
		out.println(RefundDBHelp.getRefundList(start, num));
		out.flush();
		out.close();
	}

	@WebServlet(name = "UpdateRefundServlet", urlPatterns = {"/UpdateRefund.admin"})
	public void UpdateRefundServlet(HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		PrintWriter out = response.getWriter();

		Refund newRefund = (Refund) Tools.requestParamsToObject(request, Refund.class);
		Refund oldRefund = new Refund();
		oldRefund.setId(newRefund.getId());
		RefundDBHelp.updateRefund(oldRefund, newRefund);
		out.print(newRefund);

		out.flush();
		out.close();
	}
	
	@WebServlet(name = "GetRefundCountServlet", urlPatterns = {"/GetRefundCount.admin"})
	public void GetRefundCountServlet(HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		PrintWriter out = response.getWriter();

		out.print(RefundDBHelp.getRefundCount());

		out.flush();
		out.close();
	}

}
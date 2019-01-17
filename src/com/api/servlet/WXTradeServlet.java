package com.api.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.api.annotation.WebServlet;
import com.api.dao.table.WXTrade;
import com.api.dao.tablehelp.WXTradeDBHelp;
import com.api.utils.Tools;

public class WXTradeServlet {

	@WebServlet(name = "AddWXTradeServlet", urlPatterns = {"/AddWXTrade.admin"})
	public void AddWXTradeServlet(HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		PrintWriter out = response.getWriter();

		WXTrade wxtrade = (WXTrade) Tools.requestParamsToObject(request, WXTrade.class);
		WXTradeDBHelp.insertWXTrade(wxtrade);
		out.print(wxtrade);

		out.flush();
		out.close();

	}

	@WebServlet(name = "DeleteWXTradeServlet", urlPatterns = {"/DeleteWXTrade.admin"})
	public void DeleteWXTradeServlet(HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		PrintWriter out = response.getWriter();

		WXTrade wxtrade = (WXTrade) Tools.requestParamsToObject(request, WXTrade.class);
		WXTradeDBHelp.deleteWXTrade(wxtrade);
		out.print(wxtrade);

		out.flush();
		out.close();
	}

	@WebServlet(name = "GetWXTradeServlet", urlPatterns = {"/GetWXTrade.admin"})
	public void GetWXTradeServlet(HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		PrintWriter out = response.getWriter();

		WXTrade wxtrade = (WXTrade) Tools.requestParamsToObject(request, WXTrade.class);
		WXTrade rewxtrade = WXTradeDBHelp.getWXTrade(wxtrade);
		if (rewxtrade != null) {
			out.print(rewxtrade);
		} else {
			out.print("[]");
		}
		
		out.flush();
		out.close();
	}
	
	@WebServlet(name = "GetWXTradeListServlet", urlPatterns = {"/GetWXTradeList.admin"})
	public void GetWXTradeListServlet(HttpServletRequest request,
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
		out.print(WXTradeDBHelp.getWXTradeList(start, num));
		out.flush();
		out.close();
	}

	@WebServlet(name = "UpdateWXTradeServlet", urlPatterns = {"/UpdateWXTrade.admin"})
	public void UpdateWXTradeServlet(HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		PrintWriter out = response.getWriter();

		WXTrade newWXTrade = (WXTrade) Tools.requestParamsToObject(request, WXTrade.class);
		WXTrade oldWXTrade = new WXTrade();
		oldWXTrade.setOut_trade_no(newWXTrade.getOut_trade_no());
		WXTradeDBHelp.updateWXTrade(oldWXTrade, newWXTrade);
		out.print(newWXTrade);

		out.flush();
		out.close();
	}

	@WebServlet(name = "GetZFBTradeCountServlet", urlPatterns = {"/GetWXTradeCount.admin"})
	public void GetZFBTradeCountServlet(HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		PrintWriter out = response.getWriter();

		out.print(WXTradeDBHelp.getWXTradeCount());

		out.flush();
		out.close();
	}
	
}
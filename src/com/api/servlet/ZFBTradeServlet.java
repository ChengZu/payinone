package com.api.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.api.annotation.WebServlet;
import com.api.dao.table.ZFBTrade;
import com.api.dao.tablehelp.ZFBTradeDBHelp;
import com.api.utils.Tools;

public class ZFBTradeServlet {

	@WebServlet(name = "AddZFBTradeServlet", urlPatterns = {"/AddZFBTrade.admin"})
	public void AddZFBTradeServlet(HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		PrintWriter out = response.getWriter();

		ZFBTrade zfbtrade = (ZFBTrade) Tools.requestParamsToObject(request, ZFBTrade.class);
		ZFBTradeDBHelp.insertZFBTrade(zfbtrade);
		out.print(zfbtrade);

		out.flush();
		out.close();

	}

	@WebServlet(name = "DeleteZFBTradeServlet", urlPatterns = {"/DeleteZFBTrade.admin"})
	public void DeleteZFBTradeServlet(HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		PrintWriter out = response.getWriter();

		ZFBTrade zfbtrade = (ZFBTrade) Tools.requestParamsToObject(request, ZFBTrade.class);
		ZFBTradeDBHelp.deleteZFBTrade(zfbtrade);
		out.print(zfbtrade);

		out.flush();
		out.close();
	}

	@WebServlet(name = "GetZFBTradeServlet", urlPatterns = {"/GetZFBTrade.admin"})
	public void GetZFBTradeServlet(HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		PrintWriter out = response.getWriter();

		ZFBTrade zfbtrade = (ZFBTrade) Tools.requestParamsToObject(request, ZFBTrade.class);
		ZFBTrade rezfbtrade = ZFBTradeDBHelp.getZFBTrade(zfbtrade);
		if (rezfbtrade != null) {
			out.print(rezfbtrade);
		} else {
			out.print("[]");
		}

		out.flush();
		out.close();
	}
	
	@WebServlet(name = "GetZFBTradeListServlet", urlPatterns = {"/GetZFBTradeList.admin"})
	public void GetZFBTradeListServlet(HttpServletRequest request,
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
		out.println(ZFBTradeDBHelp.getZFBTradeList(start, num));
		out.flush();
		out.close();
	}

	@WebServlet(name = "UpdateZFBTradeServlet", urlPatterns = {"/UpdateZFBTrade.admin"})
	public void UpdateZFBTradeServlet(HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		PrintWriter out = response.getWriter();

		ZFBTrade newZFBTrade = (ZFBTrade) Tools.requestParamsToObject(request, ZFBTrade.class);
		ZFBTrade oldZFBTrade = new ZFBTrade();
		oldZFBTrade.setOutTradeNo(newZFBTrade.getOutTradeNo());
		ZFBTradeDBHelp.updateZFBTrade(oldZFBTrade, newZFBTrade);
		out.print(newZFBTrade);

		out.flush();
		out.close();
	}
	
	@WebServlet(name = "GetZFBTradeCountServlet", urlPatterns = {"/GetZFBTradeCount.admin"})
	public void GetZFBTradeCountServlet(HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		PrintWriter out = response.getWriter();


		
		out.print(ZFBTradeDBHelp.getZFBTradeCount());

		out.flush();
		out.close();
	}

}
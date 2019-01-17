package com.api.servlet;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alipay.demo.trade.utils.ZxingUtils;
import com.api.annotation.WebServlet;
import com.api.client.NotifySocket;
import com.api.dao.table.Refund;
import com.api.dao.table.User;
import com.api.dao.table.WXTrade;
import com.api.dao.tablehelp.WXTradeDBHelp;
import com.api.payutil.MyWXPayUtil;
import com.api.utils.Tools;
import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayConfigImpl;
import com.github.wxpay.sdk.WXPayUtil;

public class WXServlet {
	static final Logger logger = LoggerFactory.getLogger(WXServlet.class);

	@WebServlet(name = "WXTradePayServlet", urlPatterns = { "/WXTradePay.admin" }, method = { "post" })
	public void WXTradePayServlet(HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		WXTrade wxtrade = (WXTrade) Tools.requestParamsToObject(request,
				WXTrade.class);
		if (wxtrade.getBody() == null)
			return;

	}

	@WebServlet(name = "WXTradePreCreateServlet", urlPatterns = { "/WXTradePreCreate.admin" }, method = { "post" })
	public void WXTradePreCreateServlet(HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		WXTrade wxtrade = (WXTrade) Tools.requestParamsToObject(request,
				WXTrade.class);
		if (wxtrade.getBody() == null)
			return;

		PrintWriter out = response.getWriter();
		wxtrade.setOut_trade_no(WXTradeDBHelp.creatWXOutTradeNo());

		if (wxtrade.getDevice_info() == null)
			wxtrade.setDevice_info("1");

		if (wxtrade.getFee_type() == null)
			wxtrade.setFee_type("CNY");

		if (wxtrade.getTrade_type() == null)
			wxtrade.setTrade_type("NATIVE");

		if (wxtrade.getProduct_id() == null)
			wxtrade.setProduct_id("12");
		String time = new java.text.SimpleDateFormat("yyyyMMddHHmmss")
				.format(new java.util.Date());
		wxtrade.setCreate_time(time);

		// 终端IP
		String spbill_creat_ip = "118.190.44.144";

		// 通知地址，必填
		String notify_url = WXPayConfigImpl.INSTANCE.getNotifyUrl();

		// String time_expire = new
		// java.text.SimpleDateFormat("yyyyMMddHHmmss").format(new
		// java.util.Date());

		WXPay wxpay;

		try {
			wxpay = new WXPay(WXPayConfigImpl.INSTANCE);
		} catch (Exception e1) {
			e1.printStackTrace();
			logger.error("WXPay 初始化失败");
			return;
		}

		HashMap<String, String> data = new HashMap<String, String>();
		data.put("body", wxtrade.getBody());
		data.put("out_trade_no", wxtrade.getOut_trade_no());
		data.put("device_info", wxtrade.getDevice_info());
		data.put("fee_type", wxtrade.getFee_type());
		data.put("total_fee", wxtrade.getTotal_fee());
		data.put("spbill_create_ip", spbill_creat_ip);
		data.put("notify_url", notify_url);
		data.put("trade_type", wxtrade.getTrade_type());
		data.put("product_id", wxtrade.getProduct_id());
		// data.put("time_expire", time_expire);
		try {
			Map<String, String> r = wxpay.unifiedOrder(data);
			r.put("out_trade_no", wxtrade.getOut_trade_no());

			switch (r.get("result_code")) {
			case "SUCCESS":
				logger.info("微信预下单成功: )");
				WXTradeDBHelp.insertWXTrade(wxtrade);
				String basePath = request.getSession().getServletContext()
						.getRealPath("/");
				String fileName = String.format("images%sqr-%s.png",
						File.separator, wxtrade.getOut_trade_no());
				String filePath = new StringBuilder(basePath).append(fileName)
						.toString();

				out.println("<img src=\"" + fileName + "\" />");
				ZxingUtils.getQRCodeImge(r.get("code_url"), 256, filePath);

				break;

			case "FAILED":
				logger.error("微信预下单失败!!!");
				break;

			case "UNKNOWN":
				logger.error("系统异常，预下单状态未知!!!");
				break;

			default:
				logger.error("不支持的交易状态，交易返回异常!!!");
				break;
			}

			out.println(Tools.mapToJson(r));
		} catch (Exception e) {
			e.printStackTrace();

		}

		out.flush();
		out.close();

	}

	@WebServlet(name = "WXTradeQueryServlet", urlPatterns = { "/WXTradeQuery.admin" }, method = { "post" })
	public void WXTradeQueryServlet(HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		PrintWriter out = response.getWriter();

		if (request.getParameter("out_trade_no") != null) {
			WXPay wxpay = null;
			try {
				wxpay = new WXPay(WXPayConfigImpl.INSTANCE);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			HashMap<String, String> data = new HashMap<String, String>();
			data.put("out_trade_no", request.getParameter("out_trade_no"));
			// data.put("transaction_id", "4008852001201608221962061594");
			try {
				Map<String, String> r = wxpay.orderQuery(data);
				out.println(r);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		out.flush();
		out.close();
	}

	@WebServlet(name = "WXTradeRefundServlet", urlPatterns = { "/WXTradeRefund.admin" }, method = { "post" })
	public void WXTradeRefundServlet(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		PrintWriter out = response.getWriter();

		HttpServletRequest req = (HttpServletRequest) request;

		HttpSession session = req.getSession();
		User user = (User) session.getAttribute("user");
		if (request.getParameter("out_trade_no") != null) {
			Refund refund = new Refund();
			refund.setOutTradeNo(request.getParameter("out_trade_no"));
			refund.setOperator(user.getId());
			out.println(MyWXPayUtil.doRefund(refund));
		}

		out.flush();
		out.close();
	}

	@WebServlet(name = "WXTradeNotifyServlet", urlPatterns = { "/wx_notify.do" }, method = { "post" })
	public void WXTradeNotifyServlet(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		PrintWriter out = response.getWriter();

		InputStream inStream = request.getInputStream();
		ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
		byte[] buffer = new byte[10240];
		int len = 0;
		while ((len = inStream.read(buffer)) != -1) {
			outSteam.write(buffer, 0, len);
		}

		outSteam.close();
		inStream.close();
		String xmlStr = new String(outSteam.toByteArray(), "utf-8");// 获取微信调用我们notify_url的返回信息

		boolean isSignatureValid = false;
		try {
			if (xmlStr.length() > 0)
				isSignatureValid = WXPayUtil.isSignatureValid(xmlStr,
						WXPayConfigImpl.INSTANCE.getKey());
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (isSignatureValid) {
			Map<String, String> hm = new HashMap<String, String>();
			try {
				hm = WXPayUtil.xmlToMap(xmlStr);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (hm.get("return_code").equals("SUCCESS")
					&& hm.get("result_code").equals("SUCCESS")) {
				// TODO 客户付款成功
				WXTrade newWXTrade = new WXTrade();
				newWXTrade.setPay_state(hm.get("result_code"));

				WXTrade oldWXTrade = new WXTrade();
				oldWXTrade.setOut_trade_no(hm.get("out_trade_no"));

				WXTradeDBHelp.updateWXTrade(oldWXTrade, newWXTrade);
				// 向客户端发送订单支付成功消息
				NotifySocket.wxTradeSuccess(hm.get("out_trade_no"));

				out.println("<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>");

			} else {
				out.println("<xml><return_code><![CDATA[FAIL]]></return_code><return_msg><![CDATA[PAY_FAIL]]></return_msg></xml>");
			}

		} else {
			out.println("<xml><return_code><![CDATA[FAIL]]></return_code><return_msg><![CDATA[VERIFY_FAIL]]></return_msg></xml>");
		}

		out.flush();
		out.close();

	}

}

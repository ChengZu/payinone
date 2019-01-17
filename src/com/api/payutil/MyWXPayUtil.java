package com.api.payutil;

import java.util.HashMap;
import java.util.Map;

import com.api.dao.table.Refund;
import com.api.dao.table.WXTrade;
import com.api.dao.tablehelp.RefundDBHelp;
import com.api.dao.tablehelp.WXTradeDBHelp;
import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayConfigImpl;
import com.google.gson.Gson;

public class MyWXPayUtil {
	public static boolean doRefund(String out_trade_no) {
		WXPay wxpay;
		WXTrade wxtrade1 = new WXTrade();
		wxtrade1.setOut_trade_no(out_trade_no);
		WXTrade wxtrade = WXTradeDBHelp.getWXTrade(wxtrade1);
		if (wxtrade == null)
			return false;
		String total_fee = wxtrade.getTotal_fee();

		try {
			wxpay = new WXPay(WXPayConfigImpl.INSTANCE);
			HashMap<String, String> data = new HashMap<String, String>();
			data.put("out_trade_no", out_trade_no);
			data.put("out_refund_no", out_trade_no);
			data.put("total_fee", total_fee);
			data.put("refund_fee", total_fee);
			data.put("refund_fee_type", "CNY");
			data.put("op_user_id", WXPayConfigImpl.INSTANCE.getMchID());
			Map<String, String> r = wxpay.refund(data);
			// TODO 记录
			if (r.get("result_code").equals("SUCCESS"))
				return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;

	}
	
	public static String doRefund(Refund refund) {
		WXPay wxpay;
		WXTrade wxtrade1 = new WXTrade();
		wxtrade1.setOut_trade_no(refund.getOutTradeNo());
		WXTrade wxtrade = WXTradeDBHelp.getWXTrade(wxtrade1);
		if (wxtrade.getPay_state().equals("SUCCESS")) {
			WXTrade newWXTrade = new WXTrade();
			WXTrade oldWXTrade = new WXTrade();
			oldWXTrade.setOut_trade_no(refund.getOutTradeNo());

			try {
				wxpay = new WXPay(WXPayConfigImpl.INSTANCE);
				HashMap<String, String> data = new HashMap<String, String>();
				data.put("out_trade_no", refund.getOutTradeNo());
				data.put("out_refund_no", refund.getOutTradeNo());
				data.put("total_fee", wxtrade.getTotal_fee());
				data.put("refund_fee", wxtrade.getTotal_fee());
				data.put("refund_fee_type", "CNY");
				data.put("op_user_id", WXPayConfigImpl.INSTANCE.getMchID());
				Map<String, String> r = wxpay.refund(data);
				// TODO 记录
				if (r.get("result_code").equals("SUCCESS")) {
					r.put("error_code", "0");
					newWXTrade.setPay_state("SUCCESS_REFUNDBY_"
							+ refund.getOperator());
					WXTradeDBHelp.updateWXTrade(oldWXTrade, newWXTrade);
					RefundDBHelp.insertRefund(refund);
					return new Gson().toJson(r);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		return "{\"error_code\":\"1\"}";

	}
}

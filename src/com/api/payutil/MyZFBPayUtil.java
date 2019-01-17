package com.api.payutil;

import com.alipay.demo.trade.model.builder.AlipayTradeRefundRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FRefundResult;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.api.dao.table.ZFBTrade;
import com.api.dao.tablehelp.ZFBTradeDBHelp;

public class MyZFBPayUtil {
	public static boolean doRefund(String outTradeNo) {
		ZFBTrade zfbtrade1 = new ZFBTrade();
		zfbtrade1.setOutTradeNo(outTradeNo);
		ZFBTrade zfbtrade = ZFBTradeDBHelp.getZFBTrade(zfbtrade1);
		if (zfbtrade == null)
			return false;

		// if(request.getParameter("outTradeNo")!=null){

		AlipayTradeService tradeService = new AlipayTradeServiceImpl.ClientBuilder()
				.build();

		// (必填) 退款金额，该金额必须小于等于订单的支付金额，单位为元
		String refundAmount = zfbtrade.getTotalAmount();

		// (可选，需要支持重复退货时必填) 商户退款请求号，相同支付宝交易号下的不同退款请求号对应同一笔交易的不同退款申请，
		// 对于相同支付宝交易号下多笔相同商户退款请求号的退款交易，支付宝只会进行一次退款
		// String outRequestNo = request.getParameter("outRequestNo");

		// (必填) 退款原因，可以说明用户退款原因，方便为商家后台提供统计
		String refundReason = "业务失败";

		// (必填) 商户门店编号，退款情况下可以为商家后台提供退款权限判定和统计等作用，详询支付宝技术支持
		String storeId = zfbtrade.getStoreId();

		AlipayTradeRefundRequestBuilder builder = new AlipayTradeRefundRequestBuilder()
				.setOutTradeNo(outTradeNo).setRefundAmount(refundAmount)
				.setRefundReason(refundReason)
				// .setOutRequestNo(outRequestNo)
				.setStoreId(storeId);

		AlipayF2FRefundResult result = tradeService.tradeRefund(builder);

		switch (result.getTradeStatus()) {
		case SUCCESS:
			// logger.info("支付宝退款成功: )");
			break;

		case FAILED:
			// logger.error("支付宝退款失败!!!");
			break;

		case UNKNOWN:
			// logger.error("系统异常，订单退款状态未知!!!");
			break;

		default:
			// logger.error("不支持的交易状态，交易返回异常!!!");
			break;
		}
		return false;

	}
}

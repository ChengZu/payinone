package com.api.dao.table;

public class Refund {
	// 退款请求ID
	private String id;
	// 订单支付时传入的商户订单号
	private String outTradeNo;
	// 标识一次退款请求，同一笔交易多次退款需要保证唯一，如需部分退款，则此参数必传。
	private String refundTradeNo;
	// 需要退款的金额，该金额不能大于订单金额,单位为元，支持两位小数
	private String amount;
	// 退款的原因说明
	private String reason;
	// 退款操作人员
	private String Operator;
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getOutTradeNo() {
		return outTradeNo;
	}
	public void setOutTradeNo(String outTradeNo) {
		this.outTradeNo = outTradeNo;
	}
	public String getRefundTradeNo() {
		return refundTradeNo;
	}
	public void setRefundTradeNo(String refundTradeNo) {
		this.refundTradeNo = refundTradeNo;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public String getOperator() {
		return Operator;
	}
	public void setOperator(String operator) {
		Operator = operator;
	}

}

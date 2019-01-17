package com.api.dao.table;

import com.google.gson.Gson;

public class WXTrade {
	// 商户订单号，必填
	private String out_trade_no;
	// 商品描述 ，必填
	private String body;
	// 设备号
	private String device_info;
	// 标价币种
	private String fee_type;
	// 标价金额，必填
	private String total_fee;
	// 终端IP
	private String spill_create_ip;
	// 交易类型，必填
	private String trade_type;
	// 商品ID
	private String product_id;
	// 下单时间
	private String create_time;
	// 支付状态
	private String pay_state;
	public String toString(){
		return new Gson().toJson(this);
	}
	public String getOut_trade_no() {
		return out_trade_no;
	}
	public void setOut_trade_no(String out_trade_no) {
		this.out_trade_no = out_trade_no;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public String getDevice_info() {
		return device_info;
	}
	public void setDevice_info(String device_info) {
		this.device_info = device_info;
	}
	public String getFee_type() {
		return fee_type;
	}
	public void setFee_type(String fee_type) {
		this.fee_type = fee_type;
	}
	public String getTotal_fee() {
		return total_fee;
	}
	public void setTotal_fee(String total_fee) {
		this.total_fee = total_fee;
	}
	public String getSpill_create_ip() {
		return spill_create_ip;
	}
	public void setSpill_create_ip(String spill_create_ip) {
		this.spill_create_ip = spill_create_ip;
	}
	public String getTrade_type() {
		return trade_type;
	}
	public void setTrade_type(String trade_type) {
		this.trade_type = trade_type;
	}
	public String getProduct_id() {
		return product_id;
	}
	public void setProduct_id(String product_id) {
		this.product_id = product_id;
	}
	public String getCreate_time() {
		return create_time;
	}
	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}
	public String getPay_state() {
		return pay_state;
	}
	public void setPay_state(String pay_state) {
		this.pay_state = pay_state;
	}
}

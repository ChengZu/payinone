package com.api.dao.table;

public class Device {
	private String id;
	private String name;
	private String tradeSubject;
	private String tradeBody;
	private String tradeAmount;
	private String parentId;
	private String type;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTradeSubject() {
		return tradeSubject;
	}
	public void setTradeSubject(String tradeSubject) {
		this.tradeSubject = tradeSubject;
	}
	public String getTradeBody() {
		return tradeBody;
	}
	public void setTradeBody(String tradeBody) {
		this.tradeBody = tradeBody;
	}
	public String getTradeAmount() {
		return tradeAmount;
	}
	public void setTradeAmount(String tradeAmount) {
		this.tradeAmount = tradeAmount;
	}
	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
}

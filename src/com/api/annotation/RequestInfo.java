package com.api.annotation;

public class RequestInfo {
	public String url;
	public String method;

	public RequestInfo(String url, String method) {
		this.url = url;
		this.method = method.toLowerCase();
	}
	
	public String toString(){
		return "{\"url\":\""+url+"\",\"method\":\""+method+"\"}";
	}
}

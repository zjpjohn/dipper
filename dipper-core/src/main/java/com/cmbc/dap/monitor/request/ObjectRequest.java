package com.cmbc.dap.monitor.request;

import java.util.Map;

import com.alibaba.fastjson.JSONObject;

public class ObjectRequest {
	String jsonrpc = "2.0";
	Object params;

	String method;

	String auth;

	Integer id;

	public void putParam(Object value) {
		params = value;
	}

	public Object removeParam() {
		return params = "";
	}

	public String getJsonrpc() {
		return jsonrpc;
	}

	public void setJsonrpc(String jsonrpc) {
		this.jsonrpc = jsonrpc;
	}

	public Object getParams() {
		return params;
	}

	public void setParams(Map<String, Object> params) {
		this.params = params;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getAuth() {
		return auth;
	}

	public void setAuth(String auth) {
		this.auth = auth;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return JSONObject.toJSONString(this);
	}
}

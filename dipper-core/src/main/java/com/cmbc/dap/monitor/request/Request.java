package com.cmbc.dap.monitor.request;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;

public class Request {
	private String jsonrpc = "2.0";
	private Map<String, Object> params = new HashMap<String, Object>();
	private String method;
	private String auth;
	private int id;
	
	private static final Logger LOGGER = Logger.getLogger(Request.class);

	public void putParam(String key, Object value) {
		params.put(key, value);
	}

	public Object removeParam(String key) {
		return params.remove(key);
	}

	public String getJsonrpc() {
		return jsonrpc;
	}

	public void setJsonrpc(String jsonrpc) {
		this.jsonrpc = jsonrpc;
	}

	public Map<String, Object> getParams() {
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

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public String toString() {
		String aa = "";
		try {
			aa = new String(JSONObject.toJSONString(this).getBytes(),"utf-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error("code transfer error", e);
		}
		return aa;
	}
}

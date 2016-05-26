package com.cmbc.dap.monitor.client;

import com.alibaba.fastjson.JSONObject;
import com.cmbc.dap.monitor.request.ObjectRequest;
import com.cmbc.dap.monitor.request.Request;

public interface ZabbixApi {

	public void init();

	public void destory();

	public String apiVersion();

	public JSONObject call(Request request);

	public JSONObject call(ObjectRequest request);

	public boolean login(String user, String password);
}

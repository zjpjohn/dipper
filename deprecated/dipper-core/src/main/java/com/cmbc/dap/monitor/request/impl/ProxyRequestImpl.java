package com.cmbc.dap.monitor.request.impl;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cmbc.dap.monitor.client.ZabbixMethod;
import com.cmbc.dap.monitor.request.ProxyRequest;
import com.cmbc.dap.monitor.request.Request;
import com.cmbc.dap.monitor.request.RequestBuilder;

/**  
 * date：2015年12月3日 下午4:28:48  
 * project name：cmbc-devops-core  
 * @author langzi  
 * @version 1.0   
 * @since JDK 1.7.0_21  
 * file name：ProxyRequestImpl.java  
 * description：  
 */
public class ProxyRequestImpl implements ProxyRequest {
	
	private static final Logger LOGGER = Logger.getLogger(ProxyRequest.class);
	
	@Override
	public Request getProxyRequest(String proxyName) {
		JSONObject jo = new JSONObject();
		JSONArray ja = new JSONArray();
		ja.add(proxyName);
		jo.put("selectHosts", ja);
		Request request = RequestBuilder.newBuilder().method(ZabbixMethod.GET_PROXY.getMethod())
				.paramEntry("params", jo).build();
		LOGGER.info(request);
		return request != null ? request : null;
	}

}

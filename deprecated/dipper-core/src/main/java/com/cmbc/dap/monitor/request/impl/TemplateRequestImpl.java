package com.cmbc.dap.monitor.request.impl;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cmbc.dap.monitor.client.ZabbixMethod;
import com.cmbc.dap.monitor.request.Request;
import com.cmbc.dap.monitor.request.RequestBuilder;
import com.cmbc.dap.monitor.request.TemplateRequest;

/**  
 * date：2015年11月17日 上午11:47:46  
 * project name：cmbc-devops-core  
 * @author langzi  
 * @version 1.0   
 * @since JDK 1.7.0_21  
 * file name：TemplateMonitorImpl.java  
 * description：  
 */
@Component
public class TemplateRequestImpl implements TemplateRequest {
	
	private static final Logger logger = Logger.getLogger(TemplateRequest.class);

	/* (non-Javadoc)
	 * @see com.cmbc.devops.core.zabbix.monitor.TemplateMonitor#getTempRequest(java.lang.String)
	 */
	@Override
	public Request getTempRequest(String tempName) {
		JSONObject jo = new JSONObject();
		JSONArray ja = new JSONArray();
		ja.add(tempName);
		jo.put("host", ja);
		Request request = RequestBuilder.newBuilder().method(ZabbixMethod.GET_TEMPLATE.getMethod())
				.paramEntry("filter", jo).build();
		logger.info(request);
		return request != null ? request : null;
	}

}

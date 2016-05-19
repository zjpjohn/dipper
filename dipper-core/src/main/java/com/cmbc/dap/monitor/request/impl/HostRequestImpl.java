package com.cmbc.dap.monitor.request.impl;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cmbc.dap.monitor.client.ZabbixMethod;
import com.cmbc.dap.monitor.model.HostInfo;
import com.cmbc.dap.monitor.model.HostWithTemplate;
import com.cmbc.dap.monitor.request.HostRequest;
import com.cmbc.dap.monitor.request.ObjectRequest;
import com.cmbc.dap.monitor.request.ObjectRequestBuilder;
import com.cmbc.dap.monitor.request.Request;
import com.cmbc.dap.monitor.request.RequestBuilder;

/**  
 * date：2015年11月17日 上午11:46:59  
 * project name：cmbc-devops-core  
 * @author langzi  
 * @version 1.0   
 * @since JDK 1.7.0_21  
 * file name：HostMonitorImpl.java  
 * description：  
 */
@Component
public class HostRequestImpl implements HostRequest {

	private static final Logger logger = Logger.getLogger(HostRequest.class);
	
	/* (non-Javadoc)
	 * @see com.cmbc.devops.core.zabbix.monitor.HostMonitor#addHostMonitorWithTemplateRequest(int, com.cmbc.devops.core.zabbix.entity.HostWithTemplate)
	 */
	@Override
	public Request addHostMonitorWithTemplateRequest(int groupId,
			HostWithTemplate host) {
		JSONArray groups = new JSONArray();
		JSONObject group = new JSONObject();
		JSONArray interfaces = new JSONArray();
		JSONArray templates = new JSONArray();
		interfaces.add(JSONObject.toJSON(host.getHostInfo()));
		group.put("groupid", groupId);
		groups.add(group);
		if (host.getTemplateInfos()!=null) {
			templates = (JSONArray)JSONArray.toJSON(host.getTemplateInfos());
		}
		Request request = RequestBuilder.newBuilder().method(ZabbixMethod.ADD_HOST.getMethod())
				.paramEntry("host", host.getHostInfo().getHostname()).paramEntry("interfaces", interfaces)
				.paramEntry("groups", groups)
				.paramEntry("proxy_hostid", host.getHostInfo().getProxyHostId())
				.paramEntry("templates", templates).build();
		logger.warn(request);
		return request != null ? request:null;
	}

	/* (non-Javadoc)
	 * @see com.cmbc.devops.core.zabbix.monitor.HostMonitor#addHostMonitor(int, com.cmbc.devops.core.zabbix.entity.HostInfo)
	 */
	@Override
	public Request addHostMonitorRequest(int groupId, HostInfo host) {
		JSONArray groups = new JSONArray();
		JSONObject group = new JSONObject();
		JSONArray interfaces = new JSONArray();
		interfaces.add(JSONObject.toJSON(host));
		group.put("groupid", groupId);
		groups.add(group);
		Request request = RequestBuilder.newBuilder().method(ZabbixMethod.ADD_HOST.getMethod())
				.paramEntry("host", host.getHostname()).paramEntry("interfaces", interfaces)
				.paramEntry("groups", groups).build();
		logger.warn(request);
		return request != null ? request:null;
	}

	/* (non-Javadoc)
	 * @see com.cmbc.devops.core.zabbix.request.HostRequest#enableHostMonitorRequest(java.lang.String)
	 */
	@Override
	public Request enableHostMonitorRequest(String hostId) {
		Request request = RequestBuilder.newBuilder().method(ZabbixMethod.UPDATE_HOST.getMethod())
				.paramEntry("hostid", hostId).
				paramEntry("status", 0).build();
		logger.warn(request);
		return request != null ? request : null;
	}
	
	/* (non-Javadoc)
	 * @see com.cmbc.devops.core.zabbix.request.HostRequest#disableHostMonitorRequest(java.lang.String)
	 */
	@Override
	public Request disableHostMonitorRequest(String hostId) {
		Request request = RequestBuilder.newBuilder().method(ZabbixMethod.UPDATE_HOST.getMethod())
				.paramEntry("hostid", hostId).
				paramEntry("status", 1).build();
		logger.warn(request);
		return request != null ? request : null;
	}
	
	/* (non-Javadoc)
	 * @see com.cmbc.devops.core.zabbix.monitor.HostMonitor#cancelHostMonitor(java.lang.String[])
	 */
	@Override
	public ObjectRequest cancelHostMonitorRequest(String[] hostIds) {
		ObjectRequest request = ObjectRequestBuilder.newBuilder().method(ZabbixMethod.DEL_HOST.getMethod())
		.paramEntry(hostIds).build();
		logger.warn(request);
		return request != null ? request:null;
	}

	@Override
	public Request getHostGroupRequest(String groupName) {
		JSONObject jo = new JSONObject();
		JSONArray ja = new JSONArray();
		ja.add(groupName);
		jo.put("name", ja);
		Request request = RequestBuilder.newBuilder().method(ZabbixMethod.GET_HOSTGROUP.getMethod())
				.paramEntry("filter", jo).build();
		logger.warn(request);
		return request != null ? request : null;
	}

}

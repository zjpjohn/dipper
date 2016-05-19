package com.cmbc.dap.monitor.client;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cmbc.dap.monitor.model.HostWithTemplate;
import com.cmbc.dap.monitor.model.ItemInfo;
import com.cmbc.dap.monitor.model.TriggerInfo;
import com.cmbc.dap.monitor.model.ZxNodeInfo;
import com.cmbc.dap.monitor.request.HostRequest;
import com.cmbc.dap.monitor.request.ItemRequest;
import com.cmbc.dap.monitor.request.ObjectRequest;
import com.cmbc.dap.monitor.request.Request;
import com.cmbc.dap.monitor.request.RequestBuilder;
import com.cmbc.dap.monitor.request.TemplateRequest;
import com.cmbc.dap.monitor.request.TriggerRequest;
import com.cmbc.devops.bean.Result;
import com.cmbc.devops.config.SystemConfig;

/**
 * 默认容器监控互操作实现类
 * 
 * @author dmw
 *
 */
@Component
public class DefaultMonitor implements Monitor {

	private static final Logger LOGGER = Logger.getLogger(DefaultMonitor.class);

	private ZabbixApi zabbix;
	@Autowired
	private SystemConfig systemConfig;
	@Autowired
	private HostRequest hostRequest;
	@Autowired
	private ItemRequest itemRequest;
	@Autowired
	private TemplateRequest templateRequest;
	@Autowired
	private TriggerRequest triggerRequest;

	@PostConstruct
	public void init() {
		if (null == zabbix) {
			zabbix = new DefaultZabbixApi(systemConfig.getZabbixServer());
		}
	}

	public ZabbixApi getZabbix() {
		return zabbix;
	}

	public void setZabbix(ZabbixApi zabbix) {
		this.zabbix = zabbix;
	}

	public SystemConfig getSystemConfig() {
		return systemConfig;
	}

	public void setSystemConfig(SystemConfig systemConfig) {
		this.systemConfig = systemConfig;
	}

	@Override
	public Result addHostMonitor(int groupId, HostWithTemplate host) {
		Request request = hostRequest.addHostMonitorWithTemplateRequest(groupId, host);
		boolean success = zabbixLoggin();
		if (!success) {
			return new Result(false, "zabbix user auth error");
		}
		JSONObject response = zabbix.call(request);
		LOGGER.info(response);
		if (null != response.get("error")) {
			return new Result(false, response.getJSONObject("error").getString("message"));
		}
		String hostId = response.getJSONObject("result").getJSONArray("hostids").getString(0);
		LOGGER.info(hostId);
		return new Result(true, hostId);
	}

	@Override
	public Result cancelHostMonitor(String hostId) {
		String[] hosts = { hostId };
		ObjectRequest request = hostRequest.cancelHostMonitorRequest(hosts);
		boolean success = zabbixLoggin();
		if (!success) {
			return new Result(false, "zabbix user auth error");
		}
		JSONObject response = zabbix.call(request);
		LOGGER.info(response);
		if (null != response.get("error")) {
			return new Result(false, response.getJSONObject("error").getString("message"));
		}
		JSONObject obj=response.getJSONObject("result");
		hostId = obj.getJSONArray("hostids").getString(0);
		return new Result(true, hostId);
	}

	@Override
	public List<Result> addHostMonitor(int groupId, List<HostWithTemplate> hosts) {
		List<Result> results = new ArrayList<Result>();
		if (null != hosts) {
			for (HostWithTemplate host : hosts) {
				results.add(this.addHostMonitor(groupId, host));
			}
		}
		return results;
	}

	@Override
	public Result disableHostMonitor(String hostId) {
		Request request = hostRequest.disableHostMonitorRequest(hostId);
		boolean success = zabbixLoggin();
		if (!success) {
			return new Result(false, "zabbix user auth error");
		}
		JSONObject response = zabbix.call(request);
		LOGGER.info(response);
		if (null != response.get("error")) {
			return new Result(false, response.getJSONObject("error").getString("message"));
		}
		JSONObject obj=response.getJSONObject("result");
		hostId = obj.getJSONArray("hostids").getString(0);
		return new Result(true, hostId);
	}

	@Override
	public Result enableHostMonitor(String hostId) {
		Request request = hostRequest.enableHostMonitorRequest(hostId);
		boolean success = zabbixLoggin();
		if (!success) {
			return new Result(false, "zabbix user auth error");
		}
		JSONObject response = zabbix.call(request);
		LOGGER.info(response);
		if (null != response.get("error")) {
			return new Result(false, response.getJSONObject("error").getString("message"));
		}
		JSONObject obj=response.getJSONObject("result");
		hostId = obj.getJSONArray("hostids").getString(0);
		return new Result(true, hostId);
	}

	@Override
	public Result cancelHostMonitor(String[] hostIds) {
		ObjectRequest request = hostRequest.cancelHostMonitorRequest(hostIds);
		boolean success = zabbixLoggin();
		if (!success) {
			return new Result(false, "zabbix user auth error");
		}
		JSONObject response = zabbix.call(request);
		LOGGER.info(response);
		if (null != response.get("error")) {
			return new Result(false, response.getJSONObject("error").getString("message"));
		}
		String hostId = response.getJSONObject("result").getJSONArray("hostids").toJSONString();
		return new Result(true, hostId);
	}

	@Override
	public Result addMoniItem(ItemInfo item) {
		Result interfaceResult = this.getInterfaceId(item.getHostId(), "10050");
		if (!interfaceResult.isSuccess()) {
			return new Result(false, "get host interface failed!");
		}
		item.setInterfaceId(interfaceResult.getMessage());
		Request request = itemRequest.addItemMonitorRequest(item);
		boolean success = zabbixLoggin();
		if (!success) {
			return new Result(false, "zabbix user auth error");
		}
		JSONObject response = zabbix.call(request);
		LOGGER.info(response);
		if (null != response.get("error")) {
			return new Result(false, response.getJSONObject("error").getString("message"));
		}
		String itemids = response.getJSONObject("result").getJSONArray("itemids").getString(0);
		return new Result(true, itemids);
	}

	@Override
	public List<Result> addMoniItems(List<ItemInfo> items) {
		List<Result> results = new ArrayList<Result>();
		if (null != items) {
			for (ItemInfo itemIntf : items) {
				results.add(this.addMoniItem(itemIntf));
			}
		}
		return results;
	}

	@Override
	public Result addTriggerMonitor(TriggerInfo trigger) {
		Request request = triggerRequest.addTriggerMonitorRequest(trigger);
		boolean success = zabbixLoggin();
		if (!success) {
			return new Result(false, "zabbix user auth error");
		}
		JSONObject response = zabbix.call(request);
		LOGGER.info(response);
		if (null != response.get("error")) {
			return new Result(false, response.getJSONObject("error").getString("message"));
		}
		String triggerids = response.getJSONObject("result").getJSONArray("triggerids").getString(0);
		return new Result(true, triggerids);
	}

	@Override
	public List<Result> addTriggerMonitor(List<TriggerInfo> triggers) {
		List<Result> results = new ArrayList<Result>();
		if (!triggers.isEmpty()) {
			for (TriggerInfo trigger : triggers) {
				results.add(this.addTriggerMonitor(trigger));
			}
		}
		return results;
	}

	@Override
	public Result getTemplates() {
		Request request = templateRequest.getTempRequest(systemConfig.getZabbixTemplateNames());
		boolean success = zabbixLoggin();
		if (!success) {
			return new Result(false, "zabbix user auth error");
		}
		JSONObject response = zabbix.call(request);
		if (null != response.get("error")) {
			return new Result(false, response.getJSONObject("error").getString("message"));
		}
		String templateId = (String) ((JSONObject) response.getJSONArray("result").get(0)).get("templateid");
		LOGGER.info(templateId);
		return new Result(true, templateId);
	}

	@Override
	public Result getGroup() {
		Request request = hostRequest.getHostGroupRequest(systemConfig.getZabbixHostName());
		boolean success = zabbixLoggin();
		if (!success) {
			return new Result(false, "zabbix user auth error");
		}
		JSONObject response = zabbix.call(request);
		if (null != response.get("error")) {
			return new Result(false, response.getJSONObject("error").getString("message"));
		}
		LOGGER.info(response);
		String hostId = (String) ((JSONObject) response.getJSONArray("result").get(0)).get("groupid");
		LOGGER.info(hostId);
		return new Result(true, hostId);
	}

	private Result getInterfaceId(String hostId, String port) {
		Request request = RequestBuilder.newBuilder().method(ZabbixMethod.GET_INTF.getMethod())
				.paramEntry("hostids", hostId).paramEntry("output", "extend").build();
		boolean success = zabbixLoggin();
		if (!success) {
			return new Result(false, "zabbix user auth error");
		}
		JSONObject response = zabbix.call(request);
		if (null != response.get("error")) {
			return new Result(false, response.getJSONObject("error").getString("message"));
		}
		Result result = new Result(false, "null");
		JSONArray interfaces = response.getJSONArray("result");
		for (int i = 0; i < interfaces.size(); i++) {
			JSONObject interObj = interfaces.getJSONObject(i);
			String interfacePort = interObj.getString("port");
			if (port.equalsIgnoreCase(interfacePort)) {
				result = new Result(true, interObj.getString("interfaceid"));
				break;
			}
		}
		return result;
	}

	/** 根据容器的名称向Zabbix查询所有节点nodezx_list的items中的负载信息 */
	/**
	 * 调用函数：根据应用的ID获取全部相关的容器的所在主机和ID，组装容器名称"Container_"+ip（主机IP地址）+"_"+uuid（
	 * CON_UUID的substring(0,12)
	 */
	public String queryZxConItems(List<String> nodes) {

		/* 链表为空则直接返回null */
		if (nodes.isEmpty()) {
			return null;
		}
		/*（1）登陆zabbix系统获取授权信息*/
		boolean success = zabbixLoggin();
		if (!success) {
			LOGGER.error(" zabbix user auth error!");
			return null;
		}
		
		/*（2）将主机名称列表转化为字符串数组，嵌入到给Zabbix的请求中*/
		int nodeSize = nodes.size();
		String[] hostNames = new String[nodeSize];
		for(int i=0;i<nodeSize;i++){
			hostNames[i] = nodes.get(i);
		}
		JSONObject jo = new JSONObject();
		jo.put("host", hostNames);
		

		/*（3）创建查询主机的请求，仅请求返回相应的主机（容器）ID信息 */
		Request request = RequestBuilder.newBuilder().method(ZabbixMethod.GET_ALLHOST.getMethod())
				.paramEntry("filter", jo).paramEntry("output", "hostid").build();

		/* 向Zabbix发起查询请求 */
		LOGGER.info("Begin query all host request:[" + request.toString() + "]");
		JSONObject responseJo = zabbix.call(request);
		if (null != responseJo.get("error")) {
			LOGGER.info(responseJo.getJSONObject("error").getString("message"));
			return null;
		}

		/*（4）解析返回的主机信息，获取主机（容器的ID）的ID */
		LOGGER.info("Finish query all host response:[" + responseJo.toString() + "]");
		List<String> nodeIds = new ArrayList<String>();
		JSONArray resultJo = responseJo.getJSONArray("result");
		if (resultJo == null) {
			return null;
		}
		for (int i = 0; i < resultJo.size(); i++) {
			JSONObject nodeObj = resultJo.getJSONObject(i);
			nodeIds.add(nodeObj.getString("hostid"));
		}

		/* 释放请求主机对象 */
		request = null;
		responseJo.clear();

		/* 判断节点的ID链表是否为空 */
		if (nodeIds.isEmpty()) {
			LOGGER.info("Query hostid list is empty.Please check zabbix.");
			return null;
		}
		
		/*（5）将返回的列表转化为字符串数组*/
		int nodeIdSIze = nodeIds.size();
		String[] ids = new String[nodeIdSIze];
		for(int i=0;i<nodeIdSIze;i++){
			ids[i] = nodeIds.get(i);
		}

		/* （6）创建查询主机中Items的请求 */
		Request queryitemsRequest = RequestBuilder.newBuilder().method(ZabbixMethod.GET_ITEMS.getMethod())
				.paramEntry("hostids", ids).paramEntry("output", "extend").build();
		
		/* 向Zabbix发起查询监控项请求 */
		LOGGER.info("Begin query all items request:[" + queryitemsRequest.toString() + "]");
		JSONObject response = zabbix.call(request);
		if (null != response.get("error")) {
			LOGGER.info(response.getJSONObject("error").getString("message"));
			return null;
		}
		/* 解析返回的主机信息，获取主机（容器的ID）的ID */
		LOGGER.info("Finish query all items response:[" + responseJo.toString() + "]");
		
		/*（7）解析返回的item.get的结果信息*/
		JSONArray items = responseJo.getJSONArray("result");
		for (int i = 0; i < items.size(); i++) {
			JSONObject nodeObj = items.getJSONObject(i);
			nodeIds.add(nodeObj.getString("hostid"));
		}
		

		return null;
	}

	/** 模拟获取zabbix上的监控数据 **/
	public String queryZxHosts(Integer appId) {

		DecimalFormat dcmFmt = new DecimalFormat("0.0");
		Random rand = new Random();
		List<ZxNodeInfo> retList = new ArrayList<ZxNodeInfo>();
		for (int count = 0; count < 20; count++) {
			retList.add(new ZxNodeInfo("" + count, "node" + count,
					Float.parseFloat(dcmFmt.format(30 + rand.nextFloat() * 10)),
					Float.parseFloat(dcmFmt.format(60 + rand.nextFloat() * 10)),
					Float.parseFloat(dcmFmt.format(90 + rand.nextFloat() * 10))));
		}

		return JSON.toJSONString(retList);
	}

	private boolean zabbixLoggin() {
		return zabbix.login(systemConfig.getZabbixManager(), systemConfig.getZabbixPassword());
	}

}

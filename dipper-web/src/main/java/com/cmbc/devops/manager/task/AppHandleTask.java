package com.cmbc.devops.manager.task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.cxf.endpoint.Client;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cmbc.dap.monitor.client.Monitor;
import com.cmbc.dap.monitor.model.HostInfo;
import com.cmbc.dap.monitor.model.HostWithTemplate;
import com.cmbc.dap.monitor.model.ItemInfo;
import com.cmbc.dap.monitor.model.MonitorList;
import com.cmbc.dap.monitor.model.TemplateInfo;
import com.cmbc.dap.monitor.model.TriggerInfo;
import com.cmbc.devops.bean.Result;
import com.cmbc.devops.config.SystemConfig;
import com.cmbc.devops.constant.Status;
import com.cmbc.devops.entity.App;
import com.cmbc.devops.model.ApplicationReleaseModel;
import com.cmbc.devops.webservice.ApplicationClientBuilder;
import com.cmbc.devops.webservice.ApplicationMethod;

/**  
 * date：2015年12月28日 下午1:50:29  
 * project name：cmbc-devops-web  
 * @author langzi  
 * @version 1.0   
 * @since JDK 1.7.0_21  
 * file name：ApplicationReleaseTask.java  
 * description：  
 */
public class AppHandleTask implements Callable<ApplicationReleaseModel>{
	
	private ApplicationClientBuilder builder;
	private ApplicationMethod applicationMethod;
	private SystemConfig config;
	private Monitor monitor;
	private App app;
	private JSONObject conJo;
	private ApplicationReleaseModel model;
	
	Logger logger = Logger.getLogger(AppHandleTask.class);
	
	public AppHandleTask(ApplicationClientBuilder builder, ApplicationMethod applicationMethod, 
			SystemConfig config, Monitor monitor, App app, JSONObject conJo, ApplicationReleaseModel model) {
		this.builder = builder;
		this.applicationMethod = applicationMethod;
		this.config = config;
		this.monitor = monitor;
		this.app = app;
		this.conJo = conJo;
		this.model = model;
	}

	@Override
	public ApplicationReleaseModel call() throws Exception {
		JSONArray ports = (JSONArray) conJo.get("ports");
		String conName = ((JSONArray) conJo.get("names")).getString(0);
		model.setConName(conName);
		//根据健康检查开关判断是否进行健康检查
		if(model.getAppHealth() == 0){
			model.setAppStatus(Status.APP_STATUS.UNDEFINED.ordinal());
			model.setMonitorStatus(Status.MONITOR_STATUS.UNDEFINED.ordinal());
			return model;
		}
		//应用健康状态检查
		Result result = new Result(false, "");
		int appPort = app.getAppPriPort() == null ? 0 : app.getAppPriPort();
		String ip = "", pubPort = "";
		for (int i = 0; i < ports.size(); i++) {
			JSONObject port = (JSONObject) ports.get(i);
			int conPort = port.getInteger("privatePort");
			if (appPort!=conPort) {
				continue;
			}
			ip = port.getString("ip");
			pubPort = port.getString("publicPort");
		}
		//应用是否启动检测
		result = isApplicationStarted(ip, pubPort);
		if (!result.isSuccess()) {
			model.setAppStatus(Status.APP_STATUS.ERROR.ordinal());
			model.setMonitorStatus(Status.MONITOR_STATUS.ABNORMAL.ordinal());
			logger.info("application is start failed:"+model.toString());
			return model;
		}
		//应用健康检查
		result = appHealthCheck(ip, pubPort);
		// 应用不健康，不用添加应用状态和主机监控
		if (!result.isSuccess()) {
			model.setAppStatus(Status.APP_STATUS.ERROR.ordinal());
			model.setMonitorStatus(Status.MONITOR_STATUS.ABNORMAL.ordinal());
			logger.info("application health check failed:"+model.toString());
			return model;
		} 
		// 应用健康，修改应用状态
		model.setAppStatus(Status.APP_STATUS.NORMAL.ordinal());
		//根据监控开关是否添加监控
		if(model.getAppMonitor() == 0){
			model.setMonitorStatus(Status.MONITOR_STATUS.UNDEFINED.ordinal());
			return model;
		}
		// 8.加入主机监控
		String hostIp = ((JSONObject) ports.get(0)).getString("ip");
		String conUuid = conJo.getString("id").substring(0, 12);
		Result monitorResult = new Result();
		monitorResult = addHostMonitor(hostIp, conUuid);
		// 加入主机监控失败，监控状态不正常
		if (!monitorResult.isSuccess()) {
			model.setMonitorStatus(Status.MONITOR_STATUS.ABNORMAL.ordinal());
			logger.info("model.toString():"+model.toString());
			return model;
		}
		// 加入主机监控，监控状态正常
		model.setMonitorStatus(Status.MONITOR_STATUS.NORMAL.ordinal());
		model.setMonitorHostId(monitorResult.getMessage());
		// 获取监控信息
		MonitorList monitors = getMonitorList(ip, app.getAppProxy(), pubPort);
		if (monitors == null) {
			logger.warn("Other monitors not find or get other items failed");
			logger.info("model.toString():"+model.toString());
			return model;
		} 
		// 9.添加其他监控项
		List<ItemInfo> items = monitors.getItemInfos();
		List<ItemInfo> addItems = new ArrayList<ItemInfo>();
		logger.info(JSON.toJSON(items));
		if (!items.isEmpty()) {
			for (ItemInfo item : items) {
				ItemInfo itemInfo = new ItemInfo(monitorResult.getMessage(), item.getItemKey(), "", 10);
				addItems.add(itemInfo);
			}
			monitor.addMoniItems(addItems);
		}
		// 10.添加trigger
		List<TriggerInfo> triggers = monitors.getTriggerInfos();
		List<TriggerInfo> addTriggers = new ArrayList<TriggerInfo>();
		logger.info(JSON.toJSON(triggers));
		if (!triggers.isEmpty()) {
			for (TriggerInfo trigger : triggers) {
				TriggerInfo triggerInfo = new TriggerInfo();
				triggerInfo.setExpression(trigger.getExpression());
				triggerInfo.setDescription(trigger.getDescription());
				triggerInfo.setPriority(trigger.getPriority());
				addTriggers.add(triggerInfo);
			}
			monitor.addTriggerMonitor(addTriggers);
		}
		// 11.保存到数据库
		logger.info("model.toString():"+model.toString());
		return model;
	}
	
	private Result isApplicationStarted(String ip, String port) {
		Result result = new Result(false, "");
		long begingTime = System.currentTimeMillis();
		while (true) {
			logger.info("check application is started");
			if ((System.currentTimeMillis() - begingTime) > config.getDelayTime()) {
				result.setMessage("应用启动检查超时！");
				break;
			}
			Client client = builder.createClient(ip, port,
					ApplicationClientBuilder.HEALTH_PATH, true);
			String method = applicationMethod.isApplicationStarted();
			try {
				if (client == null) {
					Thread.sleep(10 * 1000);
					continue;
				}
				Object[] objs = client.invoke(method);
				if (objs != null) {
					logger.info("application is started result:" + objs[0]);
					result.setSuccess((boolean) objs[0]);
				}
				if (!result.isSuccess()) {
					Thread.sleep(10 * 1000);
					continue;
				}
				logger.info("check application start stop");
				break;
			} catch (Exception e) {
				logger.error("check application start error", e);
				break;
			}
		}
		return result;
	}
	
	private Result appHealthCheck(String ip, String port) {
		Result result = new Result(false, "");
		//long begingTime = System.currentTimeMillis();
		//while (true) {
			logger.info("healthcheck start");
			/*if ((System.currentTimeMillis() - begingTime) > config.getDelayTime()) {
				result.setMessage("应用检查超时！");
				break;
			}*/
		Client client = builder.createClient(ip, port,
				ApplicationClientBuilder.HEALTH_PATH, true);
		String method = applicationMethod.healthCheck();
		try {
			/*if (client == null) {
				Thread.sleep(10 * 1000);
				continue;
			}*/
			Object[] objs = client.invoke(method);
			if (objs != null) {
				logger.info("healthcheck result:" + objs[0]);
				result.setSuccess((boolean) objs[0]);
			}
			logger.info("healthcheck stop");
			//break;
		} catch (Exception e) {
			logger.error("application health check error", e);
		}
		//}
		return result;
	}
	
	private Result addHostMonitor(String ip, String conUuid) {
		HostInfo hostInfo = new HostInfo(ip, "", conUuid, config.getZabbixProxyId());
		TemplateInfo templateInfo = new TemplateInfo();
		Result tempResult = monitor.getTemplates();
		if (!tempResult.isSuccess()) {
			logger.warn("Get template info failed");
			return new Result(false, "未获取到模板信息，加入主机监控失败");
		}
		templateInfo.setTemplateid(tempResult.getMessage());
		List<TemplateInfo> templateInfos = new ArrayList<TemplateInfo>();
		templateInfos.add(templateInfo);
		HostWithTemplate monitorhost = new HostWithTemplate(hostInfo, templateInfos);
		Result groupResult = monitor.getGroup();
		if (!groupResult.isSuccess()) {
			return new Result(false, "未获取到主机所在分组信息，加入主机监控失败");
		}
		return monitor.addHostMonitor(Integer.parseInt(groupResult.getMessage()), monitorhost);
	}

	private MonitorList getMonitorList(String ip, String monitorProxy, String port) {
		MonitorList monitorList = null;
		//long begingTime = System.currentTimeMillis();
		/*while (true) {
			if ((System.currentTimeMillis() - begingTime) > config.getDelayTime()) {
				logger.info("获取监控列表超时");
				break;
			}*/
			Client client = builder.createClient(ip, port,
					ApplicationClientBuilder.MONITOR_PATH, false);
			String method = applicationMethod.monitorList();
			
		if (client == null) {
			logger.info("get application monitor connection is null");
			return null;
		}
		try {
			/*if (client == null) {
				Thread.sleep(10 * 1000);
				continue;
			}*/
			String[] proxy=monitorProxy.split(":");
			Object[] objs = client.invoke(method, proxy[0],Integer.parseInt(proxy[1]));
			if (objs != null) {
				String monitorJson = (String) objs[0];
				logger.info("monitor items:"+monitorJson);
				monitorList = JSON.parseObject(monitorJson, MonitorList.class);
			}
			//break;
		} catch (Exception e) {
			logger.error("Get monitor items error", e);
			//break;
		}
		//}
		return monitorList;
	}


}

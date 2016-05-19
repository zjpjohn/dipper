package com.cmbc.devops.controller.action;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.cmbc.dap.monitor.client.DefaultMonitor;
import com.cmbc.devops.entity.App;
import com.cmbc.devops.entity.Cluster;
import com.cmbc.devops.entity.Container;
import com.cmbc.devops.entity.Host;
import com.cmbc.devops.entity.User;
import com.cmbc.devops.service.AppService;
import com.cmbc.devops.service.ClusterService;
import com.cmbc.devops.service.ContainerService;
import com.cmbc.devops.service.HostService;

/**
 * date：2016年1月5日 下午3:04:31 project name：cmbc-devops-web
 * 
 * @author langzi
 * @version 1.0
 * @since JDK 1.7.0_21 file name：AppAction.java description：
 */
@RequestMapping("zabbix")
@Controller
public class ZabbixAction {
	private static final Logger LOGGER = Logger.getLogger(ZabbixAction.class);
	@Autowired
	private DefaultMonitor defaultMonitor;
	@Autowired
	private AppService appService;
	@Autowired
	private ClusterService clusterService;
	@Autowired
	private ContainerService containerService;
	@Autowired
	private HostService hostService;

	/**
	 * @author yangqinglin
	 * @param id
	 * @return 通过应用的维度查看监控信息
	 * @version 1.0 2016年1月26日
	 */
	@RequestMapping("/appview/{appId}.html")
	public ModelAndView appview(HttpServletRequest request, @PathVariable Integer appId) {
		User user = (User) request.getSession().getAttribute("user");
		ModelAndView mav = new ModelAndView("monitor/mon_index");
		mav.addObject("type", "app");
		try {
			App application = appService.findAppById(user.getTenantId(), appId);
			if (application != null) {
				mav.addObject("itemName", application.getAppName());
			}
		} catch (Exception e) {
			LOGGER.error("通过应用ID：" + appId + "，查询对象失败。请核对数据库记录。", e);
		}
		mav.addObject("appId", appId);
		return mav;
	}

	/**
	 * @author yangqinglin
	 * @param id
	 * @return 通过集群的维度查看监控信息
	 * @version 1.0 2016年1月26日
	 */
	@RequestMapping("/clusterview/{clusterId}.html")
	public ModelAndView clusterview(@PathVariable Integer clusterId) {
		ModelAndView mav = new ModelAndView("monitor/mon_index");
		mav.addObject("type", "cluster");
		try {
			Cluster cluster = clusterService.getCluster(clusterId);
			if (cluster != null) {
				mav.addObject("itemName", cluster.getClusterName());
			}
		} catch (Exception e) {
			LOGGER.error("通过集群ID：" + clusterId + "，查询对象失败。请核对数据库记录。", e);
		}
		mav.addObject("clusterId", clusterId);
		return mav;
	}

	/** 基于应用ID查询全部主机负载 */
	@RequestMapping(value = "/queryAppView", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public String queryAppView(HttpServletRequest request, Integer appId) {
		/* 获取全部的主机列表 */
		List<Host> allhost_list = null;
		Host host = new Host();
		try {
			allhost_list = hostService.loadAllHost(host);
		} catch (Exception e1) {
			LOGGER.error("query all host list failed", e1);
		}

		/* 如果返回主机列表为空，则直接返回错误 */
		if (allhost_list.isEmpty()) {
			return null;
		}

		ArrayList<String> allZxConName_list = new ArrayList<String>();
		/* 根据应用，获取所有属于此应用的容器 */
		try {
			List<Container> container_list = containerService.listContainersByAppId(appId);
			for (Container sin_container : container_list) {
				/*
				 * 组装容器名称"Container_"+ip（主机IP地址）+"_"+uuid（CON_UUID的substring(0,
				 * 12)
				 */
				Integer host_id = sin_container.getHostId();
				for (Host sin_host : allhost_list) {
					if (sin_host.getHostId() == host_id) {
						String conName = "Container_" + sin_host.getHostIp() + "_"
								+ sin_container.getConUuid().substring(0, 12);
						allZxConName_list.add(conName);
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error("query container list by app id :" + appId + " failed", e);
		}

		return defaultMonitor.queryZxConItems(allZxConName_list);
	}

	/** 基于集群ID查询全部主机负载 */
	@RequestMapping(value = "/queryClusterView", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public String queryClusterView(HttpServletRequest request, Integer clusterId) {
		List<Container> container_list = null;
		Container sel_container = new Container();
		try {
			container_list = containerService.listAllContainer(sel_container);
		} catch (Exception e) {
			LOGGER.error("query all container list failed", e);
		}
		if (container_list.isEmpty()) {
			return null;
		}

		List<Host> host_list = null;
		try {
			host_list = hostService.listHostByClusterId(clusterId);
		} catch (Exception e) {
			LOGGER.error("query all host list by clusterId:" + clusterId + " failed", e);
		}

		ArrayList<String> allZxConName_list = new ArrayList<String>();
		for (Container sin_container : container_list) {
			Integer host_id = sin_container.getHostId();
			for (Host sin_host : host_list) {
				if (sin_host.getHostId() == host_id) {
					String conName = "Container_" + sin_host.getHostIp() + "_"
							+ sin_container.getConUuid().substring(0, 12);
					allZxConName_list.add(conName);
				}
			}
		}

		return defaultMonitor.queryZxConItems(allZxConName_list);
	}
}

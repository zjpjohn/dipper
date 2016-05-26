package com.cmbc.devops.manager;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.cmbc.devops.constant.Status;
import com.cmbc.devops.constant.Type;
import com.cmbc.devops.entity.App;
import com.cmbc.devops.entity.Cluster;
import com.cmbc.devops.entity.Container;
import com.cmbc.devops.entity.Host;
import com.cmbc.devops.entity.LoadBalance;
import com.cmbc.devops.entity.Registry;
import com.cmbc.devops.service.AppService;
import com.cmbc.devops.service.ClusterService;
import com.cmbc.devops.service.ContainerService;
import com.cmbc.devops.service.HostService;
import com.cmbc.devops.service.LoadBalanceService;
import com.cmbc.devops.service.RegistryService;

@Component
public class DashboardManager {
	private static final Logger LOGGER = Logger.getLogger(DashboardManager.class);

	@Resource
	private HostService hostService;
	@Resource
	private ContainerService containerService;
	@Resource
	private ClusterService clusterService;
	@Resource
	private AppService appService;
	@Resource
	private LoadBalanceService loadbalanceService;
	@Resource
	private RegistryService registryService;

	public JSONObject getDashboardData() {
		JSONObject ret_dashboad_data = new JSONObject();
		/* 获取各种主机的数量 */
		Host host = new Host();
		List<Host> allHostList=new ArrayList<Host>();
		try {
			allHostList = hostService.loadAllHost(host);
		} catch (Exception e1) {
			LOGGER.error("load all host failed!", e1);
			return null;
		}
		/* 遍历主机列表，分别获取各项主机的数量信息 */
		int swarmhost_num = 0;
		int dockerhost_num = 0;
		int registry_num = 0;
		int nginxhost_num = 0;

		/* 获取各类型主机的标志值 */
		int swarm_type = Type.HOST.SWARM.ordinal();
		int docker_type = Type.HOST.DOCKER.ordinal();
		int registry_type = Type.HOST.REGISTRY.ordinal();
		int nginx_type = Type.HOST.NGINX.ordinal();

		for (Host each_host : allHostList) {
			int host_type = each_host.getHostType().intValue();
			if (host_type == swarm_type) {
				swarmhost_num++;
			} else if (host_type == docker_type) {
				dockerhost_num++;
			} else if (host_type == registry_type) {
				registry_num++;
			} else if (host_type == nginx_type) {
				nginxhost_num++;
			}
		}
		/* 想jsonobject中写入各项主机的数量 */
		ret_dashboad_data.put("SwarmHostNumber", swarmhost_num);
		ret_dashboad_data.put("DockerHostNumber", dockerhost_num);
		ret_dashboad_data.put("RegistryHostNumber", registry_num);
		ret_dashboad_data.put("NginxHostNumber", nginxhost_num);

		/* 获取所有容器的数量信息 */
		Container container = new Container();
		/* 定义容器的各种数量 */
		int container_total = 0;
		int container_running = 0;
		int container_stop = 0;

		List<Container> container_list = null;
		try {
			container_list = containerService.listAllContainerInApp(container);
		} catch (Exception e) {
			LOGGER.error("获取全部容器列表失败！", e);
			return null;
		}
		/* 读取容器的状态信息 */
		int con_running = Status.POWER.UP.ordinal();
		int con_stop = Status.POWER.OFF.ordinal();

		if (container_list != null) {
			for (Container each_container : container_list) {
				int container_power = each_container.getConPower().intValue();
				if (container_power == con_running) {
					container_total++;
					container_running++;
				} else if (container_power == con_stop) {
					container_total++;
					container_stop++;
				}
			}
		}

		ret_dashboad_data.put("ContainerTotal", container_total);
		ret_dashboad_data.put("ContainerRunning", container_running);
		ret_dashboad_data.put("ContainerStop", container_stop);

		/* 分别获取集群、应用、负载均衡、仓库各项数量信息 */
		int cluster_number = 0;
		int application_number = 0;
		int loadbalance_number = 0;
		int registry_number = 0;

		List<Cluster> cluster_list=new ArrayList<Cluster>();
		try {
			cluster_list = clusterService.listAllCluster();
		} catch (Exception e1) {
			LOGGER.error("get all cluster failed!", e1);
			return null;
		}
		if (cluster_list.size() > 0) {
			cluster_number = cluster_list.size();
		}
		List<App> application_list=new ArrayList<App>();
		try {
			application_list = appService.listAll();
		} catch (Exception e1) {
			LOGGER.error("list all application failed!", e1);
			return null;
		}
		if (application_list.size() > 0) {
			application_number = application_list.size();
		}
		LoadBalance lb = new LoadBalance();
		try {
			List<LoadBalance> lb_list = loadbalanceService.listAll(lb);
			if (lb_list.size() > 0) {
				loadbalance_number = lb_list.size();
			}
		} catch (Exception e) {
			LOGGER.error("获取全部负载均衡列表失败！", e);
			return null;
		}
		Registry regi = new Registry();
		List<Registry> regi_list=new ArrayList<Registry>();
		try {
			regi_list = registryService.loadAllRegistries(regi);
		} catch (Exception e) {
			LOGGER.error("load all registries falied!", e);
			return null;
		}
		if (regi_list.size() > 0) {
			registry_number = regi_list.size();
		}

		ret_dashboad_data.put("ClusterNumber", cluster_number);
		ret_dashboad_data.put("ApplicationNumber", application_number);
		ret_dashboad_data.put("LoadBalanceNumer", loadbalance_number);
		ret_dashboad_data.put("RegistryNumber", registry_number);

		return ret_dashboad_data;
	}
}

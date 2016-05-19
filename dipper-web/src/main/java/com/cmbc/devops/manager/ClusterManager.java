package com.cmbc.devops.manager;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.cmbc.devops.bean.GridBean;
import com.cmbc.devops.bean.Result;
import com.cmbc.devops.constant.Status;
import com.cmbc.devops.core.ClusterCore;
import com.cmbc.devops.core.HostCore;
import com.cmbc.devops.entity.Cluster;
import com.cmbc.devops.entity.ClusterResource;
import com.cmbc.devops.entity.ClusterWithHostContainerNum;
import com.cmbc.devops.entity.Container;
import com.cmbc.devops.entity.Host;
import com.cmbc.devops.entity.User;
import com.cmbc.devops.model.ClusterModel;
import com.cmbc.devops.service.ClusterAppService;
import com.cmbc.devops.service.ClusterResourceService;
import com.cmbc.devops.service.ClusterService;
import com.cmbc.devops.service.ContainerService;
import com.cmbc.devops.service.HostService;
import com.cmbc.devops.service.UserService;

/**
 * @author luogan 2015年8月17日 下午3:08:20
 */
@Component
public class ClusterManager {

	private static final Logger LOGGER = Logger.getLogger(ClusterManager.class);
	@Resource
	private ClusterService clusterService;
	@Resource
	private ClusterCore clusterCore;
	@Resource
	private HostCore hostCore;
	@Resource
	private HostService hostService;
	@Resource
	private ContainerService containerService;
	@Resource
	private UserService userService;
	@Resource
	private ClusterAppService clusterAppService;
	@Resource
	private ClusterResourceService clusterResourceService;

	/**
	 * @author yangqinglin
	 * @time 2015年10月14日
	 * @description 根据集群的ID
	 */
	public JSONObject getClusterTopoJson(Integer clusterId) {
		/* 根据应用的ID查询集群对象信息 */
		Cluster cluster = getCluster(clusterId);
		if (cluster == null) {
			return null;
		}
		// 删除前台不用的集群信息
		cluster.setClusterUuid(null);
		cluster.setClusterCreatetime(null);
		// 解析集群类型，转化为jsonobject对象
		JSONObject clusterJo = (JSONObject) JSONObject.toJSON(cluster);
		// 根据cluster的id信息查询所有属于cluster的主机
		List<Host> hosts = new ArrayList<Host>();
		try {
			hosts = hostService.listHostByClusterId(clusterId);
		} catch (Exception e1) {
			LOGGER.error("get host list by ClusterId failed", e1);
			return null;
		}
		/* 当返回的主机链表不为空的情况下，遍历主机链表 */
		if (hosts.isEmpty()) {
			return clusterJo;
		}
		for (Host host : hosts) {
			/* 删除前台不用的主机信息 */
			host.setHostUuid(null);
			host.setHostCreatetime(null);
			JSONObject hostJo = (JSONObject) JSONObject.toJSON(host);
			/* 添加容器的数量参数给Host对象 */
			int conNum = 0;
			List<Container> containers = null;
			try {
				containers = containerService.listContainersByHostId(host.getHostId());
			} catch (Exception e) {
				LOGGER.error("get Container list By HostId[" + host.getHostId() + "] falied!", e);
				continue;
			}
			/* 当容器链表不为空的情况下，遍历容器链表 */
			if (!containers.isEmpty()) {
				for (Container container : containers) {
					/* 删除前台不用的容器信息 */
					container.setConUuid(null);
					container.setConCreatetime(null);

					JSONObject containerJo = (JSONObject) JSONObject.toJSON(container);
					hostJo.put("container_" + container.getConId(), containerJo);
					/* 自增容器数量计数器 */
					conNum++;
				}
			}
			/* 添加主机对应的容器计数器的值 */
			hostJo.put("hostConCounter", conNum);
			clusterJo.put("host_" + host.getHostId(), hostJo);
		}
		return clusterJo;
	}

	//
	public Result createCluster(ClusterModel model) {

		// 1.可达测试校验主机节点2.检查swarm以及swarm的文件是否存在4.启动swarm5.成功后保存信息到dop_cluster表中
		Result result = new Result();
		Host host = new Host();
		host.setHostId(model.getMasteHostId());
		try {
			host = hostService.getHost(host);
		} catch (Exception e) {
			LOGGER.error("get host by hostId failed ", e);
			return new Result(false, "创建集群失败：获取主机信息失败！");
		}
		if (host == null) {
			LOGGER.warn("No host found");
			return new Result(false, "创建集群失败：集群依赖主机不存在，请重新选择！");
		}
		boolean swarmResult = clusterCore.createSwarm(host.getHostIp(), host.getHostUser(), host.getHostPwd(), model);
		if (swarmResult) {
			String clusterLogFile = "swarm_" + model.getClusterPort() + ".log";
			model.setLogFile(clusterLogFile);
			int createFlag = clusterService.createCluster(model);
			if (createFlag == 1) {
				LOGGER.info("Create cluster success");
				result = new Result(true, "创建集群：创建集群成功！");
			} else {
				result = new Result(false, "创建集群：操作数据库异常");
			}
		} else {
			LOGGER.error("Create cluster fail");
			result = new Result(false, "创建集群失败：swarm主机无法连接!");
		}
		return result;
	}

	public Result deleteCluster(int clusterId, int userId, int tenantId) {
		Result result = new Result(false, "");
		// 查询要删除集群的信息
		Cluster cluster = getCluster(clusterId);
		if (cluster == null) {
			result.setMessage("删除集群失败：未获取到集群信息");
			return result;
		}
		// 集群存在，先执行删除集群操作，首先判断集群是否与用户属于同一租户
		if (cluster.getTenantId().intValue() == tenantId) {
			int removeResult = clusterService.deleteCluster(clusterId);
			if (removeResult <= 0) {
				clusterService.update(cluster);
				LOGGER.info("Remove cluster failed");
				return new Result(false, "删除集群失败：数据库操作异常！");
			}
			List<Host> hosts = null;
			try {
				hosts = hostService.listHostByClusterId(clusterId);
			} catch (Exception e) {
				LOGGER.error("get host list by clusterId failed", e);
				clusterService.update(cluster);
				return new Result(false, "删除集群失败：获取集群中主机列表失败！");
			}
			if (!hosts.isEmpty()) {
				LOGGER.warn("Cluster can not remove");
				clusterService.update(cluster);
				return new Result(false, "删除集群失败：集群存在可用的主机，删除集群失败！");
			}
			Host host = new Host();
			host.setHostId(cluster.getMasteHostId());
			try {
				host = hostService.getHost(host);
			} catch (Exception e1) {
				clusterService.update(cluster);
				LOGGER.error("get host by hostId error", e1);
				return new Result(false, "删除集群失败：获取集群所在的主机信息失败！");
			}
			boolean stopResult = clusterCore.removeSwarm(host.getHostIp(), host.getHostUser(), host.getHostPwd(),
					cluster.getClusterPort(), cluster.getManagePath());
			if (stopResult) {
				LOGGER.info("Remove cluster success");
				return new Result(true, "删除集群：删除集群成功！");
			} else {
				clusterService.update(cluster);
				return new Result(false, "删除集群失败：获取集群主机链接失败！");
			}
		} else {
			result.setMessage("删除集群失败：用户与集群所在的租户ID不匹配");
			LOGGER.warn("删除集群失败：用户所在租户(ID:" + tenantId + ")与集群的租户(ID:" + cluster.getTenantId() + ")不匹配");
			return result;
		}

	}

	//
	public Result updateCluster(Integer userId, ClusterModel model) {
		try {
			Cluster cluster = new Cluster();
			BeanUtils.copyProperties(model, cluster);
			int result = clusterService.update(cluster);
			return result > 0 ? new Result(true, "修改集群成功！") : new Result(false, "修改集群失败！");
		} catch (Exception e) {
			LOGGER.error("Update host fail", e);
			return new Result(false, "修改集群失败：数据库保存失败！");
		}
	}

	// 集群健康检查
	public Result clusterHealthCheck(Integer clusterId) {
		Result message = null;
		Cluster cluster = null;
		try {
			cluster = clusterService.getCluster(clusterId);
		} catch (Exception e) {
			LOGGER.error("get cluster by cluster id failed", e);
			message = new Result(false, "查询集群信息失败！");
			return message;
		}
		// 通过clusterId去查找集群下主机的IP
		List<Host> lists = new ArrayList<Host>();
		try {
			lists = hostService.listHostByClusterId(clusterId);
		} catch (Exception e) {
			LOGGER.error("get host list by cluster id failed", e);
			message = new Result(false, "获取集群中主机列表失败！");
			return message;
		}
		Host host = new Host();
		host.setHostId(cluster.getMasteHostId());
		Host clusterHost = new Host();
		try {
			clusterHost = hostService.getHost(host);
		} catch (Exception e) {
			LOGGER.error("get host by hostid[" + cluster.getMasteHostId() + "] id failed", e);
			message = new Result(false, "查询Master主机信息失败！");
			return message;
		}
		//
		String responseString = clusterCore.clusterHealthCheck(clusterHost.getHostIp(), clusterHost.getHostUser(),
				clusterHost.getHostPwd(), cluster.getClusterPort());

		if ("false".equals(responseString)) {
			LOGGER.info("connect to host[" + clusterHost.getHostIp() + "] failed!");
			return new Result(false, "主机(" + clusterHost.getHostName() + ")连接失败，请检查网络是否正常");
		}
		String[] ips = responseString.split("\n");
		Iterator<Host> hostIterator = lists.iterator();
		while (hostIterator.hasNext()) {
			Host item = hostIterator.next();
			if (contains(ips, item.getHostIp())) {
				hostIterator.remove();
			}
		}
		if (lists.size() == 0) {
			LOGGER.info("Check cluster consistent");
			message = new Result(true, "集群健康，数据库和服务器一致！");
		} else {
			LOGGER.info("Check cluster inconsistent");
			message = new Result(false, "集群不健康,数据库和服务器不一致！");
		}
		return message;
	}

	public Result recoverCluster(JSONObject ja) {
		Result recoverResult = new Result();
		int clusterId = ja.getInteger("clusterId");
		Cluster cluster = null;
		try {
			cluster = clusterService.getCluster(clusterId);
		} catch (Exception e) {
			LOGGER.error("get cluster by cluster id failed", e);
			return new Result(false, "集群恢复:查询集群信息失败！");
		}
		Host host = new Host();
		host.setHostId(cluster.getMasteHostId());
		Host clusterHost = new Host();
		try {
			clusterHost = hostService.getHost(host);
		} catch (Exception e) {
			LOGGER.error("get host by host id failed", e);
			return new Result(false, "集群恢复:查询主机信息失败！");
		}
		Boolean result = clusterCore.recoverSwarm(clusterHost.getHostIp(), clusterHost.getHostUser(),
				clusterHost.getHostPwd(), cluster.getClusterPort(), cluster.getManagePath());
		if (result == null) {
			recoverResult = new Result(false, "集群恢复:集群主机(" + clusterHost.getHostName() + ")连接失败，请检查主机是否正常！");
		} else {
			if (result) {
				/** 集群恢复成功后，更新此集群的状态 */
				cluster.setClusterStatus((byte) Status.CLUSTER.NORMAL.ordinal());
				clusterService.update(cluster);
				recoverResult = new Result(true, "集群恢复:集群恢复成功！");
			} else {
				recoverResult = new Result(true, "集群恢复:集群运行正常，不需要重新恢复！");
			}
		}
		return recoverResult;
	}

	public ClusterWithHostContainerNum detail(Integer clusterId) {
		ClusterWithHostContainerNum clusterHC = new ClusterWithHostContainerNum();
		Cluster cluster = null;
		try {
			cluster = clusterService.getCluster(clusterId);
		} catch (Exception e1) {
			LOGGER.error("get cluster by cluster id failed", e1);
			return null;
		}

		/* 将查询的cluster所有属性全部添加到新对象clusterHC中 */
		clusterHC.setClusterId(cluster.getClusterId());
		clusterHC.setClusterUuid(cluster.getClusterUuid());
		clusterHC.setClusterName(cluster.getClusterName());
		clusterHC.setClusterType(cluster.getClusterType());
		clusterHC.setClusterStatus(cluster.getClusterStatus());
		clusterHC.setClusterPort(cluster.getClusterPort());
		clusterHC.setManagePath(cluster.getManagePath());
		clusterHC.setClusterDesc(cluster.getClusterDesc());
		clusterHC.setMasteHostId(cluster.getMasteHostId());
		clusterHC.setClusterCreatetime(cluster.getClusterCreatetime());
		clusterHC.setClusterCreator(cluster.getClusterCreator());

		/* 取得管理主机节点的IP地址 */
		Host master_host = new Host();
		master_host.setHostId(cluster.getMasteHostId());
		try {
			master_host = hostService.getHost(master_host);
		} catch (Exception e1) {
			LOGGER.error("get master host by host id failed", e1);
			return null;
		}
		clusterHC.setMasteHostIPaddr(master_host.getHostIp());

		/* 取得创建用户的名称 */
		User user = new User();
		user.setUserId(cluster.getClusterCreator());
		try {
			user = userService.getUser(user);
		} catch (Exception e2) {
			LOGGER.error("get user by userid[" + cluster.getClusterCreator() + "] falied!", e2);
			return null;
		}
		clusterHC.setCreateUsername(user.getUserName());

		/* 查询集群中主机的数量并添加 */
		List<Host> host_list = new ArrayList<Host>();
		try {
			host_list = hostService.listHostByClusterId(clusterId);
		} catch (Exception e1) {
			LOGGER.error("get host list by cluster id failed", e1);
			return null;
		}
		/* 设置集群中包含主机的数量 */
		clusterHC.setHostQuantity(host_list.size());
		int container_quantity = 0;

		for (Host host : host_list) {
			int host_id = host.getHostId();
			try {
				List<Container> container_list = containerService.listContainersByHostId(host_id);
				container_quantity = container_quantity + container_list.size();
			} catch (Exception e) {
				LOGGER.error("get container list by hostid[" + host_id + "] falied!", e);
				continue;
			}
		}

		clusterHC.setContainerQuantity(container_quantity);

		return clusterHC;
	}

	public boolean checkPort(JSONObject jo) {
		Integer hostId = jo.getInteger("hostId");
		String port = jo.getString("port");
		Host host = new Host();
		try {
			host = hostService.loadHost(hostId);
		} catch (Exception e) {
			LOGGER.error("load host by host id failed ", e);
			return false;
		}
		return clusterCore.checkPort(host.getHostIp(), host.getHostUser(), host.getHostPwd(), port);
	}

	public boolean checkName(String clusterName) {
		Cluster cluster = new Cluster();
		try {
			cluster = clusterService.getClusterByName(clusterName);
		} catch (Exception e) {
			LOGGER.error("get cluster by cluster name failed", e);
			return false;
		}
		if (cluster != null) {
			return false;
		} else {
			return true;
		}
	}

	public boolean checkClusterConfFile(String managePath) {
		Cluster cluster = new Cluster();
		try {
			cluster = clusterService.getClusterBymanagePath(managePath);
		} catch (Exception e) {
			LOGGER.error("get cluster by managerPath[" + managePath + "] failed", e);
			return false;
		}
		if (cluster != null) {
			return false;
		} else {
			return true;
		}
	}

	private boolean contains(String[] ips, String ip) {
		for (String temp : ips) {
			if (ip.equals(temp.trim())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @author youngtsinglin
	 * @time 2015年11月25日 14:48
	 * @description 将原来返回字符串的方法修改为GridBean的方式
	 */
	public GridBean clusterSearchAllList(int userId, int tenantId, int pageNum, int pageSize,
			ClusterModel clusterModel) {
		try {
			return clusterService.searchAllClusters(userId, tenantId, pageNum, pageSize, clusterModel);
		} catch (Exception e) {
			LOGGER.error("search all clusters failed.", e);
			return null;
		}
	}

	public GridBean advancedSearchCluster(Integer userId, Integer tenantId, int pagenumber, int pagesize,
			ClusterModel clusterModel, JSONObject json_object) {
		try {
			return clusterService.advancedSearchCluster(userId, tenantId, pagenumber, pagesize, clusterModel,
					json_object);
		} catch (Exception e) {
			LOGGER.error("advanced search cluser falied!", e);
			return null;
		}
	}

	private Cluster getCluster(int clusterId) {
		try {
			return clusterService.getCluster(clusterId);
		} catch (Exception e) {
			LOGGER.info("Get cluster infos error", e);
			return null;
		}
	}

	public int getMaxIns(int clusterId, int appCpu) {
		int sum = 0;
		if(appCpu!=0){
			List<ClusterResource> hostlist = new ArrayList<ClusterResource>();
			try {
				hostlist = clusterResourceService.findHostsByClusterId(clusterId);
			} catch (Exception e) {
				LOGGER.info("Get cluster max instance error", e);
			}
			if (!hostlist.isEmpty()) {
				for (ClusterResource cr : hostlist) {
					if (cr.getCpuNum() >= appCpu) {
						sum += cr.getCpuNum() / appCpu;
					}
				}
			}	
		}
		return sum;
	}
}
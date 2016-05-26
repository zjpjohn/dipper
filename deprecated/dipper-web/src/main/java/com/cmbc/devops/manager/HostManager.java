package com.cmbc.devops.manager;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cmbc.devops.bean.GridBean;
import com.cmbc.devops.bean.Result;
import com.cmbc.devops.config.SystemConfig;
import com.cmbc.devops.constant.Status;
import com.cmbc.devops.constant.Type;
import com.cmbc.devops.core.HostCore;
import com.cmbc.devops.entity.Cluster;
import com.cmbc.devops.entity.ClusterResource;
import com.cmbc.devops.entity.Container;
import com.cmbc.devops.entity.Host;
import com.cmbc.devops.entity.LoadBalance;
import com.cmbc.devops.entity.Registry;
import com.cmbc.devops.entity.Software;
import com.cmbc.devops.model.HostModel;
import com.cmbc.devops.service.ClusterResourceService;
import com.cmbc.devops.service.ClusterService;
import com.cmbc.devops.service.ContainerService;
import com.cmbc.devops.service.HostService;
import com.cmbc.devops.service.LoadBalanceService;
import com.cmbc.devops.service.RegistryService;
import com.cmbc.devops.service.SoftwareService;
import com.cmbc.devops.util.CommandExcutor;
import com.cmbc.devops.util.SSH;

/**
 * @author luogan 2015年8月17日 下午3:08:25
 */
@Component
public class HostManager {

	private static final Logger LOGGER = Logger.getLogger(HostManager.class);
	@Resource
	private HostService hostService;
	@Resource
	private ClusterService clusterService;
	@Resource
	private HostCore hostCore;
	@Resource
	private ContainerService containerService;
	@Resource
	private LoadBalanceService loadBalanceService;
	@Resource
	private RegistryService registryService;
	@Autowired
	private SystemConfig config;
	@Autowired
	private SoftwareService softwareService;
	@Autowired
	private ClusterResourceService clusterResourceService;

	public Result createHost(HostModel model) {
		// 1.获取连接
		SSH ssh = CommandExcutor.getSsh(model.getHostIp(), model.getHostUser(), model.getHostPwd());
		if (ssh == null) {
			return new Result(false, "添加主机失败：未获取主机链接，ip地址,用户名或者密码错误");
		}
		// 2.获取主机cpu，内存，内核版本的基础信息
		Result hostResult = hostCore.getHostInfo(ssh, model.getHostIp());
		if (!hostResult.isSuccess()) {
			return hostResult;
		}

		// 4.对信息集中处理
		String line = "";
		Host host = new Host();
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(new ByteArrayInputStream(hostResult.getMessage().getBytes())));
		try {
			while ((line = reader.readLine()) != null) {
				LOGGER.info("Get reader.readLine Info is :" + line);
				if (line.contains("hostName")) {
					host.setHostRealName(line.split(":")[1]);
				} else if (line.contains("Kernel")) {
					host.setHostKernelVersion(line.split(":")[1]);
				} else if (line.contains("CPU")) {
					host.setHostCpu(Integer.parseInt(line.split(":")[1]));
				} else if (line.contains("MEM")) {
					String memory = line.split(":")[1];
					host.setHostMem(Float.valueOf(memory.substring(0, memory.length() - 3)).intValue());
				}
			}
			host.setHostUuid(UUID.randomUUID().toString());
			host.setHostName(model.getHostName());
			host.setHostIp(model.getHostIp());
			host.setHostUser(model.getHostUser());
			host.setHostPwd(model.getHostPwd());
			host.setHostType(model.getHostType().byteValue());
			host.setHostStatus((byte) Status.HOST.NORMAL.ordinal());
			host.setHostDesc(model.getHostDesc());
			host.setHostCreator(model.getCreator());
			host.setHostCreatetime(new Date());
			if (hostService.createHost(host) > 0) {
				return new Result(true, "添加主机成功！");
			} else {
				return new Result(false, "添加主机失败：数据库操作异常！");
			}
		} catch (IOException e) {
			LOGGER.error("Read hostInfos error", e);
			return new Result(false, "添加主机失败：读取服务器信息异常！");
		} catch (Exception e1) {
			LOGGER.error("Read hostInfos error", e1);
			return new Result(false, "添加主机失败：数据库操作异常！");
		}
	}

	//
	public Result deleteHost(HostModel model) {
		// 删除主机标识（0：删除失败，>0删除成功）
		int removeNum = 0;
		// 1.查找要删除的主机
		Host host = null;
		Integer hostId = model.getHostId();
		try {
			host = hostService.loadHost(hostId);
		} catch (Exception e1) {
			LOGGER.error("Remove host fail", e1);
			return new Result(false, "获取删除主机" + host.getHostName() + "信息异常！");
		}
		if (host == null) {
			return new Result(false, "未获取到删除主机的信息！");
		} else {
			// 如果主机存在，则先删除数据库
			try {
				removeNum = hostService.deleteHost(hostId);
				if (removeNum <= 0) {
					LOGGER.info("Remove host fail");
					return new Result(false, "删除主机" + host.getHostName() + "失败！");
				}
			} catch (Exception e) {
				LOGGER.error("Remove host fail", e);
				return new Result(false, "删除主机" + host.getHostName() + "数据异常！");
			}
		}
		// 2、根据不同类型主机，校验是否满足删除条件
		Result result = checkRemoveHost(host);
		// 3.满足条件，删除主机
		if (result.isSuccess()) {
			LOGGER.info("Remove host success");
			return new Result(true, "删除主机成功！");
		} else {
			// 如果后台删除失败，进行回滚操作
			hostService.update(host);
			LOGGER.warn("Remove host fail");
			return result;
		}
	}

	//
	public Result deleteHosts(int[] hostIds) {
		// 获取删除主机的信息
		List<Integer> removeHostIds = new ArrayList<>();
		String removeHostNames = "", hostNames = "";
		for (Integer hostId : hostIds) {
			Host host = new Host();
			host.setHostId(hostId);
			try {
				host = hostService.getHost(host);
			} catch (Exception e) {
				LOGGER.error("Get remove host infos failed", e);
			}
			if (host == null) {
				continue;
			}
			/* 判断主机是否符合删除的条件 */
			Result result = checkRemoveHost(host);
			if (!result.isSuccess()) {
				hostNames += host.getHostName() + " ";
				continue;
			}
			LOGGER.info("Check remove host success");
			removeHostNames += host.getHostName() + " ";
			removeHostIds.add(hostId);
		}
		if (removeHostIds.isEmpty()) {
			LOGGER.info("There is no host allowed to remove");
			return new Result(false, "删除主机失败：删除的主机" + hostNames + "存在未清空的数据，不允许删除！");
		}
		int removeNum = hostService.deleteHosts(removeHostIds);
		if (removeNum <= 0) {
			return new Result(false, "删除主机失败：删除主机" + removeHostNames + "数据库操作失败");
		}
		LOGGER.info("Remove host success");
		return new Result(true, "删除主机成功");
	}

	/**
	 * @author langzi
	 * @param jo
	 * @version 1.0 2015年10月23日
	 */
	public Result addHost2Cluster(JSONObject jo) {
		// 1.获取集群主机
		Integer clusterHostId = jo.getInteger("clusterHostId");
		Integer clusterId = jo.getInteger("clusterId");
		Host clusterHost = null;
		try {
			clusterHost = hostService.loadHost(clusterHostId);
			if (clusterHost == null) {
				return new Result(false, "主机加入集群失败：未获取到集群所在服务器信息");
			}
		} catch (Exception e) {
			LOGGER.error("load host error", e);
			return new Result(false, "查询集群中主机失败！");
		}

		Cluster cluster = null;
		try {
			cluster = clusterService.getCluster(clusterId);
		} catch (Exception e1) {
			LOGGER.error("load cluster error", e1);
			return new Result(false, "获取集群信息异常！");
		}
		if (cluster == null) {
			return new Result(false, "主机加入集群失败：未获取到集群信息");
		}
		// 2.获取配置文件和加入主机信息
		String file = cluster.getManagePath();
		String[] hostIds = jo.getString("hosts").split(",");
		StringBuffer ipRoutes = new StringBuffer();
		for (String hostId : hostIds) {
			try {
				Host host = hostService.loadHost(Integer.parseInt(hostId.trim()));
				if (host != null) {
					// 先更新数据库
					host.setClusterId(clusterId);
					hostService.update(host);
					ipRoutes.append(host.getHostIp()).append(":" + config.getDockerPort()).append("\n");
				}
			} catch (Exception e) {
				LOGGER.error("load host error", e);
				return new Result(false, "查询集群中主机失败！");
			}
		}
		if (ipRoutes.toString().equals("")) {
			Host host = new Host();
			for (String hostId : hostIds) {
				try {
					host = hostService.loadHost(Integer.parseInt(hostId.trim()));
				} catch (Exception e) {
					LOGGER.error("load host error", e);
					return new Result(false, "添加集群：获取主机信息失败！");
				}
				if (host != null) {
					host.setClusterId(null);
					hostService.update(host);
				}
			}
			return new Result(false, "主机加入集群失败：加入到集群的主机不存在！");
		}
		// 3. 获取ssh
		SSH ssh = CommandExcutor.getSsh(clusterHost.getHostIp(), clusterHost.getHostUser(), clusterHost.getHostPwd());
		if (ssh == null) {
			Host host = new Host();
			for (String hostId : hostIds) {
				try {
					host = hostService.loadHost(Integer.parseInt(hostId.trim()));
				} catch (Exception e) {
					LOGGER.error("load host error", e);
					return new Result(false, "添加集群：获取主机信息失败！");
				}
				if (host != null) {
					host.setClusterId(null);
					hostService.update(host);
				}
			}
			return new Result(false, "主机加入集群失败：获取服务器连接失败");
		}
		// 物理加入
		Result result = hostCore.addHostToCluster(ssh, clusterHost.getHostUser(), file, ipRoutes.toString(),
				cluster.getClusterMode(), cluster.getClusterPort(), cluster.getClusterLogFile());
		// 3.根据返回结果，更新数据库
		if (result.isSuccess()) {
			// 如果集群资源独享，添加主机资源限制
			if (cluster.getResType() == Type.CLUSTER_RES.PRIVATE.ordinal()) {
				Host hostinfo = new Host();
				for (String hostId : hostIds) {
					try {
						hostinfo = hostService.loadHost(Integer.parseInt(hostId.trim()));
						ClusterResource record = new ClusterResource(null, hostinfo.getHostId(), null, clusterId, null);
						for (int i = 0; i < hostinfo.getHostCpu(); i++) {
							record.setCpuId(i);
							clusterResourceService.insert(record);
						}
					} catch (Exception e) {
						LOGGER.error("load host error", e);
						result = new Result(false, "添加集群：添加集群主机资源限制失败！");
					}
				}
			}
		}

		if (!result.isSuccess()) {
			Host host = new Host();
			for (String hostId : hostIds) {
				try {
					host = hostService.loadHost(Integer.parseInt(hostId.trim()));
				} catch (Exception e) {
					LOGGER.error("load host error", e);
					return new Result(false, "添加集群：获取主机信息失败！");
				}
				if (host != null) {
					host.setClusterId(null);
					hostService.update(host);
				}
			}
			LOGGER.error("add to cluster falied");
		} else {
			result.setMessage("主机添加到集群成功！");
		}
		return result;
	}

	/**
	 * @author langzi
	 * @param clusterId
	 * @param hostId
	 * @return
	 * @version 1.0 2015年11月27日
	 */
	public Result ClusterAddHost(Integer clusterId, Integer hostId) {
		// 1获取集群信息
		Cluster cluster = null;
		try {
			cluster = clusterService.getCluster(clusterId);
		} catch (Exception e) {
			LOGGER.error("Get cluster infos error", e);
			return new Result(false, "获取集群信息异常");
		}
		if (cluster == null) {
			return new Result(false, "未获取到集群信息");
		}
		// 2.获取集群所在主机信息
		Host masterHost = null;
		try {
			masterHost = hostService.loadHost(cluster.getMasteHostId());
		} catch (Exception e) {
			LOGGER.error("Get masterHost info error", e);
			return new Result(false, "获取集群所在服务器信息异常");
		}
		if (masterHost == null) {
			return new Result(false, "未获取到集群所在服务器信息");
		}
		// 3.获取要添加的主机的信息
		Host slaveHost = null;
		try {
			slaveHost = hostService.loadHost(hostId);
		} catch (Exception e) {
			LOGGER.error("Get slave host infos error", e);
			return new Result(false, "获取加入集群的主机信息异常！");
		}
		if (slaveHost == null) {
			return new Result(false, "未获取加入集群的主机信息！");
		}
		// 更新数据库
		slaveHost.setClusterId(clusterId);
		int updateResult = hostService.update(slaveHost);
		if (updateResult <= 0) {
			return new Result(false, "集群添加主机失败：更新加入主机信息失败！");
		}
		// 5.构建配置文件信息信息
		String ipRoute = slaveHost.getHostIp() + ":" + config.getDockerPort() + "\n";
		// 4.获取集群主机链接
		SSH ssh = CommandExcutor.getSsh(masterHost.getHostIp(), masterHost.getHostUser(), masterHost.getHostPwd());
		if (ssh == null) {
			slaveHost.setClusterId(null);
			hostService.update(slaveHost);
			return new Result(false, "集群添加主机失败：未获取到集群所在服务器的连接！");
		}
		String configFile = cluster.getManagePath();
		int clusterMode = cluster.getClusterMode();
		String clusterPort = cluster.getClusterPort();
		String logFile = cluster.getClusterLogFile();
		// 6.主机加入集群
		Result result = hostCore.addHostToCluster(ssh, masterHost.getHostUser(), configFile, ipRoute, clusterMode,
				clusterPort, logFile);
		if (!result.isSuccess()) {
			// 7.写入数据库
			slaveHost.setClusterId(null);
			hostService.update(slaveHost);
			return result;
		}
		return new Result(true, "集群添加主机成功！");
	}

	/**
	 * @author yangqinglin
	 * @param clusterId
	 * @param hostId
	 * @return
	 * @version 1.0 2016年4月18日
	 */
	public Result addManyHost(Integer clusterId, String hostIds) {
		// 1获取集群信息
		Cluster cluster = null;
		try {
			cluster = clusterService.getCluster(clusterId);
		} catch (Exception e) {
			LOGGER.error("Get cluster infos error", e);
			return new Result(false, "获取集群信息异常");
		}
		if (cluster == null) {
			return new Result(false, "未获取到集群信息");
		}
		// 2.获取集群所在主机信息
		Host masterHost = null;
		try {
			masterHost = hostService.loadHost(cluster.getMasteHostId());
		} catch (Exception e) {
			LOGGER.error("Get masterHost info error", e);
			return new Result(false, "获取集群所在服务器信息异常");
		}
		if (masterHost == null) {
			return new Result(false, "未获取到集群所在服务器信息");
		}

		if (!org.springframework.util.StringUtils.hasText(hostIds)) {
			return new Result(false, "请求的主机链表信息为空，请核对后添加。");
		}

		/** 将主机的ID字符串分割 */
		String[] hostIdArray = hostIds.split(",");

		List<HostModel> hostList = null;
		try {
			hostList = hostService.getHostModelByIds(hostIdArray);
			if (hostList.isEmpty()) {
				return new Result(false, "未获取加入集群的主机列表信息！");
			}
		} catch (Exception e) {
			LOGGER.error("Get host list infos error", e);
			return new Result(false, "获取加入集群的主机列表信息异常！");
		}

		// 4.获取集群主机链接
		SSH ssh = CommandExcutor.getSsh(masterHost.getHostIp(), masterHost.getHostUser(), masterHost.getHostPwd());
		if (ssh == null) {
			return new Result(false, "集群添加主机失败：未获取到集群所在服务器的连接！");
		}

		ArrayList<Integer> succHostIds = new ArrayList<Integer>();
		ArrayList<String> retInfoString = new ArrayList<String>();
		for (HostModel hostModel : hostList) {
			// 5.构建配置文件信息信息
			String ipRoute = hostModel.getHostIp() + ":" + config.getDockerPort() + "\n";
			String configFile = cluster.getManagePath();
			int clusterMode = cluster.getClusterMode();
			String clusterPort = cluster.getClusterPort();
			String logFile = cluster.getClusterLogFile();

			boolean flag = false;
			try {
				flag = ssh.connect();
			} catch (Exception e) {
				LOGGER.error("host " + hostModel.getHostName() + " line Exception！");
			}

			// 6.主机加入集群
			if (flag) {
				Result result = hostCore.addHostToCluster(ssh, masterHost.getHostUser(), configFile, ipRoute,
						clusterMode, clusterPort, logFile);

				if (result.isSuccess()) {
					// 如果集群资源独享，添加主机资源限制
					if (cluster.getResType() == Type.CLUSTER_RES.PRIVATE.ordinal()) {
						try {
							ClusterResource record = new ClusterResource(null, hostModel.getHostId(), null, clusterId,
									null);
							for (int i = 0; i < hostModel.getHostCpu(); i++) {
								record.setCpuId(i);
								clusterResourceService.insert(record);
							}
						} catch (Exception e) {
							LOGGER.error(" insert cluster_resouse: HostId[" + hostModel.getHostId() + "] ClusterId["
									+ clusterId + "] hostCPU[" + hostModel.getHostCpu() + "]", e);
							result = new Result(false, "添加集群：添加集群主机资源限制失败！");
						}
					}
					succHostIds.add(hostModel.getHostId());
					retInfoString.add(hostModel.getHostName() + ",");
				}
			}

		}

		/** 批量更新主机信息，划入到集群中 */
		try {
			hostService.updateHostInCluster(clusterId, succHostIds);
		} catch (Exception e) {
			LOGGER.error("update host in cluster clusterId:[" + clusterId + "]  hostids:["
					+ StringUtils.join(succHostIds, ",") + "]", e);
			return new Result(false, "更新集群信息失败！");
		}
		ssh.close();
		if ((succHostIds.size() == hostIdArray.length)) {
			return new Result(true, "向集群内添加全部主机成功。");
		} else {
			return new Result(false, "向集群内添加部分主机" + retInfoString.subList(0, (retInfoString.size() - 1)) + "成功。");
		}

	}

	/**
	 * @author langzi
	 * @param jo
	 * @return
	 * @version 1.0 2015年10月23日
	 */
	public Result removeHostFromCluster(JSONObject jo) {
		// 1.获取ssh
		Integer clusterId = Integer.parseInt(jo.getString("clusterId"));
		Cluster cluster = null;
		try {
			cluster = clusterService.getCluster(clusterId);
		} catch (Exception e2) {
			LOGGER.error("load cluster error", e2);
			return new Result(false, "获取集群信息异常！");
		}
		if (cluster == null) {
			return new Result(false, "主机移除集群失败：未获取到集群信息");
		}
		// 查找集群主机信息
		Host clusterHost = null;
		try {
			clusterHost = hostService.loadHost(cluster.getMasteHostId());
		} catch (Exception e1) {
			LOGGER.error("load host error", e1);
			return new Result(false, "获取集群主机信息异常！");
		}
		if (clusterHost == null) {
			return new Result(false, "主机移除集群失败：未获取到集群" + cluster.getClusterName() + "所在服务器信息");
		}
		String[] hostIds = null;
		Result result = null;

		// 2.传递连接的参数
		String file = cluster.getManagePath();

		List<Host> hosts = new ArrayList<Host>();
		List<Host> removeHosts = new ArrayList<Host>();
		try {
			hosts = hostService.listHostByClusterId(clusterId);
		} catch (Exception e1) {
			LOGGER.error("get hosts by cluster error", e1);
			return new Result(false, "获取集群中主机列表异常！");
		}
		if (hosts.isEmpty()) {
			return new Result(false, "主机移出集群失败：集群中不存在任何主机");
		}
		hostIds = jo.getString("hosts").split(",");
		StringBuffer ipRoutes = new StringBuffer();
		// 检查移除集群的主机中，是否含有容器，有不让移出，没有可以移出
		int conNumber = 0;
		String hostName = "";
		for (Host host : hosts) {
			if (contains(hostIds, host.getHostId())) {
				List<Container> container_list;
				try {
					container_list = containerService.listContainersByHostId(host.getHostId());
					conNumber += container_list != null ? container_list.size() : 0;
				} catch (Exception e) {
					LOGGER.error("Get container infos of hosts failed", e);
					return new Result(false, host.getHostName() + "获取应用实例信息异常！");
				}
				if (!container_list.isEmpty()) {
					hostName += host.getHostName() + " ";
				}
				removeHosts.add(host);
			} else {
				continue;
			}
		}
		if (conNumber > 0) {
			return new Result(false, "主机" + hostName + "上存在容器，不允许移出集群！");
		}
		// 先更改数据库
		if (!removeHosts.isEmpty()) {
			for (Host removeHost : removeHosts) {
				removeHost.setClusterId(null);
				hostService.update(removeHost);
			}
		}
		SSH ssh = CommandExcutor.getSsh(clusterHost.getHostIp(), clusterHost.getHostUser(), clusterHost.getHostPwd());
		if (ssh == null) {
			for (Host host : removeHosts) {
				host.setClusterId(clusterId);
				hostService.update(host);
			}
			return new Result(false, "主机移除集群失败：未获取到集群服务器连接");
		}
		if (cluster.getClusterMode() == Type.CLUSTER_MODE.CONFIG.ordinal()) {
			hosts.removeAll(removeHosts);
			if (!hosts.isEmpty()) {
				for (Host host : hosts) {
					ipRoutes.append(host.getHostIp()).append(":" + config.getDockerPort()).append("\n");
				}
			} else {
				ipRoutes.append("");
			}
			LOGGER.info("removeHostFromCluster ipRoutes.toString():" + ipRoutes.toString());
			result = hostCore.removeHostFromCluster(ssh, clusterHost.getHostUser(), file, ipRoutes.toString());
		} else {
			String ipRoute = "";
			for (Host host : removeHosts) {
				ipRoute += host.getHostIp() + ":" + config.getDockerPort() + "\n";
				result = hostCore.removeHostFromCluster(ssh, ipRoute);
			}
		}

		if (result.isSuccess()) {
			// 删除资源限制数据
			if (cluster.getResType() == Type.CLUSTER_RES.PRIVATE.ordinal()) {
				for (Host host : removeHosts) {
					try {
						clusterResourceService.deleteByHostIdAndClusterId(host.getHostId(), clusterId);
					} catch (Exception e) {
						LOGGER.error(" delete cluster_resouse By HostId[" + host.getHostId() + "] And ClusterId["
								+ clusterId + "]", e);
						result = new Result(false, "主机移除集群失败：解除集群主机资源限制失败！");
					}
				}
			}
		}

		// 3.根据返回结果，是否跟新回来
		if (!result.isSuccess()) {
			for (Host host : removeHosts) {
				host.setClusterId(clusterId);
				hostService.update(host);
			}
		}
		return result;
	}

	/**
	 * @author langzi
	 * @param clusterId
	 * @param hostId
	 * @version 1.0 2015年11月27日
	 */
	public Result clusterRemoveHost(Integer clusterId, String[] hostIds) {
		// 记录移出成功的主机和未成功的主机
		List<String> removeSuccess = new ArrayList<String>();
		List<String> removeFailed = new ArrayList<String>();
		// 1获取集群信息
		Cluster cluster = null;
		try {
			cluster = clusterService.getCluster(clusterId);
		} catch (Exception e) {
			LOGGER.error("Get cluster infos error", e);
			return new Result(false, "集群解绑主机异常：未获取到集群信息");
		}
		if (cluster == null) {
			return new Result(false, "集群解绑主机失败：未获取到集群信息");
		}
		// 2.获取集群所在主机信息
		Host masterHost = null;
		try {
			masterHost = hostService.loadHost(cluster.getMasteHostId());
		} catch (Exception e) {
			LOGGER.error("Get masterHost info error", e);
			return new Result(false, "集群解绑主机异常：未获取到集群所在主机信息");
		}
		if (masterHost == null) {
			return new Result(false, "集群解绑主机失败：未获取到集群所在主机信息");
		}
		// 3.获取集群下所有的主机信息
		List<Host> slaveHostList = new ArrayList<Host>();
		try {
			slaveHostList = hostService.listHostByClusterId(clusterId);
		} catch (Exception e) {
			LOGGER.error("Get slaveHost infos error", e);
			return new Result(false, "集群解绑主机异常：未获取到集群内所有主机信息");
		}
		if (slaveHostList.isEmpty()) {
			return new Result(false, "集群解绑主机失败：未获取到集群内所有主机信息");
		}
		for (String hostinfoId : hostIds) {
			Integer hostId = Integer.valueOf(hostinfoId);
			// 5.获取要移除的主机
			Host removeHost = null;
			try {
				removeHost = hostService.loadHost(hostId);
			} catch (Exception e) {
				LOGGER.error("Get remove host infos error", e);
				return new Result(false, "集群解绑主机异常：获取移出集群主机的信息异常");
			}
			if (removeHost == null) {
				return new Result(false, "集群解绑主机失败：未获取移出集群的主机信息");
			}
			removeHost.setClusterId(null);
			int updateResult = hostService.update(removeHost);
			if (updateResult <= 0) {
				return new Result(false, "集群解绑主机失败：更新解绑主机的信息失败");
			}
			// 6.检查是否存在容器
			List<Container> containers = null;
			try {
				containers = containerService.listContainersByHostId(hostId);
			} catch (Exception e) {
				LOGGER.error("Get remove host's containers error", e);
				return new Result(false, "集群解绑主机失败：获取集群上应用实例信息异常");
			}
			int conNum = containers != null ? containers.size() : 0;
			if (conNum > 0) {
				removeHost.setClusterId(clusterId);
				hostService.update(removeHost);
				return new Result(false, "集群解绑主机失败：主机" + removeHost.getHostName() + "上存在应用实例，不允许移除集群！");
			}
			// 7.获取集群主机链接
			SSH ssh = CommandExcutor.getSsh(masterHost.getHostIp(), masterHost.getHostUser(), masterHost.getHostPwd());
			if (ssh == null) {
				removeHost.setClusterId(clusterId);
				hostService.update(removeHost);
				return new Result(false, "集群解绑主机失败：获取集群主机链接失败！");
			}
			// 8.构建配置文件信息信息
			Result result = null;
			if (Type.CLUSTER_MODE.CONFIG.ordinal() == cluster.getClusterMode()) {
				String ipRoute = "";
				for (Host slaveHost : slaveHostList) {
					if (!slaveHost.getHostId().equals(removeHost.getHostId())) {
						ipRoute += slaveHost.getHostIp() + ":" + config.getDockerPort() + "\n";
					}
				}
				String confFile = cluster.getManagePath();
				// 9移除配置文件
				result = hostCore.removeHostFromCluster(ssh, masterHost.getHostUser(), confFile, ipRoute);
			} else {
				String ipRoutes = removeHost.getHostIp() + ":" + config.getDockerPort() + "\n";
				result = hostCore.removeHostFromCluster(ssh, ipRoutes);
			}

			if (result.isSuccess()) {
				// 删除资源限制数据
				if (cluster.getResType() == Type.CLUSTER_RES.PRIVATE.ordinal()) {
					try {
						clusterResourceService.deleteByHostIdAndClusterId(removeHost.getHostId(), clusterId);
					} catch (Exception e) {
						LOGGER.error("主机：[" + removeHost.getHostId() + "]，集群：[" + clusterId + "]解除主机资源限制失败！", e);
						result = new Result(false, "主机移除集群失败：解除集群主机资源限制失败！");
					}
				}
			}

			if (!result.isSuccess()) {
				// 回滚数据库
				removeHost.setClusterId(clusterId);
				hostService.update(removeHost);
				removeFailed.add(removeHost.getHostName());
			} else {
				removeSuccess.add(removeHost.getHostName());
			}
		}
		String message = "";
		if (removeSuccess.size() > 0) {
			message += "主机：" + StringUtils.join(removeSuccess, " , ") + "移出集群成功！</br>";
		}
		if (removeFailed.size() > 0) {
			message += "主机：" + StringUtils.join(removeFailed, " , ") + "移出集群失败！";
		}
		return new Result(true, message);
	}

	/**
	 * @author langzi
	 * @param hostName
	 * @return
	 * @version 1.0 2015年10月21日
	 */
	public boolean checkHostName(String hostName) {
		try {
			return hostService.getHostByName(hostName) == null ? true : false;
		} catch (Exception e) {
			LOGGER.error("get host by hostName[" + hostName + "] failed", e);
			return false;
		}
	}

	/**
	 * @author langzi
	 * @param hostIp
	 * @return
	 * @version 1.0 2015年10月21日
	 */
	public boolean checkHostIp(String hostIp, Integer hostType) {
		Host checkHost = new Host();
		checkHost.setHostIp(hostIp);
		checkHost.setHostType(hostType.byteValue());
		try {
			return hostService.getHostByIp(checkHost) == null ? true : false;
		} catch (Exception e) {
			LOGGER.error("check host ip failed", e);
			return false;
		}
	}

	//
	public Result updateHost(Integer userId, HostModel model) {
		try {
			// 1.获取修改主机信息
			Host host = hostService.loadHost(model.getHostId());
			if (host == null) {
				return new Result(false, "修改主机失败：获取主机信息失败！");
			}
			host.setHostName(model.getHostName());
			host.setHostDesc(model.getHostDesc());
			int result = hostService.update(host);
			return result > 0 ? new Result(true, "修改主机成功！") : new Result(false, "修改主机失败！");
		} catch (Exception e) {
			LOGGER.error("Update host fail", e);
			return new Result(false, "修改主机失败：数据持久化异常！");
		}
	}

	public Host detail(int host_id) {
		Host host = new Host();
		host.setHostId(host_id);
		try {
			return hostService.getHost(host);
		} catch (Exception e) {
			LOGGER.error("get Host by hostId[" + host_id + "] failed ", e);
			return null;
		}
	}

	private boolean contains(String[] ids, int id) {
		for (String temp : ids) {
			if (Integer.valueOf(temp) == id) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @author langzi
	 * @param host
	 * @return
	 * @version 1.0 2015年10月29日
	 */
	private Result checkRemoveHost(Host host) {
		boolean result = false;
		String message = "";
		String hostName = host.getHostName();
		Integer hostType = Integer.parseInt(host.getHostType().toString());
		Integer hostId = host.getHostId();
		if (hostType == Type.HOST.SWARM.ordinal()) {
			List<Cluster> listClus = new ArrayList<Cluster>();
			try {
				listClus = clusterService.listClustersByhostId(hostId);
			} catch (Exception e) {
				LOGGER.error("get Clusters by hostId failed", e);
				return new Result(false, "获取主机" + hostName + "上的集群列表异常！");
			}
			if (listClus.size() == 0) {
				result = true;
			} else {
				message = "主机(" + hostName + ")上存在集群，不允许删除";
			}
		} else if (hostType == Type.HOST.DOCKER.ordinal()) {
			List<Container> listCons = null;
			try {
				listCons = containerService.listContainersByHostId(hostId);
			} catch (Exception e) {
				LOGGER.error("List contaier fail", e);
				return new Result(false, "获取主机" + hostName + "上的容器信息异常");
			}
			if (listCons.size() == 0) {
				result = true;
			} else {
				message = "主机(" + hostName + ")上存在未销毁的容器，不允许删除";
			}
			if (host.getClusterId() != null && host.getClusterId() > 0) {
				message = "主机(" + hostName + ")存在于集群中，请先解绑，再删除";
			} else {
				result = true;
			}
		} else if (hostType == Type.HOST.REGISTRY.ordinal()) {
			List<Registry> listRegs = null;
			try {
				listRegs = registryService.listRegistrysByHostId(hostId);
			} catch (Exception e) {
				LOGGER.error("List registry fail", e);
				return new Result(false, "获取主机" + hostName + "上的仓库信息异常");
			}
			if (listRegs.size() == 0) {
				result = true;
			} else {
				message = "主机(" + hostName + ")上存在运行的仓库，不允许删除";
			}
		} else if (hostType == Type.HOST.NGINX.ordinal()) {
			List<LoadBalance> listLbs = null;
			try {
				listLbs = loadBalanceService.listLoadBalanceByHostId(hostId);
			} catch (Exception e) {
				LOGGER.error("List loadBalance fail", e);
			}
			if (listLbs.size() == 0) {
				result = true;
			} else {
				message = "主机(" + hostName + ")上存在运行的负载均衡，不允许删除！";
			}
		} else {
			result = true;
			LOGGER.warn("Host hostTpye is not exist");
		}
		return new Result(result, message);
	}

	public GridBean advancedSearchHost(Integer userId, int pagenumber, int pagesize, HostModel hostModel,
			JSONObject json_object) {
		try {
			return hostService.advancedSearchHost(userId, pagenumber, pagesize, hostModel, json_object);
		} catch (Exception e) {
			LOGGER.error("advanced Search Host failed", e);
			return null;
		}
	}

	public JSONObject checkHostId(Integer host_id) {
		try {
			Host host = hostService.loadHost(host_id);
			if (host != null) {
				JSONObject json_object = (JSONObject) JSONObject.toJSON(host);
				json_object.put("success", true);
				return json_object;
			}
		} catch (Exception e) {
			LOGGER.error("Search Host(ID:" + host_id + ") failed", e);
		}
		return null;
	}

	/** 根据集群的ID获取此集群下的所有主机列表JSONArray **/
	public JSONArray hostList(int clusterId) {
		try {
			List<Host> hostList = hostService.listHostByClusterId(clusterId);
			if (!hostList.isEmpty()) {
				JSONArray ja = (JSONArray) JSONArray.toJSON(hostList);
				return ja;
			}
		} catch (Exception e) {
			LOGGER.error("Search hostList by clusterId(ID:" + clusterId + ") failed", e);
		}
		return null;
	}

	public String getSoftsByHostId(Integer hostId) {
		String softName = "";
		try {
			List<Software> softlist = softwareService.getListByHostId(hostId);
			if (!softlist.isEmpty()) {
				String[] softNames = new String[softlist.size()];
				for (int i = 0; i < softlist.size(); i++) {
					softNames[i] = softlist.get(i).getSwName() + "-" + softlist.get(i).getSwVersion();
				}
				softName = StringUtils.join(softNames, " , ");
			}
		} catch (Exception e) {
			LOGGER.error("Search softlist by hostId(ID:" + hostId + ") failed", e);
			return "";
		}
		return softName;
	}

	/** 向目标主机请求一个当前没有占用的端口 */
	public String getFreePort(Integer hostId) {

		/* (1)获取远程的主机对象 */
		Host selHost = new Host();
		selHost.setHostId(hostId);
		try {
			selHost = hostService.getHost(selHost);
		} catch (Exception e) {
			LOGGER.error("Select host by hostId(ID:" + hostId + ") failed", e);
			/** 查询主机失败 */
			return "dbfail";
		}

		try {
			/* (2)获取远程主机连接 */
			SSH ssh = CommandExcutor.getSsh(selHost.getHostIp(), selHost.getHostUser(), selHost.getHostPwd());
			if (ssh == null) {
				LOGGER.error("未获取主机链接，ip地址,用户名或者密码错误");
				/** 无法获取远程主机连接 */
				return "sshfail";
			}
			// 2.获取主机cpu，内存，内核版本的基础信息
			return hostCore.getFreePort(ssh);
		} catch (Exception e) {
			LOGGER.error("未获取主机链接，请检查对应管理节点主机。", e);
			return "exception";
		}
	}
}

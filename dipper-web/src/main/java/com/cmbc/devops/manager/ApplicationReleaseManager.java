package com.cmbc.devops.manager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cmbc.dap.monitor.client.Monitor;
import com.cmbc.devops.bean.MessageResult;
import com.cmbc.devops.bean.Result;
import com.cmbc.devops.config.SystemConfig;
import com.cmbc.devops.constant.Status;
import com.cmbc.devops.constant.Type;
import com.cmbc.devops.core.ApplicationReleaseCore;
import com.cmbc.devops.core.ContainerCore;
import com.cmbc.devops.entity.App;
import com.cmbc.devops.entity.Cluster;
import com.cmbc.devops.entity.ClusterResource;
import com.cmbc.devops.entity.ConPort;
import com.cmbc.devops.entity.Container;
import com.cmbc.devops.entity.Env;
import com.cmbc.devops.entity.Host;
import com.cmbc.devops.manager.task.AppHandleTask;
import com.cmbc.devops.message.MessagePush;
import com.cmbc.devops.model.ApplicationReleaseModel;
import com.cmbc.devops.model.HostResourceModel;
import com.cmbc.devops.model.SimpleContainer;
import com.cmbc.devops.service.AppService;
import com.cmbc.devops.service.ClusterResourceService;
import com.cmbc.devops.service.ClusterService;
import com.cmbc.devops.service.ConportService;
import com.cmbc.devops.service.ContainerService;
import com.cmbc.devops.service.EnvService;
import com.cmbc.devops.service.HostService;
import com.cmbc.devops.webservice.ApplicationClientBuilder;
import com.cmbc.devops.webservice.ApplicationMethod;

/**
 * date：2015年12月10日 上午9:50:46 project name：cmbc-devops-web
 * 
 * @author langzi
 * @version 1.0
 * @since JDK 1.7.0_21 file name：ApplicationReleaseManager.java description：
 */
@Component
public class ApplicationReleaseManager {

	private static final Logger LOGGER = Logger.getLogger(ApplicationReleaseManager.class);

	@Autowired
	private ClusterManager clusterManager;
	// @Autowired
	// private ContainerManager containerManager;
	@Autowired
	private ClusterService clusterService;
	@Autowired
	private HostService hostService;
	@Autowired
	private ContainerService containerService;
	@Autowired
	private ConportService conportService;
	@Autowired
	private ClusterResourceService crService;
	@Autowired
	private Monitor defaultMonitor;
	@Autowired
	private SystemConfig systemConfig;
	@Autowired
	private ApplicationClientBuilder applicationClinetBuilder;
	@Autowired
	private ApplicationMethod applicationMethod;
	@Autowired
	private ApplicationReleaseCore releaseCore;
	@Autowired
	private AppService appService;
	@Autowired
	private MessagePush messagePush;
	@Autowired
	private ContainerManager containerManager;
	@Autowired
	private ContainerCore containerCore;
	@Autowired
	private EnvService envService;

	/**
	 * @author langzi
	 * @return
	 * @version 1.0 2015年12月10日 应用发布
	 */
	public Result appRelease(ApplicationReleaseModel model) {
		// 1.获取集群信息
		int userId = model.getUserId();
		Cluster cluster = getClusterInfo(model.getClusterId());
		if (cluster == null) {
			return new Result(false, "未获取应用集群信息!");
		}
		pushMessage(userId, new MessageResult(false, "10#" + "获取集群信息(名称:" + cluster.getClusterName() + ")成功。", "应用发布"));
		// 2.检查集群是否正常
		Result result = clusterManager.clusterHealthCheck(cluster.getClusterId());
		if (!result.isSuccess()) {
			return new Result(false, "应用集群状态异常，请检查集群状态，再发布应用！");
		}
		pushMessage(userId, new MessageResult(false, "15#" + "集群健康检查(名称:" + cluster.getClusterName() + ")成功。", "应用发布"));
		// 3.获取集群所在主机信息
		Host host = getHostInfo(cluster.getMasteHostId());
		if (host == null) {
			return new Result(false, "未获取集群所在主机信息！");
		}
		// 判断集群下是否有子节点
		try {
			List<Host> slaveHost = hostService.listHostByClusterId(cluster.getClusterId());
			if (slaveHost.isEmpty()) {
				return new Result(false, "集群中不存在可使用的节点，请先添加节点，再发布应用！");
			}
		} catch (Exception e) {
			LOGGER.error("Get slave node error", e);
			return new Result(false, "获取集群子节点信息异常");
		}
		pushMessage(userId,
				new MessageResult(false, "30#" + "获取集群节点信息(名称:" + cluster.getClusterName() + ")成功。", "应用发布"));
		// 4.获取应用信息
		App app = getAppInfo(model.getTenantId(), model.getAppId());
		if (app == null) {
			return new Result(false, "应用发布失败：未获取到应用信息！");
		}
		pushMessage(userId, new MessageResult(false, "40#" + "获取应用信息(名称:" + app.getAppName() + ")成功。", "应用发布"));
		// 5.获取容器最后一条记录
		Integer lastConId = getLastConId();
		if (lastConId == null) {
			return new Result(false, "应用发布失败：获取应用实例历史信息失败");
		}
		model.setLastConId(lastConId);
		// 根据发布方式获取默认信息 (0:普通发布 1：灰度发布)
		if (model.getReleaseMode() == 0 || model.getReleaseMode() == 1) {
			if (app.getAppCpu() != null) {
				model.setCpu(app.getAppCpu());
			}
			if (app.getAppMem() != null) {
				model.setMem(app.getAppMem());
			}
			if (app.getAppEnv() != null) {
				model.setEnv(app.getAppEnv());
			}
			if (app.getAppVolumn() != null) {
				model.setVolume(app.getAppVolumn());
			}
			if (app.getAppParams() != null) {
				model.setParams(app.getAppParams());
			}
			if (app.getAppHealth() != null) {
				model.setAppHealth(app.getAppHealth());
			}
			if (app.getAppMonitor() != null) {
				model.setAppMonitor(app.getAppMonitor());
			}
		}
		//获取环境中的参数
		try {
			Env env=envService.find(model.getEnvId());
			String envparam=env.getEnvParam();
			if(!StringUtils.isEmpty(envparam)){
				String envArray=" -e "+envparam.replaceAll(";", " -e ");
				envArray+=" "+model.getEnv();
				model.setEnv(envArray);
			}
		} catch (Exception e2) {
			LOGGER.error("get env by id["+model.getEnvId()+"] failed!", e2);
		}
		// 独享类资源，根据发布数目获取可用资源
		if (cluster.getResType() == Type.CLUSTER_RES.PRIVATE.ordinal()) {
			int releaseNum = model.getReleaseNum();
			int cpuNum = model.getCpu();
			List<HostResourceModel> hrmList = new ArrayList<HostResourceModel>();
			for (int i = 0; i < releaseNum; i++) {
				List<ClusterResource> crList = crService.findSuitableHostId(cluster.getClusterId(), cpuNum);
				if (crList==null||crList.isEmpty()) {
					return new Result(false, "集群资源不够，请扩充资源再发布应用");
				}
				int hostId = crList.get(0).getHostId();
				String hostRealName = getHostInfo(hostId).getHostRealName();
				Integer[] cpuCores = new Integer[cpuNum];
				for (int j = 0; j < cpuNum; j++) {
					ClusterResource cr = crList.get(j);
					cpuCores[j] = cr.getCpuId();
					cr.setConId(0);
					// 预先抢占资源
					crService.update(cr);
				}
				HostResourceModel hrm = new HostResourceModel(cpuCores, hostRealName, hostId);
				hrmList.add(hrm);
			}
			model.setHrmList(hrmList);
		}

		// 6.发布应用
		pushMessage(userId, new MessageResult(false, "50#" + "应用启动开始。", "应用发布"));
		JSONArray containers = releaseCore.releaseApp(host.getHostIp(), host.getHostUser(), host.getHostPwd(),
				cluster.getClusterPort(), cluster.getResType(), model);

		if (containers.isEmpty()) {
			try {
				crService.collbackUpdate();
			} catch (Exception e) {
				LOGGER.error("collback update clusterResouce containerId failed",e);
			}
			return new Result(false, "应用发布失败：服务器异常，请检查docker服务器和集群是否正常！");
		}
		pushMessage(userId, new MessageResult(false, "70#" + "应用启动成功。", "应用发布"));
		// 应用不进行健康检查和监控
		if (app.getAppHealth() == 0 || app.getAppMonitor() == 0) {
			// 容器信息保存数据库
			String conids = "";
			for (int i = 0; i < containers.size(); i++) {
				JSONObject conJo = containers.getJSONObject(i);
				JSONArray ports = (JSONArray) conJo.get("ports");
				ApplicationReleaseModel arm = new ApplicationReleaseModel();
				BeanUtils.copyProperties(model, arm);
				arm.setConJo(conJo);
				arm.setAppStatus(Status.APP_STATUS.UNDEFINED.ordinal());
				arm.setMonitorStatus(Status.MONITOR_STATUS.UNDEFINED.ordinal());
				conids += addContainerInfo(arm, host, cluster, conJo, ports) + (i == containers.size() - 1 ? "" : ",");
			}
			pushMessage(userId, new MessageResult(false, "100#" + "应用发布完成。", "应用发布"));
			// 如果是灰度发布，发布成功后删除被替换的应用版本
			if (model.getReleaseMode() == 1) {
				return getReplacedContainer(model.getOldImageId(), model.getReleaseNum());
			}

			return new Result(true, conids);
		}
		ExecutorService executor = Executors.newCachedThreadPool();
		CompletionService<ApplicationReleaseModel> comp = new ExecutorCompletionService<>(executor);
		for (int i = 0; i < containers.size(); i++) {
			JSONObject conJo = containers.getJSONObject(i);
			ApplicationReleaseModel arm = new ApplicationReleaseModel();
			BeanUtils.copyProperties(model, arm);
			arm.setConJo(conJo);
			comp.submit(new AppHandleTask(applicationClinetBuilder, applicationMethod, systemConfig, defaultMonitor,
					app, conJo, arm));
		}
		executor.shutdown();
		// 应用健康检查和添加监控项
		if (app.getAppHealth() == 1) {
			pushMessage(userId, new MessageResult(false, "80#" + "应用健康检查。", "应用发布"));
		}
		int index = 0;
		List<ApplicationReleaseModel> releaseModels = new ArrayList<ApplicationReleaseModel>();
		while (index < containers.size()) {
			Future<ApplicationReleaseModel> future = null;
			try {
				future = comp.take();
				ApplicationReleaseModel releaseModel = future.get();
				LOGGER.info("releaseModel:" + releaseModel.toString());
				releaseModels.add(releaseModel);
				index++;
				LOGGER.info("app health check and get mo");
			} catch (InterruptedException e1) {
				LOGGER.error("CompletionService Interrupt error", e1);
				continue;
			} catch (Exception e) {
				LOGGER.error("app healthCheck and operate monitor error", e);
				continue;
			}
		}
		if (app.getAppMonitor() == 1) {
			pushMessage(userId, new MessageResult(false, "85#" + "应用添加监控信息。", "应用发布"));
		}
		// 循环检查每一个启动的容器
		Integer flag = 0;
		// 不健康应用
		StringBuilder unHealthApp = new StringBuilder();
		// 监控异常应用
		StringBuilder unMonitorApp = new StringBuilder();
		String conIds = "";
		for (int i = 0; i < releaseModels.size(); i++) {
			ApplicationReleaseModel releaseModel = releaseModels.get(i);
			JSONObject conJo = releaseModel.getConJo();
			JSONArray ports = (JSONArray) conJo.get("ports");

			if (releaseModel.getAppStatus() == (byte) Status.APP_STATUS.ERROR.ordinal()) {
				// 健康检查异常处理 删除不健康的容器
				SimpleContainer simCon = new SimpleContainer(conJo.getString("id"), host.getHostIp(),
						cluster.getClusterPort(), releaseModel.getMonitorHostId());
				List<SimpleContainer> simList = new ArrayList<SimpleContainer>();
				simList.add(simCon);
				containerCore.stopContainer(simList);
				containerCore.removeContainer(simList);
				unHealthApp.append(conJo.getString("id").subSequence(0, 8)).append(",");
			} else if (releaseModel.getMonitorStatus() == (byte) Status.MONITOR_STATUS.ABNORMAL.ordinal()) {
				// 添加监控结果处理，删除添加监控失败的容器
				SimpleContainer simCon = new SimpleContainer(conJo.getString("id"), host.getHostIp(),
						cluster.getClusterPort(), releaseModel.getMonitorHostId());
				List<SimpleContainer> simList = new ArrayList<SimpleContainer>();
				simList.add(simCon);
				containerCore.stopContainer(simList);
				containerCore.removeContainer(simList);
				unMonitorApp.append(conJo.getString("id").subSequence(0, 8)).append(",");
			} else {
				Integer addSuccess = addContainerInfo(releaseModel, host, cluster, conJo, ports);
				if (addSuccess > 0) {
					lastConId++;
					conIds += lastConId + (i != containers.size() - 1 ? "," : "");
					flag++;
				}
			}
		}
		pushMessage(userId, new MessageResult(false, "100#" + "应用发布完成。", "应用发布"));
		// 如果是灰度发布，发布成功后删除被替换的应用版本
		if (model.getReleaseMode() == 1) {
			// replaceApplication(model.getOldImageId(), flag);
			return getReplacedContainer(model.getOldImageId(), model.getReleaseNum());
		}
		// 批量处理不成功事件
		if (flag == model.getReleaseNum()) {
			return new Result(true, conIds);
		} else {
			// 如果不成功，则返回失败具体信息
			String returnMsg = "应用发布实例成功" + flag + "个";
			if (StringUtils.hasText(unHealthApp.toString())) {
				returnMsg += "，应用实例ID" + unHealthApp.toString() + "健康检查失败，请检查应用接口是否开放或者正确";
			}
			if (StringUtils.hasText(unMonitorApp.toString())) {
				returnMsg += unMonitorApp.toString() + "应用实例加入监控失败";
			}
			return new Result(false, returnMsg);
		}

	}

	/**
	 * @author langzi
	 * @param clusterId
	 * @return
	 * @version 1.0 2015年12月10日
	 */
	private Cluster getClusterInfo(Integer clusterId) {
		try {
			return clusterService.getCluster(clusterId);
		} catch (Exception e) {
			LOGGER.error("get cluster by cluster id failed", e);
			return null;
		}
	}

	/**
	 * @author langzi
	 * @param hostId
	 * @return
	 * @version 1.0 2015年12月10日
	 */
	private Host getHostInfo(Integer hostId) {
		try {
			return hostService.loadHost(hostId);
		} catch (Exception e) {
			LOGGER.error("Get host by host id error", e);
			return null;
		}
	}

	/**
	 * @author langzi
	 * @param appId
	 * @return
	 * @version 1.0 2015年12月10日
	 */
	private App getAppInfo(Integer tenantId, Integer appId) {
		try {
			return appService.findAppById(tenantId, appId);
		} catch (Exception e) {
			LOGGER.error("get application by appidfalied！", e);
			return null;
		}
	}

	/**
	 * @author langzi
	 * @return
	 * @version 1.0 2015年12月10日
	 */
	private Integer getLastConId() {
		try {
			return containerService.getLastConId();
		} catch (Exception e) {
			LOGGER.error("Get last conId error", e);
			return null;
		}
	}

	/**
	 * @author langzi
	 * @param model
	 * @param host
	 * @param cluster
	 * @param conJo
	 * @param ports
	 * @return
	 * @version 1.0 2015年11月25日
	 */
	private Integer addContainerInfo(ApplicationReleaseModel model, Host host, Cluster cluster, JSONObject conJo,
			JSONArray ports) {
		Container container = new Container();
		container.setConUuid(conJo.getString("id"));
		// 容器名称
		container.setConName(model.getConName());
		container.setConImgid(model.getImageId());
		if (conJo.getString("status").contains("Up")) {
			container.setConPower((byte) Status.POWER.UP.ordinal());
		} else {
			container.setConPower((byte) Status.POWER.OFF.ordinal());
		}
		container.setAppStatus((byte) model.getAppStatus());
		container.setMonitorStatus((byte) model.getMonitorStatus());
		container.setConDesc(model.getAppDesc());
		container.setConStatus((byte) Status.CONTAINER.EXIT.ordinal());
		container.setConStartCommand(conJo.getString("runCommand"));
		container.setConStartParam(model.getParams());
		container.setAppId(model.getAppId());
		container.setClusterIp(host.getHostIp());
		container.setClusterPort(cluster.getClusterPort());
		container.setConCreator(model.getUserId());
		container.setConCreatetime(new Date());
		container.setMonitorHostId(model.getMonitorHostId());
		// 添加端口
		String hostIp = ((JSONObject) ports.get(0)).getString("ip");
		int hostId = getHostId(hostIp);
		container.setHostId(hostId);
		try {
			containerService.addContaier(container);
		} catch (Exception e) {
			LOGGER.error("Create container error", e);
			return -1;
		}
		for (int j = 0; j < ports.size(); j++) {
			JSONObject port = (JSONObject) ports.get(j);
			addConPort(container.getConId(), port);
		}
		List<HostResourceModel> hrmList = model.getHrmList();
		Integer[] cpuIds = null;
		/** 对于hrmList进行判空处理,HostResourceModel */
		if (hrmList != null) {   
			for (HostResourceModel hrm : hrmList) {
				if (hrm.getHostId() == hostId) {
					cpuIds = hrm.getHostCpuCore();
					crService.updateConIdByHostIdAndCpuIds(hostId, cpuIds, container.getConId());
				}
			}
		}
		return container.getConId();
	}

	/**
	 * @author langzi
	 * @param conId
	 * @param port
	 * @return
	 * @version 1.0 2015年11月25日
	 */
	private int addConPort(Integer conId, JSONObject port) {
		ConPort conPort = new ConPort();
		conPort.setContainerId(conId);
		String ip = port.getString("ip");
		conPort.setConIp(StringUtils.hasText(ip) ? ip : null);
		int publicPort = port.getInteger("publicPort");
		int privatePort = port.getInteger("privatePort");
		if (publicPort > 0) {
			conPort.setPubPort(String.valueOf(publicPort));
		}
		if (privatePort > 0) {
			conPort.setPriPort(String.valueOf(privatePort));
		}
		try {
			return conportService.addConports(conPort);
		} catch (Exception e) {
			LOGGER.error("Mofify container port infos failed");
			return -1;
		}
	}

	private Integer getHostId(String hostIp) {
		Host host = new Host();
		host.setHostIp(hostIp);
		host.setHostType((byte) Type.HOST.DOCKER.ordinal());
		try {
			host = hostService.getHostByIp(host);
			return host.getHostId();
		} catch (Exception e) {
			LOGGER.error("Get host id by ip and type failed", e);
			return null;
		}
	}

	private void pushMessage(final Integer userId, final MessageResult message) {
		messagePush.pushMessage(userId, JSONObject.toJSONString(message));
		LOGGER.info("Send message :" + message + ", to user(id:" + userId + ")");
	}

	/**
	 * @param imageId
	 * @param replaceNum
	 */
	@SuppressWarnings("unused")
	private void replaceApplication(int imageId, int replaceNum) {
		try {
			List<SimpleContainer> simList = containerService.selectContainerByImageId(imageId, replaceNum);
			String[] conids = new String[simList.size()];
			for (int i = 0; i < simList.size(); i++) {
				SimpleContainer sim = simList.get(i);
				conids[i] = String.valueOf(sim.getContainerId());
			}
			rollbackApplication(conids);
			LOGGER.debug("delete container[" + org.apache.commons.lang.StringUtils.join(conids) + "] by oldimageid["
					+ imageId + "]  success.");
		} catch (Exception e) {
			LOGGER.error("get container by imageid[" + imageId + "] error", e);
		}
	}

	private Result getReplacedContainer(int imageId, int replaceNum) {
		try {
			List<SimpleContainer> simList = containerService.selectContainerByImageId(imageId, replaceNum);
			String conids = "";
			for (int i = 0; i < simList.size(); i++) {
				SimpleContainer sim = simList.get(i);
				conids += String.valueOf(sim.getContainerId()) + (i == simList.size() - 1 ? "" : ",");
			}
			return new Result(true, conids);
		} catch (Exception e) {
			LOGGER.error("get container by imageid[" + imageId + "] error", e);
			return new Result(true, "");
		}
	}

	/**
	 * @param containerIds
	 */
	private void rollbackApplication(String[] containerIds) {
		Result result = null;
		result = containerManager.stopContainer(containerIds);
		if (result.isSuccess()) {
			result = containerManager.removeContainer(containerIds);
		}
	}

}

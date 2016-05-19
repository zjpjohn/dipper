package com.cmbc.devops.manager;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.cxf.endpoint.Client;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
import com.cmbc.devops.bean.GridBean;
import com.cmbc.devops.bean.Result;
import com.cmbc.devops.config.SystemConfig;
import com.cmbc.devops.constant.Status;
import com.cmbc.devops.constant.Type;
import com.cmbc.devops.core.ContainerCore;
import com.cmbc.devops.entity.App;
import com.cmbc.devops.entity.Cluster;
import com.cmbc.devops.entity.ConPort;
import com.cmbc.devops.entity.Container;
import com.cmbc.devops.entity.Host;
import com.cmbc.devops.entity.Image;
import com.cmbc.devops.entity.expand.ContainerExpand;
import com.cmbc.devops.model.ContainerModel;
import com.cmbc.devops.model.SimpleContainer;
import com.cmbc.devops.service.AppService;
import com.cmbc.devops.service.ClusterResourceService;
import com.cmbc.devops.service.ClusterService;
import com.cmbc.devops.service.ConportService;
import com.cmbc.devops.service.ContainerService;
import com.cmbc.devops.service.HostService;
import com.cmbc.devops.service.ImageService;
import com.cmbc.devops.util.TimeUtils;
import com.cmbc.devops.webservice.ApplicationClientBuilder;
import com.cmbc.devops.webservice.ApplicationMethod;
import com.github.dockerjava.api.model.Container.Port;

/**
 * date：2015年8月26日 上午11:12:42 project name：cmbc-devops-web
 * 
 * @author langzi
 * @version 1.0
 * @since JDK 1.7.0_21 file name：ContainerManager.java description：
 */
@Component
public class ContainerManager {

	@Autowired
	private ContainerCore containerCore;
	@Autowired
	private ApplicationClientBuilder applicationClinetBuilder;
	@Autowired
	private ApplicationMethod applicationMethod;
	@Autowired
	private ClusterManager clusterManager;
	@Autowired
	private ContainerService containerService;
	@Autowired
	private ClusterService clusterService;
	@Autowired
	private HostService hostService;
	@Autowired
	private ConportService conportService;
	@Autowired
	private ImageService imageService;
	@Autowired
	private AppService appService;
	@Autowired
	private ClusterResourceService crService;
	@Autowired
	private Monitor defaultMonitor;
	@Autowired
	private SystemConfig systemConfig;

	private static final Logger LOGGER = Logger.getLogger(ContainerManager.class);

	/**
	 * @author langzi
	 * @param jo
	 * @return
	 * @version 1.0 2015年8月27日
	 */
	public Result createContainer(ContainerModel model) {
		// 检查容器模型是否为null
		if (model == null) {
			return new Result(false, "创建容器失败：传入参数异常");
		}
		// 1.获取集群信息
		Cluster cluster = getClusterInfo(model.getClusterId());
		if (cluster == null) {
			return new Result(false, "未获取应用集群信息!");
		}
		// 2.检查集群是否正常
		Result result = clusterManager.clusterHealthCheck(cluster.getClusterId());
		if (!result.isSuccess()) {
			return new Result(false, "应用集群状态异常，请检查集群状态，再创建容器！");
		}
		// 3.获取集群所在主机信息
		Host host = getHostInfo(cluster.getMasteHostId());
		if (host == null) {
			return new Result(false, "未获取集群所在主机信息！");
		}
		// 判断集群下是否有子节点
		try {
			List<Host> slaveHost = hostService.listHostByClusterId(cluster.getClusterId());
			if (slaveHost.isEmpty()) {
				return new Result(false, "集群中不存在可使用的节点，请先添加节点，再创建容器！");
			}
		} catch (Exception e) {
			LOGGER.error("Get slave node error", e);
			return new Result(false, "获取集群子节点信息异常");
		}

		// 4.获取应用信息
		App app = getAppInfo(model.getTenantId(), Integer.parseInt(model.getAppId()));
		if (app == null) {
			return new Result(false, "创建容器失败：未获取到应用信息！");
		}
		// 5.获取容器最后一条记录
		Integer lastConId = getLastConId();
		if (lastConId == null) {
			return new Result(false, "创建容器失败：获取容器记录失败");
		}
		model.setLastConId(lastConId);
		// 6.创建容器
		JSONArray containers = containerCore.createContainer(host.getHostIp(), host.getHostUser(), host.getHostPwd(),
				cluster.getClusterPort(), model);
		if (containers.isEmpty()) {
			return new Result(false, "创建容器失败：服务器异常，请检查docker服务器和集群是否正常！");
		}
		// 循环检查每一个启动的容器
		Integer flag = 0;
		for (int i = 0; i < containers.size(); i++) {
			JSONObject conJo = containers.getJSONObject(i);
			JSONArray ports = (JSONArray) conJo.get("ports");
			// 7.应用检查
			Result appHealthResult = appHealthCheck(app, ports);
			// 应用不健康，不用添加应用状态和主机监控
			if (!appHealthResult.isSuccess()) {
				model.setAppStatus(Status.APP_STATUS.ERROR.ordinal());
				model.setMonitorStatus(Status.MONITOR_STATUS.ABNORMAL.ordinal());
				// continue;
			} else {
				// 应用健康，修改应用状态
				model.setAppStatus(Status.APP_STATUS.NORMAL.ordinal());
				// 8.加入主机监控
				String hostIp = ((JSONObject) ports.get(0)).getString("ip");
				String conUuid = conJo.getString("id").substring(0, 12);
				Result monitorResult = new Result();
				monitorResult = addHostMonitor(hostIp, conUuid);
				// 加入主机监控失败，监控状态不正常
				if (!monitorResult.isSuccess()) {
					model.setMonitorStatus(Status.MONITOR_STATUS.ABNORMAL.ordinal());
				} else {
					// 加入主机监控，监控状态正常
					model.setMonitorStatus(Status.MONITOR_STATUS.NORMAL.ordinal());
					// 获取监控信息
					MonitorList monitors = getMonitorList(app, ports);
					if (monitors != null) {
						// 9.添加其他监控项
						List<ItemInfo> items = monitors.getItemInfos();
						List<ItemInfo> addItems = new ArrayList<ItemInfo>();
						LOGGER.info(JSON.toJSON(items));
						if (!items.isEmpty()) {
							for (ItemInfo item : items) {
								ItemInfo itemInfo = new ItemInfo(monitorResult.getMessage(), item.getItemKey(), "", 10);
								addItems.add(itemInfo);
							}
							defaultMonitor.addMoniItems(addItems);
						}
						// 10.添加trigger
						List<TriggerInfo> triggers = monitors.getTriggerInfos();
						List<TriggerInfo> addTriggers = new ArrayList<TriggerInfo>();
						LOGGER.info(JSON.toJSON(triggers));
						if (!triggers.isEmpty()) {
							for (TriggerInfo trigger : triggers) {
								TriggerInfo triggerInfo = new TriggerInfo();
								triggerInfo.setExpression(trigger.getExpression());
								triggerInfo.setDescription(trigger.getDescription());
								triggerInfo.setPriority(trigger.getPriority());
								addTriggers.add(triggerInfo);
							}
							defaultMonitor.addTriggerMonitor(addTriggers);
						}
					} else {
						LOGGER.warn("Other monitors not find or get other items failed");
					}
					// 11.保存到数据库
					model.setMonitorHostId(monitorResult.getMessage());
				}
			}
			String conName = model.getConName() + (i == 0 ? "" : "-" + i);
			model.setConName(conName);
			Integer addSuccess = addContainerInfo(model, host, cluster, conJo, ports);
			// 添加不成功
			if (addSuccess == 1) {
				flag++;
			}
		}
		// 批量处理不成功事件
		if (!flag.equals(model.getConNumber())) {
			return new Result(false, "创建容器成功" + flag + "个，请检查集群或者docker服务器是否正常！");
		}
		return new Result(true, "创建容器成功！");
	}

	/**
	 * @author langzi
	 * @param jo
	 * @return
	 * @version 1.0 2015年8月27日
	 */
	public Result startContainer(String[] containerIds) {

		// 1.获取需要启动的容器的信息
		List<SimpleContainer> simCons = null;
		try {
			simCons = containerService.selectContainerUuid(containerIds);
		} catch (Exception e) {
			LOGGER.error("Get container infos error", e);
			return new Result(false, "启动容器失败：获取容器详细信息异常！");
		}
		if (simCons == null) {
			return new Result(false, "启动容器失败：未获取到容器信息！");
		}
		// 2.启动容器，并且返回执行结果
		List<SimpleContainer> scs = containerCore.startContainer(simCons);
		// 3.根据返回结果，判断容器是否正常启动
		if (scs.isEmpty()) {
			return new Result(false, "启动容器失败：请检查服务器中Docker服务是否正常以及容器数据表和服务器状态是否一致");
		}
		// 4.修改容器在数据库中的状态
		//过滤没有做健康检查和添加监控的应用
		List<SimpleContainer> normalScs = new ArrayList<>();
		List<SimpleContainer> unnormalScs = new ArrayList<>();
		for (int i = 0; i < scs.size(); i++) {
			SimpleContainer sc = scs.get(i);
			if (sc.getMonitorStatus() == Status.MONITOR_STATUS.ABNORMAL.ordinal()) {
				normalScs.add(sc);
				App app=new App();
				JSONArray ports;
				try {
					app = appService.findAppByConId(sc.getContainerId());
					ports = (JSONArray) scs.get(i).getConJo().get("ports");
				} catch (Exception e) {
					LOGGER.error("get appInfos error", e);
					return new Result(false, "启动容器失败：获取应用信息出错！");
				}
				MonitorList monitors = getMonitorList(app,ports);
				if (monitors != null) {
					LOGGER.info("启动添加加监控项成功！");
				}
			}else{
				unnormalScs.add(sc);
			}
		}
		// 获取已经停止容器的uuid
		String[] conUuids = new String[scs.size()];
		
		for (int i = 0; i < normalScs.size(); i++) {
			SimpleContainer sc = normalScs.get(i);
			conUuids[i] = sc.getContainerUuid();
			// 开启监控
			defaultMonitor.enableHostMonitor(sc.getMonitorHostId());
		}
		ContainerExpand ce = new ContainerExpand();
		ce.setConPower((byte) Status.POWER.UP.ordinal());
		ce.setAppStatus((byte) Status.APP_STATUS.NORMAL.ordinal());
		ce.setMonitorStatus((byte) Status.MONITOR_STATUS.NORMAL.ordinal());
		ce.setConUuids(conUuids);
		
		// 更新不正常的容器
		String[] unNormalConUuids = new String[scs.size()];
		for (int i = 0; i < unnormalScs.size(); i++) {
			SimpleContainer sc = unnormalScs.get(i);
			unNormalConUuids[i] = sc.getContainerUuid();
			// 关闭监控
			defaultMonitor.disableHostMonitor(sc.getMonitorHostId());
		}
		// 修改容器的状态
		ContainerExpand unNormalce = new ContainerExpand();
		unNormalce.setConPower((byte) Status.POWER.UP.ordinal());
		unNormalce.setConUuids(unNormalConUuids);
		try {
			// 修改容器的状态
			containerService.modifyConStatus(ce);
			containerService.modifyConStatus(unNormalce);
		} catch (Exception e) {
			containerCore.stopContainer(simCons);
			LOGGER.error("Modify container status failed", e);
			// 异常处理
			return new Result(false, "启动容器失败：数据持久化异常");
		}
		// 修改集群以及端口映射信息
		for(SimpleContainer sc:scs){
			Integer conId = sc.getContainerId();
			/*Container container = new Container();
			container.setConId(conId);
			try {
				container = containerService.getContainer(container);
			} catch (Exception e1) {
				LOGGER.error("get container info error", e1);
			}*/
			JSONObject con = sc.getConJo();
			JSONArray ports = (JSONArray) con.get("ports");
			for (int k = 0; k < ports.size(); k++) {
				JSONObject port = (JSONObject) ports.get(k);
				addConPort(conId, port);
			}
		}
		// 判读容器是全部启动成功，还是部分启动成功！
		if (containerIds.length != scs.size()) {
			return new Result(false, "部分容器启动成功，请检查集群或者docker主机是否正常！");
		}
		return new Result(true, "启动容器成功！");
	}

	/**
	 * @author langzi
	 * @param jo
	 * @return
	 * @version 1.0 2015年8月27日
	 */
	public Result stopContainer(String[] containerIds) {

		// 1.获取需要停止的容器的信息
		List<SimpleContainer> simCons = null;
		try {
			simCons = containerService.selectContainerUuid(containerIds);
			if (simCons.isEmpty()) {
				return new Result(false, "停止容器失败：未获取到容器信息！");
			}
		} catch (Exception e) {
			LOGGER.error("Get container infos error", e);
			return new Result(false, "停止容器失败：获取容器详细信息失败！");
		}
		// 2.停止容器，并且返回执行结果
		List<SimpleContainer> scs = containerCore.stopContainer(simCons);
		// 3.根据返回结果，判断容器是否正常停止
		if (scs.isEmpty()) {
			return new Result(false, "停止容器失败：容器状态不一致或者服务器异常！！");
		}
		// 4.修改容器在数据库中的状态
		//过滤没有做健康检查和添加监控的应用
		List<SimpleContainer> normalScs = new ArrayList<>();
		List<SimpleContainer> unnormalScs = new ArrayList<>();
		for (int i = 0; i < scs.size(); i++) {
			SimpleContainer sc = scs.get(i);
			if (sc.getMonitorStatus() == Status.MONITOR_STATUS.NORMAL.ordinal()) {
				normalScs.add(sc);
			}else{
				unnormalScs.add(sc);
			}
		}
		// 更新正常的容器
		String[] normalConUuids = new String[scs.size()];
		for (int i = 0; i < normalScs.size(); i++) {
			SimpleContainer sc = normalScs.get(i);
			normalConUuids[i] = sc.getContainerUuid();
			// 关闭监控
			defaultMonitor.disableHostMonitor(sc.getMonitorHostId());
		}
		// 修改容器的状态
		ContainerExpand normalce = new ContainerExpand();
		normalce.setConPower((byte) Status.POWER.OFF.ordinal());
		normalce.setAppStatus((byte) Status.APP_STATUS.ABNORMAL.ordinal());
		normalce.setMonitorStatus((byte) Status.MONITOR_STATUS.ABNORMAL.ordinal());
		normalce.setConUuids(normalConUuids);
		
		// 更新不正常的容器
		String[] unNormalConUuids = new String[scs.size()];
		for (int i = 0; i < unnormalScs.size(); i++) {
			SimpleContainer sc = unnormalScs.get(i);
			unNormalConUuids[i] = sc.getContainerUuid();
			// 关闭监控
			defaultMonitor.disableHostMonitor(sc.getMonitorHostId());
		}
		// 修改容器的状态
		ContainerExpand unNormalce = new ContainerExpand();
		unNormalce.setConPower((byte) Status.POWER.OFF.ordinal());
		unNormalce.setConUuids(unNormalConUuids);
		try {
			containerService.modifyConStatus(normalce);
			containerService.modifyConStatus(unNormalce);
			//删除容器的ip
			for(SimpleContainer sc:scs){
				Integer conId = sc.getContainerId();
				String[] conIds = {String.valueOf(conId)};
				try {
					conportService.removeConports(conIds);
				} catch (Exception e) {
					LOGGER.error("remove container ports infos error", e);
				}
			}
			// 判读容器是全部停止成功，还是部分停止成功！
			if (containerIds.length != scs.size()) {
				return new Result(false, "部分容器停止成功，请检查集群或者docker主机是否正常！");
			}
			return new Result(true, "停止容器成功！");
		} catch (Exception e) {
			LOGGER.error("Modify container power status failed", e);
			// 异常处理
			return new Result(false, "停止容器失败：数据持久化异常");
		}
	}

	/**
	 * @author langzi
	 * @param jo
	 * @return
	 * @version 1.0 2015年8月27日
	 */
	public Result removeContainer(String[] containerIds) {
		// 1.获取需要删除的容器的信息
		List<SimpleContainer> simCons = null;
		try {
			simCons = containerService.selectContainerUuid(containerIds);
		} catch (Exception e) {
			LOGGER.error("Get container infos error", e);
			return new Result(false, "删除容器失败：获取容器详细信息失败！");
		}
		// 2.删除容器，并返回执行结果
		List<SimpleContainer> scs = containerCore.removeContainer(simCons);
		// 3.根据结果判断容器是否已经删除
		if (scs.isEmpty()) {
			return new Result(false, "删除容器失败：容器状态不一致或者服务器异常！！");
		}
		// 4.修改容器状态

		ContainerExpand ce = new ContainerExpand();
		ce.setConStatus((byte) Status.CONTAINER.DELETE.ordinal());
		String[] conUuids = new String[scs.size()];
		String[] conIds = new String[scs.size()];
		Integer[] conids = new Integer[scs.size()];
		for (int i = 0; i < scs.size(); i++) {
			SimpleContainer sc = scs.get(i);
			conUuids[i] = sc.getContainerUuid();
			conIds[i] = String.valueOf(sc.getContainerId());
			conids[i] = sc.getContainerId();
			// 清除监控
			if (sc.getMonitorStatus() != Status.MONITOR_STATUS.UNDEFINED.ordinal()) {
				defaultMonitor.cancelHostMonitor(sc.getMonitorHostId());
			}
		}
		ce.setConUuids(conUuids);
		try {
			containerService.modifyConStatus(ce);
			conportService.removeConports(conIds);
			crService.updateByConId(conids);
			if (containerIds.length != scs.size()) {
				return new Result(false, "部分容器删除成功，请检查集群或者docker主机是否正常！");
			}
			return new Result(true, "容器删除成功！");
		} catch (Exception e) {
			LOGGER.error("Delete containers error", e);
			scs.clear();
			return new Result(false, "容器删除操作失败，可能导致数据异常，请通过同步功能保持状态同步！");
		}
	}

	/**
	 * @author langzi
	 * @param jo
	 * @return
	 * @version 1.0 2015年8月27日
	 */
	public Result syncContainer(Integer tenantId) {
		try {
			// 1.获取数据库中所有的容器（数据库中）,@date:2016年3月28日 添加租户维度
			Container sel_con = new Container();
			sel_con.setTenantId(tenantId);
			List<Container> persistentConList = containerService.listAllContainer(sel_con);
			// 定义conUuids，存储所有容器的uuid
			List<String> conUuids = new ArrayList<String>();
			if (!persistentConList.isEmpty()) {
				for (Container container : persistentConList) {
					conUuids.add(container.getConUuid());
				}
			}
			// 2.获取所有的集群信息,并清除异常的集群
			List<Cluster> clusters = clusterService.listAllCluster();
			if (clusters.isEmpty()) {
				return new Result(false, "未获取到集群的信息");
			}
			// 异常集群列表
			String exceptionClusterName = "";
			for (Cluster cluster : clusters) {
				// 集群健康检查
				Result result = clusterManager.clusterHealthCheck(cluster.getClusterId());
				// 当集群异常时把集群名存入exceptionClusterName中
				if (!result.isSuccess()) {
					exceptionClusterName += cluster.getClusterName() + ",";
				}
			}
			// 如果有异常的集群，则返回提示信息，没有则继续
			if (!exceptionClusterName.isEmpty()) {
				return new Result(false, exceptionClusterName + "异常集群，同步会导致数据混乱，请确保该集群正常，再同步！");
			}
			// 获取每个集群中容器信息
			for (Cluster cluster : clusters) {
				// 获取每个集群所在主机信息（master节点）
				Host host = new Host();
				host.setHostId(cluster.getMasteHostId());
				host = hostService.getHost(host);
				if (host == null) {
					continue;
				}
				// 查询该集群中所有容器（服务器上）
				List<com.github.dockerjava.api.model.Container> contianers = containerCore
						.listAllContainer(host.getHostIp(), cluster.getClusterPort());
				if (contianers.isEmpty()) {
					continue;
				}
				for (com.github.dockerjava.api.model.Container container : contianers) {
					// 判断集群物理主机上的容器是否存在于数据库中，如果不存在则新增一条记录
					if (!conUuids.contains(container.getId())) {
						// 对比容器信息
						Container diffContainer = new Container();
						diffContainer.setConName(container.getNames()[0]);
						diffContainer.setConUuid(container.getId());
						diffContainer.setClusterIp(host.getHostIp());
						diffContainer.setClusterPort(cluster.getClusterPort());
						diffContainer.setConStartCommand(container.getCommand());
						diffContainer.setConStatus((byte) Status.CONTAINER.EXIT.ordinal());
						// 如果容器是否为启动状态
						if (container.getStatus().contains("Up")) {
							// 如果为启动状态则数据库中该容器的状态设置为启动中
							diffContainer.setConPower((byte) Status.POWER.UP.ordinal());
							// 获取容器映射的端口列表
							Port[] ports = container.getPorts();
							if (ports.length != 0) {
								String hostIp = ports[0].getIp();
								if (!hostIp.equals("")) {
									Integer hostId = getHostId(hostIp);
									hostId = hostId == -1 ? null : hostId;
									diffContainer.setHostId(hostId);
								}
							}
							containerService.addContaier(diffContainer);
							diffContainer = containerService.getContainer(diffContainer);
							if (ports.length == 0) {
								continue;
							}
							for (Port port : ports) {
								JSONObject portJo = (JSONObject) JSONObject.toJSON(port);
								addConPort(diffContainer.getConId(), portJo);
							}
						} else {
							diffContainer.setConPower((byte) Status.POWER.OFF.ordinal());
							containerService.addContaier(diffContainer);
						}
					} else {
						Container con = new Container();
						con.setConUuid(container.getId());
						con = containerService.getContainer(con);
						byte power;
						// 同步为启动状态
						if (container.getStatus().contains("Up")) {
							power = (byte) Status.POWER.UP.ordinal();
							Port[] ports = container.getPorts();
							for (Port port : ports) {
								JSONObject portJo = (JSONObject) JSONObject.toJSON(port);
								addConPort(con.getConId(), portJo);
							}
							// 打开监控
						} else {
							power = (byte) Status.POWER.OFF.ordinal();
							// 关闭监控
						}
						con.setConPower(power);
						containerService.modifyContainer(con);
					}
					conUuids.remove(container.getId());
				}
			}
			// 删除不存在的容器，删除集群异常的容器
			for (String conUuid : conUuids) {
				Container con = new Container();
				con.setConUuid(conUuid);
				con = containerService.getContainer(con);
				containerService.removeContainer(con.getConId());
			}

		} catch (Exception e) {
			LOGGER.error("sysnc container error", e);
			return new Result(false, "容器同步失败！");
		}
		LOGGER.info("sysnc container success");
		return new Result(true, "容器同步成功！");
	}

	public Map<String, Object> detail(Integer tenantId, Integer id) {
		Container container = new Container();
		container.setConId(id);
		List<ConPort> conPorts = new ArrayList<ConPort>();
		String portInfo = "";
		try {
			conPorts = conportService.listConPorts(id);
			if (!conPorts.isEmpty()) {
				for (ConPort port : conPorts) {
					if (port.getConIp() == null || port.getConIp().equals("")) {
						continue;
					}
					if (port.getPubPort() == null || port.getPubPort().equals("")) {
						continue;
					}
					if (port.getPriPort() == null || port.getPriPort().equals("")) {
						continue;
					}
					portInfo += port.getConIp() + ":" + port.getPubPort() + "--->" + port.getPriPort() + "</br>";
				}
			}
		} catch (Exception e1) {
			LOGGER.error("List container port infos failed", e1);
		}
		Map<String, Object> conMap = new HashMap<String, Object>();
		try {
			container = containerService.getContainer(container);
			if (container == null) {
				conMap.put("conId", "容器不存在");
				return conMap;
			}
			conMap.put("conId", container.getConId());
			conMap.put("conName", container.getConName());
			conMap.put("conPower", container.getConPower() == 0 ? "<i class='fa fa-stop text-danger'>&nbsp; 已停止</i>"
					: "<i class='fa fa-play-circle text-success'> &nbsp;运行中</i>");
			conMap.put("conPort", portInfo);
			if (container.getConImgid() != null) {
				Image image = imageService.loadImage(tenantId, container.getConImgid());
				if (image != null) {
					conMap.put("imageName", image.getImageName() == null ? "" : image.getImageName());
				}
			} else {
				conMap.put("imageName", "依赖镜像未知");
			}
			if (container.getHostId() != null) {
				Host host = new Host();
				host.setHostId(container.getHostId());
				host = hostService.getHost(host);
				if (host != null) {
					conMap.put("hostName", host.getHostName() == null ? "" : host.getHostName());
				}
			} else {
				conMap.put("hostName", "主机未知");
			}
			if (container.getAppId() != null) {
				App app = new App();
				app = appService.findAppById(tenantId, container.getAppId());
				conMap.put("appName", app.getAppName());
			} else {
				conMap.put("appName", "应用未知");
			}
			conMap.put("conStartCom", container.getConStartCommand());
			conMap.put("createTime", TimeUtils.formatTime(container.getConCreatetime()));
		} catch (Exception e) {
			LOGGER.error("Get container details error", e);
		}
		return conMap;
	}

	public GridBean advancedSearchContainer(Integer userId, Integer tenantId, int pagenumber, int pagesize, JSONObject json_object) {
		try {
			return containerService.advancedSearchContainer(userId, tenantId, pagenumber, pagesize, json_object);
		} catch (Exception e) {
			LOGGER.error("search container error", e);
		}
		return null;
	}

	/**
	 * @author langzi
	 * @param clusterId
	 * @return
	 * @version 1.0 2015年11月25日
	 */
	private Cluster getClusterInfo(Integer clusterId) {
		Cluster cluster = null;
		try {
			cluster = clusterService.getCluster(clusterId);
		} catch (Exception e) {
			LOGGER.error("get cluster by cluster id failed", e);
			return null;
		}
		return cluster;
	}

	/**
	 * @author langzi
	 * @param hostId
	 * @return
	 * @version 1.0 2015年11月25日
	 */
	private Host getHostInfo(Integer hostId) {
		Host host = new Host();
		host.setHostId(hostId);
		try {
			host = hostService.getHost(host);
		} catch (Exception e) {
			LOGGER.error("Get host by host id failed", e);
			return null;
		}
		return host;
	}

	/**
	 * @author langzi
	 * @param appId
	 * @return
	 * @version 1.0 2015年11月25日
	 */
	private App getAppInfo(Integer tenantId, Integer appId) {
		App app = new App();
		try {
			app = appService.findAppById(tenantId, appId);
		} catch (Exception e) {
			LOGGER.error("get application by appidfalied！", e);
			return null;
		}
		return app;
	}

	/**
	 * @author langzi
	 * @return
	 * @version 1.0 2015年11月25日
	 */
	private Integer getLastConId() {
		Integer lastConId = 0;
		try {
			if (containerService.getLastConId() != null) {
				lastConId = containerService.getLastConId();
			}
		} catch (Exception e) {
			LOGGER.error("Get last conId error", e);
			return null;
		}
		return lastConId;
	}

	/**
	 * @author langzi
	 * @param app
	 * @param ports
	 * @return
	 * @version 1.0 2015年11月25日
	 */
	private Result appHealthCheck(App app, JSONArray ports) {
		Result result = new Result(false, "");
		for (int j = 0; j < ports.size(); j++) {
			JSONObject port = (JSONObject) ports.get(j);
			if (port.getInteger("privatePort").equals(app.getAppPriPort())) {
				long begingTime = System.currentTimeMillis();
				String ip = port.getString("ip");
				String pubPort = String.valueOf(port.getInteger("publicPort"));
				try {
					while (true) {
						if ((System.currentTimeMillis() - begingTime) > systemConfig.getDelayTime()) {
							result.setMessage("应用检查超时！");
							break;
						}
						Client client = applicationClinetBuilder.createClient(ip, pubPort,
								ApplicationClientBuilder.HEALTH_PATH, true);
						String method = applicationMethod.healthCheck();
						if (client != null) {
							Object[] objs = client.invoke(method);
							if (objs != null) {
								result.setSuccess((boolean) objs[0]);
							}
							break;
						} else {
							Thread.sleep(10 * 1000);
							continue;
						}
					}
				} catch (Exception e) {
					LOGGER.error("application health check failed", e);
				}
			}
		}
		return result;
	}

	/**
	 * @author langzi
	 * @param ip
	 * @param conUuid
	 * @return
	 * @version 1.0 2015年11月25日
	 */
	private Result addHostMonitor(String ip, String conUuid) {
		HostInfo hostInfo = new HostInfo(ip, "", conUuid, "10105");
		TemplateInfo templateInfo = new TemplateInfo();
		Result tempResult = defaultMonitor.getTemplates();
		if (!tempResult.isSuccess()) {
			LOGGER.warn("Get template info failed");
			return new Result(false, "未获取到模板信息，加入主机监控失败");
		}
		templateInfo.setTemplateid(tempResult.getMessage());
		List<TemplateInfo> templateInfos = new ArrayList<TemplateInfo>();
		templateInfos.add(templateInfo);
		HostWithTemplate monitorhost = new HostWithTemplate(hostInfo, templateInfos);
		Result groupResult = defaultMonitor.getGroup();
		if (!groupResult.isSuccess()) {
			return new Result(false, "未获取到主机所在分组信息，加入主机监控失败");
		}
		return defaultMonitor.addHostMonitor(Integer.parseInt(groupResult.getMessage()), monitorhost);
	}

	private MonitorList getMonitorList(App app, JSONArray ports) {
		MonitorList monitorList = new MonitorList();
		//获取应用代理ip和端口
		String appProxy=app.getAppProxy();
		String proxyIp=appProxy.split(":")[0];
		String proxyPort=appProxy.split(":")[1];
		for (int i = 0; i < ports.size(); i++) {
			JSONObject port = (JSONObject) ports.get(i);
			final Integer intAppPriPort = app.getAppPriPort();
			final Integer intPrivatePort = port.getInteger("privatePort");
			if (intPrivatePort.equals(intAppPriPort)) {
				long begingTime = System.currentTimeMillis();
				String ip = port.getString("ip");
				String pubPort = String.valueOf(port.getInteger("publicPort"));
				try {
					while (true) {
						if ((System.currentTimeMillis() - begingTime) > systemConfig.getDelayTime()) {
							LOGGER.info("获取监控列表超时");
							break;
						}
						Client client = applicationClinetBuilder.createClient(ip, pubPort,
								ApplicationClientBuilder.MONITOR_PATH, false);
						String method = applicationMethod.monitorList();
						if (client != null) {
							Object[] objs = client.invoke(method, proxyIp, Integer.parseInt(proxyPort));
							if (objs != null) {
								String monitorJson = (String) objs[0];
								monitorList = JSON.parseObject(monitorJson, MonitorList.class);
							}
							break;
						} else {
							Thread.sleep(10 * 1000);
							continue;
						}
					}
				} catch (Exception e) {
					LOGGER.error("Get item and trigger error", e);
					return null;
				}
			}
		}
		return monitorList;
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
	private Integer addContainerInfo(ContainerModel model, Host host, Cluster cluster, JSONObject conJo,
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
		container.setConDesc(model.getConDesc());
		container.setConStatus((byte) Status.CONTAINER.EXIT.ordinal());
		container.setConStartCommand(conJo.getString("runCommand"));
		container.setConStartParam(model.getCreateParams());
		container.setAppId(Integer.parseInt(model.getAppId()));
		container.setClusterIp(host.getHostIp());
		container.setClusterPort(cluster.getClusterPort());
		container.setConCreator(model.getUserId());
		container.setConCreatetime(new Date());
		/* 添加租户的ID信息 */
		container.setTenantId(model.getTenantId());
		try {
			String hostIp = "";
			// 添加端口
			if (!ports.isEmpty()) {
				hostIp = ((JSONObject) ports.get(0)).getString("ip");
				container.setMonitorHostId(model.getMonitorHostId());
				Integer hostId = getHostId(hostIp);
				hostId = hostId == -1 ? null : hostId;
				container.setHostId(hostId);
				containerService.addContaier(container);
				for (int j = 0; j < ports.size(); j++) {
					JSONObject port = (JSONObject) ports.get(j);
					addConPort(container.getConId(), port);
				}
			} else {
				containerService.addContaier(container);
				LOGGER.warn("Container's port is empty!");
			}
			if (container.getAppStatus() == (byte) Status.APP_STATUS.ABNORMAL.ordinal()) {
				String conId = container.getConId().toString();
				String[] conIds = { conId };
				stopContainer(conIds);
			}
			return 1;
		} catch (Exception e) {
			LOGGER.error("Create container error", e);
			return -1;
		}
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
		conPort.setConIp(port.getString("ip").equals("") ? null : port.getString("ip"));
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

	/**
	 * @author langzi
	 * @param hostIp
	 * @param hostType
	 * @return
	 * @version 1.0 2015年11月25日
	 */
	private Integer getHostId(String hostIp) {
		Host checkHost = new Host();
		checkHost.setHostIp(hostIp);
		checkHost.setHostType((byte) Type.HOST.DOCKER.ordinal());
		try {
			checkHost = hostService.getHostByIp(checkHost);
			return checkHost.getHostId();
		} catch (Exception e) {
			LOGGER.error("Get host id by ip and type failed", e);
			return -1;
		}
	}

	public GridBean listApp(Integer userId, int pagenumber, int pagesize, ContainerModel model) {
		try {
			return containerService.listApp(userId, pagenumber, pagesize, model);
		} catch (Exception e) {
			LOGGER.error("Get container list by app ip :(" + model.getAppId() + ") and type failed", e);
			return null;
		}
	}
	
}

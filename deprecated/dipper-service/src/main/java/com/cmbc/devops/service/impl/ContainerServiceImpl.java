package com.cmbc.devops.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cmbc.devops.bean.GridBean;
import com.cmbc.devops.constant.Status;
import com.cmbc.devops.dao.AppMapper;
import com.cmbc.devops.dao.ContainerMapper;
import com.cmbc.devops.entity.App;
import com.cmbc.devops.entity.Cluster;
import com.cmbc.devops.entity.ConAppImgPort;
import com.cmbc.devops.entity.ConPort;
import com.cmbc.devops.entity.Container;
import com.cmbc.devops.entity.Host;
import com.cmbc.devops.entity.Image;
import com.cmbc.devops.entity.expand.ContainerExpand;
import com.cmbc.devops.model.ContainerModel;
import com.cmbc.devops.model.SimpleContainer;
import com.cmbc.devops.service.AppService;
import com.cmbc.devops.service.ClusterService;
import com.cmbc.devops.service.ConportService;
import com.cmbc.devops.service.ContainerService;
import com.cmbc.devops.service.HostService;
import com.cmbc.devops.service.ImageService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

/**  
 * date：2015年8月21日 下午2:54:23  
 * project name：cmbc-devops-service  
 * @author langzi  
 * @version 1.0   
 * @since JDK 1.7.0_21  
 * file name：ContainerServiceImpl.java  
 * description：  
 */
/**
 * @author langzi
 *
 */
@Component
public class ContainerServiceImpl implements ContainerService {

	private static final Logger logger = Logger.getLogger(ContainerService.class);

	@Resource
	private ContainerMapper mapper;
	@Autowired
	private ClusterService clusterService;
	@Autowired
	private HostService hostService;
	@Autowired
	private ImageService imageService;
	@Autowired
	private AppService appSerivce;
	@Autowired
	private ConportService conportService;
	@Autowired
	private AppMapper appMapper;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cmbc.devops.service.ContainerService#listAllContainer()
	 */
	@Override
	public List<Container> listAllContainer(Container container) {
		try {
			return mapper.selectAll(container);
		} catch (Exception e) {
			logger.error("Get all containers error!");
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cmbc.devops.service.ContainerService#listContainersByAppId(java.lang.
	 * Integer)
	 */
	@Override
	public List<Container> listContainersByAppId(Integer appId) throws Exception {
		return mapper.selectContainerByAppId(appId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cmbc.devops.service.ContainerService#listAllContainersJsonArray()
	 */
	@Override
	public JSONArray listAllContainersJsonArray(Integer tenantId) {
		Container sel_container = new Container();
		sel_container.setTenantId(tenantId);
		return (JSONArray) JSONArray.toJSON(listAllContainer(sel_container));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cmbc.devops.service.ContainerService#selectContainerUuid(net.sf.json.
	 * JSONArray)
	 */
	@Override
	public List<SimpleContainer> selectContainerUuid(String[] containerIds) throws Exception {
		List<Container> containers = mapper.selectContainers(containerIds);
		List<SimpleContainer> scs = new ArrayList<SimpleContainer>();
		if (containers.size() > 0) {
			for (Container con : containers) {
				SimpleContainer sc = new SimpleContainer(con.getConUuid(), con.getClusterIp(), con.getClusterPort(),
						con.getMonitorHostId());
				sc.setContainerId(con.getConId());
				sc.setMonitorStatus(con.getMonitorStatus());
				scs.add(sc);
			}
		}
		return scs;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cmbc.devops.service.ContainerService#listOnePageContainers(java.lang.
	 * Integer, int, int, com.cmbc.devops.model.ContainerModel)
	 */
	@Override
	public GridBean listOnePageContainers(Integer userId, Integer tenantId, int pagenum, int pagesize,
			ContainerModel model) {
		PageHelper.startPage(pagenum, pagesize);
		Container container = new Container();

		if (NumberUtils.isNumber(model.getAppId())) {
			container.setAppId(Integer.parseInt(model.getAppId()));
		}
		if (model.getHostId() != null && model.getHostId() != 0) {
			container.setHostId(model.getHostId());
		}
		if (model.getClusterId() != null && model.getClusterId() != 0) {
			Cluster cluster = null;
			try {
				cluster = clusterService.getCluster(model.getClusterId());
			} catch (Exception e) {
				logger.error("User(ID:" + userId + ") in Tenant(ID:" + tenantId + ") get cluster by cluster id failed",
						e);
				return null;
			}
			Host host = new Host();
			try {
				host = hostService.loadHost(cluster.getMasteHostId());
			} catch (Exception e) {
				logger.error("User(ID:" + userId + ") in Tenant(ID:" + tenantId + ")  load host by host id failed", e);
			}
			container.setClusterIp(host.getHostIp());
			container.setClusterPort(cluster.getClusterPort());
		}
		if (model.getImageId() != null && model.getImageId() != 0) {
			container.setConImgid(model.getImageId());
		}
		if (StringUtils.hasText(model.getConName())) {
			container.setConName(model.getConName());
		}
		List<Container> containers = listAllContainer(container);
		int totalPage = ((Page<?>) containers).getPages();
		Long totalNum = ((Page<?>) containers).getTotal();
		return new GridBean(pagenum, totalPage, totalNum.intValue(), containers);
	}

	public GridBean listOnePageContainers(Integer userId, Integer tenantId, int pagenum, int pagesize, int appId) {
		PageHelper.startPage(pagenum, pagesize);
		Container container = new Container();
		if (appId != 0) {
			container.setAppId(appId);
		}
		/** @date:2016年3月28日 添加租户维度 */
		container.setTenantId(tenantId);
		List<Container> containers = listAllContainer(container);
		int totalPage = ((Page<?>) containers).getPages();
		Long totalNum = ((Page<?>) containers).getTotal();
		return new GridBean(pagenum, totalPage, totalNum.intValue(), containers);
	}

	/**
	 * 获取容器相关的全部内容，并返回给前台
	 * 
	 * @throws Exception
	 **/
	@Override
	public GridBean listContainersByAppid(Integer userId, Integer tenantId, int pagenum, int pagesize, int appId,
			Integer imageId) throws Exception {
		PageHelper.startPage(pagenum, pagesize);
		Container container = new Container();
		if (appId != 0) {
			container.setAppId(appId);
		} else {
			// 如果不存在应用ID，则直接返回为空
			return null;
		}
		if (imageId != 0) {
			container.setConImgid(imageId);
		} else {
			// 如果不存在应用ID，则直接返回为空
			return null;
		}
		List<Container> containers = listAllContainer(container);

		Page<ConAppImgPort> page_obj = new Page<ConAppImgPort>();
		for (Container single_container : containers) {
			ConAppImgPort caip = new ConAppImgPort();
			Integer container_id = single_container.getConId();
			caip.setConId(container_id);
			caip.setConUuid(single_container.getConUuid());
			Integer image_id = single_container.getConImgid();
			/** 查询嵌入镜像的版本标签 */
			if (image_id != null) {
				caip.setConImgid(single_container.getConImgid());
				Image image = imageService.loadImage(tenantId, image_id);
				String image_tag = image.getImageTag();
				if (StringUtils.hasText(image_tag)) {
					caip.setImageTag(image_tag);
				}
			}
			caip.setConCreator(single_container.getConCreator());
			caip.setConName(single_container.getConName());
			caip.setConPower(single_container.getConPower());
			caip.setAppStatus(single_container.getAppStatus());
			caip.setMonitorStatus(single_container.getMonitorStatus());
			caip.setConStatus(single_container.getConStatus());
			caip.setConStartCommand(single_container.getConStartCommand());
			caip.setConStartParam(single_container.getConStartParam());
			caip.setConCpu(single_container.getConCpu());
			caip.setConMem(single_container.getConMem());
			caip.setConDesc(single_container.getConDesc());
			caip.setAppId(appId);

			/** 查询查询注入应用的名称内容 **/
			App single_app = appSerivce.findAppById(tenantId, appId);
			String appName = single_app.getAppName();
			if (StringUtils.hasText(appName)) {
				caip.setAppName(appName);
			}

			caip.setMonitorHostId(single_container.getMonitorHostId());
			caip.setClusterIp(single_container.getClusterIp());
			caip.setClusterPort(single_container.getClusterPort());
			caip.setHostId(single_container.getHostId());
			caip.setConCreatetime(single_container.getConCreatetime());

			/** 查询容器相关的端口信息 */
			List<ConPort> conport_list = conportService.listConPorts(container_id);
			if (!conport_list.isEmpty()) {
				App appinfo = new App();
				appinfo = appMapper.select(appId);
				if (appinfo.getAppPriPort() != null) {
					int appport = appinfo.getAppPriPort();
					for (ConPort con_port : conport_list) {
						if (appport == Integer.valueOf(con_port.getPriPort())) {
							caip.setConIp(con_port.getConIp());
							caip.setConPortInfo(con_port.getPubPort() + "-->" + con_port.getPriPort());
							break;
						}
					}
				}
			}
			page_obj.add(caip);
		}

		int totalPage = ((Page<?>) containers).getPages();
		Long totalNum = ((Page<?>) containers).getTotal();
		return new GridBean(pagenum, totalPage, totalNum.intValue(), page_obj);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cmbc.devops.service.ContainerService#getContainer(com.cmbc.devops.
	 * entity.Container)
	 */
	@Override
	public Container getContainer(Container container) throws Exception {
		try {
			return mapper.selectContainer(container);
		} catch (Exception e) {
			logger.error("Get one container error");
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cmbc.devops.service.ContainerService#getContainerJsonObject(com.cmbc.
	 * devops.entity.Container)
	 */
	@Override
	public JSONObject getContainerJsonObject(Container container) throws Exception {
		return (JSONObject) JSONObject.toJSON(container);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cmbc.devops.service.ContainerService#getLastConId()
	 */
	@Override
	public Integer getLastConId() throws Exception {
		Integer lastConid = mapper.selectLastConId();
		return lastConid == null ? 0 : lastConid;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cmbc.devops.service.ContainerService#addContaier(com.cmbc.devops.
	 * entity.Container)
	 */
	@Override
	public int addContaier(Container container) throws Exception {
		return mapper.insertContainer(container);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cmbc.devops.service.ContainerService#modifyContainer(java.lang.
	 * Integer)
	 */
	@Override
	public int modifyContainer(Container container) throws Exception {
		return mapper.updateContainer(container);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cmbc.devops.service.ContainerService#modifyContainerPower(java.lang.
	 * Integer, net.sf.json.JSONArray)
	 */
	@Override
	public int modifyConStatus(ContainerExpand ce) throws Exception {
		return mapper.updateConStatus(ce);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cmbc.devops.service.ContainerService#trashContainer(java.lang.
	 * Integer)
	 */
	@Override
	public int removeContainer(Integer conId) throws Exception {
		return mapper.deleteContainer(conId);
	}

	@Override
	public List<Container> listContainersByHostId(Integer hostId) throws Exception {
		return mapper.selectContainerByHostId(hostId);
	}

	@Override
	public GridBean advancedSearchContainer(Integer userId, Integer tenantId, int pagenumber, int pagesize,
			JSONObject json_object) throws Exception {
		PageHelper.startPage(pagenumber, pagesize);

		/* 组装应用查询数据的条件 */
		Container container = new Container();

		/* 获取用户填写的各项查询条件 */
		String[] params = json_object.getString("params").split(",");
		String[] values = json_object.getString("values").split(",");
		Integer appId = json_object.getInteger("app_id");

		/* 匹配端口部分 */
		String related_port = "";
		boolean is_checkport = false;

		/* 遍历填充各项查询条件 */
		for (int i = 0; i < params.length; i++) {
			switch (params[i].trim()) {
			/* 填充实例标识（UUID的前八位）信息 */
			case "1":
				String conName = values[i].trim().toLowerCase();
				/* 对于搜索的名称进行过滤处理，去掉多余的字符 */
				if (conName.contains("c-")) {
					conName = conName.replace("c-", "");
				} else if (conName.contains("-")) {
					conName = conName.replace("-", "");
				}
				container.setConUuid(conName);
				break;
			/* 容器运行状态情况（0：已停止，1：运行中） */
			case "2":
				if ("已停止".contains(values[i])) {
					container.setConPower((byte) Status.POWER.OFF.ordinal());
				} else if ("运行中".contains(values[i])) {
					container.setConPower((byte) Status.POWER.UP.ordinal());
				} else {/* 以上状态均不包含的情况下 */
					container.setConPower((byte) Integer.MAX_VALUE);
				}
				break;
			/* 容器监控状态 （0:关闭，1：健康，2：异常，3：未检测） */
			case "3":
				if ("关闭".contains(values[i])) {
					container.setAppStatus((byte) Status.APP_STATUS.ABNORMAL.ordinal());
				} else if ("健康".contains(values[i])) {
					container.setAppStatus((byte) Status.APP_STATUS.NORMAL.ordinal());
				} else if ("异常".contains(values[i])) {
					container.setAppStatus((byte) Status.APP_STATUS.ERROR.ordinal());
				} else if ("未检测".contains(values[i])) {
					container.setAppStatus((byte) Status.APP_STATUS.UNDEFINED.ordinal());
				} else {/* 以上状态均不包含的情况下 */
					container.setAppStatus((byte) Integer.MAX_VALUE);
				}
				break;
			/* 容器监控信息 (0：未监控，1：监控中，2：未添加) */
			case "4":
				if ("监控中".contains(values[i])) {
					container.setMonitorStatus((byte) Status.MONITOR_STATUS.NORMAL.ordinal());
				} else if ("未监控".contains(values[i])) {
					container.setMonitorStatus((byte) Status.MONITOR_STATUS.ABNORMAL.ordinal());
				} else if ("未添加".contains(values[i])) {
					container.setMonitorStatus((byte) Status.MONITOR_STATUS.UNDEFINED.ordinal());
				} else {/* 以上状态均不包含的情况下 */
					container.setMonitorStatus((byte) Integer.MAX_VALUE);
				}
				break;
			/* 容器端口占用情况 */
			case "5":
				if (NumberUtils.isNumber(values[i])) {
					is_checkport = true;
					related_port = values[i];
				}
				break;
			/* 容器备注描述信息 */
			case "6":
				container.setConDesc(values[i]);
				break;
			default:
				break;
			}
		}

		/* 添加租户维度 */
		container.setTenantId(tenantId);
		container.setAppId(appId);

		List<Container> containers = mapper.selectContainersByShortUUIDAll(container);

		Page<ConAppImgPort> page_obj = new Page<ConAppImgPort>();
		for (Container single_container : containers) {
			ConAppImgPort caip = new ConAppImgPort();
			Integer container_id = single_container.getConId();
			caip.setConId(container_id);
			caip.setConUuid(single_container.getConUuid());
			Integer image_id = single_container.getConImgid();

			/* 此容器是否包含查询的端口 */
			boolean is_containport = false;

			/** 查询嵌入镜像的版本标签 */
			if (image_id != null) {
				caip.setConImgid(single_container.getConImgid());
				Image image;
				image = imageService.loadImage(tenantId, image_id);
				if (image != null) {
					String image_tag = image.getImageTag();
					if (StringUtils.hasText(image_tag)) {
						caip.setImageTag(image_tag);
					}
				}
			}
			caip.setConCreator(single_container.getConCreator());
			caip.setConName(single_container.getConName());
			caip.setConPower(single_container.getConPower());
			caip.setAppStatus(single_container.getAppStatus());
			caip.setMonitorStatus(single_container.getMonitorStatus());
			caip.setConStatus(single_container.getConStatus());
			caip.setConStartCommand(single_container.getConStartCommand());
			caip.setConStartParam(single_container.getConStartParam());
			caip.setConCpu(single_container.getConCpu());
			caip.setConMem(single_container.getConMem());
			caip.setConDesc(single_container.getConDesc());
			caip.setAppId(appId);

			/** 查询查询注入应用的名称内容 **/
			App single_app = appSerivce.findAppById(tenantId, appId);
			String appName = single_app.getAppName();
			if (StringUtils.hasText(appName)) {
				caip.setAppName(appName);
			}

			caip.setMonitorHostId(single_container.getMonitorHostId());
			caip.setClusterIp(single_container.getClusterIp());
			caip.setClusterPort(single_container.getClusterPort());
			caip.setHostId(single_container.getHostId());
			caip.setConCreatetime(single_container.getConCreatetime());

			/** 查询容器相关的端口信息 */
			String conport_info = "";
			List<ConPort> conport_list = conportService.listConPorts(container_id);

			if (!conport_list.isEmpty()) {
				App appinfo = new App();
				appinfo = appMapper.select(appId);
				if (appinfo.getAppPriPort() != null) {
					int appport = appinfo.getAppPriPort();
					for (ConPort con_port : conport_list) {
						if (appport == Integer.valueOf(con_port.getPriPort())) {
							caip.setConIp(con_port.getConIp());
							caip.setConPortInfo(con_port.getPubPort() + "-->" + con_port.getPriPort());
							break;
						}
					}
				}
			}

			if (!conport_list.isEmpty()) {
				App appinfo = new App();
				appinfo = appMapper.select(appId);
				if (appinfo.getAppPriPort() != null) {
					int appport = appinfo.getAppPriPort();
					for (ConPort con_port : conport_list) {
						if (appport == Integer.valueOf(con_port.getPriPort())) {
							caip.setConIp(con_port.getConIp());
							conport_info = con_port.getPubPort() + "-->" + con_port.getPriPort();
							/* 检测是否包含查询中请求的端口 */
							if (is_checkport) {
								if (con_port.getPubPort().equals(related_port)) {
									is_containport = true;
								} else if (con_port.getPriPort().equals(related_port)) {
									is_containport = true;
								}
							}
							break;
						}
					}
				}
			}
			/* 如果不检查端口，则直接添加到列表中 */
			if (!is_checkport) {
				caip.setConPortInfo(conport_info);
				page_obj.add(caip);
			} else if (is_containport) {
				caip.setConPortInfo(conport_info);
				page_obj.add(caip);
			}
		}

		int totalPage = ((Page<?>) page_obj).getPages();
		Long totalNum = ((Page<?>) page_obj).getTotal();
		return new GridBean(pagenumber, totalPage, totalNum.intValue(), page_obj);
	}

	@Override
	public GridBean listApp(Integer userId, int pagenumber, int pagesize, ContainerModel model) throws Exception {
		PageHelper.startPage(pagenumber, pagesize);
		List<Container> container_list = mapper.selectContainerByAppId(Integer.parseInt(model.getAppId()));
		int totalpage = ((Page<?>) container_list).getPages();
		Long totalNum = ((Page<?>) container_list).getTotal();
		GridBean gridBean = new GridBean(pagenumber, totalpage, totalNum.intValue(), container_list);
		return gridBean;
	}

	@Override
	/** 获取所有挂在应用下面的容器列表 */
	public List<Container> listAllContainerInApp(Container container) throws Exception {
		List<Container> container_list = mapper.selectAll(container);
		/** 保存所有被包含在应用下的容器链表 */
		List<Container> ret_list = new ArrayList<Container>();
		if (!container_list.isEmpty()) {
			for (Container single_con : container_list) {
				if (null != single_con.getAppId()) {
					ret_list.add(single_con);
				}
			}
			return ret_list;
		} else {
			logger.info("Get all containers in app empty!");
			return null;
		}
	}

	@Override
	public GridBean listPowerConInfo(Integer userId, Integer tenantId, int pagenumber, int pagesize, int powerStatus)
			throws Exception {
		PageHelper.startPage(pagenumber, pagesize);
		Container container = new Container();
		switch (powerStatus) {
		case 1:
			container.setConPower((byte) Status.POWER.UP.ordinal());
			break;
		case 2:
			container.setConPower((byte) Status.POWER.OFF.ordinal());
			break;
		default:
			break;
		}
		List<Container> containers = listAllContainer(container);

		Page<ConAppImgPort> page_obj = new Page<ConAppImgPort>();
		for (Container single_container : containers) {
			ConAppImgPort caip = new ConAppImgPort();
			Integer container_id = single_container.getConId();
			caip.setConId(container_id);
			caip.setConUuid(single_container.getConUuid());
			Integer image_id = single_container.getConImgid();
			/** 查询嵌入镜像的版本标签 */
			if (image_id != null) {
				caip.setConImgid(single_container.getConImgid());
				Image image = imageService.loadImage(tenantId, image_id);
				String image_tag = image.getImageTag();
				if (StringUtils.hasText(image_tag)) {
					caip.setImageTag(image_tag);
				}
			}
			caip.setConCreator(single_container.getConCreator());
			caip.setConName(single_container.getConName());
			caip.setConPower(single_container.getConPower());
			caip.setAppStatus(single_container.getAppStatus());
			caip.setMonitorStatus(single_container.getMonitorStatus());
			caip.setConStatus(single_container.getConStatus());
			caip.setConStartCommand(single_container.getConStartCommand());
			caip.setConStartParam(single_container.getConStartParam());
			caip.setConCpu(single_container.getConCpu());
			caip.setConMem(single_container.getConMem());
			caip.setConDesc(single_container.getConDesc());

			Integer app_id = single_container.getAppId();

			if (app_id != null && app_id != 0) {
				caip.setAppId(app_id);

				/** 查询查询注入应用的名称内容 **/
				App single_app = appSerivce.findAppById(tenantId, app_id);
				String appName = single_app.getAppName();
				if (StringUtils.hasText(appName)) {
					caip.setAppName(appName);
				}
			}

			caip.setMonitorHostId(single_container.getMonitorHostId());
			caip.setClusterIp(single_container.getClusterIp());
			caip.setClusterPort(single_container.getClusterPort());
			caip.setHostId(single_container.getHostId());
			caip.setConCreatetime(single_container.getConCreatetime());

			/** 查询容器相关的端口信息 */
			String conport_info = "";
			List<ConPort> conport_list = conportService.listConPorts(container_id);
			if (!conport_list.isEmpty()) {
				for (ConPort con_port : conport_list) {
					conport_info = conport_info + con_port.getConIp() + ":" + con_port.getPubPort() + "-->"
							+ con_port.getPriPort() + "\n";
				}
				conport_info = conport_info.substring(0, conport_info.length() - 1);
			}
			caip.setConPortInfo(conport_info);

			page_obj.add(caip);
		}

		int totalPage = ((Page<?>) containers).getPages();
		Long totalNum = ((Page<?>) containers).getTotal();
		return new GridBean(pagenumber, totalPage, totalNum.intValue(), page_obj);
	}

	@SuppressWarnings("null")
	@Override
	public GridBean listSearchConIns(Integer userId, Integer tenantId, int pagenumber, int pagesize,
			JSONObject param_json) throws Exception {
		PageHelper.startPage(pagenumber, pagesize);
		/* 获取查询的标识关键字和所属应用ID信息 */
		String search_name = param_json.getString("searchName");
		Integer app_id = param_json.getInteger("appId");

		Container container = new Container();
		List<Container> containers = null;

		if (app_id != 0) {
			container.setAppId(app_id);
		} else {
			// 如果不存在应用ID，则直接返回为空
			int totalPage = ((Page<?>) containers).getPages();
			Long totalNum = ((Page<?>) containers).getTotal();
			return new GridBean(pagenumber, totalPage, totalNum.intValue(), containers);
		}
		if (StringUtils.hasText(search_name)) {
			container.setConUuid(search_name);
		} else {
			// 如果标识关键字为空，则直接返回为空
			int totalPage = ((Page<?>) containers).getPages();
			Long totalNum = ((Page<?>) containers).getTotal();
			return new GridBean(pagenumber, totalPage, totalNum.intValue(), containers);
		}

		containers = mapper.selectContainersByShortUUIDAll(container);

		Page<ConAppImgPort> page_obj = new Page<ConAppImgPort>();
		for (Container single_container : containers) {
			ConAppImgPort caip = new ConAppImgPort();
			Integer container_id = single_container.getConId();
			caip.setConId(container_id);
			caip.setConUuid(single_container.getConUuid());
			Integer image_id = single_container.getConImgid();
			/** 查询嵌入镜像的版本标签 */
			if (image_id != null) {
				caip.setConImgid(single_container.getConImgid());
				Image image = imageService.loadImage(tenantId, image_id);
				String image_tag = image.getImageTag();
				if (StringUtils.hasText(image_tag)) {
					caip.setImageTag(image_tag);
				}
			}
			caip.setConCreator(single_container.getConCreator());
			caip.setConName(single_container.getConName());
			caip.setConPower(single_container.getConPower());
			caip.setAppStatus(single_container.getAppStatus());
			caip.setMonitorStatus(single_container.getMonitorStatus());
			caip.setConStatus(single_container.getConStatus());
			caip.setConStartCommand(single_container.getConStartCommand());
			caip.setConStartParam(single_container.getConStartParam());
			caip.setConCpu(single_container.getConCpu());
			caip.setConMem(single_container.getConMem());
			caip.setConDesc(single_container.getConDesc());
			caip.setAppId(app_id);

			/** 查询查询注入应用的名称内容 **/
			App single_app = appSerivce.findAppById(tenantId, app_id);
			String appName = single_app.getAppName();
			if (StringUtils.hasText(appName)) {
				caip.setAppName(appName);
			}

			caip.setMonitorHostId(single_container.getMonitorHostId());
			caip.setClusterIp(single_container.getClusterIp());
			caip.setClusterPort(single_container.getClusterPort());
			caip.setHostId(single_container.getHostId());
			caip.setConCreatetime(single_container.getConCreatetime());

			/** 查询容器相关的端口信息 */
			String conport_info = "";
			List<ConPort> conport_list = conportService.listConPorts(container_id);
			if (!conport_list.isEmpty()) {
				for (ConPort con_port : conport_list) {
					conport_info = conport_info + con_port.getConIp() + ":" + con_port.getPubPort() + "-->"
							+ con_port.getPriPort() + "\n";
				}
				conport_info = conport_info.substring(0, conport_info.length() - 1);
			}
			caip.setConPortInfo(conport_info);

			page_obj.add(caip);
		}

		int totalPage = ((Page<?>) containers).getPages();
		Long totalNum = ((Page<?>) containers).getTotal();
		return new GridBean(pagenumber, totalPage, totalNum.intValue(), page_obj);
	}

	public List<SimpleContainer> selectContainerByImageId(Integer imageId, Integer flag) throws Exception {
		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put("imageId", imageId);
		map.put("conNum", flag);
		List<Container> containers = mapper.selectContainerByImageId(map);
		List<SimpleContainer> scs = new ArrayList<SimpleContainer>();
		if (containers.size() > 0) {
			for (Container con : containers) {
				SimpleContainer sc = new SimpleContainer(con.getConUuid(), con.getClusterIp(), con.getClusterPort(),
						con.getMonitorHostId());
				sc.setContainerId(con.getConId());
				sc.setMonitorStatus(con.getMonitorStatus());
				scs.add(sc);
			}
		}
		return scs;
	}
}

package com.cmbc.devops.manager;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cmbc.devops.bean.GridBean;
import com.cmbc.devops.bean.Result;
import com.cmbc.devops.entity.App;
import com.cmbc.devops.entity.AppAuxiliaryCluster;
import com.cmbc.devops.entity.AppEnvImg;
import com.cmbc.devops.entity.AppViewElement;
import com.cmbc.devops.entity.AppViewInfo;
import com.cmbc.devops.entity.Cluster;
import com.cmbc.devops.entity.ClusterApp;
import com.cmbc.devops.entity.ClusterAuxiliaryImage;
import com.cmbc.devops.entity.Container;
import com.cmbc.devops.entity.Env;
import com.cmbc.devops.entity.EnvApp;
import com.cmbc.devops.entity.Host;
import com.cmbc.devops.entity.Image;
import com.cmbc.devops.entity.ImageAuxiliaryContainer;
import com.cmbc.devops.service.AppEnvImgService;
import com.cmbc.devops.service.AppService;
import com.cmbc.devops.service.ClusterAppService;
import com.cmbc.devops.service.ClusterService;
import com.cmbc.devops.service.ContainerService;
import com.cmbc.devops.service.EnvAppService;
import com.cmbc.devops.service.EnvService;
import com.cmbc.devops.service.HostService;
import com.cmbc.devops.service.ImageService;

/**
 * date：2016年1月5日 下午2:50:23 project name：cmbc-devops-web
 * 
 * @author langzi
 * @version 1.0
 * @since JDK 1.7.0_21 file name：AppManager.java description：
 */
@Component
public class AppManager {

	private static final Logger LOGGER = Logger.getLogger(AppManager.class);

	@Autowired
	private AppService appService;
	@Autowired
	private ClusterAppService clusterAppService;
	@Autowired
	private EnvAppService envAppService;
	@Autowired
	private ClusterService clusterService;
	@Autowired
	private EnvService envService;
	@Autowired
	private ContainerService containerService;
	@Autowired
	private HostService hostService;
	@Autowired
	private ImageService imageService;
	@Autowired
	private AppEnvImgService appEnvImgService;

	/**
	 * @author langzi
	 * @param app
	 * @return
	 * @version 1.0 2016年1月5日
	 */
	public Result addApp(App app) {
		int addResult = 0;
		try {
			addResult = appService.addApp(app);
			int appId = appService.findLastAppId();
			String[] clusterIds = app.getClusterIds().split(",");
			for (String clusterId : clusterIds) {
				ClusterApp ca = new ClusterApp();
				ca.setAppId(appId);
				ca.setClusterId(Integer.parseInt(clusterId));
				clusterAppService.addClusterApp(ca);
			}
			String[] envIds = app.getEnvIds().split(",");
			for (String envId : envIds) {
				EnvApp ea = new EnvApp();
				ea.setAppId(appId);
				ea.setEnvId(Integer.parseInt(envId));
				envAppService.add(ea);
			}
		} catch (Exception e) {
			LOGGER.error("add app error", e);
		}
		if (addResult > 0) {
			return new Result(true, "应用创建成功！");
		} else {
			return new Result(false, "应用创建失败！");
		}
	}

	/**
	 * @author langzi
	 * @param app
	 * @return
	 * @version 1.0 2016年1月5日
	 */
	public Result modifyApp(App app) {
		int modifyResult = 0;
		try {
			modifyResult = appService.modifyApp(app);
			// 修改成功
			if (modifyResult > 0) {
				String[] clusterIds = app.getClusterIds().split(",");
				if (clusterIds.length > 0) {
					Integer[] appIds = { app.getAppId() };
					clusterAppService.removeClusterAppByAppId(appIds);
					for (String clusterId : clusterIds) {
						ClusterApp ca = new ClusterApp();
						ca.setAppId(app.getAppId());
						ca.setClusterId(Integer.parseInt(clusterId));
						clusterAppService.addClusterApp(ca);
					}
				}
				String[] envIds = app.getEnvIds().split(",");
				if (envIds.length > 0) {
					envAppService.removeByAppId(app.getAppId());
					for (String envId : envIds) {
						EnvApp ea = new EnvApp();
						ea.setAppId(app.getAppId());
						ea.setEnvId(Integer.parseInt(envId));
						envAppService.add(ea);
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error("add app error", e);
		}
		if (modifyResult > 0) {
			return new Result(true, "应用更新成功！");
		} else {
			return new Result(false, "应用更新失败！");
		}
	}

	/**
	 * @author langzi
	 * @param appId
	 * @return
	 * @version 1.0 2016年1月5日
	 */
	public Result removeApp(int appId) {
		int removeResult = 0;
		try {
			if (!containerService.listContainersByAppId(appId).isEmpty()) {
				return new Result(false, "应用存在实例，不允许删除");
			}
			removeResult = appService.removeApp(appId);
			Integer[] appIds = { appId };
			clusterAppService.removeClusterAppByAppId(appIds);
			envAppService.removeByAppId(appId);
		} catch (Exception e) {
			LOGGER.error("add app error", e);
		}
		if (removeResult > 0) {
			return new Result(true, "应用删除成功！");
		} else {
			return new Result(false, "应用删除失败！");
		}
	}

	/**
	 * @author langzi
	 * @param envId
	 * @return
	 * @version 1.0 2016年1月12日
	 */
	public JSONArray listAppByEnvId(int tenantId, int envId) {
		JSONArray array = new JSONArray();
		try {
			List<EnvApp> envApps = envAppService.listAllByEnvId(envId);
			List<App> apps = new ArrayList<App>();
			if (envApps.isEmpty()) {
				return array;
			}
			for (EnvApp envApp : envApps) {
				App app = appService.findAppById(tenantId, envApp.getAppId());
				if (app != null) {
					apps.add(app);
				}
			}
			return (JSONArray) JSONArray.toJSON(apps);
		} catch (Exception e) {
			LOGGER.error("list all app by envId error", e);
			return array;
		}
	}

	/**
	 * @author langzi
	 * @param appId
	 * @return
	 * @version 1.0 2016年1月5日
	 */
	public App getAppbyId(int tenantId, int appId) {
		App app = new App();
		try {
			app = appService.findAppById(tenantId, appId);
			List<Cluster> clusters = clusterService.getClustersByAppId(appId);
			String clusterName = "";
			for (Cluster cluster : clusters) {
				clusterName += cluster.getClusterName() + ",";
			}
			app.setClusterNames(clusterName);

			List<Env> envs = envService.listByAppId(appId);
			String envsName = "";
			for (Env enva : envs) {
				envsName += enva.getEnvName() + ",";
			}
			app.setEnvNames(envsName);
		} catch (Exception e) {
			LOGGER.error("getAppbyId[" + appId + "] error", e);
		}
		return app;
	}

	/**
	 * @author langzi
	 * @param appId
	 * @return
	 * @version 1.0 2016年1月5日
	 */
	public JSONObject getOne(int tenantId, int appId) {
		JSONObject json = new JSONObject();
		try {
			App app = appService.findAppById(tenantId, appId);
			List<Cluster> clusters = clusterService.getClustersByAppId(appId);
			List<Env> envs = envService.listByAppId(appId);
			json.put("app", JSONObject.toJSON(app));
			json.put("clusterList", JSONArray.toJSON(clusters));
			json.put("envList", envs);
		} catch (Exception e) {
			LOGGER.error("getAppbyId[" + appId + "] error", e);
		}
		return json;
	}

	public JSONArray listAppInLb(int lbId, int tenantId) {
		try {
			List<App> apps = appService.listAppInLb(lbId, tenantId);
			return apps.isEmpty() ? null : (JSONArray) JSONArray.toJSON(apps);
		} catch (Exception e) {
			LOGGER.error("list app in loadbalance error", e);
			return null;
		}
	}

	public JSONArray listAppNotInLb(int tenantId) {
		try {
			List<App> apps = appService.listAppNotInLb(tenantId);
			return apps.isEmpty() ? null : (JSONArray) JSONArray.toJSON(apps);
		} catch (Exception e) {
			LOGGER.error("list app not in loadbalance error", e);
			return null;
		}
	}

	/** 基于多租户的场景，透明传输租户资源ID */
	public JSONArray queryAppResInfo(int tenantId) {
		/* 设置空对象，用于返回给请求 */
		JSONArray retnull_array = new JSONArray();

		/* （1）首先获取全部的应用列表 */
		List<App> app_list = null;
		try {
			app_list = appService.listAll_TID(tenantId);
		} catch (Exception e) {
			LOGGER.error("list all app exception.", e);
		}

		/* 取小数点后两位 */
		DecimalFormat dec_fmt = new DecimalFormat("##0.00");

		/* 包含多应用的返回列表 */
		List<AppViewInfo> avi_list = new ArrayList<AppViewInfo>();

		/* 初始化应用使用的CPU和内存量 */
		Integer app_cpus = 0;
		Integer app_mems = 0;
		/* （2）遍历应用，获得资源（CPU和内存）使用情况 */
		if (!app_list.isEmpty()) {

			/* 保存全部应用使用CPU和总CPU数量 */
			Integer appcomsume_cpus = 0;
			Integer intotal_cpus = 0;

			/* 保存全部应用占用的内存量和总内存量 */
			Integer appcomsume_mems = 0;
			Integer intotal_mems = 0;

			/* 第一个显示全部应用内容的视图 */
			AppViewInfo first_view = new AppViewInfo();
			first_view.setAppId(0);
			first_view.setAppName("全部应用");

			/* 首先统计获取全部的应用所占用的资源信息，包含CPU和内存 */
			for (App tempsin_app : app_list) {
				if (tempsin_app.getAppCpu() != null) {
					appcomsume_cpus += tempsin_app.getAppCpu();
				}
				if (tempsin_app.getAppMem() != null) {
					appcomsume_mems += tempsin_app.getAppMem();
				}
			}

			/* 获取全部集群的资源量 */
			try {
				List<Cluster> cluster_list = clusterService.listAllCluster();
				if (!cluster_list.isEmpty()) {
					for (Cluster tempsin_cluster : cluster_list) {
						List<Host> allhost_list = hostService.listHostByClusterId(tempsin_cluster.getClusterId());
						if (!allhost_list.isEmpty()) {
							for (Host tempsin_host : allhost_list) {
								intotal_cpus += tempsin_host.getHostCpu();
								intotal_mems += tempsin_host.getHostMem();
							}
						}
					}
				}
			} catch (Exception e1) {
				LOGGER.error("list all cluster_list  exception.", e1);
			}

			/* 分别计算整体的CPU和内存资源的使用率 */
			Float totalcpu_userate = 0.00f;
			Float totalcpu_freerate = 0.00f;
			Float totalmem_userate = 0.00f;
			Float totalmem_freerate = 0.00f;

			/** 当初始化无资源的情况下，CPU和内存资源为0，做判断理。 */
			if (intotal_cpus > 0) {
				totalcpu_userate = Float
						.parseFloat(dec_fmt.format(((float) appcomsume_cpus / (float) intotal_cpus) * 100));
				totalcpu_freerate = Float
						.parseFloat(dec_fmt.format(100 - ((float) appcomsume_cpus / (float) intotal_cpus) * 100));
			}
			if (intotal_mems > 0) {
				totalmem_userate = Float
						.parseFloat(dec_fmt.format(((float) appcomsume_mems / (float) intotal_mems) * 100));
				totalmem_freerate = Float
						.parseFloat(dec_fmt.format(100 - ((float) appcomsume_mems / (float) intotal_mems) * 100));
			}

			List<AppViewElement> allc_list = new ArrayList<AppViewElement>();
			AppViewElement allcpuused_ele = new AppViewElement("已用CPU(" + appcomsume_cpus + "核)", totalcpu_userate,
					"#2091CF");
			AppViewElement allcpufree_ele = new AppViewElement("空闲CPU(" + (intotal_cpus - appcomsume_cpus) + "核)",
					totalcpu_freerate, "#68BC31");
			allc_list.add(allcpuused_ele);
			allc_list.add(allcpufree_ele);

			List<AppViewElement> allm_list = new ArrayList<AppViewElement>();
			AppViewElement allmemused_ele = new AppViewElement("已用内存(" + appcomsume_mems + "MB)", totalmem_userate,
					"#1081BF");
			AppViewElement allmemfree_ele = new AppViewElement("空闲内存(" + (intotal_mems - appcomsume_mems) + "MB)",
					totalmem_freerate, "#58AC21");
			allm_list.add(allmemused_ele);
			allm_list.add(allmemfree_ele);

			first_view.setCpuinfoList(allc_list);
			first_view.setMeminfoList(allm_list);

			/* 插入第一个显示全部资源使用情况的元素 */
			avi_list.add(first_view);

			for (App single_app : app_list) {
				/* 初始化返回的对象信息 */
				AppViewInfo ret_view = new AppViewInfo();

				/* 对于应用的CPU和内存量做判空处理 */
				app_cpus = single_app.getAppCpu() == null ? 0 : single_app.getAppCpu();
				app_mems = single_app.getAppMem() == null ? 0 : single_app.getAppMem();

				/* 组装单个应用相关的信息 */
				ret_view.setAppId(single_app.getAppId());
				ret_view.setAppName(single_app.getAppName());

				/* （3）获取应用对应的所有集群信息 */
				Integer cluster_cpus = 0;
				Integer cluster_mems = 0;
				Integer occupy_cpus = 0;
				Integer occupy_mems = 0;
				Integer[] appids = new Integer[1];
				appids[0] = single_app.getAppId();
				List<Integer> cluid_list = new ArrayList<Integer>();
				try {
					List<ClusterApp> cluapp_list = clusterAppService.listClusterAppsByAppId(appids);
					if (!cluapp_list.isEmpty()) {
						/* 循环获取全部集群的ID信息 */
						for (ClusterApp single_ca : cluapp_list) {
							cluid_list.add(single_ca.getClusterId());
						}
						/* (4)循环通过集群的ID获取集群主机的信息 */
						for (Integer clu_id : cluid_list) {
							List<Host> host_list = hostService.listHostByClusterId(clu_id);
							if (host_list.isEmpty()) {
								continue;
							} else {
								for (Host single_host : host_list) {
									cluster_cpus += single_host.getHostCpu();
									cluster_mems += single_host.getHostMem();
								}
							}

							/* 在通过集群的ID反推集群中其他应用所占的资源信息，并统计 */
							Integer[] clu_ids = new Integer[1];
							clu_ids[0] = clu_id;
							List<ClusterApp> cluapp_byc_list = clusterAppService.listClusterAppsByClusterId(clu_ids);
							for (ClusterApp singca_byc : cluapp_byc_list) {
								Integer checkapp_id = singca_byc.getAppId();
								if (checkapp_id != single_app.getAppId()) {
									App check_app = appService.findAppById(tenantId, checkapp_id);
									if (check_app.getAppCpu() != null) {
										occupy_cpus += check_app.getAppCpu();
									}
									if (check_app.getAppMem() != null) {
										occupy_mems += check_app.getAppMem();
									}
								} else {
									continue;
								}
							}
						}
					} else {
						LOGGER.error("Get cluster list by appid (" + appids + ") is empty.");
						return retnull_array;
					}

					/* 组装CPU对象数组 */
					Float cpuused_rate = 0.00f;

					if (cluster_cpus > 0) {
						cpuused_rate = Float
								.parseFloat(dec_fmt.format(((float) app_cpus / (float) cluster_cpus) * 100));
					}
					Float cpuoccupy_rate = 0.0f;
					Float cpufree_rate = 0.0f;
					/* 统计空闲的CPU数量 */
					Integer free_cpus = 0;
					/* 当其他应用占用CPU资源的情况下 */
					if (cluster_cpus > 0) {
						if (occupy_cpus != 0) {
							cpuoccupy_rate = Float
									.parseFloat(dec_fmt.format(((float) occupy_cpus / (float) cluster_cpus) * 100));
							cpufree_rate = Float
									.parseFloat(dec_fmt.format(100 - ((float) app_cpus / (float) cluster_cpus) * 100
											- ((float) occupy_cpus / (float) cluster_cpus) * 100));
							free_cpus = cluster_cpus - app_cpus - occupy_cpus;
						} else {
							cpufree_rate = Float
									.parseFloat(dec_fmt.format(100 - ((float) app_cpus / (float) cluster_cpus) * 100));
							free_cpus = cluster_cpus - app_cpus;
						}
					}

					List<AppViewElement> avec_list = new ArrayList<AppViewElement>();
					AppViewElement cpuused_ele = new AppViewElement("已用CPU(" + app_cpus + "核)", cpuused_rate,
							"#2091CF");
					AppViewElement cpufree_ele = new AppViewElement("空闲CPU(" + free_cpus + "核)", cpufree_rate,
							"#68BC31");

					AppViewElement cpuoccupy_ele = null;
					/* 当其他应用占用CPU资源的情况下 */
					if (occupy_cpus != 0) {
						cpuoccupy_ele = new AppViewElement("其它占用(" + occupy_cpus + "核)", cpuoccupy_rate, "#DA5430");
					}
					avec_list.add(cpuused_ele);
					avec_list.add(cpufree_ele);
					if (occupy_cpus != 0) {
						avec_list.add(cpuoccupy_ele);
					}
					ret_view.setCpuinfoList(avec_list);

					/* 组装MEM对象数组 */
					Float memused_rate = 0.00f;

					if (cluster_mems > 0) {
						memused_rate = Float
								.parseFloat(dec_fmt.format(((float) app_mems / (float) cluster_mems) * 100));
					}
					Float memoccupy_rate = 0.0f;
					Float memfree_rate = 0.0f;
					/* 统计空闲的内存容量 */
					Integer free_mems = 0;
					if (cluster_mems > 0) {
						if (occupy_mems != 0) {
							memoccupy_rate = Float
									.parseFloat(dec_fmt.format(((float) occupy_mems / (float) cluster_mems) * 100));
							memfree_rate = Float
									.parseFloat(dec_fmt.format(100 - ((float) app_mems / (float) cluster_mems) * 100
											- ((float) occupy_mems / (float) cluster_mems) * 100));
							free_mems = cluster_mems - app_mems - occupy_mems;
						} else {
							memfree_rate = Float
									.parseFloat(dec_fmt.format(100 - ((float) app_mems / (float) cluster_mems) * 100));
							free_mems = cluster_mems - app_mems;
						}
					}

					List<AppViewElement> avem_list = new ArrayList<AppViewElement>();
					AppViewElement memused_ele = new AppViewElement("已用内存(" + app_mems + "MB)", memused_rate,
							"#1081BF");
					AppViewElement memfree_ele = new AppViewElement("空闲内存(" + free_mems + "MB)", memfree_rate,
							"#58AC21");

					AppViewElement memoccupy_ele = null;
					if (occupy_mems != 0) {
						memoccupy_ele = new AppViewElement("其他占用(" + occupy_mems + "MB)", memoccupy_rate, "#CA4420");
					}

					avem_list.add(memused_ele);
					avem_list.add(memfree_ele);
					if (occupy_mems != 0) {
						avem_list.add(memoccupy_ele);
					}
					ret_view.setMeminfoList(avem_list);
					avi_list.add(ret_view);

				} catch (Exception e) {
					LOGGER.error("list all clusterapp by appid(" + appids + ") exception.", e);
				}
			}
			return (JSONArray) JSONArray.toJSON(avi_list);
		} else {
			/* 当应用为空的情况下，仍然显示剩余资源的情况 */
			/* 保存全部应用使用CPU和总CPU数量 */
			Integer intotalCpus = 0;

			/* 保存全部应用占用的内存量和总内存量 */
			Integer intotalMems = 0;

			/* 第一个显示全部应用内容的视图 */
			AppViewInfo first_view = new AppViewInfo();
			first_view.setAppId(0);
			first_view.setAppName("全部应用");

			/* 获取全部集群的资源量 */
			try {
				List<Cluster> clusters = clusterService.listAllCluster();
				if (clusters.isEmpty()) {
					return retnull_array;
				}
				for (Cluster cluster : clusters) {
					List<Host> hosts = hostService.listHostByClusterId(cluster.getClusterId());
					if (!hosts.isEmpty()) {
						for (Host host : hosts) {
							intotalCpus += host.getHostCpu();
							intotalMems += host.getHostMem();
						}
					}
				}
			} catch (Exception e1) {
				LOGGER.error("list all cluster_list  exception.", e1);
			}

			/* 分别计算整体的CPU和内存资源的使用率 */
			Float totalcpu_freerate = Float.parseFloat(dec_fmt.format(100));
			Float totalmem_freerate = Float.parseFloat(dec_fmt.format(100));

			List<AppViewElement> allc_list = new ArrayList<AppViewElement>();
			AppViewElement allcpufree_ele = new AppViewElement("空闲CPU(" + (intotalCpus) + "核)", totalcpu_freerate,
					"#68BC31");
			allc_list.add(allcpufree_ele);

			List<AppViewElement> allm_list = new ArrayList<AppViewElement>();
			AppViewElement allmemfree_ele = new AppViewElement("空闲内存(" + (intotalMems) + "MB)", totalmem_freerate,
					"#58AC21");
			allm_list.add(allmemfree_ele);

			first_view.setCpuinfoList(allc_list);
			first_view.setMeminfoList(allm_list);

			/* 插入第一个显示全部资源使用情况的元素 */
			avi_list.add(first_view);

			return (JSONArray) JSONArray.toJSON(avi_list);
		}
	}

	/* 用户通过应用名称模糊查询符合的应用列表 */
	public GridBean listSearch(int tenantId, int pagenumber, int pagesize, String search_name) {
		try {
			return appService.listSearchApps(tenantId, pagenumber, pagesize, search_name);
		} catch (Exception e) {
			LOGGER.error("select app list by fuzzy name failed.", e);
		}
		return null;
	}

	public GridBean advancedSearchApp(Integer tenantId, int pagenumber, int pagesize, JSONObject json_object) {
		try {
			return appService.advancedSearchApp(tenantId, pagenumber, pagesize, json_object);
		} catch (Exception e) {
			LOGGER.error("advanced search app list failed.param is:" + json_object.toJSONString(), e);
			return null;
		}
	}

	/**
	 * @author yangqinglin
	 * @time 2016年3月9日
	 * @description 根据应用的ID获取应用拓扑图（应用-->集群-->镜像版本-->容器列表）
	 */
	public JSONObject getAppTopoJson(Integer tenantId, Integer appId) {
		JSONObject ret_appjson = new JSONObject();

		/* (1)通过AppId获取应用的信息 */
		App sel_app = null;
		try {
			sel_app = appService.findAppById(tenantId, appId);
		} catch (Exception e1) {
			LOGGER.error("query app from database by id :" + appId + " failed", e1);
		}
		/* 判断应用对象是否为空，为空则直接返回 */
		if (sel_app == null) {
			return ret_appjson;
		}

		/* 初始化返回的树结构对象 */
		AppAuxiliaryCluster appauxcluster = new AppAuxiliaryCluster();
		/* 复制获取全部应用属性内容 */
		BeanUtils.copyProperties(sel_app, appauxcluster);

		/* (2)根据AppId获取所有的容器列表 */
		List<Container> allcon_list = null;
		try {
			allcon_list = containerService.listContainersByAppId(appId);
		} catch (Exception e1) {
			LOGGER.error("query all container list from database by app id :" + appId + " failed", e1);
		}

		/* (3)通过应用于集群的映射关系获取应用下所有集群的列表 */
		Integer[] appid_array = new Integer[1];
		appid_array[0] = appId;
		List<ClusterApp> cluapp_list = null;
		try {
			cluapp_list = clusterAppService.listClusterAppsByAppId(appid_array);
		} catch (Exception e1) {
			LOGGER.error("query all cluapp_list from database by app id :" + StringUtils.join(appid_array) + " failed",
					e1);
		}

		if (cluapp_list.isEmpty()) {
			/* 组装应用对象，返回给前台 */
			ret_appjson = (JSONObject) JSONObject.toJSON(sel_app);
			return ret_appjson;
		} else {
			/* 初始化应用树状对象下的集群树状对象列表 */
			List<ClusterAuxiliaryImage> cluauximg_list = new ArrayList<ClusterAuxiliaryImage>();
			for (ClusterApp sin_cluapp : cluapp_list) {
				/* 初始化集群镜像树状结构对象 */
				ClusterAuxiliaryImage clusterauximage = new ClusterAuxiliaryImage();

				/* 保存此集群下面的所有容器链表 */
				List<Container> belong_containers = new ArrayList<Container>();
				Integer cluster_id = sin_cluapp.getClusterId();
				Cluster sin_cluster = null;
				try {
					sin_cluster = clusterService.getCluster(cluster_id);
				} catch (Exception e1) {
					LOGGER.error("query cluster from database by cluster id :" + cluster_id + " failed", e1);
				}
				/* 复制集群全部的属性信息 */
				BeanUtils.copyProperties(sin_cluster, clusterauximage);

				List<Host> host_list = null;
				try {
					host_list = hostService.listHostByClusterId(cluster_id);
				} catch (Exception e1) {
					LOGGER.error("query host list from database by cluster id :" + cluster_id + " failed", e1);
				}
				if (!host_list.isEmpty()) {
					for (Host sin_host : host_list) {
						Integer host_id = sin_host.getHostId();

						/* 方式1：遍历总的容器链表，存在主机ID匹配的就加入到belong_containers中 */
						for (Container single_container : allcon_list) {
							if (single_container.getHostId() == host_id) {
								belong_containers.add(single_container);
							}
						}
					}

					/* 遍历全部主机列表完成，将容器根据所属镜像类型进行分类(重复的镜像ID去除) */
					Set<Integer> imageid_set = new HashSet<Integer>();
					for (Container sin_container : belong_containers) {
						imageid_set.add(sin_container.getConImgid());
					}

					Iterator<Integer> iterator = imageid_set.iterator();
					/* 初始化保存镜像树状结构的链表 */
					List<ImageAuxiliaryContainer> imgauxcon_list = new ArrayList<ImageAuxiliaryContainer>();
					while (iterator.hasNext()) {
						Integer image_id = iterator.next();
						Image sel_image = null;
						try {
							sel_image = imageService.loadImage(tenantId, image_id);
						} catch (Exception e) {
							LOGGER.error("query image object from database by image id :" + image_id + " failed", e);
						}
						ImageAuxiliaryContainer imgaux_con = new ImageAuxiliaryContainer();

						/* 将查询到的镜像所有的属性拷贝到新对象中 */
						BeanUtils.copyProperties(sel_image, imgaux_con);

						List<Container> img_con_list = new ArrayList<Container>();
						/* 遍历此集群中的容器，判断与此镜像相匹配 */
						for (Container sin_container : belong_containers) {
							if (sin_container.getConImgid() == image_id) {
								img_con_list.add(sin_container);
							}
						}
						imgaux_con.setContainerList(img_con_list);

						/* 将组装好的镜像树状结构插入链表 */
						imgauxcon_list.add(imgaux_con);
					}

					/* 向集群树状结构对象插入镜像树状结构对象 */
					clusterauximage.setImageList(imgauxcon_list);
				}
				/* 插入集群树状结构对象 */
				cluauximg_list.add(clusterauximage);
			}
			/* 向应用树状结构对象插入集群树状结构对象列表 */
			appauxcluster.setClusterList(cluauximg_list);
		}

		ret_appjson = (JSONObject) JSONObject.toJSON(appauxcluster);
		return ret_appjson;
	}

	/**
	 * @param request
	 * @param envId
	 * @return
	 */
	public int checkAppInEnv(int appId, int envId) {
		return appService.checkAppInEnv(appId, envId);
	}

	/**
	 * @param request
	 * @param clusterPort
	 * @param appId
	 * @return
	 */
	public int checkAppInCluster(String clusterPort, int appId) {
		return appService.checkAppInCluster(clusterPort, appId);
	}

	/** 通过应用的ID获取环境的列表JSONArray */
	public JSONArray getEnvsByAppId(Integer tenantId, Integer appid) {
		try {
			List<Env> envs = envService.listByAppId(appid);
			return (JSONArray) JSONArray.toJSON(envs);
		} catch (Exception e) {
			LOGGER.error("query env list from database by app id :" + appid + " failed", e);
		}
		return null;
	}

	/** 通过应用的ID和镜像id获取环境的列表JSONArray */
	public JSONArray getEnvsByImageId(Integer appid, Integer imageid) {
		try {
			List<AppEnvImg> envs = appEnvImgService.listByAppIdAndImageId(appid, imageid);
			return (JSONArray) JSONArray.toJSON(envs);
		} catch (Exception e) {
			LOGGER.error("query env list from database by app id :" + appid + " failed", e);
		}
		return null;
	}

	public JSONArray getEnvByImgId(Integer imageid) {
		try {
			List<AppEnvImg> envs = appEnvImgService.listAllByImgId(imageid);
			return (JSONArray) JSONArray.toJSON(envs);
		} catch (Exception e) {
			LOGGER.error("query env list from database by image id :" + imageid + " failed", e);
		}
		return null;
	}

	public Boolean checkAppName(String appName) {
		try {
			return appService.getAppByName(appName) == null ? true : false;
		} catch (Exception e) {
			LOGGER.error("get App by appName[" + appName + "] failed!", e);
			return false;
		}
	}

	public Boolean checkAppUrl(Integer balanceId, String appUrl) {
		try {
			return appService.getAppByLbAndUrl(balanceId,appUrl) == null ? true : false;
		} catch (Exception e) {
			LOGGER.error("get App by loadbalanceId[" + balanceId + "] and appUrl ["+appUrl+"]failed!", e);
			return false;
		}
	}
}

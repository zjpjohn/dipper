package com.cmbc.devops.manager;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cmbc.devops.bean.GridBean;
import com.cmbc.devops.bean.Result;
import com.cmbc.devops.constant.NormalConstant;
import com.cmbc.devops.constant.Status;
import com.cmbc.devops.entity.App;
import com.cmbc.devops.entity.AppViewElement;
import com.cmbc.devops.entity.AppViewInfo;
import com.cmbc.devops.entity.Cluster;
import com.cmbc.devops.entity.ClusterApp;
import com.cmbc.devops.entity.DkResource;
import com.cmbc.devops.entity.Env;
import com.cmbc.devops.entity.EnvApp;
import com.cmbc.devops.entity.Host;
import com.cmbc.devops.entity.Image;
import com.cmbc.devops.entity.Parameter;
import com.cmbc.devops.entity.Tenant;
import com.cmbc.devops.entity.User;
import com.cmbc.devops.entity.UserRole;
import com.cmbc.devops.model.TenantModel;
import com.cmbc.devops.service.AppService;
import com.cmbc.devops.service.ClusterAppService;
import com.cmbc.devops.service.ClusterService;
import com.cmbc.devops.service.ContainerService;
import com.cmbc.devops.service.EnvAppService;
import com.cmbc.devops.service.EnvService;
import com.cmbc.devops.service.HostService;
import com.cmbc.devops.service.ImageService;
import com.cmbc.devops.service.ParameterService;
import com.cmbc.devops.service.ResourceService;
import com.cmbc.devops.service.TenantService;
import com.cmbc.devops.service.UserService;
import com.cmbc.devops.util.HashUtil;

/**
 * date：2016年1月5日 下午2:50:23 project name：cmbc-devops-web
 * 
 * @author langzi
 * @version 1.0
 * @since JDK 1.7.0_21 file name：AppManager.java description：
 */
@Component
public class TenantManager {

	private final static Logger LOGGER = Logger.getLogger(TenantManager.class);

	@Autowired
	private TenantService tenantService;

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
	private ParameterService paramService;
	@Autowired
	private ResourceService resourceService;
	@Autowired
	private UserService userService;

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
	public JSONArray listAppByEnvId(int tenant_id, int envId) {
		JSONArray array = new JSONArray();
		try {
			List<EnvApp> envApps = envAppService.listAllByEnvId(envId);
			List<App> apps = new ArrayList<App>();
			if (envApps.isEmpty()) {
				return array;
			}
			for (EnvApp envApp : envApps) {
				App app = appService.findAppById(tenant_id, envApp.getAppId());
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
	public App getAppbyId(int tenant_id, int appId) {
		App app = new App();
		try {
			app = appService.findAppById(tenant_id, appId);
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
	public JSONObject getOne(int tenant_id, int appId) {
		JSONObject json = new JSONObject();
		try {
			App app = appService.findAppById(tenant_id, appId);
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

	public JSONArray listAppInLb(int lbId, int tenant_id) {
		try {
			List<App> apps = appService.listAppInLb(lbId, tenant_id);
			return apps.isEmpty() ? null : (JSONArray) JSONArray.toJSON(apps);
		} catch (Exception e) {
			LOGGER.error("list app in loadbalance error", e);
			return null;
		}
	}

	public JSONArray listAppNotInLb(int tenant_id) {
		try {
			List<App> apps = appService.listAppNotInLb(tenant_id);
			return apps.isEmpty() ? null : (JSONArray) JSONArray.toJSON(apps);
		} catch (Exception e) {
			LOGGER.error("list app not in loadbalance error", e);
			return null;
		}
	}

	/** 基于多租户的场景，透明传输租户资源ID */
	public JSONArray queryAppResInfo(int tenant_id) {
		/* 设置空对象，用于返回给请求 */
		JSONArray retnull_array = new JSONArray();

		/* （1）首先获取全部的应用列表 */
		List<App> app_list = null;
		try {
			app_list = appService.listAll_TID(tenant_id);
		} catch (Exception e) {
			LOGGER.error("list all app exception.", e);
		}

		/* 取小数点后两位 */
		DecimalFormat dec_fmt = new DecimalFormat("##0.00");

		/* 包含多应用的返回列表 */
		ArrayList<AppViewInfo> avi_list = new ArrayList<AppViewInfo>();

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
				appcomsume_cpus += tempsin_app.getAppCpu();
				appcomsume_mems += tempsin_app.getAppMem();
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
			Float totalcpu_userate = Float
					.parseFloat(dec_fmt.format(((float) appcomsume_cpus / (float) intotal_cpus) * 100));
			Float totalcpu_freerate = Float
					.parseFloat(dec_fmt.format(100 - ((float) appcomsume_cpus / (float) intotal_cpus) * 100));
			Float totalmem_userate = Float
					.parseFloat(dec_fmt.format(((float) appcomsume_mems / (float) intotal_mems) * 100));
			Float totalmem_freerate = Float
					.parseFloat(dec_fmt.format(100 - ((float) appcomsume_mems / (float) intotal_mems) * 100));

			ArrayList<AppViewElement> allc_list = new ArrayList<AppViewElement>();
			AppViewElement allcpuused_ele = new AppViewElement("已用CPU(" + appcomsume_cpus + "核)", totalcpu_userate,
					"#2091CF");
			AppViewElement allcpufree_ele = new AppViewElement("空闲CPU(" + (intotal_cpus - appcomsume_cpus) + "核)",
					totalcpu_freerate, "#68BC31");
			allc_list.add(allcpuused_ele);
			allc_list.add(allcpufree_ele);

			ArrayList<AppViewElement> allm_list = new ArrayList<AppViewElement>();
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

				app_cpus = single_app.getAppCpu();
				app_mems = single_app.getAppMem();

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
				ArrayList<Integer> cluid_list = new ArrayList<Integer>();
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
									App check_app = appService.findAppById(tenant_id, checkapp_id);
									occupy_cpus += check_app.getAppCpu();
									occupy_mems += check_app.getAppMem();
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

					Float cpuused_rate = Float
							.parseFloat(dec_fmt.format(((float) app_cpus / (float) cluster_cpus) * 100));
					Float cpuoccupy_rate = 0.0f;
					Float cpufree_rate = 0.0f;
					/* 统计空闲的CPU数量 */
					Integer free_cpus = 0;
					/* 当其他应用占用CPU资源的情况下 */
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

					ArrayList<AppViewElement> avec_list = new ArrayList<AppViewElement>();
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
					Float memused_rate = Float
							.parseFloat(dec_fmt.format(((float) app_mems / (float) cluster_mems) * 100));
					Float memoccupy_rate = 0.0f;
					Float memfree_rate = 0.0f;
					/* 统计空闲的内存容量 */
					Integer free_mems = 0;
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

					ArrayList<AppViewElement> avem_list = new ArrayList<AppViewElement>();
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
			Integer intotal_cpus = 0;

			/* 保存全部应用占用的内存量和总内存量 */
			Integer intotal_mems = 0;

			/* 第一个显示全部应用内容的视图 */
			AppViewInfo first_view = new AppViewInfo();
			first_view.setAppId(0);
			first_view.setAppName("全部应用");

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
				} else {
					return retnull_array;
				}
			} catch (Exception e1) {
				LOGGER.error("list all cluster_list  exception.", e1);
			}

			/* 分别计算整体的CPU和内存资源的使用率 */
			Float totalcpu_freerate = Float.parseFloat(dec_fmt.format(100));
			Float totalmem_freerate = Float.parseFloat(dec_fmt.format(100));

			ArrayList<AppViewElement> allc_list = new ArrayList<AppViewElement>();
			AppViewElement allcpufree_ele = new AppViewElement("空闲CPU(" + (intotal_cpus) + "核)", totalcpu_freerate,
					"#68BC31");
			allc_list.add(allcpufree_ele);

			ArrayList<AppViewElement> allm_list = new ArrayList<AppViewElement>();
			AppViewElement allmemfree_ele = new AppViewElement("空闲内存(" + (intotal_mems) + "MB)", totalmem_freerate,
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
	public GridBean listSearch(int tenant_id, int pagenumber, int pagesize, String search_name) {
		try {
			return appService.listSearchApps(tenant_id, pagenumber, pagesize, search_name);
		} catch (Exception e) {
			LOGGER.error("select app list by fuzzy name failed.", e);
		}
		return null;
	}

	public GridBean advancedSearchApp(Integer tenant_id, int pagenumber, int pagesize, JSONObject json_object) {
		try {
			return appService.advancedSearchApp(tenant_id, pagenumber, pagesize, json_object);
		} catch (Exception e) {
			LOGGER.error("advanced search app list failed.param is:" + json_object.toJSONString(), e);
			return null;
		}
	}

	/** 获取单页的租户列表信息 */
	public GridBean listOnePageTenants(int pagenumber, int pagesize, int tenantId) {
		try {
			return tenantService.listOnePageTenants(pagenumber, pagesize, tenantId);
		} catch (Exception e) {
			LOGGER.error("list one page tenants failed!", e);
			return null;
		}
	}

	/** 添加租户对象，返回为JSONObject对象，包含新建租户的ID信息 */
	public JSONObject addTenant(TenantModel tenantModel) {
		String tenantName = tenantModel.getTenantName();
		try {
			Tenant tenant = new Tenant();
			BeanUtils.copyProperties(tenantModel, tenant);
			tenantService.addTenant(tenant);
			/* 获取新增租户的ID信息 */
			int tenantId = tenant.getTenantId();
			/* （1）向新增租户挂载全部公共镜像（复制），租户ID为0的 */
			Image selImage = new Image();
			selImage.setTenantId(NormalConstant.ADMIN_TENANTID);
			List<Image> imageList = imageService.selectActiveAllImages(selImage);

			/** 判断获取的公共镜像列表是否为空 */
			if (!imageList.isEmpty()) {
				/* 逐个写入租户的Id信息 */
				for (Image sinImg : imageList) {
					sinImg.setImageId(null);
					sinImg.setTenantId(tenantId);
					sinImg.setImageCreatetime(new Date());
				}
				/* 批量插入镜像链表 */
				imageService.batchInsertImages(imageList);
			}
			LOGGER.info("Attach public images to tenant(" + tenantName + ") finished!");

			/* （2）向租户挂载集群（修改） */
			String cluIds = tenantModel.getCluArray();
			List<Integer> cluIdList = convertStrToList(cluIds);
			/* 批量将集群划入到租户资源中 */
			clusterService.updateClusterInTenant(tenantId, cluIdList);
			LOGGER.info("Attach cluster(ID:" + cluIds + ") to tenant(" + tenantName + ") finished!");

			/* （3）向租户挂载全部公共参数（复制） */
			Parameter selParam = new Parameter();
			selParam.setTenantId(NormalConstant.ADMIN_TENANTID);
			List<Parameter> paramList = paramService.selectAll(selParam);

			/** 判断获取的公共参数类表是否为空 */
			if (!paramList.isEmpty()) {
				/* 逐个写入租户的Id信息 */
				for (Parameter sinPrm : paramList) {
					sinPrm.setParamId(null);
					sinPrm.setTenantId(tenantId);
					sinPrm.setParamCreatetime(new Date());
				}
				/* 批量插入参数链表 */
				paramService.batchInsertParams(paramList);
			}
			LOGGER.info("Attach public parameters to tenant(" + tenantName + ") finished!");

			/* （4）向租户挂载公共资源（复制） */
			DkResource selDkRes = new DkResource();
			selDkRes.setTenantId(NormalConstant.ADMIN_TENANTID);
			List<DkResource> resList = resourceService.selectAll(selDkRes);

			/** 判断获取的公共资源方案列表是否为空 */
			if (!resList.isEmpty()) {
				/* 逐个写入租户的Id信息 */
				for (DkResource sinRes : resList) {
					sinRes.setResId(null);
					sinRes.setTenantId(tenantId);
					sinRes.setResCreatetime(new Date());
					
				}
				/* 批量插入参数链表 */
				resourceService.batchInsertReses(resList);
			}
			LOGGER.info("Attach public resources to tenant(" + tenantName + ") finished!");

			/* （5）向租户中添加新的管理员 */
			User manager = new User();
			manager.setUserName(tenantModel.getManagerName());
			manager.setUserMail(tenantModel.geteMail());
			manager.setUserPhone(tenantModel.getPhoneNumber());
			manager.setUserCompany(tenantModel.getCompanyName());
			manager.setUserCreator(tenantModel.getCreator());
			manager.setUserStatus((byte) Status.USER.NORMAL.ordinal());
			manager.setUserLevel(1);
			// 创建用户时默认初始化密码是123456
			manager.setUserPass(HashUtil.md5Hash("123456"));
			manager.setUserCreatedate(new Date());
			/** 管理员写入租户的Id信息 **/
			manager.setTenantId(tenantId);
			int createUserResult = userService.create(manager);
			/** 用户添加成功，赋予用户权限 */
			if (createUserResult > 0) {
				LOGGER.info(
						"Attach manager(NAME:" + manager.getUserName() + ") to tenant(" + tenantName + ") finished!");
				UserRole userRole = new UserRole();
				userRole.setRoleId(1);
				userRole.setUserId(manager.getUserId());
				userService.updateUserRole(userRole);
				LOGGER.info("Attach manager(NAME:" + manager.getUserName() + ") to auth Role(ID:1) finished!");
			}

			return (JSONObject) JSONObject.toJSON(tenant);
		} catch (Exception e) {
			LOGGER.error("create new tenant failed!", e);
		}
		return null;
	}

	/* 将多个Id的字符串转化为List */
	private static List<Integer> convertStrToList(String sthIds) {
		String[] sthArray = sthIds.split(",");
		List<Integer> retList = new ArrayList<Integer>();
		for (String sinId : sthArray) {
			retList.add(Integer.parseInt(sinId));
		}
		return retList;
	}
}

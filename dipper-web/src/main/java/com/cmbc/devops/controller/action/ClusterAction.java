package com.cmbc.devops.controller.action;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cmbc.devops.bean.GridBean;
import com.cmbc.devops.bean.Result;
import com.cmbc.devops.entity.Cluster;
import com.cmbc.devops.entity.ClusterWithHostContainerNum;
import com.cmbc.devops.entity.ClusterWithIPAndUser;
import com.cmbc.devops.entity.Host;
import com.cmbc.devops.entity.User;
import com.cmbc.devops.manager.ClusterManager;
import com.cmbc.devops.manager.HostManager;
import com.cmbc.devops.model.ClusterModel;
import com.cmbc.devops.model.HostModel;
import com.cmbc.devops.query.QueryList;

/**
 * @author luogan 2015年8月13日 上午10:53:00
 */

@Controller
@RequestMapping("cluster")
public class ClusterAction {

	private static final Logger LOGGER = Logger.getLogger(ClusterAction.class);

	@Resource
	private QueryList queryList;
	@Resource
	private ClusterManager clusterManager;
	@Resource
	private HostManager hostManager;

	/**
	 * @author youngtsinglin
	 * @time 2015年9月6日 10:35
	 * @description 将原来返回字符串的方法修改为GridBean的方式
	 */
	@RequestMapping("/advancedSearch")
	@ResponseBody
	/** @date:2016年3月28日 添加租户维度 */
	public GridBean advancedSearch(HttpServletRequest request,
			@RequestParam(value = "page", required = true) int pagenumber,
			@RequestParam(value = "rows", required = true) int pagesize, ClusterModel clusterModel) {
		try {
			User user = (User) request.getSession().getAttribute("user");
			String params = request.getParameter("params").trim();
			String values = request.getParameter("values").trim();
			JSONObject json_object = new JSONObject();
			json_object.put("params", params);
			json_object.put("values", values);
			return clusterManager.advancedSearchCluster(user.getUserId(), user.getTenantId(), pagenumber, pagesize,
					clusterModel, json_object);

		} catch (Exception e) {
			LOGGER.error("查询集群列表内容失败！", e);
			return null;
		}
	}

	/**
	 * @author youngtsinglin
	 * @time 2015年10月8日 10:24
	 * @description 添加查询仓库详细信息
	 */
	@RequestMapping("/detail/{id}.html")
	public ModelAndView detail(@PathVariable Integer id) {
		ModelAndView mav = new ModelAndView("cluster/detail");
		ClusterWithHostContainerNum clusterHC = clusterManager.detail(id);
		mav.addObject("clusterHC", clusterHC);
		return mav;
	}

	@RequestMapping(value = "/create", method = { RequestMethod.POST })
	@ResponseBody
	/** @date:2016年3月28日 添加租户维度 */
	public Result create(HttpServletRequest request, ClusterModel model) {
		User user = (User) request.getSession().getAttribute("user");
		model.setUserId(user.getUserId());
		model.setTenantId(user.getTenantId());
		return clusterManager.createCluster(model);
	}

	@RequestMapping(value = "/delete", method = { RequestMethod.POST })
	@ResponseBody
	/** @date:2016年3月28日 添加租户维度 */
	public Result delete(HttpServletRequest request, int clusterId) {

		User user = (User) request.getSession().getAttribute("user");
		return clusterManager.deleteCluster(clusterId, user.getUserId(), user.getTenantId());
	}

	@RequestMapping(value = "/deletes", method = { RequestMethod.POST })
	@ResponseBody
	public void deletes(HttpServletRequest request, @RequestParam(value = "ids", required = true) String ids) {

	}

	@RequestMapping(value = "/update", method = { RequestMethod.POST })
	@ResponseBody
	public Result update(HttpServletRequest request, ClusterModel clusterModel) {
		User user = (User) request.getSession().getAttribute("user");
		return clusterManager.updateCluster(user.getUserId(), clusterModel);

	}

	@RequestMapping(value = "/list", method = { RequestMethod.GET })
	@ResponseBody
	/** @date:2016年3月28日 添加租户维度 */
	public GridBean clusterList(HttpServletRequest request, @RequestParam(value = "page", required = true) int page,
			@RequestParam(value = "rows", required = true) int rows, ClusterWithIPAndUser ClusterWithIPAndUser) {
		User user = (User) request.getSession().getAttribute("user");
		int userId = user.getUserId();
		int tenantId = user.getTenantId();
		return queryList.queryClusterList(userId, tenantId, page, rows, ClusterWithIPAndUser);

	}

	@RequestMapping(value = "/clusterMasterList", method = { RequestMethod.GET })
	@ResponseBody
	/** @date:2016年3月28日 添加租户维度 */
	public String clusterMasterList(HttpServletRequest request, ClusterModel clusterModel) {
		User user = (User) request.getSession().getAttribute("user");
		int tenantId = user.getTenantId();
		JSONArray ja = queryList.queryClusterMasterList(tenantId);
		return ja.toString();
	}

	@RequestMapping(value = "/all", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	/** @date:2016年3月28日 添加租户维度 */
	public String allList(HttpServletRequest request, ClusterWithIPAndUser clusterWithIPAndUser) {
		User user = (User) request.getSession().getAttribute("user");
		int userId = user.getUserId();
		int tenantId = user.getTenantId();
		JSONArray ja = queryList.queryAllClusterList(userId, tenantId, clusterWithIPAndUser);
		return ja.toString();
	}

	@RequestMapping(value = "/getOrphanClus", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	/** @date:2016年4月6日 获取全部租户ID为空的集群列表 */
	public String getOrphanClus(HttpServletRequest request, ClusterWithIPAndUser clusterWithIPAndUser) {
		// User user = (User) request.getSession().getAttribute("user");
		JSONArray ja = queryList.getOrphanClus();
		return ja.toString();
	}

	@RequestMapping(value = "/clusterInApp", method = { RequestMethod.GET })
	@ResponseBody
	public String clusterInApp(HttpServletRequest request, Integer appId) {
		JSONArray ja = queryList.queryAllClustersInApp(appId);
		return ja.toString();
	}

	@RequestMapping(value = "/cluster", method = { RequestMethod.GET })
	@ResponseBody
	public Cluster cluster(HttpServletRequest request, HostModel hostModel) {
		Host host = new Host();
		host.setHostId(hostModel.getHostId());
		Cluster cluster = queryList.queryCluster(host);
		if (cluster != null) {
			return cluster;
		} else {
			return null;
		}
	}

	@RequestMapping(value = "/addHost", method = { RequestMethod.POST })
	@ResponseBody
	public Result addHost2Cluster(HttpServletRequest request, int clusterId, int hostId) {
		return hostManager.ClusterAddHost(clusterId, hostId);
	}

	@RequestMapping(value = "/addManyHost", method = { RequestMethod.POST })
	@ResponseBody
	public Result addManyHost(HttpServletRequest request, Integer clusterId, String hostId) {
		return hostManager.addManyHost(clusterId, hostId);
	}

	@RequestMapping(value = "/removeHost", method = { RequestMethod.POST })
	@ResponseBody
	public Result removeHostFromCluster(HttpServletRequest request, int clusterId, String hostIds) {
		String[] hosts=hostIds.split(",");
		return hostManager.clusterRemoveHost(clusterId, hosts);
	}

	@RequestMapping(value = "/healthCheck", method = { RequestMethod.POST })
	@ResponseBody
	public Result healthCheck(HttpServletRequest request, int clusterId) {
		return clusterManager.clusterHealthCheck(clusterId);
	}

	@RequestMapping(value = "/recover", method = { RequestMethod.POST })
	@ResponseBody
	public Result recover(HttpServletRequest request, int clusterId) {
		User user = (User) request.getSession().getAttribute("user");
		JSONObject params = new JSONObject();
		params.put("clusterId", clusterId);
		params.put("userId", user.getUserId());
		return clusterManager.recoverCluster(params);
	}

	/**
	 * @author yangqinglin
	 * @time 2015年10月14日
	 * @description
	 */
	@RequestMapping(value = "/viewTopology", method = { RequestMethod.POST })
	public String viewTopology(Model model, Integer Cluster_Id) {
		model.addAttribute("Cluster_Id", Cluster_Id);
		return "cluster/topology";
	}

	/**
	 * @author yangqinglin
	 * @time 2015年10月14日
	 * @description
	 */
	@RequestMapping(value = "/topology", method = { RequestMethod.POST })
	@ResponseBody
	public String topology(Integer Cluster_Id) {
		JSONObject json_object = clusterManager.getClusterTopoJson(Cluster_Id);
		return json_object.toString();
	}

	@RequestMapping(value = "/checkPort", method = { RequestMethod.POST })
	@ResponseBody
	public Boolean checkPort(HttpServletRequest request, Integer hostId, String port) {
		JSONObject params = new JSONObject();
		params.put("hostId", hostId);
		params.put("port", port);
		return clusterManager.checkPort(params);
	}

	@RequestMapping(value = "/checkName", method = { RequestMethod.POST })
	@ResponseBody
	public Boolean checkPort(HttpServletRequest request, String clusterName) {
		return clusterManager.checkName(clusterName);
	}

	@RequestMapping(value = "/checkClusterConfFile", method = { RequestMethod.POST })
	@ResponseBody
	public Boolean checkManagePath(HttpServletRequest request, String managePath) {
		return clusterManager.checkClusterConfFile(managePath);
	}

	@RequestMapping(value = "/listSearch", method = { RequestMethod.GET })
	@ResponseBody
	/** @date:2016年3月28日 添加租户维度 */
	public GridBean listSearch(HttpServletRequest request, @RequestParam(value = "page", required = true) int page,
			@RequestParam(value = "rows", required = true) int rows, ClusterModel clusterModel) {
		User user = (User) request.getSession().getAttribute("user");
		/* 获取查询主机名称的关键字信息 */
		String search_name = request.getParameter("clusterName").trim();
		clusterModel.setSearch(search_name);
		int userId = user.getUserId();
		int tenantId = user.getTenantId();
		return clusterManager.clusterSearchAllList(userId, tenantId, page, rows, clusterModel);
	}

	@RequestMapping(value = "/hostList", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public JSONArray hostList(HttpServletRequest request, int clusterId) {
		return hostManager.hostList(clusterId);
	}
	
	@RequestMapping(value = "/maxIns", method = {RequestMethod.GET })
	@ResponseBody
	public String maxIns(HttpServletRequest request, int clusterId,int appCpu) {
		return String.valueOf(clusterManager.getMaxIns(clusterId,appCpu));
	}
}

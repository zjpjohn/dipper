package com.cmbc.devops.controller.action;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
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
import com.cmbc.devops.constant.Type;
import com.cmbc.devops.entity.Host;
import com.cmbc.devops.entity.User;
import com.cmbc.devops.manager.HostManager;
import com.cmbc.devops.model.HostModel;
import com.cmbc.devops.query.QueryList;
import com.cmbc.devops.service.UserService;

/**
 * @author luogan 2015年8月13日 上午10:53:27
 */

@Controller
@RequestMapping("host")
public class HostAction {
	private static final Logger LOGGER = Logger.getLogger(HostAction.class);

	@Resource
	private QueryList queryList;
	@Resource
	private HostManager hostManager;
	@Resource
	private UserService userService;

	@RequestMapping("/detail/{id}.html")
	public ModelAndView detail(@PathVariable Integer id) {
		ModelAndView mav = new ModelAndView("host/detail");
		Host host = hostManager.detail(id);
		String softNames = hostManager.getSoftsByHostId(id);
		Integer userId = host.getHostCreator();
		User user = new User();
		user.setUserId(userId);
		try {
			user = userService.getUser(user);
		} catch (Exception e) {
			LOGGER.error("通过用户ID(" + userId + ")获取用户名称失败！", e);
		}
		mav.addObject("host", host);
		mav.addObject("softNames", softNames);
		mav.addObject("creatorName", user.getUserName());
		return mav;
	}

	@RequestMapping(value = "/create", method = { RequestMethod.POST })
	@ResponseBody
	/** @date:2016年3月29日,此函数与租户无关 */
	public Result create(HttpServletRequest request, HostModel hostModel) {
		User user = (User) request.getSession().getAttribute("user");
		hostModel.setCreator(user.getUserId());
		return hostManager.createHost(hostModel);
	}

	@RequestMapping(value = "/delete", method = { RequestMethod.POST })
	@ResponseBody
	public Result delete(HttpServletRequest request, HostModel model) {
		return hostManager.deleteHost(model);
	}

	@RequestMapping(value = "/deletes", method = { RequestMethod.POST })
	@ResponseBody
	public Result deletes(HttpServletRequest request, int[] hostIds) {
		return hostManager.deleteHosts(hostIds);
	}

	@RequestMapping(value = "/update", method = { RequestMethod.POST })
	@ResponseBody
	public Result update(HttpServletRequest request, HostModel hostModel) {
		User user = (User) request.getSession().getAttribute("user");
		return hostManager.updateHost(user.getUserId(), hostModel);
	}

	@RequestMapping(value = "/list", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public GridBean hostList(HttpServletRequest request, @RequestParam(value = "page", required = true) int page,
			@RequestParam(value = "rows", required = true) int rows, HostModel hostModel) {
		User user = (User) request.getSession().getAttribute("user");
		int userId = user.getUserId();
		int tenantId = user.getTenantId();
		hostModel.setTenantId(tenantId);
		String hostType = request.getParameter("hostType");
		if (!"".equalsIgnoreCase(hostType) && hostType != null) {
			hostModel.setHostType(Integer.parseInt(hostType));
			return queryList.queryHostList(userId, page, rows, hostModel);
		} else {
			return queryList.queryHostList(userId, page, rows, hostModel);
		}
	}

	/**
	 * @author youngtsinglin
	 * @time 2015年9月6日 10:35
	 * @description 将原来返回字符串的方法修改为GridBean的方式
	 */
	@RequestMapping("/advancedSearch")
	@ResponseBody
	public GridBean advancedSearch(HttpServletRequest request,
			@RequestParam(value = "page", required = true) int pagenumber,
			@RequestParam(value = "rows", required = true) int pagesize, HostModel hostModel) {
		try {
			User user = (User) request.getSession().getAttribute("user");
			String params = request.getParameter("params").trim();
			String values = request.getParameter("values").trim();
			JSONObject json_object = new JSONObject();
			json_object.put("params", params);
			json_object.put("values", values);

			return hostManager.advancedSearchHost(user.getUserId(), pagenumber, pagesize, hostModel, json_object);

		} catch (Exception e) {
			LOGGER.error("查询主机列表失败！", e);
			return null;
		}
	}

	@RequestMapping(value = "/listSearch", method = { RequestMethod.GET })
	@ResponseBody
	public GridBean listSearch(HttpServletRequest request, @RequestParam(value = "page", required = true) int page,
			@RequestParam(value = "rows", required = true) int rows, HostModel hostModel) {
		User user = (User) request.getSession().getAttribute("user");
		/* 获取查询主机名称的关键字信息 */
		String search_name = request.getParameter("hostName").trim();
		hostModel.setSearch(search_name);
		int userId = user.getUserId();
		return queryList.hostSearchAllList(userId, page, rows, hostModel);
	}

	@RequestMapping(value = "/addToCluster", method = { RequestMethod.POST })
	@ResponseBody
	public Result addToCluster(HttpServletRequest request, String hosts, int clusterhost, int cluster) {

		User user = (User) request.getSession().getAttribute("user");
		JSONObject params = new JSONObject();
		params.put("clusterHostId", clusterhost);
		params.put("clusterId", cluster);
		params.put("userId", user.getUserId());
		params.put("hosts", hosts);
		return hostManager.addHost2Cluster(params);
	}

	@RequestMapping(value = "/removeFromCluster", method = { RequestMethod.POST })
	@ResponseBody
	public Result removeFromCluster(HttpServletRequest request, String hosts, int clusterId) {
		User user = (User) request.getSession().getAttribute("user");
		JSONObject params = new JSONObject();
		params.put("clusterId", clusterId);
		params.put("userId", user.getUserId());
		params.put("hosts", hosts);
		return hostManager.removeHostFromCluster(params);
	}

	@RequestMapping(value = "/addAll", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String addAll(HttpServletRequest request) {
		User user = (User) request.getSession().getAttribute("user");
		List<Host> hostList = queryList.queryAllHostList(user.getUserId(), Type.HOST.DOCKER.ordinal());
		return JSONArray.toJSONString(hostList);
	}

	@RequestMapping(value = "/removeAll", method = { RequestMethod.GET })
	@ResponseBody
	public String removeAll(HttpServletRequest request, Integer clusterId) {
		User user = (User) request.getSession().getAttribute("user");
		List<Host> hostList = queryList.queryAllClusterHostList(user.getUserId(), clusterId);
		return JSONArray.toJSONString(hostList);
	}

	@RequestMapping(value = "/checkName", method = { RequestMethod.POST })
	@ResponseBody
	public Boolean checkName(HttpServletRequest request, String hostName) {
		return hostManager.checkHostName(hostName);
	}

	@RequestMapping(value = "/checkIp", method = { RequestMethod.POST })
	@ResponseBody
	public Boolean checkIp(HttpServletRequest request, String hostIp, Integer hostType) {
		return hostManager.checkHostIp(hostIp, hostType);
	}

	@RequestMapping(value = "/checkHostId", method = { RequestMethod.POST })
	@ResponseBody
	public JSONObject checkHostId(HttpServletRequest request, Integer hostId) {
		return hostManager.checkHostId(hostId);
	}

	@RequestMapping(value = "/getFreePort", method = { RequestMethod.POST })
	@ResponseBody
	/** @date:2016年4月18日,获取当前没有占用的端口 */
	public String getFreePort(HttpServletRequest request, Integer hostId) {
		// User user = (User) request.getSession().getAttribute("user");
		return hostManager.getFreePort(hostId);
	}

}

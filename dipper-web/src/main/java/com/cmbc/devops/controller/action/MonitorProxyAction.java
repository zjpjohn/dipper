package com.cmbc.devops.controller.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cmbc.devops.bean.GridBean;
import com.cmbc.devops.bean.Result;
import com.cmbc.devops.entity.MonitorProxy;
import com.cmbc.devops.entity.User;
import com.cmbc.devops.manager.MonitorProxyManager;

/**
 * date：2015年8月19日 上午12:18:56 project name：cmbc-devops-web
 * 
 * @author mayh
 * @version 1.0
 * @since JDK 1.7.0_21 file name：ApplicationAction.java description：
 */
@RequestMapping("mntrproxy")
@Controller
public class MonitorProxyAction {
	private static final Logger LOGGER = Logger.getLogger(MonitorProxyAction.class);

	@Resource
	private MonitorProxyManager monitorProxyManager;

	/**
	 * @author yangqinlgin
	 * @datetime 2015年9月8日 9:27
	 * @description 添加增加应用部分内容
	 */
	@RequestMapping(value = "/create", method = { RequestMethod.POST })
	@ResponseBody
	public Result create(HttpServletRequest request, MonitorProxy proxy) {
		User user = (User) request.getSession().getAttribute("user");
		if (proxy != null) {
			JSONObject params = new JSONObject();
			params.put("userId", user.getUserId());
			params.put("mpName", proxy.getMpName());
			params.put("mpIP", proxy.getMpIP());
			params.put("mpPort", proxy.getMpPort());
			params.put("mpDesc", proxy.getMpDesc());
			params.put("mpComment", proxy.getMpComment());
			return monitorProxyManager.createMonitorProxy(params);
		} else {
			Result result = new Result(false, "参数输入异常!");
			LOGGER.info(user.getUserName() + ":" + result.getMessage());
			return result;

		}
	}

	/**
	 * @author youngtsinglin
	 * @time 2016年1月12日
	 * @description 将原来返回字符串的方法修改为GridBean的方式
	 * 
	 */
	@RequestMapping("/list")
	@ResponseBody
	public GridBean list(HttpServletRequest request, @RequestParam(value = "page", required = true) int pagenumber,
			@RequestParam(value = "rows", required = true) int pagesize, MonitorProxy proxy) {
		try {
			User user = (User) request.getSession().getAttribute("user");
			return monitorProxyManager.mntrProxyAllList(user.getUserId(), pagenumber, pagesize, proxy);
		} catch (Exception e) {
			LOGGER.error("查询应用列表失败！", e);
			return null;
		}
	}

	/**
	 * @author youngtsinglin
	 * @time 2016年1月12日
	 * @description 将原来返回字符串的方法修改为GridBean的方式
	 * 
	 */
	@RequestMapping(value = "/all", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public JSONArray listAll(HttpServletRequest request, MonitorProxy proxy) {
		try {
			User user = (User) request.getSession().getAttribute("user");
			return monitorProxyManager.listAll(user.getUserId(), proxy);
		} catch (Exception e) {
			LOGGER.error("查询全部监控代理列表失败！", e);
			return null;
		}
	}

	/**
	 * @author youngtsinglin
	 * @time 2015年9月6日 10:35
	 * @description 将原来返回字符串的方法修改为GridBean的方式
	 */
	@RequestMapping("/listSearch")
	@ResponseBody
	public GridBean listSearch(HttpServletRequest request,
			@RequestParam(value = "page", required = true) int pagenumber,
			@RequestParam(value = "rows", required = true) int pagesize) {
		try {
			User user = (User) request.getSession().getAttribute("user");
			/* 获取查询仓库的关键字信息 */
			String searchName = request.getParameter("search_name").trim();
			return monitorProxyManager.mntrPxySearchAllList(user.getUserId(), pagenumber, pagesize, searchName);
		} catch (Exception e) {
			LOGGER.error("查询应用列表失败！", e);
			return null;
		}
	}

	/**
	 * @author youngtsinglin
	 * 
	 * @time 2015年9月6日 10:35
	 * @description 将原来返回字符串的方法修改为GridBean的方式
	 */
	@RequestMapping("/advancedSearch")
	@ResponseBody
	public GridBean advancedSearch(HttpServletRequest request,
			@RequestParam(value = "page", required = true) int pagenumber,
			@RequestParam(value = "rows", required = true) int pagesize) {
		try {
			User user = (User) request.getSession().getAttribute("user");
			String params = request.getParameter("params").trim();
			String values = request.getParameter("values").trim();
			JSONObject searchParam = new JSONObject();
			searchParam.put("params", params);
			searchParam.put("values", values);
			return monitorProxyManager.advancedSearchMp(user.getUserId(), pagenumber, pagesize, searchParam);

		} catch (Exception e) {
			LOGGER.error("查询应用列表失败！", e);
			return null;
		}
	}

	@RequestMapping(value = "/delete", method = { RequestMethod.POST })
	@ResponseBody
	public Result delete(HttpServletRequest request, @RequestParam(value = "mpIds", required = true) String mpIds,
			@RequestParam(value = "mpNames", required = true) String mpNames) {
		User user = (User) request.getSession().getAttribute("user");
		if (StringUtils.hasText(mpIds)) {
			String[] ids = mpIds.split(",");
			List<Integer> idLists = new ArrayList<Integer>();
			for (int i = 0; i < ids.length; i++) {
				idLists.add(Integer.parseInt(ids[i]));
			}
			Result result = monitorProxyManager.delete(idLists, mpNames);
			return result;
		}
		Result result = new Result(false, "提交删除应用的参数为空！");
		LOGGER.info(user.getUserName() + ":" + result.getMessage());
		return result;
	}

	/* 添加判断应用名称是否重复判断 */
	@RequestMapping(value = "/checkMpName", method = { RequestMethod.POST })
	@ResponseBody
	public Boolean duplicateAppName(HttpServletRequest request) {
		JSONObject jo = new JSONObject();
		jo.put("mpName", request.getParameter("mpName"));
		User user = (User) request.getSession().getAttribute("user");
		jo.put("userId", user.getUserId());
		return monitorProxyManager.duplicateMpName(jo);
	}

	/* 添加判断应用名称是否重复判断 */
	@RequestMapping(value = "/checkMpIpPort", method = { RequestMethod.POST })
	@ResponseBody
	public Result checkMpIpPort(HttpServletRequest request, MonitorProxy proxy) {
		User user = (User) request.getSession().getAttribute("user");
		return monitorProxyManager.duplicateMpIpPort(user.getUserId(), proxy);
	}

	@RequestMapping(value = "/update", method = { RequestMethod.POST })
	@ResponseBody
	public Result update(HttpServletRequest request, MonitorProxy proxy) {
		User user = (User) request.getSession().getAttribute("user");
		if (proxy != null) {
			JSONObject params = new JSONObject();
			params.put("userId", user.getUserId());
			params.put("mpId", proxy.getMpId());
			params.put("mpName", proxy.getMpName());
			params.put("mpIP", proxy.getMpIP());
			params.put("mpPort", proxy.getMpPort());
			params.put("mpDesc", proxy.getMpDesc());
			params.put("mpComment", proxy.getMpComment());
			Result result = monitorProxyManager.updateMonitorProxy(params);
			return result;
		} else {
			return new Result(false, "参数输入异常!");
		}
	}
}

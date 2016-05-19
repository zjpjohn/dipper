package com.cmbc.devops.controller.action;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.cmbc.devops.entity.User;
import com.cmbc.devops.manager.ContainerManager;
import com.cmbc.devops.model.ContainerModel;
import com.cmbc.devops.query.QueryList;

/**
 * date：2015年8月20日 下午4:15:36 project name：cmbc-devops-web
 * 
 * @author langzi
 * @version 1.0
 * @since JDK 1.7.0_21 file name：ContainerAction.java description：
 */
@RequestMapping("/container")
@Controller
public class ContainerAction {
	private static final Logger LOGGER = Logger.getLogger(ContainerAction.class);

	@Autowired
	private QueryList query;
	@Autowired
	private ContainerManager containerManager;

	/**
	 * @author langzi
	 * @param requst
	 * @return
	 * @version 1.0 2015年8月20日
	 */
	@RequestMapping(value = "/list")
	@ResponseBody
	/** @date:2016年3月28日 添加租户维度 */
	public GridBean listContainerByPage(HttpServletRequest request,
			@RequestParam(value = "page", required = true) int pagenumber,
			@RequestParam(value = "rows", required = true) int pagesize, ContainerModel model) {
		User user = (User) request.getSession().getAttribute("user");
		return query.listOnePageContainer(user.getUserId(), user.getTenantId(), pagenumber, pagesize, model);
	}

	/**
	 * @author langzi
	 * @param requst
	 * @return
	 * @version 1.0 2015年8月20日
	 */
	@RequestMapping(value = "/listApp")
	@ResponseBody
	/** @date:2016年3月28日 添加租户维度 */
	public GridBean listContainerOfAppByPage(HttpServletRequest request,
			@RequestParam(value = "page", required = true) int pagenumber,
			@RequestParam(value = "rows", required = true) int pagesize, int appId) {
		User user = (User) request.getSession().getAttribute("user");
		return query.listOnePageContainer(user.getUserId(), user.getTenantId(), pagenumber, pagesize, appId);
	}

	/**
	 * @author langzi
	 * @param requst
	 * @return
	 * @version 1.0 2015年8月20日
	 */
	@RequestMapping(value = "/listConInfoByAppid")
	@ResponseBody
	/** @date:2016年3月28日 添加租户维度 */
	public GridBean listConInfoByAppid(HttpServletRequest request,
			@RequestParam(value = "page", required = true) int pagenumber,
			@RequestParam(value = "rows", required = true) int pagesize, Integer appId, Integer imageId) {
		User user = (User) request.getSession().getAttribute("user");
		return query.listConInfoByAppid(user.getUserId(), user.getTenantId(), pagenumber, pagesize, appId, imageId);
	}

	/**
	 * @author langzi
	 * @param requst
	 * @return
	 * @version 1.0 2015年8月20日
	 */
	@RequestMapping(value = "/listSearch", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	/** @date:2016年3月28日 添加租户维度 */
	public GridBean listSearch(HttpServletRequest request,
			@RequestParam(value = "page", required = true) int pagenumber,
			@RequestParam(value = "rows", required = true) int pagesize) {
		/* 获取搜索的关键字和应用ID数据 */
		String searchName = request.getParameter("search_name");
		Integer appId = Integer.parseInt(request.getParameter("appId"));
		JSONObject param = new JSONObject();
		/* 对于搜索的名称进行过滤处理，去掉多余的字符 */
		if (searchName.contains("c-")) {
			searchName = searchName.replace("c-", "");
		} else if (searchName.contains("-")) {
			searchName = searchName.replace("-", "");
		}
		param.put("searchName", searchName);
		param.put("appId", appId);

		User user = (User) request.getSession().getAttribute("user");
		return query.listSearchConIns(user.getUserId(), user.getTenantId(), pagenumber, pagesize, param);
	}

	/**
	 * @author yangqinglin
	 * @param requst
	 * @return 返回给应用发布页面所有运行中的容器列表
	 * @version 1.0 2016年1月25日
	 */
	@RequestMapping(value = "/listPowerConInfo")
	@ResponseBody
	/** @date:2016年3月28日 添加租户维度 */
	public GridBean listPowerConInfo(HttpServletRequest request,
			@RequestParam(value = "page", required = true) int pagenumber,
			@RequestParam(value = "rows", required = true) int pagesize,
			@RequestParam(value = "powerStatus", required = true) int power_status) {
		User user = (User) request.getSession().getAttribute("user");
		return query.listPowerConInfo(user.getUserId(), user.getTenantId(), pagenumber, pagesize, power_status);
	}

	/**
	 * @author langzi
	 * @param request
	 * @return
	 * @version 1.0 2015年8月20日
	 */
	@RequestMapping(value = "/listAll", method = { RequestMethod.GET })
	@ResponseBody
	/** @date:2016年3月28日 添加租户维度 */
	public String listContainers(HttpServletRequest request) {
		User user = (User) request.getSession().getAttribute("user");
		Integer tenantId = user.getTenantId();
		return query.listContainers(tenantId).toString();
	}

	/**
	 * @author langzi
	 * @param request
	 * @param conId
	 * @return
	 * @version 1.0 2015年9月16日
	 */
	@RequestMapping(value = "/listPort", method = { RequestMethod.GET })
	@ResponseBody
	public JSONArray listConports(HttpServletRequest request, String conId) {
		return query.listConPortByConId(Integer.parseInt(conId));
	}

	@RequestMapping("/detail/{appId}/{imageId}/{balanceId}/{id}.html")
	/** @date:2016年3月28日 添加租户维度 */
	public ModelAndView detail(HttpServletRequest request, @PathVariable Integer appId, @PathVariable Integer imageId,
			@PathVariable Integer balanceId, @PathVariable Integer id) {
		/* 获取用户保存在Session中的信息 */
		User user = (User) request.getSession().getAttribute("user");
		Integer tenantId = user.getTenantId();
		ModelAndView mav = new ModelAndView("container/detail");
		Map<String, Object> conMap = containerManager.detail(tenantId, id);
		mav.addObject("container", conMap);
		mav.addObject("appId", appId);
		mav.addObject("imageId", imageId);
		mav.addObject("balanceId", balanceId);
		return mav;
	}

	/**
	 * @author langzi
	 * @param request
	 * @version 1.0 2015年8月20日
	 */
	@RequestMapping(value = "/create", method = { RequestMethod.POST })
	@ResponseBody
	/** @date:2016年3月28日 添加租户维度 */
	public Result createContainer(HttpServletRequest request, ContainerModel model) {
		User user = (User) request.getSession().getAttribute("user");
		model.setUserId(user.getUserId());
		model.setTenantId(user.getTenantId());
		return containerManager.createContainer(model);

	}

	@RequestMapping(value = "/start", method = { RequestMethod.GET })
	@ResponseBody
	/** @date:2016年3月28日 添加租户维度，此函数不涉及租户维度 */
	public Result startContainer(HttpServletRequest request, @RequestParam String[] containerids) {
		return containerManager.startContainer(containerids);
	}

	/**
	 * @author langzi
	 * @param request
	 * @param containerId
	 * @version 1.0 2015年8月20日
	 */
	/** @date:2016年3月28日 添加租户维度，此函数不涉及租户维度 */
	@RequestMapping(value = "/stop", method = { RequestMethod.GET })
	@ResponseBody
	public Result stopContainer(HttpServletRequest request, @RequestParam String[] containerids) {
		return containerManager.stopContainer(containerids);
	}

	/**
	 * @author langzi
	 * @param request
	 * @param containerIds
	 * @version 1.0 2015年8月20日
	 */
	/** @date:2016年3月28日 添加租户维度，此函数不涉及租户维度 */
	@RequestMapping(value = "/trash", method = { RequestMethod.GET })
	@ResponseBody
	public Result trashContainer(HttpServletRequest request, @RequestParam String[] containerids) {
		return containerManager.removeContainer(containerids);
	}

	/**
	 * @author langzi
	 * @param request
	 * @param containerIds
	 * @version 1.0 2015年8月20日
	 */
	/** @date:2016年3月28日 添加租户维度 */
	@RequestMapping(value = "/sync", method = { RequestMethod.GET })
	@ResponseBody
	public Result syncContainer(HttpServletRequest request) {
		/* 获取用户保存在Session中的信息 */
		User user = (User) request.getSession().getAttribute("user");
		Integer tenantId = user.getTenantId();
		return containerManager.syncContainer(tenantId);
	}

	/**
	 * @author youngtsinglin
	 * @time 2015年11月3日 10:35
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
			String appId = request.getParameter("app_id").trim();
			JSONObject param = new JSONObject();
			param.put("params", params);
			param.put("values", values);
			param.put("app_id", appId);

			return containerManager.advancedSearchContainer(user.getUserId(), user.getTenantId(), pagenumber, pagesize,
					param);
		} catch (Exception e) {
			LOGGER.error("查询主机列表失败！", e);
			return null;
		}
	}

}

package com.cmbc.devops.controller.action;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
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
import com.cmbc.devops.constant.Status;
import com.cmbc.devops.entity.App;
import com.cmbc.devops.entity.User;
import com.cmbc.devops.manager.AppManager;
import com.cmbc.devops.query.QueryList;

/**
 * date：2016年1月5日 下午3:04:31 project name：cmbc-devops-web
 * 
 * @author langzi
 * @version 1.0
 * @since JDK 1.7.0_21 file name：AppAction.java description：
 */
@RequestMapping("app")
@Controller
public class AppAction {

	@Autowired
	private AppManager appManager;
	@Autowired
	private QueryList query;

	@RequestMapping("/list")
	@ResponseBody
	/** Tenant Finished */
	public GridBean list(HttpServletRequest request, @RequestParam(value = "page", required = true) int pagenumber,
			@RequestParam(value = "rows", required = true) int pagesize) {
		/* #采用多租户方式，透传租户资源的ID */
		User user = (User) request.getSession().getAttribute("user");
		/* 直接传递用户的租户资源ID */
		return query.listOnePageApps(pagenumber, pagesize, user.getTenantId());
	}
	
	@RequestMapping("/all")
	@ResponseBody
	/** Tenant Finished */
	public GridBean all(HttpServletRequest request, @RequestParam(value = "page", required = true) int pagenumber,
			@RequestParam(value = "rows", required = true) int pagesize) {
		/* #采用多租户方式，透传租户资源的ID */
		User user = (User) request.getSession().getAttribute("user");
		/* 直接传递用户的租户资源ID */
		return query.listOnePageApps(pagenumber, pagesize, user.getTenantId());
	}

	@RequestMapping(value = "/add", method = { RequestMethod.POST })
	@ResponseBody
	/** Tenant Finished */
	public Result addApp(HttpServletRequest request, App app) {
		User user = (User) request.getSession().getAttribute("user");
		app.setAppStatus((byte) Status.APPLICATION.NORMAL.ordinal());
		app.setAppHealth((byte) app.getAppHealth());
		app.setAppMonitor((byte) app.getAppMonitor());
		app.setAppCreator(user.getUserId());
		app.setAppCreatetime(new Date());
		/* 写入租户资源的ID */
		app.setTenantId(user.getTenantId());
		return appManager.addApp(app);
	}

	@RequestMapping(value = "/modify", method = { RequestMethod.POST })
	@ResponseBody
	public Result modifyApp(HttpServletRequest request, App app) {
		/* 添加用户ID信息 */
		return appManager.modifyApp(app);
	}

	@RequestMapping(value = "/remove", method = { RequestMethod.POST })
	@ResponseBody
	public Result removeApp(HttpServletRequest request, int appId) {
		return appManager.removeApp(appId);
	}

	@RequestMapping(value = "/listByEnvId", method = { RequestMethod.POST })
	@ResponseBody
	public JSONArray listByEnvId(HttpServletRequest request, int envId) {
		/* 添加用户ID信息 */
		User user = (User) request.getSession().getAttribute("user");
		return appManager.listAppByEnvId(user.getTenantId(), envId);
	}

	/*
	 * 应用详情列表 by zll
	 */
	@RequestMapping("/detail/{id}.html")
	public ModelAndView detail(HttpServletRequest request, @PathVariable Integer id) {
		ModelAndView mav = new ModelAndView();
		User user = (User) request.getSession().getAttribute("user");
		App app = appManager.getAppbyId(user.getTenantId(), id);
		if (app != null) {
			mav.addObject("app", app);
			mav.setViewName("application/detail");
		} else {
			mav.setViewName("/404");
		}
		return mav;
	}

	/* 列出所有在绑定负载均衡的应用信息 */
	@RequestMapping(value = "/appInLb", method = { RequestMethod.GET })
	@ResponseBody
	/** Tenant Finished */
	public String listAppInLb(HttpServletRequest request, int balanceId) {
		User user = (User) request.getSession().getAttribute("user");
		JSONArray ja = appManager.listAppInLb(balanceId, user.getTenantId());
		return ja == null ? "" : ja.toString();
	}

	@RequestMapping(value = "/appNotInLb", method = { RequestMethod.GET })
	@ResponseBody
	/** Tenant Finished */
	public String listAppNotInLb(HttpServletRequest request) {
		User user = (User) request.getSession().getAttribute("user");
		JSONArray ja = appManager.listAppNotInLb(user.getTenantId());
		return ja == null ? "" : ja.toString();
	}

	@RequestMapping(value = "/getOne", method = { RequestMethod.GET })
	@ResponseBody
	/** ?unused? */
	public String getOne(HttpServletRequest request, Integer appid) {
		User user = (User) request.getSession().getAttribute("user");
		JSONObject app = appManager.getOne(user.getTenantId(), appid);
		return app.toString();
	}

	@RequestMapping(value = "/getEnvsByAppId", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public JSONArray getEnvsByAppId(HttpServletRequest request, Integer appid) {
		User user = (User) request.getSession().getAttribute("user");
		return appManager.getEnvsByAppId(user.getTenantId(), appid);
	}
	
	@RequestMapping(value = "/getEnvsByImageId", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public JSONArray getEnvsByImageId(HttpServletRequest request, Integer appid,Integer imageid) {
		return appManager.getEnvsByImageId(appid,imageid);
	}
	
	@RequestMapping(value = "/getEnvByImgId", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public JSONArray getEnvByImgId(HttpServletRequest request,Integer imageid) {
		return appManager.getEnvByImgId(imageid);
	}

	/* 查询全部应用的资源占用情况，需要添加租户的维度 */
	@RequestMapping(value = "/queryAppResInfo", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	/** Tenant Finished */
	public JSONArray queryAppResInfo(HttpServletRequest request, HttpServletResponse response) {
		User user = (User) request.getSession().getAttribute("user");
		JSONArray app_jarray = appManager.queryAppResInfo(user.getTenantId());
		return (JSONArray) (app_jarray == null ? "" : app_jarray);
	}

	/**
	 * @author youngtsinglin
	 * @time 2016年3月4日
	 * @description 将原来返回字符串的方法修改为GridBean的方式
	 */
	@RequestMapping("/listSearch")
	@ResponseBody
	/** Tenant Finished */
	public GridBean listSearch(HttpServletRequest request,
			@RequestParam(value = "page", required = true) int pagenumber,
			@RequestParam(value = "rows", required = true) int pagesize) {
		try {
			User user = (User) request.getSession().getAttribute("user");
			/* 获取查询仓库的关键字信息 */
			String search_name = request.getParameter("search_name").trim();
			return appManager.listSearch(user.getTenantId(), pagenumber, pagesize, search_name);
		} catch (Exception e) {
			return null;
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
			@RequestParam(value = "rows", required = true) int pagesize) {
		try {
			User user = (User) request.getSession().getAttribute("user");
			String params = request.getParameter("params").trim();
			String values = request.getParameter("values").trim();
			JSONObject json_object = new JSONObject();
			json_object.put("params", params);
			json_object.put("values", values);
			return appManager.advancedSearchApp(user.getTenantId(), pagenumber, pagesize, json_object);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * @author yangqinglin
	 * @time 2015年3月9日
	 * @description 通过应用的ID获取（应用-->集群-->镜像版本-->容器列表）树状结构信息
	 */
	@RequestMapping(value = "/topology", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String topology(HttpServletRequest request, Integer App_Id) {
		User user = (User) request.getSession().getAttribute("user");
		JSONObject json_object = appManager.getAppTopoJson(user.getTenantId(), App_Id);
		return json_object.toString();
	}
	
	/**
	 * @param request
	 * @param envId
	 * @return
	 */
	@RequestMapping(value = "/checkAppInEnv", method = {RequestMethod.POST })
	@ResponseBody
	public int checkAppInEnv(HttpServletRequest request,int appId, int envId){
		return appManager.checkAppInEnv(appId, envId);
	}
	
	/**
	 * @param request
	 * @param clusterPort
	 * @param appId
	 * @return
	 */
	@RequestMapping(value = "/checkAppInCluster", method = {RequestMethod.POST })
	@ResponseBody
	public int checkAppInCluster(HttpServletRequest request, String clusterPort, int appId){
		return appManager.checkAppInCluster(clusterPort, appId);
	}
	

	@RequestMapping(value = "/checkAppName", method = {RequestMethod.POST })
	@ResponseBody
	public Boolean checkAppName(HttpServletRequest request, String appName){
		return appManager.checkAppName(appName);
	}
	
	@RequestMapping(value = "/checkAppUrl", method = {RequestMethod.POST })
	@ResponseBody
	public Boolean checkAppUrl(HttpServletRequest request, Integer balanceId,String appUrl){
		if(balanceId==null||StringUtils.isEmpty(appUrl)){
			return false;
		}
		return appManager.checkAppUrl(balanceId,appUrl);
	}

}

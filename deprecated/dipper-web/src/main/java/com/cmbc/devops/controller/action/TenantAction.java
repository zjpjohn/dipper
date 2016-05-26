package com.cmbc.devops.controller.action;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.cmbc.devops.bean.GridBean;
import com.cmbc.devops.bean.Result;
import com.cmbc.devops.constant.Status;
import com.cmbc.devops.constant.Type;
import com.cmbc.devops.entity.App;
import com.cmbc.devops.entity.User;
import com.cmbc.devops.manager.AppManager;
import com.cmbc.devops.manager.TenantManager;
import com.cmbc.devops.model.TenantModel;

/**
 * date：2016年1月5日 下午3:04:31 project name：cmbc-devops-web
 * 
 * @author langzi
 * @version 1.0
 * @since JDK 1.7.0_21 file name：AppAction.java description：
 */
@RequestMapping("tenant")
@Controller
public class TenantAction {

	@Autowired
	private TenantManager tenantManager;

	@Autowired
	private AppManager appManager;

	private final static Logger LOGGER = Logger.getLogger(TenantAction.class);

	@RequestMapping("/list")
	@ResponseBody
	/** Tenant Finished */
	public GridBean list(HttpServletRequest request, @RequestParam(value = "page", required = true) int pagenumber,
			@RequestParam(value = "rows", required = true) int pagesize) {
		/* #采用多租户方式，透传租户资源的ID */
		User user = (User) request.getSession().getAttribute("user");
		/* 直接传递用户的租户资源ID */
		return tenantManager.listOnePageTenants(pagenumber, pagesize, user.getTenantId());
	}

	@RequestMapping(value = "/add", method = { RequestMethod.POST })
	@ResponseBody
	/** Tenant Finished */
	public JSONObject addTenant(HttpServletRequest request, TenantModel tenantModel) {
		User user = (User) request.getSession().getAttribute("user");
		SimpleDateFormat sdf = (SimpleDateFormat) DateFormat.getDateInstance();
		sdf.applyPattern("yyyy'年'MM'月'dd'日'");
		/* 获取前台输入的开始时间和结束时间参数 */
		try {
			Date inserviceDate = sdf.parse(tenantModel.getBeginDate());
			Date expireDate = sdf.parse(tenantModel.getEndDate());
			tenantModel.setInserviceDate(inserviceDate);
			tenantModel.setExpireDate(expireDate);
		} catch (ParseException e) {
			LOGGER.error("parse date string:" + tenantModel.getBeginDate() + " error.", e);
		}

		tenantModel.setResType((byte) Type.TENANT.TENANT.ordinal());
		tenantModel.setTenantStatus((byte) Status.TENANT.ACTIVATE.ordinal());
		tenantModel.setCreateTime(new Date());
		tenantModel.setCreator(user.getUserId());
		/* 创建租户的父ID设置为0 */
		tenantModel.setParentId(0);
		return tenantManager.addTenant(tenantModel);
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
}

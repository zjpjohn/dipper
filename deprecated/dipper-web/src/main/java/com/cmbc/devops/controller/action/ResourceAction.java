package com.cmbc.devops.controller.action;

import java.util.Date;

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
import com.cmbc.devops.constant.Status;
import com.cmbc.devops.entity.DkResource;
import com.cmbc.devops.entity.DkResourceWithUser;
import com.cmbc.devops.entity.User;
import com.cmbc.devops.manager.ResourceManager;

/**
 * date：2015年8月19日 上午12:18:56 project name：cmbc-devops-web
 * 
 * @author mayh
 * @version 1.0
 * @since JDK 1.7.0_21 file name：ApplicationAction.java description：
 */
@RequestMapping("resource")
@Controller
public class ResourceAction {
	private static final Logger LOGGER = Logger.getLogger(ResourceAction.class);

	@Resource
	private ResourceManager resourceManager;

	/**
	 * @author youngtsinglin
	 * @time 2015年10月8日 10:24
	 * @description 添加查询仓库详细信息
	 */
	@RequestMapping("/detail/{id}.html")
	public ModelAndView detail(@PathVariable int id) {
		ModelAndView mav = new ModelAndView("resource/detail");
		DkResourceWithUser dk_resuser = resourceManager.detail(id);
		mav.addObject("dk_resuser", dk_resuser);
		return mav;
	}

	/**
	 * @author yangqinlgin
	 * @datetime 2015年9月8日 9:27
	 * @description 添加增加定制资源部分内容
	 */
	@RequestMapping(value = "/create", method = { RequestMethod.POST })
	@ResponseBody
	public Result create(HttpServletRequest request, DkResource resource) {
		User user = (User) request.getSession().getAttribute("user");
		if (resource != null) {
			/* 插入创建人ID和创建时间，创建定制资源项 */
			resource.setResCreator(user.getUserId());
			resource.setResCreatetime(new Date());
			resource.setResBLKIO(0);
			resource.setResStatus((byte) Status.RESOURCE.NORMAL.ordinal());
			Result return_result = resourceManager.createResource(resource);
			return return_result;
		} else {
			Result rtn_result = new Result(false, "参数输入异常!");
			LOGGER.info(user.getUserName() + ":" + rtn_result.getMessage());
			return rtn_result;
		}
	}

	/**
	 * @author yangqinlgin
	 * @datetime 2015年9月8日 9:27
	 * @description 添加更新定制资源部分内容
	 */
	@RequestMapping(value = "/update", method = { RequestMethod.POST })
	@ResponseBody
	public Result updateResource(HttpServletRequest request, DkResource resource) {
		User user = (User) request.getSession().getAttribute("user");
		if (resource != null) {
			/* 插入创建人ID和创建时间，创建定制资源项 */
			Result return_result = resourceManager.updateResource(resource);
			return return_result;
		} else {
			Result rtn_result = new Result(false, "参数输入异常!");
			LOGGER.info(user.getUserName() + ":" + rtn_result.getMessage());
			return rtn_result;
		}
	}

	/**
	 * @author yangqinlgin
	 * @datetime 2015年9月8日 9:27
	 * @description 删除定制资源部分内容
	 */
	@RequestMapping(value = "/delete", method = { RequestMethod.POST })
	@ResponseBody
	public Result deleteResource(HttpServletRequest request,String resIds,String resNames) {
		JSONObject param = new JSONObject();
		User user = (User) request.getSession().getAttribute("user");
		if (resIds != null) {
			/* 插入创建人ID和创建时间，创建定制资源项 */
			param.put("res_ids", resIds);
			param.put("res_names", resNames);
			param.put("userId", user.getUserId());
			Result return_result = resourceManager.deleteResources(param);
			return return_result;
		} else {
			LOGGER.info(user.getUserName() + ":" + "参数输入异常!");
			return new Result(false, "参数输入异常!");
		}
	}

	/**
	 * @author youngtsinglin
	 * @time 2015年9月6日 10:35
	 * @description 将原来返回字符串的方法修改为GridBean的方式
	 */
	@RequestMapping("/list")
	@ResponseBody
	public GridBean list(HttpServletRequest request, @RequestParam(value = "page", required = true) int pagenumber,
			@RequestParam(value = "rows", required = true) int pagesize, DkResource resource) {
		try {
			User user = (User) request.getSession().getAttribute("user");
			return resourceManager.resourceAllList(user.getUserId(), pagenumber, pagesize);
		} catch (Exception e) {
			LOGGER.error("查询资源列表失败！", e);
			return null;
		}
	}

	/**
	 * @author youngtsinglin
	 * @time 2015年9月6日 10:35
	 * @description 将原来返回字符串的方法修改为GridBean的方式
	 */
	@RequestMapping(value = "/all", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public JSONArray allResJsonArray(HttpServletRequest request, DkResource resource) {
		try {
			User user = (User) request.getSession().getAttribute("user");
			return resourceManager.allResJsonArray(user.getUserId(), resource);
		} catch (Exception e) {
			LOGGER.error("查询资源列表JSONArray失败！", e);
			return null;
		}
	}

	/**
	 * @author youngtsinglin
	 * @time 2015年9月6日 10:35
	 * @description 将原来返回字符串的方法修改为GridBean的方式
	 */
	@RequestMapping(value = "/pubres", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public JSONArray publicResources(HttpServletRequest request) {
		try {
			return resourceManager.publicResources();
		} catch (Exception e) {
			LOGGER.error("查询公共资源列表JSONArray失败！", e);
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
			String search_name = request.getParameter("search_name").trim();
			return resourceManager.resourceSearchAllList(user.getUserId(), pagenumber, pagesize, search_name);
		} catch (Exception e) {
			LOGGER.error("查询定制资源列表失败！", e);
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
			JSONObject jO = new JSONObject();
			jO.put("params", params);
			jO.put("values", values);
			return resourceManager.advancedSearchRes(user.getUserId(), pagenumber, pagesize, jO);
		} catch (Exception e) {
			LOGGER.error("高级查询定制资源列表失败！", e);
			return null;
		}
	}

	@RequestMapping(value = "/checkResName", method = { RequestMethod.POST })
	@ResponseBody
	public Boolean checkName(HttpServletRequest request, String resName) {
		return resourceManager.checkAppName(resName);
	}
}

package com.cmbc.devops.controller.action;

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
import com.cmbc.devops.entity.Env;
import com.cmbc.devops.entity.User;
import com.cmbc.devops.manager.EnvManager;
import com.cmbc.devops.query.QueryList;

/**
 * date：2016年1月11日 下午2:47:29 project name：cmbc-devops-web
 * 
 * @author langzi
 * @version 1.0
 * @since JDK 1.7.0_21 file name：EnvAction.java description：
 */
@RequestMapping("env")
@Controller
public class EnvAction {

	private static final Logger LOGGER = Logger.getLogger(EnvAction.class);

	@Autowired
	private EnvManager manager;
	@Autowired
	private QueryList query;

	@RequestMapping("/list")
	@ResponseBody
	public GridBean list(HttpServletRequest request, @RequestParam(value = "page", required = true) int pagenumber,
			@RequestParam(value = "rows", required = true) int pagesize) {
		return query.listOnePageEnvs(pagenumber, pagesize);
	}

	@RequestMapping("/listAll")
	@ResponseBody
	public String listAll(HttpServletRequest request) {
		return query.listAllEnv().toString();
	}

	@RequestMapping(value = "/add", method = { RequestMethod.POST })
	@ResponseBody
	public Result add(HttpServletRequest request, Env env) {
		User user = (User) request.getSession().getAttribute("user");
		env.setEnvCreatetime(new Date());
		env.setEnvCreator(user.getUserId());
		env.setEnvStatus((byte) Status.ENVIRONMENT.NORMAL.ordinal());
		return manager.add(env);
	}

	@RequestMapping(value = "/modify", method = { RequestMethod.POST })
	@ResponseBody
	public Result modify(HttpServletRequest request, Env env) {
		return manager.modify(env);
	}

	@RequestMapping(value = "/remove", method = { RequestMethod.POST })
	@ResponseBody
	public Result remove(HttpServletRequest request, int envId) {
		return manager.remove(envId);
	}

	@RequestMapping(value = "/checkEnvName", method = { RequestMethod.POST })
	@ResponseBody
	public Boolean checkEnvName(HttpServletRequest request, String envName) {
		return manager.checkEnvName(envName);
	}

	/**
	 * @author yangqinglin
	 * @time 2016年1月21日
	 * @description 返回用户模糊查询的结果。
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
			return manager.envSearchAllList(user.getUserId(), pagenumber, pagesize, search_name);
		} catch (Exception e) {
			LOGGER.error("模糊查询环境列表失败！", e);
			return null;
		}
	}

	/**
	 * @author yangqinglin
	 * @time 2015年1月21日 10:35
	 * @description 高级查询返回所有环境结果
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
			return manager.advancedSearchEnvs(user.getUserId(), pagenumber, pagesize, json_object);
		} catch (Exception e) {
			LOGGER.error("高级查询环境列表失败！", e);
			return null;
		}
	}

}

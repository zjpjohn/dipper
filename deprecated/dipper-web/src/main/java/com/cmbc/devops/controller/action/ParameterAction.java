/**
 * 
 */
package com.cmbc.devops.controller.action;

import java.text.ParseException;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cmbc.devops.bean.GridBean;
import com.cmbc.devops.bean.Result;
import com.cmbc.devops.constant.Status;
import com.cmbc.devops.entity.Parameter;
import com.cmbc.devops.entity.User;
import com.cmbc.devops.manager.ParameterManager;

/**
 * date：2015年8月21日 下午5:25:13 project name：cmbc-devops-web
 * 
 * @author mayh
 * @version 1.0
 * @since JDK 1.7.0_21 file name：ParameterAction.java description：
 */
@RequestMapping("param")
@Controller
public class ParameterAction {
	private static final Logger LOGGER = Logger.getLogger(ParameterAction.class);

	@Autowired
	private ParameterManager parameterManager;

	/**
	 * 参数分页
	 * 
	 * @param request
	 * @param page
	 * @param rows
	 * @param name
	 * @return
	 */
	@RequestMapping(value = "/list")
	@ResponseBody
	/** @date:2016年3月29日 添加租户维度 */
	public GridBean list(HttpServletRequest request, int page, int rows, String name) {
		User user = (User) request.getSession().getAttribute("user");
		int userId = user.getUserId();
		int tenantId = user.getTenantId();
		return parameterManager.parameterList(userId, tenantId, name, page, rows);
	}

	/**
	 * 查询参数
	 * 
	 * @param request
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "/all")
	@ResponseBody
	/** @date:2016年3月29日 添加租户维度 */
	public GridBean all(HttpServletRequest request, @RequestParam(value = "page", required = true) int page,
			@RequestParam(value = "rows", required = true) int rows, Parameter parameter) throws Exception, Exception {
		if (parameter.getParamName() != null) {
			String search_name = request.getParameter("paramName").trim();
			parameter.setParamName(search_name);
		}
		User creator = (User) request.getSession().getAttribute("user");
		int tenantId = creator.getTenantId();
		parameter.setTenantId(tenantId);
		return parameterManager.paramAllList(creator.getUserId(), page, rows, parameter);
	}

	/**
	 * 添加参数
	 * 
	 * @param request
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "/create", method = { RequestMethod.POST })
	@ResponseBody
	/** @date:2016年3月29日 添加租户维度 */
	public Result create(HttpServletRequest request, Parameter parameter) {
		if (parameter != null) {
			User user = (User) request.getSession().getAttribute("user");
			parameter.setParamCreator(user.getUserId());
			parameter.setTenantId(user.getTenantId());
			parameter.setParamStatus((byte) Status.PARAMETER.ACTIVATE.ordinal());
			return parameterManager.create(parameter);
		} else {
			return new Result(false, "参数添加失败：参数传入异常！");
		}
	}

	/**
	 * 删除参数
	 * 
	 * @param request
	 * @param ids
	 * @return
	 */
	@RequestMapping(value = "/remove/batch", method = { RequestMethod.POST })
	@ResponseBody
	public Result delete(HttpServletRequest request, String ids) {
		if (null == ids || ids.isEmpty()) {
			return new Result(false, "传入参数为空！");
		} else {
			return this.parameterManager.delete(ids);
		}
	}

	/**
	 * 删除参数
	 * 
	 * @param request
	 * @param ids
	 * @return
	 */
	@RequestMapping(value = "/delete/{id}")
	@ResponseBody
	public Result delete(HttpServletRequest request, @PathVariable Integer id) {
		if (null == id) {
			return new Result(false, "传入参数为空！");
		} else {
			return this.parameterManager.delete(id);
		}
	}

	/**
	 * 更新参数
	 * 
	 * @param request
	 * @param param
	 * @return
	 * @throws ParseException
	 */
	@RequestMapping(value = "/update", method = { RequestMethod.POST })
	@ResponseBody
	public Result update(HttpServletRequest request, Parameter param) throws ParseException {
		if (param == null) {
			return new Result(false, "修改参数失败：传入参数有误");
		} else {
			return parameterManager.update(param);
		}
	}

	@RequestMapping(value = "/allValue", method = { RequestMethod.GET })
	@ResponseBody
	public String parameterList(HttpServletRequest request) {
		User user = (User) request.getSession().getAttribute("user");
		int tenantId = user.getTenantId();
		JSONArray json_array = parameterManager.allParam(tenantId);
		return json_array.toString();
	}

	@RequestMapping(value = "/pubParams", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String pubParams(HttpServletRequest request) {
		JSONArray json_array = parameterManager.pubParams();
		return json_array.toString();
	}

	@RequestMapping(value = "/checkName", method = { RequestMethod.POST })
	@ResponseBody
	public boolean checkName(HttpServletRequest request, String paramName) {
		User user = (User) request.getSession().getAttribute("user");
		int tenantId = user.getTenantId();
		return parameterManager.checkName(tenantId, paramName);
	}

	@RequestMapping("/advancedSearch")
	@ResponseBody
	/** @date:2016年3月29日 添加租户维度 */
	public GridBean advancedSearch(HttpServletRequest request,
			@RequestParam(value = "page", required = true) int pagenum,
			@RequestParam(value = "rows", required = true) int pagesize, Parameter parameter) {
		try {
			User user = (User) request.getSession().getAttribute("user");
			int userId = user.getUserId();
			int tenantId = user.getTenantId();
			parameter.setTenantId(tenantId);
			String params = request.getParameter("params").trim();
			String values = request.getParameter("values").trim();
			JSONObject json_object = new JSONObject();
			json_object.put("params", params);
			json_object.put("values", values);
			return parameterManager.advancedSearchParam(userId, pagenum, pagesize, parameter, json_object);
		} catch (Exception e) {
			LOGGER.error("查询参数列表失败！", e);
			return null;
		}
	}

}

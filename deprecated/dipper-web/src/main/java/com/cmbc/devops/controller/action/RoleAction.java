package com.cmbc.devops.controller.action;

import java.text.ParseException;

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
import com.cmbc.devops.entity.Role;
import com.cmbc.devops.entity.User;
import com.cmbc.devops.manager.RoleManager;
import com.cmbc.devops.query.QueryList;

@Controller
@RequestMapping("role")
public class RoleAction {
	private static final Logger LOGGER = Logger.getLogger(RoleAction.class);

	@Resource
	private QueryList queryList;
	@Resource
	private RoleManager roleManager;

	/*
	 * 角色详情列表
	 * by luogan
	 */
	@RequestMapping("/detail/{id}.html")
	public ModelAndView detail(@PathVariable Integer id) {
		ModelAndView mav = new ModelAndView("role/detail");
		Role role = roleManager.detail(id);
		mav.addObject("roleInfo", role);
		return mav;
	}
	
	/*
	 * 查询角色列表
	 * 包含按照角色名称进行模糊查询
	 * by luogan
	 */
	@RequestMapping(value = "/all")
	@ResponseBody
	public GridBean all(HttpServletRequest request,
			@RequestParam(value = "page", required = true) int page,
			@RequestParam(value = "rows", required = true) int rows, Role role) {
		User user = (User) request.getSession().getAttribute("user");
		if (role.getRoleName()!= null) {
			String search_name = request.getParameter("roleName").trim();
			role.setRoleName(search_name);
		}
		return queryList.roleList(user.getUserId(), page, rows, role);
	}

	/*
	 * 查询角色列表 
	 * by luogan
	 */
	@RequestMapping(value = "/list", method = { RequestMethod.GET })
	@ResponseBody
	public String allList(HttpServletRequest request, Role role) {
		User user = (User) request.getSession().getAttribute("user");
		JSONArray ja = queryList.queryAllRoleList(user.getUserId(), role);
		return ja.toString();
	}

	/*
	 * 查询角色权限列表 
	 * by luogan
	 */
	@RequestMapping(value = "/roleAuth", method = { RequestMethod.GET })
	@ResponseBody
	public String roleAuthList(HttpServletRequest request, com.cmbc.devops.entity.RoleAction roleAction) {
		User user = (User) request.getSession().getAttribute("user");
		JSONArray ja = queryList.getRoleAuthList(user.getUserId(), roleAction);
		return ja.toString();
	}
	
	/*
	 * 更新角色 
	 * by luogan
	 */
	@RequestMapping(value = "/update", method = { RequestMethod.POST })
	@ResponseBody
	public Result update(HttpServletRequest request, Role role)
			throws ParseException {
		if (role == null) {
			return new Result(false, "修改角色失败：传入角色参数有误");
		} else {
			return roleManager.update(role);
		}
	}

	/*
	 * 权限授权给角色
	 * by luogan
	 */
	@RequestMapping(value = "/authToRole", method = { RequestMethod.POST })
	@ResponseBody
	public Result authToRole(HttpServletRequest request, String roles,
			String auths) {

		User user = (User) request.getSession().getAttribute("user");
		JSONArray ja = new JSONArray();
		JSONObject params = new JSONObject();
		params.put("userId", user.getUserId());
		params.put("roles", roles);
		params.put("auths", auths);
		ja.add(0, params);
		return roleManager.authToRole(params);
	}
	
	/**
	 * 多功能查询
	 * @param request
	 * @param pagenum
	 * @param pagesize
	 * @param role
	 * @return
	 */
	@RequestMapping("/advancedSearch")
	@ResponseBody
	public GridBean advancedSearch(HttpServletRequest request,
			@RequestParam(value = "page", required = true) int pagenum,
			@RequestParam(value = "rows", required = true) int pagesize, Role role) {
		try {
			User user = (User) request.getSession().getAttribute("user");
			String params = request.getParameter("params").trim();
			String values = request.getParameter("values").trim();
			JSONObject json_object = new JSONObject();
			json_object.put("params", params);
			json_object.put("values", values);
			return roleManager.advancedSearchRole(user.getUserId(), pagenum, pagesize, role,json_object);

		} catch (Exception e) {
			LOGGER.error("查询角色列表失败！", e);
			return null;
		}
	}
	
	/*
	 * 查询用户角色列表 
	 * by luogan
	 */
	@RequestMapping(value = "/userRoleList", method = { RequestMethod.GET })
	@ResponseBody
	public String userRoleList(HttpServletRequest request, int userId) {
		JSONArray ja = queryList.queryAllRoleListByUserId(userId);
		return ja.toString();
	}
	
	/*
	 * 创建角色 
	 * by luogan
	 */
	@RequestMapping(value = "/create", method = { RequestMethod.POST })
	@ResponseBody
	public Result create(HttpServletRequest request, Role role)
			throws ParseException {
		if(role!=null){
			role.setRoleStatus((byte) Status.ROLE.NORMAL.ordinal());
		}
		if (role == null) {
			return new Result(false, "创建角色失败：传入角色参数有误");
		} else {
			User user = (User) request.getSession().getAttribute("user");
			role.setRoleCreator(user.getUserId());
			return roleManager.create(role);
		}
	}
			
	/*
	 * 角色名唯一性校验 
	 * by zll
	 */
	@RequestMapping(value = "/checkRoleName", method = { RequestMethod.GET })
	@ResponseBody
	public Boolean checkRoleName(HttpServletRequest request, String roleName) {
		return roleManager.checkRoleName(roleName);
		
	}
			
}

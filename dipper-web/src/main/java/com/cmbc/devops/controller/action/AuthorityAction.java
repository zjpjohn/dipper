package com.cmbc.devops.controller.action;

import java.text.ParseException;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cmbc.devops.bean.GridBean;
import com.cmbc.devops.bean.Result;
import com.cmbc.devops.entity.Authority;
import com.cmbc.devops.entity.User;
import com.cmbc.devops.manager.AuthorityManager;
import com.cmbc.devops.query.QueryList;

@Controller
@RequestMapping("auth")
public class AuthorityAction {
	private static final Logger LOGGER = Logger.getLogger(AuthorityAction.class);
	
	@Resource
	private QueryList queryList;
	@Resource
	private AuthorityManager authorityManager;

	/**
	 * 查询权限列表
	 * 包含按照权限名称进行模糊查询
	 * @param request
	 * @param page
	 * @param rows
	 * @param authority
	 * @return
	 */
	@RequestMapping(value = "/all")
	@ResponseBody
	public GridBean all(HttpServletRequest request,
			@RequestParam(value = "page", required = true) int page,
			@RequestParam(value = "rows", required = true) int rows,
			Authority authority) {
		User user = (User) request.getSession().getAttribute("user");
		if (authority.getActionName()!= null) {
			String search_name = request.getParameter("actionName").trim();
			authority.setActionName(search_name);
		}
		return queryList.authorityList(user.getUserId(), page, rows, authority);
	}

	/**
	 * 查询权限列表
	 * 包含按照权限的依赖关系
	 * @param request
	 * @param authority
	 * @return
	 */
	@RequestMapping(value = "/list", method = { RequestMethod.GET })
	@ResponseBody
	public String allList(HttpServletRequest request, Authority authority) {
		User user = (User) request.getSession().getAttribute("user");
		JSONArray ja = queryList.queryAllList(user.getUserId(), authority);
		return ja.toString();
	}

	/**
	 * 获取所有权限并拼装成json
	 * @param request
	 * @param authority
	 * @return
	 */
	@RequestMapping(value = "/tree", method = { RequestMethod.GET })
	@ResponseBody
	public String treeList(HttpServletRequest request, Authority authority) {
		User user = (User) request.getSession().getAttribute("user");
		JSONObject result = new JSONObject();
		result.put("success", true);
		JSONArray data = new JSONArray();
		data=getTreeData(user.getUserId(), 0, data);
		result.put("data", data);
		return result.toString();
	}
	/**
	 * 递归获取权限树
	 * @param userid
	 * @param parentid
	 * @param data
	 * @return JSONArray
	 */
	private JSONArray getTreeData(Integer userid,Integer parentid,JSONArray data) {
		JSONObject temp = null;
		List<Authority> childrenAuths = queryList.queryListByActionParentId(userid,parentid);
		if (childrenAuths.size() > 0) {
			for (Authority authChi : childrenAuths) {
				temp = new JSONObject();
				temp.put("id", authChi.getActionId());
				temp.put("name", authChi.getActionName());
				temp.put("pId", parentid);
				data.add(temp);
				data=getTreeData(userid, authChi.getActionId(), data);
			}
		}
		return data;
	}
	
	/**
	 *  更新权限
	 * @param request
	 * @param authority
	 * @return
	 * @throws ParseException
	 */
	@RequestMapping(value = "/update", method = { RequestMethod.POST })
	@ResponseBody
	public Result update(HttpServletRequest request, Authority authority)
			throws ParseException {
		if (authority == null) {
			return new Result(false, "修改权限失败：传入权限参数有误");
		} else {
			return authorityManager.update(authority);
		}
	}

	/**
	 * 高级查询
	 * @param request
	 * @param pagenum
	 * @param pagesize
	 * @param authority
	 * @return
	 */
	@RequestMapping("/advancedSearch")
	@ResponseBody
	public GridBean advancedSearch(HttpServletRequest request,
			@RequestParam(value = "page", required = true) int pagenum,
			@RequestParam(value = "rows", required = true) int pagesize, Authority authority) {
		try {
			User user = (User) request.getSession().getAttribute("user");
			String params = request.getParameter("params").trim();
			String values = request.getParameter("values").trim();
			JSONObject json_object = new JSONObject();
			json_object.put("params", params);
			json_object.put("values", values);
			return authorityManager.advancedSearchAuth(user.getUserId(), pagenum, pagesize, authority,json_object);

		} catch (Exception e) {
			LOGGER.error("查询权限列表失败！", e);
			return null;
		}
	}
	
	
	/***
	 * 通过角色id获取其下所有的权限信息
	 * @param request
	 * @param roleId
	 * @return
	 */
	@RequestMapping(value = "/treeByRoleId", method = { RequestMethod.GET })
	@ResponseBody
	public String treeByRoleId(HttpServletRequest request, Integer roleId) {
		JSONObject result = new JSONObject();
		result.put("success", true);
		JSONArray data = new JSONArray();
		// ztree
		data=getRoleTreeData(roleId, 0, data);
		result.put("data", data);
		return result.toString();
	}
	
	private JSONArray getRoleTreeData(Integer roleid,Integer parentid,JSONArray data) {
		JSONObject temp = null;
		List<Authority> childrenAuths = queryList.getAuthListByRoleId(roleid,parentid);
		if (childrenAuths.size() > 0) {
			for (Authority authChi : childrenAuths) {
				temp = new JSONObject();
				temp.put("id", authChi.getActionId());
				temp.put("name", authChi.getActionName());
				temp.put("pId", parentid);
				data.add(temp);
				data=getRoleTreeData(roleid, authChi.getActionId(), data);
			}
		}
		return data;
	}
	
}

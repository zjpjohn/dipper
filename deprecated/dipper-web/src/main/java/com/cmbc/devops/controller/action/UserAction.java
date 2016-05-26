package com.cmbc.devops.controller.action;

import java.util.Date;

import javax.annotation.Resource;
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
import com.cmbc.devops.config.EmailCfg;
import com.cmbc.devops.constant.Status;
import com.cmbc.devops.entity.User;
import com.cmbc.devops.manager.UserManager;
import com.cmbc.devops.query.QueryList;
import com.cmbc.devops.util.Base64;
import com.cmbc.devops.util.EmailSendUtil;
import com.cmbc.devops.util.HashUtil;

@Controller
@RequestMapping("user")
public class UserAction {
	private static final Logger LOGGER = Logger.getLogger(UserAction.class);

	@Resource
	private QueryList queryList;
	@Resource
	private UserManager userManager;
	@Autowired
	private EmailCfg config;

	/*
	 * 用户详情列表 by luogan
	 */
	@RequestMapping("/detail/{id}.html")
	public ModelAndView detail(@PathVariable Integer id) {
		ModelAndView mav = new ModelAndView();
		User user = userManager.detail(id);
		if (user != null) {
			mav.addObject("userInfo", user);
			mav.setViewName("user/detail");
		} else {
			mav.setViewName("/404");
		}
		return mav;
	}

	/*
	 * 查询用户列表 包含按照用户名称进行模糊查询 by luogan
	 */
	@RequestMapping(value = "/all")
	@ResponseBody
	public GridBean all(HttpServletRequest request, @RequestParam(value = "page", required = true) int page,
			@RequestParam(value = "rows", required = true) int rows, User user) throws Exception, Exception {
		if (user.getUserName() != null) {
			String search_name = request.getParameter("userName").trim();
			user.setUserName(search_name);
		}
		User creator = (User) request.getSession().getAttribute("user");
		user.setTenantId(creator.getTenantId());
		return queryList.userList(creator.getUserId(), page, rows, user);
	}

	/*
	 * 创建用户 by luogan
	 */
	@RequestMapping(value = "/create", method = { RequestMethod.POST })
	@ResponseBody
	public Result create(HttpServletRequest request, User user) throws Exception {
		if (user != null) {
			User creator = (User) request.getSession().getAttribute("user");
			user.setUserCreator(creator.getUserId());
			user.setUserStatus((byte) Status.USER.NORMAL.ordinal());
			user.setUserLevel(1);
			// 创建用户时默认初始化密码是123456
			user.setUserPass(HashUtil.md5Hash("123456"));
			user.setUserCreatedate(new Date());
			String path = request.getContextPath();
			String bath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path
					+ "/";
			return userManager.create(user, bath);
		} else {
			return new Result(false, "用户添加失败：用户参数传入异常！");
		}
	}

	/*
	 * 更新用户 by luogan
	 */
	@RequestMapping(value = "/update", method = { RequestMethod.POST })
	@ResponseBody
	public Result update(HttpServletRequest request, User user) {
		if (user == null) {
			return new Result(false, "修改用户失败：传入用户参数有误");
		} else {
			return userManager.update(user);
		}
	}

	/*
	 * 删除用户 by luogan
	 */
	@RequestMapping(value = "/delete/{id}")
	@ResponseBody
	public Result delete(HttpServletRequest request, @PathVariable Integer id) {
		if (null == id) {
			return new Result(false, "传入参数为空！");
		} else {
			return userManager.delete(id);
		}
	}

	/*
	 * 激活用户 by luogan
	 */
	@RequestMapping(value = "/active/{id}")
	@ResponseBody
	public Result active(HttpServletRequest request, @PathVariable Integer id) {
		if (null == id) {
			return new Result(false, "传入参数为空！");
		} else {
			return userManager.active(id);
		}
	}

	/*
	 * 角色授权给用户 by luogan
	 */
	@RequestMapping(value = "/authToUser", method = { RequestMethod.POST })
	@ResponseBody
	public Result authToUser(HttpServletRequest request, String users, String roles) {
		User user = (User) request.getSession().getAttribute("user");
		JSONObject params = new JSONObject();
		params.put("userId", user.getUserId());
		params.put("users", users);
		params.put("roles", roles);
		return userManager.authToUser(params);
	}

	/*
	 * 用户名唯一性验证 by luogan
	 */
	@RequestMapping(value = "/checkName", method = { RequestMethod.POST })
	@ResponseBody
	public Boolean checkName(HttpServletRequest request, String userName) {
		return userManager.checkUserName(userName);
	}

	/**
	 * 多条件查询用户列表
	 * 
	 * @param request
	 * @param pagenum
	 * @param pagesize
	 * @param user
	 * @return
	 */
	@RequestMapping("/advancedSearch")
	@ResponseBody
	public GridBean advancedSearch(HttpServletRequest request,
			@RequestParam(value = "page", required = true) int pagenum,
			@RequestParam(value = "rows", required = true) int pagesize, User user) {
		try {
			User userSession = (User) request.getSession().getAttribute("user");
			int tenantId = userSession.getTenantId();
			user.setTenantId(tenantId);
			String params = request.getParameter("params").trim();
			String values = request.getParameter("values").trim();
			JSONObject json_object = new JSONObject();
			json_object.put("params", params);
			json_object.put("values", values);
			return userManager.advancedSearchUser(userSession.getUserId(), pagenum, pagesize, user, json_object);

		} catch (Exception e) {
			LOGGER.error("查询用户列表失败！", e);
			return null;
		}
	}

	/*
	 * 修改密码 by luogan
	 */
	@RequestMapping(value = "/modifyPass", method = { RequestMethod.POST })
	@ResponseBody
	public Result modifyPass(HttpServletRequest request, String UD, String PASS) {
		if ("".equals(UD) || UD == null) {
			return null;
		} else {
			Integer userid = Integer.valueOf(Integer.valueOf(Base64.getFromBase64(UD)));
			User userinfo = userManager.detail(userid);
			if (userinfo != null) {
				if (userinfo.getUserStatus() == Status.USER.DELETE.ordinal()) {
					return new Result(false, "该账号已被冻结,请联系管理员！");
				}
			}
			User user = new User();
			user.setUserId(userid);
			user.setUserPass(HashUtil.md5Hash(PASS));
			return userManager.update(user);
		}
	}

	/**
	 * 用户登出系统
	 * 
	 * @param request
	 * @param userId
	 */
	@RequestMapping(value = "/logout", method = { RequestMethod.POST })
	public void logout(HttpServletRequest request, @RequestParam int userId) {
		request.getSession().invalidate();
	}

	/*
	 * 忘记密码 by luogan
	 */
	@RequestMapping(value = "/forgetPass", method = { RequestMethod.POST })
	@ResponseBody
	public Result forgetPass(HttpServletRequest request, String userName) {
		User user = userManager.getUserByName(userName);
		if (user == null) {
			return new Result(false, "该用户不存在,请输入正确的用户名！");
		} else {
			if (user.getUserStatus() == Status.USER.DELETE.ordinal()) {
				return new Result(false, "该用户已被冻结，请联系管理员！");
			}
			try {
				String path = request.getContextPath();
				String bath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
						+ path + "/";
				EmailSendUtil.sendMail(config, "cmbc账户密码修改",
						user.getUserName() + ",您好:</br> 请您<a href='" + bath + "user/updateUser/"
								+ Base64.getBase64(String.valueOf(user.getUserId())) + ".html'>修改密码</a>。",
						user.getUserMail());
			} catch (Exception e) {
				LOGGER.error("send user Email failed", e);
				return new Result(false, "邮件发送失败,请联系邮箱管理员！");
			}
			return new Result(true, "请您查收邮件并重置密码！");
		}
	}

	/**
	 * 校验用户是否在别处登陆
	 * 
	 * @param request
	 * @return Result
	 */
	@RequestMapping(value = "/checkuser", method = { RequestMethod.GET })
	@ResponseBody
	public Result checkuser(HttpServletRequest request) {
		User user = (User) request.getSession().getAttribute("user");
		if (user != null) {
			// 获取session中的userLoginStatus
			String old_userFlag = user.getUserLoginStatus();
			User info = userManager.detail(user.getUserId());
			// 如果session中的userLoginStatus与数据库中的不一致则用户退出登陆
			if (info.getUserLoginStatus() != null && (!info.getUserLoginStatus().equals(old_userFlag))) {
				return new Result(true, "该用户已在别处登录！");
			} else {
				return new Result(false, "");
			}
		} else {
			return new Result(false, "");
		}
	}

	/**
	 * 获取孤儿管理员（超级管理员创建的，但是没有挂靠租户资源的管理员）列表
	 * 
	 * @param HttpServletRequest
	 *            request
	 * @return JSONArray orphanMangerList
	 */
	@RequestMapping(value = "/getOrphanUsers", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public JSONArray getOrphanUsers(HttpServletRequest request) {
		User user = (User) request.getSession().getAttribute("user");
		if (user != null) {
			JSONArray orphanUsers = userManager.getOrphanUsers(user.getUserId());
			return orphanUsers;
		} else {
			return null;
		}
	}

}

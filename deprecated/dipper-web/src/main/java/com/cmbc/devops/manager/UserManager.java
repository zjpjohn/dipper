/**
 * 
 */
package com.cmbc.devops.manager;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cmbc.devops.bean.GridBean;
import com.cmbc.devops.bean.Result;
import com.cmbc.devops.config.EmailCfg;
import com.cmbc.devops.entity.Role;
import com.cmbc.devops.entity.User;
import com.cmbc.devops.entity.UserRole;
import com.cmbc.devops.service.RoleService;
import com.cmbc.devops.service.UserService;
import com.cmbc.devops.util.Base64;
import com.cmbc.devops.util.EmailSendUtil;

/**
 * date：2015年8月23日 下午8:23:30 project name：cmbc-devops-web
 * 
 * @author dingmw
 * @version 1.0
 * @since JDK 1.7.0_21 file name：ParameterManager.java description：
 */
@Component
public class UserManager {
	private static final Logger LOGGER = Logger.getLogger(UserManager.class);
	@Resource
	private UserService userService;
	@Resource
	private RoleService roleService;
	@Autowired
	private EmailCfg config;

	public Result create(User user, String bath) {
		int result = 0;
		try {
			result = userService.create(user);
		} catch (Exception e1) {
			LOGGER.error("create user falied!", e1);
			return new Result(false, "添加用户失败！");
		}
		if (result > 0) {
			try {
				EmailSendUtil.sendMail(config, "cmbc账户开通",
						user.getUserName() + ",您好:</br>您的cmbc帐号已开通，初始密码为123456,请您尽快<a href='" + bath
								+ "user/updateUser/" + Base64.getBase64(String.valueOf(user.getUserId()))
								+ ".html'>修改密码</a>。",
						user.getUserMail());
			} catch (Exception e) {
				LOGGER.error("send user Email failed!", e);
				return new Result(false, "添加用户成功.但修改密码邮件发送失败,请检查您的邮箱服务器！");
			}
			LOGGER.info("create user success");
			return new Result(true, "添加用户成功.修改密码邮件已发至用户邮箱！");
		} else {
			LOGGER.error("create user fail");
			return new Result(false, "添加用户失败！");
		}
	}

	public Result update(User user) {
		int result = 0;
		try {
			result = userService.update(user);
		} catch (Exception e) {
			LOGGER.error("Update user fail", e);
			return new Result(false, "更新用户失败！");
		}
		if (result > 0) {
			LOGGER.info("Update user success");
			return new Result(true, "更新用户成功！");
		} else {
			LOGGER.error("Update user fail");
			return new Result(true, "更新用户失败！");
		}
	}

	public Result delete(int id) {
		int result = 0;
		try {
			result = userService.delete(id);
		} catch (Exception e) {
			LOGGER.error("Update user status freeze fail", e);
			return new Result(false, "冻结用户失败！");
		}
		if (result > 0) {
			LOGGER.info("Update user status freeze success");
			return new Result(true, "冻结用户成功！");
		} else {
			LOGGER.error("Update user status freeze fail");
			return new Result(false, "冻结用户失败！");
		}
	}

	public Result active(int id) {
		int result;
		try {
			result = userService.active(id);
		} catch (Exception e) {
			LOGGER.error("Update user  status active fail", e);
			return new Result(false, "激活用户失败！");
		}
		if (result > 0) {
			LOGGER.info("Update user status active success");
			return new Result(true, "激活用户成功！");
		} else {
			LOGGER.error("Update user  status active fail");
			return new Result(false, "激活用户失败！");
		}
	}

	public Result deletes(String idString) {
		String[] idArray = idString.split(",");
		List<Integer> idList = new ArrayList<Integer>();
		for (String id : idArray) {
			idList.add(new Integer(id));
		}
		int result = userService.deletes(idList);
		if (result > 0) {
			LOGGER.info("Update users success");
			return new Result(true, "批量删除用户成功！");
		} else {
			LOGGER.error("Update users fail");
			return new Result(false, "批量删除用户失败！");
		}
	}

	public Result authToUser(JSONObject jsonObject) {

		// User user = new User();
		UserRole userRole = new UserRole();
		String users = jsonObject.getString("users");
		String roles = jsonObject.getString("roles");
		String[] userIds = users.split(",");
		String[] roleIds = roles.split(",");
		int delResult = 0;
		int addResult = 0;

		for (int i = 0; i < userIds.length; i++) {
			userRole.setUserId(Integer.parseInt(userIds[i]));
			// 先删除选中用户的所有角色
			try {
				delResult = userService.deleteByUserId(Integer.parseInt(userIds[i]));
			} catch (Exception e) {
				LOGGER.error("Update users auth fail", e);
				return new Result(false, "用户授权失败！");
			}
			// 新添加选中用户的角色
			if (delResult > 0) {
				for (int j = 0; j < roleIds.length; j++) {
					userRole.setRoleId(Integer.parseInt(roleIds[j]));
					try {
						addResult = userService.updateUserRole(userRole);
					} catch (Exception e) {
						LOGGER.error("Update user role fail", e);
						return new Result(false, "用户授权失败！");
					}
				}
			}
		}
		if (addResult > 0) {
			LOGGER.info("Update users auth success");
			return new Result(true, "用户授权成功！");
		} else {
			LOGGER.error("Update users auth fail");
			return new Result(false, "用户授权失败！");
		}
	}

	/**
	 * @author luogan
	 * @param userName
	 * @return
	 * @version 1.0
	 */
	public boolean checkUserName(String userName) {
		try {
			return userService.getUserByName(userName) == null ? true : false;
		} catch (Exception e) {
			LOGGER.error("get user by user name[" + userName + "] falied!", e);
			return false;
		}
	}

	/**
	 * @author luogan
	 * @param advancedSearchParam
	 * @return
	 * @version 1.0 2015年10月21日
	 */
	public GridBean advancedSearchUser(Integer userId, int pagenum, int pagesize, User user, JSONObject json_object) {
		try {
			LOGGER.info("Advanced search user success");
			return userService.advancedSearchUser(userId, pagenum, pagesize, user, json_object);
		} catch (Exception e) {
			LOGGER.info("Advanced search user fail");
			return null;
		}
	}

	public User detail(int userId) {
		User user = new User();
		user.setUserId(userId);
		try {
			user = userService.getUser(user);
			if (user == null) {
				return null;
			}
		} catch (Exception e) {
			LOGGER.error("get user by userid[" + userId + "] falied!", e);
			return null;
		}
		String roleLists = "";
		List<Role> roles = new ArrayList<Role>();
		try {
			roles = roleService.getRolesByUserId(userId);
		} catch (Exception e) {
			LOGGER.error("get roles by userid[" + userId + "] falied!", e);
			return null;
		}
		for (Role role : roles) {
			roleLists += role.getRoleName() + ",";
		}
		user.setRoleString(roleLists);
		return user;
	}

	/**
	 * @author zll
	 * @param userName
	 * @return
	 * @version 1.0
	 */
	public User getUserByName(String userName) {
		try {
			return userService.getUserByName(userName);
		} catch (Exception e) {
			LOGGER.error("get user by username[" + userName + "] falied!", e);
			return null;
		}
	}

	public JSONArray getOrphanUsers(int userId) {
		try {
			return userService.getOrphanUsers(userId);
		} catch (Exception e) {
			LOGGER.error("get orphan users by userid[" + userId + "] falied!", e);
			return null;
		}
	}

}

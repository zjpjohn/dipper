package com.cmbc.devops.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cmbc.devops.bean.GridBean;
import com.cmbc.devops.constant.Status;
import com.cmbc.devops.dao.RoleMapper;
import com.cmbc.devops.dao.UserMapper;
import com.cmbc.devops.dao.UserRoleMapper;
import com.cmbc.devops.entity.Role;
import com.cmbc.devops.entity.User;
import com.cmbc.devops.entity.UserRole;
import com.cmbc.devops.entity.UserWithRole;
import com.cmbc.devops.service.UserService;
import com.cmbc.devops.util.HashUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

/**
 * date：2015年8月14日 上午10:48:23 project name：cmbc-devops-service
 * 
 * @author langzi
 * @version 1.0
 * @since JDK 1.7.0_21 file name：UserServiceImpl.java description：
 */
@Component("userService")
public class UserServiceImpl implements UserService {
	@Resource
	private UserMapper userMapper;
	@Resource
	private UserRoleMapper userRoleMapper;
	@Autowired
	private RoleMapper roleMapper;

	@Override
	public User checkLogin(String userName, String password) throws Exception {
		User user = new User();
		user.setUserName(userName);
		user = getUser(user);
		if (user == null || user.getUserId() == null) {
			return null;
		}
		if (user.getUserStatus() == Status.USER.DELETE.ordinal()) {
			return user;
		}
		String pass = user.getUserPass();
		if (!pass.equals(HashUtil.md5Hash(password))) {
			user.setUserPass("");
		}
		return user;
	}

	@Override
	public User getUser(User user) throws Exception {
		return userMapper.selectUser(user);
	}

	@Override
	public JSONObject getJsonObjectOfUser(User user) throws Exception {
		return (JSONObject) JSONObject.toJSON(this.getUser(user));
	}

	@Override
	public GridBean list(Integer owner, int pageNum, int pageSize, User user) throws Exception {

		PageHelper.startPage(pageNum, pageSize);
		user.setUserStatus((byte) 1);
		if (null != user.getUserName()) {
			user.setUserName(user.getUserName());
		}
		if (null != owner) {
			user.setUserCreator(owner);
		}
		List<User> users = userMapper.selectAll(user);
		UserRole userRole = new UserRole();
		List<UserRole> userRoles = userRoleMapper.selectAll(userRole);
		Role role = new Role();
		List<Role> roles = roleMapper.selectAll(role);

		/* 定义返回的pages类型 */
		Page<UserWithRole> retURs = new Page<UserWithRole>();
		for (User sinUser : users) {
			UserWithRole userWithRole = new UserWithRole(sinUser);
			for (UserRole sinUserRole : userRoles) {
				if (sinUser.getUserId() == sinUserRole.getUserId()) {
					int urRoleId = sinUserRole.getRoleId();
					for (Role sinRole : roles) {
						if (urRoleId == sinRole.getRoleId()) {
							userWithRole.setRoleName(sinRole.getRoleName());
							break;
						}
					}
					break;
				}
			}
			retURs.add(userWithRole);
		}

		int total = ((Page<?>) users).getPages();
		int records = (int) ((Page<?>) users).getTotal();
		GridBean gridbean = new GridBean(pageNum, total, records, retURs);
		return gridbean;
	}

	@Override
	public int create(User user) throws Exception {
		user.setTenantId(0);
		return userMapper.insertUser(user);
	}

	@Override
	public int update(User user) throws Exception {
		return userMapper.updateUser(user);
	}

	@Override
	public int delete(Integer userId) throws Exception {
		return userMapper.deleteUser(userId);
	}

	@Override
	public int active(Integer userId) throws Exception {
		return userMapper.activeUser(userId);
	}

	@Override
	public int deletes(List<Integer> ids) {
		return 1;
	}

	@Override
	public int updateUserRole(UserRole userRole) throws Exception {
		return userRoleMapper.insert(userRole);
	}

	@Override
	public User getUserByName(String userName) throws Exception {
		return userMapper.selectUserByName(userName);
	}

	@Override
	public GridBean advancedSearchUser(Integer userId, int pagenum, int pagesize, User user, JSONObject json_object)
			throws Exception {
		PageHelper.startPage(pagenum, pagesize);
		/* 组装应用查询数据的条件 */
		User userSraech = new User();
		userSraech.setUserStatus(((byte) Status.USER.NORMAL.ordinal()));
		/* 获取用户填写的各项查询条件 */
		String[] params = json_object.getString("params").split(",");
		String[] values = json_object.getString("values").split(",");

		/* 遍历填充各项查询条件 */
		for (int i = 0; i < params.length; i++) {
			switch (params[i].trim()) {
			case "1":
				userSraech.setUserName(values[i].trim());
				break;
			case "2":
				userSraech.setUserMail(values[i].trim());
				break;
			case "3":
				userSraech.setUserPhone(values[i].trim());
				break;
			case "4":
				userSraech.setUserCompany(values[i].trim());
				break;
			default:
				break;
			}
		}
		/** @date:2016年3月29日 添加租户维度 */
		userSraech.setTenantId(user.getTenantId());
		List<User> users = userMapper.selectAll(userSraech);

		int totalpage = ((Page<?>) users).getPages();
		Long totalNum = ((Page<?>) users).getTotal();

		GridBean gridBean = new GridBean(pagenum, totalpage, totalNum.intValue(), users);
		return gridBean;
	}

	@Override
	public int deleteByUserId(Integer userId) throws Exception {

		userRoleMapper.deleteByUserId(userId);
		return 1;
	}

	/** 获取全部没有挂载租户资源的用户列表 */
	@Override
	public JSONArray getOrphanUsers(int userId) throws Exception {
		List<User> orphanUsers = userMapper.getOrphanUsers();
		return (JSONArray) JSONArray.toJSON(orphanUsers);
	}

	@Override
	public int updateUserInTenant(Integer tenantId, List<Integer> userIds) throws Exception {
		Map<String, Object> updateUserMap = new HashMap<String, Object>();
		updateUserMap.put("tenantId", tenantId);
		updateUserMap.put("userIdList", userIds);
		return userMapper.updateUserInTenant(updateUserMap);
	}

}

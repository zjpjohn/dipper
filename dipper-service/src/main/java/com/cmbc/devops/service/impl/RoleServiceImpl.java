package com.cmbc.devops.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cmbc.devops.bean.GridBean;
import com.cmbc.devops.constant.Status;
import com.cmbc.devops.dao.RoleActionMapper;
import com.cmbc.devops.dao.RoleMapper;
import com.cmbc.devops.entity.Role;
import com.cmbc.devops.entity.RoleAction;
import com.cmbc.devops.service.RoleService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

/**
 * date：2015年8月14日 上午10:48:23 project name：cmbc-devops-service
 * 
 * @author langzi
 * @version 1.0
 * @since JDK 1.7.0_21 file name：UserServiceImpl.java description：
 */
@Component
public class RoleServiceImpl implements RoleService {

	private static Logger logger = Logger.getLogger(RoleServiceImpl.class);
	@Resource
	private RoleMapper roleMapper;
	@Resource
	private RoleActionMapper roleActionMapper;

	@Override
	public Role getRole(Role role) throws Exception {
		return roleMapper.selectRole(role);
	}

	@Override
	public GridBean list(Integer owner, int pageNum, int pageSize, Role role) throws Exception {
		PageHelper.startPage(pageNum, pageSize);
		/* 暂时将所有状态的角色全部显示 */
		if (null != role.getRoleName()) {
			role.setRoleName(role.getRoleName());
		}
		if (null != owner) {
			//根据创建者查询角色
			role.setRoleCreator(null);
		}
		List<Role> roles = roleMapper.selectAll(role);
		int total = ((Page<?>) roles).getPages();
		int records = (int) ((Page<?>) roles).getTotal();
		GridBean gridbean = new GridBean(pageNum, total, records, roles);
		return gridbean;
	}

	@Override
	public int update(Role role) {
		try {
			return roleMapper.update(role);
		} catch (Exception e) {
			logger.error("update role fail", e);
			return 0;
		}
	}

	@Override
	public int deleteByRoleId(Integer roleId) {
		try {
			roleActionMapper.deleteByRoleId(roleId);
			logger.error("update authority success");
			return 1;
		} catch (Exception e) {
			logger.error("update authority fail", e);
			return 0;
		}
	}

	@Override
	public JSONArray getAllRoleList(int userId, Role role) throws Exception {
		List<Role> roles = roleMapper.selectAll(role);
		JSONArray ja = (JSONArray) JSONArray.toJSON(roles);
		return ja;
	}

	@Override
	public int updateAuth(RoleAction roleAction) {
		try {
			return roleActionMapper.insert(roleAction);
		} catch (Exception e) {
			logger.error("update role auth fail", e);
			return 0;
		}
	}

	@Override
	public List<RoleAction> getRoleAuthList(int userId, RoleAction roleAction) throws Exception {
		List<RoleAction> ras = roleActionMapper.selectAll(roleAction);
		return ras;
	}

	@Override
	public GridBean advancedSearchRole(Integer userId, int pagenum, int pagesize, Role role, JSONObject json_object)
			throws Exception {

		PageHelper.startPage(pagenum, pagesize);
		/* 组装应用查询数据的条件 */
		Role roleSraech = new Role();
		roleSraech.setRoleStatus(((byte) Status.USER.NORMAL.ordinal()));
		/* 获取用户填写的各项查询条件 */
		String[] params = json_object.getString("params").split(",");
		String[] values = json_object.getString("values").split(",");

		/* 遍历填充各项查询条件 */
		for (int array_count = 0, array_length = params.length; array_count < array_length; array_count++) {
			switch (params[array_count].trim()) {
			case "1": 
				roleSraech.setRoleName(values[array_count].trim());
				break;
			case "2": 
				roleSraech.setRoleDesc(values[array_count].trim());
				break;
			case "3": 
				if ("正常".indexOf(values[array_count].trim()) != -1) {
					roleSraech.setRoleStatus((byte) Status.ROLE.NORMAL.ordinal());
				} else if ("注销".indexOf(values[array_count].trim()) != -1) {
					roleSraech.setRoleStatus((byte) Status.ROLE.DELETE.ordinal());
				} else {
					roleSraech.setRoleStatus((byte) Integer.MAX_VALUE);
				}
				break;
			default:
				break;
			}
		}
		List<Role> roles = roleMapper.selectAll(roleSraech);

		int totalpage = ((Page<?>) roles).getPages();
		Long totalNum = ((Page<?>) roles).getTotal();

		GridBean gridBean = new GridBean(pagenum, totalpage, totalNum.intValue(), roles);
		return gridBean;
	}

	@Override
	public List<Role> getRolesByUserId(int userId) throws Exception {
		List<Role> roles = roleMapper.selectRolesByUserId(userId);
		return roles;
	}

	@Override
	public List<Role> getAllRoleListByUserId(int userId) throws Exception {
		List<Role> roles = roleMapper.selectRolesAndUserByUserId(userId);
		return roles;
	}

	@Override
	public int create(Role role) {
		try {
			//标记2为普通角色   1为超级管理员
			role.setRoleRemarks((byte)2);
			return roleMapper.insert(role);
		} catch (Exception e) {
			logger.error("create role fail", e);
			return 0;
		}
	}

	@Override
	public Role getRoleByName(String roleName) throws Exception {
		return roleMapper.getRoleByName(roleName);
	}
}

/**
 * 
 */
package com.cmbc.devops.manager;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.cmbc.devops.bean.GridBean;
import com.cmbc.devops.bean.Result;
import com.cmbc.devops.entity.Role;
import com.cmbc.devops.entity.RoleAction;
import com.cmbc.devops.service.AuthorityService;
import com.cmbc.devops.service.RoleService;

/**
 * date：2015年8月23日 下午8:23:30 project name：cmbc-devops-web
 * 
 * @author dingmw
 * @version 1.0
 * @since JDK 1.7.0_21 file name：ParameterManager.java description：
 */
@Component
public class RoleManager {
	private static final Logger LOGGER = Logger.getLogger(RoleManager.class);
	@Resource
	private RoleService roleService;
	@Resource
	private AuthorityService authorityService;

	public Result update(Role role) {
		int result = roleService.update(role);
		if(result>0){
			LOGGER.info("Update role success");
			return new Result(true, "更新角色成功！");
		}else{
			LOGGER.error("Update role fail");
			return new Result(true, "更新角色失败！");
		}
	}

	
	public Result authToRole(JSONObject jsonObject) {
		RoleAction roleAction = new RoleAction();
		String roles = jsonObject.getString("roles");
		String auths = jsonObject.getString("auths");
		String[] roleIds = roles.split(",");
		String[] authIds = auths.split(",");
		int delResult = 0;
		int addResult = 0;
		for (int i = 0; i < roleIds.length; i++) {
			roleAction.setRoleId(Integer.parseInt(roleIds[i]));
			//先删除选中角色的所有权限
			delResult = roleService.deleteByRoleId(Integer.parseInt(roleIds[i]));
			//新添加选中角色的权限
			if(delResult>0){
			  for(int j = 0; j < authIds.length; j++){
				  roleAction.setActionId(Integer.parseInt(authIds[j]));
				  addResult = roleService.updateAuth(roleAction);
			  }
			}
		}
		if (addResult > 0) {
			LOGGER.info("Update roles auth success");
			return new Result(true, "角色授权成功！");
		} else {
			LOGGER.error("Update roles auth fail");
			return new Result(false, "角色授权失败！");
		}
	}
	
	/**
	 * @author luogan
	 * @param advancedSearchParam
	 * @return
	 * @version 1.0
	 * 2015年10月21日
	 */
	public GridBean advancedSearchRole(Integer userId, int pagenum, int pagesize, Role role,JSONObject json_object) {
		try {
			LOGGER.info("Advanced search role success");
			return roleService.advancedSearchRole(userId, pagenum, pagesize, role, json_object);
		} catch (Exception e) {
			LOGGER.info("Advanced search role fail");
			return null;
		}
	}
	
	/**
	 * 获取角色详情
	 * @param roleId	角色id
	 * @return	Role
	 */
	public Role detail(int roleId) {
		Role role = new Role();
		role.setRoleId(roleId);
		try {
			role = roleService.getRole(role);
		} catch (Exception e) {
			LOGGER.error("get role by roleid["+roleId+"] falied!", e);
			return null;
		}
		return role;
	}


	public Result create(Role role) {
		int result=0;
		try {
			result = roleService.create(role);
		} catch (Exception e) {
			LOGGER.error("insert role error!", e);
			return null;
		}
		if(result>0){
			LOGGER.info("create role success");
			return new Result(true, "创建角色成功！");
		}else{
			LOGGER.error("create role fail");
			return new Result(true, "创建角色失败！");
		}
	}


	public Boolean checkRoleName(String roleName) {
		try {
			return roleService.getRoleByName(roleName) == null ? true : false;
		} catch (Exception e) {
			LOGGER.error("get role by rolerName[" + roleName + "] failed!", e);
			return false;
		}
	}
}

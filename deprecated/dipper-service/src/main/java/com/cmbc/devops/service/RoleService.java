package com.cmbc.devops.service;

import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cmbc.devops.bean.GridBean;
import com.cmbc.devops.entity.Role;
import com.cmbc.devops.entity.RoleAction;

/**  
 * date：2015年8月14日 上午10:33:39  
 * project name：cmbc-devops-service  
 * @author langzi  
 * @version 1.0   
 * @since JDK 1.7.0_21  
 * file name：UserService.java  
 * description：  
 */
public interface RoleService {
	
	/**
	 * Get User Object by userName
	 * @author langzi
	 * @param userName
	 * @return
	 * @version 1.0
	 * 2015年8月14日
	 * @throws Exception 
	 */
	public Role getRole(Role role) throws Exception;
	
	/**用户分页
	 * @param owner 所有者
	 * @param name 用户名称
	 * @param pageNum 当前页
	 * @param pageSize 页面行数
	 * @return
	 * @throws Exception 
	 */
	public GridBean list(Integer owner,int pageNum, int pageSize,Role role) throws Exception;
	
	/**更新用户
	 * @param param
	 * @return
	 */
	public int update(Role role);
	
	/**
	 * 获取角色列表
	 * @param userId
	 * @param role
	 * @return
	 * @throws Exception
	 */
	public abstract JSONArray getAllRoleList(int userId, Role role) throws Exception;
	
	public abstract List<RoleAction> getRoleAuthList(int userId, RoleAction roleAction) throws Exception;
	
	public abstract int updateAuth(RoleAction roleAction);
	
	public abstract int deleteByRoleId(Integer roleId);
	
	/**
	 * @author luogan
	 * 角色列表高级查询
	 * @throws Exception 
	 */
	public abstract GridBean advancedSearchRole(Integer userId, int pagenum, int pagesize, Role role,JSONObject json_object) throws Exception;
	
	public abstract List<Role> getRolesByUserId(int userId) throws Exception;
	
	public abstract List<Role> getAllRoleListByUserId(int userId) throws Exception;

	public abstract int create(Role role) throws Exception;

	public abstract Role getRoleByName(String roleName)throws Exception;
}
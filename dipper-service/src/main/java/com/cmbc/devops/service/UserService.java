package com.cmbc.devops.service;

import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cmbc.devops.bean.GridBean;
import com.cmbc.devops.entity.User;
import com.cmbc.devops.entity.UserRole;

/**
 * date：2015年8月14日 上午10:33:39 project name：cmbc-devops-service
 * 
 * @author langzi
 * @version 1.0
 * @since JDK 1.7.0_21 file name：UserService.java description：
 */
public interface UserService {

	/**
	 * Check user login status
	 * 
	 * @author langzi
	 * @param userName
	 * @param password
	 * @return
	 * @version 1.0 2015年8月17日
	 * @throws Exception
	 */
	public User checkLogin(String userName, String password) throws Exception;

	/**
	 * Get User Object by userName
	 * 
	 * @author langzi
	 * @param userName
	 * @return
	 * @version 1.0 2015年8月14日
	 * @throws Exception
	 */
	public User getUser(User user) throws Exception;

	/**
	 * @author langzi
	 * @param user
	 * @return
	 * @version 1.0 2015年8月14日
	 * @throws Exception
	 */
	public JSONObject getJsonObjectOfUser(User user) throws Exception;

	/**
	 * 用户分页
	 * 
	 * @param owner
	 *            所有者
	 * @param name
	 *            用户名称
	 * @param pageNum
	 *            当前页
	 * @param pageSize
	 *            页面行数
	 * @return
	 * @throws Exception
	 */
	public GridBean list(Integer owner, int pageNum, int pageSize, User user) throws Exception;

	/**
	 * 添加用户
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public int create(User user) throws Exception;

	/**
	 * 更新用户
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public int update(User user) throws Exception;

	/**
	 * 更新用户权限关系
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public int updateUserRole(UserRole userRole) throws Exception;

	/**
	 * 删除用户
	 * 
	 * @param paramId
	 * @return
	 * @throws Exception
	 */
	public int delete(Integer userId) throws Exception;

	/**
	 * 删除用户
	 * 
	 * @param paramId
	 * @return
	 * @throws Exception
	 */
	public int active(Integer userId) throws Exception;

	/**
	 * 批量删除用户
	 * 
	 * @param ids
	 * @return
	 */
	public int deletes(List<Integer> ids);

	/**
	 * 验证用户的唯一性
	 * 
	 * @param userName
	 * @return
	 * @throws Exception
	 */
	public User getUserByName(String userName) throws Exception;

	/**
	 * @author luogan 用户列表高级查询
	 * @throws Exception
	 */
	public GridBean advancedSearchUser(Integer userId, int pagenum, int pagesize, User user, JSONObject json_object)
			throws Exception;

	/** 获取全部没有挂载租户资源的用户列表 */
	public JSONArray getOrphanUsers(int userId) throws Exception;

	/**
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public int deleteByUserId(Integer userId) throws Exception;

	/* 批量将用户划入到租户资源中 */
	public int updateUserInTenant(Integer tenantId, List<Integer> userIds) throws Exception;

}

package com.cmbc.devops.dao;

import java.util.List;
import java.util.Map;

import com.cmbc.devops.entity.User;
import com.cmbc.devops.exception.SqlException;

public interface UserMapper {

	/**
	 * @author langzi
	 * @param user
	 * @return
	 * @version 1.0
	 * @throws SqlException
	 *             2015年8月14日 insert user entity to DB
	 */
	public int insertUser(User user) throws SqlException;

	/**
	 * @author langzi
	 * @param userId
	 * @return
	 * @version 1.0
	 * @throws SqlException
	 *             2015年8月14日 delete user entity from DB by user Id(primary key)
	 */
	public int deleteUser(Integer userId) throws SqlException;

	/**
	 * @author langzi
	 * @param userId
	 * @return
	 * @version 1.0
	 * @throws SqlException
	 *             2015年8月14日 active user from DB by user Id(primary key)
	 */
	public int activeUser(Integer userId) throws SqlException;

	/**
	 * @author langzi
	 * @param user
	 * @return
	 * @version 1.0
	 * @throws SqlException
	 *             2015年8月14日 update user entity to DB
	 */
	public int updateUser(User user) throws SqlException;

	/**
	 * @author langzi
	 * @return
	 * @version 1.0
	 * @throws SqlException
	 *             2015年8月14日 select all user entities from DB which meet user
	 *             conditions
	 */
	public List<User> selectAll(User user) throws SqlException;

	/** 透明获取全部用户列表，后台匹配数据使用 */
	public List<User> fetchAllUser() throws SqlException;

	/**
	 * @author langzi
	 * @param user
	 * @return
	 * @version 1.0
	 * @throws SqlException
	 *             2015年8月14日 select user entity from DB which meet user
	 *             condition
	 */
	public User selectUser(User user) throws SqlException;

	/**
	 * @author langzi
	 * @param hostName
	 * @return
	 * @throws SqlException
	 * @version 1.0 2015年10月21日 select user entity from DB by user name
	 */
	public User selectUserByName(String userName) throws SqlException;

	/** 获取全部没有挂载租户资源的用户列表 */
	public List<User> getOrphanUsers() throws SqlException;

	/** 批量更新，将符合条件的集群划入租户资源中 */
	public int updateUserInTenant(Map<String, Object> insMap) throws Exception;
}
package com.cmbc.devops.dao;

import java.util.List;

import com.cmbc.devops.entity.UserRole;

public interface UserRoleMapper {
	
	/**
	 * @param userRole
	 * @return
	 * @throws Exception
	 */
	public List<UserRole> selectAll(UserRole userRole) throws Exception;
	
	/**
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public int deleteByPrimaryKey(Integer id) throws Exception;

	/**
	 * @param record
	 * @return
	 * @throws Exception
	 */
	public int insert(UserRole record) throws Exception;

	/**
	 * @param record
	 * @return
	 * @throws Exception
	 */
	public int insertSelective(UserRole record) throws Exception;

	/**
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public UserRole selectByPrimaryKey(Integer id) throws Exception;

	/**
	 * @param record
	 * @return
	 * @throws Exception
	 */
	public int updateByPrimaryKeySelective(UserRole record) throws Exception;

	/**
	 * @param record
	 * @return
	 * @throws Exception
	 */
	public int updateByPrimaryKey(UserRole record) throws Exception;
	
	/**
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public int deleteByUserId(Integer userId) throws Exception;
}
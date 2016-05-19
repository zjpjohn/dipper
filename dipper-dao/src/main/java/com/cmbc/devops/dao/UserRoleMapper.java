package com.cmbc.devops.dao;

import java.util.List;

import com.cmbc.devops.entity.UserRole;
import com.cmbc.devops.exception.SqlException;

public interface UserRoleMapper {
	
	/**
	 * @param userRole
	 * @return
	 * @throws SqlException
	 */
	public List<UserRole> selectAll(UserRole userRole) throws SqlException;
	
	/**
	 * @param id
	 * @return
	 * @throws SqlException
	 */
	public int deleteByPrimaryKey(Integer id) throws SqlException;

	/**
	 * @param record
	 * @return
	 * @throws SqlException
	 */
	public int insert(UserRole record) throws SqlException;

	/**
	 * @param record
	 * @return
	 * @throws SqlException
	 */
	public int insertSelective(UserRole record) throws SqlException;

	/**
	 * @param id
	 * @return
	 * @throws SqlException
	 */
	public UserRole selectByPrimaryKey(Integer id) throws SqlException;

	/**
	 * @param record
	 * @return
	 * @throws SqlException
	 */
	public int updateByPrimaryKeySelective(UserRole record) throws SqlException;

	/**
	 * @param record
	 * @return
	 * @throws SqlException
	 */
	public int updateByPrimaryKey(UserRole record) throws SqlException;
	
	/**
	 * @param userId
	 * @return
	 * @throws SqlException
	 */
	public int deleteByUserId(Integer userId) throws SqlException;
}
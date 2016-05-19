package com.cmbc.devops.dao;

import java.util.List;

import com.cmbc.devops.entity.RoleAction;
import com.cmbc.devops.exception.SqlException;

public interface RoleActionMapper {
	
	/**
	 * @author langzi
	 * @param record
	 * @return
	 * @throws SqlException
	 * @version 1.0
	 * 2016年3月9日
	 * insert role and action relations entity to DB
	 */
	public int insert(RoleAction record) throws SqlException;
	
	/**
	 * @author langzi
	 * @param id
	 * @return
	 * @throws SqlException
	 * @version 1.0
	 * 2016年3月9日
	 * delete role and action relations entity from DB by id(primary key)
	 */
	public int deleteById(Integer id) throws SqlException;

	/**
	 * @author langzi
	 * @param roleId
	 * @return
	 * @throws SqlException
	 * @version 1.0
	 * 2016年3月9日
	 * delete role and action relations entity from DB by roleId
	 */
	public int deleteByRoleId(Integer roleId) throws SqlException;
	
	/**
	 * @author langzi
	 * @param record
	 * @return
	 * @throws SqlException
	 * @version 1.0
	 * 2016年3月9日
	 * update role and action entity to DB
	 */
	public int update(RoleAction record) throws SqlException;
	
	/**
	 * @author langzi
	 * @param id
	 * @return
	 * @throws SqlException
	 * @version 1.0
	 * 2016年3月9日
	 * select role and action entity from DB by Id(primary key)
	 */
	public RoleAction selectById(Integer id) throws SqlException;
	
	/**
	 * @author langzi
	 * @param roleAction
	 * @return
	 * @throws SqlException
	 * @version 1.0
	 * 2016年3月9日
	 * select all role and action entities from DB which meet role and action conditions
	 */
	public List<RoleAction> selectAll(RoleAction roleAction) throws SqlException;


}
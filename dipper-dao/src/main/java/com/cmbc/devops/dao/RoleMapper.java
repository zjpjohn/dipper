package com.cmbc.devops.dao;

import java.util.List;

import com.cmbc.devops.entity.Role;
import com.cmbc.devops.exception.SqlException;

public interface RoleMapper {
	
	/**
	 * @author langzi
	 * @param record
	 * @return
	 * @throws SqlException
	 * @version 1.0
	 * 2016年3月9日
	 * insert role entity to DB
	 */
	public int insert(Role record) throws SqlException;
	
	/**
	 * @author langzi
	 * @param roleId
	 * @return
	 * @throws SqlException
	 * @version 1.0
	 * 2016年3月9日
	 * delete role entity from DB by role id(primary key)
	 */
	public int deleteById(Integer roleId) throws SqlException;

	/**
	 * @author langzi
	 * @param record
	 * @return
	 * @throws SqlException
	 * @version 1.0
	 * 2016年3月9日
	 * update role entity to DB
	 */
	public int update(Role record) throws SqlException;

	/**
	 * @author langzi
	 * @param roleId
	 * @return
	 * @throws SqlException
	 * @version 1.0
	 * 2016年3月9日
	 * select role entity from DB by roleId
	 */
	public Role selectRoleById(Integer roleId) throws SqlException;
	
    /**
     * @author langzi
     * @param role
     * @return
     * @throws SqlException
     * @version 1.0
     * 2016年3月9日
     * select role entity from DB which meet role conditions
     */
    public Role selectRole(Role role) throws SqlException;
    
    /**
     * @author langzi
     * @param role
     * @return
     * @throws SqlException
     * @version 1.0
     * 2016年3月9日
     * select role entity from DB which meet role conditions
     */
    public List<Role> selectAll(Role role) throws SqlException;
    
    /**
     * @author langzi
     * @param userId
     * @return
     * @throws SqlException
     * @version 1.0
     * 2016年3月9日
     * select role entities from DB by user id
     */
    public List<Role> selectRolesByUserId(Integer userId) throws SqlException;
    
    /**
     * @author langzi
     * @param userId
     * @return
     * @throws SqlException
     * @version 1.0
     * 2016年3月9日
     * select role and user infos from DB by user id
     */
    public List<Role> selectRolesAndUserByUserId(Integer userId) throws SqlException;

	public Role getRoleByName(String roleName) throws SqlException;
	
}
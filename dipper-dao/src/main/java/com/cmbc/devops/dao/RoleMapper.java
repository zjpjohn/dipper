package com.cmbc.devops.dao;

import java.util.List;

import com.cmbc.devops.entity.Role;

public interface RoleMapper {
	
	/**
	 * @author langzi
	 * @param record
	 * @return
	 * @throws Exception
	 * @version 1.0
	 * 2016年3月9日
	 * insert role entity to DB
	 */
	public int insert(Role record) throws Exception;
	
	/**
	 * @author langzi
	 * @param roleId
	 * @return
	 * @throws Exception
	 * @version 1.0
	 * 2016年3月9日
	 * delete role entity from DB by role id(primary key)
	 */
	public int deleteById(Integer roleId) throws Exception;

	/**
	 * @author langzi
	 * @param record
	 * @return
	 * @throws Exception
	 * @version 1.0
	 * 2016年3月9日
	 * update role entity to DB
	 */
	public int update(Role record) throws Exception;

	/**
	 * @author langzi
	 * @param roleId
	 * @return
	 * @throws Exception
	 * @version 1.0
	 * 2016年3月9日
	 * select role entity from DB by roleId
	 */
	public Role selectRoleById(Integer roleId) throws Exception;
	
    /**
     * @author langzi
     * @param role
     * @return
     * @throws Exception
     * @version 1.0
     * 2016年3月9日
     * select role entity from DB which meet role conditions
     */
    public Role selectRole(Role role) throws Exception;
    
    /**
     * @author langzi
     * @param role
     * @return
     * @throws Exception
     * @version 1.0
     * 2016年3月9日
     * select role entity from DB which meet role conditions
     */
    public List<Role> selectAll(Role role) throws Exception;
    
    /**
     * @author langzi
     * @param userId
     * @return
     * @throws Exception
     * @version 1.0
     * 2016年3月9日
     * select role entities from DB by user id
     */
    public List<Role> selectRolesByUserId(Integer userId) throws Exception;
    
    /**
     * @author langzi
     * @param userId
     * @return
     * @throws Exception
     * @version 1.0
     * 2016年3月9日
     * select role and user infos from DB by user id
     */
    public List<Role> selectRolesAndUserByUserId(Integer userId) throws Exception;

	public Role getRoleByName(String roleName) throws Exception;
	
}
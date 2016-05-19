package com.cmbc.devops.dao;

import java.util.List;
import java.util.Map;

import com.cmbc.devops.entity.Authority;
import com.cmbc.devops.exception.SqlException;

public interface AuthorityMapper {
	
    /**
     * @author langzi
     * @param record
     * @return
     * @throws SqlException
     * @version 1.0
     * 2016年3月9日
     * insert authority entity to DB
     */
    public int insert(Authority record) throws SqlException;

    /**
     * @author langzi
     * @param actionId
     * @return
     * @throws SqlException
     * @version 1.0
     * 2016年3月9日
     * delete authority entity from DB
     */
    public int deleteById(Integer actionId) throws SqlException;

    /**
     * @author langzi
     * @param record
     * @return
     * @throws SqlException
     * @version 1.0
     * 2016年3月9日
     * update authority entity to DB
     */
    public int update(Authority record) throws SqlException;
    
    /**
     * @author langzi
     * @param actionId
     * @return
     * @throws SqlException
     * @version 1.0
     * 2016年3月9日
     * select authority entity from DB by actionId(primary Id)
     */
    public Authority selectById(Integer actionId) throws SqlException;
    
    /**
     * @author langzi
     * @param authority
     * @return
     * @throws SqlException
     * @version 1.0
     * 2016年3月9日
     * select authority entitys from DB which meet authority condition
     */
    public List<Authority> selectAll(Authority authority) throws SqlException;
	
	/**
	 * @author langzi
	 * @param pid
	 * @return
	 * @throws SqlException
	 * @version 1.0
	 * 2016年3月9日
	 * select authorities entity from DB by action parent Id
	 */
	public List<Authority> selectAuthoritiesByActionParentId(Integer pid) throws SqlException;
	
	/**
	 * @author langzi
	 * @param userId
	 * @return
	 * @throws SqlException
	 * @version 1.0
	 * 2016年3月9日
	 * select authorities entity from DB by userId
	 */
	public List<Authority> selectAuthoritiesByUserId(Integer userId) throws SqlException;
	
	/**
	 * @author langzi
	 * @param map
	 * @return
	 * @throws SqlException
	 * @version 1.0
	 * 2016年3月9日
	 * select authorities entity from DB by roleId
	 */
	public List<Authority> selectAuthoritiesByRoleId(Map<String,Integer> map) throws SqlException;
    
}
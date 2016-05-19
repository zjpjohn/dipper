package com.cmbc.devops.dao;

import java.util.List;

import com.cmbc.devops.entity.ClusterApp;
import com.cmbc.devops.exception.SqlException;

public interface ClusterAppMapper {
	
	/**
	 * @author langzi
	 * @param record
	 * @return
	 * @throws SqlException
	 * @version 1.0
	 * 2015年10月10日
	 * insert clusterApp entity to DB
	 */
	public int insert(ClusterApp record) throws SqlException;
	
    /**
     * @author langzi
     * @param id
     * @return
     * @throws SqlException
     * @version 1.0
     * 2015年10月10日
     * delete clusterApp entity from DB
     */
    public int deleteByID(Integer id) throws SqlException;
    
    /**
     * @author langzi
     * @param appIds
     * @return
     * @throws SqlException
     * @version 1.0
     * 2015年10月10日
     * delete clusterApp entities from DB by appIds
     */
    public int deleteByAppId(Integer[] appIds) throws SqlException;
    
    /**
     * @author langzi
     * @param clusterIds
     * @return
     * @throws SqlException
     * @version 1.0
     * 2015年10月10日
     * delete clusterApp entites from DB by clusterIds
     */
    public int deleteByClusterId(Integer[] clusterIds) throws SqlException;
    
    /**
     * @author langzi
     * @param record
     * @return
     * @throws SqlException
     * @version 1.0
     * 2015年10月10日
     * update clusterApp entity to DB
     */
    public int updateByID(ClusterApp record) throws SqlException;
    
    /**
     * @author langzi
     * @param id
     * @return
     * @throws SqlException
     * @version 1.0
     * 2015年10月10日
     * select clusterApp entity from DB by id(primary key)
     */
    public ClusterApp selectById(Integer id) throws SqlException;
    
    /**
     * @author langzi
     * @param appIds
     * @return
     * @throws SqlException
     * @version 1.0
     * 2015年10月10日
     * select clusterApp entity from DB by appIds
     */
    public List<ClusterApp> selectByAppId(Integer[] appIds) throws SqlException;
    
    /**
     * @author langzi
     * @param clusterIds
     * @return
     * @throws SqlException
     * @version 1.0
     * 2015年10月10日
     * select clusterApp entity from DB by clusterIds
     */
    public List<ClusterApp> selectByClusterId(Integer[] clusterIds) throws SqlException;

}
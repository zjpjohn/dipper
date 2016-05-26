package com.cmbc.devops.dao;

import java.util.List;

import com.cmbc.devops.entity.ConPort;

public interface ConPortMapper {

	/**
	 * @author langzi
	 * @param record
	 * @return
	 * @throws Exception
	 * @version 1.0
	 * 2015年8月31日
	 * insert conport entity to DB
	 */
	public int insertConport(ConPort record) throws Exception;
    
	/**
	 * @author langzi
	 * @param id
	 * @return
	 * @throws Exception
	 * @version 1.0
	 * 2015年8月31日
	 * delete conport entity from DB by containerIds
	 */
	public int deleteConport(String[] containerIds) throws Exception;
    
	/**
	 * @author langzi
	 * @param record
	 * @return
	 * @throws Exception
	 * @version 1.0
	 * 2015年8月31日
	 * update conport entity to DB
	 */
	public int updateConport(ConPort record) throws Exception;
	
	/**
	 * @author langzi
	 * @param conPort
	 * @return
	 * @throws Exception
	 * @version 1.0
	 * 2015年8月31日
	 * select conport entities from DB by container Id
	 */
	public List<ConPort> selectConport(Integer containerId) throws Exception;
	
	/**
	 * @author langzi
	 * @param appId
	 * @return
	 * @throws Exception
	 * @version 1.0
	 * 2015年9月15日
	 * select conport entiteis from DB by application Id
	 */
	public List<ConPort> selectConportByAppId(Integer appId) throws Exception;

}
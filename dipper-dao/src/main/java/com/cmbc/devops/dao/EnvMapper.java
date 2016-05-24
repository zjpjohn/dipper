package com.cmbc.devops.dao;

import java.util.List;

import com.cmbc.devops.entity.Env;

public interface EnvMapper {
	
	/**
	 * @author langzi
	 * @param env
	 * @return
	 * @throws Exception
	 * @version 1.0
	 * 2016年3月9日
	 * insert env entity to DB
	 */
	public int insert(Env env) throws Exception;

	/**
	 * @author langzi
	 * @param envId
	 * @return
	 * @throws Exception
	 * @version 1.0
	 * 2016年3月9日
	 * delete env entity from DB by envId(primary key)
	 */
	public int delete(int envId) throws Exception;
	
	/**
	 * @author langzi
	 * @param env
	 * @return
	 * @throws Exception
	 * @version 1.0
	 * 2016年3月9日
	 * update env entity to DB
	 */
	public int update(Env env) throws Exception;
	
	/**
	 * @author langzi
	 * @param envId
	 * @return
	 * @throws Exception
	 * @version 1.0
	 * 2016年3月9日
	 * select env entity from DB by envId
	 */
	public Env select(int envId) throws Exception;

	/**
	 * @author langzi
	 * @return
	 * @throws Exception
	 * @version 1.0
	 * 2016年3月9日
	 * select env entities from DB 
	 */
	public List<Env> selectAll() throws Exception;

	/**
	 * @author langzi
	 * @param env
	 * @return
	 * @throws Exception
	 * @version 1.0
	 * 2016年3月9日
	 * select env entities from DB which meet env conditions
	 */
	public List<Env> selectAllEnvs(Env env) throws Exception;

	/**
	 * @author langzi
	 * @param appId
	 * @return
	 * @throws Exception
	 * @version 1.0
	 * 2016年3月9日
	 * select env entities from DB by application Id
	 */
	public List<Env> listByAppId(Integer appId) throws Exception;
}
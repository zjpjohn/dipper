package com.cmbc.devops.dao;

import java.util.List;

import com.cmbc.devops.entity.Env;
import com.cmbc.devops.exception.SqlException;

public interface EnvMapper {
	
	/**
	 * @author langzi
	 * @param env
	 * @return
	 * @throws SqlException
	 * @version 1.0
	 * 2016年3月9日
	 * insert env entity to DB
	 */
	public int insert(Env env) throws SqlException;

	/**
	 * @author langzi
	 * @param envId
	 * @return
	 * @throws SqlException
	 * @version 1.0
	 * 2016年3月9日
	 * delete env entity from DB by envId(primary key)
	 */
	public int delete(int envId) throws SqlException;
	
	/**
	 * @author langzi
	 * @param env
	 * @return
	 * @throws SqlException
	 * @version 1.0
	 * 2016年3月9日
	 * update env entity to DB
	 */
	public int update(Env env) throws SqlException;
	
	/**
	 * @author langzi
	 * @param envId
	 * @return
	 * @throws SqlException
	 * @version 1.0
	 * 2016年3月9日
	 * select env entity from DB by envId
	 */
	public Env select(int envId) throws SqlException;

	/**
	 * @author langzi
	 * @return
	 * @throws SqlException
	 * @version 1.0
	 * 2016年3月9日
	 * select env entities from DB 
	 */
	public List<Env> selectAll() throws SqlException;

	/**
	 * @author langzi
	 * @param env
	 * @return
	 * @throws SqlException
	 * @version 1.0
	 * 2016年3月9日
	 * select env entities from DB which meet env conditions
	 */
	public List<Env> selectAllEnvs(Env env) throws SqlException;

	/**
	 * @author langzi
	 * @param appId
	 * @return
	 * @throws SqlException
	 * @version 1.0
	 * 2016年3月9日
	 * select env entities from DB by application Id
	 */
	public List<Env> listByAppId(Integer appId) throws SqlException;
}
package com.cmbc.devops.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.cmbc.devops.entity.App;
import com.cmbc.devops.exception.SqlException;
import com.cmbc.devops.model.ApplicationDataModel;
import com.cmbc.devops.model.ApplicationModel;

public interface AppMapper {

	/**
	 * @author langzi
	 * @param app
	 * @return
	 * @throws SqlException
	 * @version 1.0 2016年3月9日 insert application entity to DB
	 */
	public int insert(App app) throws SqlException;

	/**
	 * @author langzi
	 * @param appId
	 * @return
	 * @throws SqlException
	 * @version 1.0 2016年3月9日 delete application entity from DB
	 */
	public int delete(Integer appId) throws SqlException;

	/**
	 * @author langzi
	 * @param app
	 * @return
	 * @throws SqlException
	 * @version 1.0 2016年3月9日 update application entity to DB
	 */
	public int update(App app) throws SqlException;

	/**
	 * @author langzi
	 * @param appId
	 * @return
	 * @throws SqlException
	 * @version 1.0 2016年3月9日 select application entity from DB by appId
	 */
	public App select(Integer appId) throws SqlException;

	/**
	 * @author langzi
	 * @return
	 * @throws SqlException
	 * @version 1.0 2016年3月9日 select last appId from DB
	 */
	public int selectLastAppId() throws SqlException;

	/**
	 * @author langzi
	 * @return
	 * @throws SqlException
	 * @version 1.0 2016年3月9日 select all application entitys from DB
	 */
	public List<App> selectAll() throws SqlException;

	/**
	 * @author langzi
	 * @param app
	 * @return
	 * @throws SqlException
	 * @version 1.0 2016年3月9日 select all application entitys from DB which meet
	 *          the condition
	 */
	public List<App> selectAll(App app) throws SqlException;

	/**
	 * @author langzi
	 * @param lbId
	 * @return
	 * @throws SqlException
	 * @version 1.0 2016年3月9日 select all application entitys from DB by
	 *          loadbalanceId
	 */
	public List<App> selectAppInLb(App app) throws SqlException;

	/**
	 * @author langzi
	 * @return
	 * @throws SqlException
	 * @version 1.0 2016年3月9日 select all application entitys from DB which is
	 *          not satisfied with loadbalance Id
	 */
	public List<App> selectAppNotInLb(@Param("tenantId") int tenantId) throws SqlException;
	
	/**
	 * @param envId
	 * @return
	 */
	public int checkAppInEnv(Map<String, Object> map) throws SqlException;
	
	/**
	 * @param clusterPort
	 * @param appId
	 * @return
	 */
	public int checkAppInCluster(Map<String, Object> map) throws SqlException;

	/**
	 * get App by containerid
	 * @param conId
	 * @return
	 */
	public App selectByConId(int conId) throws SqlException;
	
	public List<ApplicationDataModel> selectAllAppImage(ApplicationModel model) throws SqlException;

	public App selectAppByName(String appName) throws SqlException;

	@SuppressWarnings("rawtypes")
	public App getAppByLbAndUrl(Map map) throws SqlException;
}
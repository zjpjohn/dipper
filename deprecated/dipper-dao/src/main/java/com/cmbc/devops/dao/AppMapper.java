package com.cmbc.devops.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.cmbc.devops.entity.App;
import com.cmbc.devops.model.ApplicationDataModel;
import com.cmbc.devops.model.ApplicationModel;

public interface AppMapper {

	/**
	 * @author langzi
	 * @param app
	 * @return
	 * @throws Exception
	 * @version 1.0 2016年3月9日 insert application entity to DB
	 */
	public int insert(App app) throws Exception;

	/**
	 * @author langzi
	 * @param appId
	 * @return
	 * @throws Exception
	 * @version 1.0 2016年3月9日 delete application entity from DB
	 */
	public int delete(Integer appId) throws Exception;

	/**
	 * @author langzi
	 * @param app
	 * @return
	 * @throws Exception
	 * @version 1.0 2016年3月9日 update application entity to DB
	 */
	public int update(App app) throws Exception;

	/**
	 * @author langzi
	 * @param appId
	 * @return
	 * @throws Exception
	 * @version 1.0 2016年3月9日 select application entity from DB by appId
	 */
	public App select(Integer appId) throws Exception;

	/**
	 * @author langzi
	 * @return
	 * @throws Exception
	 * @version 1.0 2016年3月9日 select last appId from DB
	 */
	public int selectLastAppId() throws Exception;

	/**
	 * @author langzi
	 * @return
	 * @throws Exception
	 * @version 1.0 2016年3月9日 select all application entitys from DB
	 */
	public List<App> selectAll() throws Exception;

	/**
	 * @author langzi
	 * @param app
	 * @return
	 * @throws Exception
	 * @version 1.0 2016年3月9日 select all application entitys from DB which meet
	 *          the condition
	 */
	public List<App> selectAll(App app) throws Exception;

	/**
	 * @author langzi
	 * @param lbId
	 * @return
	 * @throws Exception
	 * @version 1.0 2016年3月9日 select all application entitys from DB by
	 *          loadbalanceId
	 */
	public List<App> selectAppInLb(App app) throws Exception;

	/**
	 * @author langzi
	 * @return
	 * @throws Exception
	 * @version 1.0 2016年3月9日 select all application entitys from DB which is
	 *          not satisfied with loadbalance Id
	 */
	public List<App> selectAppNotInLb(@Param("tenantId") int tenantId) throws Exception;
	
	/**
	 * @param envId
	 * @return
	 */
	public int checkAppInEnv(Map<String, Object> map) throws Exception;
	
	/**
	 * @param clusterPort
	 * @param appId
	 * @return
	 */
	public int checkAppInCluster(Map<String, Object> map) throws Exception;

	/**
	 * get App by containerid
	 * @param conId
	 * @return
	 */
	public App selectByConId(int conId) throws Exception;
	
	public List<ApplicationDataModel> selectAllAppImage(ApplicationModel model) throws Exception;

	public App selectAppByName(String appName) throws Exception;

	@SuppressWarnings("rawtypes")
	public App getAppByLbAndUrl(Map map) throws Exception;
}
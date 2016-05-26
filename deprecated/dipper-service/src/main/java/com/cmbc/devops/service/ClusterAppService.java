package com.cmbc.devops.service;

import java.util.List;

import com.cmbc.devops.entity.ClusterApp;

/**  
 * date：2015年10月10日 上午11:48:11  
 * project name：cmbc-devops-service  
 * @author langzi  
 * @version 1.0   
 * @since JDK 1.7.0_21  
 * file name：ClusterAppService.java  
 * description：  
 */
public interface ClusterAppService {
	/**
	 * @author langzi
	 * @param record
	 * @return
	 * @throws Exception
	 * @version 1.0
	 * 2015年11月25日
	 */
	public int addClusterApp(ClusterApp record) throws Exception;
	
	/**
	 * @author langzi
	 * @param clusterIds
	 * @return
	 * @throws Exception
	 * @version 1.0
	 * 2015年11月25日
	 */
	public int removeClusterAppByClusterId(Integer[] clusterIds) throws Exception;
	
	/**
	 * @author langzi
	 * @param appIds
	 * @return
	 * @throws Exception
	 * @version 1.0
	 * 2015年11月25日
	 */
	public int removeClusterAppByAppId(Integer[] appIds) throws Exception;
	
	/**
	 * @author langzi
	 * @param clusterIds
	 * @return
	 * @throws Exception
	 * @version 1.0
	 * 2015年11月25日
	 */
	public List<ClusterApp> listClusterAppsByClusterId(Integer[] clusterIds) throws Exception;
	
	/**
	 * @author langzi
	 * @param appIds
	 * @return
	 * @throws Exception
	 * @version 1.0
	 * 2015年11月25日
	 */
	public List<ClusterApp> listClusterAppsByAppId(Integer[] appIds) throws Exception;
}

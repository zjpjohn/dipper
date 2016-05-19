package com.cmbc.devops.service;

import java.util.List;

import com.cmbc.devops.entity.EnvApp;

/**  
 * date：2016年1月12日 下午1:46:00  
 * project name：cmbc-devops-service  
 * @author langzi  
 * @version 1.0   
 * @since JDK 1.7.0_21  
 * file name：EnvAppService.java  
 * description：  
 */
public interface EnvAppService {
	
	/**
	 * @author langzi
	 * @param appId
	 * @return
	 * @version 1.0
	 * @throws Exception
	 * 2016年1月12日
	 */
	public List<EnvApp> listAllByAppId(int appId) throws Exception;
	
	/**
	 * @author langzi
	 * @param envId
	 * @return
	 * @throws Exception
	 * @version 1.0
	 * 2016年1月12日
	 */
	public List<EnvApp> listAllByEnvId(int envId) throws Exception;
	
	/**
	 * @author langzi
	 * @param envApp
	 * @return
	 * @version 1.0
	 * @throws Exception
	 * 2016年1月12日
	 */
	public int add(EnvApp envApp) throws Exception;
	
	/**
	 * @author langzi
	 * @param appId
	 * @return
	 * @version 1.0
	 * @throws Exception
	 * 2016年1月12日
	 */
	public int removeByAppId(int appId) throws Exception;
	
}

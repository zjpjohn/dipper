package com.cmbc.devops.service;

import java.util.List;

import com.cmbc.devops.entity.ConPort;

/**  
 * date：2015年8月31日 下午2:12:37  
 * project name：cmbc-devops-service  
 * @author langzi  
 * @version 1.0   
 * @since JDK 1.7.0_21  
 * file name：ConportService.java  
 * description：  
 */
public interface ConportService {
	
	/**
	 * @author langzi
	 * @param containerId
	 * @return
	 * @version 1.0
	 * @throws Exception
	 * 2015年8月31日
	 */
	public List<ConPort> listConPorts(Integer containerId) throws Exception;
	
	/**
	 * @author langzi
	 * @param appId
	 * @return
	 * @throws Exception
	 * @version 1.0
	 * 2015年9月15日
	 */
	public List<ConPort> listConPortsByAppId(Integer appId) throws Exception;
	
	/**
	 * @author langzi
	 * @param port
	 * @return
	 * @version 1.0
	 * @throws Exception
	 * 2015年8月31日
	 */
	public int addConports(ConPort port) throws Exception;
	
	/**
	 * @author langzi
	 * @param port
	 * @return
	 * @throws Exception
	 * @version 1.0
	 * 2015年9月16日
	 */
	public int updateConports(ConPort port) throws Exception;
	
	/**
	 * @author langzi
	 * @param containerId
	 * @return
	 * @version 1.0
	 * @throws Exception
	 * 2015年8月31日
	 */
	public int removeConports(String[] containerIds) throws Exception;
}

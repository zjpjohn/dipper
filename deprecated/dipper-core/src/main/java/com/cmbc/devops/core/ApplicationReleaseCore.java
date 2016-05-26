package com.cmbc.devops.core;

import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.cmbc.devops.model.ApplicationReleaseModel;
import com.github.dockerjava.api.model.Container;

/**  
 * date：2015年8月25日 上午11:29:47  
 * project name：cmbc-devops-core  
 * @author langzi  
 * @version 1.0   
 * @since JDK 1.7.0_21  
 * file name：ContainerCore.java  
 * description：  
 */
public interface ApplicationReleaseCore {
	
	
	/**
	 * @author langzi
	 * @param hostIp
	 * @param hostName
	 * @param hostPassword
	 * @param clusterPort
	 * @version 1.0
	 * 2015年8月25日
	 */
	public List<Container> listAllContainer(String ip,String port);
	/**
	 * @author langzi
	 * @param model
	 * @return
	 * @version 1.0
	 * 2015年8月25日
	 */
	public JSONArray releaseApp(String ip, String username, String password, String port, int clusterType, ApplicationReleaseModel model);
	
}

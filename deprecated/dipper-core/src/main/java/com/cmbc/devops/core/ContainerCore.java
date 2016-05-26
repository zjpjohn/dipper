package com.cmbc.devops.core;

import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.cmbc.devops.model.ContainerModel;
import com.cmbc.devops.model.SimpleContainer;
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
public interface ContainerCore {
	
	
	/**
	 * @author langzi
	 * @param hostIp
	 * @param hostName
	 * @param hostPassword
	 * @param clusterPort
	 * @version 1.0
	 * 2015年8月25日
	 */
	public List<Container> listAllContainer(String hostIp,String clusterPort);
	/**
	 * @author langzi
	 * @param model
	 * @return
	 * @version 1.0
	 * 2015年8月25日
	 */
	public JSONArray createContainer(String hostIp, String hostName, String hostPassword, String clusterPort, ContainerModel model);
	
	/**
	 * @author langzi
	 * @param containerUuid
	 * @return
	 * @version 1.0
	 * 2015年8月25日
	 */
	public List<SimpleContainer> startContainer(List<SimpleContainer> simpleContainers);
	
	/**
	 * @author langzi
	 * @param containerUuid
	 * @return
	 * @version 1.0
	 * 2015年8月25日
	 */
	public List<SimpleContainer> stopContainer(List<SimpleContainer> simpleContainers);
	
	/**
	 * @author langzi
	 * @param containerUuid
	 * @return
	 * @version 1.0
	 * 2015年8月25日
	 */
	public List<SimpleContainer> removeContainer(List<SimpleContainer> simpleContainers);
	
}

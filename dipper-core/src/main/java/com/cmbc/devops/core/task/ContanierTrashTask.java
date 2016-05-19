package com.cmbc.devops.core.task;

import java.util.concurrent.Callable;

import org.apache.log4j.Logger;

import com.github.dockerjava.api.DockerClient;


/**  
 * date：2015年8月26日 下午5:02:34  
 * project name：cmbc-devops-core  
 * @author langzi  
 * @version 1.0   
 * @since JDK 1.7.0_21  
 * file name：ContanierTrashTask.java  
 * description：  
 */
public class ContanierTrashTask implements Callable<Boolean> {
	
	private DockerClient client;
	private String containerId;
	
	private static final Logger LOGGER = Logger.getLogger(ContanierTrashTask.class);
	/**
	 * Constructor
	 * @author liangzi
	 * @param client
	 * @param containerId
	 * @version 1.0
	 * 2015年8月26日
	 */
	public ContanierTrashTask(DockerClient client, String containerId) {
		super();
		this.client = client;
		this.containerId = containerId;
	}

	@Override
	public Boolean call() throws Exception {
		try {
			client.removeContainerCmd(containerId).exec();
			return true;
		} catch (Exception e) {
			LOGGER.error("remove container error", e);
			return false;
		}
	}
	
}

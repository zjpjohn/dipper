package com.cmbc.devops.core.task;

import java.util.concurrent.Callable;

import org.apache.log4j.Logger;

import com.github.dockerjava.api.DockerClient;


/**  
 * date：2015年8月26日 下午5:02:01  
 * project name：cmbc-devops-core  
 * @author langzi  
 * @version 1.0   
 * @since JDK 1.7.0_21  
 * file name：ContainerStopTask.java  
 * description：  
 */
public class ContainerStopTask implements Callable<Boolean> {
	
	private DockerClient client;
	private String containerId;
	
	private static final Logger LOGGER = Logger.getLogger(ContainerStopTask.class);
	
	public ContainerStopTask(DockerClient client, String containerId) {
		this.client = client;
		this.containerId = containerId;
	}

	@Override
	public Boolean call() throws Exception {
		try {
			client.stopContainerCmd(containerId).exec();
			return true;
		} catch (Exception e) {
			LOGGER.error("stop container error", e);
			return false;
		}
	}

}

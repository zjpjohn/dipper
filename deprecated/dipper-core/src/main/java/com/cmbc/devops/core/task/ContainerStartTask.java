package com.cmbc.devops.core.task;

import java.util.List;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;


/**  
 * date：2015年8月26日 下午5:01:26  
 * project name：cmbc-devops-core  
 * @author langzi  
 * @version 1.0   
 * @since JDK 1.7.0_21  
 * file name：ContainerStartTask.java  
 * description：  
 */
public class ContainerStartTask implements Callable<JSONObject> {
	
	private DockerClient client;
	private String containerId;
	private static final Logger LOGGER = Logger.getLogger(ContainerStartTask.class);
	
	public ContainerStartTask(DockerClient client, String containerId) {
		super();
		this.client = client;
		this.containerId = containerId;
	}

	@Override
	public JSONObject call() {
		try {
			client.startContainerCmd(containerId).exec();
			return containerInfo(client, containerId);
		} catch (Exception e) {
			LOGGER.error("Start container error", e);
			return null;
		}
	}
	
	/**
	 * @author langzi
	 * @param containerId
	 * @return
	 * @version 1.0
	 * 2015年9月10日
	 */
	private JSONObject containerInfo(DockerClient client, String containerId){
		JSONObject jo = new JSONObject();
		if (!"".equals(containerId)) {
			boolean start = false;
			while (!start) {
				List<Container> containers = client.listContainersCmd().withShowAll(true).exec();
				int flag = 0;
				for (Container container : containers) {
					if (container.getId().equals(containerId)) {
						jo = (JSONObject) JSONObject.toJSON(container);
						start = true;
						break;
					}
					flag++;
				}
				if (flag >= containers.size()) {
					break;
				}
			}
		}
		return jo;
	}

}

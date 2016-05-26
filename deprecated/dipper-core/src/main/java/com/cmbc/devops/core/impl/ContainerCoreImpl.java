package com.cmbc.devops.core.impl;

import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cmbc.devops.core.ContainerCore;
import com.cmbc.devops.core.task.ContainerStartTask;
import com.cmbc.devops.core.task.ContainerStopTask;
import com.cmbc.devops.core.task.ContanierTrashTask;
import com.cmbc.devops.model.ContainerModel;
import com.cmbc.devops.model.SimpleContainer;
import com.cmbc.devops.util.SSH;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.core.DockerClientBuilder;

/**  
 * date：2015年8月25日 下午2:08:32  
 * project name：cmbc-devops-core  
 * @author langzi  
 * @version 1.0   
 * @since JDK 1.7.0_21  
 * file name：ContainerCoreImpl.java  
 * description：  
 */
@Component
public class ContainerCoreImpl implements ContainerCore {
	
	private static final Logger LOGGER = Logger.getLogger(ContainerCore.class);

	/* (non-Javadoc)
	 * @see com.cmbc.devops.core.ContainerCore#listAllContainer(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public List<Container> listAllContainer(String hostIp,String clusterPort) {
		DockerClient client = getDockerClient(hostIp, clusterPort);
		return client.listContainersCmd().withShowAll(true).exec();
	}
	
	/* (non-Javadoc)
	 * @see com.cmbc.devops.core.ContainerCore#createContainer(java.lang.String, java.lang.String, java.lang.String, java.lang.String, com.cmbc.devops.model.ContainerModel)
	 */
	@Override
	public JSONArray createContainer(String hostIp, String hostName, String hostPassword, String clusterPort, ContainerModel model) {
		//1.获取客户端
		String containerId = "";
		String imageName = model.getImageName();
		JSONArray containerArray = new JSONArray();
		DockerClient client = getDockerClient(hostIp, clusterPort);
		//2.检查镜像是否存在，不存在时，拉取镜像
		if (!imageIsExist(client, imageName)) {
			client.pullImageCmd(imageName).exec(null);
		}
		//3.获取ssh连接
		SSH ssh = new SSH(hostIp,hostName,hostPassword);
		for(int i = 0; i<model.getConNumber(); i++){
			try {
				if (ssh.connect()) {
					//4.构建启动命令
					String command = getStartCommand(hostIp, clusterPort, model, i);	
					try {
						LOGGER.info(command);
						containerId = ssh.executeWithResult(command);
					} catch (Exception e) {
						LOGGER.error("Create container though docker error!",e);
					} finally{
						ssh.close();
					}
					//5.判断容器是否创建成功
					JSONObject jo = containerInfo(client, containerId.substring(0, containerId.length()-1));
					if (!jo.isEmpty()) {
						jo.put("runCommand", command);
						containerArray.add(jo);
					}
				}
			} catch (SocketException e) {
				LOGGER.error("ssh connect error:", e);
			} catch (IOException e) {
				LOGGER.error("ssh connect error:", e);
			}
		}
		return containerArray;
	}

	/* (non-Javadoc)
	 * @see com.cmbc.devops.core.ContainerCore#startContainer(java.lang.String[])
	 */
	@Override
	public List<SimpleContainer> startContainer(List<SimpleContainer> simpleContainers) {
		//1.并发提交任务
		ExecutorService executor = Executors.newCachedThreadPool();  
        CompletionService<JSONObject> comp = new ExecutorCompletionService<>(executor);  
        for(SimpleContainer simCon:simpleContainers){
        	DockerClient client = getDockerClient(simCon.getClusterIp(), simCon.getClusterPort());
        	comp.submit(new ContainerStartTask(client, simCon.getContainerUuid()));
        }
        executor.shutdown();
        //2.等待执行结果
        int index = 0;
        List<SimpleContainer> scs = new ArrayList<SimpleContainer>();
        try {
			while(index < simpleContainers.size()){
				Future<JSONObject> future = comp.poll();
				if(future != null && future.get() != null) {  
					SimpleContainer sc = simpleContainers.get(index);
					sc.setConJo(future.get());
					scs.add(sc);
			    	index++;
			    }  
				TimeUnit.MILLISECONDS.sleep(1000);
			}
		} catch (Exception e) {
			LOGGER.error("Start container error:operate server error", e);
			return null;
		}
		LOGGER.info("Start container success:operate server success");
		return scs;
	}

	/* (non-Javadoc)
	 * @see com.cmbc.devops.core.ContainerCore#stopContainer(java.lang.String[])
	 */
	@Override
	public List<SimpleContainer> stopContainer(List<SimpleContainer> simpleContainers) {
		//1.并发提交任务
		ExecutorService executor = Executors.newCachedThreadPool();  
        CompletionService<Boolean> comp = new ExecutorCompletionService<>(executor);  
        for(SimpleContainer simCon:simpleContainers){
        	DockerClient client = getDockerClient(simCon.getClusterIp(), simCon.getClusterPort());
        	comp.submit(new ContainerStopTask(client, simCon.getContainerUuid()));
        }
        executor.shutdown();
        int index = 0;
        //2.等待执行结果
        List<SimpleContainer> scs = new ArrayList<SimpleContainer>();
        while(index < simpleContainers.size()){
        	Future<Boolean> future = comp.poll();
        	try {
	        	if(future != null){  
	            	if (future.get()) {
	            		scs.add(simpleContainers.get(index));
					}	
	            	index++;
	            }  
	        	TimeUnit.MILLISECONDS.sleep(1000);
	        } catch (Exception e) {
				LOGGER.error("Stop container error:operate server error", e);
				index++;
				continue;
			}
        }
		LOGGER.info("Stop container success:operate server success");
		return scs;
		
	}
	
	/* (non-Javadoc)
	 * @see com.cmbc.devops.core.ContainerCore#deleteContainer(java.lang.String[])
	 */
	@Override
	public List<SimpleContainer> removeContainer(List<SimpleContainer> simpleContainers) {
		ExecutorService executor = Executors.newCachedThreadPool();  
        CompletionService<Boolean> comp = new ExecutorCompletionService<>(executor);  
        for(SimpleContainer simCon:simpleContainers){
        	DockerClient client = getDockerClient(simCon.getClusterIp(), simCon.getClusterPort());
        	comp.submit(new ContanierTrashTask(client, simCon.getContainerUuid()));
        }
        executor.shutdown();
        int index = 0;
        List<SimpleContainer> scs = new ArrayList<SimpleContainer>();
        while(index < simpleContainers.size()){
        	try {
	        	Future<Boolean> future = comp.poll();
	        	if(future != null){  
	            	if (future.get()) {
	            		scs.add(simpleContainers.get(index));
					}	
	            	index++;
	            }  
	        	TimeUnit.MILLISECONDS.sleep(1000);
        	} catch (Exception e) {
    			LOGGER.error("Remove container error:operate server error", e);
    			index++;
    			continue;
    		}
        }
		LOGGER.info("Remove container success:operate server success");
		return scs;
	}
	
	/**
	 * @author langzi
	 * @param client
	 * @param imageName
	 * @return
	 * @version 1.0
	 * 2015年9月10日
	 */
	private boolean imageIsExist(DockerClient client, String imageName){
		List<Image> imageList = client.listImagesCmd().exec();
		for(Image image:imageList){
			for(int i=0;i<image.getRepoTags().length;i++){
				if (imageName.equals(image.getRepoTags()[i])) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * @author langzi
	 * @param containerId
	 * @return
	 * @version 1.0
	 * 2015年9月10日
	 * 转化容器的信息
	 */
	private JSONObject containerInfo(DockerClient client, String containerId){
		JSONObject jo = new JSONObject();
		LOGGER.info(containerId);
		if (!"".equals(containerId)) {
			boolean start = false;
			while (!start) {
				List<Container> containers = client.listContainersCmd().withShowAll(true).exec();
				int flag = 0;
				for (Container container : containers) {
					if (container.getId().equals(containerId)) {
						jo = (JSONObject) JSONObject.toJSON(container);
						LOGGER.info(jo.toString());
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
		return jo.isEmpty() ? null:jo;
	}
	
	/**
	 * @author langzi
	 * @param clusterIp
	 * @param clusterPort
	 * @return
	 * @version 1.0
	 * 2015年9月10日
	 * 构建dockerclient客户端
	 */
	private DockerClient getDockerClient(String clusterIp, String clusterPort){
		String url =  "http://"+clusterIp+":"+clusterPort;
		return DockerClientBuilder.getInstance(url).build();
	}
	
	/**
	 * @author langzi
	 * @param clusterIp
	 * @param clusterPort
	 * @return
	 * @version 1.0
	 * 2015年11月25日
	 */
	private String getStartCommand(String clusterIp, String clusterPort, ContainerModel model, int conId){
		StringBuilder command = new StringBuilder();
		String masterUrl = clusterIp + ":" + clusterPort;
		command.append("docker -H tcp://");
		command.append(masterUrl);
		if ("0".equals(model.getCreateModel())) {
			command.append(" create ");
		}else{
			command.append(" run ");
			command.append(" -d ");
		}
		command.append(" -P");
		command.append(" -e DOCKER_HOST=\""+clusterIp +":"+clusterPort+"\"");
		String createParams = model.getCreateParams();
		if (!("").equals(createParams)) {
			if(createParams.contains("/tempDir")){
				createParams = createParams.replaceAll("/tempDir", "/"+model.getConName()+(model.getLastConId()+conId+1));
			}
			command.append(" " + createParams);
		}
		command.append(" "+model.getImageName()+";");
		return command.toString();
	}
	
}

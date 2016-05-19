package com.cmbc.devops.core.impl;

import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cmbc.devops.core.ApplicationReleaseCore;
import com.cmbc.devops.core.task.ContainerCreateTask;
import com.cmbc.devops.model.ApplicationReleaseModel;
import com.cmbc.devops.model.HostResourceModel;
import com.cmbc.devops.util.CommandExcutor;
import com.cmbc.devops.util.SSH;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.core.DockerClientBuilder;

/**
 * date：2015年8月25日 下午2:08:32 project name：cmbc-devops-core
 * 
 * @author langzi
 * @version 1.0
 * @since JDK 1.7.0_21 file name：ContainerCoreImpl.java description：
 */
@Component
public class ApplicationReleaseCoreImpl implements ApplicationReleaseCore {

	private static final Logger LOGGER = Logger.getLogger(ApplicationReleaseCore.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cmbc.devops.core.ContainerCore#listAllContainer(java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public List<Container> listAllContainer(String ip, String port) {
		DockerClient client = getDockerClient(ip, port);
		if (client == null) {
			return null;
		}
		return client.listContainersCmd().withShowAll(true).exec();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cmbc.devops.core.ContainerCore#createContainer(java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String,
	 * com.cmbc.devops.model.ContainerModel)
	 */
	@Override
	public JSONArray releaseApp(String ip, String username, String password, String port, int clusterType,
			ApplicationReleaseModel model) {
		// 1.获取客户端
		DockerClient client = getDockerClient(ip, port);
		if (client == null) {
			return null;
		}
		// 2.检查镜像是否存在，不存在时，拉取镜像
		String imageUrl = model.getImageUrl();
		if (!imageIsExist(client, imageUrl)) {
			client.pullImageCmd(imageUrl).exec(null);
		}
		// 3.获取ssh连接
		int releaseNum = model.getReleaseNum();
		JSONArray containerArray = new JSONArray();
		ExecutorService executor = Executors.newCachedThreadPool();
		CompletionService<JSONObject> comp = new ExecutorCompletionService<>(executor);
		LOGGER.warn("Current Thread Will Release " + releaseNum + " Containers");
		List<HostResourceModel> hrmList = model.getHrmList();
		for (int i = 0; i < releaseNum; i++) {
			SSH ssh = CommandExcutor.getSsh(ip, username, password);
			String createCommand = "";
			if ((hrmList != null) && (hrmList.size() > 0)) {
				HostResourceModel hrm = hrmList.get(i);
				createCommand = getCreateCommand(ip, port, model, i, hrm);
			} else {
				createCommand = getCreateCommand(ip, port, model, i, null);
			}
			LOGGER.info("Create ExSSH ip:" + ip + ", username:" + username + ", ssh is null ?:" + (ssh == null));
			comp.submit(new ContainerCreateTask(ssh, createCommand, ip, username, password));
		}
		executor.shutdown();
		// 2.等待执行结果
		int index = 0;
		while (index < releaseNum) {
			/** @2016年2月15日，暂时关闭超时机制，以后进行确定。 */
			Future<JSONObject> future = null;
			try {
				future = comp.take();
				JSONObject result = future.get();
				String conUuid = result.getString("containerId");
				JSONObject jo = containerInfo(client, conUuid.substring(0, conUuid.length() - 1));
				if (!jo.isEmpty()) {
					jo.put("runCommand", result.getString("createCommand"));
					containerArray.add(jo);
				}
				index++;
				TimeUnit.MILLISECONDS.sleep(1000);
			} catch (InterruptedException e1) {
				LOGGER.error("application release InterruptedException error", e1);
				continue;
			} catch (Exception e) {
				LOGGER.error("application release Exception error", e);
				continue;
			}
		}
		return containerArray;
	}

	/**
	 * @author langzi
	 * @param client
	 * @param imageName
	 * @return
	 * @version 1.0 2015年9月10日
	 */
	private boolean imageIsExist(DockerClient client, String imageName) {
		List<Image> imageList = client.listImagesCmd().exec();
		for (Image image : imageList) {
			for (int i = 0; i < image.getRepoTags().length; i++) {
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
	 * @version 1.0 2015年9月10日 转化容器的信息
	 */
	private JSONObject containerInfo(DockerClient client, String conUuid) {
		JSONObject jo = new JSONObject();
		LOGGER.info(conUuid);
		if (StringUtils.hasText(conUuid)) {
			List<Container> containers = client.listContainersCmd().withShowAll(true).exec();
			if (containers == null || containers.isEmpty()) {
				return jo;
			}
			for (Container container : containers) {
				if (container.getId().equals(conUuid)) {
					jo = (JSONObject) JSONObject.toJSON(container);
					LOGGER.info(jo.toString());
					break;
				}
			}
		}
		return jo;
	}

	/**
	 * @author langzi
	 * @param clusterIp
	 * @param clusterPort
	 * @return
	 * @version 1.0 2015年9月10日 构建dockerclient客户端
	 */
	private DockerClient getDockerClient(String ip, String port) {
		String url = "http://" + ip + ":" + port;
		return DockerClientBuilder.getInstance(url).build();
	}

	/**
	 * @author langzi
	 * @param clusterIp
	 * @param clusterPort
	 * @return
	 * @version 1.0 2015年11月25日
	 */
	private String getCreateCommand(String ip, String port, ApplicationReleaseModel model, int conId,
			HostResourceModel hrm) {
		String cpuCores = "";
		StringBuilder command = new StringBuilder();
		String masterUrl = ip + ":" + port;
		command.append("docker -H tcp://");
		command.append(masterUrl);
		command.append(" run ");
		if (hrm != null) {
			cpuCores = org.apache.commons.lang.StringUtils.join(hrm.getHostCpuCore(), ",");
			String hostRealName = hrm.gethostRealName();
			command.append("-e \"constraint:node==" + hostRealName + "\"");
		}
		command.append(" -e DOCKER_HOST=\"" + ip + ":" + port + "\"");
		if (StringUtils.hasText(model.getEnv())) {
			command.append(" " + model.getEnv());
		}
		command.append(" -v /etc/localtime:/etc/localtime");
		if (StringUtils.hasText(model.getVolume())) {
			String[] vols = model.getVolume().split(" ");
			if (vols.length > 0) {
				for (String vol : vols) {
					String[] volumes = vol.split(":");
					if (volumes.length == 2) {
						String hostVol = volumes[0];
						String conVol = volumes[1];
						String volume = "";
						conId += model.getLastConId() + 1;
						if (hostVol.endsWith("/")) {
							volume = hostVol + "container_" + conId + ":" + conVol;
						} else {
							volume = hostVol + "/container_" + conId + ":" + conVol;
						}
						command.append(" -v " + volume);
					}
				}
			}
		}
		if (model.getCpu() > 0 && StringUtils.hasText(cpuCores)) {
			command.append(" --cpuset-cpus=" + cpuCores);
		}
		if (model.getMem() > 0) {
			command.append(" -m " + model.getMem() + "m");
		}
		String createParams = model.getParams();
		/** 特殊发布中没有输入参数，此处添加判空处理 */
		if (createParams != null) {
			createParams.replaceAll(";", " ");
			if (StringUtils.hasText(createParams)) {
				command.append(" " + createParams);
			}
		}
		command.append(" -d ");
		command.append(" -P");
		command.append(" " + model.getImageUrl());
		if (StringUtils.hasText(model.getCommand())) {
			command.append(" " + model.getCommand() + ";");
		}
		return command.toString();
	}

}

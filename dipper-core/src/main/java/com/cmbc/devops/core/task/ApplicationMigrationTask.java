package com.cmbc.devops.core.task;

import java.util.List;
import java.util.concurrent.Callable;


import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import com.cmbc.devops.bean.Result;
import com.cmbc.devops.util.SSH;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Image;

public class ApplicationMigrationTask implements Callable<Result> {
	
	private SSH ssh;
	private DockerClient client;
	private String imageName;
	private String saveImgCommad;
	
	public ApplicationMigrationTask(SSH ssh, DockerClient client, String imageName, String saveImgCommand) {
		this.ssh = ssh;
		this.client = client;
		this.imageName = imageName;
		this.saveImgCommad = saveImgCommand;
	}
	
	private static final Logger LOGGER = Logger.getLogger(ApplicationMigrationTask.class);
	
	@Override
	public Result call() throws Exception {
		//1.检查镜像是否存在
		if (!imageIsExist(client, imageName)) {
			LOGGER.info("Get image from registry");
			client.pullImageCmd(imageName).exec(null);
		}
		//2.镜像导出成tar包
		String execInfo = "";
		if (ssh.connect()) {
			execInfo = ssh.executeWithResult(saveImgCommad);
			ssh.close();
		}
		if (StringUtils.hasText(execInfo)) {
			return new Result(false, execInfo);
		}
		return new Result(true, execInfo);
	}
	
	/**
	 * @author langzi
	 * @param client
	 * @param imageName
	 * @return
	 * @version 1.0 2015年9月10日
	 */
	private boolean imageIsExist(DockerClient client, String imageInfo) {
		List<Image> imageList = client.listImagesCmd().exec();
		for (Image image : imageList) {
			for (int i = 0; i < image.getRepoTags().length; i++) {
				if (imageInfo.equals(image.getRepoTags()[i])) {
					return true;
				}
			}
		}
		return false;
	}

}

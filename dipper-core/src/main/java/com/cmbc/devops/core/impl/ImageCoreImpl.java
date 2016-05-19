package com.cmbc.devops.core.impl;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.cmbc.devops.bean.PathObject;
import com.cmbc.devops.bean.RemoteFile;
import com.cmbc.devops.bean.Result;
import com.cmbc.devops.config.RegistryConfig;
import com.cmbc.devops.config.SystemConfig;
import com.cmbc.devops.core.ImageCore;
import com.cmbc.devops.core.task.ApplicationMigrationTask;
import com.cmbc.devops.util.CommandExcutor;
import com.cmbc.devops.util.SSH;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DockerClientBuilder;

/**
 * 镜像制作核心接口实现类，用户登录到仓库主机进行镜像的相关操作，主要包括镜像的制作，打标和发布
 * 
 * @author dmw
 * @version 1.0
 */
@Component
public class ImageCoreImpl implements ImageCore {

	private static final Logger LOGGER = Logger.getLogger(ImageCoreImpl.class);
	@Autowired
	private RegistryConfig registryConfig;

	@Autowired
	private SystemConfig config;

	@Override
	public Result makeImage(String ip, String name, String password, String fileName, String imageName, String imageTag,
			boolean basic, String imageUuid) {
		SSH ssh = CommandExcutor.getSsh(ip, name, password);

		/* 根据镜像的类型，tar或者是tar.gz,zip分别进行处理 */
		if (basic) {
			StringBuilder commandStr = new StringBuilder();
			commandStr.append("cd ").append(registryConfig.getTempPath() + imageUuid + "/").append(";");
			if (fileName.contains(".tar")) {
				commandStr.append(
						"docker load --input " + registryConfig.getTempPath() + imageUuid + "/" + fileName + ";");
				/* 删除整个保存上传文件文件夹 */
				commandStr.append("rm -rf " + registryConfig.getTempPath() + imageUuid + "/;");
				commandStr.append(" docker images|grep ").append(imageName).append("|grep ").append(imageTag)
						.append("|awk '{print \"IMAGE_ID:\"$3 \"\\n IMAGE_SIZE:\" $7 $8 }';");
			} else {
				LOGGER.error("制作基础镜像异常：不支持此类文件类型！文件名：" + fileName);
				return new Result(false, "制作基础镜像异常：不支持此类文件(" + fileName + ")类型！");
			}
			try {
				if (ssh.connect()) {
					String command = commandStr.toString();
					LOGGER.info("Build Basic Image Command:" + command);
					try {
						String result = ssh.executeWithResult(command.toString());
						LOGGER.info("Make basic image result:" + result);
						return processMakeImageResult(basic, result);
					} catch (Exception e) {
						LOGGER.info("Make basic image fail!" + e);
						return new Result(false, "制作基础镜像失败：脚本执行异常！");
					} finally {
						ssh.close();
					}
				} else {
					return new Result(false, "SSH无法连接仓库主机！请检查主机运行状态、网络连接状态或核对输入正确的用户名和密码。");
				}
			} catch (Exception e) {
				LOGGER.error("镜像制作失败：SSH无法连接仓库主机！请检查主机运行状态、网络连接状态或核对输入正确的用户名和密码。", e);
				return new Result(false, "镜像制作失败：SSH无法连接仓库主机！请检查主机运行状态、网络连接状态或核对输入正确的用户名和密码。");
			} finally {
				ssh.close();
			}
		} else {
			/* 制作应用镜像部分，首先获取解压后的文件夹名称 */
			String targetFolder = extractTargetFile(ip, name, password, fileName, imageUuid);
			/* 通过目标文件夹，生成制作应用镜像命令 */
			String buildImageCommand = geneBuildImageCommand(targetFolder, imageName, imageTag, imageUuid);
			try {
				if (!ssh.connect()) {
					return new Result(false, "SSH无法连接仓库主机！请检查主机运行状态、网络连接状态或核对输入正确的用户名和密码。");
				}
				if (!StringUtils.hasText(buildImageCommand)) {
					return new Result(false, "制作应用镜像命令失败！");
				}
				LOGGER.info("Build App Image Command:" + buildImageCommand);
				String result = ssh.executeWithResult(buildImageCommand);
				LOGGER.info("Make app image result:" + result);
				return processMakeImageResult(basic, result);
			} catch (Exception e) {
				LOGGER.error("镜像制作失败：SSH无法连接仓库主机！请检查主机运行状态、网络连接状态或核对输入正确的用户名和密码。", e);
				return new Result(false, "镜像制作失败：SSH无法连接仓库主机！请检查主机运行状态、网络连接状态或核对输入正确的用户名和密码。");
			} finally {
				ssh.close();
			}
		}
	}

	@Override
	/** 向后台请求制作镜像 **/
	public Result makeRemoteImage(String ip, String name, String password, String fileFolder, String fileName,
			String imageName, String imageTag, boolean basic) {

		SSH ssh = CommandExcutor.getSsh(ip, name, password);
		/* 根据镜像的类型，tar或者是tar.gz,zip分别进行处理 */
		if (basic) {
			StringBuilder loadCommandStr = new StringBuilder();
			String checkImageListCmd = "docker images|grep " + imageName + "|awk '{print \"IMAGE_ID:\"$3 \"\\n \""
					+ "\"IMAGE_TAG:\"$2 \"\\n \"" + "\"IMAGE_SIZE:\" $7 $8 }';";

			loadCommandStr.append("cd ").append(fileFolder + "/").append(";");
			if (fileName.contains(".tar")) {
				loadCommandStr.append("docker load --input " + fileFolder + "/" + fileName + ";");
				/* 删除整个保存上传文件文件夹 */
				loadCommandStr.append("rm -rf " + fileFolder + "/;");
				// commandStr.append(" docker images|grep
				// ").append(imageName).append("|grep ").append(imageTag)
				// .append("|awk '{print \"IMAGE_ID:\"$3 \"\\n IMAGE_SIZE:\" $7
				// $8 }';");
			} else {
				LOGGER.warn("(远程制作基础)制作基础镜像异常：不支持此类文件类型！文件名：" + fileName);
				return new Result(false, "(远程制作基础)制作基础镜像异常：不支持此类文件(" + fileName + ")类型！");
			}
			try {
				if (!ssh.connect()) {
					return new Result(false, "SSH无法连接仓库主机！请检查主机运行状态、网络连接状态或核对输入正确的用户名和密码。");
				}
				String command = loadCommandStr.toString();
				LOGGER.info("Remote Build Basic Image Command:" + command);

				String loadImageResult = ssh.executeWithResult(command.toString());
				if (StringUtils.hasText(loadImageResult)) {
					return new Result(false, "(远程加载镜像)加载镜像出现异常：" + loadImageResult);
				}
				LOGGER.info("Remote load basic image result:" + loadImageResult);
				ssh.close();

				/** 在加载镜像之后做镜像列表检查 */
				ssh.connect();
				LOGGER.info("On registry exec cmd:(" + checkImageListCmd + ")");
				String afterLoadResult = ssh.executeWithResult(checkImageListCmd);
				LOGGER.info("After docker load image.tar :\n" + afterLoadResult);
				/** 获取返回值的前三行内容 */
				String loadResponse = "";
				if (StringUtils.hasText(afterLoadResult)) {
					String[] lineArray = afterLoadResult.split("\n");
					loadResponse = lineArray[0] + "\n" + lineArray[1] + "\n" + lineArray[2] + "\n";
				}
				LOGGER.info("Docker load image.tar response is :\n" + loadResponse);

				return processLoadImageResult(basic, loadResponse);
			} catch (Exception e) {
				LOGGER.error("镜像制作失败：SSH无法连接仓库主机！请检查主机运行状态、网络连接状态或核对输入正确的用户名和密码。", e);
				return new Result(false, "镜像制作失败：SSH无法连接仓库主机！请检查主机运行状态、网络连接状态或核对输入正确的用户名和密码。");
			} finally {
				ssh.close();
			}
		} else {
			/* 制作应用镜像部分，首先获取解压后的文件夹名称 */
			String targetFolder = rmtExtractTargetFile(ip, name, password, fileFolder, fileName);
			/* 通过目标文件夹，生成制作应用镜像命令 */
			String buildImgCommand = rmtGeneBuildImageCommand(targetFolder, imageName, imageTag, fileFolder);
			if (!StringUtils.hasText(buildImgCommand)) {
				return new Result(false, "制作应用镜像命令失败！");
			}
			try {
				if (!ssh.connect()) {
					return new Result(false, "SSH无法连接仓库主机！请检查主机运行状态、网络连接状态或核对输入正确的用户名和密码。");
				}
				LOGGER.info("Remote Build App Image Command:" + buildImgCommand);
				String result = ssh.executeWithResult(buildImgCommand);
				LOGGER.info("Remote Make app image result:" + result);
				return processMakeImageResult(basic, result);
			} catch (Exception e) {
				LOGGER.error("镜像制作失败：SSH无法连接仓库主机！请检查主机运行状态、网络连接状态或核对输入正确的用户名和密码。", e);
				return new Result(false, "镜像制作失败：SSH无法连接仓库主机！请检查主机运行状态、网络连接状态或核对输入正确的用户名和密码。");
			} finally {
				ssh.close();
			}
		}
	}

	/**
	 * @param basic
	 * @param response
	 * @return
	 */
	private Result processLoadImageResult(boolean basic, String response) {
		if (response.contains("cannot find or open")) {
			return new Result(false, "【应用包解压失败】：应用文件压缩包不存在！可能被其他用户后台删除");
		} else if (response.contains("Cannot connect to the Docker daemon.")) {
			return new Result(false, "【制作镜像失败】：仓库主机上的Docker服务异常！");
			/** @bug219_begin:[镜像管理]制作镜像,当找不到文件夹或文件而制作失败,请将该错误原因返回给终端 **/
		} else if (response.toLowerCase().contains("no such file or directory")) {
			return new Result(false, "【制作镜像失败】：仓库主机上无法找到与制作镜像相关的文件或文件夹！");
		}
		/** @bug219_finish **/
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(new ByteArrayInputStream(response.getBytes())));
		String imageUuid = null, imageSize = null, imageTag = null;
		try {
			String line = null;
			while ((line = reader.readLine()) != null) {
				LOGGER.info("----" + line);
				if (line.contains("IMAGE_ID")) {
					imageUuid = line.split(":")[1];
				} else if (line.contains("IMAGE_SIZE")) {
					imageSize = line.split(":")[1];
				} else if (line.contains("IMAGE_TAG")) {
					imageTag = line.split(":")[1];
				} else {
					continue;
				}
			}
		} catch (IOException e) {
			LOGGER.error("解析返回值失败", e);
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				LOGGER.error("关闭数据流失败：", e);
			}
		}
		if (null == imageUuid) {
			return new Result(false, "仓库加载镜像执行异常，镜像导入失败！");
		} else {
			return new Result(true, imageTag + ":" + imageUuid + ":" + imageSize);
		}
	}

	@Override
	public Result pushImage(String ip, String name, String password, String image) {
		SSH ssh = CommandExcutor.getSsh(ip, name, password);
		boolean flag = false;
		try {
			flag = ssh.connect();
		} catch (Exception e) {
			LOGGER.error("SSH无法连接仓库主机！请检查主机运行状态、网络连接状态或核对输入正确的用户名和密码。", e);
			return new Result(false, "镜像发布失败：SSH无法连接仓库主机！请检查主机运行状态、网络连接状态或核对输入正确的用户名和密码。");
		}
		if (flag) {
			StringBuilder commandStr = new StringBuilder();
			commandStr.append("docker push " + image);
			try {
				LOGGER.info("Push image Command:" + commandStr.toString());
				String result = ssh.executeWithResult(commandStr.toString());
				LOGGER.info("Push image result:" + result);
				if (result.contains("error") || result.contains("failed") || result.contains("EOF")) {
					return new Result(false, "镜像发布脚本执行失败！");
				} else if (result.contains("Cannot connect to the Docker daemon.")) {
					return new Result(false, "【制作镜像失败】：仓库主机上的Docker服务异常！");
				} else if (result.contains("refused")) {
					return new Result(false, "仓库服务调用异常");
				} else {
					return new Result(true, "镜像发布成功！");
				}
			} catch (Exception e) {
				LOGGER.error("Push image fail", e);
				return new Result(false, "发布镜像失败：发布镜像脚本执行异常！");
			} finally {
				ssh.close();
			}
		} else {
			LOGGER.warn("连接主机异常！可能是用户名或密码错误！");
			return new Result(false, "镜像发布失败：连接仓库主机异常！");
		}

	}

	@Override
	public Result tagImage(String ip, String name, String password, String source, String target) {
		SSH ssh = CommandExcutor.getSsh(ip, name, password);
		try {
			if (ssh.connect()) {
				StringBuilder command = new StringBuilder();
				command.append("docker tag ").append(source).append(" ").append(target);
				try {
					String result = ssh.executeWithResult(command.toString());
					if (result.contains("Cannot connect to the Docker daemon")) {
						LOGGER.error("Connect host(IP:" + ip + ") and execute failed!");
						return new Result(false, "脚本执行异常，链接主机(IP:" + ip + ")失败！");
					}

					LOGGER.info("tag image result:" + result);
					return new Result(true, "镜像打标识成功！");
				} catch (Exception e) {
					LOGGER.error("Tag image fail", e);
					return new Result(false, "镜像打标识失败：脚本执行异常！");
				} finally {
					ssh.close();
				}
			} else {
				LOGGER.warn("镜像打标失败：仓库主机无法连接！");
				return new Result(false, "镜像打标识失败：仓库主机无法连接！");
			}
		} catch (SocketException e) {
			LOGGER.error("ssh connect error:", e);
			return new Result(false, "镜像打标识失败：仓库主机无法连接！");
		} catch (IOException e) {
			LOGGER.error("ssh connect error:", e);
			return new Result(false, "镜像打标识失败：仓库主机无法连接！");
		}
	}

	@Override
	public Result tagLoadImage(String ip, String name, String password, String imageUuid, String target) {
		SSH ssh = CommandExcutor.getSsh(ip, name, password);
		try {
			if (ssh.connect()) {
				StringBuilder command = new StringBuilder();
				command.append("docker tag ").append(imageUuid).append(" ").append(target);
				try {
					String result = ssh.executeWithResult(command.toString());
					if (result.contains("Cannot connect to the Docker daemon")) {
						LOGGER.error("Connect host(IP:" + ip + ") and execute failed!");
						return new Result(false, "脚本执行异常，链接主机(IP:" + ip + ")失败！");
					}

					LOGGER.info("tag image result:" + result);
					return new Result(true, "镜像打标识成功！");
				} catch (Exception e) {
					LOGGER.error("Tag image fail", e);
					return new Result(false, "镜像打标识失败：脚本执行异常！");
				} finally {
					ssh.close();
				}
			} else {
				LOGGER.warn("镜像打标失败：仓库主机无法连接！");
				return new Result(false, "镜像打标识失败：仓库主机无法连接！");
			}
		} catch (SocketException e) {
			LOGGER.error("ssh connect error:", e);
			return new Result(false, "镜像打标识失败：仓库主机无法连接！");
		} catch (IOException e) {
			LOGGER.error("ssh connect error:", e);
			return new Result(false, "镜像打标识失败：仓库主机无法连接！");
		}
	}

	@Override
	public PathObject queryPathElement(String masterIp, String name, String password, String path) {
		/** 与远程主机建立SSH链接 **/
		SSH ssh = CommandExcutor.getSsh(masterIp, name, password);
		boolean flag = false;
		try {
			flag = ssh.connect();
		} catch (Exception e) {
			ssh.close();
			LOGGER.error("SSH无法连接仓库主机！请检查主机运行状态、网络连接状态或核对输入正确的用户名和密码。", e);
			return new PathObject(1, "SSH无法连接目标主机(" + masterIp + ")！请检查主机运行状态、网络连接状态或核对输入正确的用户名和密码。", null);
		}
		if (flag) {
			StringBuilder command = new StringBuilder();
			command.append("cd ").append(path).append(";ls -lh;");
			try {
				String resultInfo = ssh.executeWithResult(command.toString());
				if (resultInfo.toLowerCase().contains("no such file or directory")) {
					LOGGER.error("浏览文件路径：" + path + "，在主机(" + masterIp + ")中不存在。");
					return new PathObject(4, "浏览文件路径：" + path + "，在主机(" + masterIp + ")中不存在。", null);
				} else {
					/** 根据回车分割返回的字符串信息 **/
					List<RemoteFile> rfList = new ArrayList<RemoteFile>();
					String[] retArray = resultInfo.split("[\\n]+");
					int retLength = retArray.length;
					/** 当文件夹内容为空的情况 **/
					if (retLength <= 1) {
						return new PathObject(6, "浏览文件路径：" + path + "，内容为空。", null);
					} else {
						/** count从1开始，跳过内容[total 0]的第一行 **/
						for (int i = 1; i < retArray.length; i++) {
							RemoteFile remoteFile = new RemoteFile();
							/** 根据空格或者制表符切分每行信息 **/
							String[] eleArray = retArray[i].split("\\s+");
							int eleLength = eleArray.length;
							/** 写入文件名称 **/
							remoteFile.setFileName(eleArray[eleLength - 1]);
							/** 填充文件夹类型 ，与前台中图标名称对应 **/
							if (eleArray[0].startsWith("d")) {
								/* 文件夹类型 */
								remoteFile.setFileSuffix("directory");
							} else if (eleArray[0].startsWith("l")) {
								/* 链接文件类型 */
								remoteFile.setFileSuffix("link");
							} else if (eleArray[eleLength - 1].toLowerCase().equals("dockerfile")) {
								/* docker编译文件类型 */
								remoteFile.setFileSuffix("docker");
							} else {
								String[] filenameArr = eleArray[eleLength - 1].split("\\.");
								int nameLength = filenameArr.length;
								remoteFile.setFileSuffix(filenameArr[nameLength - 1]);
							}
							rfList.add(remoteFile);
						}
						return new PathObject(0, "浏览文件路径：" + path + "，在主机(" + masterIp + ")中成功。", rfList);
					}
				}

			} catch (Exception e) {
				LOGGER.error("执行命令：" + command.toString() + "失败。", e);
				return new PathObject(2, "执行命令：" + command.toString() + "失败。", null);
			} finally {
				ssh.close();
			}
		} else {
			LOGGER.error("SSH无法连接仓库主机！请检查主机运行状态、网络连接状态或核对输入正确的用户名和密码。");
			return new PathObject(1, "SSH无法连接目标主机(" + masterIp + ")！请检查主机运行状态、网络连接状态或核对输入正确的用户名和密码。", null);
		}
	}

	@Override
	public Result createRegTempFolder(String hostIp, String hostUser, String hostPwd, String folder) {
		/** 与远程主机建立SSH链接 **/
		SSH ssh = CommandExcutor.getSsh(hostIp, hostUser, hostPwd);
		try {
			if (ssh.connect()) {
				StringBuilder command = new StringBuilder();
				command.append("cd;").append("mkdir -p /home/" + hostUser + "/" + folder + ";")
						.append("cd /home/" + hostUser + "/" + folder + ";").append("pwd;");
				try {
					String pwdInfo = ssh.executeWithResult(command.toString());
					LOGGER.info("仓库主机(IP:" + hostIp + ")执行命令:[" + command + "]");
					/* 执行命令 */
					return new Result(true, pwdInfo);
				} catch (Exception e) {
					LOGGER.error("执行命令：" + command.toString() + "失败。", e);
					return new Result(false, "执行命令：" + command.toString() + "失败。");
				}
			} else {
				LOGGER.error("SSH无法连接仓库主机！请检查主机运行状态、网络连接状态或核对输入正确的用户名和密码。");
				return new Result(false, "SSH无法连接目标主机(" + hostIp + ")！请检查主机运行状态、网络连接状态或核对输入正确的用户名和密码。");
			}
		} catch (NullPointerException | IOException npe) {
			LOGGER.error("SSH无法连接仓库主机！请检查主机运行状态、网络连接状态或核对输入正确的用户名和密码。", npe);
			return new Result(false, "SSH无法连接目标主机(" + hostIp + ")！请检查主机运行状态、网络连接状态或核对输入正确的用户名和密码。");
		} finally {
			ssh.close();
		}
	}

	@Override
	public Result deleteRegTempFolder(String hostIp, String hostUser, String hostPwd, String folder) {
		/** 与远程主机建立SSH链接 **/
		SSH ssh = CommandExcutor.getSsh(hostIp, hostUser, hostPwd);
		boolean flag = false;
		try {
			flag = ssh.connect();
		} catch (Exception e) {
			ssh.close();
			LOGGER.error("SSH无法连接仓库主机！请检查主机运行状态、网络连接状态或核对输入正确的用户名和密码。", e);
			return new Result(false, "SSH无法连接目标主机(" + hostIp + ")！请检查主机运行状态、网络连接状态或核对输入正确的用户名和密码。");
		}

		if (flag) {
			StringBuilder command = new StringBuilder();
			command.append("cd;").append("rm -rf " + folder + ";");
			try {
				String pwdInfo = ssh.executeWithResult(command.toString());
				LOGGER.info("仓库主机(IP:" + hostIp + ")执行命令:[" + command + "]");
				/* 执行命令 */
				return new Result(true, pwdInfo);
			} catch (Exception e) {
				LOGGER.error("执行命令：" + command.toString() + "失败。", e);
				return new Result(false, "执行命令：" + command.toString() + "失败。");
			}
		} else {
			LOGGER.error("SSH无法连接仓库主机！请检查主机运行状态、网络连接状态或核对输入正确的用户名和密码。");
			return new Result(false, "SSH无法连接目标主机(" + hostIp + ")！请检查主机运行状态、网络连接状态或核对输入正确的用户名和密码。");
		}
	}

	@Override
	public Result exportImage(String hostIp, String hostUser, String hostPwd, String imageInfo, String imageName,
			String imageTag, String savePath) {
		String url = "http://" + hostIp + ":" + config.getDockerPort();
		LOGGER.info("docker client url:" + url);
		savePath = savePath.endsWith("/") ? savePath : savePath + "/";
		String saveImgCommand = "docker save -o " + savePath + imageName + ":" + imageTag.trim() + ".tar " + imageInfo;
		LOGGER.info("save image command:" + saveImgCommand);
		// 1.并发提交任务
		ExecutorService executor = Executors.newCachedThreadPool();
		CompletionService<Result> comp = new ExecutorCompletionService<>(executor);
		DockerClient client = DockerClientBuilder.getInstance(url).build();
		SSH ssh = CommandExcutor.getSsh(hostIp, hostUser, hostPwd);
		comp.submit(new ApplicationMigrationTask(ssh, client, imageInfo, saveImgCommand));
		executor.shutdown();
		// 2.等待执行结果
		int index = 0;
		Result result = new Result(false, "");
		while (index < 1) {
			Future<Result> future = comp.poll();
			try {
				if (future != null && future.get() != null) {
					result = future.get();
					LOGGER.info(result.getMessage());
					index++;
				}
				TimeUnit.MILLISECONDS.sleep(1000);
			} catch (InterruptedException e) {
				LOGGER.error("export image was interrupted", e);
			} catch (ExecutionException e) {
				LOGGER.error("exec export image command error", e);
			}
		}
		return result;
	}

	/**
	 * @param imageUuid
	 * @param fileName
	 * @param fileType
	 * @return
	 */
	private String getExecFileCommand(String imageUuid, String fileName, String fileType) {
		StringBuilder execFileCommand = new StringBuilder();
		/* 进入仓库主机的临时文件夹目录 */
		execFileCommand.append("cd ").append(registryConfig.getTempPath() + imageUuid + "/").append(";");
		if (fileName.contains(".tar.gz")) {
			execFileCommand.append("tar zxf " + fileName).append(";");
		} else if (fileName.contains(".tgz")) {
			execFileCommand.append("tar zxf " + fileName).append(";");
		} else if (fileName.contains(".zip")) {
			execFileCommand.append("unzip -q " + fileName).append(";");
		}
		execFileCommand.append("ls").append(";");
		return execFileCommand.toString();
	}

	/**
	 * @param ssh
	 * @param execCommand
	 * @param fileType
	 * @return
	 */
	private String getFileFolder(SSH ssh, String execCommand, String fileType) {
		String fileFolder = "";
		String execResult = "";
		/* 执行解压命令，获取返回的字符串信息 */
		try {
			LOGGER.info("Registry Host Execute Command: " + execCommand.toString());
			execResult = ssh.executeWithResult(execCommand.toString());
			LOGGER.info("Get File List From Registry Host is:[" + execResult + "]");
			/* 过滤执行后的结果，打包文件和文件夹，以空格分隔 */

		} catch (Exception e) {
			LOGGER.info("Registry Host Execute Command Fail", e);
			return fileFolder;
		} finally {
			ssh.close();
		}
		String[] files = execResult.split("\n");
		for (String file : files) {
			file = file.trim();
			if (StringUtils.hasText(file) && !file.contains(fileType)) {
				fileFolder = file;
			} else {
				continue;
			}
		}
		LOGGER.info("Get Target Folder is:[" + fileFolder + "]");
		return fileFolder;
	}

	/**
	 * @param ipaddr
	 * @param username
	 * @param password
	 * @param fileName
	 * @param imageUuid
	 * @return （1）登陆到仓库服务器，解压文件夹并返回解压后的文件夹名称
	 */
	private String extractTargetFile(String ipaddr, String username, String password, String fileName,
			String imageUuid) {
		/* 获取与仓库主机的连接 */
		SSH ssh = CommandExcutor.getSsh(ipaddr, username, password);
		try {
			if (!ssh.connect()) {
				LOGGER.info("Connect SSH to Registry Host Failed. ");
				return null;
			}
		} catch (SocketException e) {
			LOGGER.error("ssh connect error:", e);
		} catch (IOException e) {
			LOGGER.error("ssh connect error:", e);
		}
		if (fileName.contains(".tar.gz")) {
			String execFileCommand = getExecFileCommand(imageUuid, fileName, ".tar.gz");
			// get file folder
			return getFileFolder(ssh, execFileCommand.toString(), ".tar.gz");
		} else if (fileName.contains(".tgz")) {
			String execFileCommand = getExecFileCommand(imageUuid, fileName, ".tar.gz");
			// get file folder
			return getFileFolder(ssh, execFileCommand.toString(), ".tgz");
		} else if (fileName.contains(".zip")) {
			String execFileCommand = getExecFileCommand(imageUuid, fileName, ".tar.gz");
			// get file folder
			return getFileFolder(ssh, execFileCommand.toString(), ".zip");
		}
		return null;
	}

	/**
	 * @param targetFolder
	 * @param imageName
	 * @param imageTag
	 * @param image_uuid
	 * @return （2）根据解压后的目标业务文件夹，生成制作镜像以及后续处理命令字符串
	 */
	private String geneBuildImageCommand(String targetFolder, String imageName, String imageTag, String imageUuid) {
		String tag = imageName + ":" + imageTag;
		StringBuilder buildImageCommand = new StringBuilder();
		buildImageCommand.append(
				"docker build -t " + tag + " " + registryConfig.getTempPath() + imageUuid + "/" + targetFolder + "/")
				.append(";");
		/* 删除整个保存上传文件文件夹 */
		buildImageCommand.append("rm -rf " + registryConfig.getTempPath() + imageUuid + "/").append(";");
		buildImageCommand.append(" docker images|grep ").append(imageName).append("|grep ").append(imageTag)
				.append("|awk '{print \"IMAGE_ID:\"$3 \"\\n IMAGE_SIZE:\" $7 $8 }'").append(";");
		return buildImageCommand.toString();
	}

	/**
	 * @param ipaddr
	 * @param username
	 * @param password
	 * @param fileFolder
	 * @param fileName
	 * @return （3）（远程制作镜像）登陆到仓库服务器，解压文件夹并返回解压后的文件夹名称
	 */
	private String rmtExtractTargetFile(String ipaddr, String username, String password, String fileFolder,
			String fileName) {
		/* 获取与仓库主机的连接 */
		SSH ssh = CommandExcutor.getSsh(ipaddr, username, password);
		/* 进入仓库主机的临时文件夹目录 */
		StringBuilder commandStr = new StringBuilder();
		commandStr.append("cd ").append(fileFolder + "/").append(";");
		try {
			if (ssh.connect()) {
				if (fileName.contains(".tar.gz")) {
					/* 去除列出详细信息的参数-v */
					commandStr.append("tar zxf " + fileName).append(";");
					commandStr.append("ls").append(";");
					/* 执行解压命令，获取返回的字符串信息 */
					return getFileFolder(ssh, commandStr.toString(), ".tar.gz");
				} else if (fileName.contains(".tgz")) {
					/* 去除列出详细信息的参数-v */
					commandStr.append("tar zxf " + fileName).append(";");
					commandStr.append("ls").append(";");

					/* 执行解压命令，获取返回的字符串信息 */
					return getFileFolder(ssh, commandStr.toString(), ".tgz");
				} else if (fileName.contains(".zip")) {
					/* 添加-q参数，解压过程中不现实任何信息 */
					commandStr.append("unzip -q " + fileName).append(";");
					commandStr.append("ls").append(";");
					/* 执行解压命令，获取返回的字符串信息 */
					getFileFolder(ssh, commandStr.toString(), ".zip");
				}
			} else {
				LOGGER.info("Connect SSH to Registry Host Failed. ");
			}
		} catch (SocketException e) {
			LOGGER.error("ssh connect error:", e);
		} catch (IOException e) {
			LOGGER.error("ssh connect error:", e);
		}

		/* 应用压缩文件后缀名不在（tar.gz，tgz和zip之中的） */
		return null;
	}

	/**
	 * @param targetFolder
	 * @param imageName
	 * @param imageTag
	 * @param fileFolder
	 * @return 4）(远程制作镜像)根据解压后的目标业务文件夹，生成制作镜像以及后续处理命令字符串
	 */
	private String rmtGeneBuildImageCommand(String targetFolder, String imageName, String imageTag, String fileFolder) {
		String tag = imageName + ":" + imageTag;
		StringBuilder commandStr = new StringBuilder();
		commandStr.append("docker build -t " + tag + " " + fileFolder + "/" + targetFolder + "/").append(";");
		/* 删除整个保存上传文件文件夹 */
		commandStr.append("rm -rf " + fileFolder + "/").append(";");
		commandStr.append(" docker images|grep ").append(imageName).append("|grep ").append(imageTag)
				.append("|awk '{print \"IMAGE_ID:\"$3 \"\\n IMAGE_SIZE:\" $7 $8 }'").append(";");
		return commandStr.toString();
	}

	/**
	 * @param basic
	 * @param response
	 * @return
	 */
	private Result processMakeImageResult(boolean basic, String response) {
		if (response.contains("cannot find or open")) {
			return new Result(false, "【应用包解压失败】：应用文件压缩包不存在！可能被其他用户后台删除");
		} else if (response.contains("Cannot connect to the Docker daemon.")) {
			return new Result(false, "【制作镜像失败】：仓库主机上的Docker服务异常！");
			/** @bug219_begin:[镜像管理]制作镜像,当找不到文件夹或文件而制作失败,请将该错误原因返回给终端 **/
		} else if (response.toLowerCase().contains("no such file or directory")) {
			return new Result(false, "【制作镜像失败】：仓库主机上无法找到与制作镜像相关的文件或文件夹！");
		}
		/** @bug219_finish **/
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(new ByteArrayInputStream(response.getBytes())));
		String uuid = null, imagesize = null;
		try {
			String line = null;
			while ((line = reader.readLine()) != null) {
				LOGGER.info("----" + line);
				if (line.contains("IMAGE_ID")) {
					uuid = line.split(":")[1];
				} else if (line.contains("IMAGE_SIZE")) {
					imagesize = line.split(":")[1];
				} else {
					continue;
				}
			}
		} catch (IOException e) {
			LOGGER.error("解析返回值失败", e);
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				LOGGER.error("关闭数据流失败：", e);
			}
		}
		if (null == uuid) {
			return new Result(false, "镜像制作脚本执行异常，镜像制作失败！");
		} else {
			return new Result(true, uuid + ":" + imagesize);
		}
	}

}

package com.cmbc.devops.upload;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import com.cmbc.devops.config.RegistryConfig;
import com.cmbc.devops.entity.Host;
import com.cmbc.devops.entity.User;
import com.cmbc.devops.message.Message;
import com.cmbc.devops.model.UpFileModel;
import com.cmbc.devops.service.HostService;
import com.cmbc.devops.util.SSH;

@Component
public class UploadServiceHandller implements WebSocketHandler {
	private static Logger logger = Logger.getLogger(UploadServiceHandller.class);
	@Resource
	private HostService hostService;
	@Resource
	private RegistryConfig registryConfig;
	private static List<WebSocketSession> currentUsers;

	private static List<WebSocketSession> getCurrentUsers() {
		return currentUsers;
	}

	private static void setCurrentUsers(List<WebSocketSession> currentUsers) {
		UploadServiceHandller.currentUsers = currentUsers;
	}

	static {
		UploadServiceHandller.setCurrentUsers(new ArrayList<WebSocketSession>());
	}

	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		UploadServiceHandller.getCurrentUsers().add(session);
	}

	public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
		if (message instanceof TextMessage) {
			handleTextMessage(session, (TextMessage) message);
		} else if (message instanceof BinaryMessage) {
			handleBinaryMessage(session, (BinaryMessage) message);
		} else {
			logger.error("Unexpected WebSocket message type: " + message);
			throw new IllegalStateException("Unexpected WebSocket message type: " + message);
		}
	}

	// 处理字符串
	private void handleTextMessage(WebSocketSession session, TextMessage message) {
		try {
			if (message.getPayload().contains("fileStartName")) {
				UpFileModel filemodel = new ObjectMapper().readValue(message.getPayload(), UpFileModel.class);
				Host host = new Host();
				host.setHostId(filemodel.getHostId());
				try {
					host = hostService.getHost(host);
				} catch (Exception e) {
					logger.error("get host by host id[" + filemodel.getHostId() + "] failed", e);
					return;
				}
				if (host == null) {
					logger.warn("目标主机在数据库中不存在！");
					session.sendMessage(
							new TextMessage(new ObjectMapper().writeValueAsString(new UploadMessage("NOHOST"))));
					return;
				}
				/**
				 * @bug80_begin 上传镜像,仓库镜像暂存空间主目录下根据登录发布平台的操作用户,
				 *              在暂存空间主目录下生成相应的子文件夹
				 * @description 生成本地暂存文件夹的UUID名称，并新建本地UUID文件夹
				 */
				String folder = UUID.randomUUID().toString();
				filemodel.setLocalUuid(folder);
				String cache_folder = System.getProperty("user.dir") + "/" + folder + "/";
				makeDirs(cache_folder);
				/* 将上传的文件保存在UUID命名的文件夹中 */
				filemodel.setFileSrc(cache_folder + filemodel.getFileStartName());
				System.out.println("filemodel.setFileSrc : " + System.getProperty("user.dir") + "/" + folder
						+ "/" + filemodel.getFileStartName());
				/** @bug80_finish */
				/* 设置UUID标识作为上传文件夹保存的路径 */
				filemodel.setUuid(UUID.randomUUID().toString());
				session.getAttributes().put("filemodel", filemodel);
				session.sendMessage(new TextMessage(new ObjectMapper().writeValueAsString(new UploadMessage("OK"))));
			} else if (message.getPayload().contains("sendover")) {
				UpFileModel filemodel = (UpFileModel) session.getAttributes().get("filemodel");
				String result = "FALSE";
				Host host = new Host();
				host.setHostId(filemodel.getHostId());
				try {
					host = hostService.getHost(host);
				} catch (Exception e1) {
					logger.error("get host by host id[" + filemodel.getHostId() + "] failed", e1);
					return;
				}
				SSH ssh = new SSH(host.getHostIp(), host.getHostUser(), host.getHostPwd());
				if (ssh.connect()) {
					/* 获取保存文件的路径 */
					String localPath = filemodel.getFileSrc();
					localPath = localPath.replace("\\", "/");
					/**
					 * @bug79_begin 同一账号多点登录,上传同一镜像文件,仓库暂存目录中意外将2个文件叠加,
					 *              合并为一个无效的大文件
					 * @bug80_begin 需求:上传镜像,仓库暂存空间主目录下根据登录发布平台的操作用户,在该目录生成相应子文件夹
					 */
					String comadString = "cd " + registryConfig.getTempPath() + ";";
					comadString += "mkdir " + filemodel.getUuid();
					try {
						ssh.execute(comadString);
						ssh.close();
					} catch (Exception e) {
						logger.info("文件转存结果：" + e.getMessage());
					}
					ssh.connect();
					boolean pushSuccess = ssh.scpFile(localPath,
							registryConfig.getTempPath() + filemodel.getUuid() + "/");
					/** @bug79_finish @bug80_finish */
					ssh.close();
					logger.info("文件转存结果：" + pushSuccess);
					if (pushSuccess) {
						/**
						 * @bug80_begin 上传镜像,仓库镜像暂存空间主目录下根据登录发布平台的操作用户,
						 *              在暂存空间主目录下生成相应的子文件夹
						 */
						String cache_folder = System.getProperty("user.dir") + "/" + filemodel.getLocalUuid() + "/";
						deleteDirectory(cache_folder);
						/** @bug80_finish */
						result = "TRUE," + filemodel.getUuid();
					}
					/** @bug224_begin:[镜像管理]上传镜像,当转存失败,请将失败的原因返回 **/
					/** 当向目标服务器转存的情况下，转存失败，做报警处理 ***/
					else {
						result = "UNLOADERROR";
					}
					/** @bug224_finish **/
				} else {
					result = "CONNERROR";
				}
				session.sendMessage(new TextMessage(new ObjectMapper().writeValueAsString(new UploadMessage(result))));
			} else if (message.getPayload().contains("cancel")) {
				UpFileModel filemodel = (UpFileModel) session.getAttributes().get("filemodel");
				/**
				 * @bug80_begin 上传镜像,仓库镜像暂存空间主目录下根据登录发布平台的操作用户,
				 *              在暂存空间主目录下生成相应的子文件夹
				 */
				String cache_folder = System.getProperty("user.dir") + "/" + filemodel.getLocalUuid() + "/";
				deleteDirectory(cache_folder);
				/** @bug80_finish */
				/**
				 * @bug110_begin 镜像上传完后,进入"制作"窗口,不制作直接"取消"关闭窗口,
				 *               镜像文件及其临时文件夹在仓库主机上残留
				 ******/
				/** 获取仓库主机保存的目录，取得主机的连接，删除仓库主机暂存文件 */
				Host host = new Host();
				host.setHostId(filemodel.getHostId());
				try {
					host = hostService.getHost(host);
				} catch (Exception e2) {
					logger.error("Get registry host by host id[" + filemodel.getHostId() + "] failed", e2);
					return;
				}
				SSH ssh = new SSH(host.getHostIp(), host.getHostUser(), host.getHostPwd());
				if (ssh.connect()) {
					String regi_temp_path = registryConfig.getTempPath() + filemodel.getUuid() + "/";
					String comadString = "rm -rf " + regi_temp_path + ";";
					try {
						ssh.execute(comadString);
						ssh.close();
						logger.info("删除仓库主机的暂存文件夹成功。");
						/** @bug218_begin:[镜像管理]当镜像文件转存过程中取消操作,显示信息不正常 **/
						session.sendMessage(
								new TextMessage(new ObjectMapper().writeValueAsString(new UploadMessage("DELTMPSUC"))));
						return;
					} catch (Exception e) {
						logger.info("删除文件转存目录结果异常：" + e.getMessage());
						session.sendMessage(
								new TextMessage(new ObjectMapper().writeValueAsString(new UploadMessage("DELTMPEXP"))));
						return;
						/** @bug218_finish **/
					}
				}
				/** @bug110_finish ********/
				session.sendMessage(
						new TextMessage(new ObjectMapper().writeValueAsString(new UploadMessage("CANCEL"))));
				return;
			}
		} catch (JsonParseException e1) {
			logger.error("上传文件异常：", e1);
		} catch (JsonMappingException e1) {
			logger.error("上传文件异常：", e1);
		} catch (IOException e1) {
			logger.error("上传文件异常：", e1);
		}

	}

	/* 新建本地文件夹，并保存 */
	public static boolean makeDirs(String folderName) {
		if (folderName == null || folderName.isEmpty()) {
			return false;
		}
		File folder = new File(folderName);
		System.out.println("Make Dir Temp Save Path is :" + folder.getAbsolutePath());
		return (folder.exists() && folder.isDirectory()) ? true : folder.mkdirs();
	}

	// 处理二进制内容
	private void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
		UpFileModel filemodel = (UpFileModel) session.getAttributes().get("filemodel");
		if (filemodel != null) {
			try {
				if (saveFileFromBytes(message.getPayload(), filemodel.getFileSrc())) {
					session.sendMessage(
							new TextMessage(new ObjectMapper().writeValueAsString(new UploadMessage("OK"))));
				} else {
					session.sendMessage(
							new TextMessage(new ObjectMapper().writeValueAsString(new UploadMessage("FALSE"))));
				}
			} catch (IOException e) {
				logger.error("上传文件异常：", e);
			}
		}
	}

	public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
		if (session.isOpen()) {
			session.close();
		}
		UploadServiceHandller.getCurrentUsers().remove(session);
	}

	public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
		UploadServiceHandller.getCurrentUsers().remove(session);
	}

	public boolean supportsPartialMessages() {
		return false;
	}

	public void sendMessageToUsers(Message message) {
		for (WebSocketSession user : UploadServiceHandller.getCurrentUsers()) {
			try {
				if (user.isOpen()) {
					user.sendMessage(new TextMessage(new ObjectMapper().writeValueAsString(message)));
				}
			} catch (IOException e) {
				logger.error("send message to users failed!", e);
				return;
			}
		}
	}

	public void sendMessageToUser(int userId, Message message) {
		for (WebSocketSession user : UploadServiceHandller.getCurrentUsers()) {
			User iterator = (User) user.getAttributes().get("user");
			if (iterator != null) {
				if (iterator.getUserId() == userId) {
					try {
						if (user.isOpen()) {
							user.sendMessage(new TextMessage(new ObjectMapper().writeValueAsString(message)));
						}
					} catch (IOException e) {
						logger.error("send message to user failed!", e);
						return;
					}
				}
			}
		}
	}

	/**
	 * 将二进制byte[]数组写入文件中
	 * 
	 * @param byteBuffer
	 *            byte[]数组
	 * @param outputFile
	 *            文件位置
	 * @return 成功: true 失败: false
	 */
	private static boolean saveFileFromBytes(ByteBuffer byteBuffer, String outputFile) {
		FileOutputStream fstream = null;
		File file = null;
		try {
			file = new File(outputFile);
			if (!file.exists())
				file.createNewFile();
			fstream = new FileOutputStream(file, true);
			fstream.write(byteBuffer.array());
		} catch (FileNotFoundException fne) {
			logger.error("无法找到创建的文件(" + outputFile + ")", fne);
			return false;
		} catch (Exception e) {
			logger.error("将byte流写入文件保存 异常!", e);
			return false;
		} finally {
			if (fstream != null) {
				try {
					fstream.close();
				} catch (IOException e1) {
					logger.error("将byte流写入文件保存异常!", e1);
				}
			}
		}
		return true;
	}

	public static boolean deleteFile_backup(String outputFile) {
		File file = null;
		try {
			file = new File(outputFile);
			if (file.exists())
				file.delete();

			return true;
		} catch (Exception e) {
			logger.error("delete faile backup failed!", e);
			return false;
		}
	}

	/**
	 * 删除单个文件
	 * 
	 * @param sPath
	 *            被删除文件的文件名
	 * @return 单个文件删除成功返回true，否则返回false
	 */
	private static boolean deleteFile(String sPath) {
		boolean flag = false;
		File file = new File(sPath);
		// 路径为文件且不为空则进行删除
		if (file.isFile() && file.exists()) {
			file.delete();
			flag = true;
		}
		return flag;
	}

	/**
	 * 删除目录（文件夹）以及目录下的文件
	 * 
	 * @param sPath
	 *            被删除目录的文件路径
	 * @return 目录删除成功返回true，否则返回false
	 */
	private static boolean deleteDirectory(String sPath) {
		// 如果sPath不以文件分隔符结尾，自动添加文件分隔符
		if (!sPath.endsWith(File.separator)) {
			sPath = sPath + File.separator;
		}
		File dirFile = new File(sPath);
		// 如果dir对应的文件不存在，或者不是一个目录，则退出
		if (!dirFile.exists() || !dirFile.isDirectory()) {
			return false;
		}
		boolean flag = true;
		// 删除文件夹下的所有文件(包括子目录)
		File[] files = dirFile.listFiles();
		if (files != null) {
			Integer file_length = files.length;
			for (int i = 0; i < file_length; i++) {
				// 删除子文件
				if (files[i].isFile()) {
					flag = deleteFile(files[i].getAbsolutePath());
					if (!flag)
						break;
				} // 删除子目录
				else {
					flag = deleteDirectory(files[i].getAbsolutePath());
					if (!flag)
						break;
				}
			}

		}
		if (!flag)
			return false;
		// 删除当前目录
		if (dirFile.delete()) {
			return true;
		} else {
			return false;
		}
	}
}

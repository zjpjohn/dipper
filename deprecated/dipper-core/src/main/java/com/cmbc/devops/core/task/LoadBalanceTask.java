package com.cmbc.devops.core.task;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;

import com.cmbc.devops.config.LoadBalanceConfig;
import com.cmbc.devops.constant.LoadBalanceConstants;
import com.cmbc.devops.model.HostModel;
import com.cmbc.devops.model.LoadBalanceTemplate;
import com.cmbc.devops.util.SSH;

/**
 * date：2015年9月15日 上午9:14:42 project name：cmbc-devops-core
 * 
 * @author langzi
 * @version 1.0
 * @since JDK 1.7.0_21 file name：LoadBalanceTask.java description：
 */
public class LoadBalanceTask implements Callable<Boolean> {

	private static final Logger LOGGER = Logger.getLogger(LoadBalanceTask.class);

	private LoadBalanceTemplate balanceTemp;

	public LoadBalanceTask(LoadBalanceTemplate balanceTemp) {
		super();
		this.balanceTemp = balanceTemp;
	}

	@Override
	public Boolean call() throws Exception {
		Properties p = new Properties();
		p.setProperty(VelocityEngine.FILE_RESOURCE_LOADER_PATH, balanceTemp.getLocalFile());
		Velocity.init(p);
		VelocityContext context = new VelocityContext();
		// 组合配置信息
		context.put("upstream", balanceTemp.getUpStream());
		context.put("application_location", balanceTemp.getLocation());
		// 加载配置文件
		Template template = null;
		try {
			template = Velocity.getTemplate("nginx_temp.conf");
		} catch (Exception e) {
			LOGGER.error("Get nginx template infos error", e);
			return false;
		}
		// 生成新的配置文件
		File file = new File(balanceTemp.getLocalFile() + "nginx-" + UUID.randomUUID() + ".conf");
		if (!file.exists()) {
			// 不存在则创建新文件
			file.createNewFile();
		}
		// 替换文件内容
		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter writer = new BufferedWriter(fw);
		template.merge(context, writer);
		writer.flush();
		writer.close();
		// 读取服务器信息
		HostModel model = balanceTemp.getHostModel();
		SSH ssh = new SSH(model.getHostIp(), model.getHostUser(), model.getHostPwd());
		// 备份原配置文件
		LOGGER.warn("Server Nginx Config File Path is :" + balanceTemp.getServerConfPath());
		if (!backupBalanceConf(ssh, balanceTemp.getServerConfPath())) {
			LOGGER.warn("Backup nginx conf file failed");
			return false;
		}
		// 拷贝文件
		String localNginxCfg = balanceTemp.getLocalFile() + file.getName();
		String remoteNginxFolder = balanceTemp.getServerConfPath();
		LOGGER.warn(
				"Local Nginx Config File Path is :" + localNginxCfg + " ,Remote Nginx Folder is :" + remoteNginxFolder);
		if (!scpFile(ssh, localNginxCfg, remoteNginxFolder)) {
			LOGGER.warn("Copy nginx conf file failed");
			return false;
		}
		// 验证文件
		if (!verifyBalanceConf(ssh, balanceTemp.getServerConfPath(), file.getName())) {
			// 验证不通过，返回错误
			if (!recoverBalanceConf(ssh, balanceTemp.getServerConfPath())) {
				LOGGER.warn("Recover nginx conf file failed");
				return false;
			}
		}
		// 恢复文件或者更新负载
		if (reloadBalance(ssh)) {
			file.delete();
			return true;
		} else {
			return false;
		}

	}

	/**
	 * @author langzi
	 * @param ssh
	 * @return
	 * @version 1.0 2015年9月15日
	 */
	private boolean backupBalanceConf(SSH ssh, String confFilePath) {
		// 备份文件
		try {
			if (ssh.connect()) {
				String commandStr = "cd " + confFilePath + ";";
				commandStr += "cp nginx.conf nginx_backup.conf;";
				ssh.executeWithResult(commandStr);
				LOGGER.info("Backup nginx conf file success");
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			LOGGER.error("Backup nginx conf file failed", e);
			return false;
		} finally {
			ssh.close();
		}
	}

	/**
	 * @author langzi
	 * @param ssh
	 * @param localFile
	 * @param remotePath
	 * @return
	 * @version 1.0 2015年9月15日
	 */
	public boolean scpFile(SSH ssh, String localFile, String remotePath) {
		try {
			if (ssh.connect()) {
				LOGGER.info(localFile + "-----");
				LOGGER.info(remotePath + "-----");
				return ssh.scpFile(localFile, remotePath);
			} else {
				LOGGER.warn("SSH connect failed");
				return false;
			}
		} catch (Exception e) {
			LOGGER.error("Upload local file to server failed", e);
			return false;
		}
	}

	/**
	 * @author langzi
	 * @param ssh
	 * @return
	 * @version 1.0 2015年9月15日
	 */
	private boolean verifyBalanceConf(SSH ssh, String confFilePath, String fileName) {
		try {
			if (ssh.connect()) {
				String verifyCommand = "cd " + confFilePath + ";";
				verifyCommand += "mv " + fileName + " nginx.conf;";
				verifyCommand += LoadBalanceConfig.getValue(LoadBalanceConstants.VERIFY_COMMAND);
				String result = ssh.executeWithResult(verifyCommand);
				if (result.contains("failed")) {
					LOGGER.info("Nginx file is not correct!");
					return false;
				} else {
					return true;
				}
			} else {
				LOGGER.warn("SSH connect failed");
				return false;
			}
		} catch (Exception e) {
			LOGGER.error("Verify Balance config file failed", e);
			return false;
		} finally {
			ssh.close();
		}
	}

	/**
	 * @author langzi
	 * @param ssh
	 * @return
	 * @version 1.0 2015年9月15日
	 */
	private boolean recoverBalanceConf(SSH ssh, String confFilePath) {
		// 备份文件
		try {
			if (ssh.connect()) {
				String recoverCommand = "cd " + confFilePath + ";";
				recoverCommand += LoadBalanceConfig.getValue(LoadBalanceConstants.RECOVER_COMMAND);
				ssh.executeWithResult(recoverCommand);
				LOGGER.info("Recover nginx conf file success");
				return true;
			} else {
				LOGGER.warn("SSH connect failed");
				return false;
			}
		} catch (Exception e) {
			LOGGER.error("Recover nginx conf file failed", e);
			return false;
		} finally {
			ssh.close();
		}
	}

	/**
	 * @author langzi
	 * @param ssh
	 * @return
	 * @version 1.0 2015年9月15日
	 */
	private boolean reloadBalance(SSH ssh) {
		try {
			if (ssh.connect()) {
				String relaodCommand = LoadBalanceConfig.getValue(LoadBalanceConstants.RELOAD_COMMAND);
				ssh.executeWithResult(relaodCommand);
				LOGGER.info("Reload Balance config file success");
				return true;
			} else {
				LOGGER.warn("SSH connect failed");
				return false;
			}
		} catch (Exception e) {
			LOGGER.error("Relaod Balance config file failed", e);
			return false;
		} finally {
			ssh.close();
		}
	}

	public static void main(String[] args) {
		System.out.println(LoadBalanceTask.class.getClassLoader().getResource("nginx_temp.conf"));
	}

}

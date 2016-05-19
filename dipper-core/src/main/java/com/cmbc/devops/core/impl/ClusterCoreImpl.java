package com.cmbc.devops.core.impl;

import java.io.IOException;
import java.net.SocketException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cmbc.devops.config.SystemConfig;
import com.cmbc.devops.constant.ServerCommand;
import com.cmbc.devops.constant.Type;
import com.cmbc.devops.core.ClusterCore;
import com.cmbc.devops.model.ClusterModel;
import com.cmbc.devops.util.CommandExcutor;
import com.cmbc.devops.util.SSH;

/**
 * @author luogan 2015年8月17日 下午4:34:37
 */
@Component
public class ClusterCoreImpl implements ClusterCore {
	private static final Logger LOGGER = Logger.getLogger(ClusterCore.class);

	@Autowired
	private SystemConfig config;

	@Override
	public boolean createSwarm(String ip, String username, String password, ClusterModel model) {
		SSH ssh = CommandExcutor.getSsh(ip, username, password);
		String port = model.getClusterPort();
		// swarm集群的管理Docker节点方式，暂时支持配置文件和zookeeper方式
		String discover = "", logFile = "";
		try {
			if (!ssh.connect()) {
				return false;
			}
		} catch (SocketException e) {
			LOGGER.error("ssh connect error:", e);
		} catch (IOException e) {
			LOGGER.error("ssh connect error:", e);
		}
		// 配置文件方式
		if (model.getClusterMode() == Type.CLUSTER_MODE.CONFIG.ordinal()) {
			discover = "/home/" + username + "/" + model.getManagePath();
			// 添加配置文件
			String addFileCommand = "echo '' > " + discover;
			boolean result = CommandExcutor.executeCommand(ssh, addFileCommand);
			if (!result) {
				return false;
			}
			String directory = discover.substring(0, discover.lastIndexOf("/"));
			logFile = directory + "/swarm_" + port + ".log";
			discover = "file://" + discover;
		} else {// zookeeper服务发现方式
			discover = config.getZkServer() + port;
			logFile = "/home/" + username + "/" + "swarm_" + port + ".log";
		}
		return CommandExcutor.executeCommand(ssh, ServerCommand.createSwarm(ip, port, discover, logFile));
	}

	@Override
	public boolean removeSwarm(String ip, String username, String password, String port, String file) {
		SSH ssh = CommandExcutor.getSsh(ip, username, password);
		String stopSwarmCommand = ServerCommand.stopSwarm(port);
		stopSwarmCommand += "; rm -rf /home/" + username + "/" + file + ";";
		stopSwarmCommand += "rm -rf /home/" + username + "/swarm_" + port + ".log;";
		LOGGER.info(stopSwarmCommand);
		return CommandExcutor.executeCommand(ssh, stopSwarmCommand);
	}

	@Override
	public Boolean recoverSwarm(String ip, String username, String password, String port, String file) {
		SSH ssh = CommandExcutor.getSsh(ip, username, password);
		// 1.判断swarm集群是否正常
		// 2.恢复swarm集群
		String recoverCommand = "nohup swarm manage -H tcp://" + ip + ":" + port + " file:///home/" + username + "/"
				+ file + " >> /home/" + username + "/swarm_" + port + ".log 2>&1 &";
		LOGGER.info(recoverCommand);
		return CommandExcutor.executeCommand(ssh, recoverCommand);
	}

	@Override
	public boolean checkPort(String ip, String username, String password, String port) {
		SSH ssh = CommandExcutor.getSsh(ip, username, password);
		String execResult = CommandExcutor.executeCommandWithResult(ssh, ServerCommand.checkPort(port));
		String expectResult = "(No info could be read for " + "\"" + "-p" + "\""
				+ ": geteuid()=1000 but you should be root.)";
		if (execResult.isEmpty() || expectResult.equals(execResult.substring(0, execResult.length() - 1))) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public String clusterHealthCheck(String ip, String username, String password, String port) {
		SSH ssh = CommandExcutor.getSsh(ip, username, password);
		return CommandExcutor.executeCommandWithResult(ssh, ServerCommand.healthCheckSwarm(ip, port));
	}

}

package com.cmbc.devops.core.impl;

import java.io.IOException;
import java.net.SocketException;
import java.util.Random;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.cmbc.devops.bean.Result;
import com.cmbc.devops.config.SystemConfig;
import com.cmbc.devops.constant.ServerCommand;
import com.cmbc.devops.constant.Type;
import com.cmbc.devops.core.HostCore;
import com.cmbc.devops.util.CommandExcutor;
import com.cmbc.devops.util.SSH;

/**
 * @author luogan 2015年8月17日 下午4:34:49
 */
@Component
public class HostCoreImpl implements HostCore {

	private static final Logger LOGGER = Logger.getLogger(HostCoreImpl.class);

	@Autowired
	private SystemConfig config;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cmbc.devops.core.HostCore#getHostInfos(com.cmbc.devops.util.SSH)
	 */
	@Override
	public Result getHostInfo(SSH ssh, String ipAddr) {
		String hostInfos = "";
		Result result = new Result(false, "");
		hostInfos = CommandExcutor.executeCommandWithResult(ssh, ServerCommand.queryHostInfosCommand());
		if ((hostInfos.contains("false")) || (hostInfos.contains("failed"))) {
			result.setMessage("获取主机(" + ipAddr + ")链接失败，请检查网络是否畅通");
			return result;
		}
		result.setSuccess(true);
		result.setMessage(hostInfos);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cmbc.devops.core.HostCore#addHostToCluster(com.cmbc.devops.util.SSH,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public Result addHostToCluster(SSH ssh, String hostUser, String file, String ipRoutes, int clusterMode,
			String clusterPort, String logFile) {
		Result result = new Result(false, "");
		if (clusterMode == Type.CLUSTER_MODE.ZOOKEEPER.ordinal()) {
			String[] hostRoutes = ipRoutes.split("\n");
			String discover = config.getZkServer() + clusterPort;
			int hostNums = hostRoutes.length, successHostNums = 0;
			logFile = "/home" + "/" + hostUser + "/" + logFile;
			for (String route : hostRoutes) {
				String[] ipPort = route.split(":");
				String ip = ipPort[0];
				String port = ipPort[1];
				String joinCommand = ServerCommand.joinSwarm(ip, port, discover, logFile);
				try {
					String execResult = CommandExcutor.executeCommandWithResult(ssh, joinCommand);
					LOGGER.info(execResult);
					if ("false".equals(execResult)) {
						LOGGER.error("Get cluster host connection failed");
						continue;
					} else if (StringUtils.hasText(execResult) && !"false".equals(execResult)) {
						LOGGER.error(execResult);
						return result;
					}
					successHostNums++;
				} catch (Exception e) {
					LOGGER.error("Host join in swarm cluster error", e);
					continue;
				}
			}
			if (hostNums == successHostNums) {
				result.setSuccess(true);
			} else if (successHostNums != hostNums && successHostNums > 0) {
				result.setMessage("部分主机加入集群成功！");
			} else {
				result.setMessage("主机加入集群失败");
			}
			return result;
		} else {
			String configFile = "/home" + "/" + hostUser + "/" + file;
			StringBuilder command = new StringBuilder();
			command.append("echo -e").append(" '").append(ipRoutes).append("'").append(">>").append(configFile);
			String execResult = "";
			try {
				execResult = CommandExcutor.executeCommandWithResult(ssh, command.toString());
			} catch (Exception e) {
				LOGGER.error("Update cluster config file error", e);
				result.setMessage("主机加入集群时出现异常！");
				return result;
			}
			if ("false".equals(execResult)) {
				LOGGER.error("Get cluster host connection failed");
				result.setMessage("主机加入集群失败：未获取集群服务器连接");
			} else if (StringUtils.hasText(execResult) && !"false".equals(execResult)) {
				LOGGER.error(execResult);
				result.setMessage("主机加入集群异常：写入配置文件失败");
				return result;
			}
			result.setSuccess(true);
			return result;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cmbc.devops.core.HostCore#removeHostFromCluster(com.cmbc.devops.util.
	 * SSH, java.lang.String, java.lang.String)
	 */
	@Override
	public Result removeHostFromCluster(SSH ssh, String hostUser, String file, String ipRoutes) {
		Result result = new Result(false, "");
		String swarmCfgPath = "/home" + "/" + hostUser + "/" + file;
		StringBuilder command = new StringBuilder();
		command.append("cd " + "/home/" + hostUser + ";").append("true > ").append(file + ";").append("echo -e")
				.append(" '").append(ipRoutes).append("'").append(">>").append(swarmCfgPath);
		LOGGER.info(command.toString());
		String execResult = "";
		try {
			execResult = CommandExcutor.executeCommandWithResult(ssh, command.toString());
		} catch (Exception e) {
			LOGGER.error("Connect host and remove cluster config fail", e);
			result.setMessage("主机离开集群时出现异常！");
			return result;
		}
		if ("false".equals(execResult)) {
			result.setMessage("集群解绑主机失败：未获取到与集群服务器的连接");
			return result;
		} else if (StringUtils.hasText(execResult) && !"false".equals(execResult)) {
			result.setMessage("集群解绑主机失败：写入配置文件失败!");
			LOGGER.error(execResult);
			return result;
		}
		result.setSuccess(true);
		result.setMessage("集群解绑主机成功！");
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cmbc.devops.core.HostCore#removeHostFromCluster(com.cmbc.devops.util.
	 * SSH, java.lang.String)
	 */
	@Override
	public Result removeHostFromCluster(SSH ssh, String ipRoutes) {
		Result result = new Result(false, "");
		String execResult = "";
		String[] routes = ipRoutes.split("\n");
		int succNum = 0;
		for (String route : routes) {
			try {
				execResult = CommandExcutor.executeCommandWithResult(ssh, ServerCommand.unbundingHostFromSwarm(route));
			} catch (Exception e) {
				LOGGER.error("Connect host and remove cluster config fail", e);
				continue;
			}
			if ("false".equals(execResult)) {
				continue;
			} else if (StringUtils.hasText(execResult) && !"false".equals(execResult)) {
				result.setMessage("集群解绑主机失败：主机离开集群未知错误！");
				LOGGER.error(execResult);
				continue;
			}
			succNum++;
		}
		// 有问题
		if (succNum == routes.length) {
			result.setSuccess(true);
			result.setMessage("集群解绑主机成功！");
		}
		return result;
	}

	@Override
	public String getFreePort(SSH ssh) {
		String port = "";
		Random random = new Random();
		boolean getResult = false;
		while (!getResult) {
			int tip = random.nextInt(1_0000);
			port = (3_0000 + tip) + "";
			try {
				if (ssh.connect()) {
					String portStatus = CommandExcutor.executeCommandWithResult(ssh, ServerCommand.checkPort(port));
					if (StringUtils.hasText(portStatus)) {
						continue;
					} else {
						getResult = true;
					}
				} else {
					break;
				}
			} catch (SocketException e) {
				LOGGER.error("SocketException get ssh failed", e);
				return "exception";
			} catch (IOException e) {
				LOGGER.error("IOException get ssh failed", e);
				return "exception";
			}

			try {
				/** 休眠50毫秒 */
				Thread.sleep(50);
			} catch (InterruptedException e) {
				LOGGER.error("InterruptedException sleep failed", e);
				return "exception";
			}
		}

		return port;
	}

}

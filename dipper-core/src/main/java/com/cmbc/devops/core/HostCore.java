package com.cmbc.devops.core;

import java.net.ConnectException;

import com.cmbc.devops.bean.Result;
import com.cmbc.devops.util.SSH;

/**
 * @author luogan 2015年8月17日 下午2:44:04
 */
public interface HostCore {

	/**
	 * @author langzi
	 * @param ssh
	 * @return
	 * @version 1.0 2015年10月23日
	 * @throws ConnectException
	 */
	public Result getHostInfo(SSH ssh, String ipAddr);

	/**
	 * @author langzi
	 * @param ssh
	 * @param filePath
	 * @param ipRoutes
	 * @return
	 * @version 1.0 2015年10月23日
	 */
	public Result addHostToCluster(SSH ssh, String hostUser, String file, String ipRoutes, int clusterMode,
			String clusterPort, String logFile);

	/**
	 * @author langzi
	 * @param ssh
	 * @param hostUser
	 * @param file
	 * @param ipRoutes
	 *            剩余主机的ip和port，格式[ip:port,...,...]
	 * @return
	 * @version 1.0 2016年3月8日
	 */
	public Result removeHostFromCluster(SSH ssh, String hostUser, String file, String ipRoutes);

	/**
	 * @author langzi
	 * @param ssh
	 * @param hostUser
	 * @param file
	 * @param ipRoutes
	 *            要解绑的主机的ip加port，格式[ip:port,...,...]
	 * @return
	 * @version 1.0 2016年3月8日 主机解绑集群，（服务注册的方式）
	 */
	public Result removeHostFromCluster(SSH ssh, String ipRoutes);

	/** 向远程主机请求空闲的端口号 */
	public String getFreePort(SSH ssh);

}

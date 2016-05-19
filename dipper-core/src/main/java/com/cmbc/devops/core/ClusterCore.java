package com.cmbc.devops.core;

import com.cmbc.devops.model.ClusterModel;

/**
 * @author luogan
 * 2015年8月17日
 * 下午2:43:54
 */
public interface ClusterCore{
	
	/**
	 * @author luogan
	 * @param model
	 * @return
	 * @version 1.0
	 * 2015年8月25日
	 */
	public boolean createSwarm(String ip, String username, String password, ClusterModel model);
	
	
	/**
	 * 检查docker的信息
	 * @author lining
	 * @param hostIp, hostpwd
	 * @return hostUuid
	 */
	public boolean removeSwarm(String ip, String username, String password, String port, String file);
	
	/**
	 * @author langzi
	 * @param ip
	 * @param account
	 * @param password
	 * @param port
	 * @return
	 * @version 1.0
	 * 2016年1月19日
	 * @throws ConnectException 
	 */
	public String clusterHealthCheck(String ip, String username, String password, String port);
	
	/**
	 * @author langzi
	 * @param ip
	 * @param user
	 * @param password
	 * @param port
	 * @param file
	 * @return
	 * @version 1.0
	 * 2015年10月15日
	 */
	public Boolean recoverSwarm(String ip, String username, String password, String port, String file);
	
	/**
	 * @author langzi
	 * @param ip
	 * @param user
	 * @param password
	 * @param port
	 * @return
	 * @version 1.0
	 * 2015年10月19日
	 * @throws ConnectException 
	 */
	public boolean checkPort(String ip, String username, String password, String port) ;

}

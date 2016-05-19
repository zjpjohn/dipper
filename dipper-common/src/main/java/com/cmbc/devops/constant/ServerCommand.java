package com.cmbc.devops.constant;

import org.springframework.beans.factory.annotation.Autowired;

import com.cmbc.devops.config.SystemConfig;

/**  
 * date：2015年8月17日 下午3:19:55  
 * project name：cmbc-devops-common  
 * @author langzi  
 * @version 1.0   
 * @since JDK 1.7.0_21  
 * file name：SshCommand.java  
 * description：  
 */
public final class ServerCommand {
	
	private ServerCommand() {
	}
	
	public final static String COMMAND_MOUNTALL = "/bin/mount -a";
	
	public final static String COMMAND_MAKEDIR = "mkdir -p ";
	
	public final static String COMMAND_MOVE = "mv -f ";
	
	public final static String COMMAND_COPY = "\\cp -r ";
	
	@Autowired
	public static SystemConfig config;
	
	/**
	 * @author langzi
	 * @return
	 * @version 1.0
	 * 2016年1月19日
	 * Get host infos
	 */
	public final static String queryHostInfosCommand(){
		return "uname -a|awk '{print \"hostName:\"$2 \"\\n\" \"Kernel:\"$3}';cat /proc/cpuinfo |grep 'processor'|wc -l|awk '{print \"CPU:\"$1}';free|grep Mem|awk '{print \"MEM:\"$2/1024\"MiB\"}';";
	}
	
	public final static String createSwarm(String ip,String port,String discover, String logFile){
		return "nohup swarm manage -H tcp://"+ip+":"+port+" "+discover+" > "+logFile+" 2>&1 &";
	}
	
	public final static String joinSwarm(String ip,String port,String discover, String logFile){
		return "nohup swarm join --addr "+ip+":"+port+" "+discover+" > "+logFile+ " 2>&1 &";
	}
	
	public final static String unbundingHostFromSwarm(String ipRoute){
		return "kill -9 $(ps aux | grep swarm | grep join | grep "+ipRoute+" | awk '{print $2}')";
	}
	
	public final static String stopSwarm(String port) {
		return "kill -9 $(lsof -i :"+port+" | awk '{print $2}' | grep -v PID)";
	}
	
	public final static String healthCheckSwarm(String ip,String port) {
		return "docker -H tcp://"+ip+":"+port+" info|grep [0-9].[0-9].[0-9].[0-9]|awk '{print $2}'|awk -F ':' '{print $1}'";
	}
	
	public final static String checkPort(String port) {
		return "netstat -nlptu | awk '{print $4}' | grep "+port;
	}
}

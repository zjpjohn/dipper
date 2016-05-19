package com.cmbc.devops.webservice;

import org.apache.cxf.endpoint.Client;

/**  
 * date：2015年11月6日 上午11:46:21  
 * project name：cmbc-devops-core  
 * @author langzi  
 * @version 1.0   
 * @since JDK 1.7.0_21  
 * file name：ApplicationServiceClient.java  
 * description：  
 */
public interface ApplicationClientBuilder {
	
	public static String HEALTH_PATH = "/com.cmbc.dap.core.health.HealthManager?wsdl";
	
	public static String MONITOR_PATH = "/com.cmbc.dap.monitor.DynamicItemsAndTriggers?wsdl";
	
	/**
	 * @author langzi
	 * @return
	 * @throws Exception
	 * @version 1.0
	 * 2015年11月16日
	 */
	public abstract Client createClient(String ip, String port, String method, boolean firstCreated);
	
	
	/**
	 * @author langzi
	 * @param client
	 * @param methodName
	 * @return
	 * @throws Exception
	 * @version 1.0
	 * 2015年11月16日
	 */
	public abstract Object[] invoke(Client client, String methodName) throws Exception;
	
	/**
	 * @author langzi
	 * @return
	 * @throws Exception
	 * @version 1.0
	 * 2015年11月16日
	 */
	public abstract void destroyClinet(Client client);
	
}

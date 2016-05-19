package com.cmbc.dap.monitor.request;
/**  
 * date：2015年12月3日 下午4:27:37  
 * project name：cmbc-devops-core  
 * @author langzi  
 * @version 1.0   
 * @since JDK 1.7.0_21  
 * file name：ProxyRequest.java  
 * description：  
 */
public interface ProxyRequest {
	
	/**
	 * @author langzi
	 * @param proxyName
	 * @return
	 * @version 1.0
	 * 2015年12月3日
	 */
	public abstract Request getProxyRequest(String proxyName);
	
}

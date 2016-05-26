package com.cmbc.dap.monitor.request;


/**  
 * date：2015年11月17日 上午11:44:41  
 * project name：cmbc-devops-core  
 * @author langzi  
 * @version 1.0   
 * @since JDK 1.7.0_21  
 * file name：TemplateMonitor.java  
 * description：  
 */
public interface TemplateRequest {
	
	/**
	 * @author langzi
	 * @param tempName
	 * @return
	 * @version 1.0
	 * 2015年11月17日
	 */
	public abstract Request getTempRequest(String tempName);
	
}

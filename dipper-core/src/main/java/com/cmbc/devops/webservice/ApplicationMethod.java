package com.cmbc.devops.webservice;
/**  
 * date：2015年11月16日 上午11:25:07  
 * project name：cmbc-devops-core  
 * @author langzi  
 * @version 1.0   
 * @since JDK 1.7.0_21  
 * file name：ApplicationMethod.java  
 * description：  
 */
public interface ApplicationMethod {
	
	/**
	 * @author langzi
	 * @return
	 * @version 1.0
	 * 2015年11月16日
	 */
	public abstract String healthCheck();
	
	/**
	 * @return
	 */
	public abstract String isApplicationStarted();
	
	/**
	 * @author langzi
	 * @return
	 * @version 1.0
	 * 2015年11月27日
	 */
	public abstract String monitorList();
	
}

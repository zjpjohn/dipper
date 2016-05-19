package com.cmbc.devops.webservice.impl;

import org.springframework.stereotype.Component;

import com.cmbc.devops.webservice.ApplicationMethod;

/**  
 * date：2015年11月16日 上午11:26:31  
 * project name：cmbc-devops-core  
 * @author langzi  
 * @version 1.0   
 * @since JDK 1.7.0_21  
 * file name：ApplicationMethodImpl.java  
 * description：  
 */
@Component("applicationMethod")
public class ApplicationMethodImpl implements ApplicationMethod {

	/* (non-Javadoc)
	 * @see com.cmbc.devops.webservice.ApplicationMethod#healthCheck()
	 */
	@Override
	public String healthCheck() {
		return "healthcheck";
	}

	@Override
	public String monitorList() {
		return "getDynamicItemsAndTriggers";
	}

	@Override
	public String isApplicationStarted() {
		return "isApplicationStarted";
	}
	
	

}

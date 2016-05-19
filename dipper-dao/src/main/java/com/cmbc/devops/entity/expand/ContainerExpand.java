package com.cmbc.devops.entity.expand;

import com.cmbc.devops.entity.Container;

/**  
 * date：2015年8月27日 上午11:21:03  
 * project name：cmbc-devops-dao  
 * @author langzi  
 * @version 1.0   
 * @since JDK 1.7.0_21  
 * file name：ContainerExpand.java  
 * description：  
 */
public class ContainerExpand extends Container {
	private String[] conUuids;

	public String[] getConUuids() {
		return conUuids;
	}

	public void setConUuids(String[] conUuids) {
		this.conUuids = conUuids;
	}
	
}

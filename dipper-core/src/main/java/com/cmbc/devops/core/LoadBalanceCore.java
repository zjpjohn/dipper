package com.cmbc.devops.core;

import java.util.List;

import com.cmbc.devops.model.LoadBalanceTemplate;

/**  
 * date：2015年9月11日 上午10:59:53  
 * project name：cmbc-devops-core  
 * @author langzi  
 * @version 1.0   
 * @since JDK 1.7.0_21  
 * file name：LoadBalanceCore.java  
 * description：  
 */
public interface LoadBalanceCore {
	
	/**
	 * @author langzi
	 * @param balanceParams
	 * @return
	 * @version 1.0
	 * @throws Exception
	 *
	 * 2015年9月11日
	 */
	public Integer reloadBalance(List<LoadBalanceTemplate> balanceTemps);
	
}

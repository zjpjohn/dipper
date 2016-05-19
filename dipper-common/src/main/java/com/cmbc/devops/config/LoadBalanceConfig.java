package com.cmbc.devops.config;

import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.cmbc.devops.constant.LoadBalanceConstants;

/**  
 * date：2015年9月15日 上午9:25:13  
 * project name：cmbc-devops-core  
 * @author langzi  
 * @version 1.0   
 * @since JDK 1.7.0_21  
 * file name：LoadBalanceConfig.java  
 * description：  
 */
public final class LoadBalanceConfig {
	
	private LoadBalanceConfig() {
	}
	
	private static final Logger LOGGER = Logger.getLogger(LoadBalanceConfig.class);
	
	public static final Properties PROPS = new Properties();
	
	static {
		try {
			PROPS.load(LoadBalanceConfig.class.getResourceAsStream(LoadBalanceConstants.BALANCE_CONFIG));
		} catch (IOException e) {
			LOGGER.error("Read config file failed", e);
		}
	}
	
	/**
	 * result is null for the below conditions:
	 *  (1) the key is null
	 *  (2) the key is not founded
	 * 
	 * @param key
	 * @return 
	 */
	public static String getValue(String key) {
		return (key == null) ? null : (String) PROPS.get(key);
	}
}

package com.cmbc.devops.constant;
/**  
 * date：2015年9月15日 上午9:24:53  
 * project name：cmbc-devops-core  
 * @author langzi  
 * @version 1.0   
 * @since JDK 1.7.0_21  
 * file name：LoadBalanceConstants.java  
 * description：  
 */
public final class LoadBalanceConstants {
	
	private LoadBalanceConstants() {
	}
	
	public final static String BALANCE_CONFIG = "/com/cmbc/devops/config/nginx.config";
	
	public final static String BALANCE_TEMP = "balance_temp";
	
	public final static String BALANCE_FILE = "balance_file";
	
	public final static String CP_COMMAND = "cp_command";
	
	public final static String VERIFY_COMMAND = "verify_command";
	
	public final static String RECOVER_COMMAND = "recover_command";
	
	public final static String RELOAD_COMMAND = "reload_command";
			
}

package com.cmbc.devops.component;

import java.util.Locale;
import java.util.ResourceBundle;

/**  
 * date：2015年8月12日 下午4:02:57  
 * project name：cmbc-devops-common  
 * @author langzi  
 * @version 1.0   
 * @since JDK 1.7.0_21  
 * file name：MessageSourceComponent.java  
 * description：  
 */
public class MessageSourceComponent {
	
	/**
	 * @author langzi
	 * @version 1.0
	 * @return
	 * 2015年8月12日
	 */
	public static MessageSourceComponent getInstance(){
		return new MessageSourceComponent();
	}
	
	/**
	 * @author langzi
	 * @param code
	 * @return
	 * 2015年8月12日
	 */
	public String getMessage(String code){    	 
	   	ResourceBundle rb = ResourceBundle.getBundle("com/cmbc/devops/config/message", Locale.CHINA);
	   	return rb.getString(code);
    } 
	
}

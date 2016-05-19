package com.cmbc.devops.message;
/**  
 * date：2015年8月17日 下午3:49:26  
 * project name：cmbc-devops-web  
 * @author langzi  
 * @version 1.0   
 * @since JDK 1.7.0_21  
 * file name：MessageUtilities.java  
 * description：  
 */
public class MessageUtilities {
	
	/**
	 * return success message
	 * @author langzi
	 * @param msg
	 * @return
	 * @version 1.0
	 * 2015年8月17日
	 */
	public static String stickyToSuccess(String msg) {
		return "<div class='alert alert-success'>" + msg + "</div>";
	}

	/**
	 * return info message
	 * @author langzi
	 * @param msg
	 * @return
	 * @version 1.0
	 * 2015年8月17日
	 */
	public static String stickyToInfo(String msg) {
		return "<div class='alert alert-info'>" + msg + "</div>";
	}

	/**
	 * return error message
	 * @author langzi
	 * @param msg
	 * @return
	 * @version 1.0
	 * 2015年8月17日
	 */
	public static String stickyToError(String msg) {
		return "<div class='alert alert-danger'>" + msg + "</div>";
	}

	/**
	 * return warning message
	 * @author langzi
	 * @param msg
	 * @return
	 * @version 1.0
	 * 2015年8月17日
	 */
	public static String stickyToWarning(String msg) {
		return "<div class='alert alert-warning'>" + msg + "</div>";
	}
}

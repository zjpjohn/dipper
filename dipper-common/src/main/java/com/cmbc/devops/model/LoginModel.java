package com.cmbc.devops.model;
/**  
 * date：2015年8月17日 上午9:45:00  
 * project name：cmbc-devops-common  
 * @author langzi  
 * @version 1.0   
 * @since JDK 1.7.0_21  
 * file name：LoginModel.java  
 * description：  
 */
public class LoginModel {
	
	private String userName;
	private String password;
	private String vercode;
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getVercode() {
		return vercode;
	}
	public void setVercode(String vercode) {
		this.vercode = vercode;
	}
	
}

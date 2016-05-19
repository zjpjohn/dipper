package com.cmbc.devops.model;

import java.util.List;

/**  
 * date：2015年8月17日 上午10:18:29  
 * project name：cmbc-devops-common  
 * @author langzi  
 * @version 1.0   
 * @since JDK 1.7.0_21  
 * file name：RegisterModel.java  
 * description：  
 */
public class RegisterModel {
	
	private Object object;
	private String methodName;
	private List<Class<?>> paramsList;

	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public List<Class<?>> getParamsList() {
		return paramsList;
	}

	public void setParamsList(List<Class<?>> paramsList) {
		this.paramsList = paramsList;
	}
}

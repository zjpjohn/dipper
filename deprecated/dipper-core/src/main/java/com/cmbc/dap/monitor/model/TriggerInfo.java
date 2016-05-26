package com.cmbc.dap.monitor.model;

import java.io.Serializable;

/**  
 * date：2015年11月17日 上午10:01:29  
 * project name：cmbc-devops-core  
 * @author langzi  
 * @version 1.0   
 * @since JDK 1.7.0_21  
 * file name：TriggerInfo.java  
 * description：  
 */
public class TriggerInfo implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	//告警描述信息
	private String description;
	//告警表达式（key值）
	private String expression;
	//报警级别
	private Integer priority;
	/*Possible values are: 
		0 - (default) not classified; 
		1 - information; 
		2 - warning; 
		3 - average; 
		4 - high; 
		5 - disaster.*/
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getExpression() {
		return expression;
	}
	public void setExpression(String expression) {
		this.expression = expression;
	}
	public Integer getPriority() {
		return priority;
	}
	public void setPriority(Integer priority) {
		this.priority = priority;
	}
	
	
}

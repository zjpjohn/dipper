/**
 * Copyright 2016-2016 Institute of Software, Chinese Academy of Sciences.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.once.crosscloud.util;

import java.io.Serializable;

/**
 * @author wuheng(wuheng@otcaix.iscas.ac.cn)
 * @date   Jun 9, 2016
 */
public class PageUtil implements Serializable{
	/*
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/*
     * 页码，从1开始
     */
    private int pageNum;
    /*
     * 页面大小
     */
    private int pageSize;
    /*
     * 排序字段
     */
    private String orderByColumn;
	/*
	 * 排序方式
	 */
    private String orderByType;
    
	public int getPageNum() {
		return pageNum;
	}
	
	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}
	
	public int getPageSize() {
		return pageSize;
	}
	
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	
	public String getOrderByColumn() {
		return orderByColumn;
	}
	
	public void setOrderByColumn(String orderByColumn) {
		this.orderByColumn = orderByColumn;
	}
	
	public String getOrderByType() {
		return orderByType;
	}
	
	public void setOrderByType(String orderByType) {
		this.orderByType = orderByType;
	}
	
}

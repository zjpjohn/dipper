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
package com.once.crosscloud.security.utils;

import java.util.Map;

/**
 * @author wuheng(wuheng@otcaix.iscas.ac.cn)
 * @date 2016年6月9日
 *
 */
public class Column {
	
	/**
	 * 编号
	 */
	private String id;
	
	/**
	 * 是否参与高级查询
	 */
	private String search;
	
	/**
	 * 是否作为导出列导出[default:true]
	 */
	private boolean export = true;
	
	/**
	 * 是否作为打印列打印[default:true]
	 */
	private boolean print = true;
	
	/**
	 * 是否作为扩展列隐藏备用[default:true(对于自定义的复选或相关操作内容，请设置为false以免数据冲突)]
	 */
	private boolean extra = true;
	
	/**
	 * 显示的列名
	 */
	private String title;
	
	/**
	 * 数据类型
	 */
	private String type;
	
	/**
	 * 格式化
	 */
	private String format;
	
	/**
	 * 原始数据类型
	 */
	private String otype;
	
	/**
	 * 原始格式
	 */
	private String oformat;
	
	/**
	 * 码表映射，用于高级查询及显示
	 */
	private Map<String, Object> codeTable;
	
	/**
	 * 列样式
	 */
	private String columnStyle;
	
	/**
	 * 列样式表
	 */
	private String columnClass;
	
	/**
	 * 列头样式
	 */
	private String headerStyle;
	
	/**
	 * 列头样式表
	 */
	private String headerClass;
	
	/**
	 * 彻底隐藏
	 */
	private boolean hide = false;
	
	/**
	 * 隐藏类别
	 */
	private String hideType;
	
	/**
	 * 快速查询
	 */
	private boolean fastQuery;
	
	/**
	 * 快速查询类别
	 */
	private String fastQueryType;
	
	/**
	 * 高级查询
	 */
	private boolean advanceQuery;
	
	/**
	 * 回调方法，参数：record value
	 */
	private String resolution;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSearch() {
		return search;
	}

	public void setSearch(String search) {
		this.search = search;
	}

	public boolean isExport() {
		return export;
	}

	public void setExport(boolean export) {
		this.export = export;
	}

	public boolean isPrint() {
		return print;
	}

	public void setPrint(boolean print) {
		this.print = print;
	}

	public boolean isExtra() {
		return extra;
	}

	public void setExtra(boolean extra) {
		this.extra = extra;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getOtype() {
		return otype;
	}

	public void setOtype(String otype) {
		this.otype = otype;
	}

	public String getOformat() {
		return oformat;
	}

	public void setOformat(String oformat) {
		this.oformat = oformat;
	}

	public Map<String, Object> getCodeTable() {
		return codeTable;
	}

	public void setCodeTable(Map<String, Object> codeTable) {
		this.codeTable = codeTable;
	}

	public String getColumnStyle() {
		return columnStyle;
	}

	public void setColumnStyle(String columnStyle) {
		this.columnStyle = columnStyle;
	}

	public String getColumnClass() {
		return columnClass;
	}

	public void setColumnClass(String columnClass) {
		this.columnClass = columnClass;
	}

	public String getHeaderStyle() {
		return headerStyle;
	}

	public void setHeaderStyle(String headerStyle) {
		this.headerStyle = headerStyle;
	}

	public String getHeaderClass() {
		return headerClass;
	}

	public void setHeaderClass(String headerClass) {
		this.headerClass = headerClass;
	}

	public boolean isHide() {
		return hide;
	}

	public void setHide(boolean hide) {
		this.hide = hide;
	}

	public String getHideType() {
		return hideType;
	}

	public void setHideType(String hideType) {
		this.hideType = hideType;
	}

	public boolean isFastQuery() {
		return fastQuery;
	}

	public void setFastQuery(boolean fastQuery) {
		this.fastQuery = fastQuery;
	}

	public String getFastQueryType() {
		return fastQueryType;
	}

	public void setFastQueryType(String fastQueryType) {
		this.fastQueryType = fastQueryType;
	}
	
	public boolean isAdvanceQuery() {
		return advanceQuery;
	}

	public void setAdvanceQuery(boolean advanceQuery) {
		this.advanceQuery = advanceQuery;
	}

	public String getResolution() {
		return resolution;
	}

	public void setResolution(String resolution) {
		this.resolution = resolution;
	}

}

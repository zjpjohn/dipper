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
package com.once.crosscloud.utils;

/**
 * @author wuheng(wuheng@otcaix.iscas.ac.cn)
 * @date   Jun 9, 2016
 */
public class Sort {
	
	/**
	 * 字段
	 */
	private String field;
	
	/**
	 * 排序逻辑
	 */
	private String logic;

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getLogic() {
		return logic;
	}

	public void setLogic(String logic) {
		this.logic = logic;
	}
	
}

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
package com.once.crosscloud.security.models;

import java.util.Date;

/**
 * @author wuheng(wuheng@otcaix.iscas.ac.cn)
 * @date   Jun 13, 2016
 *
 */
public class UserInfoEntity extends BaseEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer sex;
	
	/*
	 * 处理日期转换
	 */
	private Date birthday;
	
	private String telephone;
	
	private String email;
	
	private String address;
	
	private Date createTime;

	public Integer getSex() {
		return sex;
	}

	public void setSex(Integer sex) {
		this.sex = sex;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	@Override
	public String toString() {
		return "UserInfo [id=" + id + ", sex=" + sex + ", birthday=" + birthday
				+ ", telephone=" + telephone + ", email=" + email
				+ ", address=" + address + ", createTime=" + createTime + "]";
	}
	
	
	
}

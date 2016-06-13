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
package com.once.crosscloud.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.once.crosscloud.base.baseservice.impl.AbstractService;
import com.once.crosscloud.exceptions.ServiceException;
import com.once.crosscloud.mappers.UserMapper;
import com.once.crosscloud.models.UserEntity;
import com.once.crosscloud.services.UserService;
import com.once.crosscloud.util.EmailUtil;

/**
 * @author wuheng(wuheng@otcaix.iscas.ac.cn)
 * @date   Jun 13, 2016
 *
 */
@Service("userService")
public class UserServiceImpl extends AbstractService<UserEntity, Long> implements UserService{

	@Autowired
	private UserMapper userMapper;
	
	@Autowired
	private EmailUtil emailUtil;
	
	//这句必须要加上。不然会报空指针异常，因为在实际调用的时候不是BaseMapper调用，而是具体的mapper，这里为userMapper
	@Autowired
	public void setBaseMapper() {
		super.setBaseMapper(userMapper);
	}
	
	/**
	 * 重写用户插入，逻辑：
	 * 1、插入用户
	 * 2、插入用户和角色的对应关系
	 * 3、插入用户的个人资料信息
	 */
	public int insert(UserEntity userEntity, String password){
		try
		{
			if(userMapper.insert(userEntity) == 1)
			{
				if(userMapper.insertUserRole(userEntity) == 1)
				{
					userEntity.getUserInfo().setId(userEntity.getId());
					int cnt = userMapper.insertUserInfo(userEntity);
					//发送邮件
					emailUtil.send126Mail(userEntity.getAccountName(), "系统消息通知", "您好,您的账户已创建,账户名:"+userEntity.getAccountName() +" ,密码:" + password);
					return cnt;
				}else
				{
					return 0;
				}
			}else
			{
				return 0;
			}
		}catch(Exception e)
		{
			throw new ServiceException(e);
		}
	}
	

	/**
	 * 重写用户更新逻辑：
	 * 1、更新用户
	 * 2、更新用户和角色的对应关系
	 * 3、更新用户个人资料信息
	 */
	public int update(UserEntity userEntity){
		try
		{
			if(userMapper.update(userEntity) == 1)
			{
				if(userMapper.updateUserRole(userEntity) == 1)
				{
					return userMapper.updateUserInfo(userEntity);
				}else
				{
					return 0;
				}
			}else
			{
				return 0;
			}
		}catch(Exception e)
		{
			throw new ServiceException(e);
		}
	}
	
	/**
	 * 重写用户删除逻辑：
	 * 1、删除用户和角色的对应关系
	 * 2、删除用户
	 */
	public int deleteBatchById(List<Long> userIds){
		try
		{
			int result = userMapper.deleteBatchUserRole(userIds);
			if(result == userIds.size())
			{
				return userMapper.deleteBatchById(userIds);
			}else
			{
				return 0;
			}
		}catch(Exception e)
		{
			throw new ServiceException(e);
		}
	}

	@Override
	public int updateOnly(UserEntity userEntity, String password) throws ServiceException{
		try
		{ 
			int cnt = userMapper.update(userEntity);
			//发送邮件
			emailUtil.send126Mail(userEntity.getAccountName(), "系统密码重置", "您好，您的密码已重置，新密码是：" + password);
			return cnt;
		}catch(Exception e)
		{
			throw new ServiceException(e);
		}
	}
	
}

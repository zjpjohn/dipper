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


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.once.crosscloud.base.baseservice.impl.AbstractService;
import com.once.crosscloud.mappers.LoginInfoMapper;
import com.once.crosscloud.models.LoginInfoEntity;
import com.once.crosscloud.services.LoginInfoService;

/**
 * @author wuheng(wuheng@otcaix.iscas.ac.cn)
 * @date   Jun 13, 2016
 *
 */
@Service("loginInfoService")
public class LoginInfoServiceImpl extends AbstractService<LoginInfoEntity, Long> implements LoginInfoService{

	@Autowired
	private LoginInfoMapper loginInfoMapper;
	
	//这句必须要加上。不然会报空指针异常，因为在实际调用的时候不是BaseMapper调用，而是具体的mapper，这里为userMapper
	@Autowired
	public void setBaseMapper() {
		super.setBaseMapper(loginInfoMapper);
	}

	@Override
	public int log(LoginInfoEntity loginInfo) {
		return loginInfoMapper.insert(loginInfo);
	}
	
}

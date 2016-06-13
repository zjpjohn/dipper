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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.once.crosscloud.base.baseservice.impl.AbstractService;
import com.once.crosscloud.exception.ServiceException;
import com.once.crosscloud.mappers.RoleMapper;
import com.once.crosscloud.models.RoleEntity;
import com.once.crosscloud.services.RoleService;

/**
 * @author wuheng(wuheng@otcaix.iscas.ac.cn)
 * @date   Jun 13, 2016
 *
 */
@Service("roleService")
public class RoleServiceImpl extends AbstractService<RoleEntity, Long>
		implements RoleService {

	@Autowired
	private RoleMapper roleMapper;

	// 这句必须要加上。不然会报空指针异常，因为在实际调用的时候不是BaseMapper调用，而是具体的mapper，这里为userMapper
	@Autowired
	public void setBaseMapper() {
		super.setBaseMapper(roleMapper);
	}

	@Override
	public boolean addRolePerm(int id, List<Integer> ids){
		boolean flag = false;
		try
		{
			int permCount = roleMapper.findRoleResourceById(id);
			boolean delFlag = true;
			if(permCount > 0)
			{
				int delResult = roleMapper.deleteRoleResource(id);
				if(permCount != delResult)
				{
					delFlag = false;
				}
			}
			
			if (delFlag) {
				if(ids.size() > 0)
				{
					Map<String, Object> parameter = new HashMap<String, Object>();
					parameter.put("roleId", id);
					parameter.put("resourceIds", ids);
					int addResult = roleMapper.addRoleResource(parameter);
					if (addResult == ids.size()) {
						flag = true;
					}
				}else
				{
					flag = true;
				}
			}
			return flag;
		}catch(Exception e)
		{
			throw new ServiceException(e);
		}
	}


}

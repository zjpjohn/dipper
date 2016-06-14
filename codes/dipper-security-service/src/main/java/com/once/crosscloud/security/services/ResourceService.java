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
package com.once.crosscloud.security.services;

import java.util.List;
import java.util.Map;

import com.once.crosscloud.security.models.ResourceEntity;


/**
 * @author wuheng(wuheng@otcaix.iscas.ac.cn)
 * @date   Jun 13, 2016
 *
 */
public interface ResourceService{

	/**
	 * 自定义方法
	 * 获取用户ID对应的资源信息
	 * @param userId
	 * @return
	 */
	public List<ResourceEntity> findResourcesByUserId(int userId);

	public List<ResourceEntity> queryListByPage(Map<String, Object> parameter);
	
	public ResourceEntity findByName(String name);
	
	public int insert(ResourceEntity resourceEntity);
	
	public ResourceEntity findById(Long id);

	public int update(ResourceEntity resourceEntity);
    
    public int deleteBatchById(List<Long> roleIds);
    
    public List<ResourceEntity> queryResourceList(Map<String, Object> parameter);
}

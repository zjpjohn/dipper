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
package com.once.crosscloud.security.mappers;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

/**
 * @author wuheng(wuheng@otcaix.iscas.ac.cn)
 * @date   2016年6月14日
 *
 * @param <T> 
 * @param <ID>
 */
public interface BaseMapper<T,ID extends Serializable> {

    /**
     * @param t
     * @return
     */
    public int insert(T t);
    
    /**
     * @param t
     * @return
     */
    public int insertBatch(List<T> t);
    
    /**
     * @param ids
     * @return
     */
    public int deleteBatchById(List<ID> ids);
 
    /**
     * @param id
     * @return
     */
    public int deleteById(@Param("id")ID id);
 
    /**
     * @param uuid
     * @return
     */
    public int deleteByUUID(String uuid);
 
    /**
     * @param t
     * @return
     */
    public int update(T t);
 
    /**
     * @param parameter
     * @return
     */
    public T find(Map<String, Object> parameter);
 
    /**
     * @param id
     * @return
     */
    public T findById(@Param("id")ID id);
 
    /**
     * @param uuid
     * @return
     */
    public T findByUUID(@Param("uuid")String uuid);
 
    /**
     * @param name
     * @return
     */
    public T findByName(@Param("name")String name);
 
    /**
     * @param parameter
     * @return
     */
    public List<T> queryListAll(Map<String, Object> parameter);
    
    /**
     * @param parameter
     * @return
     */
    public List<T> queryListByPage(Map<String, Object> parameter);
    
    /**
     * @param parameter
     * @return
     */
    int count(Map<String, Object> parameter);
	
}

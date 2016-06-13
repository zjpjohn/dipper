package com.once.crosscloud.cores.base.baseservice;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface BaseService<T,ID extends Serializable> {

    public int insert(T t);
    
    public int insertBatch(List<T> t);
    
    public int deleteBatchById(List<ID> ids);
 
    public int deleteById(ID id);
 
    public int deleteByUUID(String uuid);
 
    public int update(T t);
    
    public T find(Map<String, Object> parameter);
 
    public T findById(ID id);
 
    public T findByUUID(String uuid);
 
    public T findByName(String name);
 
    public List<T> queryListAll(Map<String, Object> parameter);
    
    public List<T> queryListByPage(Map<String, Object> parameter);
    
    int count(Map<String, Object> parameter);

}

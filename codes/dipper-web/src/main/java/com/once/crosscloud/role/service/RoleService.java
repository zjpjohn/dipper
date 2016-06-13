package com.once.crosscloud.role.service;

import java.util.List;
import java.util.Map;

import com.once.crosscloud.models.RoleEntity;


public interface RoleService {

	public List<RoleEntity> queryListByPage(Map<String, Object> parameter);

	public RoleEntity findByName(String name);
	
	public int insert(RoleEntity roleEntity);
	
	public RoleEntity findById(Long id);

	public int update(RoleEntity roleEntity);
    
    public int deleteBatchById(List<Long> roleIds);
    
    public boolean addRolePerm(int id, List<Integer> ids) throws Exception ;

}
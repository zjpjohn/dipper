package com.once.crosscloud.security.services.impl;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.once.crosscloud.security.exceptions.ServiceException;
import com.once.crosscloud.security.mappers.BaseMapper;
import com.once.crosscloud.security.services.BaseService;

public class AbstractService<T, ID extends Serializable> implements BaseService<T, ID> {

	private BaseMapper<T, ID> baseMapper;
	
	public void setBaseMapper(BaseMapper<T, ID> baseMapper) {
		this.baseMapper = baseMapper;
	}

	public int insert(T t) {
		try
		{
			return baseMapper.insert(t);
		}catch(Exception e)
		{
			throw new ServiceException(e);
		}
	}

	public int insertBatch(List<T> t) {
		try
		{
			return baseMapper.insertBatch(t);
		}catch(Exception e)
		{
			throw new ServiceException(e);
		}
	}

	public int deleteById(ID id) {
		try
		{
			return baseMapper.deleteById(id);
		}catch(Exception e)
		{
			throw new ServiceException(e);
		}
	}

	public int deleteByUUID(String uuid) {
		try
		{
			return baseMapper.deleteByUUID(uuid);
		}catch(Exception e)
		{
			throw new ServiceException(e);
		}
	}

	public int update(T t) {
		try
		{
			return baseMapper.update(t);
		}catch(Exception e)
		{
			throw new ServiceException(e);
		}
	}
	

	public int deleteBatchById(List<ID> ids) {
		try
		{
			return baseMapper.deleteBatchById(ids);
		}catch(Exception e)
		{
			throw new ServiceException(e);
		}
	}
	
	public T find(Map<String, Object> parameter) {
		return baseMapper.find(parameter);
	}

	public T findById(ID id) {
		return baseMapper.findById(id);
	}

	public T findByUUID(String uuid) {
		return baseMapper.findByUUID(uuid);
	}

	public T findByName(String name) {
		return baseMapper.findByName(name);
	}

	public List<T> queryListAll(Map<String, Object> parameter) {
		return baseMapper.queryListAll(parameter);
	}

	public List<T> queryListByPage(Map<String, Object> parameter) {
		return baseMapper.queryListByPage(parameter);
	}

	public int count(Map<String, Object> parameter) {
		return baseMapper.count(parameter);
	}

}

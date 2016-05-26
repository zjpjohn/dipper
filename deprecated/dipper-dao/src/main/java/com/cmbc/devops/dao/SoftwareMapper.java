package com.cmbc.devops.dao;

import java.util.List;

import com.cmbc.devops.entity.Software;

public interface SoftwareMapper {

	public int deleteByPrimaryKey(Integer swId) throws Exception;

	public int insert(Software software) throws Exception;

	public int insertSelective(Software software) throws Exception;

	public List<Software> selectAll(Software software) throws Exception;

	public Software selectByPrimaryKey(Integer swId) throws Exception;

	public int updateByPrimaryKeySelective(Software software) throws Exception;

	public int updateByPrimaryKey(Software software) throws Exception;

	/** 批量删除软件记录 */
	public int deleteSofts(List<Integer> softIds) throws Exception;

	public List<Software> getListByHostId(Integer hostId) throws Exception;
	
	public Software selectByName(String swName) throws Exception;
}
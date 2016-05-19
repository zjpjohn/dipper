package com.cmbc.devops.dao;

import java.util.List;

import com.cmbc.devops.entity.Software;
import com.cmbc.devops.exception.SqlException;

public interface SoftwareMapper {

	public int deleteByPrimaryKey(Integer swId) throws SqlException;

	public int insert(Software software) throws SqlException;

	public int insertSelective(Software software) throws SqlException;

	public List<Software> selectAll(Software software) throws SqlException;

	public Software selectByPrimaryKey(Integer swId) throws SqlException;

	public int updateByPrimaryKeySelective(Software software) throws SqlException;

	public int updateByPrimaryKey(Software software) throws SqlException;

	/** 批量删除软件记录 */
	public int deleteSofts(List<Integer> softIds) throws SqlException;

	public List<Software> getListByHostId(Integer hostId) throws SqlException;
	
	public Software selectByName(String swName) throws SqlException;
}
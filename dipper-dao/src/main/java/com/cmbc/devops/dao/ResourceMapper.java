package com.cmbc.devops.dao;

import java.util.List;

import com.cmbc.devops.entity.DkResource;
import com.cmbc.devops.exception.SqlException;

public interface ResourceMapper {

	/**
	 * @author langzi
	 * @param record
	 * @return
	 * @throws SqlException
	 * @version 1.0 2015年8月19日 insert resource entity to DB
	 */
	public int insertResource(DkResource record) throws SqlException;

	/**
	 * @author langzi
	 * @param logId
	 * @return
	 * @throws SqlException
	 * @version 1.0 2015年8月19日 delete resource entity from DB by resourece
	 *          id(primary key)
	 */
	public int deleteResource(Integer resId) throws SqlException;

	/**
	 * @author yangqinglin
	 * @return 删除多个资源项
	 * @throws SqlException
	 * @version 1.0 2015年8月19日 delete resource entity from DB by resourece
	 *          ids(primary key)
	 */
	public int deleteResources(List<Integer> residList);

	/**
	 * @author langzi
	 * @param record
	 * @return
	 * @throws SqlException
	 * @version 1.0 2015年8月19日 update resource entity to DB
	 */
	public int updateResource(DkResource record) throws SqlException;

	/**
	 * @author langzi
	 * @return
	 * @throws SqlException
	 * @version 1.0 2015年8月19日 select all resource entities from DB which meet
	 *          resource conditions
	 */
	public List<DkResource> selectAll(DkResource record) throws SqlException;

	/**
	 * @author langzi
	 * @param resIds
	 * @return
	 * @throws SqlException
	 * @version 1.0 2016年3月9日 select all resource entities from DB by resource
	 *          ids
	 */
	public List<DkResource> selectAllViaIds(List<Integer> resIds) throws SqlException;

	/**
	 * @author langzi
	 * @return
	 * @throws SqlException
	 * @version 1.0 2015年8月19日 select all resource entities from DB by resource
	 *          id
	 */
	public DkResource selectResource(Integer resId) throws SqlException;

	/** 通过资源的ID列表获取全部的资源列表信息 */
	public List<DkResource> selectMultiReses(List<Integer> resIdList) throws Exception;

	/** 批量插入资源记录 */
	public Integer batchInsertReses(List<DkResource> resList) throws Exception;

}
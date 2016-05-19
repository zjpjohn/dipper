package com.cmbc.devops.dao;

import java.util.List;
import com.cmbc.devops.entity.Parameter;
import com.cmbc.devops.exception.SqlException;

public interface ParameterMapper {

	/**
	 * TODO
	 * 
	 * @author mayh
	 * @return int
	 * @throws SqlException
	 * @version 1.0 2015年8月19日 insert parameter entity to DB
	 */
	public int insertParameter(Parameter record) throws SqlException;

	/**
	 * TODO
	 * 
	 * @author mayh
	 * @return int
	 * @throws SqlException
	 * @version 1.0 2015年8月19日 delete parameter entity from DB by param id
	 */
	public int deleteParameter(Integer paramId) throws SqlException;

	/**
	 * @author langzi
	 * @param list
	 * @throws SqlException
	 * @version 1.0 2016年3月9日 delete parameter entity from DB by param ids
	 */
	public void update(List<Integer> paramIds) throws SqlException;

	/**
	 * TODO
	 * 
	 * @author mayh
	 * @return int
	 * @throws SqlException
	 * @version 1.0 2015年8月19日 update parameter entity to DB
	 */
	public int updateParameter(Parameter record) throws SqlException;

	/**
	 * TODO
	 * 
	 * @author mayh
	 * @return List<Parameter>
	 * @throws SqlException
	 * @version 1.0 2015年8月23日 select all parameter entities from DB which meet
	 *          parameter conditions
	 */
	public List<Parameter> selectAll(Parameter record) throws SqlException;

	/**
	 * @author langzi
	 * @param paramName
	 * @throws SqlException
	 * @version 1.0 2015年10月21日 select parameter entities from DB by parameter
	 *          name
	 */
	public Parameter selectParamByName(Parameter param) throws SqlException;

	/**
	 * @author langzi
	 * @param paramId
	 * @return
	 * @throws SqlException
	 * @version 1.0 2016年3月9日 select parameter entity from DB by parameter
	 *          id(primary key)
	 */
	public Parameter selectParamById(Integer paramId);

	/** 通过参数的ID列表获取全部的参数列表信息 */
	public List<Parameter> selectMultiParams(List<Integer> paramIdList) throws Exception;

	/** 批量插入参数记录 */
	public Integer batchInsertParams(List<Parameter> paramList) throws Exception;

}
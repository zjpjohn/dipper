package com.cmbc.devops.dao;

import java.util.List;

import com.cmbc.devops.entity.RegImage;
import com.cmbc.devops.exception.SqlException;

public interface RegImageMapper {
	
   
    /**
	 * @author langzi
	 * @param record
	 * @return
	 * @throws SqlException
	 * @version 1.0
	 * 2016年3月11日
	 * inert RegImage entity to DB
	 */
	public int insert(RegImage record) throws SqlException;
   
    /**
	 * @author langzi
	 * @param id
	 * @return
	 * @throws SqlException
	 * @version 1.0
	 * 2016年3月11日
	 * delete RegImage entity from DB by id(primary key)
	 */
	public int deleteById(Integer id) throws SqlException;
   
	/**
	 * @author langzi
	 * @param id
	 * @return
	 * @throws SqlException
	 * @version 1.0
	 * 2016年3月11日
	 * delete RegImage entity from DB by imageId
	 */
	public int deleteByImageId(Integer id) throws SqlException;

	/**
	 * @author langzi
	 * @param record
	 * @return
	 * @throws SqlException
	 * @version 1.0
	 * 2016年3月11日
	 * update RegImage entity to DB
	 */
	public int update(RegImage record) throws SqlException;
	
	/**
	 * @author langzi
	 * @param id
	 * @return
	 * @throws SqlException
	 * @version 1.0
	 * 2016年3月11日
	 * select RegImage entity from DB by id(primary key)
	 */
	public RegImage selectById(Integer id) throws SqlException;
    
    /**
     * @author langzi
     * @param record
     * @return
     * @throws SqlException
     * @version 1.0
     * 2016年3月11日
     * select all RegImage entities from DB 
     */
    public List<RegImage> selectAll(RegImage record) throws SqlException;
}
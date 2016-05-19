package com.cmbc.devops.dao;


import java.util.List;

import com.cmbc.devops.entity.Log;
import com.cmbc.devops.exception.SqlException;

public interface LogMapper {
	
    /**
     * @author langzi
     * @param record
     * @return
     * @throws SqlException
     * @version 1.0
     * 2015年8月19日
     * insert log entity to DB
     */
    public int insertLog(Log record) throws SqlException;
    
    /**
     * @author langzi
     * @param logId
     * @return
     * @throws SqlException
     * @version 1.0
     * 2015年8月19日
     * delete log entity from DB by logId(primary key)
     */
    public int deleteLog(Integer logId) throws SqlException;

    /**
     * @author langzi
     * @param record
     * @return
     * @throws SqlException
     * @version 1.0
     * 2015年8月19日
     * update log entity to DB
     */
    public int updateLog(Log record) throws SqlException;
    
    /**
	 * @author langzi
	 * @return
	 * @throws SqlException
	 * @version 1.0
	 * 2015年8月19日
	 * select all log entities from DB 
	 */
	public List<Log> selectAll(Log log) throws SqlException;
	
	/**
	 * @author langzi
	 * @return
	 * @throws SqlException
	 * @version 1.0
	 * 2015年8月19日
	 * select log entity from DB
	 */
	public Log selectLog() throws SqlException;
    
}
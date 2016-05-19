package com.cmbc.devops.dao;

import java.util.List;

import com.cmbc.devops.entity.EnvApp;
import com.cmbc.devops.exception.SqlException;

public interface EnvAppMapper {
	
	/**
     * @author langzi
     * @param record
     * @return
     * @throws SqlException
     * @version 1.0
     * 2016年1月12日
     * insert Env and application relation entities to DB
     */
    public int insert(EnvApp record) throws SqlException;
	
    /**
     * @author langzi
     * @param id
     * @return
     * @throws SqlException
     * @version 1.0
     * 2016年1月12日
     * delete envApp entities from DB by appId
     */
    public int delete(int appId) throws SqlException;
    
    /**
     * @author langzi
     * @param record
     * @return
     * @throws SqlException
     * @version 1.0
     * 2016年1月12日
     * update envApp entities to DB
     */
    public int update(EnvApp record) throws SqlException;

    /**
     * @author langzi
     * @param id
     * @return
     * @throws SqlException
     * @version 1.0
     * 2016年1月12日
     * select envApp entity from DB by id(primary key)
     */
    public EnvApp select(int id) throws SqlException;
    
    /**
     * @author langzi
     * @param appId
     * @return
     * @throws SqlException
     * @version 1.0
     * 2016年1月12日
     * select envApp entities from DB by appId
     */
    public List<EnvApp> selectAllByAppId(int appId) throws SqlException;
    
    /**
     * @author langzi
     * @param appId
     * @return
     * @throws SqlException
     * @version 1.0
     * 2016年1月12日
     * select envApp entities from DB by envId
     */
    public List<EnvApp> selectAllByEnvId(int envId);

}
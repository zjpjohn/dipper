package com.cmbc.devops.dao;

import java.util.List;

import com.cmbc.devops.entity.EnvApp;

public interface EnvAppMapper {
	
	/**
     * @author langzi
     * @param record
     * @return
     * @throws Exception
     * @version 1.0
     * 2016年1月12日
     * insert Env and application relation entities to DB
     */
    public int insert(EnvApp record) throws Exception;
	
    /**
     * @author langzi
     * @param id
     * @return
     * @throws Exception
     * @version 1.0
     * 2016年1月12日
     * delete envApp entities from DB by appId
     */
    public int delete(int appId) throws Exception;
    
    /**
     * @author langzi
     * @param record
     * @return
     * @throws Exception
     * @version 1.0
     * 2016年1月12日
     * update envApp entities to DB
     */
    public int update(EnvApp record) throws Exception;

    /**
     * @author langzi
     * @param id
     * @return
     * @throws Exception
     * @version 1.0
     * 2016年1月12日
     * select envApp entity from DB by id(primary key)
     */
    public EnvApp select(int id) throws Exception;
    
    /**
     * @author langzi
     * @param appId
     * @return
     * @throws Exception
     * @version 1.0
     * 2016年1月12日
     * select envApp entities from DB by appId
     */
    public List<EnvApp> selectAllByAppId(int appId) throws Exception;
    
    /**
     * @author langzi
     * @param appId
     * @return
     * @throws Exception
     * @version 1.0
     * 2016年1月12日
     * select envApp entities from DB by envId
     */
    public List<EnvApp> selectAllByEnvId(int envId);

}
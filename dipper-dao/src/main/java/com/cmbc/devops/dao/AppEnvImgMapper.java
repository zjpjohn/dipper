package com.cmbc.devops.dao;

import java.util.List;
import com.cmbc.devops.entity.AppEnvImg;
import com.cmbc.devops.exception.SqlException;

public interface AppEnvImgMapper {

	/** (1) */
	public AppEnvImg select(int id) throws SqlException;

	/** (2) */
	public List<AppEnvImg> selectAllByAppId(int appId) throws SqlException;

	/** (3) */
	public List<AppEnvImg> selectAllByEnvId(int envId) throws SqlException;
	
	/** */
	public List<AppEnvImg> selectAllByImgId(int imgId) throws SqlException;

	/** 批量添加记录 */
	public int batchInsertAEIs(List<AppEnvImg> aeiList) throws SqlException;

	/** (4) */
	public int deleteByAppId(int appId) throws SqlException;

	/** (5) */
	public int deleteByEnvId(int envId) throws SqlException;

	/** (6) */
	public int deleteByImgId(int imgId) throws SqlException;

	/** (7) */
	public void insert(AppEnvImg record) throws SqlException;

	/** (8) */
	public int update(AppEnvImg record) throws SqlException;


	public List<AppEnvImg> listByAppIdAndImageId(AppEnvImg record);

	public void deleteById(AppEnvImg aei);

}
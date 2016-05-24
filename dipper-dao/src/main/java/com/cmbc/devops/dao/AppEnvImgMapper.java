package com.cmbc.devops.dao;

import java.util.List;
import com.cmbc.devops.entity.AppEnvImg;

public interface AppEnvImgMapper {

	/** (1) */
	public AppEnvImg select(int id) throws Exception;

	/** (2) */
	public List<AppEnvImg> selectAllByAppId(int appId) throws Exception;

	/** (3) */
	public List<AppEnvImg> selectAllByEnvId(int envId) throws Exception;
	
	/** */
	public List<AppEnvImg> selectAllByImgId(int imgId) throws Exception;

	/** 批量添加记录 */
	public int batchInsertAEIs(List<AppEnvImg> aeiList) throws Exception;

	/** (4) */
	public int deleteByAppId(int appId) throws Exception;

	/** (5) */
	public int deleteByEnvId(int envId) throws Exception;

	/** (6) */
	public int deleteByImgId(int imgId) throws Exception;

	/** (7) */
	public void insert(AppEnvImg record) throws Exception;

	/** (8) */
	public int update(AppEnvImg record) throws Exception;


	public List<AppEnvImg> listByAppIdAndImageId(AppEnvImg record);

	public void deleteById(AppEnvImg aei);

}
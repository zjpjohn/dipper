package com.cmbc.devops.service;

import java.util.List;
import com.cmbc.devops.entity.AppEnvImg;

/**
 * date：2016年1月12日 下午1:46:00 project name：cmbc-devops-service
 * 
 * @author langzi
 * @version 1.0
 * @since JDK 1.7.0_21 file name：EnvAppService.java description：
 */
public interface AppEnvImgService {

	public List<AppEnvImg> listAllByAppId(int appId) throws Exception;

	public List<AppEnvImg> listAllByEnvId(int envId) throws Exception;

	public List<AppEnvImg> listAllByImgId(int envId) throws Exception;

	public void insert(AppEnvImg aei) throws Exception;

	public int batchInsert(List<AppEnvImg> aeiList) throws Exception;

	public int removeByAppId(int appId) throws Exception;

	public int removeByEnvId(int envId) throws Exception;

	public int removeByImgId(int imgId) throws Exception;

	public int update(AppEnvImg aei) throws Exception;

	public List<AppEnvImg> listByAppIdAndImageId(Integer appid, Integer imageid)throws Exception;

	public void removeById(Integer appId, Integer imageId);

}

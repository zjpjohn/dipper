package com.cmbc.devops.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cmbc.devops.dao.EnvAppMapper;
import com.cmbc.devops.entity.EnvApp;
import com.cmbc.devops.service.EnvAppService;

/**  
 * date：2016年1月12日 下午1:49:01  
 * project name：cmbc-devops-service  
 * @author langzi  
 * @version 1.0   
 * @since JDK 1.7.0_21  
 * file name：EnvAppServiceImpl.java  
 * description：  
 */
@Component
public class EnvAppServiceImpl implements EnvAppService {

	@Autowired
	private EnvAppMapper mapper;
	
	@Override
	public List<EnvApp> listAllByAppId(int appId) throws Exception {
		return mapper.selectAllByAppId(appId);
	}
	
	@Override
	public List<EnvApp> listAllByEnvId(int envId) throws Exception {
		return mapper.selectAllByEnvId(envId);
	}

	@Override
	public int add(EnvApp envApp) throws Exception {
		return mapper.insert(envApp);
	}

	@Override
	public int removeByAppId(int appId) throws Exception {
		return mapper.delete(appId);
	}

}

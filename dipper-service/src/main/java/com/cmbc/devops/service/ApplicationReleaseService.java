package com.cmbc.devops.service;

import com.cmbc.devops.bean.GridBean;
import com.cmbc.devops.model.ApplicationModel;

/**
 * date：2015年12月10日 上午10:35:34 project name：cmbc-devops-service
 * 
 * @author langzi
 * @version 1.0
 * @since JDK 1.7.0_21 file name：ApplicationReleaseService.java description：
 */
public interface ApplicationReleaseService {

	/**
	 * @author langzi
	 * @param userId
	 * @param pagenum
	 * @param pagesize
	 * @param model
	 * @return
	 * @throws Exception
	 * @version 1.0 2015年12月10日
	 */
	public GridBean listOnePageReleasedApp(Integer userId, int pagenum, int pagesize, ApplicationModel model)
			throws Exception;

}

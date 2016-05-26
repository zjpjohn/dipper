package com.cmbc.devops.service;

import com.cmbc.devops.bean.GridBean;
import com.cmbc.devops.bean.Result;
import com.cmbc.devops.entity.Log;

/**  
 * date：2015年8月19日 上午11:43:28  
 * project name：cmbc-devops-service  
 * @author langzi  
 * @version 1.0   
 * @since JDK 1.7.0_21  
 * file name：LogService.java  
 * description：  
 */
public interface LogService {
	/**日志分页
	 * @param pageNum 页码
	 * @param pageSize 页面大小
	 * @return 分页信息
	 * @throws Exception 
	 */
	public GridBean list(Log log,int pageNum,int pageSize) throws Exception;
	/**保存日志
	 * @param log 日志实体
	 * @return
	 * @throws Exception 
	 */
	public Result save(Log log) throws Exception;
}

/**
 * 
 */
package com.cmbc.devops.manager;

import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.cmbc.devops.bean.GridBean;
import com.cmbc.devops.bean.Result;
import com.cmbc.devops.entity.Authority;
import com.cmbc.devops.service.AuthorityService;

/**
 * date：2015年8月23日 下午8:23:30 project name：cmbc-devops-web
 * 
 * @author dingmw
 * @version 1.0
 * @since JDK 1.7.0_21 file name：ParameterManager.java description：
 */
@Component
public class AuthorityManager {
	private static final Logger LOGGER = Logger.getLogger(AuthorityManager.class);
	@Resource
	private AuthorityService authorityService;

	public Result update(Authority authority) {
		int result = authorityService.update(authority);
		if(result>0){
			LOGGER.info("Update authority success");
			return new Result(true, "更新权限成功！");
		}else{
			LOGGER.error("Update authority fail");
			return new Result(true, "更新权限失败！");
		}
	}

	public List<Authority> getUserRoleAuths(Integer userId){
		try {
			return authorityService.listAuthsByUserId(userId);
		} catch (Exception e) {
			LOGGER.error("get auth list by userid["+userId+"] falied! ", e);
			return null;
		}
	}
	
	/**
	 * @author luogan
	 * @param advancedSearchParam
	 * @return
	 * @version 1.0
	 * 2015年10月21日
	 */
	public GridBean advancedSearchAuth(Integer userId, int pagenum, int pagesize, Authority authority,com.alibaba.fastjson.JSONObject json_object) {
		try {
			return authorityService.advancedSearchAuth(userId, pagenum, pagesize, authority, json_object);
		} catch (Exception e) {
			LOGGER.info("Advanced search auth fail",e);
			return null;
		}
	}
	
}

package com.cmbc.devops.manager;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.cmbc.devops.bean.GridBean;
import com.cmbc.devops.bean.Result;
import com.cmbc.devops.entity.Env;
import com.cmbc.devops.entity.EnvApp;
import com.cmbc.devops.service.EnvAppService;
import com.cmbc.devops.service.EnvService;

/**
 * date：2016年1月11日 下午2:34:00 project name：cmbc-devops-web
 * 
 * @author langzi
 * @version 1.0
 * @since JDK 1.7.0_21 file name：EnvManager.java description：
 */
@Component
public class EnvManager {

	private static final Logger LOGGER = Logger.getLogger(EnvManager.class);

	@Autowired
	private EnvService service;
	
	@Autowired
	private EnvAppService envAppService;

	/**
	 * @author langzi
	 * @param env
	 * @return
	 * @version 1.0 2016年1月11日
	 */
	public Result add(Env env) {
		int addResult = 0;
		try {
			addResult = service.add(env);
		} catch (Exception e) {
			LOGGER.error("add env error", e);
		}
		if (addResult > 0) {
			return new Result(true, " 运行环境创建成功！");
		} else {
			return new Result(false, "运行环境创建失败！");
		}
	}

	/**
	 * @author langzi
	 * @param env
	 * @return
	 * @version 1.0 2016年1月11日
	 */
	public Result modify(Env env) {
		int modifyResult = 0;
		try {
			modifyResult = service.modify(env);
		} catch (Exception e) {
			LOGGER.error("add app error", e);
		}
		if (modifyResult > 0) {
			return new Result(true, "运行环境更新成功！");
		} else {
			return new Result(false, "运行环境更新失败！");
		}
	}

	/**
	 * @author langzi
	 * @param envId
	 * @return
	 * @version 1.0 2016年1月11日
	 */
	public Result remove(int envId) {
		
		try {
			List<EnvApp> list=envAppService.listAllByEnvId(envId);
			if(list!=null&&list.size()>0){
				return new Result(false, "删除失败:该运行环境已被应用绑定！");
			}
		} catch (Exception e1) {
			LOGGER.error("list all envApp by envid["+envId+"]error!", e1);
			return new Result(false, "运行环境删除失败！");
		}
		
		int removeResult = 0;
		try {
			removeResult = service.remove(envId);
		} catch (Exception e) {
			LOGGER.error("add app error", e);
		}
		if (removeResult > 0) {
			return new Result(true, "运行环境删除成功！");
		} else {
			return new Result(false, "运行环境删除失败！");
		}
	}

	public Boolean checkEnvName(String envName) {
		try {
			return service.getEnvByName(envName) == null ? true : false;
		} catch (Exception e) {
			LOGGER.error("get environment by envname[" + envName + "] falied!", e);
			return false;
		}
	}

	public GridBean envSearchAllList(Integer userId, int pagenumber, int pagesize, String searchName) {
		try {
			return service.searchAllEnv(userId, pagenumber, pagesize, searchName);
		} catch (Exception e) {
			LOGGER.error("搜索环境列表失败！", e);
			return null;
		}
	}

	public GridBean advancedSearchEnvs(Integer userId, int pagenumber, int pagesize, JSONObject jO) {
		try {
			return service.advancedSearchEnvs(userId, pagenumber, pagesize, jO);
		} catch (Exception e) {
			LOGGER.error("高级查询环境列表失败！", e);
		}
		return null;
	}
}

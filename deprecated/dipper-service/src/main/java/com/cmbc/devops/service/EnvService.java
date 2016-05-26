package com.cmbc.devops.service;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.cmbc.devops.bean.GridBean;
import com.cmbc.devops.entity.Env;

/**
 * date：2016年1月11日 下午2:24:10 project name：cmbc-devops-service
 * 
 * @author langzi
 * @version 1.0
 * @since JDK 1.7.0_21 file name：EnvService.java description：
 */
public interface EnvService {

	/**
	 * @author langzi
	 * @return
	 * @throws Exception
	 * @version 1.0 2016年1月5日 list all envs
	 */
	public List<Env> listAll() throws Exception;

	/**
	 * @author langzi
	 * @param pagenumber
	 * @param pagesize
	 * @return
	 * @throws Exception
	 * @version 1.0 2016年1月5日 get one page envs
	 */
	public GridBean getOnePageEnvs(int pagenumber, int pagesize) throws Exception;

	/**
	 * @author langzi
	 * @param envId
	 * @return
	 * @throws Exception
	 * @version 1.0 2016年1月5日
	 * 
	 */
	public Env find(int envId) throws Exception;

	/**
	 * @author langzi
	 * @param env
	 * @return
	 * @throws Exception
	 * @version 1.0 2016年1月5日
	 */
	public int add(Env env) throws Exception;

	/**
	 * @author langzi
	 * @param env
	 * @return
	 * @throws Exception
	 * @version 1.0 2016年1月5日
	 */
	public int modify(Env env) throws Exception;

	/**
	 * @author langzi
	 * @param envId
	 * @return
	 * @throws Exception
	 * @version 1.0 2016年1月5日
	 */
	public int remove(int envId) throws Exception;

	/**
	 * @author langzi
	 * @param envId
	 * @return
	 * @throws Exception
	 * @version 1.0 2016年1月5日
	 */
	public List<Env> listByAppId(Integer appId) throws Exception;

	/**
	 * @author yangqinglin
	 * @param envId
	 * @return
	 * @throws Exception
	 * @version 1.0 2016年1月21日
	 */
	public Env getEnvByName(String envName) throws Exception;

	/**
	 * @author yangqinglin
	 * @param envId
	 * @return
	 * @throws Exception
	 * @version 1.0 2016年1月21日
	 */
	public GridBean searchAllEnv(Integer userId, int pagenumber, int pagesize, String search_name) throws Exception;

	/**
	 * @author yangqinglin
	 * @param envId
	 * @return 高级查询返回符合条件的环境结果
	 * @throws Exception
	 * @version 1.0 2016年1月21日
	 */
	public GridBean advancedSearchEnvs(Integer userId, int pagenumber, int pagesize, JSONObject json_object)
			throws Exception;
}

/**
 * 
 */
package com.cmbc.devops.manager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cmbc.devops.bean.GridBean;
import com.cmbc.devops.bean.Result;
import com.cmbc.devops.entity.Parameter;
import com.cmbc.devops.service.ParameterService;

/**
 * date：2015年8月23日 下午8:23:30 project name：cmbc-devops-web
 * 
 * @author dingmw
 * @version 1.0
 * @since JDK 1.7.0_21 file name：ParameterManager.java description：
 */
@Component
public class ParameterManager {
	private static final Logger LOGGER = Logger.getLogger(ParameterManager.class);
	@Resource
	private ParameterService parameterService;

	/**
	 * 参数列表分页
	 * 
	 * @param userId
	 * @param name
	 * @param pageNum
	 * @param pageSize
	 */
	public GridBean parameterList(Integer userId, Integer tenantId, String name, int pageNum, int pageSize) {
		try {
			return parameterService.list(userId, tenantId, name, pageNum, pageSize);
		} catch (Exception e) {
			LOGGER.error("get param list falied!", e);
			return null;
		}
	}

	/**
	 * 参数列表分页
	 * 
	 * @param userId
	 * @param name
	 * @param pageNum
	 * @param pageSize
	 * @return GridBean
	 * 
	 */
	public GridBean paramAllList(Integer userId, int pageNum, int pageSize, Parameter parameter) {
		try {
			return parameterService.allList(userId, pageNum, pageSize, parameter);
		} catch (Exception e) {
			LOGGER.error("get all param list falied!", e);
			return null;
		}
	}

	/**
	 * @return JSONArray
	 * @version 1.0 2015年8月28日
	 * @description 查询所有可以使用的参数
	 */
	public JSONArray allParam(int tenantId) {
		return parameterService.allParam(tenantId);
	}

	public Result create(Parameter param) {
		param.setParamCreatetime(new Date());
		int result = parameterService.create(param);
		if (result > 0) {
			LOGGER.info("create createParameter success");
			return new Result(true, "添加参数成功！");
		} else {
			LOGGER.error("create createParameter fail");
			return new Result(false, "添加参数失败！");
		}
	}

	public Result update(Parameter param) {
		int result = parameterService.update(param);
		if (result > 0) {
			LOGGER.info("Update update Parameter (id:" + param.getParamId() + ") success");
			return new Result(true, "更新参数成功！");
		} else {
			LOGGER.error("Update update Parameter (id:" + param.getParamId() + ")  fail");
			return new Result(false, "更新参数失败！");
		}
	}

	public Result delete(int id) {
		int result = parameterService.delete(id);
		if (result > 0) {
			LOGGER.info("Update deleteParameterDispath success");
			return new Result(true, "删除参数成功！");
		} else {
			LOGGER.error("Update deleteParameterDispath fail");
			return new Result(false, "删除参数失败！");
		}
	}

	public Result delete(String idString) {
		String[] idArray = idString.split(",");
		List<Integer> idList = new ArrayList<Integer>();
		for (String id : idArray) {
			idList.add(new Integer(id));
		}
		int result = parameterService.delete(idList);
		if (result > 0) {
			LOGGER.info("Update deleteParameterDispath success");
			return new Result(true, "删除参数成功！");
		} else {
			LOGGER.error("Update deleteParameterDispath fail");
			return new Result(false, "删除参数失败！");
		}
	}

	/**
	 * @author yangqinglin
	 * @param paramName
	 * @return 在数据库中查询是否存在相同名称的参数
	 * @version 1.0 2015年10月21日
	 */
	public boolean checkName(Integer tenantId, String paramName) {
		try {
			return parameterService.selectParamByName(tenantId, paramName) == null ? true : false;
		} catch (Exception e) {
			LOGGER.error("select param by paramName[" + paramName + "] falied!", e);
			return false;
		}
	}

	/**
	 * @author luogan
	 * @param advancedSearchParam
	 * @return
	 * @version 1.0 2015年10月21日
	 */
	public GridBean advancedSearchParam(Integer userId, int pagenum, int pagesize, Parameter parameter,
			JSONObject json_object) {
		try {
			LOGGER.info("Advanced search param success,User(ID:" + userId + ")");
			return parameterService.advancedSearchParam(userId, pagenum, pagesize, parameter, json_object);
		} catch (Exception e) {
			LOGGER.info("Advanced search param fail,User(ID:" + userId + ")");
			return null;
		}
	}

	/**
	 * @return JSONArray
	 * @version 1.0 2015年8月28日
	 * @description 查询所有可以待选的参数
	 */
	public JSONArray pubParams() {
		try {
			return parameterService.pubParams();
		} catch (Exception e) {
			LOGGER.info("List all public faile .", e);
			return null;
		}
	}
}

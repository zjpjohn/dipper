/**
 * 
 */
package com.cmbc.devops.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cmbc.devops.bean.GridBean;
import com.cmbc.devops.constant.NormalConstant;
import com.cmbc.devops.constant.Status;
import com.cmbc.devops.dao.ParameterMapper;
import com.cmbc.devops.entity.Parameter;
import com.cmbc.devops.service.ParameterService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

/**
 * date：2015年8月21日 下午5:12:00 project name：cmbc-devops-service
 * 
 * @author mayh
 * @version 1.0
 * @since JDK 1.7.0_21 file name：ParameterServiceImpl.java description：
 */
@Component
public class ParameterServiceImpl implements ParameterService {
	private static Logger logger = Logger.getLogger(ParameterServiceImpl.class);
	@Resource
	private ParameterMapper parameterMapper;

	@Override
	public GridBean list(Integer userId, Integer tenantId, String name, int pageNum, int pageSize) throws Exception {
		PageHelper.startPage(pageNum, pageSize);
		Parameter parameter = new Parameter();
		parameter.setParamStatus((byte) Status.PARAMETER.ACTIVATE.ordinal());
		if (null != name) {
			parameter.setParamName(name);
		}
		if (null != userId) {
			parameter.setParamCreator(userId);
		}
		/** @date:2016年3月29日 添加租户维度 */
		parameter.setTenantId(tenantId);
		List<Parameter> parameters = parameterMapper.selectAll(parameter);
		int total = ((Page<?>) parameters).getPages();
		int records = (int) ((Page<?>) parameters).getTotal();
		GridBean gridbean = new GridBean(pageNum, total, records, parameters);
		return gridbean;
	}

	@Override
	public int create(Parameter param) {
		try {
			return parameterMapper.insertParameter(param);
		} catch (Exception e) {
			logger.error("createParameter fail", e);
			return 0;
		}
	}

	@Override
	public int update(Parameter param) {
		try {
			return parameterMapper.updateParameter(param);
		} catch (Exception e) {
			logger.error("update parameter fail", e);
			return 0;
		}
	}

	@Override
	public int delete(List<Integer> ids) {
		try {
			parameterMapper.update(ids);
			return 1;
		} catch (Exception e) {
			logger.error("delete parameter fail", e);
			return 0;
		}
	}

	@Override
	public List<Parameter> selectMultiParams(List<Integer> paramIdList) throws Exception {
		List<Parameter> params = parameterMapper.selectMultiParams(paramIdList);
		return params;
	}

	@Override
	public Integer batchInsertParams(List<Parameter> paramList) throws Exception {
		return parameterMapper.batchInsertParams(paramList);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cmbc.devops.service.ParameterService#allParam(net.sf.json.
	 * JSONObject)
	 */
	/** @date:2016年3月29日 添加租户维度 */
	@Override
	public JSONArray allParam(Integer tenantId) {
		try {
			Parameter parameter = new Parameter();
			parameter.setParamStatus((byte) Status.PARAMETER.ACTIVATE.ordinal());
			parameter.setTenantId(tenantId);
			List<Parameter> list = parameterMapper.selectAll(parameter);
			return (JSONArray) JSONArray.toJSON(list);
		} catch (Exception e) {
			logger.error("get all value fail", e);
			return null;
		}
	}

	@Override
	public int delete(Integer paramId) {
		try {
			return this.parameterMapper.deleteParameter(paramId);
		} catch (Exception e) {
			logger.error("remove param error:", e);
			return 0;
		}
	}

	@Override
	public Parameter selectParamByName(Integer tenantId, String paramName) throws Exception {
		Parameter sel_param = new Parameter();
		sel_param.setParamName(paramName);
		sel_param.setTenantId(tenantId);
		return parameterMapper.selectParamByName(sel_param);
	}

	@Override
	public GridBean allList(Integer owner, int pageNum, int pageSize, Parameter parameter) throws Exception {
		PageHelper.startPage(pageNum, pageSize);
		parameter.setParamStatus((byte) Status.PARAMETER.ACTIVATE.ordinal());
		if (null != parameter.getParamName()) {
			parameter.setParamName(parameter.getParamName());
		}
		if (null != owner) {
			parameter.setParamCreator(owner);
		}
		List<Parameter> parameters = parameterMapper.selectAll(parameter);
		int total = ((Page<?>) parameters).getPages();
		int records = (int) ((Page<?>) parameters).getTotal();
		GridBean gridbean = new GridBean(pageNum, total, records, parameters);
		return gridbean;
	}

	@Override
	public GridBean advancedSearchParam(Integer userId, int pagenum, int pagesize, Parameter parameter,
			JSONObject json_object) throws Exception {
		PageHelper.startPage(pagenum, pagesize);
		/* 组装应用查询数据的条件 */
		// Application application = new Application();
		// application.setAppStatus((byte) Status.APPLICATION.NORMAL.ordinal());
		Parameter parameterSraech = new Parameter();
		parameterSraech.setParamStatus((byte) Status.PARAMETER.ACTIVATE.ordinal());
		/* 获取用户填写的各项查询条件 */
		String[] params = json_object.getString("params").split(",");
		String[] values = json_object.getString("values").split(",");

		/* 遍历填充各项查询条件 */
		for (int array_count = 0, array_length = params.length; array_count < array_length; array_count++) {
			switch (params[array_count].trim()) {
			case "1":
				parameterSraech.setParamName(values[array_count].trim());
				break;
			case "2":
				parameterSraech.setParamValue(values[array_count].trim());
				break;
			case "3":
				parameterSraech.setParamDesc(values[array_count].trim());
				break;
			default:
				break;
			}
		}

		/** @date:2016年3月29日 添加租户维度 */
		parameterSraech.setTenantId(parameter.getTenantId());
		List<Parameter> parameters = parameterMapper.selectAll(parameterSraech);

		int totalpage = ((Page<?>) parameters).getPages();
		Long totalNum = ((Page<?>) parameters).getTotal();

		GridBean gridBean = new GridBean(pagenum, totalpage, totalNum.intValue(), parameters);
		return gridBean;

	}

	/** @date:2016年3月29日 添加租户维度 */
	@Override
	public JSONArray pubParams() throws Exception {
		try {
			Parameter parameter = new Parameter();
			parameter.setTenantId(NormalConstant.ADMIN_TENANTID);
			List<Parameter> list = parameterMapper.selectAll(parameter);
			return (JSONArray) JSONArray.toJSON(list);
		} catch (Exception e) {
			logger.error("get all value fail", e);
			return null;
		}
	}

	@Override
	public List<Parameter> selectAll(Parameter selParam) throws Exception {
		return parameterMapper.selectAll(selParam);
	}
}

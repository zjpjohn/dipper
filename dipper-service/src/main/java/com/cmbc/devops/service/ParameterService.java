/**
 * 
 */
package com.cmbc.devops.service;

import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cmbc.devops.bean.GridBean;
import com.cmbc.devops.entity.Parameter;

/**
 * 参数服务接口
 * 
 * @author dmw
 *
 */
public interface ParameterService {
	/**
	 * 参数分页
	 * 
	 * @param owner
	 *            所有者
	 * @param name
	 *            参数名称
	 * @param pageNum
	 *            当前页
	 * @param pageSize
	 *            页面行数
	 * @return
	 * @throws Exception
	 */
	public GridBean list(Integer userId, Integer tenantId, String name, int pageNum, int pageSize) throws Exception;

	/**
	 * 添加参数
	 * 
	 * @param param
	 * @return
	 */
	public int create(Parameter param);

	/**
	 * 更新参数
	 * 
	 * @param param
	 * @return
	 */
	public int update(Parameter param);

	/**
	 * 删除参数
	 * 
	 * @param ids
	 * @return
	 */
	public int delete(List<Integer> ids);

	/**
	 * 删除参数
	 * 
	 * @param paramId
	 * @return
	 */
	public int delete(Integer paramId);

	/**
	 * @author langzi
	 * @param paramName
	 * @return
	 * @version 1.0 2015年10月21日
	 * @throws Exception
	 */
	public Parameter selectParamByName(Integer tenantId, String paramName) throws Exception;

	/**
	 * @author langzi
	 * @return
	 * @version 1.0 2015年10月21日
	 */
	public JSONArray allParam(Integer tenantId);

	/**
	 * 参数分页
	 * 
	 * @param owner
	 *            所有者
	 * @param name
	 *            用户名称
	 * @param pageNum
	 *            当前页
	 * @param pageSize
	 *            页面行数
	 * @return
	 * @throws Exception
	 */
	public GridBean allList(Integer owner, int pageNum, int pageSize, Parameter parameter) throws Exception;

	/**
	 * @author luogan 参数列表高级查询
	 * @throws Exception
	 */
	public GridBean advancedSearchParam(Integer userId, int pagenum, int pagesize, Parameter parameter,
			JSONObject json_object) throws Exception;

	/**
	 * @return
	 * @throws Exception
	 */
	public JSONArray pubParams() throws Exception;

	public List<Parameter> selectMultiParams(List<Integer> paramIdList) throws Exception;

	public Integer batchInsertParams(List<Parameter> paramList) throws Exception;

	/** 查询获取全部参数 **/
	public List<Parameter> selectAll(Parameter selParam) throws Exception;
}

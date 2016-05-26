package com.cmbc.devops.service;

import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.cmbc.devops.bean.GridBean;
import com.cmbc.devops.entity.Software;

/**
 * date：2016年1月5日 下午2:20:17 project name：cmbc-devops-service
 */
public interface SoftwareService {

	/**
	 * 基于用户的租户ID查询所有应用
	 */
	public List<Software> listAllByType(int softType) throws Exception;

	/**
	 * 列出单页软件信息
	 * 
	 * @param int
	 *            pagenumber
	 * @param int
	 *            pagesize
	 * @param int
	 *            tenantId
	 * @return
	 * @throws Exception
	 */
	public GridBean listOnePageSofts(int pagenumber, int pagesize, int tenantId) throws Exception;

	/**
	 * 通过软件id获取软件详情
	 * 
	 * @param softid
	 * @return
	 * @throws Exception
	 */
	public Software getOneById(int softid) throws Exception;

	/**
	 * 更新软件信息
	 * 
	 * @param Software
	 *            software
	 * @return
	 * @throws Exception
	 */
	public int updateSoft(Software software) throws Exception;

	/**
	 * 新增软件记录的处理
	 * 
	 * @param Software
	 *            software
	 * @return
	 * @throws Exception
	 */
	public int insertSoft(Software software) throws Exception;

	/**
	 * 删除软件记录
	 * 
	 * @param List<Integer>
	 *            softIdList
	 * @return
	 * @throws Exception
	 */
	public int deleteSofts(List<Integer> softIdList) throws Exception;

	/**
	 * 通过hostid获取已安装软件列表
	 * 
	 * @param hostId
	 * @return
	 * @throws Exception
	 */
	public List<Software> getListByHostId(Integer hostId) throws Exception;

	/**
	 * 模糊查询获取软件的列表信息
	 * 
	 * @param int
	 *            pagenumber
	 * @param int
	 *            pagesize
	 * @param int
	 *            tenantId
	 * @return
	 * @throws Exception
	 */
	public GridBean listSearch(Integer userId, int pagenumber, int pagesize, Software software) throws Exception;

	public JSONArray advancedSearch(Integer userId,  Software software) throws Exception;

	public Software getSoftByName(String softName)throws Exception;
}

package com.cmbc.devops.service;

import java.util.List;

import com.cmbc.devops.bean.GridBean;
import com.cmbc.devops.entity.Authority;

/**  
 * date：2015年8月14日 上午10:33:39  
 * project name：cmbc-devops-service  
 * @author langzi  
 * @version 1.0   
 * @since JDK 1.7.0_21  
 * file name：UserService.java  
 * description：  
 */
public interface AuthorityService {
	
	
	/**用户分页
	 * @param owner 所有者
	 * @param name 用户名称
	 * @param pageNum 当前页
	 * @param pageSize 页面行数
	 * @return
	 * @throws Exception 
	 */
	public GridBean list(Integer owner,int pageNum, int pageSize,Authority authority) throws Exception;
	
	/**更新用户
	 * @param param
	 * @return
	 */
	public int update(Authority authority);
	
	public abstract List<Authority> getAllAuthList(int userId, Authority authority) throws Exception;
	
	public abstract List<Authority> ListByActionParentId( Integer id) throws Exception;
	
	public List<Authority> listAuthsByUserId(Integer userId) throws Exception;
	
	/**
	 * @author luogan
	 * 权限列表高级查询
	 * @throws Exception 
	 */
	public GridBean advancedSearchAuth(Integer userId, int pagenum, int pagesize, Authority authority,com.alibaba.fastjson.JSONObject json_object) throws Exception;
	
	public  List<Authority> getAuthListByRoleId(Integer roleId,Integer authId) throws Exception;
	
}

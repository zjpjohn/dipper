package com.cmbc.devops.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.cmbc.devops.bean.GridBean;
import com.cmbc.devops.constant.Type;
import com.cmbc.devops.dao.AuthorityMapper;
import com.cmbc.devops.entity.Authority;
import com.cmbc.devops.service.AuthorityService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

/**
 * date：2015年8月14日 上午10:48:23 project name：cmbc-devops-service
 * 
 * @author langzi
 * @version 1.0
 * @param <AuthorityMapper>
 * @since JDK 1.7.0_21 file name：UserServiceImpl.java description：
 */
@Component
public class AuthorityServiceImpl implements AuthorityService {

	private static final Logger logger = Logger.getLogger(AuthorityServiceImpl.class);
	@Resource
	private AuthorityMapper authorityMapper;

	@Override
	public GridBean list(Integer owner, int pageNum, int pageSize, Authority authority) throws Exception {

		PageHelper.startPage(pageNum, pageSize);
		if (null != authority.getActionName()) {
			authority.setActionName(authority.getActionName());
		}
		if (null != owner) {
		}
		List<Authority> authoritys = authorityMapper.selectAll(authority);
		int total = ((Page<?>) authoritys).getPages();
		int records = (int) ((Page<?>) authoritys).getTotal();
		GridBean gridbean = new GridBean(pageNum, total, records, authoritys);
		return gridbean;
	}

	@Override
	public int update(Authority authority) {

		try {
			return authorityMapper.update(authority);
		} catch (Exception e) {
			logger.error("update authority fail" + e);
			return 0;
		}

	}

	@Override
	public List<Authority> getAllAuthList(int userId, Authority authority) throws Exception {
		List<Authority> auths = authorityMapper.selectAll(authority);
		return auths;
	}

	@Override
	public List<Authority> ListByActionParentId(Integer actionParentId) throws Exception {

		List<Authority> authParents = authorityMapper.
				selectAuthoritiesByActionParentId(actionParentId);
		return authParents;
	}

	@Override
	public List<Authority> listAuthsByUserId(Integer userId) throws Exception {
		return authorityMapper.selectAuthoritiesByUserId(userId);
	}

	@Override
	public GridBean advancedSearchAuth(Integer userId, int pagenum, int pagesize, Authority authority,
			com.alibaba.fastjson.JSONObject json_object) throws Exception {

		PageHelper.startPage(pagenum, pagesize);
		/* 组装应用查询数据的条件 */
		Authority authoritySraech = new Authority();
		/* 获取用户填写的各项查询条件 */
		String[] params = json_object.getString("params").split(",");
		String[] values = json_object.getString("values").split(",");

		/* 遍历填充各项查询条件 */
		for (int i = 0; i < params.length; i++) {
			switch (params[i].trim()) {
			case "1": 
				authoritySraech.setActionName(values[i].trim());
				break;
			case "2": 
				authoritySraech.setActionDesc(values[i].trim());
				break;
			case "3": 
				authoritySraech.setActionRelativeUrl(values[i].trim());
				break;
			case "4": 
				/** @bug211_begin: [权限管理]高级查询,以权限类型为条件列,检索无效 */
				if ("父节点".indexOf(values[i].trim()) != -1) {
					authoritySraech.setActionType((byte) Type.AUTHORITY.PARENT.ordinal());
				} else if ("子节点".indexOf(values[i].trim()) != -1) {
					authoritySraech.setActionType((byte) Type.AUTHORITY.CHILD.ordinal());
				} else {
					authoritySraech.setActionType((byte) Integer.MAX_VALUE);
				}
				/** @bug211_finish */
				break;
			case "5": 
				authoritySraech.setActionRemarks(values[i].trim());
				break;
			default:
				break;
			}
		}
		List<Authority> authoritys = authorityMapper.selectAll(authoritySraech);
		int totalpage = ((Page<?>) authoritys).getPages();
		Long totalNum = ((Page<?>) authoritys).getTotal();
		GridBean gridBean = new GridBean(pagenum, totalpage, totalNum.intValue(), authoritys);
		return gridBean;
	}

	@Override
	public List<Authority> getAuthListByRoleId(Integer roleId, Integer authId) throws Exception {
		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put("roleId", roleId);
		map.put("authId", authId);
		List<Authority> authParents = authorityMapper.selectAuthoritiesByRoleId(map);
		return authParents;
	}

}

package com.cmbc.devops.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONArray;
import com.cmbc.devops.bean.GridBean;
import com.cmbc.devops.dao.SoftwareMapper;
import com.cmbc.devops.dao.UserMapper;
import com.cmbc.devops.entity.Software;
import com.cmbc.devops.entity.SoftwareWithCreator;
import com.cmbc.devops.entity.User;
import com.cmbc.devops.service.SoftwareService;
import com.cmbc.devops.service.TenantService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

/**
 * date：2016年1月5日 下午2:20:53 project name：cmbc-devops-service
 * 
 * @author langzi
 * @version 1.0
 * @since JDK 1.7.0_21 file name：AppServiceImpl.java description：
 */
@Component
public class SoftwareServiceImpl implements SoftwareService {

	@Autowired
	private UserMapper userMapper;
	@Autowired
	private SoftwareMapper softMapper;

	private static final Logger logger = Logger.getLogger(TenantService.class);

	@Override
	public List<Software> listAllByType(int softType) throws Exception {
		Software selSoft = new Software();
		selSoft.setSwType((byte) softType);
		return softMapper.selectAll(selSoft);
	}

	@Override
	public int updateSoft(Software software) throws Exception {
		return softMapper.updateByPrimaryKeySelective(software);
	}

	@Override
	public int insertSoft(Software software) throws Exception {
		return softMapper.insertSelective(software);
	}

	/** 返回列表查询的单页租户信息 */
	@Override
	public GridBean listOnePageSofts(int pagenumber, int pagesize, int tenantId) throws Exception {
		PageHelper.startPage(pagenumber, pagesize);
		Software selSoft = new Software();
		List<Software> softList = softMapper.selectAll(selSoft);
		List<User> userList = userMapper.fetchAllUser();

		if (softList.isEmpty()) {
			logger.info("tenant list is empty");
			return new GridBean(pagenumber, 0, 0, softList);
		} else {
			Page<SoftwareWithCreator> pageObject = new Page<SoftwareWithCreator>();
			for (Software sinSoft : softList) {
				SoftwareWithCreator softWithCreator = new SoftwareWithCreator(sinSoft);
				/* 获取登记人的ID，查询填入登记人的登录名 */
				Integer swUserId = sinSoft.getSwCreator();
				if (swUserId != null) {
					for (User sinUser : userList) {
						if (swUserId == sinUser.getUserId()) {
							softWithCreator.setSwCreatorName(sinUser.getUserName());
						}
					}
				}
				pageObject.add(softWithCreator);
			}

			int totalpage = ((Page<?>) softList).getPages();
			Long totalNum = ((Page<?>) softList).getTotal();
			GridBean gridBean = new GridBean(pagenumber, totalpage, totalNum.intValue(), pageObject);
			return gridBean;
		}
	}

	/**
	 * @author zll 通过id获取软件详情
	 * @date 2016/4/12
	 */
	@Override
	public Software getOneById(int softid) throws Exception {
		return softMapper.selectByPrimaryKey(softid);
	}

	@Override
	public int deleteSofts(List<Integer> softIdList) throws Exception {
		return softMapper.deleteSofts(softIdList);
	}

	@Override
	public List<Software> getListByHostId(Integer hostId) throws Exception {
		return softMapper.getListByHostId(hostId);
	}

	/** 返回列表模糊查询的全部软件信息 */
	@Override
	public GridBean listSearch(Integer userId, int pagenumber, int pagesize, Software software) throws Exception {
		PageHelper.startPage(pagenumber, pagesize);
		Software selSoft = new Software();
		/** 嵌入软件的模糊名称和租户ID信息 */
		selSoft.setSwName(software.getSwName());
		selSoft.setTenantId(software.getTenantId());
		List<Software> softList = softMapper.selectAll(selSoft);
		List<User> userList = userMapper.fetchAllUser();

		if (softList.isEmpty()) {
			logger.info("list Search software list is empty.");
			return new GridBean(pagenumber, 0, 0, softList);
		} else {
			Page<SoftwareWithCreator> pageObject = new Page<SoftwareWithCreator>();
			for (Software sinSoft : softList) {
				SoftwareWithCreator softWithCreator = new SoftwareWithCreator(sinSoft);
				/* 获取登记人的ID，查询填入登记人的登录名 */
				Integer swUserId = sinSoft.getSwCreator();
				if (swUserId != null) {
					for (User sinUser : userList) {
						if (swUserId == sinUser.getUserId()) {
							softWithCreator.setSwCreatorName(sinUser.getUserName());
						}
					}
				}
				pageObject.add(softWithCreator);
			}

			int totalpage = ((Page<?>) softList).getPages();
			Long totalNum = ((Page<?>) softList).getTotal();
			GridBean gridBean = new GridBean(pagenumber, totalpage, totalNum.intValue(), pageObject);
			return gridBean;
		}
	}

	/** 返回列表模糊查询的全部软件信息 */
	@Override
	public JSONArray advancedSearch(Integer userId, Software software) throws Exception {
		/** 嵌入软件的模糊名称和租户ID信息 */
		List<Software> softList = softMapper.selectAll(software);
		List<User> userList = userMapper.fetchAllUser();

		if (softList.isEmpty()) {
			logger.info("list advancedSearch software list is empty.");
			return new JSONArray();
		} else {
			List<SoftwareWithCreator> softInfoList = new ArrayList<SoftwareWithCreator>();
			for (Software sinSoft : softList) {
				SoftwareWithCreator softWithCreator = new SoftwareWithCreator(sinSoft);
				/* 获取登记人的ID，查询填入登记人的登录名 */
				Integer swUserId = sinSoft.getSwCreator();
				if (swUserId != null) {
					for (User sinUser : userList) {
						if (swUserId == sinUser.getUserId()) {
							softWithCreator.setSwCreatorName(sinUser.getUserName());
						}
					}
				}
				softInfoList.add(softWithCreator);
			}
			return (JSONArray) JSONArray.toJSON(softInfoList);
		}
	}

	@Override
	public Software getSoftByName(String softName) throws Exception {
		return softMapper.selectByName(softName);
	}
}

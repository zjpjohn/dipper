package com.cmbc.devops.service.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.cmbc.devops.bean.GridBean;
import com.cmbc.devops.dao.AppMapper;
import com.cmbc.devops.dao.TenantMapper;
import com.cmbc.devops.dao.UserMapper;
import com.cmbc.devops.entity.App;
import com.cmbc.devops.entity.Tenant;
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
public class TenantServiceImpl implements TenantService {

	@Autowired
	private TenantMapper tenantMapper;
	@Autowired
	private UserMapper userMapper;

	@Autowired
	private AppMapper appMapper;

	private static final Logger logger = Logger.getLogger(TenantService.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cmbc.devops.service.AppService#listAll()
	 */
	@Override
	public List<App> listAll() throws Exception {
		return appMapper.selectAll();
	}

	@Override
	public List<App> listAll_TID(int tenant_id) throws Exception {
		App sel_app = new App();
		sel_app.setTenantId(tenant_id);
		return appMapper.selectAll(sel_app);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cmbc.devops.service.AppService#getOnePageApps(int, int)
	 */
	@Override
	public GridBean getOnePageApps(int pagenumber, int pagesize) throws Exception {
		PageHelper.startPage(pagenumber, pagesize);
		// 应用状态（0 删除 1正常）
		List<App> apps = listAll();
		if (apps.isEmpty()) {
			logger.info("app list is empty");
			return null;
		}
		int totalpage = ((Page<?>) apps).getPages();
		Long totalNum = ((Page<?>) apps).getTotal();
		GridBean gridBean = new GridBean(pagenumber, totalpage, totalNum.intValue(), apps);
		return gridBean;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cmbc.devops.service.AppService#getOnePageApps(int, int)
	 */
	/** @date2016_03_25 #多租户部分，增加添加用户ID透传部分 */
	@Override
	public GridBean getOnePageApps(int pagenumber, int pagesize, int tenant_id) throws Exception {

		PageHelper.startPage(pagenumber, pagesize);
		App sel_app = new App();
		sel_app.setTenantId(tenant_id);
		/* 根据资源ID获取对应的全部应用 */
		List<App> apps = appMapper.selectAll(sel_app);
		if (apps.isEmpty()) {
			logger.info("app list is empty");
			return null;
		}
		int totalpage = ((Page<?>) apps).getPages();
		Long totalNum = ((Page<?>) apps).getTotal();
		GridBean gridBean = new GridBean(pagenumber, totalpage, totalNum.intValue(), apps);
		return gridBean;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cmbc.devops.service.AppService#findAppById(int)
	 */
	@Override
	public App findAppById(int tenant_id, int appId) throws Exception {
		return appMapper.select(appId);
	}

	/**2016年4月6日*/
	@Override
	public int addTenant(Tenant tenant) throws Exception {
		return tenantMapper.insertTenant(tenant);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cmbc.devops.service.AppService#modifyApp(com.cmbc.devops.entity.App)
	 */
	@Override
	public int modifyApp(App app) throws Exception {
		return appMapper.update(app);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cmbc.devops.service.AppService#removeApp(int)
	 */
	@Override
	public int removeApp(int appId) throws Exception {
		return appMapper.delete(appId);
	}

	@Override
	public int findLastAppId() throws Exception {
		Integer lastConid = appMapper.selectLastAppId();
		return lastConid == null ? 0 : lastConid;
	}

	@Override
	public List<App> getAppByLbId(int lbId) throws Exception {
		App sel_app = new App();
		sel_app.setBalanceId(lbId);
		return appMapper.selectAppInLb(sel_app);
	}

	@Override
	public List<App> listAppInLb(int lbId, int tenent_id) throws Exception {
		App sel_app = new App();
		sel_app.setBalanceId(lbId);
		sel_app.setTenantId(tenent_id);
		return appMapper.selectAppInLb(sel_app);
	}

	@Override
	public List<App> listAppNotInLb(int tenant_id) throws Exception {
		return appMapper.selectAppNotInLb(tenant_id);
	}

	@Override
	public GridBean listSearchApps(int tenant_id, int pagenum, int pagesize, String fuzzyName) throws Exception {
		PageHelper.startPage(pagenum, pagesize);
		// 应用状态（0 删除 1正常）
		App sel_app = new App();
		sel_app.setAppName(fuzzyName);
		sel_app.setTenantId(tenant_id);
		List<App> apps = appMapper.selectAll(sel_app);
		if (apps.isEmpty()) {
			logger.info("select app list by fuzzyName is empty.");
			return null;
		}
		int totalpage = ((Page<?>) apps).getPages();
		Long totalNum = ((Page<?>) apps).getTotal();
		GridBean gridBean = new GridBean(pagenum, totalpage, totalNum.intValue(), apps);
		return gridBean;
	}

	@Override
	public GridBean advancedSearchApp(Integer tenant_id, int pagenumber, int pagesize, JSONObject json_object)
			throws Exception {
		PageHelper.startPage(pagenumber, pagesize);
		App sel_app = new App();

		/* 添加所有的查询信息 */
		/* 获取用户填写的各项查询条件 */
		String[] params = json_object.getString("params").split(",");
		String[] values = json_object.getString("values").split(",");

		/* 遍历填充各项查询条件 */
		for (int array_count = 0, array_length = params.length; array_count < array_length; array_count++) {
			switch (params[array_count].trim()) {
			/* 应用名称 */
			case ("1"): {
				sel_app.setAppName(values[array_count].trim());
				break;
			}
			/* 访问路径 */
			case "2": {
				sel_app.setAppUrl(values[array_count].trim());
				break;
			}
			/* 应用描述 */
			case "3": {
				sel_app.setAppDesc(values[array_count].trim());
				break;
			}
			}
		}

		/* 填入租户的ID信息 */
		sel_app.setTenantId(tenant_id);

		List<App> app_list = appMapper.selectAll(sel_app);
		int totalpage = ((Page<?>) app_list).getPages();
		Long totalNum = ((Page<?>) app_list).getTotal();

		GridBean gridBean = new GridBean(pagenumber, totalpage, totalNum.intValue(), app_list);
		return gridBean;

	}

	/** 返回列表查询的单页租户信息 */
	@Override
	public GridBean listOnePageTenants(int pagenumber, int pagesize, int userId) throws Exception {
		PageHelper.startPage(pagenumber, pagesize);
		Tenant selTenant = new Tenant();
		List<Tenant> tenants = tenantMapper.selectAll(selTenant);
		if (tenants.isEmpty()) {
			logger.info("tenant list is empty");
			return new GridBean(pagenumber, 0, 0, tenants);
		} else {
			int totalpage = ((Page<?>) tenants).getPages();
			Long totalNum = ((Page<?>) tenants).getTotal();
			GridBean gridBean = new GridBean(pagenumber, totalpage, totalNum.intValue(), tenants);
			return gridBean;
		}
	}
}

package com.cmbc.devops.service.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cmbc.devops.bean.GridBean;
import com.cmbc.devops.constant.Status;
import com.cmbc.devops.dao.AppMapper;
import com.cmbc.devops.dao.ContainerMapper;
import com.cmbc.devops.entity.Container;
import com.cmbc.devops.model.ApplicationDataModel;
import com.cmbc.devops.model.ApplicationModel;
import com.cmbc.devops.service.ApplicationReleaseService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

/**
 * date：2015年12月10日 上午10:38:52 project name：cmbc-devops-service
 * 
 * @author langzi
 * @version 1.0
 * @since JDK 1.7.0_21 file name：ApplicationReleaseServiceImpl.java description：
 */
@Service
public class ApplicationReleaseServiceImpl implements ApplicationReleaseService {

	private static final Logger logger = Logger.getLogger(ApplicationReleaseService.class);

	@Autowired
	private AppMapper appMapper;
	@Autowired
	private ContainerMapper conMapper;

	@Override
	public GridBean listOnePageReleasedApp(Integer userId, int pagenum, int pagesize, ApplicationModel model)
			throws Exception {
		PageHelper.startPage(pagenum, pagesize);
		
		// 1.获取所有的镜像列表
		List<ApplicationDataModel> images=appMapper.selectAllAppImage(model);
		// 2.判断是否有应用存在
		if (images.isEmpty()) {
			logger.warn("Get application is null");
			return null;
		}
		
		for (ApplicationDataModel appmodel : images) {
			//拼接appurl
			String appUrl = "";
			int appId = appmodel.getAppId();
			Integer balanceId = appmodel.getBalanceId();
			balanceId = balanceId == null ? -1 :balanceId;
			appmodel.setBalanceId(balanceId);
			/*LoadBalance balance = null;
			if (appmodel.getBalanceId() != null) {
				balance = balanceMapper.selectLoadBalance(appmodel.getBalanceId());
			}
			if (balance != null) {
				Host host = hostMapper.selectHost(balance.getLbMainHost());
				if (host != null) {
					if (appmodel.getAppUrl().startsWith("/")) {
						appUrl = "http://" + host.getHostIp() + appmodel.getAppUrl();
					} else {
						appUrl = "http://" + host.getHostIp() + "/" + appmodel.getAppUrl();
					}
				}
			} else {
				appUrl = "应用未对外提供服务，暂时不可访问";
			}*/
			
			if(appmodel.getImageId()==null){
				appmodel.setAppNum(0);
				appmodel.setRunNum(0);
				appmodel.setMaintenanceNum(0);
				appmodel.setAppUrl("");
				appmodel.setAppVersion("没有应用版本信息");
			}else{
				//计算该应用版本下应用实例数
				List<Container> containers = conMapper.selectContainerByAppIdAndImgId(appId, appmodel.getImageId());
				if (!containers.isEmpty()) {
					int totalNum = 0, runNum = 0, maintenanceNum = 0;
					appmodel.setUpdateTime(containers.get(0).getConCreatetime());
					for (Container con : containers) {
						totalNum++;
						if (con.getConPower() == Status.POWER.UP.ordinal()) {
							runNum++;
						} else {
							maintenanceNum++;
						}
					}
					appmodel.setAppNum(totalNum);
					appmodel.setRunNum(runNum);
					appmodel.setMaintenanceNum(maintenanceNum);
					appmodel.setAppUrl(appUrl);
				} else {
					appmodel.setAppNum(0);
					appmodel.setRunNum(0);
					appmodel.setMaintenanceNum(0);
					appmodel.setAppUrl("");
				}
			}
			
			
		}
		int totalPage = ((Page<?>) images).getPages();
		Long totalNum = ((Page<?>) images).getTotal();
		return new GridBean(pagenum, totalPage, totalNum.intValue(), images);
	}


}

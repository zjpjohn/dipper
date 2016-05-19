package com.cmbc.devops.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cmbc.devops.bean.GridBean;
import com.cmbc.devops.bean.Result;
import com.cmbc.devops.dao.LogMapper;
import com.cmbc.devops.dao.UserMapper;
import com.cmbc.devops.entity.Log;
import com.cmbc.devops.entity.LogWithUserName;
import com.cmbc.devops.entity.User;
import com.cmbc.devops.service.LogService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

/**
 * date：2015年8月19日 上午11:48:29 project name：cmbc-devops-service
 * 
 * @author langzi
 * @version 1.0
 * @since JDK 1.7.0_21 file name：LogServiceImpl.java description：
 */
@Service("logService")
public class LogServiceImpl implements LogService {

	@Autowired
	private LogMapper logMapper;
	@Autowired
	private UserMapper userMapper;

	@Override
	public GridBean list(Log log, int pageNum, int pageSize) throws Exception {
		PageHelper.startPage(pageNum, pageSize);
		List<Log> logList = logMapper.selectAll(log);

		Page<LogWithUserName> log_user_list = new Page<LogWithUserName>();
		for (Log single_log : logList) {
			LogWithUserName log_user = new LogWithUserName();
			log_user.setLogId(single_log.getLogId());
			log_user.setLogObject(single_log.getLogObject());
			log_user.setLogAction(single_log.getLogAction());
			log_user.setLogResult(single_log.getLogResult());

			/* 日志中插入用户的名称信息 */
			Integer user_id = single_log.getUserId();
			log_user.setUserId(user_id);
			User user = new User();
			user.setUserId(user_id);
			user = userMapper.selectUser(user);
			if (user != null) {
				log_user.setUserName(user.getUserName());
			}

			log_user.setUserIp(single_log.getUserIp());
			log_user.setLogCreatetime(single_log.getLogCreatetime());
			log_user.setBeginTime(single_log.getBeginTime());
			log_user.setEndTime(single_log.getEndTime());
			log_user.setLogDetail(single_log.getLogDetail());

			log_user_list.add(log_user);
		}

		int totalpage = ((Page<?>) logList).getPages();
		Long totalNum = ((Page<?>) logList).getTotal();
		GridBean gridBean = new GridBean(pageNum, totalpage, totalNum.intValue(), log_user_list);
		return gridBean;
	}

	@Override
	public Result save(Log log) throws Exception {
		int result = this.logMapper.insertLog(log);
		if (result != 0) {
			return new Result(true, "add log success");
		} else {
			return new Result(false, "add log error!");
		}
	}
}

package com.cmbc.devops.controller.action;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cmbc.devops.bean.GridBean;
import com.cmbc.devops.entity.Log;
import com.cmbc.devops.service.LogService;
import com.cmbc.devops.util.TimeUtils;

/**
 * @author luogan 2015年8月13日 上午11:10:10
 */

@Controller
@RequestMapping("log")
public class LogAction {
	private static final Logger LOGGER = Logger.getLogger(LogAction.class);
	@Autowired
	private LogService logService;

	@RequestMapping(value = "/list")
	@ResponseBody
	public GridBean list(@RequestParam(value = "page", required = true) int page,
			@RequestParam(value = "rows", required = true) int rows, String from, String to, String target) {
		Log log = new Log();
		if (null != from && !from.isEmpty()) {
			log.setBeginTime(TimeUtils.stringToDateTime(from));
		}
		if (null != to && !to.isEmpty()) {
			log.setEndTime(TimeUtils.stringToDateTime(to));
		}
		if (null != target && !target.isEmpty()) {
			log.setLogObject(target);
		}
		try {
			return logService.list(log, page, rows);
		} catch (Exception e) {
			LOGGER.error("get log list falied!", e);
			return null;
		}
	}
}

package com.once.crosscloud.loginfo.service;

import java.util.List;
import java.util.Map;

import com.once.crosscloud.loginfo.model.LogInfoEntity;

public interface LogInfoService {

	public int log(LogInfoEntity logInfo);
	
	public List<LogInfoEntity> queryListByPage(Map<String, Object> parameter);
}

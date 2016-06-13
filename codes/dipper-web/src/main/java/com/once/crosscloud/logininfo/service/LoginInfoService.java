package com.once.crosscloud.logininfo.service;

import java.util.List;
import java.util.Map;

import com.once.crosscloud.models.LoginInfoEntity;


public interface LoginInfoService {

	public int log(LoginInfoEntity loginInfo);
	
	public List<LoginInfoEntity> queryListByPage(Map<String, Object> parameter);
}

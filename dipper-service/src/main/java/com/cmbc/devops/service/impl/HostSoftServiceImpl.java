package com.cmbc.devops.service.impl;


import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.cmbc.devops.dao.HostSoftMapper;
import com.cmbc.devops.entity.HostSoft;
import com.cmbc.devops.service.HostSoftService;
@Component
public class HostSoftServiceImpl implements HostSoftService {

	@Resource
	private HostSoftMapper hostsoftMapper;
	@Override
	public int createRecord(HostSoft hs) {
		int result=0;
		HostSoft hostsoft=hostsoftMapper.selectByHostAndSoftId(hs);
		if(hostsoft==null){
			result=hostsoftMapper.insert(hs);
		}
		return result;
	}


}

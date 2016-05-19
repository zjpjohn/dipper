package com.cmbc.devops.dao;

import com.cmbc.devops.entity.HostSoft;

public interface HostSoftMapper {
    int deleteByPrimaryKey(Integer hsId);

    int insert(HostSoft record);

    int insertSelective(HostSoft record);

    HostSoft selectByPrimaryKey(Integer hsId);

    int updateByPrimaryKeySelective(HostSoft record);

    int updateByPrimaryKey(HostSoft record);
    
    HostSoft selectByHostAndSoftId(HostSoft hs);
}
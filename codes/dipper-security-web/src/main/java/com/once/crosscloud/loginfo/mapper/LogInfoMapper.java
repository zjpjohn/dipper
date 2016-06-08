package com.once.crosscloud.loginfo.mapper;

import org.springframework.stereotype.Repository;

import com.once.crosscloud.base.basemapper.BaseMapper;
import com.once.crosscloud.loginfo.model.LogInfoEntity;

@Repository
public interface LogInfoMapper extends BaseMapper<LogInfoEntity, Long> {

}

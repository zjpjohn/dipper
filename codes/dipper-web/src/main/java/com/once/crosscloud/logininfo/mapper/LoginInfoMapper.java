package com.once.crosscloud.logininfo.mapper;

import org.springframework.stereotype.Repository;

import com.once.crosscloud.base.basemapper.BaseMapper;
import com.once.crosscloud.logininfo.model.LoginInfoEntity;

@Repository
public interface LoginInfoMapper extends BaseMapper<LoginInfoEntity, Long> {

}

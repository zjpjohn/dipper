package com.cmbc.devops.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cmbc.devops.dao.AppEnvImgMapper;
import com.cmbc.devops.entity.AppEnvImg;
import com.cmbc.devops.service.AppEnvImgService;

@Component
public class AppEnvImgServiceImpl implements AppEnvImgService {

	@Autowired
	private AppEnvImgMapper aeiMapper;

	@Override
	public List<AppEnvImg> listAllByAppId(int appId) throws Exception {
		return aeiMapper.selectAllByAppId(appId);
	}

	@Override
	public List<AppEnvImg> listAllByEnvId(int envId) throws Exception {
		return aeiMapper.selectAllByEnvId(envId);
	}

	@Override
	public List<AppEnvImg> listAllByImgId(int imgId) throws Exception {
		return aeiMapper.selectAllByImgId(imgId);
	}

	@Override
	public void insert(AppEnvImg aei) throws Exception {
		aeiMapper.insert(aei);
	}

	@Override
	public int batchInsert(List<AppEnvImg> aeiList) throws Exception {
		return aeiMapper.batchInsertAEIs(aeiList);
	}

	@Override
	public int removeByAppId(int appId) throws Exception {
		return aeiMapper.deleteByAppId(appId);
	}

	@Override
	public int removeByEnvId(int envId) throws Exception {
		return aeiMapper.deleteByEnvId(envId);
	}

	@Override
	public int removeByImgId(int imgId) throws Exception {
		return aeiMapper.deleteByImgId(imgId);
	}

	@Override
	public int update(AppEnvImg aei) throws Exception {
		return aeiMapper.update(aei);
	}

	@Override
	public List<AppEnvImg> listByAppIdAndImageId(Integer appid, Integer imageid) throws Exception {
		AppEnvImg aei=new AppEnvImg();
		aei.setAppId(appid);
		aei.setImgId(imageid);
		return aeiMapper.listByAppIdAndImageId(aei);
	}

	@Override
	public void removeById(Integer appId, Integer imageId) {
		AppEnvImg aei=new AppEnvImg();
		aei.setAppId(appId);
		aei.setImgId(imageId);
		aeiMapper.deleteById(aei);
	}
}

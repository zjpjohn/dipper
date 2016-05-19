package com.cmbc.devops.service.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.cmbc.devops.bean.GridBean;
import com.cmbc.devops.dao.EnvMapper;
import com.cmbc.devops.dao.UserMapper;
import com.cmbc.devops.entity.Env;
import com.cmbc.devops.entity.EnvWithUser;
import com.cmbc.devops.entity.User;
import com.cmbc.devops.service.EnvService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

/**
 * date：2016年1月11日 下午2:27:40 project name：cmbc-devops-service
 * 
 * @author langzi
 * @version 1.0
 * @since JDK 1.7.0_21 file name：EnvServiceImpl.java description：
 */
@Component
public class EnvServiceImpl implements EnvService {

	@Autowired
	private EnvMapper mapper;
	@Autowired
	private UserMapper userMapper;

	private static final Logger logger = Logger.getLogger(EnvService.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cmbc.devops.service.EnvService#listAll()
	 */
	@Override
	public List<Env> listAll() throws Exception {
		return mapper.selectAll();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cmbc.devops.service.EnvService#getOnePageEnvs(int, int)
	 */
	@Override
	public GridBean getOnePageEnvs(int pagenumber, int pagesize) throws Exception {
		PageHelper.startPage(pagenumber, pagesize);
		List<Env> envs = listAll();

		Page<EnvWithUser> envuser_list = new Page<EnvWithUser>();

		/** 获取全部用户列表 **/
		List<User> userList = userMapper.fetchAllUser();

		if (envs.isEmpty()) {
			logger.info("环境列表为空，请检查。");
			return null;
		} else {
			for (Env single_env : envs) {
				EnvWithUser env_user = new EnvWithUser(single_env);

				/** 处理用户信息部分 **/
				Integer user_id = single_env.getEnvCreator();
				if (user_id != null) {
					for (User sinUser : userList) {
						if (sinUser.getUserId() == user_id) {
							env_user.setUserName(sinUser.getUserName());
						}
					}
				}
				envuser_list.add(env_user);
			}
		}

		int totalpage = ((Page<?>) envs).getPages();
		Long totalNum = ((Page<?>) envs).getTotal();
		GridBean gridBean = new GridBean(pagenumber, totalpage, totalNum.intValue(), envuser_list);
		return gridBean;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cmbc.devops.service.EnvService#find(int)
	 */
	@Override
	public Env find(int envId) throws Exception {
		return mapper.select(envId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cmbc.devops.service.EnvService#add(com.cmbc.devops.entity.Env)
	 */
	@Override
	public int add(Env env) throws Exception {
		return mapper.insert(env);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cmbc.devops.service.EnvService#modify(com.cmbc.devops.entity.Env)
	 */
	@Override
	public int modify(Env env) throws Exception {
		return mapper.update(env);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cmbc.devops.service.EnvService#remove(int)
	 */
	@Override
	public int remove(int envId) throws Exception {
		return mapper.delete(envId);
	}

	@Override
	public List<Env> listByAppId(Integer appId) throws Exception {
		return mapper.listByAppId(appId);
	}

	@Override
	public Env getEnvByName(String envName) throws Exception {
		List<Env> env_list = mapper.selectAll();
		for (Env single_env : env_list) {
			if (single_env.getEnvName().equals(envName)) {
				return single_env;
			}
		}
		return null;
	}

	@Override
	public GridBean searchAllEnv(Integer userId, int pagenumber, int pagesize, String search_name) throws Exception {
		PageHelper.startPage(pagenumber, pagesize);
		Env env = new Env();
		env.setEnvName(search_name);
		List<Env> envs = mapper.selectAllEnvs(env);

		Page<EnvWithUser> envuser_list = new Page<EnvWithUser>();
		/** 获取全部用户列表 **/
		List<User> userList = userMapper.fetchAllUser();

		if (envs.isEmpty()) {
			logger.info("环境列表为空，请检查。");
			return new GridBean(pagenumber, 0, 0, envuser_list);
		} else {
			for (Env single_env : envs) {
				EnvWithUser env_user = new EnvWithUser(single_env);

				/** 处理用户信息部分 **/
				Integer user_id = single_env.getEnvCreator();

				if (user_id != null) {
					for (User sinUser : userList) {
						if (sinUser.getUserId() == user_id) {
							env_user.setUserName(sinUser.getUserName());
						}
					}
				}
				envuser_list.add(env_user);
			}
		}

		int totalpage = ((Page<?>) envs).getPages();
		Long totalNum = ((Page<?>) envs).getTotal();
		GridBean gridBean = new GridBean(pagenumber, totalpage, totalNum.intValue(), envuser_list);
		return gridBean;
	}

	@Override
	public GridBean advancedSearchEnvs(Integer userId, int pagenumber, int pagesize, JSONObject json_object)
			throws Exception {
		PageHelper.startPage(pagenumber, pagesize);
		Env env = new Env();
		/* 获取用户填写的各项查询条件 */
		String[] params = json_object.getString("params").split(",");
		String[] values = json_object.getString("values").split(",");

		/* 遍历填充各项查询条件 */
		for (int array_count = 0, array_length = params.length; array_count < array_length; array_count++) {
			switch (params[array_count].trim()) {
			case "1":
				env.setEnvName(values[array_count].trim());
				break;
			case "2":
				env.setEnvDesc(values[array_count].trim());
				break;
			default:
				break;
			}
		}
		List<Env> envs = mapper.selectAllEnvs(env);

		Page<EnvWithUser> envuser_list = new Page<EnvWithUser>();
		/** 获取全部用户列表 **/
		List<User> userList = userMapper.fetchAllUser();

		if (envs.isEmpty()) {
			logger.info("高级查询环境列表为空，请检查。");
			return new GridBean(pagenumber, 0, 0, envuser_list);
		} else {
			for (Env single_env : envs) {
				EnvWithUser env_user = new EnvWithUser(single_env);

				/** 处理用户信息部分 **/
				Integer user_id = single_env.getEnvCreator();
				if (user_id != null) {
					for (User sinUser : userList) {
						if (sinUser.getUserId() == user_id) {
							env_user.setUserName(sinUser.getUserName());
						}
					}
				}
				envuser_list.add(env_user);
			}
		}

		int totalpage = ((Page<?>) envs).getPages();
		Long totalNum = ((Page<?>) envs).getTotal();
		GridBean gridBean = new GridBean(pagenumber, totalpage, totalNum.intValue(), envuser_list);
		return gridBean;
	}

}

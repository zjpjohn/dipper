/**
 * Copyright 2016-2016 Institute of Software, Chinese Academy of Sciences.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.once.crosscloud.controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.once.crosscloud.exceptions.AjaxException;
import com.once.crosscloud.exceptions.ServiceException;
import com.once.crosscloud.models.RoleEntity;
import com.once.crosscloud.models.UserEntity;
import com.once.crosscloud.models.UserInfoEntity;
import com.once.crosscloud.services.RoleService;
import com.once.crosscloud.services.UserService;
import com.once.crosscloud.utils.Common;
import com.once.crosscloud.utils.EndecryptUtils;
import com.once.crosscloud.utils.PageUtil;
import com.once.crosscloud.utils.Pager;
import com.once.crosscloud.utils.RandomUtil;

/**
 * @author wuheng(wuheng@otcaix.iscas.ac.cn)
 * @date   Jun 13, 2016
 *
 */
@Controller
@Scope("prototype")
@RequestMapping("/user/")
public class UserController {

	@Autowired
	private UserService userService;
	
	@Autowired
	private RoleService roleService;
	
	@RequestMapping("listUI.html")
	public String listUI(Model model, HttpServletRequest request) {
		try
		{
			PageUtil page = new PageUtil();
			if(request.getParameterMap().containsKey("page")){
				page.setPageNum(Integer.valueOf(request.getParameter("page")));
				page.setPageSize(Integer.valueOf(request.getParameter("rows")));
				page.setOrderByColumn(request.getParameter("sidx"));
				page.setOrderByType(request.getParameter("sord"));
			}
			model.addAttribute("page", page);
			return Common.BACKGROUND_PATH + "/user/list";
		}catch(Exception e)
		{
			throw new AjaxException(e);
		}
	}
	
	
	/**
	 * ajax分页动态加载模式
	 * @param dtGridPager Pager对象
	 * @throws Exception
	 */
	@RequestMapping(value = "/list.html", method = RequestMethod.POST)
	@ResponseBody
	public Object list(String dtGridPager) throws Exception{
		Map<String, Object> parameters = null;
		// 映射Pager对象
		Pager pager = JSON.parseObject(dtGridPager, Pager.class);
		// 判断是否包含自定义参数
		parameters = pager.getParameters();
		if (parameters.size() < 0) {
			parameters.put("userName", null);
		}
		// 设置分页，page里面包含了分页信息
		Page<Object> page = PageHelper.startPage(pager.getNowPage(),pager.getPageSize(), "u_id DESC");
		List<UserEntity> list = userService.queryListByPage(parameters);
		parameters.clear();
		parameters.put("isSuccess", Boolean.TRUE);
		parameters.put("nowPage", pager.getNowPage());
		parameters.put("pageSize", pager.getPageSize());
		parameters.put("pageCount", page.getPages());
		parameters.put("recordCount", page.getTotal());
		parameters.put("startRecord", page.getStartRow());
		//列表展示数据
		parameters.put("exhibitDatas", list);
		return parameters;
	}
	
	
	
	@RequestMapping("addUI.html")
	public String addUI(Model model) {
		try
		{
			List<RoleEntity> list = roleService.queryListByPage(new HashMap<String, Object>());
			model.addAttribute("roleList", list);
			return Common.BACKGROUND_PATH + "/user/form";
		}catch(Exception e)
		{
			throw new AjaxException(e);
		}
		
	}
	
	@RequestMapping("add.html")
	@ResponseBody
	public Object add(UserEntity userEntity) throws AjaxException
	{
		Map<String, Object> map = new HashMap<String, Object>();
		try
		{
			String password = userEntity.getPassword();
			//获取登录用户
			HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
			UserEntity sessionUser = (UserEntity)request.getSession().getAttribute("userSession");
			// 加密用户输入的密码，得到密码和加密盐，保存到数据库
			UserEntity user = EndecryptUtils.md5Password(userEntity.getAccountName(), userEntity.getPassword(), 2);
			//设置添加用户的密码和加密盐
			userEntity.setPassword(user.getPassword());
			userEntity.setCredentialsSalt(user.getCredentialsSalt());
			//设置创建者姓名
			userEntity.setCreatorName(sessionUser.getUserName());
			userEntity.setCreateTime(new Date());
			//设置锁定状态：未锁定；删除状态：未删除
			userEntity.setLocked(0);
			userEntity.setDeleteStatus(0);
			UserInfoEntity userInfo = new UserInfoEntity();
			userEntity.setUserInfo(userInfo);
			int result = userService.insert(userEntity, password);
			if(result == 1)
			{
				map.put("success", Boolean.TRUE);
				map.put("data", null);
				map.put("message", "添加成功");
			}else
			{
				map.put("success", Boolean.FALSE);
				map.put("data", null);
				map.put("message", "添加失败");
			}
		}catch(ServiceException e)
		{
			throw new AjaxException(e);
		}
		return map;
	}
	
	
	@RequestMapping("editUI.html")
	public String editUI(Model model, HttpServletRequest request, Long id) {
		try
		{
			UserEntity userEntity = userService.findById(id);
			PageUtil page = new PageUtil();
			page.setPageNum(Integer.valueOf(request.getParameter("page")));
			page.setPageSize(Integer.valueOf(request.getParameter("rows")));
			page.setOrderByColumn(request.getParameter("sidx"));
			page.setOrderByType(request.getParameter("sord"));
			
			List<RoleEntity> list = roleService.queryListByPage(new HashMap<String, Object>());
			
			model.addAttribute("page", page);
			model.addAttribute("userEntity", userEntity);
			model.addAttribute("roleList", list);
			return Common.BACKGROUND_PATH + "/user/form";
		}catch(Exception e)
		{
			throw new AjaxException(e);
		}
	}
	
	@RequestMapping("edit.html")
	@ResponseBody
	public Object update(UserEntity userEntity) throws AjaxException
	{
		Map<String, Object> map = new HashMap<String, Object>();
		try
		{
			//获取登录用户
			HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
			UserEntity sessionUser = (UserEntity)request.getSession().getAttribute("userSession");
			//设置创建者姓名
			userEntity.setCreatorName(sessionUser.getCreatorName());
			int result = userService.update(userEntity);
			if(result == 1)
			{
				map.put("success", Boolean.TRUE);
				map.put("data", null);
				map.put("message", "编辑成功");
			}else
			{
				map.put("success", Boolean.FALSE);
				map.put("data", null);
				map.put("message", "编辑失败");
			}
		}catch(Exception e)
		{
			throw new AjaxException(e);
		}
		return map;
	}
	
	
	@RequestMapping("deleteBatch.html")
	@ResponseBody
	public Object deleteBatch(String ids){
		Map<String, Object> result = new HashMap<String, Object>();
		try
		{
			String[] userIds = ids.split(",");
			List<Long> list = new ArrayList<Long>();
			for (String string : userIds) {
				list.add(Long.valueOf(string));
			}
			int cnt = userService.deleteBatchById(list);
			if(cnt == list.size())
			{
				result.put("success", true);
				result.put("data", null);
				result.put("message", "删除成功");
			}else
			{
				result.put("success", false);
				result.put("data", null);
				result.put("message", "删除失败");
			}
		}catch(Exception e)
		{
			throw new AjaxException(e);
		}
		return result;
	}
	
	@RequestMapping("resetPassword.html")
	@ResponseBody
	public Object resetPassword(UserEntity userEntity){
		Map<String, Object> result = new HashMap<String, Object>();
		try
		{
			//生成随机密码
			String password = RandomUtil.generateString(6);
			
			//加密用户输入的密码，得到密码和加密盐，保存到数据库
			UserEntity user = EndecryptUtils.md5Password(userEntity.getAccountName(), password, 2);
			//设置添加用户的密码和加密盐
			userEntity.setPassword(user.getPassword());
			userEntity.setCredentialsSalt(user.getCredentialsSalt());
			if(userEntity.getId() == null)
			{
				user = null;
				user = userService.findByName(userEntity.getAccountName());
				if(user != null)
				{
					userEntity.setId(user.getId());
					userEntity.setUserName(user.getUserName());
					int cnt = userService.updateOnly(userEntity, password);
					if(cnt > 0)
					{
						result.put("success", true);
						result.put("data", null);
						result.put("message", "密码重置成功");
					}else
					{
						result.put("success", false);
						result.put("data", null);
						result.put("message", "密码重置失败");
					}
				}else
				{
					result.put("success", false);
					result.put("data", null);
					result.put("message", "账户不存在");
				}
			}else
			{
				int cnt = userService.updateOnly(userEntity, password);
				if(cnt > 0)
				{
					result.put("success", true);
					result.put("data", null);
					result.put("message", "密码重置成功");
				}else
				{
					result.put("success", false);
					result.put("data", null);
					result.put("message", "密码重置失败");
				}
			}
			
		}catch(Exception e)
		{
			throw new AjaxException(e);
		}
		return result;
	}
	
	@RequestMapping("resetPassWithoutAuthc.html")
	@ResponseBody
	public Object resetPassWithoutAuthc(UserEntity userEntity){
		Map<String, Object> result = new HashMap<String, Object>();
		try
		{
			//生成随机密码
			String password = RandomUtil.generateString(6);
			
			//加密用户输入的密码，得到密码和加密盐，保存到数据库
			UserEntity user = EndecryptUtils.md5Password(userEntity.getAccountName(), password, 2);
			//设置添加用户的密码和加密盐
			userEntity.setPassword(user.getPassword());
			userEntity.setCredentialsSalt(user.getCredentialsSalt());
			if(userEntity.getId() == null)
			{
				user = null;
				user = userService.findByName(userEntity.getAccountName());
				if(user != null)
				{
					userEntity.setId(user.getId());
					userEntity.setUserName(user.getUserName());
					int cnt = userService.updateOnly(userEntity, password);
					if(cnt > 0)
					{
						result.put("success", true);
						result.put("data", null);
						result.put("message", "密码重置成功");
					}else
					{
						result.put("success", false);
						result.put("data", null);
						result.put("message", "密码重置失败");
					}
				}else
				{
					result.put("success", false);
					result.put("data", null);
					result.put("message", "账户不存在");
				}
			}else
			{
				int cnt = userService.updateOnly(userEntity, password);
				if(cnt > 0)
				{
					result.put("success", true);
					result.put("data", null);
					result.put("message", "密码重置成功");
				}else
				{
					result.put("success", false);
					result.put("data", null);
					result.put("message", "密码重置失败");
				}
			}
			
		}catch(Exception e)
		{
			throw new AjaxException(e);
		}
		return result;
	}
	
	
	@RequestMapping("infoUI.html")
	public String infoUI(Model model, Long id) {
		try
		{
			UserEntity userEntity = userService.findById(id);
			model.addAttribute("userEntity", userEntity);
			return Common.BACKGROUND_PATH + "/user/info";
		}catch(Exception e)
		{
			throw new AjaxException(e);
		}
	}
	
	@RequestMapping("info.html")
	@ResponseBody
	public Object info(UserEntity userEntity)
	{
		Map<String, Object> map = new HashMap<String, Object>();
		try
		{
			int result = userService.update(userEntity);
			if(result == 1)
			{
				map.put("success", Boolean.TRUE);
				map.put("data", null);
				map.put("message", "编辑成功");
			}else
			{
				map.put("success", Boolean.FALSE);
				map.put("data", null);
				map.put("message", "编辑失败");
			}
		}catch(Exception e)
		{
			throw new AjaxException(e);
		}
		return map;
	}
	
	
	@RequestMapping("passwordUI.html")
	public String passwordUI(Model model, UserEntity userEntity) {
		try
		{
			model.addAttribute("userEntity", userEntity);
			return Common.BACKGROUND_PATH + "/user/password";
		}catch(Exception e)
		{
			throw new AjaxException(e);
		}
	}
	
	
	@RequestMapping("password.html")
	@ResponseBody
	public Object password(UserEntity userEntity){
		Map<String, Object> result = new HashMap<String, Object>();
		try
		{
			String password = userEntity.getPassword();
			userEntity.setUserName(new String(userEntity.getUserName().getBytes("iso-8859-1"),"utf-8"));
			//加密用户输入的密码，得到密码和加密盐，保存到数据库
			UserEntity user = EndecryptUtils.md5Password(userEntity.getAccountName(), userEntity.getPassword(), 2);
			//设置添加用户的密码和加密盐
			userEntity.setPassword(user.getPassword());
			userEntity.setCredentialsSalt(user.getCredentialsSalt());
			int cnt = userService.updateOnly(userEntity, password);
			if(cnt > 0)
			{
				result.put("success", true);
				result.put("data", null);
				result.put("message", "密码修改成功");
			}else
			{
				result.put("success", false);
				result.put("data", null);
				result.put("message", "密码修改失败");
			}
		}catch(Exception e)
		{
			throw new AjaxException(e);
		}
		return result;
	}
	
	
	@RequestMapping("validateAccountName.html")
	@ResponseBody
	public Object validateAccount(String accountName){
		try
		{
			UserEntity userEntity = userService.findByName(accountName);
			if(userEntity == null)
			{
				return true;
			}else
			{
				return false;
			}
		}catch(Exception e)
		{
			throw new AjaxException(e);
		}
	}
	
	
}

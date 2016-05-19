package com.cmbc.devops.controller.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import com.cmbc.devops.component.CaptchaNumber;
import com.cmbc.devops.constant.Status;
import com.cmbc.devops.entity.Authority;
import com.cmbc.devops.entity.User;
import com.cmbc.devops.manager.AuthorityManager;
import com.cmbc.devops.manager.UserManager;
import com.cmbc.devops.model.LoginModel;
import com.cmbc.devops.service.UserService;
import com.cmbc.devops.util.CaptchaUtil;


@Controller
public class LoginAction {

	private static final Logger LOGGER = Logger.getLogger(LoginAction.class);
	@Resource
	private UserService userService;
	@Resource
	private UserManager userManager;
	@Resource
	private AuthorityManager authorityManager;

	@RequestMapping(value = "/login", method = { RequestMethod.POST })
	public ModelAndView login(HttpServletRequest request, HttpServletResponse response, LoginModel loginRequestModel,
			RedirectAttributes redirectAttributes) {
		LOGGER.info("【" + loginRequestModel.getUserName() + "】尝试登录");
		
		User user=new User();
		//使用addFlashAttribute,参数不会出现在url地址栏中  
		redirectAttributes.addFlashAttribute("userName", loginRequestModel.getUserName());
		redirectAttributes.addFlashAttribute("password", loginRequestModel.getPassword());
		// 判断验证码的session是否过期
		if (loginRequestModel.getVercode() == null || request.getSession().getAttribute("rand") == null) {
			LOGGER.warn("登录失败：验证码失效");
			redirectAttributes.addFlashAttribute("message", "验证码失效");
			return new ModelAndView(new RedirectView("/login.html"));
		} 
		try {
			user = userService.checkLogin(loginRequestModel.getUserName(),loginRequestModel.getPassword());
		} catch (Exception e) {
			LOGGER.error("check user login by userName["+loginRequestModel.getUserName()+"] falied!", e);
			redirectAttributes.addFlashAttribute("message", "用户登录失败，数据库连接异常！");
			return new ModelAndView(new RedirectView("/login.html"));
		}
		//用户不存在
		if (user == null) {
			redirectAttributes.addFlashAttribute("message", "用户不存在！");
			return new ModelAndView(new RedirectView("/login.html"));
		}
		//用户密码错误
		if(user != null && !StringUtils.hasText(user.getUserPass())){
			redirectAttributes.addFlashAttribute("message", "用户密码错误");
			return new ModelAndView(new RedirectView("/login.html"));
		}
		//用户被冻结
		if(user != null && user.getUserStatus() == Status.USER.DELETE.ordinal()){
			LOGGER.warn("登录失败：该用户已被冻结！");
			redirectAttributes.addFlashAttribute("message", "该用户已被冻结,请联系管理员！");
			return new ModelAndView(new RedirectView("/login.html"));
		}
		
		//【二】：判断验证码是否正确
		String captcha = (String) request.getSession().getAttribute("rand");
		if (!loginRequestModel.getVercode().toLowerCase().equals(captcha.toLowerCase())) {
			LOGGER.error("登录失败：验证码不正确");
			redirectAttributes.addFlashAttribute("message", "验证码不正确！");
			return new ModelAndView(new RedirectView("/login.html"));
		} 
		String path = request.getContextPath();
		String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()+ path + "/";
		//获取用户第一个一级菜单权限
		String startPage=new String();
		boolean startFlag=true;
		
		//【三】：获取用户权限信息，存储在session中
		List<String> authlist=new ArrayList<String>();
		List<Authority> listAuths = authorityManager.getUserRoleAuths(user.getUserId());
		if(listAuths.size()==0){
			LOGGER.warn("登录失败：用户没有相关权限！");
			redirectAttributes.addFlashAttribute("message", "用户没有相关权限！");
			return new ModelAndView(new RedirectView("/login.html"));
		}
		//pages： 一级菜单权限、  buttons：二级按钮权限
		String pages = "";
		String buttons = "";
		for(Authority ahr:listAuths){
			if(ahr.getActionRelativeUrl()!=null){
				authlist.add(ahr.getActionRelativeUrl());
			}
			if(ahr.getActionType() == Status.AUTHTYPE.PAGE.ordinal()){
				pages += ahr.getActionRemarks()+",";
				if(startFlag&&ahr.getActionRelativeUrl()!=null){
					startPage=ahr.getActionRelativeUrl();
					startFlag=false;
				}
			}else if(ahr.getActionType() == Status.AUTHTYPE.BUTTON.ordinal()){
				buttons += ahr.getActionRemarks()+",";
			}
		}
		//用户登录成功，设置用户状态为登录状态()
		try {
			user.setUserLoginStatus(request.getSession().getId());
			userService.update(user);
		} catch (Exception e) {
			LOGGER.error("get user by username["+loginRequestModel.getUserName()+"] falied!", e);
			redirectAttributes.addFlashAttribute("message", "用户登录失败，数据库连接异常！");
			return new ModelAndView(new RedirectView("/login.html"));
		}
		
		//权限存储到session中（pagesAuth、buttonsAuth控制界面的权限显示效果，authlist 权限url过滤）
		request.getSession().setAttribute("pagesAuth", pages);
		request.getSession().setAttribute("buttonsAuth", buttons);
		request.getSession().setAttribute("authlist", authlist);
		
		//用户信息和项目url存储在session中
		request.getSession().setAttribute("user", user);
		request.getSession().setAttribute("basePath", basePath);
		
		LOGGER.info(loginRequestModel.getUserName() + "登录成功");
		
		//用户权限中有概览权限则跳到index.html,没有则跳转到第一个一级菜单权限
		if(authlist.contains("/index.html")){
			return new ModelAndView(new RedirectView("/index.html"));
		}else{
			return new ModelAndView(new RedirectView(startPage));
		}
	}

	@RequestMapping("/logout.html")
	public ModelAndView logout(HttpSession session) {
		session.removeAttribute("user");
		return new ModelAndView("redirect:/login.html");
	}

	@RequestMapping(value = "/captcha", method = { RequestMethod.GET })
	public ResponseEntity<byte[]> image(HttpServletRequest request) throws IOException {
		CaptchaNumber capnumber = CaptchaUtil.getCaptchaNumber();
		HttpSession session = request.getSession(true);
		session.setAttribute("rand", capnumber.getTotalNum().toString());
		return new ResponseEntity<byte[]>(CaptchaUtil.getImage(capnumber.getFirNum(), capnumber.getSecNum()),
				CaptchaUtil.getCaptchaHeaders(), HttpStatus.OK);
	}
}

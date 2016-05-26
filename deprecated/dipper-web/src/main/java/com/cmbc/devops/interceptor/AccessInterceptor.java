package com.cmbc.devops.interceptor;

import java.util.Date;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.cmbc.devops.entity.Log;
import com.cmbc.devops.entity.User;
import com.cmbc.devops.service.LogService;
import com.cmbc.devops.util.RequestUtil;

/**
 * 访问拦截器
 * 
 * @author mingwei.dmw
 *
 */
@Repository
public class AccessInterceptor implements HandlerInterceptor {

	private List<String> ignoreUrls;
	private static Logger logger = Logger.getLogger(AccessInterceptor.class);
	private Log log;
	
	@Autowired
	private LogService logService;

	public void setIgnoreUrls(List<String> ignoreUrls) {
		this.ignoreUrls = ignoreUrls;
	}

	
	public List<String> getIgnoreUrls() {
		return ignoreUrls;
	}


	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		String requestPath = request.getServletPath();
		if (null != ignoreUrls) {
			for (String url : ignoreUrls) {
				if (requestPath.contains(url)) {
					return true;
				}
			}
		}
		User user = (User) request.getSession().getAttribute("user");
		if (null == user) {
			response.sendRedirect(request.getContextPath() + "/login.html");
			return false;
		} else {
			@SuppressWarnings("unchecked")
			List<String> authlist=(List<String>) request.getSession().getAttribute("authlist");
			if(!requestPath.equals("/")){
				if(!authlist.contains(requestPath.replaceAll("/\\d+", ""))){
					logger.warn("该url已被拦截："+requestPath);
					request.getSession().invalidate();
					response.sendRedirect(request.getContextPath() + "/login.html");
					return false;
				}
			}
			
			String accessIp = RequestUtil.getRemoteHost(request);
			Enumeration<String> params = request.getParameterNames();
			StringBuffer requestContent = new StringBuffer();
			if (null != params) {
				while (params.hasMoreElements()) {
					String param = params.nextElement();
					String value = request.getParameter(param);
					requestContent.append(param).append(":").append(value).append("|");
				}
			}
			if(requestPath.isEmpty()||requestPath.equals("/")){
			}else{
				log = new Log(null, requestPath.split("/")[1], requestPath, null, user.getUserId(), accessIp, new Date(), null, null,requestContent.toString());
			}
			return true;
		}
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		if(null != log){
			log.setLogResult(""+response.getStatus());
			logService.save(log);
		}
		log = null;
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
	}

}

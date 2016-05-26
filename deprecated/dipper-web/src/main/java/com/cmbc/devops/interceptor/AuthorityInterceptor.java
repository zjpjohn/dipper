package com.cmbc.devops.interceptor;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

//@Repository
public class AuthorityInterceptor extends HandlerInterceptorAdapter {

	//不需要权限验证的请求地址,并且不需要登录的请求
	private List<String> uncheckUrls;
	//不需要权限验证的请求地址,但是需要登录的请求
	private List<String> uncheckUrlsWithSession;

	public List<String> getUncheckUrls() {
		return uncheckUrls;
	}

	public void setUncheckUrls(List<String> uncheckUrls) {
		this.uncheckUrls = uncheckUrls;
	}

	
	public List<String> getUncheckUrlsWithSession() {
		return uncheckUrlsWithSession;
	}

	public void setUncheckUrlsWithSession(List<String> uncheckUrlsWithSession) {
		this.uncheckUrlsWithSession = uncheckUrlsWithSession;
	}

	/**
	 * isAjaxRequest:判断请求是否为Ajax请求. <br/>
	 * 
	 * @author cyh
	 * @param request
	 *            请求对象
	 * @return boolean
	 * @since JDK 1.6
	 */
	public boolean isAjaxRequest(HttpServletRequest request) {
		String header = request.getHeader("X-Requested-With");
		boolean isAjax = "XMLHttpRequest".equals(header) ? true : false;
		return isAjax;
	}

	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		
		if (!uncheckUrls.contains(request.getRequestURI())) {
			if (request.getSession().getAttribute("user") == null) {
				response.sendRedirect("/login");
				return false;
			} else {
				/*if (!uncheckUrlsWithSession.contains(request.getRequestURI())) {
					// TODO:判断权限，用户登录时，将权限信息全部缓存到session中，每次请求，进行判断。
					if (!CheckAuthority(request, response, handler)) {
						// 权限验证不通过，判断是否为Ajax请求
						if (!isAjaxRequest(request)) {
							// 转到403 没有权限页面
							response.sendRedirect("/403");
						} else {
							// 返回ajax的 403 前台AjaxComplete全局等待，如果是返回是403，统一处理
							response.setStatus(403);
						}
						return false;
					}
				}*/
			}
		}
		// System.out.println("权限通过");
		return true;
	}

	// / 判断权限
	@SuppressWarnings("unused")
	private boolean CheckAuthority(HttpServletRequest request,
			HttpServletResponse response, Object handler) {
		// System.out.println(request.getRequestURI().toLowerCase());
		/*Object obj = request.getSession().getAttribute("authoritylists");
		if (obj != null) {
			PagedList<RoleActionView> lists = (PagedList<RoleActionView>) obj;
			for (RoleActionView roleAction : lists) {
				if (request.getRequestURI().toLowerCase()
						.equals(roleAction.getOcaction().getActionRelativeUrl()
								.toLowerCase())) {
					// TODO : 判断权限类型，是否是可操作。
					if (roleAction.getRoleAction().getAuthorityType() == AuthorityType.OPERATION)
						return true;
				}
			}
		}

		return false;*/
		 return true;
	}
}

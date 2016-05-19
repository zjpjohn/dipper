/**
 * @author hty
 * @time 上午9:39:37
 * @date 2014年12月5日
 */
package com.cmbc.devops.message;

import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import com.cmbc.devops.entity.User;

@Component
public class MessageHandshakeInterceptor implements HandshakeInterceptor {

	public boolean beforeHandshake(ServerHttpRequest request,
			ServerHttpResponse response, WebSocketHandler wsHandler,
			Map<String, Object> attributes) throws Exception {
		if (request instanceof ServletServerHttpRequest) {
			ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
			HttpSession session = servletRequest.getServletRequest()
					.getSession(false);
			if (session != null) {
				User user = (User) session.getAttribute("user");
				attributes.put("user", user);
			}
		}
		return true;
	}

	public void afterHandshake(ServerHttpRequest request,
			ServerHttpResponse response, WebSocketHandler wsHandler,
			Exception exception) {

	}
}
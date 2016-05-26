/**
 * @author hty
 * @time 上午9:39:37
 * @date 2014年12月5日
 */
package com.cmbc.devops.message;

import javax.annotation.Resource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

@Configuration
@EnableWebMvc
@EnableWebSocket
public class MessageConfig extends WebMvcConfigurerAdapter implements
		WebSocketConfigurer {
	
	@Resource
	private MessageWebSocketHandler messageWebSocketHandler;
	@Resource
	private MessageHandshakeInterceptor messageHandshakeInterceptor;

	@Bean
	public DefaultHandshakeHandler handshakeHandler() {
		return new DefaultHandshakeHandler();
	}

//	@Bean
//    public ServletServerContainerFactoryBean createWebSocketContainer() {
//        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
//        container.setMaxTextMessageBufferSize(8192);
//        container.setMaxBinaryMessageBufferSize(8192);
//        return container;
//    }
	
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(messageWebSocketHandler, "/messagingService")
				.addInterceptors(messageHandshakeInterceptor)
				.setHandshakeHandler(handshakeHandler());
		registry.addHandler(messageWebSocketHandler, "/sockjs/messagingService")
				.addInterceptors(messageHandshakeInterceptor)
				.setHandshakeHandler(handshakeHandler()).withSockJS();
	}
}
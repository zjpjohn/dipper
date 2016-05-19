package com.cmbc.devops.message;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;


/**  
 * date：2015年8月17日 下午3:41:13  
 * project name：cmbc-devops-web  
 * @author langzi  
 * @version 1.0   
 * @since JDK 1.7.0_21  
 * file name：MessagePush.java  
 * description：  
 */
@Component
public class MessagePush {
	
	@Resource
	private MessageWebSocketHandler messageWebSocketHandler;

	public void pushMessage(final int userId, final String content) {
		messageWebSocketHandler.sendMessageToUser(userId, new StickyMessage(content));
		new Thread(new Runnable() {
			@Override
			public void run() {
			}
		}).start();
	}
	public void pushMessage(final String content) {
		messageWebSocketHandler.sendMessageToUsers(new StickyMessage(content));
		new Thread(new Runnable() {
			@Override
			public void run() {
			}
		}).start();
	}
	
	public MessagePush() {
		super();
		// TODO Auto-generated constructor stub
	}
}

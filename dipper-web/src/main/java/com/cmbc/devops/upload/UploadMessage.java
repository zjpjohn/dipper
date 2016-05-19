package com.cmbc.devops.upload;

import com.cmbc.devops.message.Message;
import com.cmbc.devops.message.MessageType;

public class UploadMessage implements Message {
	private String messageType;
	private String content;

	public String getMessageType() {
		return messageType;
	}

	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public UploadMessage(String content) {
		this.setMessageType(MessageType.UP_FILE);
		this.setContent(content);
	}
}

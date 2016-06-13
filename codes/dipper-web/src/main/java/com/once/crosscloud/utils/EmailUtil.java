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
package com.once.crosscloud.utils;

import jodd.mail.Email;
import jodd.mail.EmailMessage;
import jodd.mail.SendMailSession;
import jodd.mail.SmtpServer;
import jodd.mail.SmtpSslServer;
import jodd.util.MimeTypes;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


/**
 * @author wuheng(wuheng@otcaix.iscas.ac.cn)
 * @date   Jun 9, 2016
 */
@Component
public class EmailUtil {
	
	@Value("${mail.QQ.username}")
	private String USER_QQ;

	@Value("${mail.QQ.password}")
	private String PASSWORD_QQ;

	@Value("${mail.126.username}")
	private String USER_126;

	@Value("${mail.126.password}")
	private String PASSWORD_126;

	/**
	 * 发送QQ邮箱
	 * 
	 * @param toMail
	 * @param subject
	 * @param text
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public boolean sendQQMail(String toMail, String subject, String text) throws Exception{
		boolean flag = false;
		try {
			Email email = Email.create();
			email.addMessage(new EmailMessage("Hello!",
					MimeTypes.MIME_TEXT_PLAIN));
			email.from(USER_QQ).to(toMail).subject(subject).addText(text);
			SmtpServer smtpServer = SmtpSslServer.create("smtp.qq.com")
					.authenticateWith(USER_QQ, PASSWORD_QQ);
			SendMailSession session = smtpServer.createSession();
			session.open();
			session.sendMail(email);
			session.close();
			flag = true;
		} catch (Exception e) {
			throw new RuntimeException("邮件发送失败:"+ e.getMessage());
		}
		return flag;
	}

	/**
	 * 发送126邮箱
	 * 
	 * @param toMail
	 * @param subject
	 * @param text
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public boolean send126Mail(String toMail, String subject, String text) throws Exception{
		boolean flag = false;
		try {
			Email email = Email.create().from(USER_126).to(toMail)
					.subject(subject).addText(text);
			SmtpServer smtpServer = SmtpServer.create("smtp.126.com")
					.authenticateWith(USER_126, PASSWORD_126);
			SendMailSession session = smtpServer.createSession();
			session.open();
			session.sendMail(email);
			session.close();
			flag = true;
		} catch (Exception e) {
			throw new RuntimeException("邮件发送失败:"+ e.getMessage());
		}
		return flag;
	}
}

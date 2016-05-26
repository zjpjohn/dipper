package com.cmbc.devops.util;

import javax.mail.internet.MimeMessage;

import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import com.cmbc.devops.config.EmailCfg;

public class EmailSendUtil {
	/**
	*用spring mail 发送邮件,依赖jar：spring.jar，activation.jar，mail.jar 
	*/
	
	public static void sendMail(EmailCfg config, String subject, String text, String to) throws Exception {
			JavaMailSenderImpl senderImpl = new JavaMailSenderImpl();

			// 设定mail server
			senderImpl.setHost(config.getEmailHost());
			senderImpl.setUsername(config.getAccount());
			senderImpl.setPassword(config.getPassword());
			// 建立HTML邮件消息
			MimeMessage mailMessage = senderImpl.createMimeMessage();
			// true表示开始附件模式
			MimeMessageHelper messageHelper = new MimeMessageHelper(mailMessage, true, "utf-8");

			// 设置收件人，寄件人
			messageHelper.setTo(to);
			messageHelper.setFrom(config.getEmailFrom());
			messageHelper.setSubject(subject);
			// true 表示启动HTML格式的邮件
			messageHelper.setText("<html><head></head><body>"+text+"</h1></body></html>", true);
/*			FileSystemResource file1 = new FileSystemResource(new File("d:/logo.jpg"));
			FileSystemResource file2 = new FileSystemResource(new File("d:/读书.txt"));
			// 添加2个附件
			messageHelper.addAttachment("logo.jpg", file1);
			
			try {
				//附件名有中文可能出现乱码
				messageHelper.addAttachment(MimeUtility.encodeWord("读书.txt"), file2);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				throw new MessagingException();
			}*/
			// 发送邮件
			senderImpl.send(mailMessage);
			System.out.println("邮件发送成功.....");

		}
}
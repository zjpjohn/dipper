package com.cmbc.devops.util;

import java.io.IOException;
import java.net.ConnectException;

import org.apache.log4j.Logger;

/**
 * date：2016年1月19日 上午11:26:18 project name：cmbc-devops-common
 * 
 * @author langzi
 * @version 1.0
 * @since JDK 1.7.0_21 file name：CommandExcutor.java description：
 */
public final class CommandExcutor {

	private CommandExcutor() {
	}

	private static final Logger LOGGER = Logger.getLogger(CommandExcutor.class);

	/**
	 * @author langzi
	 * @param ip
	 * @param username
	 * @param password
	 * @return
	 * @version 1.0 2016年1月19日
	 */
	public static SSH getSsh(String ip, String username, String password) {
		SSH ssh = new SSH(ip, username, password);
		return ssh != null ? ssh : null;
	}

	/**
	 * @author langzi
	 * @param ssh
	 * @param command
	 * @return
	 * @version 1.0 2016年1月19日
	 */
	public static boolean executeCommand(SSH ssh, String command) {
		try {
			if (!ssh.connect()) {
				return false;
			}
		} catch (IOException e1) {
			LOGGER.error("Create ExSSH connection IOException failed." + "Execute command:[" + command + "] error", e1);
			ssh.close();
			return false;
		}
		
		try {
			LOGGER.info(command);
			return ssh.execute(command);
		} catch (Exception e) {
			LOGGER.error("Execute command:[" + command + "] error", e);
			return false;
		} finally {
			ssh.close();
		}

	}

	/**
	 * @author langzi
	 * @param ssh
	 * @param command
	 * @return
	 * @version 1.0 2016年1月19日
	 */
	public static String executeCommandWithResult(SSH ssh, String command) {
		try {
			if (!ssh.connect()) {
				return "connect to host failed";
			}
		} catch (ConnectException ce) {
			LOGGER.error("Create ExSSH connection ConnectException failed." + "Execute command:[" + command + "] error",
					ce);
			ssh.close();
			return "Create ExSSH connection ConnectException failed.";
		} catch (IOException e1) {
			LOGGER.error("Create ExSSH connection IOException failed." + "Execute command:[" + command + "] error", e1);
			ssh.close();
			return "Create ExSSH connection IOException failed.";
		}
		try {
			LOGGER.info(command);
			return ssh.executeWithResult(command, 60 * 1000);
		} catch (Exception e) {
			LOGGER.error("Execute command:[" + command + "] error", e);
			return "Execute command:[" + command + "] error";
		} finally {
			ssh.close();
		}
	}

}

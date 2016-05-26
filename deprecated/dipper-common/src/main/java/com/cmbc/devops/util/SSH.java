package com.cmbc.devops.util;

import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketException;

import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import com.cmbc.devops.constant.ServerCommand;

import ch.ethz.ssh2.ChannelCondition;
import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.SCPClient;
import ch.ethz.ssh2.SFTPv3Client;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

public class SSH {

	private static final Logger LOGGER = Logger.getLogger(SSH.class);

	private static final int DEFAULT_PORT = 22;

	/**
	 * The host name of the agent, in form of IP address
	 */
	private String hostname;

	/**
	 * The user name of the agent
	 */
	private String username;

	/**
	 * The password of the agent, corresponds to the user-name
	 */
	private String password;

	/**
	 * The port
	 */
	private int port;
	/**
	 * SSH connection between the console and agent
	 */
	private Connection conn;

	private Session session;

	/**
	 * Constructor
	 * 
	 * @author liangzi
	 * @param hostname
	 * @param username
	 * @param password
	 * @version 1.0 2015年8月17日
	 */
	public SSH(String hostname, String username, String password) {
		this(hostname, username, password, DEFAULT_PORT);
	}

	/**
	 * Constructor
	 * 
	 * @author liangzi
	 * @param hostname
	 * @param username
	 * @param password
	 * @param port
	 * @version 1.0 2015年8月17日
	 */
	public SSH(String hostname, String username, String password, int port) {
		this.hostname = hostname;
		this.username = username;
		this.password = password;
		this.port = port;
	}

	/**
	 * @author langzi
	 * @return
	 * @version 1.0 2015年8月17日
	 */
	public boolean connect() throws IOException, SocketException  {
		boolean isAuthenticated = false;
		conn = new Connection(hostname, port);
		conn.connect();
		isAuthenticated = conn.authenticateWithPassword(username, password);
		session = conn.openSession();
		return isAuthenticated;
	}

	/**
	 * @author langzi
	 * @version 1.0 2015年8月17日
	 */
	public void close() {

		if (session != null) {
			session.close();
		}

		if (conn != null) {
			conn.close();
		}
	}

	@Override
	public String toString() {
		return hostname + File.pathSeparator + username + File.pathSeparator + password;
	}

	/********************************************************************************************
	 * 
	 * Other Commands
	 * 
	 ********************************************************************************************/

	/**
	 * 直到指令全部执行完，方能返回结果。 如果执行大文件拷贝指令，则需要等待很久
	 * 
	 * @param commandLine
	 * @return
	 * @throws IOException 
	 */
	public boolean execute(String commandLine) throws IOException {
		return execute(commandLine, 0);
	}

	/**
	 * 如果timeout，则返回false
	 * 
	 * 
	 * @param commandLine
	 * @param timeout
	 * @return
	 * @throws Exception
	 */
	public boolean execute(String commandLine, long timeout) throws IOException {
		if (session == null || !StringUtils.hasText(commandLine)) {
			return false;
		}

		session.execCommand(commandLine);
		session.waitForCondition(ChannelCondition.EXIT_STATUS, timeout);
		// 当超时发生时，session.getExitStatus()为null
		return (session.getExitStatus()==null) ? false : ((session.getExitStatus() == 0) ? true : false);
	}

	/**
	 * 直到指令全部执行完，方能返回结果。 如果执行大文件拷贝指令，则需要等待很久
	 * 
	 * @param commandLine
	 * @return
	 * @throws IOException 
	 * @throws Exception
	 */
	public String executeWithResult(String commandLine) throws IOException {
		return executeWithResult(commandLine, 0);
	}

	/**
	 * 如果timeout，则返回""
	 * 
	 * @param commandLine
	 * @param timeout
	 * @return
	 * @throws IOException 
	 */
	public String executeWithResult(String commandLine, long timeout) throws IOException, ConnectException {
		StringBuilder result = new StringBuilder();
		if (session == null || !StringUtils.hasText(commandLine)) {
			return null;
		}
		session.execCommand(commandLine);
		int condition = session.waitForCondition(ChannelCondition.EXIT_STATUS, timeout);
		if (timeout(condition)) {
			LOGGER.error("Command [" + commandLine + "] is timeout");
		} else {
			StreamGobbler is = new StreamGobbler(session.getStderr());
			result.append(IOUtils.toString(new StreamGobbler(session.getStdout()))).append(IOUtils.toString(is));
			IOUtils.close(is);
		}
		return result.toString();
	}

	/**
	 * @author langzi
	 * @param condition
	 * @return
	 * @version 1.0 2015年8月17日
	 */
	private boolean timeout(int condition) {
		return ((condition & ChannelCondition.TIMEOUT) == 1) ? true : false;
	}

	/********************************************************************************************
	 * 
	 * Other Commands
	 * 
	 ********************************************************************************************/

	/**
	 * @author langzi
	 * @param localFile
	 * @param remoteDir
	 * @return
	 * @version 1.0 2015年8月17日
	 */
	public boolean scpFile(String localFile, String remoteDir) {
		SCPClient cp = new SCPClient(conn);
		LOGGER.info(cp.getClass());
		try {
			LOGGER.info("localFile:" + localFile);
			LOGGER.info("remoteDir:" + remoteDir);
			cp.put(localFile, remoteDir);
			return true;
		} catch (Exception e) {
			LOGGER.error(e);
			return false;
		}
	}

	/**
	 * @author langzi
	 * @param toDelFileName
	 * @return
	 * @version 1.0 2015年8月17日
	 */
	public boolean rmFile(String toDelFileName) {
		try {
			SFTPv3Client sftpClient = new SFTPv3Client(conn);
			sftpClient.rm(toDelFileName);
			return true;
		} catch (IOException e) {
			LOGGER.error(e);
			return false;
		}
	}

	/**
	 * @author langzi
	 * @param remoteFile
	 * @return
	 * @version 1.0 2015年8月17日
	 */
	public boolean getFile(String remoteFile) {
		SCPClient sc = new SCPClient(conn);
		try {
			sc.get(remoteFile, this.getClass().getResource("/").getPath());
			return true;
		} catch (IOException e) {
			LOGGER.error(e);
			return false;
		}
	}

	/**
	 * @author yangqinglin
	 * @param remoteFile
	 * @return
	 * @version 1.0 2016年1月15日
	 */
	public boolean fetchFile(String remoteFile, String localDir) {
		SCPClient client = new SCPClient(conn);
		try {
			client.get(remoteFile, localDir);
			return true;
		} catch (IOException e) {
			LOGGER.error(e);
			return false;
		}
	}

	/**
	 * @author langzi
	 * @return
	 * @version 1.0 2015年8月17日
	 */
	public boolean mountAll() {
		try {
			this.execute(ServerCommand.COMMAND_MOUNTALL);
			return true;
		} catch (Exception e) {
			LOGGER.error(e);
			return false;
		}
	}

	/**
	 * @author langzi
	 * @param dir
	 * @return
	 * @version 1.0 2015年8月17日
	 */
	public boolean forceMakeDir(String dir) {
		try {
			this.execute(ServerCommand.COMMAND_MAKEDIR + dir);
			return true;
		} catch (Exception e) {
			LOGGER.error(e);
			return false;
		}
	}

	/**
	 * @author langzi
	 * @param src
	 * @param target
	 * @return
	 * @version 1.0 2015年8月17日
	 */
	public boolean forceMoveFolderTo(String src, String target) {
		try {
			this.execute(ServerCommand.COMMAND_MOVE + src + " " + target);
			return true;
		} catch (Exception e) {
			LOGGER.error(e);
			return false;
		}
	}

	/**
	 * @author langzi
	 * @param src
	 * @param target
	 * @return
	 * @version 1.0 2015年8月17日
	 */
	public boolean forceCopyFolderTo(String src, String target) {
		try {
			execute(ServerCommand.COMMAND_COPY + src + " " + target);
			return true;
		} catch (Exception e) {
			LOGGER.error(e);
			return false;
		}
	}

}

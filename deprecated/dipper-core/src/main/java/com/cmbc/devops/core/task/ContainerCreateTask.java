package com.cmbc.devops.core.task;

import java.io.IOException;
import java.net.SocketException;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.cmbc.devops.util.CommandExcutor;
import com.cmbc.devops.util.SSH;

/**
 * date：2016年1月13日 下午3:50:19 project name：cmbc-devops-core
 * 
 * @author langzi
 * @version 1.0
 * @since JDK 1.7.0_21 file name：ContainerCreateTask.java description：
 */
public class ContainerCreateTask implements Callable<JSONObject> {

	/* 暂定重试SSH连接次数为5次 */
	private static int RETRY_TIMES = 5;
	/* 设定批次数值，根据计数器的余数设置休眠的时间 */
	private static int BATCH_NUM = 6;
	private SSH ssh;
	private String createCommand;
	/* 添加ip地址，用户名和密码，在SSH链接失败的情况下，便于重新建立连接 */
	private String ipAddr;
	private String userName;
	private String password;

	private static final Logger logger = Logger.getLogger(ContainerCreateTask.class);

	public ContainerCreateTask(SSH ssh, String createCommand, String ipAddr, String userName, String password) {
		super();
		this.ssh = ssh;
		this.createCommand = createCommand;
		this.ipAddr = ipAddr;
		this.userName = userName;
		this.password = password;
	}

	private boolean connect() throws InterruptedException {
		int retryTimes = 0;
		while (retryTimes < RETRY_TIMES) {
			try {
				if (ssh.connect()) {
					return true;
				} else {
					logger.warn("Thread{" + Thread.currentThread().getId() + "}" + "Connect to Host exception : "
							+ (retryTimes + 1) + " times");
					retryTimes++;
					/* 获取原子long型计数器的数值，并分批进行休眠处理 */
					long counter = TaskRequestCounter.AL_CONCRT_COUNTER.getAndIncrement();
					/* 排除余数为0的情况 */
					int remainder = (int) (counter % BATCH_NUM) + 1;
					Thread.sleep(1000 * remainder);
					logger.warn(
							"Thread{" + Thread.currentThread().getId() + "}" + "Connect to Host exception Thread sleep "
									+ remainder + " seconds, counter is :" + counter);
					/* 休眠若干秒之后，重新创建SSH链接，进入循环 */
					this.ssh = CommandExcutor.getSsh(ipAddr, userName, password);
				}
			} catch (SocketException se) {
				/* 捕获ssh的IOException后，计数器自增，重新进入循环 */
				logger.warn("Thread{" + Thread.currentThread().getId() + "}"
						+ "Create ssh connection SocketException failed.", se);
				retryTimes++;
				/* 获取原子long型计数器的数值，并分批进行休眠处理 */
				long counter = TaskRequestCounter.AL_CONCRT_COUNTER.getAndIncrement();
				/* 排除余数为0的情况 */
				int remainder = (int) (counter % BATCH_NUM) + 1;
				logger.warn("Thread{" + Thread.currentThread().getId() + "}" + "After SocketException Thread sleep "
						+ remainder + " seconds, counter is :" + counter);
				Thread.sleep(1000 * remainder);
				/* 休眠若干秒之后，重新创建SSH链接，进入循环 */
				this.ssh = CommandExcutor.getSsh(ipAddr, userName, password);
				continue;
			} catch (IOException e) {
				/* 捕获ssh的IOException后，计数器自增，重新进入循环 */
				logger.warn("Thread{" + Thread.currentThread().getId() + "}"
						+ "Create ssh connection IOException failed.", e);
				retryTimes++;
				/* 获取原子long型计数器的数值，并分批进行休眠处理 */
				long counter = TaskRequestCounter.AL_CONCRT_COUNTER.getAndIncrement();
				/* 排除余数为0的情况 */
				int remainder = (int) (counter % BATCH_NUM) + 1;
				logger.warn("Thread{" + Thread.currentThread().getId() + "}" + "After IOException Thread sleep "
						+ remainder + " seconds, counter is :" + counter);
				Thread.sleep(1000 * remainder);

				/* 休眠若干秒之后，重新创建SSH链接，进入循环 */
				this.ssh = CommandExcutor.getSsh(ipAddr, userName, password);
				continue;
			}
		}
		return false;
	}

	@Override
	public JSONObject call() {
		JSONObject jo = new JSONObject();
		try {
			if (connect()) {
				String containerId = ssh.executeWithResult(createCommand);
				logger.info(createCommand);
				jo.put("containerId", containerId);
				jo.put("createCommand", createCommand);
			} else {
				logger.warn("Thread{" + Thread.currentThread().getId() + "}, "
						+ "create container error, connect server failed");
			}
		} catch (InterruptedException ie) {
			logger.error(
					"Thread{" + Thread.currentThread().getId() + "}, " + "Connect to Host Sleep InterruptedException ",
					ie);
		} catch (Exception e) {
			logger.error("Thread{" + Thread.currentThread().getId() + "}, " + "create container error", e);
		} finally {
			ssh.close();
		}
		return jo;
	}

}

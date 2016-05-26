package com.cmbc.devops.core.impl;

import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.cmbc.devops.core.LoadBalanceCore;
import com.cmbc.devops.core.task.LoadBalanceTask;
import com.cmbc.devops.model.LoadBalanceTemplate;

/**
 * date：2015年9月11日 上午11:19:20 project name：cmbc-devops-core
 * 
 * @author langzi
 * @version 1.0
 * @since JDK 1.7.0_21 file name：LoadBalanceCoreImpl.java description：
 */
@Component
public class LoadBalanceCoreImpl implements LoadBalanceCore {

	private static final Logger LOGGER = Logger.getLogger(LoadBalanceCore.class);
	private static int THREAD_SIZE = 20;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cmbc.devops.core.LoadBalanceCore#reloadBalance(java.util.List)
	 */
	@Override
	public Integer reloadBalance(List<LoadBalanceTemplate> balanceTemps){
		Integer successNum = 0;
		ExecutorService executor = Executors.newFixedThreadPool(THREAD_SIZE);
		CompletionService<Boolean> comp = new ExecutorCompletionService<>(executor);
		for (LoadBalanceTemplate balanceTemp : balanceTemps) {
			comp.submit(new LoadBalanceTask(balanceTemp));
		}
		executor.shutdown();
		int index = 0;
		while (index < balanceTemps.size()) {
			Future<Boolean> future = null;

			try {
				future = comp.take();
				if (future.get()) {
					successNum++;
				}
				index++;
				TimeUnit.MILLISECONDS.sleep(5000);
			} catch (InterruptedException e1) {
				LOGGER.error("Reload balance InterruptedException error", e1);
				continue;
			} catch (Exception e) {
				LOGGER.error("Reload balance Exception error", e);
				continue;
			}
		}
		return successNum;
	}

}

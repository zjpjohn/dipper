package com.cmbc.devops.core.task;

import java.util.concurrent.atomic.AtomicLong;

public class TaskRequestCounter {
	/* 添加静态原子long型计数器，更具余数确定休眠时间 */
	public static AtomicLong AL_CONCRT_COUNTER = new AtomicLong(0);
}

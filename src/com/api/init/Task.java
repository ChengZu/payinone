package com.api.init;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Task implements Runnable {
	static final Logger logger = LoggerFactory.getLogger(Task.class);
	// 定时任务执行间隔时间
	static int TIME = 7200;
	static Task TASK = null;

	public static void Init() {
		if (TASK != null)
			return;
		TASK = new Task();
		ScheduledExecutorService service = Executors
				.newSingleThreadScheduledExecutor();
		// 第二个参数为首次执行的延时时间，第三个参数为定时执行的间隔时间
		service.scheduleAtFixedRate(TASK, TIME, TIME, TimeUnit.SECONDS);
	}

	public void toDoTask() {
		logger.info("task run...");
	}

	@Override
	public void run() {
		toDoTask();
	}

}
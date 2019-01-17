package com.api.init;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alipay.demo.trade.config.Configs;
import com.api.client.NotifySocket;
import com.api.dao.DBHelp;
import com.github.wxpay.sdk.WXPayConfigImpl;

public class StartInit implements ServletContextListener {
	static final Logger logger = LoggerFactory.getLogger(StartInit.class);
	public static  String APPLICATIONPATH;

	// 系统初始化执行方法
	public void contextDestroyed(ServletContextEvent e) {
		logger.info("系统停止...");
	}

	public void contextInitialized(ServletContextEvent e) {
		logger.info("系统初始化开始...");

		// 获取项目根目录
		APPLICATIONPATH = e.getServletContext().getRealPath("/");
		logger.info("application path : {}", APPLICATIONPATH);


		//初始化数据库
		DBHelp.Init();
		logger.info("数据库初始化成功");
		// 初始化计时任务
		Task.Init();
		logger.info("定时任务初始化成功");
		
		try {
			NotifySocket.Init(5678);
		} catch (Exception e2) {
			e2.printStackTrace();
		}
		
		Configs.init("zfbinfo.properties");
		
		try {
			WXPayConfigImpl.Init("wxinfo.properties");
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		logger.info("系统初始化结束...");
	}

}
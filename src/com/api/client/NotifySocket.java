package com.api.client;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.api.dao.table.Device;
import com.api.dao.table.WXTrade;
import com.api.dao.table.ZFBTrade;
import com.api.dao.tablehelp.DeviceDBHelp;
import com.api.dao.tablehelp.WXTradeDBHelp;
import com.api.dao.tablehelp.ZFBTradeDBHelp;

public class NotifySocket extends Thread {
	public static ServerSocket server;
	public static NotifySocket notifySocket;
	public static ArrayList<SocketClient> CLIENTS = new ArrayList<SocketClient>();

	private NotifySocket(int port) {
		try {
			server = new ServerSocket(port);
		} catch (IOException e) {
			Log log = LogFactory.getLog("NotifySocket");
			log.error("NotifySocket Init Fail");
			e.printStackTrace();
		}
		this.start();
	}

	public static NotifySocket Init(int port) throws Exception {
		if (notifySocket == null) {
			synchronized (NotifySocket.class) {
				if (notifySocket == null) {
					notifySocket = new NotifySocket(port);
				}
			}
		}
		return notifySocket;
	}

	public static void wxTradeSuccess(String out_trade_no) {
		WXTrade wxtrade1 = new WXTrade();
		wxtrade1.setOut_trade_no(out_trade_no);
		WXTrade wxtrade = WXTradeDBHelp.getWXTrade(wxtrade1);

		Device device = new Device();
		device.setId(wxtrade.getDevice_info());
		Device device2 = (Device) DeviceDBHelp.getDevice(device).get(0);
		SocketClient mc = getClientById(device2.getParentId());
		String msg = "trade?tradeNo=wx" + out_trade_no + "&mid="
				+ wxtrade.getDevice_info();
		mc.sendMessage(new CmdInfo(msg));

	}

	public static void zfbTradeSuccess(String outTradeNo) {
		ZFBTrade zfbtrade1 = new ZFBTrade();
		zfbtrade1.setOutTradeNo(outTradeNo);
		ZFBTrade zfbtrade = ZFBTradeDBHelp.getZFBTrade(zfbtrade1);

		Device device = new Device();
		device.setId(zfbtrade.getStoreId());
		Device device2 = (Device) DeviceDBHelp.getDevice(device).get(0);
		SocketClient mc = getClientById(device2.getParentId());
		String msg = "trade?tradeNo=zfb" + outTradeNo + "&mid="
				+ zfbtrade.getStoreId();
		mc.sendMessage(new CmdInfo(msg));

		// 失败处理

	}

	public static SocketClient getClientById(String id) {
		for (int i = 0; i < NotifySocket.CLIENTS.size(); i++) {
			String mid = NotifySocket.CLIENTS.get(i).getMid();
			if (mid != null && mid.equals(id))
				return NotifySocket.CLIENTS.get(i);
		}

		return null;
	}

	public static boolean removeClientById(String id) {
		for (int i = 0; i < NotifySocket.CLIENTS.size(); i++) {
			String mid = NotifySocket.CLIENTS.get(i).getMid();
			if (mid != null && mid.equals(id)) {
				System.out.println("remove logout: " + id);
				NotifySocket.CLIENTS.get(i).Logout("recv_fail");
				return true;
			}
		}
		return false;
	}

	public static void clearCacheClient() {
		for (int i = 0; i < NotifySocket.CLIENTS.size(); i++) {
			long intervalTime = new Date().getTime()
					- NotifySocket.CLIENTS.get(i).getLastConnectTime();
			if (NotifySocket.CLIENTS.get(i).illegal && intervalTime > 600000) {
				NotifySocket.CLIENTS.get(i).Logout("recv_fail");
				i--;
			}
		}
	}

	public void run() {
		try {
			while (true) {
				SocketClient mc = new SocketClient(server.accept());
				clearCacheClient();
				CLIENTS.add(mc);
			}
		} catch (IOException ex) {
		} finally {
		}
	}
}

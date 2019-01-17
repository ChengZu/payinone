package com.api.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.api.dao.table.Device;
import com.api.dao.table.WXTrade;
import com.api.dao.table.ZFBTrade;
import com.api.dao.tablehelp.DeviceDBHelp;
import com.api.dao.tablehelp.WXTradeDBHelp;
import com.api.dao.tablehelp.ZFBTradeDBHelp;
import com.api.payutil.MyWXPayUtil;
import com.api.payutil.MyZFBPayUtil;

public class SocketClient {

	private Socket client;
	private long lastConnectTime = new Date().getTime();
	private String mid;
	public boolean illegal = true;
	private boolean unDispose = true;
	private boolean isOnline = true;
	private boolean doCmdLock = true;
	private Thread thread1 = null;
	private Thread thread2 = null;
	private PrintWriter out;
	public ArrayList<CmdInfo> cmds = new ArrayList<CmdInfo>();
	public Map<String, String> myDevice = new HashMap<String, String>();

	public SocketClient(Socket c) {
		this.client = c;
		try {
			out = new PrintWriter(client.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.run();
	}

	public void run() {
		final SocketClient sc = this;
		thread1 = new Thread() {
			public void run() {

				while (unDispose) {
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if (doCmdLock)
						continue;
					for (int i = 0; i < cmds.size(); i++) {
						long intervalTime = new Date().getTime()
								- cmds.get(i).getLastExecTime();
						if (intervalTime > 100) {
							cmds.get(i).exec(sc);
						}
						// TODO 时间过长移除和记录
						if (cmds.get(i).hasExecNo > 1800) {
							recordFailCmdIsTrade(cmds.get(i));
							cmds.remove(i);
							i--;
						}
					}
				}
			}
		};
		thread1.start();

		thread2 = new Thread() {
			public void run() {

				try {
					BufferedReader in = new BufferedReader(
							new InputStreamReader(client.getInputStream()));

					while (unDispose) {
						String str = in.readLine();
						System.out.println(mid + " revcMessage: " + str);
						// TODO 并发处理?????
						doCmdLock = true;
						doCmd(new CmdInfo(str));
						doCmdLock = false;
					}
					client.close();

				} catch (IOException ex) {
				} finally {
				}

			}

		};
		thread2.start();

	}
	
	public boolean recordFailCmdIsTrade(CmdInfo cmd) {
		if (cmd.getCmd().equals("trade")) {
			String tradeNo = cmd.getParams().get("tradeNo");
			String type=tradeNo.substring(0, 4);
			if (type.equals("4000")) {

				WXTrade wxtrade1 = new WXTrade();
				wxtrade1.setOut_trade_no(tradeNo);
				WXTrade wxtrade = WXTradeDBHelp.getWXTrade(wxtrade1);
				if(wxtrade.getPay_state().equals("SUCCESS")){
					WXTrade newWXTrade = new WXTrade();
					newWXTrade.setPay_state("SUCCESS_CONFIRMFAIL");

					WXTrade oldWXTrade = new WXTrade();
					oldWXTrade.setOut_trade_no(tradeNo);
					if(MyWXPayUtil.doRefund(tradeNo)){
						newWXTrade.setPay_state("SUCCESS_CONFIRMFAIL_SYSREFUND");
					}

					WXTradeDBHelp.updateWXTrade(oldWXTrade, newWXTrade);
				}
			} else if (type.equals("4001")) {

				ZFBTrade zfbtrade1 = new ZFBTrade();
				zfbtrade1.setOutTradeNo(tradeNo);
				ZFBTrade zfbtrade = ZFBTradeDBHelp.getZFBTrade(
						zfbtrade1);
				if(zfbtrade.getPayState().equals("TRADE_SUCCESS")){
					ZFBTrade newZFBTrade = new ZFBTrade();
					newZFBTrade.setPayState("TRADE_SUCCESS_CONFIRMFAIL");

					ZFBTrade oldZFBTrade = new ZFBTrade();
					oldZFBTrade.setOutTradeNo(tradeNo);
					if(MyZFBPayUtil.doRefund(tradeNo)){
						newZFBTrade.setPayState("TRADE_SUCCESS_CONFIRMFAIL_SYSREFUND");
					}

					ZFBTradeDBHelp.updateZFBTrade(oldZFBTrade, newZFBTrade);
				}
				
			}
		}
		return true;
	}

	public boolean Login(CmdInfo cmd) {

		String mid = cmd.getParams().get("id");
		String password = cmd.getParams().get("password");
		Device device = new Device();
		device.setId(mid);
		device.setTradeBody(password);
		device.setType("1");
		if (mid != null && password != null
				&& DeviceDBHelp.getDevice(device).size() > 0) {

			SocketClient client = NotifySocket.getClientById(mid);
			if (client != null && client != this) {
				NotifySocket.removeClientById(mid);
			}
			this.illegal = false;
			this.mid = mid;
			this.sendMessage("login?result=0");
			return true;
		}
		this.sendMessage("login?result=1");
		this.Logout("login_fail");
		return false;
	}

	public boolean Logout(String errorMsg) {
		this.sendMessage("logout?error="+errorMsg);
		this.thread1 = null;
		this.thread2 = null;
		this.unDispose = false;
		NotifySocket.CLIENTS.remove(this);
		return true;
	}

	public boolean confirmTrade(CmdInfo cmd) {
		for (int i = 0; i < cmds.size(); i++) {
			if (cmd.getParams().get("tradeNo")
					.equals(cmds.get(i).getParams().get("tradeNo"))
					&& cmd.getParams().get("result").equals("0")) {
				cmds.get(i).setExecSuccess(true);
				cmds.remove(i);
				i--;
			}
		}
		return false;
	}

	public boolean confirmOnline(CmdInfo cmd) {
		return this.isOnline = true;
	}

	public boolean isOnline() {
		long beginTime = new Date().getTime();
		this.sendMessage("online");
		this.isOnline = false;
		while (!isOnline) {
			long useTime = new Date().getTime() - beginTime;
			if (useTime > 3000) {
				Logout("check_online_fail");
				break;
			}
		}
		return this.isOnline;
	}

	public boolean confirmMachine(CmdInfo cmd) {
		//客户端不可信
		if (this.myDevice.get(cmd.getParams().get("mid")) != null) {
			this.myDevice.put(cmd.getParams().get("mid"),
					cmd.getParams().get("use"));
		}
		return true;
	}

	public boolean isMachineOnUse(String mid) {
		long beginTime = new Date().getTime();
		this.sendMessage("machine?mid=" + mid);
		this.myDevice.put(mid, "-1");
		while (this.myDevice.get(mid).equals("-1")) {
			long useTime = new Date().getTime() - beginTime;
			if (useTime > 3000) {
				Logout("check_online_fail");
				break;
			}
		}
		String state = this.myDevice.get(mid);
		if (state == null || !state.equals("0")) {
			return false;
		} else {
			return true;
		}
	}

	public boolean doCmd(CmdInfo cmd) {

		if (cmd.getCmd() == null) {
			return this.Logout("recv_fail");
		}

		if (this.illegal) {
			if (cmd.getCmd().equals("login")) {
				return Login(cmd);
			} else {
				return this.Logout("illegal");
			}

		}

		switch (cmd.getCmd()) {

		case "login":

			Login(cmd);

			break;
		case "logout":

			Logout("logout");

			break;

		case "trade":

			confirmTrade(cmd);

			break;

		case "online":

			confirmOnline(cmd);

			break;
		case "machine":

			confirmMachine(cmd);

			break;

		case "null":

			Logout("recv_fail");

			break;

		default:

			this.sendMessage("error?msg=cmd_unkown");

			break;

		}

		return illegal;
	}

	public boolean sendMessage(String str) {
		System.out.println(mid+" sendMessage: " + str);
		out.println(str);
		out.flush();
		return true;

	}

	public boolean sendMessage(CmdInfo cmd) {
		cmds.add(cmd);
		cmd.setLastExecTime();
		this.sendMessage(cmd.toString());
		cmd.setHasExec(true);
		return true;
	}

	public String getMid() {
		return mid;
	}

	public void setMid(String mid) {
		this.mid = mid;
	}

	public long getLastConnectTime() {
		return lastConnectTime;
	}

	public void setLastConnectTime(long lastConnectTime) {
		this.lastConnectTime = lastConnectTime;
	}

	public boolean isDoCmdLock() {
		return doCmdLock;
	}

	public void setDoCmdLock(boolean doCmdLock) {
		this.doCmdLock = doCmdLock;
	}

}
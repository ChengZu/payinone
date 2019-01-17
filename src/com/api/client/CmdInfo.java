package com.api.client;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class CmdInfo {
	// private String cmdStr;
	private String cmd;
	private Map<String, String> params = new HashMap<String, String>();
	private boolean hasExec;
	public int hasExecNo = 0;
	private boolean execSuccess;
	private long lastExecTime;

	public CmdInfo(String cmdStr) {
		String cmd = cmdStr;
		String argStr = "";
		Map<String, String> params = new HashMap<String, String>();

		if (cmdStr.indexOf("?") != -1) {
			String[] str = cmdStr.split("\\?");
			cmd = str[0];
			argStr = str[1];
		}

		if (argStr.length() > 0) {
			String args1[] = argStr.split("&");
			for (String ss : args1) {
				int index = ss.indexOf("=");
				if (index != -1) {
					params.put(ss.substring(0, index),
							ss.substring(index + 1, ss.length()));
				}
			}
		}
		this.cmd = cmd;
		this.params = params;
	}

	public String toString() {
		String str = "";
		for (Entry<String, String> m : params.entrySet()) {
			str += m.getKey() + "=" + m.getValue() + "&";
		}
		if (params.size() > 0)
			str = str.substring(0, str.length() - 1);
		return cmd + "?" + str;
	}

	public boolean exec(SocketClient sc) {
		hasExecNo++;
		sc.sendMessage(toString());
		setLastExecTime();
		return true;
	}

	public String getCmd() {
		return cmd;
	}

	public void setCmd(String cmd) {
		this.cmd = cmd;
	}

	public Map<String, String> getParams() {
		return params;
	}

	public void setParams(Map<String, String> params) {
		this.params = params;
	}

	public boolean isHasExec() {
		return hasExec;
	}

	public void setHasExec(boolean hasExec) {
		this.hasExec = hasExec;
	}

	public boolean isExecSuccess() {
		return execSuccess;
	}

	public void setExecSuccess(boolean execSuccess) {
		this.execSuccess = execSuccess;
	}

	public long getLastExecTime() {
		return lastExecTime;
	}

	public void setLastExecTime() {
		this.lastExecTime = new Date().getTime();
	}
}

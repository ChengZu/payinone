package com.api.dao.tablehelp;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.api.dao.DBHelp;
import com.api.dao.DBTableHelp;
import com.api.dao.table.ZFBTrade;

public class ZFBTradeDBHelp {

	public static void insertZFBTrade(ZFBTrade zfbtrade) {
		try {
			DBTableHelp.execInsertSql(zfbtrade);
		} catch (ClassNotFoundException | NoSuchFieldException
				| SecurityException | IllegalArgumentException
				| IllegalAccessException | SQLException e) {
			e.printStackTrace();
		}
	}

	public static void deleteZFBTrade(ZFBTrade zfbtrade) {
		try {
			DBTableHelp.execDeleteSql(zfbtrade);
		} catch (ClassNotFoundException | NoSuchFieldException
				| SecurityException | IllegalArgumentException
				| IllegalAccessException | SQLException e) {
			e.printStackTrace();
		}
	}

	public static void updateZFBTrade(ZFBTrade oldZFBTrade, ZFBTrade newZFBTrade) {
		try {
			DBTableHelp.execUpdateSql(oldZFBTrade, newZFBTrade);
		} catch (ClassNotFoundException | NoSuchFieldException
				| SecurityException | IllegalArgumentException
				| IllegalAccessException | SQLException e) {
			e.printStackTrace();
		}
	}

	public static ZFBTrade getZFBTrade(ZFBTrade zfbtrade) {
		ZFBTrade obj=null;
		List<Object> appList = new ArrayList<Object>();
		try {

			appList = DBTableHelp.execQuerySql(zfbtrade,
					DBTableHelp.getQuerySql(zfbtrade) + " LIMIT 1");
			if (appList.size() > 0)
				obj = (ZFBTrade) appList.get(0);

		} catch (ClassNotFoundException | NoSuchFieldException
				| SecurityException | IllegalArgumentException
				| IllegalAccessException | InstantiationException e1) {
			e1.printStackTrace();
		}
		return obj;
	}

	public static List<Object> getZFBTradeList(int start, int num) {
		String sql = "select * from zfbtrade order by outTradeNo desc limit " + start + ","
				+ num;
		ZFBTrade zfbtrade = new ZFBTrade();
		List<Object> appList = new ArrayList<Object>();
		try {
			appList = DBTableHelp.execQuerySql(zfbtrade, sql);
		} catch (ClassNotFoundException | NoSuchFieldException
				| SecurityException | IllegalArgumentException
				| IllegalAccessException | InstantiationException e) {
			e.printStackTrace();
		}
		return appList;
	}
	
	public static List<Object> serachWXTradeList(ZFBTrade zfbtrade, int start, int num) {
		String sql = "";
		try {
			sql = DBTableHelp.getQuerySql(zfbtrade)
					+ "order by outTradeNo desc limit " + start + "," + num;
		} catch (ClassNotFoundException | NoSuchFieldException
				| SecurityException | IllegalArgumentException
				| IllegalAccessException e1) {
			e1.printStackTrace();
		}
		List<Object> appList = new ArrayList<Object>();
		try {
			appList = DBTableHelp.execQuerySql(zfbtrade, sql);

		} catch (ClassNotFoundException | NoSuchFieldException
				| SecurityException | IllegalArgumentException
				| IllegalAccessException | InstantiationException e) {
			e.printStackTrace();
		}
		return appList;
	}
	
	public static String creatZFBOutTradeNo() {

		String num = "4001"
				+ new java.text.SimpleDateFormat("yyyyMMddHHmmss")
						.format(new java.util.Date());
		String sql = "select count(*) from zfbtrade";
		String result = "0";
		try {
			ResultSet rs = DBHelp.dbConn.conn.createStatement().executeQuery(
					sql);
			rs.next();

			result = rs.getString("count(*)");
			rs.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
		for (int i = 10 - result.length(); i > 0; i--) {
			result = "0" + result;
		}
		return num + result;
	}

	
	public static String getZFBTradeCount() {
		String sql = "select count(*) from zfbtrade";
		String result = "{\"tradeNum\":\"0\"}";
		try {
			ResultSet rs = DBHelp.dbConn.conn.createStatement().executeQuery(sql);
			rs.next();

			result = "{\"tradeNum\":\"" + rs.getString("count(*)") + "\"}";
			rs.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return result;
	}

}

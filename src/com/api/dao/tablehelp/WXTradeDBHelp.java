package com.api.dao.tablehelp;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.api.dao.DBHelp;
import com.api.dao.DBTableHelp;
import com.api.dao.table.WXTrade;

public class WXTradeDBHelp {

	public static void insertWXTrade(WXTrade wxtrade) {
		try {
			DBTableHelp.execInsertSql(wxtrade);
		} catch (ClassNotFoundException | NoSuchFieldException
				| SecurityException | IllegalArgumentException
				| IllegalAccessException | SQLException e) {
			e.printStackTrace();
		}
	}

	public static void deleteWXTrade(WXTrade wxtrade) {
		try {
			DBTableHelp.execDeleteSql(wxtrade);
		} catch (ClassNotFoundException | NoSuchFieldException
				| SecurityException | IllegalArgumentException
				| IllegalAccessException | SQLException e) {
			e.printStackTrace();
		}
	}

	public static void updateWXTrade(WXTrade oldWXTrade, WXTrade newWXTrade) {
		try {
			DBTableHelp.execUpdateSql(oldWXTrade, newWXTrade);
		} catch (ClassNotFoundException | NoSuchFieldException
				| SecurityException | IllegalArgumentException
				| IllegalAccessException | SQLException e) {
			e.printStackTrace();
		}
	}

	public static WXTrade getWXTrade(WXTrade wxtrade) {
		List<Object> list = new ArrayList<Object>();
		WXTrade obj = null;
		try {

			list = DBTableHelp.execQuerySql(wxtrade,
					DBTableHelp.getQuerySql(wxtrade) + " LIMIT 1");
			if (list.size() > 0)
				obj = (WXTrade) list.get(0);

		} catch (ClassNotFoundException | NoSuchFieldException
				| SecurityException | IllegalArgumentException
				| IllegalAccessException | InstantiationException e) {
			e.printStackTrace();
		}
		return obj;
	}

	public static List<Object> getWXTradeList(int start, int num) {
		String sql = "select * from wxtrade order by out_trade_no desc limit "
				+ start + "," + num;
		WXTrade wxtrade = new WXTrade();
		List<Object> list = new ArrayList<Object>();
		try {
			list = DBTableHelp.execQuerySql(wxtrade, sql);
		} catch (ClassNotFoundException | NoSuchFieldException
				| SecurityException | IllegalArgumentException
				| IllegalAccessException | InstantiationException e) {
			e.printStackTrace();
		}
		return list;
	}

	public static List<Object> serachWXTradeList(WXTrade wxtrade, int start, int num) {
		String sql = "";
		try {
			sql = DBTableHelp.getQuerySql(wxtrade)
					+ "order by out_trade_no desc limit " + start + "," + num;
		} catch (ClassNotFoundException | NoSuchFieldException
				| SecurityException | IllegalArgumentException
				| IllegalAccessException e) {
			e.printStackTrace();
		}
		List<Object> list = new ArrayList<Object>();
		try {
			list = DBTableHelp.execQuerySql(wxtrade, sql);

		} catch (ClassNotFoundException | NoSuchFieldException
				| SecurityException | IllegalArgumentException
				| IllegalAccessException | InstantiationException e) {
			e.printStackTrace();
		}
		return list;
	}

	public static String creatWXOutTradeNo() {

		String num = "4000"
				+ new java.text.SimpleDateFormat("yyyyMMddHHmmss")
						.format(new java.util.Date());
		String sql = "select count(*) from wxtrade";
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

	public static String getWXTradeCount() {
		String sql = "select count(*) from wxtrade";
		String result = "{\"count\":\"0\"}";
		try {
			ResultSet rs = DBHelp.dbConn.conn.createStatement().executeQuery(sql);
			rs.next();

			result = "{\"count\":\"" + rs.getString("count(*)") + "\"}";
			rs.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return result;
	}
	
}

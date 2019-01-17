package com.api.dao.tablehelp;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.api.dao.DBHelp;
import com.api.dao.DBTableHelp;
import com.api.dao.table.Refund;

public class RefundDBHelp {
	public static void insertRefund(Refund refund) {
		try {
			DBTableHelp.execInsertSql(refund);
		} catch (ClassNotFoundException | NoSuchFieldException
				| SecurityException | IllegalArgumentException
				| IllegalAccessException | SQLException e) {
			e.printStackTrace();
		}
	}

	public static void deleteRefund(Refund refund) {
		try {
			DBTableHelp.execDeleteSql(refund);
		} catch (ClassNotFoundException | NoSuchFieldException
				| SecurityException | IllegalArgumentException
				| IllegalAccessException | SQLException e) {
			e.printStackTrace();
		}
	}

	public static void updateRefund(Refund oldRefund, Refund newRefund) {
		try {
			DBTableHelp.execUpdateSql(oldRefund, newRefund);
		} catch (ClassNotFoundException | NoSuchFieldException
				| SecurityException | IllegalArgumentException
				| IllegalAccessException | SQLException e) {
			e.printStackTrace();
		}
	}

	public static Refund getRefund(Refund refund) {
		List<Object> list = new ArrayList<Object>();
		Refund obj = null;
		try {

			list = DBTableHelp.execQuerySql(refund,
					DBTableHelp.getQuerySql(refund) + " LIMIT 1");
			if (list.size() > 0)
				obj = (Refund) list.get(0);

		} catch (ClassNotFoundException | NoSuchFieldException
				| SecurityException | IllegalArgumentException
				| IllegalAccessException | InstantiationException e) {
			e.printStackTrace();
		}
		return obj;
	}

	public static List<Object> getRefundList(int start, int num) {
		String sql = "select * from refund order by id desc limit " + start + ","
				+ num;
		List<Object> list = new ArrayList<Object>();
		Refund refund = new Refund();

		try {
			list = DBTableHelp.execQuerySql(refund, sql);
		} catch (ClassNotFoundException | NoSuchFieldException
				| SecurityException | IllegalArgumentException
				| IllegalAccessException | InstantiationException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	public static String getRefundCount() {
		String sql = "select count(*) from refund";
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

package com.api.dao.tablehelp;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.api.dao.DBHelp;
import com.api.dao.DBTableHelp;
import com.api.dao.table.User;

public class UserDBHelp {

	public static void insertUser(User user) {
		try {
			DBTableHelp.execInsertSql(user);
		} catch (ClassNotFoundException | NoSuchFieldException
				| SecurityException | IllegalArgumentException
				| IllegalAccessException | SQLException e) {
			e.printStackTrace();
		}
	}

	public static void deleteUser(User user) {
		try {
			DBTableHelp.execDeleteSql(user);
		} catch (ClassNotFoundException | NoSuchFieldException
				| SecurityException | IllegalArgumentException
				| IllegalAccessException | SQLException e) {
			e.printStackTrace();
		}
	}

	public static void updateUser(User oldUser, User newUser) {
		try {
			DBTableHelp.execUpdateSql(oldUser, newUser);
		} catch (ClassNotFoundException | NoSuchFieldException
				| SecurityException | IllegalArgumentException
				| IllegalAccessException | SQLException e) {
			e.printStackTrace();
		}
	}

	public static List<Object> getUser(User user) {
		List<Object> appList = new ArrayList<Object>();
		try {

			appList = DBTableHelp.execQuerySql(user);

		} catch (ClassNotFoundException | NoSuchFieldException
				| SecurityException | IllegalArgumentException
				| IllegalAccessException | InstantiationException e1) {
			e1.printStackTrace();
		}
		return appList;
	}

	public static List<Object> getUserList(int start, int num) {
		String sql = "select * from user order by id desc limit " + start + ","
				+ num;
		List<Object> appList = new ArrayList<Object>();
		User user = new User();

		try {
			appList = DBTableHelp.execQuerySql(user, sql);
		} catch (ClassNotFoundException | NoSuchFieldException
				| SecurityException | IllegalArgumentException
				| IllegalAccessException | InstantiationException e) {
			e.printStackTrace();
		}
		return appList;
	}
	
	public static String getUserCount() {
		String sql = "select count(*) from user";
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

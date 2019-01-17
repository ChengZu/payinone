package com.api.dao;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DBTableHelp {

	public static void execInsertSql(Object o) throws ClassNotFoundException,
			NoSuchFieldException, SecurityException, IllegalArgumentException,
			IllegalAccessException, SQLException {
		DBHelp.dbConn.stmt.execute(getInsertSql(o));
	}

	public static void execDeleteSql(Object o) throws ClassNotFoundException,
			NoSuchFieldException, SecurityException, IllegalArgumentException,
			IllegalAccessException, SQLException {
		DBHelp.dbConn.stmt.execute(getDeleteSql(o));
	}

	public static List<Object> execQuerySql(Object obj, String sql)
			throws ClassNotFoundException, NoSuchFieldException,
			SecurityException, IllegalArgumentException,
			IllegalAccessException, InstantiationException {
		ArrayList<Object> list = new ArrayList<Object>();
		try {
			ResultSet rs = DBHelp.dbConn.conn.createStatement().executeQuery(
					sql);
			Class<?> c = Class.forName(obj.getClass().toString().split(" ")[1]);
			Field[] fs = c.getDeclaredFields();

			while (rs.next()) {

				Object o = c.newInstance();
				for (Field field : fs) {
					field.setAccessible(true);
					//TODO this should use rs.getObject(),but not uses for this project
					//field.set(o, rs.getObject(field.getName()));
					field.set(o, rs.getString(field.getName()));
				}
				list.add(o);

			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public static List<Object> execQuerySql(Object obj)
			throws ClassNotFoundException, NoSuchFieldException,
			SecurityException, IllegalArgumentException,
			IllegalAccessException, InstantiationException {
		return execQuerySql(obj, getQuerySql(obj));
	}

	public static void execUpdateSql(Object oldObj, Object newObj)
			throws ClassNotFoundException, NoSuchFieldException,
			SecurityException, IllegalArgumentException,
			IllegalAccessException, SQLException {
		DBHelp.dbConn.stmt.executeUpdate(
				getUpdateSql(oldObj, newObj));
	}

	public static String getInsertSql(Object o) throws ClassNotFoundException,
			NoSuchFieldException, SecurityException, IllegalArgumentException,
			IllegalAccessException {
		Class<?> c = Class.forName(o.getClass().toString().split(" ")[1]);
		Field[] fs = c.getDeclaredFields();

		StringBuffer sql = new StringBuffer();
		StringBuffer sql2 = new StringBuffer();

		sql.append("INSERT INTO " + c.getSimpleName() + " (");
		sql2.append(" VALUES (");
		int oneMore = 0;
		for (Field field : fs) {
			field.setAccessible(true);
			if (field.get(o) != null) {
				oneMore++;
				sql.append(field.getName() + ",");
				sql2.append("'" + field.get(o) + "',");
			}
		}

		if (oneMore > 0) {
			sql.replace(sql.length() - 1, sql.length(), ")");
			sql2.replace(sql2.length() - 1, sql2.length(), ")");
		} else {
			sql.append(")");
			sql2.append(")");
		}
		sql.append(sql2);
		return sql.toString();
	}

	public static String getDeleteSql(Object o) throws ClassNotFoundException,
			NoSuchFieldException, SecurityException, IllegalArgumentException,
			IllegalAccessException {
		Class<?> c = Class.forName(o.getClass().toString().split(" ")[1]);
		Field[] fs = c.getDeclaredFields();
		StringBuffer sql = new StringBuffer();

		sql.append("DELETE FROM " + c.getSimpleName() + " WHERE ");
		int oneMore = 0;
		for (Field field : fs) {
			field.setAccessible(true);
			if (field.get(o) != null) {
				oneMore++;
				sql.append(field.getName() + " = '" + field.get(o) + "' and ");
			}
		}
		if (oneMore > 0) {
			sql.replace(sql.length() - 4, sql.length(), "");
			return sql.toString();
		} else {
			return "";
		}
	}

	public static String getQuerySql(Object o) throws ClassNotFoundException,
			NoSuchFieldException, SecurityException, IllegalArgumentException,
			IllegalAccessException {
		Class<?> c = Class.forName(o.getClass().toString().split(" ")[1]);
		Field[] fs = c.getDeclaredFields();
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT * FROM " + c.getSimpleName() + " WHERE ");
		int oneMore = 0;
		for (Field field : fs) {
			field.setAccessible(true);
			if (field.get(o) != null) {
				oneMore++;
				sql.append(field.getName() + " = '" + field.get(o) + "' and ");
			}
		}
		if (oneMore > 0) {
			sql.replace(sql.length() - 4, sql.length(), "");
			return sql.toString();
		} else {
			return "SELECT * FROM " + c.getSimpleName();
		}

	}

	public static String getUpdateSql(Object oldObj, Object newObj)
			throws ClassNotFoundException, NoSuchFieldException,
			SecurityException, IllegalArgumentException, IllegalAccessException {
		Class<?> c = Class.forName(oldObj.getClass().toString().split(" ")[1]);
		Field[] fs = c.getDeclaredFields();
		StringBuffer sql = new StringBuffer();

		sql.append("UPDATE " + c.getSimpleName() + " SET ");
		int oneMore1 = 0;
		for (Field field : fs) {
			field.setAccessible(true);
			if (field.get(newObj) != null) {
				oneMore1++;
				sql.append(field.getName() + " = '" + field.get(newObj) + "', ");
			}
		}
		if (oneMore1 > 0)
			sql.replace(sql.length() - 2, sql.length(), " WHERE ");
		int oneMore2 = 0;
		for (Field field : fs) {
			field.setAccessible(true);
			if (field.get(oldObj) != null) {
				oneMore2++;
				sql.append(field.getName() + " = '" + field.get(oldObj)
						+ "' and ");
			}
		}
		if (oneMore2 > 0)
			sql.replace(sql.length() - 4, sql.length(), "");

		if (oneMore1 > 0 && oneMore2 > 0) {

			return sql.toString();
		} else {
			return "";
		}

	}

}

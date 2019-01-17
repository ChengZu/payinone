package com.api.dao.tablehelp;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.api.dao.DBHelp;
import com.api.dao.DBTableHelp;
import com.api.dao.table.Device;

public class DeviceDBHelp {
	public static void insertDevice(Device device) {
		try {
			DBTableHelp.execInsertSql(device);
		} catch (ClassNotFoundException | NoSuchFieldException
				| SecurityException | IllegalArgumentException
				| IllegalAccessException | SQLException e) {
			e.printStackTrace();
		}
	}

	public static void deleteDevice(Device device) {
		try {
			DBTableHelp.execDeleteSql(device);
		} catch (ClassNotFoundException | NoSuchFieldException
				| SecurityException | IllegalArgumentException
				| IllegalAccessException | SQLException e) {
			e.printStackTrace();
		}
	}

	public static void updateDevice(Device oldDevice, Device newDevice) {
		try {
			DBTableHelp.execUpdateSql(oldDevice, newDevice);
		} catch (ClassNotFoundException | NoSuchFieldException
				| SecurityException | IllegalArgumentException
				| IllegalAccessException | SQLException e) {
			e.printStackTrace();
		}
	}

	public static List<Object> getDevice(Device device) {
		List<Object> appList = new ArrayList<Object>();
		try {

			appList = DBTableHelp.execQuerySql(device);

		} catch (ClassNotFoundException | NoSuchFieldException
				| SecurityException | IllegalArgumentException
				| IllegalAccessException | InstantiationException e1) {
			e1.printStackTrace();
		}
		return appList;
	}

	public static List<Object> getDeviceList(int start, int num) {
		String sql = "select * from device order by id desc limit " + start + ","
				+ num;
		List<Object> appList = new ArrayList<Object>();
		Device device = new Device();

		try {
			appList = DBTableHelp.execQuerySql(device, sql);
		} catch (ClassNotFoundException | NoSuchFieldException
				| SecurityException | IllegalArgumentException
				| IllegalAccessException | InstantiationException e) {
			e.printStackTrace();
		}
		return appList;
	}
	
	public static String getDeviceCount() {
		String sql = "select count(*) from device";
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

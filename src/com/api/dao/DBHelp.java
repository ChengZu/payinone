package com.api.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.configuration.ConfigurationException;

public class DBHelp {
	public static DBConn dbConn = null;

	public static boolean Init() {
		try {
			DBHelp.dbConn = new DBConn();
		} catch (ConfigurationException e) {

			e.printStackTrace();
		} catch (ClassNotFoundException e) {

			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return true;
	}


	
	
	
	
	public static boolean loginCheckUser(String idorname, String password) {
		String sql = "select id, name,password from user where (id ='"
				+ idorname + "' or name='" + idorname + "') and password ='"
				+ password + "'";
		boolean isUser = false;
		try {
			ResultSet rs = dbConn.conn.createStatement().executeQuery(sql);
			while (rs.next()) {
				isUser = true;
			}
			rs.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return isUser;
	}


}

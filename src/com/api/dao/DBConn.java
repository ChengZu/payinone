package com.api.dao;

import java.sql.*;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

public class DBConn {
	private String url = "jdbc:mysql://127.0.0.1/test";
	private String name = "com.mysql.jdbc.Driver";
	private String user = "root";
	private String password = "root";

	public Connection conn = null;
	public Statement stmt = null;
	private Configuration configs;

	public DBConn() throws ConfigurationException, ClassNotFoundException,
			SQLException {

		configs = new PropertiesConfiguration("mysql.properties");
		url = configs.getString("url");
		name = configs.getString("name");
		user = configs.getString("user");
		password = configs.getString("password");

		Class.forName(name);// 指定连接类型
		conn = DriverManager.getConnection(url, user, password);// 获取连接
		stmt = conn.createStatement();

	}

	public void close() {
		try {
			conn.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
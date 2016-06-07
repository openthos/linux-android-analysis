package com.appstore.interfaces;

import java.sql.Connection;
import java.sql.ResultSet;

/**
 * DML and Connection
 */
public interface DataBase {

	public Connection getConn();

	public boolean InsertDML(String sql);
	public boolean DeleDML(String sql);
	public boolean UpdateDML(String sql);
	public ResultSet QueryDML(String sql);
	public void CloseAll();
}

package com.haikuo.spider.utils;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbcp.BasicDataSourceFactory;
import org.apache.log4j.Logger;

public class DBConn {	
	private static final Logger Log = Logger.getLogger(DBConn.class.getName());
	public static String CONN_URL;
	public static String USERNAME;
	public static String PASSWORD;
	
	private static BasicDataSource bds = null;
	private static BasicDataSourceFactory bdsf = null;
	
	private static org.apache.commons.dbcp2.BasicDataSource bds2 = null;
	private static BasicDataSourceFactory bdsf2 = null;
	
	private DBConn() {
		
	}
	
	public static Connection getConnection(){
		Connection conn = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(CONN_URL, USERNAME, PASSWORD);
		} 
		catch (Exception e) {
			Log.error(e);
		}
		return conn;
	}
	/**
	 * 数据连接池
	 * @return
	 * @throws SQLException
	 */
	public static BasicDataSource getConnectionDS() throws SQLException{
	
		bds = new BasicDataSource();
		bds.setUrl(CONN_URL);
		bds.setUsername(USERNAME);
		bds.setPassword(PASSWORD);
		bds.setDriverClassName("com.mysql.jdbc.Driver");  
		//设置初始化连接总数
		bds.setInitialSize(200);
        //设置同时应用的连接总数
        bds.setMaxActive(-1);
        //设置在缓冲池的最大连接数
        bds.setMaxIdle(-1);
        //设置在缓冲池的最小连接数
        bds.setMinIdle(0);
        //设置最长的等待时间
        bds.setMaxWait(-1);
        
		return bds;
	}
	
	/**
	 * 数据连接池
	 * @return
	 * @throws SQLException
	 */
	public static org.apache.commons.dbcp2.BasicDataSource getConnectionDS2() throws SQLException{
		
		bds2 = new org.apache.commons.dbcp2.BasicDataSource();
		bds2.setUrl(CONN_URL);
		bds2.setUsername(USERNAME);
		bds2.setPassword(PASSWORD);
		bds2.setDriverClassName("com.mysql.jdbc.Driver");  
		//设置初始化连接总数
		bds2.setInitialSize(200);
        //设置同时应用的连接总数
        bds2.setMaxTotal(-1);;
        //设置在缓冲池的最大连接数
        bds2.setMaxIdle(-1);
        //设置在缓冲池的最小连接数
        bds2.setMinIdle(0);
        //设置最长的等待时间
        bds2.setMaxWaitMillis(-1);;
        
		return bds2;
	}
}

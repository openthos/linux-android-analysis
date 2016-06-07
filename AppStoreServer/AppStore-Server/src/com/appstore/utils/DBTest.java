package com.appstore.utils;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;

public class DBTest {

	public static final String dbUrl = "jdbc:mysql://127.0.0.1:3306/AppStore";  
    public static final String dbDriver = "com.mysql.jdbc.Driver";  
    public static final String dbUser = "root";  
    public static final String dbPass = "123"; 
    private PreparedStatement statement = null;  
    private Connection conn=null;  
	public static void main(String[] args) {
		DBTest db=new DBTest();
		db.getConn();
		db.insertSQL("insert into game(id ,game_name ,dev_name,dev_id,update_time,soft_language,soft_version,soft_download_count ,introduce ,soft_size,game_classify,allow) values(15,'app6','app6','app6','app6','app6','app6',11,'app6',10,4,1)");
		
		

//		ResultSet rs=db.selectSQL("select * from AppInfo");
//        try {
//			while (rs.next()) 
//			     System.out.println(rs.getInt("id") + "  "+ rs.getString("name") );
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//   
	}
	
	//获得数据库连接
	public Connection getConn(){	
         try  
         {  
             Class.forName(dbDriver);  
         }  
         catch (ClassNotFoundException e)  
         {  
        	 System.err.println(  "装载 JDBC驱动程序失败。" ); 
             e.printStackTrace();  
         }  
         try  
         {  
             conn = DriverManager.getConnection(dbUrl,dbUser,dbPass);
         }  
         catch (SQLException e)  
         {  
        	 System.err.println( "无法连接数据库" );  
             e.printStackTrace();  
         }  
         return conn;  	
	}
	
	//获取查询结果集
	public ResultSet selectSQL(String sql) {  
        ResultSet rs = null;  
        try {  
            statement = getConn().prepareStatement(sql);  
            rs = statement.executeQuery(sql);  
        } catch (SQLException e) {  
            e.printStackTrace();  
        }  
        return rs;  
    }  
  
    //执行插入操作
    public boolean insertSQL(String sql) {  
        try {  
            statement = conn.prepareStatement(sql);  
            statement.executeUpdate();  
            return true;  
        } catch (SQLException e) {  
            System.out.println("插入数据库时出错：");  
            e.printStackTrace();  
        } catch (Exception e) {  
            System.out.println("插入时出错：");  
            e.printStackTrace();  
        }  
        return false;  
    }  

    //执行删除操作
   public  boolean deleteSQL(String sql) {  
        try {  
            statement = conn.prepareStatement(sql);  
            statement.executeUpdate();  
            return true;  
        } catch (SQLException e) {  
            System.out.println("插入数据库时出错：");  
            e.printStackTrace();  
        } catch (Exception e) {  
            System.out.println("插入时出错：");  
            e.printStackTrace();  
        }  
        return false;  
    }  

    //执行更新操作
   public  boolean updateSQL(String sql) {  
        try {  
            statement = conn.prepareStatement(sql);  
            statement.executeUpdate();  
            return true;  
        } catch (SQLException e) {  
            System.out.println("插入数据库时出错：");  
            e.printStackTrace();  
        } catch (Exception e) {  
            System.out.println("插入时出错：");  
            e.printStackTrace();  
        }  
        return false;  
    }  
}

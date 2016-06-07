package com.appstore.utils;

import java.io.IOException;
import java.util.Properties;

public class GetDBProperties {
	
	Properties properties  = null;
	public GetDBProperties(){
		try {
		   properties  = new Properties();
			properties.load(this.getClass().getResourceAsStream("db.properties"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public  String getProperty(String key){
		return  properties.getProperty(key);
	}
	
//	public static void main(String[] args) {
//		System.out.println(new GetDBProperties().getProperty("dbUrl"));
//	}
}

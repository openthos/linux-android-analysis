package com.appstore.utils;

import java.net.HttpURLConnection;
import java.net.URL;

public class A {
public static void main(String[] args) {
	new A().getFileSize();
}
private void getFileSize(){
	
	    
		 String urlpath="http://127.0.0.1:8080/AppStoreServer/Resource/Software/10/jiemian1.jpg";
   	     URL url;
				try {
					url = new URL(urlpath);
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				       
					conn.setRequestMethod("POST"); 
					conn.setConnectTimeout(6*1000);
					conn.setRequestProperty("Accept-Language", "zh-CN");
					conn.setRequestProperty("Referer", urlpath);
					if( conn.getResponseCode()== 200){
						int temp =conn.getContentLength();
						System.out.println("�ļ�����"+temp);
					}
				} catch ( Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			

}
}

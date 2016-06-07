package com.appstore.servlet;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.DriverManager;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.appstore.utils.OperateService;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 
 * @author entity
 *处理客户端tab的servlet
 */
public class AppStoreController extends HttpServlet {
	private static final long serialVersionUID = 1L;
  
    
	public AppStoreController() {
        super();
       
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request,response);
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	    String operate = new String( request.getParameter("operate"));
	    System.out.println(request.getRemoteAddr()+"进行了"+operate+"操作");
	    String json = null;
		if(operate.equals("paihang")){
			OperateService oper   = new OperateService();
  			 json =	oper.operate_paihang(request);
	    }
		else if(operate.equals("software")){
			  OperateService oper   = new OperateService();;   
			try {
				json =	oper.operate_software(request);
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
       
//        else if(operate.equals("amusement")){
//        	 Operate oper   = new Operate();
//   			 json =	oper.operate_amusement(request);
//		}
       
        else if(operate.equals("game")){
        	  OperateService oper   = new OperateService();;   
  			json =	oper.operate_game(request);
		}
//        else if(operate.equals("search")){
//        	 OperateService oper   = new OperateService();;   
//   			json =	oper.Search(request);
//        }
//        else if(operate.equals("register_dev")){
//        	    Operate oper   = new Operate();   
//    			json =	oper.operate_register(request);
//        }
//        else if(operate.equals("auther_other_works")){
//        	Operate oper   = new Operate();   
//			json =	oper.auther_otherworks(request);
//        }
//        else if(operate.equals("get_estimate")){
//        	OperateService oper   = new OperateService();   
//			json =	oper.get_estimate(request);
//        }
//        else if(operate.equals("hate")){
//        	OperateService oper   = new OperateService();   
//			json =	oper.why_hate(request);
//        }
		if(json == null){
			json = "wrong";
		}
		 OutputStream out = response.getOutputStream();  
         out.write(json.getBytes());  
		//request.setAttribute("json", json.toString());
		//request.getRequestDispatcher("/WEB-INF/page/jsonnewslist.jsp").forward(request, response);
	}

}






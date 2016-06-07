package com.appstore.servlet;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.appstore.utils.DataBaseService;

/**
 * Servlet implementation class DownloadCountCul
 * 用于下载次数统计
 */
@WebServlet("/DownloadCountCul")
public class DownloadCountCul extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DownloadCountCul() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String fileid=request.getParameter("id");
		String classify=request.getParameter("classify");
		System.out.println("当前的分类："+classify);
		System.out.println("当前的文件ID："+fileid);
		if(classify.equals("software")){
			String sql="select soft_download_count from software where id="+fileid;
			DataBaseService dbs=new DataBaseService();
			dbs.getConn();
			ResultSet rs=dbs.QueryDML(sql);
			try {
				while(rs.next()){
					dbs.UpdateDML("update software set soft_download_count=soft_download_count+1 where id="+fileid);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		else{
			String sql="select soft_download_count from game where id="+fileid;
			DataBaseService dbs=new DataBaseService();
			dbs.getConn();
			ResultSet rs=dbs.QueryDML(sql);
			try {
				while(rs.next()){
					dbs.UpdateDML("update game set soft_download_count=soft_download_count+1 where id="+fileid);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

}

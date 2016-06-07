package com.appstore.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 实现断点续传的类
 * 
 * @author entity
 *
 */
public class DownloadApkService {
	
	public void downloadFile(HttpServletRequest request, HttpServletResponse response) {
		System.out.println("进入下载页面");
		System.out.println("===========");
		String path = request.getParameter("path");
		System.out.println("请求的地址是：" + path);
		System.out.println("===========");
		File downloadFile = new File(request.getRealPath("") + "/Resource/" + path);
		if (downloadFile.exists()) {
			System.out.println("文件存在");
			if (downloadFile.isFile()) {
				if (downloadFile.length() > 0) {
				} else {
					return;
				}
				if (!downloadFile.canRead()) {
					return;
				} else {
				}
			} else {
				return;
			}
		} else {
			return;
		}

		long fileLength = downloadFile.length(); // 记录文件大小
		long pastLength = 0; // 记录已下载文件大小
		int rangeSwitch = 0; // 0：从头开始的全文下载；1：从某字节开始的下载（bytes=27000-）；2：从某字节开始到某字节结束的下载（bytes=27000-39000）
		long toLength = 0; // 记录客户端需要下载的字节段的最后一个字节偏移量（比如bytes=27000-39000，则这个值是为39000）
		long contentLength = 0; // 客户端请求的字节总量
		String rangeBytes = ""; // 记录客户端传来的形如“bytes=27000-”或者“bytes=27000-39000”的内容
		RandomAccessFile raf = null; // 负责读取数据
		OutputStream os = null; // 写出数据
		OutputStream out = null; // 缓冲
		byte b[] = new byte[1024]; // 暂存容器
		// response.setStatus(206);
		response.reset(); // 告诉客户端允许断点续传多线程连接下载,响应的格式是:Accept-Ranges: bytes
		response.setHeader("Accept-Ranges", "bytes");
		if (request.getHeader("Range") != null) { // 客户端请求的下载的文件块的开始字节
			System.out.println("有断点请求");
			response.setStatus(206);
			rangeBytes = request.getHeader("Range").replaceAll("bytes=", "");
			System.out.println(rangeBytes);
			if (rangeBytes.indexOf('-') == rangeBytes.length() - 1) {// bytes=969998336-
				rangeSwitch = 1;
				rangeBytes = rangeBytes.substring(0, rangeBytes.indexOf('-'));
				pastLength = Long.parseLong(rangeBytes.trim());
				contentLength = fileLength - pastLength; // 客户端请求的是 969998336
															// 之后的字节
				System.out.println("类型：1132123-" + contentLength);
			} else { // bytes=1275856879-1275877358
				rangeSwitch = 2;
				String temp0 = rangeBytes.substring(0, rangeBytes.indexOf('-'));
				String temp2 = rangeBytes.substring(rangeBytes.indexOf('-') + 1, rangeBytes.length());
				pastLength = Long.parseLong(temp0.trim()); // bytes=1275856879-1275877358，从第
															// 1275856879
															// 个字节开始下载
				toLength = Long.parseLong(temp2.trim()); // bytes=1275856879-1275877358，到第
				// 1275877358 个字节结束
				contentLength = toLength - pastLength; // 客户端请求的是
														// 1275856879-1275877358
														// 之间的字节
				System.out.println("类型：1212-1121211" + contentLength + "===" + pastLength + "===" + toLength);
			}
		} else { // 从开始进行下载
			contentLength = fileLength; // 客户端要求全文下载
			System.out.println("一次性下载:" + contentLength);
		}
		// if (pastLength != 0) {
		// switch (rangeSwitch) {
		// case 1: { // 针对 bytes=27000- 的请求
		// String contentRange = new StringBuffer("bytes ").append(new
		// Long(pastLength).toString()).append("-")
		// .append(new Long(fileLength - 1).toString()).append("/").append(new
		// Long(fileLength).toString())
		// .toString();
		// response.setHeader("Content-Range", contentRange);
		// break;
		// }
		// case 2: { // 针对 bytes=27000-39000 的请求
		// String contentRange = rangeBytes + "/" + new
		// Long(fileLength).toString();
		// response.setHeader("Content-Range", contentRange);
		// break;
		// }
		// default: {
		// break;
		// }
		// }
		// } else {
		// // 是从开始下载
		// }

		try {
			response.addHeader("Content-Disposition", "attachment; filename=\"" + downloadFile.getName() + "\"");
			response.addHeader("Content-Length", String.valueOf(contentLength));
			os = response.getOutputStream();
			out = new BufferedOutputStream(os);
			raf = new RandomAccessFile(downloadFile, "r");
			try {
				switch (rangeSwitch) {
				case 0: { // 普通下载，或者从头开始的下载
					// 同1
				}
				case 1: { // 针对 bytes=27000- 的请求
					raf.seek(pastLength); // 形如 bytes=969998336- 的客户端请求，跳过
											// 969998336 个字节
					int n = 0;
					while ((n = raf.read(b, 0, 1024)) != -1) {
						out.write(b, 0, n);
					}
					break;
				}
				case 2: { // 针对 bytes=27000-39000 的请求
					raf.seek(pastLength); // 形如 bytes=1275856879-1275877358
											// 的客户端请求，找到第 1275856879 个字节
					int n = 0;
					long readLength = 0; // 记录已读字节数
					while (readLength <= contentLength - 1024) {// 大部分字节在这里读取
						n = raf.read(b, 0, 1024);
						readLength += 1024;
						out.write(b, 0, n);
					}
					if (readLength <= contentLength) { // 余下的不足 1024 个字节在这里读取
						n = raf.read(b, 0, (int) (contentLength - readLength));
						out.write(b, 0, n);
					}
					break;
				}
				default: {
					break;
				}
				}
				out.flush();
			} catch (IOException ie) {
				// ignore
			}
		} catch (Exception e) {
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
			if (raf != null) {
				try {
					raf.close();
				} catch (IOException e) {
				}
			}
		}
	}
}

// package com.appstore.utils;
//
// import java.io.BufferedOutputStream;
// import java.io.File;
// import java.io.FileInputStream;
// import java.io.IOException;
// import java.io.OutputStream;
// import java.io.RandomAccessFile;
// import java.net.URLEncoder;
//
// import javax.servlet.ServletException;
// import javax.servlet.ServletOutputStream;
// import javax.servlet.annotation.WebServlet;
// import javax.servlet.http.HttpServlet;
// import javax.servlet.http.HttpServletRequest;
// import javax.servlet.http.HttpServletResponse;
//
// import sun.security.x509.AuthorityInfoAccessExtension;
//
/// **
// * Servlet implementation class DownLoadApk
// * 2016.5.25
// */
// @WebServlet("/DownLoadApk")
// public class DownLoadApkService extends HttpServlet {
// private static final long serialVersionUID = 1L;
//
// /**
// * @see HttpServlet#HttpServlet()
// */
// public DownLoadApkService() {
// super();
// // TODO Auto-generated constructor stub
// }
//
// /**
// * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
// * response)
// */
// protected void doGet(HttpServletRequest request, HttpServletResponse
// response)
// throws ServletException, IOException {
//
// doPost(request, response);
// }
//
// /**
// * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
// * response)
// */
// protected void doPost(HttpServletRequest request, HttpServletResponse
// response)
// throws ServletException, IOException {
//
// // System.out.println("进入下载页面");
// // String path=request.getParameter("path");
// // System.out.println(path);
// // File f = new File(request.getRealPath("")+"/Resource/"+path);
// // if(f.exists()){
// // System.out.println("文件存在");
// // FileInputStream fis = new FileInputStream(f);
// // String filename=URLEncoder.encode(f.getName(),"utf-8");
// // //解决中文文件名下载后乱码的问题
// // response.setCharacterEncoding("utf-8");
// // response.setContentType("application/x-download");
// // response.setHeader("Content-Disposition","attachment;
// // filename="+filename+"");
// // response.setHeader("Accept-Ranges", "bytes");
// // //获取响应报文输出流对象
// // ServletOutputStream out =response.getOutputStream();
// // byte[] data = new byte[2048];
// // int len = 0;
// // while ((len = fis.read(data)) > 0) {
// // out.write(data, 0, len);
// // }
// // out.flush();
// // out.close();
// // fis.close();
// // }
// //
// System.out.println("进入下载页面");
// System.out.println("===========");
// String path = request.getParameter("path");
// System.out.println("请求的地址是："+path);
// System.out.println("===========");
// File downloadFile = new File(request.getRealPath("") + "/Resource/" + path);
// if (downloadFile.exists()) {
// System.out.println("文件存在");
// if (downloadFile.isFile()) {
// if (downloadFile.length() > 0) {
// } else {
// return;
// }
// if (!downloadFile.canRead()) {
// return;
// } else {
// }
// } else {
// return;
// }
// } else {
// return;
// }
//
// long fileLength = downloadFile.length(); // 记录文件大小
// long pastLength = 0; // 记录已下载文件大小
// int rangeSwitch = 0; //
// 0：从头开始的全文下载；1：从某字节开始的下载（bytes=27000-）；2：从某字节开始到某字节结束的下载（bytes=27000-39000）
// long toLength = 0; //
// 记录客户端需要下载的字节段的最后一个字节偏移量（比如bytes=27000-39000，则这个值是为39000）
// long contentLength = 0; // 客户端请求的字节总量
// String rangeBytes = ""; // 记录客户端传来的形如“bytes=27000-”或者“bytes=27000-39000”的内容
// RandomAccessFile raf = null; // 负责读取数据
// OutputStream os = null; // 写出数据
// OutputStream out = null; // 缓冲
// byte b[] = new byte[1024]; // 暂存容器
// //response.setStatus(206);
// response.reset(); // 告诉客户端允许断点续传多线程连接下载,响应的格式是:Accept-Ranges: bytes
// response.setHeader("Accept-Ranges", "bytes");
// if (request.getHeader("Range") != null) { // 客户端请求的下载的文件块的开始字节
// System.out.println("有断点请求");
// response.setStatus(206);
// rangeBytes = request.getHeader("Range").replaceAll("bytes=", "");
// System.out.println(rangeBytes);
// if (rangeBytes.indexOf('-') == rangeBytes.length() - 1) {// bytes=969998336-
// rangeSwitch = 1;
// rangeBytes = rangeBytes.substring(0, rangeBytes.indexOf('-'));
// pastLength = Long.parseLong(rangeBytes.trim());
// contentLength = fileLength - pastLength; // 客户端请求的是 969998336
// // 之后的字节
// System.out.println("类型：1132123-"+contentLength);
// } else { // bytes=1275856879-1275877358
// rangeSwitch = 2;
// String temp0 = rangeBytes.substring(0, rangeBytes.indexOf('-'));
// String temp2 = rangeBytes.substring(rangeBytes.indexOf('-') + 1,
// rangeBytes.length());
// pastLength = Long.parseLong(temp0.trim()); // bytes=1275856879-1275877358，从第
// // 1275856879
// // 个字节开始下载
// toLength = Long.parseLong(temp2.trim()); // bytes=1275856879-1275877358，到第
// // 1275877358 个字节结束
// contentLength = toLength - pastLength; // 客户端请求的是
// // 1275856879-1275877358
// // 之间的字节
// System.out.println("类型：1212-1121211"+contentLength+"==="+pastLength+"==="+toLength);
// }
// } else { // 从开始进行下载
// contentLength = fileLength; // 客户端要求全文下载
// System.out.println("一次性下载:"+contentLength);
// }
//// if (pastLength != 0) {
//// switch (rangeSwitch) {
//// case 1: { // 针对 bytes=27000- 的请求
//// String contentRange = new StringBuffer("bytes ").append(new
// Long(pastLength).toString()).append("-")
//// .append(new Long(fileLength - 1).toString()).append("/").append(new
// Long(fileLength).toString())
//// .toString();
//// response.setHeader("Content-Range", contentRange);
//// break;
//// }
//// case 2: { // 针对 bytes=27000-39000 的请求
//// String contentRange = rangeBytes + "/" + new Long(fileLength).toString();
//// response.setHeader("Content-Range", contentRange);
//// break;
//// }
//// default: {
//// break;
//// }
//// }
//// } else {
//// // 是从开始下载
//// }
//
// try {
// response.addHeader("Content-Disposition", "attachment; filename=\"" +
// downloadFile.getName() + "\"");
// response.addHeader("Content-Length", String.valueOf(contentLength));
// os = response.getOutputStream();
// out = new BufferedOutputStream(os);
// raf = new RandomAccessFile(downloadFile, "r");
// try {
// switch (rangeSwitch) {
// case 0: { // 普通下载，或者从头开始的下载
// // 同1
// }
// case 1: { // 针对 bytes=27000- 的请求
// raf.seek(pastLength); // 形如 bytes=969998336- 的客户端请求，跳过
// // 969998336 个字节
// int n = 0;
// while ((n = raf.read(b, 0, 1024)) != -1) {
// out.write(b, 0, n);
// }
// break;
// }
// case 2: { // 针对 bytes=27000-39000 的请求
// raf.seek(pastLength); // 形如 bytes=1275856879-1275877358
// // 的客户端请求，找到第 1275856879 个字节
// int n = 0;
// long readLength = 0; // 记录已读字节数
// while (readLength <= contentLength - 1024) {// 大部分字节在这里读取
// n = raf.read(b, 0, 1024);
// readLength += 1024;
// out.write(b, 0, n);
// }
// if (readLength <= contentLength) { // 余下的不足 1024 个字节在这里读取
// n = raf.read(b, 0, (int) (contentLength - readLength));
// out.write(b, 0, n);
// }
// break;
// }
// default: {
// break;
// }
// }
// out.flush();
// } catch (IOException ie) {
// // ignore
// }
// } catch (Exception e) {
// } finally {
// if (out != null) {
// try {
// out.close();
// } catch (IOException e) {
// }
// }
// if (raf != null) {
// try {
// raf.close();
// } catch (IOException e) {
// }
// }
// }
// }
// }
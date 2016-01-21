package com.ztm;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

public class NetTraffic {
	
	public static Cookie[] cookies = null;
	public static String dataUrl = "";
	public static int datamsg = -1;
	public static int runningTasks = 0;
	public static String data;
	public static ProgressDialog progressDialog = null;
	public static Handler myhandler;
	public static Thread acTrd;
	
	public static void getUrlHtml(Activity ac,String url, int msg,Handler handler) {
		
		runningTasks++;
		myhandler = handler;
		dataUrl = url;
		datamsg = msg;
		acTrd = new Thread() {

			@Override
			public void run() {
				// 需要花时间计算的方法
				try {
					data = NetTraffic.getHtmlContent(dataUrl);
				} catch (Exception e) {
					data = "error";
				}
				if (this.getName() != null
						&& this.getName().equals("NoUse")) {
					sendMsg(myhandler,12345);
					return;
				}
				sendMsg(myhandler,datamsg);
			}
		};
		
		if (msg == 123 || progressDialog == null || !progressDialog.isShowing()) {
			progressDialog = ProgressDialog.show(ac,
					"请稍等...", "抓取网页信息中...", true);
			progressDialog.setCancelable(true);
			 progressDialog.setOnCancelListener(new OnCancelListener(){
			     public void onCancel(DialogInterface arg0) {
			    		ConstParam.isLoading = false;
			    		runningTasks--;
			    		acTrd.setName("NoUse");
			      
			     }});
			
		}
		
		acTrd.start();

		
	}
	
	static NameValuePair[] nvpCont;
	
	public static void postUrlHtml(Activity ac,String url, NameValuePair[] newVp  ,int msg,Handler handler) {
		if (msg == 123 || progressDialog == null || !progressDialog.isShowing()) {
			progressDialog = ProgressDialog.show(ac,
					"请稍等...", "抓取网页信息中...", true);
		}
		runningTasks++;
		myhandler = handler;
		dataUrl = url;
		datamsg = msg;
		nvpCont = newVp;
		new Thread() {

			@Override
			public void run() {
				// 需要花时间计算的方法
				try {
					data = postHtmlContent(dataUrl, nvpCont);
				} catch (Exception e) {
					data = "error";
				}
				sendMsg(myhandler,datamsg);
			}
		}.start();

	}
	
	
	
	public static void sendMsg(Handler handler,int meg) {
		Message msg = new Message();
		msg.what = meg;
		handler.sendMessage(msg);
	}
	
	
	

	
	
	
	public static void setMyCookie(String NUM,String id ,String KEY)
	{
		cookies = new Cookie[3];

		cookies[0] = new Cookie();
		cookies[0].setDomain("bbs.nju.edu.cn");
		cookies[0].setPath("/");
		cookies[0].setName("_U_NUM");
		cookies[0].setValue(NUM);

		cookies[1] = new Cookie();
		cookies[1].setDomain("bbs.nju.edu.cn");
		cookies[1].setPath("/");
		cookies[1].setName("_U_UID");
		cookies[1].setValue(id);

		cookies[2] = new Cookie();
		cookies[2].setDomain("bbs.nju.edu.cn");
		cookies[2].setPath("/");
		cookies[2].setName("_U_KEY");
		cookies[2].setValue(KEY);
	}
	
	public static HttpClient getClient() {
		// 构造HttpClient的实例
		HttpClient httpClient = new HttpClient();

		if (cookies != null) {
			for (Cookie cc : cookies) {
				httpClient.getState().addCookie(cc);
			}
		}

		httpClient.getParams().setCookiePolicy(
				CookiePolicy.BROWSER_COMPATIBILITY);
		httpClient.getParams().setParameter(
				"http.protocol.single-cookie-header", true);
		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(
				10000);
		httpClient.getHttpConnectionManager().getParams().setSoTimeout(20000);

		return httpClient;
	}

	

	/**
	 * 根据url取得其对应的response
	 */
	public static String getHtmlContent(String url) {
		if (!url.startsWith("http"))
			url = "http:////" + url;
		String result = "";// 返回的结果
		StringBuffer resultBuffer = new StringBuffer();

		HttpClient httpClient = getClient();
		// 创建GET方法的实例
		GetMethod getMethod = new GetMethod(url);
		// getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
		// new DefaultHttpMethodRetryHandler());
		getMethod.getParams().setContentCharset("GB2312");
		try {
			// 执行getMethod
			int statusCode = httpClient.executeMethod(getMethod);
			if (statusCode != HttpStatus.SC_OK) {
				System.err.println("Method failed: "
						+ getMethod.getStatusLine());
			}
			BufferedReader in = new BufferedReader(new InputStreamReader(
					getMethod.getResponseBodyAsStream(), getMethod
							.getResponseCharSet()));
			String inputLine = null;
			while ((inputLine = in.readLine()) != null) {
				resultBuffer.append(inputLine);
				resultBuffer.append("\n");
			}
			result = new String(resultBuffer);
			return result;
		} catch (HttpException e) {
			// 发生致命的异常，可能是协议不对或者返回的内容有问题
			result = "error";
			e.printStackTrace();
		} catch (IOException e) {
			// 发生网络异常
			result = "error";
			e.printStackTrace();
		} finally {
			// 释放连接
			getMethod.releaseConnection();
		}
		return result;
	}

	/**
	 * POST 数据到服务器上
	 */
	public static String postHtmlContent(String url, NameValuePair[] nvp) {
		if (!url.startsWith("http"))
			url = "http:////" + url;
		String result = "";// 返回的结果
		StringBuffer resultBuffer = new StringBuffer();

		HttpClient httpClient = getClient();
		// 创建GET方法的实例
		PostMethod post = new PostMethod(url);
		// getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
		// new DefaultHttpMethodRetryHandler());
		post.getParams().setContentCharset("GB2312");
		post.setRequestBody(nvp);
		try {
			// 执行getMethod
			int statusCode = httpClient.executeMethod(post);
			if (statusCode != HttpStatus.SC_OK) {
				System.err.println("Method failed: " + post.getStatusLine());
			}
			BufferedReader in = new BufferedReader(new InputStreamReader(post
					.getResponseBodyAsStream(), post.getResponseCharSet()));
			String inputLine = null;
			while ((inputLine = in.readLine()) != null) {
				resultBuffer.append(inputLine);
				resultBuffer.append("\n");
			}
			result = new String(resultBuffer);
			return result;
		} catch (HttpException e) {
			// 发生致命的异常，可能是协议不对或者返回的内容有问题
			result = "error";
			e.printStackTrace();
		} catch (IOException e) {
			// 发生网络异常
			result = "error";
			e.printStackTrace();
		} finally {
			// 释放连接
			post.releaseConnection();
		}
		return result;
	}
	
	
	/**
	 * POST 数据到服务器上
	 */
	public static String postFile(String url,File file,String board) {
		if (!url.startsWith("http"))
			url = "http:////" + url;
		String result = "";// 返回的结果
		StringBuffer resultBuffer = new StringBuffer();

		HttpClient httpClient = getClient();
		
		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(20000);
		httpClient.getHttpConnectionManager().getParams().setSoTimeout(20000);
		
		// 创建GET方法的实例
		PostMethod post = new PostMethod(url);
		
		//PostMethod filePost = new PostMethod(targetURL);  


		
	
		try {
		
			
			FilePart part1 = new FilePart("up",file.getName(),file);
			StringPart sp = new StringPart("board",board);
			StringPart sp1 = new StringPart("ptext","");
			StringPart sp2 = new StringPart("exp","UploadByLilyDroid");
			
			
			Part[] parts = {part1,sp2,sp1,sp };
			//post.getParams().setContentCharset("GB2312");
			post.setRequestEntity(new MultipartRequestEntity(parts,	post.getParams()));
			
			post.addRequestHeader("Content-Type", "multipart/form-data");
			// 执行getMethod
			
			
			int statusCode = httpClient.executeMethod(post);
			if (statusCode != HttpStatus.SC_OK) {
				System.err.println("Method failed: " + post.getStatusLine());
			}
			BufferedReader in = new BufferedReader(new InputStreamReader(post
					.getResponseBodyAsStream(), post.getResponseCharSet()));
			String inputLine = null;
			while ((inputLine = in.readLine()) != null) {
				resultBuffer.append(inputLine);
				resultBuffer.append("\n");
			}
			result = new String(resultBuffer);
			return result;
		}  catch (Exception e) {
			// 发生网络异常
			result = "error";
			e.printStackTrace();
		} finally {
			// 释放连接
			post.releaseConnection();
		}
		return result;
	}

}

package com.wondertek.mobilevideo.recommend.webapp.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;

import com.wondertek.mobilevideo.core.recommend.util.RecommendUtil;
import com.wondertek.mobilevideo.core.util.StringUtil;
import com.wondertek.mobilevideo.recommend.webapp.action.BaseAction;

public class UpdateCacheServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	public final static Log log = LogFactory.getLog(UpdateCacheServlet.class);
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		this.doPost(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		log.info("UpdateCacheServlet---[RemoteAddr=" + req.getRemoteAddr() + "],RequestURL=" + req.getRequestURL() + "],RequestURI=" 
				+ req.getRequestURI() + "],RequestQuery="+req.getQueryString());
		
		String type = StringUtil.null2Str(req.getParameter("type"));
		String isRefresh = req.getParameter("isRefresh");
		String otherParameter = null;
		if(type.equalsIgnoreCase("1")){//刷新所有缓存
			log.debug("========Handler RefreshCache is start========");
			try{
				ServletContext context = ServletActionContext.getServletContext();
				//刷新所有缓存
				RecommendUtil.init();
				
				BaseAction.setupContext(context);
			}catch(Exception e){
				StringBuilder sb = new StringBuilder();
				sb.append("{success:false}");
				resp.setContentType("text/html");    
				resp.setCharacterEncoding("UTF-8");    
				PrintWriter out = resp.getWriter();    
				out.println(sb);    
				out.close(); 
				return ;
			}
			log.debug("========Handler RefreshCache is end==========");
		}else{
			StringBuilder sb = new StringBuilder();
			sb.append("{success:false}");
			resp.setContentType("text/html");    
			resp.setCharacterEncoding("UTF-8");    
			PrintWriter out = resp.getWriter();    
			out.println(sb);    
			out.close();  
			return;
		}
		
		// 刷新其他服务器缓存
		if(isRefresh == null || !"NO".equalsIgnoreCase(isRefresh.trim())){
			UpdateCacheThread tp = new UpdateCacheThread(req.getRequestURI().toString(), type,otherParameter);
			tp.start();
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append("{success:true}");
		resp.setContentType("text/html");    
		resp.setCharacterEncoding("UTF-8");    
		PrintWriter out = resp.getWriter();    
		out.println(sb);    
		out.close();  
	}

}
package com.wondertek.mobilevideo.recommend.webapp.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.wondertek.mobilevideo.core.recommend.commons.RecommendConstants;

public class MultiLangFilter implements Filter {
	private static final Logger log = Logger.getLogger(MultiLangFilter.class);
    
    public MultiLangFilter() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Filter#destroy()
	 */
	@Override
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest)request;
		HttpSession session = req.getSession(false);
		if(session != null) {
			Object langObj = session.getAttribute("_lang");
			if(langObj == null) {
				session.setAttribute("_lang", RecommendConstants.LANG_S_CN);//默认为简体中文
				log.info("user " + session.getAttribute("_sso_username") + " initialize lang " + RecommendConstants.LANG_S_CN);
			}
			
			log.debug("user " + session.getAttribute("_sso_username") + " choose lang " + session.getAttribute("_lang"));
		}
		
		chain.doFilter(request, response);
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	@Override
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}

}

package com.wondertek.mobilevideo.recommend.webapp.listener;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.wondertek.mobilevideo.core.base.Constants;
import com.wondertek.mobilevideo.core.recommend.util.AppContextUtils;
import com.wondertek.mobilevideo.core.recommend.util.RecommendUtil;
import com.wondertek.mobilevideo.core.recommend.util.SysTool;

public class StartupListener implements ServletContextListener {
	private static final Log log = LogFactory.getLog(StartupListener.class);

	@Override
	@SuppressWarnings("unchecked")
	public void contextInitialized(ServletContextEvent event) {
		try {
			if (log.isInfoEnabled()) {
				log.info("contextInitialized start...");
			}
			ServletContext context = event.getServletContext();
			Constants.DEPOSITORY_PATH = context.getRealPath(Constants.DEPOSITORY);
			SysTool.WEB_INF_PATH = context.getRealPath(SysTool.WEB_INF);

			Map<String, Object> config = (HashMap<String, Object>) context.getAttribute(Constants.CONFIG);
			if (config == null) {
				config = new HashMap<String, Object>();
			}
			ApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(context);
			Constants.ctx = ctx;
			
			String[] beans = ctx.getBeanDefinitionNames();
			for (String bean : beans) {
				log.info(bean);
			}
			context.setAttribute(Constants.CONFIG, config);
			AppContextUtils.setApplicationContext(ctx);
			
			RecommendUtil.init();
			
			if (log.isInfoEnabled()) {
				log.info("contextInitialized end...");
			}
		} catch (Exception e) {
			log.error(e, e);
			e.printStackTrace();
		}
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		
	}
}

package com.wondertek.mobilevideo.recommend.webapp.job;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.wondertek.mobilevideo.core.recommend.util.RecommendUtil;
/**
 * 系统刷新缓存，定期刷新系统属性
 * @author lvliuzhong
 *
 */
public class SystemRefreshJob{
	private static final Log log = LogFactory.getLog(SystemRefreshJob.class);
	protected void refresh() {
		Long start = System.currentTimeMillis();
		log.debug("SystemRefreshJob start");
		RecommendUtil.initSysCache();
		Long end = System.currentTimeMillis();
		log.debug("SystemRefreshJob end,duration:" + (end - start));
	}
}
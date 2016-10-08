package com.wondertek.mobilevideo.recommend.webapp.job;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.wondertek.mobilevideo.core.recommend.util.RecommendUtil;
/**
 * 定时清理redis里面的数据
 * @author lvliuzhong
 *
 */
public class RedisRefreshJob{
	private static final Log log = LogFactory.getLog(RedisRefreshJob.class);
	protected void refresh() {
		Long start = System.currentTimeMillis();
		log.debug("RecomdRefreshJob start");
		RecommendUtil.initRedisCache();
		Long end = System.currentTimeMillis();
		log.debug("RecomdRefreshJob end,duration:" + (end - start));
	}
}
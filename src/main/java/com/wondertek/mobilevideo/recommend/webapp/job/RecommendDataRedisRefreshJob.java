package com.wondertek.mobilevideo.recommend.webapp.job;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.wondertek.mobilevideo.core.recommend.util.RecommendUtil;
/**
 * 定期刷新redis里面的数据
 * @author madongwei
 *
 */
public class RecommendDataRedisRefreshJob {
	public static final Log log = LogFactory.getLog(RecommendDataRedisRefreshJob.class);
	protected void refresh() {
		Long start = System.currentTimeMillis();
		log.debug("RecommendDataRedisRefreshJob start");
		RecommendUtil.initRecommednDataRedisCache();
		Long end = System.currentTimeMillis();
		log.debug("RecommendDataRedisRefreshJob end,duration:"+(end-start));
	}
}

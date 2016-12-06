package com.wondertek.mobilevideo.recommend.webapp.job;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.wondertek.mobilevideo.core.recommend.util.RecommendUtil;
/**
 * 定期刷新redis里面的数据
 * @author madongwei
 *
 */
public class VomsRecommendRedisRefreshJob {
	public static final Log log = LogFactory.getLog(VomsRecommendRedisRefreshJob.class);
	protected void refresh() {
		Long start = System.currentTimeMillis();
		log.debug("VomsRecommendRedisRefreshJob start");
		RecommendUtil.initVomsRecommednRedisCache();
		Long end = System.currentTimeMillis();
		log.debug("VomsRecommendRedisRefreshJob end,duration:"+(end-start));
	}
}

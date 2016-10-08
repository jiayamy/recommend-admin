package com.wondertek.mobilevideo.recommend.webapp.action;

import org.junit.Before;

import com.wondertek.mobilevideo.core.base.GenericActionTest;

public class RecommendInfoActionTest extends GenericActionTest<RecommendInfoAction> {
	
	@Override
	protected String[] getContextConfigs() {
		return new String[] { "classpath:/applicationContext-resources.xml",
				"classpath*:/applicationContext-mam*.xml",
				"classpath:/applicationContext*.xml" };
	}
	
	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
		this.namespace = "/pages/";
		this.extension = ".htm";
//		this.request.addHeader("X-UP-CALLING-LINE-ID", "13777777777");
//		 this.request.setParameter("p1", "pvalue");
//		 this.request.setParameter("p1", "pvalue");
	}
	
}

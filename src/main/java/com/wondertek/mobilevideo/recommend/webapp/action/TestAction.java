package com.wondertek.mobilevideo.recommend.webapp.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.NameValuePair;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wondertek.mobilevideo.core.recommend.cache.redis.service.UserTagCacheManager;
import com.wondertek.mobilevideo.core.recommend.search.HttpClientUtil;
import com.wondertek.mobilevideo.core.recommend.util.RequestConstants;
import com.wondertek.mobilevideo.core.recommend.vo.mongo.CatInfo;
import com.wondertek.mobilevideo.core.recommend.vo.mongo.UserTag;
import com.wondertek.mobilevideo.core.util.StringUtil;

/**
 * 
 * @author madongwei
 * 处理testAction
 *
 */
public class TestAction extends BaseAction {
	private static final long serialVersionUID = -2836437294838899724L;
	
	private UserTagCacheManager userTagCacheManager;
	private UserTag userTag;
	private String catsInfoJson; //这个是默认模式传来的catsJson
	private String catsJson;  //这是自定义格式传来的json
	private int choosJson; //这是选择哪一种模式传json   value = 1 表示是第一种默认模式，，，value=2 表示是自定义模式传json
	
	
	public void testQueryTag() {
		try {
			resultMap.put("success", true);
			String userId = getParam("userId");
			String host = getParam("host");
			if (StringUtil.isNullStr(userId)) {
				resultMap.put(RequestConstants.R_SUCC, Boolean.FALSE);
				resultMap.put(RequestConstants.R_CODE, RequestConstants.R_CODE_110001);
				resultMap.put(RequestConstants.R_MSG, "userId为空");
				this.writeTextResponse(JSON.toJSONString(resultMap));
				return ;
			}
			if (StringUtil.isNullStr(host)) {
				resultMap.put(RequestConstants.R_CODE, RequestConstants.R_CODE_110003);
				resultMap.put(RequestConstants.R_SUCC, Boolean.FALSE);
				resultMap.put(RequestConstants.R_MSG,this.getText("request.error.contentnull"));
				this.writeTextResponse(JSON.toJSONString(resultMap));
				return ;
			}
			String url = "http://" + host + "/req/queryTag.msp";
			//可以传递
			Map<String, String> map = new HashMap<String,String>();
			map.put("userId", userId);
			String rst = HttpClientUtil.httpClientPost(url, HttpClientUtil.praseParameterMap(map));
			if(StringUtil.isNullStr(rst)){
				resultMap.put(RequestConstants.R_CODE, RequestConstants.R_CODE_110003);
				resultMap.put(RequestConstants.R_SUCC, Boolean.FALSE);
				resultMap.put(RequestConstants.R_MSG,"连接服务器出现异常");
				this.writeTextResponse(JSON.toJSONString(resultMap));
				return;
			}
			JSONObject obj = JSON.parseObject(rst);
			if(obj.containsKey("root") && obj.containsKey("success") && obj.getBooleanValue("success") 
					&& obj.containsKey("returnCode") && "000000".equals(obj.getString("returnCode"))
					&& obj.get("root") != null){
				Object root = obj.get("root");
				UserTag userTag = JSON.parseObject(root.toString(), UserTag.class);
				if(StringUtil.isNullStr(userTag.getId())){
					this.writeTextResponse(rst);
				}else{
					this.writeTextResponse(JSON.toJSONString(JSON.parseObject(root.toString()), true));
				}
			}else{
				this.writeTextResponse(rst);
			}
		} catch (Exception e) {
			resultMap.put("error", true);
			this.writeTextResponse(JSON.toJSONString(resultMap));
		}
		return ;
		
	}
	/**
	 * 处理testAction
	 *  [{"catName":"电影","score":1.03,"recommendation":[{"label":"影视1","score":100}],"items":[{"labelName":"播出年代","labelValue":"2016","score":100}]},{"catName":"电视剧","score":1.03,"recommendation":[{"label":"影视2","score":100}],"items":[{"labelName":"播出年代","labelValue":"2016","score":100}]}]}
	 * @return
	 */
	//userTag的json {"id":"681274129","prdType":"MIGUVIDEO","start":"0","limit":"10","ctVer":"v1.0","cats":[{"catName":"电影","score":1.03,"recommendation":[{"label":"影视1","score":100}],"items":[{"labelName":"播出年代","labelValue":"2016","score":100}]},{"catName":"电视剧","score":1.03,"recommendation":[{"label":"影视2","score":100}],"items":[{"labelName":"播出年代","labelValue":"2016","score":100}]}]
	
	
	public void testSearch() {
		String host = getParam("host");
		resultMap.put(RequestConstants.R_SUCC, Boolean.TRUE);
		try {
			catsInfoJson = StringUtil.null2Str(catsInfoJson);
			UserTag oldUserTag = null;
			if(StringUtil.isNullStr(catsInfoJson)) {
				try {
					oldUserTag = JSON.parseObject(catsInfoJson, UserTag.class);
				} catch (Exception e) {
					
				}
			}
			if(oldUserTag != null){
				if(!StringUtil.isNullStr(userTag.getId())){
					oldUserTag.setId(userTag.getId());
				}
				if(userTag.getStart() != null){
					oldUserTag.setStart(userTag.getStart());
				}
				if(userTag.getLimit() != null){
					oldUserTag.setLimit(userTag.getLimit());
				}
				if(!StringUtil.isNullStr(userTag.getCtVer())){
					oldUserTag.setCtVer(userTag.getCtVer());
				}
				if(!StringUtil.isNullStr(userTag.getPrdType())){
					oldUserTag.setPrdType(userTag.getPrdType());
				}
			}else{
				oldUserTag = userTag;
			}
		    String userTagJson = JSON.toJSONString(oldUserTag);
		    //可以传递
			String url = "http://" + host + "/req/search.msp";
			String rst = HttpClientUtil.httpClientPost(url, new NameValuePair[0], userTagJson);
			if(StringUtil.isNullStr(rst)){
				resultMap.put(RequestConstants.R_CODE, RequestConstants.R_CODE_110003);
				resultMap.put(RequestConstants.R_SUCC, Boolean.FALSE);
				resultMap.put(RequestConstants.R_MSG,"连接服务器出现异常");
				this.writeTextResponse(JSON.toJSONString(resultMap));
				return;
			}
			this.writeTextResponse(JSON.toJSONString(JSON.parseObject(rst), true));
		} catch (Exception e) {
			resultMap.put("error", true);
			this.writeTextResponse(JSON.toJSONString(resultMap));
		}
		return ;
		
		
	}

	
	
	public UserTagCacheManager getUserTagCacheManager() {
		return userTagCacheManager;
	}
	public void setUserTagCacheManager(UserTagCacheManager userTagCacheManager) {
		this.userTagCacheManager = userTagCacheManager;
	}
	public UserTag getUserTag() {
		return userTag;
	}
	public void setUserTag(UserTag userTag) {
		this.userTag = userTag;
	}
	public String getCatsInfoJson() {
		return catsInfoJson;
	}
	public void setCatsInfoJson(String catsInfoJson) {
		this.catsInfoJson = catsInfoJson;
	}
	public String getCatsJson() {
		return catsJson;
	}
	public void setCatsJson(String catsJson) {
		this.catsJson = catsJson;
	}
	public int getChoosJson() {
		return choosJson;
	}
	public void setChoosJson(int choosJson) {
		this.choosJson = choosJson;
	}
	
	
}
	
	



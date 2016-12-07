package com.wondertek.mobilevideo.recommend.webapp.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.NameValuePair;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wondertek.mobilevideo.core.poms.bean.Poms;
import com.wondertek.mobilevideo.core.poms.service.PomsService;
import com.wondertek.mobilevideo.core.recommend.cache.redis.service.UserTagCacheManager;
import com.wondertek.mobilevideo.core.recommend.util.RequestConstants;
import com.wondertek.mobilevideo.core.recommend.vo.mongo.UserTag;
import com.wondertek.mobilevideo.core.util.StringUtil;
import com.wondertek.mobilevideo.recommend.webapp.util.HttpClientUtil;

/**
 * @author madongwei
 * 处理testAction
 */
public class TestAction extends BaseAction {
	private static final long serialVersionUID = -2836437294838899724L;
	
	private UserTagCacheManager userTagCacheManager;
	private UserTag userTag;
	private String catsInfoJson; //这个是默认模式传来的catsJson
	private PomsService pomsService;
	
	/**
	 * 测试获取用户标签
	 */
	public void testQueryTag() {
		try {
			//获取参数
			String userId = getParam("userId");
			String host = getParam("host");
			//校验参数
			if (StringUtil.isNullStr(userId)) {
				resultMap.put(RequestConstants.R_SUCC, Boolean.FALSE);
				resultMap.put(RequestConstants.R_MSG, "用户ID不可为空");
				this.writeTextResponse(JSON.toJSONString(resultMap));
				return ;
			}
			if (StringUtil.isNullStr(host)) {
				resultMap.put(RequestConstants.R_SUCC, Boolean.FALSE);
				resultMap.put(RequestConstants.R_MSG, "服务器不可为空");
				this.writeTextResponse(JSON.toJSONString(resultMap));
				return ;
			}
			//请求指定推荐服务器
			String url = "http://" + host + "/req/queryTag.msp";
			
			Map<String, String> map = new HashMap<String,String>();
			map.put("userId", userId);
			String rst = HttpClientUtil.httpClientPost(url, HttpClientUtil.praseParameterMap(map));
			if(StringUtil.isNullStr(rst)){
				resultMap.put(RequestConstants.R_SUCC, Boolean.FALSE);
				resultMap.put(RequestConstants.R_MSG,"连接指定推荐服务器出现异常");
				this.writeTextResponse(JSON.toJSONString(resultMap));
				return;
			}
			//解析返回数据
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
	 * 测试内容推荐
	 */
	public void testSearch() {
		try {
			//获取校验参数
			String host = getParam("host");
			if (StringUtil.isNullStr(host)) {
				resultMap.put(RequestConstants.R_SUCC, Boolean.FALSE);
				resultMap.put(RequestConstants.R_MSG, "服务器不可为空");
				this.writeTextResponse(JSON.toJSONString(resultMap));
				return ;
			}
			catsInfoJson = StringUtil.null2Str(catsInfoJson);
			UserTag oldUserTag = null;
			if(!StringUtil.isNullStr(catsInfoJson)) {
				try {
					oldUserTag = JSON.parseObject(catsInfoJson, UserTag.class);
				} catch (Exception e) {
					log.error("处理json串出错，msg:"+e.getMessage()+",json:" + catsInfoJson);
				}
			}
			if(oldUserTag != null){//以手动输入的为准
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
			}else{//否则默认就是输入的对象
				oldUserTag = userTag;
			}
			if(oldUserTag == null){
				resultMap.put(RequestConstants.R_SUCC, Boolean.FALSE);
				resultMap.put(RequestConstants.R_MSG,"未找到任何参数");
				this.writeTextResponse(JSON.toJSONString(resultMap));
				return;
			}
		    String userTagJson = JSON.toJSONString(oldUserTag);
		    //可以传递
			String url = "http://" + host + "/req/search.msp";
			String rst = HttpClientUtil.httpClientPost(url, new NameValuePair[0], userTagJson);
			if(StringUtil.isNullStr(rst)){
				resultMap.put(RequestConstants.R_SUCC, Boolean.FALSE);
				resultMap.put(RequestConstants.R_MSG,"连接指定推荐服务器出现异常");
				this.writeTextResponse(JSON.toJSONString(resultMap));
				return;
			}
			this.writeTextResponse(JSON.toJSONString(JSON.parseObject(rst), true));
		} catch (Exception e) {
			resultMap.put("error", true);
			this.writeTextResponse(JSON.toJSONString(resultMap));
		}
	}

	/**
	 * 测试系统由prdContId 得到整个节目的信息
	 */
	public void testSystemSearch() {
		try {
			//获取校验参数
			String host = getParam("host");
			if (StringUtil.isNullStr(host)) {
				resultMap.put(RequestConstants.R_SUCC, Boolean.FALSE);
				resultMap.put(RequestConstants.R_MSG, "服务器不可为空");
				this.writeTextResponse(JSON.toJSONString(resultMap));
				return ;
			}
			catsInfoJson = StringUtil.null2Str(catsInfoJson);
			UserTag oldUserTag = null;
			if(!StringUtil.isNullStr(catsInfoJson)) {
				try {
					oldUserTag = JSON.parseObject(catsInfoJson, UserTag.class);
				} catch (Exception e) {
					log.error("处理json串出错，msg:"+e.getMessage()+",json:" + catsInfoJson);
				}
			}
			if(oldUserTag != null){//以手动输入的为准
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
			}else{//否则默认就是输入的对象
				oldUserTag = userTag;
			}
			if(oldUserTag == null){
				resultMap.put(RequestConstants.R_SUCC, Boolean.FALSE);
				resultMap.put(RequestConstants.R_MSG,"未找到任何参数");
				this.writeTextResponse(JSON.toJSONString(resultMap));
				return;
			}
		    String userTagJson = JSON.toJSONString(oldUserTag);
		    //可以传递
			String url = "http://" + host + "/req/search.msp";
			String rst = HttpClientUtil.httpClientPost(url, new NameValuePair[0], userTagJson);
			
			if(StringUtil.isNullStr(rst)){
				resultMap.put(RequestConstants.R_SUCC, Boolean.FALSE);
				resultMap.put(RequestConstants.R_MSG,"连接指定推荐服务器出现异常");
				this.writeTextResponse(JSON.toJSONString(resultMap));
				return;
			}
			//根据rootArray得到 List<Poms>
			JSONObject rstObjJson = JSON.parseObject(rst);
			JSONArray rootArray = (JSONArray)JSON.parse(rstObjJson.get("root").toString());
			List<Poms> pomses = this.getPomses(rootArray);
			System.out.println(pomses);
			
			this.writeTextResponse(JSON.toJSONString(JSON.parseObject(rst), true));
		} catch (Exception e) {
			resultMap.put("error", true);
			this.writeTextResponse(JSON.toJSONString(resultMap));
		}
	}
	
	//根据rootArray去service中得到 List<Poms>
	public List<Poms> getPomses(JSONArray rootArray ) {
		List<Long> prdContIds = null;
		List<Poms> pomses = null;
		try {
			if (rootArray != null) {
				//遍历rootArray
				String rootArrayStr = JSON.toJSONString(rootArray);
				System.out.println(rootArrayStr);
				List<HashMap> rootArrayMap = JSON.parseArray(rootArrayStr, HashMap.class);
				for (int i = 0; i < rootArrayMap.size(); i++) {
					String prdContId = (String) rootArrayMap.get(i).get("prdContId");
					 prdContIds.add(StringUtil.nullToLong(prdContId));
				}
			}
			pomses = pomsService.testSystemSearch(prdContIds);
			
		} catch (Exception e) {
			resultMap.put("error", true);
			resultMap.put(RequestConstants.R_MSG, "出现未知错误");
			e.printStackTrace();
		}
		return pomses;
			
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
	public PomsService getPomsService() {
		return pomsService;
	}
	public void setPomsService(PomsService pomsService) {
		this.pomsService = pomsService;
	}
	
}
package com.wondertek.mobilevideo.recommend.webapp.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.NameValuePair;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.wondertek.mobilevideo.core.recommend.cache.redis.service.UserTagCacheManager;
import com.wondertek.mobilevideo.core.recommend.service.PomsService;
import com.wondertek.mobilevideo.core.recommend.util.RequestConstants;
import com.wondertek.mobilevideo.core.recommend.vo.PrdContInfo;
import com.wondertek.mobilevideo.core.recommend.vo.RecommendInfoVo;
import com.wondertek.mobilevideo.core.recommend.vo.VomsRecommendVo;
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
	private String userTagStr;
	
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
	 * 测试由prdContId 得到整个节目的信息
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
			String code = rstObjJson.getString(RequestConstants.R_CODE);
			if(RequestConstants.R_CODE_000000.equals(code)){
				List<RecommendInfoVo> list = JSON.parseArray(rstObjJson.get("root").toString(), RecommendInfoVo.class);
				
				List<PrdContInfo> pomses = this.getPomses(list);
				resultMap.put(RequestConstants.R_SUCC, rstObjJson.get(RequestConstants.R_SUCC));
				resultMap.put(RequestConstants.R_CODE, rstObjJson.get(RequestConstants.R_CODE));
				resultMap.put(RequestConstants.R_MSG, rstObjJson.get(RequestConstants.R_MSG));
				resultMap.put(RequestConstants.R_ROOT, pomses);
				resultMap.put(RequestConstants.R_TOTAL, rstObjJson.get(RequestConstants.R_TOTAL));
				
				this.writeTextResponse(JSON.toJSONString(resultMap,SerializerFeature.WriteDateUseDateFormat));
				return;
			}
			this.writeTextResponse(JSON.toJSONString(rstObjJson, true));
		} catch (Exception e) {
			resultMap.put("error", true);
			this.writeTextResponse(JSON.toJSONString(resultMap));
		}
	}
	
	public void testSearchAll() {
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
			String url = "http://" + host + "/req/searchAll.msp";
			String rst = HttpClientUtil.httpClientPost(url, new NameValuePair[0], userTagJson);
			if(StringUtil.isNullStr(rst)){
				resultMap.put(RequestConstants.R_SUCC, Boolean.FALSE);
				resultMap.put(RequestConstants.R_MSG,"连接指定推荐服务器出现异常");
				this.writeTextResponse(JSON.toJSONString(resultMap));
				return;
			}
			//根据rootArray得到 List<Poms>
			JSONObject rstObjJson = JSON.parseObject(rst);
			String code = rstObjJson.getString(RequestConstants.R_CODE);
			if(RequestConstants.R_CODE_000000.equals(code)){
				List<RecommendInfoVo> list = JSON.parseArray(rstObjJson.get(RequestConstants.R_POMS_CONT).toString(), RecommendInfoVo.class);
				List<VomsRecommendVo> specialTopicList = JSON.parseArray(rstObjJson.get(RequestConstants.R_VOMS_SPECIALTOPIC).toString(), VomsRecommendVo.class);
				List<VomsRecommendVo> combinedContList = JSON.parseArray(rstObjJson.get(RequestConstants.R_VOMS_COMBINEDCONT).toString(), VomsRecommendVo.class);
				List<VomsRecommendVo> bigPicContList = JSON.parseArray(rstObjJson.get(RequestConstants.R_VOMS_BIGPICCONT).toString(), VomsRecommendVo.class);
				List<VomsRecommendVo> multiPicContList = JSON.parseArray(rstObjJson.get(RequestConstants.R_VOMS_MULTIPICCONT).toString(), VomsRecommendVo.class);
				
				List<PrdContInfo> pomses = this.getPomses(list);
				resultMap.put(RequestConstants.R_SUCC, rstObjJson.get(RequestConstants.R_SUCC));
				resultMap.put(RequestConstants.R_CODE, rstObjJson.get(RequestConstants.R_CODE));
				resultMap.put(RequestConstants.R_MSG, rstObjJson.get(RequestConstants.R_MSG));
				resultMap.put(RequestConstants.R_POMS_CONT, pomses);
				resultMap.put(RequestConstants.R_TOTAL, rstObjJson.get(RequestConstants.R_TOTAL));
				resultMap.put(RequestConstants.R_VOMS_SPECIALTOPIC, specialTopicList);
				resultMap.put(RequestConstants.R_TOTAL_SPECIALTOPIC, rstObjJson.get(RequestConstants.R_TOTAL_SPECIALTOPIC));
				resultMap.put(RequestConstants.R_VOMS_COMBINEDCONT, combinedContList);
				resultMap.put(RequestConstants.R_TOTAL_COMBINEDCONT, rstObjJson.get(RequestConstants.R_TOTAL_COMBINEDCONT));
				resultMap.put(RequestConstants.R_VOMS_BIGPICCONT, bigPicContList);
				resultMap.put(RequestConstants.R_TOTAL_BIGPICCONT, rstObjJson.get(RequestConstants.R_TOTAL_BIGPICCONT));
				resultMap.put(RequestConstants.R_VOMS_MULTIPICCONT, multiPicContList);
				resultMap.put(RequestConstants.R_TOTAL_MULTIPICCONT, rstObjJson.get(RequestConstants.R_TOTAL_MULTIPICCONT));
				
				this.writeTextResponse(JSON.toJSONString(resultMap,SerializerFeature.WriteDateUseDateFormat));
				return;
			}
			this.writeTextResponse(JSON.toJSONString(rstObjJson, true));
			
		} catch (Exception e) {
			resultMap.put("error", true);
			this.writeTextResponse(JSON.toJSONString(resultMap));
		}
	}
	
	public void testSearchVoms() {
		try {
			//获取校验参数
			String host = getParam("host");
			if (StringUtil.isNullStr(host)) {
				resultMap.put(RequestConstants.R_SUCC, Boolean.FALSE);
				resultMap.put(RequestConstants.R_MSG, "服务器不可为空");
				this.writeTextResponse(JSON.toJSONString(resultMap));
				return ;
			}
			String id = this.getParam("id");
			String type = this.getParam("type");
			String prdType = this.getParam("prdType");
			String labelInfo = this.getParam("labelInfo");
			String startStr = this.getParam("start");
			String limitStr = this.getParam("limit");
			if (StringUtil.isNullStr(labelInfo) || StringUtil.isNullStr(prdType) 
					|| StringUtil.isNullStr(startStr)  || StringUtil.isNullStr(limitStr) ) {
				resultMap.put(RequestConstants.R_SUCC, Boolean.FALSE);
				resultMap.put(RequestConstants.R_MSG, "必填参数为空");
				this.writeTextResponse(JSON.toJSONString(resultMap));
				return ;
			}
			NameValuePair[] values ={
					new NameValuePair("id",id),
					new NameValuePair("type",type),
					new NameValuePair("prdType",prdType),
					new NameValuePair("labelInfo",labelInfo),
					new NameValuePair("start",startStr),
					new NameValuePair("limit",limitStr),
			};
			
		    //可以传递
			String url = "http://" + host + "/req/searchVoms.msp";
			String rst = HttpClientUtil.httpClientPost(url,values);
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
	
	//根据rootArray去service中得到 List<Poms>
	public List<PrdContInfo> getPomses(List<RecommendInfoVo> list ) {
		List<PrdContInfo> pomses = null;
		List<PrdContInfo> rst = new ArrayList<PrdContInfo>();
		if (list != null && !list.isEmpty()) {
			List<Long> prdContIds = new ArrayList<Long>();
			for (RecommendInfoVo vo : list) {
				prdContIds.add(vo.getPrdContId());
			}
			try {
				pomses = pomsService.getInfoByPrdContIds(prdContIds);
			} catch (Exception e) {
				log.error(e.getMessage(),e);
			}
			prdContIds.clear();
			prdContIds = null;
			
			Map<Long, PrdContInfo> infos = new HashMap<Long,PrdContInfo>();
			if(pomses != null){
				for(PrdContInfo info : pomses){
					infos.put(info.getPrdContId(), info);
				}
				pomses.clear();
				pomses = null;
			}
			
			PrdContInfo info = null;
			for (RecommendInfoVo vo : list) {
				info = infos.get(vo.getPrdContId());
				if(info == null){
					rst.add(new PrdContInfo(vo.getPrdContId(),vo.getContName()));
				}else{
					rst.add(info);
				}
			}
			infos.clear();
			infos = null;
		}
		return rst;
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
	public String getUserTagStr() {
		return userTagStr;
	}
	public void setUserTagStr(String userTagStr) {
		this.userTagStr = userTagStr;
	}
}
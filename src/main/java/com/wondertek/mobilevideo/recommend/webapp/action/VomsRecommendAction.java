package com.wondertek.mobilevideo.recommend.webapp.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wondertek.mobilevideo.core.recommend.bean.PageList;
import com.wondertek.mobilevideo.core.recommend.cache.PrdTypeRelationCache;
import com.wondertek.mobilevideo.core.recommend.model.PrdTypeRelation;
import com.wondertek.mobilevideo.core.recommend.model.VomsRecommend;
import com.wondertek.mobilevideo.core.recommend.service.VomsRecommendService;
import com.wondertek.mobilevideo.core.recommend.util.RequestUtil;
import com.wondertek.mobilevideo.core.recommend.util.ST;
import com.wondertek.mobilevideo.core.util.StringUtil;

/**
 *
 */
public class VomsRecommendAction extends BaseAction {
	private static final long serialVersionUID = -2493942786912764258L;

	private VomsRecommendService vomsRecommendService;
	private List<PrdTypeRelation> prdTypeRelations;
	
	public String getPage(){
		String ip = RequestUtil.getIpAddr(this.getRequest());
		if(log.isInfoEnabled())
			log.info("getPage:" + ip);
		
		prdTypeRelations = new ArrayList<PrdTypeRelation>();
		for(String key : PrdTypeRelationCache.PRDTYPE_RELATIONS.keySet()){
			prdTypeRelations.add(PrdTypeRelationCache.PRDTYPE_RELATIONS.get(key));
		}
		return SUCCESS;
	}
	public String addVomsRecommend(){
		try {
			resultMap.put("success", true);
			String ip = RequestUtil.getIpAddr(this.getRequest());
			if(log.isInfoEnabled())
				log.info("addVomsRecommend:" + ip);
			//获取参数
			String objIdStr = this.getParam("objId");
			String objTypeStr = this.getParam("objType");
			String prdType = this.getParam("prdType");
			String typeStr = this.getParam("type");
			String name = this.getParam("name");
			String isRecommendStr = this.getParam("isRecommend");
			String labelInfo = this.getParam("labelInfo");
			//校验参数
			Long objId = StringUtil.nullToCloneLong(objIdStr);
			String type = StringUtil.nullToString(typeStr);
			String objType = StringUtil.nullToString(objTypeStr);
			
			if(objId == null || StringUtil.isNullStr(isRecommendStr) || StringUtil.isNullStr(objTypeStr)){//类型校验
				resultMap.put("error", true);
				resultMap.put("message", this.getText("voms.recomd.param.error"));
				return SUCCESS;
			}
			Boolean isRecommend = StringUtil.nullToBoolean(isRecommendStr);
			if(isRecommend){//推荐校验
				if(StringUtil.isNullStr(prdType) || StringUtil.isNullStr(typeStr) || StringUtil.isNullStr(name) || StringUtil.isNullStr(labelInfo)){
					resultMap.put("error", true);
					resultMap.put("message", this.getText("voms.recomd.param.none"));
					return SUCCESS;
				}
				StringBuffer sb = new StringBuffer();
				String[] labelInfos = labelInfo.split("[,|，|\\||#]");
				if(labelInfos != null){
					for(String tmp : labelInfos){
						if(!StringUtil.isNullStr(tmp)){
							sb.append(tmp).append(",");
						}
					}
				}
				if(sb.length() > 0){
					labelInfo = "," + sb.toString();
				}else{
					resultMap.put("error", true);
					resultMap.put("message", this.getText("voms.recomd.param.none"));
					return SUCCESS;
				}
			}
			//获取对象,并填充对象
			List<VomsRecommend> voRecommends = vomsRecommendService.queryByParam(prdType, type,objType, objId);
			if(isRecommend){
				VomsRecommend vomsRecommend = null;
				if(voRecommends == null || voRecommends.isEmpty()){
					vomsRecommend = new VomsRecommend();
					vomsRecommend.setObjId(objId);
					vomsRecommend.setObjType(objType);
					vomsRecommend.setPrdType(prdType);
					vomsRecommend.setType(type);
					vomsRecommend.setCreateTime(new Date());
					vomsRecommend.setCreator(this.getUsername());
				}else {
					vomsRecommend = voRecommends.get(0);
				}
				
				vomsRecommend.setName(name);
				vomsRecommend.setLabelInfo(labelInfo);
				vomsRecommend.setIsRecommend(isRecommend);
				vomsRecommend.setUpdator(this.getUsername());
				vomsRecommend.setUpdateTime(new Date());
				vomsRecommendService.save(vomsRecommend);
				
			}else if(voRecommends != null && !voRecommends.isEmpty()){//撤回有记录
				for(VomsRecommend vomsRecommend : voRecommends) {
					vomsRecommend.setIsRecommend(isRecommend);
					vomsRecommend.setUpdator(this.getUsername());
					vomsRecommend.setUpdateTime(new Date());
				}
				//保存入库
				vomsRecommendService.saveOrUpdateAll(voRecommends);	
			}
			resultMap.put("error", true);
			resultMap.put("message", this.getText("voms.recomd.success"));
		} catch (Exception e) {
			resultMap.put("error",false);
			resultMap.put("message", this.getText("voms.recomd.error"));
		}
		return SUCCESS;
	}
	public String list() {
		String ip = RequestUtil.getIpAddr(this.getRequest());
		if(log.isInfoEnabled())
			log.info("list:" + ip);		
		Map<String, Object> paramsMap = new HashMap<String, Object>();		
		getSearchParam(paramsMap);
		List<VomsRecommend> configList = new ArrayList<VomsRecommend>();
		Long count = 0L;
		PageList pageList = new PageList();
		try {
			if(isNotLogin()){
				resultMap.put("rows",  configList);
				resultMap.put("total", pageList.getPageCount());
				resultMap.put("records", count);
				return SUCCESS;
			}
			count = vomsRecommendService.getCountByParam(paramsMap);
			if (count > 0) {
				int limit = StringUtil.nullToInteger(getParam("rows"));
				int pageNo = StringUtil.nullToInteger(getParam("page"));
				
				pageList.setPageIndex(pageNo);
				pageList.setRecordCount(count.intValue());
				pageList.setPageSize(limit);
				pageList.initialize();
				
				configList = vomsRecommendService.getByParam(paramsMap, pageList.getStart(), limit);
				for(VomsRecommend info : configList){
					info.setLabelInfo(ST.cutStringComma(info.getLabelInfo()));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		resultMap.put("rows",  configList);
		resultMap.put("total", pageList.getPageCount());
		resultMap.put("records", count);
		return SUCCESS;
	}
	private void getSearchParam(Map<String, Object> paramsMap) {
		String sidx = getParam("sidx");
		String sord = getParam("sord");
		paramsMap.put("sidx", sidx);
		paramsMap.put("sord", sord);
		
		String labelInfo = getParam("labelInfo");
		if(!StringUtil.isNullStr(labelInfo)){
			paramsMap.put("labelInfo", labelInfo);
		}
		String prdType = getParam("prdType");
		if(!StringUtil.isNullStr(prdType)){
			paramsMap.put("prdType", prdType);
		}
		String objType = getParam("objType");
		if(!StringUtil.isNullStr(objType)){
			paramsMap.put("objType", objType);
		}
		String objId = getParam("objId");
		if(!StringUtil.isNullStr(objId)){
			paramsMap.put("objId", StringUtil.nullToLong(objId));
		}
		String name = getParam("name");
		if(!StringUtil.isNullStr(name)){
			paramsMap.put("name", name);
		}
		String isRecommend = getParam("isRecommend");
		if(!StringUtil.isNullStr(isRecommend)){
			paramsMap.put("isRecommend", isRecommend);
		}
	}
	public VomsRecommendService getVomsRecommendService() {
		return vomsRecommendService;
	}
	public void setVomsRecommendService(VomsRecommendService vomsRecommendService) {
		this.vomsRecommendService = vomsRecommendService;
	}
}

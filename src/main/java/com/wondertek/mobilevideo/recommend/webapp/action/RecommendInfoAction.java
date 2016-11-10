package com.wondertek.mobilevideo.recommend.webapp.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wondertek.mobilevideo.core.recommend.bean.PageList;
import com.wondertek.mobilevideo.core.recommend.cache.EnumsInfoCache;
import com.wondertek.mobilevideo.core.recommend.cache.PrdTypeRelationCache;
import com.wondertek.mobilevideo.core.recommend.model.PrdTypeRelation;
import com.wondertek.mobilevideo.core.recommend.model.RecommendInfo;
import com.wondertek.mobilevideo.core.recommend.service.RecommendInfoService;
import com.wondertek.mobilevideo.core.recommend.util.RecommendConstants;
import com.wondertek.mobilevideo.core.recommend.util.RequestUtil;
import com.wondertek.mobilevideo.core.recommend.util.ST;
import com.wondertek.mobilevideo.core.recommend.vo.KeyVal;
import com.wondertek.mobilevideo.core.util.StringUtil;

/**
 * 推荐信息
 * @author lvliuzhong
 */
public class RecommendInfoAction extends BaseAction {
	private static final long serialVersionUID = -2493942786912764258L;
	
	private RecommendInfoService recommendInfoService;
    private List<PrdTypeRelation> prdTypeRelations;
    private List<KeyVal> catInfos;
    
	public String getPage(){
		String ip = RequestUtil.getIpAddr(this.getRequest());
		if(log.isInfoEnabled())
			log.info("getPage:" + ip);
		
		prdTypeRelations = new ArrayList<PrdTypeRelation>();
		for(String key : PrdTypeRelationCache.PRDTYPE_RELATIONS.keySet()){
			prdTypeRelations.add(PrdTypeRelationCache.PRDTYPE_RELATIONS.get(key));
		}
		catInfos = new ArrayList<KeyVal>();
		Map<String,String> maps = EnumsInfoCache.VAL_ENUMSINFO.get(EnumsInfoCache.TYPE_CAT);
		if(maps != null){
			for(String key : maps.keySet()){
				catInfos.add(new KeyVal(maps.get(key),key));
			}
		}
    	return SUCCESS;
    }
	public String list() {
		String ip = RequestUtil.getIpAddr(this.getRequest());
		if(log.isInfoEnabled())
			log.info("list:" + ip);
		
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		
		getSearchParam(paramsMap);
		List<RecommendInfo> configList = new ArrayList<RecommendInfo>();
		Long count = 0L;
		PageList pageList = new PageList();
		try {
			if(isNotLogin()){
				resultMap.put("rows",  configList);
				resultMap.put("total", pageList.getPageCount());
				resultMap.put("records", count);
				return SUCCESS;
			}
			count = recommendInfoService.getCountByParam(paramsMap);
			if (count > 0) {
				int limit = StringUtil.nullToInteger(getParam("rows"));
				int pageNo = StringUtil.nullToInteger(getParam("page"));
				
				pageList.setPageIndex(pageNo);
				pageList.setRecordCount(count.intValue());
				pageList.setPageSize(limit);
				pageList.initialize();
				
				configList = recommendInfoService.getByParam(paramsMap, pageList.getStart(), limit);
				for(RecommendInfo info : configList){
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
		String catId = getParam("catId");
		if(!StringUtil.isNullStr(catId)){
			paramsMap.put("catId", catId);
		}
		String prdContId = getParam("prdContId");
		if(!StringUtil.isNullStr(prdContId)){
			paramsMap.put("prdContId", StringUtil.nullToLong(prdContId));
		}
		String contName = getParam("contName");
		if(!StringUtil.isNullStr(contName)){
			paramsMap.put("contName", contName);
		}
		String status = getParam("status");
		if(!StringUtil.isNullStr(status)){
			paramsMap.put("status", StringUtil.nullToInteger(status));
		}
	}
	/**
	 * 删除
	 * @return
	 */
	public String delete() {
		String ip = RequestUtil.getIpAddr(this.getRequest());
		if(log.isInfoEnabled())
			log.info("delete:" + ip);
		
		if(isNotLogin()){
			resultMap.put("success", false);
			return SUCCESS;
		}
		String configIds = getParam("ids");
		if(log.isInfoEnabled())
			log.info("delete,userName:" + this.getUsername() +",configIds:" + configIds);
		
		String[] ids = {};
		if (configIds != null && configIds.length() > 0 ) {
			ids = configIds.substring(0, configIds.lastIndexOf(",")).split(",");
		}
		try {
			for (String id : ids) {
				if(StringUtil.nullToLong(id) > 0){
					recommendInfoService.deleteById(Long.parseLong(id));
				}
			}
			resultMap.put("success", true);
		} catch (NumberFormatException e) {
			resultMap.put("success", false);
			e.printStackTrace();
		}
		return SUCCESS;
	}
	/**
	 * 新增
	 * @return
	 */
	public String addSave() {
		String ip = RequestUtil.getIpAddr(this.getRequest());
		if(log.isInfoEnabled())
			log.info("addSave:" + ip);
		
		resultMap.put("success", true);
		resultMap.put("error", true);
		
		if(isNotLogin()){
			resultMap.put("msg", this.getText("common.isNotLogin"));
			return SUCCESS;
		}
		String labelInfo = getParam("labelInfo");
		String prdType = getParam("prdType");
		String catId = getParam("catId");
		Long prdContId = StringUtil.nullToCloneLong(this.getParam("prdContId"));
		String contName = getParam("contName");
		if(prdContId == null || prdContId <= 0){
			resultMap.put("msg", this.getText("recomd.prdContId.error"));
			return SUCCESS;
		}
		
		//校验标签
		int count = 0;
		String tmpLableInfo = null;
		for(String label : labelInfo.split(",")){
			if(!StringUtil.isNullStr(label)){
				if(count == 0){
					tmpLableInfo = label;
				}else{
					tmpLableInfo = tmpLableInfo + "," + label;
				}
				count ++;
			}
		}
		if(StringUtil.isNullStr(tmpLableInfo) || StringUtil.isNullStr(prdType) 
				|| StringUtil.isNullStr(catId) || StringUtil.isNullStr(contName)){
			resultMap.put("msg", this.getText("recomd.param.null"));
			return SUCCESS;
		}
		
		RecommendInfo recommendInfo = new RecommendInfo();
		recommendInfo.setLabelInfo(","+labelInfo+",");
		recommendInfo.setPrdType(prdType);
		recommendInfo.setCatId(catId);
		recommendInfo.setPrdContId(prdContId);
		recommendInfo.setContName(contName);
		recommendInfo.setStatus(RecommendConstants.VALID);
		recommendInfo.setCreateTime(new Date());
		recommendInfo.setCreator(this.getUsername());
		recommendInfo.setUpdateTime(recommendInfo.getCreateTime());
		recommendInfo.setUpdator(recommendInfo.getCreator());
		
		Boolean isExist = recommendInfoService.checkExist(recommendInfo.getPrdType(),recommendInfo.getCatId(),
				recommendInfo.getPrdContId(),recommendInfo.getId());
		if(isExist){
			resultMap.put("msg", this.getText("recomd.prdContId.exist"));
			return SUCCESS;
		}
		try {
			if(log.isInfoEnabled())
				log.info("addSave,userName:" + this.getUsername() +",recommendInfo:" + recommendInfo);
			
			recommendInfoService.save(recommendInfo);
			resultMap.put("msg", this.getText("common.add.success"));
			resultMap.put("error", false);
		} catch (Exception e) {
			resultMap.put("msg", this.getText("common.add.fail"));
			log.error("add error" + e.getMessage(),e);
		}
		return SUCCESS;
	}
	/**
	 * 编辑
	 * @return
	 */
	public String editSave() {
		String ip = RequestUtil.getIpAddr(this.getRequest());
		if(log.isInfoEnabled())
			log.info("editSave:" + ip);
		
		resultMap.put("success", true);
		resultMap.put("error", true);
		
		if(isNotLogin()){
			resultMap.put("msg", this.getText("common.isNotLogin"));
			return SUCCESS;
		}
		
		String id = getParam("id");
		String labelInfo = getParam("labelInfo");
		String prdType = getParam("prdType");
		String catId = getParam("catId");
		Long prdContId = StringUtil.nullToCloneLong(this.getParam("prdContId"));
		String contName = getParam("contName");
		Integer status = StringUtil.nullToInteger(getParam("status"));
		if(prdContId == null || prdContId <= 0){
			resultMap.put("msg", this.getText("recomd.prdContId.error"));
			return SUCCESS;
		}
		
		//校验标签
		int count = 0;
		String tmpLableInfo = null;
		for(String label : labelInfo.split(",")){
			if(!StringUtil.isNullStr(label)){
				if(count == 0){
					tmpLableInfo = label;
				}else{
					tmpLableInfo = tmpLableInfo + "," + label;
				}
				count ++;
			}
		}
		if(StringUtil.isNullStr(tmpLableInfo) || StringUtil.isNullStr(prdType) 
				|| StringUtil.isNullStr(catId) || StringUtil.isNullStr(contName)){
			resultMap.put("msg", this.getText("recomd.param.null"));
			return SUCCESS;
		}
		try {
			Boolean isExist = recommendInfoService.checkExist(prdType,catId,
					prdContId,StringUtil.nullToLong(id));
			if(isExist){
				resultMap.put("msg", this.getText("recomd.prdContId.exist"));
				return SUCCESS;
			}
			
			RecommendInfo recommendInfo = recommendInfoService.get(StringUtil.nullToLong(id));
			
			recommendInfo.setLabelInfo(","+labelInfo+",");
			recommendInfo.setPrdType(prdType);
			recommendInfo.setCatId(catId);
			recommendInfo.setPrdContId(prdContId);
			recommendInfo.setContName(contName);
			recommendInfo.setStatus(status);
			recommendInfo.setUpdateTime(new Date());
			recommendInfo.setUpdator(this.getUsername());
			
			if(log.isInfoEnabled())
				log.info("editSave,userName:" + this.getUsername() +",recommendInfo:" + recommendInfo);
			
			recommendInfoService.save(recommendInfo);
			resultMap.put("error", false);
			resultMap.put("msg", this.getText("common.edit.success"));
		} catch (Exception e) {
			resultMap.put("msg", this.getText("common.edit.fail"));
			e.printStackTrace();
		}
		return SUCCESS;
	}
	public RecommendInfoService getRecommendInfoService() {
		return recommendInfoService;
	}
	public void setRecommendInfoService(RecommendInfoService recommendInfoService) {
		this.recommendInfoService = recommendInfoService;
	}
	public List<PrdTypeRelation> getPrdTypeRelations() {
		return prdTypeRelations;
	}
	public void setPrdTypeRelations(List<PrdTypeRelation> prdTypeRelations) {
		this.prdTypeRelations = prdTypeRelations;
	}
	public List<KeyVal> getCatInfos() {
		return catInfos;
	}
	public void setCatInfos(List<KeyVal> catInfos) {
		this.catInfos = catInfos;
	}
}

package com.wondertek.mobilevideo.recommend.webapp.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wondertek.mobilevideo.core.recommend.bean.PageList;
import com.wondertek.mobilevideo.core.recommend.cache.PrdTypeRelationCache;
import com.wondertek.mobilevideo.core.recommend.model.PrdTypeRelation;
import com.wondertek.mobilevideo.core.recommend.model.TopRecommend;
import com.wondertek.mobilevideo.core.recommend.service.TopRecommendService;
import com.wondertek.mobilevideo.core.recommend.util.RecommendConstants;
import com.wondertek.mobilevideo.core.recommend.util.RequestUtil;
import com.wondertek.mobilevideo.core.util.StringUtil;
/**
 * 置顶推荐相关
 * @author lvliuzhong
 *
 */
public class TopRecommendAction extends BaseAction {
	private static final long serialVersionUID = -2493942786912764258L;

	private TopRecommendService topRecommendService;
	private List<PrdTypeRelation> prdTypeRelations;
	/**
	 * 获取页面
	 * @return
	 */	
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
	
	public String list() {
		String ip = RequestUtil.getIpAddr(this.getRequest());
		if(log.isInfoEnabled())
			log.info("list:" + ip);
		
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		
		getSearchParam(paramsMap);
		List<TopRecommend> configList = new ArrayList<TopRecommend>();
		Long count = 0L;
		PageList pageList = new PageList();
		try {
			if(isNotLogin()){
				resultMap.put("rows",  configList);
				resultMap.put("total", pageList.getPageCount());
				resultMap.put("records", count);
				return SUCCESS;
			}
			count = topRecommendService.getCountByParam(paramsMap);
			if (count > 0) {
				int limit = StringUtil.nullToInteger(getParam("rows"));
				int pageNo = StringUtil.nullToInteger(getParam("page"));
				
				pageList.setPageIndex(pageNo);
				pageList.setRecordCount(count.intValue());
				pageList.setPageSize(limit);
				pageList.initialize();
				
				configList = topRecommendService.getByParam(paramsMap, pageList.getStart(), limit);				
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
		
		String prdType = getParam("prdType");
		if(!StringUtil.isNullStr(prdType)){
			paramsMap.put("prdType", prdType);
		}
		String topId = getParam("topId");
		if(!StringUtil.isNullStr(topId)){
			paramsMap.put("topId", StringUtil.nullToLong(topId));
		}		
		String topName = getParam("topName");
		if(!StringUtil.isNullStr(topName)){
			paramsMap.put("topName", topName);
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
					topRecommendService.deleteById(Long.parseLong(id));
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
		
		String prdType = getParam("prdType");
		Long topId = StringUtil.nullToCloneLong(this.getParam("topId"));
		String topName = getParam("topName");
		if(topId == null || topId <= 0){
			resultMap.put("msg", this.getText("top.recomd.tId.error"));
			return SUCCESS;
		}		
		
		if(StringUtil.isNullStr(prdType) || StringUtil.isNullStr(topId) || StringUtil.isNullStr(topName)){
			resultMap.put("msg", this.getText("top.recomd.param.null"));
			return SUCCESS;
		}
		
		TopRecommend topRecommend = new TopRecommend();		
		topRecommend.setTopId(topId);
		topRecommend.setPrdType(prdType);		
		topRecommend.setTopName(topName);
		topRecommend.setStatus(RecommendConstants.VALID);
		topRecommend.setCreateTime(new Date());
		topRecommend.setCreator(this.getUsername());
		topRecommend.setUpdateTime(topRecommend.getCreateTime());
		topRecommend.setUpdator(topRecommend.getCreator());
		
		Boolean isExist = topRecommendService.checkExist(topRecommend.getTopId(),topRecommend.getPrdType(),
				topRecommend.getTopName(),topRecommend.getTopId());
		
		if(isExist){
			resultMap.put("msg", this.getText("top.recomd.tId.exist"));
			return SUCCESS;
		}
		try {
			if(log.isInfoEnabled())
				log.info("addSave,userName:" + this.getUsername() +",topRecommend:" + topRecommend);
			
			topRecommendService.save(topRecommend);			
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
		Long topId = StringUtil.nullToCloneLong(this.getParam("topId"));		
		String prdType = getParam("prdType");		
		String topName = getParam("topName");
		Integer status = StringUtil.nullToInteger(getParam("status"));
		if(topId == null || topId <= 0){
			resultMap.put("msg", this.getText("top.recomd.tId.error"));
			return SUCCESS;
		}
	
		if(StringUtil.isNullStr(prdType) || StringUtil.isNullStr(topId) || StringUtil.isNullStr(topName)){
			resultMap.put("msg", this.getText("top.recomd.param.null"));
			return SUCCESS;
		}
		try {
			Boolean isExist = topRecommendService.checkExist(topId, topName, topName,StringUtil.nullToLong(id));
			if(isExist){
				resultMap.put("msg", this.getText("top.recomd.tId.exist"));
				return SUCCESS;
			}
			
			TopRecommend topRecommend = topRecommendService.get(StringUtil.nullToLong(id));			
			topRecommend.setTopId(topId);
			topRecommend.setPrdType(prdType);					
			topRecommend.setTopName(topName);
			topRecommend.setStatus(status);
			topRecommend.setUpdateTime(new Date());
			topRecommend.setUpdator(this.getUsername());
			
			if(log.isInfoEnabled())
				log.info("editSave,userName:" + this.getUsername() +",topRecommend:" + topRecommend);
			
			topRecommendService.save(topRecommend);
			resultMap.put("error", false);
			resultMap.put("msg", this.getText("common.edit.success"));
		} catch (Exception e) {
			resultMap.put("msg", this.getText("common.edit.fail"));
			e.printStackTrace();
		}
		return SUCCESS;
	}
	public TopRecommendService getTopRecommendService() {
		return topRecommendService;
	}
	public void setTopRecommendService(TopRecommendService topRecommendService) {
		this.topRecommendService = topRecommendService;
	}
	public List<PrdTypeRelation> getPrdTypeRelations() {
		return prdTypeRelations;
	}
	public void setPrdTypeRelations(List<PrdTypeRelation> prdTypeRelations) {
		this.prdTypeRelations = prdTypeRelations;
	}
}

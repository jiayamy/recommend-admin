package com.wondertek.mobilevideo.recommend.webapp.action;

import java.util.Date;

import com.wondertek.mobilevideo.core.recommend.model.VomsRecommend;
import com.wondertek.mobilevideo.core.recommend.service.VomsRecommendService;
import com.wondertek.mobilevideo.core.recommend.util.RequestUtil;
import com.wondertek.mobilevideo.core.util.StringUtil;

/**
 *
 */
public class VomsRecommendAction extends BaseAction {
	private static final long serialVersionUID = -2493942786912764258L;
	
	private VomsRecommendService vomsRecommendService;
	
	public String addVomsRecommend(){
		try {
			resultMap.put("success", true);
			String ip = RequestUtil.getIpAddr(this.getRequest());
			if(log.isInfoEnabled())
				log.info("addVomsRecommend:" + ip);
			//获取参数
			String objIdStr = this.getParam("objId");
			String prdType = this.getParam("prdType");
			String typeStr = this.getParam("type");
			String name = this.getParam("name");
			String isRecommendStr = this.getParam("isRecommend");
			String labelInfo = this.getParam("labelInfo");
			//校验参数
			Long objId = StringUtil.nullToCloneLong(objIdStr);
			int type = StringUtil.nullToInteger(typeStr);
			if(StringUtil.isNullStr(prdType) || StringUtil.isNullStr(typeStr) || StringUtil.isNullStr(isRecommendStr)){
				resultMap.put("error", true);
				resultMap.put("message", this.getText("voms.recomd.param.none"));
				return SUCCESS;
			}
			if(objId == null || !(type == 0 || type == 1)){//类型校验
				resultMap.put("error", true);
				resultMap.put("message", this.getText("voms.recomd.param.error"));
				return SUCCESS;
			}
			Boolean isRecommend = StringUtil.nullToBoolean(isRecommendStr);
			if(isRecommend){//推荐校验
				if(StringUtil.isNullStr(name) || StringUtil.isNullStr(labelInfo)){
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
			VomsRecommend vomsRecommend = vomsRecommendService.queryByParam(prdType, type, objId);
			if(isRecommend){
				if(vomsRecommend == null){
					vomsRecommend = new VomsRecommend();
					vomsRecommend.setPrdType(prdType);
					vomsRecommend.setType(type);
					vomsRecommend.setObjId(objId);
					vomsRecommend.setCreateTime(new Date());
					vomsRecommend.setCreator(this.getUsername());
				}
				vomsRecommend.setName(name);
				vomsRecommend.setLabelInfo(labelInfo);
				vomsRecommend.setIsRecommend(isRecommend);
			}else{
				if(vomsRecommend != null){
					vomsRecommend.setIsRecommend(isRecommend);
				}else{
					resultMap.put("error", false);
					resultMap.put("message", this.getText("voms.recomd.success"));
					return SUCCESS;
				}
			}
			vomsRecommend.setUpdator(this.getUsername());
			vomsRecommend.setUpdateTime(new Date());
			
			//保存入库
			vomsRecommendService.save(vomsRecommend);
			
			resultMap.put("error", false);
			resultMap.put("message", this.getText("voms.recomd.success"));
		} catch (Exception e) {
			resultMap.put("error", true);
			resultMap.put("message", this.getText("voms.recomd.error"));
		}
		return SUCCESS;
	}

	public VomsRecommendService getVomsRecommendService() {
		return vomsRecommendService;
	}
	public void setVomsRecommendService(VomsRecommendService vomsRecommendService) {
		this.vomsRecommendService = vomsRecommendService;
	}
}

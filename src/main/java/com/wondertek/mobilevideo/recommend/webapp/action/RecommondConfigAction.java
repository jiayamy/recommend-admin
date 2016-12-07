package com.wondertek.mobilevideo.recommend.webapp.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wondertek.mobilevideo.core.recommend.model.EnumsConfig;
import com.wondertek.mobilevideo.core.recommend.model.EnumsInfo;
import com.wondertek.mobilevideo.core.recommend.service.EnumsConfigService;
import com.wondertek.mobilevideo.core.recommend.service.EnumsInfoService;
import com.wondertek.mobilevideo.core.recommend.util.RequestUtil;
import com.wondertek.mobilevideo.core.recommend.vo.AdditionalParameters;
import com.wondertek.mobilevideo.core.recommend.vo.RecommendParam;
import com.wondertek.mobilevideo.core.recommend.vo.RecommondListVo;

public class RecommondConfigAction extends BaseAction {

	private static final long serialVersionUID = 8926974373038941213L;

	private EnumsConfigService enumsConfigService;
	
	private EnumsInfoService enumsInfoService;

	public String getPage() {
		return SUCCESS;
	}

	public String list() {
		String sid = getParam("skey");
		
		List<EnumsConfig> items = new ArrayList<EnumsConfig>();
		RecommondListVo result = new RecommondListVo();
		List<RecommendParam> recommendParams = new ArrayList<RecommendParam>();
		
		List<EnumsInfo> enumsInfos = enumsInfoService.getAll();
		Map<String, String> enumsMap = new HashMap<String, String>();
		for(EnumsInfo enums : enumsInfos){
			String k1 = enums.getKey() + "-" + enums.getType();
			String v1 = enums.getVal();
			enumsMap.put(k1, v1);
		}
		
		if("0".equals(sid)){
			items = enumsConfigService.findByType("0");
			for (EnumsConfig eConfig : items) {

				RecommendParam r = new RecommendParam();
				r.setText(enumsMap.get(eConfig.getKey() + "-" + eConfig.getType()));
				r.setLaberType(eConfig.getType());
				r.setId(eConfig.getId());
				r.setWeight(eConfig.getWeight());
				r.setType("folder");
				AdditionalParameters ad = new AdditionalParameters();
				r.setAdditionalParameters(ad);
				recommendParams.add(r);
			}
		}else{
			EnumsConfig enumsConfig = enumsConfigService.get(Long.parseLong(sid));
			String skey = enumsConfig.getKey();
			List<EnumsConfig> childs = enumsConfigService.findByParent(skey);
			for(EnumsConfig eConfig : childs){
				RecommendParam r = new RecommendParam();
				r.setText(enumsMap.get(eConfig.getKey() + "-" + eConfig.getType()));
				r.setLaberType(eConfig.getType());
				r.setId(eConfig.getId());
				r.setWeight(eConfig.getWeight());
				r.setType("item");
				r.setParentId(enumsConfig.getId().toString());
				r.setParentText(enumsMap.get(enumsConfig.getKey() + "-" + enumsConfig.getType()));
				AdditionalParameters ad = new AdditionalParameters();
				r.setAdditionalParameters(ad);
				recommendParams.add(r);
			}
		}
		
		result.setData(recommendParams);

		resultMap.put("data", result);

		return SUCCESS;
	}
	
	public String addLabelNameList(){
		
		List<EnumsInfo> enumsInfos = enumsInfoService.queryByType(1);
		resultMap.put("data", enumsInfos);
		return SUCCESS;
	}
	/**
	 * 添加、删除、修改标签
	 */
	public String editLabel(){
		String ip = RequestUtil.getIpAddr(this.getRequest());
		if(log.isInfoEnabled()){
			log.info("edit:"+ip);
		}
		if(isNotLogin()){
			resultMap.put("msg", this.getText("common.isNotLogin"));
			return SUCCESS;
		}
		String id = getParam("configKey");
		String weights = getParam("configValue");
		String addLabelKey = getParam("addLabelName");
		String parentLabelId = getParam("labelId");
		String addLabelType = getParam("addLabelType");
		String addLabelWeight = getParam("addLabelWeight");
		String ids = getParam("configIds");
		String pid = getParam("parentId");
		try {
			EnumsConfig enumsConfig = null; 
			List<EnumsInfo> enumsInfos = enumsInfoService.getAll();
			RecommondListVo result = new RecommondListVo();
			Map<String, String> enumsMap = new HashMap<String, String>();
			for(EnumsInfo enums : enumsInfos){
				String k1 = enums.getKey() + "-" + enums.getType();
				String v1 = enums.getVal();
				enumsMap.put(k1, v1);
			}
			List<EnumsConfig> items = new ArrayList<EnumsConfig>();
			List<RecommendParam> recommendParams = new ArrayList<RecommendParam>();
			items = enumsConfigService.findByType("0");
			//有id：编辑
			if(id != null && !"".equals(id)){
				enumsConfig = enumsConfigService.get(Long.parseLong(id));
				enumsConfig.setWeight(weights);
				enumsConfigService.update(enumsConfig);
				resultMap.put("msg", this.getText("system.editTag.success"));
				
				
				for (EnumsConfig eConfig : items) {
					RecommendParam r = new RecommendParam();
					r.setText(enumsMap.get(eConfig.getKey() + "-" + eConfig.getType()));
					r.setLaberType(eConfig.getType());
					r.setId(eConfig.getId());
					r.setWeight(eConfig.getWeight());
					r.setType("folder");
					AdditionalParameters ad = new AdditionalParameters();
					if(pid.equals(eConfig.getId().toString())){
						ad.setItemSelected(true);
					}
					r.setAdditionalParameters(ad);
					recommendParams.add(r);
				}
				result.setData(recommendParams);
				resultMap.put("data", result);
			}
			//无id：添加
			if(parentLabelId != null && !"".equals(parentLabelId)){
				if(parentLabelId == null || "".equals(parentLabelId)){
					resultMap.put("msg", this.getText("system.addTag.fail"));
					return SUCCESS;
				}
				enumsConfig = enumsConfigService.get(Long.parseLong(parentLabelId));
				EnumsConfig addEnumsConfig = new EnumsConfig();
				addEnumsConfig.setKey(addLabelKey);
				addEnumsConfig.setParent(enumsConfig.getKey());
				addEnumsConfig.setType(addLabelType);
				addEnumsConfig.setWeight(addLabelWeight);
				Boolean isExist = enumsConfigService.checkExistLabel(addLabelKey,enumsConfig.getKey(),addLabelType);
				if(!isExist){
					enumsConfigService.save(addEnumsConfig);
					resultMap.put("msg", this.getText("system.addSecondTag.success"));
				}else{
					resultMap.put("msg", this.getText("system.SecondTag.exist"));
				}
				
				
				for (EnumsConfig eConfig : items) {
					RecommendParam r = new RecommendParam();
					r.setText(enumsMap.get(eConfig.getKey() + "-" + eConfig.getType()));
					r.setLaberType(eConfig.getType());
					r.setId(eConfig.getId());
					r.setWeight(eConfig.getWeight());
					r.setType("folder");
					AdditionalParameters ad = new AdditionalParameters();
					if(parentLabelId.equals(eConfig.getId().toString())){
						ad.setItemSelected(true);
					}
					r.setAdditionalParameters(ad);
					recommendParams.add(r);
				}
				result.setData(recommendParams);
				resultMap.put("data", result);
			}
			//删除二级标签
			if(ids != null && !"".equals(ids)){
				String[] paramIds = ids.split(",");
				for(int i=0;i<paramIds.length;i++){
					enumsConfig = enumsConfigService.get(Long.parseLong(paramIds[i]));
					if("0".equals(enumsConfig.getType())){
						continue;
					}
					enumsConfigService.deleteById(Long.parseLong(paramIds[i]));
					resultMap.put("success", true);
				}
			}
		} catch (Exception e) {
			resultMap.put("msg", this.getText("system.editTag.fail"));
			e.printStackTrace();
		}

		return SUCCESS;
	}

	public EnumsConfigService getEnumsConfigService() {
		return enumsConfigService;
	}

	public void setEnumsConfigService(EnumsConfigService enumsConfigService) {
		this.enumsConfigService = enumsConfigService;
	}

	public EnumsInfoService getEnumsInfoService() {
		return enumsInfoService;
	}

	public void setEnumsInfoService(EnumsInfoService enumsInfoService) {
		this.enumsInfoService = enumsInfoService;
	}

}

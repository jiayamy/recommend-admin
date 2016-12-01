package com.wondertek.mobilevideo.recommend.webapp.action;

import java.util.ArrayList;
import java.util.List;

import com.wondertek.mobilevideo.core.recommend.model.AdditionalParameters;
import com.wondertek.mobilevideo.core.recommend.model.EnumsConfig;
import com.wondertek.mobilevideo.core.recommend.model.EnumsInfo;
import com.wondertek.mobilevideo.core.recommend.model.RecommendParam;
import com.wondertek.mobilevideo.core.recommend.service.EnumsConfigService;
import com.wondertek.mobilevideo.core.recommend.service.EnumsInfoService;
import com.wondertek.mobilevideo.core.recommend.vo.RecommondListVo;

public class RecommondConfigAction extends BaseAction {

	private static final long serialVersionUID = 8926974373038941213L;

	private EnumsConfigService enumsConfigService;
	
	private EnumsInfoService enumsInfoService;

	public String getPage() {
		return SUCCESS;
	}

	public String list() {
		String skey = getParam("skey");

		List<EnumsConfig> items = new ArrayList<EnumsConfig>();
		RecommondListVo result = new RecommondListVo();
		List<RecommendParam> recommendParams = new ArrayList<RecommendParam>();

		items = enumsConfigService.findByType("0");
		for (EnumsConfig eConfig : items) {

			RecommendParam r = new RecommendParam();
			String key = eConfig.getKey();
			List<EnumsConfig> childs = enumsConfigService.findByParent(key);
			if (childs != null && childs.size() > 0) {
				r.setText(eConfig.getValue());
				r.setLaberType(eConfig.getType().equals("0") ? "一级标签" : "二级标签");
				r.setType("folder");
				r.setId(eConfig.getId());
				r.setWeight(eConfig.getWeight());
				AdditionalParameters additionParameters = new AdditionalParameters();
				additionParameters.setId(eConfig.getKey());

				for (EnumsConfig es : childs) {
					RecommendParam rs = new RecommendParam();
					rs.setId(es.getId());
					rs.setLaberType(es.getType().equals("0") ? "一级标签" : "二级标签");
					rs.setText(es.getValue());
					rs.setType("item");
					rs.setWeight(es.getWeight());
					rs.setParentText(eConfig.getValue());
					rs.setParentId(eConfig.getId().toString());
					additionParameters.getChildren().add(rs);

				}
				if (skey.equals(eConfig.getKey())) {
					additionParameters.isItemSelected();
					result.setData(additionParameters.getChildren());
					resultMap.put("data", result);
					return SUCCESS;
				}
				r.setAdditionalParameters(additionParameters);

			} else {
				r.setText(eConfig.getValue());
				r.setLaberType(eConfig.getType().equals("0") ? "一级标签" : "二级标签");
				r.setType("item");
				r.setId(eConfig.getId());
				r.setWeight(eConfig.getWeight());
			}
			recommendParams.add(r);

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

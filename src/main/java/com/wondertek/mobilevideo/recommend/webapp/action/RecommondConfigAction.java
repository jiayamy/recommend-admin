package com.wondertek.mobilevideo.recommend.webapp.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wondertek.mobilevideo.core.base.Constants;
import com.wondertek.mobilevideo.core.recommend.cache.EnumsInfoCache;
import com.wondertek.mobilevideo.core.recommend.model.EnumsConfig;
import com.wondertek.mobilevideo.core.recommend.model.EnumsInfo;
import com.wondertek.mobilevideo.core.recommend.service.EnumsConfigService;
import com.wondertek.mobilevideo.core.recommend.service.EnumsInfoService;
import com.wondertek.mobilevideo.core.recommend.util.RequestUtil;
import com.wondertek.mobilevideo.core.recommend.vo.RecommendParam;
import com.wondertek.mobilevideo.core.util.StringUtil;

public class RecommondConfigAction extends BaseAction {

	private static final long serialVersionUID = 8926974373038941213L;

	private EnumsConfigService enumsConfigService;
	
	private EnumsInfoService enumsInfoService;

	private List<RecommendParam> recommendParams = new ArrayList<RecommendParam>();
	private List<EnumsInfo> enumsInfos;
		
	public String getPage() {
		try {
			enumsInfos = enumsInfoService.queryByType(1);
		} catch (Exception e) {
		}
		if(enumsInfos == null){
			enumsInfos = new ArrayList<EnumsInfo>();
		}
		return SUCCESS;
	}

	@SuppressWarnings({ "unchecked" })
	public String list() {
		String nodeId = getParam("nodeId");
		String isRefresh = StringUtil.nullToString(getRequest().getParameter("isRefresh"));
		List<EnumsConfig> items = new ArrayList<EnumsConfig>();
		Map<String, String> enumsMap = new HashMap<String, String>();
//		List<EnumsInfo> enumsInfos = enumsInfoService.getAll();
//		for(EnumsInfo enums : enumsInfos){
//			String k1 = enums.getKey() + "-" + enums.getType();
//			String v1 = enums.getVal();
//			enumsMap.put(k1, v1);
//		}
		Map<String,String> map = null;
		for(Integer type : EnumsInfoCache.VAL_ENUMSINFO.keySet()){
			map = EnumsInfoCache.VAL_ENUMSINFO.get(type);
			if(map != null){
				for(String val : map.keySet()){
					enumsMap.put(map.get(val) + "-" + type, val);
				}
			}
		}
		
		List<RecommendParam> rps = (List<RecommendParam>) getSession().getAttribute(Constants.CONTEENT_TREE_NODES);
		if("0".equals(nodeId) || rps == null || nodeId ==null || nodeId.trim().isEmpty()){
			items = enumsConfigService.findByType("0");
			for (EnumsConfig eConfig : items) {
				RecommendParam r = new RecommendParam();
				r.setText(enumsMap.get(eConfig.getKey() + "-" + eConfig.getType()));
				r.setLabelType(eConfig.getType());
				r.setId(eConfig.getId());
				r.setWeight(eConfig.getWeight());
				r.setIconCls("folder");
				r.setIsParent(true);
				recommendParams.add(r);
			}
		}else{
			if("Y".equals(isRefresh)){
				EnumsConfig enumsConfig = enumsConfigService.get(Long.parseLong(nodeId));
				String skey = enumsConfig.getKey();
				List<EnumsConfig> childs = enumsConfigService.findByParent(skey);
				for(EnumsConfig eConfig : childs){
					RecommendParam r = new RecommendParam();
					r.setText(enumsMap.get(eConfig.getKey() + "-" + eConfig.getType()));
					r.setLabelType(eConfig.getType());
					r.setId(eConfig.getId());
					r.setWeight(eConfig.getWeight());
					r.setIconCls("item");
					r.setIsParent(false);
					r.setParentId(enumsConfig.getId().toString());
					r.setParentText(enumsMap.get(enumsConfig.getKey() + "-" + enumsConfig.getType()));
					recommendParams.add(r);
				}
				return SUCCESS;
			}

		}
		getSession().setAttribute(Constants.CONTEENT_TREE_NODES, recommendParams);
		return SUCCESS;
	}
	
	public String addLabelNameList(){
		
		List<EnumsInfo> enumsInfos = enumsInfoService.queryByType(1);
		resultMap.put("data", enumsInfos);
		return SUCCESS;
	}
	/**
	 * 添加、修改标签
	 */
	public String edit(){
		String ip = RequestUtil.getIpAddr(this.getRequest());
		if(log.isInfoEnabled()){
			log.info("edit:"+ip);
		}
		if(isNotLogin()){
			resultMap.put("msg", this.getText("common.isNotLogin"));
			return SUCCESS;
		}
		String id = getParam("id");
		String weight = getParam("weight");
		String key = getParam("key");
		String parentId = getParam("parentId");
		String type = getParam("type");
		try {
			EnumsConfig enumsConfig = null; 
			Double weightD = Double.valueOf(weight);
			if(weightD < 0 || weightD > 100){
				resultMap.put("msg", "权重赋值范围为[0-100]");
				return SUCCESS;
			}
			//有id：编辑
			if(id != null && !"".equals(id)){
				enumsConfig = enumsConfigService.get(Long.parseLong(id));
				enumsConfig.setWeight(weight);
				enumsConfigService.update(enumsConfig);
				resultMap.put("msg", this.getText("common.edit.success"));
			}else if(parentId != null && !"".equals(parentId)){//修改
				enumsConfig = enumsConfigService.get(Long.parseLong(parentId));
				EnumsConfig addEnumsConfig = new EnumsConfig();
				addEnumsConfig.setKey(key);
				addEnumsConfig.setParent(enumsConfig.getKey());
				addEnumsConfig.setType(type);
				addEnumsConfig.setWeight(weight);
				
				Boolean isExist = enumsConfigService.checkExistLabel(key,enumsConfig.getKey(),type);
				if(!isExist){
					enumsConfigService.save(addEnumsConfig);
					resultMap.put("msg", this.getText("common.add.success"));
				}else{
					resultMap.put("msg", "您添加的二级标签已存在，不能重复添加");
				}
			}else{
				resultMap.put("msg", "操作失败");
			}
		} catch (Exception e) {
			resultMap.put("msg", "操作失败");
			e.printStackTrace();
		}
		return SUCCESS;
	}
	/**
	 * 删除标签
	 */
	public String delete(){
		resultMap.put("success", false);
		String ip = RequestUtil.getIpAddr(this.getRequest());
		if(log.isInfoEnabled()){
			log.info("edit:"+ip);
		}
		if(isNotLogin()){
			resultMap.put("msg", this.getText("common.isNotLogin"));
			return SUCCESS;
		}
		String ids = getParam("ids");
		try {
			//删除二级标签
			if(ids != null && !"".equals(ids)){
				String[] paramIds = ids.split(",");
				for(int i=0;i<paramIds.length;i++){
					EnumsConfig enumsConfig = enumsConfigService.get(Long.parseLong(paramIds[i]));
					if("0".equals(enumsConfig.getType())){
						continue;
					}
					enumsConfigService.deleteById(Long.parseLong(paramIds[i]));
				}
			}
			resultMap.put("success", true);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
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
	public List<RecommendParam> getRecommendParams() {
		return recommendParams;
	}
	public void setRecommendParams(List<RecommendParam> recommendParams) {
		this.recommendParams = recommendParams;
	}

	public List<EnumsInfo> getEnumsInfos() {
		return enumsInfos;
	}

	public void setEnumsInfos(List<EnumsInfo> enumsInfos) {
		this.enumsInfos = enumsInfos;
	}
}
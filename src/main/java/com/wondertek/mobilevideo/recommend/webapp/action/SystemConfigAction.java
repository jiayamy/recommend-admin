package com.wondertek.mobilevideo.recommend.webapp.action;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wondertek.mobilevideo.core.recommend.bean.PageList;
import com.wondertek.mobilevideo.core.recommend.model.SystemConfig;
import com.wondertek.mobilevideo.core.recommend.service.SystemConfigService;
import com.wondertek.mobilevideo.core.recommend.util.RequestUtil;
import com.wondertek.mobilevideo.core.util.StringUtil;
import com.wondertek.mobilevideo.recommend.webapp.util.ExportExcelUtil;
/**
 * 系统配置
 * @author lvliuzhong
 *
 */
public class SystemConfigAction extends BaseAction {
	private static final long serialVersionUID = -3264337698808641096L;
	
	private SystemConfigService systemConfigService;
    private InputStream inputStream;
    
    public String getPage(){
    	return SUCCESS;
    }
	public String list() {
		String ip = RequestUtil.getIpAddr(this.getRequest());
		if(log.isInfoEnabled())
			log.info("list:" + ip);
		
		String sysParmsKey = getParam("sysParmsKey");
		String sysParmsValue = getParam("sysParmsValue");
		
		int limit = StringUtil.nullToInteger(getParam("rows"));
		int pageNo = StringUtil.nullToInteger(getParam("page"));
		String sidx = getParam("sidx");
		String sord = getParam("sord");
		
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("sidx", sidx);
		paramsMap.put("sord", sord);
		paramsMap.put("configKey", sysParmsKey);
		paramsMap.put("configValue", sysParmsValue);
		
		List<SystemConfig> configList = new ArrayList<SystemConfig>();
		Long count = 0L;
		PageList pageList = new PageList();
		try {
			if(isNotLogin()){
				resultMap.put("rows",  configList);
				resultMap.put("total", pageList.getPageCount());
				resultMap.put("records", count);
				return SUCCESS;
			}
			
			count = systemConfigService.getConfigCount(paramsMap);
			
			pageList.setPageIndex(pageNo);
			pageList.setRecordCount(count.intValue());
			pageList.setPageSize(limit);
			pageList.initialize();
			
			if (count > 0) {
				configList = systemConfigService.getConfigList(paramsMap, pageList.getStart(), limit);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		resultMap.put("rows",  configList);
		resultMap.put("total", pageList.getPageCount());
		resultMap.put("records", count);
		return SUCCESS;
	}
	public String delConfig() {
		String ip = RequestUtil.getIpAddr(this.getRequest());
		if(log.isInfoEnabled())
			log.info("delConfig:" + ip);
		if(isNotLogin()){
			resultMap.put("success", false);
			return SUCCESS;
		}
		String configIds = getParam("configIds");
		
		if(log.isInfoEnabled())
			log.info("delConfig,userName:" + this.getUsername() + ",configIds:" + configIds);
		
		String[] ids = {};
		if (configIds != null && configIds.length() > 0 ) {
			ids = configIds.substring(0, configIds.lastIndexOf(",")).split(",");
		}
		 
		try {
			for (String id : ids) {
				systemConfigService.deleteById(Long.parseLong(id));
			}
			resultMap.put("success", true);
		} catch (NumberFormatException e) {
			resultMap.put("success", false);
			e.printStackTrace();
		}
		return SUCCESS;
	}
	
	public String addSave() {
		String ip = RequestUtil.getIpAddr(this.getRequest());
		if(log.isInfoEnabled())
			log.info("delConfig:" + ip);
		
		if(isNotLogin()){
			resultMap.put("msg", this.getText("common.add.fail"));
			return SUCCESS;
		}
		String configKey = getParam("configKey");
		String configValue = getParam("configValue");
		String detail = getParam("detail");
		SystemConfig systemConfig = new SystemConfig();
		systemConfig.setConfigKey(configKey);
		systemConfig.setConfigValue(configValue);
		systemConfig.setDetail(detail);
		
		if(log.isInfoEnabled())
			log.info("addSave,userName:" + this.getUsername() +",systemConfig:" + systemConfig);
		
		try {
			systemConfigService.save(systemConfig);
			resultMap.put("msg", this.getText("common.add.success"));
		} catch (Exception e) {
			resultMap.put("msg", this.getText("common.add.fail"));
			e.printStackTrace();
			log.error("------添加失败，可能是主键重复" + e.getMessage());
		}
		return SUCCESS;
	}
	
	public String editSave() {
		String ip = RequestUtil.getIpAddr(this.getRequest());
		if(log.isInfoEnabled())
			log.info("editSave:" + ip);
		
		if(isNotLogin()){
			resultMap.put("msg", this.getText("common.edit.fail"));
			return SUCCESS;
		}
		String id = getParam("id");
		String configKey = getParam("configKey");
		String configValue = getParam("configValue");
		String detail = getParam("detail");
		
		try {
			SystemConfig systemConfig = systemConfigService.get(StringUtil.nullToLong(id));
			systemConfig.setConfigKey(configKey);
			systemConfig.setConfigValue(configValue);
			systemConfig.setDetail(detail);
			
			if(log.isInfoEnabled())
				log.info("editSave,userName:" + this.getUsername() +",systemConfig:" + systemConfig);
			
			systemConfigService.save(systemConfig);
			resultMap.put("msg", this.getText("common.edit.success"));
		} catch (Exception e) {
			resultMap.put("msg", this.getText("common.edit.fail"));
			e.printStackTrace();
		}
		return SUCCESS;
	}
	public String exportExcel() {
		String ip = RequestUtil.getIpAddr(this.getRequest());
		if(log.isInfoEnabled()){
			log.info("exportExcel:" + ip);
			log.info("exportExcel,userName:" + this.getUsername() );
		}
		
		String sidx = this.getParam("sidx");                  
		String sord = this.getParam("sord"); 
		String sysParmsKey = this.getParam("sysParmsKey");  				  
		String sysParmsValue = this.getParam("sysParmsValue");  
		
		Map<String,Object> paramsMap = new HashMap<String,Object>();
		paramsMap.put("sidx", sidx);
		paramsMap.put("sord", sord);
		paramsMap.put("configKey", sysParmsKey);
		paramsMap.put("configValue", sysParmsValue);
		
		String sheetName = this.getText("sys.sysConfig.export");
		String titleName[] = new String[] {
				this.getText("sys.sysConfig.id"),
				this.getText("sys.sysConfig.key"),
				this.getText("sys.sysConfig.value"),
				this.getText("sys.sysConfig.detail")
		};
		String[][] data  = new String[0][titleName.length];
		try {
			List<SystemConfig> configList = systemConfigService.getConfigList(paramsMap, 0, 0);
			if (configList.size() > 0) {
				data = new String[configList.size()][titleName.length];
		 		for (int y = 0; y < configList.size(); y++) {
	    			SystemConfig sc = configList.get(y);
	    	
	    			int x = 0;
	    			data[y][x] = StringUtil.nullToString(sc.getId());
	    			++x;
	    			data[y][x] = StringUtil.nullToString(sc.getConfigKey());
	    			++x;
	    			data[y][x] = StringUtil.nullToString(sc.getConfigValue());
	    			++x;
	    			data[y][x] = StringUtil.nullToString(sc.getDetail());
	    			++x;
		 		}
			}
			inputStream = ExportExcelUtil.export(sheetName, titleName, data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return SUCCESS;
	}
	

	
	public SystemConfigService getSystemConfigService() {
		return systemConfigService;
	}

	public void setSystemConfigService(SystemConfigService systemConfigService) {
		this.systemConfigService = systemConfigService;
	}
	public InputStream getInputStream() {
		return inputStream;
	}
	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}
}

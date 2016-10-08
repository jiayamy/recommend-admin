package com.wondertek.mobilevideo.recommend.webapp.servlet;

import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.wondertek.mobilevideo.core.util.HttpClientUtil;
import com.wondertek.mobilevideo.core.util.StringUtil;

public class UpdateCacheThread extends Thread {
	public final static Log log = LogFactory.getLog(UpdateCacheThread.class);
	private String updateUrl;
	
	public UpdateCacheThread(String updateUrl,  String parameterValue, String otherParameter) {
		this.updateUrl = updateUrl + "?cacheType=" + parameterValue +  "&isRefresh=NO";
		if(otherParameter != null)
			this.updateUrl = this.updateUrl + "&" + otherParameter;
	}
	public UpdateCacheThread(String updateUrl){
		this.updateUrl = updateUrl;
	}

	@Override
	public void run() {
		// 刷新其他服务器缓存
		ResourceBundle resourceBundle = ResourceBundle.getBundle("config");
		String otherAddressWebApp = StringUtil.null2Str(resourceBundle.getString("allSystemUpdateCacheIp"));
		String[] address = (otherAddressWebApp == null) ? new String[0] : otherAddressWebApp.split(",");
		
		if(updateUrl != null && updateUrl.startsWith("/")){
			updateUrl = updateUrl.substring(1);
		}
		
		for(int i = 0; i < address.length; i ++){
			if(!StringUtil.isNullStr(address[i])){
				String url = null;
				try{
					url = "http://" + address[i] + "/"+updateUrl;
					HttpClientUtil.requestGet(url);
					log.info(url);
				}catch(Exception e){
					log.error(e.getMessage()+",url:"+url);
					continue;
				}
			}
		}
	}
	
	public String getUpdateUrl() {
		return updateUrl;
	}
	public void setUpdateUrl(String updateUrl) {
		this.updateUrl = updateUrl;
	}
}

package com.wondertek.mobilevideo.recommend.webapp.action;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionSupport;
import com.wondertek.mobilevideo.core.base.Constants;
import com.wondertek.mobilevideo.core.recommend.bean.ResultBean;
import com.wondertek.mobilevideo.core.recommend.util.ST;
import com.wondertek.mobilevideo.core.util.StringUtil;

/**
 * 基类
 * @author lvliuzhong
 *
 */
public class BaseAction extends ActionSupport {
    private static final long serialVersionUID = 3525445612504421307L;

    public static final String CANCEL = "cancel";

    protected final transient Log log = LogFactory.getLog(getClass());

    protected Map<String, Object> resultMap = new HashMap<String, Object>();
    public Map<String, Object> getResultMap() {
		return resultMap;
	}
	public void setResultMap(Map<String, Object> resultMap) {
		this.resultMap = resultMap;
	}
    /**
     * Save the message in the session, appending if messages already exist
     *
     * @param msg the message to put in the session
     */
    @SuppressWarnings("unchecked")
    protected void saveMessage(String msg) {
        List<String> messages = (List<String>) getRequest().getSession().getAttribute("messages");
        if (messages == null) {
            messages = new ArrayList<String>();
        }
        messages.add(msg);
        getRequest().getSession().setAttribute("messages", messages);
    }

    /**
     * Convenience method to get the Configuration HashMap
     * from the servlet context.
     *
     * @return the user's populated form from the session
     */
    @SuppressWarnings("rawtypes")
	protected Map getConfiguration() {
        Map config = (HashMap) getSession().getServletContext().getAttribute(Constants.CONFIG);
        // so unit tests don't puke when nothing's been set
        if (config == null) {
            return new HashMap();
        }
        return config;
    }

    /**
     * Convenience method to get the request
     *
     * @return current request
     */
    protected HttpServletRequest getRequest() {
        return ServletActionContext.getRequest();
    }

    /**
     * Convenience method to get the response
     *
     * @return current response
     */
    protected HttpServletResponse getResponse() {
        return ServletActionContext.getResponse();
    }

    /**
     * Convenience method to get the session. This will create a session if one doesn't exist.
     *
     * @return the session from the request (request.getSession()).
     */
    protected HttpSession getSession() {
        return getRequest().getSession();
    }
    
    
	public String getUserId() {
		if (this.getSession().getAttribute("_sso_userid") != null)
			return this.getSession().getAttribute("_sso_userid").toString();
		return "";
	}

	public String getUsername() {
		if (this.getSession().getAttribute("_sso_username") != null)
			return (String) this.getSession().getAttribute("_sso_username");
		return "";
	}
	
    protected String reParam(HttpServletRequest request,String defaultValue,String fieldName){
    	String value = request.getParameter(fieldName) == null ? defaultValue:request.getParameter(fieldName);
    	if(value != null) {
    		return value.trim();
    	}else {
    		return null;
    	}
	}
    protected void writeResponseForForm(String msg) {
		String responseText = "{success: true,data: " + msg + "}";
		getResponse().setHeader("Cache-Control","no-cache");
		this.writeTextResponse(responseText);
	}
    
    protected void writeResponse(String msg, String header){
		try {
			if (msg != null)
			{
				getResponse().setContentType(ST.getDefault(header, "text/json;charset=UTF-8"));
				getResponse().getWriter().write(msg);
				getResponse().getWriter().flush();
			}
		} catch (IOException e){
			log.error(e.getCause());
			e.printStackTrace();
		}
	}
    
    protected void writeResponse(String msg) {
		try {
			if (msg != null) {
				getResponse().getWriter().write(msg);
				getResponse().getWriter().flush();
			}
		} catch (IOException e) {
			log.error(e.getCause());
			e.printStackTrace();
		}
	}

	protected void writeTextResponse(String msg) {
		getResponse().setContentType("text/plain;charset=UTF-8");
		getResponse().setHeader("Cache-Control","no-cache");
		writeResponse(msg);
	}
	
	protected void writeTextResponse(String msg,String contentType) {
		getResponse().setContentType(contentType);
		getResponse().setHeader("Cache-Control","no-cache");
		writeResponse(msg);
	}
	
	protected void writeHtmlResponse(String msg) {
		getResponse().setContentType("text/html;charset=UTF-8");
		writeResponse(msg);
	}
	
//	public void sendResponseMsg(ResultBean result) {
//		writeResponse(
//			result.getReturnJson(), 
//			"text/html;charset=UTF-8"
//		);
//	}
	
	protected String createFile(String fileName,String tempDir){
		String filePath = this.getSession().getServletContext().getRealPath(tempDir + fileName);
		File file = new File(filePath);
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		return filePath;
	}
    /**
     * 重载ActionSupport的getText方法，解决输出的的消息出现{0}等现象
     */
    public String getText(String key,String arg1){
    	 List<String> args= new ArrayList<String>();
         args.add(arg1);
         return super.getText(key,args);
    }
    /**
     * 重载ActionSupport的getText方法，解决输出的的消息出现{0}等现象
     */
    public String getText(String key,String arg1,String arg2){
    	 List<String> args= new ArrayList<String>();
         args.add(arg1);
         args.add(arg2);
         return super.getText(key,args);
    }
    /**
     * 重载ActionSupport的getText方法，解决输出的的消息出现{0}等现象
     */
    public String getText(String key,String arg1,String arg2,String arg3){
    	 List<String> args= new ArrayList<String>();
         args.add(arg1);
         args.add(arg2);
         args.add(arg3);
         return super.getText(key,args);
    }
    /**
     * 重载ActionSupport的getText方法，解决输出的的消息出现{0}等现象
     */
    public String getText(String key,String arg1,String arg2,String arg3,String arg4){
    	 List<String> args= new ArrayList<String>();
         args.add(arg1);
         args.add(arg2);
         args.add(arg3);
         args.add(arg4);
         return super.getText(key,args);
    }
	/**
	 * 根据名称获取一个请求参数的值 
	 * @param k
	 * @return
	 */
	protected String getParam(String k){
		return getParam(k, "");
	}
	protected String getParam(String k, String def){
		return ST.getDefault(getRequest().getParameter(k), def);
	}
	/**
	 * 根据名称获取一个请求参数的值
	 * @param k
	 * @return
	 */
	protected String[] getParams(String k){
		String[] vals = getRequest().getParameterValues(k);
		if (vals == null){
			vals = new String[0];
		}
		return vals;
	}
	/**
	 * 根据请求参数初始化指定对象的值
	 * @param clazz
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	@SuppressWarnings("unchecked")
	protected Object initObject(Class<?> clazz) throws InstantiationException, IllegalAccessException {
		Object obj = clazz.newInstance();
		Map<String,String[]> values = getRequest().getParameterMap();
		
		String name = null;
		String[] value = null;
		String val = null;
		for (Enumeration<String> list = getRequest().getParameterNames(); list.hasMoreElements();) {
			name = list.nextElement();
			value = values.get(name);
			try {
				if(value != null && value.length > 0){
					val = ST.getRange(value);
				}else{
					val = null;
				}
				BeanUtils.setProperty(obj, name, val);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return obj;
	}
	/**
	 * 检查seesion是否失效
	 */
	public void checkSession(){
		this.writeHtmlResponse("true");
	}
	/**
	 * 
	 * @param context
	 */
	public static void setupContext(ServletContext context) {
//		context.setAttribute(UmsConstants.MEMBER_LEVEL_KEY, UmsConstants.MEMBER_LEVELS);
	}
	/**
	 * 设置业务失败信息
	 * setErrorMsg  
	 * @param result
	 * @return    
	 * ResultBean   
	 * @exception    
	 * @since  1.0.0
	 */
	protected ResultBean setErrorMsg(ResultBean result) {
		if (!result.isSuccess()) {
			result.setMsg(getText(result.getMsg()));
		}
		return result;
	}
	/**
	 * 获取首页
	 * @return
	 */
	public String getMainPage(){
		return SUCCESS;
	}
	/**
	 * 是否没登录
	 * @return
	 */
	public Boolean isNotLogin(){
		return StringUtil.isNullStr(getUsername());
	}
}

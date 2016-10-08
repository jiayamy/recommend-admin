package com.wondertek.mobilevideo.recommend.webapp.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.net.ssl.SSLContext;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;

import com.wondertek.mobilevideo.core.util.Configuration;
import com.wondertek.mobilevideo.core.util.JsonUtil;

public class HttpsUtils {

	private static HttpClient client = null;
	private static Configuration conf = null;
	static {
		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
		cm.setMaxTotal(128);
		cm.setDefaultMaxPerRoute(128);
		client = HttpClients.custom().setConnectionManager(cm).build();
		conf = new Configuration("/keyStore.properties");
	}
	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public static KeyStore getKeyTrustStore(String keyStorePassword,String keyTrustStorePath) throws Exception {
		KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
		FileInputStream is = new FileInputStream(keyTrustStorePath);
		ks.load(is, keyStorePassword.toCharArray());
		is.close();
		return ks;
	}
	/**
	 * 获取证书
	 * @param keyStorePassword
	 * @param keyStorePath
	 * @return
	 * @throws Exception
	 */
	public static KeyStore getKeyStore(String keyStorePassword,String keyStorePath) throws Exception {
		KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
		FileInputStream is = new FileInputStream(keyStorePath);
		ks.load(is, keyStorePassword.toCharArray());
		is.close();
		return ks;
	}
	/**
	 * 
	 * @param keyStorePassword
	 * @param keyStorePath
	 * @param keyTrustStorePath
	 * @return
	 */
	private static CloseableHttpClient createSSLInsecureClient(String keyStorePassword,String keyStorePath,String keyTrustStorePath) {
		try {
			SSLContext sslContext = SSLContexts
					.custom()
					.loadKeyMaterial(getKeyStore(keyStorePassword,keyStorePath),keyStorePassword.toCharArray())
//					 .loadTrustMaterial(getKeyTrustStore(keyStorePassword,keyTrustStorePath),new TrustSelfSignedStrategy())
					.build();
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
					sslContext, new String[] { "TLSv1" }, null,
					SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			HttpClientBuilder builder = HttpClients.custom()
					.setSSLSocketFactory(sslsf);
			return builder.build();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 发送一个 Post 请求, 使用指定的字符集编码.
	 * 
	 * @param url
	 * @param body
	 *            RequestBody
	 * @param mimeType
	 *            例如 application/json
	 * @param charset
	 *            编码
	 * @param connTimeout
	 *            建立链接超时时间,毫秒.
	 * @param readTimeout
	 *            响应超时时间,毫秒.
	 * @return ResponseBody, 使用指定的字符集编码.
	 * 
	 * @throws ConnectTimeoutException
	 *             建立链接超时异常
	 * @throws SocketTimeoutException
	 *             响应超时
	 * @throws Exception
	 */
	public static String post(String siteName,String url, String body, String mimeType,
			String charset, Integer connTimeout, Integer readTimeout,
			String proxyHost, Integer proxyPort) {
		HttpClient client = null;
		HttpPost post = new HttpPost(url);
		String result = "";
		try {
			if (StringUtils.isNotBlank(body)) {
				HttpEntity entity = new StringEntity(body, ContentType.create(
						mimeType, charset));
				post.setEntity(entity);
			}
			// 设置参数
			Builder customReqConf = RequestConfig.custom();
			if (connTimeout != null) {
				customReqConf.setConnectTimeout(connTimeout);
			}
			if (readTimeout != null) {
				customReqConf.setSocketTimeout(readTimeout);
			}
			if (StringUtils.isNotBlank(proxyHost) && proxyPort != null) {
				customReqConf.setProxy(new HttpHost(proxyHost, proxyPort));
			}
			post.setConfig(customReqConf.build());
			HttpResponse res;
			if (url.startsWith("https")) {
				client = createSSLInsecureClient(conf.getProperty(siteName + ".https.password"),conf.getProperty(siteName + ".keyStore.path")
						,conf.getProperty(siteName + ".trustStore.path"));
				res = client.execute(post);
			} else {
				client = HttpsUtils.client;
				res = client.execute(post);
			}
			result = IOUtils.toString(res.getEntity().getContent(), charset);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			post.releaseConnection();
			try {
				if (url.startsWith("https") && client != null
						&& client instanceof CloseableHttpClient) {
					((CloseableHttpClient) client).close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * 发送一个 Post 请求, 使用指定的字符集编码.
	 * 
	 * @param url
	 * @param body
	 *            RequestBody
	 * @param mimeType
	 *            例如 application/json
	 * @param charset
	 *            编码
	 * @param connTimeout
	 *            建立链接超时时间,毫秒.
	 * @param readTimeout
	 *            响应超时时间,毫秒.
	 * @return ResponseBody, 使用指定的字符集编码.
	 * @throws ConnectTimeoutException
	 *             建立链接超时异常
	 * @throws SocketTimeoutException
	 *             响应超时
	 * @throws Exception
	 */
	public static String post(String siteName,String url, String body, String mimeType,
			String charset, Integer connTimeout, Integer readTimeout) {
		return post(siteName, url, body, mimeType, charset, connTimeout, readTimeout,
				null, null);
	}

	/**
	 * 发送一个 GET 请求
	 * 
	 * @param url
	 * @param charset
	 * @return
	 * @throws Exception
	 */
	public static String get(String siteName, String url, String charset) throws Exception {
		return get(siteName,url, charset, null, null);
	}

	/**
	 * 发送一个 GET 请求
	 * 
	 * @param url
	 * @param charset
	 * @param connTimeout
	 *            建立链接超时时间,毫秒.
	 * @param readTimeout
	 *            响应超时时间,毫秒.
	 * @return
	 * @throws ConnectTimeoutException
	 *             建立链接超时
	 * @throws SocketTimeoutException
	 *             响应超时
	 * @throws Exception
	 */
	public static String get(String siteName,String url, String charset, Integer connTimeout,
			Integer readTimeout) throws ConnectTimeoutException,
			SocketTimeoutException, Exception {
		HttpClient client = null;
		HttpGet get = new HttpGet(url);
		String result = "";
		try {
			// 设置参数
			Builder customReqConf = RequestConfig.custom();
			if (connTimeout != null) {
				customReqConf.setConnectTimeout(connTimeout);
			}
			if (readTimeout != null) {
				customReqConf.setSocketTimeout(readTimeout);
			}
			get.setConfig(customReqConf.build());

			HttpResponse res = null;

			if (url.startsWith("https")) {
				client = createSSLInsecureClient(conf.getProperty(siteName + ".https.password"),conf.getProperty(siteName + ".keyStore.path")
						,conf.getProperty(siteName + ".trustStore.path"));
				res = client.execute(get);
			} else {
				client = HttpsUtils.client;
				res = client.execute(get);
			}
			result = IOUtils.toString(res.getEntity().getContent(), charset);
		} finally {
			get.releaseConnection();
			if (url.startsWith("https") && client != null
					&& client instanceof CloseableHttpClient) {
				((CloseableHttpClient) client).close();
			}
		}
		return result;
	}

	/**
	 * 提交form表单
	 * 
	 * @param url
	 * @param params
	 * @param connTimeout
	 * @param readTimeout
	 * @return
	 * @throws ConnectTimeoutException
	 * @throws SocketTimeoutException
	 * @throws Exception
	 */
	public static String postForm(String siteName,String url, Map<String, String> params,Map<String, String> headers, Integer connTimeout,
			Integer readTimeout, String proxyHost, Integer proxyPort)
			throws ConnectTimeoutException, SocketTimeoutException, Exception {
		HttpClient client = null;
		HttpPost post = new HttpPost(url);
		try {
			if (params != null && !params.isEmpty()) {
				List<NameValuePair> formParams = new ArrayList<NameValuePair>();
				Set<Entry<String, String>> entrySet = params.entrySet();
				for (Entry<String, String> entry : entrySet) {
					formParams.add(new BasicNameValuePair(entry.getKey(), entry
							.getValue()));
				}
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(
						formParams, Consts.UTF_8);
				post.setEntity(entity);
			}
			if (headers != null && !headers.isEmpty()) {
				for (Entry<String, String> entry : headers.entrySet()) {
					post.addHeader(entry.getKey(), entry.getValue());
				}
			}
			Builder customReqConf = RequestConfig.custom();
			if (connTimeout != null) {
				customReqConf.setConnectTimeout(connTimeout);
			}
			if (readTimeout != null) {
				customReqConf.setSocketTimeout(readTimeout);
			}
			if (StringUtils.isNotBlank(proxyHost) && proxyPort != null) {
				customReqConf.setProxy(new HttpHost(proxyHost, proxyPort));
			}
			post.setConfig(customReqConf.build());
			HttpResponse res = null;
			if (url.startsWith("https")) {
				client = createSSLInsecureClient(conf.getProperty(siteName + ".https.password"),conf.getProperty(siteName + ".keyStore.path")
						,conf.getProperty(siteName + ".trustStore.path"));
				res = client.execute(post);
			} else {
				client = HttpsUtils.client;
				res = client.execute(post);
			}
			return IOUtils.toString(res.getEntity().getContent(), "UTF-8");
		} finally {
			post.releaseConnection();
			if (url.startsWith("https") && client != null
					&& client instanceof CloseableHttpClient) {
				((CloseableHttpClient) client).close();
			}
		}
	}
	//
	public static void main(String[] args) {
		String httpsUrl = "https://m.douban.com/movie/review/1291557/";
		String siteName = "douban";
		
		//httpsUrl = "https://www.baidu.com";
		siteName = "baidu";
		Map<String, Object> body = new HashMap<String, Object>();
		body.put("platForm", "0003");
		body.put("msisdn", "13817423959");
		System.out.println(body);
		String jsonStr = JsonUtil.map2json(body);
		System.out.println("aaaa:"+jsonStr);
		try {
			String result = post(siteName,httpsUrl, jsonStr, "application/json",
					"UTF-8", 10000, 10000, "192.168.1.39", 3128);
			System.out.println("result:"+result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}


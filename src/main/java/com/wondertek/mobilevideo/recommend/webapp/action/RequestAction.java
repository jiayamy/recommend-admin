package com.wondertek.mobilevideo.recommend.webapp.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.wondertek.mobilevideo.core.recommend.cache.EnumsConfigCache;
import com.wondertek.mobilevideo.core.recommend.cache.EnumsInfoCache;
import com.wondertek.mobilevideo.core.recommend.cache.PrdTypeRelationCache;
import com.wondertek.mobilevideo.core.recommend.cache.redis.service.RecommendInfoCacheManager;
import com.wondertek.mobilevideo.core.recommend.cache.redis.service.SearchCacheManager;
import com.wondertek.mobilevideo.core.recommend.cache.redis.service.TopRecommendCacheManager;
import com.wondertek.mobilevideo.core.recommend.cache.redis.service.UserTagCacheManager;
import com.wondertek.mobilevideo.core.recommend.cache.redis.service.VomsRecommendCacheManager;
import com.wondertek.mobilevideo.core.recommend.model.PrdTypeRelation;
import com.wondertek.mobilevideo.core.recommend.search.SearchRequest;
import com.wondertek.mobilevideo.core.recommend.search.SearchResult;
import com.wondertek.mobilevideo.core.recommend.util.CatInfoSort;
import com.wondertek.mobilevideo.core.recommend.util.CatItemSort;
import com.wondertek.mobilevideo.core.recommend.util.RecomdItemSort;
import com.wondertek.mobilevideo.core.recommend.util.RecommendConstants;
import com.wondertek.mobilevideo.core.recommend.util.RequestConstants;
import com.wondertek.mobilevideo.core.recommend.util.RequestUtil;
import com.wondertek.mobilevideo.core.recommend.vo.RecommendInfoVo;
import com.wondertek.mobilevideo.core.recommend.vo.RecommendTopVo;
import com.wondertek.mobilevideo.core.recommend.vo.VomsRecommendVo;
import com.wondertek.mobilevideo.core.recommend.vo.mongo.CatInfo;
import com.wondertek.mobilevideo.core.recommend.vo.mongo.CatItem;
import com.wondertek.mobilevideo.core.recommend.vo.mongo.RecomdItem;
import com.wondertek.mobilevideo.core.recommend.vo.mongo.UserTag;
import com.wondertek.mobilevideo.core.util.StringUtil;

/**
 * 外部请求
 * @author lvliuzhong
 *
 */
public class RequestAction extends BaseAction {
	private static final long serialVersionUID = -4711535232308356966L;
	private RecommendInfoCacheManager recommendInfoCacheManager;
	private UserTagCacheManager userTagCacheManager;
	private SearchCacheManager searchCacheManager;
	private VomsRecommendCacheManager vomsRecommendCacheManager;
	private TopRecommendCacheManager topRecommendCacheManager;
	/**
	 * 搜索合并的返回
	 * @return
	 */
	public String searchAllList() {
		String ip = RequestUtil.getIpAddr(this.getRequest());
		if(log.isInfoEnabled())
			log.info("searchAllList Ip:" + ip);
		//首先流中获取请求体
		String reqJson = RequestUtil.receiveReq(this.getRequest());
		if(log.isDebugEnabled() && RequestConstants.V_PRINT_REQUEST_ENABLE){
			log.debug("searchAllList reqJson:" + reqJson);
		}
		if(StringUtil.isNullStr(reqJson)){//请求为空，返回错误
			resultMap.put(RequestConstants.R_SUCC, Boolean.FALSE);
			resultMap.put(RequestConstants.R_CODE, RequestConstants.R_CODE_110001);
			resultMap.put(RequestConstants.R_MSG, this.getText("request.error.contentnull"));
			return SUCCESS;
		}
		//解析请求体
		UserTag reqUserTag = null;
		try {
			reqUserTag = JSON.parseObject(reqJson,UserTag.class);
		} catch (Exception e) {
		}
		//校验请求体
		if(reqUserTag == null){
			resultMap.put(RequestConstants.R_SUCC, Boolean.FALSE);
			resultMap.put(RequestConstants.R_CODE, RequestConstants.R_CODE_110002);
			resultMap.put(RequestConstants.R_MSG, this.getText("request.error.contenterror"));
			return SUCCESS;
		}
		if(StringUtil.isNullStr(reqUserTag.getPrdType()) || StringUtil.isNullStr(reqUserTag.getCtVer())){
			resultMap.put(RequestConstants.R_SUCC, Boolean.FALSE);
			resultMap.put(RequestConstants.R_CODE, RequestConstants.R_CODE_110003);
			resultMap.put(RequestConstants.R_MSG, this.getText("request.error.paramnull"));
			return SUCCESS;
		}
		PrdTypeRelation prdTypeRelation = PrdTypeRelationCache.PRDTYPE_RELATIONS.get(reqUserTag.getPrdType());
		if(prdTypeRelation == null || StringUtil.isNullStr(prdTypeRelation.getPrdInfoIds())){//搜索引擎需要产品包ID，平台需要产品
			resultMap.put(RequestConstants.R_SUCC, Boolean.FALSE);
			resultMap.put(RequestConstants.R_CODE, RequestConstants.R_CODE_120001);
			resultMap.put(RequestConstants.R_MSG, this.getText("request.error.prdtypenotfound"));
			return SUCCESS;
		}
		if(reqUserTag.getStart() == null || reqUserTag.getStart() < 0){
			reqUserTag.setStart(0);
		}
		if(reqUserTag.getLimit() == null || reqUserTag.getLimit() < 0){
			reqUserTag.setLimit(RequestConstants.V_DEFAUL_REQUEST_LIMIT);
		}
		//请求的基本参数。用户ID，搜索开始和条数
		String prdType = reqUserTag.getPrdType();
		String userId = reqUserTag.getId();//可能为空，为空，则获取默认推荐配置标签
		int start = reqUserTag.getStart();
		int limit = reqUserTag.getLimit();
		String order = reqUserTag.getOrder();
		String ctVer = reqUserTag.getCtVer();
		//当前页数
		int page = (start / limit) + 1;
		//初始化数据
		int recomdCatMax = RequestConstants.V_DEFAULT_RECOMD_CAT_MAX;//一级分类最多推荐个数
		int recomdCatItemMax = RequestConstants.V_DEFAULT_RECOMD_CATITEM_MAX;//一级分类标签最多推荐个数

		int searchCatMax = RequestConstants.V_DEFAULT_SEARCH_CAT_MAX;//一级分类最多搜索个数
		int searchCatPerMax = RequestConstants.V_DEFAULT_SEARCH_CAT_PER_MAX;//一级分类最多搜索次数
		int searchCatItemMax = RequestConstants.V_DEFAULT_SEARCH_CATITEM_MAX;//一级分类标签最多搜索个数
		
		int searchLimit = RequestConstants.V_DEFAULT_SEARCH_LIMIT;//正常搜索最多搜索多少条
		int searchItemLimit = RequestConstants.V_DEFAULT_SEARCH_LIMIT_CATITEM;//子项单独搜索最多搜索多少条
		
		String searchUrl = RequestConstants.V_SEARCH_URL;
//		int searchMaxCount = RequestConstants.V_DEFAULT_SEARCH_COUNT_MAX;//正常搜索最多搜索多少条
		
		Double defaultScore = RequestConstants.V_DEFAULT_USERTAG_SCORE;//默认分数
		
		Double specialTopicRatio = RequestConstants.V_DEFAULT_RECOMD_SPECIALTOPIC_RATIO;
		Double combinedContRatio = RequestConstants.V_DEFAULT_RECOMD_COMBINEDCONT_RATIO;
		Double bigPicContRatio = RequestConstants.V_DEFAULT_RECOMD_BIGPICCONT_RATIO;
		Double multiPicContRatio = RequestConstants.V_DEFAULT_RECOMD_MULTIPICCONT_RATIO;
		
		//最大的搜索个数，超过后直接返回空对象回去
//		if(searchMaxCount <= start){
//			resultMap.put(RequestConstants.R_SUCC, Boolean.TRUE);
//			resultMap.put(RequestConstants.R_CODE, RequestConstants.R_CODE_000000);
//			resultMap.put(RequestConstants.R_MSG, this.getText("request.success"));
//			resultMap.put(RequestConstants.R_ROOT, new ArrayList<RecommendInfoVo>());
//			resultMap.put(RequestConstants.R_TOTAL, searchMaxCount);
//			return SUCCESS;
//		}
		long s = System.currentTimeMillis();
		long end1 = s;
		long end2 = s;
		long end3 = s;
		long end4 = s;
		long end5 = s;
		long end6 = s;
		long end7 = s;
		long end8 = s;
		long end9 = s;
		long end10 = s;
		long end11 = s;
		long end12 = s;
		//从redis或mongo中获取该用户的标签
		UserTag dbUserTag = null;
		try {
			dbUserTag = null;
			if(!StringUtil.isNullStr(userId)){
				try {
					dbUserTag = userTagCacheManager.queryById(userId);
				} catch (Exception e) {
				}
				if(dbUserTag == null || dbUserTag.getId() == null){
					dbUserTag = null;
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
		end1 = System.currentTimeMillis();
		
		List<RecommendInfoVo> pomsContList = new ArrayList<RecommendInfoVo>();

		int total = 0;
		
		UserTag originalUserTag = null;//用于查询voms数据，防止被修改
		Boolean isInitVomsLabel = Boolean.FALSE; 
		StringBuffer vomsLabelInfo = new StringBuffer();
		//查找poms的数据
		//TODO 循环执行
		int exeCount = 0;
		while(exeCount < 2){
			List<RecommendInfoVo> recomdList = new ArrayList<RecommendInfoVo>();
			List<RecommendInfoVo> searchList = new ArrayList<RecommendInfoVo>();
			try {
				//确定用户最后的标签
				if(!checkTagsNotNull(reqUserTag)){//请求没带标签
					if (dbUserTag == null){//redis里面没有用户的标签
						//如果mongo中没有用户的标签，则获取默认标签
						try {
							reqUserTag = JSON.parseObject(RequestConstants.V_DEFAULT_USERTAG,UserTag.class);
							exeCount = exeCount + 2;//已经使用了系统的默认标签，无需重复搜索
						} catch (Exception e) {
							log.error(e.getMessage(),e);
						}
						//没有任何标签
						if (!checkTagsNotNull(reqUserTag)){
							resultMap.put(RequestConstants.R_SUCC, Boolean.FALSE);
							resultMap.put(RequestConstants.R_CODE, RequestConstants.R_CODE_120002);
							resultMap.put(RequestConstants.R_MSG, this.getText("request.error.tagnull"));
							if(log.isDebugEnabled())
								log.debug("searchAllList end,duration:" + (s -end1));
							return SUCCESS;
						}
					}else{
						//如果传送过来的一级分类或者人工推荐数据都为空，则从mongo里面获取
						reqUserTag = dbUserTag;
					}
				}
				end2 = System.currentTimeMillis();
				//获取用户的标签分数
				Map<String,Double> userTagScoreMap = new HashMap<String,Double>();	//用户标签中的分数，用于填充用户请求的标签分数
				Map<String,CatInfo> dbCatInfos = new HashMap<String,CatInfo>();		//一级分类下的标签，用于填充没有携带标签的请求
				Map<String, String> catIds = EnumsInfoCache.VAL_ENUMSINFO.get(EnumsInfoCache.TYPE_CAT);
				Map<String, String> labelIds = EnumsInfoCache.VAL_ENUMSINFO.get(EnumsInfoCache.TYPE_LABEL);
				if(dbUserTag != null){
					getUserTagScore(dbUserTag,userTagScoreMap,catIds,labelIds,dbCatInfos);
				}
				end3 = System.currentTimeMillis();
				//填充分数并排序
				fillSortScore(reqUserTag,userTagScoreMap,catIds,labelIds,dbCatInfos,defaultScore);
				end4 = System.currentTimeMillis();
				//清空不需要的对象
				userTagScoreMap.clear();
				userTagScoreMap = null;
				dbCatInfos.clear();
				dbCatInfos = null;
				if(originalUserTag == null){
					originalUserTag = reqUserTag;
				}
				//每个一级分类都找下对应的人工推荐和搜索引擎搜索数据
				if(reqUserTag.getCats() != null && reqUserTag.getCats().size() > 0){
					int searchCatCount = 1;		//一级分类下搜索查找次数
					int recomdCatCount = 1;		//一级分类下推荐标签查找次数
					int catItemCount = 1;		//一级分类下的标签查找次数
					String recomdLabels = null;	//搜索的标签，最后搜索引擎需要使用
					for(CatInfo catInfo : reqUserTag.getCats()){//一个一级分类查找N次
						if(StringUtil.isNullStr(catInfo.getCatId())){//如果一级标签不存在，则不查找
							continue;
						}
						if(recomdCatCount > recomdCatMax && searchCatCount > searchCatMax){//超过了人工推荐和搜索引擎最多的一级分类搜索次数
							break;
						}
						//查找内容形态
						String mediaShape = null;//多个逗号分隔
						boolean isAllMediaShape = true;
						if(catInfo.getItems() != null 
								&& searchCatCount <= searchCatMax){
							for(CatItem catItem : catInfo.getItems()){
								if(StringUtil.isNullStr(catItem.getLabelId()) 
										|| StringUtil.isNullStr(catItem.getLabelValue())){//搜索的ID必须要有，搜索的值必须要有
									continue;
								}
								if(RequestConstants.SEARCH_KEY_MEDIASHAPE.equals(catItem.getLabelId())){
									if(mediaShape == null){
										mediaShape = catItem.getLabelValue();
									}else{
										mediaShape = mediaShape + RecommendConstants.SPLIT_COMMA + catItem.getLabelValue();
									}
								}else{
									isAllMediaShape = false;
								}
							}
						}
						
						recomdLabels = null;
						boolean isSearchCat = false;
						//搜索推荐标签，推荐标签除了人工推荐需要，搜索引擎也需要
						if(catInfo.getRecommendation() != null 
								&& (recomdCatCount <= recomdCatMax || searchCatCount <= searchCatMax)){
							catItemCount = 1;
							for(RecomdItem recomdItem : catInfo.getRecommendation()){
								if(catItemCount > recomdCatItemMax){//每个一级分类下的推荐标签最多取多少个
									break;
								}
								if(StringUtil.isNullStr(recomdItem.getLabel())){
									continue;
								}
								if(catItemCount == 1){
									recomdLabels = recomdItem.getLabel();
								}else{
									recomdLabels = recomdLabels + RecommendConstants.SPLIT_COMMA + recomdItem.getLabel();
								}
								//调用搜索引擎查找推荐标签(如果一级分类下的普通标签也可以带着推荐标签查询的话，这边就不用查，否则一个个推荐标签的查)
								if(searchCatCount <= searchCatMax && !RequestConstants.V_SEARCH_RECOMD_ENABLE){
									SearchRequest(searchList,searchUrl,order,RecommendConstants.S_BLANK+searchItemLimit,catInfo.getCatId(),
											prdTypeRelation.getPrdInfoIds(),ctVer,prdTypeRelation.getSearchCt(),
											RequestConstants.SEARCH_KEY_RECOMMD,recomdItem.getLabel(),mediaShape,null);
									isSearchCat = true;
								}
								catItemCount ++;
							}
							if(recomdLabels != null && !isInitVomsLabel){
								vomsLabelInfo.append(recomdLabels).append(RecommendConstants.SPLIT_COMMA);
							}
							//调用个性化推荐平台人工推荐数据
							if(recomdCatCount <= recomdCatMax && recomdLabels != null){
								Long rstart = System.currentTimeMillis();
								//从平台搜索数据//2016
								List<RecommendInfoVo> list = recommendInfoCacheManager.queryByLabels(recomdLabels,prdType,catInfo.getCatId(),null);
								Long rend = System.currentTimeMillis();
								if(log.isDebugEnabled())
									log.debug("search from system,duration:" + (rend - rstart));
								if(list != null){//已去重
									if(log.isDebugEnabled() && RequestConstants.V_PRINT_REQUEST_ENABLE){
										log.debug("searchAllList recmdRsts:" + list.size());
									}
									recomdList.addAll(list);
									//清空不需要的对象
									list.clear();
									list = null;
								}
								recomdCatCount ++;
							}
						}
						//分类可能为空,要考虑，直接不传一级分类给搜索引擎
						if(catInfo.getItems() != null 
								&& searchCatCount <= searchCatMax){
							catItemCount = 1;						//刚开始默认第一次
							String tmpFields = null;
							String tmpKeyword = null;
							for(CatItem catItem : catInfo.getItems()){
								if(StringUtil.isNullStr(catItem.getLabelId()) 
										|| StringUtil.isNullStr(catItem.getLabelValue())){//搜索的ID必须要有，搜索的值必须要有
									continue;
								}
								if(catItemCount > searchCatItemMax){//每个一级分类最多搜索多少个标签
									break;
								}
								if(!isAllMediaShape && RequestConstants.SEARCH_KEY_MEDIASHAPE.equals(catItem.getLabelId())){//给出来的不都是内容形态，此时内容形态不单独查询
									continue;
								}
								//搜索一级分类下的某一个评分高的对象。这个默认搜索（searchCatItemMax - 1 ）次
								tmpFields = catItem.getLabelId();
								tmpKeyword = catItem.getLabelValue();
								
								if(isAllMediaShape){//全部都是内容形态，则一个个内容形态查询
									SearchRequest(searchList,searchUrl,order,RecommendConstants.S_BLANK+searchItemLimit,catInfo.getCatId(),
											prdTypeRelation.getPrdInfoIds(),ctVer,prdTypeRelation.getSearchCt(),
											tmpFields,tmpKeyword,null,null);
								}else if(!StringUtil.isNullStr(recomdLabels) && RequestConstants.V_SEARCH_RECOMD_ENABLE){//普通标签查询时附带推荐标签
									tmpFields = tmpFields + RecommendConstants.SPLIT_COMMA + RequestConstants.SEARCH_KEY_RECOMMD;
									tmpKeyword = tmpKeyword + RecommendConstants.SPLIT_COMMA + recomdLabels;
									
									SearchRequest(searchList,searchUrl,order,RecommendConstants.S_BLANK+searchItemLimit,catInfo.getCatId(),
											prdTypeRelation.getPrdInfoIds(),ctVer,prdTypeRelation.getSearchCt(),
											tmpFields,tmpKeyword,mediaShape,null);
								}else{//普通标签查询
									SearchRequest(searchList,searchUrl,order,RecommendConstants.S_BLANK+searchItemLimit,catInfo.getCatId(),
											prdTypeRelation.getPrdInfoIds(),ctVer,prdTypeRelation.getSearchCt(),
											tmpFields,tmpKeyword,mediaShape,null);
								}
								catItemCount ++;
							}
							isSearchCat = true;
						}
						if(isSearchCat){
							searchCatCount ++;
						}
					}
				}
				end5 = System.currentTimeMillis();
			} catch (Exception e) {
				log.error(e.getMessage(),e);
			}
			isInitVomsLabel = Boolean.TRUE;
			//添加经过过滤去重排序的队列
			List<RecommendInfoVo> allList = new ArrayList<RecommendInfoVo>();
			allList.addAll(filterAndSort(recomdList));
			allList.addAll(filterAndSort(searchList));
			end6 = System.currentTimeMillis();
	
			if(log.isDebugEnabled() && RequestConstants.V_PRINT_REQUEST_ENABLE){
				log.debug("searchAllList recomdList:" + recomdList.size());
				log.debug("searchAllList searchList:" + searchList.size());
				log.debug("searchAllList allList:" + allList.size());
			}
			recomdList.clear();//清空不需要的对象
			recomdList = null;
			searchList.clear();
			searchList = null;
			//过滤去重
			int count = 1;
			List<RecommendInfoVo> uniqList = new ArrayList<RecommendInfoVo>();//去重后的对象
			Map<Long,Long> contIdMap = new HashMap<Long,Long>();
			for(RecommendInfoVo vo : allList){
//				if(count > searchMaxCount){
//					break;
//				}
				if(vo.getPrdContId() == null || contIdMap.containsKey(vo.getPrdContId())){
					continue;
				}
				contIdMap.put(vo.getPrdContId(), vo.getPrdContId());
				uniqList.add(vo);
				count ++;
			}
			if(log.isDebugEnabled() && RequestConstants.V_PRINT_REQUEST_ENABLE){
				log.debug("searchAllList uniqList:" + uniqList.size());
			}
			
			end7 = System.currentTimeMillis();
			
			allList.clear();//清空不需要的对象
			allList = null;
			contIdMap.clear();
			contIdMap = null;
			
			//返回最后的分页结果
			total = uniqList.size();
			int requestTotal = (start + limit);
			int end = requestTotal > total ? total : requestTotal;
//			//自己获取最后的分页结果
			if(start < total){
				for(int i = start; i < end ; i++){
					pomsContList.add(uniqList.get(i));
				}
			}
			end8 = System.currentTimeMillis();
			
			uniqList.clear();//清空不需要的对象
			uniqList = null;

			if(exeCount == 1 && total > limit){
				total = limit;
			}
			if(total > 0 || pomsContList.size() > 0 || exeCount == 1){
				exeCount = exeCount + 2;
			}else{
				exeCount ++;
				try {
					reqUserTag = JSON.parseObject(RequestConstants.V_DEFAULT_USERTAG,UserTag.class);
				} catch (Exception e) {
					log.error(e.getMessage(),e);
				}
				if(reqUserTag == null){//系统默认的都为空，无需执行
					exeCount = exeCount + 2;
				}
				dbUserTag = null;
				start = 0;
			}
		}
		//2 查询voms数据
		List<String> types = new ArrayList<String>();
		String labelInfo = vomsLabelInfo.toString();
		int vomsstart = 0;
		int vomslimit = 0;
		if(log.isDebugEnabled() && RequestConstants.V_PRINT_REQUEST_ENABLE){
			log.debug("searchAllList voms lableInfo:" + labelInfo);
		}
		//专题
		List<VomsRecommendVo> specialTopicList = new ArrayList<VomsRecommendVo>();
		int specialTopicTotal = 0;
		if(specialTopicRatio != 0){
			try {
				types.clear();
				types.add("10");
				List<VomsRecommendVo> allList = vomsRecommendCacheManager.queryByLabelInfo(types, prdType, labelInfo);
				//每页个数
				vomslimit = getVomsLimitByRatio(limit,specialTopicRatio);
				vomsstart = vomslimit * (page - 1);
				//数据分页
				specialTopicTotal = allList.size();
				if(log.isDebugEnabled() && RequestConstants.V_PRINT_REQUEST_ENABLE){
					log.debug("searchAllList specialTopicList:" + specialTopicTotal + ",start:" + vomsstart + ",limit:" + vomslimit);
				}
				int requestTotal = (vomsstart + vomslimit);
				int end = requestTotal > specialTopicTotal ? specialTopicTotal : requestTotal;
				if(vomsstart < specialTopicTotal){
					for(int i = vomsstart; i < end ; i++){
						specialTopicList.add(allList.get(i));
					}
				}
			} catch (Exception e) {
				log.error(e.getMessage(),e);
			}
		}
		end9 = System.currentTimeMillis();
		//组合内容
		List<VomsRecommendVo> combinedContList = new ArrayList<VomsRecommendVo>();
		int combinedContTotal = 0;
		if(combinedContRatio != 0){
			try {
				types.clear();
				types.add("11");
				List<VomsRecommendVo> allList = vomsRecommendCacheManager.queryByLabelInfo(types, prdType, labelInfo);
				//每页个数
				vomslimit = getVomsLimitByRatio(limit,combinedContRatio);
				vomsstart = vomslimit * (page - 1);
				//数据分页
				combinedContTotal = allList.size();
				if(log.isDebugEnabled() && RequestConstants.V_PRINT_REQUEST_ENABLE){
					log.debug("searchAllList combinedContList:" + combinedContTotal + ",start:" + vomsstart + ",limit:" + vomslimit);
				}
				int requestTotal = (vomsstart + vomslimit);
				int end = requestTotal > combinedContTotal ? combinedContTotal : requestTotal;
				if(vomsstart < combinedContTotal){
					for(int i = vomsstart; i < end ; i++){
						combinedContList.add(allList.get(i));
					}
				}
			} catch (Exception e) {
				log.error(e.getMessage(),e);
			}
		}
		end10 = System.currentTimeMillis();
		List<VomsRecommendVo> bigPicContList = new ArrayList<VomsRecommendVo>();
		int bigPicContTotal = 0;
		if(bigPicContRatio != 0){
			try {
				types.clear();
				types.add("20");
				List<VomsRecommendVo> allList = vomsRecommendCacheManager.queryByLabelInfo(types, prdType, labelInfo);
				//每页个数
				vomslimit = getVomsLimitByRatio(limit,bigPicContRatio);
				vomsstart = vomslimit * (page - 1);
				//数据分页
				bigPicContTotal = allList.size();
				if(log.isDebugEnabled() && RequestConstants.V_PRINT_REQUEST_ENABLE){
					log.debug("searchAllList bigPicContList:" + bigPicContTotal + ",start:" + vomsstart + ",limit:" + vomslimit);
				}
				int requestTotal = (vomsstart + vomslimit);
				int end = requestTotal > bigPicContTotal ? bigPicContTotal : requestTotal;
				if(vomsstart < bigPicContTotal){
					for(int i = vomsstart; i < end ; i++){
						bigPicContList.add(allList.get(i));
					}
				}
			} catch (Exception e) {
				log.error(e.getMessage(),e);
			}
		}
		end11 = System.currentTimeMillis();
		List<VomsRecommendVo> multiPicContList = new ArrayList<VomsRecommendVo>();
		int multiPicContTotal = 0;
		if(multiPicContRatio != 0){
			try {
				types.clear();
				types.add("21");
				List<VomsRecommendVo> allList = vomsRecommendCacheManager.queryByLabelInfo(types, prdType, labelInfo);
				//每页个数
				vomslimit = getVomsLimitByRatio(limit,multiPicContRatio);
				vomsstart = vomslimit * (page - 1);
				//数据分页
				if(log.isDebugEnabled() && RequestConstants.V_PRINT_REQUEST_ENABLE){
					log.debug("searchAllList multiPicContList:" + multiPicContTotal + ",start:" + vomsstart + ",limit:" + vomslimit);
				}
				multiPicContTotal = allList.size();
				int requestTotal = (vomsstart + vomslimit);
				int end = requestTotal > multiPicContTotal ? multiPicContTotal : requestTotal;
				if(vomsstart < multiPicContTotal){
					for(int i = vomsstart; i < end ; i++){
						multiPicContList.add(allList.get(i));
					}
				}
			} catch (Exception e) {
				log.error(e.getMessage(),e);
			}
		}
		end12 = System.currentTimeMillis();
		//返回结果
		resultMap.put(RequestConstants.R_SUCC, Boolean.TRUE);
		resultMap.put(RequestConstants.R_CODE, RequestConstants.R_CODE_000000);
		resultMap.put(RequestConstants.R_MSG, this.getText("request.success"));
		resultMap.put(RequestConstants.R_POMS_CONT, pomsContList);
		resultMap.put(RequestConstants.R_TOTAL, total);
		resultMap.put(RequestConstants.R_VOMS_SPECIALTOPIC, specialTopicList);
		resultMap.put(RequestConstants.R_TOTAL_SPECIALTOPIC, specialTopicTotal);
		resultMap.put(RequestConstants.R_VOMS_COMBINEDCONT, combinedContList);
		resultMap.put(RequestConstants.R_TOTAL_COMBINEDCONT, combinedContTotal);
		resultMap.put(RequestConstants.R_VOMS_BIGPICCONT, bigPicContList);
		resultMap.put(RequestConstants.R_TOTAL_BIGPICCONT, bigPicContTotal);
		resultMap.put(RequestConstants.R_VOMS_MULTIPICCONT, multiPicContList);
		resultMap.put(RequestConstants.R_TOTAL_MULTIPICCONT, multiPicContTotal);
		
		if(log.isDebugEnabled())
			log.debug("searchAllList end,duration:" + (end12 -s) + "|" + (end1 - s) + "|" + (end2 - end1) + "|" + (end3 - end2) + "|" + (end4 - end3) + "|" + (end5 - end4)
				 + "|" + (end6 - end5) + "|" + (end7 - end6) + "|" + (end8 - end7) + "|" + (end9 - end8) + "|" + (end10 - end9) + "|" + (end11 - end10)
				 + "|" + (end12 - end11));
		return SUCCESS;
	}
	/**
	 * 获取voms数据每页条数
	 * @param limit
	 * @param specialTopicRatio
	 * @return
	 */
	private int getVomsLimitByRatio(int limit, Double specialTopicRatio) {
		Double tmp = specialTopicRatio * limit;
		int rst = tmp.intValue();
		if(rst != tmp){
			rst ++;
		}
		return rst;
	}
	/**
	 * 获取置顶(TOP)数据
	 * @return
	 */
	public String searchTop(){
		String ip = RequestUtil.getIpAddr(this.getRequest());
		//获取参数		
		String prdType = this.getParam("prdType");				
		String startStr = this.getParam("start");
		String limitStr = this.getParam("limit");
		if (log.isDebugEnabled()) {	
			log.debug("searchVomsData ip:" + ip + "," + prdType +"|" + startStr +"|" + limitStr +"|" );
		}
		//校验参数		
		int start = StringUtil.nullToInteger(startStr);
		int limit = StringUtil.nullToInteger(limitStr);
		if(StringUtil.isNullStr(prdType)|| StringUtil.isNullStr(startStr)  || StringUtil.isNullStr(limitStr)){
			resultMap.put(RequestConstants.R_SUCC, Boolean.FALSE);
			resultMap.put(RequestConstants.R_CODE, RequestConstants.R_CODE_110003);
			resultMap.put(RequestConstants.R_MSG, this.getText("request.error.paramnull"));
			return SUCCESS;
		}
		// 处理参数		
		List<RecommendTopVo> returnList = new ArrayList<RecommendTopVo>();
		int total = 0;
		
		long s = System.currentTimeMillis();
		long end1 = s;
		long end2 = s;
		try {
			//获取数据
			List<RecommendTopVo> aList = topRecommendCacheManager.queryTopVos(prdType);
			end1 = System.currentTimeMillis();
			if(log.isDebugEnabled() && RequestConstants.V_PRINT_REQUEST_ENABLE){
				log.debug("searchTopData aList:" + aList.size());
			}
			//数据分页
			total = aList.size();
			int requestTotal = (start + limit);
			int end = requestTotal > total ? total : requestTotal;
			if(start < total){
				for(int i = start; i < end ; i++){
					returnList.add(aList.get(i));
				}
			}
			end2 = System.currentTimeMillis();
			
			//返回结果
			resultMap.put(RequestConstants.R_SUCC, Boolean.TRUE);
			resultMap.put(RequestConstants.R_CODE, RequestConstants.R_CODE_000000);
			resultMap.put(RequestConstants.R_MSG, this.getText("request.success"));
			resultMap.put(RequestConstants.R_ROOT, returnList);
			resultMap.put(RequestConstants.R_TOTAL, total);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			resultMap.put(RequestConstants.R_SUCC, Boolean.FALSE);
			resultMap.put(RequestConstants.R_CODE, RequestConstants.R_CODE_999999);
			resultMap.put(RequestConstants.R_MSG, this.getText("request.error.system"));
			end2 = System.currentTimeMillis();
		}
		
		if(log.isDebugEnabled())
			log.debug("searchVomsData end,duration:" + (end2 -s) + "|" + (end1 - s) + "|" + (end2 - end1));
		
		return SUCCESS;		
	}
	/**
	 * 获取VOMS数据
	 * @return
	 */
	public String searchVoms() {
		String ip = RequestUtil.getIpAddr(this.getRequest());
		// 获取参数
		String id = this.getParam("id");
		String type = this.getParam("type");
		String prdType = this.getParam("prdType");
		String labelInfo = this.getParam("labelInfo");
		String startStr = this.getParam("start");
		String limitStr = this.getParam("limit");
		if (log.isDebugEnabled()) {	
			log.debug("searchVomsData ip:" + ip + "," + id +"|" + type +"|" + prdType +"|" + startStr +"|" + limitStr +"|" + labelInfo);
		}
		// 校验参数
		if (StringUtil.isNullStr(labelInfo) || StringUtil.isNullStr(prdType) 
				|| StringUtil.isNullStr(startStr)  || StringUtil.isNullStr(limitStr) ) {
			resultMap.put(RequestConstants.R_SUCC, Boolean.FALSE);
			resultMap.put(RequestConstants.R_CODE, RequestConstants.R_CODE_110003);
			resultMap.put(RequestConstants.R_MSG, this.getText("request.error.paramnull"));
			return SUCCESS;
		}
		// 处理参数
		int start = StringUtil.nullToInteger(startStr);
		int limit = StringUtil.nullToInteger(limitStr);
		
		List<String> types = new ArrayList<String>();
		if(!StringUtil.isNullStr(type)){
			for(String tmp : type.split(",")){
				if(!StringUtil.isNullStr(tmp)){
					types.add(StringUtil.null2Str(tmp));
				}
			}
		}
		List<VomsRecommendVo> returnList = new ArrayList<VomsRecommendVo>();
		int total = 0;
		
		long s = System.currentTimeMillis();
		long end1 = s;
		long end2 = s;
		try {
			//获取数据
			List<VomsRecommendVo> allList = vomsRecommendCacheManager.queryByLabelInfo(types, prdType, labelInfo);
			end1 = System.currentTimeMillis();
			if(log.isDebugEnabled() && RequestConstants.V_PRINT_REQUEST_ENABLE){
				log.debug("searchVomsData allList:" + allList.size());
			}
			//数据分页
			total = allList.size();
			int requestTotal = (start + limit);
			int end = requestTotal > total ? total : requestTotal;
			if(start < total){
				for(int i = start; i < end ; i++){
					returnList.add(allList.get(i));
				}
			}
			end2 = System.currentTimeMillis();
			
			//返回结果
			resultMap.put(RequestConstants.R_SUCC, Boolean.TRUE);
			resultMap.put(RequestConstants.R_CODE, RequestConstants.R_CODE_000000);
			resultMap.put(RequestConstants.R_MSG, this.getText("request.success"));
			resultMap.put(RequestConstants.R_ROOT, returnList);
			resultMap.put(RequestConstants.R_TOTAL, total);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			resultMap.put(RequestConstants.R_SUCC, Boolean.FALSE);
			resultMap.put(RequestConstants.R_CODE, RequestConstants.R_CODE_999999);
			resultMap.put(RequestConstants.R_MSG, this.getText("request.error.system"));
			end2 = System.currentTimeMillis();
		}
		
		if(log.isDebugEnabled())
			log.debug("searchVomsData end,duration:" + (end2 -s) + "|" + (end1 - s) + "|" + (end2 - end1));
		
		return SUCCESS;

	}
	/**
	 * 添加用户标签
	 * @return
	 */
	public String addTag(){
		String ip = RequestUtil.getIpAddr(this.getRequest());
		log.info("addTagIp:" + ip);
		//首先流中获取请求体
		String reqJson = RequestUtil.receiveReq(this.getRequest());
		if(StringUtil.isNullStr(reqJson)){//请求为空，返回错误
			resultMap.put(RequestConstants.R_SUCC, Boolean.FALSE);
			resultMap.put(RequestConstants.R_CODE, RequestConstants.R_CODE_110001);
			resultMap.put(RequestConstants.R_MSG, this.getText("request.error.contentnull"));
			return SUCCESS;
		}
		//直接透传
		resultMap.put(RequestConstants.R_SUCC, Boolean.TRUE);
		resultMap.put(RequestConstants.R_CODE, RequestConstants.R_CODE_000000);
		return SUCCESS;
	}
	/**
	 * 删除用户标签
	 * @return
	 */
	public String delTag(){
		String ip = RequestUtil.getIpAddr(this.getRequest());
		log.info("queryTagIp:" + ip);
		//首先流中获取请求体
		String reqJson = RequestUtil.receiveReq(this.getRequest());
		if(StringUtil.isNullStr(reqJson)){//请求为空，返回错误
			resultMap.put(RequestConstants.R_SUCC, Boolean.FALSE);
			resultMap.put(RequestConstants.R_CODE, RequestConstants.R_CODE_110001);
			resultMap.put(RequestConstants.R_MSG, this.getText("request.error.contentnull"));
			return SUCCESS;
		}
		//直接透传
		resultMap.put(RequestConstants.R_SUCC, Boolean.TRUE);
		resultMap.put(RequestConstants.R_CODE, RequestConstants.R_CODE_000000);
		return SUCCESS;
	}
	/**
	 * 查询用户标签
	 * @return
	 */
	public void queryTag(){
		String ip = RequestUtil.getIpAddr(this.getRequest());
		if(log.isInfoEnabled())
			log.info("queryTag Ip:" + ip);
		String userId = this.getParam("userId");
		if(log.isDebugEnabled() && RequestConstants.V_PRINT_REQUEST_ENABLE){
			log.debug("queryTag userId:" + userId);
		}
		if(StringUtil.isNullStr(userId)){
			resultMap.put(RequestConstants.R_SUCC, Boolean.FALSE);
			resultMap.put(RequestConstants.R_CODE, RequestConstants.R_CODE_110003);
			resultMap.put(RequestConstants.R_MSG, this.getText("request.error.paramnull"));
			
			this.writeTextResponse(JSON.toJSONString(resultMap), "application/json;charset=UTF-8");
			return;
		}
		long start = System.currentTimeMillis();
		long end = start;
		UserTag userTag;
		try {
//			userTag = userTagCacheManager.queryById(userId);
			userTag = userTagCacheManager.queryCutById(userId);
			if(userTag == null || userTag.getId() == null){
				userTag = null;
			}
			resultMap.put(RequestConstants.R_SUCC, Boolean.TRUE);
			resultMap.put(RequestConstants.R_CODE, RequestConstants.R_CODE_000000);
			resultMap.put(RequestConstants.R_MSG, this.getText("request.success"));
			resultMap.put(RequestConstants.R_ROOT, userTag);
			
			this.writeTextResponse(JSON.toJSONString(resultMap), "application/json;charset=UTF-8");
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			resultMap.put(RequestConstants.R_SUCC, Boolean.FALSE);
			resultMap.put(RequestConstants.R_CODE, RequestConstants.R_CODE_999999);
			resultMap.put(RequestConstants.R_MSG, this.getText("request.error.system"));
			
			this.writeTextResponse(JSON.toJSONString(resultMap), "application/json;charset=UTF-8");
		}
		end = System.currentTimeMillis();
		if(log.isDebugEnabled())
			log.debug("queryTag end,duration:" + (end - start));
		return;
	}
	/**
	 * 查询推荐列表
	 * 请求参数为一个json数据
	 * {"id":"681274129","prdType":"MIGUVIDEO","start":"0","limit":"10","ctVer":"v1.0","cats":[{"catName":"电影","score":1.03,"recommendation":[{"label":"影视1","score":100}],"items":[{"labelName":"播出年代","labelValue":"2016","score":100}]},{"catName":"电视剧","score":1.03,"recommendation":[{"label":"影视2","score":100}],"items":[{"labelName":"播出年代","labelValue":"2016","score":100}]}]}
	 * @return
	 */
	public String list() {
		String ip = RequestUtil.getIpAddr(this.getRequest());
		if(log.isInfoEnabled())
			log.info("list Ip:" + ip);
		//首先流中获取请求体
		String reqJson = RequestUtil.receiveReq(this.getRequest());
		if(log.isDebugEnabled() && RequestConstants.V_PRINT_REQUEST_ENABLE){
			log.debug("list reqJson:" + reqJson);
		}
		if(StringUtil.isNullStr(reqJson)){//请求为空，返回错误
			resultMap.put(RequestConstants.R_SUCC, Boolean.FALSE);
			resultMap.put(RequestConstants.R_CODE, RequestConstants.R_CODE_110001);
			resultMap.put(RequestConstants.R_MSG, this.getText("request.error.contentnull"));
			return SUCCESS;
		}
		//解析请求体
		UserTag reqUserTag = null;
		try {
			reqUserTag = JSON.parseObject(reqJson,UserTag.class);
		} catch (Exception e) {
		}
		//校验请求体
		if(reqUserTag == null){
			resultMap.put(RequestConstants.R_SUCC, Boolean.FALSE);
			resultMap.put(RequestConstants.R_CODE, RequestConstants.R_CODE_110002);
			resultMap.put(RequestConstants.R_MSG, this.getText("request.error.contenterror"));
			return SUCCESS;
		}
		if(StringUtil.isNullStr(reqUserTag.getPrdType()) || StringUtil.isNullStr(reqUserTag.getCtVer())){
			resultMap.put(RequestConstants.R_SUCC, Boolean.FALSE);
			resultMap.put(RequestConstants.R_CODE, RequestConstants.R_CODE_110003);
			resultMap.put(RequestConstants.R_MSG, this.getText("request.error.paramnull"));
			return SUCCESS;
		}
		PrdTypeRelation prdTypeRelation = PrdTypeRelationCache.PRDTYPE_RELATIONS.get(reqUserTag.getPrdType());
		if(prdTypeRelation == null || StringUtil.isNullStr(prdTypeRelation.getPrdInfoIds())){//搜索引擎需要产品包ID，平台需要产品
			resultMap.put(RequestConstants.R_SUCC, Boolean.FALSE);
			resultMap.put(RequestConstants.R_CODE, RequestConstants.R_CODE_120001);
			resultMap.put(RequestConstants.R_MSG, this.getText("request.error.prdtypenotfound"));
			return SUCCESS;
		}
		if(reqUserTag.getStart() == null || reqUserTag.getStart() < 0){
			reqUserTag.setStart(0);
		}
		if(reqUserTag.getLimit() == null || reqUserTag.getLimit() < 0){
			reqUserTag.setLimit(RequestConstants.V_DEFAUL_REQUEST_LIMIT);
		}
		//请求的基本参数。用户ID，搜索开始和条数
		String prdType = reqUserTag.getPrdType();
		String userId = reqUserTag.getId();//可能为空，为空，则获取默认推荐配置标签
		int start = reqUserTag.getStart();
		int limit = reqUserTag.getLimit();
		String order = reqUserTag.getOrder();
		String ctVer = reqUserTag.getCtVer();
		//初始化数据
		int recomdCatMax = RequestConstants.V_DEFAULT_RECOMD_CAT_MAX;//一级分类最多推荐个数
		int recomdCatItemMax = RequestConstants.V_DEFAULT_RECOMD_CATITEM_MAX;//一级分类标签最多推荐个数

		int searchCatMax = RequestConstants.V_DEFAULT_SEARCH_CAT_MAX;//一级分类最多搜索个数
		int searchCatPerMax = RequestConstants.V_DEFAULT_SEARCH_CAT_PER_MAX;//一级分类最多搜索次数
		int searchCatItemMax = RequestConstants.V_DEFAULT_SEARCH_CATITEM_MAX;//一级分类标签最多搜索个数
		
		int searchLimit = RequestConstants.V_DEFAULT_SEARCH_LIMIT;//正常搜索最多搜索多少条
		int searchItemLimit = RequestConstants.V_DEFAULT_SEARCH_LIMIT_CATITEM;//子项单独搜索最多搜索多少条
		
		String searchUrl = RequestConstants.V_SEARCH_URL;
		int searchMaxCount = RequestConstants.V_DEFAULT_SEARCH_COUNT_MAX;//正常搜索最多搜索多少条
		
		Double defaultScore = RequestConstants.V_DEFAULT_USERTAG_SCORE;//默认分数
		//最大的搜索个数，超过后直接返回空对象回去
		if(searchMaxCount <= start){
			resultMap.put(RequestConstants.R_SUCC, Boolean.TRUE);
			resultMap.put(RequestConstants.R_CODE, RequestConstants.R_CODE_000000);
			resultMap.put(RequestConstants.R_MSG, this.getText("request.success"));
			resultMap.put(RequestConstants.R_ROOT, new ArrayList<RecommendInfoVo>());
			resultMap.put(RequestConstants.R_TOTAL, searchMaxCount);
			return SUCCESS;
		}
		//从缓存中获取
		
		long s = System.currentTimeMillis();
		long end1 = s;
		long end2 = s;
		long end3 = s;
		long end4 = s;
		long end5 = s;
		long end6 = s;
		long end7 = s;
		long end8 = s;
		//从redis或mongo中获取该用户的标签
		UserTag dbUserTag = null;
		try {
			dbUserTag = null;
			if(!StringUtil.isNullStr(userId)){
				try {
					dbUserTag = userTagCacheManager.queryById(userId);
				} catch (Exception e) {
				}
				if(dbUserTag == null || dbUserTag.getId() == null){
					dbUserTag = null;
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
		end1 = System.currentTimeMillis();
		
		List<RecommendInfoVo> recomdList = new ArrayList<RecommendInfoVo>();
		List<RecommendInfoVo> searchList = new ArrayList<RecommendInfoVo>();
		List<RecommendInfoVo> returnList = new ArrayList<RecommendInfoVo>();
		int total = 0;
		//TODO 循环执行
		int exeCount = 0;
		while(exeCount < 2){
			try {
				//确定用户最后的标签
				if(!checkTagsNotNull(reqUserTag)){//请求没带标签
					if (dbUserTag == null){//redis里面没有用户的标签
						//如果mongo中没有用户的标签，则获取默认标签
						try {
							reqUserTag = JSON.parseObject(RequestConstants.V_DEFAULT_USERTAG,UserTag.class);
							exeCount = exeCount + 2;//已经使用了系统的默认标签，无需重复搜索
						} catch (Exception e) {
							log.error(e.getMessage(),e);
						}
						//没有任何标签
						if (!checkTagsNotNull(reqUserTag)){
							resultMap.put(RequestConstants.R_SUCC, Boolean.FALSE);
							resultMap.put(RequestConstants.R_CODE, RequestConstants.R_CODE_120002);
							resultMap.put(RequestConstants.R_MSG, this.getText("request.error.tagnull"));
							if(log.isDebugEnabled())
								log.debug("list end,duration:" + (s -end1));
							return SUCCESS;
						}
					}else{
						//如果传送过来的一级分类或者人工推荐数据都为空，则从mongo里面获取
						reqUserTag = dbUserTag;
					}
				}
				end2 = System.currentTimeMillis();
				//获取用户的标签分数
				Map<String,Double> userTagScoreMap = new HashMap<String,Double>();	//用户标签中的分数，用于填充用户请求的标签分数
				Map<String,CatInfo> dbCatInfos = new HashMap<String,CatInfo>();		//一级分类下的标签，用于填充没有携带标签的请求
				Map<String, String> catIds = EnumsInfoCache.VAL_ENUMSINFO.get(EnumsInfoCache.TYPE_CAT);
				Map<String, String> labelIds = EnumsInfoCache.VAL_ENUMSINFO.get(EnumsInfoCache.TYPE_LABEL);
				if(dbUserTag != null){
					getUserTagScore(dbUserTag,userTagScoreMap,catIds,labelIds,dbCatInfos);
				}
				end3 = System.currentTimeMillis();
				//填充分数并排序
				fillSortScore(reqUserTag,userTagScoreMap,catIds,labelIds,dbCatInfos,defaultScore);
				end4 = System.currentTimeMillis();
				//清空不需要的对象
				userTagScoreMap.clear();
				userTagScoreMap = null;
				dbCatInfos.clear();
				dbCatInfos = null;
				
				//每个一级分类都找下对应的人工推荐和搜索引擎搜索数据
				if(reqUserTag.getCats() != null && reqUserTag.getCats().size() > 0){
					int searchCatCount = 1;		//一级分类下搜索查找次数
					int recomdCatCount = 1;		//一级分类下推荐标签查找次数
					int catItemCount = 1;		//一级分类下的标签查找次数
					String recomdLabels = null;	//搜索的标签，最后搜索引擎需要使用
					//2016-12-26
					Map<String,Double> enumsConfigWeights = EnumsConfigCache.ENUMS_CONFIG;//一级、二级权重
					Map<String,Double> itemValWeights = new HashMap<String,Double>();//二级标签权重 val-weight
					Map<String,Double> catValWeights = new HashMap<String,Double>();//一级标签权重val-weight
					
					for(CatInfo catInfo : reqUserTag.getCats()){//一个一级分类查找N次
						if(StringUtil.isNullStr(catInfo.getCatId())){//如果一级标签不存在，则不查找
							continue;
						}
						//2016-12-16 查出该一级下的权重、二级权重
						catValWeights.clear();//初始化变量
						itemValWeights.clear();//初始化变量
						//2012-12-26 查询该一级权重
						Map<String,Double> catKeyWeights = findFillWeight(catInfo.getCatId(), null, "0");
						if(catKeyWeights != null && !catKeyWeights.isEmpty()){
							Map<String,String> map = new HashMap<String,String>();
							for(String val:catIds.keySet()){
								map.put(catIds.get(val), val);
							}
							for(String key:catKeyWeights.keySet()){
								catValWeights.put(map.get(key), catKeyWeights.get(key));
							}
						}
						//2012-12-26 查询二级权重
						Map<String,Double> itemKeyWeights = null;
						for(RecomdItem ri:catInfo.getRecommendation()){
							if(labelIds.keySet().contains(ri.getLabel())){
								itemKeyWeights = findFillWeight(catInfo.getCatId(), labelIds.get(ri.getLabel()), "1");
								if(itemKeyWeights != null && !itemKeyWeights.isEmpty()){
									Map<String,String> map = new HashMap<String,String>();
									for(String val:catIds.keySet()){
										map.put(catIds.get(val), val);
									}
									itemValWeights.put(ri.getLabel(), catKeyWeights.get(catInfo.getCatId()+"_"+labelIds.get(ri.getLabel())));
								}
							}else{
								itemValWeights.put(ri.getLabel(), RequestConstants.V_DEFAULT_RECOMD_ITEM_WEIGHT);
							}
						}
						
						
						if(recomdCatCount > recomdCatMax && searchCatCount > searchCatMax){//超过了人工推荐和搜索引擎最多的一级分类搜索次数
							break;
						}
						//查找内容形态
						String mediaShape = null;//多个逗号分隔
						boolean isAllMediaShape = true;
						if(catInfo.getItems() != null 
								&& searchCatCount <= searchCatMax){
							for(CatItem catItem : catInfo.getItems()){
								if(StringUtil.isNullStr(catItem.getLabelId()) 
										|| StringUtil.isNullStr(catItem.getLabelValue())){//搜索的ID必须要有，搜索的值必须要有
									continue;
								}
								if(RequestConstants.SEARCH_KEY_MEDIASHAPE.equals(catItem.getLabelId())){
									if(mediaShape == null){
										mediaShape = catItem.getLabelValue();
									}else{
										mediaShape = mediaShape + RecommendConstants.SPLIT_COMMA + catItem.getLabelValue();
									}
								}else{
									isAllMediaShape = false;
								}
							}
						}
						
						recomdLabels = null;
						boolean isSearchCat = false;
						Map<String,Double> recomdLabelsScores = new HashMap<String,Double>();//
						//搜索推荐标签，推荐标签除了人工推荐需要，搜索引擎也需要
						if(catInfo.getRecommendation() != null 
								&& (recomdCatCount <= recomdCatMax || searchCatCount <= searchCatMax)){
							catItemCount = 1;
							//2016-12-16 权重提前列出来，以供下面score调用 --推荐下的一、二级权重信息
							Map<String,Double> recomdLabelScoAndWei = new HashMap<String,Double>();//
							recomdLabelScoAndWei.put(catInfo.getCatId(), catInfo.getScore()*catValWeights.get(catInfo.getCatName()));
							Map<String,Double> searchLabelScoAndWei = new HashMap<String,Double>();
									
							for(RecomdItem recomdItem : catInfo.getRecommendation()){
								if(catItemCount > recomdCatItemMax){//每个一级分类下的推荐标签最多取多少个
									break;
								}
								if(StringUtil.isNullStr(recomdItem.getLabel())){
									continue;
								}
								//2016-12-27
								recomdLabelsScores.put(recomdItem.getLabel(), recomdItem.getScore());
								searchLabelScoAndWei.clear();
								searchLabelScoAndWei.put(catInfo.getCatId(), catInfo.getScore()*catValWeights.get(catInfo.getCatName()));
								searchLabelScoAndWei.put(recomdItem.getLabel(), 
										recomdItem.getScore()*(itemValWeights.get(recomdItem.getLabel())));
								
								if(catItemCount == 1){
									recomdLabels = recomdItem.getLabel();
									//2016-12-16
									if(itemValWeights.get(recomdLabels) != null){
										recomdLabelScoAndWei.put(recomdLabels, recomdItem.getScore()*itemValWeights.get(recomdLabels));
									}
								}else{
									//2016-12-16
									if(itemValWeights.get(recomdItem.getLabel()) != null){
										recomdLabelScoAndWei.put(recomdItem.getLabel(), recomdItem.getScore()*itemValWeights.get(recomdItem.getLabel()));
									}
									
									recomdLabels = recomdLabels + RecommendConstants.SPLIT_COMMA + recomdItem.getLabel();
								}
								//调用搜索引擎查找推荐标签(如果一级分类下的普通标签也可以带着推荐标签查询的话，这边就不用查，否则一个个推荐标签的查)
								if(searchCatCount <= searchCatMax && !RequestConstants.V_SEARCH_RECOMD_ENABLE){
									SearchRequest(searchList,searchUrl,order,RecommendConstants.S_BLANK+searchItemLimit,catInfo.getCatId(),
											prdTypeRelation.getPrdInfoIds(),ctVer,prdTypeRelation.getSearchCt(),
											RequestConstants.SEARCH_KEY_RECOMMD,recomdItem.getLabel(),mediaShape,searchLabelScoAndWei);
									isSearchCat = true;
								}
								catItemCount ++;
							}
							//调用个性化推荐平台人工推荐数据
							if(recomdCatCount <= recomdCatMax && recomdLabels != null){
								Long rstart = System.currentTimeMillis();
								//从平台搜索数据
								List<RecommendInfoVo> list = recommendInfoCacheManager.queryByLabels(recomdLabels,prdType,catInfo.getCatId(),recomdLabelScoAndWei);
								Long rend = System.currentTimeMillis();
								if(log.isDebugEnabled())
									log.debug("search from system,duration:" + (rend - rstart));
								if(list != null){//已去重
									if(log.isDebugEnabled() && RequestConstants.V_PRINT_REQUEST_ENABLE){
										log.debug("list recmdRsts:" + list.size());
									}
									recomdList.addAll(list);
									//清空不需要的对象
									list.clear();
									list = null;
								}
								recomdCatCount ++;
							}
						}
						//分类可能为空,要考虑，直接不传一级分类给搜索引擎
						if(catInfo.getItems() != null 
								&& searchCatCount <= searchCatMax){
							catItemCount = 1;						//刚开始默认第一次
							String tmpFields = null;
							String tmpKeyword = null;
							for(CatItem catItem : catInfo.getItems()){
								if(StringUtil.isNullStr(catItem.getLabelId()) 
										|| StringUtil.isNullStr(catItem.getLabelValue())){//搜索的ID必须要有，搜索的值必须要有
									continue;
								}
								if(catItemCount > searchCatItemMax){//每个一级分类最多搜索多少个标签
									break;
								}
								if(!isAllMediaShape && RequestConstants.SEARCH_KEY_MEDIASHAPE.equals(catItem.getLabelId())){//给出来的不都是内容形态，此时内容形态不单独查询
									continue;
								}
								//搜索一级分类下的某一个评分高的对象。这个默认搜索（searchCatItemMax - 1 ）次
								tmpFields = catItem.getLabelId();
								tmpKeyword = catItem.getLabelValue();
								
								if(isAllMediaShape){//全部都是内容形态，则一个个内容形态查询
									SearchRequest(searchList,searchUrl,order,RecommendConstants.S_BLANK+searchItemLimit,catInfo.getCatId(),
											prdTypeRelation.getPrdInfoIds(),ctVer,prdTypeRelation.getSearchCt(),
											tmpFields,tmpKeyword,null,null);
								}else if(!StringUtil.isNullStr(recomdLabels) && RequestConstants.V_SEARCH_RECOMD_ENABLE){//普通标签查询时附带推荐标签
									tmpFields = tmpFields + RecommendConstants.SPLIT_COMMA + RequestConstants.SEARCH_KEY_RECOMMD;
									tmpKeyword = tmpKeyword + RecommendConstants.SPLIT_COMMA + recomdLabels;
									//2016-12-27
									Map<String,Double> labelScore = new HashMap<String,Double>();				//
									labelScore.put(catInfo.getCatId(), catInfo.getScore()*catKeyWeights.get(catInfo.getCatId()));
									Double douWei = enumsConfigWeights.get(catInfo.getCatId()+"_"+RequestConstants.SEARCH_KEY_RECOMMD);
									String[] labelArray = recomdLabels.split(RecommendConstants.SPLIT_COMMA);
									for(int i=1;i<labelArray.length;i++){
										String str = labelArray[i];
										if(!"".equals(str) && recomdLabelsScores != null && !recomdLabelsScores.isEmpty()){
											labelScore.put(str, recomdLabelsScores.get(str)*douWei);
										}
									}
									
									SearchRequest(searchList,searchUrl,order,RecommendConstants.S_BLANK+searchItemLimit,catInfo.getCatId(),
											prdTypeRelation.getPrdInfoIds(),ctVer,prdTypeRelation.getSearchCt(),
											tmpFields,tmpKeyword,mediaShape,labelScore);
								}else{//普通标签查询
									SearchRequest(searchList,searchUrl,order,RecommendConstants.S_BLANK+searchItemLimit,catInfo.getCatId(),
											prdTypeRelation.getPrdInfoIds(),ctVer,prdTypeRelation.getSearchCt(),
											tmpFields,tmpKeyword,mediaShape,null);
								}
								catItemCount ++;
							}
							isSearchCat = true;
						}
						if(isSearchCat){
							searchCatCount ++;
						}
					}
				}
				end5 = System.currentTimeMillis();
			} catch (Exception e) {
				log.error(e.getMessage(),e);
			}
			//添加经过过滤去重排序的队列
			List<RecommendInfoVo> allList = new ArrayList<RecommendInfoVo>();
			allList.addAll(filterAndSort(recomdList));
			allList.addAll(filterAndSort(searchList));
			end6 = System.currentTimeMillis();
	
			if(log.isDebugEnabled() && RequestConstants.V_PRINT_REQUEST_ENABLE){
				log.debug("list recomdList:" + recomdList.size());
				log.debug("list searchList:" + searchList.size());
				log.debug("list allList:" + allList.size());
			}
			recomdList.clear();//清空不需要的对象
			recomdList = null;
			searchList.clear();
			searchList = null;
			//过滤去重
			int count = 1;
			List<RecommendInfoVo> uniqList = new ArrayList<RecommendInfoVo>();//去重后的对象
			Map<Long,Long> contIdMap = new HashMap<Long,Long>();
			for(RecommendInfoVo vo : allList){
				if(count > searchMaxCount){
					break;
				}
				if(vo.getPrdContId() == null || contIdMap.containsKey(vo.getPrdContId())){
					continue;
				}
				contIdMap.put(vo.getPrdContId(), vo.getPrdContId());
				uniqList.add(vo);
				count ++;
			}
			end7 = System.currentTimeMillis();
			
			allList.clear();//清空不需要的对象
			allList = null;
			contIdMap.clear();
			contIdMap = null;
			
			//返回最后的分页结果
			
			total = uniqList.size();
			int requestTotal = (start + limit);
			int end = requestTotal > total ? total : requestTotal;
			//自己获取最后的分页结果
			if(start < total){
				for(int i = start; i < end ; i++){
					returnList.add(uniqList.get(i));
				}
			}
			end8 = System.currentTimeMillis();
			
			uniqList.clear();//清空不需要的对象
			uniqList = null;
			
			if(exeCount == 1 && total > limit){
				total = limit;
			}
			if(total > 0 || returnList.size() > 0 || exeCount == 1){
				exeCount = exeCount + 2;
			}else{
				exeCount ++;
				try {
					reqUserTag = JSON.parseObject(RequestConstants.V_DEFAULT_USERTAG,UserTag.class);
				} catch (Exception e) {
					log.error(e.getMessage(),e);
				}
				if(reqUserTag == null){//系统默认的都为空，无需执行
					exeCount = exeCount + 2;
				}
				dbUserTag = null;
				start = 0;
				
				if(recomdList == null){
					recomdList = new ArrayList<RecommendInfoVo>();
				}
				if(searchList == null){
					searchList = new ArrayList<RecommendInfoVo>();
				}
			}
		}
		//返回结果
		resultMap.put(RequestConstants.R_SUCC, Boolean.TRUE);
		resultMap.put(RequestConstants.R_CODE, RequestConstants.R_CODE_000000);
		resultMap.put(RequestConstants.R_MSG, this.getText("request.success"));
		resultMap.put(RequestConstants.R_ROOT, returnList);
		resultMap.put(RequestConstants.R_TOTAL, total);
		if(log.isDebugEnabled())
			log.debug("list end,duration:" + (end8 -s) + "|" + (end1 - s) + "|" + (end2 - end1) + "|" + (end3 - end2) + "|" + (end4 - end3) + "|" + (end5 - end4)
				 + "|" + (end6 - end5) + "|" + (end7 - end6) + "|" + (end8 - end7));
		return SUCCESS;
	}
	/**
	 * 过滤去重，按出现次数由高到低排序
	 * @param recomdList
	 * @return
	 */
	private List<RecommendInfoVo> filterAndSort(List<RecommendInfoVo> recomdList) {
		List<RecommendInfoVo> returnList = new ArrayList<RecommendInfoVo>();
		
		Map<Integer,List<Long>> checkMap1 = new HashMap<Integer,List<Long>>();//次数里面的所有内容
		Map<Long,Integer> checkMap2 = new HashMap<Long,Integer>();//内容所在次数
		Map<Long,RecommendInfoVo> dataMap = new HashMap<Long,RecommendInfoVo>();//内容map
		
		if(recomdList != null && !recomdList.isEmpty()){
			for(RecommendInfoVo tmp : recomdList){
				Integer times = checkMap2.get(tmp.getPrdContId());
				if(times == null){//没添加过
					times = 1;
					dataMap.put(tmp.getPrdContId(), tmp);
					if(checkMap1.get(times) == null){
						checkMap1.put(times, new ArrayList<Long>());
					}
					checkMap1.get(times).add(tmp.getPrdContId());
					checkMap2.put(tmp.getPrdContId(), times);
				}else{//已经添加过
					//先从数字小的map中删除掉
					checkMap1.get(times).remove(tmp.getPrdContId());
					
					//再在高一个数字里面的map中添加
					if(checkMap1.get(times + 1) == null){
						checkMap1.put(times + 1, new ArrayList<Long>());
					}
					checkMap1.get(times + 1).add(tmp.getPrdContId());
					//最后更改节目所在数字的map
					checkMap2.put(tmp.getPrdContId(), times + 1);
				}
			}
			//查找所有的KEY，并按照次数由高到低排序
			List<Integer> keys = new ArrayList<Integer>();
			for(Integer key : checkMap1.keySet()){
				keys.add(key);
			}
			Collections.sort(keys);
			//由次数最高到底添加数据
			for(int i = keys.size() - 1; i >= 0; i--){
				List<Long> prdContIds = checkMap1.get(keys.get(i));
				for(Long prdContId : prdContIds){
					RecommendInfoVo vo = dataMap.get(prdContId);
					if(vo != null){
						returnList.add(vo);
					}
				}
			}
			keys.clear();
			keys = null;
			
		}
		checkMap1.clear();
		checkMap1 = null;
		checkMap2.clear();
		checkMap2 = null;
		dataMap.clear();
		dataMap = null;
		
		return returnList;
	}
	private void getUserTagScore(UserTag dbUserTag, Map<String, Double> userTagScoreMap, Map<String, String> catIds,
			Map<String, String> labelIds, Map<String, CatInfo> dbCatInfos) {
		if(dbUserTag.getCats() != null && dbUserTag.getCats().size() > 0){//一级分类及标签
			String catId = null;
			String labelId = null;
			for(CatInfo catInfo : dbUserTag.getCats()){
				//一级分类
				catId = null;
				if(catIds != null){
					catId = catIds.get(catInfo.getCatName());
				}
				if(catId == null){//一级标签必须存在
					continue;
				}
				dbCatInfos.put(catId, catInfo);
				
				if(catInfo.getScore() != null){
					userTagScoreMap.put(getCatMapKey(catId), catInfo.getScore());
				}
				//推荐标签
				if(catInfo.getRecommendation() != null){
					for(RecomdItem recomdItem : catInfo.getRecommendation()){
						if(recomdItem.getScore() != null){
							userTagScoreMap.put(getRecommendMapKey(catId,recomdItem.getLabel()), recomdItem.getScore());
						}
					}
				}
				//一级分类下标签
				if(catInfo.getItems() != null){
					for(CatItem catItem : catInfo.getItems()){
						labelId = null;
						if(labelIds != null){
							labelId = labelIds.get(catItem.getLabelName());
						}
						if(labelId != null && catItem.getScore() != null){
							userTagScoreMap.put(getCatLabelMapKey(catId,labelId), catInfo.getScore());
						}
					}
				}
			}
		}
	}
	/**
	 * 填充排序分数
	 * @param userTag
	 * @param userTagScoreMap
	 * @param catIds
	 * @param labelIds
	 * @param defaultScore
	 */
	private void fillSortScore(UserTag userTag, Map<String, Double> userTagScoreMap, Map<String, String> catIds,
			Map<String, String> labelIds, Map<String,CatInfo> dbCatInfos, Double defaultScore) {
		if(userTag.getCats() != null && userTag.getCats().size() > 0){
			Double score = null;
			String catId = null;
			String labelId = null;
			for(CatInfo catInfo : userTag.getCats()){
				//一级分类的分数
				score = null;
				catId = null;
				if(catIds != null){
					catId = catIds.get(catInfo.getCatName());
				}
				if(catId == null){//一级分类必须存在
					continue;
				}
				catInfo.setCatId(catId);//填充搜索引擎需要的参数
				if(catInfo.getScore() == null){
					score = userTagScoreMap.get(getCatMapKey(catId));
					if(score == null){
						catInfo.setScore(defaultScore);
					}else{
						catInfo.setScore(score);
					}
				}
				//一级分类下没有标签的，获取一级分类下默认的用户标签
				if((catInfo.getRecommendation() == null || catInfo.getRecommendation().isEmpty())
					&& (catInfo.getItems() == null || catInfo.getItems().isEmpty())){
					CatInfo dbcatInfo = dbCatInfos.get(catId);
					if(dbcatInfo != null){
						catInfo.setRecommendation(dbcatInfo.getRecommendation());
						catInfo.setItems(dbcatInfo.getItems());
					}
				}
				
				//一级分类下推荐标签分数
				if(catInfo.getRecommendation() != null){
					for(RecomdItem recomdItem : catInfo.getRecommendation()){
						score = null;
						if(recomdItem.getScore() == null){
							score = userTagScoreMap.get(getRecommendMapKey(catId,recomdItem.getLabel()));
							if(score == null){
								recomdItem.setScore(defaultScore);
							}else{
								recomdItem.setScore(score);
							}
						}
					}
					
					//排序，分从高到低
					List<RecomdItem> recommendation = catInfo.getRecommendation();
					Collections.sort(recommendation,new RecomdItemSort());
					catInfo.setRecommendation(recommendation);
				}
				//一级分类下标签分数
				if(catInfo.getItems() != null){
					for(CatItem catItem : catInfo.getItems()){
						score = null;
						labelId = null;
						if(labelIds != null){
							labelId = labelIds.get(catItem.getLabelName());
						}
						catItem.setLabelId(labelId);
						if(catItem.getScore() == null){
							if(labelId != null){
								score = userTagScoreMap.get(getCatLabelMapKey(catId,labelId));
							}
							if(score == null){
								catItem.setScore(defaultScore);
							}else{
								catItem.setScore(score);
							}
						}
					}
					
					//一级分类下的标签分数填充完后做一次排序
					List<CatItem> items = catInfo.getItems();
					Collections.sort(items,new CatItemSort());
					catInfo.setItems(items);
				}
			}
			//一级分类分数填充完后做一次排序
			List<CatInfo> items = userTag.getCats();
			Collections.sort(items,new CatInfoSort());
			userTag.setCats(items);
		}
	}
	/**
	 * 请求搜索服务器
	 * @param returnList
	 * @param searchUrl
	 * @param order
	 * @param pageSize
	 * @param contDisplayType
	 * @param packId
	 * @param ctVer
	 * @param ct
	 * @param fields
	 * @param keyword
	 * @param mediaShape 内容形态
	 */
	public void SearchRequest(List<RecommendInfoVo> returnList,String searchUrl,String order,String pageSize,
			String contDisplayType,String packId,String ctVer,String ct,String fields,String keyword,String mediaShape,Map<String,Double> score){
		if(!RequestConstants.V_DEFAULT_SEARCH_ENABLE){
			return;
		}
		//搜索一级分类下的所有
		long sstart = System.currentTimeMillis();
		long send = System.currentTimeMillis();
		try {
			SearchRequest searchRequest = new SearchRequest();
			searchRequest.setKeyword(keyword);
			searchRequest.setFields(fields);
			searchRequest.setCt(ct);
			searchRequest.setCtVer(ctVer);
			searchRequest.setPackId(packId);
			searchRequest.setContDisplayType(contDisplayType);	//一级分类
			searchRequest.setMediaShape(mediaShape);			//内容形态
			
			searchRequest.setPageSize(""+pageSize);
			searchRequest.setPageStart("0");
			searchRequest.setOrder(order);
			
			List<SearchResult> searchRsts = searchCacheManager.queryByParam(searchUrl, searchRequest);
			if(searchRsts != null){
				if(log.isDebugEnabled() && RequestConstants.V_PRINT_REQUEST_ENABLE){
					log.debug("list searchRsts:" + searchRsts.size());
				}
				for(SearchResult searchRst : searchRsts){
					if(searchRst.getContentId() != null){
						RecommendInfoVo vo = new RecommendInfoVo();
//						vo.setPrdContId(StringUtil.nullToCloneLong(searchRst.getContentId()));//1227
//						vo.setContName(contName);
						//2016-12-26 添加搜索标签分数
						for(String str:keyword.split(RecommendConstants.SPLIT_COMMA)){
							vo.setPrdContId(StringUtil.nullToCloneLong(searchRst.getContentId()));
							if(score != null && !score.isEmpty()){
								if(score.keySet().contains(str)){
									vo.setScore(score.get(contDisplayType)+score.get(str));
								}
							}
						}
						
						returnList.add(vo);
					}
				}
			}
			send = System.currentTimeMillis();
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
		if(log.isDebugEnabled())
			log.debug("search from searchEngine,duration:" + (send - sstart));
	}
	/**
	 * 获取初始化分数中推荐标签的KEY
	 * @param catId
	 * @return
	 */
	private String getRecommendMapKey(String catId, String recommendLabel) {
		return RequestConstants.MAP_KEY_PREFIX_RECOMMEND + catId + "_" + recommendLabel;
	}
	/**
	 * 获取初始化分数中一级分类下的标签KEY
	 * @param catId
	 * @return
	 */
	private String getCatLabelMapKey(String catId, String labelId) {
		return RequestConstants.MAP_KEY_PREFIX_CAT_LABEL + catId + "_" + labelId;
	}
	/**
	 * 获取初始化分数中一级分类KEY
	 * @param catId
	 * @return
	 */
	private String getCatMapKey(String catId) {
		return RequestConstants.MAP_KEY_PREFIX_CAT + catId;
	}
	/**
	 * 检查传入的用户标签是否为空
	 * @param userTag
	 * @return
	 */
	public Boolean checkTagsNotNull(UserTag userTag){
		if(userTag == null){
			return Boolean.FALSE;
		}else if(userTag.getCats() != null && userTag.getCats().size() > 0){//标签不为空
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}
	/**
	 * 初始化一级cat、二级label 权重
	 * @param catId
	 * @param itemId
	 * @param type
	 */
	private Map<String,Double> findFillWeight(String catId , String itemId ,String type){
		Map<String,Double> enumsConfigWeights = EnumsConfigCache.ENUMS_CONFIG;
		Map<String,Double> weights = new HashMap<String,Double>();
		//查一级下的权重
		if(enumsConfigWeights != null && !enumsConfigWeights.isEmpty() && weights != null){
			if("0".equals(type) && catId != null){
				for(String key:enumsConfigWeights.keySet()){
					if(catId.equals(key)){
						weights.put(key, enumsConfigWeights.get(key));
						break;
					}
				}
				if(weights.isEmpty()){
					weights.put(catId, RequestConstants.V_DEFAULT_RECOMD_CAT_WEIGHT);
				}
			}
			if("1".equals(type) && catId != null && itemId != null){
				for(String key:enumsConfigWeights.keySet()){
					if((catId+"_"+itemId).equals(key)){
						weights.put(key, enumsConfigWeights.get(key));
						break;
					}
				}
				if(weights.isEmpty()){
					weights.put(catId+"_"+itemId, RequestConstants.V_DEFAULT_RECOMD_ITEM_WEIGHT);
				}
			}
			
		}
		return weights;
	}
	
	public RecommendInfoCacheManager getRecommendInfoCacheManager() {
		return recommendInfoCacheManager;
	}
	public void setRecommendInfoCacheManager(RecommendInfoCacheManager recommendInfoCacheManager) {
		this.recommendInfoCacheManager = recommendInfoCacheManager;
	}

	public UserTagCacheManager getUserTagCacheManager() {
		return userTagCacheManager;
	}

	public void setUserTagCacheManager(UserTagCacheManager userTagCacheManager) {
		this.userTagCacheManager = userTagCacheManager;
	}

	public SearchCacheManager getSearchCacheManager() {
		return searchCacheManager;
	}

	public void setSearchCacheManager(SearchCacheManager searchCacheManager) {
		this.searchCacheManager = searchCacheManager;
	}
	public VomsRecommendCacheManager getVomsRecommendCacheManager() {
		return vomsRecommendCacheManager;
	}
	public void setVomsRecommendCacheManager(VomsRecommendCacheManager vomsRecommendCacheManager) {
		this.vomsRecommendCacheManager = vomsRecommendCacheManager;
	}
	public TopRecommendCacheManager getTopRecommendCacheManager() {
		return topRecommendCacheManager;
	}
	public void setTopRecommendCacheManager(TopRecommendCacheManager topRecommendCacheManager) {
		this.topRecommendCacheManager = topRecommendCacheManager;
	}
}

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
import com.wondertek.mobilevideo.core.recommend.model.EnumsInfo;
import com.wondertek.mobilevideo.core.recommend.model.PrdTypeRelation;
import com.wondertek.mobilevideo.core.recommend.search.SearchRequest;
import com.wondertek.mobilevideo.core.recommend.search.SearchResult;
import com.wondertek.mobilevideo.core.recommend.util.CatInfoSort;
import com.wondertek.mobilevideo.core.recommend.util.CatItemSort;
import com.wondertek.mobilevideo.core.recommend.util.RecomdItemSort;
import com.wondertek.mobilevideo.core.recommend.util.RecommendConstants;
import com.wondertek.mobilevideo.core.recommend.util.RecommendInfoVoSort;
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
	 * 每次返回的记录数不一定是要求的记录数
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
			resultMap.put(RequestConstants.R_MSG, "请求内容为空");
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
			resultMap.put(RequestConstants.R_MSG, "请求体与要求不符");
			return SUCCESS;
		}
		if(StringUtil.isNullStr(reqUserTag.getPrdType()) || StringUtil.isNullStr(reqUserTag.getCtVer())){
			resultMap.put(RequestConstants.R_SUCC, Boolean.FALSE);
			resultMap.put(RequestConstants.R_CODE, RequestConstants.R_CODE_110003);
			resultMap.put(RequestConstants.R_MSG, "必填参数为空");
			return SUCCESS;
		}
		PrdTypeRelation prdTypeRelation = PrdTypeRelationCache.PRDTYPE_RELATIONS.get(reqUserTag.getPrdType());
		if(prdTypeRelation == null || StringUtil.isNullStr(prdTypeRelation.getPrdInfoIds())){//搜索引擎需要产品包ID，平台需要产品
			resultMap.put(RequestConstants.R_SUCC, Boolean.FALSE);
			resultMap.put(RequestConstants.R_CODE, RequestConstants.R_CODE_120001);
			resultMap.put(RequestConstants.R_MSG, "未找到匹配的产品");
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
		//voms推荐标签
		String vomsLabel = null;
		if(!StringUtil.isNullStr(reqUserTag.getVomsLabel())){
			vomsLabel = reqUserTag.getVomsLabel();
		}
		//当前页数
		int page = (start / limit) + 1;
		//初始化数据
		int recomdCatMax = RequestConstants.V_DEFAULT_RECOMD_CAT_MAX;//一级分类最多推荐个数
		int recomdCatItemMax = RequestConstants.V_DEFAULT_RECOMD_CATITEM_MAX;//一级分类标签最多推荐个数
		int searchCatMax = RequestConstants.V_DEFAULT_SEARCH_CAT_MAX;//一级分类最多搜索个数
		int searchCatItemMax = RequestConstants.V_DEFAULT_SEARCH_CATITEM_MAX;//一级分类标签最多搜索个数
		int searchLimit = RequestConstants.V_DEFAULT_SEARCH_LIMIT;//调用搜索引擎时，查询一次一级分类最多查询多少条记录
		String searchUrl = RequestConstants.V_SEARCH_URL;
		
		Double defaultScore = RequestConstants.V_DEFAULT_USERTAG_SCORE;//默认分数
		
		Double specialTopicRatio = RequestConstants.V_DEFAULT_RECOMD_SPECIALTOPIC_RATIO;
		Double combinedContRatio = RequestConstants.V_DEFAULT_RECOMD_COMBINEDCONT_RATIO;
		Double bigPicContRatio = RequestConstants.V_DEFAULT_RECOMD_BIGPICCONT_RATIO;
		Double multiPicContRatio = RequestConstants.V_DEFAULT_RECOMD_MULTIPICCONT_RATIO;
		
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
							resultMap.put(RequestConstants.R_MSG, "未找到任何匹配的用户标签");
							if(log.isDebugEnabled())
								log.debug("searchAllList end,duration:" + (s -end1));
							return SUCCESS;
						}
					}else{
						//如果传送过来的一级分类或者人工推荐数据都为空，则从mongo里面获取
						reqUserTag = dbUserTag;
					}
				}
				//门户提供过来的voms推荐标签
				if(vomsLabel == null){
					vomsLabel = reqUserTag.getVomsLabel();
				}
				end2 = System.currentTimeMillis();
				//获取用户的标签分数
				Map<String,Double> userTagScoreMap = new HashMap<String,Double>();	//用户标签中的分数，用于填充用户请求的标签分数
				Map<String,CatInfo> dbCatInfos = new HashMap<String,CatInfo>();		//一级分类下的标签，用于填充没有携带标签的请求
				Map<String, EnumsInfo> catIds = EnumsInfoCache.VAL_ENUMSINFO.get(EnumsInfoCache.TYPE_CAT);
				Map<String, EnumsInfo> labelIds = EnumsInfoCache.VAL_ENUMSINFO.get(EnumsInfoCache.TYPE_LABEL);
				//获取内容形态和推荐标签的字段描述
				String contRecommKeyDesc = "contRecomm";
//				String mediaShapeKeyDesc = "mediaShape";
				if(EnumsInfoCache.KEY_ENUMSINFO.containsKey(EnumsInfoCache.TYPE_LABEL)){
					Map<String, EnumsInfo> labelKeys = EnumsInfoCache.KEY_ENUMSINFO.get(EnumsInfoCache.TYPE_LABEL);
					if(labelKeys.containsKey(RequestConstants.SEARCH_KEY_RECOMMD)){
						contRecommKeyDesc = labelKeys.get(RequestConstants.SEARCH_KEY_RECOMMD).getKeyDesc();
					}
//					if(labelKeys.containsKey(RequestConstants.SEARCH_KEY_MEDIASHAPE)){
//						mediaShapeKeyDesc = labelKeys.get(RequestConstants.SEARCH_KEY_MEDIASHAPE).getKeyDesc();
//					}
				}
				//获取用户的标签评分
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
					Double catWeight = null;	//一级分类权重
					Double itemWeight = null;	//其他权重
					
					//查看总共需要调用搜索多少次，也就是能搜索几个一级分类，算出来每页该查询多少，从第几页开始查询
					int catSearchCount = 0;//能搜索几个一级分类
					for(CatInfo catInfo : reqUserTag.getCats()){//一个一级分类查找N次
						if(StringUtil.isNullStr(catInfo.getCatId())){//如果一级标签不存在，则不查找
							continue;
						}
						if(catSearchCount >= searchCatMax){//超过了搜索引擎最多的一级分类搜索次数
							break;
						}
						catSearchCount ++;
					}
					if(catSearchCount == 0) {
						catSearchCount = 1;
					}
					int pageSize = limit / catSearchCount;
					if((limit % catSearchCount) > 0){
						pageSize = pageSize + 1;
					}
					if(pageSize > searchLimit){
						pageSize = searchLimit;
					}
					int pageStart = pageSize * (page - 1);
					if(page > 1){//第二页都不查询人工推荐的数据
						recomdCatCount = recomdCatMax + 1;
					}
					//一个个一级分类查询
					for(CatInfo catInfo : reqUserTag.getCats()){//一个一级分类查找N次
						if(StringUtil.isNullStr(catInfo.getCatId())){//如果一级标签不存在，则不查找
							continue;
						}
						if(recomdCatCount > recomdCatMax && searchCatCount > searchCatMax){//超过了人工推荐和搜索引擎最多的一级分类搜索次数
							break;
						}
						//一级分类权重
						catWeight = findFillWeight(catInfo.getCatId(),null,RequestConstants.V_DEFAULT_RECOMD_TAG_CAT_WEIGHT);
						catWeight = catWeight * catInfo.getScore();
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
						List<String> fieldList = new ArrayList<String>();//查询字段
						StringBuffer searchKeywords = new StringBuffer();	//关键字
						//搜索推荐标签，推荐标签除了人工推荐需要，搜索引擎也需要
						if(catInfo.getRecommendation() != null 
								&& (recomdCatCount <= recomdCatMax || searchCatCount <= searchCatMax)){
							catItemCount = 1;
							
							//权重提前列出来，以供下面score调用 --推荐下的一、二级权重信息
							Map<String,Double> recomdLabelScoAndWei = new HashMap<String,Double>();//
							recomdLabelScoAndWei.put(catInfo.getCatId(), catWeight);
							for(RecomdItem recomdItem : catInfo.getRecommendation()){
								if(catItemCount > recomdCatItemMax){//每个一级分类下的推荐标签最多取多少个
									break;
								}
								if(StringUtil.isNullStr(recomdItem.getLabel())){
									continue;
								}
								//推荐标签权重
								itemWeight = findFillWeight(catInfo.getCatId(),RequestConstants.SEARCH_KEY_RECOMMD,RequestConstants.V_DEFAULT_RECOMD_TAG_RCMDITEM_WEIGHT);
								itemWeight = itemWeight * recomdItem.getScore();
								
								recomdLabelScoAndWei.put(recomdItem.getLabel(), itemWeight);
								
								if(catItemCount == 1){
									recomdLabels = recomdItem.getLabel();
								}else{
									recomdLabels = recomdLabels + RecommendConstants.SPLIT_COMMA + recomdItem.getLabel();
								}
								//添加搜索关键字
								if(searchCatCount <= searchCatMax){
									if(!fieldList.contains(RequestConstants.SEARCH_KEY_RECOMMD)){
										fieldList.add(RequestConstants.SEARCH_KEY_RECOMMD);
									}
									if(searchKeywords.length() == 0){
										searchKeywords.append("(");
									}else{
										searchKeywords.append(" OR ");
									}
									searchKeywords.append(contRecommKeyDesc).append(":").append("(").append(recomdItem.getLabel());
									if(RequestConstants.V_DEFAULT_SEARCH_WITH_WEIGHT){
										searchKeywords.append("^").append(itemWeight.longValue());
									}
									searchKeywords.append(")");
									isSearchCat = true;
								}
								catItemCount ++;
							}
							if(searchKeywords.length() > 0){
								searchKeywords.append(")");
							}
							
							if(recomdLabels != null && !isInitVomsLabel){
								vomsLabelInfo.append(recomdLabels).append(RecommendConstants.SPLIT_COMMA);
							}
							//调用个性化推荐平台人工推荐数据
							if(recomdCatCount <= recomdCatMax && recomdLabels != null){
								Long rstart = System.currentTimeMillis();
								//从平台搜索数据//2016
								List<RecommendInfoVo> list = recommendInfoCacheManager.queryByLabels(recomdLabels,prdType,catInfo.getCatId(),recomdLabelScoAndWei);
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
								//普通标签权重
								itemWeight = findFillWeight(catInfo.getCatId(),catItem.getLabelId(),RequestConstants.V_DEFAULT_RECOMD_TAG_ITEM_WEIGHT);
								itemWeight = itemWeight * catItem.getScore();
								//添加关键字
								if(!fieldList.contains(catItem.getLabelId())){
									fieldList.add(catItem.getLabelId());
								}
								if(searchKeywords.length() > 0){
									searchKeywords.append(" OR ");
								}
								searchKeywords.append(catItem.getLabelKey()).append(":").append("(").append(catItem.getLabelValue());
								if(RequestConstants.V_DEFAULT_SEARCH_WITH_WEIGHT){
									searchKeywords.append("^").append(itemWeight.longValue());
								}
								searchKeywords.append(")");

								catItemCount ++;
							}
							isSearchCat = true;
						}
						//只调用一次
						if(isAllMediaShape){//全部都是内容形态，则一个个内容形态查询
							SearchRequest(searchList,searchUrl,order,RecommendConstants.S_BLANK+pageStart, RecommendConstants.S_BLANK+pageSize,catInfo.getCatId(),
									prdTypeRelation.getPrdInfoIds(),ctVer,prdTypeRelation.getSearchCt(),
									RequestConstants.SEARCH_KEY_MEDIASHAPE,(mediaShape == null ? null : mediaShape.replaceAll(",", " ")),null,catWeight);
						}else{//普通标签查询
							String fields = fieldList.toString().replace("[", "").replace("]", "").replaceAll(" ", "");//逗号分隔
							SearchRequest(searchList,searchUrl,order,RecommendConstants.S_BLANK+pageStart, RecommendConstants.S_BLANK+pageSize,catInfo.getCatId(),
									prdTypeRelation.getPrdInfoIds(),ctVer,prdTypeRelation.getSearchCt(),
									fields,searchKeywords.toString(),mediaShape,catWeight);
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
			Map<Long,Long> contIdMap = new HashMap<Long,Long>();
			for(RecommendInfoVo vo : allList){
				if(vo.getPrdContId() == null || contIdMap.containsKey(vo.getPrdContId())){
					continue;
				}
				contIdMap.put(vo.getPrdContId(), vo.getPrdContId());
				pomsContList.add(vo);
			}
			if(log.isDebugEnabled() && RequestConstants.V_PRINT_REQUEST_ENABLE){
				log.debug("searchAllList uniqList:" + pomsContList.size());
			}
			
			end7 = System.currentTimeMillis();
			
			allList.clear();//清空不需要的对象
			allList = null;
			contIdMap.clear();
			contIdMap = null;
			
			//返回最后的分页结果
			total = pomsContList.size();
			if(total > 0){
				total = 100000;
			}
			end8 = System.currentTimeMillis();
			
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
			}
		}
		//2 查询voms数据
		List<String> types = new ArrayList<String>();
		//优先取推荐标签
		String labelInfo = vomsLabel;
		if(StringUtil.isNullStr(labelInfo)){
			labelInfo = vomsLabelInfo.toString();
		}
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
		resultMap.put(RequestConstants.R_MSG, "请求成功");
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
			resultMap.put(RequestConstants.R_MSG, "必填参数为空");
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
			resultMap.put(RequestConstants.R_MSG, "请求成功");
			resultMap.put(RequestConstants.R_ROOT, returnList);
			resultMap.put(RequestConstants.R_TOTAL, total);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			resultMap.put(RequestConstants.R_SUCC, Boolean.FALSE);
			resultMap.put(RequestConstants.R_CODE, RequestConstants.R_CODE_999999);
			resultMap.put(RequestConstants.R_MSG, "系统异常");
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
			resultMap.put(RequestConstants.R_MSG, "必填参数为空");
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
			resultMap.put(RequestConstants.R_MSG, "请求成功");
			resultMap.put(RequestConstants.R_ROOT, returnList);
			resultMap.put(RequestConstants.R_TOTAL, total);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			resultMap.put(RequestConstants.R_SUCC, Boolean.FALSE);
			resultMap.put(RequestConstants.R_CODE, RequestConstants.R_CODE_999999);
			resultMap.put(RequestConstants.R_MSG, "系统异常");
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
			resultMap.put(RequestConstants.R_MSG, "请求内容为空");
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
			resultMap.put(RequestConstants.R_MSG, "请求内容为空");
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
			resultMap.put(RequestConstants.R_MSG, "必填参数为空");
			
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
			resultMap.put(RequestConstants.R_MSG, "请求成功");
			resultMap.put(RequestConstants.R_ROOT, userTag);
			
			this.writeTextResponse(JSON.toJSONString(resultMap), "application/json;charset=UTF-8");
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			resultMap.put(RequestConstants.R_SUCC, Boolean.FALSE);
			resultMap.put(RequestConstants.R_CODE, RequestConstants.R_CODE_999999);
			resultMap.put(RequestConstants.R_MSG, "系统异常");
			
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
			resultMap.put(RequestConstants.R_MSG, "请求内容为空");
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
			resultMap.put(RequestConstants.R_MSG, "请求体与要求不符");
			return SUCCESS;
		}
		if(StringUtil.isNullStr(reqUserTag.getPrdType()) || StringUtil.isNullStr(reqUserTag.getCtVer())){
			resultMap.put(RequestConstants.R_SUCC, Boolean.FALSE);
			resultMap.put(RequestConstants.R_CODE, RequestConstants.R_CODE_110003);
			resultMap.put(RequestConstants.R_MSG, "必填参数为空");
			return SUCCESS;
		}
		PrdTypeRelation prdTypeRelation = PrdTypeRelationCache.PRDTYPE_RELATIONS.get(reqUserTag.getPrdType());
		if(prdTypeRelation == null || StringUtil.isNullStr(prdTypeRelation.getPrdInfoIds())){//搜索引擎需要产品包ID，平台需要产品
			resultMap.put(RequestConstants.R_SUCC, Boolean.FALSE);
			resultMap.put(RequestConstants.R_CODE, RequestConstants.R_CODE_120001);
			resultMap.put(RequestConstants.R_MSG, "未找到匹配的产品");
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
		int searchCatItemMax = RequestConstants.V_DEFAULT_SEARCH_CATITEM_MAX;//一级分类标签最多搜索个数
		
		int searchLimit = RequestConstants.V_DEFAULT_SEARCH_LIMIT;//正常搜索最多搜索多少条
		
		String searchUrl = RequestConstants.V_SEARCH_URL;
		
		Double defaultScore = RequestConstants.V_DEFAULT_USERTAG_SCORE;//默认分数
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
		
		List<RecommendInfoVo> pomsContList = new ArrayList<RecommendInfoVo>();

		int total = 0;
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
							resultMap.put(RequestConstants.R_MSG, "未找到任何匹配的用户标签");
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
				Map<String, EnumsInfo> catIds = EnumsInfoCache.VAL_ENUMSINFO.get(EnumsInfoCache.TYPE_CAT);
				Map<String, EnumsInfo> labelIds = EnumsInfoCache.VAL_ENUMSINFO.get(EnumsInfoCache.TYPE_LABEL);
				//获取内容形态和推荐标签的字段描述
				String contRecommKeyDesc = "contRecomm";
//				String mediaShapeKeyDesc = "mediaShape";
				if(EnumsInfoCache.KEY_ENUMSINFO.containsKey(EnumsInfoCache.TYPE_LABEL)){
					Map<String, EnumsInfo> labelKeys = EnumsInfoCache.KEY_ENUMSINFO.get(EnumsInfoCache.TYPE_LABEL);
					if(labelKeys.containsKey(RequestConstants.SEARCH_KEY_RECOMMD)){
						contRecommKeyDesc = labelKeys.get(RequestConstants.SEARCH_KEY_RECOMMD).getKeyDesc();
					}
//					if(labelKeys.containsKey(RequestConstants.SEARCH_KEY_MEDIASHAPE)){
//						mediaShapeKeyDesc = labelKeys.get(RequestConstants.SEARCH_KEY_MEDIASHAPE).getKeyDesc();
//					}
				}
				//获取用户的标签评分
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
					Double catWeight = null;	//一级分类权重
					Double itemWeight = null;	//其他权重
					//查看总共需要调用搜索多少次，也就是能搜索几个一级分类，算出来每页该查询多少，从第几页开始查询
					int catSearchCount = 0;//能搜索几个一级分类
					for(CatInfo catInfo : reqUserTag.getCats()){//一个一级分类查找N次
						if(StringUtil.isNullStr(catInfo.getCatId())){//如果一级标签不存在，则不查找
							continue;
						}
						if(catSearchCount >= searchCatMax){//超过了搜索引擎最多的一级分类搜索次数
							break;
						}
						catSearchCount ++;
					}
					if(catSearchCount == 0) {
						catSearchCount = 1;
					}
					int pageSize = limit / catSearchCount;
					if(pageSize > searchLimit){
						pageSize = searchLimit;
					}
					int pageStart = pageSize * (page - 1);
					if(page > 1){//第二页都不查询人工推荐的数据
						recomdCatCount = recomdCatMax + 1;
					}
					//一个个一级分类查询
					for(CatInfo catInfo : reqUserTag.getCats()){//一个一级分类查找N次
						if(StringUtil.isNullStr(catInfo.getCatId())){//如果一级标签不存在，则不查找
							continue;
						}
						if(recomdCatCount > recomdCatMax && searchCatCount > searchCatMax){//超过了人工推荐和搜索引擎最多的一级分类搜索次数
							break;
						}
						//一级分类权重
						catWeight = findFillWeight(catInfo.getCatId(),null,RequestConstants.V_DEFAULT_RECOMD_TAG_CAT_WEIGHT);
						catWeight = catWeight * catInfo.getScore();
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
						List<String> fieldList = new ArrayList<String>();//查询字段
						StringBuffer searchKeywords = new StringBuffer();	//关键字
						//搜索推荐标签，推荐标签除了人工推荐需要，搜索引擎也需要
						if(catInfo.getRecommendation() != null 
								&& (recomdCatCount <= recomdCatMax || searchCatCount <= searchCatMax)){
							catItemCount = 1;
							
							//权重提前列出来，以供下面score调用 --推荐下的一、二级权重信息
							Map<String,Double> recomdLabelScoAndWei = new HashMap<String,Double>();//
							recomdLabelScoAndWei.put(catInfo.getCatId(), catWeight);
							for(RecomdItem recomdItem : catInfo.getRecommendation()){
								if(catItemCount > recomdCatItemMax){//每个一级分类下的推荐标签最多取多少个
									break;
								}
								if(StringUtil.isNullStr(recomdItem.getLabel())){
									continue;
								}
								//推荐标签权重
								itemWeight = findFillWeight(catInfo.getCatId(),RequestConstants.SEARCH_KEY_RECOMMD,RequestConstants.V_DEFAULT_RECOMD_TAG_RCMDITEM_WEIGHT);
								itemWeight = itemWeight * recomdItem.getScore();
								
								recomdLabelScoAndWei.put(recomdItem.getLabel(), itemWeight);
								
								if(catItemCount == 1){
									recomdLabels = recomdItem.getLabel();
								}else{
									recomdLabels = recomdLabels + RecommendConstants.SPLIT_COMMA + recomdItem.getLabel();
								}
								//添加搜索关键字
								if(searchCatCount <= searchCatMax){
									if(!fieldList.contains(RequestConstants.SEARCH_KEY_RECOMMD)){
										fieldList.add(RequestConstants.SEARCH_KEY_RECOMMD);
									}
									if(searchKeywords.length() == 0){
										searchKeywords.append("(");
									}else{
										searchKeywords.append(" OR ");
									}
									searchKeywords.append(contRecommKeyDesc).append(":").append(recomdItem.getLabel())/*.append("^").append(itemWeight.longValue())*/;
									isSearchCat = true;
								}
								catItemCount ++;
							}
							if(searchKeywords.length() > 0){
								searchKeywords.append(")");
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
								//普通标签权重
								itemWeight = findFillWeight(catInfo.getCatId(),catItem.getLabelId(),RequestConstants.V_DEFAULT_RECOMD_TAG_ITEM_WEIGHT);
								itemWeight = itemWeight * catItem.getScore();
								//添加关键字
								if(!fieldList.contains(catItem.getLabelId())){
									fieldList.add(catItem.getLabelId());
								}
								if(searchKeywords.length() > 0){
									searchKeywords.append(" OR ");
								}
								searchKeywords.append(catItem.getLabelKey()).append(":").append(catItem.getLabelValue())/*.append("^").append(itemWeight.longValue())*/;

								catItemCount ++;
							}
							isSearchCat = true;
						}
						//只调用一次
						if(isAllMediaShape){//全部都是内容形态，则一个个内容形态查询
							SearchRequest(searchList,searchUrl,order,RecommendConstants.S_BLANK+pageStart,RecommendConstants.S_BLANK+pageSize,catInfo.getCatId(),
									prdTypeRelation.getPrdInfoIds(),ctVer,prdTypeRelation.getSearchCt(),
									RequestConstants.SEARCH_KEY_MEDIASHAPE,(mediaShape == null ? null : mediaShape.replaceAll(",", " ")),null,catWeight);
						}else{//普通标签查询
							String fields = fieldList.toString().replace("[", "").replace("]", "").replaceAll(" ", "");//逗号分隔
							SearchRequest(searchList,searchUrl,order,RecommendConstants.S_BLANK+pageStart, RecommendConstants.S_BLANK+pageSize,catInfo.getCatId(),
									prdTypeRelation.getPrdInfoIds(),ctVer,prdTypeRelation.getSearchCt(),
									fields,searchKeywords.toString(),mediaShape,catWeight);
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
			Map<Long,Long> contIdMap = new HashMap<Long,Long>();
			for(RecommendInfoVo vo : allList){
				if(vo.getPrdContId() == null || contIdMap.containsKey(vo.getPrdContId())){
					continue;
				}
				contIdMap.put(vo.getPrdContId(), vo.getPrdContId());
				pomsContList.add(vo);
			}
			if(log.isDebugEnabled() && RequestConstants.V_PRINT_REQUEST_ENABLE){
				log.debug("searchAllList uniqList:" + pomsContList.size());
			}
			
			end7 = System.currentTimeMillis();
			
			allList.clear();//清空不需要的对象
			allList = null;
			contIdMap.clear();
			contIdMap = null;
			
			//返回最后的分页结果
			total = pomsContList.size();
			if(total > 0){
				total = 100000;
			}
			end8 = System.currentTimeMillis();
			
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
			}
		}
		//返回结果
		resultMap.put(RequestConstants.R_SUCC, Boolean.TRUE);
		resultMap.put(RequestConstants.R_CODE, RequestConstants.R_CODE_000000);
		resultMap.put(RequestConstants.R_MSG, "请求成功");
		resultMap.put(RequestConstants.R_ROOT, pomsContList);
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
		//去重
        List<RecommendInfoVo> rst = new ArrayList<RecommendInfoVo>();
        Map<Long,RecommendInfoVo> contIdMap = new HashMap<Long,RecommendInfoVo>();
        for(RecommendInfoVo recommendInfoVo : recomdList){
        	if(!contIdMap.containsKey(recommendInfoVo.getPrdContId())){
        		contIdMap.put(recommendInfoVo.getPrdContId(), recommendInfoVo);
        	}else{
        		contIdMap.get(recommendInfoVo.getPrdContId()).setScore(recommendInfoVo.getScore() + contIdMap.get(recommendInfoVo.getPrdContId()).getScore());
        	}
        }
        rst.addAll(contIdMap.values());
        contIdMap.clear();
        contIdMap = null;
        //排序
        Collections.sort(rst,new RecommendInfoVoSort());
        
		return rst;
	}
	/**
	 * 获取用户标签里面的默认分数
	 */
	private void getUserTagScore(UserTag dbUserTag, Map<String, Double> userTagScoreMap, Map<String, EnumsInfo> catIds,
			Map<String, EnumsInfo> labelIds, Map<String, CatInfo> dbCatInfos) {
		if(dbUserTag.getCats() != null && dbUserTag.getCats().size() > 0){//一级分类及标签
			String catId = null;
			String labelId = null;
			EnumsInfo enumsInfo = null;
			for(CatInfo catInfo : dbUserTag.getCats()){
				//一级分类
				catId = null;
				enumsInfo = null;
				if(catIds != null){
					enumsInfo = catIds.get(catInfo.getCatName());
				}
				if(enumsInfo == null){//一级标签必须存在
					continue;
				}
				catId = enumsInfo.getKey();
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
						enumsInfo = null;
						if(labelIds != null){
							enumsInfo = labelIds.get(catItem.getLabelName());
							if(enumsInfo != null){
								labelId = enumsInfo.getKey();
							}
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
	 */
	private void fillSortScore(UserTag userTag, Map<String, Double> userTagScoreMap, Map<String, EnumsInfo> catIds,
			Map<String, EnumsInfo> labelIds, Map<String,CatInfo> dbCatInfos, Double defaultScore) {
		if(userTag.getCats() != null && userTag.getCats().size() > 0){
			Double score = null;
			String catId = null;
			String labelId = null;
			EnumsInfo enumsInfo = null;
			for(CatInfo catInfo : userTag.getCats()){
				//一级分类的分数
				score = null;
				enumsInfo = null;
				if(catIds != null){
					enumsInfo = catIds.get(catInfo.getCatName());
				}
				if(enumsInfo == null){//一级分类必须存在
					continue;
				}
				catId = enumsInfo.getKey();
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
						enumsInfo = null;
						if(labelIds != null){
							enumsInfo = labelIds.get(catItem.getLabelName());
						}
						if(enumsInfo != null){
							labelId = enumsInfo.getKey();
							catItem.setLabelId(labelId);
							catItem.setLabelKey(enumsInfo.getKeyDesc());
						}
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
	public void SearchRequest(List<RecommendInfoVo> returnList,String searchUrl,String order,String pageStart,String pageSize,
			String contDisplayType,String packId,String ctVer,String ct,String fields,String keyword,String mediaShape,Double score){
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
			
			searchRequest.setPageSize(pageSize);
			searchRequest.setPageStart(pageStart);
			searchRequest.setOrder(order);
			
			List<SearchResult> searchRsts = searchCacheManager.queryByParam(searchUrl, searchRequest);
			if(searchRsts != null){
				if(log.isDebugEnabled() && RequestConstants.V_PRINT_REQUEST_ENABLE){
					log.debug("list searchRsts:" + searchRsts.size());
				}
				for(SearchResult searchRst : searchRsts){
					if(searchRst.getContentId() != null){
						RecommendInfoVo vo = new RecommendInfoVo();
						vo.setPrdContId(StringUtil.nullToCloneLong(searchRst.getContentId()));
						vo.setContName(searchRst.getContName());
						vo.setScore((searchRst.getScore() != null && searchRst.getScore() != 0) ? (score * searchRst.getScore()) : score);
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
	private Double findFillWeight(String catId , String itemId ,Double defaultWeight){
		Double weight = null;
		if(itemId == null){
			weight = EnumsConfigCache.ENUMS_CONFIG.get(catId);
		}else{
			weight = EnumsConfigCache.ENUMS_CONFIG.get(catId + "_" + itemId);
		}
		if(weight == null){
			weight = defaultWeight;
		}
		return weight;
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

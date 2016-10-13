package com.wondertek.mobilevideo.recommend.webapp.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.wondertek.mobilevideo.core.recommend.cache.EnumsInfoCache;
import com.wondertek.mobilevideo.core.recommend.cache.PrdTypeRelationCache;
import com.wondertek.mobilevideo.core.recommend.cache.redis.service.RecommendInfoCacheManager;
import com.wondertek.mobilevideo.core.recommend.cache.redis.service.SearchCacheManager;
import com.wondertek.mobilevideo.core.recommend.cache.redis.service.UserTagCacheManager;
import com.wondertek.mobilevideo.core.recommend.model.PrdTypeRelation;
import com.wondertek.mobilevideo.core.recommend.search.SearchRequest;
import com.wondertek.mobilevideo.core.recommend.search.SearchResult;
import com.wondertek.mobilevideo.core.recommend.util.CatInfoSort;
import com.wondertek.mobilevideo.core.recommend.util.CatItemSort;
import com.wondertek.mobilevideo.core.recommend.util.RecomdItemSort;
import com.wondertek.mobilevideo.core.recommend.util.RecommendConstants;
import com.wondertek.mobilevideo.core.recommend.util.RequestConstants;
import com.wondertek.mobilevideo.core.recommend.util.RequestUtil;
import com.wondertek.mobilevideo.core.recommend.util.ST;
import com.wondertek.mobilevideo.core.recommend.vo.RecommendInfoVo;
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
	public String queryTag(){
		String ip = RequestUtil.getIpAddr(this.getRequest());
		if(log.isInfoEnabled())
			log.info("queryTagIp:" + ip);
		String userId = this.getParam("userId");
		if(StringUtil.isNullStr(userId)){
			resultMap.put(RequestConstants.R_SUCC, Boolean.FALSE);
			resultMap.put(RequestConstants.R_CODE, RequestConstants.R_CODE_110003);
			resultMap.put(RequestConstants.R_MSG, this.getText("request.error.paramnull"));
			
			this.writeTextResponse(JSON.toJSONString(resultMap), "application/json;charset=UTF-8");
			return SUCCESS;
		}
		long start = System.currentTimeMillis();
		long end = start;
		UserTag userTag;
		try {
			userTag = userTagCacheManager.queryById(userId);
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
		return SUCCESS;
	}
	/**
	 * 查询推荐列表
	 * 请求参数为一个json数据
	 * {"_id":"100033899","prdType":"MIGUSHIPIN","cats":[{"catName":"电视剧","items":[{"labelName":"播出年代","labelValue":"2015"},{"labelName":"首播时间","labelValue":"2015-05-03"},{"labelName":"内容类型","labelValue":"都市"},{"labelName":"内容类型","labelValue":"言情"}]},{"catName":"电影","items":[{"labelName":"内容类型","labelValue":"冒险"}]},{"catName":"原创","items":[{"labelName":"国家及地区","labelValue":"内地"},{"labelName":"播出年代","labelValue":"2016"},{"labelName":"内容形态","labelValue":"全片"}]}],"recommendation":{"items":[{"label":"影视资讯"},{"label":"社会"}]},"keywords":{"items":[{"keyword":" 保健秘籍"},{"keyword":" 时尚女性"}]}}
	 * @return
	 */
	public String list() {
		String ip = RequestUtil.getIpAddr(this.getRequest());
		if(log.isInfoEnabled())
			log.info("listIp:" + ip);
		//首先流中获取请求体
		String reqJson = RequestUtil.receiveReq(this.getRequest());
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
		List<RecommendInfoVo> allList = new ArrayList<RecommendInfoVo>();
		long s = System.currentTimeMillis();
		long end1 = s;
		long end2 = s;
		long end3 = s;
		long end4 = s;
		long end5 = s;
		long end6 = s;
		long end7 = s;
		try {
			//从redis或mongo中获取该用户的标签
			UserTag dbUserTag = null;
			if(!StringUtil.isNullStr(userId)){
				try {
					dbUserTag = userTagCacheManager.queryById(userId);
				} catch (Exception e) {
				}
				if(dbUserTag == null || dbUserTag.getId() == null){
					dbUserTag = null;
				}
			}
			end1 = System.currentTimeMillis();
			//确定用户最后的标签
			if(!checkTagsNotNull(reqUserTag)){//请求没带标签
				if (dbUserTag == null){//redis里面没有用户的标签
					//如果mongo中没有用户的标签，则获取默认标签
					try {
						reqUserTag = JSON.parseObject(RequestConstants.V_DEFAULT_USERTAG,UserTag.class);
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
			List<RecommendInfoVo> firstRst = new ArrayList<RecommendInfoVo>();//搜索引擎单个一级分类下的最高标签搜索结果
			List<RecommendInfoVo> secondRst = new ArrayList<RecommendInfoVo>();//搜索引擎单个一级分类下的所有标签搜索结果
			if(reqUserTag.getCats() != null && reqUserTag.getCats().size() > 0){
				int searchCatCount = 1;		//一级分类下搜索查找次数
				int recomdCatCount = 1;		//一级分类下推荐标签查找次数
				int catItemCount = 1;		//一级分类下的标签查找次数
				String recomdLabels = null;	//搜索的标签，最后搜索引擎需要使用
				for(CatInfo catInfo : reqUserTag.getCats()){//一个一级分类查找N次。。最高分的查N-1次，所有的再查一次
					if(StringUtil.isNullStr(catInfo.getCatId())){//如果一级标签不存在，则不查找
						continue;
					}
					if(recomdCatCount > recomdCatMax && searchCatCount > searchCatMax){//超过了人工推荐和搜索引擎最多的一级分类搜索次数
						break;
					}
					recomdLabels = null;
					//搜索推荐标签，推荐标签除了人工推荐需要，搜索引擎也需要
					if(catInfo.getRecommendation() != null && (recomdCatCount <= recomdCatMax || searchCatCount <= searchCatMax)){
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
							catItemCount ++;
						}
						if(recomdCatCount <= recomdCatMax && recomdLabels != null){
							Long rstart = System.currentTimeMillis();
							//从平台搜索数据
							List<RecommendInfoVo> list = recommendInfoCacheManager.queryByLabels(recomdLabels,prdType,catInfo.getCatId());
							Long rend = System.currentTimeMillis();
							if(log.isDebugEnabled())
								log.debug("search from system,duration:" + (rend - rstart));
							if(list != null){//已去重
								allList.addAll(list);
								//清空不需要的对象
								list.clear();
								list = null;
							}
							recomdCatCount ++;
						}
					}
					//分类可能为空,要考虑，直接不传一级分类给搜索引擎
					if(catInfo.getItems() != null && searchCatCount <= searchCatMax){
						catItemCount = 1;						//刚开始默认第一次
						StringBuffer fields = new StringBuffer();
						StringBuffer keyword = new StringBuffer();
						String tmpFields = null;
						String tmpKeyword = null;
						for(CatItem catItem : catInfo.getItems()){
							if(StringUtil.isNullStr(catItem.getLabelId())){//搜索的ID必须要有
								continue;
							}
							if(catItemCount > searchCatItemMax){//每个一级分类最多搜索多少个标签
								break;
							}
							if(searchCatPerMax > 1 && catItemCount < searchCatPerMax){
								//搜索一级分类下的某一个评分高的对象。这个默认搜索（searchCatItemMax - 1 ）次
								tmpFields = catItem.getLabelId();
								tmpKeyword = catItem.getLabelValue();
								if(!StringUtil.isNullStr(recomdLabels) && RequestConstants.V_SEARCH_RECOMD_ENABLE){
									tmpFields = tmpFields + RecommendConstants.SPLIT_COMMA + RequestConstants.SEARCH_KEY_RECOMMD;
									tmpKeyword = tmpKeyword + RecommendConstants.SPLIT_COMMA + recomdLabels;
									
									SearchRequest(firstRst,searchUrl,order,RecommendConstants.S_BLANK+searchItemLimit,catInfo.getCatId(),
											prdTypeRelation.getPrdInfoIds(),ctVer,prdTypeRelation.getSearchCt(),
											tmpFields,tmpKeyword);
								}else{
									SearchRequest(firstRst,searchUrl,order,RecommendConstants.S_BLANK+searchItemLimit,catInfo.getCatId(),
											prdTypeRelation.getPrdInfoIds(),ctVer,prdTypeRelation.getSearchCt(),
											tmpFields,tmpKeyword);
								}
							}
							fields.append(catItem.getLabelId()).append(RecommendConstants.SPLIT_COMMA);
							keyword.append(catItem.getLabelValue()).append(RecommendConstants.SPLIT_COMMA);
							catItemCount ++;
						}
						//搜索一级分类下的所有
						if(!StringUtil.isNullStr(recomdLabels) && RequestConstants.V_SEARCH_RECOMD_ENABLE){
							fields.append(RequestConstants.SEARCH_KEY_RECOMMD);
							keyword.append(recomdLabels);
							SearchRequest(secondRst,searchUrl,order,RecommendConstants.S_BLANK+searchLimit,catInfo.getCatId(),
									prdTypeRelation.getPrdInfoIds(),ctVer,prdTypeRelation.getSearchCt(),
									fields.toString(),keyword.toString());
						}else{
							SearchRequest(secondRst,searchUrl,order,RecommendConstants.S_BLANK+searchLimit,catInfo.getCatId(),
									prdTypeRelation.getPrdInfoIds(),ctVer,prdTypeRelation.getSearchCt(),
									ST.cutStringComma(fields.toString()),ST.cutStringComma(keyword.toString()));
						}
						searchCatCount ++;
					}
				}
			}
			end5 = System.currentTimeMillis();
			//添加搜索结果
			allList.addAll(firstRst);
			allList.addAll(secondRst);
			
			firstRst.clear();//清空不需要的对象
			firstRst = null;
			secondRst.clear();
			secondRst = null;
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
		
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
		end6 = System.currentTimeMillis();
		allList.clear();//清空不需要的对象
		allList = null;
		contIdMap.clear();
		contIdMap = null;
		
		//返回最后的分页结果
		List<RecommendInfoVo> returnList = new ArrayList<RecommendInfoVo>();
		int total = uniqList.size();
		int requestTotal = (start + limit);
		int end = requestTotal > total ? total : requestTotal;
		//自己获取最后的分页结果
		if(start < total){
			for(int i = start; i < end ; i++){
				returnList.add(uniqList.get(i));
			}
		}
		end7 = System.currentTimeMillis();
		
		uniqList.clear();//清空不需要的对象
		uniqList = null;
		
		//返回结果
		resultMap.put(RequestConstants.R_SUCC, Boolean.TRUE);
		resultMap.put(RequestConstants.R_CODE, RequestConstants.R_CODE_000000);
		resultMap.put(RequestConstants.R_MSG, this.getText("request.success"));
		resultMap.put(RequestConstants.R_ROOT, returnList);
		resultMap.put(RequestConstants.R_TOTAL, total);
		if(log.isDebugEnabled())
			log.debug("list end,duration:" + (end7 -s) + "|" + (end1 - s) + "|" + (end2 - end1) + "|" + (end3 - end2) + "|" + (end4 - end3) + "|" + (end5 - end4)
				 + "|" + (end6 - end5) + "|" + (end7 - end6));
		return SUCCESS;
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
	 */
	public void SearchRequest(List<RecommendInfoVo> returnList,String searchUrl,String order,String pageSize,
			String contDisplayType,String packId,String ctVer,String ct,String fields,String keyword){
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
			searchRequest.setContDisplayType(contDisplayType);
			
			searchRequest.setPageSize(""+pageSize);
			searchRequest.setPageStart("0");
			searchRequest.setOrder(order);
			
			List<SearchResult> searchRsts = searchCacheManager.queryByParam(searchUrl, searchRequest);
			if(searchRsts != null){
				for(SearchResult searchRst : searchRsts){
					if(searchRst.getContentId() == null){
						RecommendInfoVo vo = new RecommendInfoVo();
						vo.setPrdContId(StringUtil.nullToCloneLong(searchRst.getContentId()));
//						vo.setContName(contName);
						returnList.add(vo);
					}
				}
			}
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
}

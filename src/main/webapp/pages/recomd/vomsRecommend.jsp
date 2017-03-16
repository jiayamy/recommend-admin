<%@ page language="java" pageEncoding="UTF-8" contentType="text/html;charset=utf-8"%>
<%@ include file="/common/taglibs.jsp"%>
<div class="breadcrumbs" id="breadcrumbs">
	<script type="text/javascript">
			try{ace.settings.check('breadcrumbs' , 'fixed')}catch(e){}
	</script>

	<ul class="breadcrumb">
	    <li><i class="ace-icon fa fa-home home-icon"></i>
			<a href="#"><fmt:message key="index"/></a>
		</li>
		<li>
			<fmt:message key="sysManage"/>
		</li>
		<li class="active"><span><ftm:message key="sys.sysParm"/></span></li>
	</ul><!-- /.breadcrumb -->
</div>	
<div class="page-content">
	<div class="panel-group accordion no-margin-bottom" id="accordion">
		<div class="panel panel-default">
			<div class="panel-heading no-padding">
				<div id="actions" class="btn-group clearfix">	
					<cas:havePerm url="/recomd/updateVomsRecommend.htm">
				    <button type="button" class="btn btn-sm" onclick="updateVomsRecommend(true)">
				    	<i class="ace-icon glyphicon glyphicon-upload orange bigger-120"></i>
				    	推荐
				    </button>
				    <button type="button" class="btn btn-sm" onclick="updateVomsRecommend(false)">
				    	<i class="ace-icon glyphicon glyphicon-download orange bigger-120"></i>
				    	撤回
				    </button>
				    </cas:havePerm>			    
			    	<button id="searchTitle" flag="hide" onclick="change()" class="btn btn-sm"> 
				        <b class="ace-icon fa fa-search orange bigger-115"></b>
				        	<span>展开</span>
				        <span><i class="fa fa-plus bigger-110 orange"></i></span>
				    </button>
			    </div>
			</div>			    
		    <div id="searchDiv" style="display: none;">
		    	<div class="panel-body">
					<form class="form-horizontal" role="form">
						<!-- #section:elements.form -->
						<div class="form-group">
							<div class = "col-md-4">
								<label class="col-sm-3 control-label" for="s_objId"><fmt:message key="voms.recomd.objId"/></label>
								<div class="col-sm-9">
									<input type="text" id="s_objId" class="form-control" />
								</div>
							</div>
							<div class = "col-md-4">
								<label class="col-sm-3 control-label" for="s_name"><fmt:message key="voms.recomd.name"/></label>
								<div class="col-sm-9">
									<input type="text" id="s_ame" class="form-control" />
								</div>
							</div>
							<div class = "col-md-4">
							    <label class="col-sm-3 control-label" for="s_labelInfo"><fmt:message key="voms.recomd.lableInfo"/></label>
								<div class="col-sm-9">
									<input type="text" id="s_labelInfo" class="form-control" />
								</div>
							</div>
							
						</div>
						<div class="form-group">
							<div class = "col-md-4">
								<label class="col-sm-3 control-label" for="s_PrdType"><fmt:message key="voms.recomd.prdType"/></label>
								<div class="col-sm-9">
									<select class="chosen-select form-control" id="s_prdType" data-default="">
										<option value=""><fmt:message key="common.pleaseselect"/></option>
										<c:forEach var="type" varStatus="vs" items="${prdTypeRelations}">
											<option value="${type.prdType}">${type.name}</option>
										</c:forEach>
									</select>
								</div>
							</div>
							<div class = "col-md-4">
								<label class="col-sm-3 control-label" for="s_objType"><fmt:message key="voms.recomd.objType"/></label>
								<div class="col-sm-9">
									<select class="chosen-select form-control" id="s_objType" data-default="">
										<option value=""><fmt:message key="common.pleaseselect"/></option>
										<option value="0">栏目</option>
										<option value="1">展现对象</option>
										<option value="101">页面对象</option>
									</select>
								</div>
							</div>
							<div class = "col-md-4">
								<label class="col-sm-3 control-label" for="s_type"><fmt:message key="voms.recomd.type"/></label>
								<div class="col-sm-9">
									<select class="chosen-select form-control" id="s_type" data-default="">
										<option value=""><fmt:message key="common.pleaseselect"/></option>
										<option value="10">专题</option>
										<option value="11">内容组合</option>
										<option value="20">大图内容</option>
										<option value="21">多图内容</option>
									</select>
								</div>
							</div>	
						</div>
						<div class="form-group">						
							<div class = "col-md-4">
								<label class="col-sm-3 control-label" for="s_isRecommend"><fmt:message key="common.status"/></label>
								<div class="col-sm-9">
									<select class="chosen-select form-control" id="s_isRecommend" data-default="">
										<option value=""><fmt:message key="common.pleaseselect"/></option>
										<option value="1"><fmt:message key="common.isRecommend.valid"/></option>
										<option value="0"><fmt:message key="common.isRecommend.invalid"/></option>
									</select>
								</div>
							</div>
							<div class = "col-md-4">
							</div>
							<div class = "col-md-4 text-right">
							    <button type="button" class="btn  btn-md" onclick="listVomsRecommend(event)">
							    	<i class="ace-icon fa fa-search orange"></i><fmt:message key="button.search"/>
							    </button>
						        <button type="button" class="btn  btn-md" onclick="reset()">
						        	<i class="ace-icon fa fa-repeat"></i><fmt:message key="button.reset"/>
						        </button>
							</div>
						</div>
					</form>
	      		</div>
	    	</div>
	  	</div>
	</div>
	
	<div class="row">
		<div class="col-xs-12">
			<table id="grid-table"></table>
	
			<div id="grid-pager"></div>
	
			<script type="text/javascript">
				var $path_base = "..";//in Ace demo this will be used for editurl parameter
			</script>			
		</div><!-- /.col -->
	</div><!-- /.row -->
</div><!-- /.page-content -->
<div style="display:none">
	<div id="prdType_vals">
	<c:forEach var="type" varStatus="vs" items="${prdTypeRelations}">
		<input type="hidden" id="prdType_${type.prdType}" value="${type.name}">
	</c:forEach>
	</div>
</div>
<script src="<c:url value='/pages/recomd/js/vomsRecommend.js'/>"></script>
<script type="text/javascript">
	var webroot ='<c:url value="/"/>';
</script>
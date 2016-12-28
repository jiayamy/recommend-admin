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
				    <cas:havePerm url="/recomd/addTopRecommend.htm">
				    <button type="button" class="btn btn-sm" onclick="addTopRecommend()">
				    	<i class="ace-icon fa fa-plus orange bigger-110"></i>
				    	<fmt:message key="button.add"/>
				    </button>
				    </cas:havePerm>
				    
				    <cas:havePerm url="/recomd/delTopRecommend.htm">
				    <button type="button" class="btn btn-sm btn-round pull-right" onclick="delTopRecommend()">
				    	<i class="ace-icon fa fa-trash-o orange bigger-120"></i>
				    	<fmt:message key="button.delete"/>
				    </button>
				    </cas:havePerm>
				    
				    <cas:havePerm url="/recomd/editTopRecommend.htm">
				    <button type="button" class="btn btn-sm" onclick="editTopRecommend()">
				    	<i class="ace-icon fa fa-edit orange bigger-120"></i>
				    	<fmt:message key="button.edit"/>
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
								<label class="col-sm-3 control-label" for="s_PrdType"><fmt:message key="top.recomd.prdType"/></label>
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
								<label class="col-sm-3 control-label" for="s_topId"><fmt:message key="top.recomd.topId"/></label>
								<div class="col-sm-9">
									<input type="text" id="s_topId" class="form-control" />
								</div>
							</div>
							<div class = "col-md-4">
								<label class="col-sm-3 control-label" for="s_topName"><fmt:message key="top.recomd.topName"/></label>
								<div class="col-sm-9">
									<input type="text" id="s_topName" class="form-control" />
								</div>
							</div>
						</div>
						<div class="form-group">
							<div class = "col-md-4">
								<label class="col-sm-3 control-label" for="s_status"><fmt:message key="common.status"/></label>
								<div class="col-sm-9">
									<select class="chosen-select form-control" id="s_status" data-default="">
										<option value=""><fmt:message key="common.pleaseselect"/></option>
										<option value="1"><fmt:message key="common.status.valid"/></option>
										<option value="0"><fmt:message key="common.status.invalid"/></option>
									</select>
								</div>
							</div>
							<div class = "col-md-4">
							</div>
							<div class = "col-md-4 text-right">
							    <button type="button" class="btn  btn-md" onclick="listTopRecommend(event)">
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
			<!-- PAGE CONTENT ENDS -->
		</div><!-- /.col -->
	</div><!-- /.row -->
</div><!-- /.page-content -->

<!-- add sys params modal-->
<div class="modal fade" id="addTopRecommendModal" tabindex="-1"
	role="dialog" aria-labelledby="modalLabel" aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<h4 class="modal-title" id="modalLabel">
					<fmt:message key="button.add" />
				</h4>
			</div>
			<div class="modal-body">
				<form class="form-horizontal" id="addForm" onsubmit="return false;">
					<div class="form-group">						
						<div class="col-md-6">
							<label for="addTopId" class="col-sm-3 no-padding-right control-label"><fmt:message key="top.recomd.topId" /></label>
							<div class="col-sm-9">
								<input type="text" class="form-control" id="addTopId" />
							</div>
						</div>
						<div class="col-md-6">
							<label for="addTopName" class="col-sm-3 no-padding-right control-label"><fmt:message key="top.recomd.topName" /></label>
							<div class="col-sm-9">
								<input type="text" class="form-control" id="addTopName" />
							</div>
						</div>						
					</div>										
					<div class="form-group">
						<div class="col-md-6">
							<label for="addPrdType" class="col-sm-3 control-label no-padding-right"><fmt:message key="top.recomd.prdType" /></label>
							<div class="col-sm-9">
								<select class="chosen-select form-control" id="addPrdType" data-default="">
									<c:forEach var="type" varStatus="vs" items="${prdTypeRelations}">
										<option value="${type.prdType}">${type.name}</option>
									</c:forEach>
								</select>
							</div>
						</div>
					</div>
				</form>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-md" id="addSave" onclick="addSaveTopRecommend()">
					<fmt:message key="button.confirm" />
				</button>
				<button type="button" class="btn btn-md" id="modalClose" data-dismiss="modal">
					<fmt:message key="button.close" />
				</button>
			</div>
		</div>
	</div>
</div>

<div class="modal fade" id="editTopRecommendModal" tabindex="-1" role="dialog" aria-labelledby="modalLabel" aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<h4 class="modal-title" id="modalLabel">
					<fmt:message key="button.edit" />
				</h4>
			</div>
			<div class="modal-body">
				<form class="form-horizontal" id="saveEditForm"
					onsubmit="return false;">
					<input type="hidden" class="form-control" id="editId" />	
					<div class="form-group">						
						<div class="col-md-6">
							<label for="editTopId" class="col-sm-3 no-padding-right control-label"><fmt:message key="top.recomd.topId" /></label>
							<div class="col-sm-9">
								<input type="text" class="form-control" id="editTopId" />
							</div>
						</div>
						<div class="col-md-6">
							<label for="editTopName" class="col-sm-3 no-padding-right control-label"><fmt:message key="top.recomd.topName" /></label>
							<div class="col-sm-9">
								<input type="text" class="form-control" id="editTopName" />
							</div>
						</div>
					</div>					
					<div class="form-group">
						<div class="col-md-6">
							<label for="editPrdType" class="col-sm-3 control-label no-padding-right"><fmt:message key="top.recomd.prdType" /></label>
							<div class="col-sm-9">
								<select class="chosen-select form-control" id="editPrdType" data-default="">
									<c:forEach var="type" varStatus="vs" items="${prdTypeRelations}">
										<option value="${type.prdType}">${type.name}</option>
									</c:forEach>
								</select>
							</div>
						</div>						
						<div class="col-md-6">
							<label for="editStatus" class="col-sm-3 control-label no-padding-right"><fmt:message key="common.status" /></label>
							<div class="col-sm-9">
								<select class="chosen-select form-control" id="editStatus" data-default="">
									<option value="1"><fmt:message key="common.status.valid"/></option>
									<option value="0"><fmt:message key="common.status.invalid"/></option>
								</select>
							</div>
						</div>
					</div>
				</form>
			</div>

			<div class="modal-footer">
				<button type="button" class="btn btn-md" id="editSave" onClick="editSaveTopRecommend()">
					<fmt:message key="button.confirm" />
				</button>
				<button type="button" class="btn btn-md" id="modalClose" data-dismiss="modal">
					<fmt:message key="button.close" />
				</button>
			</div>
		</div>
	</div>
</div>
<div style="display:none">
	<div id="prdType_vals">
	<c:forEach var="type" varStatus="vs" items="${prdTypeRelations}">
		<input type="hidden" id="prdType_${type.prdType}" value="${type.name}">
	</c:forEach>
	</div>	
</div>
<script src="<c:url value='/pages/recomd/js/topRecommend.js'/>"></script>
<script type="text/javascript">
	var webroot ='<c:url value="/"/>';
</script>

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
				    <cas:havePerm url="/sys/addConfig.htm">
				    <button type="button" class="btn btn-sm" onclick="addSysParms()">
				    	<i class="ace-icon fa fa-plus orange bigger-110"></i>
				    	<fmt:message key="button.add"/>
				    </button>
				    </cas:havePerm>
				    
				    <cas:havePerm url="/sys/delConfig.htm">
				    <button type="button" class="btn btn-sm btn-round pull-right" onclick="delSysParms()">
				    	<i class="ace-icon fa fa-trash-o orange bigger-120"></i>
				    	<fmt:message key="button.delete"/>
				    </button>
				    </cas:havePerm>
				    
				    <cas:havePerm url="/sys/refreshAllCache.htm">
				    <button type="button" class="btn btn-sm btn-round pull-right" onclick="refreshAllCache()">
				    	<i class="ace-icon fa fa-refresh orange bigger-120"></i>
				    	<%-- <fmt:message key="button.delete"/> --%>
				    	刷新所有缓存
				    </button>
				    </cas:havePerm>
				    
				    <cas:havePerm url="/sys/editConfig.htm">
				    <button type="button" class="btn btn-sm" onclick="editSysParms()">
				    	<i class="ace-icon fa fa-edit orange bigger-120"></i>
				    	<fmt:message key="button.edit"/>
				    </button>
				    </cas:havePerm>
				    
				    <cas:havePerm url="/sys/exportConfig.htm">
				    <button type="button" class="btn btn-sm" onclick="exportSysParms()">
				    	<i class="ace-icon glyphicon glyphicon-export orange"></i>
				    	<fmt:message key="button.export"/>
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
								<label class="col-sm-3 control-label" for="sysParmsKey"><fmt:message key="sys.sysConfig.key"/></label>
								<div class="col-sm-9">
									<input type="text" id="sysParmsKey" class="form-control" />
								</div>
							</div>
						
							<div class = "col-md-4">
								<label class="col-sm-3 control-label" for="sysParmsValue"><fmt:message key="sys.sysConfig.value"/></label>
								<div class="col-sm-9">
									<input type="text" id="sysParmsValue" class="form-control" />
								</div>
							</div>
						
							<div class = "col-md-4 text-right">
							    <button type="button" class="btn  btn-md" onclick="listSysParms(event)">
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
<div class="modal fade" id="addSysParmsModal" tabindex="-1"
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
						<div class="col-md-12">
							<label for="addConfigKey"
								class="col-sm-2 control-label no-padding-right"><fmt:message
									key="sys.sysConfig.key" /></label>
							<div class="col-sm-10">
								<input type="text" class="form-control" id="addConfigKey" />
							</div>
						</div>
					</div>
					<div class="form-group">
						<div class="col-md-12">
							<label for="addConfigValue"
								class="col-sm-2 control-label no-padding-right"><fmt:message
									key="sys.sysConfig.value" /></label>
							<div class="col-sm-10">
								<input type="text" class="form-control" id="addConfigValue" />
							</div>
						</div>

					</div>
					<div class="form-group no-margin-bottom">
						<div class="col-md-12">
							<label for="addmzid" class="col-sm-2 no-padding-right control-label"><fmt:message
									key="sys.sysConfig.detail" /></label>
							<div class="col-sm-10">
								<textarea rows="5" class="form-control" id="addConfigDetail"></textarea>
							</div>
						</div>
					</div>

				</form>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-md" id="addSave"
					onclick="addSave()">
					<fmt:message key="button.confirm" />
				</button>
				<button type="button" class="btn btn-md" id="modalClose"
					data-dismiss="modal">
					<fmt:message key="button.close" />
				</button>

			</div>
		</div>
		<!-- /.modal-content -->
	</div>
	<!-- /.modal -->
</div>

<!--edit sys params modal-->
<div class="modal fade" id="editSysParamsModal" tabindex="-1"
	role="dialog" aria-labelledby="modalLabel" aria-hidden="true">
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
					<div class="form-group">
						<div class="col-md-12">
							<label for="editConfigKey"
								class="col-sm-2 control-label no-padding-right"><fmt:message
									key="sys.sysConfig.key" /></label>
							<div class="col-md-10">
								<input type="text" class="form-control" id="editConfigKey" />
							</div>
						</div>
					</div>
					<div class="form-group">
						<div class="col-md-12">
							<label for="editConfigValue"
								class="col-sm-2 control-label no-padding-right"><fmt:message
									key="sys.sysConfig.value" /></label>
							<div class="col-sm-10">
								<input type="text" class="form-control" id="editConfigValue" />
							</div>
						</div>
					</div>
					<div class="form-group">
						<div class="col-md-12">
							<label for="eidtDetail" class="col-sm-2 no-padding-right control-label"><fmt:message
									key="sys.sysConfig.detail" /></label>
							<div class="col-sm-10">
								<textarea rows="5" class="form-control" id="eidtDetail"></textarea>
							</div>
						</div>
					</div>

				</form>
			</div>

			<div class="modal-footer">
				<button type="button" class="btn btn-md" id="editSave"
					onClick="editSave()">
					<fmt:message key="button.confirm" />
				</button>
				<button type="button" class="btn btn-md" id="modalClose"
					data-dismiss="modal">
					<fmt:message key="button.close" />
				</button>
			</div>
		</div>
		<!-- /.modal-content -->
	</div>
	<div style="display:none">
		<input type="hidden" id="sys-sysConfig-id" value="<fmt:message key = "sys.sysConfig.id"/>">
		<input type="hidden" id="sys-sysConfig-key" value="<fmt:message key = "sys.sysConfig.key"/>">
		<input type="hidden" id="sys-sysConfig-value" value="<fmt:message key = "sys.sysConfig.value"/>">
		<input type="hidden" id="sys-sysConfig-detail" value="<fmt:message key = "sys.sysConfig.detail"/>">
		
		<input type="hidden" id="sys-error-keyEmpty" value="<fmt:message key = "sys.error.keyEmpty"/>">
		<input type="hidden" id="sys-error-valueEmpty" value="<fmt:message key = "sys.error.valueEmpty"/>">
		<input type="hidden" id="sys-error-selectOne" value="<fmt:message key = "common.select.mult"/>">
	</div>
	<!-- /.modal -->
</div>
<script src="<c:url value='/pages/sys/js/sysParam.js'/>"></script>
<script type="text/javascript">
	var webroot ='<c:url value="/"/>';
</script>

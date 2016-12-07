<%@ page language="java" pageEncoding="UTF-8"
	contentType="text/html;charset=utf-8"%>
<%@ include file="/common/taglibs.jsp"%>
<style type="text/css">
		.tree .tree-selected {
		  background-color: rgba(255,215,100, 0.8);
		  color: #6398B0;
		}
		.tree .tree-selected:hover {
		  background-color: rgba(255,215,100, 0.8);
		}
		.tree .tree-item:hover {
		    background-color: #ffebb2;
		}
	</style>
<div class="breadcrumbs" id="breadcrumbs">
	<script type="text/javascript">
		try {
			ace.settings.check('breadcrumbs', 'fixed')
		} catch (e) {
		}
	</script>
	
	<ul class="breadcrumb">
		<li><i class="ace-icon fa fa-home home-icon"></i> <a href="#"><fmt:message
					key="index" /></a></li>
		<li><fmt:message key="sysManage" /></li>
		<li class="active"><span><ftm:message
					key="sys.recommdParm" /></span></li>
	</ul>
	<!-- /.breadcrumb -->
</div>
<div class="page-content">
	<div class="panel-group accordion no-margin-bottom" id="accordion">
		<div class="panel panel-default">
			<div class="panel-heading no-padding">
				<div id="actions" class="btn-group clearfix">
					<cas:havePerm url="/sys/addConfig.htm">
						<button type="button" class="btn btn-sm" onclick="addSysParms()">
							<i class="ace-icon fa fa-plus orange bigger-110"></i>
							<fmt:message key="button.add" />
						</button>
					</cas:havePerm>

					<cas:havePerm url="/sys/delConfig.htm">
						<button type="button" class="btn btn-sm btn-round pull-right"
							onclick="delSysParms()">
							<i class="ace-icon fa fa-trash-o orange bigger-120"></i>
							<fmt:message key="button.delete" />
						</button>
					</cas:havePerm>



					<cas:havePerm url="/sys/editConfig.htm">
						<button type="button" class="btn btn-sm"
							onclick="editRecomdParms()">
							<i class="ace-icon fa fa-edit orange bigger-120"></i>
							<fmt:message key="button.edit" />
						</button>
					</cas:havePerm>


				</div>
			</div>
			
		</div>
	</div>

	<div class="row">
		<div class="col-sm-6">
			<div class="widget-box widget-color-green2">
				<div class="widget-header">
					<h4 class="widget-title lighter smaller">选择标签</h4>
				</div>

				<div class="widget-body">
					<div class="widget-main padding-8">
						<div id="tree1" class="tree"></div>
					</div>
				</div>
			</div>
		</div>
		<div class="col-sm-6">
			<div class="widget-box widget-color-blue2">
				<div class="widget-header">
					<h4 class="widget-title lighter smaller">标签信息</h4>
				</div>

				<form class="form-horizontal" style="padding-top:15px;" role="form">
					<div class="form-group">
						<label class="col-sm-3 control-label no-padding-right"
							for="form-field-1"> 标签名 </label>

						<div class="col-sm-9">
							<input type="text" id="laberName" placeholder="标签名" readonly="readonly"
								class="col-xs-10 col-sm-5" />
						</div>
					</div>
					
					<div class="form-group">
						<label class="col-sm-3 control-label no-padding-right"
							for="form-field-1"> 权重 </label>

						<div class="col-sm-9">
							<input type="text" id="laberQ" placeholder="权重" readonly="readonly"
								class="col-xs-10 col-sm-5" />
						</div>
					</div>
				</form>
			</div>
		</div>
	</div>
	<!-- /.row -->
</div>
<!-- /.page-content -->

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
					<div class="form-group" style="display:none">
						<div class="col-md-12">
							<label for="addLabelParentType"
								class="col-sm-2 control-label no-padding-right">上级标签类型</label>
							<div class="col-sm-10">
								<input type="text" class="form-control" id="addLabelParentType" readonly="readonly"/>
								<input type="text" class="form-control" id="addLabelParentId" readonly="readonly"/>
							</div>
						</div>
					</div>
					
					<div class="form-group">
						<div class="col-md-12">
							<label for="addLabelParent"
								class="col-sm-2 control-label no-padding-right">上级标签</label>
							<div class="col-sm-10">
								<input type="text" class="form-control" id="addLabelParent" readonly="readonly"/>
							</div>
						</div>
					</div>
					<div class="form-group">
						<div class="col-md-12">
							<label for="addLabelName"
								class="col-sm-2 control-label no-padding-right">标签名</label>
							<div class="col-sm-10">
								<select class="form-control addLabelName" id="form-field-select-1">
									<option value=""></option>

									 
								</select>
							</div>
						</div>
					</div>
					<div class="form-group">
						<div class="col-md-12">
							<label for="addLabelType"
								class="col-sm-2 control-label no-padding-right">标签类型</label>
							<div class="col-sm-10">
								
								
	
								<select class="form-control addLabelType" id="form-field-select-1">
									<option value=""></option>
<!-- 									<option value="0">一级标签</option> -->
									<option value="1">二级标签</option>
									
									
								</select>
								
							</div>
						</div>

					</div>
					<div class="form-group no-margin-bottom">
						<div class="col-md-12">
							<label for="addLabelWeight"
								class="col-sm-2 no-padding-right control-label">标签权重</label>
							<div class="col-sm-10">
								<input type="text" class="form-control" id="addLabelWeight" />
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
					<div class="form-group" style="display:none">
						<div class="col-md-12">
							<label for="editLabelId"
								class="col-sm-2 control-label no-padding-right">标签id</label>
							<div class="col-md-10">
								<input type="text" class="form-control" id="editLabelId" readonly="readonly" />
								<input type="text" class="form-control" id="editLabelParentId" readonly="readonly" />
							</div>
						</div>
					</div>
					<div class="form-group">
						<div class="col-md-12">
							<label for="editLabelParent"
								class="col-sm-2 control-label no-padding-right">上级标签</label>
							<div class="col-md-10">
								<input type="text" class="form-control" id="editLabelParent" readonly="readonly" />
							</div>
						</div>
					</div>
					<div class="form-group">
						<div class="col-md-12">
							<label for="editLabelName"
								class="col-sm-2 control-label no-padding-right">标签名</label>
							<div class="col-sm-10">
								<input type="text" class="form-control" id="editLabelName" readonly="readonly"/>
							</div>
						</div>
					</div>
					<div class="form-group">
						<div class="col-md-12">
							<label for="editLabelType"
								class="col-sm-2 no-padding-right control-label">标签类型</label>
							<div class="col-sm-10">
								<select class="form-control editLabelType" id="editLabelType" disabled="disabled">
									
									<option value=""></option>
									<option value="0">一级标签</option>
									<option value="1">二级标签</option>
									 
								</select>
							</div>
						</div>
					</div>
					<div class="form-group">
						<div class="col-md-12">
							<label for="editLabelWeight"
								class="col-sm-2 no-padding-right control-label">标签权重</label>
							<div class="col-sm-10">
								<input type="text" class="form-control" id="editLabelWeight" />
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
	<div style="display: none">
		<input type="hidden" id="sys-sysConfig-id"
			value="<fmt:message key = "sys.sysConfig.id"/>"> <input
			type="hidden" id="sys-sysConfig-key"
			value="<fmt:message key = "sys.sysConfig.key"/>"> <input
			type="hidden" id="sys-sysConfig-value"
			value="<fmt:message key = "sys.sysConfig.value"/>"> <input
			type="hidden" id="sys-sysConfig-detail"
			value="<fmt:message key = "sys.sysConfig.detail"/>"> <input
			type="hidden" id="sys-error-keyEmpty"
			value="<fmt:message key = "sys.error.keyEmpty"/>"> <input
			type="hidden" id="sys-error-valueEmpty"
			value="<fmt:message key = "sys.error.valueEmpty"/>"> <input
			type="hidden" id="sys-error-selectOne"
			value="<fmt:message key = "common.select.mult"/>">
	</div>
	<!-- /.modal -->
</div>
<script src="<c:url value='/pages/sys/js/recomdParam.js'/>"></script>
<!-- page specific plugin scripts -->
<script
	src="./ace/assets/js/fuelux/data/fuelux.tree-sample-demo-data.js"></script>
<script src="./ace/assets/js/fuelux/fuelux.tree.js"></script>

<!-- ace scripts -->
<script src="./ace/assets/js/ace-elements.js"></script>
<script src="./ace/assets/js/ace.js"></script>

<script type="text/javascript">
	var webroot = '<c:url value="/"/>';
</script>

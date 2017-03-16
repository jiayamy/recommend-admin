<%@ page language="java" pageEncoding="UTF-8"
	contentType="text/html;charset=utf-8"%>
<%@ include file="/common/taglibs.jsp"%>
<style type="text/css">
/* 	.ztree li span.button { */
/* 	    width: 14px; */
/* 	    height: 14px; */
/* 	} */
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
	<div class="row">
		<div class="col-sm-4">
			<div class="widget-box">
				<div class="widget-header">
					<h4 class="widget-title lighter smaller">选择标签</h4>
				</div>

				<div class="widget-body">
					<div class="zTreeDemoBackground left">
						<ul id="treeDemo" class="ztree"></ul>
					</div>
				</div>
			</div>
		</div>
		<div class="col-sm-8">
			<div class="widget-box">
				<div class="widget-header">
					<h4 class="widget-title lighter smaller">标签信息</h4>
				</div>
				<form class="form-horizontal" style="padding-top:15px;" role="form">
					<div class="form-group">
						<label class="col-sm-3 control-label no-padding-right" for="laberName"> 标签名: </label>

						<div class="col-sm-9">
							<input type="text" id="laberName" placeholder="标签名" readonly="readonly"
								class="col-xs-10 col-sm-5" />
						</div>
					</div>
					<div class="form-group">
						<label class="col-sm-3 control-label no-padding-right" for="laberQ"> 权重: </label>
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
								<select class="form-control addLabelName" data-placeholder="Choose a Country...">
									<c:forEach var="enumsInfo" items="${enumsInfos}" varStatus="status">
				                    <option value="${enumsInfo.key}">${enumsInfo.val}</option>
									</c:forEach>
								</select>
							</div>
						</div>
					</div>
					<div class="form-group" style="display:none">
						<div class="col-md-12">
							<label for="addLabelType"s
								class="col-sm-2 control-label no-padding-right">标签类型</label>
							<div class="col-sm-10">
								<select class="form-control addLabelType" disabled="disabled">
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
					<div class="form-group" id="editLabelParentFormGroup">
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
					<div class="form-group" style="display:none">
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
<script src="./ace/plugins/js/ztree/jquery.ztree.core-3.5.js"></script>
<script src="./ace/plugins/js/ztree/jquery.ztree.excheck-3.5.js"></script>
<script src="./ace/plugins/js/ztree/jquery.ztree.exedit-3.5.js"></script>
<script src="./ace/plugins/js/ztree/jquery.ztree.exhide-3.5.min.js"></script>
<script src="<c:url value='/pages/sys/js/recomdZtree.js'/>"></script>
<script type="text/javascript">
	var webroot = '<c:url value="/"/>';
	$(document).ready(function(){
		initTree();
	});
	$('#treeDemo').bind("contextmenu",function(e){ return false; }); 
	$('#rMenu').bind("contextmenu",function(e){ return false; }); 
</script>
<div id="rMenu">
	<ul style="margin: 0;">
		<li id="refreshLabelli" onclick="refreshTreeNode()"><span class="node-refresh"></span>更新此标签<span></span></li>
		<cas:havePerm url="/sys/editRcmdParam.htm">
		<li id="addLabelli" onclick="addSysParms()"><span class="node-add"></span><span>添加子标签</span></li>
		<li onclick="editRecomdParms()"><span class="node-del"></span><span>编辑此标签</span></li>
		</cas:havePerm>
		<cas:havePerm url="/sys/deleteRcmdParam.htm">
		<li id="delLabelli" onclick="delSysParms()"><span class="node-edit"></span><span>删除此标签</span></li>
		</cas:havePerm>
	</ul>
</div>
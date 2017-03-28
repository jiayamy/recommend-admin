<%@ page language="java" pageEncoding="UTF-8" contentType="text/html;charset=utf-8"%>
<%@ include file="/common/taglibs.jsp"%>
<div class="row col-md-12">
	<div class="col-md-12">
		<h3 class="header smaller lighter red center-block">获取用户标签接口测试</h3>
		<form class="form-horizontal" role="form" action="#" method="post" id="testActionFrom">
			<div class="form-group">
				<label for="host" class="col-sm-2 control-label no-padding-right">服务器：</label>
				<div class="col-sm-10">
					<select class="col-xs-10 col-sm-12" id="host">
						<option value="localhost:8080${ctx}">localhost</option>
						<option value="172.16.70.116${ctx}">172.16.70.116</option>
						<option value="172.16.70.119${ctx}">172.16.70.119</option>
						<option value="172.16.70.120${ctx}">172.16.70.120</option>
						<option value="172.16.70.121${ctx}">172.16.70.121</option>
						<option value="10.200.66.22${ctx}">10.200.66.22</option>
						<option value="10.200.66.23${ctx}">10.200.66.23</option>
						<option value="10.200.66.91${ctx}">10.200.66.91</option>
						<option value="10.200.66.92${ctx}">10.200.66.92</option>
					</select>
				</div>
			</div>
			<div class="form-group">
				<label class="col-sm-2 control-label no-padding-right" for="userId">用户ID:</label>
				<div class="col-sm-10">
					<input type="text" id="userId" placeholder="userId" class="col-xs-10 col-sm-12" name="userId" required value="681274129"/>
				</div>
			</div>
			<div class="clearfix form-actions center-block col-md-12">
				<div class="col-sm-2 control-label no-padding-right"></div>
				<div class="col-sm-10" id="requestTime">
					<button class="btn" type="button" onclick="subClic()">
						<i class="ace-icon fa fa-search bigger-110"></i> 查询
					</button>
					&nbsp; &nbsp; &nbsp;
					<button class="btn" type="reset">
						<i class="ace-icon fa fa-undo bigger-110"></i> 重置
					</button>
				</div>
			</div>
		</form>
		<div class="form-horizontal" role="form" action="#" method="post" id="testActionFrom">
			<div class="form-group">
				<label class="col-sm-2 control-label no-padding-right" for="userId">返回数据:</label>
				<div class="col-sm-10" id="contentDiv1">
					<textarea class="col-xs-10 col-sm-12" id="contentDiv" style="height:300px;"></textarea>
				</div>
			</div>
		</div>
	</div>
</div>
<script type="text/javascript">
	function subClic() {
		var host = $('#host').val();
		var userId = $('#userId').val();
		if(userId == "" || host == ""){
			$('#requestTime').find('span').remove();
		}
		$.ajax({
			url : "${ctx}/recomd/testQueryTag.msp",
			type : "post",
			data:{"userId": userId, "host":host},
			success : function(msg) {
				var result = jQuery.parseJSON(msg);
				var jsonMsg = JSON.stringify(result.root);
				if(result.root == undefined){
					$('#contentDiv').val(msg);
				}else{
					$('#contentDiv').val(jsonMsg);
				}
				
				if(result.requestTime == undefined){
					return "";
				}
				var p = '<span>' + "(接口请求时间：" + result.requestTime + "MS" + ")" + '<span>';
				$('#requestTime').append(p);
				
				if($('#requestTime').find('span').length > 2){
					$('#requestTime').find('span').first().remove();
				}
			},
			error : function(msg) {
				alert("失败了");
			}
		});
	}
</script>
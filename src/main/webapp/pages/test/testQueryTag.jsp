<%@ page language="java" pageEncoding="UTF-8" contentType="text/html;charset=utf-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html>
<html>
<head lang="en">
<meta charset="UTF-8">
<title></title>
</head>
<body style="background: white">
	<!-- bootstrap & fontawesome -->
	<link rel="stylesheet" href="${ctx}/ace/assets/css/bootstrap.css" />
	<link rel="stylesheet" href="${ctx}/ace/assets/css/font-awesome.css" />

	<!-- text fonts -->
	<link rel="stylesheet" href="${ctx}/ace/assets/css/ace-fonts.css" />

	<!-- ace styles -->
	<link rel="stylesheet" href="${ctx}/ace/assets/css/ace.css" />

	<!--[if lte IE 9]>
	<link rel="stylesheet" href="${ctx}/ace/assets/css/ace-part2.css"/>
	<![endif]-->
	<link rel="stylesheet" href="${ctx}/ace/assets/css/ace-rtl.css" />

	<!--[if lte IE 9]>
	<link rel="stylesheet" href="${ctx}/ace/assets/css/ace-ie.css"/>
	<![endif]-->

	<!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->

	<!--[if lt IE 9]>
	<script src="${ctx}/ace/assets/js/html5shiv.js"></script>
	<script src="${ctx}/ace/assets/js/respond.js"></script>
	<![endif]-->
	<script src="${ctx}/ace/dist/js/jquery.min.js"></script>
	<script src="${ctx}/ace/dist/js/jquery.validate.min.js"></script>
	<div class="row col-md-12">
		<div class="col-md-12">
			<h3 class="header smaller lighter red center-block">获取用户标签接口测试</h3>
			<form class="form-horizontal" role="form" action="#" method="post" id="testActionFrom">
				<div class="form-group">
					<label for="host" class="col-sm-2 control-label no-padding-right">服务器：</label>
					<div class="col-sm-10">
						<select class="col-xs-10 col-sm-12" id="host">
							<option value="localhost:8080${ctx}">localhost</option>
							<option value="172.16.70.116:8080${ctx}">172.16.70.116</option>
							<option value="172.16.70.119:8080${ctx}">172.16.70.119</option>
							<option value="172.16.70.120:8080${ctx}">172.16.70.120</option>
							<option value="172.16.70.121:8080${ctx}">172.16.70.121</option>
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
					<div class="col-sm-10">
						<button class="btn" type="button" onclick="subClic()">
							<i class="ace-icon fa fa-search bigger-110"></i> 查询
						</button>
						&nbsp; &nbsp; &nbsp;
						<button class="btn" type="reset">
							<i class="ace-icon fa fa-undo bigger-110"></i> 重置
						</button>
						&nbsp; &nbsp; &nbsp;
						<a class="btn btn-white btn-primary" href="${ctx}/pages/test/testSearch.jsp">前往内容推荐测试</a>
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
	$(function() {
		$("#testActionFrom")
			.validate({
				rules : {
					userId : {
						required : true
					},
					host : {
						required : true
					}
				},
				messages : {
					userId : {
						required : "用户id必填！"
					},
					host : {
						required : "主机地址必填！"
					}
				},
				//提交表单后，（第一个）未通过验证的表单获得焦点
				focusInvalid : true,
				//当未通过验证的元素获得焦点时，移除错误提示
				focusCleanup : true
			});
		});
		function subClic() {
			var host = $('#host').val();
			var userId = $('#userId').val();
			$.ajax({
				url : "${ctx}/recomd/testQueryTag.msp",
				type : "post",
				data:{"userId": userId, "host":host},
				success : function(msg) {
					$('#contentDiv').val(msg);
				},
				error : function(msg) {
					alert("失败了");
				}
			});
		}
	</script>
</body>
</html>
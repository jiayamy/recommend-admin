<%@ page language="java" pageEncoding="UTF-8" contentType="text/html;charset=utf-8"%>
<%@ include file="/common/taglibs.jsp" %>
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
			<h3 class="header smaller lighter red center-block">TestAction测试页面2</h3>

			<form class="form-horizontal" role="form" action="${ctx}/recomd/testSearch.msp" method="post"
				id="testActionFrom">


				<div class="form-group">
					<label for="host" class="col-sm-3 control-label no-padding-right">主机host：</label>

					<div class="col-sm-9">
						<select class="col-xs-10 col-sm-5" id="host" name="host">
							<option value="localhost:8080/recommend-admin">Alabama</option>
							<option value="AK">Alaska</option>

						</select>
					</div>
				</div>
				<!--先显示，触发radio改变事件-->
				<div id="Json1">
					<div class="form-group">
						<label class="col-sm-3 control-label no-padding-right" for="id">用户ID:</label>

						<div class="col-sm-9">
							<input type="text" id="id" placeholder="start"
								class="col-xs-10 col-sm-5" name="userTag.id" />
						</div>
					</div>
					<div class="form-group">
						<label class="col-sm-3 control-label no-padding-right" for="start">start:</label>

						<div class="col-sm-9">
							<input type="text" id="start" placeholder="start"
								class="col-xs-10 col-sm-5" name="userTag.start" />
						</div>
					</div>

					<div class="form-group">
						<label class="col-sm-3 control-label no-padding-right" for="limit">limit:</label>

						<div class="col-sm-9">
							<input type="text" id="limit" placeholder="limit"
								class="col-xs-10 col-sm-5" name="userTag.limit" />
						</div>
					</div>

					<div class="form-group">
						<label class="col-sm-3 control-label no-padding-right" for="ctVer">ctVer:</label>

						<div class="col-sm-9">
							<input type="text" id="ctVer" placeholder="ctVer"
								class="col-xs-10 col-sm-5" name="userTag.ctVer" />
						</div>
					</div>

					<div class="form-group">
						<label class="col-sm-3 control-label no-padding-right"
							for="prdType">prdType:</label>

						<div class="col-sm-9">
							<input type="text" id="prdType" placeholder="prdType"
								class="col-xs-10 col-sm-5" name="userTag.prdType" />
						</div>
					</div>

					<div class="form-group">
						<div>
							<label for="catsInfoJson"
								class="col-sm-3 control-label no-padding-right">catsInfoJson:</label>
							<div class="col-sm-9">
								<textarea class="col-xs-10 col-sm-5 " id="catsInfoJson"
									placeholder="catsInfoJson" name="catsInfoJson"></textarea>
							</div>
						</div>

					</div>



				</div>
				<div class="clearfix form-actions center-block col-md-12">
					<div class="col-md-offset-3 col-md-9">
						<button class="btn btn-info" type="submit">
							<i class="ace-icon fa fa-check bigger-110"></i> Submit
						</button>
						&nbsp; &nbsp; &nbsp;
						<button class="btn" type="reset">
							<i class="ace-icon fa fa-undo bigger-110"></i> Reset
						</button>
					</div>
				</div>


			</form>

		</div>

		<script type="text/javascript">
			$(function() {
				$("#testActionFrom").validate({
					rules : {
						userId : {
							required : true
						},
						host : {
							required : true,
							checkHost : true
						},
						catsJson : {
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
		</script>
	</div>
</body>
</html>
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
			<h3 class="header smaller lighter red center-block">内容推荐接口测试</h3>
			<form class="form-horizontal" role="form" action="${ctx}/recomd/testSearch.msp" method="post">
				<div class="form-group">
					<div class="col-md-6">
						<label for="host" class="col-sm-2 control-label no-padding-right">服务器：</label>
						<div class="col-sm-10">
							<select class="col-xs-10 col-sm-12" id="host" name="host">
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
					<div class="col-md-6">
						<label class="col-sm-2 control-label no-padding-right" for="id">用户ID:</label>
						<div class="col-sm-10">
							<input type="text" id="id" class="col-xs-10 col-sm-12" name="userTag.id" />
						</div>
					</div>
				</div>
				<div class="form-group">
					<div class="col-md-6">
						<label class="col-sm-2 control-label no-padding-right" for="start">分页开始:</label>
						<div class="col-sm-10">
							<input type="text" id="start" class="col-xs-10 col-sm-12" name="userTag.start" value="0" />
						</div>
					</div>
					<div class="col-md-6">
						<label class="col-sm-2 control-label no-padding-right" for="limit">每页条数:</label>
						<div class="col-sm-10">
							<input type="text" id="limit" class="col-xs-10 col-sm-12" name="userTag.limit" value="10"  />
						</div>
					</div>
				</div>
				<div class="form-group">
					<div class="col-md-6">
						<label class="col-sm-2 control-label no-padding-right" for="prdType">产品类型:</label>
						<div class="col-sm-10">
							<select class="col-xs-10 col-sm-12" id="prdType" name="userTag.prdType">
								<option value="MIGUVIDEO">咪咕视频</option>
								<option value="MIGUMOVIE">咪咕影院</option>
							</select>
						</div>
					</div>
					<div class="col-md-6">
						<label class="col-sm-2 control-label no-padding-right" for="ctVer">客户端版本号:</label>
						<div class="col-sm-10">
							<input type="text" id="ctVer" class="col-xs-10 col-sm-12" name="userTag.ctVer" value="v1.0"/>
						</div>
					</div>
				</div>
				<div class="form-group">
					<div class="col-md-6">
						<label for="catsInfoJson" class="col-sm-2 control-label no-padding-right">请求JSON串:</label>
						<div class="col-sm-10">
							<textarea class="col-xs-10 col-sm-12 " id="catsInfoJson" name="catsInfoJson" style="height:300px;"></textarea>
						</div>
					</div>
				</div>
				<div class="clearfix form-actions center-block col-md-12">
					<div class="col-sm-2 control-label no-padding-right"></div>
					<div class="col-sm-10">
						<button class="btn btn-info" type="submit">
							<i class="ace-icon fa fa-check bigger-110"></i> 提交
						</button>
						&nbsp; &nbsp; &nbsp;
						<button class="btn" type="reset">
							<i class="ace-icon fa fa-undo bigger-110"></i> 重置
						</button>
					</div>
				</div>
			</form>
		</div>
	</div>
</body>
</html>
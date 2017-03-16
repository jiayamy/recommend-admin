<%@ page language="java" pageEncoding="UTF-8" contentType="text/html;charset=utf-8"%>
<%@ include file="/common/taglibs.jsp" %>
<div class="row col-md-12">
		<div class="col-md-12" id="page-content">
			<h3 class="header smaller lighter red center-block">展现数据推荐接口测试</h3>
			<form class="form-horizontal no-padding-bottom no-margin-bottom" role="form" action="${ctx}/recomd/testSearch.msp" method="post" id="testForm">
				<div class="form-group">
					<div class="col-md-4">
						<label for="host" class="col-sm-2 control-label no-padding-right">服务器：</label>
						<div class="col-sm-10">
							<select class="col-xs-10 col-sm-12" id="host" name="host">
								<option value="localhost:8080${ctx}">localhost</option>
								<option value="172.16.70.116:8080${ctx}">172.16.70.116</option>
								<option value="172.16.70.119:8080${ctx}">172.16.70.119</option>
								<option value="172.16.70.120:8080${ctx}">172.16.70.120</option>
								<option value="172.16.70.121:8080${ctx}">172.16.70.121</option>
							</select>
						</div>
					</div>
					<div class="col-md-4">
						<label class="col-sm-2 control-label no-padding-right" for="id">用户ID:</label>
						<div class="col-sm-10">
							<input type="text" id="id" class="col-xs-10 col-sm-12" name="id" />
						</div>
					</div>
					<div class="col-md-4">
						<label class="col-sm-2 control-label no-padding-right" for="start">分页开始:</label>
						<div class="col-sm-10">
							<input type="text" id="start" class="col-xs-10 col-sm-12" name="start" value="0" />
						</div>
					</div>
				</div>
				<div class="form-group">
					<div class="col-md-4">
						<label class="col-sm-2 control-label no-padding-right" for="limit">每页条数:</label>
						<div class="col-sm-10">
							<input type="text" id="limit" class="col-xs-10 col-sm-12" name="limit" value="10"  />
						</div>
					</div>
					<div class="col-md-4">
						<label class="col-sm-2 control-label no-padding-right" for="prdType">产品类型:</label>
						<div class="col-sm-10">
							<select class="col-xs-10 col-sm-12" id="prdType" name="prdType">
								<option value="MIGUVIDEO">咪咕视频</option>
								<option value="MIGUMOVIE">咪咕影院</option>
							</select>
						</div>
					</div>
					<div class="col-md-4">
						<label class="col-sm-2 control-label no-padding-right" for="ctVer">客户端版本号:</label>
						<div class="col-sm-10">
							<input type="text" id="ctVer" class="col-xs-10 col-sm-12" name="ctVer" value="v1.0"/>
						</div>
					</div>
				</div>
				<div class="form-group">
					<div class="col-md-4">
						<label for="labelInfo" class="col-sm-2 control-label no-padding-right">标签(多个英文逗号分隔):</label>
						<div class="col-sm-10">
							<textarea class="col-xs-10 col-sm-12 " id="labelInfo"  name="labelInfo"></textarea>
						</div>
					</div>
				</div>
				<div class="clearfix form-actions center-block col-md-12">
					<div class="col-sm-2 control-label no-padding-right"></div>
					<div class="col-sm-10">
						<button id="subBtn" class="btn" type="button">
							<i class="ace-icon fa fa-search bigger-110"></i> 查询
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

<div class="row col-md-12">
	<div class="col-md-12">
		<div class="table-header">
			结果页面
		</div>
		<div>
			<table id="vomsContTab" class="table table-striped table-bordered table-hover">
				<thead>
					<tr>
						<th class="center">推荐对象ID</th>
						<th class="center">名称</th>
						<th class="center">推荐对象</th>
						<th class="center">类型</th>
					</tr>
				</thead>
				<tbody>
				</tbody>
			</table>
		</div>
	</div>
</div>
<script type="text/javascript">
$('#subBtn').click(function(){
	var jsonData = $('#testForm').serialize();
	$.ajax({
		url : "${ctx}/recomd/testSearchVoms.msp",
		type : "post",
		data: jsonData,
		async: false,
		success : function(msg) {
			$('#vomsContTab tbody').empty();
			var result = jQuery.parseJSON(msg);
			if(result.success == true){
				var vomsContList = result.root;
				var ht = '';
				var name = "";
				for(var i = 0;i<vomsContList.length;i++){
					ht = ht+'<tr>';
					ht = ht+'<td class="center">'+vomsContList[i].objId+'</td>';
					ht = ht+'<td class="center">'+vomsContList[i].name+'</td>';
					name = vomsContList[i].objType;
					if(vomsContList[i].objType == "0"){
						name = "栏目";
					}else if(vomsContList[i].objType == "1"){
						name = "展现对象";
					}else if(vomsContList[i].objType == "101"){
						name = "页面对象";
					}
					ht = ht+'<td class="center">'+name+'</td>';
					if(vomsContList[i].type == "10"){
						name = "专题";
					}else if(vomsContList[i].type == "11"){
						name = "内容组合";
					}else if(vomsContList[i].type == "20"){
						name = "大图内容";
					}else if(vomsContList[i].type == "21"){
						name = "多图内容";
					}
					
					ht = ht+'<td class="center">'+name+'</td>';
					ht = ht+'</tr>';
				}
				$('#vomsContTab tbody').html(ht);
			}
		},
		error : function(msg) {
			alert("失败了");
		}
	});
	
});
</script>
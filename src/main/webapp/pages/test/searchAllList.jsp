<%@ page language="java" pageEncoding="UTF-8" contentType="text/html;charset=utf-8"%>
<%@ include file="/common/taglibs.jsp" %>
<div class="row col-md-12">
		<div class="col-md-12" id="page-content">
			<h3 class="header smaller lighter red center-block">内容推荐接口测试</h3>
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
							<input type="text" id="id" class="col-xs-10 col-sm-12" name="userTag.id" />
						</div>
					</div>
					<div class="col-md-4">
						<label class="col-sm-2 control-label no-padding-right" for="start">分页开始:</label>
						<div class="col-sm-10">
							<input type="text" id="start" class="col-xs-10 col-sm-12" name="userTag.start" value="0" />
						</div>
					</div>
				</div>
				<div class="form-group">
					<div class="col-md-4">
						<label class="col-sm-2 control-label no-padding-right" for="limit">每页条数:</label>
						<div class="col-sm-10">
							<input type="text" id="limit" class="col-xs-10 col-sm-12" name="userTag.limit" value="10"  />
						</div>
					</div>
					<div class="col-md-4">
						<label class="col-sm-2 control-label no-padding-right" for="prdType">产品类型:</label>
						<div class="col-sm-10">
							<select class="col-xs-10 col-sm-12" id="prdType" name="userTag.prdType">
								<option value="MIGUVIDEO">咪咕视频</option>
								<option value="MIGUMOVIE">咪咕影院</option>
							</select>
						</div>
					</div>
					<div class="col-md-4">
						<label class="col-sm-2 control-label no-padding-right" for="ctVer">客户端版本号:</label>
						<div class="col-sm-10">
							<input type="text" id="ctVer" class="col-xs-10 col-sm-12" name="userTag.ctVer" value="v1.0"/>
						</div>
					</div>
				</div>
				<div class="form-group">
					<div class="col-md-4">
						<label for="catsInfoJson" class="col-sm-2 control-label no-padding-right">请求JSON串:</label>
						<div class="col-sm-10">
							<textarea class="col-xs-10 col-sm-12 " id="catsInfoJson" name="catsInfoJson" style="height:200px;"></textarea>
						</div>
					</div>
				</div>
				<div class="clearfix form-actions center-block col-md-12">
					<div class="col-sm-2 control-label no-padding-right"></div>
					<div class="col-sm-10">
						<button class="btn btn-info" type="button" id="subBtn">
							<i class="ace-icon fa fa-check bigger-110"></i> 提交
						</button>
						&nbsp; &nbsp; &nbsp;
						<button class="btn" type="reset">
							<i class="ace-icon fa fa-undo bigger-110"></i> 重置
						</button>
					</div>
				</div>
			</form>
			<div class="panel-body">	
                <div class="row">
                    <div class="col-xs-12">
                        <table id="grid-table"></table>

                        <div id="grid-pager"></div>
                    </div>
               </div>
            </div>
		</div>
	</div>
	
<div class="row col-md-12">
	<div class="col-md-12">
		<div class="table-header">
			pomsContList结果页面
		</div>
		<div>
			<table id="pomsContTab" class="table table-striped table-bordered table-hover">
				<thead>
					<tr>
						<th class="center">节目ID</th>
						<th class="center">节目名称</th>
					</tr>
				</thead>
				<tbody>
				</tbody>
			</table>
		</div>
	</div>
	
	<!-- 第二个 -->
	<div class="col-md-12">
		<div class="table-header">
			specialTopicList结果页面
		</div>
		<div>
			<table id="specialTopicTable" class="table table-striped table-bordered table-hover">
				<thead>
					<tr>
						<th class="center">名称</th>
						<th class="center">推荐对象ID</th>
						<th class="center">推荐对象</th>
						<th class="center">类型</th>
					</tr>
				</thead>
				<tbody>
				</tbody>
			</table>
		</div>
	</div>
	<!-- 第三个 -->
	<div class="col-md-12">
		<div class="table-header">
			combinedContList结果页面
		</div>
		<div>
			<table id="combinedContTab" class="table table-striped table-bordered table-hover">
				<thead>
					<tr>
						<th class="center">名称</th>
						<th class="center">推荐对象ID</th>
						<th class="center">推荐对象</th>
						<th class="center">类型</th>
					</tr>
				</thead>
				<tbody>
				</tbody>
			</table>
		</div>
	</div>
	<!-- 第四个 -->
	<div class="col-md-12">
		<div class="table-header">
			bigPicContList结果页面
		</div>
		<div>
			<table id="bigPicContTab" class="table table-striped table-bordered table-hover">
				<thead>
					<tr>
						<th class="center">名称</th>
						<th class="center">推荐对象ID</th>
						<th class="center">推荐对象</th>
						<th class="center">类型</th>
					</tr>
				</thead>
				<tbody>
				</tbody>
			</table>
		</div>
	</div>
	<!-- 第五个 -->
	<div class="col-md-12">
		<div class="table-header">
			multiPicContList结果页面
		</div>
		<div>
			<table id="multiPicContTab" class="table table-striped table-bordered table-hover">
				<thead>
					<tr>
						<th class="center">名称</th>
						<th class="center">推荐对象ID</th>
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
		//获取到subBtn的点击时间，加载5个list
		$('#subBtn').click(function(){
			var jsonData = $('#testForm').serialize();
			$.ajax({
				url : "${ctx}/recomd/testSearchAll.msp",
				type : "post",
				data: jsonData,
				async: false,
				success : function(msg) {
					$('#vomsContTab tbody').empty();
					var result = jQuery.parseJSON(msg);
					console.log(result);
					//给pomsContList结果页面添加数据
					if(result.success == true){
						var pomsContList = result.pomsCont;
						var ht = '';
						for(var i = 0;i<pomsContList.length;i++){
							ht = ht+'<tr>';
							ht = ht+'<td class="center">'+pomsContList[i].prdContId+'</td>';
							ht = ht+'<td class="center">'+pomsContList[i].contName+'</td>';
							ht = ht+'</tr>';
						} 
						$('#pomsContTab tbody').html(ht);
						
						//给specialTopicList页面添加数据
						var specialTopicList = result.specialTopic;
						ht = '';
						for(var i = 0;i<specialTopicList.length;i++){
							ht = ht+'<tr>';
							ht = ht+'<td class="center">'+specialTopicList[i].name+'</td>';
							ht = ht+'<td class="center">'+specialTopicList[i].objId+'</td>';
							ht = ht+'<td class="center">'+specialTopicList[i].objType+'</td>';
							ht = ht+'<td class="center">'+specialTopicList[i].type+'</td>';
							ht = ht+'</tr>';
						}
						$('#specialTopicTable tbody').html(ht);
						
						//给combinedContList页面添加数据
						var combinedContList = result.combinedCont;
						ht = '';
						for(var i = 0;i<combinedContList.length;i++){
							ht = ht+'<tr>';
							ht = ht+'<td class="center">'+combinedContList[i].name+'</td>';
							ht = ht+'<td class="center">'+combinedContList[i].objId+'</td>';
							ht = ht+'<td class="center">'+combinedContList[i].objType+'</td>';
							ht = ht+'<td class="center">'+combinedContList[i].type+'</td>';
							ht = ht+'</tr>';
						}
						$('#combinedContTab tbody').html(ht);
						
						//给bigPicContList页面添加数据
						var bigPicContList = result.bigPicCont;
						ht = '';
						for(var i = 0;i<bigPicContList.length;i++){
							ht = ht+'<tr>';
							ht = ht+'<td class="center">'+bigPicContList[i].name+'</td>';
							ht = ht+'<td class="center">'+bigPicContList[i].objId+'</td>';
							ht = ht+'<td class="center">'+bigPicContList[i].objType+'</td>';
							ht = ht+'<td class="center">'+bigPicContList[i].type+'</td>';
							ht = ht+'</tr>';
						}
						$('#bigPicContTab tbody').html(ht);
						
						//给multiPicContList页面添加数据
						var multiPicContList = result.multiPicCont;
						ht = '';
						for(var i = 0;i<multiPicContList.length;i++){
							ht = ht+'<tr>';
							ht = ht+'<td class="center">'+multiPicContList[i].name+'</td>';
							ht = ht+'<td class="center">'+multiPicContList[i].objId+'</td>';
							ht = ht+'<td class="center">'+multiPicContList[i].objType+'</td>';
							ht = ht+'<td class="center">'+multiPicContList[i].type+'</td>';
							ht = ht+'</tr>';
						}
						$('#multiPicContTab tbody').html(ht);
					}
				},
				error : function(msg) {
					console.log(msg);
					alert("失败了");
				}
			});
		});
</script>


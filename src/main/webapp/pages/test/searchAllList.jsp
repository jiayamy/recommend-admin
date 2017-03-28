<%@ page language="java" pageEncoding="UTF-8" contentType="text/html;charset=utf-8"%>
<%@ include file="/common/taglibs.jsp" %>
<div class="row col-md-12">
		<div class="col-md-12" id="page-content">
			<h3 class="header smaller lighter red center-block">综合推荐接口测试</h3>
			<form class="form-horizontal no-padding-bottom no-margin-bottom" role="form" action="${ctx}/recomd/testSearch.msp" method="post" id="testForm">
				<div class="form-group">
					<div class="col-md-4">
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
						<button class="btn" type="button" id="subBtn">
							<i class="ace-icon fa fa-search bigger-110"></i> 查询
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
			节目列表
		</div>
		<div>
			<table id="pomsContTab" class="table table-striped table-bordered table-hover">
				<thead>
					<tr>
						<th class="center">节目ID</th>
						<th class="center">内容ID</th>
						<th class="center">节目名称</th>
						<th class="center">CPID</th>
						<th class="center">一级分类</th>
						<th class="center">播控状态</th>
						<th class="center">产品包</th>
						<th class="center">发布状态</th>
						<th class="center">发布(新)状态</th>
						<th class="center">媒资类型</th>
						<th class="center">创建时间</th>
						<th class="center">更新时间</th>
						<th class="center">接口请求时间</th>
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
			专题列表
		</div>
		<div>
			<table id="specialTopicTable" class="table table-striped table-bordered table-hover">
				<thead>
					<tr>
						<th class="center">推荐对象ID</th>
						<th class="center">名称</th>
						<th class="center">推荐对象</th>
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
			内容组合列表
		</div>
		<div>
			<table id="combinedContTab" class="table table-striped table-bordered table-hover">
				<thead>
					<tr>
						<th class="center">推荐对象ID</th>
						<th class="center">名称</th>
						<th class="center">推荐对象</th>
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
			大图内容列表
		</div>
		<div>
			<table id="bigPicContTab" class="table table-striped table-bordered table-hover">
				<thead>
					<tr>
						<th class="center">推荐对象ID</th>
						<th class="center">名称</th>
						<th class="center">推荐对象</th>
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
			多图内容列表
		</div>
		<div>
			<table id="multiPicContTab" class="table table-striped table-bordered table-hover">
				<thead>
					<tr>
						<th class="center">推荐对象ID</th>
						<th class="center">名称</th>
						<th class="center">推荐对象</th>
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
					//处理结果
					var statusEnum = [
			    	                  {text: "待发布 ", value:"10"},
			    	                  {text: "发布中", value: "11"},
			    	                  {text: "发布成功", value: "12"},
			    	                  {text: "发布失败", value: "13"},
			    	                  {text: "待撤回", value: "20"},
			    	                  {text: "撤回中", value: "21"},
			    	                  {text: "撤回成功", value: "22"},
			    	                  {text: "撤回失败", value: "23"},
			    	                  {text: "无发布规则", value: "30"}
			    	                  ];
					var formTypeEnum =[
										{text: "剧集", value:"6"},
										{text: "子集", value: "7"},
										{text: "非剧集", value: "8"},
										{text: "专辑", value: "9"},
										{text: "内容集", value: "10"},
										{text: "单集", value: "11"},
										{text: "图册", value: "12"}
			    	                   ];
					//给pomsContList结果页面添加数据
					if(result.success == true){
						var pomsContList = result.pomsCont;
						var status = '';
						var sun;
						var ht = '';
						for(var i = 0;i<pomsContList.length;i++){
							ht = ht+'<tr>';
							if(pomsContList[i].prdContId == undefined){
								pomsContList[i].prdContId = null;
							}
							ht = ht+'<td class="center">'+pomsContList[i].prdContId+'</td>';
							if(pomsContList[i].contentId == undefined){
								pomsContList[i].contentId = "";
							}
							ht = ht+'<td class="center">'+pomsContList[i].contentId+'</td>';
							if(pomsContList[i].name == undefined){
								pomsContList[i].name = "";
							}
							ht = ht+'<td class="center">'+pomsContList[i].name+'</td>';
							if(pomsContList[i].cpId == undefined){
								pomsContList[i].cpId = "";
							}
							ht = ht+'<td class="center">'+pomsContList[i].cpId+'</td>';
							if(pomsContList[i].displayName == undefined){
								pomsContList[i].displayName = "";
							}
							ht = ht+'<td class="center">'+pomsContList[i].displayName+'</td>';
							status = pomsContList[i].bcStatus;
							if(pomsContList[i].bcStatus == 0){
								status = "未播控";
							}else if(pomsContList[i].bcStatus == 1){
								status = "播控通过";
							}else if (pomsContList[i].bcStatus == 2) {
								status = "播控拒绝";
							}
							if(pomsContList[i].bcStatus == undefined){
								status = "";
							}
							ht = ht+'<td class="center">'+status+'</td>';
							if(pomsContList[i].prdInfoName == undefined){
								pomsContList[i].prdInfoName = "";
							}
							ht = ht+'<td class="center">'+pomsContList[i].prdInfoName+'</td>';
							if(pomsContList[i].pubStatus != undefined){
								for (var j = 0; j < statusEnum.length; j++) {
									var k = statusEnum[j];
									if(pomsContList[i].pubStatus == k.value){
										pomsContList[i].pubStatus = k.text;
										ht = ht+'<td class="center">'+pomsContList[i].pubStatus+'</td>';
										break;
									}
								}
							}else{
								pomsContList[i].pubStatus = "";
								ht = ht+'<td class="center">'+pomsContList[i].pubStatus+'</td>';
							}
							
							if(pomsContList[i].publishNoVomsStatus != undefined){
								for (var j = 0; j < statusEnum.length; j++) {
									var k = statusEnum[j];
									if(pomsContList[i].publishNoVomsStatus == k.value){
										pomsContList[i].publishNoVomsStatus = k.text;
										ht = ht+'<td class="center">'+pomsContList[i].publishNoVomsStatus+'</td>';
										break;
									}
								}
							}else{
								ht = ht+"<td class=center></td>";
							}
							
							if(pomsContList[i].formType != undefined){
								for (var j = 0; j < formTypeEnum.length; j++) {
									var k = formTypeEnum[j];
									if(pomsContList[i].formType == k.value){
										pomsContList[i].formType = k.text;
										ht = ht+'<td class="center">'+pomsContList[i].formType+'</td>';
										break;
									}
								}
							}else{
								ht = ht+"<td class=center></td>";
							}
							if(pomsContList[i].createTime == undefined){
								pomsContList[i].createTime = "";
							}
							ht = ht+'<td class="center">'+pomsContList[i].createTime+'</td>';
							if(pomsContList[i].updateTime == undefined){
								pomsContList[i].updateTime = "";
							}
							ht = ht+'<td class="center">'+pomsContList[i].updateTime+'</td>';
							if(result.requestTime == undefined){
								result.requestTime = "";
							}
							ht = ht+'<td class="center">'+result.requestTime+ "MS" +'</td>';
							ht = ht+'</tr>';
						} 
						$('#pomsContTab tbody').html(ht);
						
						//给specialTopicList页面添加数据
						var specialTopicList = result.specialTopic;
						ht = '';
						var name = "";
						for(var i = 0;i<specialTopicList.length;i++){
							ht = ht+'<tr>';
							ht = ht+'<td class="center">'+specialTopicList[i].objId+'</td>';
							ht = ht+'<td class="center">'+specialTopicList[i].name+'</td>';
							name = specialTopicList[i].objType;
							if(specialTopicList[i].objType == "0"){
								name = "栏目";
							}else if(specialTopicList[i].objType == "1"){
								name = "展现对象";
							}else if(specialTopicList[i].objType == "101"){
								name = "页面对象";
							}
							ht = ht+'<td class="center">'+name+'</td>';
							ht = ht+'</tr>';
						}
						$('#specialTopicTable tbody').html(ht);
						
						//给combinedContList页面添加数据
						var combinedContList = result.combinedCont;
						ht = '';
						for(var i = 0;i<combinedContList.length;i++){
							ht = ht+'<tr>';
							ht = ht+'<td class="center">'+combinedContList[i].objId+'</td>';
							ht = ht+'<td class="center">'+combinedContList[i].name+'</td>';
							name = combinedContList[i].objType;
							if(combinedContList[i].objType == "0"){
								name = "栏目";
							}else if(combinedContList[i].objType == "1"){
								name = "展现对象";
							}else if(combinedContList[i].objType == "101"){
								name = "页面对象";
							}
							ht = ht+'<td class="center">'+name+'</td>';
							ht = ht+'</tr>';
						}
						$('#combinedContTab tbody').html(ht);
						
						//给bigPicContList页面添加数据
						var bigPicContList = result.bigPicCont;
						ht = '';
						for(var i = 0;i<bigPicContList.length;i++){
							ht = ht+'<tr>';
							ht = ht+'<td class="center">'+bigPicContList[i].objId+'</td>';
							ht = ht+'<td class="center">'+bigPicContList[i].name+'</td>';
							name = bigPicContList[i].objType;
							if(bigPicContList[i].objType == "0"){
								name = "栏目";
							}else if(bigPicContList[i].objType == "1"){
								name = "展现对象";
							}else if(bigPicContList[i].objType == "101"){
								name = "页面对象";
							}
							ht = ht+'<td class="center">'+name+'</td>';
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
							name = multiPicContList[i].objType;
							if(multiPicContList[i].objType == "0"){
								name = "栏目";
							}else if(multiPicContList[i].objType == "1"){
								name = "展现对象";
							}else if(multiPicContList[i].objType == "101"){
								name = "页面对象";
							}
							ht = ht+'<td class="center">'+name+'</td>';
							ht = ht+'</tr>';
						}
						$('#multiPicContTab tbody').html(ht);
					}
				},
				error : function(msg) {
					alert("失败了");
				}
			});
		});
</script>


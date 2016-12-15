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
						<button class="btn btn-info" type="button" onclick="subm()">
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
	<script type="text/javascript">
		 function subm(){
			var jsonData = $('#testForm').serialize();
			$('#grid-table').jqGrid('setGridParam',{
				postData : jsonData,
				page:1,
				mtype:"post"
			}).trigger("reloadGrid");
		} 
		var grid_selector = "#grid-table";
	    var pager_selector = "#grid-pager";
	    jQuery(document).ready(function(){
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
			$(window).on('resize.jqGrid', function () {
				$(grid_selector).jqGrid( 'setGridWidth', $("#page-content").width());
		    })
	    	jQuery(grid_selector).jqGrid({
	    		url:"${ctx}/recomd/testSystemSearch.msp",
		    	datatype: "json",//数据类型 json
		    	colNames:['节目ID','内容ID','节目名称','cpID','一级分类','播控状态','产品包','发布状态','发布(新)状态','创建时间','媒资类型','更新时间'],
				colModel:[
					{name:'prdContId',index:'prdContId', width:80,editable: false},
					{name:'contentId',index:'contentId', width:80,editable: false},
					{name:'name',index:'name', width:200,editable: false},
					{name:'cpId',index:'cpId', width:80,editable: false},
					{name:'displayName',index:'displayName', width:200,editable: false},
					{name:'bcStatus',index:'bcStatus', width:80,editable: false,
						formatter:function(cellvalue, options, rowObject){
							if(cellvalue == "0"){
								return "未播控"
							}else if (cellvalue == "1") {
								return "播控通过"
							}else if (cellvalue == "2") {
								return "播控拒绝"
							}
						},
						unformat:function(cellvalue){
							if(cellvalue == "未播控"){
								return "0"
							}else if (cellvalue == "播控通过") {
								return "1"
							}else if (cellvalue == "播控拒绝") {
								return "2"
							}
						}
					},
					{name:'prdInfoName',index:'prdInfoName', width:200,editable: false},
					{name:'pubStatus',index:'pubStatus', width:80,editable: false,
						formatter:function(cellvalue, options, rowObject){
							 for (var i = 0; i < statusEnum.length; i++) {
								var k = statusEnum[i];
								if(cellvalue == k.value){
									return k.text;
								}
							} 
						},
						unformat:function(cellvalue){
							 for (var i = 0; i < statusEnum.length; i++) {
								var k = statusEnum[i];
								if(cellvalue == k.text){
									return k.value;
								}
							}
						}
					},
					{name:'publishNoVomsStatus',index:'publishNoVomsStatus', width:200,editable: false,
						formatter:function(cellvalue, options, rowObject){
							 for (var i = 0; i < statusEnum.length; i++) {
								var k = statusEnum[i];
								if(cellvalue == k.value){
									return k.text;
								}
							} 
						},
						unformat:function(cellvalue){
							 for (var i = 0; i < statusEnum.length; i++) {
								var k = statusEnum[i];
								if(cellvalue == k.text){
									return k.value;
								}
							}
						}
					},
					{name:'createTime',index:'createTime', width:200,editable: false},
					{name:'formType',index:'formType', width:80,editable: false,
						formatter:function(cellvalue, options, rowObject){
							 for (var i = 0; i < formTypeEnum.length; i++) {
								var k = formTypeEnum[i];
								if(cellvalue == k.value){
									return k.text;
								}
							} 
						},
						unformat:function(cellvalue){
							 for (var i = 0; i < formTypeEnum.length; i++) {
								var k = formTypeEnum[i];
								if(cellvalue == k.text){
									return k.value;
								}
							}
						}
					
					},
					{name:'updateTime',index:'updateTime', width:200,editable: false}
				], 
		    	jsonReader:{
		    		root: "root",
		    		total: "count",
		    		records: "total",
		    		repeatitems: true,  //表示返回的数据标签是否可重复
	    		},
	    		shrinkToFit:false,//设置列宽，表格宽度为设置宽度，列宽度不会重新计算，使用colModel中定义的值
	    		rowNum:20,
				rowList:[20,100,500,1000],
				pager : pager_selector,
		    	multiselect: true,//支持多项选择
		    	viewrecords:true,//定义是否要显示总记录数
		    	emptyrecords:"无记录",
		    	recordtext:"{0}-{1} 共{2}条",
		    	pgtext : "{0} 共{1}页",  
		    	caption: "节目列表",//列表标题
		    	loadComplete : function() {
				}
	    	});
			$(grid_selector).jqGrid( 'setGridWidth', $("#page-content").width() );
			$("#grid-pager_center").css("display","none");
		});
	</script>
</body>
</html>
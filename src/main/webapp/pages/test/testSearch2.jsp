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
			$(window).on('resize.jqGrid', function () {
				$(grid_selector).jqGrid( 'setGridWidth', $("#page-content").width() );
		    })
	    	jQuery(grid_selector).jqGrid({
	    		url:"${ctx}/recomd/testSearch.msp",
	    		mtype:"get",
	    		postData:{},
		    	datatype: "json",//数据类型 json
		    	colNames:['contName','prdContId'],
				colModel:[
					{name:'contName',index:'contName', width:150,editable: false,editoptions:{size:"20",maxlength:"30"}},
					{name:'prdContId',index:'prdContId', width:150,editable: false,editoptions:{size:"20",maxlength:"30"}},
				], 
		    	jsonReader:{
		    		root: "root",
		    		total: "count",
		    		records: "total",
		    		repeatitems: true,  //表示返回的数据标签是否可重复
	    		},
	    		shrinkToFit:false,//设置列宽，表格宽度为设置宽度，列宽度不会重新计算，使用colModel中定义的值
	    		rowNum:1000,
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
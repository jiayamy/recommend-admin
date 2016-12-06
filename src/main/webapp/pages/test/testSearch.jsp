<%@ page language="java" pageEncoding="UTF-8" contentType="text/html;charset=utf-8"%>
<%@ include file="/common/taglibs.jsp" %>
	<div class="row col-md-12">
		<div class="col-md-12" id="page-content">
			<h3 class="header smaller lighter red center-block">内容推荐接口测试</h3>
			<form class="form-horizontal no-padding-bottom no-margin-bottom" role="form" action="${ctx}/recomd/testSearch.msp" method="post" id="testForm">
				<div class="form-group">
					<div class="col-md-6">
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
			$.ajax({
				url : "${ctx}/recomd/testSearch.msp",
				type : "post",
				data:$('#testForm').serialize(),
				success : function(msg) {
					pageInit(msg);
				},
				error : function(msg) {
					console.log(msg);
					alert("失败了");
				}
			});
		}
		function pageInit(msg){
			var resultJson = eval("("+ msg +")");
			var grid_data =resultJson.root;
			var grid_selector = "#grid-table";
			var pager_selector = "#grid-pager";
			
			//resize to fit page size
			$(window).on('resize.jqGrid', function () {
				$(grid_selector).jqGrid( 'setGridWidth', $("#page-content").width() );
		    })
			//resize on sidebar collapse/expand
			var parent_column = $(grid_selector).closest('[class*="col-"]');
			$(document).on('settings.ace.jqGrid' , function(ev, event_name, collapsed) {
				if( event_name === 'sidebar_collapsed' || event_name === 'main_container_fixed' ) {
					//setTimeout is for webkit only to give time for DOM changes and then redraw!!!
					setTimeout(function() {
						$(grid_selector).jqGrid( 'setGridWidth', parent_column.width() );
					}, 0);
				}
		    })
			jQuery(grid_selector).jqGrid({
				data: grid_data,
				datatype: "local",
				height: 200,
				colNames:['contName','prdContId'],
				colModel:[
					{name:'contName',index:'contName', width:150,editable: false,editoptions:{size:"20",maxlength:"30"}},
					{name:'prdContId',index:'prdContId', width:150,editable: false,editoptions:{size:"20",maxlength:"30"}},
				], 
				viewrecords : true,
				rowNum:10,
				rowList:[10,20,30],
				pager : pager_selector,
				
				multiselect: true,
		        multiboxonly: true,
		
				loadComplete : function() {
					var table = this;
					setTimeout(function(){
						updatePagerIcons(table);
					}, 0);
				},
				caption: "TestResult"
			});
			$(window).triggerHandler('resize.jqGrid');//trigger window resize to make the grid get the correct size
			//switch element when editing inline
			function aceSwitch( cellvalue, options, cell ) {
				setTimeout(function(){
					$(cell) .find('input[type=checkbox]')
						.addClass('ace ace-switch ace-switch-5')
						.after('<span class="lbl"></span>');
				}, 0);
			}
			//enable datepicker
			function pickDate( cellvalue, options, cell ) {
				setTimeout(function(){
					$(cell) .find('input[type=text]')
							.datepicker({format:'yyyy-mm-dd' , autoclose:true}); 
				}, 0);
			}
			
			function updatePagerIcons(table) {
		            var replacement =
		            {
		                'ui-icon-seek-first' : 'ace-icon fa fa-angle-double-left bigger-140',
		                'ui-icon-seek-prev' : 'ace-icon fa fa-angle-left bigger-140',
		                'ui-icon-seek-next' : 'ace-icon fa fa-angle-right bigger-140',
		                'ui-icon-seek-end' : 'ace-icon fa fa-angle-double-right bigger-140'
		            };
		            $('.ui-pg-table:not(.navtable) > tbody > tr > .ui-pg-button > .ui-icon').each(function(){
		                var icon = $(this);
		                var $class = $.trim(icon.attr('class').replace('ui-icon', ''));
	
		                if($class in replacement) icon.attr('class', 'ui-icon '+replacement[$class]);
		            })
		        }
			$(document).one('ajaxloadstart.page', function(e) {
	            $(grid_selector).jqGrid('GridUnload');
	            $('.ui-jqdialog').remove();
	        });
			
		}
	</script>
</body>
</html>
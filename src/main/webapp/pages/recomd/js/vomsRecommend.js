jQuery(function($) {
	initGridTable();
});
function change(){
	var flag = $("#searchTitle").attr("flag");
	 if(flag=='show'){
		 $("#searchDiv").slideUp();
		 $("#searchTitle > span:first").text("展开");
		 $("#searchTitle").find("i").removeClass("fa-minus").addClass("fa-plus").end().attr("flag","hide");
	 }else{
		 $("#searchDiv").slideDown();
		 $("#searchTitle > span:first").text("收起");
		 $("#searchTitle").find("i").removeClass("fa-plus").addClass("fa-minus").end().attr("flag","show");
	 }
};
function initGridTable() {
	//start grid table
	var grid_selector = "#grid-table";
	var pager_selector = "#grid-pager";
	
	//resize to fit page size
	$(window).on('resize.jqGrid', function () {
		$(window).on('resize.jqGrid', function () {
			/*if(window.screen.width <= 1366){
				$(grid_selector).jqGrid('setGridWidth', $(".page-content").width()).jqGrid('setGridHeight', $(window).height()-305);
			}else {
				$(grid_selector).jqGrid('setGridWidth', $(".page-content").width()).jqGrid('setGridHeight', $(window).height()-340);
			}*/
			var tableHeight = 0;
			if(window.screen.width <= 1366){
				tableHeight = $(window).height()-300;
			}else {
				tableHeight = $(window).height()-340;
			}
			$(grid_selector).jqGrid('setGridWidth', $(".page-content").width());
			$(grid_selector).parents('div.ui-jqgrid-bdiv').css({'height':'auto','min-height': tableHeight + 'px'});
	    })
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
	 
	$(grid_selector).jqGrid({
		//direction: "rtl",
        url: webroot + "recomd/listVomsRecommend.msp",
		datatype: "json",
		/*height: 350,*/		
		colNames:["序号", "节目ID",  "推荐对象", "类型", "产品", "标签名","状态",
		          "创建者", "创建时间", "修改者", "修改时间"
		],
		colModel:[
            {name:'id',index:'id', width:60,sortable : true},
			{name:'objId',index:'objId', width:80, sortable : true},
			{name:'objType',index:'objType', width:100,sortable : true,
				formatter:function(cellvalue, options, rowObject){
					var o = $("#objType_"+cellvalue);
					if(o.length == 0){
						return cellvalue;
					}
					return o.val();
				},
				unformat:function(cellvalue){
					var o = $("#objType_vals input[value='"+cellvalue +"']");
					if(o.length == 0){
						return cellvalue;
					}
					if(o[0].id){
						return o[0].id.split("_")[1]
					}
					return cellvalue;
				}
			},			
			{name:'type',index:'type', width:100,sortable : true,
				formatter:function(cellvalue, options, rowObject){
					var o = $("#info_"+cellvalue);
					if(o.length == 0){
						return cellvalue;
					}
					return o.val();
				},
				unformat:function(cellvalue){
					var o = $("#info_vals input[value='"+cellvalue +"']");
					if(o.length == 0){
						return cellvalue;
					}
					if(o[0].id){
						return o[0].id.split("_")[1]
					}
					return cellvalue;
				}
			},
			
			{name:'prdType',index:'prdType', width:150, sortable : true},
			{name:'labelInfo',index:'labelInfo', width:200,sortable : true},
			{name:'isRecommd',index:'isRecommd', width:100,sortable : true,
				formatter:function(cellvalue, options, rowObject){
					if(cellvalue == "1"){
						return "推荐"
					}else{
						return "撤回"
					}
				},
				unformat:function(cellvalue){
					if(cellvalue == "推荐"){
						return "1"
					}else{
						return "0"
					}
				}
			},
			{name:'creator',index:'creator', width:100,sortable : true},
			{name:'createTime',index:'createTime', width:130,sortable : true},
			{name:'updator',index:'updator', width:100,sortable : true},
			{name:'updateTime',index:'updateTime', width:130,sortable : true}
		], 
		sortable : true,
		viewrecords : true,
		rowNum:20,
		rowList:[20,100,500,1000],
		pager : pager_selector,
		altRows: true,
		//toppager: true,
		jsonReader:{
			total: 'total',
			records:'records',
			root:'rows',
			repeatitems:true
		},
		multiselect: true,
		//multikey: "ctrlKey",
        multiboxonly: true,
		loadComplete : function(data) {
			$(grid_selector).setGridWidth($(".page-content").width()); 
			
			var table = this;
			var pageNow = $(grid_selector).jqGrid('getGridParam', 'page');//当前页
			var totalPage = data.total;//总页数
			if(totalPage == 0){
				$(".ui-pg-input").val(0);
				$("td.ui-corner-all").addClass("ui-state-disabled");
			}else{
				if(pageNow > totalPage){
					var pageInfo = ($(".ui-pg-selbox").val()*(totalPage-1) + 1) + " - " + data.records + "\u3000共  " + data.records + " 条";
					if(totalPage == 1){
						$("td.ui-corner-all").addClass("ui-state-disabled");
					}
					$(".ui-pg-input").val(totalPage);
					$(grid_selector).jqGrid('setGridParam',{page:totalPage});
					$("#grid-pager_right > div").text(pageInfo);
					$("#next_grid-pager").addClass("ui-state-disabled").next().addClass("ui-state-disabled");
				}
			}
			
			setTimeout(function(){
				styleCheckbox(table);				
				updateActionIcons(table);
				updatePagerIcons(table);
				enableTooltips(table);
			}, 0);
		}
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
	
	//navButtons
	$(grid_selector).jqGrid('navGrid',pager_selector,
		{ 	//navbar options
			edit: false,
			editicon : 'ace-icon fa fa-pencil blue',
			add: false,
			addicon : 'ace-icon fa fa-plus-circle purple',
			del: false,
			delicon : 'ace-icon fa fa-trash-o red',
			search: false,
			searchicon : 'ace-icon fa fa-search orange',
			refresh: true,
			refreshicon : 'ace-icon fa fa-refresh orange bigger-170',
			view: false,
			viewicon : 'ace-icon fa fa-search-plus grey',
		},		
		{
			//search form
			recreateForm: true,
			afterShowSearch: function(e){
				var form = $(e[0]);
				form.closest('.ui-jqdialog').find('.ui-jqdialog-title').wrap('<div class="widget-header" />')
				style_search_form(form);
			},
			afterRedraw: function(){
				style_search_filters($(this));
			},
			multipleSearch: true,
			/**
			multipleGroup:true,
			showQuery: true
			*/
		},
		{
			//view record form
			recreateForm: true,
			beforeShowForm: function(e){
				var form = $(e[0]);
				form.closest('.ui-jqdialog').find('.ui-jqdialog-title').wrap('<div class="widget-header" />')
			}
		}
	)
	
	function style_edit_form(form) {
		//enable datepicker on "sdate" field and switches for "stock" field
		form.find('input[name=sdate]').datepicker({format:'yyyy-mm-dd' , autoclose:true})
		
		form.find('input[name=stock]').addClass('ace ace-switch ace-switch-5').after('<span class="lbl"></span>');

		//update buttons classes
		var buttons = form.next().find('.EditButton .fm-button');
		buttons.addClass('btn btn-sm').find('[class*="-icon"]').hide();//ui-icon, s-icon
		buttons.eq(0).addClass('btn-primary').prepend('<i class="ace-icon fa fa-check"></i>');
		buttons.eq(1).prepend('<i class="ace-icon fa fa-times"></i>')
		
		buttons = form.next().find('.navButton a');
		buttons.find('.ui-icon').hide();
		buttons.eq(0).append('<i class="ace-icon fa fa-chevron-left"></i>');
		buttons.eq(1).append('<i class="ace-icon fa fa-chevron-right"></i>');		
	}

	
	function style_search_filters(form) {
		form.find('.delete-rule').val('X');
		form.find('.add-rule').addClass('btn btn-xs btn-primary');
		form.find('.add-group').addClass('btn btn-xs btn-success');
		form.find('.delete-group').addClass('btn btn-xs btn-danger');
	}
	function style_search_form(form) {
		var dialog = form.closest('.ui-jqdialog');
		var buttons = dialog.find('.EditTable')
		buttons.find('.EditButton a[id*="_reset"]').addClass('btn btn-sm btn-info').find('.ui-icon').attr('class', 'ace-icon fa fa-retweet');
		buttons.find('.EditButton a[id*="_query"]').addClass('btn btn-sm btn-inverse').find('.ui-icon').attr('class', 'ace-icon fa fa-comment-o');
		buttons.find('.EditButton a[id*="_search"]').addClass('btn btn-sm btn-purple').find('.ui-icon').attr('class', 'ace-icon fa fa-search');
	}
	//unlike navButtons icons, action icons in rows seem to be hard-coded
	//you can change them like this in here if you want
	
	
	//replace icons with FontAwesome icons like above
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

	function enableTooltips(table) {
		$('.navtable .ui-pg-button').tooltip({container:'body'});
		$(table).find('.ui-pg-div').tooltip({container:'body'});
	}

	//var selr = jQuery(grid_selector).jqGrid('getGridParam','selrow');

	$(document).one('ajaxloadstart.page', function(e) {
		$(grid_selector).jqGrid('GridUnload');
		$('.ui-jqdialog').remove();
	});
}


function listVomsRecommend(e) {
	var labelInfo = $("#s_labelInfo").val();
	var prdType = $("#s_prdType").val();
	var objId = $("#s_objId").val();
	var objType = $("#s_objType").val();
	var type = $("#s_type").val();
	var isRecommend = $("#s_isRecommend").val();
	var jsonData = { 
		'labelInfo':labelInfo, 
		'prdType':prdType,
		'objId':objId,
		'objType':objType,
		'type':type,
		'isRecommend':isRecommend
	};
	$('#grid-table').jqGrid('setGridParam', {
		url : webroot + "recomd/listVomsRecommend.msp",
		postData : jsonData,
		page:1,
		mtype: "post"
	}).trigger("reloadGrid");
	setBtnDisable(e)//set search button disabled and remove the disabled attribute a few seconds later


}
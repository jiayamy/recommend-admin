<%@ page language="java" pageEncoding="UTF-8" contentType="text/html;charset=utf-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html>
<html>
	<head>
		<title>个性化推荐管理系统 </title>
		<meta name="description" content="wondertek media asset management system" />
		<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0" />
		<link rel="icon" href="./ace/assets/img/favicon.ico" type="image/x-icon" />
		<link rel="shortcut icon" href="./ace/assets/img/favicon.ico" type="image/x-icon" />
		<!-- bootstrap & fontawesome -->
		<link rel="stylesheet" href="./ace/assets/css/bootstrap.css" />
		<link rel="stylesheet" href="./ace/assets/css/bootstrap-datetimepicker.css"/>
		<link rel="stylesheet" href="./ace/assets/css/bootstrap-editable.css"/>
		<link rel="stylesheet" href="./ace/assets/css/bootstrap-multiselect.css" />
		
		<!-- page specific plugin styles -->
		<link rel="stylesheet" href="./ace/assets/css/datepicker.css" />
		<link rel="stylesheet" href="./ace/assets/css/ui.jqgrid.css" />
		<link rel="stylesheet" href="./ace/assets/css/chosen.css" />
		<link rel="stylesheet" href="./ace/assets/css/dropzone.css" />
		<link rel="stylesheet" href="./ace/assets/css/colorbox.css"/>
		<link rel="stylesheet" href="./ace/assets/css/select2.css"/>
		
		<link rel="stylesheet" href="./ace/assets/css/jquery-ui.css" />
		<link rel="stylesheet" href="./ace/assets/css/jquery-ui.custom.css"/>
		<link rel="stylesheet" href="./ace/assets/css/jquery.gritter.css"/>
		
		<!-- jquery plugin styles -->
		<link rel="stylesheet" href="./ace/plugins/css/jquery.marquee.css" type="text/css"/>
		<link rel="stylesheet" href="./ace/plugins/css/jquery-jcrop/jquery.Jcrop.css"/>
		<link rel="stylesheet" href="./ace/plugins/css/autocomplete/jquery.autocomplete.css"/>
		<link rel="stylesheet" href="./ace/plugins/css/css.css"/>
		<link rel="stylesheet" href="./ace/plugins/css/lobibox.css" />
		<link rel="stylesheet" href="./ace/plugins/css/autoSearchText.css" />
		<link rel="stylesheet" href="./ace/plugins/css/zyFile/zyUpload.css"/>
		<!-- Ztree -->
		<link rel="stylesheet" href="./ace/plugins/css/ztree/demo.css"/>
		<link rel="stylesheet" href="./ace/plugins/css/ztree/zTreeStyle.css"/>
		
		<link rel="stylesheet" href="./ace/assets/css/font-awesome.css" />
		<link rel="stylesheet" href="./ace/assets/css/ace-fonts.css" />
		
		<!-- ace styles -->
		<link rel="stylesheet" href="./ace/assets/css/ace.css" class="ace-main-stylesheet" id="main-ace-style" />
		<!-- <link rel="stylesheet" href="./ace/assets/css/ace-skins.css"/> -->
		
		<!-- mam comon css -->
		<link rel="stylesheet" href="./ace/assets/css/ace-ext.css"/>
		<link rel="stylesheet" href="./common/mam.css"/>
		
		<!--video标签的样式 -->
		<link rel="stylesheet" href="./ace/plugins/css/video/video-js.css"/>
		
		<!-- help page style -->
		<!-- <link rel="stylesheet" href="./ace/assets/css/ace.onpage-help.css" />
		<link rel="stylesheet" href="./ace/docs/assets/js/themes/sunburst.css" /> -->
		<!--[if lte IE 9]>
		<link rel="stylesheet" href="./ace/assets/css/ace-part2.css" class="ace-main-stylesheet" />
		<![endif]-->

		<!--[if lte IE 9]>
		 <link rel="stylesheet" href="./ace/assets/css/ace-ie.css" />
		<![endif]-->

		<!-- inline styles related to this page -->

		<!-- ace settings handler -->
		<script src="./ace/assets/js/ace-extra.js"></script>

		<!-- HTML5shiv and Respond.js for IE8 to support HTML5 elements and media queries -->

		<!--[if lte IE 8]>
		<script src="./ace/assets/js/html5shiv.js"></script>
		<script src="./ace/assets/js/respond.js"></script>
		<![endif]-->
		<!--[if !IE]> -->
		<script type="text/javascript">
			window.jQuery || document.write("<script src='./ace/assets/js/jquery.js'>"+"<"+"/script>");
		</script>
		<!--  <![endif]-->

		<!--[if IE]>
		<script type="text/javascript">
			window.jQuery || document.write("<script src='./ace/assets/js/jquery1x.js'>"+"<"+"/script>");
		</script>
		<![endif]-->

	</head>

	<body class="no-skin"><!-- oncontextmenu="return false" -->
		<script >
		var __webroot__ ='<c:url value="/"/>';
		var webroot = '<c:url value="/"/>';
		var preview_source = '<%=session.getAttribute("_source_preview") %>';
		var preview_store_tmp = '<%=session.getAttribute("_store_tmp_preview") %>';
		var preview_store = '<%=session.getAttribute("_store_preview") %>';
		var preview_desp = '<%=session.getAttribute("_despository_preview") %>';
		var context_path = '<%=request.getContextPath()%>/';
		var onerrorImg = __webroot__+'common/images/imgfail.png';
		var Mam=[];
		Mam.star={};
		</script>
		
		<button style="display:none;" class="btn btn-md" id="alertButton"></button><!-- 提示信息要用到 -->
		<!-- #section:basics/navbar.layout -->
		<jsp:include page="/pages/head.jsp" />
		<!-- /section:basics/navbar.layout -->
		
		<div class="main-container" id="main-container">
		<script type="text/javascript">
				try{ace.settings.check('main-container' , 'fixed')}catch(e){}
		</script>
			<!-- #section:basics/sidebar -->
			<jsp:include page="/pages/left.jsp">
				<jsp:param value="home" name="curNav" />
			</jsp:include>
			<!-- /section:basics/sidebar -->
			
			<!-- .main-content -->
			<div class="main-content">
				<jsp:include page="/pages/settings.jsp" />
				<div class="main-content-inner" id="main_page">
					<!-- jsp:include page="/pages/right.jsp" /-->
				</div>
			 </div>
			<!-- /.main-content -->
			<jsp:include page="/pages/footer.jsp" />
		</div>
	<!-- /.main-container -->
		<!-- basic scripts -->
		<script type="text/javascript">
			if('ontouchstart' in document.documentElement) document.write("<script src='./ace/assets/js/jquery.mobile.custom.js'>"+"<"+"/script>");
		</script>
		<script src="./ace/assets/js/bootstrap.js"></script>
		
		<!-- page specific plugin scripts -->
		
		<!--[if lte IE 8]>
		  <script src="./ace/assets/js/excanvas.js"></script>
		<![endif]-->
		
		<script src="./ace/assets/js/jquery-ui.custom.js"></script>
		<script src="./ace/assets/js/jquery.ui.touch-punch.js"></script>
		<script src="./ace/assets/js/flot/jquery.flot.js"></script>
		<script src="./ace/assets/js/flot/jquery.flot.pie.js"></script>
		<script src="./ace/assets/js/flot/jquery.flot.resize.js"></script>
		
		<!-- ace scripts -->
		<script src="./ace/assets/js/ace/elements.scroller.js"></script>
		<script src="./ace/assets/js/ace/elements.colorpicker.js"></script>
		<script src="./ace/assets/js/ace/elements.fileinput.js"></script>
		<script src="./ace/assets/js/ace/elements.typeahead.js"></script>
		<script src="./ace/assets/js/ace/elements.wysiwyg.js"></script>
		<script src="./ace/assets/js/ace/elements.spinner.js"></script>
		<script src="./ace/assets/js/ace/elements.treeview.js"></script>
		<script src="./ace/assets/js/ace/elements.wizard.js"></script>
		<script src="./ace/assets/js/ace/elements.aside.js"></script>
		<script src="./ace/assets/js/ace/ace.js"></script>
		<script src="./ace/assets/js/ace/ace.ajax-content.js"></script>
		<script src="./ace/assets/js/ace/ace.touch-drag.js"></script>
		<script src="./ace/assets/js/ace/ace.sidebar.js"></script>
		<script src="./ace/assets/js/ace/ace.sidebar-scroll-1.js"></script>
		<script src="./ace/assets/js/ace/ace.submenu-hover.js"></script>
		<script src="./ace/assets/js/ace/ace.widget-box.js"></script>
		<script src="./ace/assets/js/ace/ace.settings.js"></script>
		<script src="./ace/assets/js/ace/ace.settings-rtl.js"></script>
		<script src="./ace/assets/js/ace/ace.settings-skin.js"></script>
		<script src="./ace/assets/js/ace/ace.widget-on-reload.js"></script>
		<script src="./ace/assets/js/ace/ace.searchbox-autocomplete.js"></script>
		
		<script type="text/javascript"> ace.vars['base'] = '<c:url value="/"/>'; </script>
		<script src="./ace/assets/js/ace/elements.onpage-help.js"></script>
		<script src="./ace/assets/js/ace/ace.onpage-help.js"></script>
		<script src="./ace/docs/assets/js/rainbow.js"></script>
		<script src="./ace/docs/assets/js/language/generic.js"></script>
		<script src="./ace/docs/assets/js/language/html.js"></script>
		<script src="./ace/docs/assets/js/language/css.js"></script>
		<script src="./ace/docs/assets/js/language/javascript.js"></script>
		
		<!-- jqgrid -->
		<script src="./ace/assets/js/jqGrid/jquery.jqGrid.src.js"></script>
		<script src="./ace/assets/js/jqGrid/i18n/grid.locale-cn.js"></script>
		<!-- jquery plugin -->
		<script src="./ace/assets/js/jquery-ui.js"></script>
		<script src="./ace/assets/js/jquery-ui.custom.js"></script>
		<script src="./ace/assets/js/jquery.ui.touch-punch.js"></script>
		<script src="./ace/assets/js/jquery.gritter.js"></script>
		<script src="./ace/assets/js/jquery.easypiechart.js"></script>
		<!-- <script src="./ace/assets/js/jquery.sparkline.js"></script> -->
		<script src="./ace/assets/js/typeahead.jquery.js"></script>
		<script src="./ace/assets/js/jquery.hotkeys.js"></script>
		<script src="./ace/assets/js/bootstrap-wysiwyg.js"></script>
		<script src="./ace/assets/js/fuelux/fuelux.wizard.js"></script>
		<script src="./ace/assets/js/select2.js"></script>
		<script src="./ace/assets/js/x-editable/bootstrap-editable.js"></script>
		<script src="./ace/assets/js/x-editable/ace-editable.js"></script>
		<script src="./ace/assets/js/jquery.colorbox.js"></script>
		<script src="./ace/assets/js/jquery.maskedinput.js"></script>
		<script src="./ace/assets/js/chosen.jquery.js"></script>
		<script src="./ace/plugins/js/jquery-pin/jquery.pin.min.js"></script> 
		<script src="./ace/plugins/js/json/json2.min.js"></script>
		<script src="./ace/plugins/js/waypoints/jquery.waypoints.min.js"></script>
		<script src="./ace/plugins/js/jquery-jcrop/jquery.Jcrop.js"></script>
		<script src="./ace/plugins/js/autocomplete/jquery.autocomplete.js"></script>

		<script src="./ace/plugins/js/msgTips.js"></script>
		<%--<script src="./ace/plugins/js/echarts/dist/echarts-all.js"></script>--%>
		<script src="./ace/plugins/js/lobibox.js"></script>
		
		<!-- ztree -->
		<script src="./ace/plugins/js/ztree/jquery.ztree.core-3.5.js"></script>
		<script src="./ace/plugins/js/ztree/jquery.ztree.excheck-3.5.js"></script>
		<script src="./ace/plugins/js/ztree/jquery.ztree.exedit-3.5.js"></script>
		<script src="./ace/plugins/js/ztree/jquery.ztree.exhide-3.5.min.js"></script>

		<script src="./ace/assets/js/date-time/moment-with-locales.js"></script><!--时间获取和处理js -->
		<script src="./ace/assets/js/date-time/moment.js"></script>
		<script src="./ace/assets/js/date-time/daterangepicker.js"></script>
		<script src="./ace/assets/js/date-time/bootstrap-datepicker.js"></script>
		<script src="./ace/assets/js/date-time/bootstrap-datetimepicker.js"></script>
		<script src="./ace/assets/js/date-time/todayafternoselect-bootstrap-datepicker.js"></script>
		
		
		<script src="./ace/assets/js/dropzone.js"></script>
		<script src="./ace/assets/js/bootbox.js"></script>
		<script src="./ace/assets/js/fuelux/fuelux.spinner.js"></script>
		<script src="./ace/plugins/js/resize.js"></script>
		
		<script src="./ace/plugins/js/mytag/elements.mytypeahead.js"></script>
		<script src="./ace/plugins/js/mytag/bootstrap-mytag.js"></script>
		
		<!--双向 选择框 -->
		<script src="./ace/assets/js/jquery.bootstrap-duallistbox.js"></script>
		<script src="./ace/assets/js/fuelux/fuelux.tree.js"></script>
		<script src="./ace/plugins/js/mouseRightmenu/jquery-smartMenu.js"></script>
		<!-- 自定义时间控件 -->
		<script src="./ace/plugins/js/echarts/dist/echarts.js"></script>
		<!-- 首页 -->
		<script src="./pages/util.js"></script>
		<script src="./pages/main.js"></script>
		<!-- 点播 -->
		<script src="./ace/plugins/js/zyFile/zyFile.js"></script>
		<script src="./ace/plugins/js/zyFile/zyUpload.js"></script>
		
		<!--多频道字幕滚动 -->
		<script src="./ace/plugins/js/jquery.marquee.js"></script>
		
		<script type="text/javascript" charset="utf-8" src="./ueditor/ueditor.config.js"></script>

		<!-- inline scripts related to this page -->
		<script type="text/javascript">
			//TODO 暂时强制不支持touch
			ace.vars['touch'] = false;
		
			function showmyapplication() {
				$('#myapplicationmodal').modal({
					show : true,
					backdrop : "static"
				});
			}

			function showmywork() {
				$('#myworkmodal').modal({
					show : true,
					backdrop : "static"
				});
			}
			jQuery(function($) {

				$('.widget-container-col').sortable({
					connectWith : '.widget-container-col',
					items : '> .widget-box',
					handle : ace.vars['touch'] ? '.widget-header' : false,
					cancel : '.fullscreen',
					opacity : 0.8,
					revert : true,
					forceHelperSize : true,
					placeholder : 'widget-placeholder',
					forcePlaceholderSize : true,
					tolerance : 'pointer',
					start : function(event, ui) {
						//when an element is moved, it's parent becomes empty with almost zero height.
						//we set a min-height for it to be large enough so that later we can easily drop elements back onto it
						ui.item.parent().css({
							'min-height' : ui.item.height()
						});
						//ui.sender.css({'min-height':ui.item.height() , 'background-color' : '#F5F5F5'})
					},
					update : function(event, ui) {
						ui.item.parent({
							'min-height' : ''
						});
						//p.style.removeProperty('background-color');
					}
				});

			});

			//slidebar
			$(document).ready(function() {
				$.ajaxSetup({
					cache : false
				//关闭AJAX相应的缓存
				});
				$('#left_menu a').each(function(n, v) {
					var url = $(v).attr("menu-url");
					if (url) {
						$(v).unbind('click').click(function(e) {
							openPage($(this).attr("menu-url"));

							$('#left_menu .active').each(function(nn, vv) {
								$(vv).removeClass("active");
							});
							$(this).parents("li").each(function(nn, vv) {
								$(vv).addClass("active");
							});
						});
					}
				});
			});
			function openPage(url) {
				var p = $("#main_page");
				if (p.length > 0) {
					p.load(url, function(response, status, xhr) {
						if (status != "success") {
							if(checkSession) {
								checkSession();
							}
						}
					});
				}
			}
			
			$("#lang-box").find("button:visible").click(function() {
				$(this).siblings().show(10, function() {
					$("#lang-box").find("button").click(function() {
						$(this).show().siblings().hide();
					});
				});
			});
			
			<cas:havePerm url="/checkSession.htm">
			//定时检查session是否过期 2分钟执行一次 
			var tmchecksession = setInterval("checkSession()", 120000);
			var checksessioncount = 0;
			function checkSession() {
				$.ajax({url: webroot + "checkSession.htm"})
					.done(function (data) {
						checksessioncount = 0;
					}).fail(function( jqXHR, textStatus ) {
					  checksessioncount ++;
					  if(checksessioncount > 3) {
						  checksessioncount = 0;
						  clearInterval(tmchecksession);
						  Lobibox.confirm({ 
						       title:"提示",
						       msg: "您的登录已超时，系统即将刷新，是否重新登录？",
						       callback: function ($this, type, ev) {
						               if (type === 'yes') { 
						            	   location.reload();
						               }
						          } 
						});
					  }
				  });
			}
			</cas:havePerm>
			
			try {
				ace.settings.check('navbar', 'fixed');
				ace.settings.check('sidebar', 'fixed');
			} catch (e) {
			}
		
		</script>
	</body>
</html>

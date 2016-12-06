<%@ page language="java" pageEncoding="UTF-8"
	contentType="text/html;charset=utf-8"%>
<%@ include file="/common/taglibs.jsp"%>
<div id="sidebar" class="sidebar responsive">
	<script type="text/javascript">
        try {
            ace.settings.check('sidebar', 'fixed')
        } catch (e) {
        }
    </script>
	<div class="sidebar-toggle sidebar-collapse" id="sidebar-collapse">
		<i class="ace-icon fa fa-angle-double-left"
			data-icon1="ace-icon fa fa-angle-double-left"
			data-icon2="ace-icon fa fa-angle-double-right"></i>
	</div>
	<ul class="nav nav-list" id="left_menu">
		<cas:havePerm url="/menuRecommendManage.htm">
			<li><a href="#" class="dropdown-toggle"> <i
					class="menu-icon fa fa-film"></i> <span class="menu-text">推荐管理</span>
					<b class="arrow fa fa-angle-down"></b>
			</a> <b class="arrow"></b>
				<ul class="submenu">

		<cas:havePerm url="/menuArtificialInfo.htm">
						<li><a href="#"
							menu-url="<c:url value='/recomd/artificialInfo.msp'/>"> <!-- <i class="menu-icon fa fa-caret-right"></i> -->
								人工推荐
						</a> <b class="arrow"></b></li>
					</cas:havePerm>
					<cas:havePerm url="/menuVomsRecommend.htm">
						<li><a href="#"
							menu-url="<c:url value='/recomd/queryVomsRecommend.msp'/>">
								VOMS推荐 </a> <b class="arrow"></b></li>
					</cas:havePerm>
				</ul></li>
		</cas:havePerm>
		<cas:havePerm url="/menuSysManage.htm">
			<li><a href="#" class="dropdown-toggle"> <i
					class="menu-icon fa fa-cog"></i> 
					<span class="menu-text">系统管理 </span> <b
					class="arrow fa fa-angle-down"></b>
			</a> <b class="arrow"></b>
   <cas:havePerm url="#">
        <li>
            <a href="#" class="dropdown-toggle">
                <i class="menu-icon fa fa-cog"></i>
                <span class="menu-text">测试 </span>
                <b class="arrow fa fa-angle-down"></b>
            </a> <b class="arrow"></b>

            <ul class="submenu">
                <cas:havePerm url="#">
                <li>
                    <a href="#" menu-url="<c:url value='/pages/test/testQueryTag.jsp'/>">
                        	测试获取用户标签
          			</a>
                    <b class="arrow"></b>
                </li>
                 
                 <li>
                    <a href="#" menu-url="<c:url value='/pages/test/testSearch.jsp'/>">
                        	测试内容推荐
                    </a>
                    <b class="arrow"></b>
                </li>
                
                </cas:havePerm>
            </ul>
        </li>
        </cas:havePerm>
        
        
    </ul>
    <!-- /.nav-list -->
				<ul class="submenu">
					<cas:havePerm url="/menuSysParmsManage.htm">
						<li><a href="#"
							menu-url="<c:url value='/sys/sysParmsManage.msp'/>"> <!-- <i class="menu-icon fa fa-caret-right"></i> -->
								系统参数管理
						</a> <b class="arrow"></b></li>
					</cas:havePerm>
					<cas:havePerm url="/menuRecommonParmsManage.htm">
						<li><a href="#" id="menuRecomdMang"
							menu-url="<c:url value='/sys/recommondParmsManage.msp'/>"> <!-- <i class="menu-icon fa fa-caret-right"></i> -->
								推荐标签权重管理 
						</a> <b class="arrow"></b></li>
					</cas:havePerm>
				</ul></li>
		</cas:havePerm>
	</ul>
	<!-- /.nav-list -->

	<script type="text/javascript">
        try {
            ace.settings.check('sidebar', 'collapsed')
        } catch (e) {
        }
    </script>
</div>

var zTree,rMenu,selectedNode, editNode;
var curStatus = "expand", curAsyncCount = 0, asyncForAll = false,goAsync = false;
var zTreeNodes = [{text: '展现栏目',iconCls:'folder',nodeId: '0',targetId: '0',isParent:'true'}];

var setting = {
	async: {
		enable: true,
		url:"sys/listRecommondParam.msp",
		autoParam:["nodeId"],
		otherParam:{"isRefresh":"Y"},
		dataFilter: filter,
		type: "get"
	},
	view: {
//		addDiyDom: addDiyDom,
		dblClickExpand: false,
		showLine: false,
		selectedMulti: true
	},
	data:{
		key : {
			name : "text",
		}
	},
	callback: {
		onClick: zTreeOnClick,
		onAsyncSuccess: onAsyncSuccess,
		onAsyncError: onAsyncError,
//		onCheck: zTreeOnCheck,
		onRightClick: zTreeOnRightClick
	},
//	check: {  
//		enable: true
//	}
};

/**初始化展现对象菜单树**/
function initTree(){
	$.ajax({
		url: "sys/listRecommondParam.msp",
		data:{"nodeId":"0"},
		success:function(data){
			$.fn.zTree.init($("#treeDemo"), setting,  data);
			zTree = $.fn.zTree.getZTreeObj("treeDemo");
			rMenu = $("#rMenu");
		}
	});
	
}

/**单击菜单加载内容列表TabelGrid**/
function zTreeOnClick(event, treeId, treeNode) {
	//
	addLabelInfo(treeNode);
	//
	zTree.expandNode(treeNode);
//	var btn = $("#diyBtn_"+treeNode.id);
	if(treeNode.nodeId == 0){
		$("#ObjectDict_list .secondaryNav").find("button").each(function (){
			$(this).attr("disabled",true);
		});
	}else{
		$("#ObjectDict_list .secondaryNav").find("button").each(function (){
			$(this).attr("disabled",false);
		});
		
	}
};



function addLabelInfo(treeNode){
	if(treeNode != null){
		$laberName.val(treeNode.text);
		$labelType.val(treeNode.labelType);
		$laberQ.val(treeNode.weight);
		$addLabelParent.val(treeNode.text);
		$addLabelParentType.val(treeNode.labelType);
		$addLabelParentId.val(treeNode.nodeId);
		$editLabelParent.val(treeNode.parentText);
		$editLabelParentId.val(treeNode.parentId);
		$editLabelName.val(treeNode.text);
		$editLabelType.val(treeNode.labelType);
		$editLabelWeight.val(treeNode.weight);
		$editLabelId.val(treeNode.nodeId);
	}
	
}


function zTreeOnRightClick(event, treeId, treeNode){
	editNode = treeNode;
	if(treeNode != null){
		if(treeNode.labelType == "0"){
			$("#delLabelli").css('display','none');
			$("#addLabelli").css("display",'block');
		}
		if(treeNode.labelType == "1"){
			$("#addLabelli").css('display','none');
			$("#delLabelli").css("display",'block');
		}
	}
	zTree.selectNode(treeNode);
	showRMenu( event.clientX,event.clientY);
	addLabelInfo(treeNode);
	
}

function filter(treeId, parentNode, childNodes) {
	if (!childNodes) return null;
	for (var i=0, l=childNodes.length; i<l; i++) {
		childNodes[i].text = childNodes[i].text.replace(/\.n/g, '.');
	}
	return childNodes;
}

function onAsyncSuccess(event, treeId, treeNode, msg) {
	if (curStatus == "expand") {
		expandNodes(treeNode);
	} else if (curStatus == "async") {
		asyncNodes(treeNode.children);
	}
}

function onAsyncError(event, treeId, treeNode, XMLHttpRequest, textStatus, errorThrown) {
}

function expandNodes(nodes) {
	if (!nodes) return;
	for (var i=0, l=nodes.length; i<l; i++) {
		zTree.expandNode(nodes[i], true, false, false);
		if (nodes[i].isParent && nodes[i].zAsync) {
			expandNodes(nodes[i].children);
		} else {
			goAsync = true;
		}
	}
}

function reset() {
	$.fn.zTree.init($("#treeDemo"), setting, zTreeNodes);
}



/**更新此栏目**/
function refreshTreeNode(){
	hideRMenu();
	zTree.reAsyncChildNodes(editNode, "refresh");
}




function showRMenu( x, y) {
	$("#rMenu ul").show();
	x=x-$('#main_page').position().left +200;
	y=y-$('#main_page').position().top +55;
	$("#rMenu").css({"top":y+"px", "left":x+"px", "visibility":"visible"});
	$("body").bind("mousedown", onBodyMouseDown);
}

function hideRMenu() {
	if ($("#rMenu")) rMenu.css({"visibility": "hidden"});
	$("body").unbind("mousedown", onBodyMouseDown);
}

function onBodyMouseDown(event){
	if (!(event.target.id == "rMenu" || $(event.target).parents("#rMenu").length>0)) {
		$("#rMenu").css({"visibility" : "hidden"});
	}
}


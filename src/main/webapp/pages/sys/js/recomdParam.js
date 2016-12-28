var $laberName = $('#laberName'),$addLabelParentId=$('#addLabelParentId'),$mainPage = $('#main_page'), $labelType = $('#labelType'), $laberQ = $('#laberQ'), $addLabelParent = $('#addLabelParent'), $addLabelName = $('.addLabelName'), $addLabelWeight = $('#addLabelWeight'), $addLabelType = $('.addLabelType'), $addLabelParentType = $('#addLabelParentType'), $editLabelParent = $('#editLabelParent'),$editLabelParentId = $('#editLabelParentId'), $editLabelName = $('#editLabelName'), $editLabelType = $('#editLabelType'), $editLabelWeight = $('#editLabelWeight'), $editLabelId = $('#editLabelId');
var allSelectedIds, elems;

jQuery(function($) {

	//construct the data source object to be used by tree  
	var remoteUrl = 'sys/listRecommondParam.msp';
	var remoteDateSource = function(options, callback,parent_id) {
		if(elems != null && elems != ''){
			var d = elems; 
			elems = null;
			callback({data:d});
			return;
		}
		if (!('text' in options || 'type' in options)) {
			parent_id = 0; //load first level data  
		} else if ('type' in options && options['type'] == 'folder') { //it has children  
			
				parent_id = options['id']
		}
		if (parent_id !== null) {
			$.ajax({
				url : remoteUrl,
				data : 'skey=' + parent_id,
				type : 'POST',
				dataType : 'json',
				success : function(response) {

					callback({
						data : response.data.data
					});
				},
				error : function(response) {
				}
			})
		}
	};

	$('#tree1')
			.ace_tree(
					{
						dataSource : remoteDateSource,
						multiSelect : true,
						loadingHTML : '<div class="tree-loading"><i class="ace-icon fa fa-refresh fa-spin blue"></i></div>',
						'open-icon' : 'ace-icon tree-minus',
						'close-icon' : 'ace-icon tree-plus',
						'selectable' : true,
						'selected-icon' : 'ace-icon fa fa-check',
						'unselected-icon' : 'ace-icon fa fa-times',
						cacheItems : true,
						folderSelect : true
					});

	$('#tree1').on('updated', function(e, result) {
		//result.info  >> an array containing selected items
		//result.item
		//result.eventType >> (selected or unselected)
	}).on('selected', function(e) {
	}).on('deselected', function(e) {
	}).on('loaded', function(e) {
	}).on('opened', function(e) {
	}).on('closed', function(e) {
	});

	/**
	$('#tree1').on('loaded', function (evt, data) {
	});

	$('#tree1').on('opened', function (evt, data) {
	});

	$('#tree1').on('closed', function (evt, data) {
	});*/

	$('#tree1').on('selected.fu.tree', function(evt, data) {

		$laberName.val(data.target.text);
		$labelType.val(data.target.labelType);
		$laberQ.val(data.target.weight);
		$addLabelParent.val(data.target.text);
		$addLabelParentType.val(data.target.labelType);
		$addLabelParentId.val(data.target.id);
		$editLabelParent.val(data.target.parentText);
		$editLabelParentId.val(data.target.parentId);
		$editLabelName.val(data.target.text);
		$editLabelType.val(data.target.labelType);
		$editLabelWeight.val(data.target.weight);
		$editLabelId.val(data.target.id);

	});
	$('#tree1').on('deselected.fu.tree', function(evt, data) {
		var selectedIds = $('.tree-selected');
		if(selectedIds == '' || selectedIds == null || selectedIds.length < 1){
			$addLabelParent.val("");
			$addLabelParentType.val("");
			$addLabelParentId.val("");
			$editLabelParent.val("");
			$editLabelParentId.val("");
			$editLabelName.val("");
			$editLabelType.val("");
			$editLabelWeight.val("");
		}else{
			var items = $('#tree1').tree('selectedItems');
			for ( var i in items){
				if (items.hasOwnProperty(i)) {
					var item = items[i];
					$addLabelParent.val(item.text);
					$addLabelParentType.val(item.labelType);
					$addLabelParentId.val(item.id);
					$editLabelParent.val(item.parentText);
					$editLabelParentId.val(item.parentId);
					$editLabelName.val(item.text);
					$editLabelType.val(item.labelType);
					$editLabelWeight.val(item.weight);
				}
			}
		}
		

	});

	//show selected items inside a modal  
	$('#submit-button').on(
			'click',
			function() {
				var output = '';
				var items = $('#treeview').tree('selectedItems');
				for ( var i in items)
					if (items.hasOwnProperty(i)) {
						var item = items[i];
						output += item.additionalParameters['id'] + ":"
								+ item.text + "\n";
					}

				$('#modal-tree-items').modal('show');
				$('#tree-value').css({
					'width' : '98%',
					'height' : '200px'
				}).val(output);

			});
});

function editRecomdParms() {
//	var selectedIds = $('.tree-selected'); //返回选中多行ids
	if(editNode == null){
		alertmsg("warning", "请至少选择一条标签进行操作");
	}else{
		$("#editSysParamsModal").modal("show");
	}
		
}

function editSave() {

	if ($editLabelWeight.val() == "") {
		alertmsg("warning", "权重为空");
		return;
	}
	var reg = /^(\d{1,2}(\.\d{1,2})?|100)$/;
	if (!reg.test($editLabelWeight.val())) {
		alertmsg("warning", "请输入0-100以内的权重数值，小数点后最多2位");
		return;
	}

	$.ajax({
		type : "post",
		url : webroot + "sys/editRcmdParam.msp",
		data : {
			"id" : $editLabelId.val(),
			"weight" : $editLabelWeight.val()
		},
		success : function(data) {
			zTree.reAsyncChildNodes(editNode.getParentNode(), "refresh");
			alertmsg("warning", data.msg);
		}
	});
	$editLabelName.val("");
	$editLabelType.val("");
	$editLabelWeight.val("");
	$("#editSysParamsModal").modal("hide");
}

function addSysParms() {
	if(editNode == null){
		alertmsg("warning", "请至少选择一条标签进行操作");
	}else if(editNode.labelType == '0'){
		$("#addSysParmsModal").modal("show");
	}else{
		alertmsg("warning", "请选择一级标签进行添加");
	}
	
}
function addSave() {

	if ($addLabelParent.val() == "" || $addLabelParentType.val() != "0") {
		alertmsg("warning", "请选择一级标签进行添加");
		return;
	}

	if ($addLabelName.val() == "") {
		alertmsg("warning", "标签名为空");
		return;
	}
	if ($addLabelType.val() == "") {
		alertmsg("warning", "标签类型为空");
		return;
	}
	if ($addLabelWeight.val() == "") {
		alertmsg("warning", "标签权重为空");
		return;
	}
	var reg = /^(\d{1,2}(\.\d{1,2})?|100)$/;
	if (!reg.test($addLabelWeight.val())) {
		alertmsg("warning", "请输入0-100以内的权重数值，小数点后最多2位");
		return;
	}

	$.ajax({
		type : "post",
		url : webroot + "sys/editRcmdParam.msp",
		data : {
			"parentId" : $addLabelParentId.val(),
			"type" : $addLabelType.val(),
			"weight" : $addLabelWeight.val(),
			"key" : $addLabelName.val()
		},
		success : function(data) {
			zTree.reAsyncChildNodes(editNode, "refresh");
			alertmsg("warning", data.msg);
		}
	});
	$addLabelName.val("");
	$addLabelWeight.val("");
	$("#addSysParmsModal").modal("hide");
}

function delSysParms() {
	

	if (editNode == null) {
		alertmsg("warning", "请至少选择一条标签进行操作");
	} else if(editNode.labelType == '0'){
		alertmsg("warning", "只能删除二级标签");
	}else{

		Lobibox.confirm({
			title : "删除提示",
			msg : "确定删除标签 : "+editNode.text+" ?",
			callback : function($this, type, eve) {
				if (type == "yes") {
					var data = {
						"ids" : editNode.nodeId
					};
					$.post(webroot + "sys/deleteRcmdParam.msp", data, function(data) {
						if (data.success == true) {
							zTree.reAsyncChildNodes(editNode.getParentNode(), "refresh");
							alertmsg("success", "删除成功");
						} else {
							alertmsg("error", "删除失败");
						}
					});
				}
			}
		});
	}
}
function getDatas() {
	var output = "";
	var ids = "";
	var items = $('#tree1').tree('selectedItems');
	for ( var i in items)
		if (items.hasOwnProperty(i)) {
			var item = items[i];
			ids += item.id + ",";
			output += item.text + ",";
		}

	ids = ids.substring(0, ids.lastIndexOf(","));
	allSelectedIds = ids;
	output = output.substring(0, output.lastIndexOf(","));
}
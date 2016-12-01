var $laberName = $('#laberName'),
     	$laberType = $('#laberType'),
     	$laberQ = $('#laberQ'),
     	$addLabelParent = $('#addLabelParent'),
     	$addLabelName = $('.addLabelName'),
     	$addLabelWeight = $('#addLabelWeight'),
     	$addLabelType = $('.addLabelType'),
     	$addLabelParentType = $('#addLabelParentType'),
     	$editLabelParent = $('#editLabelParent'),
     	$editLabelName = $('#editLabelName'),
     	$editLabelType = $('#editLabelType'),
     	$editLabelWeight = $('#editLabelWeight');


jQuery(function($) {
	
	  //construct the data source object to be used by tree  
    var remoteUrl = 'sys/listRecommondParam.msp';
    var remoteDateSource = function (options, callback) {  
    	//console.log(options);
         var parent_id = null  
         if ( !('text' in options || 'type' in options) ){  
            parent_id = 0; //load first level data  
        }  
         else if ('type' in options && options['type' ] == 'folder' ) { //it has children  
             if ('additionalParameters' in options && 'children' in options.additionalParameters)  
                 parent_id = options.additionalParameters['id' ]  
        }  
          
         if (parent_id !== null) {  
            $.ajax({  
                 url: remoteUrl,  
                 data: 'skey=' +parent_id,  
                 type: 'POST' ,  
                 dataType: 'json' ,  
                 success : function (response) {  
                      
                    	 
                         callback({ data: response.data.data });
                         console.log(response.data.data)
                 },  
                 error: function (response) {  
                      //console.log(response);  
                 }  
            })  
        }  
   }; 
    
//    var treeDataSource = function(options , callback) {
//    	 //options has extra info such as "type" "text" "additionalParameteres", etc
//    	 //which you can use to specify requested set of data
//    	
//    	 var myData = [ ... ];//set of data
//    	 callback({ data: myData });
//    }
   $.ajax({  
       url: 'sys/addLabelNameList.msp',  
       type: 'POST' ,  
       dataType: 'json' ,  
       success : function (response) {  
            
          	 
              var $addLabelList = response.data;
               
               var max = $addLabelList.length;
               for(var i =0;i<max; i++){
               	$addLabelName.append('<option value="'+$addLabelList[i].key+'">'+$addLabelList[i].val+'</option>');
               }
       },  
       error: function (response) {  
            //console.log(response);  
       }  
  }) ;
   
	$('#tree1')
			.ace_tree(
					{
						dataSource: remoteDateSource ,  
	                      multiSelect: true ,  
	                      loadingHTML: '<div class="tree-loading"><i class="ace-icon fa fa-refresh fa-spin blue"></i></div>',  
	                       'open-icon' : 'ace-icon tree-minus',  
	                       'close-icon' : 'ace-icon tree-plus',  
	                       'selectable' : true ,  
	                       'selected-icon' : 'ace-icon fa fa-check',  
	                       'unselected-icon' : 'ace-icon fa fa-times',  
	                      cacheItems: true ,  
	                      folderSelect: true  
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

	$('#tree1').on('selected.fu.tree', function (evt, data) {
		
		$laberName.val(data.target.text);
		$laberType.val(data.target.laberType);
		$laberQ.val(data.target.weight);
		$addLabelParent.val(data.target.text);
		$addLabelParentType.val(data.target.laberType);
		$editLabelParent.val(data.target.parentText);
		$editLabelName.val(data.target.text);
		$editLabelType.val(data.target.laberType);
		$editLabelWeight.val(data.target.weight);
		
	});
	$('#tree1').on('deselected.fu.tree', function (evt, data) {
		
		
		$addLabelParent.val("");
		$addLabelParentType.val("");
		$editLabelParent.val("");
		$editLabelName.val("");
		$editLabelType.val("");
		$editLabelWeight.val("");
		
	});
	
	//show selected items inside a modal  
    $( '#submit-button' ).on('click' , function () {  
          var output = '' ;  
          var items = $('#treeview' ).tree('selectedItems' );  
          for (var i in items) if (items.hasOwnProperty(i)) {  
              var item = items[i];  
             output += item.additionalParameters['id' ] + ":"+ item.text+"\n" ;  
         }  
           
        $( '#modal-tree-items' ).modal('show' );  
        $( '#tree-value' ).css({'width' :'98%' , 'height' :'200px' }).val(output);  
      
    });  
});

function editRecomdParms() {
	var selectedIds = $('.tree-selected'); //返回选中多行ids
    if (selectedIds == '' || selectedIds == null || selectedIds.length < 1) {
	    alertmsg("warning", "请选择一条记录进行操作");
    } else if (selectedIds.length > 1) {
        alertmsg("warning", "请只选择一条记录进行操作");
    } else {
    	
        
        $("#editSysParamsModal").modal("show");
        
        
    }
    
}

function editSave() {
	
    if ($editLabelWeight.val() == "") {
    	alertmsg("warning", "权重为空");
    	return;
    }
    var reg = /^(\d{1,2}(\.\d{1,2})?|100)$/;
    if(!reg.test($editLabelWeight.val())){
    	alertmsg("warning","请输入0-100以内的权重数值，小数点后最多2位");
    	return;
    }
    
    
	$.ajax({
		type:"post",
		url:webroot+"#",
		data:{"id": id, "configKey":configKey, "configValue": configValue, "detail":detail},
		success:function(data){
			alertmsg("warning", data.msg);
			//编辑成功重新加载jqGrid
			$("#grid-table").jqGrid('setGridParam',{ 
		        page:1,
		        mtype:"post"
		    }).trigger("reloadGrid"); //重新载入 
		}
	});
	$editLabelName.val("");
    $editLabelType.val("");
    $editLabelWeight.val("");
	$("#editSysParamsModal").modal("hide");
}

function addSysParms() {
	var selectedIds = $('.tree-selected'); //返回选中多行ids
    if (selectedIds == '' || selectedIds == null || selectedIds.length < 1) {
	    alertmsg("warning", "请选择一条一级标签进行操作");
	    return;
    } else if (selectedIds.length > 1) {
        alertmsg("warning", "请只选择一条一级标签进行操作");
        return;
    } else {
    	if($addLabelParent.val()=="" || $addLabelParentType.val()!="一级标签"){
    		alertmsg("warning", "请选择一级标签进行添加");
        	return;
    	}
    $("#addSysParmsModal").modal("show");
    }
}
function addSave() {
	
	if($addLabelParent.val()=="" || $addLabelParentType.val()!="一级标签"){
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
    if($addLabelWeight.val() == ""){
    	alertmsg("warning", "标签权重为空");
    	return;
    }
    var reg = /^(\d{1,2}(\.\d{1,2})?|100)$/;
    if(!reg.test($addLabelWeight.val())){
    	alertmsg("warning","请输入0-100以内的权重数值，小数点后最多2位");
    	return;
    }
    
    
	$.ajax({
		type:"post",
		url:webroot+"#",
		data:{ "labelName":$addLabelName, "addLabelType": $addLabelType, "addLabelWeight":addLabelWeight},
		success:function(data){
			alertmsg("warning", data.msg);
			//添加成功重新加载jqGrid
			$("#grid-table").jqGrid('setGridParam',{ 
		        page:1,
		        mtype:"post"
		    }).trigger("reloadGrid"); //重新载入 
		}
	});
	$addLabelName.val("");
    $addLabelWeight.val("");
	$("#addSysParmsModal").modal("hide"); 
}

function delSysParms() {
	var selectedIds = $('.tree-selected');
    if (selectedIds == '' || selectedIds == null || selectedIds.length < 1) {
	    alertmsg("warning","请至少选择一条记录进行操作");
    } else if (selectedIds.length >= 1) {
    	
    	Lobibox.confirm({
    		title: "删除提示",
    		msg: "确定删除所选记录?",
    		callback: function ($this, type, eve) {
    			if(type == "yes"){
    				var ids = "";
    				for (var i = 0; i < selectedIds.length; i++) {
			    		var rowData = $('#grid-table').getRowData(selectedIds[i]);
			    		var configId = rowData.id;
			    		ids = ids + configId + ",";
			    	}
    				var data = {"configIds":ids};
    				$.post(webroot+"#",
    					data,
    					function(data){
    						if(data.success == true){
    							$("#grid-table").jqGrid('setGridParam',{ 
    						        page:1,
    						        mtype:"post"
    						    }).trigger("reloadGrid"); //重新载入 
    							alertmsg("success","删除成功");
    						}else{
    							alertmsg("error","删除失败");
    						}
    					}
    				);
    			}			
    		}
    	});
    }  
} 
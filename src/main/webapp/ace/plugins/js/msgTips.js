$(function() {
	$.fn.manhua_msgTips = function(options) {
		$("body").find(".container.tip_container").remove();
		var defaults = {
			Event : "click",			
			timeOut : 500,		//下滑时间		
			msg : "",			
			speed : 300,		//下滑速度		
			type : "success"	//警告类型  （1、success 2、error 3、warning）		
		};
		var options = $.extend(defaults,options);
		var bid = parseInt(Math.random()*100000);
		/*$("body").prepend('<div id="tip_container'+bid+'" class="container tip_container"><div id="tip'+bid+'" class="mtip"><i class="micon"></i><span id="tsc'+bid+'"></span><i class="mclose"></i></div></div>');*/
		//$("body").prepend('<div id="tip_container'+bid+'" class="container tip_container"><div id="tip'+bid+'" class="mtip"><i class="micon"></i><span id="tsc'+bid+'"></span></div></div>');
		$("body").prepend('<div id="tip_container'+bid+'" class="container tip_container"><div id="tip'+bid+'" class="mtip clearfix"><div class="col-xs-2" style="margin-top: -2px;"><i class="micon"></i></div><div id="tsc'+bid+'" class="col-xs-10 no-padding-left"></div></div></div>');
		var $this = $(this);
		var $tip_container = $("#tip_container"+bid)
		var $tip = $("#tip"+bid);
		var $tipSpan = $("#tsc"+bid);
		/*var $colse = $("#mclose"+bid);	*/	
		//�������ʱ��
		clearTimeout(window.timer);
		
		//����Ԫ�ذ��¼�
		$this.unbind().bind(options.Event,function(){
			/*$tip.attr("class", options.type).addClass("mtip");*/	
			if(options.type == "warning") {
				$tip.find("i").attr("class", "fa fa-exclamation-circle orange bigger-130").addClass("mtip");
			}else if(options.type == "success"){
				$tip.find("i").attr("class", "fa fa-check-circle green bigger-130").addClass("mtip");
			}else if(options.type == "error"){
				$tip.find("i").attr("class", "fa fa-times-circle red bigger-130").addClass("mtip");
			}else{
				console.log("error! no such type of tip");
			}
			$tipSpan.html(options.msg);			
			$tip_container.slideDown(options.speed);
			//��ʾ�����ض�ʱ��
			window.timer = setTimeout(function (){
				$tip_container.slideUp(options.speed);
			}, options.timeOut);
		
		});	
		//����Ƶ���ʾ��ʱ���ʱ��
		$tip_container.bind("mouseover",function() {
			clearTimeout(window.timer);
		});
		
		//����Ƴ���ʾ��ʱ������ʱ��
		$tip_container.bind("mouseout",function() {
			window.timer = setTimeout(function (){
				$tip_container.slideUp(options.speed);
			}, options.timeOut);
		});
	
		//�رհ�ť���¼�
		/*$colse.bind("click",function() {
			$tip_container.slideUp(options.speed);
		});*/
	}
});
/*www.sucaijiayuan.com*/
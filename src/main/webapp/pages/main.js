window.emptyWords = "无";//表格中内容为空时显示的值
//确认框：  content：提示内容（必填）；title：提示框标题（选填）； button:button1显示内容,即“确定”按钮显示内容（选填）； callback：点击button1（“确定”）按钮执行回调函数（必填）
jQuery(function ($) {
	$("#ace-settings-navbar").prop("checked","checked");
	$("#navbar").addClass("navbar-fixed-top");
	$("#ace-settings-sidebar").prop("checked","checked");
	$("#sidebar").addClass("sidebar-fixed sidebar-scroll");
    $(document).tooltip({
        show: {
            effect: "slideDown",
            delay: 1000
        }
    });
});
//this function will set a button disabled and remove the disabled attribute 3 seconds later,added by likang
function setBtnDisable(e){
	if(e){
		$(e.target).attr("disabled","disabled");
		setTimeout(function(){
			$(e.target).removeAttr("disabled");
		},3000);
	}
}
function confirmMsg(content, title, button, callBack) {
    if (title) {
        if ($.type(title) === "function") {
            callBack = title;
        } else {
            $("#mainModelTitle").text(title);
        }
    }
    if (content) {
        if ($.type(content) === "function") {
            callBack = content;
        } else {
            $("#mainModelContent").text(content);
        }
    }
    if (button) {
        if ($.type(button) === "function") {
            callBack = button;
        } else {
            $("#mainModelButton1").html(button);
        }
    }
    $("#showModal").modal("show");
    $("#mainModelButton1").off('click').bind('click', function () {
        callBack();
    });
    $("#mainModelButton2").off('click').click(function () {
        $("#mainModelTitle").text("提示");
        $("#mainModelContent").text("？");
        $("#mainModelButton1").html("确定");
    });
}
//显示提示信息方法
function alertmsg(type, msg) {
    $("#alertButton").manhua_msgTips({
        Event: "click",	//响应的事件
        timeOut: 2000,		//提示层显示的时间
        msg: msg,			//显示的消息
        speed: 800,		//滑动速度
        type: type			//提示类型（1、success 2、error 3、warning）

    });
    $("#alertButton").click();
}

function clearprofiledata(feed) {
    var feed = $("#" + feed).children();
    for (var i = 0; i < feed.length; i++) {
        if ($(feed[i]).css("display") == "block") {
            $(feed[i]).remove();
        }
    }

}
function mainshowOrhide(ms) {
    $("#rightPage").slideToggle(ms);
    $("#mainchangePage").slideToggle(ms);
}
function mainshowOrhide2(ms) {
    $("#rightPage").slideToggle(ms);
    $("#createCopyright").slideToggle(ms);
}
$(document).ready(function(){
	bootbox.setDefaults("locale","zh_CN");
	//var userId = $("#userId").val();
	/*url = base + "user/logout";
	data={
			userId:userId
	}
	$.post(url,data,function(){
		
	});*/
	checkuserLoginStatus();
	
});

function checkuserLoginStatus(){
	 $.get(base + "user/checkuser",{},function(response){
		 if(response!=""){
			 if(response.success){
				 if(response.success){
           		  bootbox.confirm("<b>"+response.message+"</b>",
       				    function(result) {
           			  		location.href=base+'logout.html';
               		  	}
           		  );
				 }
			 }
		 }
		
	 });
	 setTimeout("checkuserLoginStatus()",30000);  
}
function showMessage(message,callbackFn){
	bootbox.dialog({
		message: "<b>温馨提示</b><hr/><center><b>"+message+"</b></center>", 
		buttons: {
			"success" : {
				"label" : "确定",
				"className" : "btn-sm btn-primary btn-round",
				callback: callbackFn
			}
		}
	});
}
function confirm(message,callbackOk,callbackCancel){
	bootbox.confirm("<b>操作提示</b><hr><center><b>"+message+"</b></center>", function(result) {
		if(result) {
			callbackOk();
		}else{
			if(null == callbackCancel || undefined == callbackCancel){
				return;
			}
			callbackCancel();
		}
	});
}
function successNotice(title,content){
	notify(title,content,'center','success');
}
function errorNotice(title,content){
	notify(title,content,'center','error');
}
function warnNotice(title,content){
	notify(title,content,'center','warn');
}
function notify(title,content,location,type){
	switch(type){
	case 'success':
		type = 'info';
		break;
	case 'warn':
		type='warning';
		break;
	case 'error':
		type='error';
		break;
	default:
		type='info';
		break;
	}
	$.gritter.add({
		title: title,
		text: '<div class="center"><b>'+content+'</b></center>',
		class_name: 'gritter-'+type+' gritter-light gritter-'+location,
		time:3000
	});
}
function stringFilter(s){
	var pattern = new RegExp("[`~!@#$^&*()=|{}':;',\\[\\].<>/?~！@#￥……&*（）——|{}【】‘；：”“'。，、？]");
	var rs = "";   
    for (var i = 0; i < s.length; i++) {   
        rs = rs+s.substr(i, 1).replace(pattern, '');   
    } 
    return rs;
}

function logout(){
	confirm("您确认退出系统吗？", function(){
		location.href=base+'logout.html';
	});
}

function showWord(){
	showMessage("文档完善中，请耐心等待")
}

var Loading={};
/***
 * loading效果显示
 */
Loading.show=function(){
    $('div.well').first().append('<div class="icon-spinner" style="float:right">' + '<i id = "spinner" class="ace-icon fa fa-spinner fa-spin blue bigger-225"></i>&nbsp;&nbsp;&nbsp;' + '</div>');
}
/***
 * loading效果隐藏
 */
Loading.hide=function(){
	 $("div.icon-spinner").remove();
}
//显示遮罩层
function showMask(){     
    $("#mask").css("height",$(document).height());     
    $("#mask").css("width",$(document).width());   
    $("#mask").show();  
    $("#spinner-message").show();
    $("#spinner").show();
}  
//隐藏遮罩层  
function hideMask(){     
    $("#mask").hide();    
    $("#spinner-message").hide();
    $("#spinner").hide();
    
} 
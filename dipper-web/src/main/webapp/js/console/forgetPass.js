$(function(){
	$("#modify_submit").click(function(){
		var uName=$.trim($("#userName").val());
		if(uName==""){
			showMessage("请输入用户名！");
			return false;
		}else{
			data = {
				userName : uName
			};
			url = base + 'user/forgetPass';
			$.post(url, data, function(response) {
				if(response==""){
					showMessage("服务器异常！");
				}else{
					showMessage(response.message);
				}
			});
		}
	})
})
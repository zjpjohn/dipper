$(function() {
    $("#modify_submit").click(function() {
        if ($("#update_pass_form").valid()) {
            var ud = $.trim($("#input_ud").val());
            var pass = $.trim($("#user_pass_edit").val());
            if (pass == "") {
                return false;
            } else {
                data = {
                    UD: ud,
                    PASS: pass
                };
                url = base + 'user/modifyPass';
                $.post(url, data,
                function(response) {
                    if(response==""){
                    	showMessage("服务器异常！");
                    }else{
                    	if(response.success){
                    		  bootbox.confirm("<b>密码修改成功，点击确定按钮跳转到登录页面！</b>",
                				    function(result) {
                    			  			if(result) location.href = base;
	                    		  	}
                    		  );
                    	}else{
                    		showMessage(response.message);
                    	}
                    }
                });
            }
        }
    })

    $("#update_pass_form").validate({
        rules: {
            user_pass_edit: {
                required: true,
                maxlength: 20,
                isPwd: true
            }
        },
        messages: {
            user_pass_edit: {
                required: "密码不能为空",
                maxlength: $.validator.format("密码不能大于20个字符")
            }
        }
    });
})
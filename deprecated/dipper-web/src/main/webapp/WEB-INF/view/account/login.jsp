<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head lang="en">
    <link rel="shortcut icon" href="${basePath }img/title_cloud.png" />
<title>Devops管理平台</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0" />
<link rel="stylesheet" href="${basePath }ace/assets/css/bootstrap.min.css" />
<link rel="stylesheet" href="${basePath }css/validation.css" />
<link rel="stylesheet" type="text/css" href="${basePath}css/land.css" />
<script src="${basePath }ace/assets/js/jquery.min.js"></script>
<script src="${basePath }ace/assets/js/jquery.validate.min.js"></script>
<script	src="${basePath }js/validate.js"></script>
<link rel="shortcut icon" href="fav.ico" />
</head>
<body>
<div style="width: 100%;background-color: darkcyan;height: 100%;position: absolute;-webkit-filter: blur(100px); -moz-filter: blur(10px); -ms-filter: blur(10px);filter: blur(10px);"></div>
    <div style="overflow-x: auto;min-width: 850px;width: 100%;height: 100%;position: relative;">
        <div>
            <img style=" margin:15px; width:280px;height:66px" src="${basePath}img/logo.gif" alt="logo"/>
        </div>
        <div>
            <div style="text-align:center; margin-top:30px; margin-bottom: 12px;">
                <div style="font-size: 48px;color: #fff;font-weight: bolder;">Devops管理平台</div>
            </div>
            <div id="loginbox" style="display:block; border: 1px solid #4A5D82; width: 420px;height:auto;text-align: center;border-radius: 20px;margin: auto;padding: 10px 0; margin-bottom:40px; box-shadow: 0px 0px 40px #9edeff;">
                <form id="login-form" action="${basePath}login" autocomplete="off"
				method="post" class="login-form">
					<div class="login_content">
                        <input type="text" name="userName" class="login_input" placeholder="用户名" value="${userName}"/>
                    </div>
                    <div class="login_content">
                        <input type="password" name="password" class="login_input" placeholder="密码" value="${password}" />
                    </div>
                    <div class="login_content">
                        <input type="text" class="login_input" placeholder="验证码" name="vercode" value=""/>
                        <div style="width: 280px;padding-top: 6px;margin: auto;text-align: left;">
                        	<img alt="验证码" id="authImg" /> 
                        	<a id="ver-change" class="ver-change" style="color:deepskyblue;" onclick="refresh()">换一个</a>
							<a class="ver-change" style="color:deepskyblue;" onclick="forgetPass()">忘记密码？</a>
                        </div> 
                    </div>
                    <div class="login_content">
	                    <label class="error">${message}</label>
                    </div>
                    <div class="login_content">
                        <input type="submit" class="btn btn-primary" style="font-weight: bolder;width: 280px;" value="登&nbsp;&nbsp;&nbsp;录"/>
                    </div>
				</form>
            </div>
        </div>
    </div>
    <script>
		function refresh() {
			$("#authImg").attr("src", "${basePath}captcha?" + Math.random());
		}
		function forgetPass(){
			location.href="${basePath }forgetPass.html?"+Math.random();
		}
		$(function() {
			refresh();
			$("#login-form").validate({
				rules:{
					userName: {
						required: true,
						stringCheck:true,
						maxlength:20
					},
					password: {
						required:true,
						maxlength:20,
						isPwd:true
					},
					vercode: {
						required:true,
						isIntGteZero : true
					}
				},
				messages:{
					userName: {
						required: "用户名不能为空",
						maxlength:$.validator.format("用户名不能大于20个字符")
					},
					password: {
						required: "密码不能为空",
						maxlength:$.validator.format("密码不能大于20个字符")
					},
					vercode: {
						required: "验证码不能为空",
						maxlength:$.validator.format("验证码必须大于或等于0")
					}
				}
			});
		})
	</script>
</body>
</html>
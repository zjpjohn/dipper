<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
<link rel="shortcut icon" href="${basePath }img/title_cloud.png" />
<title>用户管理</title>
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
<meta charset="utf-8" />
<meta name="description" content="overview &amp; stats" />
<meta name="viewport"
	content="width=device-width, initial-scale=1.0, maximum-scale=1.0" />
<jsp:include page="../js.jsp"></jsp:include>
</head>
<body class="no-skin">
	<jsp:include page="../header.jsp"></jsp:include>
	<div class="main-container" id="main-user">
		<script type="text/javascript">
			try {
				ace.settings.check('main-user', 'fixed')
			} catch (e) {
			}
			function toParent() {
				window.location.href = base + 'user/index.html';
			}
		</script>
		<jsp:include page="../nav.jsp">
			<jsp:param value="manage_user" name="page_index" />
		</jsp:include>
		<div class="main-content">
			<div class="main-content-inner">
				<div class="breadcrumbs" id="breadcrumbs">
					<ul class="breadcrumb">
						<li><i class="ace-icon fa fa-home home-icon"></i> <a href="${basePath}index.html"><strong>首页</strong></a>
						</li>
						<li class="active"><b>用户管理</b></li>
					</ul>
				</div>
				<div class="page-content">
					<div class="row">
						<div class="col-xs-12">
							<div class="well well-sm">
								<a href="#" id="return_user_btn" class="btn btn-sm btn-primary"
									onclick="toParent()"> <i
									class="ace-icon fa fa-share bigger-110 icon-only"></i> <b>返回用户管理</b>
								</a>
							</div>
							<div>
								<div class="profile-user-info profile-user-info-striped">
									<div class="profile-info-row">
										<div class="profile-info-name">用户ID</div>
										<div class="profile-info-value">
											<span>${userInfo.userId}</span>
										</div>
									</div>
									<div class="profile-info-row">
										<div class="profile-info-name">用户名称</div>
										<div class="profile-info-value">
											<span>${userInfo.userName}</span>
										</div>
									</div>
									<div class="profile-info-row">
										<div class="profile-info-name">用户邮箱</div>
										<div class="profile-info-value">
											<span>${userInfo.userMail}</span>
										</div>
									</div>
									<div class="profile-info-row">
										<div class="profile-info-name">用户公司</div>
										<div class="profile-info-value">
											<span>${userInfo.userCompany}</span>
										</div>
									</div>
									<div class="profile-info-row">
										<div class="profile-info-name">用户角色</div>
										<div class="profile-info-value">
											<span>${userInfo.roleString}</span>
										</div>
									</div>
									<div class="profile-info-row">
										<div class="profile-info-name">用户状态</div>
										<div class="profile-info-value">
											<span>${userInfo.userStatus==1?"激活":"冻结"}</span>
										</div>
									</div>
									<div class="profile-info-row">
										<div class="profile-info-name">用户创建人</div>
										<div class="profile-info-value">
											<span>${userInfo.createUserName}</span>
										</div>
									</div>
									<div class="profile-info-row">
										<div class="profile-info-name">用户创建时间</div>
										<div class="profile-info-value">
											<span>${userInfo.userCreatedate}</span>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</html>

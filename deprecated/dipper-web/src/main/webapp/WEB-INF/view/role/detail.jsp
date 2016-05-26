<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
<link rel="shortcut icon" href="${basePath }img/title_cloud.png" />
<title>角色管理</title>
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
<meta charset="utf-8" />
<meta name="description" content="overview &amp; stats" />
<meta name="viewport"
	content="width=device-width, initial-scale=1.0, maximum-scale=1.0" />
<jsp:include page="../js.jsp"></jsp:include>
</head>
<body class="no-skin">
	<jsp:include page="../header.jsp"></jsp:include>
	<div class="main-container" id="main-role">
		<script type="text/javascript">
			try {
				ace.settings.check('main-role', 'fixed')
			} catch (e) {
			}
			function toParent() {
				window.location.href = base + 'role/index.html';
			}
		</script>
		<jsp:include page="../nav.jsp">
			<jsp:param value="manage_role" name="page_index" />
		</jsp:include>
		<div class="main-content">
			<div class="main-content-inner">
				<div class="breadcrumbs" id="breadcrumbs">
					<ul class="breadcrumb">
						<li><i class="ace-icon fa fa-home home-icon"></i> <a
							href="${basePath}index.html"><strong>首页</strong></a></li>
						<li class="active"><b>角色管理</b></li>
					</ul>
				</div>
				<div class="page-content">
					<div class="row">
						<div class="col-xs-12">
							<div class="well well-sm">
								<a href="#" id="return_user_btn" class="btn btn-sm btn-primary"
									onclick="toParent()"> <i
									class="ace-icon fa fa-share bigger-110 icon-only"></i> <b>返回角色管理</b>
								</a>
							</div>
							<div>
								<div class="profile-user-info profile-user-info-striped">
									<div class="profile-info-row">
										<div class="profile-info-name">角色ID</div>
										<div class="profile-info-value">
											<span>${roleInfo.roleId}</span>
										</div>
									</div>
									<div class="profile-info-row">
										<div class="profile-info-name">角色名称</div>
										<div class="profile-info-value">
											<span>${roleInfo.roleName}</span>
										</div>
									</div>
									<div class="profile-info-row">
										<div class="profile-info-name">角色描述</div>
										<div class="profile-info-value">
											<span>${roleInfo.roleDesc}</span>
										</div>
									</div>
									<div class="profile-info-row">
										<div class="profile-info-name">角色状态</div>
										<div class="profile-info-value">
											<span>${roleInfo.roleStatus==1?"正常":"不正常"}</span>
										</div>
									</div>
									<div class="profile-info-row">
										<div class="profile-info-name">角色权限</div>
										<div class="profile-info-value">
											<div id="roleAuthTree" class="ztree col-sm-9"></div>
										</div>
									</div>

								</div>

								<input type="hidden" id="roleInfo_roleId" name="roleInfo_roleId"
									value="${roleInfo.roleId}" />
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</body>
	<script src="${basePath }js/console/roledetail.js"></script>
</html>

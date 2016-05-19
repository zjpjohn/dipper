<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
<link rel="shortcut icon" href="${basePath }img/title_cloud.png" />
<title>资源管理</title>
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
<meta charset="utf-8" />
<meta name="description" content="overview &amp; stats" />
<meta name="viewport"
	content="width=device-width, initial-scale=1.0, maximum-scale=1.0" />
<jsp:include page="../js.jsp"></jsp:include>
<style type="text/css">
#contextmenu {
	border: 1px solid #aaa;
	border-bottom: 0;
	background: #eee;
	position: absolute;
	list-style: none;
	margin: 0;
	padding: 0;
	display: none;
}

#contextmenu li a {
	display: block;
	padding: 10px;
	border-bottom: 1px solid #aaa;
	cursor: pointer;
}

#contextmenu li a:hover {
	background: #fff;
}
</style>
</head>
<body class="no-skin">
	<jsp:include page="../header.jsp"></jsp:include>
	<div class="main-container" id="main-container">
		<script type="text/javascript">
			try {
				ace.settings.check('main-container', 'fixed');
			} catch (e) {
			}
			function toParent() {
				window.location.href = base + 'resource/index.html';
			}
		</script>
		<jsp:include page="../nav.jsp">
			<jsp:param value="resource_admin" name="page_index" />
		</jsp:include>
		<div class="main-content">
			<div class="main-content-inner">
				<div class="breadcrumbs" id="breadcrumbs">
					<ul class="breadcrumb">
						<li><i class="ace-icon fa fa-home home-icon"></i> <a
							href="${basePath}index.html"><strong>首页</strong></a></li>
						<li class="active"><b>定制资源</b></li>
						<li class="active"><b>详细信息</b></li>
					</ul>
				</div>
				<div class="page-content">
					<div class="row">
						<div class="col-xs-12">
							<div class="well well-sm">
								<a href="#" id="return_appconfig_btn"
									class="btn  btn-xs btn-round btn-primary" onclick="toParent()">
									<i class="ace-icon fa fa-share bigger-110 icon-only"></i><b>返回资源管理</b>
								</a>
							</div>
							<div>
								<div class="profile-user-info profile-user-info-striped">
									<div class="profile-info-row">
										<div class="profile-info-name">资源ID</div>
										<div id="detail_app_id" class="profile-info-value">
											${dk_resuser.resId}</div>
									</div>
									<div class="profile-info-row">
										<div class="profile-info-name">资源名称</div>
										<div class="profile-info-value">
											<span>${dk_resuser.resName}</span>
										</div>
									</div>
									<div class="profile-info-row">
										<div class="profile-info-name">CPU份额</div>
										<div class="profile-info-value">
											<span>${dk_resuser.resCPU}</span>
										</div>
									</div>
									<div class="profile-info-row">
										<div class="profile-info-name">内存容量</div>
										<div class="profile-info-value">
											<span>${dk_resuser.resMEM}</span>
										</div>
									</div>
									<!-- 
									@2016年2月1日，暂时屏蔽磁盘IO部分功能
									<div class="profile-info-row">
										<div class="profile-info-name">磁盘IO占比</div>
										<div class="profile-info-value">
											<span>${dk_resuser.resBLKIO}</span>
										</div>
									</div>
									-->
									<div class="profile-info-row">
										<div class="profile-info-name">描述信息</div>
										<div class="profile-info-value">
											<span>${dk_resuser.resDesc}</span>
										</div>
									</div>
									<div class="profile-info-row">
										<div class="profile-info-name">备注内容</div>
										<div class="profile-info-value">
											<span>${dk_resuser.resComment}</span>
										</div>
									</div>
									<div class="profile-info-row">
										<div class="profile-info-name">创建人</div>
										<div class="profile-info-value">
											<span>${dk_resuser.resUserName}</span>
										</div>
									</div>
									<div class="profile-info-row">
										<div class="profile-info-name">创建时间</div>
										<div class="profile-info-value">
											<span>${dk_resuser.resCreatetime}</span>
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

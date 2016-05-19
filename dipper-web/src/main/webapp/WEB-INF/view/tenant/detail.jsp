<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
<link rel="shortcut icon" href="${basePath }img/title_cloud.png" />
<title>仓库管理</title>
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
<meta charset="utf-8" />
<meta name="description" content="overview &amp; stats" />
<meta name="viewport"
	content="width=device-width, initial-scale=1.0, maximum-scale=1.0" />
<jsp:include page="../js.jsp"></jsp:include>
</head>
<body class="no-skin">
	<jsp:include page="../header.jsp"></jsp:include>
	<div class="main-container" id="main-container">
		<script type="text/javascript">
			try {
				ace.settings.check('main-container', 'fixed')
			} catch (e) {
			}
			function toParent() {
				window.location.href = base + 'registry/index.html';
			}
		</script>
		<jsp:include page="../nav.jsp">
			<jsp:param value="registry_admin" name="page_index" />
		</jsp:include>
		<div class="main-content">
			<div class="main-content-inner">
				<div class="breadcrumbs" id="breadcrumbs">
					<ul class="breadcrumb">
						<li><i class="ace-icon fa fa-home home-icon"></i> <a href="${basePath}index.html"><strong>首页</strong></a>
						</li>
						<li class="active"><b>仓库管理</b></li>
						<li class="active"><b>详细信息</b></li>
					</ul>
				</div>
				<div class="page-content">
					<div class="row">
						<div class="col-xs-12">
							<div class="well well-sm">
								<a href="#" id="create_image_btn" class="btn btn-xs btn-round btn-primary"
									onclick="toParent()"> <i
									class="ace-icon fa fa-share bigger-110 icon-only"></i> <b>返回仓库管理</b>
								</a>
							</div>
							<div>
								<div class="profile-user-info profile-user-info-striped">
									<div class="profile-info-row">
										<div class="profile-info-name">仓库ID</div>
										<div class="profile-info-value">
											<span>${registry.registryId}</span>
										</div>
									</div>
									<div class="profile-info-row">
										<div class="profile-info-name">仓库名称</div>
										<div class="profile-info-value">
											<span>${registry.registryName}</span>
										</div>
									</div>
									<div class="profile-info-row">
										<div class="profile-info-name">绑定端口</div>
										<div class="profile-info-value">
											<span>${registry.registryPort}</span>
										</div>
									</div>
									<div class="profile-info-row">
										<div class="profile-info-name">运行状态</div>
										<div class="profile-info-value" id="registry_status">
											<span></span>
										</div>
									</div>
									<div class="profile-info-row">
										<div class="profile-info-name">描述信息</div>
										<div class="profile-info-value">
											<span>${registry.registryDesc}</span>
										</div>
									</div>
									<div class="profile-info-row">
										<div class="profile-info-name">创建时间</div>
										<div class="profile-info-value">
											<span>${registry.registryCreatetime}</span>
										</div>
									</div>
								</div>
								<script type="text/javascript">
									var status = '${registry.registryStatus}';
									switch (status) {
									case '0':
										status = "删除";
										break;
									case '1':
										status = "正常";
										break;
									case '2':
										status = "异常";
										break;
									default:
										status = "未知";
										break;
									}
									$('#registry_status').html(status);
								</script>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</html>

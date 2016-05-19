<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
<link rel="shortcut icon" href="${basePath }img/title_cloud.png" />
<title>应用版本</title>
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
				window.location.href = base + 'image/index.html';
			}
		</script>
		<jsp:include page="../nav.jsp">
			<jsp:param value="image_admin" name="page_index" />
		</jsp:include>
		<div class="main-content">
			<div class="main-content-inner">
				<div class="breadcrumbs" id="breadcrumbs">
					<ul class="breadcrumb">
						<li><i class="ace-icon fa fa-home home-icon"></i> <a
							href="${basePath}index.html"><strong>首页</strong></a></li>
						<li class="active"><b>镜像管理</b></li>
					</ul>
				</div>
				<div class="page-content">
					<div class="row">
						<div class="col-xs-12">
							<div class="well well-sm">
								<a href="#" id="create_image_btn"
									class="btn btn-sm btn-round btn-primary" onclick="toParent()">
									<i class="ace-icon fa fa-share bigger-110 icon-only"></i> <b>返回上层</b>
								</a>
							</div>
							<div>
								<div class="profile-user-info profile-user-info-striped">
									<div class="profile-info-row">
										<div class="profile-info-name">镜像ID</div>
										<div class="profile-info-value">
											<span>${image.imageUuid}</span>
										</div>
									</div>
									<div class="profile-info-row">
										<div class="profile-info-name">镜像名称</div>
										<div class="profile-info-value">
											<span>${image.imageName }</span>
										</div>
									</div>
									<div class="profile-info-row">
										<div class="profile-info-name">镜像版本</div>
										<div class="profile-info-value">
											<span>${image.imageTag}</span>
										</div>
									</div>
									<div class="profile-info-row">
										<div class="profile-info-name">镜像状态</div>
										<div class="profile-info-value" id="image-status">
											<span></span>
										</div>
									</div>
									<div class="profile-info-row">
										<div class="profile-info-name">镜像类型</div>
										<div class="profile-info-value" id="image-type">
											<span></span>
										</div>
									</div>
									<!-- 
												<div class="profile-info-row">
													<div class="profile-info-name"> 镜像大小 </div>
													<div class="profile-info-value">
														<span >${image.imageSize }</span>
													</div>
												</div> -->
									<div class="profile-info-row">
										<div class="profile-info-name">创建时间</div>
										<div class="profile-info-value">
											<span>${image.imageCreatetime}</span>
										</div>
									</div>
								</div>
								<script type="text/javascript">
									var status = '${image.imageStatus}';
									var type = '${image.imageType}';
									switch (status) {
									case '0':
										status = "已删除";
										break;
									case '1':
										status = "已发布";
										break;
									case '2':
										status = "已制作";
										break;
									case '3':
										status = "异常";
										break;
									default:
										status = "未知";
										break;
									}
									if ('APP' == type) {
										type = '应用镜像';
									} else if ('BASIC' == type) {
										type = "基础镜像";
									} else {
										type = '未知类型';
									}
									$('#image-status').html(status);
									$('#image-type').html(type);
								</script>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</html>

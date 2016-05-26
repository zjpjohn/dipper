<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
<link rel="shortcut icon" href="${basePath }img/title_cloud.png" />
<title>构建应用</title>
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
<meta charset="utf-8" />
<meta name="description" content="overview &amp; stats" />
<meta name="viewport"
	content="width=device-width, initial-scale=1.0, maximum-scale=1.0" />
<jsp:include page="../js.jsp"></jsp:include>
<script src="${basePath }ace/assets/js/jtopo-0.4.8-min.js"></script>
<script src="${basePath }js/console/application_topology.js"></script>
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
				ace.settings.check('main-container', 'fixed')
			} catch (e) {
			}
			function toParent() {
				window.location.href = base + 'application/index.html';
			}
		</script>
		<jsp:include page="../nav.jsp">
			<jsp:param value="application_admin" name="page_index" />
		</jsp:include>
		<div class="main-content">
			<div class="main-content-inner">
				<div class="breadcrumbs" id="breadcrumbs">
					<ul class="breadcrumb">
						<li><i class="ace-icon fa fa-home home-icon"></i> <a
							href="${basePath}index.html"><strong>首页</strong></a></li>
						<li class="active"><b>应用管理</b></li>
						<li class="active"><b>详细信息</b></li>
					</ul>
				</div>
				<div class="page-content">
					<div class="row">
						<div class="col-xs-12">
							<div class="well well-sm">
								<a href="#" id="create_image_btn"
									class="btn  btn-xs btn-round btn-primary" onclick="toParent()">
									<i class="ace-icon fa fa-share bigger-110 icon-only"></i><b>返回应用管理</b>
								</a>
							</div>
							<div>
								<div class="profile-user-info profile-user-info-striped">
									<div class="profile-info-row">
										<div class="profile-info-name">应用ID</div>
										<div class="profile-info-value" id="application_id">
											${app.appId}</div>
									</div>
									<div class="profile-info-row">
										<div class="profile-info-name">应用名称</div>
										<div class="profile-info-value">
											<span>${app.appName}</span>
										</div>
									</div>
									<div class="profile-info-row">
										<div class="profile-info-name">应用端口</div>
										<div class="profile-info-value">
											<span>${app.appPriPort}</span>
										</div>
									</div>
									<div class="profile-info-row">
										<div class="profile-info-name">应用状态</div>
										<div class="profile-info-value" id="app_status">
											<span></span>
										</div>
									</div>
									<div class="profile-info-row">
										<div class="profile-info-name">环境约束</div>
										<div class="profile-info-value">
											<span>${app.envNames}</span>
										</div>
									</div>
									<div class="profile-info-row">
										<div class="profile-info-name">集群约束</div>
										<div class="profile-info-value">
											<span>${app.clusterNames}</span>
										</div>
									</div>
									<div class="profile-info-row">
										<div class="profile-info-name">资源约束</div>
										<div class="profile-info-value">
											<span>CPU：${app.appCpu}&nbsp;&nbsp;内存：${app.appMem}</span>
										</div>
									</div>
									<div class="profile-info-row">
										<div class="profile-info-name">环境变量</div>
										<div class="profile-info-value" id="app_type">
											<span>${app.appEnv}</span>
										</div>
									</div>
									<div class="profile-info-row">
										<div class="profile-info-name">挂载点</div>
										<div class="profile-info-value">
											<span>${app.appVolumn}</span>
										</div>
									</div>
									<div class="profile-info-row">
										<div class="profile-info-name">其他参数</div>
										<div class="profile-info-value" id="app_type">
											<span>${app.appParams}</span>
										</div>
									</div>
									<div class="profile-info-row">
										<div class="profile-info-name">启动命令</div>
										<div class="profile-info-value">
											<span>${app.appCommand}</span>
										</div>
									</div>
									<div class="profile-info-row">
										<div class="profile-info-name">访问路径</div>
										<div class="profile-info-value">
											<span>${app.appUrl}</span>
										</div>
									</div>
									<div class="profile-info-row">
										<div class="profile-info-name">应用负载</div>
										<div class="profile-info-value">
											<span>${app.balanceId}</span>
										</div>
									</div>
									<div class="profile-info-row">
										<div class="profile-info-name">监测代理</div>
										<div class="profile-info-value">
											<span>${app.appProxy}</span>
										</div>
									</div>
									<div class="profile-info-row">
										<div class="profile-info-name">应用描述</div>
										<div class="profile-info-value">
											<span>${app.appDesc}</span>
										</div>
									</div>
									<div class="profile-info-row">
										<div class="profile-info-name">创建时间</div>
										<div class="profile-info-value">
											<span>${app.appCreatetime}</span>
										</div>
									</div>
								</div>
								<script type="text/javascript">
									var status = '${app.appStatus}';
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
									$('#app_status').html(status);
								</script>
							</div>
							<!-- 插入拓扑展示内容，包含四层（应用->集群->镜像版本->容器列表）  -->
							<div class="row">
								<div class="col-xs-12">
									<div>
										<ul id="contextmenu" style="display: none">
											<li><a>详细信息</a></li>
											<li><a>放大视图</a></li>
											<li><a>缩小视图</a></li>
										</ul>
										<canvas id="canvas" height="600" width="1000"
											style="border: 1px solid rgb(68, 68, 68); cursor: default;"></canvas>
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

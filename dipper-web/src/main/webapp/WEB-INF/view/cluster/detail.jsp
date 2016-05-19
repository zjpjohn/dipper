<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
<link rel="shortcut icon" href="${basePath }img/title_cloud.png" />
<title>集群管理</title>
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
<meta charset="utf-8" />
<meta name="description" content="overview &amp; stats" />
<meta name="viewport"
	content="width=device-width, initial-scale=1.0, maximum-scale=1.0" />
<jsp:include page="../js.jsp"></jsp:include>
<script src="${basePath }ace/assets/js/jtopo-0.4.8-min.js"></script>
<script src="${basePath }js/console/cluster_topology.js"></script>
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
	padding: 2px;
	border-bottom: 2px solid #aaa;
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
				window.location.href = base + 'cluster/index.html';
			}
		</script>
		<jsp:include page="../nav.jsp">
			<jsp:param value="cluster_admin" name="page_index" />
		</jsp:include>
		<div class="main-content">
			<div class="main-content-inner">
				<div class="breadcrumbs" id="breadcrumbs">
					<ul class="breadcrumb">
						<li><i class="ace-icon fa fa-home home-icon"></i> <a
							href="${basePath}index.html"><strong>首页</strong></a></li>
						<li class="active"><b>集群详细</b></li>
					</ul>
				</div>
				<div class="page-content">
					<div class="row">
						<div class="col-xs-12">
							<div class="well well-sm">
								<a href="#" id="create_cluster_btn"
									class="btn btn-sm btn-round btn-primary" onclick="toParent()">
									<i class="ace-icon fa fa-share bigger-110 icon-only"></i> <b>返回上层</b>
								</a>
							</div>
							<div>
								<div class="profile-user-info profile-user-info-striped">
									<div class="profile-info-row">
										<div class="profile-info-name">集群ID</div>
										<div id="detail_cluster_id" class="profile-info-value">
											${clusterHC.clusterId}</div>
									</div>
									<div class="profile-info-row">
										<div class="profile-info-name">集群UUID</div>
										<div class="profile-info-value">
											<span>${clusterHC.clusterUuid }</span>
										</div>
									</div>
									<div class="profile-info-row">
										<div class="profile-info-name">集群名称</div>
										<div class="profile-info-value">
											<span>${clusterHC.clusterName}</span>
										</div>
									</div>
									<div class="profile-info-row">
										<div class="profile-info-name">集群类型</div>
										<div class="profile-info-value" id="cluster_type">
											<span></span>
										</div>
									</div>
									<div class="profile-info-row">
										<div class="profile-info-name">运行状态</div>
										<div class="profile-info-value" id="cluster_status">
											<span></span>
										</div>
									</div>
									<div class="profile-info-row">
										<div class="profile-info-name">集群端口</div>
										<div class="profile-info-value">
											<span>${clusterHC.clusterPort}</span>
										</div>
									</div>
									<div class="profile-info-row">
										<div class="profile-info-name">管理文件</div>
										<div class="profile-info-value">
											<span>${clusterHC.managePath}</span>
										</div>
									</div>
									<div class="profile-info-row">
										<div class="profile-info-name">集群描述</div>
										<div class="profile-info-value">
											<span>${clusterHC.clusterDesc}</span>
										</div>
									</div>
									<div class="profile-info-row">
										<div class="profile-info-name">主机数量</div>
										<div class="profile-info-value">
											<span>${clusterHC.hostQuantity}</span>
										</div>
									</div>
									<div class="profile-info-row">
										<div class="profile-info-name">容器数量</div>
										<div class="profile-info-value">
											<span>${clusterHC.containerQuantity}</span>
										</div>
									</div>
									<div class="profile-info-row">
										<div class="profile-info-name">管理节点IP</div>
										<div class="profile-info-value">
											<span>${clusterHC.masteHostIPaddr}</span>
										</div>
									</div>
									<div class="profile-info-row">
										<div class="profile-info-name">创建时间</div>
										<div class="profile-info-value">
											<span>${clusterHC.clusterCreatetime}</span>
										</div>
									</div>
									<div class="profile-info-row">
										<div class="profile-info-name">用户名称</div>
										<div class="profile-info-value">
											<span>${clusterHC.createUsername}</span>
										</div>
									</div>
								</div>
								<script type="text/javascript">
									var status = '${clusterHC.clusterStatus}';
									var type = '${clusterHC.clusterType}';
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
									switch (type) {
									case '0':
										type = "DOCKER";
										break;
									case '1':
										type = "REGISTRY";
										break;
									default:
										type = "未知";
										break;
									}
									$('#cluster_status').html(status);
									$('#cluster_type').html(type);
								</script>
							</div>
							<!-- 插入拓扑展示内容，包含三层，集群>主机>容器 -->
							<div class="row">
								<div class="col-xs-12">
									<div>
										<ul id="contextmenu" style="display: none">
											<li><a>详细信息</a></li>
											<li><a>原始尺寸</a></li>
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

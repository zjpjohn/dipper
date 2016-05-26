<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
<link rel="shortcut icon" href="${basePath }img/title_cloud.png" />
<title>主机管理</title>
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
				window.location.href = base + 'host/index.html';
			}
		</script>
		<jsp:include page="../nav.jsp">
			<jsp:param value="host_admin" name="page_index" />
		</jsp:include>
		<div class="main-content">
			<div class="main-content-inner">
				<div class="breadcrumbs" id="breadcrumbs">
					<ul class="breadcrumb">
						<li><i class="ace-icon fa fa-home home-icon"></i> <a
							href="${basePath}index.html"><strong>首页</strong></a></li>
						<li class="active"><b>主机管理</b></li>
					</ul>
				</div>
				<div class="page-content">
					<div class="row">
						<div class="col-xs-12">
							<div class="well well-sm">
								<a href="#" id="return_host_btn"
									class="btn btn-sm btn-primary btn-round" onclick="toParent()">
									<i class="ace-icon fa fa-share bigger-110 icon-only"></i> <b>返回上层</b>
								</a>
							</div>
							<div>
								<div class="profile-user-info profile-user-info-striped">
									<div class="profile-info-row">
										<div class="profile-info-name">主机ID</div>
										<div class="profile-info-value">
											<span>${host.hostId}</span>
										</div>
									</div>
									<div class="profile-info-row">
										<div class="profile-info-name">主机UUID</div>
										<div class="profile-info-value">
											<span>${host.hostUuid}</span>
										</div>
									</div>
									<div class="profile-info-row">
										<div class="profile-info-name">主机名称</div>
										<div class="profile-info-value">
											<span>${host.hostName}</span>
										</div>
									</div>
									<div class="profile-info-row">
										<div class="profile-info-name">主机类型</div>
										<div class="profile-info-value" id="host_type">
											<span></span>
										</div>
									</div>
									<script type="text/javascript">
										var type = '${host.hostType}';
										switch (type) {
										case '0':
											type = "Swarm主机";
											break;
										case '1':
											type = "Docker宿主机";
											break;
										case '2':
											type = "仓库主机";
											break;
										case '3':
											type = "Nginx主机";
											break;
										default:
											type = "其他";
											break;
										}
										$('#host_type').text(type);
									</script>
									<div class="profile-info-row">
										<div class="profile-info-name">IP地址</div>
										<div class="profile-info-value">
											<span>${host.hostIp }</span>
										</div>
									</div>
									<div class="profile-info-row">
										<div class="profile-info-name">CPU数量</div>
										<div class="profile-info-value">
											<span>${host.hostCpu}</span>
										</div>
									</div>
									<div class="profile-info-row">
										<div class="profile-info-name">内存容量</div>
										<div class="profile-info-value">
											<span>${host.hostMem}MB</span>
										</div>
									</div>
									<div class="profile-info-row">
										<div class="profile-info-name">集群ID</div>
										<div class="profile-info-value">
											<span>${host.clusterId}</span>
										</div>
									</div>
									<div class="profile-info-row">
										<div class="profile-info-name">内核版本</div>
										<div class="profile-info-value">
											<span>${host.hostKernelVersion}</span>
										</div>
									</div>
									<div class="profile-info-row">
										<div class="profile-info-name">已安装软件</div>
										<div class="profile-info-value">
											<span>${softNames}</span>
										</div>
									</div>
									<div class="profile-info-row">
										<div class="profile-info-name">创建人</div>
										<div class="profile-info-value">
											<span>${creatorName}</span>
										</div>
									</div>
									<div class="profile-info-row">
										<div class="profile-info-name">创建时间</div>
										<div class="profile-info-value">
											<span>${host.hostCreatetime}</span>
										</div>
									</div>
								</div>
								<script type="text/javascript">
									var type = '${host.hostType}';
									switch (type) {
									case 0:
										return 'SWARM';
										break;
									case 1:
										return 'DOCKER';
										break;
									case 2:
										return 'REGISTRY';
										break;
									case 3:
										return 'NGINX';
										break;
									default:
										status = "未知";
										break;
									}
									$('#host_type').html(status);
								</script>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</html>

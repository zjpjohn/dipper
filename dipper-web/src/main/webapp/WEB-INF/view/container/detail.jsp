<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
	<head>
		<link rel="shortcut icon" href="${basePath }img/title_cloud.png" />
		<title>容器管理</title>
		<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
		<meta charset="utf-8" />
		<meta name="description" content="overview &amp; stats" />
		<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0" />
		<jsp:include page="../js.jsp"></jsp:include>
		<script type="text/javascript">
				try{ace.settings.check('main-container' , 'fixed')}catch(e){}
				function toParent(){
					window.location.href=base+'appRelease/detail/${appId}/${imageId}/${balanceId}.html';
				}
		</script>
	</head>
	<body class="no-skin">
		<jsp:include page="../header.jsp"></jsp:include>
		<div class="main-container" id="main-container">
			<jsp:include page="../nav.jsp">
			<jsp:param value="pushapp_admin" name="page_index" />
		</jsp:include>
			<div class="main-content">
				<div class="main-content-inner">
					<div class="breadcrumbs" id="breadcrumbs">
						<ul class="breadcrumb">
							<li>
								<i class="ace-icon fa fa-home home-icon"></i>
								<a href="${basePath}index.html"><strong>首页</strong></a>
							</li>
							<li class="active"><b>容器管理</b></li>
						</ul>
					</div>
					<div class="page-content">
						<div class="row">
							<div class="col-xs-12">
								<div class="well well-sm">
									<a href="#" class="btn btn-sm btn-primary" onclick="toParent()">
										<i class="ace-icon fa fa-share bigger-110 icon-only"></i>
										<b>返回上层</b>
									</a>
								</div>
								<div>
									<div class="profile-user-info profile-user-info-striped">
										<div class="profile-info-row">
											<div class="profile-info-name"> 容器ID </div>
											<div class="profile-info-value">
												<span>${container.conId}</span>
											</div>
										</div>
										<div class="profile-info-row">
											<div class="profile-info-name"> 容器名称 </div>
											<div class="profile-info-value">
												<span>${container.conName}</span>
											</div>
										</div>
										<div class="profile-info-row">
											<div class="profile-info-name"> 容器状态 </div>
											<div class="profile-info-value">
												${container.conPower}
											</div>
										</div>
										<div class="profile-info-row">
											<div class="profile-info-name"> 所属主机 </div>
											<div class="profile-info-value" >
												<span >${container.hostName}</span>
											</div>
										</div>
										<div class="profile-info-row">
											<div class="profile-info-name"> 所属应用 </div>
											<div class="profile-info-value">
												<span>${container.appName}</span>
											</div>
										</div>
										<div class="profile-info-row">
											<div class="profile-info-name">创建命令 </div>
											<div class="profile-info-value" >
												<span>${container.conStartCom}</span>
											</div>
										</div>
										<div class="profile-info-row">
											<div class="profile-info-name"> 端口信息 </div>
											<div class="profile-info-value" >
												<span>${container.conPort}</span>
											</div>
										</div>
										<div class="profile-info-row">
											<div class="profile-info-name"> 创建时间 </div>
											<div class="profile-info-value">
												<span >${container.createTime}</span>
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

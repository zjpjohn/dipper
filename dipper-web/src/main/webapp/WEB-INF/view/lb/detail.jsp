<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
	<head>
		<link rel="shortcut icon" href="${basePath }img/title_cloud.png" />
		<title>应用负载</title>
		<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
		<meta charset="utf-8" />
		<meta name="description" content="overview &amp; stats" />
		<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0" />
		<jsp:include page="../js.jsp"></jsp:include>
	</head>
	<body class="no-skin">
		<jsp:include page="../header.jsp"></jsp:include>
		<div class="main-container" id="main-container">
			<script type="text/javascript">
				try{ace.settings.check('main-container' , 'fixed')}catch(e){}
				function toParent(){
					window.location.href=base+'lb/index.html';
				}
			</script>
			<jsp:include page="../nav.jsp">
				<jsp:param value="lb_admin" name="page_index"/>
			</jsp:include>		
			<div class="main-content">
				<div class="main-content-inner">
					<div class="breadcrumbs" id="breadcrumbs">
						<ul class="breadcrumb">
							<li>
								<i class="ace-icon fa fa-home home-icon"></i>
								<a href="${basePath}index.html"><strong>首页</strong></a>
							</li>
							<li class="active"><b>负载管理</b></li>
						</ul>
					</div>
					<div class="page-content">
						<div class="row">
							<div class="col-xs-12">
								<div class="well well-sm">
									<a href="#" id="create_lb_btn" class="btn btn-sm btn-round btn-primary" onclick="toParent()">
										<i class="ace-icon fa fa-share bigger-110 icon-only"></i>
										<b>返回上层</b>
									</a>
								</div>
								<div>
									<div class="profile-user-info profile-user-info-striped">
										<div class="profile-info-row">
											<div class="profile-info-name"> 负载ID </div>
											<div class="profile-info-value">
												<span>${lb.lbId}</span>
											</div>
										</div>
										<div class="profile-info-row">
											<div class="profile-info-name"> 负载名称 </div>
											<div class="profile-info-value">
												<span>${lb.lbName}</span>
											</div>
										</div>
										<div class="profile-info-row">
											<div class="profile-info-name"> 负载状态 </div>
											<div class="profile-info-value">
												<span>${lb.lbStatus}</span>
											</div>
										</div>
										<div class="profile-info-row">
											<div class="profile-info-name"> 应用信息 </div>
											<div class="profile-info-value" id="lb-status">
												<span>${lb.appInfo}</span>
											</div>
										</div>
										<div class="profile-info-row">
											<div class="profile-info-name"> 主服务器 </div>
											<div class="profile-info-value"  id="lb-host">
												<span>${lb.hostServer}</span>
											</div>
										</div>
										<div class="profile-info-row">
											<div class="profile-info-name"> 文件位置 </div>
											<div class="profile-info-value"  id="lb-host-conf">
												<span>${lb.hostConf}</span>
											</div>
										</div>
										<div class="profile-info-row">
											<div class="profile-info-name"> 备用服务器 </div>
											<div class="profile-info-value"  id="lb-backup">
												<span>${lb.backupServer}</span>
											</div>
										</div>
										<div class="profile-info-row">
											<div class="profile-info-name"> 配置文件 </div>
											<div class="profile-info-value"  id="lb-backup-conf">
												<span>${lb.backupConf}</span>
											</div>
										</div>
										<div class="profile-info-row">
											<div class="profile-info-name"> 描述信息 </div>
											<div class="profile-info-value"  id="lb-desc">
												<span>${lb.lbDesc}</span>
											</div>
										</div>
										<div class="profile-info-row">
											<div class="profile-info-name"> 创建时间 </div>
											<div class="profile-info-value">
												<span>${lb.createTime}</span>
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

<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html>
<html lang="en">
<head>
<link rel="shortcut icon" href="${basePath }img/title_cloud.png" />
<title>数据纵览</title>
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
<meta charset="utf-8" />
<meta name="description" content="overview &amp; stats" />
<meta name="viewport"
	content="width=device-width, initial-scale=1.0, maximum-scale=1.0" />
<jsp:include page="js.jsp"></jsp:include>
<script src="${basePath }js/console/dashboard.js"></script>
<script src="${basePath }ace/assets/js/flot/jquery.flot.min.js"></script>
<script src="${basePath }ace/assets/js/flot/jquery.flot.pie.min.js"></script>
<script src="${basePath }ace/assets/js/flot/jquery.flot.resize.min.js"></script>
<c:set var="authStr" value='${pagesAuth}'></c:set>
</head>
<body class="no-skin">
	<jsp:include page="header.jsp"></jsp:include>
	<div class="main-container" id="main-container">
		<script type="text/javascript">
			try {
				ace.settings.check('main-container', 'fixed');
			} catch (e) {
			}

			/*当页面加载完成后，强制从Server端获取全部最新文件。类似于Ctrl+F5*/
			function reloadDevops() {
				/*向后台请求，是否需要自动刷新浏览器中的缓存*/
				$.post(base + 'check/reload', null, function(response) {
					if (response.reload_devops == "true") {
						/*用户刚刚登陆平台，需要重新加载所有相关网页文件*/
						location.reload(true);
					} else {
						/*无需加载相关文件，直接返回*/
						return;
					}
				}, 'json');
			}
			window.onload = reloadDevops;
		</script>
		<jsp:include page="nav.jsp">
			<jsp:param value="manage_dashboard" name="page_index" />
		</jsp:include>
		<div class="main-content">
			<div class="main-content-inner">
				<div class="breadcrumbs" id="breadcrumbs">
					<ul class="breadcrumb">
						<li><i class="ace-icon fa fa-home home-icon"></i> <a
							href="javascript:void(0)"><strong>首页</strong></a></li>
					</ul>
				</div>
				<div class="page-content">
					<div class="row" id="dashboard_mainview">
						<%-- <div class="col-xs-12">
							<div class="widget-box">
								<div
									class="widget-header widget-header-flat widget-header-small">
									<h5 class="widget-title">
										<i class="ace-icon fa fa-signal"></i><b>Devops管理平台</b>
									</h5>
								</div>
								<div class="widget-body">
									<div class="widget-main">
										<div id="piechart-placeholder"></div>
										<div class="hr hr8 hr-double"></div>
										<div class="clearfix">
											<div class="grid4">
												<span class="grey" id="dashboard_cluster_num"><a
													href="${basePath}cluster/index.html"><i
														class="ace-icon fa fa-globe fa-4x blue"></i>&nbsp;<b>基础设施</b></a>
												</span>
											</div>
											<div class="grid4">
												<span class="grey" id="dashboard_application_num"><a
													href="${basePath}app/index.html"><i
														class="ace-icon fa fa-laptop fa-4x pink"></i>&nbsp;<b>应用管理</b></a>
												</span>
											</div>
											<div class="grid4">
												<span class="grey" id="dashboard_loadbalance_num"><a
													href="${basePath}lb/index.html"><i
														class="ace-icon fa fa-cogs fa-4x green"></i>&nbsp;<b>应用运维</b></a>
												</span>
											</div>
											<div class="grid4">
												<span class="grey" id="dashboard_registry_num"><a
													href="${basePath}registry/index.html"><i
														class="ace-icon fa fa-home fa-4x dark"></i>&nbsp;<b>用户角色</b></a>
												</span>
											</div>
										</div>
									</div>
									<!-- /.widget-main -->
								</div>
								<!-- /.widget-body -->
							</div>
							<!-- /.widget-box -->
						</div> --%>

						<div class="col-sm-6">
							<div class="widget-box">
								<div
									class="widget-header widget-header-flat widget-header-small">
									<h5 class="widget-title">
										<i class="ace-icon fa fa-desktop"></i> <b>服务器统计</b>
									</h5>
								</div>
								<div class="widget-body">
									<div class="widget-main">
										<div class="hr hr8 hr-double"></div>
										<div class="clearfix">
										
											<div class="grid4">
												<span class="grey" id="dashboard_dockernum"
													<c:if test="${fn:contains(authStr,'hostIndex')}">
														 name="active" 
														 onclick="JumpToHostIndex(1)" 
														 style="cursor: pointer"
													</c:if>
													><i
													class="ace-icon fa fa-cubes fa-2x orange"></i>&nbsp;<b>应用主机</b></span>
											</div>
										
											<div class="grid4">
												<span class="grey" id="dashboard_swarmnum"
													<c:if test="${fn:contains(authStr,'hostIndex')}">
														 name="active" 
														 onclick="JumpToHostIndex(0)" style="cursor: pointer"
													</c:if>
													><i
													class="ace-icon fa fa-puzzle-piece fa-2x red"></i>&nbsp;<b>集群主机</b>
												</span>
											</div>
											<div class="grid4">
												<span class="grey" id="dashboard_nginxnum"
													<c:if test="${fn:contains(authStr,'hostIndex')}">
														 name="active" 
														 onclick="JumpToHostIndex(3)" style="cursor: pointer"
													</c:if>
													><i
													class="ace-icon fa fa-sitemap fa-2x green"></i>&nbsp;<b>负载主机</b></span>
											</div>
											<div class="grid4">
												<span class="grey" id="dashboard_registrynum"
													<c:if test="${fn:contains(authStr,'hostIndex')}">
														 name="active" 
														 onclick="JumpToHostIndex(2)" style="cursor: pointer"
													</c:if>
													><i
													class="ace-icon fa fa-university fa-2x brown"></i>&nbsp;<b>仓库主机</b></span>
											</div>
										</div>
									</div>
									<!-- /.widget-main -->
								</div>
								<!-- /.widget-body -->
							</div>
							<!-- /.widget-box -->
						</div>
						<!-- /.col -->
						<div class="col-sm-6">
							<div class="widget-box">
								<div
									class="widget-header widget-header-flat widget-header-small">
									<h5 class="widget-title">
										<i class="ace-icon fa fa-inbox"></i> <b>应用实例统计</b>
									</h5>
								</div>
								<div class="widget-body">
									<div class="widget-main">
										<div class="hr hr8 hr-double"></div>
										<div class="clearfix">
											<div class="grid3">
												<span class="grey" id="dashboard_container_total"
													<c:if test="${fn:contains(authStr,'pushapp')}">
														 name="active" 
														 onclick="JumpToContainerIndex(0)" style="cursor: pointer"
													</c:if>
													><i
													class="ace-icon fa fa-list-alt fa-2x blue"></i>&nbsp;<b>总数</b>
												</span>
											</div>
											<div class="grid3">
												<span class="grey" id="dashboard_container_running"
													<c:if test="${fn:contains(authStr,'pushapp')}">
														 name="active" 
														 onclick="JumpToContainerIndex(1)" style="cursor: pointer"
													</c:if>
													><i
													class="ace-icon fa fa-play fa-2x green"></i>&nbsp;<b>运行实例</b>
												</span>
											</div>
											<div class="grid3">
												<span class="grey" id="dashboard_container_stop"
													<c:if test="${fn:contains(authStr,'pushapp')}">
														 name="active" 
														 onclick="JumpToContainerIndex(2)" style="cursor: pointer"
													</c:if>
													><i
													class="ace-icon fa fa-pause fa-2x red"></i>&nbsp;<b>维护实例</b>
												</span>
											</div>
										</div>
									</div>
									<!-- /.widget-main -->
								</div>
								<!-- /.widget-body -->
							</div>
							<!-- /.widget-box -->
						</div>


						<!-- /.col -->
						<div class="col-xs-12">
							<div class="widget-box">
								<div
									class="widget-header widget-header-flat widget-header-small">
									<h5 class="widget-title">
										<i class="ace-icon fa fa-signal"></i><b>其他统计信息</b>
									</h5>
								</div>
								<div class="widget-body">
									<div class="widget-main">
										<div class="hr hr8 hr-double"></div>
										<div class="clearfix">
											<div class="grid4">
												<span class="grey" id="dashboard_cluster_num"
												<c:if test="${fn:contains(authStr,'clusterIndex')}">
													name="active" 
												</c:if>>
												<c:if test="${fn:contains(authStr,'clusterIndex')}">
													<a href="${basePath}cluster/index.html">
												</c:if>
												<i class="ace-icon fa fa-globe fa-4x blue"></i>&nbsp;<b>集群</b>
												<c:if test="${fn:contains(authStr,'clusterIndex')}">
													</a>
												</c:if>
												</span>
											</div>
											<div class="grid4">
												<span class="grey" id="dashboard_application_num"
													<c:if test="${fn:contains(authStr,'applicationIndex')}">
														name="active" 
													</c:if>>
													<c:if test="${fn:contains(authStr,'applicationIndex')}">
														<a href="${basePath}application/index.html">
													</c:if>
													<i class="ace-icon fa fa-laptop fa-4x pink"></i>&nbsp;<b>应用</b>
													<c:if test="${fn:contains(authStr,'applicationIndex')}">
														</a>
													</c:if>
												</span>
											</div>
											<div class="grid4">
												<span class="grey" id="dashboard_loadbalance_num"
													<c:if test="${fn:contains(authStr,'lbIndex')}">
														name="active" 
													</c:if>>
													<c:if test="${fn:contains(authStr,'lbIndex')}">
														<a href="${basePath}lb/index.html">
													</c:if>
													<i class="ace-icon fa fa-cogs fa-4x green"></i>&nbsp;<b>负载</b>
													<c:if test="${fn:contains(authStr,'lbIndex')}">
														</a>
													</c:if>
												</span>
											</div>
											<div class="grid4">
												<span class="grey" id="dashboard_registry_num"
													<c:if test="${fn:contains(authStr,'registryIndex')}">
														name="active" 
													</c:if>>
													<c:if test="${fn:contains(authStr,'registryIndex')}">
														<a href="${basePath}registry/index.html">
													</c:if>
													<i class="ace-icon fa fa-home fa-4x dark"></i>&nbsp;<b>仓库</b>
													<c:if test="${fn:contains(authStr,'registryIndex')}">
														</a>
													</c:if>
												</span>
											</div>
										</div>
									</div>
									<!-- /.widget-main -->
								</div>
								<!-- /.widget-body -->
							</div>
							<!-- /.widget-box -->
						</div>
						<!-- /.col -->

						<!-- 显示应用资源占用情况的信息 -->
						<div class="col-xs-12">
							<div class="widget-box transparent" id="recent-box">
								<div class="widget-header">
									<h4 class="widget-title lighter smaller">
										<i class="ace-icon fa fa-bar-chart-o red"></i>应用使用资源情况
									</h4>
									<div class="widget-toolbar no-border">
										<ul class="nav nav-tabs" id="recent-tab">
										</ul>
									</div>
								</div>
								<div class="widget-body">
									<div class="widget-main padding-4">
										<div class="tab-content padding-8" id="appview_mainshow">
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
</body>
</html>

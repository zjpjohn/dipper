<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<!DOCTYPE html>
<html lang="en">
<head>
<link rel="shortcut icon" href="${basePath }img/title_cloud.png" />
<title>监测管理</title>
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
<meta charset="utf-8" />
<meta name="description" content="overview &amp; stats" />
<meta name="viewport"
	content="width=device-width, initial-scale=1.0, maximum-scale=1.0" />
<jsp:include page="../js.jsp"></jsp:include>
<script src="${basePath }js/console/monitor.js"></script>
<link rel="stylesheet" type="text/css"
	href="${basePath }css/monitor/jquery.gridster.min.css">
<link rel="stylesheet" type="text/css"
	href="${basePath }css/monitor/style.css">
<script type="text/javascript"
	src="${basePath }ace/monitor/jquery.gridster.js" charset="utf-8"></script>
<script type="text/javascript"
	src="${basePath }ace/monitor/FusionCharts.js" charset="utf-8"></script>
<c:set var="authButton" value='${buttonsAuth}'></c:set>

</head>
<body class="no-skin">
	<jsp:include page="../header.jsp"></jsp:include>
	<div class="main-container" id="main-container">
		<script type="text/javascript">
			try {
				ace.settings.check('main-container', 'fixed');
			} catch (e) {
			}
		</script>
		<jsp:include page="../nav.jsp">
			<jsp:param value="monitor_admin" name="page_index" />
		</jsp:include>
		<div class="main-content">
			<div class="main-content-inner">
				<div class="breadcrumbs" id="breadcrumbs">
					<ul class="breadcrumb">
						<li><i class="ace-icon fa fa-home home-icon"></i> <a
							href="${basePath}index.html"><strong>首页</strong></a></li>
						<li class="active"><i
							class="ace-icon fa fa-exclamation-circle"></i> <b>监测管理</b></li>
					</ul>
				</div>
				<div class="page-header" id="show_header">
					<h1>
						<!-- （1）用户进入应用维度监控页面时，增加返回应用维度首页按钮 -->
						<c:if test="${type=='app'}">
							<a onclick="window.location.href='/monitor/index.html'"
								class="btn btn-sm btn-success btn-round"> <i
								class="ace-icon fa fa-cubes bigger-125"></i><b>返回应用视图</b>
							</a>
						</c:if>
						<!-- （2）用户进入集群维度监控页面时，增加返回集群维度首页按钮 -->
						<c:if test="${type=='cluster'}">
							<a onclick="window.location.href='/monitor/cluster_view.html'"
								class="btn btn-sm btn-primary btn-round"> <i
								class="ace-icon fa fa-sitemap bigger-125"></i><b>返回集群视图</b>
							</a>
						</c:if>
						【<font color="blue"><b>${itemName}</b></font>】监控视图
					</h1>
				</div>

			</div>
		</div>
		<%--advanced search modal end --%>
	</div>
	<!-- 保存应用ID和请求视图类型的信息 -->
	<input id="appId" type="hidden" value="${appId}">
	<input id="clusterId" type="hidden" value="${clusterId}">
	<input id="type" type="hidden" value="${type}">


	<div role="main">
		<section class="demo">
			<div>
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				<button onclick="changeInterval(1)">&nbsp;&nbsp;1秒&nbsp;&nbsp;</button>
				<button onclick="changeInterval(5)">&nbsp;&nbsp;5秒&nbsp;&nbsp;</button>
				<button onclick="changeInterval(10)">&nbsp;&nbsp;10秒&nbsp;&nbsp;</button>
				<button onclick="changeInterval(30)">&nbsp;&nbsp;30秒&nbsp;&nbsp;</button>
				<button onclick="changeInterval(60)">&nbsp;&nbsp;60秒&nbsp;&nbsp;</button>
			</div>
			<div class="gridster" id="zabbix_show">
				<ul></ul>
			</div>
		</section>
	</div>
	<script type="text/javascript">
		var gridster;
		$(function() {
			gridster = $(".gridster > ul").gridster({
				widget_margins : [ 10, 10 ],
				widget_base_dimensions : [ 140, 140 ],
				min_cols : 6
			}).data('gridster');
		});
	</script>
	<div style="text-align: center; clear: both"></div>
</body>
</html>

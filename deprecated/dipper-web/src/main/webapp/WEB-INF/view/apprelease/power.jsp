<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<!DOCTYPE html>
<html lang="en">
<head>
<link rel="shortcut icon" href="${basePath }img/title_cloud.png" />
<title>应用发布</title>
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
<meta charset="utf-8" />
<meta name="description" content="overview &amp; stats" />
<meta name="viewport"
	content="width=device-width, initial-scale=1.0, maximum-scale=1.0" />
<jsp:include page="../js.jsp"></jsp:include>
<link rel="stylesheet" href="${basePath}css/user/container.css" />
<script src="${basePath }ace/assets/js/jquery.validate.min.js"></script>
<script src="${basePath }ace/assets/js/additional-methods.min.js"></script>
<script src="${basePath }ace/assets/js/jquery.maskedinput.min.js"></script>
<script src="${basePath }ace/assets/js/select2.min.js"></script>
<script src="${basePath }js/bootstrap-paginator.min.js"></script>
<script src="${basePath }ace/assets/js/fuelux/fuelux.wizard.min.js"></script>
<script src="${basePath }js/console/appreleasepower.js"></script>
<c:set var="authButton" value='${buttonsAuth}'></c:set>

</head>
<body class="no-skin">
	<jsp:include page="../header.jsp"></jsp:include>
	<c:if test="${fn:contains(authButton,'containerTrash')}">
		<input type="hidden" id="delete_container">
	</c:if>
	<c:if test="${fn:contains(authButton,'containerStop')}">
		<input type="hidden" id="stop_container">
	</c:if>
	<c:if test="${fn:contains(authButton,'containerStart')}">
		<input type="hidden" id="start_container">
	</c:if>

	<div class="main-container" id="main-container">
		<script type="text/javascript">
			try {
				ace.settings.check('main-container', 'fixed')
			} catch (e) {
			}
		</script>
		<jsp:include page="../nav.jsp">
			<jsp:param value="pushapp_admin" name="page_index" />
		</jsp:include>
		<div class="main-content">
			<div class="main-content-inner">
				<div class="breadcrumbs" id="breadcrumbs">
					<ul class="breadcrumb">
						<li><i class="ace-icon fa fa-home home-icon"></i> <a
							href="${basePath}index.html"><strong>首页</strong></a></li>
						<li class="active"><b>应用发布</b></li>
						<li class="active"><b>&nbsp;<span id="show_pwrstatus"></span>&nbsp;实例列表
						</b></li>
					</ul>
				</div>
				<input id="powerStatus" type="hidden" value="${powerStatus}">
				<div class="page-content">
					<div class="row">
						<div class="col-xs-12">
							<div class="well well-sm">
								<button class="btn btn-sm btn-danger btn-round"
									onclick="window.location.href='/index.html'">
									<i class="ace-icon fa fa-arrow-left bigger-125"></i><b>返回首页</b>
								</button>
								<div class="btn-group">
									<button data-toggle="dropdown"
										class="btn btn-primary btn-round btn-sm dropdown-toggle">
										<i class="ace-icon fa fa-wrench  bigger-110 icon-only"></i> <b>更多操作</b>
										<i class="ace-icon fa fa-angle-down icon-on-right"></i> <input
											id="conIds" type="hidden">
									</button>
									<ul class="dropdown-menu dropdown-primary dropdown-menu-left">
										<c:if test="${fn:contains(authButton,'containerStart')}">
											<li><a class="btn-forbidden" id="start"><span
													class="glyphicon glyphicon-play"></span>&nbsp;批量启动</a></li>
										</c:if>
										<c:if test="${fn:contains(authButton,'containerStop')}">
											<li><a class="btn-forbidden" id="stop"><span
													class="glyphicon glyphicon-stop"></span>&nbsp;批量停止</a></li>
										</c:if>
										<c:if test="${fn:contains(authButton,'containerTrash')}">
											<li><a class="btn-forbidden" id="trash"><span
													class="glyphicon glyphicon-trash"></span>&nbsp;批量删除</a></li>
										</c:if>
									</ul>
								</div>
							</div>
							<div>
								<c:if test="${fn:contains(authButton,'containerList')}">
									<table id="container_list"></table>
									<div id="container_page"></div>
								</c:if>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</body>
</html>

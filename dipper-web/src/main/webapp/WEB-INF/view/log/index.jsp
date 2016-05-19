<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<!DOCTYPE html>
<html lang="en">
<head>
<link rel="shortcut icon" href="${basePath }img/title_cloud.png" />
<title>日志管理</title>
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
<meta charset="utf-8" />
<meta name="description" content="overview &amp; stats" />
<meta name="viewport"
	content="width=device-width, initial-scale=1.0, maximum-scale=1.0" />
<jsp:include page="../js.jsp"></jsp:include>
<link rel="stylesheet"
	href="${basePath}ace/assets/css/bootstrap-datetimepicker.css" />
<script src="${basePath}ace/assets/js/date-time/moment.min.js"></script>
<script src="${basePath}ace/assets/js/jquery-ui.custom.min.js"></script>
<script
	src="${basePath}ace/assets/js/date-time/bootstrap-datetimepicker.min.js"></script>
<script src="${basePath }js/console/log.js"></script>
<c:set var="authButton" value='${buttonsAuth}'></c:set>

</head>
<body class="no-skin">
	<jsp:include page="../header.jsp"></jsp:include>
	<div class="main-container" id="main-container">
		<script type="text/javascript">
			try {
				ace.settings.check('main-container', 'fixed')
			} catch (e) {
			}
		</script>
		<jsp:include page="../nav.jsp">
			<jsp:param value="log_admin" name="page_index" />
		</jsp:include>
		<div class="main-content">
			<div class="main-content-inner">
				<div class="breadcrumbs" id="breadcrumbs">
					<ul class="breadcrumb">
						<li><i class="ace-icon fa fa-home home-icon"></i> <a
							href="${basePath}index.html"><strong>首页</strong></a></li>
						<li class="active"><i class="ace-icon fa fa-file-o"></i><b>日志管理</b></li>
					</ul>
				</div>
				<div class="page-header">
				   <h1>欢迎来到日志管理 
				   	<small>
				   		<br>日志是发布平台中所有操作的监控日志，日志管理提供日志的查询功能。详情查看“使用文档”
				   	</small>
				   </h1>
				</div>
				<div class="page-content">
					<div class="row">
						<div class="col-xs-12">
							<div class="well well-sm">
								<c:if test="${fn:contains(authButton,'logList')}">
									<form class="form-horizontal" role="form">
										<div class="form-group">
											<label class="col-sm-1 control-label no-padding-right"
												for="form-field-1-1"><strong>操作对象</strong> </label>
											<div class="col-sm-2">
												<div class="input-group">
													<input id="target" type="text" class="form-control" />
												</div>
											</div>
											<label class="col-sm-1 control-label no-padding-right"
												for="form-field-1-1"><strong>时间范围</strong> </label>
											<div class="col-sm-3">
												<div class="input-group">
													<input id="start_time" type="text" class="form-control" />
													<span class="input-group-addon"> <i
														class="fa fa-clock-o bigger-110"></i>
													</span>
												</div>
											</div>
											<div class="col-sm-3">
												<div class="input-group">
													<input id="end_time" type="text" class="form-control" /> <span
														class="input-group-addon"> <i
														class="fa fa-clock-o bigger-110"></i>
													</span>
												</div>
											</div>
											<div class="col-sm-1">
												<span class="input-group-btn">
													<button type="button"
														class="btn btn-primary btn-sm btn-round"
														onclick="searchLogs()">
														查询 <i class="ace-icon fa fa-search icon-on-right bigger-110"></i>
													</button>
												</span>
											</div>
										</div>
									</form>
								</c:if>
							</div>
						</div>
						<div class="col-xs-12">
							<c:if test="${fn:contains(authButton,'logList')}">
								<div>
									<table id="log_list"></table>
									<div id="log_page"></div>
								</div>
							</c:if>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</body>
</html>

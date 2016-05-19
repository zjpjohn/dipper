<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<!DOCTYPE html>
<html lang="en">
<head>
<link rel="shortcut icon" href="${basePath }img/title_cloud.png" />
<title>异常处理</title>
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
<meta charset="utf-8" />
<meta name="description" content="overview &amp; stats" />
<meta name="viewport"
	content="width=device-width, initial-scale=1.0, maximum-scale=1.0" />
<jsp:include page="../js.jsp"></jsp:include>
<script src="${basePath }js/console/excep.js"></script>
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
			<jsp:param value="exception_admin" name="page_index" />
		</jsp:include>
		<div class="main-content">
			<div class="main-content-inner">
				<div class="breadcrumbs" id="breadcrumbs">
					<ul class="breadcrumb">
						<li><i class="ace-icon fa fa-home home-icon"></i> <a
							href="${basePath}index.html"><strong>首页</strong></a></li>
						<li class="active"><i class="ace-icon fa fa-exclamation"></i>
							<b>异常处理</b></li>
					</ul>
				</div>
				<div class="page-header">
					<h1>
						欢迎来到异常处理 <small> <br>
						</small>
					</h1>
				</div>
				<div class="page-content">
					<div class="row">
						<div class="col-xs-12">
							<div class="well well-sm">
								<c:if test="#">
									<button class="btn btn-sm btn-primary btn-round"
										data-toggle="modal" data-target="#createParamModal">
										<i class="ace-icon fa fa-pencil-square-o bigger-125"></i> <b>功能待定</b>
									</button>
								</c:if>
								<c:if test="#">
									<button class="btn btn-sm btn-primary btn-round">
										<i class="ace-icon fa fa-trash-o bigger-125"></i> <b>功能待定</b>
									</button>
								</c:if>
							</div>
							<div>
								<span>该功能模块开发中，请耐心等待！</span>
							</div>
							<%-- <c:if test="${fn:contains(authButton,'paramList')}">
								<div>
									<table id="param_list"></table>
									<div id="param_page"></div>
								</div>
							</c:if> --%>
						</div>
					</div>
				</div>
			</div>
		</div>
		
		<%--advanced search modal end --%>
	</div>
</body>
</html>

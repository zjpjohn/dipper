<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
<link rel="shortcut icon" href="${basePath }img/title_cloud.png" />
<title>软件详情</title>
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
<meta charset="utf-8" />
<meta name="description" content="overview &amp; stats" />
<meta name="viewport"
	content="width=device-width, initial-scale=1.0, maximum-scale=1.0" />
<jsp:include page="../js.jsp"></jsp:include>
</head>
<body class="no-skin">
	<jsp:include page="../header.jsp"></jsp:include>
	<div class="main-container">
		<jsp:include page="../nav.jsp">
			<jsp:param value="software_admin" name="page_index" />
		</jsp:include>
		<div class="main-content">
			<div class="main-content-inner">
				<div class="breadcrumbs" id="breadcrumbs">
					<ul class="breadcrumb">
						<li><i class="ace-icon fa fa-home home-icon"></i> <a href="${basePath}index.html"><strong>首页</strong></a>
						</li>
						<li class="active"><b>软件管理管理</b></li>
						<li class="active"><b>详细信息</b></li>
					</ul>
				</div>
				<div class="page-content">
					<div class="row">
						<div class="col-xs-12">
							<div class="well well-sm">
								<a href="${basePath}software/index.html" class="btn btn-xs btn-round btn-primary"> 
									<i class="ace-icon fa fa-share bigger-110 icon-only"></i> <b>返回软件管理</b>
								</a>
							</div>
							<div>
								<div class="profile-user-info profile-user-info-striped">
									<div class="profile-info-row">
										<div class="profile-info-name">软件名称</div>
										<div class="profile-info-value">
											<span>${soft.swName}</span>
										</div>
									</div>
									<div class="profile-info-row">
										<div class="profile-info-name">软件版本</div>
										<div class="profile-info-value">
											<span>${soft.swVersion}</span>
										</div>
									</div>
									<div class="profile-info-row">
										<div class="profile-info-name">软件类型</div>
										<div class="profile-info-value">
											<span>
												<c:choose>
													<c:when test="${soft.swType==0}">
														基础环境
													</c:when>
													<c:when test="${soft.swType==1}">
														中间件程序
													</c:when>
													<c:otherwise>
														未知类型
													</c:otherwise>
												</c:choose>
											</span>
										</div>
									</div>
									<div class="profile-info-row">
										<div class="profile-info-name">软件状态</div>
										<div class="profile-info-value">
											<span>
												<c:if test="${soft.swStatus==0}">
													失效
												</c:if>
												<c:if test="${soft.swStatus==1}">
													正常
												</c:if>
											</span>
										</div>
									</div>
									<div class="profile-info-row">
										<div class="profile-info-name">已安装主机</div>
										<div class="profile-info-value">
											<c:if test="${!empty clusterList}">
												<c:forEach var="cluster" items="${clusterList}">
													<span><c:out value="${cluster}"></c:out></span></br>
												</c:forEach>
											</c:if>
										</div>
									</div>
									
									<div class="profile-info-row">
										<div class="profile-info-name">描述信息</div>
										<div class="profile-info-value">
											<span>${soft.swDesc}</span>
										</div>
									</div>
									<div class="profile-info-row">
										<div class="profile-info-name">创建时间</div>
										<div class="profile-info-value">
											<span>${soft.swCreatetime}</span>
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

<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<!DOCTYPE html>
<html lang="en">
<head>
<link rel="shortcut icon" href="${basePath }img/title_cloud.png" />
<title>监控管理</title>
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
<meta charset="utf-8" />
<meta name="description" content="overview &amp; stats" />
<meta name="viewport"
	content="width=device-width, initial-scale=1.0, maximum-scale=1.0" />
<jsp:include page="../js.jsp"></jsp:include>
<link rel="stylesheet" href="${basePath}css/user/apprelease.css" />
<script src="${basePath }ace/assets/js/jquery.validate.min.js"></script>
<script src="${basePath }ace/assets/js/additional-methods.min.js"></script>
<script src="${basePath }ace/assets/js/jquery.maskedinput.min.js"></script>
<script src="${basePath }ace/assets/js/select2.min.js"></script>
<script src="${basePath }js/bootstrap-paginator.min.js"></script>
<script src="${basePath }ace/assets/js/fuelux/fuelux.wizard.min.js"></script>
<script src="${basePath }js/console/monitor_clusterview.js"></script>
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
						<li class="active"><b>监控管理</b></li>
						<li class="active"><b>集群维度</b></li>
					</ul>
				</div>
				<div class="page-header">
					<h1>
						欢迎来到监控管理（集群维度） <small> <br>监控管理为平台提供运维功能，通过不同维度的视角查看服务器和发布的应用所属容器的各项负载信息。
							<br>点击【集群名称】列，进入对应集群的监控视图页面。
						</small>
					</h1>
				</div>
				<div class="page-content">
					<div class="row">
						<div class="col-xs-12">
							<div class="well well-sm">
								<a onclick="window.location.href='/monitor/index.html'"
									class="btn btn-sm btn-success btn-round"> <i
									class="ace-icon fa fa-cubes bigger-125"></i><b>应用维度视图</b>
								</a>&nbsp;&nbsp;&nbsp;<a onclick="window.location.href='/monitor/cluster_view.html'"
									class="btn btn-sm btn-primary btn-round"> <i
									class="ace-icon fa fa-sitemap bigger-125"></i><b>集群维度视图</b>
								</a>
								<div class="col-xs-12 col-sm-4" style="float: right">
									<c:if test="${fn:contains(authButton,'appReleaseList')}">
										<div class="input-group">
											<input id="search_name" type="text"
												class="form-control search-query" placeholder="名称">
											<span class="input-group-btn">
												<button id="search" type="button"
													class="btn btn-primary btn-round btn-sm">
													查找 <i
														class="ace-icon fa fa-search icon-on-right bigger-110"></i>
												</button>
											</span> <span class="input-group-btn"> &nbsp; </span> <span
												class="input-group-btn">
												<button id="detailSearch" type="button"
													class="btn btn-warning btn-round btn-sm"
													onclick="AdvancedSearchContainer()">
													更多 <i
														class="ace-icon fa fa-search icon-on-right bigger-110"></i>
												</button>
											</span>
										</div>
									</c:if>
								</div>
							</div>
							<div>
								<c:if test="${fn:contains(authButton,'appReleaseList')}">
									<table id="view_list"></table>
									<div id="view_page"></div>
								</c:if>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>

	<%--advanced search modal begin --%>
	<div class="modal fade" id="advanSearchContainerModal" tabindex="-1"
		role="dialog" aria-labelledby="containerModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal"
						aria-hidden="true">×</button>
					<h4 class="modal-title" id="containerModalLabel">高级搜索</h4>
				</div>
				<div class="modal-body">
					<div class="left">
						<div class="item">
							<label><b>列名称</b></label>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<label
								style="margin-left: 42px;"><b>搜索参数</b></label>
						</div>
						<form class='form-horizontal' role='form'
							id='advanced_search_container_frm'>
							<div class="item">
								<ul class="con_params" id="con_params"
									style="list-style-type: none; margin: 0px 0px 10px 0px;">
									<li class="con_params" style="margin-top: 10px;">
										<div class="select-con"
											style="height: 33px; width: 100px; float: left;">
											<select class="dropdown-select param-meter" name="con_meter"
												id="con_meter">
												<option value='0'>请选条件</option>
												<option value='1'>应用名称</option>
												<option value='2'>应用版本</option>
												<option value='3'>访问地址</option>
											</select>
										</div> <input class="short-input" type="text" name="con_param_value"
										id="con_param_value" placeholder="输入查询值..." value=""
										style="width: 73%; border: 1px solid #ccc; height: 30px">
										<a href="#" id="con_remove-param" style="display: none;">
											<span class="glyphicon glyphicon-remove delete-param"></span>
									</a>
									</li>
								</ul>
								<a class="btn btn-primary" id="con_add-param" type="button"
									style="color: #fff; width: 87px;">
									<div style="margin: -7px -7px -7px -10px;">
										<span class="glyphicon glyphicon-plus"></span> <span
											class="text">添加条件</span>
									</div>
								</a>
							</div>
						</form>

						<div class="modal-footer">
							<button id="con_advanced_search" type="button"
								class="btn btn-success btn-round">查询</button>
							<button id="con_advanced_cancel" type="button"
								class="btn btn-danger btn-round">取消</button>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<%--advanced search modal end --%>

</body>
</html>

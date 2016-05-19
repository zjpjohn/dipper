<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<!DOCTYPE html>
<html lang="en">
<head>
<link rel="shortcut icon" href="${basePath }img/title_cloud.png" />
<title>应用负载</title>
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
<meta charset="utf-8" />
<meta name="description" content="overview &amp; stats" />
<meta name="viewport"
	content="width=device-width, initial-scale=1.0, maximum-scale=1.0" />
<jsp:include page="../js.jsp"></jsp:include>
<link rel="stylesheet" href="${basePath}css/user/loadbalance.css" />
<script src="${basePath }js/console/lb.js"></script>
<c:set var="authButton" value='${buttonsAuth}'></c:set>

</head>
<body class="no-skin">
	<jsp:include page="../header.jsp"></jsp:include>
	<c:if test="${fn:contains(authButton,'loadbalanceRemove')}">
		<input type="hidden" id="delete_loadbalance">
	</c:if>
	<c:if test="${fn:contains(authButton,'loadbalanceModify')}">
		<input type="hidden" id="update_loadbalance">
	</c:if>

	<div class="main-container" id="main-container">
		<script type="text/javascript">
			try {
				ace.settings.check('main-container', 'fixed')
			} catch (e) {
			}
		</script>
		<jsp:include page="../nav.jsp">
			<jsp:param value="lb_admin" name="page_index" />
		</jsp:include>
		<div class="main-content">
			<div class="main-content-inner">
				<div class="breadcrumbs" id="breadcrumbs">
					<ul class="breadcrumb">
						<li><i class="ace-icon fa fa-home home-icon"></i> <a
							href="${basePath}index.html"><strong>首页</strong></a></li>
						<li class="active"><b>应用负载</b></li>
					</ul>
				</div>
				<div class="page-header">
					<h1>
						欢迎来到应用负载 <small> <br>应用负载是发布平台中所有负载均衡的简称，负载管理提供添加负载，添加应用、移除应用、动态更新、编辑、删除负载等功能。详情查看“使用文档”
						</small>
					</h1>
				</div>
				<div id="mask" class="mask">
					<div id = "spinner-message" class="spinner-message">
						<font></font>
					</div>
					<i id = "spinner" class="spinner ace-icon fa fa-spinner fa-spin white"></i>
		        </div> 
				<div class="page-content">
					<div class="row">
						<div class="col-xs-12">
							<div class="well well-sm">
								<c:if test="${fn:contains(authButton,'loadbalanceCreate')}">
									<button class="btn btn-sm btn-success btn-round"
										data-toggle="modal" onClick="show_addLBModal()">
										<i class="ace-icon fa fa-cogs bigger-125"></i> <b>添加负载</b>
									</button>
								</c:if>
								<div class="btn-group">
									<button data-toggle="dropdown"
										class="btn btn-primary btn-round btn-sm dropdown-toggle">
										<i class="ace-icon fa fa-wrench  bigger-110 icon-only"></i> <b>更多操作</b>
										<i class="ace-icon fa fa-angle-down icon-on-right"></i>
									</button>
									<input id="lbIds" type="hidden" /> <input id="appIds"
										type="hidden" />
									<ul class="dropdown-menu dropdown-primary dropdown-menu-right">
										<c:if test="${fn:contains(authButton,'loadbalanceAddApp')}">
											<li><a class="btn-forbidden" id="addApp"
												onclick="addApp()"><span
													class="glyphicon glyphicon-plus"></span>&nbsp;添加应用</a></li>
										</c:if>
										<c:if test="${fn:contains(authButton,'loadbalanceDelApp')}">
											<li><a class="btn-forbidden" id="removeApp"
												onclick="delApp()"><span
													class="glyphicon glyphicon-plus"></span>&nbsp;移除应用</a></li>
										</c:if>
										<c:if test="${fn:contains(authButton,'loadbalanceReload')}">
											<li><a class="btn-forbidden" id="reload"><span
													class="glyphicon glyphicon-repeat"></span>&nbsp;动态更新</a></li>
										</c:if>
										<c:if test="${fn:contains(authButton,'loadbalanceRemove')}">
											<li><a class="btn-forbidden" id="remove"><span
													class="glyphicon glyphicon-trash"></span>&nbsp;批量删除</a></li>
										</c:if>
									</ul>
								</div>
								<div class="col-xs-12 col-sm-4" style="float: right">
									<c:if test="${fn:contains(authButton,'loadbalanceList')}">
										<div class="input-group">
											<input type="text" id="search_name"
												class="form-control search-query" placeholder="请输入名称进行模糊查询">
											<span class="input-group-btn">
												<button type="button"
													class="btn btn-primary  btn-round btn-sm" id="search">
													查找 <i
														class="ace-icon fa fa-search icon-on-right bigger-110"></i>
												</button>
											</span> <span class="input-group-btn"> &nbsp; </span> <span
												class="input-group-btn">
												<button id="detailSearch" type="button"
													class="btn btn-warning btn-round btn-sm"
													onclick="AdvancedSearchLoadbalance()">
													更多 <i
														class="ace-icon fa fa-search icon-on-right bigger-110"></i>
												</button>
											</span>
										</div>
									</c:if>
								</div>
							</div>
							<c:if test="${fn:contains(authButton,'loadbalanceList')}">
								<div>
									<table id="lb_list"></table>
									<div id="lb_page"></div>
								</div>
							</c:if>
						</div>
					</div>
				</div>
			</div>
		</div>
		<%--Add load balance--%>
		<div class="modal fade" id="addLBModal" tabindex="-1" role="dialog"
			aria-labelledby="myModalLabel" aria-hidden="true"
			data-backdrop="static">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal"
							aria-hidden="true">×</button>
						<h4 class="modal-title" id="myModalLabel">添加负载均衡</h4>
					</div>
					<div class="modal-body">
						<div class='well ' style='margin-top: 1px;'>
							<form class='form-horizontal' role='form' id='add_balance_form'>
								<div class='form-group'>
									<label class='col-sm-3' style="text-align: right;"><b>负载名称：</b></label>
									<div class='col-sm-9' style="margin-left: -20px;">
										<input id="balance_name" name="balance_name" type='text'
											class="form-control" placeholder="输入名称..." />
									</div>
								</div>
								<div class='form-group'>
									<label class='col-sm-3' style="text-align: right;"><b>主服务器：</b></label>
									<div class='col-sm-9' style="margin-left: -20px;">
										<select id='main_host' name="main_host" style="width: 100%">
											<option value="0">选择服务器</option>
										</select>
									</div>
								</div>
								<div class='form-group'>
									<label class='col-sm-3' style="text-align: right;"><b>配置文件位置：</b></label>
									<div class='col-sm-9' style="margin-left: -20px;">
										<input id="main_conf" name="main_conf" type='text'
											class="form-control" placeholder="输入配置文件位置：例如:/home/..." />
									</div>
								</div>
								<div class='form-group'>
									<label class='col-sm-3' style="text-align: right;"><b>备服务器：</b></label>
									<div class='col-sm-9' style="margin-left: -20px;">
										<select id='backup_host' name="backup_host"
											style="width: 100%">
											<option value="0">选择服务器</option>
										</select>
									</div>
								</div>
								<div class='form-group'>
									<label class='col-sm-3' style="text-align: right;"><b>配置文件位置：</b></label>
									<div class='col-sm-9' style="margin-left: -20px;">
										<input id="backup_conf" name="backup_conf" type='text'
											class="form-control" placeholder="输入配置文件位置：例如:/home/..." />
									</div>
								</div>
								<div class='form-group'>
									<label class='col-sm-3' style="text-align: right;"><b>描述信息：</b></label>
									<div class='col-sm-9' style="margin-left: -20px;">
										<textarea id="balance_desc" name="balance_desc"
											class="form-control" rows="3" placeholder="输入描述信息..."></textarea>
									</div>
								</div>
							</form>
						</div>
					</div>
					<div class="modal-footer">
						<button id="cancel" type="button" class="btn btn-round btn-danger">
							取消</button>
						<button id="submit" type="button"
							class="btn btn-round btn-success">提交</button>
					</div>
				</div>
			</div>
		</div>
		<!-- end -->

		<%--Modify load balance--%>
		<div class="modal fade" id="modifyLBModal" tabindex="-1" role="dialog"
			aria-labelledby="myModalLabel" aria-hidden="true">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal"
							aria-hidden="true">×</button>
						<h4 class="modal-title" id="myModalLabel">修改负载均衡</h4>
					</div>
					<div class="modal-body">
						<div class='well ' style='margin-top: 1px;'>
							<form class='form-horizontal' role='form' id='modify_balance_frm'>
								<div class='form-group'>
									<label class='col-sm-3' style="text-align: right;"><b>负载名称：</b></label>
									<div class='col-sm-9' style="margin-left: -20px;">
										<input id='balance_id_edit' type='hidden' /> <input
											id='balance_oldname_edit' type='hidden' /> <input
											id="balance_name_edit" name="balance_name_edit" type='text'
											class="form-control" placeholder="输入名称..." />
									</div>
								</div>
								<div class='form-group'>
									<label class='col-sm-3' style="text-align: right;"><b>主服务器：</b></label>
									<div class='col-sm-9' style="margin-left: -20px;">
										<select id='main_host_edit' name="main_host_edit"
											style="width: 100%">
											<option value="0">选择服务器</option>
										</select>
									</div>
								</div>
								<div class='form-group'>
									<label class='col-sm-3' style="text-align: right;"><b>配置文件位置：</b></label>
									<div class='col-sm-9' style="margin-left: -20px;">
										<input id="main_conf_edit" name="main_conf_edit" type='text'
											class="form-control" placeholder="输入配置文件位置：例如:/home/..." />
									</div>
								</div>
								<div class='form-group'>
									<label class='col-sm-3' style="text-align: right;"><b>备服务器：</b></label>
									<div class='col-sm-9' style="margin-left: -20px;">
										<select id='backup_host_edit' name="backup_host_edit"
											style="width: 100%">
											<option value="0">选择服务器</option>
										</select>
									</div>
								</div>
								<div class='form-group'>
									<label class='col-sm-3' style="text-align: right;"><b>配置文件位置：</b></label>
									<div class='col-sm-9' style="margin-left: -20px;">
										<input id="backup_conf_edit" name="backup_conf_edit"
											type='text' class="form-control"
											placeholder="输入配置文件位置：例如:/home/..." />
									</div>
								</div>
								<div class='form-group'>
									<label class='col-sm-3' style="text-align: right;"><b>描述信息：</b></label>
									<div class='col-sm-9' style="margin-left: -20px;">
										<textarea id="balance_desc_edit" name="balance_desc_edit"
											class="form-control" rows="3" placeholder="输入描述信息..."></textarea>
									</div>
								</div>
							</form>
						</div>
					</div>
					<div class="modal-footer">
						<button id="modify_cancel" type="button"
							class="btn btn-round btn-danger">取消</button>
						<button id="modify" type="button"
							class="btn btn-round btn-success">提交</button>
					</div>
				</div>
			</div>
		</div>
		<%--end --%>

		<!-- Add application tp lb -->
		<div id="add_application" class="hide">
			<div class='well ' style='margin-top: 1px;'>
				<form class='form-horizontal' role='form' id='add_application_frm'>
					<div class='form-group'>
						<label class='col-sm-3' style="text-align: right;"><b>应用名称：</b></label>
						<div class='col-sm-9' style="margin-left: -20px;">
							<select class="dropdown-select" id="app_select" name="app_select"
								style="width: 81%">
								<option value="0">请选择应用</option>
							</select>
						</div>
					</div>
				</form>
			</div>
		</div>

		<%--show nginx config--%>
		<div class="modal fade" id="showLBConfModal" tabindex="-1"
			role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal"
							aria-hidden="true">×</button>
						<h4 class="modal-title" id="myModalLabel">查看负载配置文件</h4>
					</div>
					<div class="modal-body">
						<div class='well ' style='margin-top: 1px;'>
							<form class='form-horizontal' role='form' id='modify_balance_frm'>
								<div class='form-group'>
									<label class='col-sm-3' style="text-align: right;"><b>配置文件：</b></label>
									<div class='col-sm-9' style="margin-left: -20px;" id="confList">
									</div>
								</div>
								<div class='form-group'>
									<label class='col-sm-3' style="text-align: right;"><b>文件内容：</b></label>
									<div class='col-sm-9' style="margin-left: -20px;" id="confList">
										<p id="confContent"></p>
									</div>
								</div>
							</form>
						</div>
					</div>
					<div class="modal-footer">
						<button id="conf_cancel" type="button"
							class="btn btn-round btn-danger">取消</button>
						<button id="conf_submit" type="button"
							class="btn btn-round btn-success">继续</button>
					</div>
				</div>
			</div>
		</div>
		<%--end --%>

		<!-- Remove application tp lb -->
		<div id="remove_application" class="hide">
			<div class='well ' style='margin-top: 1px;'>
				<form class='form-horizontal' role='form'
					id='remove_application_frm'>
					<div class='form-group'>
						<label class='col-sm-3' style="text-align: right;"><b>应用名称：</b></label>
						<div class='col-sm-9' style="margin-left: -20px;">
							<select class="dropdown-select" id="remove_app_select"
								name="remove_app_select" style="width: 81%">
								<option value="0">请选择应用</option>
							</select>
						</div>
					</div>
				</form>
			</div>
		</div>

		<%--show nginx config--%>
		<div class="modal fade" id="removeLBConfModal" tabindex="-1"
			role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal"
							aria-hidden="true">×</button>
						<h4 class="modal-title" id="myModalLabel">查看负载配置文件</h4>
					</div>
					<div class="modal-body">
						<div class='well ' style='margin-top: 1px;'>
							<form class='form-horizontal' role='form' id='modify_balance_frm'>
								<div class='form-group'>
									<label class='col-sm-3' style="text-align: right;"><b>配置文件：</b></label>
									<div class='col-sm-9' style="margin-left: -20px;"
										id="removeconfList"></div>
								</div>
								<div class='form-group'>
									<label class='col-sm-3' style="text-align: right;"><b>文件内容：</b></label>
									<div class='col-sm-9' style="margin-left: -20px;" id="confList">
										<p id="removeconfContent"></p>
									</div>
								</div>
							</form>
						</div>
					</div>
					<div class="modal-footer">
						<button id="remove_conf_cancel" type="button"
							class="btn btn-round btn-danger">取消</button>
						<button id="remove_conf_submit" type="button"
							class="btn btn-round btn-success">继续</button>
					</div>
				</div>
			</div>
		</div>
		<%--end --%>


		<%--advanced search modal begin --%>
		<div class="modal fade" id="advancedSearchLoadBalanceModal"
			tabindex="-1" role="dialog" aria-labelledby="myModalLabel"
			aria-hidden="true">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal"
							aria-hidden="true">×</button>
						<h4 class="modal-title" id="myModalLabel">高级搜索</h4>
					</div>
					<div class="modal-body">
						<div class="left">
							<div class="item">
								<label><b>列名称</b></label>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<label
									style="margin-left: 42px;"><b>搜索参数</b></label>
							</div>
							<form class='form-horizontal' role='form'
								id='advanced_search_frm'>
								<div class="item">
									<ul class="params" id="params"
										style="list-style-type: none; margin: 0px 0px 10px 0px;">
										<li class="param" style="margin-top: 10px;">
											<div class="select-con"
												style="height: 33px; width: 100px; float: left;">
												<select class="dropdown-select param-meter" name="meter"
													id="meter">
													<option value='0'>请选列名</option>
													<option value='1'>均衡名称</option>
													<option value='2'>主配置路径</option>
													<option value='3'>描述信息</option>
												</select>
											</div> <input class="short-input" type="text" name="param_value"
											id="param_value" placeholder="输入参数值..." value=""
											style="width: 73%; border: 1px solid #ccc; height: 30px">
											<a href="#" id="remove-param" style="display: none;"><span
												class="glyphicon glyphicon-remove delete-param"></span> </a>
										</li>
									</ul>
									<a class="btn btn-primary" id="add-param" type="button"
										style="color: #fff; width: 87px;">
										<div style="margin: -7px -7px -7px -10px;">
											<span class="glyphicon glyphicon-plus"></span> <span
												class="text">添加条件</span>
										</div>
									</a>
								</div>
							</form>
						</div>
					</div>
					<div class="modal-footer">
						<button id="advanced_cancel" type="button"
							class="btn btn-danger btn-round">取消</button>
						<button id="advanced_search" type="button"
							class="btn btn-success btn-round">查询</button>
					</div>
				</div>
			</div>
		</div>
		<%--advanced search modal end --%>

	</div>
</body>
</html>

<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<!DOCTYPE html>
<html lang="en">
<head>
<link rel="shortcut icon" href="${basePath }img/title_cloud.png" />
<title>监控代理</title>
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
<meta charset="utf-8" />
<meta name="description" content="overview &amp; stats" />
<meta name="viewport"
	content="width=device-width, initial-scale=1.0, maximum-scale=1.0" />
<jsp:include page="../js.jsp"></jsp:include>
<script src="${basePath }ace/assets/js/jquery.validate.min.js"></script>
<script src="${basePath }js/bootstrap-paginator.min.js"></script>
<script src="${basePath }ace/assets/js/fuelux/fuelux.wizard.min.js"></script>
<script src="${basePath }js/console/monitor_proxy.js"></script>
<c:set var="authButton" value='${buttonsAuth}'></c:set>
</head>
<body class="no-skin">
	<c:if test="${fn:contains(authButton,'mntrproxyDelete')}">
		<input type="hidden" id="delete_app">
	</c:if>
	<c:if test="${fn:contains(authButton,'mntrproxyUpdate')}">
		<input type="hidden" id="update_app">
	</c:if>
	<jsp:include page="../header.jsp"></jsp:include>
	<div class="main-container" id="main-container">
		<script type="text/javascript">
			try {
				ace.settings.check('main-container', 'fixed')
			} catch (e) {
			}
		</script>
		<jsp:include page="../nav.jsp">
			<jsp:param value="mntrpxy_admin" name="page_index" />
		</jsp:include>
		<div class="main-content">
			<div class="main-content-inner">
				<div class="breadcrumbs" id="breadcrumbs">
					<ul class="breadcrumb">
						<li><i class="ace-icon fa fa-home home-icon"></i> <a
							href="${basePath}index.html"><strong>首页</strong></a></li>
						<li class="active"><i class="ace-icon fa fa-cubes"></i> <b>监控代理</b></li>
					</ul>
				</div>
				<div class="page-header">
					<h1>
						欢迎来到监控代理 <small> <br>监控代理是发布平台中所有管理监控代理的简称，监控代理提供添加代理，修改代理，删除代理等功能。详情查看“使用文档”
						</small>
					</h1>
				</div>
				<div class="page-content">
					<div class="row">
						<div class="col-xs-12">
							<div class="well well-sm">
								<!-- onclick="insertApplication()" -->
								<c:if test="${fn:contains(authButton,'mntrproxyCreate')}">
									<button class="btn btn-sm btn-success btn-round"
										data-toggle="modal" data-target="#createMntrpxyModal">
										<i class="ace-icon fa fa-pencil-square-o bigger-125"></i>&nbsp;<b>添加监控代理</b>
									</button>
								</c:if>
								<button class="btn btn-sm btn-danger btn-round"
									onclick="window.location.href='/mntrproxy/index.html'">
									<i class="ace-icon fa fa-list-alt bigger-125"></i><b>显示全部代理</b>
								</button>
								<c:if test="${fn:contains(authButton,'mntrproxyDelete')}">
									<button class="btn btn-sm btn-inverse btn-round"
										id="batch_rmmp_btn">
										<i class="ace-icon fa fa-trash-o  bigger-125"></i> &nbsp;<b>批量删除</b>
									</button>
								</c:if>
								<div class="col-xs-12 col-sm-4" style="float: right">
									<c:if test="${fn:contains(authButton,'mntrproxyList')}">
										<div class="input-group">
											<input type="text" id="searchAppName"
												class="form-control search-query" placeholder="请输入名称进行模糊查询">
											<span class="input-group-btn">
												<button type="button"
													class="btn btn-primary  btn-round btn-sm"
													onclick="SearchMonitorProxys()">
													查找 <i
														class="ace-icon fa fa-search icon-on-right bigger-110"></i>
												</button>
											</span> <span class="input-group-btn"> &nbsp; </span> <span
												class="input-group-btn">
												<button type="button"
													class="btn btn-warning btn-round btn-sm"
													onclick="AdvancedSearchApp()">
													更多 <i
														class="ace-icon fa fa-search icon-on-right bigger-110"></i>
												</button>
											</span>
										</div>
									</c:if>
								</div>
							</div>
							<c:if test="${fn:contains(authButton,'mntrproxyList')}">
								<div>
									<table id="mntrpxy_list"></table>
									<div id="mntrpxy_page"></div>
								</div>
							</c:if>
						</div>
					</div>
				</div>
			</div>
		</div>
		<%--create app modal begin --%>
		<div class="modal fade" id="createMntrpxyModal" tabindex="-1"
			role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal"
							aria-hidden="true">×</button>
						<h4 class="modal-title" id="myModalLabel">添加监控代理</h4>
					</div>
					<div class="modal-body">
						<div class='well ' style='margin-top: 1px;'>
							<form class='form-horizontal' role='form' id='add_mntrpxy_frm'>
								<div class='form-group'>
									<label class='col-sm-3'><b>代理名称：</b></label>
									<div class='col-sm-9'>
										<input class="form-control" id="mntrpxy_name"
											name="mntrpxy_name" type='text' placeholder="请输入代理名称" />
									</div>
								</div>
								<div class='form-group'>
									<label class='col-sm-3'><b>代理地址：</b></label>
									<div class='col-sm-9'>
										<input class="form-control" id="mntrpxy_ip" name="mntrpxy_ip"
											type='text' placeholder="请输入代理IP地址，例如：192.168.150.123" />
									</div>
								</div>
								<div class='form-group'>
									<label class='col-sm-3'><b>监听端口：</b></label>
									<div class='col-sm-9'>
										<input class="form-control" id="mntrpxy_port"
											name="mntrpxy_port" type='text' placeholder="请输入端口号，例如:3360" />
									</div>
								</div>
								<div class='form-group'>
									<label class='col-sm-3'><b>代理描述：</b></label>
									<div class='col-sm-9'>
										<textarea class="form-control" id="mntrpxy_desc"
											name="mntrpxy_desc" rows="3" placeholder="请输入此监控代理的描述信息。"></textarea>
									</div>
								</div>
								<div class='form-group'>
									<label class='col-sm-3'><b>备注信息：</b></label>
									<div class='col-sm-9'>
										<textarea class="form-control" id="mntrpxy_comment"
											name="mntrpxy_comment" rows="3" placeholder="请输入此监控代理的备注信息。"></textarea>
									</div>
								</div>
							</form>
						</div>
					</div>
					<div class="modal-footer">
						<button id="cancel" type="button" class="btn btn-danger btn-round">
							取消</button>
						<button id="submit" type="button"
							class="btn btn-success btn-round">提交</button>
					</div>
				</div>
			</div>
		</div>
		<%--create app modal end --%>

		<%--modify app modal begin --%>
		<div class="modal fade" id="modifyMntrpxyModal" tabindex="-1"
			role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal"
							aria-hidden="true">×</button>
						<h4 class="modal-title" id="myModalLabel">修改监控代理</h4>
					</div>
					<div class="modal-body">
						<div class='well ' style='margin-top: 1px;'>
							<form class='form-horizontal' role='form' id='modify_mntrpxy_frm'>
								<div class='form-group'>
									<input class="form-control" id="mntrpxy_id_edit"
										name="application_id_edit" type='hidden' /> <label
										class='col-sm-3'><b>代理名称：</b></label>
									<div class='col-sm-9'>
										<input class="form-control" id="mntrpxy_name_edit"
											name="mntrpxy_name_edit" type='text' readonly="readonly" />
									</div>
								</div>
								<div class='form-group'>
									<label class='col-sm-3'><b>代理地址：</b></label>
									<div class='col-sm-9'>
										<input id="old_mntrpxy_ip_edit" type="hidden"/>
										<input class="form-control" id="mntrpxy_ip_edit"
											name="mntrpxy_ip_edit" type='text' />
									</div>
								</div>
								<div class='form-group'>
									<label class='col-sm-3'><b>监听端口：</b></label>
									<div class='col-sm-9'>
										<input id="old_mntrpxy_port_edit" type="hidden"/>
										<input class="form-control" id="mntrpxy_port_edit"
											name="mntrpxy_port_edit" type='text' />
									</div>
								</div>
								<div class='form-group'>
									<label class='col-sm-3'><b>代理描述：</b></label>
									<div class='col-sm-9'>
										<textarea class="form-control" id="mntrpxy_desc_edit"
											name="mntrpxy_desc_edit" rows="3"></textarea>
									</div>
								</div>
								<div class='form-group'>
									<label class='col-sm-3'><b>备注信息：</b></label>
									<div class='col-sm-9'>
										<textarea class="form-control" id="mntrpxy_comment_edit"
											name="mntrpxy_comment_edit" rows="3"></textarea>
									</div>
								</div>
							</form>
						</div>
					</div>
					<div class="modal-footer">
						<button id="modify_cancel" type="button"
							class="btn btn-danger btn-round">取消</button>
						<button id="modify" type="button"
							class="btn btn-success btn-round">提交</button>
					</div>
				</div>
			</div>
		</div>
		<%--modify app modal end --%>

		<%--advanced search modal begin --%>
		<div class="modal fade" id="advancedSearchModal" tabindex="-1"
			role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
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
													<option value='1'>代理名称</option>
													<option value='2'>主机地址</option>
													<option value='3'>监听端口</option>
													<option value='4'>代理描述</option>
													<option value='5'>备注信息</option>
												</select>
											</div> <input class="short-input" type="text" name="param_value"
											id="param_value" placeholder="输入参数值..." value=""
											style="width: 73%; border: 1px solid #ccc; height: 30px">
											<a href="#" id="remove-param" style="display: none;"><span
												class="glyphicon glyphicon-remove delete-param"></span></a>
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

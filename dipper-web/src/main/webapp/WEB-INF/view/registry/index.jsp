<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<!DOCTYPE html>
<html lang="en">
<head>
<link rel="shortcut icon" href="${basePath }img/title_cloud.png" />
<title>仓库管理</title>
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
<meta charset="utf-8" />
<meta name="description" content="overview &amp; stats" />
<meta name="viewport"
	content="width=device-width, initial-scale=1.0, maximum-scale=1.0" />
<jsp:include page="../js.jsp"></jsp:include>
<script src="${basePath }ace/assets/js/jquery.validate.min.js"></script>
<script src="${basePath }js/bootstrap-paginator.min.js"></script>
<script src="${basePath }ace/assets/js/fuelux/fuelux.wizard.min.js"></script>
<script src="${basePath }js/console/registry.js"></script>
<c:set var="authButton" value='${buttonsAuth}'></c:set>
</head>
<body class="no-skin">
	<jsp:include page="../header.jsp"></jsp:include>
	<c:if test="${fn:contains(authButton,'registryDeletes')}">
		<input type="hidden" id="delete_registry">
	</c:if>
	<c:if test="${fn:contains(authButton,'registryUpdate')}">
		<input type="hidden" id="update_registry">
	</c:if>
	<c:if test="${fn:contains(authButton,'registrySyncImage')}">
		<input type="hidden" id="sync_registry">
	</c:if>
	<c:if test="${fn:contains(authButton,'registryImages')}">
		<input type="hidden" id="show_Images">
	</c:if>

	<div class="main-container" id="main-container">
		<script type="text/javascript">
			try {
				ace.settings.check('main-container', 'fixed')
			} catch (e) {
			}
		</script>
		<jsp:include page="../nav.jsp">
			<jsp:param value="registry_admin" name="page_index" />
		</jsp:include>
		<div class="main-content">
			<div class="main-content-inner">
				<div class="breadcrumbs" id="breadcrumbs">
					<ul class="breadcrumb">
						<li><i class="ace-icon fa fa-home home-icon"></i><a
							href="${basePath}index.html"><strong>首页</strong></a></li>
						<li class="active"><i class="ace-icon fa fa-university"></i>
							<b>仓库管理</b></li>
					</ul>
				</div>
				<div class="page-header">
					<h1>
						欢迎来到仓库管理 <small> <br>仓库是发布平台中中所有镜像存放的地方，仓库管理提供仓库的创建、编辑、删除、查询仓库中的镜像信息和同步镜像等功能。详情查看“使用文档”
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
								<c:if test="${fn:contains(authButton,'registryCreate')}">
									<button class="btn btn-sm btn-success btn-round"
										data-toggle="modal" data-target="#createRegistryModal">
										<i class="ace-icon fa fa-pencil-square-o bigger-125"></i> <b>创建仓库</b>
									</button>
								</c:if>
								<div class="btn-group">
									<button data-toggle="dropdown"
										class="btn btn-sm btn-round btn-primary dropdown-toggle">
										<i class="ace-icon fa fa-wrench  icon-only"></i> <b>更多操作</b> <i
											class="ace-icon fa fa-angle-down icon-on-right"></i>
									</button>
									<ul class="dropdown-menu dropdown-primary dropdown-menu-right">
										<c:if test="${fn:contains(authButton,'registrySyn')}">
											<li><a class="glyphicon glyphicon-transfer" href="#"
												onclick="batchSyncRegistryImages()">同步镜像</a></li>
										</c:if>
										<c:if test="${fn:contains(authButton,'registryDeletes')}">
											<li><a class="glyphicon glyphicon-trash" href="#"
												onclick="batchRemoveRegistrys()">批量删除</a></li>
										</c:if>
									</ul>
								</div>
								<div class="col-xs-12 col-sm-4" style="float: right">
									<c:if test="${fn:contains(authButton,'registryList')}">
										<div class="input-group">
											<input type="text" id="searchRegiName"
												class="form-control search-query" placeholder="请输入仓库名称模糊查询">
											<span class="input-group-btn">
												<button type="button"
													class="btn btn-primary btn-round btn-sm"
													onclick="SearchRegistrys()">
													查找 <i
														class="ace-icon fa fa-search icon-on-right bigger-110"></i>
												</button>
											</span> <span class="input-group-btn"> &nbsp; </span> <span
												class="input-group-btn">
												<button type="button"
													class="btn btn-warning btn-round btn-sm"
													onclick="AdvancedSearchRegi()">
													更多 <i
														class="ace-icon fa fa-search icon-on-right bigger-110"></i>
												</button>
											</span>
										</div>
									</c:if>
								</div>
							</div>
							<c:if test="${fn:contains(authButton,'registryList')}">
								<div>
									<table id="registry_list"></table>
									<div id="registry_page"></div>
								</div>
							</c:if>
						</div>
					</div>
				</div>
			</div>
		</div>

		<%--create registry modal begin --%>
		<div class="modal fade" id="createRegistryModal" tabindex="-1"
			role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal"
							aria-hidden="true">×</button>
						<h4 class="modal-title" id="myModalLabel">创建仓库</h4>
					</div>
					<div class="modal-body">
						<form class='form-horizontal' role='form' id='create_registry_frm'>
							<div class='form-group'>
								<label class='col-sm-3'><b>仓库名称：</b></label>
								<div class='col-sm-9'>
									<input class="form-control" id="registry_name"
										name="registry_name" type='text' placeholder="请输入仓库名称(限64个字符)" />
								</div>
							</div>
							<div class='form-group'>
								<label class="col-sm-3"><b>所在主机：</b></label>
								<div class="col-sm-9">
									<select id="registry_host" name="registry_host"
										class="form-control">
										<option value="0">请输入仓库所在的主机地址</option>
									</select>
								</div>
							</div>
							<div class='form-group'>
								<label class='col-sm-3'><b>仓库端口号：</b></label>
								<div class='col-sm-9'>
									<input class="form-control" id="registry_port"
										name="registry_port" type='text' placeholder="请输入端口号，例如:8080" />
								</div>
							</div>
							<div class='form-group'>
								<label class='col-sm-3'><b>仓库描述：</b></label>
								<div class='col-sm-9'>
									<textarea class="form-control" id="registry_desc"
										name="registry_desc" rows="3" placeholder="请输入仓库的描述信息。"></textarea>
								</div>
							</div>
						</form>

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

		<%--modify registry modal begin --%>
		<div class="modal fade" id="modifyRegistryModal" tabindex="-1"
			role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal"
							aria-hidden="true">×</button>
						<h4 class="modal-title" id="myModalLabel">修改仓库</h4>
					</div>
					<div class="modal-body">
						<form class='form-horizontal' role='form' id='modify_registry_frm'>
							<div class='form-group'>
								<input class="form-control" id="registry_id_edit"
									name="registry_id_edit" type='hidden' /> <label
									class='col-sm-3'><b>仓库名称：</b></label>
								<div class='col-sm-9'>
									<input class="form-control" id="registry_name_edit"
										name="registry_name_edit" type='text'
										placeholder="请输入仓库名称(限64个字符)" />
								</div>
							</div>
							<div class='form-group'>
								<label class="col-sm-3"><b>所在主机：</b></label>
								<div class="col-sm-9">
									<input class="form-control" id="registry_host_edit"
										name="registry_host_edit" type='text' readonly="readonly" />
								</div>
							</div>
							<div class='form-group'>
								<label class='col-sm-3'><b>仓库端口号：</b></label>
								<div class='col-sm-9'>
									<input class="form-control" id="registry_port_edit"
										name="registry_port_edit" type='text' readonly="readonly" />
								</div>
							</div>
							<div class='form-group'>
								<label class='col-sm-3'><b>仓库描述：</b></label>
								<div class='col-sm-9'>
									<textarea class="form-control" id="registry_desc_edit"
										name="registry_desc_edit" rows="3" placeholder="请输入仓库的描述信息。"></textarea>
								</div>
							</div>
						</form>
					</div>
					<div class="modal-footer">
						<button id="modify_cancel" type="button"
							class="btn btn-danger btn-round">取消</button>
						<button id="modify_submit" type="button"
							class="btn btn-success btn-round">提交</button>
					</div>
				</div>
			</div>
		</div>
		<%--modify app modal end --%>

		<%--advanced search modal begin --%>
		<div class="modal fade" id="advancedSearchRegiModal" tabindex="-1"
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
													<option value='1'>仓库名称</option>
													<option value='2'>仓库端口</option>
													<option value='3'>描述信息</option>
													<option value='4'>主机地址</option>
													<option value='5'>用户名称</option>
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

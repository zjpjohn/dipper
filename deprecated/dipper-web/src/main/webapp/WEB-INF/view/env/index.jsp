<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<!DOCTYPE html>
<html lang="en">
<head>
<link rel="shortcut icon" href="${basePath }img/title_cloud.png" />
<title>资源管理</title>
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
<meta charset="utf-8" />
<meta name="description" content="overview &amp; stats" />
<meta name="viewport"
	content="width=device-width, initial-scale=1.0, maximum-scale=1.0" />
<jsp:include page="../js.jsp"></jsp:include>
<script src="${basePath }ace/assets/js/jquery.validate.min.js"></script>
<script src="${basePath }js/bootstrap-paginator.min.js"></script>
<script src="${basePath }ace/assets/js/fuelux/fuelux.wizard.min.js"></script>
<script src="${basePath }ace/assets/js/ace-extra.min.js"></script>
<!-- 添加拖动条必备内容 -->
<script src="${basePath }ace/assets/js/jquery-ui.min.js"></script>
<link rel="stylesheet"
	href="${basePath }ace/assets/css/jquery-ui.min.css" />
<!-- 添加拖动条必备内容 -->
<script src="${basePath }js/console/env.js"></script>
<c:set var="authButton" value='${buttonsAuth}'></c:set>
</head>
<body class="no-skin">
	<c:if test="${fn:contains(authButton,'resourceDelete')}">
		<input type="hidden" id="delete_app">
	</c:if>
	<c:if test="${fn:contains(authButton,'resourceUpdate')}">
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
			<jsp:param value="env_admin" name="page_index" />
		</jsp:include>
		<div class="main-content">
			<div class="main-content-inner">
				<div class="breadcrumbs" id="breadcrumbs">
					<ul class="breadcrumb">
						<li><i class="ace-icon fa fa-home home-icon"></i> <a
							href="${basePath}index.html"><strong>首页</strong></a></li>
						<li class="active"><i class="ace-icon fa fa-flag-checkered"></i>
							<b>环境管理</b></li>
					</ul>
				</div>
				<div class="page-header">
					<h1>
						欢迎来到环境管理 <small> <br>环境管理向用户提供创建、编辑应用运行所在环境的功能。详情查看“使用文档”
						</small>
					</h1>
				</div>
				<div class="page-content">
					<div class="row">
						<div class="col-xs-12">
							<div class="well well-sm">
								<!-- onclick="insertApplication()" -->
								<c:if test="${fn:contains(authButton,'resourceCreate')}">
									<button class="btn btn-sm btn-success btn-round"
										data-toggle="modal" data-target="#createEnvModal">
										<i class="fa fa-sliders"></i>&nbsp;<b>添加环境</b>
									</button>
								</c:if>
								<button class="btn btn-sm btn-danger btn-round"
									onclick="window.location.href='/env/index.html'">
									<i class="ace-icon fa fa-list-alt bigger-125"></i><b>全部环境</b>
								</button>
								<div class="col-xs-12 col-sm-4" style="float: right">
									<c:if test="${fn:contains(authButton,'resourceList')}">
										<div class="input-group">
											<input type="text" id="searchEnvName"
												class="form-control search-query" placeholder="请输入名称进行模糊查询">
											<span class="input-group-btn">
												<button type="button"
													class="btn btn-primary  btn-round btn-sm"
													onclick="SearchEnvs()">
													查找 <i
														class="ace-icon fa fa-search icon-on-right bigger-110"></i>
												</button>
											</span> <span class="input-group-btn"> &nbsp; </span> <span
												class="input-group-btn">
												<button type="button"
													class="btn btn-warning btn-round btn-sm"
													onclick="AdvancedSearchEnvs()">
													更多 <i
														class="ace-icon fa fa-search icon-on-right bigger-110"></i>
												</button>
											</span>
										</div>
									</c:if>
								</div>
							</div>
							<c:if test="${fn:contains(authButton,'resourceList')}">
								<div>
									<table id="env_list"></table>
									<div id="env_page"></div>
								</div>
							</c:if>
						</div>
					</div>
				</div>
			</div>
		</div>

		<%--create enviroment modal begin --%>
		<div class="modal fade" id="createEnvModal" tabindex="-1"
			role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<button id="createClose" type="button" class="close"
							data-dismiss="modal" aria-hidden="true">×</button>
						<h4 class="modal-title" id="myModalLabel">创建环境</h4>
					</div>
					<div class="modal-body">
						<div class='well ' style='margin-top: 1px;'>
							<form class='form-horizontal' role='form' id='add_env_frm'>
								<div class='form-group'>
									<label class='col-sm-3'><b>环境名称：</b></label>
									<div class='col-sm-9'>
										<input class="form-control" id="env_name" name="env_name"
											type='text' placeholder="请输入环境名称" />
									</div>
								</div>
								<div class='form-group'>
									<label class='col-sm-3'><b>配置中心：</b></label>
									<div class='col-sm-9'>
										<input class="form-control" id="config_center" name="config_center"
											type='text' placeholder="请输入配置中心" />
									</div>
								</div>
								<div class='form-group env_path' >
									<label class='col-sm-3'><b>环境变量：</b></label>
									<div class='col-sm-8' style="margin-left:-15px">
										<input class="form-control" name="env_path"
											type='text' placeholder="请输入环境变量" />
									</div>
									<a href="#" id="add_param" style="line-height:30px;"><span class="glyphicon glyphicon-plus delete-param" ></span></a>	
								</div>
								<div class='form-group'>
									<label class='col-sm-3'><b>环境描述：</b></label>
									<div class='col-sm-9'>
										<textarea class="form-control" id="env_desc" name="env_desc"
											rows="3" placeholder="请输入环境的描述信息。"></textarea>
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
		<%--create enviroment modal end --%>

		<%--modify enviroment modal begin --%>
		<div class="modal fade" id="modifyEnvModal" tabindex="-1"
			role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal"
							aria-hidden="true">×</button>
						<h4 class="modal-title" id="myModalLabel">修改环境</h4>
					</div>
					<div class="modal-body">
						<div class='well ' style='margin-top: 1px;'>
							<form class='form-horizontal' role='form' id='modify_env_frm'>
								<div class='form-group'>
									<input class="form-control" id="env_id_edit" name="env_id_edit"
										type='hidden' /> <label class='col-sm-3'><b>环境名称：</b></label>
									<div class='col-sm-9'>
										<input class="form-control" id="env_name_edit"
											name="env_name_edit" type='text' readonly="readonly" />
									</div>
								</div>
								<div class='form-group'>
									<label class='col-sm-3'><b>配置中心：</b></label>
									<div class='col-sm-9'>
										<input class="form-control" id="config_center_edit" name="config_center_edit"
											type='text'/>
									</div>
								</div>
								<div class='form-group env_path'>
									<label class='col-sm-3'><b>环境变量：</b></label>
									<div class='col-sm-8' style="margin-left:-15px">
										<input class="form-control" name="env_path_edit"
											type='text' placeholder="请输入环境变量" />
									</div>
									<a href="#" id="add_param_edit" style="line-height:30px;"><span class="glyphicon glyphicon-plus delete-param" ></span></a>	
								</div>
								<div class='form-group'>
									<label class='col-sm-3'><b>环境描述：</b></label>
									<div class='col-sm-9'>
										<textarea class="form-control" id="env_desc_edit"
											name="env_desc_edit" rows="3"></textarea>
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
		<%--modify enviroment modal end --%>

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
													<option value='1'>环境名称</option>
													<option value='2'>环境描述</option>
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

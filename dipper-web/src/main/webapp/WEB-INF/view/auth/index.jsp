<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<!DOCTYPE html>
<html lang="en">
<head>
<link rel="shortcut icon" href="${basePath }img/title_cloud.png" />
<title>权限管理</title>
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
<meta charset="utf-8" />
<meta name="description" content="overview &amp; stats" />
<meta name="viewport"
	content="width=device-width, initial-scale=1.0, maximum-scale=1.0" />
<jsp:include page="../js.jsp"></jsp:include>
<link rel="stylesheet" href="${basePath}css/user/host.css" />
<script src="${basePath }js/console/authority.js"></script>
<c:set var="authButton" value='${buttonsAuth}'></c:set>

</head>
<body class="no-skin">
	<c:if test="${fn:contains(authButton,'authDelete')}">
		<input type="hidden" id="delete_auth">
	</c:if>
	<c:if test="${fn:contains(authButton,'authUpdate')}">
		<input type="hidden" id="update_auth">
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
			<jsp:param value="manage_right" name="page_index" />
			<jsp:param value="manage_power" name="parent_index" />
		</jsp:include>
		<div class="main-content">
			<div class="main-content-inner">
				<div class="breadcrumbs" id="breadcrumbs">
					<ul class="breadcrumb">
						<li><i class="ace-icon fa fa-home home-icon"></i> <a
							href="${basePath}index.html"><strong>首页</strong></a></li>
						<li class="active"><b>权限管理</b></li>
					</ul>
				</div>
				<div class="page-header">
				   <h1>欢迎来到权限管理 
				   	<small>
				   		<br>权限是发布平台中所有权限的简称，权限管理提供修改权限的功能。详情查看“使用文档”
				   	</small>
				   </h1>
				</div>
				<div class="page-content">
					<div class="row">
						<div class="col-xs-12">
							<div class="well well-sm" style="height:58px;padding:12px;">
								<!-- 注释该代码，改为由js显示添加窗口	<button class="btn btn-sm btn-primary" data-toggle="modal" data-target="#createAuthModal"> -->
								<%-- <c:if test="${fn:contains(authButton,'authCreate')}">
									<button class="btn btn-sm btn-primary btn-round" id="addAuth">
										<i class="ace-icon fa fa-pencil-square-o bigger-125"></i> <b>添加权限</b>
									</button>
								</c:if> --%>
								<div class="col-xs-12 col-sm-4" style="float: right">
									<c:if test="${fn:contains(authButton,'authList')}">
										<div class="input-group">
											<input type="text" id="search_auth"
												class="form-control search-query" placeholder="请输入名称进行模糊查询">
											<span class="input-group-btn">
												<button type="button"
													class="btn btn-round btn-primary btn-sm"
													onclick="searchAuthoritys()">
													查找 <i
														class="ace-icon fa fa-search icon-on-right bigger-110"></i>
												</button>
											</span> <span class="input-group-btn"> &nbsp; </span> <span
												class="input-group-btn">
												<button id="detailSearch" type="button"
													class="btn btn-warning btn-round btn-sm" onclick="AdvancedSearchAuths()">
													更多 <i
														class="ace-icon fa fa-search icon-on-right bigger-110"></i>
												</button>
											</span>
										</div>
									</c:if>
								</div>
							</div>
							<c:if test="${fn:contains(authButton,'authList')}">
								<div>
									<table id="authority_list"></table>
									<div id="authority_page"></div>
								</div>
							</c:if>
						</div>
					</div>
				</div>
			</div>
		</div>
		<%-- Create host info --%>
		<div class="modal fade" id="createAuthModal" tabindex="-1"
			role="dialog" aria-labelledby="myModalLabel" aria-hidden="true"
			data-backdrop="static">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal"
							aria-hidden="true">×</button>
						<h4 class="modal-title" id="myModalLabel">添加权限</h4>
					</div>
					<div class="modal-body">
						<div class="well" style="margin-top: 1px;">
							<form class='form-horizontal' role='form' id='create_auth_form'>
								<div class='form-group'>
									<label class='col-sm-3'><b>权限名称：</b></label>
									<div class='col-sm-9'>
										<input id="action_name" name='action_name' type='text'
											class="form-control" placeholder="输入权限名称..." />
									</div>
								</div>
								<div class='form-group'>
									<label class='col-sm-3'><b>权限描述：</b></label>
									<div class='col-sm-9'>
										<input id="action_desc" name='action_desc' type='text'
											class="form-control" placeholder="输入权限描述..." />
									</div>
								</div>
								<div class='form-group'>
									<label class='col-sm-3'><b>权限URL：</b></label>
									<div class='col-sm-9'>
										<input id="action_relative_url" name='action_relative_url'
											type='text' class="form-control" placeholder="输入权限URL..." />
									</div>
								</div>
								<div class='form-group'>
									<label class='col-sm-3'><b>权限类型：</b></label>
									<div class='col-sm-9'>
										<input id="action_type" name='action_type' type='text'
											class="form-control" placeholder="输入权限类型..." />
									</div>
								</div>
								<div class='form-group'>
									<label class='col-sm-3'><b>权限标记：</b></label>
									<div class='col-sm-9'>
										<input id="action_remarks" name='action_remarks' type='text'
											class="form-control" placeholder="输入权限标记..." />
									</div>
								</div>
								<div class='form-group'>
									<label class='col-sm-3'><b>父节点：</b></label>
									<div id="authTree" class="ztree col-sm-9"></div>
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

		<%-- Modify host info --%>
		<div class="modal fade" id="modifyAuthModal" tabindex="-1"
			role="dialog" aria-labelledby="myModalLabel" aria-hidden="true"
			data-backdrop="static">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal"
							aria-hidden="true">×</button>
						<h4 class="modal-title" id="myModalLabel">修改权限</h4>
					</div>
					<div class="modal-body">
						<div class="well" style="margin-top: 1px;">
							<form class='form-horizontal' role='form' id='modify_auth_form'>
								<div class='form-group'>
									<label class='col-sm-3'><b>权限ID：</b></label>
									<div class='col-sm-9'>
										<input id="action_id_edit" name='action_id_edit' type='text'
											class="form-control" readOnly="true" />
									</div>
								</div>
								<div class='form-group'>
									<label class='col-sm-3'><b>权限名称：</b></label>
									<div class='col-sm-9'>
										<input id="action_name_edit" name='action_name_edit'
											type='text' class="form-control" />
									</div>
								</div>
								<div class='form-group'>
									<label class='col-sm-3'><b>权限描述：</b></label>
									<div class='col-sm-9'>
										<input id="action_desc_edit" name='action_desc_edit'
											type='text' class="form-control" />
									</div>
								</div>
								<div class='form-group'>
									<label class='col-sm-3'><b>权限URL：</b></label>
									<div class='col-sm-9'>
										<input id="action_relative_url_edit"
											name='action_relative_url_edit' type='text'
											class="form-control" readOnly="true"/>
									</div>
								</div>
								<div class='form-group'>
									<label class='col-sm-3'><b>权限类型：</b></label>
									<div class='col-sm-9'>
										<input id="action_type_edit" name='action_type_edit'
											type='text' class="form-control" readOnly="true"/>
									</div>
								</div>
								<div class='form-group'>
									<label class='col-sm-3'><b>权限标记：</b></label>
									<div class='col-sm-9'>
										<input id="action_remarks_edit" name='action_remarks_edit'
											type='text' class="form-control" readOnly="true"/>
									</div>
								</div>
								<div class='form-group'>
									<label class='col-sm-3'><b>权限依赖：</b></label>
								    <div class='col-sm-9'>
										<input id="action_parent_id_edit" name='action_parent_id_edit' type='text' class="form-control" readOnly="true"/>
									</div> 
									<!-- <div id="authTreeEdit" class="ztree col-sm-9"></div> -->
								</div>
							</form>
						</div>
					</div>
					<div class="modal-footer">
						<button id="modify_submit" type="button"
							class="btn btn-round btn-success">提交</button>
						<button id="modify_cancel" type="button"
							class="btn btn-round btn-danger">取消</button>
					</div>
				</div>
			</div>
		</div>
<%--advanced search modal begin --%>
		<div class="modal fade" id="advancedSearchAuthModal" tabindex="-1"
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
													<option value='1'>权限名</option>
													<option value='2'>权限描述</option>
													<option value='3'>权限URL</option>
													<option value='4'>权限类型</option>
													<option value='5'>权限标记</option>
												</select>
											</div> <input class="short-input" type="text"
											name="search_auth_value" id="search_auth_value"
											placeholder="输入参数值..." value=""
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
						<button id="advanced_auth_search" type="button"
							class="btn btn-success btn-round">查询</button>
						<button id="advanced_auth_cancel" type="button"
							class="btn btn-danger btn-round">取消</button>
					</div>
				</div>
			</div>
		</div>
		<%--advanced search modal end --%>
	</div>
</body>
</html>

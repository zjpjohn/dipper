<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<!DOCTYPE html>
<html lang="en">
<head>
<link rel="shortcut icon" href="${basePath }img/title_cloud.png" />
<title>角色管理</title>
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
<meta charset="utf-8" />
<meta name="description" content="overview &amp; stats" />
<meta name="viewport"
	content="width=device-width, initial-scale=1.0, maximum-scale=1.0" />
<jsp:include page="../js.jsp"></jsp:include>
<link rel="stylesheet" href="${basePath}css/user/host.css" />
<script src="${basePath }js/console/role.js"></script>
<c:set var="authButton" value='${buttonsAuth}'></c:set>

</head>
<body class="no-skin">
	<c:if test="${fn:contains(authButton,'roleDelete')}">
		<input type="hidden" id="delete_role">
	</c:if>
	<c:if test="${fn:contains(authButton,'roleUpdate')}">
		<input type="hidden" id="update_role">
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
			<jsp:param value="manage_role" name="page_index" />
		</jsp:include>
		<div class="main-content">
			<div class="main-content-inner">
				<div class="breadcrumbs" id="breadcrumbs">
					<ul class="breadcrumb">
						<li><i class="ace-icon fa fa-home home-icon"></i> <a
							href="${basePath}index.html"><strong>首页</strong></a></li>
						<li class="active"><b>角色管理</b></li>
					</ul>
				</div>
				<div class="page-header">
				   <h1>欢迎来到角色管理 
				   	<small>
				   		<br>角色是发布平台中所有角色的简称，角色管理提供角色的赋权、角色的修改等功能。详情查看“使用文档”
				   	</small>
				   </h1>
				</div>
				<div class="page-content">
					<div class="row">
						<div class="col-xs-12">
							<div class="well well-sm">
								<c:if test="${fn:contains(authButton,'roleAuth')}">
									<button class="btn btn-sm btn-success btn-round" id="authToRole">
										<i class="ace-icon fa fa-pencil-square-o bigger-125"></i> <b>角色授权</b>
									</button>
								</c:if>
								 <c:if test="${fn:contains(authButton,'roleCreate')}"> 
									<button class="btn btn-sm btn-success btn-round" id="createRole">
										<i class="ace-icon fa fa-pencil-square-o bigger-125"></i> <b>创建角色</b>
									</button>
								 </c:if>
								<div class="col-xs-12 col-sm-4" style="float: right">
									<c:if test="${fn:contains(authButton,'roleList')}">
										<div class="input-group">
											<input type="text" id="search_role"
												class="form-control search-query" placeholder="请输入名称进行模糊查询">
											<span class="input-group-btn">
												<button type="button"
													class="btn btn-round btn-primary btn-sm"
													onclick="searchRoles()">
													查找 <i
														class="ace-icon fa fa-search icon-on-right bigger-110"></i>
												</button>
											</span> <span class="input-group-btn"> &nbsp; </span> <span
												class="input-group-btn">
												<button id="detailSearch" type="button"
													class="btn btn-warning btn-round btn-sm" onclick="AdvancedSearchRoles()">
													更多 <i
														class="ace-icon fa fa-search icon-on-right bigger-110"></i>
												</button>
											</span>
										</div>
									</c:if>
								</div>
							</div>
							<c:if test="${fn:contains(authButton,'roleList')}">
								<div>
									<table id="role_list"></table>
									<div id="role_page"></div>
								</div>
							</c:if>
						</div>
					</div>
				</div>
			</div>
		</div>
		<%-- Create host info--%>
		<div class="modal fade" id="createRoleModal" tabindex="-1"
			role="dialog" aria-labelledby="myModalLabel" aria-hidden="true"
			data-backdrop="static">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal"
							aria-hidden="true">×</button>
						<h4 class="modal-title" id="myModalLabel">添加角色</h4>
					</div>
					<div class="modal-body">
						<div class="well" style="margin-top: 1px;">
							<form class='form-horizontal' role='form' id='create_role_form'>
								<div class='form-group'>
									<label class='col-sm-3'><b>角色名称：</b></label>
									<div class='col-sm-9'>
										<input id="role_name" name='role_name' type='text'
											class="form-control" placeholder="输入角色名称..." />
									</div>
								</div>
								<div class='form-group'>
									<label class='col-sm-3'><b>角色描述：</b></label>
									<div class='col-sm-9'>
										<input id="role_desc" name='role_desc' type='text'
											class="form-control" placeholder="输入角色描述..."  />
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

		<%-- Modify host info --%>
		<div class="modal fade" id="modifyRoleModal" tabindex="-1"
			role="dialog" aria-labelledby="myModalLabel" aria-hidden="true"
			data-backdrop="static">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal"
							aria-hidden="true">×</button>
						<h4 class="modal-title" id="myModalLabel">修改角色</h4>
					</div>
					<div class="modal-body">
						<div class="well" style="margin-top: 1px;">
							<form class='form-horizontal' role='form' id='modify_role_form'>
								<div class='form-group'>
									<label class='col-sm-3'><b>角色ID：</b></label>
									<div class='col-sm-9'>
										<input id="role_id_edit" name='role_id_edit' type='text'
											class="form-control" readOnly="true" />
									</div>
								</div>
								<div class='form-group'>
									<label class='col-sm-3'><b>角色名称：</b></label>
									<div class='col-sm-9'>
										<input id="old_role_name_edit" type='hidden'/>
										<input id="role_name_edit" name='role_name_edit' type='text'
											class="form-control" />
									</div>
								</div>
								<div class='form-group'>
									<label class='col-sm-3'><b>角色描述：</b></label>
									<div class='col-sm-9'>
										<input id="role_desc_edit" name='role_desc_edit' type='text'
											class="form-control" />
									</div>
								</div>
							</form>
						</div>

					</div>
					<div class="modal-footer">
						<button id="modify_cancel" type="button"
							class="btn btn-round btn-danger">取消</button>
						<button id="modify_submit" type="button"
							class="btn btn-round btn-success">提交</button>
					</div>
				</div>
			</div>
		</div>
		<%--Auth to role --%>
		<div class="modal fade" id="authToRoleModal" tabindex="-1"
			role="dialog" aria-labelledby="myModalLabel" aria-hidden="true"
			data-backdrop="static">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal"
							aria-hidden="true">×</button>
						<h4 class="modal-title" id="myModalLabel">角色赋予权限</h4>
					</div>
					<div class="modal-body">
						<label class='col-sm-3'><b>权限树：</b></label>
						<div id="roleAuthTree" class="ztree col-sm-9"></div>
						<!-- <div class='well ' style='margin-top: 1px;'>
							<form class='form-horizontal' role='form' id='add_cluster_form'>
								<div class='form-group' style="overflow:auto;height:auto">
									<table class="table table-bordered once-table"
										style="margin: 10px; width: 97%">
										<thead>
											<tr>
												<th width="6%"></th>
												<th width="18%">权限ID</th>
												<th width="43%">权限名称</th>
											</tr>
										</thead>
										<tbody id="authbody">
										</tbody>
									</table>
								</div>
							</form>
						</div> -->
					</div>
					<div class="modal-footer">
						<button id="authAuth_cancel" type="button"
							class="btn btn-round btn-danger">取消</button>
						<button id="authAuth_submit" type="button"
							class="btn btn-round btn-success">提交</button>
					</div>
				</div>
			</div>
		</div>
<%--advanced search modal begin --%>
		<div class="modal fade" id="advancedSearchRoleModal" tabindex="-1"
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
													<option value='1'>角色名</option>
													<option value='2'>角色描述</option>
													<option value='3'>角色状态</option>
												</select>
											</div> <input class="short-input" type="text"
											name="search_role_value" id="search_role_value"
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
						<button id="advanced_role_cancel" type="button"
							class="btn btn-danger btn-round">取消</button>
						<button id="advanced_role_search" type="button"
							class="btn btn-success btn-round">查询</button>
					</div>
				</div>
			</div>
		</div>
		<%--advanced search modal end --%>
	</div>
</body>
</html>

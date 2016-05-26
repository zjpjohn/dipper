<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<!DOCTYPE html>
<html lang="en">
<head>
<link rel="shortcut icon" href="${basePath }img/title_cloud.png" />
<title>用户管理</title>
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
<meta charset="utf-8" />
<meta name="description" content="overview &amp; stats" />
<meta name="viewport"
	content="width=device-width, initial-scale=1.0, maximum-scale=1.0" />
<jsp:include page="../js.jsp"></jsp:include>
<link rel="stylesheet" href="${basePath}css/user/host.css" />
<script src="${basePath }js/console/user.js"></script>
<c:set var="authButton" value='${buttonsAuth}'></c:set>

</head>
<body class="no-skin">
	<c:if test="${fn:contains(authButton,'userDelete')}">
		<input type="hidden" id="delete_user">
	</c:if>
	<c:if test="${fn:contains(authButton,'userUpdate')}">
		<input type="hidden" id="update_user">
	</c:if>
	<c:if test="${fn:contains(authButton,'activeUser')}">
		<input type="hidden" id="active_user">
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
			<jsp:param value="manage_user" name="page_index" />
		</jsp:include>
		<div class="main-content">
			<div class="main-content-inner">
				<div class="breadcrumbs" id="breadcrumbs">
					<ul class="breadcrumb">
						<li><i class="ace-icon fa fa-home home-icon"></i> <a
							href="${basePath}index.html"><strong>首页</strong></a></li>
						<li class="active"><b>用户管理</b></li>
					</ul>
				</div>
				<div class="page-header">
				   <h1>欢迎来到用户管理 
				   	<small>
				   		<br>用户是发布平台中所有角色下用户的简称，用户管理提供用户的添加、授权、编辑、冻结、激活等功能。详情查看“使用文档”
				   	</small>
				   </h1>
				</div>
				<div class="page-content">
					<div class="row">
						<div class="col-xs-12">
							<div class="well well-sm">
								<c:if test="${fn:contains(authButton,'userCreate')}">
									<button class="btn btn-sm btn-success btn-round" data-toggle="modal"
										data-target="#createUserModal">
										<i class="ace-icon fa fa-pencil-square-o bigger-125"></i> <b>添加用户</b>
									</button>
								</c:if>
								<div class="btn-group">
									<button data-toggle="dropdown"
										class="btn btn-primary btn-sm dropdown-toggle btn-round">
										<i class="ace-icon fa fa-wrench  bigger-110 icon-only"></i> <b>更多操作</b>
										<i class="ace-icon fa fa-angle-down icon-on-right"></i>
									</button>
									<c:if test="${fn:contains(authButton,'userAddRole')}">
										<ul class="dropdown-menu dropdown-primary dropdown-menu-right">
											<li><a id="authToUser" class="glyphicon glyphicon-play"
												href="#">用户授权</a></li>
										</ul>
									</c:if>
								</div>
								<div class="col-xs-12 col-sm-4" style="float: right">
									<c:if test="${fn:contains(authButton,'userList')}">
										<div class="input-group">
											<input type="text" id="search_user"
												class="form-control search-query" placeholder="请输入名称进行模糊查询">
											<span class="input-group-btn">
												<button type="button"
													class="btn btn-round btn-primary btn-sm"
													onclick="searchUsers()">
													查找 <i
														class="ace-icon fa fa-search icon-on-right bigger-110"></i>
												</button>
											</span> <span class="input-group-btn"> &nbsp; </span> <span
												class="input-group-btn">
												<button id="detailSearch" type="button"
													class="btn btn-warning btn-round btn-sm" onclick="AdvancedSearchUsers()">
													更多 <i
														class="ace-icon fa fa-search icon-on-right bigger-110"></i>
												</button>
											</span>
										</div>
									</c:if>
								</div>
							</div>
							<c:if test="${fn:contains(authButton,'userList')}">
								<div>
									<table id="user_list"></table>
									<div id="user_page"></div>
								</div>
							</c:if>
						</div>
					</div>
				</div>
			</div>
		</div>
		<%-- Create host info --%>
		<div class="modal fade" id="createUserModal" tabindex="-1"
			role="dialog" aria-labelledby="myModalLabel" aria-hidden="true"
			data-backdrop="static">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal"
							aria-hidden="true">×</button>
						<h4 class="modal-title" id="myModalLabel">添加用户</h4>
					</div>
					<div class="modal-body">
						<div class="well" style="margin-top: 1px;">
							<form class='form-horizontal' role='form' id='create_user_form'>
								<div class='form-group'>
									<label class='col-sm-3'><b><font color="red">*</font>&nbsp;用户名：</b></label>
									<div class='col-sm-9'>
										<input id="user_name" name='user_name' type='text'
											class="form-control" placeholder="只能包含中文、英文、数字、下划线,不能大于32位..." />
									</div>
								</div>
								<!-- <div class='form-group'>
									<label class='col-sm-3'><b>登录密码：</b></label>
									<div class='col-sm-9'>
										<input id="user_pass" name='user_pass' type='password'
											class="form-control" placeholder="输入密码..." />
									</div>
								</div> -->
								<div class='form-group'>
									<label class='col-sm-3'><b><font color="red">*</font>&nbsp;邮箱：</b></label>
									<div class='col-sm-9'>
										<input id="user_mail" name='user_mail' type='text'
											class="form-control" placeholder="输入合法的邮箱：例如：xx@xx.com..." />
									</div>
								</div>
								<div class='form-group'>
									<label class='col-sm-3'><b><font color="red">*</font>&nbsp;电话：</b></label>
									<div class='col-sm-9'>
										<input id="user_phone" name='user_phone' type='text'
											class="form-control" placeholder="输入合法的11位手机号..." />
									</div>
								</div>

								<div class='form-group'>
									<label class='col-sm-3'><b>公司：</b></label>
									<div class='col-sm-9'>
										<input id="user_company" name='user_company' type='text'
											class="form-control" placeholder="输入公司地址..." />
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
		<div class="modal fade" id="modifyUserModal" tabindex="-1"
			role="dialog" aria-labelledby="myModalLabel" aria-hidden="true"
			data-backdrop="static">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal"
							aria-hidden="true">×</button>
						<h4 class="modal-title" id="myModalLabel">修改用户</h4>
					</div>
					<div class="modal-body">
						<div class="well" style="margin-top: 1px;">
							<form class='form-horizontal' role='form' id='modify_user_form'>
								<div class='form-group'>
									<label class='col-sm-3'><b>用户ID：</b></label>
									<div class='col-sm-9'>
										<input id="user_id_edit" name='user_id_edit' type='text'
											class="form-control" readOnly="true" />
									</div>
								</div>
								<div class='form-group'>
									<label class='col-sm-3'><b><font color="red">*</font>&nbsp;用户名：</b></label>
									<div class='col-sm-9'>
										<input type="hidden" id="user_oldname_edit"/>
										<input id="user_name_edit" name='user_name_edit' type='text'
											class="form-control" />
									</div>
								</div>
								<!-- <div class='form-group'>
									<label class='col-sm-3'><b>登录密码：</b></label>
									<div class='col-sm-9'>
										<input id="user_pass_edit" name='user_pass_edit'
											type='password' class="form-control" />
									</div>
								</div> -->
								<div class='form-group'>
									<label class='col-sm-3'><b><font color="red">*</font>&nbsp;邮箱：</b></label>
									<div class='col-sm-9'>
										<input id="user_mail_edit" name='user_mail_edit' type='text'
											class="form-control" />
									</div>
								</div>
								<div class='form-group'>
									<label class='col-sm-3'><b><font color="red">*</font>&nbsp;电话：</b></label>
									<div class='col-sm-9'>
										<input id="user_phone_edit" name='user_phone_edit' type='text'
											class="form-control" />
									</div>
								</div>

								<div class='form-group'>
									<label class='col-sm-3'><b>公司：</b></label>
									<div class='col-sm-9'>
										<input id="user_company_edit" name='user_company_edit'
											type='text' class="form-control" />
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
	  <%--Auth role to user --%>
		<div class="modal fade" id="authToUserModal" tabindex="-1"
			role="dialog" aria-labelledby="myModalLabel" aria-hidden="true"
			data-backdrop="static">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal"
							aria-hidden="true">×</button>
						<h4 class="modal-title" id="myModalLabel">用户赋予角色</h4>
					</div>
					<div class="modal-body">
						<div class='well ' style='margin-top: 1px;'>
							<form class='form-horizontal' role='form' id='add_cluster_form'>
								<div class='form-group'>
									<table class="table table-bordered once-table"
										style="margin: 10px; width: 97%">
										<thead>
											<tr>
												<th width="6%"></th>
												<th width="18%">角色ID</th>
												<th width="43%">角色名称</th>
											</tr>
										</thead>
										<tbody id="rolebody">
										</tbody>
									</table>
								</div>
							</form>
						</div>
					</div>
					<div class="modal-footer">
						<button id="authUser_cancel" type="button"
							class="btn btn-round btn-danger">取消</button>
						<button id="authUser_submit" type="button"
							class="btn btn-round btn-success">提交</button>
					</div>
				</div>
			</div>
		</div>
       <%--advanced search modal begin --%>
		<div class="modal fade" id="advancedSearchUserModal" tabindex="-1"
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
													<option value='1'>用户名</option>
													<option value='2'>邮箱</option>
													<option value='3'>电话</option>
													<option value='4'>公司</option>
												</select>
											</div> <input class="short-input" type="text"
											name="search_user_value" id="search_user_value"
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
						<button id="advanced_user_cancel" type="button"
							class="btn btn-danger btn-round">取消</button>
						<button id="advanced_user_search" type="button"
							class="btn btn-success btn-round">查询</button>
					</div>
				</div>
			</div>
		</div>
		<%--advanced search modal end --%>
	</div>
</body>
</html>

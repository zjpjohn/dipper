<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<!DOCTYPE html>
<html lang="en">
<head>
<link rel="shortcut icon" href="${basePath }img/title_cloud.png" />
<title>主机管理</title>
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
<meta charset="utf-8" />
<meta name="description" content="overview &amp; stats" />
<meta name="viewport"
	content="width=device-width, initial-scale=1.0, maximum-scale=1.0" />
<jsp:include page="../js.jsp"></jsp:include>
<link rel="stylesheet" href="${basePath}css/user/host.css" />
<script src="${basePath }js/console/host.js"></script>
<c:set var="authButton" value='${buttonsAuth}'></c:set>
</head>
<body class="no-skin">
	<jsp:include page="../header.jsp"></jsp:include>
	<c:if test="${fn:contains(authButton,'hostDeleteOne')}">
		<input type="hidden" id="delete_host">
	</c:if>
	<c:if test="${fn:contains(authButton,'hostUpdate')}">
		<input type="hidden" id="update_host">
	</c:if>

	<div class="main-container" id="main-container">
		<script type="text/javascript">
			try {
				ace.settings.check('main-container', 'fixed')
			} catch (e) {
			}
		</script>
		<jsp:include page="../nav.jsp">
			<jsp:param value="host_admin" name="page_index" />
		</jsp:include>
		<div class="main-content">
			<div class="main-content-inner">
				<div class="breadcrumbs" id="breadcrumbs">
					<ul class="breadcrumb">
						<li><i class="ace-icon fa fa-home home-icon"></i> <a
							href="${basePath}index.html"><strong>首页</strong></a></li>
						<li class="active"><b>主机管理</b></li>
					</ul>
				</div>
				<div class="page-header">
					<h1>
						欢迎来到主机管理 <small> <br>主机是发布平台中所有类型主机的简称，主机管理提供链接主机，修改主机，删除主机等功能。详情查看“使用文档”
						</small>
					</h1>
				</div>
				<div id="mask" class="mask">
					<div id = "spinner-message" class="spinner-message">
						<font></font>
					</div>
					<i id = "spinner" class="spinner ace-icon fa fa-spinner fa-spin white"></i>
		        </div> 
				<input id="hostType" type="hidden" value="${hostType}">
				<div class="page-content">
					<div class="row">
						<div class="col-xs-12">
							<div class="well well-sm">
								<c:if test="${fn:contains(authButton,'hostCreate')}">
									<button class="btn btn-sm btn-success btn-round"
										data-toggle="modal" data-target="#createHostModal">
										<i class="ace-icon fa fa-pencil-square-o bigger-125"></i> <b>添加主机</b>
									</button>
								</c:if>
								<c:if test="${fn:contains(authButton,'powerManager')}">
									<button class="btn btn-sm btn-primary btn-round" id="powerManager"
											data-toggle="modal" onclick="javascript:showPowerDalig()" disabled>
											<i class="ace-icon fa fa-pencil-square-o bigger-125"></i> <b>电源管理</b>
									</button>
								</c:if>
								<div class="btn-group">
									<button data-toggle="dropdown"
										class="btn btn-primary btn-sm dropdown-toggle btn-round">
										<i class="ace-icon fa fa-wrench  bigger-110 icon-only"></i> <b>更多操作</b>
										<i class="ace-icon fa fa-angle-down icon-on-right"></i>
									</button>
									<ul class="dropdown-menu dropdown-primary dropdown-menu-right">
										<c:if test="${fn:contains(authButton,'hostAddCluster')}">
											<li><a id="joinCluster" class="glyphicon glyphicon-play"
												href="#">移入集群</a></li>
										</c:if>
										<c:if test="${fn:contains(authButton,'hostRemoveCluster')}">
											<li><a id="removeCluster"
												class="glyphicon glyphicon-stop" href="#">移出集群</a></li>
										</c:if>
										<c:if test="${fn:contains(authButton,'hostDeletes')}">
											<li><a id="remove" class="glyphicon glyphicon-trash"
												href="#">批量删除</a></li></c:if>
										<c:if test="${fn:contains(authButton,'hostRemoveCluster')}">
											<li><a class="glyphicon glyphicon-play" id="startHost" style="display:none"
												href="javascript:Power.openHost()">连接服务器</a></li>
										</c:if>
										<c:if test="${fn:contains(authButton,'shutdownHostPower')}">
											<li><a id="shutDownHost" style="display:none"
												class="glyphicon glyphicon-stop" href="javascript:Power.closeHost()">关闭服务器</a></li>
										</c:if>
									</ul>
								</div>
								<div class="col-xs-12 col-sm-4" style="float: right">
									<c:if test="${fn:contains(authButton,'hostList')}">
										<div class="input-group">
											<input type="text" id="search_host"
												class="form-control search-query" placeholder="请输入名称进行模糊查询">
											<span class="input-group-btn">
												<button type="button"
													class="btn btn-primary  btn-round btn-sm"
													onclick="searchHosts()">
													查找 <i
														class="ace-icon fa fa-search icon-on-right bigger-110"></i>
												</button>
											</span> <span class="input-group-btn"> &nbsp; </span> <span
												class="input-group-btn">
												<button id="detailSearch" type="button"
													class="btn btn-warning btn-round btn-sm"
													onclick="AdvancedSearchHost()">
													更多 <i
														class="ace-icon fa fa-search icon-on-right bigger-110"></i>
												</button>
											</span>
										</div>
									</c:if>
								</div>
							</div>
							<c:if test="${fn:contains(authButton,'hostList')}">
								<div>
									<table id="host_list"></table>
									<div id="host_page"></div>
								</div>
							</c:if>
						</div>
					</div>
				</div>
			</div>
		</div>
		<%-- Create host info --%>
		<div class="modal fade" id="createHostModal" tabindex="-1"
			role="dialog" aria-labelledby="myModalLabel" aria-hidden="true"
			data-backdrop="static">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal"
							aria-hidden="true">×</button>
						<h4 class="modal-title" id="myModalLabel">添加主机</h4>
					</div>
					<div class="modal-body">
						<div class="well" style="margin-top: 1px;">
							<form class='form-horizontal' role='form' id='create_host_form'>
								<div class='form-group'>
									<label class='col-sm-3'><b>主机名称：</b></label>
									<div class='col-sm-9'>
										<input id="host_name" name='host_name' type='text'
											class="form-control" />
									</div>
								</div>
								<div class='form-group'>
									<label class='col-sm-3'><b>主机地址：</b></label>
									<div class='col-sm-9'>
										<input id="host_ip" name='host_ip' type='text'
											class="form-control" />
									</div>
								</div>
								<div class='form-group' id="form-host-type">
									<label class='col-sm-3'><b>主机类型：</b></label>
									<div class='col-sm-9'>
										<select id='host_type' name='host_type' class='form-control'>
											<option value='0'>SWARM</option>
											<option value='1'>DOCKER</option>
											<option value='2'>REGISTRY</option>
											<option value='3'>NGINX</option>
											<option value="4">其他</option>
										</select>
									</div>
								</div>
								<div class='form-group'>
									<label class='col-sm-3'><b>登录账号：</b></label>
									<div class='col-sm-9'>
										<input id="userName" name='userName' type='text'
											class="form-control" />
									</div>
								</div>
								<div class='form-group'>
									<label class='col-sm-3'><b>登录密码：</b></label>
									<div class='col-sm-9'>
										<input id="password" name='password' type='password'
											class="form-control" />
									</div>
								</div>
								<div class='form-group'>
									<label class='col-sm-3'><b>主机描述：</b></label>
									<div class='col-sm-9'>
										<textarea id="host_desc" name="host_desc" class="form-control"
											rows="3"></textarea>
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
		<div class="modal fade" id="modifyHostModal" tabindex="-1"
			role="dialog" aria-labelledby="myModalLabel" aria-hidden="true"
			data-backdrop="static">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal"
							aria-hidden="true">×</button>
						<h4 class="modal-title" id="myModalLabel">修改主机</h4>
					</div>
					<div class="modal-body">
						<div class="well" style="margin-top: 1px;">
							<form class='form-horizontal' role='form' id='modify_host_form'>
								<div class='form-group'>
									<label class='col-sm-3'><b>主机名称：</b></label>
									<div class='col-sm-9'>
										<input type="hidden" id="host_oldname_edit"/>
										<input id='host_id_edit' type='hidden' /> <input
											id="host_name_edit" name='host_name_edit' type='text'
											class="form-control" />
									</div>
								</div>
								<div class='form-group'>
									<label class='col-sm-3'><b>主机描述：</b></label>
									<div class='col-sm-9'>
										<textarea id="host_desc_edit" name="host_desc_edit"
											class="form-control" rows="3"></textarea>
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

		<%--Add host to cluster --%>
		<div class="modal fade" id="addHostInClusterModal" tabindex="-1"
			role="dialog" aria-labelledby="myModalLabel" aria-hidden="true"
			data-backdrop="static">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal"
							aria-hidden="true">×</button>
						<h4 class="modal-title" id="myModalLabel">加入集群</h4>
					</div>
					<div class="modal-body">
						<div class='well ' style='margin-top: 1px;'>
							<form class='form-horizontal' role='form' id='add_cluster_form'>
								<div class='form-group'>
									<label class='col-sm-3'><b>目标集群：</b></label>
									<div class='col-sm-9'>
										<select id='cluster_select' name='cluster_select'
											class='form-control'>
											<option value='0:0'>请选择集群</option>
										</select>
									</div>
								</div>
							</form>
						</div>
					</div>
					<div class="modal-footer">
						<button id="addHost_cancel" type="button"
							class="btn btn-round btn-danger">取消</button>
						<button id="addHost" type="button"
							class="btn btn-round btn-success">提交</button>
					</div>
				</div>
			</div>
		</div>
		<%--Add host to cluster end--%>

		<%--advanced search modal begin --%>
		<div class="modal fade" id="advancedSearchHostModal" tabindex="-1"
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
													<option value='1'>主机名称</option>
													<option value='2'>主机类型</option>
													<option value='3'>CPU数量</option>
													<option value='4'>创建人</option>
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
		<div class="modal fade" id="powerManagerModal" tabindex="-1"
			role="dialog" aria-labelledby="myModalLabel" aria-hidden="true"
			data-backdrop="static">
			<div class="modal-dialog">
				<div class="modal-content">
				<div class="modal-header">
					<h4 class="modal-title" id="modaltitle">服务器电源管理<a class="close" data-dismiss="modal" aria-hidden="true"><span class="glyphicon glyphicon-remove"></span></a></h4>
				</div>
				
				<div class="modal-body">
					<div class="well" style="margin-top: 1px;">
							<form class='form-horizontal' role='form' id='create-form'>
								<div class='form-group'>
									<label class='col-sm-3'><b>服务器IP：</b></label>
									<div class='col-sm-9'>
										<input id='pow_host_id' type='hidden' /> <input
											id="pow_host_ip" name='pow_host_ip' type='text'
											class="form-control"  disabled/>
									</div>
								</div>
								<div class='form-group'>
									<label class='col-sm-3'><b>服务器主板IP：</b></label>
									<div class='col-sm-9'>
										<input id="pow_motherboard_ip" name='pow_motherboard_ip' type='text'
											class="form-control" />
									</div>
								</div>
								<div class='form-group'>
									<label class='col-sm-3'><b>端口：</b></label>
									<div class='col-sm-9'>
										<input id="pow_port" name='pow_port' type='text'
												class="form-control" />
									</div>
								</div>
								<div class='form-group'>
									<label class='col-sm-3'><b>账号：</b></label>
									<div class='col-sm-9'>
										<input id="pow_username" name='pow_username' type='text'
												class="form-control" />
									</div>
								</div>
								<div class='form-group'>
									<label class='col-sm-3'><b>密码：</b></label>
									<div class='col-sm-9'>
										<input id="pow_pwd" name='pow_pwd' type='password'
												class="form-control" />
									</div>
								</div>
								<div class='form-group'>
									<label class='col-sm-3'><b>当前状态：</b></label>
									<div class='col-sm-9'>
										<input id="server_status_val" type="hidden" value="-1"/>
										<input id="server_status_text" name='server_status' type='text' value="未验证"
												class="form-control" disabled/>
									</div>
								</div>
							</form>
						</div>
				
				
				
					<!-- <form class="form form-horizontal" id="create-form">
						<fieldset>
							<div class="item">
								<div class="control-label"></div>
								<div class="controls">
								<input type="hidden" id="power_uuid" name="power_uuid"  >
									<input type="text" id="host_uuid" name="host_uuid" disabled class="oc-disable" >
								</div>
							</div>
							<div id="pwditem" class="item">
								<div class="control-label"></div>
								<div class="controls">
									<input type="password" id="power_pwd" name="power_pwd" autofocus="" >
								</div>
							</div>
							<div class="item">
								<div class="control-label">当前状态</div>
								<div class="controls">
									<input type="hidden" id="powerStatus">
									<input type="text" id="server_status" value="运行中" disabled class="oc-disable">
								</div>
							</div>
						</fieldset>
					</form> -->
				</div>
				<div class="modal-footer">
		        	<button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
					<button class="btn btn-primary" id="validAction" type="button">验证</button>
					<!-- <button id="startServerAction" type="button" class="btn btn-default">启动服务器</button>
					<button id="closeServerAction" type="button" class="btn btn-danger">关闭服务器</button> -->
					<button class="btn btn-success" id="savePower" type="button">保存</button> 
		      	</div>
			</div>
			</div>
		</div>		
	</div>
</body>
</html>

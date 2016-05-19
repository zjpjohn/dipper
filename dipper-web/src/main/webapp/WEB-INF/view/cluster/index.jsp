<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<!DOCTYPE html>
<html lang="en">
<head>
<link rel="shortcut icon" href="${basePath }img/title_cloud.png" />
<title>集群管理</title>
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
<meta charset="utf-8" />
<meta name="description" content="overview &amp; stats" />
<meta name="viewport"
	content="width=device-width, initial-scale=1.0, maximum-scale=1.0" />
<jsp:include page="../js.jsp"></jsp:include>
<script src="${basePath }js/console/cluster.js"></script>
<!-- 添加额外的CSS样式表 -->
<link rel="stylesheet" href="${basePath}css/user/app.css" />
<!-- 添加额外的CSS样式表 -->
<c:set var="authButton" value='${buttonsAuth}'></c:set>

</head>
<body class="no-skin">
	<jsp:include page="../header.jsp"></jsp:include>
	<c:if test="${fn:contains(authButton,'clusterDelete')}">
		<input type="hidden" id="delete_cluster">
	</c:if>
	<c:if test="${fn:contains(authButton,'clusterUpdate')}">
		<input type="hidden" id="update_cluster">
	</c:if>

	<div class="main-container" id="main-container">
		<script type="text/javascript">
			try {
				ace.settings.check('main-container', 'fixed')
			} catch (e) {
			}
		</script>
		<jsp:include page="../nav.jsp">
			<jsp:param value="cluster_admin" name="page_index" />
		</jsp:include>
		<div class="main-content">
			<div class="main-content-inner">
				<div class="breadcrumbs" id="breadcrumbs">
					<ul class="breadcrumb">
						<li><i class="ace-icon fa fa-home home-icon"></i> <a
							href="${basePath}index.html"><strong>首页</strong></a></li>
						<li class="active"><b>集群管理</b></li>
					</ul>
				</div>
				<div class="page-header">
					<h1>
						欢迎来到集群管理 <small> <br>集群提供了对应用主机的统一管理和资源调度，是容器的载体，为所有的容器提供统一的操作入口。集群管理
							提供了创建集群，删除集群，修改集群，集群应用约束等功能。详情查看“使用文档”
						</small>
					</h1>
				</div>
				<div id="mask" class="mask">
					<div id="spinner-message" class="spinner-message">
						<font></font>
					</div>
					<i id="spinner"
						class="spinner ace-icon fa fa-spinner fa-spin white"></i>
				</div>
				<div class="page-content">
					<div class="row">
						<div class="col-xs-12">
							<div class="well well-sm">
								<c:if test="${fn:contains(authButton,'clusterCreate')}">
									<button class="btn btn-sm btn-success btn-round"
										data-toggle="modal" onclick="initCreateCluster();">
										<i class="ace-icon fa fa-pencil-square-o bigger-125"></i> <b>创建集群</b>
									</button>
								</c:if>
								<div class="btn-group">
									<button data-toggle="dropdown"
										class="btn btn-primary btn-sm dropdown-toggle btn-round">
										<i class="ace-icon fa fa-wrench  bigger-110 icon-only"></i> <b>更多操作</b>
										<i class="ace-icon fa fa-angle-down icon-on-right"></i>
									</button>
									<ul class="dropdown-menu dropdown-primary dropdown-menu-right">
										<c:if test="${fn:contains(authButton,'clusterAddHost')}">
											<li><a class="glyphicon glyphicon-play"
												id="addHostToCluster" href="#">添加主机</a></li>
										</c:if>
										<c:if test="${fn:contains(authButton,'clusterRemoveHost')}">
											<li><a class="glyphicon glyphicon-stop"
												id="removeHostFromCluster" href="#">解绑主机</a></li>
										</c:if>
										<c:if test="${fn:contains(authButton,'clusterHealthCheck')}">
											<li><a class="glyphicon glyphicon-play"
												id="healthyCheck" href="#">健康检查</a></li>
										</c:if>
										<c:if test="${fn:contains(authButton,'clusterRecover')}">
											<li><a class="glyphicon glyphicon-play" id="recover"
												href="#">恢复集群</a></li>
										</c:if>
									</ul>
								</div>
								<div class="col-xs-12 col-sm-4" style="float: right">
									<c:if test="${fn:contains(authButton,'clusterList')}">
										<div class="input-group">
											<input type="text" id="search_cluster"
												class="form-control search-query" placeholder="请输入集群名称模糊查询">
											<span class="input-group-btn">
												<button type="button"
													class="btn btn-primary  btn-round btn-sm"
													onclick="searchClusters()">
													查找 <i
														class="ace-icon fa fa-search icon-on-right bigger-110"></i>
												</button>
											</span> <span class="input-group-btn"> &nbsp; </span> <span
												class="input-group-btn">
												<button id="detailSearch" type="button"
													class="btn btn-warning btn-round btn-sm"
													onclick="AdvancedSearchCluster()">
													更多 <i
														class="ace-icon fa fa-search icon-on-right bigger-110"></i>
												</button>
											</span>
										</div>
									</c:if>
								</div>
							</div>
							<c:if test="${fn:contains(authButton,'clusterList')}">
								<div>
									<table id="cluster_list"></table>
									<div id="cluster_page"></div>
								</div>
							</c:if>
						</div>
					</div>
				</div>
			</div>
		</div>

		<%--Add Cluster --%>
		<div class="modal fade" id="createClusterModal" tabindex="-1"
			role="dialog" aria-labelledby="myModalLabel" aria-hidden="true"
			data-backdrop="static">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal"
							aria-hidden="true">×</button>
						<h4 class="modal-title" id="myModalLabel">创建集群</h4>
					</div>
					<div class="modal-body">
						<div class='well ' style='margin-top: 1px;'>
							<form class="form-horizontal" role="form"
								id="create_cluster_form">
								<div class="form-group">
									<label class="col-sm-3"><b>集群名称：</b></label>
									<div class="col-sm-9">
										<input id="cluster_name" name="cluster_name" type="text"
											placeholder="请输入集群名称" class="form-control" />
									</div>
								</div>
								<div class="form-group">
									<label class="col-sm-3"><b>管理节点：</b></label>
									<div class="col-sm-9">
										<select id="hostMaster_select" name="hostMaster_select"
											class="form-control">
											<!-- onclick='getClusterMster()' -->
											<option value="0">请选择主机</option>
										</select>
									</div>
								</div>
								<div class="form-group">
									<label class="col-sm-3"><b>集群端口：</b></label>
									<div class="col-sm-9">
										<font color="red"><b> <input id="cluster_port"
												name="cluster_port" type="text" readonly="readonly"
												class="form-control" /></b></font>
									</div>
								</div>
								<div class="form-group">
									<label class="col-sm-3"><b>创建方式：</b></label>
									<div class="col-sm-9"
										style="padding-top: 5px; font-size: 12px; color: #666;">
										<input name="cluster_mode" type="radio" class="ace" value="0"
											checked /> <span class="lbl">服务注册</span>
										&nbsp;&nbsp;&nbsp;&nbsp; <input name="cluster_mode"
											type="radio" class="ace" value="1" /> <span class="lbl">配置文件</span>
									</div>
								</div>
								<div class="form-group" id="cluster_file" style="display: none">
									<label class="col-sm-3"><b>管理文件：</b></label>
									<div class="col-sm-9">
										<input id="cluster_manage" name="cluster_manage" type="text"
											placeholder="集群配置文件" class="form-control" />
									</div>
								</div>
								<div class="form-group">
									<label class="col-sm-3"><b>资源配置：</b></label>
									<div class="col-sm-9"
										style="padding-top: 5px; font-size: 12px; color: #666;">
										<input name="cluster_resType" type="radio" class="ace" value="0"
											checked /> <span class="lbl">资源共享</span>
										&nbsp;&nbsp;&nbsp;&nbsp; <input name="cluster_resType"
											type="radio" class="ace" value="1" /> <span class="lbl">资源独享</span>
									</div>
								</div>
								<div class="form-group">
									<label class="col-sm-3"><b>集群描述：</b></label>
									<div class="col-sm-9">
										<textarea id="cluster_desc" name="cluster_desc"
											class="form-control" placeholder="请输入描述信息" rows="3"></textarea>
									</div>
								</div>
							</form>
						</div>
					</div>
					<div class="modal-footer">
						<button id="cancel" type="button" class="btn btn-round btn-danger">取消</button>
						<button id="submit" type="button"
							class="btn btn-round btn-success">提交</button>
					</div>
				</div>
			</div>
		</div>
		<%--Modify cluster info --%>
		<div class="modal fade" id="modifyClusterModal" tabindex="-1"
			role="dialog" aria-labelledby="myModalLabel" aria-hidden="true"
			data-backdrop="static">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal"
							aria-hidden="true">×</button>
						<h4 class="modal-title" id="myModalLabel">修改集群</h4>
					</div>
					<div class="modal-body">
						<div class='well ' style='margin-top: 1px;'>
							<form class='form-horizontal' role='form'
								id='modify_cluster_form'>
								<div class='form-group'>
									<label class='col-sm-3'><b>集群名称：</b></label>
									<div class='col-sm-9'>
										<input id='cluster_id_edit' type='hidden' /> <input
											type="hidden" id="cluster_oldname_edit" /> <input
											id="cluster_name_edit" name="cluster_name_edit" type='text'
											class="form-control" />
									</div>
								</div>
								<div class='form-group'>
									<label class='col-sm-3'><b>集群描述：</b></label>
									<div class='col-sm-9'>
										<textarea id="cluster_desc_edit" name="cluster_desc_edit"
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
						<h4 class="modal-title" id="myModalLabel">
							选择加入(<span id="clusterShowName"></span>)的主机
						</h4>
					</div>
					<div class="modal-body">
						<div class="well" style="margin-top: 1px;">
							<form class="form-horizontal" role="form" id="add_host_form">
								<div class='form-group' id="hosts_notincluster">
									<input type="hidden" id="clusterInfo"> <label
										class='col-sm-3'><b>选择主机：</b></label>
									<div class='col-sm-9'>
										<div class="item">
											<div class="leftusershow">
												<a href="#" class="list-group-item active">备选主机</a>
												<div class="blockUserShow">
													<div class="btn-group-vertical" id="blockHosts"></div>
												</div>
											</div>
											<div class="operate">
												<button type="button"
													class="btn btn-purple btn-sm btn-round" id="add-hosts">
													<span class="glyphicon glyphicon-forward"></span>&nbsp;增加
												</button>
												<button type="button" class="btn btn-grey btn-sm btn-round"
													id="remove-hosts" style="margin-top: 10px">
													<span class="glyphicon glyphicon-backward"></span>&nbsp;&nbsp;移除
												</button>
											</div>
											<div class="rightusershow">
												<a href="#" class="list-group-item active">已选主机</a>
												<div class="blockUserShow">
													<div class="btn-group-vertical" id="activeHosts"></div>
												</div>
											</div>
										</div>
									</div>
								</div>
							</form>
						</div>
					</div>
					<div class="modal-footer">
						<button id="addHost_cancel" type="button"
							class="btn btn-round btn-danger">取消</button>
						<button id="addHost" type="button"
							class="btn btn-round btn-success">确定</button>
					</div>
				</div>
			</div>
		</div>
		<%--Remove host from cluster --%>
		<div class="modal fade" id="removeHostFromClusterModal" tabindex="-1"
			role="dialog" aria-labelledby="myModalLabel" aria-hidden="true"
			data-backdrop="static">
			<div class="modal-dialog">
				<!-- <div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal"
							aria-hidden="true">×</button>
						<h4 class="modal-title" id="myModalLabel">移除主机</h4>
					</div>
					<div class="modal-body">
						<div class="well" style="margin-top: 1px;">
							<form class="form-horizontal" role="form" id="remove_host_form">
								<div class="form-group">
									<label class="col-sm-3"><b>主机列表：</b></label>
									<div class="col-sm-9">
										<select id="host_in_cluster" name="host_in_cluster"
											class="form-control">
											<option value="0">请选择主机</option>
										</select>
									</div>
								</div>
							</form>
						</div>
					</div>
					<div class="modal-footer">
						<button id="removeHost_cancel" type="button"
							class="btn btn-round btn-danger">取消</button>
						<button id="removeHost" type="button"
							class="btn btn-round btn-success">确定</button>
					</div>
				</div> -->
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal"
							aria-hidden="true">×</button>
						<h4 class="modal-title"">
							选择移出(<span id="show_cluster_name"></span>)的主机
						</h4>
					</div>
					<div class="modal-body">
						<div class="well" style="margin-top: 1px;">
							<form class="form-horizontal" role="form" id="remove_host_form">
								<div class='form-group'>
									<label class='col-sm-3'><b>选择主机：</b></label>
									<div class='col-sm-9'>
										<div class="item">
											<div class="leftusershow">
												<a href="#" class="list-group-item active">备选主机</a>
												<div class="blockUserShow">
													<div class="btn-group-vertical" id="remove_blockHosts"></div>
												</div>
											</div>
											<div class="operate">
												<button type="button" class="btn btn-purple btn-sm btn-round" id="remove_addHosts">
													<span class="glyphicon glyphicon-forward"></span>&nbsp;增加
												</button>
												<button type="button" class="btn btn-grey btn-sm btn-round" id="remove_removeHosts" style="margin-top: 10px">
													<span class="glyphicon glyphicon-backward"></span>&nbsp;&nbsp;移除
												</button>
											</div>
											<div class="rightusershow">
												<a href="#" class="list-group-item active">已选主机</a>
												<div class="blockUserShow">
													<div class="btn-group-vertical" id="remove_activeHosts"></div>
												</div>
											</div>
										</div>
									</div>
								</div>
							</form>
						</div>
					</div>
					<div class="modal-footer">
						<button id="removeHost_cancel" type="button"
							class="btn btn-round btn-danger">取消</button>
						<button id="removeHost" type="button"
							class="btn btn-round btn-success">确定</button>
					</div>
				</div>
			</div>
		</div>
		<%--advanced search modal begin --%>
		<div class="modal fade" id="advancedSearchClusterModal" tabindex="-1"
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
													<option value='1'>名称</option>
													<option value='2'>主服务器</option>
													<option value='3'>端口号</option>
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

	</div>
</body>
</html>

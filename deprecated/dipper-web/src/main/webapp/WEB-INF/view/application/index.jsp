<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<!DOCTYPE html>
<html lang="en">
<head>
<link rel="shortcut icon" href="${basePath }img/title_cloud.png" />
<title>构建应用</title>
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
<meta charset="utf-8" />
<meta name="description" content="overview &amp; stats" />
<meta name="viewport"
	content="width=device-width, initial-scale=1.0, maximum-scale=1.0" />
<jsp:include page="../js.jsp"></jsp:include>
<link rel="stylesheet" href="${basePath}css/user/app.css" />
<script src="${basePath }ace/assets/js/jquery.validate.min.js"></script>
<script src="${basePath }js/bootstrap-paginator.min.js"></script>
<script src="${basePath }ace/assets/js/fuelux/fuelux.wizard.min.js"></script>
<script src="${basePath }js/console/application.js"></script>
<c:set var="authButton" value='${buttonsAuth}'></c:set>
</head>
<body class="no-skin">
	<c:if test="${fn:contains(authButton,'appRemove')}">
		<input type="hidden" id="delete_app">
	</c:if>
	<c:if test="${fn:contains(authButton,'appModify')}">
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
			<jsp:param value="application_admin" name="page_index" />
		</jsp:include>
		<div class="main-content">
			<div class="main-content-inner">
				<div class="breadcrumbs" id="breadcrumbs">
					<ul class="breadcrumb">
						<li><i class="ace-icon fa fa-home home-icon"></i> <a
							href="${basePath}index.html"><strong>首页</strong></a></li>
						<li class="active"><i class="ace-icon fa fa-cubes"></i> <b>构建应用</b></li>
					</ul>
				</div>
				<div class="page-header">
					<h1>
						欢迎来到构建应用 <small><br>应用是发布平台中所有应用的简称，应用管理提供添加应用，修改应用，删除应用等功能。详情查看“使用文档”
						</small>
					</h1>
			        <div>
				</div>
				<div class="page-content">
					<div class="row">
						<div class="col-xs-12">
							<div class="well well-sm">
								<c:if test="${fn:contains(authButton,'appAdd')}">
									<button class="btn btn-sm btn-success btn-round" data-toggle="modal" onclick="showCreateModal()">
										<i class="ace-icon fa fa-pencil-square-o bigger-125"></i>&nbsp;<b>创建应用</b>
									</button>
								</c:if>
								<button class="btn btn-sm btn-danger btn-round"
									onclick="showAllApplications()">
									<i class="ace-icon fa fa-list-alt bigger-125"></i><b>显示全部</b>
								</button>
								<div class="col-xs-12 col-sm-4" style="float: right">
									<c:if test="${fn:contains(authButton,'appList')}">
										<div class="input-group">
											<input type="text" id="searchAppName"
												class="form-control search-query" placeholder="请输入名称进行模糊查询">
											<span class="input-group-btn">
												<button type="button"
													class="btn btn-primary  btn-round btn-sm"
													onclick="SearchApplications()">
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
							<c:if test="${fn:contains(authButton,'appList')}">
								<div>
									<table id="app_list"></table>
									<div id="app_page"></div>
								</div>
							</c:if>
						</div>
					</div>
				</div>
			</div>
		</div>
		<!-- create application begin -->
		<div id="modal-wizard" class="modal" data-backdrop="static" data-keyboard="false">
			<div class="modal-dialog" style="width:800px">
				<div class="modal-content">
					<div class="modal-header" data-target="#modal-step-contents">
						<ul class="wizard-steps">
							<li data-target="#modal-step1" class="active">
								<span class="step">1</span>
								<span class="title">基础配置</span>
							</li>

							<li data-target="#modal-step2">
								<span class="step">2</span>
								<span class="title">运行环境</span>
							</li>

							<li data-target="#modal-step3">
								<span class="step">3</span>
								<span class="title">其他配置</span>
							</li>

							<li data-target="#modal-step4">
								<span class="step">4</span>
								<span class="title">信息汇总</span>
							</li>
						</ul>
					</div>
					<div class="modal-body step-content" id="modal-step-contents">
						
						<div class="step-pane active" id="modal-step1">
							<div class="left">
								<div class="well" style="margin-top: 1px; margin-left:20px">
									<form class='form-horizontal' role='form' id='app_basic_form'>
										<div class="panel panel-primary">
											<div class="panel-heading" style="padding: 2px 10px;">
												<div class="panel-title">
													<h5>应用信息</h5>
												</div>
											</div>
											<div class="panel-body">
												<div class='form-group'>
													<label class='col-sm-3'><b><font color="red">*</font>应用名称</b></label>
													<div class='col-sm-9'>
														<input type="hidden" id="cre_app_id" value="0"/>
														<input type="hidden" id="old_app_name">
														<input class="short-input" name="app_name" id="app_name" type="text" placeholder="应用名称" value="" style="width:100%">
													</div>
												</div>
												<div class='form-group'>
													<label class='col-sm-3'><b>应用负载</b></label>
													<div class='col-sm-9'>
														<select class="dropdown-select" id="app_balance" name="app_balance" style="width: 100%">
															<option value="-1">请选择负载</option>
														</select>
													</div>
												</div>
												<div class='form-group'>
													<label class='col-sm-3'><b><font color="red" class="imp_input" style="display:none">*</font>访问路径</b></label>
													<div class='col-sm-9'>
														<input class="short-input" name="app_url" id="app_url" placeholder="应用访问路径，例如:/tomcat" type="text" value="" style="width:100%"/>
													</div>
												</div>
												<div class='form-group'>
													<label class='col-sm-3'><b><font color="red" class="imp_input" style="display:none">*</font>服务端口</b></label>
													<div class='col-sm-9'>
														<input class="short-input" name="app_port" id="app_port" type="text" placeholder="容器内部服务端口" value="" style="width:100%">
													</div>
												</div>
												<div class='form-group'>
													<label class='col-sm-3'><b>描述信息</b></label>
													<div class='col-sm-9'>
														<textarea id="app_desc" name="app_desc" class="short-input"
															rows="3" style="width:100%"></textarea>
													</div>
												</div>
												
											</div>
										</div>
										<div class="panel panel-success">
											<div class="panel-heading" style="padding: 0px 10px;">
												<div class="panel-title">
													<h5>默认参数配置</h5>
												</div>
											</div>
											<div class="panel-body">
												<div class='form-group'>
													<label class='col-sm-3'><b>环境变量</b></label>
													<div class='col-sm-9'>
														<ul class="env_params" id="env_params" style="list-style-type: none; margin: 0px">
															<li class="param">
																<input class="short-input" name="app_env" id="app_env" type="text"  placeholder="应用环境变量" value="" style="width: 94%;">
																<a href="#" id="env-plus" style="color: green"><i class="ace-icon glyphicon glyphicon-plus"></i></a>
																<a href="#" id="env-minus" style="color: red; display: none;"><i class="ace-icon glyphicon glyphicon-minus"></i></a>
															</li>
														</ul>
													</div>
												</div>
												<div class='form-group'>
													<label class='col-sm-3'><b>挂载点</b></label>
													<div class='col-sm-9'>
														<ul class="vol_params" id="vol_params" style="list-style-type: none; margin: 0px">
															<li class="param">
																<input class="short-input" type="text" name="host_volume" id="host_volume" placeholder="物理主机挂载目录" value=""
																style="width: 46%;">
																<input class="short-input" type="text" name="container_volume" id="container_volume" placeholder="容器内部被挂载目录" value=""
																style="width: 46%; margin-left:8px">
																<a href="#" id="vol-plus" style="color: green"><i class="ace-icon glyphicon glyphicon-plus"></i></a>
																<a href="#" id="vol-minus" style="color: red; display: none"><i class="ace-icon glyphicon glyphicon-minus"></i></a>
															</li>
														</ul>
													</div>
												</div>
												<div class='form-group'>
													<label class='col-sm-3'><b>其他参数</b></label>
													<div class='col-sm-9'>
														<ul class="params" id="params" style="list-style-type: none; margin: 0px">
															<li class="param">
																<div class="select-con" style="width:46%;float:left;height:30px">
																	<select class="dropdown-select param-meter" id="meter" name="meter" style="width:100%;">
																		<option value='0'>启动参数</option>
																	</select>&nbsp;&nbsp;
																</div>  
																<input class="short-input" type="text" name="param_value" id="param_value" placeholder="输入参数值..." value=""
																style="width: 46%;margin-left:11px;">
																<a href="#" id="param-plus" style="color: green"><i class="ace-icon glyphicon glyphicon-plus"></i></a>
																<a href="#" id="param-minus" style="color: red; display: none"><i class="ace-icon glyphicon glyphicon-minus"></i></a>
															</li>
														</ul>
													</div>
												</div>
												<div class='form-group'>
													<label class='col-sm-3'><b>启动命令</b></label>
													<div class='col-sm-9'>
														<input class="short-input" name="app_command" id="app_command" placeholder="应用实例启动命令" type="text" value="" style="width:100%"/>
													</div>
												</div>
											</div>
										</div>
									</form>
								</div>
							</div>
						</div>
						<div class="step-pane" id="modal-step2">
							<div class="left">
								<div class="well" style="margin-top: 1px; margin-left:20px">
									<form class='form-horizontal' role='form' id='app_env_form'>
										<div class='form-group'>
											<label class='col-sm-3'><b><font color="red">*</font>环境约束</b></label>
											<div class='col-sm-9'>
												<div class="item">
													<div class="left">
														<a href="#" class="list-group-item active">备选环境</a>
														<div class="block">
															<div class="btn-group-vertical" id="blockEnv">
															 </div>
														</div>
													</div>
													<div class="operate">
														<button type="button" class="btn btn-purple btn-sm btn-round" id="add-env">
															<span class="glyphicon glyphicon-forward"></span>&nbsp;增加
														</button>
														<button type="button" class="btn btn-grey btn-sm btn-round" id="remove-env" style="margin-top: 10px">
															<span class="glyphicon glyphicon-backward"></span>&nbsp;&nbsp;移除
														</button>
													</div>
													<div class="right">
														<a href="#" class="list-group-item active">已选环境 </a>
														<div class="block">
															<div class="btn-group-vertical" id="activateEnv"></div>
														</div>
													</div>
												</div> 
											</div>
										</div>
										<div class='form-group'>
											<label class='col-sm-3'><b><font color="red">*</font>集群约束</b></label>
											<div class='col-sm-9'>
												<div class="item">
													<div class="left">
														<a href="#" class="list-group-item active">备选集群</a>
														<div class="block">
															<div class="btn-group-vertical" id="blockCluster">
															</div>
														</div>
													</div>
													<div class="operate">
														<button type="button" class="btn btn-purple btn-sm btn-round" id="add-cluster">
															<span class="glyphicon glyphicon-forward"></span>&nbsp;增加
														</button>
														<button type="button" class="btn btn-grey btn-sm btn-round" id="remove-cluster" style="margin-top: 10px">
															<span class="glyphicon glyphicon-backward"></span>&nbsp;&nbsp;移除
														</button>
													</div>
													<div class="right">
														<a href="#" class="list-group-item active">已选集群 </a>
														<div class="block" >
															<div class="btn-group-vertical" id="activeCluster">
															</div>
														</div>
													</div>
												</div> 
											</div>
										</div>
										<div class='form-group'>
											<label class='col-sm-3'><b>资源约束</b></label>
											<div class='col-sm-9' style="margin-top:-5px">
												<label class="control-label bolder blue" style="float:left">CPU</label>
												<div class="radio">
													<label>
														<input name="cpu-radio" type="radio" class="ace" value="1" />
														<span class="lbl"> 1核</span>
													</label>
													<label>
														<input name="cpu-radio" type="radio" class="ace" value="2" />
														<span class="lbl"> 2核</span>
													</label>
													<label>
														<input name="cpu-radio" type="radio" class="ace" value="4" />
														<span class="lbl"> 4核</span>
													</label>
													<label>
														<input name="cpu-radio" type="radio" class="ace" value="8" />
														<span class="lbl"> 8核</span>
													</label>
													<label>
														<input name="cpu-radio" type="radio" class="ace" value="16" />
														<span class="lbl"> 16核</span>
													</label>
													<label>
														<input name="cpu-radio" type="radio" class="ace" value="0" checked="true" />
														<span class="lbl"> 无限制</span>
													</label>
												</div>
												<label class="control-label bolder blue" style="float:left">内存</label>
												<div class="radio">
													<label style="padding-left:19px">
														<input name="mem-radio" type="radio" class="ace" value="1" />
														<span class="lbl"> 1G</span>
													</label>
													<label style="padding-left:25px">
														<input name="mem-radio" type="radio" class="ace" value="2" />
														<span class="lbl"> 2G</span>
													</label>
													<label style="padding-left:23px">
														<input name="mem-radio" type="radio" class="ace" value="4" />
														<span class="lbl"> 4G</span>
													</label>
													<label style="padding-left:25px">
														<input name="mem-radio" type="radio" class="ace" value="8" />
														<span class="lbl"> 8G</span>
													</label>
													<label style="padding-left:25px">
														<input name="mem-radio" type="radio" class="ace" value="16" />
														<span class="lbl"> 16G</span>
													</label>
													<label style="padding-left:25px">
														<input name="mem-radio" type="radio" class="ace" value="0" checked="true" />
														<span class="lbl"> 无限制</span>
													</label>
												</div>
											</div>
										</div>
									</form>
								</div>
							</div>
						</div>
						<div class="step-pane" id="modal-step3">
							<div class="left">
								<div class="well" style="margin-top: 1px; margin-left:20px">
									<form class='form-horizontal' role='form' id='app_other_form'>
										<div class='form-group'>
											<label class='col-sm-3'><b>监测代理</b></label>
											<div class='col-sm-9'>
												<select class="dropdown-select" id="app_proxy_ip" name="app_proxy_ip" style="width: 100%">
													<option value="0">请选择检测代理</option>
												</select>
											</div>
										</div>
										<div class="form-group">
											<label class='col-sm-3'><b>健康检查</b></label>
											<div class="col-sm-9" style="margin-top:4px">
												<label>
													<input name="switch-health" id="app_health" class="ace ace-switch ace-switch-4" type="checkbox" />
													<span class="lbl"></span>
												</label>
											</div>
										</div>
										<div class="form-group">
											<label class='col-sm-3'><b>监控开关</b></label>
											<div class="col-sm-9" style="margin-top:4px">
												<label>
													<input name="switch-monitor" id="app_monitor" class="ace ace-switch ace-switch-4" type="checkbox" />
													<span class="lbl"></span>
												</label>
											</div>
										</div>
									</form>
								</div>
							</div>
						</div>

						<div class="step-pane" id="modal-step4">
							<div class="left">
								<div class="well" id="app_info" style="margin-top: 1px; margin-left:20px">
								</div>
							</div>
						</div>
					</div>

					<div class="modal-footer wizard-actions">
						<button class="btn btn-sm btn-round btn-prev">
							<i class="ace-icon fa fa-arrow-left"></i>
							上一步
						</button>

						<button class="btn btn-success btn-round btn-sm btn-next" data-last="创建">
							下一步
							<i class="ace-icon fa fa-arrow-right icon-on-right"></i>
						</button>

						<button class="btn btn-danger btn-round btn-sm pull-left" id="add_cancel" data-dismiss="modal">
							<i class="ace-icon fa fa-times"></i>
							取消
						</button>
					</div>
				</div>
			</div>
		</div>
		<!-- create application end -->
		
		<%--advanced search modal begin --%>
	<div class="modal fade" id="advanSearchApplicationModal" tabindex="-1"
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
							id='advanced_search_application_frm'>
							<div class="item">
								<ul class="app_params" id="app_params"
									style="list-style-type: none; margin: 0px 0px 10px 0px;">
									<li class="app_params" style="margin-top: 10px;">
										<div class="select-con"
											style="height: 33px; width: 100px; float: left;">
											<select class="dropdown-select param-meter" name="app_meter"
												id="app_meter">
												<option value='0'>请选条件</option>
												<option value='1'>应用名称</option>
												<option value='2'>访问路径</option>
												<option value='3'>应用描述</option>
											</select>
										</div> <input class="short-input" type="text" name="app_param_value"
										id="app_param_value" placeholder="输入查询值..." value=""
										style="width: 73%; border: 1px solid #ccc; height: 30px">
										<a href="#" id="app_remove-param" style="display: none;">
											<span class="glyphicon glyphicon-remove delete-param"></span>
									</a>
									</li>
								</ul>
								<a class="btn btn-primary" id="app_add-param" type="button"
									style="color: #fff; width: 87px;">
									<div style="margin: -7px -7px -7px -10px;">
										<span class="glyphicon glyphicon-plus"></span> <span
											class="text">添加条件</span>
									</div>
								</a>
							</div>
						</form>

						<div class="modal-footer">
							<button id="app_advanced_cancel" type="button"
								class="btn btn-danger btn-round">取消</button>
							<button id="app_advanced_search" type="button"
								class="btn btn-success btn-round">查询</button>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<%--advanced search modal end --%>
	</div>
</body>
</html>

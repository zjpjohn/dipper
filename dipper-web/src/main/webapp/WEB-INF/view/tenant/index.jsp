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
<!-- 添加额外的CSS样式表 -->
<link rel="stylesheet"
	href="${basePath}ace/assets/css/jquery-ui.custom.min.css" />
<link rel="stylesheet" href="${basePath}ace/assets/css/chosen.css" />
<link rel="stylesheet" href="${basePath}ace/assets/css/datepicker.css" />
<link rel="stylesheet"
	href="${basePath}ace/assets/css/ace-skins.min.css" />
<link rel="stylesheet" href="${basePath}ace/assets/css/ace-rtl.min.css" />
<link rel="stylesheet" href="${basePath}css/user/app.css" />
<!-- 添加额外的CSS样式表 -->
<script src="${basePath }ace/assets/js/jquery.validate.min.js"></script>
<script src="${basePath }js/bootstrap-paginator.min.js"></script>
<script src="${basePath }ace/assets/js/fuelux/fuelux.wizard.min.js"></script>
<!-- 添加弹出的提起选择框支持 -->
<script
	src="${basePath }ace/assets/js/date-time/bootstrap-datepicker.min.js"></script>
<script src="${basePath }ace/assets/js/date-time/daterangepicker.min.js"></script>
<script src="${basePath }js/console/tenant.js"></script>
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
			<jsp:param value="tenant_admin" name="page_index" />
		</jsp:include>
		<div class="main-content">
			<div class="main-content-inner">
				<div class="breadcrumbs" id="breadcrumbs">
					<ul class="breadcrumb">
						<li><i class="ace-icon fa fa-home home-icon"></i><a
							href="${basePath}index.html"><strong>首页</strong></a></li>
						<li class="active"><i class="ace-icon fa fa-university"></i>
							<b>租户管理</b></li>
					</ul>
				</div>
				<div class="page-header">
					<h1>
						欢迎来到租户管理 <small> <br>租户是向独立部门或者用户分配的封闭资源,租户管理提供划分、编辑和删除的功能。详情查看“使用文档”
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
								<c:if test="${fn:contains(authButton,'registryCreate')}">
									<button class="btn btn-sm btn-success btn-round"
										data-toggle="modal" onclick="showCreateModal()">
										<i class="ace-icon fa fa-pencil-square-o bigger-125"></i>&nbsp;<b>创建租户</b>
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
												class="form-control search-query" placeholder="请输入租户名称模糊查询">
											<span class="input-group-btn">
												<button type="button"
													class="btn btn-primary btn-round btn-sm"
													onclick="SearchTenants()">
													查找 <i
														class="ace-icon fa fa-search icon-on-right bigger-110"></i>
												</button>
											</span> <span class="input-group-btn"> &nbsp; </span> <span
												class="input-group-btn">
												<button type="button"
													class="btn btn-warning btn-round btn-sm"
													onclick="AdvancedSearchTenant()">
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
									<table id="tenant_list"></table>
									<div id="tenant_page"></div>
								</div>
							</c:if>
						</div>
					</div>
				</div>
			</div>
		</div>

		<%--create tenant modal begin --%>
		<div id="tenant-wizard" class="modal" data-backdrop="static"
			data-keyboard="false">
			<div class="modal-dialog" style="width: 800px">
				<div class="modal-content">
					<div class="modal-header" data-target="#modal-step-contents">
						<ul class="wizard-steps">
							<li data-target="#modal-step1" class="active"><span
								class="step">1</span> <span class="title">基本信息</span></li>

							<li data-target="#modal-step2"><span class="step">2</span> <span
								class="title">包含集群</span></li>

							<li data-target="#modal-step3"><span class="step">3</span> <span
								class="title">创建管理员</span></li>

							<li data-target="#modal-step4"><span class="step">4</span> <span
								class="title">信息汇总</span></li>
						</ul>
					</div>
					<div class="modal-body step-content" id="modal-step-contents">
						<!-- 步骤1：保存租户名称、起止日期、资源配置等基础信息 -->
						<div class="step-pane active" id="modal-step1">
							<div class="left">
								<div class="well" style="margin-top: 1px; margin-left: 5px">
									<form class='form-horizontal' role='form' id='basicInfoForm'>
										<div class='form-group'>
											<input class="form-control" id="tenant_id" name="tenant_id"
												value="0" type='hidden' /> <label class='col-sm-3'><b>租户名称：</b></label>
											<div class='col-sm-9'>
												<input class="form-control" id="tenant_name"
													name="tenant_name" type='text' placeholder="请输入租户名称" />
											</div>
										</div>
										<div class='form-group'>
											<label class="col-sm-3"><b>起止日期：</b></label>
											<div class="col-sm-9">
												<div class="input-daterange input-group">
													<input class="input-xm form-control" id="tenant_start"
														name="tenant_start" /> <span class="input-group-addon">
														<i class="fa fa-exchange"></i>
													</span> <input class="input-xm form-control " id="tenant_end"
														name="tenant_end" />
												</div>
											</div>
										</div>

										<div class='form-group'>
											<label class='col-sm-3'><b>租户描述：</b></label>
											<div class='col-sm-9'>
												<textarea class="form-control" id="tenant_desc"
													name="tenant_desc" rows="3" placeholder="请输入仓库的描述信息。"></textarea>
											</div>
										</div>
									</form>
								</div>
							</div>
						</div>
						<!-- 步骤2：为租户初始化镜像和集群的分配信息 -->
						<div class="step-pane" id="modal-step2">
							<div class="left">
								<div class="well" style="margin-top: 1px; margin-left: 20px">
									<form class='form-horizontal' role='form' id='icInfoForm'>
										<div class='form-group'>
											<label class='col-sm-3'><b><font color="red">*</font>选择集群</b></label>
											<div class='col-sm-9'>
												<div class="item">
													<div class="leftusershow">
														<a href="#" class="list-group-item active">备选集群</a>
														<div class="blockUserShow">
															<div class="btn-group-vertical" id="blockCluster">
															</div>
														</div>
													</div>
													<div class="operate">
														<button type="button"
															class="btn btn-purple btn-sm btn-round" id="add-cluster">
															<span class="glyphicon glyphicon-forward"></span>&nbsp;增加
														</button>
														<button type="button"
															class="btn btn-grey btn-sm btn-round" id="remove-cluster"
															style="margin-top: 10px">
															<span class="glyphicon glyphicon-backward"></span>&nbsp;&nbsp;移除
														</button>
													</div>
													<div class="rightusershow">
														<a href="#" class="list-group-item active">已选集群 </a>
														<div class="blockUserShow">
															<div class="btn-group-vertical" id="activeCluster">
															</div>
														</div>
													</div>
												</div>
											</div>
										</div>
									</form>
								</div>
							</div>
						</div>
						<!-- 步骤3：将租户资源与平台中的管理员用户进行绑定 -->
						<div class="step-pane" id="modal-step3">
							<div class="left">
								<div class="well" id="user_info"
									style="margin-top: 1px; margin-left: 5px">
									<form class='form-horizontal' role='form' id='userInfoForm'>
										<div class='form-group'>
											<label class='col-sm-3'><b>管理员：</b></label>
											<div class='col-sm-9'>
												<input class="form-control" id="managerName"
													name="managerName" type='text'
													placeholder="包含中文、英文等超过32个字符的名称" />
											</div>

										</div>
										<div class='form-group'>
											<label class='col-sm-3'><b>电邮地址：</b></label>
											<div class='col-sm-9'>
												<input class="form-control" id="eMail" name="eMail"
													type='text' placeholder="例如：abc@xyz.com" />
											</div>
										</div>
										<div class='form-group'>
											<label class='col-sm-3'><b>移动电话：</b></label>
											<div class='col-sm-9'>
												<input class="form-control" id="phoneNumber"
													name="phoneNumber" type='text' placeholder="包含11位数字。" />
											</div>
										</div>
										<div class='form-group'>
											<label class='col-sm-3'><b>所在公司：</b></label>
											<div class='col-sm-9'>
												<input class="form-control" id="companyName"
													name="companyName" type='text' placeholder="请输入所属公司名称" />
											</div>
										</div>
									</form>
								</div>
							</div>
						</div>
						<!-- 步骤4：展示用户所作的全部操作信息结果 -->
						<div class="step-pane" id="modal-step4">
							<div class="left">
								<div class="well" id="tenant_info"
									style="margin-top: 1px; margin-left: 20px"></div>
							</div>
						</div>
					</div>

					<div class="modal-footer wizard-actions">
						<button class="btn btn-sm btn-round btn-prev">
							<i class="ace-icon fa fa-arrow-left"></i> 上一步
						</button>

						<button class="btn btn-success btn-round btn-sm btn-next"
							data-last="创建租户">
							下一步 <i class="ace-icon fa fa-arrow-right icon-on-right"></i>
						</button>

						<button class="btn btn-danger btn-round btn-sm pull-left"
							id="add_cancel" data-dismiss="modal">
							<i class="ace-icon fa fa-times"></i> 取消
						</button>
					</div>
				</div>
			</div>
		</div>
		<%--create tenant modal end --%>

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

		<script type="text/javascript"
			src="${basePath }ace/assets/js/date-time/locales/bootstrap-datepicker.zh-CN.js"
			charset="UTF-8"></script>
		<script type="text/javascript">
			$(function() {
				$('.input-daterange').datepicker({
					language : 'zh-CN',
					autoclose : true,
					todayHighlight : true
				});
			});
		</script>

	</div>
</body>
</html>

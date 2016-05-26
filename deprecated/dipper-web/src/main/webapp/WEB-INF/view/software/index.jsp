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
<link rel="stylesheet" href="${basePath}css/user/app.css" />
<!-- 添加额外的CSS样式表 -->
<script src="${basePath }ace/assets/js/jquery.validate.min.js"></script>
<script src="${basePath }ace/assets/js/fuelux/fuelux.wizard.min.js"></script>
<!-- 添加弹出的提起选择框支持 -->
<script src="${basePath }js/console/software.js"></script>
<c:set var="authButton" value='${buttonsAuth}'></c:set>
</head>
<body class="no-skin">
	<jsp:include page="../header.jsp"></jsp:include>
	<c:if test="${fn:contains(authButton,'softwareDelete')}">
		<input type="hidden" id="delete_software">
	</c:if>
	<c:if test="${fn:contains(authButton,'softwareUpdate')}">
		<input type="hidden" id="update_software">
	</c:if>
	<div class="main-container" id="main-container">
		<script type="text/javascript">
			try {
				ace.settings.check('main-container', 'fixed')
			} catch (e) {
			}
		</script>
		<jsp:include page="../nav.jsp">
			<jsp:param value="software_admin" name="page_index" />
		</jsp:include>
		<div class="main-content">
			<div class="main-content-inner">
				<div class="breadcrumbs" id="breadcrumbs">
					<ul class="breadcrumb">
						<li><i class="ace-icon fa fa-home home-icon"></i><a
							href="${basePath}index.html"><strong>首页</strong></a></li>
						<li class="active"><i class="ace-icon fa fa-university"></i>
							<b>软件管理</b></li>
					</ul>
				</div>
				<div class="page-header">
					<h1>
						欢迎来到软件管理 <small> <br>软件管理提供向指定的多台主机同时安装软件的功能，详情查看“使用文档”。
							<br>（1）“<b>安装软件</b>”操作之前需要在软件列表行首点击选择。
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
								<c:if test="${fn:contains(authButton,'softwareInstall')}">
									<button class="btn btn-sm btn-success btn-round"
										data-toggle="modal" onclick="showInstallModal()">
										<i class="ace-icon fa fa-sitemap bigger-125"></i>&nbsp;<b>安装软件</b>
									</button>
								</c:if>
								<c:if test="${fn:contains(authButton,'softwareInsert')}">
									<button class="btn btn-sm btn-warning btn-round"
										data-toggle="modal" onclick="showCreateModal()">
										<i class="ace-icon fa fa-pencil-square-o bigger-125"></i>&nbsp;<b>新增软件</b>
									</button>
								</c:if>
								<button class="btn btn-sm btn-danger btn-round"
									onclick="window.location.href='/software/index.html'">
									<i class="ace-icon fa fa-list-alt bigger-125"></i><b>全部显示</b>
								</button>
								<%-- 
								<div class="btn-group">
									<button data-toggle="dropdown"
										class="btn btn-sm btn-round btn-primary dropdown-toggle">
										<i class="ace-icon fa fa-wrench  icon-only"></i> <b>更多操作</b> <i
											class="ace-icon fa fa-angle-down icon-on-right"></i>
									</button>
									<ul class="dropdown-menu dropdown-primary dropdown-menu-right">
										<c:if test="${fn:contains(authButton,'softwareDelete')}">
											<li><a class="glyphicon glyphicon-trash" href="#"
												onclick="batchRemoveRegistrys()">批量删除</a></li>
										</c:if>
									</ul>
								</div>
								--%>
								<div class="col-xs-12 col-sm-4" style="float: right">
									<c:if test="${fn:contains(authButton,'softwareList')}">
										<div class="input-group">
											<input type="text" id="searchSoftName"
												class="form-control search-query" placeholder="请输入软件名称模糊查询">
											<span class="input-group-btn">
												<button type="button"
													class="btn btn-primary btn-round btn-sm"
													onclick="SearchSofts()">
													查找 <i
														class="ace-icon fa fa-search icon-on-right bigger-110"></i>
												</button>
											</span> <span class="input-group-btn"> &nbsp; </span> <span
												class="input-group-btn">
												<button type="button"
													class="btn btn-warning btn-round btn-sm"
													onclick="AdSchSoft()">
													更多 <i
														class="ace-icon fa fa-search icon-on-right bigger-110"></i>
												</button>
											</span>
										</div>
									</c:if>
								</div>
							</div>
							<c:if test="${fn:contains(authButton,'softwareList')}">
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

		<%--install software modal begin --%>
		<div id="tenant-wizard" class="modal" data-backdrop="static"
			data-keyboard="false">
			<div class="modal-dialog" style="width: 800px">
				<div class="modal-content">
					<div class="modal-header" data-target="#modal-step-contents">
						<ul class="wizard-steps">
							<li data-target="#modal-step1" class="active"><span
								class="step">1</span> <span class="title">选择集群主机</span></li>

							<li data-target="#modal-step2"><span class="step">2</span> <span
								class="title">信息汇总确认</span></li>
						</ul>
					</div>
					<div class="modal-body step-content" id="modal-step-contents">
						<!-- 步骤1：用户选择集群后，集群下对应的主机列表显示，之后勾选需要安装的目标主机 -->
						<div class="step-pane active" id="modal-step1">
							<div class="left">
								<div class="well" style="margin-top: 1px; margin-left: 5px">
									<form class='form-horizontal' role='form' id='clusterForm'>
										<div class='form-group'>
											<label class="col-sm-3"><b>目标集群：</b></label>
											<div class="col-sm-9">
												<select id="target_cluster" name="target_cluster"
													class="form-control">
													<option value="0">请选择需要安装软件的目标集群</option>
												</select>
											</div>
										</div>
										<div class='form-group'>
											<label class='col-sm-3'><b><font color="red"></font>选择主机：</b></label>
											<div class='col-sm-9'>
												<div class="item">
													<div class="leftusershow">
														<a href="#" class="list-group-item active">备选主机</a>
														<div class="blockUserShow">
															<div class="btn-group-vertical" id="blockHost"></div>
														</div>
													</div>
													<div class="operate">
														<button type="button"
															class="btn btn-purple btn-sm btn-round" id="add-host">
															<span class="glyphicon glyphicon-forward"></span>&nbsp;增加
														</button>
														<button type="button"
															class="btn btn-grey btn-sm btn-round" id="remove-host"
															style="margin-top: 10px">
															<span class="glyphicon glyphicon-backward"></span>&nbsp;&nbsp;移除
														</button>
													</div>
													<div class="rightusershow">
														<a href="#" class="list-group-item active">已选主机 </a>
														<div class="blockUserShow">
															<div class="btn-group-vertical" id="activeHost"></div>
														</div>
													</div>
												</div>
											</div>
										</div>
									</form>
								</div>
							</div>
						</div>
						<!-- 步骤2：展示用户所作的全部操作信息结果 -->
						<div class="step-pane" id="modal-step2">
							<div class="left">
								<div class="well" id="softwareInfo"
									style="margin-top: 1px; margin-left: 20px"></div>
							</div>
							<br> <br>
							<div id="pid" style="display: none;">
								<div style="" id="showId">已经完成0%</div>
								<div class="progress">
									<div id="progressid"
										class="progress-bar progress-bar-success progress-bar-striped"
										role="progressbar" aria-valuenow="40" aria-valuemin="0"
										aria-valuemax="100" style="width: 0%;">
										<span id="prog_span"></span>
									</div>
								</div>
							</div>
						</div>
					</div>

					<div class="modal-footer wizard-actions">
						<button class="btn btn-sm btn-round btn-prev" id="prevButton">
							<i class="ace-icon fa fa-arrow-left"></i> 上一步
						</button>

						<button class="btn btn-success btn-round btn-sm btn-next"
							data-last="开始安装" id="nextButton">
							下一步 <i class="ace-icon fa fa-arrow-right icon-on-right"></i>
						</button>

						<button class="btn btn-success btn-round" id="ensureButton"
							style="display: none;">
							<i class="ace-icon fa fa-cubes"></i>确定
						</button>

						<button class="btn btn-danger btn-round btn-sm pull-left"
							id="add_cancel" data-dismiss="modal">
							<i class="ace-icon fa fa-times"></i> 取消
						</button>
					</div>
				</div>
			</div>
		</div>
		<%--install soft modal end --%>

		<%--add software modal begin --%>
		<div class="modal fade" id="addSoftwareModal" tabindex="-1"
			role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close inssoft_cancel" data-dismiss="modal"
							aria-hidden="true">×</button>
						<h4 class="modal-title" id="myModalLabel">新增软件</h4>
					</div>
					<div class="modal-body">
						<form class='form-horizontal' role='form' id='add_soft_frm'>
							<div class='form-group'>
								<label class='col-sm-3'><b>软件名称:</b></label>
								<div class='col-sm-9'>
									<input class="form-control" id="soft_name" name="soft_name"
										type='text' placeholder="请输入软件名称" />
								</div>
							</div>
							<div class='form-group'>
								<label class="col-sm-3"><b>版本信息:</b></label>
								<div class="col-sm-9">
									<input class="form-control" id="soft_version"
										name="soft_version" type='text' placeholder="软件的版本信息，例如：1.2.3" />
								</div>
							</div>
							<div class='form-group'>
								<label class='col-sm-3'><b>所属类型:</b></label>
								<div class='col-sm-9'>
									<div class="radio">
										<label> <input name="add_soft_type" value="0"
											type="radio" class="ace" checked /> <span class="lbl">基础类型</span>
										</label> <label> <input name="add_soft_type" value="1"
											type="radio" class="ace" /> <span class="lbl">中间件类型</span>
										</label>
									</div>
								</div>
							</div>
							<div class='form-group'>
								<label class='col-sm-3'><b>yum标识:</b></label>
								<div class='col-sm-9'>
									<input class="form-control" id="soft_yum" name="soft_yum"
										type='text' placeholder="软件在yum中的标识字符串，例如：redis.x86_64" />
								</div>
							</div>
							<div class='form-group'>
								<label class='col-sm-3'><b>软件描述:</b></label>
								<div class='col-sm-9'>
									<textarea class="form-control" id="soft_desc" name="soft_desc"
										rows="3" placeholder="请输入软件的描述信息。"></textarea>
								</div>
							</div>
						</form>
					</div>
					<div class="modal-footer">
						<button type="button"
							class="btn btn-danger btn-round inssoft_cancel">取消</button>
						<button id="inssoft_submit" type="button"
							class="btn btn-success btn-round">提交</button>
					</div>
				</div>
			</div>
		</div>
		<%--add software modal begin --%>


		<%--modify software modal begin --%>
		<div class="modal fade" id="modifySoftwareModal" tabindex="-1"
			role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal"
							aria-hidden="true">×</button>
						<h4 class="modal-title" id="myModalLabel">修改软件</h4>
					</div>
					<div class="modal-body">
						<form class='form-horizontal' role='form' id='modify_soft_frm'>
							<div class='form-group'>
								<input class="form-control" id="soft_id_edit"
									name="soft_id_edit" type='hidden' /> <label class='col-sm-3'><b>软件名称:</b></label>
								<div class='col-sm-9'>
									<input class="form-control" id="soft_name_edit"
										name="soft_name_edit" type='text' readonly="readonly" />
								</div>
							</div>
							<div class='form-group'>
								<label class="col-sm-3"><b>版本信息:</b></label>
								<div class="col-sm-9">
									<input class="form-control" id="soft_version_edit"
										name="soft_version_edit" type='text' />
								</div>
							</div>
							<div class='form-group'>
								<label class='col-sm-3'><b>所属类型:</b></label>
								<div class='col-sm-9'>
									<div class="radio">
										<label> <input name="soft_type_radio" value="0"
											type="radio" class="ace" /> <span class="lbl">基础类型</span>
										</label> <label> <input name="soft_type_radio" value="1"
											type="radio" class="ace" /> <span class="lbl">中间件类型</span>
										</label>
									</div>
								</div>
							</div>
							<div class='form-group'>
								<label class='col-sm-3'><b>yum标识:</b></label>
								<div class='col-sm-9'>
									<input class="form-control" id="soft_yum_edit"
										name="soft_yum_edit" type='text' />
								</div>
							</div>
							<div class='form-group'>
								<label class='col-sm-3'><b>软件描述:</b></label>
								<div class='col-sm-9'>
									<textarea class="form-control" id="soft_desc_edit"
										name="soft_desc_edit" rows="3" placeholder="请输入软件的描述信息。"></textarea>
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
		<%--modify software modal end --%>

		<%--advanced search modal begin --%>
		<div class="modal fade" id="adSchSoftModal" tabindex="-1"
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
													<option value='1'>软件名称</option>
													<option value='2'>版本编号</option>
													<option value='3'>yum标识</option>
													<option value='4'>描述信息</option>
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

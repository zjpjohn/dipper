<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<!DOCTYPE html>
<html lang="en">
<head>
<link rel="shortcut icon" href="${basePath }img/title_cloud.png" />
<title>容器管理</title>
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
<meta charset="utf-8" />
<meta name="description" content="overview &amp; stats" />
<meta name="viewport"
	content="width=device-width, initial-scale=1.0, maximum-scale=1.0" />
<jsp:include page="../js.jsp"></jsp:include>
<link rel="stylesheet" href="${basePath}css/user/container.css" />
<script src="${basePath }ace/assets/js/jquery.validate.min.js"></script>
<script src="${basePath }ace/assets/js/additional-methods.min.js"></script>
<script src="${basePath }ace/assets/js/jquery.maskedinput.min.js"></script>
<script src="${basePath }ace/assets/js/select2.min.js"></script>
<script src="${basePath }js/bootstrap-paginator.min.js"></script>
<script src="${basePath }ace/assets/js/fuelux/fuelux.wizard.min.js"></script>
<script src="${basePath }js/console/container.js"></script>
<c:set var="authButton" value='${buttonsAuth}'></c:set>

</head>
<body class="no-skin">
	<jsp:include page="../header.jsp"></jsp:include>
	<c:if test="${fn:contains(authButton,'containerTrash')}">
		<input type="hidden" id="delete_container">
	</c:if>
	<c:if test="${fn:contains(authButton,'containerStop')}">
		<input type="hidden" id="stop_container">
	</c:if>
	<c:if test="${fn:contains(authButton,'containerStart')}">
		<input type="hidden" id="start_container">
	</c:if>
	<c:if test="${fn:contains(authButton,'containerSync')}">
		<input type="hidden" id="sync_container">
	</c:if>
	
	<div class="main-container" id="main-container">
		<script type="text/javascript">
			try {
				ace.settings.check('main-container', 'fixed')
			} catch (e) {
			}
		</script>
		<jsp:include page="../nav.jsp">
			<jsp:param value="container_admin" name="page_index" />
		</jsp:include>
		<div class="main-content">
			<div class="main-content-inner">
				<div class="breadcrumbs" id="breadcrumbs">
					<ul class="breadcrumb">
						<li><i class="ace-icon fa fa-home home-icon"></i> <a
							href="${basePath}index.html"><strong>首页</strong></a></li>
						<li class="active"><b>容器管理</b></li>
					</ul>
				</div>
				<div class="page-header">
				   <h1>欢迎来到容器管理 
				   	<small>
				   		<br>容器是发布平台中所有容器的简称，容器管理提容器的创建、启动、停止、删除、同步等功能。详情查看“使用文档”
				   	</small>
				   </h1>
				</div>
				<div class="page-content">
					<div class="row">
						<div class="col-xs-12">
							<div class="well well-sm">
								<a href="#modal-wizard" data-toggle="modal"
									class="btn btn-sm btn-success btn-round"> <i
									class="ace-icon fa fa-inbox bigger-125"></i> <b>创建容器</b>
								</a>
								<div class="btn-group">
									<button data-toggle="dropdown"
										class="btn btn-primary btn-round btn-sm dropdown-toggle">
										<i class="ace-icon fa fa-wrench  bigger-110 icon-only"></i> <b>更多操作</b>
										<i class="ace-icon fa fa-angle-down icon-on-right"></i> <input
											id="conIds" type="hidden">
									</button>
									<ul class="dropdown-menu dropdown-primary dropdown-menu-left">
										<li><a class="btn-forbidden" id="start"><span
												class="glyphicon glyphicon-play"></span>&nbsp;批量启动</a></li>
										<li><a class="btn-forbidden" id="stop"><span
												class="glyphicon glyphicon-stop"></span>&nbsp;批量停止</a></li>
										<li><a class="btn-forbidden" id="trash"><span
												class="glyphicon glyphicon-trash"></span>&nbsp;批量删除</a></li>
										<li><a id="sync"><span
												class="glyphicon glyphicon-repeat"></span>&nbsp;容器同步</a></li>
									</ul>
								</div>
								<div class="col-xs-12 col-sm-4" style="float: right">
									<div class="input-group">
										<input id="search_name" type="text"
											class="form-control search-query" placeholder="名称"> <span
											class="input-group-btn">
											<button id="search" type="button"
												class="btn btn-primary btn-round btn-sm">
												查找 <i class="ace-icon fa fa-search icon-on-right bigger-110"></i>
											</button>
										</span> <span class="input-group-btn"> &nbsp; </span> <span
											class="input-group-btn">
											<button id="detailSearch" type="button"
												class="btn btn-warning btn-round btn-sm"
												onclick="AdvancedSearchContainer()">
												更多 <i class="ace-icon fa fa-search icon-on-right bigger-110"></i>
											</button>
										</span>
									</div>
								</div>
							</div>
							<div>
								<table id="container_list"></table>
								<div id="container_page"></div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<div id="modal-wizard" class="modal">
		<div class="modal-dialog" style="width:700px">
			<div class="modal-content">
				<div class="modal-header" data-target="#modal-step-contents">
					<ul class="wizard-steps">
						<li data-target="#modal-step1" class="active"><span
							class="step">1</span> <span class="title">镜像配置</span></li>
						<li data-target="#modal-step2"><span class="step">2</span> <span
							class="title">基本配置</span></li>
						<li data-target="#modal-step3"><span class="step">3</span> <span
						class="title">启动模板配置</span></li>
						<li data-target="#modal-step4"><span class="step">4</span> <span
							class="title">参数配置</span></li>
					</ul>
				</div>

				<div class="modal-body step-content" id="modal-step-contents">
					<div class="step-pane active" id="modal-step1">
						<div class="left">
							<div class="item">
								<label>选择应用：</label> <select class="dropdown-select"
									id="app_select" name="app_select" style="width: 81%">
									<option value="0">请选择应用</option>
								</select>
							</div>
							<div class="item">
								<label>应用镜像列表: </label>
								<div class="toolbar-right" style="float: right">
									<table>
										<tr>
											<td>页数&nbsp;<a id="currentPtpl"></a>&nbsp;/&nbsp;<a
												id="totalPtpl"></a></td>
											<td style="padding: 0px 10px 0px 10px">
												<div class="pagination-small">
													<ul class="pagination" id="tplpage" style="display: inline">10
													</ul>
													<input id="currentPage" type="hidden">
												</div>
											</td>
										</tr>
									</table>
								</div>
							</div>
							<div class="imagelist" id="imagelist"
								style="margin: 10px 40px 0px 58px"></div>
						</div>
					</div>

					<div class="step-pane" id="modal-step2">
						<div class="left">
							<div class="item">
								<label>选择集群：</label> <select class="dropdown-select"
									id="cluster_select" name="cluster_select" style="width: 81%">
									<option value="0">请选择集群</option>
								</select>
							</div>
							<div class="item">
								<label>创建方式：</label> <select class="dropdown-select"
									id="create_mode" name="create_mode" style="width: 81%">
									<option value="0">创建不启动</option>
									<option value="1">创建并启动</option>
								</select>
							</div>
							<div class="item">
								<label>容器数量：</label> <input type="text" id="container_num"
									name="container_num" placeholder="输入创建容器数量..." value=""
									style="width: 81%; border: 1px solid #ccc; height: 30px">
							</div>
							<div class="item">
								<label>容器名称：</label> <input type="text" id="container_name"
									name="container_name" placeholder="输入容器名称..." value=""
									style="width: 81%; border: 1px solid #ccc; height: 30px">
							</div>
							<div class="item">
								<label style="float: left">备注信息：</label>
								<textarea id="container_desc" name="container_desc"
									placeholder="备注信息..." value=""
									style="width: 81%; border: 1px solid #ccc; margin-left: 3px"
									rows="3"></textarea>
							</div>
						</div>
					</div>

					<div class="step-pane" id="modal-step3">
						<div class="left">
							<div class="item">
								<label>选择模板：</label> <select class="dropdown-select"
									id="temp_select" name="temp_select" style="width: 81%">
								</select>
							</div>
							<div class="item" id="temp_param_label" style="display:none">
								<label>参数名称</label> <label style="margin-left: 42px;">参数值</label>
							</div>
							<div class="item" id="temp_param" style="display:none">
								<ul class="params" id="temp_params"
									style="list-style-type: none; margin: 0px 0px 10px 0px;">
								</ul>
							</div>
						</div>
					</div>

					<div class="step-pane" id="modal-step4">
						<div class="left">
							<div class="item">
								<label>参数名称</label> <label style="margin-left: 42px;">参数值</label>
							</div>
							<div class="item">
								<ul class="params" id="params"
									style="list-style-type: none; margin: 0px 0px 10px 0px;">
									<li class="param" style="margin-top: 10px;">
										<div class="select-con"
											style="height: 33px; width: 100px; float: left;">
											<select class="dropdown-select param-meter" name="meter"
												id="meter">
												<option value='0'>启动参数</option>
											</select>&nbsp;&nbsp;
										</div>  <input class="short-input" type="text" name="param_value"
										id="param_value" placeholder="输入参数值..." value=""
										style="width: 73%; border: 1px solid #ccc; height: 30px">
										<a href="#" id="remove-param" style="display: none;"> <span
											class="glyphicon glyphicon-remove delete-param"></span>
									</a>
									</li>
								</ul>
								<a class="btn btn-primary" id="add-param" type="button"
									style="color: #fff; width: 87px;">
									<div style="margin: -7px -7px -7px -10px">
										<span class="glyphicon glyphicon-plus"></span> <span
											class="text">添加参数</span>
									</div>
								</a>
							</div>
						</div>
					</div> 
				</div>

				<div class="modal-footer wizard-actions">
					<button class="btn btn-sm btn-round btn-prev">
						<i class="ace-icon fa fa-arrow-left"></i> 上一步
					</button>
					<button class="btn btn-success btn-round btn-sm btn-next"
						data-last="创建">
						下一步 <i class="ace-icon fa fa-arrow-right icon-on-right"></i>
					</button>
					<button class="btn btn-danger btn-round btn-sm pull-left"
						id="cancel" data-dismiss="modal">
						<i class="ace-icon fa fa-times"></i> 取消
					</button>
				</div>
			</div>
		</div>
	</div>

	<%--advanced search modal begin --%>
	<div class="modal fade" id="advanSearchContainerModal" tabindex="-1"
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
							id='advanced_search_container_frm'>
							<div class="item">
								<ul class="con_params" id="con_params"
									style="list-style-type: none; margin: 0px 0px 10px 0px;">
									<li class="con_params" style="margin-top: 10px;">
										<div class="select-con"
											style="height: 33px; width: 100px; float: left;">
											<select class="dropdown-select param-meter" name="con_meter"
												id="con_meter">
												<option value='0'>请选条件</option>
												<option value='1'>UUID</option>
												<option value='2'>容器名称</option>
												<option value='3'>运行状态</option>
												<option value='4'>备注信息</option>
											</select>
										</div> <input class="short-input" type="text" name="con_param_value"
										id="con_param_value" placeholder="输入查询值..." value=""
										style="width: 73%; border: 1px solid #ccc; height: 30px">
										<a href="#" id="con_remove-param" style="display: none;">
											<span class="glyphicon glyphicon-remove delete-param"></span>
									</a>
									</li>
								</ul>
								<a class="btn btn-primary" id="con_add-param" type="button"
									style="color: #fff; width: 87px;">
									<div style="margin: -7px -7px -7px -10px;">
										<span class="glyphicon glyphicon-plus"></span> <span
											class="text">添加条件</span>
									</div>
								</a>
							</div>
						</form>

						<div class="modal-footer">
							<button id="con_advanced_search" type="button"
								class="btn btn-success btn-round">查询</button>
							<button id="con_advanced_cancel" type="button"
								class="btn btn-danger btn-round">取消</button>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<%--advanced search modal end --%>
</body>
</html>

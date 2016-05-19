<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<!DOCTYPE html>
<html lang="en">
<head>
<link rel="shortcut icon" href="${basePath }img/title_cloud.png" />
<title>应用发布</title>
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
<meta charset="utf-8" />
<meta name="description" content="overview &amp; stats" />
<meta name="viewport"
	content="width=device-width, initial-scale=1.0, maximum-scale=1.0" />
<jsp:include page="../js.jsp"></jsp:include>
<link rel="stylesheet" href="${basePath}css/user/apprelease.css" />
<script src="${basePath }ace/assets/js/jquery.validate.min.js"></script>
<script src="${basePath }ace/assets/js/additional-methods.min.js"></script>
<script src="${basePath }ace/assets/js/jquery.maskedinput.min.js"></script>
<script src="${basePath }ace/assets/js/select2.min.js"></script>
<script src="${basePath }js/bootstrap-paginator.min.js"></script>
<script src="${basePath }ace/assets/js/fuelux/fuelux.wizard.min.js"></script>
<script type="${basePath}ace/assets/js/uncompressed/fuelux/fuelux.spinner.js"></script>
<script src="${basePath }js/console/apprelease.js"></script>
<c:set var="authButton" value='${buttonsAuth}'></c:set>

</head>
<body class="no-skin">
	<jsp:include page="../header.jsp"></jsp:include>
	<div class="main-container" id="main-container">
		<script type="text/javascript">
			try {
				ace.settings.check('main-container', 'fixed')
			} catch (e) {
			}
		</script>
		<jsp:include page="../nav.jsp">
			<jsp:param value="pushapp_admin" name="page_index" />
		</jsp:include>
		<div class="main-content">
			<div class="main-content-inner">
				<div class="breadcrumbs" id="breadcrumbs">
					<ul class="breadcrumb">
						<li><i class="ace-icon fa fa-home home-icon"></i> <a
							href="${basePath}index.html"><strong>首页</strong></a></li>
						<li class="active"><b>应用发布</b></li>
					</ul>
				</div>
				<div class="page-header">
					<h1>
						欢迎来到应用发布 <small> <br>应用发布是平台的核心功能，通过选择应用基础环境，应用模板以及一些基础信息，能够快速方便的提供对外服务的应用。
						</small>
					</h1>
				</div>
				<input id="balanceId" type="hidden">
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
								<c:if test="${fn:contains(authButton,'applicationRelease')}">
									<a href="javascript:showReleaseModel();" data-toggle="modal"
										class="btn btn-sm btn-success btn-round"> <i
										class="ace-icon fa fa-inbox bigger-125"></i> <b>应用发布</b>
									</a>
								</c:if>
								<button class="btn btn-sm btn-danger btn-round"
									onclick="showAllAppRels()">
									<i class="ace-icon fa fa-list-alt bigger-125"></i><b>显示全部</b>
								</button>
								<div class="col-xs-12 col-sm-4" style="float: right;">
									<c:if test="${fn:contains(authButton,'appReleaseList')}">
										<div class="input-group">
											<input id="search_name" type="text"
												class="form-control search-query" placeholder="名称">
											<span class="input-group-btn">
												<button id="search" type="button"
													class="btn btn-primary btn-round btn-sm">
													查找 <i
														class="ace-icon fa fa-search icon-on-right bigger-110"></i>
												</button>
											</span> <span class="input-group-btn"> &nbsp; </span> <span
												class="input-group-btn">
												<button id="detailSearch" type="button"
													class="btn btn-warning btn-round btn-sm"
													onclick="AdvancedSearchAR()">
													更多 <i
														class="ace-icon fa fa-search icon-on-right bigger-110"></i>
												</button>
											</span>
										</div>
									</c:if>
								</div>
							</div>
							<div>
								<c:if test="${fn:contains(authButton,'appReleaseList')}">
									<table id="apprelease_list"></table>
									<div id="apprelease_page"></div>
								</c:if>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<div id="modal-wizard" class="modal">
		<div class="modal-dialog" style="width: 800px">
			<div class="modal-content">
				<div class="modal-header" data-target="#modal-step-contents">
					<ul class="wizard-steps">
						<li data-target="#modal-step1" class="active"><span
							class="step">1</span> <span class="title">发布环境</span></li>
						<li data-target="#modal-step2"><span class="step">2</span> <span
							class="title">发布信息</span></li>
						<li data-target="#modal-step3"><span class="step">3</span> <span
							class="title">发布汇总</span></li>
					</ul>
				</div>

				<div class="modal-body step-content" id="modal-step-contents">
					<div class="step-pane active" id="modal-step1">
						<div class="left">
							<div class="well" style="margin-top: 1px; margin-left: 20px">
								<form class='form-horizontal' role='form' id='release_env_form'>
									<div class='form-group'>
										<label class='col-sm-2'><b>发布应用</b></label>
										<div class='col-sm-10'>
											<input type="text" id="app_select" disabled class="disabled" style="width:90%">
										</div>
									</div>
									<div class='form-group'>
										<label class='col-sm-2'><b>应用版本</b></label>
										<div class='col-sm-10'>
											<input type="text" id="app_version" disabled class="disabled" style="width:90%">
										</div>
									</div>
									<div class='form-group'>
										<label class='col-sm-2'><b>发布环境</b></label>
										<div class='col-sm-10'>
											<select class="dropdown-select" id="env_select"
												name="env_select" style="width: 90%">
												<option value="0">请选择发布环境</option>
											</select>
										</div>
									</div>
									<!-- <div class='form-group'>
										<label class='col-sm-2'><b>发布应用</b></label>
										<div class='col-sm-10'>
											<select class="dropdown-select" id="app_select"
												name="app_select" style="width: 90%">
												<option value="0">请选择待发布的应用</option>
											</select>
										</div>
									</div>
									<div class='form-group'>
										<label class='col-sm-2'><b>应用版本</b></label>
										<div class='col-sm-10'>
											<div class="toolbar-right"
												style="float: right; margin-right: 46px">
												<table>
													<tr>
														<td>页数&nbsp;<a id="currentPtpl"></a>&nbsp;/&nbsp;<a
															id="totalPtpl"></a></td>
														<td style="padding: 0px 10px 0px 10px">
															<div class="pagination-small">
																<ul class="pagination" id="tplpage"
																	style="display: inline">10
																</ul>
																<input id="currentPage" type="hidden">
															</div>
														</td>
													</tr>
												</table>
											</div>
										</div>
									</div> 
									<div class="imagelist" id="imagelist"
										style="margin: 10px 40px 0px 38px; width: 86%"></div>-->
								</form>
							</div>
						</div>
					</div>

					<div class="step-pane" id="modal-step2">
						<div class="left">
							<div class="well" style="margin-top: 1px; margin-left: 20px">
								<form class='form-horizontal' role='form'
									id='release_basic_form'>
									<div class='form-group'>
										<label class='col-sm-2'><b>应用集群</b></label>
										<div class='col-sm-10'>
											<select class="dropdown-select" id="cluster_select"
												name="cluster_select" style="width: 90%">
												<option value="0">请选择集群</option>
											</select>
										</div>
									</div>
									<div class='form-group'>
										<label class='col-sm-2'><b>发布方式</b></label>
										<div class='col-sm-10'>
											<select class="dropdown-select" id="release_mode"
												name="release_mode" style="width: 90%">
												<option value="0">普通发布</option>
												<option value="1">灰度发布</option>
												<option value="2">特殊发布</option>
											</select>
										</div>
									</div>

									<div class='form-group' id="resource_form"
										style="display: none">
										<label class='col-sm-2'><b>资源约束</b></label>
										<div class='col-sm-10' style="margin-top: -5px">
											<label class="control-label bolder blue" style="float: left">CPU</label>
											<div class="radio">
												<label> <input name="cpu-radio" type="radio"
													class="ace" value="1" /> <span class="lbl"> 1核</span>
												</label> <label> <input name="cpu-radio" type="radio"
													class="ace" value="2" /> <span class="lbl"> 2核</span>
												</label> <label> <input name="cpu-radio" type="radio"
													class="ace" value="4" /> <span class="lbl"> 4核</span>
												</label> <label> <input name="cpu-radio" type="radio"
													class="ace" value="8" /> <span class="lbl"> 8核</span>
												</label> <label> <input name="cpu-radio" type="radio"
													class="ace" value="16" /> <span class="lbl"> 16核</span>
												</label> <label> <input name="cpu-radio" type="radio"
													class="ace" value="0" checked/> <span class="lbl"> 无限制</span>
												</label>
											</div>
											<label class="control-label bolder blue" style="float: left">内存</label>
											<div class="radio">
												<label style="padding-left: 19px"> <input
													name="mem-radio" type="radio" class="ace" value="1" /> <span
													class="lbl"> 1G</span>
												</label> <label style="padding-left: 24px"> <input
													name="mem-radio" type="radio" class="ace" value="2" /> <span
													class="lbl"> 2G</span>
												</label> <label style="padding-left: 23px"> <input
													name="mem-radio" type="radio" class="ace" value="4" /> <span
													class="lbl"> 4G</span>
												</label> <label style="padding-left: 24px"> <input
													name="mem-radio" type="radio" class="ace" value="8" /> <span
													class="lbl"> 8G</span>
												</label> <label style="padding-left: 24px"> <input
													name="mem-radio" type="radio" class="ace" value="16" /> <span
													class="lbl"> 16G</span>
												</label> <label style="padding-left: 24px"> <input
													name="mem-radio" type="radio" class="ace" value="0" checked/> <span
													class="lbl" > 无限制</span>
												</label>
											</div>
										</div>
									</div>

									<div class='form-group' id="env_form" style="display: none">
										<label class='col-sm-2'><b>环境变量</b></label>
										<div class='col-sm-10'>
											<ul class="env_params" id="env_params"
												style="list-style-type: none; margin: 0px">
												<li class="param"><input class="short-input"
													name="release_env" id="release_env" type="text"
													placeholder="应用环境变量" value="" style="width: 90%;">
													<a href="#" id="env-plus" style="color: green"><i
														class="ace-icon glyphicon glyphicon-plus"></i></a> <a href="#"
													id="env-minus" style="color: red; display: none;"><i
														class="ace-icon glyphicon glyphicon-minus"></i></a></li>
											</ul>
										</div>
									</div>
									<div class='form-group' id="vol_form" style="display: none">
										<label class='col-sm-2'><b>挂载点</b></label>
										<div class='col-sm-10'>
											<ul class="vol_params" id="vol_params"
												style="list-style-type: none; margin: 0px">
												<li class="param"><input class="short-input"
													type="text" name="host_volume" id="host_volume"
													placeholder="物理主机挂载目录" value="" style="width: 44%;">
													<input class="short-input" type="text"
													name="container_volume" id="container_volume"
													placeholder="容器内部被挂载目录" value=""
													style="width: 44%; margin-left: 8px"> <a href="#"
													id="vol-plus" style="color: green"><i
														class="ace-icon glyphicon glyphicon-plus"></i></a> <a href="#"
													id="vol-minus" style="color: red; display: none"><i
														class="ace-icon glyphicon glyphicon-minus"></i></a></li>
											</ul>
										</div>
									</div>
									<div class='form-group' id="param_form" style="display: none">
										<label class='col-sm-2'><b>其他参数</b></label>
										<div class='col-sm-10'>
											<ul class="params" id="params"
												style="list-style-type: none; margin: 0px">
												<li class="param">
													<div class="select-con"
														style="width: 44%; float: left; height: 35px">
														<select class="dropdown-select param-meter" id="meter"
															name="meter" style="width: 100%; height: 35px">
															<option value='0'>启动参数</option>
														</select>&nbsp;&nbsp;
													</div> <input class="short-input" type="text" name="param_value"
													id="param_value" placeholder="输入参数值..." value=""
													style="width: 44%; margin-left: 11px;"> <a href="#"
													id="param-plus" style="color: green"><i
														class="ace-icon glyphicon glyphicon-plus"></i></a> <a href="#"
													id="param-minus" style="color: red; display: none"><i
														class="ace-icon glyphicon glyphicon-minus"></i></a>
												</li>
											</ul>
										</div>
									</div>
									<div class='form-group' id="command_form" style="display: none">
										<label class='col-sm-2'><b>启动命令</b></label>
										<div class='col-sm-10'>
											<input class="short-input" id="release_command"
												placeholder="应用实例启动命令" type="text" value=""
												style="width: 90%" />
										</div>
									</div>
									<div class="form-group" id="health_form" style="display: none">
										<label class='col-sm-2'><b>健康检查</b></label>
										<div class="col-sm-10" style="margin-left: -5px">
											<label> <input name="switch-field-1" id="app_health"
												class="ace ace-switch ace-switch-4" type="checkbox" /> <span
												class="lbl"></span>
											</label>
										</div>
									</div>
									<div class="form-group" id="monitor_form" style="display: none">
										<label class='col-sm-2'><b>监控开关</b></label>
										<div class="col-sm-10" style="margin-left: -5px">
											<label> <input name="switch-field-1" id="app_monitor"
												class="ace ace-switch ace-switch-4" type="checkbox" /> <span
												class="lbl"></span>
											</label>
										</div>
									</div>
									<div class='form-group' id="release_version" style="display: none">
										<label class='col-sm-2'><b>替换版本</b></label>
										<div class='col-sm-10'>
											<select class="dropdown-select" id="release_tag"
												name="release_tag" style="width: 90%">
												
											</select>
										</div>
									</div>
									<div class='form-group' id="gray_policy" style="display: none">
										<label class='col-sm-2'><b>灰度策略</b></label>
										<div class='col-sm-10'>
											<div class="tabbable" style="width: 90%">
												<ul class="nav nav-tabs" id="myTab">
													<li class="active"><a data-toggle="tab" href="#num">
															<i class="green ace-icon fa fa-exchange bigger-120"></i>
															实例数替换
													</a></li>
													<li><a data-toggle="tab" href="#percent"> <i
															class="red ace-icon fa fa-exchange bigger-120"></i> 比例替换
													</a></li>
												</ul>
												<div class="tab-content">
													<div id="num" class="tab-pane fade in active" id="replaceNum_html">
														<label>替换实例数：</label> <input type="text"
															class="input-mini" id="replace_num"
															style="border: 1px solid #ccc;" value="0" />
													</div>
													<div id="percent" class="tab-pane fade">
														<label>替换百分比：</label> <input type="text"
															class="input-mini" id="replace_percent"
															style="border: 1px solid #ccc;" />
													</div>
												</div>
											</div>
										</div>
									</div>
									<div class='form-group' id="releaseNum">
										<label class='col-sm-2'><b>发布实例</b></label>
										<div class='col-sm-10' id="releaseNum_html">
											<input type="text" class="input-mini" id="release_num"
												style="border: 1px solid #ccc;" />
										</div>
									</div>
									<div class='form-group'>
										<label class='col-sm-2'><b>备注信息</b></label>
										<div class='col-sm-10'>
											<textarea id="release_desc" name="release_desc"
												placeholder="备注信息..." style="width: 90%; border: 1px solid #ccc; margin-left: 3px"
												rows="3"></textarea>
										</div>
									</div>
								</form>
							</div>
						</div>
					</div>
					<div class="step-pane" id="modal-step3">
						<div class="left">
							<p class="alert alert-info" style="margin: 0px 0px 5px 20px">
								<b>请确认应用发布信息！</b>
							</p>
							<div class="well" id="release_info"
								style="margin-top: 1px; margin-left: 20px"></div>

							<div id="apprelease_pro" style="display: none; margin-left: 20px">
								<div style="" id="apprelease_msg"></div>
								<div class="progress">
									<div id="progress_ar"
										class="progress-bar progress-bar-success progress-bar-striped active"
										role="progressbar" aria-valuenow="40" aria-valuemin="0"
										aria-valuemax="100" style="width: 0%;">
										<span id="prog_span"></span>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
				<div class="modal-footer wizard-actions">
					<button class="btn btn-sm btn-round btn-prev" id="pre">
						<i class="ace-icon fa fa-arrow-left"></i> 上一步
					</button>
					<button class="btn btn-success btn-round btn-sm btn-next" id="next"
						data-last="发布">
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
	<div class="modal fade" id="advanSearchARModal" tabindex="-1"
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
							id='advanced_search_ar_frm'>
							<div class="item">
								<ul class="ar_params" id="ar_params"
									style="list-style-type: none; margin: 0px 0px 10px 0px;">
									<li class="ar_params" style="margin-top: 10px;">
										<div class="select-con"
											style="height: 33px; width: 100px; float: left;">
											<select class="dropdown-select param-meter" name="ar_meter"
												id="ar_meter">
												<option value='0'>请选条件</option>
												<option value='1'>应用名称</option>
												<option value='2'>应用版本</option>
												<option value='3'>访问地址</option>
											</select>
										</div> <input class="short-input" type="text" name="ar_param_value"
										id="ar_param_value" placeholder="输入查询值..." value=""
										style="width: 73%; border: 1px solid #ccc; height: 30px">
										<a href="#" id="ar_remove-param" style="display: none;"> <span
											class="glyphicon glyphicon-remove delete-param"></span>
									</a>
									</li>
								</ul>
								<a class="btn btn-primary" id="ar_add-param" type="button"
									style="color: #fff; width: 87px;">
									<div style="margin: -7px -7px -7px -10px;">
										<span class="glyphicon glyphicon-plus"></span> <span
											class="text">添加条件</span>
									</div>
								</a>
							</div>
						</form>

						<div class="modal-footer">
							<button id="ar_advanced_cancel" type="button"
								class="btn btn-danger btn-round">取消</button>
							<button id="ar_advanced_search" type="button"
								class="btn btn-success btn-round">查询</button>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<%--advanced search modal end --%>

</body>
</html>

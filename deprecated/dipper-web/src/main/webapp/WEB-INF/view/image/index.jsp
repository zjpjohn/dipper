<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<!DOCTYPE html>
<html lang="en">
<head>
<link rel="shortcut icon" href="${basePath }img/title_cloud.png" />
<title>应用版本</title>
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
<meta charset="utf-8" />
<meta name="description" content="overview &amp; stats" />
<meta name="viewport"
	content="width=device-width, initial-scale=1.0, maximum-scale=1.0" />
<jsp:include page="../js.jsp"></jsp:include>
<link rel="stylesheet" href="${basePath}css/user/image.css" />
<script src="${basePath }ace/assets/js/jquery.validate.min.js"></script>
<script src="${basePath }ace/assets/js/additional-methods.min.js"></script>
<script src="${basePath }ace/assets/js/jquery.maskedinput.min.js"></script>
<script src="${basePath }ace/assets/js/select2.min.js"></script>
<script src="${basePath }ace/assets/js/fuelux/fuelux.wizard.min.js"></script>
<script src="${basePath }js/console/image.js"></script>
<!-- 添加额外的CSS样式表 -->
<link rel="stylesheet" href="${basePath}css/user/app.css" />
<!-- 添加额外的CSS样式表 -->
<c:set var="authButton" value='${buttonsAuth}'></c:set>
</head>
<body class="no-skin">
	<jsp:include page="../header.jsp"></jsp:include>
	<c:if test="${fn:contains(authButton,'imageRemove')}">
		<input type="hidden" id="delete_image">
	</c:if>
	<c:if test="${fn:contains(authButton,'imageMod')}">
		<input type="hidden" id="mod_image">
	</c:if>
	<%-- <c:if test="${fn:contains(authButton,'imagFastpush')}">
		<input type="hidden" id="push_image">
	</c:if> --%>

	<div class="main-container" id="main-container">
		<script type="text/javascript">
			try {
				ace.settings.check('main-container', 'fixed');
			} catch (e) {
			}
		</script>
		<jsp:include page="../nav.jsp">
			<jsp:param value="image_admin" name="page_index" />
		</jsp:include>
		<div class="main-content">
			<div class="main-content-inner">
				<div class="breadcrumbs" id="breadcrumbs">
					<ul class="breadcrumb">
						<li><i class="ace-icon fa fa-home home-icon"></i> <a
							href="${basePath}index.html"><strong>首页</strong></a></li>
						<li class="active"><b>应用版本</b></li>
					</ul>
				</div>
				<div class="page-header">
					<h1>
						欢迎来到应用版本管理 <small> <br>应用版本管理是针对同一个应用，不同版本的管理，包括本地上传，远程制作，导出，导入，版本流转等功能。
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
								<c:if test="${fn:contains(authButton,'imageImport')}">
									<a href="#" id="import_img" class="btn btn-sm btn-success btn-round"  data-toggle="modal"
												data-target="#RmtCrtImgModal"  onclick="showRmtCrtImgModal('import')"> 
										<i class="ace-icon fa fa-hand-o-down bigger-110 icon-only"></i> <b>版本导入</b>
									</a>
								</c:if>
								<c:if test="${fn:contains(authButton,'exportImage')}">
									<a href="#" id="export_img" class="btn btn-sm btn-warning btn-round"> <i
											class="ace-icon fa fa-hand-o-up bigger-128"></i> <b>版本导出</b>
									</a>
								</c:if>
								<div class="btn-group">
									<button data-toggle="dropdown"
										class="btn btn-primary btn-sm dropdown-toggle btn-round">
										<i class="ace-icon fa fa-wrench  bigger-110 icon-only"></i> <b>更多操作</b>
										<i class="ace-icon fa fa-angle-down icon-on-right"></i>
									</button>
									<ul class="dropdown-menu dropdown-primary dropdown-menu-left">
										
										<%-- <c:if test="${fn:contains(authButton,'imagFastpush')}">
											<li><a href="#" id="push_img_menu"> <i
													class="ace-icon fa fa-cloud-upload bigger-128"></i> <b>发布镜像</b>
											</a></li>
										</c:if> --%>
										<c:if test="${fn:contains(authButton,'imageCreate')}">
											<li><a href="#" id="create_image_btn"
												onclick="showCreateImageModal()"> <i
												class="ace-icon fa fa-camera-retro  bigger-110 icon-only"></i>
												<b>本地创建版本</b>
											</a></li>
										</c:if>
										<c:if test="${fn:contains(authButton,'imageRmtMake')}">
											<li><a href="#" id="rmt_crt_img_btn"
												onclick="showRmtCrtImgModal('create')" data-toggle="modal"
												data-target="#RmtCrtImgModal"> <i
												class="ace-icon fa fa-rocket bigger-110 icon-only"></i> <b>远程创建版本</b>
											</a></li>
										</c:if>
										<%-- <c:if test="${fn:contains(authButton,'imagFastpush')}">
											<li><a href="#" id="push_img_menu"> <i
													class="ace-icon fa fa-cloud-upload bigger-128"></i> <b>发布镜像</b>
											</a></li>
										</c:if> --%>
										<c:if test="${fn:contains(authButton,'imageRemove')}">
											<li><a href="#" id="batch_delete_menu"> <i
													class="ace-icon fa fa-trash-o bigger-128"></i> <b>批量删除</b>
											</a></li>
										</c:if>
									</ul>
								</div>

								<div class="col-xs-12 col-sm-4" style="float: right">
									<c:if test="${fn:contains(authButton,'imageList')}">
										<div class="input-group">
											<input type="text" id="search_text"
												class="form-control search-query" placeholder="请输入名称进行模糊查询">
											<span class="input-group-btn">
												<button type="button"
													class="btn btn-primary  btn-round btn-sm"
													onclick="searchImage()">
													查找 <i
														class="ace-icon fa fa-search icon-on-right bigger-110"></i>
												</button>
											</span> <span class="input-group-btn"> &nbsp; </span> <span
												class="input-group-btn">
												<button id="detailSearch" type="button"
													class="btn btn-warning btn-round btn-sm"
													onclick="AdvancedSearchImage()">
													更多 <i
														class="ace-icon fa fa-search icon-on-right bigger-110"></i>
												</button>
											</span>
										</div>
									</c:if>
								</div>
							</div>
							<c:if test="${fn:contains(authButton,'imageList')}">
								<div>
									<table id="image_list"></table>
									<div id="image_page"></div>
								</div>
							</c:if>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<div id="mod_image_modal" class="modal" data-backdrop="static">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal"
						aria-hidden="true">×</button>
					<h4 class="modal-title" id="myModalLabel">应用版本发布</h4>
				</div>
				<div class="modal-body">
					<div class="well" style="margin-top: 1px;">
						<form class='form-horizontal' role='form' id='update_image_form'>
							<div class='form-group'>
								<label class='col-sm-3'><b>镜像类型：</b></label>
								<div class="col-sm-9">
									<input id="mod_image_id" type="hidden"> <input
										id="mod_image_type" name="mod_image_type" type="radio"
										value="BASIC" onclick="changeModSelect()" class="ace"
										checked="checked" /> <span class="lbl"> <b>基础镜像</b></span> <input
										id="mod_image_type" name="mod_image_type" type="radio"
										value="APP" onclick="changeModSelect()" class="ace" /> <span
										class="lbl"> <b>应用镜像</b></span>
								</div>
							</div>
							<div class='form-group' id="mod_belong_app_div"
								style="display: none">
								<label class='col-sm-3'><b>所属应用：</b></label>
								<div class='col-sm-9'>
									<select id="belong_app_select" name="belong_app_select"
										class='form-control'></select>
								</div>
							</div>
							<div class='form-group' id="mod_belong_env_div"
								style="display: none">
								<label class='col-sm-3'><b>发布环境：</b></label>
								<div class='col-sm-9'>
									<div class="item">
										<div class="left">
											<a href="#" class="list-group-item active">备选环境</a>
											<div class="block">
												<div class="btn-group-vertical" id="update_blockEnv"></div>
											</div>
										</div>
										<div class="operate">
											<button type="button" class="btn btn-purple btn-sm btn-round" id="update_add-env">
												<span class="glyphicon glyphicon-forward"></span>&nbsp;增加
											</button>
											<button type="button" class="btn btn-grey btn-sm btn-round" id="update_remove-env" style="margin-top: 10px">
												<span class="glyphicon glyphicon-backward"></span>&nbsp;&nbsp;移除
											</button>
										</div>
										<div class="right">
											<a href="#" class="list-group-item active">已选环境 </a>
											<div class="block">
												<div class="btn-group-vertical" id="update_activateEnv">
												</div>
											</div>
										</div>
									</div>
								</div>
							</div>
						</form>
					</div>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-round btn-danger"
						onclick="hideModal('mod_image_modal');">取消</button>
					<button type="button" class="btn btn-round btn-success"
						onclick="modImgFun();">提交</button>
				</div>
			</div>
		</div>
	</div>
	<div id="modal-wizard" class="modal" data-backdrop="static"></div>
	<div id="wizard_content_div" style="display: none;">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header" data-target="#modal-step-contents">
					<ul class="wizard-steps">
						<li data-target="#modal-step1" class="active"><span
							class="step">1</span> <span class="title">上传文件</span></li>
						<li data-target="#modal-step2"><span class="step">2</span> <span
							class="title">应用版本制作</span></li>
						<li data-target="#modal-step3"><span class="step">3</span> <span
							class="title">应用版本发布</span></li>
					</ul>
				</div>
				<div class="modal-body step-content" id="modal-step-contents">
					<div class="step-pane active" id="modal-step1">
						<div class="modal-footer wizard-actions center"
							style="margin: 10px 10px" role="alert">大文件上传，可能需要很长的时间，请耐心等待！</div>
						<div>
							<form class="form form-horizontal" id="create-form"
								style="margin: 0px 10px">
								<div class='form-group'>
									<label class='col-sm-3'><b>目标仓库：</b></label>
									<div class="col-sm-9">
										<select id="registry_select" name="registry_select"
											class='form-control'></select>
									</div>
								</div>
								<!-- <div class='form-group'>
									<label class='col-sm-3'><b>镜像类型：</b></label>
									<div class="col-sm-9">
										<input id="image_type" name="image_type" type="radio"
											value="BASIC" onclick="changeSelect()" class="ace"
											checked="checked" /> <span class="lbl"> <b>基础镜像</b></span> <input
											id="image_type" name="image_type" type="radio" value="APP"
											onclick="changeSelect()" class="ace" /> <span class="lbl">
											<b>应用镜像</b>
										</span>
									</div>
								</div> -->
								<div class='form-group' id="make_image_app_id_div">
									<label class='col-sm-3'><b>所属应用：</b></label>
									<div class="col-sm-9">
										<select id="app_select" name="app_select" class='form-control'
											onchange="localAppGetEnvs();"></select>
									</div>
								</div>
								<div class='form-group' id="make_image_appenv_div">
									<label class='col-sm-3'><b>选择环境：</b></label>
									<div class='col-sm-9'>
										<div class="itemenv">
											<div class="leftenvshow">
												<a href="#" class="list-group-item active">备选环境</a>
												<div class="blockEnvShow">
													<div class="btn-group-vertical" id="blockLclEnv"></div>
												</div>
											</div>
											<div class="operate">
												<button type="button"
													class="btn btn-purple btn-sm btn-round" id="add-lclenv"
													onclick="localAddEnvs();">
													<span class="glyphicon glyphicon-forward"></span>&nbsp;增加
												</button>
												<button type="button" class="btn btn-grey btn-sm btn-round"
													id="remove-lclenv" style="margin-top: 10px"
													onclick="localRemoveEnvs();">
													<span class="glyphicon glyphicon-backward"></span>&nbsp;&nbsp;移除
												</button>
											</div>
											<div class="rightenvshow">
												<a href="#" class="list-group-item active">已选环境 </a>
												<div class="blockEnvShow">
													<div class="btn-group-vertical" id="activeLclEnv"></div>
												</div>
											</div>
										</div>
									</div>
								</div>
								<div class='form-group'>
									<label class='col-sm-3'><b>镜像文件：</b></label>
									<div class="col-sm-9">
										<input type="hidden" id="image_id" /> <input type="file"
											id="image_file" name="image_file" class="form-control" />
									</div>
								</div>
								<div id="pid" style="display: none;">
									<div style="" id="showId">已经完成0%</div>
									<div class="progress">
										<div id="progressid"
											class="progress-bar progress-bar-success progress-bar-striped"
											role="progressbar" aria-valuenow="40" aria-valuemin="0"
											aria-valuemax="100" style="width: 0%;"></div>
									</div>
								</div>
							</form>
						</div>
					</div>
					<div class="step-pane" id="modal-step2">
						<div class="modal-footer wizard-actions center"
							style="margin: 10px 10px" role="alert">新的应用版本制作需要一定时间，请耐心等待，点击制作按钮，开始制作</div>
						<div>
							<form class="form form-horizontal" id="create-form"
								style="margin: 0px 10px">
								<!-- <div class='form-group'>
									<label class='col-sm-3'><b>应用名称：</b></label>
									<div class="col-sm-9">
										<input type="text" id="templatename" name="templatename"
											placeholder="应用名称【包含(字母、数字、斜杠、下划线和破折号)合法字符】"
											class="form-control" />
									</div>
								</div> -->
								<div class='form-group'>
									<label class='col-sm-3'><b>应用版本：</b></label>
									<div class="col-sm-9">
										<input type="text" id="templateversion" name="templateversion"
											placeholder="应用版本【包含(字母、数字、下划线、破折号和小数点)】"
											class="form-control" />
									</div>
								</div>
								<div class="item" id="imagestatus" style="display: none;">
									<div id="imagemessage" class="alert alert-warning"
										style="margin: 10px 10px" role="alert">
										<b>新的应用版本制作中，请耐心等待...</b>
									</div>
								</div>
							</form>
						</div>
					</div>
					<div class="step-pane" id="modal-step3">
						<div class="well" style="margin: 10px 10px" role="alert">新应用版本发布需要一定时间，请耐心等待，点击发布按钮，开始发布</div>
						<div>
							<form class="form form-horizontal" id="create-form"
								style="margin: 0px 10px">
								<div class="item" id="pushstatus" style="display: none;">
									<div id="pushmessage" class="alert alert-warning"
										style="margin: 10px 10px" role="alert">
										<b>新的应用版本发布中，请耐心等待...</b>
									</div>
								</div>
							</form>
						</div>
					</div>
				</div>
				<div class="modal-footer wizard-actions">
					<button id="image_publish_cancel"
						class="btn btn-danger btn-round btn-sm"
						onclick="cancelImageCreate()">
						<i class="ace-icon fa fa-times"></i> 关闭
					</button>
					<button id="upload_image_btn" type="button"
						class="btn btn-sm btn-round btn-primary"
						onclick="uploadImageFile()">
						<i class="ace-icon fa  fa-cloud-upload  bigger-110 icon-only"></i>
						<span id="upload-image_btn-text"><b>上传</b></span>
					</button>
					<button id="make_image_btn"
						class="btn btn-primary btn-round btn-sm btn-create hide"
						type="button" onclick="makeImageFun()">
						<i class="ace-icon fa fa-cog bigger-110 icon-only"></i> <span
							id="make-image_btn-text"><b>制作</b></span>
					</button>
					<button class="btn btn-sm btn-round btn-prev">
						<i class="ace-icon fa fa-arrow-left"></i> 上一步
					</button>
					<button class="btn btn-success btn-sm btn-round btn-next"
						data-last="发布镜像">
						下一步 <i class="ace-icon fa fa-arrow-right icon-on-right"></i>
					</button>
				</div>
			</div>
		</div>
	</div>

	<%--create remote image modal begin --%>
	<div class="modal fade" id="RmtCrtImgModal" tabindex="-1" role="dialog"
		aria-labelledby="myModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal"
						aria-hidden="true">×</button>
					<h4 class="modal-title rmtTitle">
						<b>远程创建</b>
					</h4>
				</div>
				<div class="modal-body">
					<form class='form-horizontal' role='form' id='remote_crtimg_frm'>
					<input type="hidden" id="titleType"/>
						<!-- <div class='form-group'>
							<label class='col-sm-3'><b>应用名称：</b></label>
							<div class="col-sm-9">
								<input type="text" id="image_name" name="image_name"
									placeholder="镜像名称【包含(字母、数字、斜杠、下划线和破折号)合法字符】"
									class="form-control" />
							</div>
						</div> -->
						<div class='form-group'>
							<label class='col-sm-3'><b>目标仓库：</b></label>
							<div class='col-sm-9'>
								<select id="rmt_reg_select" name="rmt_reg_select"
									class="form-control">
									<option value="0">请选择新应用版本推送的目标仓库</option>
								</select>
							</div>
						</div>
						<div class='form-group' id="rmtbelong_app_div">
							<label class='col-sm-3'><b>所属应用：</b></label>
							<div class='col-sm-9'>
								<select id="target_app" name="target_app" class="form-control">
									<option value="0">请选择镜像所属的应用。</option>
								</select>
							</div>
						</div>
						<div class='form-group' id="rmtbelong_appenv">
							<label class='col-sm-3'><b>选择环境：</b></label>
							<div class='col-sm-9'>
								<div class="itemenv">
									<div class="leftenvshow">
										<a href="#" class="list-group-item active">备选环境</a>
										<div class="blockEnvShow">
											<div class="btn-group-vertical" id="blockRmtEnv"></div>
										</div>
									</div>
									<div class="operate">
										<button type="button" class="btn btn-purple btn-sm btn-round"
											id="add-rmtenv">
											<span class="glyphicon glyphicon-forward"></span>&nbsp;增加
										</button>
										<button type="button" class="btn btn-grey btn-sm btn-round"
											id="remove-rmtenv" style="margin-top: 10px">
											<span class="glyphicon glyphicon-backward"></span>&nbsp;&nbsp;移除
										</button>
									</div>
									<div class="rightenvshow">
										<a href="#" class="list-group-item active">已选环境 </a>
										<div class="blockEnvShow">
											<div class="btn-group-vertical" id="activeRmtEnv"></div>
										</div>
									</div>
								</div>
							</div>
						</div>
						<div class='form-group'>
							<label class='col-sm-3'><b>应用版本：</b></label>
							<div class="col-sm-9">
								<input type="text" id="image_tag" name="image_tag"
									placeholder="镜像版本【包含(字母、数字、下划线、破折号和小数点)】" class="form-control" />
							</div>
						</div>
						
						<div class='form-group'>
							<label class="col-sm-3"><b>主机地址：</b></label>
							<div class="col-sm-9">
								<input type="text" id="source_ip" name="source_ip"
									placeholder="请输入主机IP地址。" class="form-control" />
							</div>
						</div>
						<div class='form-group'>
							<label class="col-sm-3"><b>用户名称：</b></label>
							<div class="col-sm-9">
								<input type="text" id="source_user" name="source_user"
									placeholder="请输入用户名称。" class="form-control" />
							</div>
						</div>
						<div class='form-group'>
							<label class="col-sm-3"><b>登陆密码：</b></label>
							<div class="col-sm-9">
								<input type="password" id="source_passwd" name="source_passwd"
									placeholder="请输入登陆密码。" class="form-control" />
							</div>
						</div>
					<!-- 	<div class='form-group'>
							<label class='col-sm-3'><b>镜像类型：</b></label>
							<div class='col-sm-9'>
								<input type="radio" name="rmt_image_type" value="BASIC"
									checked="checked" onclick="changeRmtModSelect()" />基础镜像&nbsp;&nbsp;&nbsp;&nbsp;<input
									type="radio" name="rmt_image_type" value="APP"
									onclick="changeRmtModSelect()" />应用镜像
							</div>
						</div> -->
						
						<div class='form-group'>
							<label class='col-sm-3'><b>源文件路径</b></label>
							<div class='col-sm-9'>
								<input style="width: 330px;" id="source_path" name="source_path"
									type='text' />&nbsp;
								<button id="show_rmt_files" type="button"
									class="btn btn-sm btn-pink btn-round"
									onclick="queryRemoteFolder()">列出文件</button>
							</div>
						</div>
						<div class='form-group'>
							<label class='col-sm-3'><b>包含文件：</b></label><font color="red" id="top_show"></font>
							<div class='col-sm-9' id='showFolderFile'></div>
						</div>
						<div class='form-group' id="showInfo" hidden="hidden">
							<label class='col-sm-3'><font color="red"><b>错误信息：</b></font></label>
							<div style="" id="showRemoteInfo"></div>
						</div>
						<div id="rmt_pid" style="display: none;">
							<div style="" id="rmt_showId"></div>
							<div class="progress">
								<div id="rmt_progressid"
									class="progress-bar progress-bar-success progress-bar-striped"
									role="progressbar" aria-valuenow="40" aria-valuemin="0"
									aria-valuemax="100" style="width: 0%;">
									<span id="prog_span"></span>
								</div>
							</div>
						</div>
					</form>
				</div>
				<div class="modal-footer">
					<button id="rmt_cancel" type="button"
						class="btn btn-danger btn-round">取消</button>
					<!-- <button id="rmt_make_submit" type="button"
						class="btn btn-primary btn-round">仅制作镜像</button> -->
					<button id="rmt_mkps_submit" type="button"
						class="btn btn-success btn-round">提交</button>
				</div>
			</div>
		</div>
	</div>
	<%--create remote image modal end --%>

	<%--create export image modal begin --%>
	<div class="modal fade" id="exportImgModal" tabindex="-1" role="dialog"
		aria-labelledby="myModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal"
						aria-hidden="true">×</button>
					<h4 class="modal-title" id="myModalLabel">
						<b>应用版本</b>
					</h4>
				</div>
				<div class="modal-body">
					<form class='form-horizontal' role='form' id='export_img_frm'>
						<div class='form-group'>
							<label class='col-sm-3'><b>应用名称：</b></label>
							<div class="col-sm-9">
								<input type="text" id="export_img_name" name="image_name"
									placeholder="镜像名称【包含(字母、数字、斜杠、下划线和破折号)合法字符】"
									class="form-control" />
							</div>
						</div>
						<div class='form-group'>
							<label class='col-sm-3'><b>应用版本：</b></label>
							<div class="col-sm-9">
								<input type="text" id="export_img_tag" name="image_tag"
									placeholder="镜像版本【包含(字母、数字、下划线、破折号和小数点)】" class="form-control" />
							</div>
						</div>
						<div class='form-group'>
							<label class="col-sm-3"><b>主机地址：</b></label>
							<div class="col-sm-9">
								<input type="text" id="export_ip" name="export_ip"
									placeholder="请输入主机IP地址。" class="form-control" />
							</div>
						</div>
						<div class='form-group'>
							<label class="col-sm-3"><b>用户名：</b></label>
							<div class="col-sm-9">
								<input type="text" id="export_user" name="export_user"
									placeholder="请输入用户名称。" class="form-control" />
							</div>
						</div>
						<div class='form-group'>
							<label class="col-sm-3"><b>密码：</b></label>
							<div class="col-sm-9">
								<input type="password" id="export_passwd" name="export_passwd"
									placeholder="请输入登陆密码。" class="form-control" />
							</div>
						</div>
						<div class='form-group'>
							<label class='col-sm-3'><b>导出路径：</b></label>
							<div class='col-sm-9'>
								<input type="text" id="export_path" name="export_path"
									placeholder="请输入保存路径" class="form-control" />
							</div>
						</div>
						<!-- <div class='form-group' id="exportInfo" hidden="hidden">
							<label class='col-sm-3'><font color="red"><b>错误信息：</b></font></label>
							<div style="" id="showExportInfo"></div>
						</div>
						<div id="export_pid" style="display: none;">
							<div style="" id="rmt_showId"></div>
							<div class="progress">
								<div id="export_progressid"
									class="progress-bar progress-bar-success progress-bar-striped"
									role="progressbar" aria-valuenow="40" aria-valuemin="0"
									aria-valuemax="100" style="width: 0%;">
									<span id="prog_span"></span>
								</div>
							</div>
						</div> -->
					</form>
				</div>
				<div class="modal-footer">
					<button id="export_cancel" type="button"
						class="btn btn-danger btn-round">取消</button>
					<button id="export_submit" type="button"
						class="btn btn-primary btn-round">导出</button>
				</div>
			</div>
		</div>
	</div>
	<%--create export image modal end --%>


	<%--advanced search modal begin --%>
	<div class="modal fade" id="advancedSearchImageModal" tabindex="-1"
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
						<form class='form-horizontal' role='form' id='advanced_search_frm'>
							<div class="item">
								<ul class="params" id="params"
									style="list-style-type: none; margin: 0px 0px 10px 0px;">
									<li class="param" style="margin-top: 10px;">
										<div class="select-con"
											style="height: 33px; width: 100px; float: left;">
											<select class="dropdown-select param-meter" name="meter"
												id="meter">
												<option value='0'>请选列名</option>
												<option value='1'>版本ID</option>
												<option value='2'>应用名称</option>
												<option value='3'>应用版本</option>
												<option value='4'>类型</option>
												<option value='5'>发布状态</option>
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
</body>
</html>

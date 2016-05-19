<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<!DOCTYPE html>
<html lang="en">
<head>
<link rel="shortcut icon" href="${basePath }img/title_cloud.png" />
<title>参数管理</title>
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
<meta charset="utf-8" />
<meta name="description" content="overview &amp; stats" />
<meta name="viewport"
	content="width=device-width, initial-scale=1.0, maximum-scale=1.0" />
<jsp:include page="../js.jsp"></jsp:include>
<script src="${basePath }js/console/param.js"></script>
<c:set var="authButton" value='${buttonsAuth}'></c:set>

</head>
<body class="no-skin">
	<c:if test="${fn:contains(authButton,'paramDelete')}">
		<input type="hidden" id="delete_param">
	</c:if>
	<c:if test="${fn:contains(authButton,'paramUpdate')}">
		<input type="hidden" id="update_param">
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
			<jsp:param value="param_admin" name="page_index" />
		</jsp:include>
		<div class="main-content">
			<div class="main-content-inner">
				<div class="breadcrumbs" id="breadcrumbs">
					<ul class="breadcrumb">
						<li><i class="ace-icon fa fa-home home-icon"></i> <a
							href="${basePath}index.html"><strong>首页</strong></a></li>
						<li class="active"><i class="ace-icon fa fa-pencil-square-o"></i>
							<b>参数管理</b></li>
					</ul>
				</div>
				<div class="page-header">
					<h1>
						欢迎来到参数管理 <small> <br>参数是发布平台中所有参数的简称，参数管理提供参数的添加、修改、编辑、删除等功能。详情查看“使用文档”
						</small>
					</h1>
				</div>
				<div class="page-content">
					<div class="row">
						<div class="col-xs-12">
							<div class="well well-sm">
								<c:if test="${fn:contains(authButton,'paramCreate')}">
									<button class="btn btn-sm btn-success btn-round"
										data-toggle="modal" data-target="#createParamModal">
										<i class="ace-icon fa fa-pencil-square-o bigger-125"></i> <b>添加参数</b>
									</button>
								</c:if>
								<c:if test="${fn:contains(authButton,'paramRemoveAll')}">
									<button class="btn btn-sm btn-inverse btn-round"
										id="batch_remove_param_btn">
										<i class="ace-icon fa fa-trash-o bigger-125"></i> <b>批量删除</b>
									</button>
								</c:if>
								<div class="col-xs-12 col-sm-4" style="float: right">
									<c:if test="${fn:contains(authButton,'paramList')}">
										<div class="input-group">
											<input type="text" id="search_param"
												class="form-control search-query" placeholder="请输入名称进行模糊查询">
											<span class="input-group-btn">
												<button type="button"
													class="btn btn-primary  btn-round btn-sm"
													onclick="searchParams()">
													查找 <i
														class="ace-icon fa fa-search icon-on-right bigger-110"></i>
												</button>
											</span> <span class="input-group-btn"> &nbsp; </span> <span
												class="input-group-btn">
												<button type="button"
													class="btn btn-warning btn-round btn-sm"
													onclick="AdvancedSearchParams()">
													更多 <i
														class="ace-icon fa fa-search icon-on-right bigger-110"></i>
												</button>
											</span>
										</div>
									</c:if>
								</div>
							</div>
							<c:if test="${fn:contains(authButton,'paramList')}">
								<div>
									<table id="param_list"></table>
									<div id="param_page"></div>
								</div>
							</c:if>
						</div>
					</div>
				</div>
			</div>
		</div>
		<%--Create param begin --%>
		<div class="modal fade" id="createParamModal" tabindex="-1"
			role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal"
							aria-hidden="true">×</button>
						<h4 class="modal-title" id="myModalLabel">添加参数</h4>
					</div>
					<div class="modal-body">
						<div class='well ' style='margin-top: 1px;'>
							<form class='form-horizontal' role='form' id='add_param_frm'>
								<div class='form-group'>
									<label class='col-sm-3'><b>参数名称(<font color="red">必填</font>)：
									</b></label>
									<div class='col-sm-9'>
										<input id="param_name" name="param_name" placeholder="请输入参数名称"
											type='text' class="form-control" />
									</div>
								</div>
								<div class='form-group'>
									<label class='col-sm-3'><b>参数键(<font color="red">必填</font>)：
									</b></label>
									<div class='col-sm-9'>
										<input id="param_key" name="param_key" placeholder="请输入参数键"
											type='text' class="form-control" />
									</div>
								</div>
								<div class='form-group'>
									<label class='col-sm-3'><b>连接符(<font color="red">必填</font>)：
									</b></label>
									<div class='col-sm-9'>
										<input type="radio" name="param_connector" value="0" checked />空格&nbsp;&nbsp;
										<input type="radio" name="param_connector" value="1" />等号&nbsp;&nbsp;
									</div>
								</div>
								<div class='form-group'>
									<label class='col-sm-3'><b>值类型(<font color="red">必填</font>)：
									</b></label>
									<div class='col-sm-9'>
										<input type="radio" name="param_type" value="0" checked />空值&nbsp;&nbsp;
										<input type="radio" name="param_type" value="1" />正整数&nbsp;&nbsp;
										<input type="radio" name="param_type" value="2" />字符串&nbsp;&nbsp;
										<input type="radio" name="param_type" value="3" />Map类型&nbsp;&nbsp;
										<input type="radio" name="param_type" value="4" />布尔型&nbsp;&nbsp;
									</div>
								</div>
								<div class='form-group'>
									<label class='col-sm-3'><b>复用类型(<font color="red">必填</font>)：
									</b></label>
									<div class='col-sm-9'>
										<input type="radio" name="param_reusable" value="0" checked />不可复用&nbsp;&nbsp;
										<input type="radio" name="param_reusable" value="1" />可复用&nbsp;&nbsp;
									</div>
								</div>
								<div class='form-group'>
									<label class='col-sm-3'><b>参数描述：</b></label>
									<div class='col-sm-9'>
										<textarea id="param_desc" name="param_desc"
											placeholder="请输入参数的描述信息" class="form-control" rows="3"></textarea>
									</div>
								</div>
								<div class='form-group'>
									<label class='col-sm-3'><b>备注信息：</b></label>
									<div class='col-sm-9'>
										<textarea id="param_comment" name="param_comment"
											placeholder="请输入参数的描述信息" class="form-control" rows="3"></textarea>
									</div>
								</div>
							</form>
						</div>
					</div>
					<div class="modal-footer">
						<button id="cancel" type="button" class="btn btn-danger btn-round">
							取消</button>
						<button id="submit" type="button"
							class="btn btn-success btn-round">提交</button>
					</div>
				</div>
			</div>
		</div>
		<%--Create param end --%>

		<%--Modify param begin --%>
		<div class="modal fade" id="modifyParamModal" tabindex="-1"
			role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal"
							aria-hidden="true">×</button>
						<h4 class="modal-title" id="myModalLabel">修改参数</h4>
					</div>
					<div class="modal-body">
						<div class='well ' style='margin-top: 1px;'>
							<form class='form-horizontal' role='form' id='modify_param_frm'>
								<div class='form-group'>
									<label class='col-sm-3'><b>参数名称：</b></label>
									<div class='col-sm-9'>
										<input id="param_id_edit" name="param_id_edit" type="hidden" />
										<input id="param_name_edit" name="param_name_edit"
											placeholder="请输入参数名称" type='text' class="form-control"
											readonly="readonly" />
									</div>
								</div>
								<div class='form-group'>
									<label class='col-sm-3'><b>参数键(<font color="red">必填</font>)：
									</b></label>
									<div class='col-sm-9'>
										<input id="param_key_edit" name="param_key_edit"
											placeholder="请输入参数键" type='text' class="form-control" />
									</div>
								</div>
								<div class='form-group'>
									<label class='col-sm-3'><b>连接符： </b></label>
									<div class='col-sm-9'>
										<input type="radio" name="param_connector_edit" value="0" />空格&nbsp;&nbsp;
										<input type="radio" name="param_connector_edit" value="1" />等号&nbsp;&nbsp;
									</div>
								</div>
								<div class='form-group'>
									<label class='col-sm-3'><b>值类型： </b></label>
									<div class='col-sm-9'>
										<input type="radio" name="param_type_edit" value="0" />空值&nbsp;&nbsp;
										<input type="radio" name="param_type_edit" value="1" />正整数&nbsp;&nbsp;
										<input type="radio" name="param_type_edit" value="2" />字符串&nbsp;&nbsp;
										<input type="radio" name="param_type_edit" value="3" />Map类型&nbsp;&nbsp;
										<input type="radio" name="param_type_edit" value="4" />布尔型&nbsp;&nbsp;
									</div>
								</div>
								<div class='form-group'>
									<label class='col-sm-3'><b>复用类型： </b></label>
									<div class='col-sm-9'>
										<input type="radio" name="param_reusable_edit" value="0" />不可复用&nbsp;&nbsp;
										<input type="radio" name="param_reusable_edit" value="1" />可复用&nbsp;&nbsp;
									</div>
								</div>
								<div class='form-group'>
									<label class='col-sm-3'><b>参数描述：</b></label>
									<div class='col-sm-9'>
										<textarea id="param_desc_edit" name="param_desc_edit"
											placeholder="请输入参数的描述信息" class="form-control" rows="3"></textarea>
									</div>
								</div>
								<div class='form-group'>
									<label class='col-sm-3'><b>备注信息：</b></label>
									<div class='col-sm-9'>
										<textarea id="param_comment_edit" name="param_comment_edit"
											placeholder="请输入参数的描述信息" class="form-control" rows="3"></textarea>
									</div>
								</div>
							</form>
						</div>
					</div>
					<div class="modal-footer">
						<button class="btn btn-danger btn-round" id="modify_cancel"
							type="button">取消</button>
						<button class="btn btn-success btn-round" id="modify_submit"
							type="button">提交</button>
					</div>
				</div>
			</div>
		</div>
		<%--Modify param end --%>

		<%--advanced search modal begin --%>
		<div class="modal fade" id="advancedSearchParamModal" tabindex="-1"
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
													<option value='1'>参数名称</option>
													<option value='2'>参数值</option>
													<option value='3'>参数描述</option>
												</select>
											</div> <input class="short-input" type="text"
											name="search_param_value" id="search_param_value"
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
						<button id="advanced_param_cancel" type="button"
							class="btn btn-danger btn-round">取消</button>
						<button id="advanced_param_search" type="button"
							class="btn btn-success btn-round">查询</button>
					</div>
				</div>
			</div>
		</div>
		<%--advanced search modal end --%>
	</div>
</body>
</html>

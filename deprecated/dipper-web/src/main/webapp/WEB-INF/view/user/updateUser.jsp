<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<html lang="en">
<head>
<link rel="shortcut icon" href="${basePath }img/title_cloud.png" />
<title>修改密码</title>
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
<meta charset="utf-8" />
<meta name="description" content="overview &amp; stats" />
<meta name="viewport"
	content="width=device-width, initial-scale=1.0, maximum-scale=1.0" />
<link rel="stylesheet" href="${basePath }ace/assets/css/bootstrap.min.css" />
<link rel="stylesheet" href="${basePath }ace/assets/css/font-awesome.min.css" />
<link rel="stylesheet" href="${basePath }ace/assets/css/ace-fonts.css" />
<link rel="stylesheet" href="${basePath }ace/assets/css/ui.jqgrid.css" />
<link rel="stylesheet" href="${basePath }ace/assets/css/jquery.ztree.css" />
<link rel="stylesheet" href="${basePath }ace/assets/css/jquery.gritter.css" />
<link rel="stylesheet" href="${basePath }ace/assets/css/ace.min.css" class="ace-main-stylesheet" id="main-ace-style" />
<link rel="stylesheet" href="${basePath }css/validation.css" />
<script src="${basePath }ace/assets/js/jquery.min.js"></script>
<script src="${basePath }ace/assets/js/ace-extra.min.js"></script>
<script src="${basePath }ace/assets/js/bootstrap.min.js"></script>
<script src="${basePath }ace/assets/js/ace/ace.js"></script>
<script src="${basePath }ace/assets/js/bootbox.min.js"></script>
<script src="${basePath }ace/assets/js/jquery.validate.min.js"></script>
<script src="${basePath }ace/assets/js/jquery.gritter.min.js"></script>
<script	src="${basePath }ace/assets/js/jqGrid/jquery.jqGrid.min.js"></script>
<script	src="${basePath }ace/assets/js/jqGrid/i18n/grid.locale-cn.js"></script>
<script src="${basePath }ace/assets/js/ace-elements.min.js"></script>
<script src="${basePath }ace/assets/js/fuelux/fuelux.tree.min.js"></script>
<script src="${basePath }ace/assets/js/jquery.ztree.min.js"></script>
<script src="${basePath }ace/assets/js/fuelux/fuelux.spinner.min.js"></script>
<script src="${basePath }ace/assets/js/jquery.ztree.excheck.js"></script>
<script	src="${basePath }js/base.js"></script>
<script	src="${basePath }js/validate.js"></script>
<script type="text/javascript">
	var base = '${basePath }';
</script>
<script src="${basePath }js/console/updateUser.js"></script>
</head>
<body class="no-skin">
	<input type="hidden" id="input_ud" value="${UD}"/>
	<div class="main-container">
		<%-- Modify host info --%>
		<div tabindex="-1" role="dialog" aria-labelledby="myModalLabel"
			aria-hidden="true" data-backdrop="static">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<!-- <button type="button" class="close" data-dismiss="modal"
							aria-hidden="true">×</button> -->
						<h4 class="modal-title">修改密码</h4>
					</div>
					<div class="modal-body">
						<div class="well" style="margin-top: 1px;">
							<form class='form-horizontal' role='form' id="update_pass_form">
								<div class='form-group'>
									<label class='col-sm-3'><b>&nbsp;用户名：</b></label>
									<div class='col-sm-9'>
										<input type='text' class="form-control" value="${userName}"
											disabled="disabled" />
									</div>
								</div>
								<div class='form-group'>
									<label class='col-sm-3'><b>密码：</b></label>
									<div class='col-sm-9'>
										<input id="user_pass_edit" name='user_pass_edit'
											type='password' class="form-control" />
									</div>
								</div>

							</form>
						</div>
					</div>
					
					<div class="modal-footer">
						<button id="modify_submit" type="button"
							class="btn btn-round btn-success" >提交</button>
					</div>
				</div>
			</div>
		</div>
	</div>
</body>
</html>

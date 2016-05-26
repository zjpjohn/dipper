<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
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
<script src="${basePath }js/console/registry.js"></script>
<script type="text/javascript">
	registry_id_of_slave_images='${reg_id}';
</script>
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
			<jsp:param value="registry_admin" name="page_index" />
		</jsp:include>
		<div class="main-content">
			<div class="main-content-inner">
				<div class="breadcrumbs" id="breadcrumbs">
					<ul class="breadcrumb">
						<li><i class="ace-icon fa fa-home home-icon"></i><a href="${basePath}index.html"><strong>首页</strong></a></li>
						<li class="active"><b>仓库管理</b></li>
						<li class="active"><b>包含镜像列表</b></li>
					</ul>
				</div>
				<div class="page-content">
					<div class="row">
						<div class="col-xs-12">
							<div class="well well-sm">
								<button class="btn  btn-xs btn-round btn-primary" onclick="gobackpage()">
									<i class="ace-icon fa fa-arrow-left bigger-125"></i>
									<b>返回仓库管理</b>
								</button>
							</div>
							<div>
								<table id="registry_image_list"></table>
								<div id="registry_image_page"></div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</body>
</html>

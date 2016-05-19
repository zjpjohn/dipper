<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<div id="navbar" class="navbar navbar-skin-3 primary">
	<div class="navbar-container" id="navbar-container">
		<button type="button" class="navbar-toggle menu-toggler pull-left"
			id="menu-toggler" data-target="#sidebar">
			<span class="sr-only"></span>
		</button>
		<div class="navbar-header pull-left">
			<a href="${basePath }index.html" class="navbar-brand"> 
			<small> 
				<i class="fa fa-cloud"></i>
				Devops管理平台
			</small>
			</a>
		</div>
		<div class="navbar-buttons navbar-header pull-right" role="navigation">
			<input id="userId" type="hidden" value="${user.userId }">
			<ul class="nav ace-nav">
				<li>
					<a class="dropdown-toggle" href="#" onclick="showWord()">
						<i class="ace-icon fa fa-file"></i>
						<span><strong>使用文档</strong></span>
					</a>
				</li>
				<li >
					<a class="dropdown-toggle" href="#">
						<i class="ace-icon fa fa-user"></i>
						<span><strong>当前用户：${user.userName} </strong></span>
					</a>
				</li>
				<li>
					<a class="dropdown-toggle" href="javascript:logout()">
						<i class="ace-icon fa fa-power-off"></i>
						<span><strong>退出</strong></span>
					</a>
				</li>
			</ul>
		</div>
	</div>
</div>

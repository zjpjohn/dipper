<%@ page language="java" import="java.util.*"
	contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%
	String pageIndex = request.getParameter("page_index");
	String parentIndex = request.getParameter("parent_index");
	pageIndex = null == pageIndex ? "" : pageIndex;
	parentIndex = null == parentIndex ? "" : parentIndex;
%>
<script src="${basePath }ace/assets/js/ace/ace.sidebar.js"></script>
<script src="${basePath }ace/assets/js/ace/ace.submenu-1.js"></script>
<c:set var="authStr" value='${pagesAuth}'></c:set>
<c:set var="authButton" value='${buttonsAuth}'></c:set>
<div id="sidebar" class="sidebar responsive">
	<script type="text/javascript">
		try {
			ace.settings.check('sidebar', 'fixed');
		} catch (e) {
		}
	</script>
	<ul class="nav nav-list">
		<c:if test="${fn:contains(authStr,'index')}">
			<li id="manage_dashboard" class=""><a
				href="${basePath}index.html"> <i
					class="menu-icon fa fa-tachometer"></i> <span class="menu-text"><strong>数据纵览</strong>
				</span>
			</a> <b class="arrow"></b></li>
		</c:if>
		<!--基础运维-->
		<c:if test="${fn:contains(authStr,'title_resource')}">
			<li class=""><a href="#" class="dropdown-toggle"> <i
					class="menu-icon fa fa-desktop"></i> <span class="menu-text"><b>基础设施</b></span>
					<b class="arrow fa fa-angle-down"></b>
			</a> <b class="arrow"></b>
				<ul class="submenu">
					<c:if test="${fn:contains(authStr,'hostIndex')}">
						<li id="host_admin" class=""><a
							href="${basePath }host/index.html"> <i
								class="menu-icon fa fa-desktop"></i> <span class="menu-text">
									<b>主机管理</b>
							</span>
						</a> <b class="arrow"></b></li>
					</c:if>
					<c:if test="${fn:contains(authStr,'clusterIndex')}">
						<li id="cluster_admin" class=""><a
							href="${basePath }cluster/index.html"> <i
								class="menu-icon fa fa-globe"></i> <span class="menu-text"><b>集群管理</b>
							</span>
						</a> <b class="arrow"></b></li>
					</c:if>
					<c:if test="${fn:contains(authStr,'registryIndex')}">
						<li id="registry_admin" class=""><a
							href="${basePath }registry/index.html"> <i
								class="menu-icon fa fa-university"></i> <span class="menu-text"><b>镜像仓库</b>
							</span>
						</a> <b class="arrow"></b></li>
					</c:if>
					<%--
					<c:if test="${fn:contains(authStr,'registryIndex')}">
						<li id="tenant_admin" class=""><a
							href="${basePath }tenant/index.html"> <i
								class="menu-icon fa fa-university"></i> <span class="menu-text"><b>租户管理</b>
							</span>
						</a> <b class="arrow"></b></li>
					</c:if>--%>
					<c:if test="${fn:contains(authStr,'softwareIndex')}">
						<li id="software_admin" class=""><a
							href="${basePath }software/index.html"> <i
								class="menu-icon fa fa-university"></i> <span class="menu-text"><b>软件管理</b>
							</span>
						</a> <b class="arrow"></b></li>
					</c:if>
				</ul></li>
		</c:if>

		<!--配置管理-->
		<c:if test="${fn:contains(authStr,'title_config')}">
			<li class=""><a href="#" class="dropdown-toggle"> <i
					class="menu-icon fa fa-pencil-square-o"></i> <span
					class="menu-text"><b>配置管理</b> </span> <b
					class="arrow fa fa-angle-down"></b>
			</a> <b class="arrow"></b>
				<ul class="submenu">
					<c:if test="${fn:contains(authStr,'envIndex')}">
						<li id="env_admin" class=""><a
							href="${basePath }env/index.html"> <i
								class="menu-icon fa fa-flag-checkered"></i> <span
								class="menu-text"><b>环境管理</b> </span>
						</a> <b class="arrow"></b></li>
					</c:if>
					<c:if test="${fn:contains(authStr,'paramIndex')}">
						<li id="param_admin" class=""><a
							href="${basePath }param/index.html"> <i
								class="menu-icon fa fa-pencil-square-o"></i> <span
								class="menu-text"><b>参数管理</b> </span>
						</a> <b class="arrow"></b></li>
					</c:if>
					<c:if test="${fn:contains(authStr,'resourceIndex')}">
						<li id="resource_admin" class=""><a
							href="${basePath }resource/index.html"> <i
								class="menu-icon fa fa-database"></i> <span class="menu-text"><b>资源管理</b>
							</span>
						</a> <b class="arrow"></b></li>
					</c:if>
					<c:if test="${fn:contains(authStr,'mntrproxIndex')}">
						<li id="mntrpxy_admin" class=""><a
							href="${basePath }mntrproxy/index.html"> <i
								class="menu-icon glyphicon glyphicon-eye-open"></i> <span
								class="menu-text"><b>监控代理</b> </span>
						</a> <b class="arrow"></b></li>
					</c:if>
				</ul></li>
		</c:if>

		<!--应用发布-->
		<c:if test="${fn:contains(authStr,'title_appRelease')}">
			<li class=""><a href="#" class="dropdown-toggle"> <i
					class="menu-icon fa fa-cubes"></i> <span class="menu-text"><b>应用管理</b></span>
					<b class="arrow fa fa-angle-down"></b>
			</a> <b class="arrow"></b>
				<ul class="submenu">
					<c:if test="${fn:contains(authStr,'applicationIndex')}">
						<li id="application_admin" class=""><a
							href="${basePath }application/index.html"> <i
								class="menu-icon fa fa-cubes"></i> <span class="menu-text"><b>构建应用</b>
							</span>
						</a> <b class="arrow"></b></li>
					</c:if>
					<c:if test="${fn:contains(authStr,'imageIndex')}">
						<li id="image_admin" class=""><a
							href="${basePath }image/index.html"> <i
								class="menu-icon fa fa-camera"></i> <span class="menu-text"><b>应用版本管理</b>
							</span>
						</a> <b class="arrow"></b></li>
					</c:if>
					<c:if test="${fn:contains(authStr,'lbIndex')}">
						<li id="lb_admin" class=""><a
							href="${basePath }lb/index.html"> <i
								class="menu-icon fa fa-cogs"></i> <span class="menu-text"><b>应用负载</b>
							</span>
						</a> <b class="arrow"></b></li>
					</c:if>
					<%-- <c:if test="${fn:contains(authStr,'containerIndex')}">
						<li id="container_admin" class=""><a
							href="${basePath }container/index.html"> <i
								class="menu-icon fa fa-inbox"></i> <span class="menu-text"><b>容器管理</b>
							</span>
						</a> <b class="arrow"></b></li>
					</c:if> --%>
					<c:if test="${fn:contains(authStr,'pushapp')}">
						<li id="pushapp_admin" class=""><a
							href="${basePath }apprelease/index.html"> <i
								class="menu-icon fa fa-inbox"></i> <span class="menu-text"><b>应用发布</b>
							</span>
						</a> <b class="arrow"></b></li>
					</c:if>
				</ul></li>
		</c:if>


		<!--监控预警-->
		<c:if test="${fn:contains(authStr,'title_monitor')}">
			<li class=""><a href="#" class="dropdown-toggle"> <i
					class="menu-icon fa fa-bell"></i> <span class="menu-text"><b>应用维护</b></span>
					<b class="arrow fa fa-angle-down"></b>
			</a> <b class="arrow"></b>
				<ul class="submenu">
					<c:if test="${fn:contains(authStr,'monitorIndex')}">
						<li id="monitor_admin" class=""><a
							href="${basePath }monitor/index.html"> <i
								class="menu-icon fa fa-exclamation-circle"></i> <span
								class="menu-text"><b>监测管理</b> </span>
						</a> <b class="arrow"></b></li>
					</c:if>
					<%-- <c:if test="${fn:contains(authStr,'warningIndex')}">
						<li id="warning_admin" class=""><a
							href="${basePath }warning/index.html"> <i
								class="menu-icon fa fa-bell-o"></i> <span class="menu-text"><b>预警管理</b>
							</span>
						</a> <b class="arrow"></b></li>
					</c:if>
					<c:if test="${fn:contains(authStr,'serviceIndex')}">
						<li id="service_admin" class=""><a
							href="${basePath }service/index.html"> <i
								class="menu-icon fa fa-recycle"></i> <span class="menu-text"><b>服务管控</b>
							</span>
						</a> <b class="arrow"></b></li>
					</c:if>
					<c:if test="${fn:contains(authStr,'exceptionIndex')}">
						<li id="exception_admin" class=""><a
							href="${basePath }exception/index.html"> <i
								class="menu-icon fa fa-exclamation"></i> <span class="menu-text"><b>异常处理</b>
							</span>
						</a> <b class="arrow"></b></li>
					</c:if> --%>
					<c:if test="${fn:contains(authStr,'logIndex')}">
						<li id="log_admin" class=""><a
							href="${basePath }log/index.html"> <i
								class="menu-icon fa fa-file-o"></i> <span class="menu-text"><b>日志管理</b>
							</span>
						</a> <b class="arrow"></b></li>
					</c:if>
					<%-- <c:if test="${fn:contains(authStr,'statisticIndex')}">
						<li id="statistic_admin" class=""><a
							href="${basePath }statistic/index.html"> <i
								class="menu-icon fa fa-reorder"></i> <span class="menu-text"><b>统计报表</b>
							</span>
						</a> <b class="arrow"></b></li>
					</c:if> --%>
					<%-- <c:if test="${fn:contains(authStr,'containerIndex')}">
						<li id="container_admin" class=""><a
							href="${basePath }container/index.html"> <i
								class="menu-icon fa fa-inbox"></i> <span class="menu-text"><b>容器管理</b>
							</span>
						</a> <b class="arrow"></b></li>
					</c:if>
					<c:if test="${fn:contains(authStr,'containerIndex')}">
						<li id="pushapp_admin" class=""><a
							href="${basePath }apprelease/index.html"> <i
								class="menu-icon fa fa-inbox"></i> <span class="menu-text"><b>应用发布</b>
							</span>
						</a> <b class="arrow"></b></li>
					</c:if> --%>
				</ul></li>
		</c:if>

		<!--权限管理-->
		<c:if test="${fn:contains(authStr,'title_auth')}">
			<li class="" id="casd"><a href="#" class="dropdown-toggle">
					<i class="menu-icon fa fa-users"></i> <span class="menu-text"><b>权限管理</b>
				</span> <b class="arrow fa fa-angle-down"></b>
			</a> <b class="arrow"></b>
				<ul class="submenu">
					<c:if test="${fn:contains(authStr,'userIndex')}">
						<li id="manage_user" class=""><a
							href='${basePath }user/index.html'> <i
								class="menu-icon fa fa-user"></i> <span class="menu-text"><b>用户管理</b>
							</span>
						</a> <b class="arrow"></b></li>
					</c:if>
					<c:if test="${fn:contains(authStr,'roleIndex')}">
						<li id="manage_role" class=""><a
							href='${basePath }role/index.html'> <i
								class="menu-icon fa fa-flag"></i> <span class="menu-text"><b>角色管理</b>
							</span>
						</a> <b class="arrow"></b></li>
					</c:if>
					<c:if test="${fn:contains(authStr,'authIndex')}">
						<li id="manage_right" class=""><a
							href='${basePath }auth/index.html'> <i
								class="menu-icon fa fa-key"></i> <span class="menu-text"><b>权限管理</b>
							</span>
						</a> <b class="arrow"></b></li>
					</c:if>
				</ul></li>
		</c:if>
	</ul>
	<!-- 菜单伸缩按钮 -->
	<div class="sidebar-toggle sidebar-collapse" id="sidebar-collapse">
		<i class="ace-icon fa fa-angle-double-left"
			data-icon1="ace-icon fa fa-angle-double-left"
			data-icon2="ace-icon fa fa-angle-double-right"></i>
	</div>
	<script type="text/javascript">
			try{ace.settings.check('sidebar' , 'collapsed')}catch(e){}
			$(document).ready(function(){
				var page_index = "<%=pageIndex%>";
			if ("" != page_index) {
				$("#" + page_index).parent().parent().addClass('open');
				$("#" + page_index).parent().show();
				$("#" + page_index).addClass('active');
			}
		});
	</script>
</div>

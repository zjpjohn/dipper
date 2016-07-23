<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<!-- bootstrap & fontawesome -->
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/assets/css/bootstrap.min.css" />
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/assets/font-awesome/4.2.0/css/font-awesome.min.css" />
<!-- page specific plugin styles -->
<!-- text fonts -->
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/assets/fonts/fonts.googleapis.com.css" />
<!-- ace styles -->
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/assets/css/ace.min.css"
	class="ace-main-stylesheet" id="main-ace-style" />
<!--[if lte IE 9]>
			<link rel="stylesheet" href="assets/css/ace-part2.min.css" class="ace-main-stylesheet" />
		<![endif]-->
<!--[if lte IE 9]>
		  <link rel="stylesheet" href="assets/css/ace-ie.min.css" />
		<![endif]-->
<!-- inline styles related to this page -->
<!-- ace settings handler -->
<script src="assets/js/ace-extra.min.js"></script>
<!-- 仪表盘CSS -->
<style>
    .container {
        width: 300px;
        margin: 0 auto;
        text-align: center;
    }

    .gauge {
        width: 300px;
        height: 300px;
    }

    a:link.button,
    a:active.button,
    a:visited.button,
    a:hover.button {
        margin: 30px 5px 0 2px;
        padding: 7px 13px;
    }
    </style>
<!-- HTML5shiv and Respond.js for IE8 to support HTML5 elements and media queries -->
<!--[if lte IE 8]>
		<script src="assets/js/html5shiv.min.js"></script>
		<script src="assets/js/respond.min.js"></script>
		<![endif]-->

</head>
<body>
	<div id="navbar" class="navbar navbar-default">
		<script type="text/javascript">
				try{ace.settings.check('navbar' , 'fixed')}catch(e){}
			</script>

		<div class="navbar-container" id="navbar-container">
			<button type="button" class="navbar-toggle menu-toggler pull-left"
				id="menu-toggler" data-target="#sidebar">
				<span class="sr-only">Toggle sidebar</span> <span class="icon-bar"></span>

				<span class="icon-bar"></span> <span class="icon-bar"></span>
			</button>

			<div class="navbar-header pull-left">
				<a href="index.html" class="navbar-brand"> <small> <i
						class="fa fa-leaf"></i> Ceph Monitor </small> </a>
			</div>
		</div>
		<!-- /.navbar-container -->
	</div>




	<div class="row">
		<div class="col-sm-12">
			<div class="widget-box transparent">
				<div class="widget-header widget-header-flat">
					<h4 class="widget-title lighter">
						<i class="ace-icon fa fa-star orange"></i> Ceph Cluster Overall
						Status
					</h4>

					<div class="widget-toolbar">
						<a href="#" data-action="collapse"> <i
							class="ace-icon fa fa-chevron-up"></i> </a>
					</div>
				</div>

				<div class="widget-body">
					<div class="widget-main no-padding">
						<table class="table  table-striped" >
							<tbody>
								<!-- 可采用jstl遍历 -->
								<c:forEach items="${healthStat}" var="v">
									<tr bgcolor="6dcff6">
										<td>&nbsp&nbsp&nbsp&nbsp${v}</td>
									</tr>
								</c:forEach>
							</tbody>
						</table>
					</div>
					<!-- /.widget-main -->
				</div>
				<!-- /.widget-body -->
			</div>
			<!-- /.widget-box -->
		</div>
	</div>

	<br />


	<div class="row">
		<div class="col-sm-12">
			<div class="widget-box transparent">
				<div class="widget-header widget-header-flat">
					<h4 class="widget-title lighter">
						<i class="ace-icon fa fa-star orange"></i> Ceph Cluster Monitor
						Status
					</h4>

					<div class="widget-toolbar">
						<a href="#" data-action="collapse"> <i
							class="ace-icon fa fa-chevron-up"></i> </a>
					</div>
				</div>

				<div class="widget-body">
					<div class="widget-main no-padding">
						<table class="table table-bordered table-striped">
							<tbody>
								<tr >
									<td width="20" bgcolor="6dcff6">&nbsp&nbsp&nbsp&nbsp${monStat0}:${monStat1}</td>

								</tr>
							</tbody>
						</table>
					</div>
					<!-- /.widget-main -->
				</div>
				<!-- /.widget-body -->
			</div>
			<!-- /.widget-box -->
		</div>
	</div>
	<br />


	<div class="row">
		<div class="col-sm-12">
			<div class="widget-box transparent">
				<div class="widget-header widget-header-flat">
					<h4 class="widget-title lighter">
						<i class="ace-icon fa fa-star orange"></i> Ceph Cluster OSD Status
					</h4>

					<div class="widget-toolbar">
						<a href="#" data-action="collapse"> <i
							class="ace-icon fa fa-chevron-up"></i> </a>
					</div>
				</div>
				<div class="widget-body">
					<div class="widget-main no-padding">
						<div class="row">
							<div class="col-xs-12 col-sm-3 widget-container-col">
								<div class="widget-box">
									<div class="widget-header">
										<h5 class="widget-title smaller" align="center">Total</h5>


									</div>

									<div class="widget-body">
										<div class="widget-main padding-6">
											<div class="alert alert-info">${osdStat0}</div>
										</div>
									</div>
								</div>
							</div>

							<div class="col-xs-12 col-sm-3 widget-container-col">
								<div class="widget-box">
									<div class="widget-header">
										<h5 class="widget-title smaller">UP</h5>


									</div>

									<div class="widget-body">
										<div class="widget-main padding-6">
											<div class="alert alert-info">${osdStat1}</div>
										</div>
									</div>
								</div>
							</div>
							<div class="col-xs-12 col-sm-3 widget-container-col">
								<div class="widget-box">
									<div class="widget-header">
										<h5 class="widget-title smaller">IN</h5>


									</div>

									<div class="widget-body">
										<div class="widget-main padding-6">
											<div class="alert alert-info">${osdStat2}</div>
										</div>
									</div>
								</div>
							</div>

							<div class="col-xs-12 col-sm-3 widget-container-col">
								<div class="widget-box">
									<div class="widget-header">
										<h5 class="widget-title smaller">Unhealth</h5>


									</div>

									<div class="widget-body">
										<div class="widget-main padding-6">
											<div class="alert alert-info">${osdStat0-osdStat1}</div>
										</div>
									</div>
								</div>
							</div>
						</div>
						<br />


					</div>
					<!-- /.widget-main -->
				</div>
				<!-- /.widget-body -->
			</div>
			<!-- /.widget-box -->
		</div>
	</div>










	<div class="row">
		<div class="col-sm-12">
			<div class="widget-box transparent">
				<div class="widget-header widget-header-flat">
					<h4 class="widget-title lighter">
						<i class="ace-icon fa fa-star orange"></i> Ceph Cluster Placement
						Groups Status
					</h4>

					<div class="widget-toolbar">
						<a href="#" data-action="collapse"> <i
							class="ace-icon fa fa-chevron-up"></i> </a>
					</div>
				</div>

				<div class="widget-body">
					<div class="widget-main no-padding">

<!-- 扇形图 -->

						<div class="vspace-12-sm"></div>

						<div class="col-sm-4">
						
						
						
							<div class="widget-box">
								<div
									class="widget-header widget-header-flat widget-header-small">
									<h5 class="widget-title">
										<i class="ace-icon fa fa-signal"></i> PG Status
									</h5>
									<div class="widget-body">
										<div class="widget-main">
											<div id="piechart-placeholder"></div>


										</div>
									</div>
									<!-- /.widget-body -->
								</div>
								<!-- /.widget-box -->
							</div>
							<!-- /.col -->
						</div>
						<!-- /.row -->
						<!-- 扇形图end -->
						
						<!-- 仪表盘 -->
				
					<div class="container">
						<h4>storage</h4>
				        <div id="g1" class="gauge"></div>
				        
				    </div>
				    <!-- 仪表盘end-->
				    
				</div>
				</div>
			</div>
		</div>
	</div>








	<!-- basic scripts ------------------------------------------------------------------------------------------------>
	<!-- 仪表盘js -->
	<script src="${pageContext.request.contextPath}/assets/js/raphael-2.1.4.min.js"></script>
    <script src="${pageContext.request.contextPath}/assets/js/justgage.js"></script>
    <script>
    var g1;
    document.addEventListener("DOMContentLoaded", function(event) {
        g1 = new JustGage({
            id: "g1",
            value: <%=session.getAttribute("val")%>,
            min: 0,
            max: 100,
            donut: true,
            gaugeWidthScale: 0.6,
            counter: true,
            hideInnerShadow: true
        });

        document.getElementById('g1_refresh').addEventListener('click', function() {
            g1.refresh(getRandomInt(0, 100));
        });
    });
    </script>
	<!-- 仪表盘js -->
	<!--[if !IE]> -->
	<script src="assets/js/jquery.2.1.1.min.js"></script>
	<!-- <![endif]-->
	<!--[if IE]>
<script src="assets/js/jquery.1.11.1.min.js"></script>
<![endif]-->
	<!--[if !IE]> -->
	<script type="text/javascript">
			window.jQuery || document.write("<script src='${pageContext.request.contextPath}/assets/js/jquery.min.js'>"+"<"+"/script>");
		</script>
	<!-- <![endif]-->
	<!--[if IE]>
<script type="text/javascript">
 window.jQuery || document.write("<script src='assets/js/jquery1x.min.js'>"+"<"+"/script>");
</script>
<![endif]-->
	<!-- script文件 --><!-- script文件 --><!-- script文件 --><!-- script文件 --><!-- script文件 --><!-- script文件 --><!-- script文件 -->
	<script type="text/javascript">
			if('ontouchstart' in document.documentElement) document.write("<script src='${pageContext.request.contextPath}/assets/js/jquery.mobile.custom.min.js'>"+"<"+"/script>");
		</script>
	<script src="${pageContext.request.contextPath}/assets/js/bootstrap.min.js"></script>

	<!-- page specific plugin scripts -->

	<!--[if lte IE 8]>
		  <script src="${pageContext.request.contextPath}/assets/js/excanvas.min.js"></script>
		<![endif]-->
	<script src="${pageContext.request.contextPath}/assets/js/jquery-ui.custom.min.js"></script>
	<script src="${pageContext.request.contextPath}/assets/js/jquery.ui.touch-punch.min.js"></script>
	<script src="${pageContext.request.contextPath}/assets/js/jquery.easypiechart.min.js"></script>
	<script src="${pageContext.request.contextPath}/assets/js/jquery.sparkline.min.js"></script>
	<script src="${pageContext.request.contextPath}/assets/js/jquery.flot.min.js"></script>
	<script src="${pageContext.request.contextPath}/assets/js/jquery.flot.pie.min.js"></script>
	<script src="${pageContext.request.contextPath}/assets/js/jquery.flot.resize.min.js"></script>

	<!-- ace scripts -->
	<script src="${pageContext.request.contextPath}/assets/js/ace-elements.min.js"></script>
	<script src="${pageContext.request.contextPath}/assets/js/ace.min.js"></script>

	<!-- inline scripts related to this page -->
	<script type="text/javascript">
			jQuery(function($) {
				$('.easy-pie-chart.percentage').each(function(){
					var $box = $(this).closest('.infobox');
					var barColor = $(this).data('color') || (!$box.hasClass('infobox-dark') ? $box.css('color') : 'rgba(255,255,255,0.95)');
					var trackColor = barColor == 'rgba(255,255,255,0.95)' ? 'rgba(255,255,255,0.25)' : '#E2E2E2';
					var size = parseInt($(this).data('size')) || 50;
					$(this).easyPieChart({
						barColor: barColor,
						trackColor: trackColor,
						scaleColor: false,
						lineCap: 'butt',
						lineWidth: parseInt(size/10),
						animate: /msie\s*(8|7|6)/.test(navigator.userAgent.toLowerCase()) ? false : 1000,
						size: size
					});
				})
			
				$('.sparkline').each(function(){
					var $box = $(this).closest('.infobox');
					var barColor = !$box.hasClass('infobox-dark') ? $box.css('color') : '#FFF';
					$(this).sparkline('html',
									 {
										tagValuesAttribute:'data-values',
										type: 'bar',
										barColor: barColor ,
										chartRangeMin:$(this).data('min') || 0
									 });
				});
			
			
			  //flot chart resize plugin, somehow manipulates default browser resize event to optimize it!
			  //but sometimes it brings up errors with normal resize event handlers
			  $.resize.throttleWindow = false;
			
			  var placeholder = $('#piechart-placeholder').css({'width':'90%' , 'min-height':'150px'});
			  var data = [
				{ label: "stale+active+clean",  data: <%=session.getAttribute("pgStatus1") %>, color: "#68BC31"},
							{ label: "stale+incomplete",  data: <%=session.getAttribute("pgStatus2") %>, color: "#2091CF"},
							
							{ label: "stale+remapped+incomplete",  data:  <%=session.getAttribute("pgStatus3") %>, color: "#DA5430"},
							{ label: "stale+active+degraded",  data:  <%=session.getAttribute("pgStatus4") %>, color: "#AF4E96"}
			  ]
			  function drawPieChart(placeholder, data, position) {
			 	  $.plot(placeholder, data, {
					series: {
						pie: {
							show: true,
							tilt:0.8,
							highlight: {
								opacity: 0.25
							},
							stroke: {
								color: '#fff',
								width: 2
							},
							startAngle: 2
						}
					},
					legend: {
						show: true,
						position: position || "ne", 
						labelBoxBorderColor: null,
						margin:[-30,15]
					}
					,
					grid: {
						hoverable: true,
						clickable: true
					}
				 })
			 }
			 drawPieChart(placeholder, data);
			
			 /**
			 we saved the drawing function and the data to redraw with different position later when switching to RTL mode dynamically
			 so that's not needed actually.
			 */
			 placeholder.data('chart', data);
			 placeholder.data('draw', drawPieChart);
			
			
			  //pie chart tooltip example
			  var $tooltip = $("<div class='tooltip top in'><div class='tooltip-inner'></div></div>").hide().appendTo('body');
			  var previousPoint = null;
			
			  placeholder.on('plothover', function (event, pos, item) {
				if(item) {
					if (previousPoint != item.seriesIndex) {
						previousPoint = item.seriesIndex;
						var tip = item.series['label'] + " : " + item.series['percent']+'%';
						$tooltip.show().children(0).text(tip);
					}
					$tooltip.css({top:pos.pageY + 10, left:pos.pageX + 10});
				} else {
					$tooltip.hide();
					previousPoint = null;
				}
				
			 });
			
				/////////////////////////////////////
				$(document).one('ajaxloadstart.page', function(e) {
					$tooltip.remove();
				});
			
			
			
			
				var d1 = [];
				for (var i = 0; i < Math.PI * 2; i += 0.5) {
					d1.push([i, Math.sin(i)]);
				}
			
				var d2 = [];
				for (var i = 0; i < Math.PI * 2; i += 0.5) {
					d2.push([i, Math.cos(i)]);
				}
			
				var d3 = [];
				for (var i = 0; i < Math.PI * 2; i += 0.2) {
					d3.push([i, Math.tan(i)]);
				}
				
			
				var sales_charts = $('#sales-charts').css({'width':'100%' , 'height':'220px'});
				$.plot("#sales-charts", [
					{ label: "Domains", data: d1 },
					{ label: "Hosting", data: d2 },
					{ label: "Services", data: d3 }
				], {
					hoverable: true,
					shadowSize: 0,
					series: {
						lines: { show: true },
						points: { show: true }
					},
					xaxis: {
						tickLength: 0
					},
					yaxis: {
						ticks: 10,
						min: -2,
						max: 2,
						tickDecimals: 3
					},
					grid: {
						backgroundColor: { colors: [ "#fff", "#fff" ] },
						borderWidth: 1,
						borderColor:'#555'
					}
				});
			
			
				$('#recent-box [data-rel="tooltip"]').tooltip({placement: tooltip_placement});
				function tooltip_placement(context, source) {
					var $source = $(source);
					var $parent = $source.closest('.tab-content')
					var off1 = $parent.offset();
					var w1 = $parent.width();
			
					var off2 = $source.offset();
					//var w2 = $source.width();
			
					if( parseInt(off2.left) < parseInt(off1.left) + parseInt(w1 / 2) ) return 'right';
					return 'left';
				}
			
			
				$('.dialogs,.comments').ace_scroll({
					size: 300
			    });
				
				
				//Android's default browser somehow is confused when tapping on label which will lead to dragging the task
				//so disable dragging when clicking on label
				var agent = navigator.userAgent.toLowerCase();
				if("ontouchstart" in document && /applewebkit/.test(agent) && /android/.test(agent))
				  $('#tasks').on('touchstart', function(e){
					var li = $(e.target).closest('#tasks li');
					if(li.length == 0)return;
					var label = li.find('label.inline').get(0);
					if(label == e.target || $.contains(label, e.target)) e.stopImmediatePropagation() ;
				});
			
				$('#tasks').sortable({
					opacity:0.8,
					revert:true,
					forceHelperSize:true,
					placeholder: 'draggable-placeholder',
					forcePlaceholderSize:true,
					tolerance:'pointer',
					stop: function( event, ui ) {
						//just for Chrome!!!! so that dropdowns on items don't appear below other items after being moved
						$(ui.item).css('z-index', 'auto');
					}
					}
				);
				$('#tasks').disableSelection();
				$('#tasks input:checkbox').removeAttr('checked').on('click', function(){
					if(this.checked) $(this).closest('li').addClass('selected');
					else $(this).closest('li').removeClass('selected');
				});
			
			
				//show the dropdowns on top or bottom depending on window height and menu position
				$('#task-tab .dropdown-hover').on('mouseenter', function(e) {
					var offset = $(this).offset();
			
					var $w = $(window)
					if (offset.top > $w.scrollTop() + $w.innerHeight() - 100) 
						$(this).addClass('dropup');
					else $(this).removeClass('dropup');
				});
			
			})
		</script>
</body>
</html>

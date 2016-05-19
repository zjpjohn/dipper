jQuery(function($) {
    var url = base + 'registry/dashboard';
    
    data = {
        userId: 1
    };
    $.post(url, data,
    function(response) {
        if (response == "") {
            showMessage("获取信息出现异常！");
        } else {
        	
        	showDashboard('dashboard_dockernum','JumpToHostIndex(1)','orange',response.DockerHostNumber,0);
        	showDashboard('dashboard_swarmnum','JumpToHostIndex(0)','red',response.SwarmHostNumber,0);
        	showDashboard('dashboard_nginxnum','JumpToHostIndex(3)','green',response.NginxHostNumber,0);
        	showDashboard('dashboard_registrynum','JumpToHostIndex(2)','brown',response.RegistryHostNumber,0);
        	showDashboard('dashboard_container_total','JumpToContainerIndex(0)','blue',response.ContainerTotal,0);
        	showDashboard('dashboard_container_running','JumpToContainerIndex(1)','green',response.ContainerRunning,0);
        	showDashboard('dashboard_container_stop','JumpToContainerIndex(2)','red',response.ContainerStop,0);
        	
        	showDashboard('dashboard_cluster_num','cluster/index.html','blue',response.ClusterNumber,1);
        	showDashboard('dashboard_application_num','application/index.html','Crimson',response.ApplicationNumber,1);
        	showDashboard('dashboard_loadbalance_num','lb/index.html','SeaGreen',response.LoadBalanceNumer,1);
        	showDashboard('dashboard_registry_num','registry/index.html','black',response.RegistryNumber,1);
        }
    });
    
    /*（1）向页面中根据应用的数量追加资源图展示量*/
	var queryapp_url = base + 'app/queryAppResInfo';

	var queryapp_data = {};
			$
			.post(
					queryapp_url,
					queryapp_data,
					function(response) {
						if (response == "") {
							//showMessage("获取信息出现异常！");
							/*系统中不存在集群和应用的信息*/
						} else {
						    /*是否在标签页中设置了激活页*/
						    var is_active = false;
						    /*保存active状态的tab页面ID信息*/
						    var active_tabid = "";
							
							/*遍历后台返回的应用JSONArray*/
							for (var count = 0, length = response.length; count < length; count++) {
								var appview_ele = response[count];
								var app_id = appview_ele.appId;
								var app_name = appview_ele.appName;
								

								/* 向标签栏中插入名称信息 *//* 向前台页面中追加显示的多个应用窗口 */
								if (!is_active) {
									var tabHtml = "<li class=\"active\"><a data-toggle=\"tab\" href=\"#"
											+ app_id
											+ "_tab\"><b>"
											+ app_name
											+ "</b></a></li>"
									$('#recent-tab').append(tabHtml);
									active_tabid = app_id + "_tab";
									is_active = true;
								} else {
									var tabHtml = "<li><a data-toggle=\"tab\" href=\"#"
											+ app_id
											+ "_tab\"><b>"
											+ app_name
											+ "</b></a></li>"
									$('#recent-tab').append(tabHtml);
								}
								

								/* 首先将全部tab页面设置为active状态，保证canvas的填充和绘制 */
								var strHtml = "<div id=\""
										+ app_id
										+ "_tab\" class=\"tab-pane active\">"
										+ "<div class=\"grid2\">"
										+ "<h5 class=\"widget-title\">"
										+ "<i class=\"ace-icon fa fa-bar-chart-o pink\"></i>&nbsp;<b>CPU统计</b></h5>"
										+ "<div id=\"piechart-cpu_"
										+ app_id
										+ "\"></div>"
										+ "</div><div class=\"grid2\">"
										+ "<h5 class=\"widget-title\">"
										+ "<i class=\"ace-icon fa fa-bar-chart-o brown\"></i>&nbsp;<b>内存统计</b></h5>"
										+ "<div id=\"piechart-mem_" + app_id
										+ "\"></div></div></div>";
								$('#appview_mainshow').append(strHtml);
							}
							
							
							/*遍历后台返回的应用JSONArray*/
							for (var count = 0, length = response.length; count < length; count++) {
								
								var appview_ele = response[count];
								var app_id = appview_ele.appId;
								var cpuinfo_data = appview_ele.cpuInfoList;
								var meminfo_data = appview_ele.memInfoList;
								
								/* （2）遍历应用，向资源展示部署注入饼图和数据 */
								/* 绘制资源使用情况的饼图 */
								var place_cpu = $('#piechart-cpu_'+app_id).css({
									'width' : '90%',
									'min-height' : '150px'
								});
								var place_mem = $('#piechart-mem_'+app_id).css({
									'width' : '90%',
									'min-height' : '150px'
								});
								
								

								/* 分别注入相应的资源用量数据 */
								var cpu_data = cpuinfo_data;
								var mem_data = meminfo_data;
								
								drawCpuPieChart(place_cpu, cpu_data);
								place_cpu.data('chart', cpu_data);
								place_cpu.data('draw', drawCpuPieChart);
								
								drawMemPieChart(place_mem, mem_data);
								place_mem.data('chart', mem_data);
								place_mem.data('draw', drawMemPieChart);
								
								

								// pie chart tooltip example
								var $tooltip = $(
										"<div class='tooltip top in'><div class='tooltip-inner'></div></div>")
										.hide().appendTo('body');
								var previousPoint = null;

								place_cpu.on('plothover', function(event, pos, item) {
									if (item) {
										if (previousPoint != item.seriesIndex) {
											previousPoint = item.seriesIndex;
											/* 将显示的百分数值取小数点后两位 */
											var tip = item.series['label'] + " : "
													+ item.series['percent'].toString().substring(0, 5)
													+ '%';
											$tooltip.show().children(0).text(tip);
										}
										$tooltip.css({
											top : pos.pageY + 10,
											left : pos.pageX + 10
										});
									} else {
										$tooltip.hide();
										previousPoint = null;
									}
								});

								place_mem.on('plothover', function(event, pos, item) {
									if (item) {
										if (previousPoint != item.seriesIndex) {
											previousPoint = item.seriesIndex;
											/* 将显示的百分数值取小数点后两位 */
											var tip = item.series['label'] + " : "
													+ item.series['percent'].toString().substring(0, 5)
													+ '%';
											$tooltip.show().children(0).text(tip);
										}
										$tooltip.css({
											top : pos.pageY + 10,
											left : pos.pageX + 10
										});
									} else {
										$tooltip.hide();
										previousPoint = null;
									}
								});
							}
							
							/*循环处理，将第一个tab页面设置为active，其他的取消active*/
							for (var count = 1, length = response.length; count < length; count++) {
								var appview_ele = response[count];
								var app_id = appview_ele.appId;

								if (active_tabid == app_id + '_tab') {
									continue;
								} else {
									$('#' + app_id + '_tab').removeClass(
											"active");
								}
							}
						}
					});
});

function drawCpuPieChart(place_cpu, cpu_data, position) {
	
	$.plot(place_cpu, cpu_data, {
		series : {
			pie : {
				show : true,
				/* 调整显示饼状图的扁度 */
				tilt : 0.7,
				highlight : {
					opacity : 0.25
				},
				stroke : {
					color : '#fff',
					width : 2
				},
				/*从左上角的象限开始显示饼状图，符合阅读习惯*/
				startAngle : 3
			}
		},
		legend : {
			show : true,
			position : position || "ne",
			labelBoxBorderColor : null,
			margin : [ -30, 15 ]
		},
		grid : {
			hoverable : true,
			clickable : true
		}
	});
}


function drawMemPieChart(place_mem, mem_data, position) {
	$.plot(place_mem, mem_data, {
		series : {
			pie : {
				show : true,
				/* 调整显示饼状图的扁度 */
				tilt : 0.7,
				highlight : {
					opacity : 0.25
				},
				stroke : {
					color : '#fff',
					width : 2
				},
				/*从左上角的象限开始显示饼状图，符合阅读习惯*/
				startAngle : 3
			}
		},
		legend : {
			show : true,
			position : position || "ne",
			labelBoxBorderColor : null,
			margin : [ -30, 15 ]
		},
		grid : {
			hoverable : true,
			clickable : true
		}
	});
}

function JumpToHostIndex(host_type) {
	window.location.href = "host/index.html?hostType=" + host_type;
}

/** 根据容器的状态，显示全部容器的信息（0：全部，1：开启，2关闭）* */
function JumpToContainerIndex(container_status) {
	window.location.href = "appRelease/power/" + container_status + ".html";
}

/**
 * 
 * @param id[元素id]
 * @param func[click事件方法名]
 * @param color[数量显示的颜色]
 * @param num[数量]
 */
function showDashboard(id,func,color,num,flag){
//	alert(id+"----"+func+"----"+color+"----"+num+"----"+flag+"-----"+$('#'+id).attr('name'));
	var dashboard="";
	if(flag==0){
		dashboard="<h4 class='bigger pull-right'><b><font color='"+color+"'>" + num + "</font></b></h4>";
	}else if(flag==1){
		dashboard="<h1 class='bigger pull-right'><b><font color='"+color+"'>" +num + "</font></b></h1>"
	}
	
	if($('#'+id).attr('name')=='active'){
		if(flag==0){
			$('#'+id).append('<a onclick="'+func+'" style="cursor:pointer">'+dashboard+'</a>');
		}else if(flag==1){
			$('#'+id).append('<a href="'+func+'">'+dashboard+'</a>');
		}
	}else{
		$('#'+id).append(dashboard);
	}
}
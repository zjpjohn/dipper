/** 设置请求图像的时间间隔数值，暂定为10秒* */
var time_interval = 10 * 1000;
/** 保存定时器返回的句柄值，方便之后的关闭操作。* */
var interval_handler;
/** 监控界面每行显示节点的数量 */
var FRONT_COLUMN = 6;

function changeInterval(second) {
	/* (1)获取用户设置时间间隔值 */
	time_interval = second * 1000;
	/* (2)关闭用户之前启动的定时器程序 */
	window.clearInterval(interval_handler);
	/* (3)启动新的定时器程序，并获取返回的句柄值 */
	interval_handler = setInterval("GetZabbixInfo()", time_interval);
}

function GetZabbixInfo() {
	/* 获取应用的ID信息，向后台请求应用维度的监控数据 */
	var app_id = $("#appId").val();
	/* 请求监控数据的方式，分别为：app：应用维度，cluster：集群维度 */
	// var type = $("#type").val();
	$.ajax({
		type : 'post',
		url : base + 'zabbix/queryAppView',
		data : {
			appId : app_id
		},
		dataType : 'json',
		success : function(array) {
			if (array.length > 0) {
				$.each(array,
						function(index, obj) {
							var host_id = obj.hostId;
							// var hostName = obj.hostName;
							var item_cpu = obj.itemCpuLoad;
							var item_mem = obj.itemMemLoad;
							var item_disk = obj.itemDiskLoad;
							
							/*在每个节点的右上角显示报警灯动态图标*/
							//$("#alert_image_"+host_id).html("<img src=\"..\/..\/img\/th_red_alert.gif\" width=\"16px\" height=\"16px\">");
							
							var value_str = item_cpu + "|" + item_mem + "|"
									+ item_disk;
							//console.log("value_str:" + value_str);

							/* 获得图像的引用 */
							var chartRef = getChartFromId("chart_" + host_id);
							if (chartRef) {
								/* 向动态图标中注入数据 */
								chartRef.feedData("&value=" + value_str);
							}
						});
			} else {
				console.log("从服务器段获取Zabbix监控数据失败。");
			}
		}
	});
}

$(document)
		.ready(
				function() {
					/* 获取应用的ID信息，向后台请求应用维度的监控数据 */
					var app_id = $("#appId").val();
					/* 请求监控数据的方式，分别为：app：应用维度，cluster：集群维度 */
					
					$
							.ajax({
								type : 'post',
								url : base + 'zabbix/queryAppView',
								data : {
									appId : app_id
								},
								dataType : 'json',
								/*同步请求，执行完毕后，才会向下执行*/
								async:false,  
								success : function(array) {
									//var host_total = array.length;
									/* (1)界面每行显示六个主机，首先根据array来确定行数和列数 */
									/* 向上取整，有小数，则整数部分加1 */
									// var quotient = Math.ceil(host_total /
									// FRONT_COLUMN);
									// var remainder = host_total %
									// FRONT_COLUMN;
									var insert_zxshow = "<ul style=\"height:480px;position:relative;\">";
									var row_counter = 1;
									var column_counter = 1;

									/* 向页面中绘出全部被节点的监控窗口 */
									$
											.each(
													array,
													function(index, obj) {
														var host_id = obj.hostId;
														var host_name = obj.hostName;

														/* 获取 */
														insert_zxshow += '<li class=\"gs_w \" data-row=\"'
																+ row_counter
																+ '\" data-col=\"'
																+ column_counter
																+ '\" data-sizex=\"1\" data-sizey=\"1\">';
														insert_zxshow += '<div align=\"center\">';
														insert_zxshow += '<b><font color=\"blue\">'
																+ host_name
																+ '</font></b>nbsp;nbsp;<span id=\"alert_image_'+host_id+'\"></span>';
														insert_zxshow += '</div>';
														insert_zxshow += '<div id=\"node_'
																+ host_id
																+ '\"></div>';
														insert_zxshow += '<div align=\"center\">';
														insert_zxshow += '<b><font color=\"#FF0000\">CPU&nbsp;&nbsp;&nbsp;</font></b><b>'
																+ '<font color=\"#00FF00\">内存&nbsp;&nbsp;&nbsp;</font></b>'
																+ '<b><font color=\"#0000FF\">磁盘</font></b>';
														insert_zxshow += '</div></li>';

														if ((index + 1)
																% FRONT_COLUMN == 0) {
															/* 行数自加1 */
															row_counter++;
															/* 列数回归为1 */
															column_counter = 1;
														} else {
															/* 每次循环，列数自加1 */
															column_counter++;
														}
													});

									insert_zxshow += "</ul>";
									//console.log("insert_zxshow:"
									//		+ insert_zxshow);
									$("#zabbix_show").append(insert_zxshow);
								}
							});

					$.ajax({
						type : 'post',
						url : base + 'zabbix/queryAppView',
						data : {
							appId : app_id
						},
						dataType : 'json',
						success : function(array) {
							/* 注入显示的flash画板信息 */
							if (array.length > 0) {
								$.each(array, function(index, obj) {
									var host_id = obj.hostId;
									var m = new FusionCharts(
											"/charts/RealTimeLine.swf",
											"chart_" + host_id, "140", "100",
											"0", "1");
									m.setDataURL("/dataprovider/RAMinfo.xml");
									m.render("node_" + host_id);
								});
							} else {
								console.log("从服务器段获取Zabbix监控数据失败。");
							}
						}
					});

					/* 启动定时器并获取返回的句柄信息 */
					interval_handler = setInterval("GetZabbixInfo()",
							time_interval);
				});

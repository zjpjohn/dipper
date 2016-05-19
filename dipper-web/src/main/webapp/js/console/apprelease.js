var Status = {};
Status.POWER = {
	OFF : 0,
	UP : 1
};
/** 保存当前高级查询条件的列数量 */
var advanceColNum = 0;

var grid_selector = "#apprelease_list";
var page_selector = "#apprelease_page";
jQuery(function($) {
	$(window).on('resize.jqGrid', function() {
		$(grid_selector).jqGrid('setGridWidth', $(".page-content").width());
		$(grid_selector).closest(".ui-jqgrid-bdiv").css({
			'overflow-x' : 'hidden'
		});
	});
	var parent_column = $(grid_selector).closest('[class*="col-"]');
	$(document).on(
			'settings.ace.jqGrid',
			function(ev, event_name, collapsed) {
				if (event_name === 'sidebar_collapsed'
						|| event_name === 'main_container_fixed') {
					setTimeout(function() {
						$(grid_selector).jqGrid('setGridWidth',
								parent_column.width());
					}, 0);
				}
			});
	jQuery(grid_selector)
			.jqGrid(
					{
						url : base + 'appRelease/list',
						datatype : "json",
						height : '100%',
						autowidth : true,
						colNames : [ 'ID', 'imageId', 'balanceId', '应用名称',
								'应用版本', '总实例', '运行实例', '维护实例', '更新时间', '版本名称',
								'应用cpu' ],
						colModel : [
								{
									name : 'appId',
									index : 'appId',
									width : 10,
									hidden : true
								},
								{
									name : 'imageId',
									index : 'imageId',
									width : 10,
									hidden : true
								},
								{
									name : 'balanceId',
									index : 'balanceId',
									width : 10,
									hidden : true
								},
								{
									name : 'appName',
									index : 'appName',
									width : 20,
									cellattr : function(rowId, tv, rawObject,
											cm, rdata) {
										// 合并单元格
										return 'id=\'appName' + rowId + "\'";
									},
									formatter : function(cellvalue, options,
											rowObject) {
										return '<a href="' + base
												+ 'appRelease/detail/'
												+ rowObject.appId + '/'
												+ rowObject.imageId + '/'
												+ rowObject.balanceId
												+ '.html"> ' + cellvalue
												+ '</a>';
									}
								}, {
									name : 'appVersion',
									index : 'appVersion',
									width : 20
								}, {
									name : 'appNum',
									index : 'appNum',
									align : 'center',
									width : 15
								}, {
									name : 'runNum',
									index : 'runNum',
									align : 'center',
									width : 15
								},

								{
									name : 'maintenanceNum',
									index : 'maintenanceNum',
									align : 'center',
									width : 15
								},

								{
									name : 'updateTime',
									index : 'updateTime',
									width : 25
								}, {
									name : 'imageName',
									index : 'imageName',
									width : 10,
									hidden : true

								}, {
									name : 'appCpu',
									index : 'appCpu',
									width : 10,
									hidden : true

								}
						/*
						 * { name : 'appUrl', index : 'appUrl', width : 30,
						 * cellattr : function(rowId, tv, rawObject, cm, rdata) { //
						 * 合并单元格 return 'id=\'appUrl' + rowId + "\'"; },
						 * formatter : function(cellvalue, options, rowObject) {
						 * return '<a href="' + rowObject.appUrl + '"
						 * target="_blank">' + rowObject.appUrl + '</i>'; } }
						 */],
						viewrecords : true,
						rowNum : 10,
						rowList : [ 10, 20, 50, 100, 1000 ],
						pager : page_selector,
						altRows : true,
						multiselect : true,
						/** 只有点击行首多选框才能选择行，否则不可选择。 */
						// beforeSelectRow:function(rowid,e){
						// var
						// cbsdis=$('tr#'+rowid+'.jqgrow>td>input.cbox:disabled',grid[0]);
						// if(cbsdis==0){
						// return true;
						// }else{
						// return false;
						// }
						// },
						jsonReader : {
							root : "rows",
							total : "total",
							page : "page",
							records : "records",
							repeatitems : false
						},
						loadComplete : function() {
							var table = this;
							setTimeout(function() {
								styleCheckbox(table);
								updateActionIcons(table);
								updatePagerIcons(table);
								enableTooltips(table);
							}, 0);
						},
						gridComplete : function() {
							// ②在gridComplete调用合并方法
							var gridName = "container_list";
							Merger(gridName, 'appName');
							Merger(gridName, 'appUrl');
						}
					});

	$(window).triggerHandler('resize.jqGrid'); // 窗口resize时重新resize表格，使其变成合适的大小
	jQuery(grid_selector).jqGrid(
			'navGrid',
			page_selector,
			{
				edit : false,
				add : false,
				del : false,
				search : false,
				searchicon : 'ace-icon fa fa-search',
				refresh : true,
				refreshstate : 'current',
				refreshicon : 'ace-icon fa fa-refresh',
				view : false
			},
			{
				recreateForm : true,
				afterShowSearch : function(e) {
					var form = $(e[0]);
					form.closest('.ui-jqdialog').find('.ui-jqdialog-title')
							.wrap('<div class="widget-header" />');
					style_search_form(form);
				},
				afterRedraw : function() {
					style_search_filters($(this));
				},
				multipleSearch : true
			});

	// 合并调用方法
	function Merger(gridName, CellName) {
		// 得到显示到界面的id集合
		var mya = $("#" + gridName + "").getDataIDs();
		// 当前显示多少条
		var length = mya.length;
		for (var i = 0; i < length; i++) {
			// 从上到下获取一条信息
			var before = $("#" + gridName + "").jqGrid('getRowData', mya[i]);
			// 定义合并行数
			var rowSpanTaxCount = 1;
			for (j = i + 1; j <= length; j++) {
				// 和上边的信息对比 如果值一样就合并行数+1 然后设置rowspan 让当前单元格隐藏
				var end = $("#" + gridName + "").jqGrid('getRowData', mya[j]);
				if (before[CellName] == end[CellName]) {
					rowSpanTaxCount++;
					$("#" + gridName + "").setCell(mya[j], CellName, '', {
						display : 'none'
					});
				} else {
					rowSpanTaxCount = 1;
					break;
				}
				$("#" + CellName + "" + mya[i] + "").attr("rowspan",
						rowSpanTaxCount);
			}
		}
	}

	function style_search_filters(form) {
		form.find('.delete-rule').val('X');
		form.find('.add-rule').addClass('btn btn-xs btn-primary');
		form.find('.add-group').addClass('btn btn-xs btn-success');
		form.find('.delete-group').addClass('btn btn-xs btn-danger');
	}
	function style_search_form(form) {
		var dialog = form.closest('.ui-jqdialog');
		var buttons = dialog.find('.EditTable');
		buttons.find('.EditButton a[id*="_reset"]').addClass(
				'btn btn-sm btn-round btn-info').find('.ui-icon').attr('class',
				'ace-icon fa fa-retweet');
		buttons.find('.EditButton a[id*="_query"]').addClass(
				'btn btn-sm btn-round btn-inverse').find('.ui-icon').attr(
				'class', 'ace-icon fa fa-comment-o');
		buttons.find('.EditButton a[id*="_search"]').addClass(
				'btn btn-sm btn-round btn-purple').find('.ui-icon').attr(
				'class', 'ace-icon fa fa-search');
	}

	function updateActionIcons(table) {
	}

	function updatePagerIcons(table) {
		var replacement = {
			'ui-icon-seek-first' : 'ace-icon fa fa-angle-double-left bigger-140',
			'ui-icon-seek-prev' : 'ace-icon fa fa-angle-left bigger-140',
			'ui-icon-seek-next' : 'ace-icon fa fa-angle-right bigger-140',
			'ui-icon-seek-end' : 'ace-icon fa fa-angle-double-right bigger-140'
		};
		$('.ui-pg-table:not(.navtable) > tbody > tr > .ui-pg-button > .ui-icon')
				.each(
						function() {
							var icon = $(this);
							var $class = $.trim(icon.attr('class').replace(
									'ui-icon', ''));
							if ($class in replacement)
								icon.attr('class', 'ui-icon '
										+ replacement[$class]);
						});
	}

	function enableTooltips(table) {
		$('.navtable .ui-pg-button').tooltip({
			container : 'body'
		});
		$(table).find('.ui-pg-div').tooltip({
			container : 'body'
		});
	}
	function styleCheckbox(table) {
	}
	current = 0;

	/**
	 * Next page, Pre page
	 */
	// currentStep = 0;
	$('#modal-wizard .modal-header')
			.ace_wizard()
			.on(
					'change',
					function(e, info) {
						if (info.step == 1) { // 第一步需要执行的操作
							/*
							 * var appId = $('#app_select
							 * option:selected').val(); var imageid =
							 * $('.imagelist').find('.selected')
							 * .attr("imageid"); if (appId == 0) {
							 * e.preventDefault(); showMessage("请选择应用,
							 * 再进行下一步操作！"); return; } if (imageid == undefined) {
							 * e.preventDefault();
							 * showMessage("请选择应用版本，再进行下一步操作！"); return; }
							 */
							var envid = $('#env_select option:selected').val();
							if (envid == 0) {
								e.preventDefault();
								showMessage("请选择应用环境, 再进行下一步操作！");
							}
							var appId = $('#app_select').attr('appId');
							getClusters(appId);
							getParams();
						} else if (info.step == 2) { // 第二步需要执行的操作
							if (info.direction == 'next') {
								$('#release_info').empty();
								var appInfos = "";
								// var appName = $('#app_select
								// option:selected')
								// .text();
								// var appVersion = $('.imagelist').find(
								// '.selected').text().split(':')[1];
								var appName = $('#app_select').val();
								var appVersion = $('#app_version').val();
								var clusterName = $(
										'#cluster_select option:selected')
										.text();
								if (clusterName == "") {
									e.preventDefault();
									showMessage("请选择应用集群，再进行下一步操作！");
								}
								var releaseMode = $(
										'#release_mode option:selected').val();
								var releaseModeName = $(
										'#release_mode option:selected').text();
								var releaseNum = $('#release_num').val();
								if (releaseNum <= 0) {
									e.preventDefault();
									showMessage("发布实例必须大于0");
								}
								var release_desc = $.trim($('#release_desc').val());
								if(release_desc!=''){
									if(!(/^[a-zA-Z0-9\u4e00-\u9fa5-_\s]{0,200}$/.test(release_desc))){
										e.preventDefault();
										showMessage('描述信息只能包含中文、英文、数字、下划线、空格等字符且小于200字符！');
									}
								}
								
								appInfos += '<div class="item"><label>应用名称：</label> <label>'
										+ appName + '</label></div>';
								appInfos += '<div class="item"><label>应用版本：</label> <label>'
										+ appVersion + '</label></div>';
								appInfos += '<div class="item"><label>应用集群：</label> <label>'
										+ clusterName + '</label></div>';
								appInfos += '<div class="item"><label>发布方式：</label> <label>'
										+ releaseModeName + '</label></div>';
								if (releaseMode == 1) {// 灰度发布
									// 获取选择的应用版本
									var imgTag = $
											.trim($('#release_tag').val());
									if (imgTag == 0) {
										e.preventDefault();
										showMessage("该应用下没有可替换的版本，请选择其他发布方式！");
									}

									// 替换方式 0：实例数替换 1：百分比替换
									var releaseType = $(
											'#myTab>li[class="active"]')
											.index();
									// 可替换最大实例数
									var instanceMax = Number($('#release_tag')
											.val());
									if (isNaN(instanceMax)) {
										e.preventDefault();
										showMessage("该应用下没有可替换的版本，请选择其他发布方式！");
									}
									appInfos += '<div class="item"><label>替换版本：</label> <label>'
											+ $('#release_tag>option:selected')
													.text() + '</label></div>';
									// 替换的实例数
									var release = 0;
									// 最后一步信息展示
									var param, paramVal;
									// 替换实例数和百分比输入校验
									if (releaseType == 0) {
										var replaceNum = Number($(
												'#replace_num').val());
										if (instanceMax >= replaceNum
												&& replaceNum > 0) {
											release = replaceNum;
										} else {
											e.preventDefault();
											showMessage("替换实例数必须大于0，同时小于"
													+ instanceMax);
											return;
										}
										param = "替换实例数";
										paramVal = replaceNum;
									} else if (releaseType == 1) {
										var replacePercent = Number($(
												'#replace_percent').val());
										if (100 > replacePercent
												&& replacePercent > 0) {
											release = Math.ceil(instanceMax
													* replacePercent / 100);
										} else {
											e.preventDefault();
											showMessage("替换实例百分比必须大于0，同时小于100");
										}
										param = "替换百分比";
										paramVal = replacePercent + "%";
									}
									appInfos += '<div class="item"><label>'
											+ param + '：</label> <label>'
											+ paramVal + '</label></div>';
								} else {
									if (releaseMode == 2) {// 特殊发布
										// 环境
										var env = '';
										$.each($('#env_params li'), function() {
											var env_value = $(this).find(
													'#release_env').val();
											if (env_value !== "") {
												env += "-e " + env_value + ' ';
											}
										});
										if (env.trim() !== '') {
											appInfos += '<div class="item"><label>环境变量：</label> <label>'
													+ env + '</label></div>';
										}
										// 挂载点
										var volume = '';
										$
												.each(
														$('#vol_params li'),
														function() {
															var hostVolume = $(
																	this)
																	.find(
																			'#host_volume')
																	.val();
															var containerVolume = $(
																	this)
																	.find(
																			'#container_volume')
																	.val();
															if (hostVolume
																	.trim() !== ""
																	&& containerVolume
																			.trim() !== "") {
																volume += hostVolume
																		+ ":"
																		+ containerVolume
																		+ " ";
															}
														});
										if (volume.trim() !== '') {
											appInfos += '<div class="item"><label>挂载点：</label> <label>'
													+ volume + '</label></div>';
										}
										// 其他参数
										var param = '';
										$
												.each(
														$("#params li"),
														function() {
															var pk = $(this)
																	.find(
																			"#meter option:selected")
																	.val();
															if (pk != 0) {
																var pConn = $(
																		this)
																		.find(
																				"#meter option:selected")
																		.attr(
																				"conn");
																pConn = pConn == 0 ? ' '
																		: '=';
																var pv = $(this)
																		.find(
																				"#param_value")
																		.val();
																param += (pk
																		+ pConn
																		+ pv + " ");
															}

														});
										if (param.trim() !== '') {
											appInfos += '<div class="item"><label>其他参数：</label> <label>'
													+ param + '</label></div>';
										}
										var command = $('#release_command')
												.val();
										if (command.trim() !== '') {
											appInfos += '<div class="item"><label>启动命令：</label> <label>'
													+ command
													+ '</label></div>';
										}
									}
									appInfos += '<div class="item"><label>发布实例：</label> <label>'
											+ releaseNum + '</label></div>';
								}

								appInfos += '<div class="item"><label>备注信息：</label> <label>'
										+ release_desc + '</label></div>';
								$('#release_info').html(appInfos);
							} else if (info.direction = 'previous') {
							}
						}
					})
			.on(
					'finished',
					function(e) { // 最后一步需要执行的操作
						e.preventDefault();
						// var appId = $('#app_select option:selected').val();
						// var imageId = $('.imagelist').find('.selected').attr(
						// "imageid");
						// var imageUrl =
						// $('.imagelist').find('.selected').attr(
						// "imageUrl");

						var appId = $('#app_select').attr('appId');
						var imageId = $('#app_version').attr('imageId');
						var imageUrl = $('#app_version').attr('imageUrl');
						var env_id = $.trim($('#env_select').val());

						var clusterId = $('#cluster_select option:selected')
								.val();
						var releaseMode = $('#release_mode option:selected')
								.val();
						// 资源限制

						var cpu = $('input:radio[name="cpu-radio"]:checked')
								.val();
						cpu = cpu == undefined ? 0 : cpu;
						var mem = $('input:radio[name="mem-radio"]:checked')
								.val();
						mem = mem == undefined ? 0 : Number(mem) * 1024;
						// 环境变量
						var env = '';
						$.each($('#env_params li'), function() {
							var envstr = $(this).find('#release_env').val();
							if (envstr != "") {
								env += '-e ' + envstr + ' ';
							}
						});
						// 挂载点
						var volume = '';
						$.each($('#vol_params li'),
								function() {
									var hostVolume = $(this).find(
											'#host_volume').val();
									var containerVolume = $(this).find(
											'#container_volume').val();
									if (hostVolume.trim() !== ""
											&& containerVolume.trim() !== "") {
										volume += hostVolume + ":"
												+ containerVolume + " ";
									}
								});
						// 其他参数
						var param = '';
						$.each($("#params li"), function() {
							var pk = $(this).find("#meter option:selected")
									.val();
							if (pk != 0) {
								var pConn = $(this).find(
										"#meter option:selected").attr("conn");
								pConn = pConn == 0 ? ' ' : '=';
								var pv = $(this).find("#param_value").val();
								param += (pk + pConn + pv + " ");
							}

						});
						// 启动命令
						var command = $('#release_command').val();
						// 健康检查开关
						var appHealth = $('#app_health').is(':checked') == true ? 1
								: 0;
						// 添加监控开关
						var appMonitor = $('#app_monitor').is(':checked') == true ? 1
								: 0;
						// 计算替换的实例数
						var releaseNum = 0;
						// 被替换版本镜像id
						var oldImageid = 0;
						if (releaseMode == 1) {
							oldImageid = $('#release_tag>option:selected')
									.attr('imageid');
							// 替换方式 0：实例数替换 1：百分比替换
							var releaseType = $('#myTab>li[class="active"]')
									.index();
							// 可替换最大实例数
							var instanceMax = Number($('#release_tag').val());
							// 替换的实例数
							if (releaseType == 0) {
								releaseNum = Number($('#replace_num').val());
							} else if (releaseType == 1) {
								var replacePercent = Number($(
										'#replace_percent').val());
								releaseNum = Math.ceil(instanceMax
										* replacePercent / 100);
							}
						} else {
							releaseNum = $('#release_num').val();
						}
						var appDesc = $('#release_desc').val();
						var data = {
							appId : appId,
							imageId : imageId,
							imageUrl : imageUrl,
							clusterId : clusterId,
							releaseMode : releaseMode,
							cpu : cpu,
							mem : mem,
							env : env,
							volume : volume,
							param : param,
							command : command,
							appHealth : appHealth,
							appMonitor : appMonitor,
							releaseNum : releaseNum,
							appDesc : appDesc,
							oldImageId : oldImageid,
							envId : env_id
						};
						$('#apprelease_pro').show();
						var balanceId = $('#balanceId').val();
						$
								.ajax({
									type : 'post',
									url : base + 'appRelease/release',
									dataType : 'json',
									data : data,
									success : function(response) {
										$('#progressModal').hide();
										if (response == "") {
											showMessage(
													"应用发布失败：应用服务器异常",
													function() {
														$(grid_selector)
																.trigger(
																		"reloadGrid");
														location.reload();
													});
										} else {
											if (response.success) {
												$(grid_selector).trigger(
														"reloadGrid");
												if (balanceId != -1) {
													bootbox
															.dialog({
																message : '<div class="alert alert-info" style="margin:10px"><span class="glyphicon glyphicon-info-sign"></span>&nbsp;应用发布成功，是否更新负载均衡&nbsp;'
																		+ '?</div>',
																title : "提示",
																buttons : {
																	cancel : {
																		label : "取消",
																		className : "btn-danger btn-round",
																		callback : function() {
																			location
																					.reload();
																		}
																	},
																	main : {
																		label : "确定",
																		className : "btn-success btn-round",
																		callback : function() {
																			if (releaseMode == 1) {
																				reloadBalance(
																						response.message,
																						2);
																				$(
																						grid_selector)
																						.trigger(
																								"reloadGrid");
																				location
																						.reload();
																			} else {
																				reloadBalance(
																						response.message,
																						0);
																				$(
																						grid_selector)
																						.trigger(
																								"reloadGrid");
																				location
																						.reload();
																			}
																		}
																	}
																}
															});
												} else {
													showMessage("应用发布成功！");
													$(grid_selector).trigger(
															"reloadGrid");
													location.reload();
												}
											} else {
												showMessage(response.message,
														function() {
															location.reload();
														});
											}
										}
									},
									error : function(response) {
										$('#progressModal').hide();
										showMessage("应用发布失败：应用服务器异常",
												function() {
													$(grid_selector).trigger(
															"reloadGrid");
													location.reload();
												});
									}
								});
						$('#apprelease_pro').show();
						// $('#modal-wizard').hide();
						// $('.modal-backdrop').hide();
						// $('#modal-wizard .modal-header').ace_wizard({
						step: 1
						// });
						// $('#modal-wizard .wizard-actions').show();
					});
	$('#modal-wizard .wizard-actions .btn[data-dismiss=modal]').removeAttr(
			'disabled');

	// 获取所有的环境
	/*
	 * getEnvlist(); function getEnvlist() { $.ajax({ type : 'get', url : base +
	 * 'env/listAll', dataType : 'json', success : function(array) {
	 * $.each(array, function(index, obj) { var envId = obj.envId; var envName =
	 * decodeURIComponent(obj.envName); $('#env_select').append( '<option
	 * value="' + envId + '">' + envName + '</option>'); }); } }); }
	 */

	/**
	 * Get application list
	 * 
	 */
	function getApplist(envId) {
		$('#app_select').empty();
		/** @bug300_begin:应用"发布环境"页面,当下拉列表选择一个可用环境后再返回无选择状态,发布应用下拉列表的提示不匹配 */
		if (envId == 0) {
			$('#app_select').append('<option value="0">请选择待发布的应用</option>');
			return;
		}
		/** @bug300_finish* */

		$
				.ajax({
					type : 'post',
					url : base + 'app/listByEnvId',
					data : {
						envId : envId
					},
					dataType : 'json',
					success : function(array) {
						if (array.length > 0) {
							$('#app_select').append(
									'<option value="0">请选择待发布的应用</option>');
							$.each(array, function(index, obj) {
								var appid = obj.appId;
								var appname = decodeURIComponent(obj.appName);
								var balanceId = obj.balanceId;
								$('#app_select').append(
										'<option balanceId="' + balanceId
												+ '" value="' + appid + '">'
												+ appname + '</option>');
							});
						} else {
							$('#app_select')
									.append(
											'<option style="color:red" value="-1">当前环境下无应用可发布</option>');
						}
					}
				});
	}

	/**
	 * Get cluster list
	 * 
	 */
	function getClusters(appId) {
		$('#cluster_select').html("<option value='0'>请选择集群</option>");
		$.ajax({
			type : 'get',
			url : base + 'cluster/clusterInApp',
			data : {
				appId : appId
			},
			dataType : 'json',
			success : function(array) {
				$.each(array, function(index, obj) {
					var clusterId = obj.clusterId;
					var clustername = decodeURIComponent(obj.clusterName);
					$('#cluster_select').append(
							'<option value="' + clusterId + '" restype="'
									+ obj.resType + '">' + clustername
									+ '</option>');
				});
			}
		});
	}

	/**
	 * Get parameters list
	 * 
	 */
	function getParams() {
		$('#meter').html("<option value='0'>启动参数</option>");
		$.ajax({
			type : 'get',
			url : base + 'param/allValue',
			dataType : 'json',
			success : function(array) {
				$.each(array, function(index, obj) {
					var pv = obj.paramValue;
					var pn = decodeURIComponent(obj.paramName);
					var pr = obj.paramReusable;
					var pc = obj.paramConnector;
					var pt = obj.paramType;
					var comment = obj.paramComment;
					$('#meter').append(
							'<option type="' + pt + '" value="' + pv
									+ '" conn="' + pc + '" comment="' + comment
									+ '">' + pn + '</option>');

				});
			}
		});
	}

	/*
	 * Get selected image from imageList
	 * 
	 */
	/*
	 * $('.imagelist').on('click', '.image-item', function(event) {
	 * event.preventDefault(); $('div',
	 * $('#imagelist')).removeClass('selected'); $(this).addClass('selected');
	 * });
	 */

	/**
	 * Release type change
	 */
	$('#release_mode').change(function() {
		load_releaseMode();
	});

	// 添加环境变量
	$("#env-plus").on('click', function(event) {
		$("#env_params li:first").clone(true).appendTo("#env_params");
		$("#env_params li").not(":first").find("#env-minus").show();
		$("#env_params li").not(":last").find("#env-plus").hide();
		$("#env_params li:first").find("#env-minus").hide();
		$("#env_params li:last").find("#env-plus").show();
		$("#env_params li:last").find("#release_env").val("");
	});

	// 删除一个环境变量
	$("#env-minus").click(function() {
		if ($("#env_params li").length > 1) {
			$(this).parent().remove();
		}
		$("#env_params li:last").find("#env-plus").css('display', '');
	});
	// 添加挂载点
	$("#vol-plus").on('click', function(event) {
		$("#vol_params li:first").clone(true).appendTo("#vol_params");
		$("#vol_params li").not(":first").find("#vol-minus").show();
		$("#vol_params li").not(":last").find("#vol-plus").hide();
		$("#vol_params li:first").find("#vol-minus").hide();
		$("#vol_params li:last").find("#vol-plus").show();
		$("#vol_params li:last").find("#host_volume").val("");
		$("#vol_params li:last").find("#container_volume").val("");
	});

	// 删除一个挂载点
	$("#vol-minus").click(function() {
		if ($("#vol_params li").length > 1) {
			$(this).parent().remove();
		}
		$("#vol_params li:last").find("#vol-plus").css('display', '');
	});

	// 添加其他参数
	$("#param-plus").on('click', function(event) {
		$("#params li:first").clone(true).appendTo("#params");
		$("#params li").not(":first").find("#param-minus").show();
		$("#params li").not(":last").find("#param-plus").hide();
		$("#params li:first").find("#param-minus").hide();
		$("#params li:last").find("#param-plus").show();
		$("#params li:last").find("#param_value").val("");
		$("#params li:last").find("#param_value").attr("placeholder", "");
	});

	// 删除一个其他参数
	$("#param-minus").click(function() {
		if ($("#params li").length > 1) {
			$(this).parent().remove();
		}
		$("#params li:last").find("#param-plus").css('display', '');
	});

	// 根据选择提示输入信息
	$("#meter").change(function() {
		console.log($(this).parent().next());
		var comment = $(this).children('option:selected').attr('comment');
		var type = $(this).children('option:selected').attr('type');
		if (type == 0) {
			$(this).parent().next().attr('placeholder', '参数不需要输入值');
			$("#param_value").attr("disabled", true);
		} else {
			$("#param_value").attr("disabled", false);
			$(this).parent().next().attr("placeholder", comment);

		}
	});

	$("#cancel").click(function() {
		location.reload();
	});

	$("#search").click(function() {
		var searchName = $('#search_name').val();
		/* 校验不符合条件的输入结果 */
		if (!isValidateValidName(searchName)) {
			showMessage("请输入正确的应用名称(包含中文、英文和数字)！");
			$('#search_name').val("").focus();
			return;
		}
		jQuery(grid_selector).jqGrid('setGridParam', {
			url : base + 'appRelease/listSearch?appName=' + searchName
		}).trigger("reloadGrid");
	});

	var paragraph = 4 * 1024 * 1024;
	// 64*1024
	var file;
	var startSize, endSize = 0;
	var i = 0;
	var j = 0;
	var count = 0;
	$(function() {
		socket.close();
		connect();
	});

	function connect() {
		if ('WebSocket' in window) {
			var host = window.location.host;
			socket = new WebSocket('ws://' + host + '/messagingService');
			socket.onopen = function() {
			};
			socket.onclose = function() {
			};
			socket.onmessage = function(event) {
				var obj = JSON.parse(event.data);
				message = JSON.parse(obj.content);
				if (obj.messageType == 'ws_sticky') {
					if (message.title == "应用发布") {
						$("#pre").attr('disabled', true);
						$("#next").attr('disabled', true);
						$("#cancel").attr('disabled', true);
						/** message的消息由进度值#提示内容组成* */
						var prog_msg = message.message.split('#');
						if (!isNaN(parseInt(prog_msg[0]))) {
							var progress = Number(prog_msg[0]);
							var show_msg = prog_msg[1];
							$("#prog_span").html(
									"<font color=\"black\"><b>" + progress
											+ "&nbsp;%</b></font>");
							$("#progress_ar").width(progress + "%");
							$("#apprelease_msg").html(
									"<font color=\"blue\">" + show_msg
											+ "</font>");
							/** 镜像已经处理完成* */
							if (progress == 100) {
								// 因此窗口
								tailinWork();
							}
						} else {
							var progress = prog_msg[0];
							var show_msg = prog_msg[1];
							if (progress == "fail") {
								$("#prog_span")
										.html(
												"<font color=\"black\"><b>0&nbsp;%</b></font>");
								$("#progress_ar").width("0%");
								$("#apprelease_msg").html(
										"<font color=\"red\"><b>" + show_msg
												+ "</b></font>");

							} else if (progress == "success") {
								$("#prog_span")
										.html(
												"<font color=\"black\"><b>100&nbsp;%</b></font>");
								$("#apprelease_msg").html(
										"<font color=\"blue\"><b>" + show_msg
												+ "</b></font>");
							}
						}
					}
				}

			};
		} else {
			console.log('Websocket not supported');

		}

	}

	function tailinWork() {
		$('#apprelease_pro').hide();
		$('#modal-wizard').hide();
		$('.modal-backdrop').hide();
		$('#modal-wizard .modal-header').ace_wizard({
			step : 1
		});
		$('#modal-wizard .wizard-actions').show();
	}

	/**
	 * 添加高级搜索的参数项
	 */
	/** @bug68594:高级查询功能增加条列数目没有限制 */
	/* 获取查询条件的总数 */
	advanceColNum = $('#ar_meter option').length - 1;

	$("#ar_add-param").on('click', function(event) {
		/* 获取当前已经添加的列数量 */
		var selNum = $('#ar_params li').length + 1;
		event.preventDefault();
		$("#ar_params li:first").clone(true).appendTo("#ar_params");
		$("#ar_params li").not(":first").find("#ar_remove-param").show();
		$("#ar_params li:first").find("#ar_remove-param").hide();
		var str = $("#ar_params li:last").find("#ar_meter").val();
		/** @bug152_begin 新增查询参数时，新增栏参数内容置空 */
		$("#ar_params li:last").find("#ar_param_value").val("");
		/** @bug152_finish */
		/* 当添加的列数量与参数数量相等数，隐藏添加条件按钮 */
		if (selNum >= advanceColNum) {
			$("#ar_add-param").hide();
		}
	});

	/**
	 * 删除高级索索的参数项
	 */
	$("#ar_remove-param").on('click', function(event) {
		/* 获取当前已经添加的列数量 */
		var selNum = $('#ar_params li').length;
		event.preventDefault();
		/* 判断当列数量小于参数数量的时候，显示添加条件按钮 */
		if (selNum <= advanceColNum) {
			$("#ar_add-param").show();
		}
		if ($("#ar_params li").length > 1) {
			$(this).parent().remove();
		}
	});

	/**
	 * 向查询按钮添加请求提交操作 删除高级索索的参数项
	 */
	$("#ar_advanced_search").on(
			'click',
			function(event) {
				/* 保存各项栏目的名称数组 */
				var column_array = new Array();
				/* 保存用户填写的各项信息数组 */
				var value_array = new Array();

				/* 获取选择栏目的名称 */
				$("select[name=ar_meter]").each(function() {
					column_array.push($(this).val());

				});

				/** @bug208_begin [高级查询]当输入重复的条件列及搜索值,查询结果取其交集，禁用重复选择******* */
				/** 判断查询的键值是否存在重复的内容。* */
				var sort_array = column_array.sort();
				for (var count = 0; count < sort_array.length; count++) {
					if (sort_array[count] == sort_array[count + 1]) {
						showMessage("查询【列名称】选择重复，请重新选择后进行查询！");
						return;
					}
				}
				/** @bug208_finish************************************** */

				/** 在高级搜索中增加对于空值的判断返回处理 */
				var bEmptyParam = false;
				/* 获取填写的参数值信息 */
				$("input[name=ar_param_value]").each(function() {
					var param_value = $(this).val();
					if (param_value.length < 1) {
						bEmptyParam = true;
						return;
					} else {
						value_array.push($(this).val());
					}
				});
				if (bEmptyParam) {
					showMessage("高级查询中【搜索参数】出现空值，请重新选择填写！");
					return;
				}

				/* 查询是否存在关键词相关的结果 */
				jQuery(grid_selector).jqGrid(
						'setGridParam',
						{
							url : base + 'appRelease/advancedSearch?params='
									+ column_array + '&values=' + value_array
						}).trigger("reloadGrid");

				/** @bug152_begin 清空用户多选的参数 */
				while ($("#ar_params li").length > 1) {
					$("#ar_remove-param").parent().remove();
				}
				/** @bug152_finish */

				$('#advanSearchARModal').modal('hide');
				$('#advanced_search_ar_frm')[0].reset();
			});

	/**
	 * 向高级搜索的取消按钮添加重置隐藏
	 */

	$("#ar_advanced_cancel").on('click', function(event) {
		event.preventDefault();
		$('#advanSearchARModal').modal('hide');
		$('#advanced_search_ar_frm')[0].reset();
	});

	// 灰度发布设置可替换应用实例最大值
	$('#release_tag').on('change', function() {
		gatedLaunch();
	})

	// 选择集群时 根据应用模板cpu限制 限制发布实例数
	$('#cluster_select').on('change', function() {
		load_releaseMode();
	});

	// 特殊发布资源约束 限制发布实例个数
	$(':radio[name="cpu-radio"]').click(function() {
		specialRelease();
	})
	// 初始化最大实例数
	load_releaseMode();
});

// 选择不同发布方式加载处理
function load_releaseMode() {
	var releaseMode = $('#release_mode').children('option:selected').val();
	switch (releaseMode) {
	case '0':
		$('#resource_form').hide();
		$('#env_form').hide();
		$('#vol_form').hide();
		$('#param_form').hide();
		$('#command_form').hide();
		$('#health_form').hide();
		$('#monitor_form').hide();
		$('#gray_policy').hide();
		$('#releaseNum').show();
		$('#release_version').hide();
		contrib();
		break;
	case '1':
		$('#resource_form').hide();
		$('#env_form').hide();
		$('#vol_form').hide();
		$('#param_form').hide();
		$('#command_form').hide();
		$('#health_form').hide();
		$('#monitor_form').hide();
		$('#gray_policy').show();
		$('#releaseNum').hide();
		$('#release_version').show();
		gatedLaunch();
		break;
	case '2':
		$('#resource_form').show();
		$('#env_form').show();
		$('#vol_form').show();
		$('#param_form').show();
		$('#command_form').show();
		$('#health_form').show();
		$('#monitor_form').show();
		$('#gray_policy').hide();
		$('#releaseNum').show();
		$('#release_version').hide();
		specialRelease();
		break;
	}
}
// 普通发布实例数限制
function contrib() {
	var clusterId = Number($('#cluster_select').val());
	var ids = $(grid_selector).jqGrid("getGridParam", "selarrrow");
	var rowData = $(grid_selector).jqGrid("getRowData", ids[0]);
	var appCpu = Number(rowData.appCpu);
	if (clusterId != 0 && appCpu != 0) {
		$('#releaseNum_html')
				.html(
						'<input type="text" class="input-mini" id="release_num" style="border: 1px solid #ccc;" />');
		setInputMax('release_num', getMaxIns(clusterId, appCpu));
	} else {
		$('#releaseNum_html')
				.html(
						'<input type="text" class="input-mini" id="release_num" style="border: 1px solid #ccc;" />');
		setInputMax('release_num', 100);
	}
}

// 灰度发布实例数限制
function gatedLaunch() {
	var clusterId = Number($('#cluster_select').val());
	var ids = $(grid_selector).jqGrid("getGridParam", "selarrrow");
	var rowData = $(grid_selector).jqGrid("getRowData", ids[0]);
	var appCpu = Number(rowData.appCpu);
	var insNum = getMaxIns(clusterId, appCpu);

	var tag_num = Number($('#release_tag').val());
	$('#replaceNum_html')
			.html(
					'<label>替换实例数：</label><input type="text" class="input-mini" id="replace_num" style="border: 1px solid #ccc;" />')
	if (insNum > tag_num) {
		setInputMax('replace_num', Number(tag_num));
	} else {
		setInputMax('replace_num', Number(insNum));
	}

	// 百分比替换
	setInputMax('replace_percent', 100);
}

// 特殊发布实例数限制
function specialRelease() {
	var clusterId = Number($('#cluster_select').val());
	var appCpu = Number($(':radio[name="cpu-radio"]:checked').val());
	$('#releaseNum_html')
			.html(
					'<input type="text" class="input-mini" id="release_num" style="border: 1px solid #ccc;" />')
	if (clusterId == 0 || appCpu == 0) {
		setInputMax('release_num', 100);
	} else {
		var insNum = getMaxIns(clusterId, appCpu);
		setInputMax('release_num', Number(insNum));
		if (Number(insNum) == 0) {
			$('#release_num').val(insNum);
		}
	}
}

/**
 * @author yangqinglin
 * @datetime 2015年11月3日 11:23
 * @description 添加高级查询函数
 */
function AdvancedSearchAR() {
	/** @bug152_begin 清空用户多选的参数 */
	while ($("#ar_params li").length > 1) {
		$("#ar_remove-param").parent().remove();
	}
	/* 隐藏高级查询第一行的删除打叉按钮 */
	$("#ar_params li:first").find("#ar_remove-param").hide();
	/** 打开高级搜索窗口，之前输入全部清空 */
	$("#ar_params li:first").find("#ar_param_value").val("");
	$("#ar_params li:first").find("#ar_meter").val("0");
	/** @bug152_finish */

	$('#advanSearchARModal').modal('show');

}

/**
 * Modify page status
 * 
 * @param current
 * @param total
 */
function modalPageUpdate(current, total) {
	$('#currentPtpl').html(current);
	$('#totalPtpl').html(total);
}

function reloadBalance(conIds, actionFlag) {
	$
			.ajax({
				type : 'post',
				url : base + 'loadbalance/reloadApp',
				data : {
					conIds : conIds,
					fileFlag : 0,
					actionFlag : actionFlag
				},
				dataType : 'json',
				success : function(response) {
					if (response == "") {
						showMessage("重新加载负载均衡异常！");
						$(grid_selector).trigger("reloadGrid");
					} else {
						var fileNames = response.message.split("#");
						var confList = '<div class="col-sm-9" style="margin-left:-20px;" id="confList">'
						for (var i = 0; i < fileNames.length; i++) {
							if (fileNames[i] != "") {
								confList += '<a style="font-size:14px;cursor:pointer;">'
										+ fileNames[i]
										+ '</a>'
										+ '&nbsp;&nbsp;&nbsp;<a style="font-size:14px;cursor:pointer;" onclick="readConfFile('
										+ "'"
										+ fileNames[i]
										+ "'"
										+ ')">显示</a>'
										+ '&nbsp;&nbsp;&nbsp;<a style="font-size:14px;cursor:pointer;" onclick="packup()">收起</a>';
							}
						}
						confList += '</div>';
						bootbox
								.dialog({
									message : '<div class="alert alert-info" style="margin:10px"><span class="glyphicon glyphicon-info-sign"></span>&nbsp;查看配置文件，是否继续更新负载均衡&nbsp;'
											+ '?'
											+ '<div class="well" id="content" style="margin-top:1px;">'
											+ '<form class="form-horizontal" role="form" id="modify_balance_frm">'
											+ '<div class="form-group" >'
											+ '<label class="col-sm-3" style="text-align:right;"><b>配置文件：</b></label>'
											+ confList
											+ '</div>'
											+ '<div class="form-group">'
											+ '<label class="col-sm-3" style="text-align:right;"><b>文件内容：</b></label>'
											+ '<div class="col-sm-9" style="margin-left:-20px;">'
											+ '<p id="confContent"></p>'
											+ '</div>'
											+ '</div>'
											+ '</form>'
											+ '</div>' + '</div>',
									title : "提示",
									buttons : {
										cancel : {
											label : "<i class='icon-info'></i> <b>取消</b>",
											className : "btn-sm btn-danger btn-round",
											callback : function() {
												$(grid_selector).trigger(
														"reloadGrid");
											}
										},
										main : {
											label : "<i class='icon-info'></i><b>继续</b>",
											className : "btn-sm btn-success btn-round",
											callback : function() {
												url = base
														+ 'loadbalance/reloadApp';
												data = {
													conIds : conIds,
													fileFlag : 1,
													actionFlag : actionFlag
												};
												// 显示遮罩层
												showMask();
												// 提示信息显示
												$('#spinner-message font')
														.html("负载更新中,请稍等....");
												$
														.post(
																url,
																data,
																function(
																		response) {
																	// 隐藏遮罩层
																	hideMask();
																	if (response == "") {
																		showMessage("重新加载负载均衡异常！");
																		$(
																				grid_selector)
																				.trigger(
																						"reloadGrid");
																	} else {
																		if (response.success) {
																			response.message = "应用发布成功！"
																		}
																		showMessage(
																				response.message,
																				function() {
																					location
																							.reload();
																				});
																	}
																});
											}
										}
									}
								});
					}

				}
			});
}

/**
 * Get active image list
 */
function getActiveImageList(appId, appName) {
	$('#release_tag').html("");
	$.ajax({
		type : 'get',
		url : base + 'image/activeListByAppId',
		data : {
			appId : appId
		},
		dataType : 'json',
		success : function(array) {
			var tableStr = "";
			if (array.length >= 1) {
				for (var i = 0; i < array.length; i++) {
					var obj = array[i];
					var imageId = obj.imageId;
					var imageName = obj.imageName;
					var conNum = obj.conNum;
					var tag = obj.imageTag;
					var imgUrl = imageName + ":" + tag;
					var appNandV = appName + ":" + tag;
					if (tag != null) {
						tableStr = tableStr + '<option value="' + conNum
								+ '" imageid="' + imageId + '">' + appNandV
								+ '</option>';
					}
				}
				$('#release_tag').html(tableStr);
				// 选中版本后，设置可替换实例最大值
				var tag_num = $('#release_tag').val();
				setInputMax('replace_num', Number(tag_num));
			} else {
				$('#release_tag').html(
						'<option value="0">该应用下没有已发布的版本，请选择其他发布方式！</option>')
			}
		}
	});
}

/**
 * 应用发布按钮
 */
function showReleaseModel() {
	var ids = $(grid_selector).jqGrid("getGridParam", "selarrrow");
	if (ids.length > 0) {
		if (ids.length > 1) {
			showMessage("一次只能选择一个应用版本进行发布！");
		} else {
			var rowData = $(grid_selector).jqGrid("getRowData", ids[0]);
			if (rowData.appVersion == '没有应用版本信息') {
				showMessage("应用没有可发布的版本！");
			} else {
				// 获取选中记录，并展示应用名称和版本信息
				$('#app_select').attr('appId', rowData.appId).val(
						$.trim($(rowData.appName).text()));
				$('#app_version').attr('imageId', rowData.imageId).attr(
						'imageUrl',
						rowData.imageName + ':' + rowData.appVersion).val(
						rowData.appVersion);
				$('#balanceId').val(rowData.balanceId);
				// 加载应用环境信息
				loadImageEnv(rowData.appId, rowData.imageId);
				// 加载应用版本信息
				getActiveImageList(rowData.appId, $.trim($(rowData.appName)
						.text()));
				$('#modal-wizard').modal({
					backdrop : 'static',
					keyboard : false
				});
			}
		}
	} else {
		showMessage("请选择应用进行发布！");
	}

}
function loadImageEnv(appid, imageid) {
	// 获取已选环境列表
	$.ajax({
		type : 'post',
		url : base + 'app/getEnvsByImageId',
		data : {
			appid : appid,
			imageid : imageid
		},
		dataType : 'json',
		async : false,
		success : function(array) {
			if (array.length > 0) {
				$.each(array, function(index, obj) {
					var envId = obj.envId;
					var envName = decodeURIComponent(obj.envName);
					$('#env_select').append(
							'<option value="' + envId + '">' + envName
									+ '</option>');
				});
			}
		}
	});
}
/* 刷新本页，重新获取全部容器列表 */
function showAllAppRels() {
	self.location.reload();
}

/**
 * Read nginx conf file name
 */
function readConfFile(fileName) {
	url = base + 'loadbalance/readConfFile';
	data = {
		fileName : fileName
	};
	$.post(url, data, function(response) {
		$('#confContent').html(response);
	});
}

function packup() {
	$('#confContent').empty();
}

function setInputMax(id, dmax) {
	$('#' + id).ace_spinner({
		value : 1,
		min : 1,
		max : dmax,
		step : 1,
		on_sides : true,
		icon_up : 'ace-icon fa fa-plus smaller-75',
		icon_down : 'ace-icon fa fa-minus smaller-75',
		btn_up_class : 'btn-success',
		btn_down_class : 'btn-danger'
	});
}

// 根据cpu核数限制发布实例数
function getMaxIns(clusterId, appCpu) {
	var flag = 100;
	// 集群限制类型
	var res = Number($('#cluster_select>option:selected').attr('restype'));
	if (res == 0) {
		return 100;
	}
	$.ajax({
		type : 'get',
		url : base + 'cluster/maxIns',
		data : {
			clusterId : clusterId,
			appCpu : appCpu
		},
		dataType : 'json',
		async : false,
		success : function(response) {
			flag = Number(response);
		}
	});
	return flag;
}

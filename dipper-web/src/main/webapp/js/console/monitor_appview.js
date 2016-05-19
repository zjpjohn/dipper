var grid_selector = "#view_list";
var page_selector = "#view_page";

jQuery(function($) {
	var options = {
		bootstrapMajorVersion : 3,
		currentPage : 1,
		totalPages : 1,
		numberOfPages : 0,
		onPageClicked : function(e, originalEvent, page) {
			if (page == "next") {
				page = parseInt($('#currentPage').val()) + 1;
			} else {
				page = parseInt($('#currentPage').val()) - 1;
			}
			var appId = $('#app_select option:selected').val();
			var appName = $('#app_select option:selected').text();
			getImageList(page, 5, appId, appName);
		},
		shouldShowPage : function(type, page, current) {
			switch (type) {
			case "first":
			case "last":
				return false;
			default:
				return true;
			}
		}
	};
	$('#tplpage').bootstrapPaginator(options);

	$('#env_select').change(function() {
		var envId = $('#env_select option:selected').val();
		getApplist(envId);
		$('#imagelist').html("");
	});

	$('#app_select').change(function() {
		var appId = $('#app_select option:selected').val();
		var appName = $('#app_select option:selected').text();
		if (appId == '请选择应用') {
			appId = 0;
		}
		getImageList(1, 5, appId, appName);
	});

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
						colNames : [ 'ID', 'imageId', '应用名称', '应用版本', '总实例',
								'运行实例', '维护实例', '更新时间', '访问地址' ],
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
										return '<i class="fa fa-cubes"></i>&nbsp;<a href="'
												+ base
												+ 'zabbix/appview/'
												+ rowObject.appId
												+ '.html"> '
												+ cellvalue + '</a>';
									}
								},
								{
									name : 'appVersion',
									index : 'appVersion',
									width : 20
								},
								{
									name : 'appNum',
									index : 'appNum',
									align : 'center',
									width : 15
								},
								{
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
								},
								{
									name : 'appUrl',
									index : 'appUrl',
									width : 30,
									cellattr : function(rowId, tv, rawObject,
											cm, rdata) {
										// 合并单元格
										return 'id=\'appUrl' + rowId + "\'";
									},
									formatter : function(cellvalue, options,
											rowObject) {
										return '<a href="' + rowObject.appUrl
												+ '" target="_blank">'
												+ rowObject.appUrl + '</i>';
									}
								} ],
						viewrecords : true,
						rowNum : 10,
						rowList : [ 10, 20, 50, 100, 1000 ],
						pager : page_selector,
						altRows : true,
						multiselect : false,
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
							var appId = $('#app_select option:selected').val();
							var imageid = $('.imagelist').find('.selected')
									.attr("imageid");
							if (appId == 0) {
								e.preventDefault();
								showMessage("请选择应用, 再进行下一步操作！");
								return;
							}
							if (imageid == undefined) {
								e.preventDefault();
								showMessage("请选择应用版本，再进行下一步操作！");
								return;
							}
							getClusters(appId);
							getParams();
						} else if (info.step == 2) { // 第二步需要执行的操作
							if (info.direction == 'next') {
								$('#release_info').empty();
								var appInfos = "";
								var appName = $('#app_select option:selected')
										.text();
								var appVersion = $('.imagelist').find(
										'.selected').text().split(':')[1];
								var clusterName = $(
										'#cluster_select option:selected')
										.text();
								if (clusterName == "") {
									e.preventDefault();
									showMessage("请选择应用集群，再进行下一步操作！");
									return;
								}
								var releaseMode = $(
										'#release_mode option:selected').val();
								var releaseModeName = $(
										'#release_mode option:selected').text();
								var releaseNum = $('#release_num').val();
								if (releaseNum <= 0) {
									e.preventDefault();
									showMessage("发布实例必须大于0");
									return;
								}
								var release_desc = $('#release_desc').val();
								appInfos += '<div class="item"><label>应用名称：</label> <label>'
										+ appName + '</label></div>';
								appInfos += '<div class="item"><label>应用版本：</label> <label>'
										+ appVersion + '</label></div>';
								appInfos += '<div class="item"><label>应用集群：</label> <label>'
										+ clusterName + '</label></div>';
								appInfos += '<div class="item"><label>发布方式：</label> <label>'
										+ releaseModeName + '</label></div>';
								if (releaseMode == 2) {
									// 环境
									var env = '';
									$.each($('#env_params li'), function() {
										env += $(this).find('#release_env')
												.val()
												+ ' ';
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
														var hostVolume = $(this)
																.find(
																		'#host_volume')
																.val();
														var containerVolume = $(
																this)
																.find(
																		'#container_volume')
																.val();
														if (hostVolume.trim() !== ""
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
															var pConn = $(this)
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
									var command = $('#release_command').val();
									if (command.trim() !== '') {
										appInfos += '<div class="item"><label>启动命令：</label> <label>'
												+ command + '</label></div>';
									}
								}
								appInfos += '<div class="item"><label>发布实例：</label> <label>'
										+ releaseNum + '</label></div>';
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
						var appId = $('#app_select option:selected').val();
						var imageId = $('.imagelist').find('.selected').attr(
								"imageid");
						var imageUrl = $('.imagelist').find('.selected').attr(
								"imageUrl");
						var clusterId = $('#cluster_select option:selected')
								.val();
						var releaseMode = $('#release_mode option:selected')
								.val();
						// 环境变量
						var env = '';
						$.each($('#env_params li'), function() {
							env += $(this).find('#release_env').val() + ' ';
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
						var releaseNum = $('#release_num').val();
						;
						var appDesc = $('#release_desc').val();

						var data = {
							appId : appId,
							imageId : imageId,
							imageUrl : imageUrl,
							clusterId : clusterId,
							releaseMode : releaseMode,
							env : env,
							volume : volume,
							param : param,
							command : command,
							releaseNum : releaseNum,
							appDesc : appDesc
						};
						$('#apprelease_pro').show();
						// $('.well').append('<div class="icon-spinner">' + '<i
						// id = "spinner" class="ace-icon fa fa-spinner fa-spin
						// blue bigger-225"></i>' + '</div>');
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
												bootbox
														.dialog({
															message : '<div class="alert alert-info" style="margin:10px"><span class="glyphicon glyphicon-info-sign"></span>&nbsp;应用发布成功，是否更新负载均衡&nbsp;'
																	+ '?</div>',
															title : "提示",
															buttons : {
																main : {
																	label : "确定",
																	className : "btn-success btn-round",
																	callback : function() {
																		console
																				.log(response.message);
																		reloadBalance(response.message);
																	}
																},
																cancel : {
																	label : "取消",
																	className : "btn-danger btn-round",
																	callback : function() {
																		location
																				.reload();
																	}
																}
															}
														});
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
	getEnvlist();
	function getEnvlist() {
		$.ajax({
			type : 'get',
			url : base + 'env/listAll',
			dataType : 'json',
			success : function(array) {
				$.each(array, function(index, obj) {
					var envId = obj.envId;
					var envName = decodeURIComponent(obj.envName);
					$('#env_select').append(
							'<option value="' + envId + '">' + envName
									+ '</option>');
				});
			}
		});
	}

	/**
	 * Get application list
	 * 
	 */
	function getApplist(envId) {
		$('#app_select').empty();

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
								$('#app_select').append(
										'<option value="' + appid + '">'
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
							'<option value="' + clusterId + '">' + clustername
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

	// 发布实例
	$('#release_num').ace_spinner({
		value : 1,
		min : 1,
		max : 100,
		step : 1,
		on_sides : true,
		icon_up : 'ace-icon fa fa-plus smaller-75',
		icon_down : 'ace-icon fa fa-minus smaller-75',
		btn_up_class : 'btn-success',
		btn_down_class : 'btn-danger'
	});

	// 替换实例
	$('#replace_num').ace_spinner({
		value : 1,
		min : 1,
		max : 100,
		step : 1,
		on_sides : true,
		icon_up : 'ace-icon fa fa-plus smaller-75',
		icon_down : 'ace-icon fa fa-minus smaller-75',
		btn_up_class : 'btn-success',
		btn_down_class : 'btn-danger'
	});

	// 百分比替换
	$('#replace_percent').ace_spinner({
		value : 1,
		min : 1,
		max : 50,
		step : 1,
		on_sides : true,
		icon_up : 'ace-icon fa fa-plus smaller-75',
		icon_down : 'ace-icon fa fa-minus smaller-75',
		btn_up_class : 'btn-success',
		btn_down_class : 'btn-danger'
	});

	/*
	 * Get selected image from imageList
	 * 
	 */
	$('.imagelist').on('click', '.image-item', function(event) {
		event.preventDefault();
		$('div', $('#imagelist')).removeClass('selected');
		$(this).addClass('selected');
	});

	/**
	 * Release type change
	 */
	$('#release_mode').change(function() {
		var releaseMode = $(this).children('option:selected').val();
		switch (releaseMode) {
		case '0':
			$('#env_form').hide();
			$('#vol_form').hide();
			$('#param_form').hide();
			$('#command_form').hide();
			$('#gray_policy').hide();
			break;
		case '1':
			$('#env_form').hide();
			$('#vol_form').hide();
			$('#param_form').hide();
			$('#command_form').hide();
			$('#gray_policy').show();
			break;
		case '2':
			$('#env_form').show();
			$('#vol_form').show();
			$('#param_form').show();
			$('#command_form').show();
			$('#gray_policy').hide();
			break;
		}
	});

	// 添加环境变量
	$("#env-plus").on('click', function(event) {
		$("#env_params li:first").clone(true).appendTo("#env_params");
		$("#env_params li").not(":first").find("#env-minus").show();
		$("#env_params li").not(":last").find("#env-plus").hide();
		$("#env_params li:first").find("#env-minus").hide();
		$("#env_params li:last").find("#env-plus").show();
		$("#env_params li:last").find("#app_env").val("");
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
		jQuery(grid_selector).jqGrid('setGridParam', {
			url : base + 'appRelease/listSearch?appName=' + searchName
		}).trigger("reloadGrid");
	});

	/**
	 * reload nginx
	 */
	$('#conf_submit')
			.click(
					function(event) {
						conIds = $("#conIds").val();
						url = base + 'loadbalance/reloadApp';
						data = {
							conIds : conIds,
							fileFlag : 1
						};
						$('div.well')
								.append(
										'<div class="icon-spinner" style="float:right;">'
												+ '<i id = "spinner" class="ace-icon fa fa-spinner fa-spin blue bigger-225"></i>'
												+ '</div>');
						$.post(url, data, function(response) {
							$('#showLBConfModal').modal('hide');
							if (response == "") {
								showMessage("更新负载均衡异常！");
								$('div.icon-spinner').remove();
								location.reload();
							} else {
								showMessage(response.message, function() {
									$('div.icon-spinner').remove();
									location.reload();
								});
							}
						});
						$('#confContent').empty();
						$('#confList').empty();
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
	$("#con_add-param").on('click', function(event) {
		event.preventDefault();
		$("#con_params li:first").clone(true).appendTo("#con_params");
		$("#con_params li").not(":first").find("#con_remove-param").show();
		$("#con_params li:first").find("#con_remove-param").hide();
		var str = $("#con_params li:last").find("#con_meter").val();
		/** @bug152_begin 新增查询参数时，新增栏参数内容置空 */
		$("#con_params li:last").find("#con_param_value").val("");
		/** @bug152_finish */
	});

	/**
	 * 删除高级索索的参数项
	 */
	$("#add-search").on('click', function(event) {
		event.preventDefault();
		$("#search_params li:first").clone(true).appendTo("#search_params");
		$("#search_params li").not(":first").find("#remove-search").show();
		$("#search_params li:first").find("#remove-search").hide();
		var str = $("#search_params li:last").find("#search_meter").val();
		if (str == 0 || str == 1 || str == 2) {
			$("#search_params li:last").find("#unit").text("%");
		} else {
			$("#parasearch_paramsms li:last").find("#unit").text("Mbps");
		}
	});

	/**
	 * 向查询按钮添加请求提交操作 删除高级索索的参数项
	 */
	$("#con_advanced_search").on(
			'click',
			function(event) {
				/* 保存各项栏目的名称数组 */
				var column_array = new Array();
				/* 保存用户填写的各项信息数组 */
				var value_array = new Array();

				/* 获取选择栏目的名称 */
				$("select[name=con_meter]").each(function() {
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

				/* 获取填写的参数值信息 */
				$("input[name=con_param_value]").each(function() {
					value_array.push($(this).val());
				});

				/* 查询是否存在关键词相关的结果 */
				jQuery(grid_selector).jqGrid(
						'setGridParam',
						{
							url : base + 'appRelease/advancedSearch?params='
									+ column_array + '&values=' + value_array
						}).trigger("reloadGrid");

				/** @bug152_begin 清空用户多选的参数 */
				while ($("#con_params li").length > 1) {
					$("#con_remove-param").parent().remove();
				}
				/** @bug152_finish */

				$('#advanSearchContainerModal').modal('hide');
				$('#advanced_search_container_frm')[0].reset();
			});

	/**
	 * 向高级搜索的取消按钮添加重置隐藏
	 */

	$("#con_advanced_cancel").on('click', function(event) {
		event.preventDefault();
		$('#advanSearchContainerModal').modal('hide');
		$('#advanced_search_container_frm')[0].reset();
	});

});

/**
 * @author yangqinglin
 * @datetime 2015年11月3日 11:23
 * @description 添加高级查询函数
 */
function AdvancedSearchContainer() {
	/** @bug152_begin 清空用户多选的参数 */
	while ($("#con_params li").length > 1) {
		$("#con_remove-param").parent().remove();
	}
	/* 隐藏高级查询第一行的删除打叉按钮 */
	$("#con_params li:first").find("#con_remove-param").hide();
	/** 打开高级搜索窗口，之前输入全部清空 */
	$("#con_params li:first").find("#con_param_value").val("");
	$("#con_params li:first").find("#con_meter").val("0");
	/** @bug152_finish */

	$('#advanSearchContainerModal').modal('show');

}

/**
 * Get image list
 */
function getImageList(page, limit, appId, appName) {
	$('#imagelist').html("");
	var appmessage = {
		page : page,
		rows : limit,
		appId : appId
	};
	$
			.ajax({
				type : 'get',
				url : base + 'image/listByappId',
				data : appmessage,
				dataType : 'json',
				success : function(array) {
					var tableStr = "";
					if (array.rows.length >= 1) {
						var totalnum = array.records;
						var totalp = 1;
						if (totalnum != 0) {
							totalp = Math.ceil(totalnum / limit);
						}
						options = {
							totalPages : totalp
						};
						$('#tplpage').bootstrapPaginator(options);
						modalPageUpdate(page, totalp);
						$('#currentPage').val(page);
						var rowData = array.rows;
						for (var i = 0; i < rowData.length; i++) {
							var obj = rowData[i];
							var imageId = obj.imageId;
							var imageUuid = obj.imageUuid;
							var imageName = obj.imageName;
							var tag = obj.imageTag;
							var imgUrl = imageName + ":" + tag;
							var appNandV = appName + ":" + tag;
							if (tag != null) {
								tableStr = tableStr
										+ '<div class="image-item" imageid="'
										+ imageId + '" imageUrl="' + imgUrl
										+ '">' + appNandV + '</div>';
							} else {
								tableStr = tableStr
										+ '<div class="image-item" imageid="'
										+ imageId + '" imageUrl="' + imgUrl
										+ '">' + appNandV + '</div>';
							}
						}
						$('#imagelist').html(tableStr);
					} else {
						$('#imagelist')
								.html(
										'<span style="color:red;font-size:14px"><b>当前应用没有任何版本镜像可用，请先发布镜像或选择其他应用！</b></span>')
					}
				}
			});
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

function reloadBalance(conIds) {
	$('.well')
			.append(
					'<div class="icon-spinner">'
							+ '<i id = "spinner" class="ace-icon fa fa-spinner fa-spin blue bigger-225"></i>'
							+ '</div>');
	$
			.ajax({
				type : 'post',
				url : base + 'loadbalance/reloadApp',
				data : {
					conIds : conIds,
					fileFlag : 0
				},
				dataType : 'json',
				success : function(response) {
					if (response == "") {
						showMessage("重新加载负载均衡异常！");
						$('.icon-spinner').remove();
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
										main : {
											label : "<i class='icon-info'></i><b>继续</b>",
											className : "btn-sm btn-success btn-round",
											callback : function() {
												url = base
														+ 'loadbalance/reloadApp';
												data = {
													conIds : conIds,
													fileFlag : 1
												};
												$
														.post(
																url,
																data,
																function(
																		response) {
																	if (response == "") {
																		showMessage("重新加载负载均衡异常！");
																		$(
																				'.icon-spinner')
																				.remove();
																		$(
																				grid_selector)
																				.trigger(
																						"reloadGrid");
																	} else {
																		showMessage(
																				response.message,
																				function() {
																					$(
																							'.icon-spinner')
																							.remove();
																					location
																							.reload();
																				});
																	}
																});
											}
										},
										cancel : {
											label : "<i class='icon-info'></i> <b>取消</b>",
											className : "btn-sm btn-danger btn-round",
											callback : function() {
												$('.icon-spinner').remove();
												$(grid_selector).trigger(
														"reloadGrid");
											}
										}
									}
								});
					}

				}
			});
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

/**
 * @author yangqinglin
 * @datetime 2015年10月8日 12:36
 * @description 添加查询应用函数
 */
function SearchAppContainers() {
	var searchAppName = $('#searchAppName').val();
	/* 查询是否存在关键词相关的结果 */
	jQuery(grid_selector).jqGrid('setGridParam', {
		url : base + 'application/listSearch?search_name=' + searchAppName
	}).trigger("reloadGrid");

}

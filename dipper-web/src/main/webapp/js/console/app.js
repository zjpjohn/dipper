var grid_selector = "#app_list";
var page_selector = "#app_page";
/* 保存原始的应用名称信息 */
var original_app_name = "";
/* 保存原始的应用URL信息 */
var original_app_url = "";

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
						url : base + 'application/list',
						datatype : "json",
						height : '100%',
						autowidth : true,
						colNames : [ 'APP_ID', '应用名称', '应用状态', '应用类型', '应用描述',
								'负载均衡ID', '负载均衡名称', '访问路径', '应用端口', '创建日期',
								'快捷操作' ],
						colModel : [
								{
									name : 'appId',
									index : 'appId',
									width : 10,
									hidden : true
								},
								{
									name : 'appName',
									index : 'appName',
									width : 10,
									formatter : function(cell, opt, obj) {
										return '<i class="fa fa-cubes"></i><a href="'
												+ base
												+ 'application/detail/'
												+ obj.appId
												+ '.html">'
												+ cell
												+ '</a>';
									}
								},
								{
									name : 'appStatus',
									index : 'appStatus',
									width : 5,
									formatter : function(cellvalue, options,
											rowObject) {
										switch (cellvalue) {
										case 0:
											return '删除';
										case 1:
											return '正常';
										default:
											return '未知';
										}
									}
								},
								{
									name : 'appType',
									index : 'appType',
									width : 8,
									formatter : function(cellvalue, options,
											rowObject) {
										switch (cellvalue) {
										case 0:
											return '对外应用';
										case 1:
											return '内部中间件';
										default:
											return '未知';
										}
									}
								},
								{
									name : 'appDesc',
									index : 'appDesc',
									width : 15
								},
								{
									name : 'balanceId',
									index : 'balanceId',
									width : 10,
									hidden : true
								},
								{
									name : 'balanceName',
									index : 'balanceName',
									width : 10
								},
								{
									name : 'appUrl',
									index : 'appUrl',
									width : 15
								},
								{
									name : 'appPort',
									index : 'appPort',
									width : 15,
									hidden : true
								},
								{
									name : 'appCreatetime',
									index : 'appCreatetime',
									width : 10
								},
								{
									name : '',
									title : false,
									index : '',
									width : 150,
									fixed : true,/* 固定像素长度 */
									sortable : false,
									resize : false,
									formatter : function(cellvalue, options,
											rowObject) {
										var strHtml = "";

										var upda = $("#update_app").val();
										if (typeof (upda) != "undefined") {
											strHtml += "<button class=\"btn btn-primary btn-xs btn-round\" onclick=\"editApplication('"
													+ rowObject.appId
													+ "','"
													+ rowObject.appName
													+ "','"
													+ rowObject.appType
													+ "','"
													+ rowObject.appUrl
													+ "','"
													+ rowObject.appPort
													+ "','"
													+ rowObject.appDesc
													+ "')\"><i class=\"ace-icon fa fa-pencil align-top\"></i>&nbsp;<b>编辑</b></button>&nbsp;"
										}
										var dele = $("#delete_app").val();
										if (typeof (dele) != "undefined") {
											strHtml += "<button class=\"btn btn-inverse btn-xs btn-round\" onclick=\"deleteApplication('"
													+ rowObject.appId
													+ "','"
													+ rowObject.appName
													+ "')\"><i class=\"ace-icon fa fa-trash-o\"></i>&nbsp;<b>删除</b></button>&nbsp;";
										}
										return strHtml;
									}
								} ],
						viewrecords : true,
						rowNum : 10,
						rowList : [ 10, 20, 50, 100, 1000 ],
						pager : page_selector,
						altRows : true,
						multiselect : true,
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
						}
					});
	$(window).triggerHandler('resize.jqGrid');// 窗口resize时重新resize表格，使其变成合适的大小
	jQuery(grid_selector).jqGrid(// 分页栏按钮
	'navGrid', page_selector, { // navbar options
		edit : false,
		add : false,
		del : false,
		search : false,
		refresh : true,
		refreshstate : 'current',
		refreshicon : 'ace-icon fa fa-refresh',
		view : false
	}, {}, {}, {}, {}, {});

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
	// 应用移除负载处理函数
	function releaseApplication(appId, appName) {

	}
	// 批量删除应用函数
	$('#batch_remove_app_btn')
			.on(
					'click',
					function() {
						var ids = $(grid_selector).jqGrid("getGridParam",
								"selarrrow");
						var appname_string = "";
						var idList = "";
						if (ids.length == 0) {
							showMessage("请先选择需要删除的应用!");
							return;
						}
						for (var i = 0; i < ids.length; i++) {
							var rowData = $(grid_selector).jqGrid("getRowData",
									ids[i]);
							idList += (i == ids.length - 1) ? rowData.appId
									: rowData.appId + ',';
							appname_string += (i == ids.length - 1) ? rowData.appName
									: rowData.appName + ',';
						}
						bootbox
								.confirm({
									buttons : {
										confirm : {
											label : '确认',
											className : 'btn-success btn-round'
										},
										cancel : {
											label : '取消',
											className : 'btn-danger btn-round'
										}
									},
									message : "<b>你确定要删除以下应用吗?</b><br><font color=\"blue\"><b><i>"
											+ appname_string
											+ "</i></b></font>",
									callback : function(result) {
										if (result) {
											$
													.post(
															base
																	+ 'application/remove/batch',
															{
																appids : idList
															},
															function(response) {
																if (response.success) {
																	showMessage(
																			response.message,
																			function() {
																				$(
																						grid_selector)
																						.trigger(
																								"reloadGrid");
																			});
																} else {
																	showMessage("<font color=\"red\">错误</font>：删除(<font color=\"blue\"><i>"
																			+ appname_string
																			+ "</i></font>)应用失败！<br><font color=\"green\">原因</font>："
																			+ response.message);
																	$(
																			grid_selector)
																			.trigger(
																					"reloadGrid");
																}
															});
										} else {
										}
									},
									title : "删除应用",
								});
					});

	/* （创建）新的应用 */
	$('#submit').click(function() {
		if ($("#add_app_frm").valid()) {
			var url = base + 'application/create';
			app_name = $('#application_name').val();
			app_type = $('input:radio[name="application_type"]:checked').val();
			app_url = $('#application_url').val();
			app_port = $('#application_port').val();
			app_desc = $('#application_desc').val();

			if (!app_name || app_name.length == 0) {
				/** @bug122_finish */
				bootbox.alert("【应用名称】不能为空值。");
				return;
			}

			data = {
				app_name : app_name,
				app_type : app_type,
				app_url : app_url,
				app_desc : app_desc,
				app_port : app_port
			};
			$.post(url, data, function(response) {
				$('#add_app_frm')[0].reset();
				$('#createAppModal').modal('hide');
				showMessage(response.message, function() {
					$(grid_selector).trigger("reloadGrid");
				});
			});
		}

	});

	/* （创建）应用取消操作 */
	$('#cancel').click(function() {
		$('#createAppModal').modal('hide');
		$('#add_app_frm')[0].reset();
		$('label.error').remove();
	});

	$(".close").click(function() {
		$('#add_app_frm')[0].reset();
		$('label.error').remove();
	});

	/* （修改） 应用提交操作 */
	$('#modify')
			.click(
					function() {
						if ($("#modify_app_frm").valid()) {
							url = base + 'application/update';
							app_id = $('#application_id_edit').val();
							app_name = $('#application_name_edit').val();
							/* 获取应用类型的数值 */
							app_type = $(
									'input:radio[name="application_type_edit"]:checked')
									.val();
							app_url = $('#application_url_edit').val();
							app_desc = $('#application_desc_edit').val();

							data = {
								app_id : app_id,
								app_name : app_name,
								app_type : app_type,
								app_url : app_url,
								app_desc : app_desc
							};
							$.post(url, data, function(response) {
								$('#modifyAppModal').modal('hide');
								showMessage(response.message, function() {
									$(grid_selector).trigger("reloadGrid");
								});
							});
						}
					});

	/* （修改） 应用取消操作 */
	$('#modify_cancel').click(function() {
		$('#modifyAppModal').modal('hide');
		$('#modify_app_frm')[0].reset();
		$('label.error').remove();
	});

	/**
	 * 添加高级搜索的参数项
	 */
	$("#add-param").on('click', function(event) {
		event.preventDefault();
		$("#params li:first").clone(true).appendTo("#params");
		$("#params li").not(":first").find("#remove-param").show();
		$("#params li:first").find("#remove-param").hide();
		$("#params li:last").find("#meter").val();
		/** @bug152_begin 新增查询参数时，新增栏参数内容置空 */
		$("#params li:last").find("#param_value").val("");
		/** @bug152_finish */
	});

	/**
	 * 删除高级索索的参数项
	 */
	$("#remove-param").on('click', function(event) {
		event.preventDefault();
		if ($("#params li").length > 1) {
			$(this).parent().remove();
		}
	});

	/**
	 * 向高级查询按钮添加请求提交操作
	 */
	$("#advanced_search").on(
			'click',
			function(event) {
				/* 保存各项栏目的名称数组 */
				var column_array = new Array();
				/* 保存用户填写的各项信息数组 */
				var value_array = new Array();

				/* 获取选择栏目的名称 */
				$("select[name=meter]").each(function() {
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
				$("input[name=param_value]").each(function() {
					value_array.push($(this).val());
				});
				/* 查询是否存在关键词相关的结果 */
				jQuery(grid_selector).jqGrid(
						'setGridParam',
						{
							url : base + 'application/advancedSearch?params='
									+ column_array + '&values=' + value_array
						}).trigger("reloadGrid");

				$('#advancedSearchModal').modal('hide');
				$('#advanced_search_frm')[0].reset();

				/** @bug152_begin 清空用户多选的参数 */
				while ($("#params li").length > 1) {
					$("#remove-param").parent().remove();
				}
				/** @bug152_finish */
			});

	/**
	 * 向高级搜索的取消按钮添加重置隐藏
	 */
	$("#advanced_cancel").on('click', function(event) {
		event.preventDefault();
		$('#advancedSearchModal').modal('hide');
		$('#advanced_search_frm')[0].reset();
		// $("#params li").parent().remove();
	});

	/**
	 * Validate create app form
	 */
	$('#add_app_frm').validate({
		rules : {
			application_name : {
				required : true,
				isRightfulString : true,
				maxlength : 64,
				isNotNull : false,
				remote : {
					url : base + "application/checkAppName",
					type : "post",
					dataType : "json",
					data : {
						appName : function() {
							return $("#application_name").val();
						}
					},
					dataFilter : function(data) {// 判断控制器返回的内容
						if (data == "true") {
							return true;
						} else {
							return false;
						}
					}
				}
			},
			application_url : {
				// isHttpUrlString : true,
				required : true,
				isURLString : true,
				maxlength : 64,
				remote : {
					url : base + "application/checkAppUrl",
					type : "post",
					dataType : "json",
					data : {
						appUrl : function() {
							return $("#application_url").val();
						}
					},
					dataFilter : function(data) {// 判断控制器返回的内容
						if (data == "true") {
							return true;
						} else {
							return false;
						}
					}
				}
			},
			application_port : {
				required : true,
				isValidServerPort : true
			},
			application_desc : {
				maxlength : 200,
				stringCheck : true
			}
		},
		messages : {
			application_name : {
				required : "应用名称不能为空值，请输入。",
				isRightfulString : "只能包含(字母、数字和下划线、破折号)合法字符",
				maxlength : $.validator.format("应用名称不能大于64个字符"),
				isNotNull : "应用名称不能为空值，请输入。",
				remote : "应用名称已经存在，请重新填写"
			},
			application_url : {
				required : "访问路径不能为空",
				maxlength : $.validator.format("访问路径不能大于64个字符"),
				required : "访问路径不能为空",
				remote : "访问路径已经存在，请重新填写"
			},
			application_port : {
				required : "应用端口不能为空",
				isInteger : "应用端口必须位于1~65536之间的整数"
			},
			application_desc : {
				maxlength : $.validator.format("描述信息不能大于200个字符")
			}
		}
	});

	/**
	 * Validate modify app form
	 */
	$("#modify_app_frm").validate(
			{
				rules : {
					application_name_edit : {
						required : true,
						isRightfulString : true,
						maxlength : 64,
						remote : {
							url : base + "application/checkAppName",
							type : "post",
							dataType : "json",
							data : {
								appName : function() {
									var app_name_edit = $(
											"#application_name_edit").val();
									/* 如果原始名称与当前名称一致，直接返回true */
									if (original_app_name == app_name_edit) {
										return;
									} else {
										return app_name_edit;
									}
								}
							},
							dataFilter : function(data) {// 判断控制器返回的内容
								if (data == "true") {
									return true;
								} else {
									return false;
								}
							}
						}
					},
					application_url_edit : {
						// isHttpUrlString : true,
						required : true,
						isURLString : true,
						maxlength : 64,
						remote : {
							url : base + "application/checkAppUrl",
							type : "post",
							dataType : "json",
							data : {
								appUrl : function() {
									var app_url_edit = $(
											"#application_url_edit").val();
									/* 如果原始名称与当前名称一致，直接返回true */
									if (original_app_url == app_url_edit) {
										return;
									} else {
										return app_url_edit;
									}
								}
							},
							dataFilter : function(data) {// 判断控制器返回的内容
								if (data == "true") {
									return true;
								} else {
									return false;
								}
							}
						}
					},
					application_desc_edit : {
						maxlength : 200,
						stringCheck : true
					}

				},
				messages : {
					application_name_edit : {
						required : "应用名称(字母与数字组合)不能为空",
						maxlength : $.validator.format("参数名称不能大于64个字符"),
						remote : "应用名称已经存在，请重新填写"
					},
					application_url_edit : {
						required : "应用地址不能为空",
						// isHttpUrlString :
						// "应用地址格式错误，例如：http://tom_cat/或者/index.do",
						maxlength : $.validator.format("访问路径不能大于64个字符"),
						remote : "访问路径已经存在，请重新填写"
					},
					application_desc_edit : {
						maxlength : $.validator.format("描述信息不能大于200个字符")
					}
				}
			});

});

/**
 * @author yangqinglin
 * @datetime 2015年10月8日 12:36
 * @description 添加查询应用函数
 */
function SearchApplications() {
	var searchAppName = $('#searchAppName').val();
	/* 查询是否存在关键词相关的结果 */
	jQuery(grid_selector).jqGrid('setGridParam', {
		url : base + 'application/listSearch?search_name=' + searchAppName
	}).trigger("reloadGrid");

}

/**
 * @author yangqinglin
 * @datetime 2015年10月29日 11:23
 * @description 添加高级查询函数
 */
function AdvancedSearchApp() {
	/** @bug152_begin 清空用户多选的参数 */
	while ($("#params li").length > 1) {
		$("#remove-param").parent().remove();
	}
	/* 隐藏高级查询第一行的删除打叉按钮 */
	$("#params li:first").find("#remove-param").hide();
	/** 打开高级搜索窗口，之前输入全部清空 */
	$("#params li:first").find("#param_value").val("");
	$("#params li:first").find("#meter").val("0");
	/** @bug152_finish */
	$('#advancedSearchModal').modal('show');
}

/**
 * @author yangqinlgin
 * @datetime 2015年9月18日 16:15
 * @description 删除应用函数
 */
function deleteApplication(app_id, app_name) {
	url = base + "application/delete";
	data = {
		appids : app_id,
		app_name : app_name
	};
	bootbox
			.confirm(
					"<b>你确定删除(<font color=\"blue\">" + app_name
							+ "</font>)应用吗?</b>",
					function(result) {
						if (result) {
							$
									.post(
											url,
											data,
											function(response) {
												if (response.success) {
													showMessage(
															response.message,
															function() {
																$(grid_selector)
																		.trigger(
																				"reloadGrid");
															});
												} else {
													showMessage("<font color=\"red\">错误</font>：删除(<font color=\"blue\"><i>"
															+ app_name
															+ "</i></font>)应用失败！<br><font color=\"green\">原因</font>："
															+ response.message);
													$(grid_selector).trigger(
															"reloadGrid");
												}
											});
						}
					});
}

function editApplication(app_id, app_name, app_type, app_url, app_port,
		app_desc) {
	/* 保存应用的原始名称，便于校验使用 */
	original_app_name = $.trim(app_name);
	original_app_url = $.trim(app_url);
	$('#application_id_edit').val(app_id);
	$('#application_name_edit').val(app_name);
	$('input:radio[name="application_type_edit"][value="' + app_type + '"]')
			.attr("checked", true);
	$('#application_url_edit').val(app_url);
	$('#application_desc_edit').val(
			(app_desc == null || app_desc == "null") ? "" : app_desc);
	$('#modifyAppModal').modal('show');
}
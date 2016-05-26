var grid_selector = "#mntrpxy_list";
var page_selector = "#mntrpxy_page";
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
						url : base + 'mntrproxy/list',
						datatype : "json",
						height : '100%',
						autowidth : true,
						colNames : [ '代理ID', '代理名称', '监控主机地址', '监听端口', '代理描述',
								'备注信息', '创建人', '创建日期', '快捷操作' ],
						colModel : [
								{
									name : 'mpId',
									index : 'mpId',
									width : 10,
									hidden : true
								},
								{
									name : 'mpName',
									index : 'mpName',
									width : 10,
									formatter : function(cell, opt, obj) {
										return '<i class="glyphicon glyphicon-eye-open"></i>&nbsp;'
												+ cell;
									}
								},
								{
									name : 'mpIP',
									index : 'mpIP',
									width : 10
								},
								{
									name : 'mpPort',
									index : 'mpPort',
									width : 6
								},
								{
									name : 'mpDesc',
									index : 'mpDesc',
									width : 15
								},
								{
									name : 'mpComment',
									index : 'mpComment',
									width : 15
								},
								{
									name : 'userName',
									index : 'userName',
									width : 8
								},
								{
									name : 'mpCreatetime',
									index : 'mpCreatetime',
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
											strHtml += "<button class=\"btn btn-primary btn-xs btn-round\" onclick=\"editMonitorProxy('"
													+ rowObject.mpId
													+ "','"
													+ rowObject.mpName
													+ "','"
													+ rowObject.mpIP
													+ "','"
													+ rowObject.mpPort
													+ "','"
													+ rowObject.mpDesc
													+ "','"
													+ rowObject.mpComment
													+ "')\"><i class=\"ace-icon fa fa-pencil align-top\"></i>&nbsp;<b>编辑</b></button>&nbsp;"
										}
										var dele = $("#delete_app").val();
										if (typeof (dele) != "undefined") {
											strHtml += "<button class=\"btn btn-inverse btn-xs btn-round\" onclick=\"deleteMonitorProxy('"
													+ rowObject.mpId
													+ "','"
													+ rowObject.mpName
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
	$('#batch_rmmp_btn')
			.on(
					'click',
					function() {
						var ids = $(grid_selector).jqGrid("getGridParam",
								"selarrrow");
						var mp_string = "";
						var idList = "";
						if (ids.length == 0) {
							showMessage("请先选择需要删除的监测代理!");
							return;
						}
						for (var i = 0; i < ids.length; i++) {
							var rowData = $(grid_selector).jqGrid("getRowData",
									ids[i]);
							idList += (i == ids.length - 1) ? rowData.mpId
									: rowData.mpId + ',';
							mp_string += (i == ids.length - 1) ? rowData.mpName
									: rowData.mpName + ',&nbsp;';
						}

						var mp_url = base + "mntrproxy/delete";
						var mp_data = {
							mpIds : idList,
							mpNames : mp_string
						};
						bootbox
								.dialog({
									message : '<div class="alert alert-info" style="margin:10px"><span class="glyphicon glyphicon-info-sign"></span>&nbsp;确定要删除&nbsp'
											+ mp_string + '?</div>',
									title : "删除监测代理",
									buttons : {
										cancel : {
											label : "取消",
											className : "btn-danger btn-round",
											callback : function() {
												$(grid_selector).trigger(
														"reloadGrid");
											}
										},
										main : {
											label : "确定",
											className : "btn-success btn-round",
											callback : function() {
												$
														.post(
																mp_url,
																mp_data,
																function(
																		response) {
																	if (response.success) {
																		showMessage(response.message);
																	} else {
																		showMessage("<font color=\"red\">错误</font>：删除(<font color=\"blue\"><i>"
																				+ mp_string
																				+ "</i></font>)监控代理失败！<br><font color=\"green\">原因</font>："
																				+ response.message);
																	}
																	$(
																			grid_selector)
																			.trigger(
																					"reloadGrid");
																});
											}
										}
									}
								});
					});

	/* （创建）新的应用 */
	$('#submit').click(function() {
		if ($("#add_mntrpxy_frm").valid()) {
			mntrpxy_name = $('#mntrpxy_name').val();
			mntrpxy_ip = $('#mntrpxy_ip').val();
			mntrpxy_port = $('#mntrpxy_port').val();
			mntrpxy_desc = $('#mntrpxy_desc').val();
			mntrpxy_comment = $('#mntrpxy_comment').val();

			var checkUrl = base + 'mntrproxy/checkMpIpPort';
			var checkData = {
				mpIP : mntrpxy_ip,
				mpPort : mntrpxy_port
			};

			/** 保存查询是否重复IP地址和端口的标志 */
			$.post(checkUrl, checkData, function(checkResponse) {
				if (!checkResponse.success) {
					showMessage(checkResponse.message);
				} else {
					/** 添加IP地址和端口的校验处理 */
					var createUrl = base + 'mntrproxy/create';
					var createData = {
						mpName : mntrpxy_name,
						mpIP : mntrpxy_ip,
						mpPort : mntrpxy_port,
						mpDesc : mntrpxy_desc,
						mpComment : mntrpxy_comment
					};
					$.post(createUrl, createData, function(response) {
						$('#add_mntrpxy_frm')[0].reset();
						$('#createMntrpxyModal').modal('hide');
						showMessage(response.message, function() {
							$(grid_selector).trigger("reloadGrid");
						});
					});
				}
			});
		}

	});

	/* （创建）监控代理取消操作 */
	$('#cancel').click(function() {
		$('#createMntrpxyModal').modal('hide');
		$('#add_mntrpxy_frm')[0].reset();
		$('label.error').remove();
	});

	$(".close").click(function() {
		$('#add_mntrpxy_frm')[0].reset();
		$('label.error').remove();
	});

	/* （修改） 应用提交操作 */
	$('#modify').click(function() {
		if ($("#modify_mntrpxy_frm").valid()) {
			
			mntrpxy_id = $('#mntrpxy_id_edit').val();
			mntrpxy_name = $('#mntrpxy_name_edit').val();
			mntrpxy_ip = $.trim($('#mntrpxy_ip_edit').val());
			mntrpxy_port = $.trim($('#mntrpxy_port_edit').val());
			mntrpxy_desc = $('#mntrpxy_desc_edit').val();
			mntrpxy_comment = $('#mntrpxy_comment_edit').val();

			
			var old_ip=$('#old_mntrpxy_ip_edit').val();
			var old_port=$('#old_mntrpxy_port_edit').val();
			//校验ip和端口是否修改
			var flag=false;
			if(old_ip==mntrpxy_ip&&old_port==mntrpxy_port){
				flag=true;
			}else{
				//校验ip和端口是否已经占用
				var checkUrl = base + 'mntrproxy/checkMpIpPort';
				var checkData = {
					mpIP : mntrpxy_ip,
					mpPort : mntrpxy_port
				};
				$.post(checkUrl, checkData, function(checkResponse) {
					if (!checkResponse.success) {
						showMessage(checkResponse.message);
					} else {
						flag=true;
					}
				});
			}
			
			//如果ip端口没被占用，则提交修改信息
			if(flag){
				/** 保存查询是否重复IP地址和端口的标志 */
				url = base + 'mntrproxy/update';
				data = {
						mpId : mntrpxy_id,
						mpName : mntrpxy_name,
						mpIP : mntrpxy_ip,
						mpPort : mntrpxy_port,
						mpDesc : mntrpxy_desc,
						mpComment : mntrpxy_comment
					};
				$.post(url, data, function(response) {
					$('#modifyMntrpxyModal').modal('hide');
					showMessage(response.message, function() {
						$(grid_selector).trigger("reloadGrid");
					});
				});
			}
		}
	});

	/* （修改） 应用取消操作 */
	$('#modify_cancel').click(function() {
		$('#modifyMntrpxyModal').modal('hide');
		$('#modify_mntrpxy_frm')[0].reset();
		$('label.error').remove();
	});

	/**
	 * 添加高级搜索的参数项
	 */
	/** @bug68594:高级查询功能增加条列数目没有限制 */
	/* 获取查询条件的总数 */
	advanceColNum = $('#meter option').length - 1;

	$("#add-param").on('click', function(event) {
		/* 获取当前已经添加的列数量 */
		var selNum = $('#params li').length + 1;
		event.preventDefault();
		$("#params li:first").clone(true).appendTo("#params");
		$("#params li").not(":first").find("#remove-param").show();
		$("#params li:first").find("#remove-param").hide();
		var str = $("#params li:last").find("#meter").val();
		/** @bug152_begin 新增查询参数时，新增栏参数内容置空 */
		$("#params li:last").find("#param_value").val("");
		/** @bug152_finish */
		/* 当添加的列数量与参数数量相等数，隐藏添加条件按钮 */
		if (selNum >= advanceColNum) {
			$("#add-param").hide();
		}
	});

	/**
	 * 删除高级索索的参数项
	 */
	$("#remove-param").on('click', function(event) {
		/* 获取当前已经添加的列数量 */
		var selNum = $('#params li').length;
		event.preventDefault();
		/* 判断当列数量小于参数数量的时候，显示添加条件按钮 */
		if (selNum <= advanceColNum) {
			$("#add-param").show();
		}
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

				/** 在高级搜索中增加对于空值的判断返回处理 */
				var bEmptyParam = false;
				/* 获取填写的参数值信息 */
				$("input[name=param_value]").each(function() {
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
							url : base + 'mntrproxy/advancedSearch?params='
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
	 * Validate create Monitor Proxy form
	 */
	$('#add_mntrpxy_frm').validate({
		rules : {
			mntrpxy_name : {
				required : true,
				stringNameCheck : true,
				maxlength : 64,
				remote : {
					url : base + "mntrproxy/checkMpName",
					type : "post",
					dataType : "json",
					data : {
						mpName : function() {
							return $("#mntrpxy_name").val();
						}
					},
					dataFilter : function(data) {// 判断控制器返回的内容
						return data;
					}
				}
			},
			mntrpxy_ip : {
				required : true,
				ip : true,
				maxlength : 15
			},
			mntrpxy_port : {
				required : true,
				isValidServerPort : true,
				maxlength : 5
			},
			mntrpxy_desc : {
				maxlength : 200,
				descriptionCheck : true
			},
			mntrpxy_comment : {
				maxlength : 200,
				descriptionCheck : true
			}
		},
		messages : {
			mntrpxy_name : {
				required : "监控代理名称不能为空值，请输入。",
				stringNameCheck : "只能包含(中文、英文、数字、下划线等字符)合法字符",
				maxlength : $.validator.format("资源名称不能大于64个字符"),
				remote : "监控代理名称已经存在，请重新填写"
			},
			mntrpxy_ip : {
				required : "代理IP地址不能为空值，请输入。",
				ip : "请输入正确的IP地址格式，例如:192.168.123.123",
				maxlength : $.validator.format("代理的IP地址为不能超过15位的字符串。")
			},
			mntrpxy_port : {
				required : "监听端口不能为空值，请输入。",
				isValidServerPort : "请输入正确的端口格式，范围: 1~65535",
				maxlength : $.validator.format("代理的监听端口为不能超过5位的整数。")
			},
			mntrpxy_desc : {
				maxlength : $.validator.format("描述信息不能大于200个字符"),
				descriptionCheck : "只能包含(中英文、数字、下划线、逗号、句号等)合法字符"
			},
			mntrpxy_comment : {
				maxlength : $.validator.format("备注内容不能大于200个字符"),
				descriptionCheck : "只能包含(中英文、数字、下划线、逗号、句号等)合法字符"
			}
		}
	});

	/**
	 * Validate modify app form
	 */
	$("#modify_mntrpxy_frm").validate({
		rules : {
			mntrpxy_ip_edit : {
				required : true,
				ip : true,
				maxlength : 15
			},
			mntrpxy_port_edit : {
				required : true,
				isValidServerPort : true,
				maxlength : 5
			},
			mntrpxy_desc_edit : {
				maxlength : 200,
				descriptionCheck : true
			},
			mntrpxy_comment_edit : {
				maxlength : 200,
				descriptionCheck : true
			}
		},
		messages : {
			mntrpxy_ip_edit : {
				required : "代理IP地址不能为空值，请输入。",
				ip : "请输入正确的IP地址格式，例如:192.168.123.123",
				maxlength : $.validator.format("代理的IP地址为不能超过15位的字符串。")
			},
			mntrpxy_port_edit : {
				required : "监听端口不能为空值，请输入。",
				isValidServerPort : "请输入正确的端口格式，范围: 1~65535",
				maxlength : $.validator.format("代理的监听端口为不能超过5位的整数。")
			},
			mntrpxy_desc_edit : {
				maxlength : $.validator.format("描述信息不能大于200个字符"),
				descriptionCheck : "只能包含(中英文、数字、下划线、逗号、句号等)合法字符"
			},
			mntrpxy_comment_edit : {
				maxlength : $.validator.format("备注内容不能大于200个字符"),
				descriptionCheck : "只能包含(中英文、数字、下划线、逗号、句号等)合法字符"
			}
		}
	});

});

/**
 * @author yangqinglin
 * @datetime 2015年10月8日 12:36
 * @description 添加查询应用函数
 */
function SearchMonitorProxys() {
	var searchAppName = $('#searchAppName').val();
	/* 查询是否存在关键词相关的结果 */
	jQuery(grid_selector).jqGrid('setGridParam', {
		url : base + 'mntrproxy/listSearch?search_name=' + searchAppName
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
function deleteMonitorProxy(mp_id, mp_name) {
	url = base + "mntrproxy/delete";
	data = {
		mpIds : mp_id,
		mpNames : mp_name
	};
	bootbox
			.dialog({
				message : '<div class="alert alert-info" style="margin:10px"><span class="glyphicon glyphicon-info-sign"></span>&nbsp;确定要删除&nbsp;'
						+ mp_name + '?</div>',
				title : "删除监测代理",
				buttons : {
					cancel : {
						label : "取消",
						className : "btn-danger btn-round",
						callback : function() {
							$(grid_selector).trigger("reloadGrid");
						}
					},
					main : {
						label : "确定",
						className : "btn-success btn-round",
						callback : function() {
							$
									.post(
											url,
											data,
											function(response) {
												if (response.success) {
													showMessage(response.message);
												} else {
													showMessage("<font color=\"red\">错误</font>：删除(<font color=\"blue\"><i>"
															+ app_name
															+ "</i></font>)应用失败！<br><font color=\"green\">原因</font>："
															+ response.message);
												}
												$(grid_selector).trigger(
														"reloadGrid");
											});
						}
					}
				}
			});
}

function editMonitorProxy(mp_id, mp_name, mp_ip, mp_port, mp_desc, mp_comment) {
	$('#mntrpxy_id_edit').val(mp_id);
	$('#mntrpxy_name_edit').val(mp_name);
	$('#mntrpxy_ip_edit').val(mp_ip);
	$('#old_mntrpxy_ip_edit').val(mp_ip);
	$('#old_mntrpxy_port_edit').val(mp_port);
	$('#mntrpxy_port_edit').val(mp_port);
	$('#mntrpxy_desc_edit').val(
			(mp_desc == null || mp_desc == "null") ? "" : mp_desc);
	$('#mntrpxy_comment_edit').val(
			(mp_comment == null || mp_comment == "null") ? "" : mp_comment);
	$('#modifyMntrpxyModal').modal('show');
}
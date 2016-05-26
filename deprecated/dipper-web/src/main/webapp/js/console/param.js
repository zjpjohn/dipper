var grid_selector = "#param_list";
var page_selector = "#param_page";

/** 保存当前高级查询条件的列数量 */
var advanceColNum = 0;

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
						url : base + 'param/list',
						datatype : "json",
						height : '100%',
						autowidth : true,
						colNames : [ 'ID', '连接符', '可复用', '参数名称', '参数键', '值类型',
								'描述信息', '备注信息', '创建时间', '快捷操作' ],
						colModel : [
								{
									name : 'paramId',
									index : 'paramId',
									width : 1,
									hidden : true
								},
								{
									name : 'paramConnector',
									index : 'paramConnector',
									width : 1,
									hidden : true
								},
								{
									name : 'paramReusable',
									index : 'paramReusable',
									width : 1,
									hidden : true
								},
								{
									name : 'paramName',
									index : 'paramName',
									width : 15
								},
								{
									name : 'paramValue',
									index : 'paramValue',
									width : 8
								},
								{
									name : 'paramType',
									index : 'paramType',
									width : 8,
									formatter : function(cellvalue, options,
											rowObject) {
										switch (rowObject.paramType) {
										case 0:
											return '空值';
										case 1:
											return '正整数';
										case 2:
											return '字符串';
										case 3:
											return 'Map类型';
										case 4:
											return '布尔值';
										}
									}
								},
								{
									name : 'paramDesc',
									index : 'paramDesc',
									width : 35
								},
								{
									name : 'paramComment',
									index : 'paramComment',
									width : 20
								},
								{
									name : 'paramCreatetime',
									index : 'paramCreatetime',
									width : 15
								},
								{
									name : '',
									title : false,
									index : '',
									width : 140,
									align : 'center',
									fixed : true,
									sortable : false,
									resize : false,
									formatter : function(cellvalue, options,
											rowObject) {
										var strHtml = "";

										var upda = $("#update_param").val();
										if (typeof (upda) != "undefined") {
											strHtml += "<button class=\"btn btn-xs btn-primary btn-round\" onclick=\"modifyParamWin('"
													+ rowObject.paramId
													+ "','"
													+ rowObject.paramName
													+ "','"
													+ rowObject.paramValue
													+ "','"
													+ rowObject.paramConnector
													+ "','"
													+ rowObject.paramType
													+ "','"
													+ rowObject.paramReusable
													+ "','"
													+ rowObject.paramDesc
													+ "','"
													+ rowObject.paramComment
													+ "')\">"
													+ "<i class=\"ace-icon fa fa-pencil-square-o bigger-125\"></i>"
													+ "<b>编辑</b></button> &nbsp;";
										}
										var dele = $("#delete_param").val();
										if (typeof (dele) != "undefined") {
											strHtml += "<button class=\"btn btn-xs btn-inverse btn-round\" onclick=\"removeParam('"
													+ rowObject.paramId
													+ "','"
													+ rowObject.paramName
													+ "','"
													+ rowObject.paramValue
													+ "')\">"
													+ "<i class=\"ace-icon fa fa-trash-o bigger-125\"></i>"
													+ "<b>删除</b></button> &nbsp;";
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
	$(window).triggerHandler('resize.jqGrid'); // 窗口resize时重新resize表格，使其变成合适的大小
	jQuery(grid_selector).jqGrid( // 分页栏按钮
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
						})
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
	// 批量删除镜像函数
	$('#batch_remove_param_btn')
			.on(
					'click',
					function() {
						var ids = $(grid_selector).jqGrid("getGridParam",
								"selarrrow");
						var message = " ";
						var idList = "";
						if (ids.length == 0) {
							showMessage("请先选择需要删除的参数!");
							return;
						}
						for (var i = 0; i < ids.length; i++) {
							var rowData = $(grid_selector).jqGrid("getRowData",
									ids[i]);
							idList += i == ids.length - 1 ? rowData.paramId
									: rowData.paramId + ',';
							message += rowData.paramName + ",&nbsp;&nbsp;";
						}

						bootbox
								.dialog({
									message : '<div class="alert alert-info" style="margin:10px"><span class="glyphicon glyphicon-info-sign"></span>&nbsp;确定要删除参数'
											+ message + '&nbsp;' + '?</div>',
									title : "删除参数",
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
																base
																		+ 'param/remove/batch',
																{
																	ids : idList
																},
																function(
																		response) {
																	if (response == "") {
																		showMessage("删除参数异常！");
																	} else {
																		showMessage(response.message);
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

	/**
	 * create add param
	 */
	$('#submit')
			.click(
					function() {
						if ($("#add_param_frm").valid()) {
							url = base + 'param/create';
							/* 原有参数信息 */
							var param_name = $('#param_name').val();
							var param_key = $('#param_key').val();
							var param_desc = $('#param_desc').val();
							/* 新增参数信息 */
							var param_connector = $(
									'input:radio[name="param_connector"]:checked')
									.val();
							var param_type = $(
									'input:radio[name="param_type"]:checked')
									.val();
							var param_reusable = $(
									'input:radio[name="param_reusable"]:checked')
									.val();
							var param_comment = $('#param_comment').val();

							data = {
								paramName : param_name,
								paramValue : param_key,
								paramDesc : param_desc,
								paramConnector : param_connector,
								paramType : param_type,
								paramReusable : param_reusable,
								paramComment : param_comment
							};
							$('#createParamModal').modal('hide');
							$.post(url, data, function(response) {
								if (response == "") {
									showMessage("创建参数异常！");
									$('#add_param_frm')[0].reset();
									$(grid_selector).trigger("reloadGrid");
								} else {
									showMessage(response.message, function() {
										$('#add_param_frm')[0].reset();
										$(grid_selector).trigger("reloadGrid");
									});
								}
							});
						}

					});

	/**
	 * Cancel add param
	 */
	$('#cancel').click(function() {
		$('#createParamModal').modal('hide');
		$('#add_param_frm')[0].reset();
		$('label.error').remove();
	});

	$(".close").click(function() {
		$('#add_param_frm')[0].reset();
		$('label.error').remove();
	});
	/**
	 * Modify add param
	 */
	$('#modify_submit').click(
			function() {
				if ($("#modify_param_frm").valid()) {
					var url = base + 'param/update';
					/* 原有参数信息 */
					var param_id = $('#param_id_edit').val();
					var param_name = $('#param_name_edit').val();
					var param_key = $('#param_key_edit').val();
					var param_desc = $('#param_desc_edit').val();
					/* 新增参数的信息 */
					/* 新增参数信息 */
					var param_connector = $(
							'input:radio[name="param_connector_edit"]:checked')
							.val();
					var param_type = $(
							'input:radio[name="param_type_edit"]:checked')
							.val();
					var param_reusable = $(
							'input:radio[name="param_reusable_edit"]:checked')
							.val();
					var param_comment = $('#param_comment_edit').val();

					data = {
						paramId : param_id,
						paramName : param_name,
						paramValue : param_key,
						paramDesc : param_desc,
						paramConnector : param_connector,
						paramType : param_type,
						paramReusable : param_reusable,
						paramComment : param_comment
					};
					$('#modifyParamModal').modal('hide');
					$.post(url, data, function(response) {
						var mess = "";
						if (response == "") {
							mess = "修改参数异常！";
						} else {
							mess = response.message;
						}
						showMessage(mess, function() {
							$(grid_selector).trigger("reloadGrid");
						});
					});
				}
			});
	/**
	 * Cancel modify add param
	 */
	$('#modify_cancel').click(function() {
		$('#modifyParamModal').modal('hide');
		$('#modify_param_frm')[0].reset();
		$('label.error').remove();
	});

	/**
	 * Validate create parameter form
	 */
	$('#add_param_frm').validate({
		rules : {
			param_name : {
				required : true,
				stringNameCheck : true,
				maxlength : 64,
				remote : {
					url : base + "param/checkName",
					type : "post",
					dataType : "json",
					data : {
						paramName : function() {
							return $("#param_name").val();
						}
					},
					dataFilter : function(data) { // 判断控制器返回的内容
						if (data == "true") {
							return true;
						} else {
							return false;
						}
					}
				}
			},
			/* 参数键内容，不能为空， */
			param_key : {
				required : true,
				maxlength : 20,
				isParameterKey : true
			},
			param_desc : {
				maxlength : 200,
				descriptionCheck : true
			},
			param_comment : {
				maxlength : 200,
				commentCheck : true
			}

		},
		messages : {
			param_name : {
				required : "参数名称不能为空",
				maxlength : $.validator.format("参数名称不能大于64个字符"),
				remote : "参数已经存在，请重新输入！"
			},
			/* 参数键内容，不能为空， */
			param_key : {
				required : "参数键内容不能为空",
				isParameterKey : "请输入正确的参数键[-字符]或者[--字符串]",
				maxlength : $.validator.format("参数键不能大于20个字符")
			},
			param_desc : {
				maxlength : $.validator.format("描述信息不能大于200个字符")
			},
			param_comment : {
				maxlength : $.validator.format("备注信息不能大于200个字符")
			}
		}
	});

	/**
	 * Validate modify parameter form
	 */
	$("#modify_param_frm").validate({
		rules : {
			param_name_edit : {
				required : true,
				stringNameCheck : true,
				maxlength : 64
			},
			param_key_edit : {
				required : true,
				isParameterKey : true,
				maxlength : 64
			},
			param_desc_edit : {
				maxlength : 200,
				descriptionCheck : true
			},
			param_comment_edit : {
				maxlength : 200,
				commentCheck : true
			}

		},
		messages : {
			param_name_edit : {
				required : "参数名称不能为空",
				maxlength : $.validator.format("参数名称不能大于64个字符")
			},
			param_key_edit : {
				required : "参数键内容不能为空",
				isParameterKey : "请输入正确的参数键[-字符]或者[--字符串]",
				maxlength : $.validator.format("参数键不能大于64个字符")
			},
			param_desc_edit : {
				maxlength : $.validator.format("描述信息不能大于200个字符")
			},
			param_comment_edit : {
				maxlength : $.validator.format("备注信息不能大于200个字符")
			}
		}
	});
	/**
	 * 向查询按钮添加请求提交操作
	 */
	$("#advanced_param_search").on(
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
				$("input[name=search_param_value]").each(function() {
					var param_value = $.trim($(this).val());
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
							url : base + 'param/advancedSearch?params='
									+ column_array + '&values=' + value_array
						}).trigger("reloadGrid");
				//
				$('#advancedSearchParamModal').modal('hide');
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
	$("#advanced_param_cancel").on('click', function(event) {
		event.preventDefault();
		$('#advancedSearchParamModal').modal('hide');
		$('#advanced_search_frm')[0].reset();
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

});

function removeParam(param_id, param_name, param_key) {
	url = base + "param/delete/" + param_id;
	data = {};
	bootbox
			.dialog({
				message : '<div class="alert alert-info" style="margin:10px"><span class="glyphicon glyphicon-info-sign"></span>确定要删除参数&nbsp;'
						+ param_name + '&nbsp;' + '?</div>',
				title : "删除参数",
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
							$.post(url, data,
									function(response) {
										if (response == "") {
											showMessage("删除参数异常！");
										} else {
											if (response.success) {
												showMessage("删除参数" + param_name
														+ "成功！");
											} else {
												showMessage("删除参数失败！");
											}
										}
										$(grid_selector).trigger("reloadGrid");
									});
						}
					}
				}
			});
}
/* 修改参数内容 */
function modifyParamWin(param_id, param_name, param_key, param_connector,
		param_type, param_reusable, param_desc, param_comment) {
	/* 原有参数内容 */
	$("#param_id_edit").attr("value", param_id);
	$("#param_name_edit").attr("value", param_name);
	$("#param_key_edit").attr("value", param_key);
	$("#param_desc_edit").text(
			(param_desc == null || param_desc == "null") ? "" : param_desc);
	/* 新增参数内容 */
	$('input:radio[name="param_type_edit"][value="' + param_type + '"]').attr(
			"checked", true);
	$(
			'input:radio[name="param_connector_edit"][value="'
					+ param_connector + '"]').attr("checked", true);
	$('input:radio[name="param_reusable_edit"][value="' + param_reusable + '"]')
			.attr("checked", true);
	$("#param_comment_edit").text(
			(param_comment == null || param_comment == "null") ? ""
					: param_comment);

	$("#modifyParamModal").modal('show');
}

function searchParams() {
	var paramName = $('#search_param').val();
	jQuery(grid_selector).jqGrid('setGridParam', {
		url : base + 'param/all?paramName=' + paramName
	}).trigger("reloadGrid");
}

function AdvancedSearchParams() {
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

	$('#advancedSearchParamModal').modal('show');
}
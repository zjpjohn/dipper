var grid_selector = "#env_list";
var page_selector = "#env_page";

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
						url : base + 'env/list',
						datatype : "json",
						height : '100%',
						autowidth : true,
						colNames : [ 'ENV_ID', '环境名称', '状态', '配置中心', '环境变量',
								'描述', '创建人ID', '创建人', '创建日期', '快捷操作' ],
						colModel : [
								{
									name : 'envId',
									index : 'envId',
									width : 10,
									hidden : true
								},
								{
									name : 'envName',
									index : 'envName',
									width : 6,
									formatter : function(cell, opt, obj) {
										return '<a href="#">' + cell + '</a>';
									}
								},
								{
									name : 'envStatus',
									index : 'envStatus',
									width : 4,
									formatter : function(cell, opt, obj) {
										switch (cell) {
										case (1): {
											return "正常";
										}
										case (2): {
											return "过渡";
										}
										default: {
											return "异常";
										}
										}
									}
								},
								{
									name : 'configCenter',
									index : 'configCenter',
									width : 6
								},
								{
									name : 'envParam',
									index : 'envParam',
									width : 10
								},
								{
									name : 'envDesc',
									index : 'envDesc',
									width : 15
								},
								{
									name : 'envCreator',
									index : 'envCreator',
									width : 10,
									hidden : true
								},
								{
									name : 'userName',
									index : 'userName',
									width : 6
								},
								{
									name : 'envCreatetime',
									index : 'envCreatetime',
									width : 8
								},
								{
									name : '',
									title : false,
									index : '',
									width : 140,
									fixed : true,/* 固定像素长度 */
									sortable : false,
									resize : false,
									formatter : function(cellvalue, options,
											rowObject) {
										var strHtml = "";

										var upda = $("#update_app").val();
										if (typeof (upda) != "undefined") {
											var param = rowObject.envParam;
											if (param != null) {
												param = param.replace(
														new RegExp(/"/g), ':');
											}

											strHtml += "<button class=\"btn btn-primary btn-xs btn-round\" onclick=\"editEnvironment('"
													+ rowObject.envId
													+ "','"
													+ rowObject.envName
													+ "','"
													+ rowObject.envDesc
													+ "','"
													+ rowObject.configCenter
													+ "','"
													+ param
													+ "')\"><i class=\"ace-icon fa fa-pencil align-top\"></i>&nbsp;<b>编辑</b></button>&nbsp;"
										}
										var dele = $("#delete_app").val();
										if (typeof (dele) != "undefined") {
											strHtml += "<button class=\"btn btn-inverse btn-xs btn-round\" onclick=\"deleteEnvironment('"
													+ rowObject.envId
													+ "','"
													+ rowObject.envName
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

	/* （创建）新的环境 */
	$('#submit').click(function() {
		if ($("#add_env_frm").valid()) {
			var url = base + 'env/add';
			var env_name = $('#env_name').val();
			var env_desc = $('#env_desc').val();
			var config_center = $('#config_center').val();

			// 获取环境变量
			var env_paths = '';
			$('#createEnvModal input[name="env_path"]').each(function() {
				if ($.trim($(this).val()) != '') {
					env_paths += $.trim($(this).val()) + ";";
				}
			})
			env_paths = env_paths.substring(0, env_paths.length - 1);
			data = {
				envName : env_name,
				envDesc : env_desc,
				configCenter : config_center,
				envParam : env_paths
			};

			$.post(url, data, function(response) {
				$('#add_env_frm')[0].reset();
				$('#createEnvModal').modal('hide');
				showMessage(response.message, function() {
					$(grid_selector).trigger("reloadGrid");
				});
			});
		}

	});

	/* （创建）环境取消操作,同时绑定取消创建和关闭窗口两个按钮 */
	$('#createClose,#cancel').click(function() {
		$('#createEnvModal').modal('hide');
		$('#add_env_frm')[0].reset();
		$('label.error').remove();
	});

	$(".close").click(function() {
		$('#createEnvModal').modal('hide');
		$('#add_env_frm')[0].reset();
		/** 清空格式报警信息* */
		$('label.error').remove();
	});

	/* （修改） 环境提交操作 */
	$('#modify').click(function() {
		if ($("#modify_env_frm").valid()) {
			var url = base + 'env/modify';

			var env_id_edit = $('#env_id_edit').val();
			var env_name_edit = $('#env_name_edit').val();
			var env_desc_edit = $('#env_desc_edit').val();
			var config_center_edit = $('#config_center_edit').val();

			// 获取环境变量
			var env_paths = '';
			$('#modifyEnvModal input[name="env_path_edit"]').each(function() {
				if ($.trim($(this).val()) != '') {
					env_paths += $.trim($(this).val()) + ";";
				}
			})
			env_paths = env_paths.substring(0, env_paths.length - 1);
			data = {
				envId : env_id_edit,
				envName : env_name_edit,
				configCenter : config_center_edit,
				envDesc : env_desc_edit,
				envParam : env_paths
			};

			$.post(url, data, function(response) {
				$('#modifyEnvModal').modal('hide');
				showMessage(response.message, function() {
					$(grid_selector).trigger("reloadGrid");
				});
			});
		}
	});

	/* （修改） 环境取消操作 */
	$('#modify_cancel').click(function() {
		$('#modifyEnvModal').modal('hide');
		$('#modify_env_frm')[0].reset();
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
							url : base + 'env/advancedSearch?params='
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
	 * Validate 创建环境 form
	 */
	$('#add_env_frm').validate({
		rules : {
			env_name : {
				required : true,
				stringNameCheck : true,
				maxlength : 64,
				remote : {
					url : base + "env/checkEnvName",
					type : "post",
					dataType : "json",
					data : {
						envName : function() {
							return $("#env_name").val();
						}
					},
					dataFilter : function(data) {// 判断控制器返回的内容
						return data;
					}
				}
			},
			config_center : {
				required : true,
				stringNameCheck : true,
				maxlength : 64
			},
			env_desc : {
				maxlength : 200,
				stringCheck : true
			}
		},
		messages : {
			env_name : {
				required : "环境名称不能为空值，请输入。",
				stringNameCheck : "只能包含(中文、英文、数字、下划线等字符)合法字符",
				maxlength : $.validator.format("环境名称不能大于64个字符"),
				remote : "环境名称已经存在，请重新填写"
			},
			config_center : {
				required : "配置中心不能为空值，请输入。",
				stringNameCheck : "只能包含(中文、英文、数字、下划线等字符)合法字符",
				maxlength : $.validator.format("配置中心不能大于64个字符"),
			},
			env_desc : {
				maxlength : $.validator.format("描述信息不能大于200个字符")
			}
		}
	});

	/**
	 * 校验修改环境页面
	 */
	$("#modify_env_frm").validate({
		rules : {
			config_center_edit : {
				required : true,
				stringNameCheck : true,
				maxlength : 64
			},
			env_desc_edit : {
				maxlength : 200,
				stringCheck : true
			}
		},
		messages : {
			config_center_edit : {
				required : "配置中心不能为空值，请输入。",
				stringNameCheck : "只能包含(中文、英文、数字、下划线等字符)合法字符",
				maxlength : $.validator.format("配置中心不能大于64个字符"),
			},
			env_desc_edit : {
				maxlength : $.validator.format("描述信息不能大于200个字符")
			}
		}
	});

	$('#add_param')
			.click(
					function() {
						var str = '<div class="form-group"><label class="col-sm-3"></label><div class="col-sm-8" style="margin-left:-15px">'
								+ '<input class="form-control" name="env_path" type="text" placeholder="请输入环境变量" /></div>'
								+ '<a href="#" class="remove_param" style="line-height:30px"><span class="glyphicon glyphicon-remove delete-param"></span></a>'
								+ '</div>';
						$('#createEnvModal .env_path:last').after(str);
					})

	$('#createEnvModal').on('click', '.remove_param', function() {
		$(this).parent().remove();
	})

	$('#add_param_edit')
			.click(
					function() {
						var str = '<div class="form-group"><label class="col-sm-3"></label><div class="col-sm-8" style="margin-left:-15px">'
								+ '<input class="form-control" name="env_path_edit" type="text" placeholder="请输入环境变量" /></div>'
								+ '<a href="#" class="remove_param" style="line-height:30px"><span class="glyphicon glyphicon-remove delete-param"></span></a>'
								+ '</div>';
						$('#modifyEnvModal :input[name="env_path_edit"]:last')
								.parent().parent().after(str);
					})
	$('#modifyEnvModal').on('click', '.remove_param', function() {
		$(this).parent().remove();
	})
});

/**
 * @author yangqinglin
 * @datetime 2015年10月8日 12:36
 * @description 添加查询应用函数
 */
function SearchEnvs() {
	var searchEnvName = $('#searchEnvName').val();
	/* 查询是否存在关键词相关的结果 */
	jQuery(grid_selector).jqGrid('setGridParam', {
		url : base + 'env/listSearch',
		postData : {
			search_name : searchEnvName
		}
	}).trigger("reloadGrid");

}

/**
 * @author yangqinglin
 * @datetime 2015年10月29日 11:23
 * @description 添加高级查询函数
 */
function AdvancedSearchEnvs() {
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
 * @datetime 2015年1月21日 16:15
 * @description 删除环境函数
 */
function deleteEnvironment(env_id, env_name) {
	url = base + "env/remove";
	data = {
		envId : env_id,
		envName : env_name
	};

	bootbox
			.dialog({
				message : '<div class="alert alert-info" style="margin:10px"><span class="glyphicon glyphicon-info-sign"></span>&nbsp;确定删除'
						+ env_name + '吗？&nbsp;</div>',
				title : "删除环境",
				buttons : {
					cancel : {
						label : "<i class='ace-icon fa fa-times gray bigger-125'></i> <b>取消</b>",
						className : "btn-sm btn-danger btn-round",
						callback : function() {
							$(grid_selector).trigger("reloadGrid");
						}
					},
					main : {
						label : "<i class='ace-icon fa fa-floppy-o bigger-125'></i><b>确定</b>",
						className : "btn-sm btn-success btn-round",
						callback : function() {
							$.post(url, data, function(response) {
								showMessage(response.message, function() {
									$(grid_selector).trigger("reloadGrid");
								});
							});
						}
					}
				}
			});
}

function editEnvironment(env_id, env_name, env_desc, configCenter, envParam) {
	/* 保存应用的原始名称，便于校验使用 */
	$('#env_id_edit').val(env_id);
	$('#env_name_edit').val(env_name);
	$('#config_center_edit').val(
			(configCenter == null || configCenter == "null") ? ""
					: configCenter);
	$('#env_desc_edit').val(
			(env_desc == null || env_desc == "null") ? "" : env_desc);
	if (envParam != '' && envParam != 'null') {
		envParam = envParam.replace(new RegExp(/:/g), '"');
		var params = envParam.split(";")
		$(':input[name="env_path_edit"]').eq(0).val(params[0]);
		if (params.length > 1) {
			$('#modifyEnvModal .remove_param').parent().remove();
			for (var i = 1; i < params.length; i++) {
				var str = '<div class="form-group"><label class="col-sm-3"></label><div class="col-sm-8" style="margin-left:-15px">'
						+ '<input class="form-control" name="env_path_edit" type="text" placeholder="请输入环境变量" value='
						+ params[i]
						+ '></div>'
						+ '<a href="#" class="remove_param" style="line-height:30px"><span class="glyphicon glyphicon-remove delete-param"></span></a>'
						+ '</div>';
				$('#modifyEnvModal input[name="env_path_edit"]:last').parent()
						.parent().after(str);
			}
		}

	}

	$('#modifyEnvModal').modal('show');
}
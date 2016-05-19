var grid_selector = "#res_list";
var page_selector = "#res_page";
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
						url : base + 'resource/list',
						datatype : "json",
						height : '100%',
						autowidth : true,
						colNames : [ 'RES_ID', '资源名称', 'CPU份额', '内存容量', /*'磁盘IO',*/
								'描述', '备注信息', '创建人ID', '创建人', '创建日期', '快捷操作' ],
						colModel : [
								{
									name : 'resId',
									index : 'resId',
									width : 10,
									hidden : true
								},
								{
									name : 'resName',
									index : 'resName',
									width : 10,
									formatter : function(cell, opt, obj) {
										return '<a href="' + base
												+ 'resource/detail/'
												+ obj.resId + '.html">' + cell
												+ '</a>';
									}
								},
								{
									name : 'resCPU',
									index : 'resCPU',
									width : 8
								},
								{
									name : 'resMEM',
									index : 'resMEM',
									width : 8
								},
								// {
								// name : 'resBLKIO',
								// index : 'resBLKIO',
								// width : 8
								// },
								{
									name : 'resDesc',
									index : 'resDesc',
									width : 15
								},
								{
									name : 'resComment',
									index : 'resComment',
									width : 15
								},
								{
									name : 'resCreator',
									index : 'resCreator',
									width : 10,
									hidden : true
								},
								{
									name : 'resUserName',
									index : 'resUserName',
									width : 8
								},
								{
									name : 'resCreatetime',
									index : 'resCreatetime',
									width : 12
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
											strHtml += "<button class=\"btn btn-primary btn-xs btn-round\" onclick=\"editResource('"
													+ rowObject.resId
													+ "','"
													+ rowObject.resName
													+ "','"
													+ rowObject.resCPU
													+ "','"
													+ rowObject.resMEM
													+ "','"
													// + rowObject.resBLKIO
													// + "','"
													+ rowObject.resDesc
													+ "','"
													+ rowObject.resComment
													+ "')\"><i class=\"ace-icon fa fa-pencil align-top\"></i>&nbsp;<b>编辑</b></button>&nbsp;"
										}
										var dele = $("#delete_app").val();
										if (typeof (dele) != "undefined") {
											strHtml += "<button class=\"btn btn-inverse btn-xs btn-round\" onclick=\"deleteResource('"
													+ rowObject.resId
													+ "','"
													+ rowObject.resName
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

	// 批量删除资源函数
	$('#batch_remove_resource_btn').on('click',function() {
		var res_ids = $(grid_selector).jqGrid("getGridParam","selarrrow");
		var resname_string = "";
		var idList = "";
		if (res_ids.length == 0) {
			showMessage("请先选择需要删除的定制资源项!");
			return;
		}
		for (var i = 0; i < res_ids.length; i++) {
			var rowData = $(grid_selector).jqGrid("getRowData",
					res_ids[i]);
			idList += (i == res_ids.length - 1) ? rowData.resId
					: rowData.resId + ',';
			resname_string += (i == res_ids.length - 1) ? rowData.resName
					: rowData.resName + ',&nbsp;&nbsp;';
		}
		bootbox.dialog({
		    message: '<div class="alert alert-info" style="margin:10px"><span class="glyphicon glyphicon-info-sign"></span>&nbsp;确定要删除资源&nbsp;'+resname_string+ '?</div>',
		    title: "删除资源",
		    buttons: {
		    	cancel: {
		            label: "取消",
		            className: "btn-danger btn-round",
		            callback: function() {
		            	$(grid_selector).trigger("reloadGrid");
		            }
		        },
		        main: {
		            label: "确定",
		            className: "btn-success btn-round",
		            callback: function() {
		            	$.post(base+'resource/delete',
						   {
	            		resIds : idList,
	            		resNames : resname_string
						   },
						   function(response) {
								if (response.success) {
									showMessage(response.message);
								} else {
									showMessage("<font color=\"red\">错误</font>：删除(<font color=\"blue\"><i>"
											+ resname_string
											+ "</i></font>)定制资源失败！<br><font color=\"green\">原因</font>："
											+ response.message);
								}
								$(grid_selector).trigger("reloadGrid");
						});
		            }
		        }
		    }
		});
	});

	/* （创建）新的应用 */
	$('#submit').click(function() {
		if ($("#add_res_frm").valid()) {
			var url = base + 'resource/create';
			resource_name = $('#resource_name').val();
			resource_cpu = $('#resource_cpu').val();
			resource_mem = $('#resource_mem').val();
			//resource_blkio = $('#resource_blkio').val();
			resource_desc = $('#resource_desc').val();
			resource_comment = $('#resource_comment').val();

			if (!resource_name || resource_name.length == 0) {
				/** @bug122_finish */
				bootbox.alert("【资源名称】不能为空值。");
				return;
			}

			data = {
				resName : resource_name,
				resCPU : resource_cpu,
				resMEM : resource_mem,
				//resBLKIO : resource_blkio,
				resDesc : resource_desc,
				resComment : resource_comment
			};
			$.post(url, data, function(response) {
				$('#add_res_frm')[0].reset();
				$('#createResModal').modal('hide');
				showMessage(response.message, function() {
					$(grid_selector).trigger("reloadGrid");
					/** @bug276_begin:添加定制资源,添加成功后再次新打开添加窗口,滚动块位置没有重置* */
					/** 初始化两个拖动条* */
					/** 控制CPU份额滚动条 */
					$("#input-cpu-slider").slider({
						value : 10,
						range : "min",
						min : 10,
						max : 100,
						step : 10
					});

					/** 控制磁盘IO权重滚动条 */
					/** @2016年2月1日，暂时屏蔽磁盘IO部分功能 */
					// $("#input-blkio-slider").slider({
					// value : 10,
					// range : "min",
					// min : 10,
					// max : 100,
					// step : 10
					// });
					/** @bug276_finish* */
				});
			});
		}

	});

	/* （创建）应用取消操作,同时绑定取消创建和关闭窗口两个按钮 */
	$('#createClose,#cancel').click(function() {
		$('#createResModal').modal('hide');
		$('#add_res_frm')[0].reset();
		$('label.error').remove();
		/** 初始化两个拖动条* */
		/** 控制CPU份额滚动条 */
		$("#input-cpu-slider").slider({
			value : 10,
			range : "min",
			min : 10,
			max : 100,
			step : 10
		});

		/** 控制磁盘IO权重滚动条 */
//		$("#input-blkio-slider").slider({
//			value : 10,
//			range : "min",
//			min : 10,
//			max : 100,
//			step : 10
//		});

	});

	$(".close").click(function() {
		$('#createResModal').modal('hide');
		$('#add_res_frm')[0].reset();
		/** 清空格式报警信息* */
		$('label.error').remove();
	});

	/* （修改） 应用提交操作 */
	$('#modify').click(function() {
		if ($("#modify_res_frm").valid()) {
			url = base + 'resource/update';

			resource_id = $('#resource_id_edit').val();
			resource_name = $('#resource_name_edit').val();
			resource_cpu = $('#resource_cpu_edit').val();
			resource_mem = $('#resource_mem_edit').val();
			//resource_blkio = $('#resource_blkio_edit').val();
			resource_desc = $('#resource_desc_edit').val();
			resource_comment = $('#resource_comment_edit').val();

			data = {
				resId : resource_id,
				resName : resource_name,
				resCPU : resource_cpu,
				resMEM : resource_mem,
				//resBLKIO : resource_blkio,
				resDesc : resource_desc,
				resComment : resource_comment
			};
			$.post(url, data, function(response) {
				$('#modifyResModal').modal('hide');
				showMessage(response.message, function() {
					$(grid_selector).trigger("reloadGrid");
				});
			});
		}
	});

	/* （修改） 应用取消操作 */
	$('#modify_cancel').click(function() {
		$('#modifyResModal').modal('hide');
		$('#modify_res_frm')[0].reset();
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

	/** 控制CPU份额滚动条 */
	$("#input-cpu-slider").slider({
		value : 10,
		range : "min",
		min : 10,
		max : 100,
		step : 10,
		slide : function(event, ui) {
			var val = parseInt(ui.value);
			$('#resource_cpu').val(val);
		}
	});

	/** 控制磁盘IO权重滚动条 */
	/** @2016年2月1日，暂时屏蔽磁盘IO部分功能 */
	// $("#input-blkio-slider").slider({
	// value : 10,
	// range : "min",
	// min : 10,
	// max : 100,
	// step : 10,
	// slide : function(event, ui) {
	// var val = parseInt(ui.value);
	// $('#resource_blkio').val(val);
	// }
	// });
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
							url : base + 'resource/advancedSearch?params='
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
	$('#add_res_frm').validate({
		rules : {
			resource_name : {
				required : true,
				stringNameCheck : true,
				maxlength : 64,
				remote : {
					url : base + "resource/checkResName",
					type : "post",
					dataType : "json",
					data : {
						resName : function() {
							return $("#resource_name").val();
						}
					},
					dataFilter : function(data) {// 判断控制器返回的内容
						return data;
					}
				}
			},
			resource_mem : {
				required : true,
				maxlength : 5,
				isValidDockerMem : true
			},
			resource_desc : {
				maxlength : 200,
				stringCheck : true
			},
			resource_comment : {
				maxlength : 200,
				stringCheck : true
			}
		},
		messages : {
			resource_name : {
				required : "资源名称不能为空值，请输入。",
				stringNameCheck : "只能包含(中文、英文、数字、下划线等字符)合法字符",
				maxlength : $.validator.format("资源名称不能大于64个字符"),
				remote : "资源名称已经存在，请重新填写"
			},
			resource_mem : {
				required : "定制资源的内存量不能为空值，请输入。",
				maxlength : $.validator.format("容器内存容量值为不能超过5位的正整数"),
				isValidDockerMem : "Docker内存量为128~16384之间的整数，请重新填写。"
			},
			resource_desc : {
				maxlength : $.validator.format("描述信息不能大于200个字符")
			},
			resource_comment : {
				maxlength : $.validator.format("备注内容不能大于200个字符")
			}
		}
	});

	/**
	 * 校验修改资源页面
	 */
	$("#modify_res_frm").validate({
		rules : {
			resource_mem_edit : {
				required : true,
				maxlength : 5,
				isValidDockerMem : true
			},
			resource_desc_edit : {
				maxlength : 200,
				stringCheck : true
			},
			resource_comment_edit : {
				maxlength : 200,
				stringCheck : true
			}

		},
		messages : {
			resource_mem_edit : {
				required : "定制资源的内存量不能为空值，请输入。",
				maxlength : $.validator.format("容器内存容量不能超过10位整数"),
				isValidDockerMem : "Docker内存量为128~16384之间的整数，请重新填写。"
			},
			resource_desc_edit : {
				maxlength : $.validator.format("描述信息不能大于200个字符")
			},
			resource_comment_edit : {
				maxlength : $.validator.format("备注内容不能大于200个字符")
			}
		}
	});

});

/** 设置资源CPU和磁盘IO的默认设置，分别为1和10 */
function initialDefault() {
	$('#resource_cpu').val(10);
	//$('#resource_blkio').val(10);
}

/**
 * @author yangqinglin
 * @datetime 2015年10月8日 12:36
 * @description 添加查询应用函数
 */
function SearchResources() {
	var searchResName = $('#searchResName').val();
	/* 查询是否存在关键词相关的结果 */
	jQuery(grid_selector).jqGrid('setGridParam', {
		url : base + 'resource/listSearch?search_name=' + searchResName
	}).trigger("reloadGrid");

}

/**
 * @author yangqinglin
 * @datetime 2015年10月29日 11:23
 * @description 添加高级查询函数
 */
function AdvancedSearchRes() {
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
function deleteResource(res_id, res_name) {
	url = base + "resource/delete";
	data = {
		resIds : res_id,
		resNames : res_name
	};
	bootbox.dialog({
	    message: '<div class="alert alert-info" style="margin:10px"><span class="glyphicon glyphicon-info-sign"></span>&nbsp;确定要删除资源&nbsp;'+res_name+ '?</div>',
	    title: "删除资源",
	    buttons: {
	    	cancel: {
	            label: "取消",
	            className: "btn-danger btn-round",
	            callback: function() {
	            	$(grid_selector).trigger("reloadGrid");
	            }
	        },
	        main: {
	            label: "确定",
	            className: "btn-success btn-round",
	            callback: function() {
	            	$.post(url, data, function(response) {
						if (response.success) {
							showMessage(response.message);
						} else {
							showMessage("<font color=\"red\">错误</font>：删除(<font color=\"blue\"><i>"
									+ res_name
									+ "</i></font>)定制资源失败！<br><font color=\"green\">原因</font>："
									+ response.message);
						}
						$(grid_selector).trigger("reloadGrid");
					});
	            }
	        }
	    }
	});
}

function editResource(res_id, res_name, res_cpu, res_mem, /* res_blkio, */
res_desc, res_comment) {
	/* 保存应用的原始名称，便于校验使用 */
	// original_app_name = $.trim(app_name);
	// original_app_url = $.trim(app_url);
	$('#resource_id_edit').val(res_id);
	$('#resource_name_edit').val(res_name);
	$('#resource_cpu_edit').val(res_cpu);
	$('#resource_mem_edit').val(res_mem);
	// $('#resource_blkio_edit').val(res_blkio);

	/** 控制CPU份额滚动条 */
	$("#input-cpu-slider_edit").slider({
		value : res_cpu,
		range : "min",
		min : 10,
		max : 100,
		step : 10,
		slide : function(event, ui) {
			var val = parseInt(ui.value);
			$('#resource_cpu_edit').val(val);
		}
	});

	/** 控制磁盘IO权重滚动条 */
	// $("#input-blkio-slider_edit").slider({
	// value : res_blkio,
	// range : "min",
	// min : 10,
	// max : 100,
	// step : 10,
	// slide : function(event, ui) {
	// var val = parseInt(ui.value);
	// $('#resource_blkio_edit').val(val);
	// }
	// });
	$('#resource_desc_edit').val(
			(res_desc == null || res_desc == "null") ? "" : res_desc);
	$('#resource_comment_edit').val(
			(res_comment == null || res_comment == "null") ? "" : res_comment);
	$('#modifyResModal').modal('show');
}
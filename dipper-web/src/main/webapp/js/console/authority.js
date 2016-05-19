var grid_selector = "#authority_list";
var page_selector = "#authority_page";
/** 保存当前高级查询条件的列数量 */
var advanceColNum = 0;
var authTreeData = [];
var setting = {
	view : {
		selectedMulti : false
	},
	check : {
		enable : true,
		chkStyle : "radio",
		radioType : "level"
	},
	data : {
		simpleData : {
			enable : true
		}
	},
	callback : {
		beforeCheck : function(selector) {
			var tree = $.fn.zTree.getZTreeObj(selector);
			$.each(tree.getCheckedNodes(), function() {
				tree.checkNode(this, false);
			});
		}
	}
};
function createZTreeFun(selector) {
	var tree = $.fn.zTree.init($(selector), setting, authTreeData);
	tree.expandAll(true);
}
function destroyZTreeFun(selector) {
	$.fn.zTree.destroy(selector);
}

jQuery(function($) {
	$(window).on('resize.jqGrid', function() {
		$(grid_selector).jqGrid('setGridWidth', $(".page-content").width());
		$(grid_selector).closest(".ui-jqgrid-bdiv").css({
			'overflow-x' : 'hidden'
		});
	})
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
			})
	jQuery(grid_selector)
			.jqGrid(
					{
						url : base + 'auth/all',
						datatype : "json",
						height : '100%',
						autowidth : true,
						colNames : [ '权限ID', '权限名', '权限描述', '权限URL', '权限类型',
								'权限标记', '权限依赖', '快捷操作' ],
						colModel : [
								{
									name : 'actionId',
									index : 'actionId',
									width : 10,
									hidden : true
								},
								{
									name : 'actionName',
									index : 'actionName',
									width : 10
								},
								{
									name : 'actionDesc',
									index : 'actionDesc',
									width : 10
								},
								{
									name : 'actionRelativeUrl',
									index : 'actionRelativeUrl',
									width : 10
								},
								{
									name : 'actionType',
									index : 'actionType',
									width : 10,
									formatter : function(cellvalue, options,
											rowObject) {
										switch (rowObject.actionType) {
										case 0:
											return '父节点';
										case 1:
											return '子节点';
										}
									}
								},
								{
									name : 'actionRemarks',
									index : 'actionRemarks',
									width : 10
								},
								{
									name : 'parentActionName',
									index : 'parentActionName',
									width : 10
								},
								{
									name : '',
									index : '',
									width : 120,
									fixed : true,
									sortable : false,
									resize : false,
									formatter : function(cellvalue, options,
											rowObject) {
										var strHtml = "";

										var upda = $("#update_auth").val();
										if (typeof (upda) != "undefined") {
											strHtml += "<button class=\"btn btn-xs btn-primary btn-round\" "
													+ "onclick=\"modifyAuthWin('"
													+ rowObject.actionId
													+ "','"
													+ rowObject.actionName
													+ "','"
													+ rowObject.actionDesc
													+ "','"
													+ rowObject.actionRelativeUrl
													+ "','"
													+ rowObject.actionType
													+ "','"
													+ rowObject.actionRemarks
													+ "','"
													+ rowObject.parentActionName
													+ "')\">"
													+ "<b>编辑</b></button> &nbsp;";
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

	// 修改权限名称
	$('#modify_submit').click(function() {
		if ($("#modify_auth_form").valid()) {
			url = base + 'auth/update';
			id = $("#action_id_edit").val();
			name = $("#action_name_edit").val();
			desc = $("#action_desc_edit").val();
			data = {
				actionId : id,
				actionName : name,
				actionDesc : desc
			};
			$.post(url, data, function(response) {
				$('#modifyAuthModal').modal('hide');
				if (response == "") {
					showMessage("更改角色权限异常！");
				} else {
					showMessage(response.message);
				}
				$(grid_selector).trigger("reloadGrid");
			});
		}
	});

	// 更改权限取消按钮
	$('#modify_cancel').click(function() {
		$('#modifyAuthModal').modal('hide');
		$('#modify_auth_form')[0].reset();
		$('label.error').remove();
		$(grid_selector).trigger("reloadGrid");
	});

	// 更改权限规则校验
	$("#modify_auth_form").validate({

		rules : {
			action_name_edit : {
				required : true,
				stringCheck : true,
				maxlength : 20
			},
			action_desc_edit : {
				maxlength : 200,
				stringCheck : true
			}
		},
		messages : {
			action_name_edit : {
				required : "权限名不能为空",
				maxlength : $.validator.format("权限名不能大于20个字符")
			},
			action_desc_edit : {
				maxlength : $.validator.format("权限描述不能大于200个字符")
			}
		}
	});
	// 替换ztree
	$.ajax({
		url : base + "auth/tree", // 要加载数据的地址
		type : 'get', // 加载方式
		dataType : 'json', // 返回数据格式
		success : function(response) {
			if (response == "") {
				authTreeDate = "";
			} else {
				authTreeData = response.data;
			}
		},
		error : function(response) {
			console.log(response);
		}
	});

	// 新增权限按钮
	$("#addAuth").click(function() {
		$("#createAuthModal").modal('show');
		createZTreeFun("#authTree");
	});

	/**
	 * 向查询按钮添加请求提交操作
	 */
	$("#advanced_auth_search").on(
			'click',
			function(event) {
				$('#advancedSearchAuthModal').modal('hide');
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
				$("input[name=search_auth_value]").each(function() {
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
							url : base + 'auth/advancedSearch?params='
									+ column_array + '&values=' + value_array
						}).trigger("reloadGrid");
				//
				$('#advancedSearchAuthModal').modal('hide');
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
	$("#advanced_auth_cancel").on('click', function(event) {
		event.preventDefault();
		$('#advancedSearchAuthModal').modal('hide');
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
		$("#params li:last").find("#search_auth_value").val("");
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

function modifyAuthWin(id, name, desc, url, type, remarks, parentActionName) {
	$("#action_id_edit").val(id);
	$("#action_name_edit").val(name);
	$("#action_desc_edit").val((desc == null || desc == "null") ? "" : desc);
	$("#action_relative_url_edit").val((url == null || url == "null") ? "" : url);
	$("#action_type_edit").val(type);
	$("#action_remarks_edit").val(remarks);
	$("#action_parent_id_edit").val((parentActionName == null || parentActionName == "null") ? "" : parentActionName);
	$("#modifyAuthModal").modal('show');

}

function searchAuthoritys() {
	var actionName = $('#search_auth').val();
	jQuery(grid_selector).jqGrid('setGridParam', {
		url : base + 'auth/all?actionName=' + actionName
	}).trigger("reloadGrid");
}

function AdvancedSearchAuths() {
	/** @bug152_begin 清空用户多选的参数 */
	while ($("#params li").length > 1) {
		$("#remove-param").parent().remove();
	}
	/* 隐藏高级查询第一行的删除打叉按钮 */
	$("#params li:first").find("#remove-param").hide();
	/** 打开高级搜索窗口，之前输入全部清空 */
	$("#params li:first").find("#search_auth_value").val("");
	$("#params li:first").find("#meter").val("0");
	/** @bug152_finish */

	$('#advancedSearchAuthModal').modal('show');
}
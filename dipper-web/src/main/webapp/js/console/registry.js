var grid_selector = "#registry_list";
var page_selector = "#registry_page";
/* 设置保存查询镜像列表的仓库ID全局变量 */
var registry_id_of_slave_images = 0;
/* 保存当前新（修改）仓库的IP和端口可用性状态 */
var registry_ip_port_valid = false;
/* 保存当前（修改）仓库的名称 */
var registry_name_edit_valid = false;
/* 保存（修改）之前的仓库名称 */
var original_registry_name = "";
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
						url : base + 'registry/listWithIP',
						datatype : "json",
						height : '100%',
						autowidth : true,
						colNames : [ '仓库ID', '仓库名称', '仓库端口', '仓库状态', '主机ID',
								'主机地址', '描述信息', '创建时间', '用户ID', '创建用户', '快捷操作' ],
						colModel : [
								{
									name : 'registryId',
									index : 'registryId',
									width : 1,
									hidden : true
								},
								{
									name : 'registryName',
									index : 'registryName',
									width : 8,
									align : 'left',
									formatter : function(cell, opt, obj) {
										return '<i class="fa fa-university"></i><a href="'
												+ base
												+ 'registry/detail/'
												+ obj.registryId
												+ '.html">'
												+ cell + '</a>';
									}
								},
								{
									name : 'registryPort',
									index : 'registryPort',
									width : 5,
									align : 'left'
								},
								{
									name : 'registryStatus',
									index : 'registryStatus',
									width : 1,
									hidden : true,
									align : 'left',
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
									name : 'hostId',
									index : 'hostId',
									width : 1,
									hidden : true
								},
								{
									name : 'hostIP',
									index : 'hostIP',
									width : 7,
									align : 'left'
								},
								{
									name : 'registryDesc',
									index : 'registryDesc',
									width : 15
								},
								{
									name : 'registryCreatetime',
									index : 'registryCreatetime',
									width : 10
								},
								{
									name : 'registryCreator',
									index : 'registryCreator',
									width : 1,
									hidden : true
								},
								{
									name : 'creatorName',
									index : 'creatorName',
									width : 6,
									align : 'left'
								},
								{
									name : '',
									title : false,
									index : '',
									width : 310,
									fixed : true,
									/* 固定像素长度 */
									sortable : false,
									resize : false,
									align : 'left',
									formatter : function(cellvalue, options,
											rowObject) {
										var strHtml = "";
										var upda = $("#update_registry").val();
										if (typeof (upda) != "undefined") {
											strHtml += "<button class=\"btn btn-primary btn-xs btn-round\" onclick=\"editRegistry('"
													+ rowObject.registryId
													+ "','"
													+ rowObject.registryName
													+ "','"
													+ rowObject.registryPort
													+ "','"
													+ rowObject.hostIP
													+ "','"
													+ rowObject.registryDesc
													+ "')\"><i class=\"ace-icon fa fa-pencil align-top\"></i>&nbsp;<b>编辑</b></button> &nbsp;";
										}
										var dele = $("#delete_registry").val();
										if (typeof (dele) != "undefined") {
											strHtml += "<button class=\"btn btn-inverse  btn-xs btn-round\" onclick=\"deleteRegistry('"
													+ rowObject.registryId
													+ "','"
													+ rowObject.registryName
													+ "')\"><i class=\"ace-icon fa fa-trash-o\"></i>&nbsp;<b>删除</b></button> &nbsp;";
										}
										var sync = $("#sync_registry").val();
										if (typeof (sync) != "undefined") {
											strHtml += "<button class=\"btn btn-danger  btn-xs btn-round\" onclick=\"syncRegistry('"
													+ rowObject.hostIP
													+ "','"
													+ rowObject.registryPort
													+ "','"
													+ rowObject.registryId
													+ "','"
													+ rowObject.registryName
													+ "','"
													+ rowObject.hostId
													+ "',this)\"><i class=\"ace-icon fa fa-refresh\"></i>&nbsp;<b>同步</b></button> &nbsp;";
										}
										var showImages = $('#show_Images')
												.val();
										if (typeof (showImages) != "undefined") {
											strHtml += "<button class=\"btn btn-purple  btn-xs btn-round\" onclick=\"queryRegistryImages('"
													+ rowObject.registryId
													+ "')\"><i class=\"ace-icon fa fa-bullseye\"></i>&nbsp;<b>所含镜像</b></button> &nbsp;";
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

	/* 在提交添加主机表中插入所有仓库类型的主机 */
	getRegistryHosts();

	/* 进行最后数据校验，向数据库提交添加仓库 */
	$('#submit')
			.click(
					function() {
						// 添加校验
						if ($("#create_registry_frm").valid()) {
							var registry_hostid = $('#registry_host').val();

							/* 校验主机地址和端口组合作为仓库可以使用 */
							if (!registry_ip_port_valid) {
								showMessage("<i class=\"fa fa-exclamation-triangle\"></i>&nbsp;请选可用的主机和端口组合。");
								return;
							}

							if ($('#registry_host').val() == 0) {
								showMessage("请选择创建仓库所在的主机！");
								return;
							} else {
								/** ****@bug168_begin******添加对于主机的校验，判断在数据库中是否存在********** */
								var check_url = base + 'host/checkHostId';
								var check_data = {
									hostId : registry_hostid
								};
								$
										.post(
												check_url,
												check_data,
												function(response) {
													if (response.success) {
														/** 主机存在通过校验,仓库主机存在 */
														submitCreateRegistry();
													} else {
														showMessage("仓库主机在数据库中不存在，请检查！");
														return;
													}
												});

								/** *****@bug168_finish****************************************** */

							}
						}
					});

	$('#modify_submit').click(function() {
		// 添加校验
		if ($("#modify_registry_frm").valid()) {
			var registry_id = $('#registry_id_edit').val();
			var registry_name = $('#registry_name_edit').val();
			var registry_desc = $('#registry_desc_edit').val();

			data = {
				registry_id : registry_id,
				registry_name : registry_name,
				registry_desc : registry_desc
			};
			url = base + 'registry/update';
			$('#modifyRegistryModal').modal('hide');
			$.post(url, data, function(response) {
				if (response == "") {
					showMessage('更新仓库信息异常！')
				} else {
					showMessage(response.message)
				}
				$(grid_selector).trigger("reloadGrid");
				$("#modify_registry_frm")[0].reset();
			});
		}
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
	 * 向查询按钮添加请求提交操作
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
							url : base + 'registry/advancedSearch?params='
									+ column_array + '&values=' + value_array
						}).trigger("reloadGrid");

				/* 查询完毕，高级搜索窗口消失 */
				$('#advancedSearchRegiModal').modal('hide');
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
		$('#advancedSearchRegiModal').modal('hide');
		$('#advanced_search_frm')[0].reset();
	});

	/* 取消添加仓库操作 */
	$('#cancel').click(function() {
		$('#createRegistryModal').modal('hide');
		$('#create_registry_frm')[0].reset();
		$('label.error').remove();
	});

	/* 取消添加仓库操作 */
	$('#modify_cancel').click(function() {
		$('#modifyRegistryModal').modal('hide');
		$('#modify_registry_frm')[0].reset();
		$('label.error').remove();
	});

	/* 为（新增）仓库端口元素添加可达性校验功能，判断仓库地址与端口对是否可以使用 */
	$('#registry_port').blur(function() {
		/** @bug246_begin:: [仓库管理]创建仓库,当一次连接校验通过后又更改了端口再次校验未返回结果前,仓库直接添加成功 * */
		/** 一旦端口数值发生修改，首先将端口可达性全局变量置为false* */
		registry_ip_port_valid = false;
		/** @bug246_finish* */

		var check_url = base + 'registry/reachRegiHost';

		/* 判断仓库主机的地址是否为空 */
		if ($('#registry_host').val() == 0) {
			showMessage("请选择创建仓库所在的主机！");
			return;
		}

		/* 获取选择主机ID的IP地址信息 */
		reg_host_id = $("#registry_host").find("option:selected").val();
		reg_ipaddr = $("#registry_host").find("option:selected").text();
		reg_port = $('#registry_port').val();

		/* 判断端口号是否为正确的数字格式 */
		if (!isInteger(reg_port)) {
			showMessage("请填写正确格式的端口号");
			return;
		}

		check_data = {
			reg_host_id : reg_host_id,
			registry_port : reg_port,
			registry_ipaddr : reg_ipaddr
		};

		/* 验证Docker仓库地址和端口的可达性 */
		$.post(check_url, check_data, function(response) {
			if (response == "") {
				showMessage("校验出现异常！");
			} else {
				if (response.success) {
					/* 主机地址和端口组合作为仓库可以使用 */
					registry_ip_port_valid = true;
					// showMessage(response.message);
					return;
				} else {
					/* 主机地址和端口组合作为仓库无法使用使用 */
					registry_ip_port_valid = false;
					showMessage(response.message);
					return;
				}
			}
		});
	});

	/* 校验（创建）仓库的表单数据 */
	$("#create_registry_frm").validate({
		rules : {
			registry_name : {
				required : true,
				stringNameCheck : true,
				maxlength : 64,
				remote : {
					url : base + "registry/checkRegiName",
					type : "post",
					dataType : "json",
					data : {
						regiName : function() {
							return $("#registry_name").val();
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
			registry_port : {
				required : true,
				isValidServerPort : true
			},
			registry_desc : {
				maxlength : 200,
				stringCheck : true
			}
		},
		messages : {
			registry_name : {
				required : "仓库名称不能为空",
				maxlength : $.validator.format("仓库名称不能大于64个字符"),
				remote : "仓库名称已经存在，请重新填写"
			},
			registry_port : {
				required : "仓库启动端口不能为空",
				isValidServerPort : "仓库启动端口必须位于1~65535之间"
			},
			registry_desc : {
				maxlength : $.validator.format("描述信息不能大于200个字符")
			}
		}
	});

	/* 校验（修改）仓库的表单数据 */
	$("#modify_registry_frm")
			.validate(
					{
						rules : {
							registry_name_edit : {
								required : true,
								stringNameCheck : true,
								maxlength : 64,
								remote : {
									url : base + "registry/checkRegiName",
									type : "post",
									dataType : "json",
									data : {
										regiName : function() {
											var regi_name_edit = $(
													"#registry_name_edit")
													.val();
											/* 如果原始名称与当前名称一致，直接返回true */
											if (original_registry_name == regi_name_edit) {
												return;
											} else {
												return regi_name_edit;
											}
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
							registry_desc_edit : {
								maxlength : 200,
								stringCheck : true
							}
						},
						messages : {
							registry_name_edit : {
								required : "仓库名称不能为空",
								maxlength : $.validator.format("仓库名称不能大于64个字符"),
								remote : "仓库名称已经存在，请重新填写"
							},
							registry_desc_edit : {
								maxlength : $.validator
										.format("描述信息不能大于200个字符")
							}
						}
					});

});

function submitCreateRegistry() {
	// 添加校验
	if ($("#create_registry_frm").valid()) {
		var registry_name = $('#registry_name').val();
		var registry_hostid = $('#registry_host').val();
		var registry_port = $('#registry_port').val();
		var registry_desc = $('#registry_desc').val();

		data = {
			registry_name : registry_name,
			registry_hostid : registry_hostid,
			registry_port : registry_port,
			registry_desc : registry_desc
		};
		var create_regi_url = base + 'registry/create';
		$('#createRegistryModal').modal('hide');
		$.post(create_regi_url, data, function(response) {
			if (response == "") {
				showMessage("创建仓库异常！");
			} else {
				showMessage(response.message);
			}
			$(grid_selector).trigger("reloadGrid");
			$("#create_registry_frm")[0].reset();
		});

	}

}
/**
 * @author yangqinglin
 * @datetime 2015年10月29日 11:23
 * @description 添加高级查询函数
 */
function AdvancedSearchRegi() {
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

	$('#advancedSearchRegiModal').modal('show');
}

/* 编辑仓库功能 */

function editRegistry(registry_id, registry_name, registry_port,
		registry_hostip, registry_desc) {

	/* 保存原始的仓库名称，便于之后比对使用 */
	original_registry_name = registry_name;
	$('#registry_id_edit').val(registry_id);
	$('#registry_name_edit').val(registry_name);
	$('#registry_host_edit').val(registry_hostip);
	$('#registry_port_edit').val(registry_port);
	/* 如果描述为空则显示内容 */
	$("#registry_desc_edit").text(
			(registry_desc == null || registry_desc == "null") ? ""
					: registry_desc);
	$('#modifyRegistryModal').modal('show');
}

/**
 * @author yangqinglin
 * @datetime 2015年10月8日 10:47
 * @description 添加查询仓库函数
 */
function SearchRegistrys() {
	var searchRegiName = $('#searchRegiName').val();

	/* 查询是否存在关键词相关的结果 */
	jQuery(grid_selector).jqGrid('setGridParam', {
		url : base + 'registry/listSearch?search_name=' + searchRegiName
	}).trigger("reloadGrid");
	console.log("searchName:" + searchRegiName);

}

/**
 * @author yangqinglin
 * @datetime 2015年9月25日 18:48
 * @description 批量同步仓库下镜像的函数
 */
function batchSyncRegistryImages() {
	var ids = $(grid_selector).jqGrid("getGridParam", "selarrrow");
	var regis_name_string = "";
	var regis_ipaddr_string = "";
	var regis_port_string = "";
	var regis_ids = "";
	if (ids.length == 0) {
		showMessage("请点击选择需要同步的仓库!");
		return;
	}
	for (var i = 0; i < ids.length; i++) {
		var rowData = $(grid_selector).jqGrid("getRowData", ids[i]);
		regis_ids += (i == ids.length - 1) ? rowData.registryId
				: rowData.registryId + ',';
		regis_name_string += (i == ids.length - 1) ? rowData.registryName
				: rowData.registryName + ',';
		regis_ipaddr_string += (i == ids.length - 1) ? rowData.hostIP
				: rowData.hostIP + ',';
		regis_port_string += (i == ids.length - 1) ? rowData.registryPort
				: rowData.registryPort + ',';
	}

	bootbox
			.dialog({
				message : '<div class="alert alert-info" style="margin:10px">&nbsp;你确定要同步'
						+ regis_name_string + '&nbsp;?</div>',
				title : "同步镜像仓库",
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
							/* 添加loading显示条 */
							// 显示遮罩层
							showMask();
							// 提示信息显示
							$('#spinner-message font').html("批量同步仓库中,请稍等....");
							$
									.post(
											base + 'registry/sync/batch',
											{
												regi_ids : regis_ids,
												regi_names : regis_name_string,
												regi_ipaddrs : regis_ipaddr_string,
												regi_ports : regis_port_string
											},
											function(response) {
												// 隐藏遮罩层
												hideMask();
												if (response == "") {
													showMessage("<font color=\"red\">错误</font>：同步(<font color=\"blue\"><i>"
															+ regis_name_string
															+ "</i></font>)仓库异常！<br>");
												} else {
													if (response.success) {
														showMessage(response.message);
													} else {
														showMessage("<font color=\"red\">错误</font>：同步(<font color=\"blue\"><i>"
																+ regis_name_string
																+ "</i></font>)仓库失败！<br><font color=\"green\">原因</font>："
																+ response.message);
													}
												}
												$(grid_selector).trigger(
														"reloadGrid");
											});
						}
					}
				}
			});
}

/**
 * @author yangqinglin
 * @datetime 2015年9月25日 18:48
 * @description 批量删除仓库函数
 */
function batchRemoveRegistrys() {
	var ids = $(grid_selector).jqGrid("getGridParam", "selarrrow");
	var regis_ids = "";
	var regis_name_string = "";
	if (ids.length == 0) {
		showMessage("请点击选择批量删除的仓库!");
		return;
	}
	for (var i = 0; i < ids.length; i++) {
		var rowData = $(grid_selector).jqGrid("getRowData", ids[i]);
		regis_ids += (i == ids.length - 1) ? rowData.registryId
				: rowData.registryId + ',';
		regis_name_string += (i == ids.length - 1) ? rowData.registryName
				: rowData.registryName + ',';
	}
	bootbox
			.dialog({
				message : '<div class="alert alert-info" style="margin:10px"><span class="glyphicon glyphicon-info-sign"></span>&nbsp;确定删除仓库'
						+ regis_name_string + '&nbsp;</div>',
				title : "删除镜像仓库",
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
							// 显示遮罩层
							showMask();
							// 提示信息显示
							$('#spinner-message font').html("批量删除仓库中,请稍等....");
							$
									.post(
											base + 'registry/remove/batch',
											{
												regi_ids : regis_ids,
												regi_names : regis_name_string
											},
											function(response) {
												// 隐藏遮罩层
												hideMask();
												if (response == "") {
													showMessage("<font color=\"red\">错误</font>：删除(<font color=\"blue\"><i>"
															+ regis_name_string
															+ "</i></font>)仓库异常！");
												} else {
													if (response.success) {
														showMessage(response.message);
													} else {
														showMessage("<font color=\"red\">错误</font>：删除(<font color=\"blue\"><i>"
																+ regis_name_string
																+ "</i></font>)仓库失败！<br><font color=\"green\">原因</font>："
																+ response.message);
													}
												}
												$(grid_selector).trigger(
														"reloadGrid");
											});
						}
					}
				}
			});
}

/**
 * @author yangqinglin
 * @datetime 2015年9月6日 18:48
 * @description 删除仓库函数
 */
function deleteRegistry(registry_id, registry_name) {
	url = base + "registry/delete";
	data = {
		registryIds : registry_id,
		registryName : registry_name
	};
	bootbox
			.dialog({
				message : '<div class="alert alert-info" style="margin:10px"><span class="glyphicon glyphicon-info-sign"></span>&nbsp;确定删除'
						+ registry_name + '吗?&nbsp;' + '?</div>',
				title : "删除镜像仓库",
				buttons : {
					cancel : {
						label : "取消",
						className : "btn-danger btn-round",
						callback : function() {
							location.reload();
						}
					},
					main : {
						label : "确定",
						className : "btn-success btn-round",
						callback : function() {
							// 显示遮罩层
							showMask();
							// 提示信息显示
							$('#spinner-message font').html("删除仓库中,请稍等....");
							$
									.post(
											url,
											data,
											function(response) {
												// 隐藏遮罩层
												hideMask();
												if (response == "") {
													showMessage("<font color=\"red\">错误</font>：删除(<font color=\"blue\"><i>"
															+ registry_name
															+ "</i></font>)仓库异常！");
												} else {
													if (response.success) {
														showMessage(response.message);
													} else {
														showMessage("<font color=\"red\">错误</font>：删除(<font color=\"blue\"><i>"
																+ registry_name
																+ "</i></font>)仓库失败！<br><font color=\"green\">原因</font>："
																+ response.message);
													}
												}
												$(grid_selector).trigger(
														"reloadGrid");
											});
						}
					}
				}
			});

}

/**
 * @author yangqinlgin
 * @datetime 2015年9月6日 19:00
 * @description 同步仓库函数
 */
function syncRegistry(host_ip, server_port, regi_id, regi_name, host_id, obj) {
	var sync_url = base + 'registry/syncRegiImgInfo';

	sync_data = {
		registry_port : server_port,
		registry_ipaddr : host_ip,
		registry_id : regi_id,
		registry_name : regi_name,
		registry_host : host_id
	};

	// 显示遮罩层
	showMask();
	// 提示信息显示
	$('#spinner-message font').html("仓库同步中,请稍等....");
	$(obj).attr("disabled", true);
	/* 验证Docker仓库地址和端口的可达性 */
	$.post(sync_url, sync_data, function(response) {
		// 隐藏遮罩层
		hideMask();
		if (response == "") {
			showMessage("同步仓库异常！");
		} else {
			if (response.success) {
				showMessage(response.message);
			} else {
				showMessage(response.message);
			}
		}
		$(grid_selector).trigger("reloadGrid");
	});
}

/**
 * @author yangqinlgin
 * @datetime 2015年9月25日 19:00
 * @description 同时同步多个仓库函数，无输入参数，根据用户选择的确定
 */
function syncMultiRegistry() {
	var sync_url = base + 'registry/syncRegiImgInfo';

	sync_data = {
		registry_port : server_port,
		registry_ipaddr : host_ip,
		registry_id : regi_id
	};

	/* 验证Docker仓库地址和端口的可达性 */
	$.post(sync_url, sync_data, function(response) {
		if (response == "") {
			showMessage("同步仓库异常！");
		} else {
			if (response.success) {
				showMessage(response.message);
			} else {
				showMessage(response.message);
			}
		}
		$(grid_selector).trigger("reloadGrid");
	});
}

/**
 * 获取主机的ID和IP地址列表
 */
// 获取仓库类型的所有主机信息
function getRegistryHosts() {
	$.ajax({
		type : 'get',
		url : base + 'registry/registryHosts',
		dataType : 'json',
		success : function(array) {
			$.each(array, function(index, obj) {
				var hostid = obj.hostId;
				var hostip = decodeURIComponent(obj.hostIp);
				$('#registry_host').append(
						'<option value="' + hostid + '">' + hostip
								+ '</option>');
			});
		}
	});
}
function getClusterList() {
	/* 首先清空元素下的所有节点 */
	$('#registry_hostid').empty();
	$.ajax({
		type : 'get',
		url : base + 'registry/registryHosts',
		dataType : 'json',
		success : function(array) {
			$.each(array, function(index, obj) {
				var hostid = obj.hostId;
				var hostip = decodeURIComponent(obj.hostIp);
				$('#registry_hostid').append(
						'<option value="' + hostid + '">' + hostip
								+ '</option>');
			});
		}
	});
}

/**
 * 获取主机的ID和IP地址列表(已有选项的情况下)
 */
function getClusterListSelected(select_host_id, select_host_ip) {
	/* 首先清空元素下的所有节点 */
	$('#registry_hostid').empty();
	$.ajax({
		type : 'get',
		url : base + 'registry/registryHosts',
		dataType : 'json',
		success : function(array) {
			$.each(array, function(index, obj) {
				var hostid = obj.hostId;
				var hostip = decodeURIComponent(obj.hostIp);
				$('#registry_hostid').append(
						'<option value="' + hostid + '">' + hostip
								+ '</option>');
			});
		}
	});
}

function validateRegistryName(reg_name) {
	/* 添加仓库之前进行数据校验 */
	if (reg_name == '') {
		showMessage("仓库的名称不能为空！");
		return;
	} else if (!isSpecialChar(reg_name)) {
		showMessage("仓库名称中不能包含特殊字符");
		return;
	}
}

function validateReachRegiHost() {
	var check_url = base + 'registry/reachRegiHost';

	/* 获取选择主机ID的IP地址信息 */
	reg_ipaddr = $("#registry_host").find("option:selected").text();
	reg_port = $('#registry_port').val();

	check_data = {
		registry_port : reg_port,
		registry_ipaddr : reg_ipaddr
	};

	/* 验证Docker仓库地址和端口的可达性 */
	$.post(check_url, check_data, function(response) {
		if (response == "") {
			showMessage("检测出现异常！");
		} else {
			if (response.success) {
				showMessage(response.message);
			} else {
				showMessage(response.message);
			}
		}
	});
}

/**
 * @author yangqinglin
 * @datetime 2015年9月10日 13:06
 * @description 查看某个仓库中所有的镜像列表页面
 */
function queryRegistryImages(registryid) {
	window.location = "images.html?registry_id=" + registryid;
}

/**
 * @author yangqinglin
 * @datetime 2015年9月10日 17:12
 * @description 返回上一个页面
 */
function gobackpage() {
	history.go(-1);
}

/**
 * @author yangqinlgin
 * @datetime 2015年9月7日 17:51
 * @description 添加修改仓库部分的功能函数
 */
function editRegistry_backup(registry_id, registry_name, registry_port,
		host_id, registry_hostip, registry_desc) {
	var url;
	if (null == registry_id) {
		url = base + 'registry/update';
	} else {
		url = base + 'registry/update';
	}
	bootbox
			.dialog({
				title : "<b>编辑仓库</b>",
				message : "<div class='well ' style='margin-top:1px;'>"
						+ "<form class='form-horizontal' role='form' id='add_item_frm'>"
						+ "<div class='form-group'>"
						+ "<label class='col-sm-3'><b>仓库名称：</b></label>"
						+ "<div class='col-sm-9'>"
						+ "<input id='registry_id' value='"
						+ registry_id
						+ "' type='hidden'/>"
						+ "<input id=\"registry_name\" type='text' class=\"form-control\" value='"
						+ registry_name
						+ "' />"
						+ "</div>"
						+ "</div>"
						+ "<div class='form-group'>"
						+ "<label class='col-sm-3'><b>主机IP：</b></label>"
						+ "<div class='col-sm-9'>"
						+ "<input id=\"registry_hostid\" readonly=\"readonly\" type='text' value='"
						+ registry_hostip
						+ "' class=\"form-control\" />"
						+ "</div>"
						+ "</div>"
						+ "<div class='form-group'>"
						+ "<label class='col-sm-3'><b>端口号：</b></label>"
						+ "<div class='col-sm-9'>"
						+ "<input id=\"registry_port\" readonly=\"readonly\" type='text' value='"
						+ registry_port
						+ "' class=\"form-control\" />"
						+ "</div>"
						+ "</div>"
						+ "<div class='form-group'>"
						+ "<label class='col-sm-3'><b>仓库描述：</b></label>"
						+ "<div class='col-sm-9'>"
						+ "<textarea id=\"registry_desc\" class=\"form-control\" rows=\"3\">"
						+ registry_desc
						+ "</textarea>"
						+ "</div>"
						+ "</div>"
						+ "</div>" + "</form>" + "</div>",
				buttons : {
					"success" : {
						"label" : "<i class=\"fa fa-floppy-o\"></i>&nbsp;<b>保存</b>",
						"className" : "btn-sm btn-round btn-success",
						"callback" : function() {
							reg_id = $('#registry_id').val();
							if ("undefined" == reg_id) {
								reg_id = null;
							}
							reg_name = $('#registry_name').val();
							reg_port = $('#registry_port').val();
							reg_hostid = $('#registry_hostid').val();
							reg_desc = $('#registry_desc').val();

							data = {
								registry_id : reg_id,
								registry_name : reg_name,
								registry_port : reg_port,
								registry_hostid : reg_hostid,
								registry_desc : reg_desc
							};
							$.post(url, data, function(response) {
								if (response == "") {
									showMessage("修改仓库信息异常！");
									$(grid_selector).trigger("reloadGrid");
								} else {
									showMessage(response.message, function() {
										$(grid_selector).trigger("reloadGrid");
									});
								}
							});
						}
					},
					"cancel" : {
						"label" : "<i class=\"fa fa-times-circle\"></i>&nbsp;<b>取消</b>",
						"className" : "btn-sm btn-round btn-danger",
						"callback" : function() {
						}
					}
				}
			});
	/* 添加默认显示当前主机的IP地址信息 */
	$('#registry_hostid').append(
			'<option value=' + host_id + ' selected=\"selected\">'
					+ registry_hostip + '</option>');
}

/**
 * @author yangqinlgin
 * @datetime 2015年9月10日 17:25
 * @description 添加在仓库下所有镜像列表(2015年9月11日)的内容
 */
var grid_regimg_selector = "#registry_image_list";
var page_regimg_selector = "#registry_image_page";
jQuery(function($) {
	$(window).on(
			'resize.jqGrid',
			function() {
				$(grid_regimg_selector).jqGrid('setGridWidth',
						$(".page-content").width());
				$(grid_regimg_selector).closest(".ui-jqgrid-bdiv").css({
					'overflow-x' : 'hidden'
				});
			});
	var parent_column = $(grid_regimg_selector).closest('[class*="col-"]');

	$(document).on(
			'settings.ace.jqGrid',
			function(ev, event_name, collapsed) {
				if (event_name === 'sidebar_collapsed'
						|| event_name === 'main_container_fixed') {
					setTimeout(function() {
						$(grid_regimg_selector).jqGrid('setGridWidth',
								parent_column.width());
					}, 0);
				}
			});
	jQuery(grid_regimg_selector).jqGrid(
			{
				url : base + 'registry/listslaveimages?registry_id='
						+ registry_id_of_slave_images,
				datatype : "json",
				height : '100%',
				autowidth : true,
				colNames : [ '镜像ID', '镜像UUID', '镜像状态', '镜像名称', '镜像标签', '镜像大小',
						'镜像描述', '应用ID', '应用名称', '镜像端口', '创建时间' ],
				colModel : [ {
					name : 'imageId',
					index : 'imageId',
					width : 6,
					hidden : true
				}, {
					name : 'imageUuid',
					index : 'imageUuid',
					width : 6,
					align : 'left'
				}, {
					name : 'imageStatus',
					index : 'imageStatus',
					width : 4,
					align : 'left',
					formatter : function(cellvalue, options, rowObject) {
						switch (cellvalue) {
						case 0:
							return '删除';
						case 1:
							return '已发布';
						case 2:
							return '已制作';
						default:
							return '连接异常';
						}
					}
				}, {
					name : 'imageName',
					index : 'imageName',
					width : 15,
					align : 'left'
				}, {
					name : 'imageTag',
					index : 'imageTag',
					width : 6,
					align : 'left'
				}, {
					name : 'imageSize',
					index : 'imageSize',
					width : 6,
					align : 'left',
					hidden : true
				}, {
					name : 'imageDesc',
					index : 'imageDesc',
					width : 6,
					align : 'left',
					hidden : true
				}, {
					name : 'appId',
					index : 'appId',
					width : 6,
					align : 'left',
					hidden : true
				}, {
					name : 'appName',
					index : 'appName',
					width : 6,
					align : 'left'
				}, {
					name : 'imagePort',
					index : 'imagePort',
					width : 8,
					align : 'left',
					hidden : true
				}, {
					name : 'imageCreatetime',
					index : 'imageCreatetime',
					width : 10,
					align : 'left'
				} ],
				viewrecords : true,
				rowNum : 10,
				rowList : [ 10, 20, 50, 100, 1000 ],
				pager : page_regimg_selector,
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
				}
			});
	$(window).triggerHandler('resize.jqGrid'); // 窗口resize时重新resize表格，使其变成合适的大小
	jQuery(grid_regimg_selector).jqGrid( // 分页栏按钮
	'navGrid', page_regimg_selector, { // navbar options
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
		$(table).find('input:checkbox').addClass('ace').wrap('<label />')
				.after('<span class="lbl align-top" />');
		$('.ui-jqgrid-labels th[id*="_cb"]:first-child').find(
				'input.cbox[type=checkbox]').addClass('ace').wrap('<label />')
				.after('<span class="lbl align-top" />');
	}
});
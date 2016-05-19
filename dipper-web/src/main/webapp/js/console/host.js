var grid_selector = "#host_list";
var page_selector = "#host_page";
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
	var hostType = $('#hostType').val();
	jQuery(grid_selector)
			.jqGrid(
					{
						url : base + 'host/list',
						datatype : "json",
						postData : {
							'hostType' : hostType
						},
						height : '100%',
						autowidth : true,
						colNames : [ 'ID', 'UUID', '主机名称', '主机类型', '状态',
								'IP地址', 'CPU（核数）', '内存（MB）', '所在集群',
								'clusterId', '内核版本', '创建时间', '快捷操作' ],
						colModel : [
								{
									name : 'hostId',
									index : 'hostId',
									width : 15,
									hidden : true
								},
								{
									name : 'hostUuid',
									index : 'hostUuid',
									width : 15,
									hidden : true
								},
								{
									name : 'hostName',
									index : 'hostName',
									width : 15,
									formatter : function(cell, opt, obj) {
										return '<a href="' + base
												+ 'host/detail/' + obj.hostId
												+ '.html">' + cell + '</a>';
									}
								},
								{
									name : 'hostType',
									index : 'hostType',
									width : 8,
									formatter : function(cellvalue, options,
											rowObject) {
										switch (rowObject.hostType) {
										case 0:
											return 'SWARM';
										case 1:
											return 'DOCKER';
										case 2:
											return 'REGISTRY';
										case 3:
											return 'NGINX';
										case 4:
											return '其他';
										}
									}
								},
								{
									name : 'hostStatus',
									index : 'hostStatus',
									width : 8,
									formatter : function(cell, opt, obj) {
										switch (obj.hostStatus) {
										case 0:
											return '<i class="fa fa-stop text-danger">&nbsp; 关闭</i>';
										case 1:
											return '<i class="fa fa-play-circle text-success"><b> &nbsp;健康<b></i>';
										case 2:
											return '<i class="fa fa-stop text-warning">&nbsp; 异常</i>';
										}
									}
								},
								{
									name : 'hostIp',
									index : 'hostIp',
									width : 12
								},
								{
									name : 'hostCpu',
									index : 'hostCpu',
									width : 8
								},
								{
									name : 'hostMem',
									index : 'hostMem',
									width : 8
								},
								{
									name : 'clusterName',
									index : 'clusterName',
									width : 12
								},
								{
									name : 'clusterId',
									index : 'clusterId',
									width : 8,
									hidden : true
								},
								{
									name : 'hostKernelVersion',
									index : 'hostKernelVersion',
									width : 15
								},
								{
									name : 'hostCreatetime',
									index : 'hostCreatetime',
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
										var upda = $("#update_host").val();
										if (typeof (upda) != "undefined") {
											strHtml += "<button class=\"btn btn-xs btn-primary btn-round\" onclick=\"updateHostWin('"
													+ rowObject.hostId
													+ "','"
													+ rowObject.hostUuid
													+ "','"
													+ rowObject.hostName
													+ "','"
													+ rowObject.hostDesc
													+ "')\">"
													+ "<i class=\"ace-icon fa fa-pencil-square-o bigger-125\"></i>"
													+ "<b>编辑</b></button> &nbsp;";
										}

										var dele = $("#delete_host").val();
										if (typeof (dele) != "undefined") {
											var dis = "";
											if (rowObject.clusterId != null) {
												dis = "disabled";
											}
											strHtml += "<button class=\"btn btn-xs btn-inverse btn-round\" onclick=\"removeHost('"
													+ rowObject.hostId
													+ "','"
													+ rowObject.hostType
													+ "','"
													+ rowObject.clusterId
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
						// 当从服务器返回响应时执行
						loadComplete : function() {
							var table = this;
							setTimeout(function() {
								styleCheckbox(table);
								updateActionIcons(table);
								updatePagerIcons(table);
								enableTooltips(table);
							}, 0);
						},
						onSelectRow : function(rowid, status) {
							// var flag=true;
							// var ids = $(grid_selector).jqGrid("getGridParam",
							// "selarrrow");
							// //控制批量删除按钮
							// for (var i = 0; i < ids.length; i++) {
							// var rowData =
							// $(grid_selector).jqGrid("getRowData", ids[i]);
							// if(rowData.clusterId!=""){
							// flag=false;
							// }
							// }
							// if(flag){
							// $('#remove').parent().show();
							// }else{
							// $('#remove').parent().hide();
							// }
							// //控制电源管理按钮
							// if(ids.length==1){
							// $('#powerManager').attr("disabled",false);
							// //根据电源状态 控制 启动和关闭主机按钮
							// var rowData =
							// $(grid_selector).jqGrid("getRowData", ids[0]);
							// $.post(base +
							// 'power/CheckPowerStatus',{hostId:rowData.hostId},function(data){
							// if(data==-1){
							// $('#shutDownHost').hide();
							// $('#startHost').hide();
							// }else if(data==0){
							// $('#shutDownHost').hide();
							// $('#startHost').show();
							// }else if(data==1){
							// $('#shutDownHost').show();
							// $('#startHost').hide();
							// }
							// });
							// }else{
							// $('#powerManager').attr("disabled",true);
							// }
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

	function getClusterInfos(hostId) {
		var clusterName = "";
		$.ajax({
			type : 'get',
			url : base + 'cluster/cluster',
			data : {
				hostId : hostId
			},
			async : false,
			dataType : 'json',
			success : function(obj) {
				clusterName = obj.clusterName;
			}
		});
		return clusterName;
	}
	getClusterList();
	/**
	 * Add host
	 */

	$('#submit').click(function() {
		if ($("#create_host_form").valid()) {
			$('#createHostModal').modal('hide');
			var hostName = $('#host_name').val();
			var hostIp = $('#host_ip').val();
			var hostType = $('#host_type').val();
			var hostDesc = $('#host_desc').val();
			var hostUser = $('#userName').val();
			var hostPwd = $('#password').val();
			data = {
				hostName : hostName,
				hostIp : hostIp,
				hostType : hostType,
				hostDesc : hostDesc,
				hostUser : hostUser,
				hostPwd : hostPwd
			};
			url = base + "host/create";
			// 显示遮罩层
			showMask();
			// 提示信息显示
			$('#spinner-message font').html("主机添加中,请稍等....");
			$.post(url, data, function(response) {
				// 隐藏遮罩层
				hideMask();
				if (response != "") {
					showMessage(response.message);
				} else {
					showMessage("连接主机失败：发生异常");
				}
				$(grid_selector).trigger("reloadGrid");
				$('#create_host_form')[0].reset();
			});
		}
	});

	/**
	 * Cancel add host
	 */
	$('#cancel').click(function() {
		$('#createHostModal').modal('hide');
		$('#create_host_form')[0].reset();
		$('label.error').remove();
	});

	$(".close").click(function() {
		$('#create_host_form')[0].reset();
		$('label.error').remove();
	});

	/**
	 * Modify host
	 */
	$('#modify').click(function() {
		if ($("#modify_host_form").valid()) {
			$('#modifyHostModal').modal('hide');
			id = $('#host_id_edit').val();
			name = $('#host_name_edit').val();
			desc = $('#host_desc_edit').val();
			url = base + 'host/update';
			data = {
				hostId : id,
				hostName : name,
				hostDesc : desc
			};
			$.post(url, data, function(response) {
				$(grid_selector).trigger("reloadGrid");
				if (response == "") {
					showMessage("更新主机信息异常！");
				} else {
					showMessage(response.message);
				}
			});
		}
	});

	/**
	 * Cancel add host
	 */
	$('#modify_cancel').click(function() {
		$('#modifyHostModal').modal('hide');
		$(grid_selector).trigger("reloadGrid");
		$('#modify_host_form')[0].reset();
		$('label.error').remove();
	});

	/**
	 * Show join cluster
	 */
	$('#joinCluster').click(function() {
		var ids = $(grid_selector).jqGrid("getGridParam", "selarrrow");
		if (ids.length > 0) {
			for (var i = 0; i < ids.length; i++) {
				var rowData = $(grid_selector).jqGrid("getRowData", ids[i]);
				if (rowData.hostType != "DOCKER") {
					showMessage(rowData.hostName + " 主机类型不正确，请重新选择！");
					return;
				}
				if (rowData.clusterId.length != 0) {
					showMessage(rowData.hostName + " 主机已经加入集群，请重新选择");
					return;
				}
			}
			$('#cluster_select').val($('#cluster_select>option').eq(0).val());
			$('#addHostInClusterModal').modal('show');
		} else {
			showMessage("请先选中主机，再加入集群！");
		}
	});

	/**
	 * Host join cluster
	 */
	$('#addHost')
			.click(
					function() {
						var ids = $(grid_selector).jqGrid("getGridParam",
								"selarrrow");
						var hosts = "";
						for (var i = 0; i < ids.length; i++) {
							var rowData = $(grid_selector).jqGrid("getRowData",
									ids[i]);
							hosts += (i == ids.length - 1 ? rowData.hostId
									: rowData.hostId + ",");
						}
						if ($('#cluster_select').val() == '0:0') {
							showMessage("请选择集群，再添加主机！");
							return;
						} else {
							var clusterInfos = $('#cluster_select').val()
									.split(":");
							hostAddCluster(hosts, clusterInfos[0],
									clusterInfos[1]);
						}
					});

	/**
	 * Cancel host join cluster
	 */
	$('#addHost_cancel').click(function() {
		$('#addHostInClusterModal').modal('hide');
	});

	/**
	 * Remove from cluster
	 */
	$('#removeCluster').click(function() {
		var ids = $(grid_selector).jqGrid("getGridParam", "selarrrow");
		if (ids.length > 0) {
			for (var i = 0; i < ids.length; i++) {
				var rowData = $(grid_selector).jqGrid("getRowData", ids[i]);
				if (rowData.hostType != "DOCKER") {
					showMessage(rowData.hostName + " 主机类型不正确，请重新选择！");
					return;
				}
				if (rowData.clusterId.length == 0) {
					showMessage(rowData.hostName + " 主机不属于集群，请重新选择");
					return;
				}
			}
			showHostRemoveClusterWin();
		} else {
			showMessage("请先选中主机，再移出集群！");
		}
	});

	/**
	 * Delete hosts
	 */
	$('#remove').click(function() {
		showDeleteHosts();
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
							url : base + 'host/advancedSearch?params='
									+ column_array + '&values=' + value_array
						}).trigger("reloadGrid");

				/** @bug152_begin 清空用户多选的参数 */
				while ($("#params li").length > 1) {
					$("#remove-param").parent().remove();
				}
				/** @bug152_finish */

				$('#advancedSearchHostModal').modal('hide');
				$('#advanced_search_frm')[0].reset();
			});

	/**
	 * 向高级搜索的取消按钮添加重置隐藏
	 */
	$("#advanced_cancel").on('click', function(event) {
		event.preventDefault();
		$('#advancedSearchHostModal').modal('hide');
		$('#advanced_search_frm')[0].reset();
	});

	/**
	 * Validate create host form
	 */
	$("#create_host_form").validate({
		rules : {
			host_name : {
				required : true,
				stringNameCheck : true,
				maxlength : 64,
				remote : {
					url : base + "host/checkName",
					type : "post",
					dataType : "json",
					data : {
						hostName : function() {
							return $("#host_name").val();
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
			host_ip : {
				required : true,
				ip : true,
				remote : {
					url : base + "host/checkIp",
					type : "post",
					dataType : "json",
					data : {
						hostIp : function() {
							return $("#host_ip").val();
						},
						hostType : function() {
							return $('#host_type').val();
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
			host_type : {
				required : true,
				remote : {
					url : base + "host/checkIp",
					type : "post",
					dataType : "json",
					data : {
						hostIp : function() {
							return $("#host_ip").val();
						},
						hostType : function() {
							return $('#host_type').val();
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
			userName : {
				required : true,
				hostName : true
			},
			password : {
				required : true,
				maxlength : 64
			},
			host_desc : {
				maxlength : 200,
				stringCheck : true
			}

		},
		messages : {
			host_name : {
				required : "主机名不能为空",
				stringNameCheck : "只能包含中文、英文、数字、减号、下划线等字符",
				maxlength : $.validator.format("主机名不能大于64个字符"),
				remote : "主机名称已经存在，请重新填写"
			},

			host_ip : {
				required : "主机地址不能为空",
				remote : "同类型同ip的主机已经存在，请修改ip或者变更类型"
			},
			host_type : {
				remote : "同类型同ip的主机已经存在，请修改ip或者变更类型"
			},
			userName : {
				required : "用户名不能为空",
				maxlength : $.validator.format("主机登录名不能大于64个字符")
			},
			password : {
				required : "密码不能为空",
				maxlength : $.validator.format("密码不能大于64个字符")
			},
			host_desc : {
				maxlength : $.validator.format("描述信息不能大于200个字符")
			}
		}
	});

	/**
	 * Validate modify host form
	 */
	$("#modify_host_form").validate({
		rules : {
			host_name_edit : {
				required : true,
				stringNameCheck : true,
				maxlength : 64,
				remote : {
					url : base + "host/checkName",
					type : "post",
					dataType : "json",
					data : {
						hostName : function() {
							return $("#host_name_edit").val();
						}
					},
					dataFilter : function(data) { // 判断控制器返回的内容
						if (data == "true") {
							return true;
						} else {
							var name = $("#host_name_edit").val();
							var oldname = $('#host_oldname_edit').val();
							if (oldname == name) {
								return true;
							} else {
								return false;
							}
						}
					}
				}
			},
			host_desc_edit : {
				stringCheck : true,
				maxlength : 200
			}
		},
		messages : {
			host_name_edit : {
				required : "主机名不能为空",
				stringNameCheck : "只能包含中文、英文、数字、减号、下划线等字符",
				maxlength : $.validator.format("主机名不能大于64个字符"),
				remote : "主机名称已经存在，请重新填写"
			},
			host_desc_edit : {
				maxlength : $.validator.format("描述信息不能大于200个字符")
			}
		}
	});

	// 电源管理--保存
	$('#powerManagerModal').on('click', '#savePower', function() {
		Power.initData();
		Power.savePower();
	});
	// 电源管理--校验
	$('#powerManagerModal').on('click', '#validAction', function() {
		Power.checkStatus();
	});
	// 电源管理--开启
	$('#open_host').click(function() {
		Power.closeHost();
	});
	// 电源管理--关闭
	$('#open_host').click(function() {
		Power.openHost();
	});
});

/**
 * @author yangqinglin
 * @datetime 2015年10月29日 11:23
 * @description 添加高级查询函数
 */
function AdvancedSearchHost() {
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
	$('#advancedSearchHostModal').modal('show');

}
/**
 * Get update host info
 */
function updateHostWin(id, uuid, name, desc) {
	$("#host_id_edit").val(id);
	$('#host_oldname_edit').val(name);
	$("#host_name_edit").val(name);
	$("#host_desc_edit").text((desc == null || desc == "null") ? "" : desc);
	$('#modifyHostModal').modal('show');
}

//
function removeHost(id, type, clusterId, obj) {
	url = base + "host/delete";
	$("#host_id").attr("value", id);
	$("#host_type").attr("value", type);
	$("#host_clusterId").attr("value", clusterId);
	if (clusterId != "null") {
		showMessage("主机属于集群，请先解绑再删除！");
	} else {
		data = {
			hostId : id,
			hostType : type
		};

		bootbox
				.dialog({
					message : '<div class="alert alert-info" style="margin:10px"><span class="glyphicon glyphicon-info-sign"></span>&nbsp;确定要删除此主机&nbsp;'
							+ '?</div>',
					title : "删除主机",
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
								$(obj).attr("disabled", true);
								$.post(url, data, function(response) {
									if (response != "") {
										showMessage(response.message);
									} else {
										showMessage("删除主机异常！");
									}
									$(grid_selector).trigger("reloadGrid");
								});
							}
						}
					}
				});
	}
}

//
function showHostRemoveClusterWin() {

	// 获取选择行数据，判断是否符合条件，符合点击保存进行下一步，不符合提示。
	var ids = $(grid_selector).jqGrid("getGridParam", "selarrrow");
	var hosts = "";
	for (var i = 0; i < ids.length; i++) {
		var rowData = $(grid_selector).jqGrid("getRowData", ids[i]);
		hosts += (i == ids.length - 1 ? rowData.hostId : rowData.hostId + ",");
	}
	clusterId = rowData.clusterId;
	bootbox
			.dialog({
				message : '<div class="alert alert-info" style="margin:10px"><span class="glyphicon glyphicon-info-sign"></span>&nbsp;你确定要解绑集群?&nbsp;</div>',
				title : "解绑集群",
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
							hostRemoveCluster(hosts, clusterId);
						}
					}
				}
			});
}

//
function getClusterList() {

	$.get(base + 'cluster/all', null, function(response) {
		if (response != "") {
			$.each(response, function(index, obj) {
				var clusterid = obj.clusterId;
				var clusterhostid = obj.masteHostId;
				var clusterName = obj.clusterName;
				$('#cluster_select').append(
						'<option value="' + clusterhostid + ':' + clusterid
								+ '">' + clusterName + '</option>');
			});
		}
	}, 'json');
}

//
function hostAddCluster(hosts, clusterhost, cluster) {

	if (clusterhost == 0) {
		showMessage("请选择集群！");
		$(grid_selector).trigger("reloadGrid");
	} else {
		$('#addHostInClusterModal').modal('hide');
		var url = base + 'host/addToCluster';
		data = {
			hosts : hosts,
			cluster : cluster,
			clusterhost : clusterhost
		};
		// 显示遮罩层
		showMask();
		// 提示信息显示
		$('#spinner-message font').html("主机加入集群进行中,请稍等....");
		$.post(url, data, function(response) {
			// 隐藏遮罩层
			hideMask();
			if (response == "") {
				showMessage("主机加入集群异常！");
			} else {
				showMessage(response.message);
			}
			$(grid_selector).trigger("reloadGrid");
		});
	}
}

//

function hostRemoveCluster(hosts, clusterId) {
	var url = base + 'host/removeFromCluster';
	data = {
		hosts : hosts,
		clusterId : clusterId
	};
	// 显示遮罩层
	showMask();
	// 提示信息显示
	$('#spinner-message font').html("主机移出集群进行中,请稍等....");
	$.post(url, data, function(response) {
		// 隐藏遮罩层
		hideMask();
		if (response == "") {
			showMessage("主机移出集群异常！");
		} else {
			showMessage(response.message);
		}
		$(grid_selector).trigger("reloadGrid");
	});
}

//
function showDeleteHosts() {

	var ids = $(grid_selector).jqGrid("getGridParam", "selarrrow");
	if (ids.length == 0) {
		showMessage('请选择要删除的主机！');
		return;
	}
	var infoList = "";
	for (var i = 0; i < ids.length; i++) {
		var rowData = $(grid_selector).jqGrid("getRowData", ids[i]);
		if (rowData.clusterId.length > 0) {
			showMessage("主机" + rowData.hostName + "存在集群中，不能删除！");
			return;
		}
		infoList += rowData.hostName + (i == ids.length - 1 ? "" : ";<br>");
	}
	bootbox
			.dialog({
				message : '<div class="alert alert-info" style="margin:10px"><span class="glyphicon glyphicon-info-sign"></span>&nbsp;删除主机&nbsp;'
						+ infoList + '?</div>',
				title : "删除主机",
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
							var hostids = new Array();
							for (var i = 0; i < ids.length; i++) {
								var rowData = $(grid_selector).jqGrid(
										"getRowData", ids[i]);
								hostids = (hostids + rowData.hostId)
										+ (((i + 1) == ids.length) ? '' : ',');
							}
							deleteHosts(hostids);
						}
					}
				}
			});
}

// 删除主机
function deleteHosts(hostidArray) {
	var data = {
		hostIds : hostidArray
	};
	// 显示遮罩层
	showMask();
	// 提示信息显示
	$('#spinner-message font').html("主机删除中,请稍等....");
	$.post(base + 'host/deletes', data, function(response) {
		// 隐藏遮罩层
		hideMask();
		if (response == "") {
			showMessage("删除主机异常！");
		} else {
			showMessage(response.message);
		}
		$(grid_selector).trigger("reloadGrid");
	});

}

function searchHosts() {
	var hostName = $('#search_host').val();
	jQuery(grid_selector).jqGrid('setGridParam', {
		url : base + 'host/listSearch?hostName=' + hostName
	}).trigger("reloadGrid");
}

// 显示电源管理modal
function showPowerDalig() {
	Power.loadInfo();
	Power.initData();
	$('#powerManagerModal').modal("show");
}

// 电源管理对象
var Power = {};

// 预加载数据
Power.loadInfo = function() {
	var ids = $(grid_selector).jqGrid("getGridParam", "selarrrow");
	var rowData = $(grid_selector).jqGrid("getRowData", ids[0]);

	var hostid = rowData.hostId;
	var hostIp = rowData.hostIp;
	$('#pow_host_id').val(hostid);
	$('#pow_host_ip').val(hostIp);
	Loading.show();
	$.get(base + 'power/loadPower', {
		hostId : hostid
	}, function(data) {
		Loading.hide();
		if (data == "") {
			showMessage("获取电源信息异常!");
		} else {
			data = eval("(" + data + ")");
			$('#pow_motherboard_ip').val(data.motherboardIp);
			$('#pow_port').val(data.port);
			$('#pow_username').val(data.userName);
			$('#pow_pwd').val(data.password);
			$('#server_status_val').val(data.status);
			if (data.status == -1) {
				$('#server_status_text').val("校验未通过");
			} else if (data.status == 0) {
				$('#server_status_text').val("已验证/已关机");
			} else if (data.status == 1) {
				$('#server_status_text').val("已验证/已开机");
			}
		}
	});
}
// 获取数据
Power.initData = function() {
	Power.hostId = $('#pow_host_id').val();
	Power.hostIp = $('#pow_host_ip').val();
	Power.motherboardIp = $('#pow_motherboard_ip').val();
	Power.port = $('#pow_port').val();
	Power.userName = $('#pow_username').val();
	Power.password = $('#pow_pwd').val();
	Power.serverStatus = $('#server_status_val').val();
}
// 保存电源数据
Power.savePower = function() {
	var data = {
		hostid : Power.hostId,
		hostip : Power.motherboardIp,
		hostport : Power.port,
		hostusername : Power.userName,
		hostpsw : Power.password,
		powerstatus : Power.serverStatus
	};
	$.post(base + 'power/savePowerStatus', data, function(response) {
		if (response == "") {
			showMessage("保存电源信息异常!");
		} else {
			showMessage(response.message);
		}
	});
}
// 电源状态校验
Power.checkStatus = function() {
	var data = {
		hostId : $('#pow_host_id').val()
	};
	$.post(base + 'power/CheckPowerStatus', data, function(data) {
		if (data == "") {
			showMessage("校验电源信息异常!");
		} else {
			$('#server_status_val').val(data);
			if (data == -1) {
				$('#server_status_text').val("校验未通过");
			} else if (datas == 0) {
				$('#server_status_text').val("已验证/已关机");
			} else if (data == 1) {
				$('#server_status_text').val("已验证/已开机");
			}
		}
	});
};

// 开启服务器
Power.openHost = function() {
	var ids = $(grid_selector).jqGrid("getGridParam", "selarrrow");
	var hostIds = new Array();
	for (var i = 0, length = ids.length; i < length; i++) {
		var rowData = $(grid_selector).jqGrid("getRowData", ids[i]);
		hostIds[i] = rowData.hostId;
	}

	// var hostid=rowData.hostId;
	var data = {
		hostIds : hostIds
	};
	Loading.show();
	$.post(base + 'power/StartPowerByhostId', data, function(data) {
		Loading.hide();
		if (data == "") {
			showMessage("服务器电源开启异常!");
		} else {
			showMessage(data.message);
		}
	});
};

// 关闭服务器
Power.closeHost = function() {
	var ids = $(grid_selector).jqGrid("getGridParam", "selarrrow");
	// var rowData = $(grid_selector).jqGrid("getRowData", ids[0]);
	var hostIds = new Array();
	for (var i = 0, length = ids.length; i < length; i++) {
		var rowData = $(grid_selector).jqGrid("getRowData", ids[i]);
		hostIds[i] = rowData.hostId;
	}
	// var hostid=rowData.hostId;
	var data = {
		hostIds : hostids
	};
	Loading.show();
	$.post(base + 'power/allShutDownPower', data, function(data) {
		Loading.hide();
		if (data == "") {
			showMessage("服务器电源关闭异常!");
		} else {
			showMessage(data.message);
		}
	});
};
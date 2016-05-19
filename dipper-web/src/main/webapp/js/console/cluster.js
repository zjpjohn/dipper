var grid_selector = "#cluster_list";
var page_selector = "#cluster_page";
/** 保存当前高级查询条件的列数量 */
var advanceColNum = 0;

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
						url : base + 'cluster/list',
						datatype : "json",
						height : '100%',
						autowidth : true,
						colNames : [ 'ID', 'UUID', '名称', '主服务器ID', '管理节点',
								'端口号', '集群状态', '用户ID', 'cpu(核数)', '内存(MB)',
								'节点数', '创建时间', '资源配置', '快捷操作' ,''],
						colModel : [
								{
									name : 'clusterId',
									index : 'clusterId',
									width : 15,
									hidden : true
								},
								{
									name : 'clusterUuid',
									index : 'clusterUuid',
									width : 15,
									hidden : true
								},
								{
									name : 'clusterName',
									index : 'clusterName',
									width : 15,
									formatter : function(cell, opt, obj) {
										return '<i class="fa fa-sitemap"></i><a href="'
												+ base
												+ 'cluster/detail/'
												+ obj.clusterId
												+ '.html">'
												+ cell + '</a>';
									}
								},
								{
									name : 'masteHostId',
									index : 'masteHostId',
									width : 20,
									hidden : true
								},
								{
									name : 'hostIP',
									index : 'hostIP',
									width : 8,
									align : 'left'
								},
								{
									name : 'clusterPort',
									index : 'clusterPort',
									width : 10
								},
								{
									name : 'status',
									index : 'status',
									width : 10,
									align : 'left',
									formatter : function(cell, opt, obj) {
										switch (obj.clusterStatus) {
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
									name : 'clusterCreator',
									index : 'clusterCreator',
									width : 15,
									hidden : true
								},
								{
									name : 'cpu',
									index : 'cpu',
									width : 7,
								},
								{
									name : 'mem',
									index : 'mem',
									width : 10,
								},
								{
									name : 'hostNum',
									index : 'hostNum',
									width : 6,
								},
								{
									name : 'clusterCreatetime',
									index : 'clusterCreatetime',
									width : 13
								},
								{
									name : 'resType',
									index : 'resType',
									width : 10,
									formatter : function(cell, opt, obj) {
										switch (obj.resType) {
										case 0:
											return '<i class="fa">&nbsp; 资源共享</i>';
										case 1:
											return '<i class="fa">&nbsp;资源独享</i>';
										}
									}
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
										var dele = $("#delete_cluster").val();
										if (typeof (dele) != "undefined") {
											strHtml += "<button class=\"btn btn-xs btn-primary btn-round\" onclick=\"modifyClusterInfo('"
													+ rowObject.clusterId
													+ "','"
													+ rowObject.clusterUuid
													+ "','"
													+ rowObject.clusterName
													+ "','"
													+ rowObject.clusterDesc
													+ "')\">"
													+ "<i class=\"ace-icon fa fa-pencil-square-o bigger-125\"></i>"
													+ "<b>编辑</b></button> &nbsp;";
										}
										var upda = $("#update_cluster").val();
										if (typeof (upda) != "undefined") {
											strHtml += "<button class=\"btn btn-xs btn-inverse btn-round\" onclick=\"removeCluster('"
													+ rowObject.clusterId
													+ "','"
													+ rowObject.clusterName
													+ "')\">"
													+ "<i class=\"ace-icon fa fa-trash-o bigger-125\"></i>"
													+ "<b>删除</b></button> &nbsp;";
										}
										return strHtml;
									}
								},
								{
									name : 'clusterStatus',
									index : 'clusterStatus',
									width : 10,
									hidden:true
								}],
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

	getClusterMaster();

	// chose cluster mode
	$("input[name=cluster_mode]").click(function() {
		var clustermode = $("input[name=cluster_mode]:checked").val();
		if (clustermode == 0) {
			$('#cluster_file').hide();
		} else {
			$('#cluster_file').show();
		}
	});

	/**
	 * Add cluster
	 */
	$('#submit').click(function() {
		// 添加校验
		if ($("#create_cluster_form").valid()) {
			var clustername = $('#cluster_name').val();
			var clusterport = $('#cluster_port').val();
			var masterip = $('#hostMaster_select').val();
			var clusterMode = $("input[name=cluster_mode]:checked").val();
			var resType = $("input[name=cluster_resType]:checked").val();
			var clumanagepath = $('#cluster_manage').val();
			var clusterdesc = $('#cluster_desc').val();
			if ($('#hostMaster_select').val() == 0) {
				showMessage("请选择创建集群所在的主机！");
				return;
			} else {
				$('#createClusterModal').modal('hide');
				data = {
					clusterName : clustername,
					clusterPort : clusterport,
					clusterMode : clusterMode,
					masteHostId : masterip,
					managePath : clumanagepath,
					clusterDesc : clusterdesc,
					resType : resType
				};
				url = base + "cluster/create";
				// 显示遮罩层
				showMask();
				// 提示信息显示
				$('#spinner-message font').html("swarm集群创建中,请稍等....");
				$.post(url, data, function(response) {
					// 隐藏遮罩层
					hideMask();
					if (response == "") {
						showMessage("创建集群：服务器异常！");
					} else {
						showMessage(response.message);
					}
					$(grid_selector).trigger("reloadGrid");
					$("#create_cluster_form")[0].reset();
				});
			}
		}
	});

	/**
	 * Cancel add cluster
	 */
	$('#cancel').click(function() {
		$('#createClusterModal').modal('hide');
		$("#create_cluster_form")[0].reset();
		$('label.error').remove();
	});

	$(".close").click(function() {
		$("#create_cluster_form")[0].reset();
		$('label.error').remove();
	});

	/**
	 * Modify cluster info
	 */
	$('#modify').click(function() {
		if ($("#modify_cluster_form").valid()) {
			$('#modifyClusterModal').modal('hide');
			var url = base + 'cluster/update';
			var id = $('#cluster_id_edit').val();
			var name = $('#cluster_name_edit').val();
			var desc = $('#cluster_desc_edit').val();
			data = {
				clusterId : id,
				clusterName : name,
				clusterDesc : desc
			};
			$.post(url, data, function(response) {
				if (response == "") {
					showMessage("修改集群:服务器异常！");
				} else {
					showMessage(response.message);
				}
				$(grid_selector).trigger("reloadGrid");
				$("#modify_cluster_form")[0].reset();
			});
		}
	});

	/**
	 * Cancel modify cluster
	 */
	$('#modify_cancel').click(function() {
		$('#modifyClusterModal').modal('hide');
		$("#modify_cluster_form")[0].reset();
		$('label.error').remove();
	});

	/**
	 * Add host to cluster modal show
	 */
	$('#addHostToCluster').click(
			function() {
				var ids = $(grid_selector).jqGrid("getGridParam", "selarrrow");
				if (ids.length == 1) {
					var hosts = "";
					var clusterId = "";
					var clusterId = $(grid_selector).jqGrid("getRowData",
							ids[0]).clusterId;
					var clusterName = $(grid_selector).jqGrid("getRowData",
							ids[0]).clusterName;
					$('#clusterInfo').val(clusterId);
					/* 清空原始集群名称，写入新的的集群名称 */
					$('#clusterShowName').empty();
					$('#clusterShowName').append(clusterName);
					/* 加载所有没有划入集群的主机列表 */
					if (getHostNotInCluster(clusterId) <= 0) {
						showMessage('不存在可以加入集群的主机，请先添加主机');
						return;
					}
					;
					$('#addHostInClusterModal').modal('show');
					/* 添加对于增加和移除按钮的处理 */
					addOrRemoveHost('add-hosts', 'remove-hosts', 'activeHosts',
							'blockHosts')
				} else if (ids.length > 1) {
					showMessage('主机每次只能添加到一个集群，请重新选择！');
				} else {
					showMessage('请选择集群，再加入主机！');
				}
			});
	/**
	 * Add host to cluster
	 */
	$('#addHost').click(
			function() {
				// var hostId = $('#host_select').val();
				var activeHosts = $('#activeHosts input[name="btn-host"]');
				var hostLength = activeHosts.length;
				if (hostLength == 0) {
					showMessage("请选择主机，再加入集群！");
					return;
				}
				var hostIds = '';
				for (var hostCount = 0; hostCount < hostLength; hostCount++) {
					hostIds += $(activeHosts[hostCount]).val()
							+ (hostCount + 1 == activeHosts.length ? '' : ',');
				}

				var clusterId = $('#clusterInfo').val();
				clusterAddHost(clusterId, hostIds);
			});
	/**
	 * Cancel Add host to cluster
	 */
	$('#addHost_cancel').click(function() {
		$('#addHostInClusterModal').modal('hide');
	});

	/**
	 * Remove host from cluster
	 */
	$('#removeHostFromCluster')
			.click(
					function() {
						var ids = $(grid_selector).jqGrid("getGridParam",
								"selarrrow");
						if (ids.length == 1) {
							$('#remove_activeHosts,#remove_blockHosts')
									.html("");
							$('#removeHostFromClusterModal').modal('show');
							var rowData = $(grid_selector).jqGrid("getRowData",
									ids[0]);
							var clusterId = rowData.clusterId;
							var clusterName = rowData.clusterName;
							$('#clusterInfo').val(clusterId);
							$('#show_cluster_name').html(clusterName);

							if (getHostInCluster(clusterId) <= 0) {
								showMessage('集群中不存在主机！');
								return;
							}
							;
							$('#removeHostFromClusterModal').modal('show');
							/* 添加对于增加和移除按钮的处理 */
							addOrRemoveHost('remove_addHosts',
									'remove_removeHosts', 'remove_activeHosts',
									'remove_blockHosts')
						} else if (ids.length > 1) {
							showMessage('主机每次只能从一个集群中删除，请重新选择！');
						} else {
							showMessage('请选择集群，再移除主机！');
						}
					});

	/**
	 * Remove host from cluster
	 */
	$('#removeHost')
			.click(
					function() {
						// var hostId = $('#host_select').val();
						var activeHosts = $('#remove_activeHosts input[name="btn-host"]');
						var hostLength = activeHosts.length;
						if (hostLength == 0) {
							showMessage("请选择主机，再移出集群！");
							return;
						}
						var hostIds = "";
						for (var hostCount = 0; hostCount < hostLength; hostCount++) {
							hostIds += $(activeHosts[hostCount]).val()
									+ (hostCount + 1 == activeHosts.length ? ''
											: ',');
						}

						var clusterId = $('#clusterInfo').val();
						clusterRemoveHost(clusterId, hostIds);
					});
	/**
	 * Cancel remove host from cluster
	 */
	$('#removeHost_cancel').click(function() {
		$('#host_in_cluster').html("<option value='0'>请选择主机</option>");
		$('#removeHostFromClusterModal').modal('hide');
	});

	/**
	 * Healthy check
	 */
	$('#healthyCheck').click(
			function() {
				var ids = $(grid_selector).jqGrid("getGridParam", "selarrrow");
				if (ids.length == 1) {
					var hosts = "";
					var clusterId = "";
					for (var i = 0; i < ids.length; i++) {
						var rowData = $(grid_selector).jqGrid("getRowData",
								ids[i]);
						hosts += (i == ids.length - 1 ? rowData.clusterId
								: rowData.clusterId + ",");
						clusterId = rowData.clusterId;
					}
					var url = base + 'cluster/healthCheck';
					data = {
						clusterId : clusterId
					};
					// 显示遮罩层
					showMask();
					// 提示信息显示
					$('#spinner-message font').html("swarm集群健康检查中,请稍等....");
					$.post(url, data, function(response) {
						// 隐藏遮罩层
						hideMask();
						if (response != "" && response != undefined) {
							showMessage(response.message, function() {
								$(grid_selector).trigger("reloadGrid");
							})
						} else {
							showMessage("健康检查未完成，服务器异常！");
						}
					});
				} else if (ids.length > 1) {
					showMessage('暂时一次只能为一个集群做健康检查，请重新选择！');
				} else {
					showMessage('请选择集群，再进行健康检查！');
				}
			});

	/** 定义主机选择栏发生变更的情况下，自动请求可用的端口 */
	$('#hostMaster_select').change(function() {
		var masterHostId = $('#hostMaster_select').val();
		if (masterHostId == 0) {
			return;
		} else {
			/** 向后台请求此节点可用的端口 */
			var getPortUrl = base + "host/getFreePort"
			var sendData = {
				hostId : masterHostId
			};
			$.post(getPortUrl, sendData, function(response) {
				switch (response) {
				case ("dbfail"):
					showMessage('从数据库查询主机信息失败！');
					break;
				case ("sshfail"):
					showMessage('与远程管理主机建立连接失败！');
					break;
				case ("exception"):
					showMessage('主机操作异常，请检查主机连接！');
					break;
				default:
					$('#cluster_port').val(response);
					break;
				}
			});
		}
	});

	/**
	 * Recover cluster
	 */
	$("#recover").click(function() {
			var ids = $(grid_selector).jqGrid("getGridParam","selarrrow");
			if (ids.length == 1) {
				var cluster=$(grid_selector).jqGrid("getRowData",ids[0]);
				var status=Number(cluster.clusterStatus);
				if(status==1){
					showMessage('集群处于健康状态，不需要恢复！');
					return false;
				}
				
				var clusterId=cluster.clusterId;
				var url = base + 'cluster/recover';
				data = {
					clusterId : clusterId
				};
				// 显示遮罩层
				showMask();
				// 提示信息显示
				$('#spinner-message font').html(
						"swarm集群恢复中,请稍等....");
				$.post(url, data, function(response) {
					// 隐藏遮罩层
					hideMask();
					if (response == "") {
						showMessage("集群恢复：服务器异常！");
					} else {
						showMessage(response.message);
					}
					$(grid_selector).trigger("reloadGrid");
				});
			} else if (ids.length > 1) {
				showMessage('暂时一次只能为一个主机做恢复，请重新选择！');
			} else {
				showMessage('请选择集群，再进行恢复！');
			}
		});

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
							url : base + 'cluster/advancedSearch?params='
									+ column_array + '&values=' + value_array
						}).trigger("reloadGrid");

				$('#advancedSearchClusterModal').modal('hide');
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
		$('#advancedSearchClusterModal').modal('hide');
		$('#advanced_search_frm')[0].reset();
	});

	/**
	 * Validate create host form
	 */
	$("#create_cluster_form").validate({
		rules : {
			cluster_name : {
				required : true,
				stringNameCheck : true,
				maxlength : 64,
				remote : {
					url : base + "cluster/checkName",
					type : "post",
					dataType : "json",
					data : {
						clusterName : function() {
							return $("#cluster_name").val();
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
			hostMaster_select : {
				required : true,
				isIntGtZero : true
			},
			cluster_port : {
				required : true,
			// isValidServerPort : true,
			/*
			 * remote : { url : base + "cluster/checkPort", type : "post",
			 * dataType : "json", data : { hostId : function() { return
			 * $("#hostMaster_select").val(); }, port : function() { return
			 * $("#cluster_port").val(); } }, dataFilter : function(data) {//
			 * 判断控制器返回的内容 if (data == "true") { return true; } else { return
			 * false; } } }
			 */
			},
			cluster_manage : {
				required : true,
				isFileName : true,
				maxlength : 200,
				remote : {
					url : base + "cluster/checkClusterConfFile",
					type : "post",
					dataType : "json",
					data : {
						managePath : function() {
							return $("#cluster_manage").val();
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
			cluster_desc : {
				maxlength : 200,
				stringCheck : true
			}

		},
		messages : {
			cluster_name : {
				required : "集群名称不能为空",
				stringNameCheck : "只能包含中文、英文、数字、减号、下划线等字符",
				maxlength : $.validator.format("集群名称不能大于64个字符"),
				remote : "集群名称已经存在，请重新填写"
			},
			hostMaster_select : {
				required : "请选择主机",
				isIntGtZero : "集群所在主机不能为空"
			},
			cluster_port : {
				required : "集群启动端口不能为空",
			// remote : "端口已经被占用，请重新填写"
			},
			cluster_manage : {
				required : "集群配置文件位置不能为空",
				remote : "管理文件已经存在，请重新填写",
				maxlength : $.validator.format("管理文件不能大于200个字符"),
			},
			cluster_desc : {
				maxlength : $.validator.format("描述信息不能大于200个字符")
			}
		}
	});

	/**
	 * Validate modify host form
	 */
	$("#modify_cluster_form").validate({
		rules : {
			cluster_name_edit : {
				required : true,
				stringNameCheck : true,
				maxlength : 64,
				remote : {
					url : base + "cluster/checkName",
					type : "post",
					dataType : "json",
					data : {
						clusterName : function() {
							return $("#cluster_name_edit").val();
						}
					},
					dataFilter : function(data) {// 判断控制器返回的内容
						if (data == "true") {
							return true;
						} else {
							var name = $("#cluster_name_edit").val();
							var oldname = $('#cluster_oldname_edit').val();
							if (oldname == name) {
								return true;
							} else {
								return false;
							}
						}
					}
				}
			},
			cluster_desc_edit : {
				stringCheck : true,
				maxlength : 200
			}
		},
		messages : {
			cluster_name_edit : {
				required : "集群名称不能为空",
				stringNameCheck : "只能包含中文、英文、数字、减号、下划线等字符",
				maxlength : $.validator.format("集群名称不能大于64个字符"),
				remote : "集群名称已经存在，请重新填写"
			},
			cluster_desc_edit : {
				maxlength : $.validator.format("描述信息不能大于200个字符")
			}
		}
	});
});

/** 初始化获取创建集群的所需信息 */
function initCreateCluster() {
	$('#createClusterModal').modal('show');
}

function addOrRemoveHost(addid, removeid, activeHostid, blockHostid) {
	$('#' + addid).unbind('click');
	$('#' + addid).click(function() {
		var blockEles = $('#' + blockHostid + ' input[name="btn-host"]');
		console.log(blockEles);
		var hosts = new Array();
		for ( var i in blockEles) {
			if (blockEles[i].checked) {
				var host = $(blockEles[i]).parent('label');
				hosts.push(host);
			}
		}
		if (hosts.length <= 0) {
			showMessage("请选择要增加的主机。");
			return;
		}
		for ( var i in blockEles) {
			if (blockEles[i].checked) {
				$(blockEles[i]).parent('label').remove();
			}
		}
		for ( var i in hosts) {
			$('#' + activeHostid).append(hosts[i]);
		}
		blockEles = document.getElementsByName("btn-host");
		for (i in blockEles) {
			blockEles[i].checked = false;
		}
	});

	$('#' + removeid).unbind('click');
	$('#' + removeid).click(function() {
		// 获取某个固定div下的
		var activeEles = $('#' + activeHostid + ' input[name="btn-host"]');
		var hosts = new Array();
		for ( var i in activeEles) {
			if (activeEles[i].checked) {
				var host = $(activeEles[i]).parent('label');
				hosts.push(host);
			}
		}
		if (hosts.length <= 0) {
			showMessage("请选择要移除的主机。");
			return;
		}
		for ( var i in activeEles) {
			if (activeEles[i].checked) {
				$(activeEles[i]).parent('label').remove();
			}
		}
		for ( var i in hosts) {
			$('#' + blockHostid).append(hosts[i]);
		}
		activeEles = document.getElementsByName("btn-host");
		for ( var i in activeEles) {
			activeEles[i].checked = false;
		}
	});
}

/**
 * @author yangqinglin
 * @datetime 2015年10月29日 11:23
 * @description 添加高级查询函数
 */
function AdvancedSearchCluster() {

	/** @bug152_begin 清空用户多选的参数 */
	while ($("#params li").length > 1) {
		$("#remove-param").parent().remove();
	}
	/** 隐藏高级查询第一行的删除打叉按钮 */
	$("#params li:first").find("#remove-param").hide();
	/** 打开高级搜索窗口，之前输入全部清空 */
	$("#params li:first").find("#param_value").val("");
	$("#params li:first").find("#meter").val("0");
	/** @bug152_finish */

	$('#advancedSearchClusterModal').modal('show');
}

// remove cluster
function removeCluster(id, name) {
	bootbox
			.dialog({
				message : '<div class="alert alert-info" style="margin:10px"><span class="glyphicon glyphicon-info-sign"></span>&nbsp;删除集群&nbsp;cluster-'
						+ name + '?</div>',
				title : "提示",
				buttons : {
					cancel : {
						label : "<i class='icon-info'></i> <b>取消</b>",
						className : "btn-sm btn-danger btn-round",
						callback : function() {
						}
					},
					main : {
						label : "<i class='icon-info'></i><b>确定</b>",
						className : "btn-sm btn-success btn-round",
						callback : function() {
							url = base + "cluster/delete";
							$("cluster_id").attr("value", id);
							data = {
								clusterId : id
							};
							// 显示遮罩层
							showMask();
							// 提示信息显示
							$('#spinner-message font').html("删除集群中,请稍等....");
							$.post(url, data, function(response) {
								// 隐藏遮罩层
								hideMask();
								if (response == "") {
									showMessage("删除集群：服务器异常！");
								} else {
									showMessage(response.message);
								}
								$(grid_selector).trigger("reloadGrid");
							});
						}
					}
				}
			});
}

// Get cluster of master
function getClusterMaster() {
	$.ajax({
		type : 'get',
		url : base + 'cluster/clusterMasterList',
		dataType : 'json',
		success : function(array) {
			$.each(array, function(index, obj) {
				var hostid = obj.hostid;
				var hostip = decodeURIComponent(obj.hostip);
				$('#hostMaster_select').append(
						'<option value="' + hostid + '">' + hostip
								+ '</option>');
			});
		}
	});
}

// add host to cluster
function clusterAddHost(clusterId, hostIds) {
	$('#addHostInClusterModal').modal('hide');
	var url = base + 'cluster/addManyHost';
	data = {
		clusterId : clusterId,
		hostId : hostIds
	};
	// 显示遮罩层
	showMask();
	// 提示信息显示
	$('#spinner-message font').html("集群添加主机中,请稍等....");
	$.post(url, data, function(response) {
		// 隐藏遮罩层
		hideMask();
		if (response == "") {
			showMessage("集群添加主机异常！");
		} else {
			if (response.success) {
				showMessage("操作成功，" + response.message);
			} else {
				showMessage("操作失败，" + response.message);
			}
		}
		$(grid_selector).trigger("reloadGrid");
	});
}
// remove host from cluster
function clusterRemoveHost(clusterId, hostIds) {
	$('#removeHostFromClusterModal').modal('hide');
	var url = base + 'cluster/removeHost';
	data = {
		clusterId : clusterId,
		hostIds : hostIds
	};
	// 显示遮罩层
	showMask();
	// 提示信息显示
	$('#spinner-message font').html("集群解绑主机中,请稍等....");
	$.post(url, data, function(response) {
		// 隐藏遮罩层
		hideMask();
		if (response == "") {
			showMessage("集群移除主机异常！");
		} else {
			showMessage(response.message);
		}
		$(grid_selector).trigger("reloadGrid");
	});
}

// get host in cluster
function getHostNotInCluster(clusterId) {
	$('#blockHosts').html('');
	$('#activeHosts').html('');
	var flag = 0;
	$
			.ajax({
				type : 'post',
				url : base + 'host/addAll',
				async : false,
				dataType : 'json',
				success : function(array) {
					flag = array.length;
					$
							.each(
									array,
									function(index, obj) {
										var hostid = obj.hostId;
										var hostName = decodeURIComponent(obj.hostName);
										var hostIp = obj.hostIp;
										$('#blockHosts')
												.append(
														'<label class="btn btn-round btn-white btn-primary">'
																+ '<input type="checkbox" name="btn-host" text="'
																+ hostName
																+ '" value="'
																+ hostid
																+ '">&nbsp;&nbsp;'
																+ hostIp
																+ '</label>');
									});
				}
			});
	return flag;
}

function getHostInCluster(clusterId) {
	var flag = 0;
	$
			.ajax({
				type : 'get',
				url : base + 'host/removeAll',
				data : {
					clusterId : clusterId
				},
				async : false,
				dataType : 'json',
				success : function(array) {
					flag = array.length;
					$
							.each(
									array,
									function(index, obj) {
										var hostid = obj.hostId;
										var hostName = decodeURIComponent(obj.hostName);
										var hostIp = obj.hostIp;
										$('#remove_blockHosts')
												.append(
														'<label class="btn btn-round btn-white btn-primary">'
																+ '<input type="checkbox" name="btn-host" value="'
																+ hostid
																+ '">&nbsp;&nbsp;'
																+ hostIp
																+ '</label>');
									});
				}
			});
	return flag;
}

// modify cluster info
function modifyClusterInfo(id, uuid, name, desc) {
	$('#cluster_id_edit').val(id);
	$('#cluster_oldname_edit').val(name);
	$('#cluster_name_edit').val(name);
	$('#cluster_desc_edit').val((desc == null || desc == "null") ? "" : desc);
	$('#modifyClusterModal').modal('show');
}

//
function showHealthCheckClusters() {
	var ids = $(grid_selector).jqGrid("getGridParam", "selarrrow");
	var hosts = "";
	for (var i = 0; i < ids.length; i++) {
		var rowData = $(grid_selector).jqGrid("getRowData", ids[i]);
		hosts += (i == ids.length - 1 ? rowData.clusterId : rowData.clusterId
				+ ",");
	}
	clusterId = rowData.clusterId;
	if (clusterId == 0) {
		showMessage("请选择集群！");
		$(grid_selector).trigger("reloadGrid");
	} else {
		var url = base + 'cluster/healthCheck';
		data = {
			clusterId : clusterId
		};
		$.get(url, data, function(response) {
			if (response == "") {
				showMessage("服务器信息异常！");
			} else {
				showMessage(response.message);
			}
			$(grid_selector).trigger("reloadGrid");
		});
	}
}

function searchClusters() {
	var clusterName = $.trim($('#search_cluster').val());
	jQuery(grid_selector).jqGrid('setGridParam', {
		url : base + 'cluster/listSearch?clusterName=' + clusterName
	}).trigger("reloadGrid");
}
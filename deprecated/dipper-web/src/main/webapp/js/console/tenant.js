var grid_selector = "#tenant_list";
var page_selector = "#tenant_page";
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
						url : base + 'tenant/list',
						datatype : "json",
						height : '100%',
						autowidth : true,
						colNames : [ '租户ID', '租户名称', '开始时间', '到期时间', 'CPU核数',
								'内存容量', '描述信息', '生成时间', '用户ID', '快捷操作' ],
						colModel : [
								{
									name : 'tenantId',
									index : 'tenantId',
									width : 1,
									hidden : true
								},
								{
									name : 'tenantName',
									index : 'tenantName',
									width : 8,
									align : 'left',
									formatter : function(cell, opt, obj) {
										return '<i class="fa fa-university"></i><a href="'
												+ base
												+ 'tenant/detail/'
												+ obj.tenantId
												+ '.html">'
												+ cell + '</a>';
									}
								},
								{
									name : 'inserviceDate',
									index : 'inserviceDate',
									width : 7,
									align : 'left',
									formatter : function(cell, opt, obj) {
										return cell.substring(0, 10);
									}
								},
								{
									name : 'expireDate',
									index : 'expireDate',
									width : 7,
									align : 'left',
									formatter : function(cell, opt, obj) {
										return cell.substring(0, 10);
									}
								},
								{
									name : 'totalCpu',
									index : 'totalCpu',
									width : 5,
									align : 'left'
								},
								{
									name : 'totalMem',
									index : 'totalMem',
									width : 5,
									align : 'left',
									formatter : function(cell, opt, obj) {
										return cell
												+ '<font color="blue"><b>&nbsp;MB</b></font>';
									}
								},
								{
									name : 'tenantDesc',
									index : 'tenantDesc',
									width : 18
								},
								{
									name : 'createTime',
									index : 'createTime',
									width : 9
								},
								{
									name : 'creator',
									index : 'creator',
									width : 1,
									hidden : true
								},
								{
									name : '',
									title : false,
									index : '',
									width : 160,
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
											strHtml += "<button class=\"btn btn-primary btn-xs btn-round\" onclick=\"editTenant('"
													+ rowObject.tenantId
													+ "')\"><i class=\"ace-icon fa fa-pencil align-top\"></i>&nbsp;<b>编辑</b></button> &nbsp;";
										}
										var dele = $("#delete_registry").val();
										if (typeof (dele) != "undefined") {
											strHtml += "<button class=\"btn btn-inverse  btn-xs btn-round\" onclick=\"deleteTenant('"
													+ rowObject.tenantId
													+ "','"
													+ rowObject.tenantName
													+ "')\"><i class=\"ace-icon fa fa-trash-o\"></i>&nbsp;<b>删除</b></button> &nbsp;";
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

	// 选择与租户资源绑定的用户列表
	$('#add-user').click(function() {
		var blockUsers = $('#blockUser input[name="btn-user"]');
		var users = new Array();
		for (i in blockUsers) {
			if (blockUsers[i].checked) {
				var user = $(blockUsers[i]).parent('label');
				users.push(user);
			}
		}
		if (users.length <= 0) {
			showMessage("请选择要绑定租户资源的用户。");
			return;
		}
		for (i in blockUsers) {
			if (blockUsers[i].checked) {
				$(blockUsers[i]).parent('label').remove();
			}
		}
		for (i in users) {
			$('#activeUser').append(users[i]);
		}
		blockUsers = document.getElementsByName("btn-user");
		for (i in blockUsers) {
			blockUsers[i].checked = false;
		}
	});

	// 从租户资源中解绑用户列表
	$('#remove-user').click(function() {
		// 获取某个固定div下的
		var activeUsers = $('#activeUser input[name="btn-user"]');
		var users = new Array();
		for (i in activeUsers) {
			if (activeUsers[i].checked) {
				var user = $(activeUsers[i]).parent('label');
				users.push(user);
			}
		}
		if (users.length <= 0) {
			showMessage("请选择从租户资源中解绑的用户。");
			return;
		}
		for (i in activeUsers) {
			if (activeUsers[i].checked) {
				$(activeUsers[i]).parent('label').remove();
			}
		}
		for (i in users) {
			$('#blockUser').append(users[i]);
		}
		activeUsers = document.getElementsByName("btn-user");
		for (i in activeUsers) {
			activeUsers[i].checked = false;
		}
	});

	// 向租户中添加集群资源
	$('#add-cluster').click(function() {
		var blockClusters = $('#blockCluster input[name="btn-cluster"]');
		var clusters = new Array();
		for (i in blockClusters) {
			if (blockClusters[i].checked) {
				var cluster = $(blockClusters[i]).parent('label');
				clusters.push(cluster);
			}
		}
		if (clusters.length <= 0) {
			showMessage("请选择要增加的集群.");
			return;
		}
		for (i in blockClusters) {
			if (blockClusters[i].checked) {
				$(blockClusters[i]).parent('label').remove();
			}
		}
		for (i in clusters) {
			$('#activeCluster').append(clusters[i]);
		}
		blockClusters = document.getElementsByName("btn-cluster");
		for (i in blockClusters) {
			blockClusters[i].checked = false;
		}
	});

	// 从租户中删除集群资源
	$('#remove-cluster').click(function() {
		// 获取某个固定div下的
		var activeClusters = $('#activeCluster input[name="btn-cluster"]');
		var clusters = new Array();
		for (i in activeClusters) {
			if (activeClusters[i].checked) {
				var cluster = $(activeClusters[i]).parent('label');
				clusters.push(cluster);
			}
		}
		if (clusters.length <= 0) {
			showMessage("请选择要移除的集群.");
			return;
		}
		for (i in activeClusters) {
			if (activeClusters[i].checked) {
				$(activeClusters[i]).parent('label').remove();
			}
		}
		for (i in clusters) {
			$('#blockCluster').append(clusters[i]);
		}
		activeClusters = document.getElementsByName("btn-cluster");
		for (i in activeClusters) {
			activeClusters[i].checked = false;
		}
	});

	// 构建应用的信息
	$('#tenant-wizard .modal-header')
			.ace_wizard()
			.on(
					'change',
					function(e, info) {

						if (info.step == 1) { // 第一步需要执行的操作
							if (info.direction == 'next') {
								// 测试，暂不做校验
								// if (!valid_tenantinfo()) {
								// return false;
								// }
								/** 加载镜像和集群列表到待选 */
								loadClusters();
							}
						} else if (info.step == 2) { // 第二步需要执行的操作
							if (info.direction == 'next') {
								// if (!$("#app_basic_form").valid()) {
								// return false;
								// }
								/* 加载参数和资源内容待选项 */
							} else if (info.direction = 'previous') {
							}
						} else if (info.step == 3) { // 第四步需要执行的操作
							if (info.direction == 'next') {
								/* 保存最后展示给用户的汇总信息 */
								var tenantInfos = "";

								// 租户名称
								var name = $('#tenant_name').val();
								if (name.trim() !== '') {
									tenantInfos += '<div class="item"><label><b>租户名称：</b></label> <label>'
											+ name + '</label></div>';
								}

								// 租用起止时间
								var startDate = $('#tenant_start').val();
								var endDate = $('#tenant_end').val();
								if (startDate.trim() !== ''
										&& endDate.trim() !== '') {
									tenantInfos += '<div class="item"><label><b>租期：</b></label> <label>'
											+ startDate
											+ '&nbsp;至&nbsp;'
											+ endDate + '</label></div>';
								}

								// 描述信息
								var desc = $('#tenant_desc').val();
								if (desc.trim() !== '') {
									tenantInfos += '<div class="item"><label><b>租户描述：</b></label> <label>'
											+ desc + '</label></div>';
								}

								// 获取所有已经选择的集群信息
								var activeClusters = $('#activeCluster input[name="btn-cluster"]');
								var clusterString = '';
								for (var cluCount = 0, cluLen = activeClusters.length; cluCount < cluLen; cluCount++) {
									clusterString += $(activeClusters[cluCount])
											.attr('text')
											+ (cluCount + 1 == activeClusters.length ? ''
													: ',&nbsp;');
								}
								if (clusterString.trim() !== '') {
									tenantInfos += '<div class="item"><label><b>包含集群：</b></label> <label>'
											+ clusterString + '</label></div>';
								}

								// 获取创建的管理员的相关信息
								var manager = $('#managerName').val();
								var email = $('#eMail').val();
								var telephone = $('#phoneNumber').val();
								var company = $('#companyName').val();

								tenantInfos += '<div class="item"><label><b>管理员：</b></label> <label>'
										+ manager + '</label></div>';
								tenantInfos += '<div class="item"><label><b>电邮地址：</b></label> <label>'
										+ email + '</label></div>';
								tenantInfos += '<div class="item"><label><b>手机号码：</b></label> <label>'
										+ telephone + '</label></div>';
								tenantInfos += '<div class="item"><label><b>公司名称：</b></label> <label>'
										+ company + '</label></div>';

								$('#tenant_info').html(tenantInfos);
							} else if (info.direction = 'previous') {
							}
						}
					})
			.on(
					'finished',
					function(e) { // 最后一步需要执行的操作
						// 提交操作
						e.preventDefault();

						/** （1）租户基础信息 */
						// 租户名称
						var name = $('#tenant_name').val();
						// 租用起止时间
						var startDate = $('#tenant_start').val();
						var endDate = $('#tenant_end').val();
						// 描述信息
						var desc = $('#tenant_desc').val();

						/** (2)包含的集群列表 */
						var activeClusters = $('#activeCluster input[name="btn-cluster"]');
						var cluIds = '';
						for (var cluCount = 0, cluLen = activeClusters.length; cluCount < cluLen; cluCount++) {
							cluIds += $(activeClusters[cluCount]).val()
									+ (cluCount + 1 == activeClusters.length ? ''
											: ',');
						}

						/** (3)新建管理员的信息 */
						var manager = $('#managerName').val();
						var email = $('#eMail').val();
						var telephone = $('#phoneNumber').val();
						var company = $('#companyName').val();

						/** (4)组装创建租户所需的数据 */
						var tenantData = {
							tenantName : name,
							beginDate : startDate,
							endDate : endDate,
							tenantDesc : desc,
							cluArray : cluIds,
							managerName : manager,
							eMail : email,
							phoneNumber : telephone,
							companyName : company
						};

						var createTenantUrl;
						var tenantId = $('#tenant_id').val();

						if (tenantId == 0) {
							createTenantUrl = base + 'tenant/add';
						} else {
							createTenantUrl = base + 'tenant/modify';
							data.tenantId = tenantId;
						}

						$.post(createTenantUrl, tenantData, function(response) {
							$('#tenant-wizard').modal('hide');
							if (response == "") {
								if (createTenantUrl == base + 'app/modify') {
									showMessage("修改租户信息异常！");
								} else {
									showMessage("创建租户异常！");
								}
								location.reload();
							} else {
								showMessage(response.message, function() {
									location.reload();
								});
							}
						});
						$('.modal-backdrop').hide();
						$('#tenant-wizard .modal-header').ace_wizard({
							step : 1
						});
						$('#tenant-wizard .wizard-actions').show();
					});
	$('#tenant-wizard .wizard-actions .btn[data-dismiss=modal]').removeAttr(
			'disabled');

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

/** ************************租户表单验证************************* */
// 验证用户是否填写租户名称和租期
function valid_tenantinfo() {
	// 租户名称
	if ($.trim($('#tenant_name').val()) == "") {
		showMessage("请填写租户名称！");
		return false;
	}
	// 租期开始时间
	if ($.trim($('#tenant_start').val()) == "") {
		console.log("tenant_start");
		showMessage("请选择租期开始时间！");
		return false;
	}
	// 租期结束时间
	if ($.trim($('#tenant_end').val()) == "") {
		console.log("tenant_end");
		showMessage("请选择租期结束时间！");
		return false;
	}
	return true;
}

/** 向待选集群列表中增加集群内容 */
function loadClusters() {
	$('#blockCluster').html('');
	$
			.ajax({
				type : 'get',
				url : base + 'cluster/getOrphanClus',
				dataType : 'json',
				success : function(array) {
					$
							.each(
									array,
									function(index, obj) {
										var clusterId = obj.clusterId;
										var clustername = decodeURIComponent(obj.clusterName);
										var clusterCPU = obj.totalCpu;
										var clusterMEM = obj.totalMem;
										$('#blockCluster')
												.append(
														'<label class="btn btn-round btn-white btn-primary">'
																+ '<input type="checkbox" name="btn-cluster" text="'
																+ clustername
																+ '" value="'
																+ clusterId
																+ '"/>&nbsp;&nbsp;'
																+ clustername
																+ '(CPU:'
																+ clusterCPU
																+ '核,&nbsp;内存:'
																+ clusterMEM
																+ 'MB)'
																+ '</label>');
									});
				}
			});
}

// 创建租户弹出框
function showCreateModal() {
	/* 重置创建租户栏中的各个窗口中的填写或选择的内容 */
	$("#basicInfoForm")[0].reset();
	$("#icInfoForm")[0].reset();
	$("#userInfoForm")[0].reset();

	// 创建应用appid设置为0，更改则大于0
	$('#cre_app_id').val(0);
	$('#activeCluster').html('');

	$('#tenant-wizard').modal('show');
}

/**编辑租户的信息*/
function editTenant(tenantId){
	/*赋值租户的Id*/
	$('#tenant_id').val(tenantId);
	$(':button[class="btn btn-success btn-round btn-sm btn-next"]').attr('data-last','确定');	
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
 * @datetime 2015年9月10日 17:12
 * @description 返回上一个页面
 */
function gobackpage() {
	history.go(-1);
}
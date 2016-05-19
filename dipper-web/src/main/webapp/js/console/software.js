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
						url : base + 'software/list',
						datatype : "json",
						height : '100%',
						autowidth : true,
						colNames : [ '软件ID', '软件名称', '软件版本', '软件类型', 'YUM标识',
								'描述信息', '登记用户', '登记时间', '用户ID', '快捷操作' ],
						colModel : [
								{
									name : 'swId',
									index : 'swId',
									width : 1,
									hidden : true
								},
								{
									name : 'swName',
									index : 'swName',
									width : 10,
									align : 'left',
									formatter : function(cell, opt, obj) {
										return '<i class="fa fa-cubes"></i><a href="'
												+ base
												+ 'software/detail/'
												+ obj.swId
												+ '.html"><b>'
												+ cell + '</b></a>';
									}
								},
								{
									name : 'swVersion',
									index : 'swVersion',
									width : 5,
									align : 'left',
									formatter : function(cell, opt, obj) {
										return '<b>' + cell + '</b>';
									}
								},
								{
									name : 'swType',
									index : 'swType',
									width : 7,
									align : 'left',
									formatter : function(cell, opt, obj) {
										switch (cell) {
										case (0):
											return '基础软件';
										case (1):
											return '中间件程序';
										}
									}
								},
								{
									name : 'swYumcall',
									index : 'swYumcall',
									width : 15,
									align : 'left'
								},
								{
									name : 'swDesc',
									index : 'swDesc',
									width : 16
								},
								{
									name : 'swCreatorName',
									index : 'swCreatorName',
									width : 6
								},
								{
									name : 'swCreatetime',
									index : 'swCreatetime',
									width : 9,
									formatter : function(cell, opt, obj) {
										if ((/^(\d{1,4})(-|\/)(\d{1,2})\2(\d{1,2}) (\d{1,2}):(\d{1,2}):(\d{1,2})$/)
												.test(cell)) {
											return cell;
										} else {
											var cellDate = new Date(cell);
											return convertDate(cellDate);
										}
									}
								},
								{
									name : 'swCreator',
									index : 'swCreator',
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
										var upda = $("#update_software").val();
										if (typeof (upda) != "undefined") {
											strHtml += "<button class=\"btn btn-primary btn-xs btn-round\" onclick=\"editSoftware('"
													+ rowObject.swId
													+ "','"
													+ rowObject.swName
													+ "','"
													+ rowObject.swVersion
													+ "','"
													+ rowObject.swType
													+ "','"
													+ rowObject.swYumcall
													+ "','"
													+ rowObject.swDesc
													+ "')\"><i class=\"ace-icon fa fa-pencil align-top\"></i>&nbsp;<b>编辑</b></button> &nbsp;";
										}
										var dele = $("#delete_software").val();
										if (typeof (dele) != "undefined") {
											strHtml += "<button class=\"btn btn-inverse  btn-xs btn-round\" onclick=\"deleteSoftware('"
													+ rowObject.swId
													+ "','"
													+ rowObject.swName
													+ "','"
													+ rowObject.swVersion
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

	// 软件安装增加目标主机
	$('#add-host').click(function() {
		var blockHosts = $('#blockHost input[name="btn-host"]');
		var hosts = new Array();
		for (i in blockHosts) {
			if (blockHosts[i].checked) {
				var host = $(blockHosts[i]).parent('label');
				hosts.push(host);
			}
		}
		if (hosts.length <= 0) {
			showMessage("请选择要添加的目标主机.");
			return;
		}
		for (i in blockHosts) {
			if (blockHosts[i].checked) {
				$(blockHosts[i]).parent('label').remove();
			}
		}
		for (i in hosts) {
			$('#activeHost').append(hosts[i]);
		}
		blockHosts = document.getElementsByName("btn-host");
		for (i in blockHosts) {
			blockHosts[i].checked = false;
		}
	});

	// 软件安装移除目标主机
	$('#remove-host').click(function() {
		// 获取某个固定div下的
		var activeHosts = $('#activeHost input[name="btn-host"]');
		var hosts = new Array();
		for (i in activeHosts) {
			if (activeHosts[i].checked) {
				var host = $(activeHosts[i]).parent('label');
				hosts.push(host);
			}
		}
		if (hosts.length <= 0) {
			showMessage("请选择要取消安装软件的目标主机.");
			return;
		}
		for (i in activeHosts) {
			if (activeHosts[i].checked) {
				$(activeHosts[i]).parent('label').remove();
			}
		}
		for (i in hosts) {
			$('#blockHost').append(hosts[i]);
		}
		activeHosts = document.getElementsByName("btn-host");
		for (i in activeHosts) {
			activeHosts[i].checked = false;
		}
	});

	// 软件安装增加软件项
	$('#add-soft').click(function() {
		var blockSofts = $('#blockSoft input[name="btn-soft"]');
		var softs = new Array();
		for (i in blockSofts) {
			if (blockSofts[i].checked) {
				var soft = $(blockSofts[i]).parent('label');
				softs.push(soft);
			}
		}
		if (softs.length <= 0) {
			showMessage("请选择要增加安装的软件项.");
			return;
		}
		/** @description:添加限制，目前仅支持单一软件的安装 */
		/* (1)用户已经选择了一个安装软件的情况 */
		var activeSoftLen = $('#activeSoft input[name="btn-soft"]').length;
		if (activeSoftLen >= 1) {
			showMessage("当前仅支持<font color='red'>单个</font>软件的安装，请确认.");
			return;
		}
		/* (2)已选为空，用户同时选择多个待选软件的情况 */
		if (softs.length > 1) {
			showMessage("当前仅支持<font color='red'>单个</font>软件的安装，请重新选择.");
			blockSofts = document.getElementsByName("btn-soft");
			for (i in blockSofts) {
				blockSofts[i].checked = false;
			}
			return;
		}
		/** 添加限制，目前仅支持单一软件的安装 */
		for (i in blockSofts) {
			if (blockSofts[i].checked) {
				$(blockSofts[i]).parent('label').remove();
			}
		}
		for (i in softs) {
			$('#activeSoft').append(softs[i]);
		}
		blockSofts = document.getElementsByName("btn-soft");
		for (i in blockSofts) {
			blockSofts[i].checked = false;
		}
	});

	// 软件安装移除软件项
	$('#remove-soft').click(function() {
		// 获取某个固定div下的
		var activeSofts = $('#activeSoft input[name="btn-soft"]');
		var softs = new Array();
		for (i in activeSofts) {
			if (activeSofts[i].checked) {
				var soft = $(activeSofts[i]).parent('label');
				softs.push(soft);
			}
		}
		if (softs.length <= 0) {
			showMessage("请选择要取消安装软件的目标主机.");
			return;
		}
		for (i in activeSofts) {
			if (activeSofts[i].checked) {
				$(activeSofts[i]).parent('label').remove();
			}
		}
		for (i in softs) {
			$('#blockSoft').append(softs[i]);
		}
		activeSofts = document.getElementsByName("btn-soft");
		for (i in activeSofts) {
			activeSofts[i].checked = false;
		}
	});

	/** 用户选择集群之后，系统根据集群选择栏中内容，加载对应主机列表 */
	$('#target_cluster')
			.change(
					function() {
						/** 首先清空备选和已选主机列表 */
						$('#blockHost').html('');
						$('#activeHost').html('');

						/** 判断用户已经选择的集群的值 */
						var clusterId = $('#target_cluster').val();
						$
								.ajax({
									type : 'get',
									url : base + 'cluster/hostList',
									data : {
										clusterId : clusterId
									},
									dataType : 'json',
									success : function(array) {
										$
												.each(
														array,
														function(index, obj) {
															var hostId = obj.hostId;
															var hostName = decodeURIComponent(obj.hostName);
															var hostIp = obj.hostIp;
															$('#blockHost')
																	.append(
																			'<label class="btn btn-round btn-white btn-primary">'
																					+ '<input type="checkbox" name="btn-host" text="'
																					+ hostName
																					+ '('
																					+ hostIp
																					+ ')" value="'
																					+ hostId
																					+ '"/>&nbsp;&nbsp;'
																					+ hostName
																					+ '('
																					+ hostIp
																					+ ')'
																					+ '</label>');
														});
									}
								});
					});

	/** 用户选择软件类型后，待选软件栏中加载用户选择的类型的软件列表 */
	$('#softwareType')
			.change(
					function() {
						/** 首先清空备选和已选软件列表 */
						$('#blockSoft').html('');
						$('#activeSoft').html('');

						/** 判断用户已经选择的集群的值 */
						var softType = $('#softwareType').val();
						$
								.ajax({
									type : 'get',
									url : base + 'software/typeList',
									data : {
										softType : softType
									},
									dataType : 'json',
									success : function(array) {
										$
												.each(
														array,
														function(index, obj) {
															var swId = obj.swId;
															var swName = decodeURIComponent(obj.swName);
															var swVersion = obj.swVersion;
															$('#blockSoft')
																	.append(
																			'<label class="btn btn-round btn-white btn-primary">'
																					+ '<input type="checkbox" name="btn-soft" text="'
																					+ swName
																					+ '<'
																					+ swVersion
																					+ '>" value="'
																					+ swId
																					+ '"/>&nbsp;&nbsp;'
																					+ swName
																					+ '<'
																					+ swVersion
																					+ '>'
																					+ '</label>');
														});
									}
								});
					});

	// 构建应用的信息
	$('#tenant-wizard .modal-header')
			.ace_wizard()
			.on(
					'change',
					function(e, info) {
						if (info.step == 1) { // 第一步需要执行的操作
							if (info.direction == 'next') {

								/* 汇总显示用户选择的主机和软件选项 */
								var installInfos = "";
								// 获取所有已经选择的全部与主机信息
								var activeHosts = $('#activeHost input[name="btn-host"]');

								// 校验是否选择了需要安装的主机
								if (activeHosts.length == 0) {
									showMessage('请选择需要安装软件的目标主机！');
									return false;
								}

								var hostString = '';
								for (var hostCount = 0, hostLen = activeHosts.length; hostCount < hostLen; hostCount++) {
									hostString += $(activeHosts[hostCount])
											.attr('text')
											+ (hostCount + 1 == activeHosts.length ? ''
													: ',&emsp;');
								}
								if (hostString.trim() !== '') {
									installInfos += '<div class="item"><label><b>目标主机 ：</b></label> <label>'
											+ hostString + '</label></div>';
								}
								/** 获取安装的软件信息 */
								var softString = '';
								var ids = $(grid_selector).jqGrid(
										"getGridParam", "selarrrow");
								var selSoftware = $(grid_selector).jqGrid(
										"getRowData", ids[0]);
								var softId = selSoftware.swId;
								var softName = selSoftware.swName;
								var softVersion = selSoftware.swVersion;
								softString += softName + "<" + softVersion
										+ ">";

								if (softString.trim() !== '') {
									installInfos += '<div class="item"><label><b>软件信息 ：</b></label> <label>'
											+ softString + '</label></div>';
								}

								$('#softwareInfo').html(installInfos);

							} else if (info.direction = 'previous') {
							}
						}
					})
			.on(
					'finished',
					function(e) { // 最后一步需要执行的操作
						// 提交操作
						e.preventDefault();

						/** 将下方的取消按钮和上一步按钮隐藏处理 */
						$('#prevButton').hide();
						$('#add_cancel').hide();

						/** (1)取得全部安装的主机列表 */
						var activeHosts = $('#activeHost input[name="btn-host"]');
						var hostIds = "";
						for (var hostCount = 0, hostLen = activeHosts.length; hostCount < hostLen; hostCount++) {
							hostIds += $(activeHosts[hostCount]).val()
									+ (hostCount + 1 == activeHosts.length ? ''
											: ',');
						}

						/** (2)获取软件安装列表 */
						var softIds = '';
						var ids = $(grid_selector).jqGrid("getGridParam",
								"selarrrow");
						softIds += $(grid_selector)
								.jqGrid("getRowData", ids[0]).swId;

						/** (4)组装创建租户所需的数据 */
						var installData = {
							hostIds : hostIds,
							softId : softIds
						};
						var installUrl = base + 'software/install';

						/* （1）显示进度条部分，展示目前安装进度 */
						$("#pid").show();
						$("#showId").html(
								"<font color=\"blue\">安装任务已经提交，请稍候。</font>");
						/** 向后台提交软件安装任务 */
						$.post(installUrl, installData);

						/** 置空请求路径和参数 */
						installUrl = null;
						installData = null;

						// $('.modal-backdrop').hide();
						// $('#tenant-wizard .modal-header').ace_wizard({
						// step : 1
						// });
						// $('#tenant-wizard .wizard-actions').show();
					});
	$('#tenant-wizard .wizard-actions .btn[data-dismiss=modal]').removeAttr(
			'disabled');

	/** 添加对于软件新增部分的功能 */
	$('#inssoft_submit').click(function() {
		// 添加校验
		if ($("#add_soft_frm").valid()) {
		var soft_name = $('#soft_name').val();
		var soft_version = $('#soft_version').val();
		var soft_type = $('input[name="add_soft_type"]:checked').val();
		var soft_yumcall = $('#soft_yum').val();
		var soft_desc = $("#soft_desc").val();

		softData = {
			swName : soft_name,
			swVersion : soft_version,
			swType : soft_type,
			swYumcall : soft_yumcall,
			swDesc : soft_desc
		};

		insertUrl = base + 'software/insert';
		$('#addSoftwareModal').modal('hide');
		$.post(insertUrl, softData, function(response) {
			if(response==""){
				showMessage('服务器异常！')
			}else{
				showMessage(response.message);
			}
			$(grid_selector).trigger("reloadGrid");
			$("#add_soft_frm")[0].reset();	
			
		});
		}
	});

	//添加软件（校验）
    $("#add_soft_frm").validate({
        rules: {
        	soft_name: {
                required: true,
                stringNameCheck: true,
                maxlength: 32,
                remote : {
					url : base + "software/checkSoftName",
					type : "post",
					dataType : "json",
					data : {
						softName : function() {
							return $.trim($("#soft_name").val());
						}
					},
					dataFilter : function(data) {
						return data;
					}
				}
            },
            soft_version: {
            	required : true,
				isVersionString : true,
				maxlength : 32
            },
            add_soft_type: {
                required: true
            },
            soft_yum: {
            	 required: true,
            	isCommand: true,
                maxlength: 256
            }
        },
        messages: {
        	soft_name: {
                required: "软件名称不能为空",
                maxlength: $.validator.format("软件名不能大于32个字符"),
                remote : '软件名称已存在，请重新输入。'
            },
            soft_version: {
            	required : "请输入软件版本",
				maxlength : $.validator.format("软件版本不能大于32个字符")
            },
            add_soft_type: {
                required: "请选择为软件类型。",
            },
            soft_yum: {
            	required: "请填写yum标识。",
                maxlength: $.validator.format("yum标识不能大于256个字符")
            }
        }
    });
    
	/** 添加对于软件修改部分的功能 */
	$('#modify_submit').click(function() {
		// 添加校验
		if ($("#modify_soft_frm").valid()) {
		var soft_id = $('#soft_id_edit').val();
		var soft_name = $('#soft_name_edit').val();
		var soft_version = $('#soft_version_edit').val();
		var soft_type = $('input[name="soft_type_radio"]:checked').val();
		var soft_yumcall = $('#soft_yum_edit').val();
		var soft_desc = $("#soft_desc_edit").val();

		softData = {
			swId : soft_id,
			swName : soft_name,
			swVersion : soft_version,
			swType : soft_type,
			swYumcall : soft_yumcall,
			swDesc : soft_desc
		};

		updateUrl = base + 'software/update';
		$('#modifySoftwareModal').modal('hide');
		$.post(updateUrl, softData, function(response) {
			showMessage(response.message);
			$(grid_selector).trigger("reloadGrid");
			$("#modify_soft_frm")[0].reset();
		});
		}
	});

	//编辑软件（校验）
    $("#modify_soft_frm").validate({
        rules: {
        	soft_name_edit: {
                required: true,
                stringNameCheck: true,
                maxlength: 32,
            },
            soft_version_edit: {
            	required : true,
				isVersionString : true,
				maxlength : 32
            },
            soft_type_radio: {
                required: true
            },
            soft_yum_edit: {
            	 required: true,
            	isCommand: true,
                maxlength: 256
            }
        },
        messages: {
        	soft_name_edit: {
                required: "软件名称不能为空",
                maxlength: $.validator.format("软件名不能大于32个字符"),
            },
            soft_version_edit: {
            	required : "请输入软件版本",
				maxlength : $.validator.format("软件版本不能大于32个字符")
            },
            soft_type_radio: {
                required: "请选择为软件类型。",
            },
            soft_yum_edit: {
            	required: "请填写yum标识。",
                maxlength: $.validator.format("yum标识不能大于256个字符")
            }
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
	$("#advanced_search")
			.on(
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

						/** 组装查询软件的数据对象 */
						var softData = {};
						for (var count = 0, length = column_array.length; count < length; count++) {
							/** 根据列的值，确定输入属性的名称 */
							switch (column_array[count]) {
							case ("1"): {
								softData.swName = value_array[count];
								break;
							}
							case ("2"): {
								softData.swVersion = value_array[count];
								break;
							}
							case ("3"): {
								softData.swYumcall = value_array[count];
								break;
							}
							case ("4"): {
								softData.swDesc = value_array[count];
								break;
							}
							}
						}

						var adSchSoftUrl = base + 'software/advancedSearch';

						/** 发送高级搜索的请求对象 */
						$.post(adSchSoftUrl, softData, function(response) {
							jQuery(grid_selector)[0].addJSONData(response);
							// jQuery(grid_selector).setGridParam({
							// datastr : response,
							// datatype : 'jsonstring',
							// rowNum : response.length
							// }).trigger('reloadGrid');
						});

						/* 查询是否存在关键词相关的结果 */
						// jQuery(grid_selector).jqGrid(
						// 'setGridParam',
						// {
						// url : base
						// + 'registry/advancedSearch?params='
						// + column_array + '&values='
						// + value_array
						// }).trigger("reloadGrid");
						/* 查询完毕，高级搜索窗口消失 */
						$('#adSchSoftModal').modal('hide');
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
		$('#adSchSoftModal').modal('hide');
		$('#advanced_search_frm')[0].reset();
	});

	/* 取消修改软件操作 */
	$('#modify_cancel').click(function() {
		$('#modifySoftwareModal').modal('hide');
		$('#modify_soft_frm')[0].reset();
		$('label.error').remove();
	});

	/* 取消新增软件操作 */
	$('.inssoft_cancel').click(function() {
		$('#addSoftwareModal').modal('hide');
		$('#add_soft_frm')[0].reset();
		$('label.error').remove();
	});
	
	//安装软件取消操作
	$('#add_cancel').click(function(){
		$('#blockHost').html('');
		$('#activeHost').html('');
		$("#add_soft_frm")[0].reset();	
		$(grid_selector).trigger("reloadGrid");
	})

});

/** 添加删除软件的处理 */
function deleteSoftware(softId, softName, softVersion) {
	bootbox
			.dialog({
				message : '<div class="alert alert-info" style="margin:10px"><span class="glyphicon glyphicon-info-sign"></span>&nbsp;确定删除软件&nbsp;'
						+ softName + '<' + softVersion + '>?</div>',
				title : "删除 软件",
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
							url = base + "software/delete";
							data = {
								softIds : softId
							};
							$.post(url, data, function(response) {
								if (response == "") {
									showMessage('删除软件出现异常。');
								} else {
									showMessage(response.message, function() {
										$(grid_selector).trigger("reloadGrid");
									});
								}
							});
						}
					}
				}
			});
}

/**
 * @author yangqinglin
 * @datetime 2016年4月14日 10:47
 * @description 添加模糊查询软件的功能
 */
function SearchSofts() {
	var searchSoftName = $('#searchSoftName').val();
	/* 查询是否存在关键词相关的结果 */
	jQuery(grid_selector).jqGrid('setGridParam', {
		url : base + 'software/listSearch?searchName=' + searchSoftName
	}).trigger("reloadGrid");
}

/** 在软件和版本页面中加载 */
function loadSoftwares() {
	/** 在软件类型栏中添加基础软件和中间件按钮 */
	/** 查询获取全部集群列表信息 */
	$('#softwareType').empty();
	$('#softwareType').append('<option value="0">请选择需要安装软件的类型</option>');
	$('#softwareType').append('<option value="1">基础软件</option>');
	$('#softwareType').append('<option value="2">中间件程序</option>');
}

/** 显示新增软件窗口 */
function showCreateModal() {
	$('#addSoftwareModal').modal('show');
}

// 创建安装软件弹出框
function showInstallModal() {
	/** 获取用户勾选的安装软件信息* */
	var ids = $(grid_selector).jqGrid("getGridParam", "selarrrow");
	if (ids.length > 1) {
		showMessage('目前仅支持<font color="red">单一</font>软件的安装，请重新选择！');
		return;
	} else if (ids.length == 0) {
		showMessage('请点击勾选需要对主机进行安装的软件！');
		return;
	}

	/* 重置创建租户栏中的各个窗口中的填写或选择的内容 */
	$("#clusterForm")[0].reset();

	/** 查询获取全部集群列表信息 */
	$('#target_cluster').empty();
	$('#target_cluster').append('<option value="0">请选择需要安装软件的目标集群</option>');
	$.ajax({
		type : 'get',
		url : base + 'cluster/all',
		dataType : 'json',
		success : function(array) {
			$.each(array, function(index, obj) {
				var clusterId = obj.clusterId;
				var clusterName = decodeURIComponent(obj.clusterName);
				$('#target_cluster').append(
						'<option value="' + clusterId + '">' + clusterName
								+ '</option>');
			});
		}
	});

	// 创建应用appid设置为0，更改则大于0
	$('#activeCluster').html('');
	$('#tenant-wizard').modal('show');
}

/** 编辑软件的全部信息 */
function editSoftware(softId, softName, softVersion, softType, softYum,
		softDesc) {
	// original_registry_name = registry_name;
	$('#soft_id_edit').val(softId);
	$('#soft_name_edit').val(softName);
	$('#soft_version_edit').val(softVersion);
	$("input[name=soft_type_radio]:eq(" + softType + ")").attr("checked",
			'checked');
	$('#soft_yum_edit').val(softYum);
	/* 如果描述为空则显示内容 */
	$("#soft_desc_edit").text(
			(softDesc == null || softDesc == "null") ? "" : softDesc);
	$('#modifySoftwareModal').modal('show');
}

/**
 * @author yangqinglin
 * @datetime 2015年10月29日 11:23
 * @description 添加高级查询函数
 */
function AdSchSoft() {
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

	$('#adSchSoftModal').modal('show');
}

/**
 * @author yangqinglin
 * @datetime 2015年9月10日 17:12
 * @description 返回上一个页面
 */
function gobackpage() {
	history.go(-1);
}

/** @description:添加与后台数据交互部分 */
$(function() {
	connect();
});

$(window).unload(function() {
	disconnect();
});

function connect() {
	if ('WebSocket' in window) {
		var host = window.location.host;
		socket = new WebSocket('ws://' + host + '/messagingService');
		socket.onopen = function() {
		};
		socket.onclose = function() {
		};
		socket.onmessage = function(event) {
			var obj = JSON.parse(event.data);
			message = JSON.parse(obj.content);
			if (obj.messageType == 'ws_sticky') {
				if (message.title == "软件安装") {
					/** message的消息由进度值#提示内容组成* */
					var prog_msg = message.message.split('#');
					if (!isNaN(parseInt(prog_msg[0]))) {
						var progress = Number(prog_msg[0]);
						var show_msg = prog_msg[1];

						if (progress != 100) {
							$("#prog_span").html(
									"<font color=\"black\"><b>" + progress
											+ "&nbsp;%</b></font>");
							$("#progressid").width(progress + "%");
							$("#showId").html(
									"<font color=\"blue\">" + show_msg
											+ "</font>");
						} else
						/** 软件已经安装完成* */
						if (progress == 100) {

							/** 在此处做出安装结果判断（1）全部成功[all]，（2）部分成功[part]，（3）全部失败[null] */
							/** show_msg包含两部分[all|part|null]$结果信息 */
							var rstMessage = show_msg.split('$');
							var result = rstMessage[0];
							var finalMsg = rstMessage[1];

							/** 进度条中填充百分比字符串 */
							$("#prog_span").html(
									"<font color=\"black\"><b>" + progress
											+ "&nbsp;%</b></font>");

							switch (result) {
							case ("all"): {
								$("#progressid").width(progress + "%");
								$("#showId").html(
										"<font color=\"blue\">" + finalMsg
												+ "</font>");
								break;
							}
							case ("part"): {
								$("#progressid").removeClass(
										"progress-bar-success");
								$("#progressid")
										.addClass("progress-bar-yellow");
								$("#progressid").width(progress + "%");
								$("#showId").html(
										"<font color=\"red\">" + finalMsg
												+ "</font>");
								break;
							}
							case ("null"): {
								$("#progressid").removeClass(
										"progress-bar-success");
								$("#progressid").addClass("progress-bar-pink");
								$("#progressid").width(progress + "%");
								$("#showId").html(
										"<font color=\"red\">" + finalMsg
												+ "</font>");
								break;
							}
							}

							/** 修改开始安装的按钮值 */
							$('#nextButton').hide();
							$('#ensureButton').show();
							$('#ensureButton').click(function() {
								$('#tenant-wizard').modal('hide');
								location.reload();
							});
						}
					} else {
						var progress = prog_msg[0];
						var show_msg = prog_msg[1];
						if (progress == "fail") {
							$("#prog_span")
									.html(
											"<font color=\"black\"><b>0&nbsp;%</b></font>");
							$("#progressid").width("0%");
							$("#showId").html(
									"<font color=\"red\"><b>" + show_msg
											+ "</b></font>");
						} else if (progress == "success") {
							$("#prog_span")
									.html(
											"<font color=\"black\"><b>100&nbsp;%</b></font>");
							$("#showId").html(
									"<font color=\"blue\"><b>" + show_msg
											+ "</b></font>");
						}

					}
				}
			}
		};
	} else {
		console.log('Websocket not supported');
	}
}

function disconnect() {
	socket.close();
	console.log("Disconnected");
}

/** 将日期格式化为字符串* */
function convertDate(date) {
	return date.getFullYear()
			+ "-"
			+ (date.getMonth() < 9 ? ("0" + (date.getMonth() + 1)) : (date
					.getMonth() + 1)) + "-"
			+ (date.getDate() < 10 ? ("0" + date.getDate()) : date.getDate())
			+ " " + date.getHours() + ":" + date.getMinutes() + ":"
			+ date.getSeconds();

}
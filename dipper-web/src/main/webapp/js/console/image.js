var grid_selector = "#image_list";
var page_selector = "#image_page";
var wizardContent = null;
var cancelUpload = false;
/* 设置全局变量保存上传文件所在文件夹名称的UUID */
var folder_uuid = null;
/** 保存远程镜像文件的类型 */
var remote_image_type = '';
/** 保存当前高级查询条件的列数量 */
var advanceColNum = 0;

jQuery(function($) {
	wizardContent = $('#wizard_content_div').html();
	$("script[src='" + base + "js/message.js']").remove();
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
						url : base + 'image/list',
						datatype : "json",
						height : '100%',
						autowidth : true,
						colNames : [ '', '版本ID', '版本名称', '应用版本', '应用类型',
								'所属应用', '状态', '创建时间', '操作', '' ],
						colModel : [
								{
									name : 'imageId',
									index : 'imageId',
									width : 1,
									hidden : true
								},
								{
									name : 'imageUuid',
									index : 'imageUuid',
									width : 10,
									formatter : function(cell, opt, obj) {
										return '<a href="' + base
												+ 'image/detail/' + obj.imageId
												+ '.html">' + cell + '</a>';
									}
								},
								{
									name : 'imageName',
									index : 'imageName',
									width : 26
								},
								{
									name : 'imageTag',
									index : 'imageTag',
									width : 10
								},
								{
									name : 'imageType',
									index : 'imageType',
									width : 10,
									formatter : function(cell, opt, obj) {
										if ("BASIC" == cell) {
											return "<b>基础镜像</b>";
										} else if ("APP" == cell) {
											return "<b>应用镜像</b>";
										} else {
											return "<b>未知类型</b>";
										}
									}
								},
								{
									name : 'appName',
									index : 'appName',
									width : 10

								},
								{
									name : 'imageStatus',
									index : 'imageStatus',
									width : 10,
									formatter : function(cell, opt, obj) {
										switch (obj.imageStatus) {
										case 0:
											return "已删除";
										case 1:
											return "已发布";
										case 2:
											return "已制作";
										default:
											return "未知态";
										}
									}
								},
								{
									name : 'imageCreatetime',
									index : 'imageCreatetime',
									width : 13
								},
								{
									name : '',
									title : false,
									index : '',
									width : 210,
									align : 'center',
									fixed : true,
									sortable : false,
									resize : false,
									formatter : function(cellvalue, options,
											rowObject) {
										var type = rowObject.imageType;
										var state = rowObject.imageStatus;
										var pushable = "disabled";
										if (state == 2) {
											pushable = "";
										}
										var pushBtn = "";
										var modBtn = "";
										var mod = $("#mod_image").val();
										if (typeof (mod) != "undefined") {
											modBtn = "<button class=\"btn btn-xs btn-primary btn-round \" onclick=\"modImg("
													+ rowObject.imageId
													+ ",'"+type+"')\">"
													+ "<i class=\"ace-icon glyphicon glyphicon-check\"></i>"
													+ "<b>版本发布</b></button> &nbsp;";
										}
										var delBtn = "";
										var dele = $("#delete_image").val();
										if (typeof (dele) != "undefined") {
											delBtn = "<button class=\"btn btn-xs btn-inverse btn-round\" onclick=\"removeImage('"
													+ rowObject.imageId
													+ "','"
													+ rowObject.imageName
													+ "','"
													+ rowObject.instanceNum
													+ "')\">"
													+ "<i class=\"ace-icon fa fa-trash-o bigger-125\"></i>"
													+ "<b>删除</b></button>";
										}
										return pushBtn + modBtn + delBtn;
									}
								}, {
									name : 'instanceNum',
									index : 'instanceNum',
									hidden : true
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

	current = 0;

	// 批量删除镜像函数
	$('#batch_delete_menu')
			.on(
					'click',
					function() {
						var ids = $(grid_selector).jqGrid("getGridParam",
								"selarrrow");
						if (ids.length == 0) {
							showMessage("请先选择需要删除的应用版本!");
							return;
						}
						var message = "";
						var idList = "";
						for (var i = 0; i < ids.length; i++) {
							var rowData = $(grid_selector).jqGrid("getRowData",
									ids[i]);
							if (rowData.instanceNum > 0) {
								showMessage('应用版本中存在应用实例， 不允许删除！');
								return false;
							}
							idList += i == ids.length - 1 ? rowData.imageId
									: rowData.imageId + ',';
							message += rowData.imageName + ":"
									+ rowData.imageTag + ",&nbsp;";
						}
						bootbox
								.dialog({
									message : '<div class="alert alert-info" style="margin:10px"><span class="glyphicon glyphicon-info-sign"></span>&nbsp;确定要删除应用版本&nbsp;'
											+ message + '?</div>',
									title : "删除应用版本",
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
												// 显示遮罩层
												showMask();
												// 提示信息显示
												$('#spinner-message font')
														.html(
																"应用版本批量删除中,请稍等....");
												$
														.post(
																base
																		+ 'image/remove/batch',
																{
																	ids : idList
																},
																function(
																		response) {
																	// 隐藏遮罩层
																	hideMask();
																	if (response == "") {
																		showMessage("删除应用版本异常！");
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
	
	/** 制作并发布远程镜像* */
	$('#rmt_mkps_submit')
			.click(
					function() {
						if ($("#remote_crtimg_frm").valid()) {
							/** 用户向后台提交制作或者制作发布镜像时，首先置灰制作镜像和制作发布按钮* */
							$('#rmt_mkps_submit').prop('disabled', true);
							/** 首先隐藏通知栏进行操作* */
							$('#showInfo').hide();
							/* 用户输入镜像名称 */
							// var image_name = $('#image_name').val();
							var image_name = $.trim($(
									'#target_app option:selected').text());
							/* 用户标识镜像标签（版本） */
							var image_tag = $('#image_tag').val();
							/* 获取仓库的ID信息 */
							var registry_id = $('#rmt_reg_select').val();
							if (registry_id == 0 || registry_id == undefined) {
								$('#showRemoteInfo').html(
										"<font color=\"red\"><b>"
												+ '请选择推送目标仓库！'
												+ "&nbsp;</b></font>");
								$('#showInfo').show();
								/** 还原制作和远程制作发布按钮* */
								restore_rmtbtn();
								return;
							}
							/* 获取所在主机的IP、用户名、密码 */
							var host_ip = $("#source_ip").val();
							var host_user = $("#source_user").val();
							var host_passwd = $("#source_passwd").val();
							/* 获取用户选择应用的ID */
							var app_id = $('#target_app').val();
							/* 取得包文件所在的文件夹路径 */
							var file_folder = $('#source_path').val();
							/* 获取tar等文件的名称 */
							var file_name = $(
									'input[name="src_project"]:checked').val();
							/** 判断用户是否选择了需要编译的文件* */
							if (file_name == undefined || file_name == null) {
								$('#showRemoteInfo').html(
										"<font color=\"red\"><b>" + '请选择需要文件！'
												+ "&nbsp;</b></font>");
								$('#showInfo').show();
								/** 还原制作和远程制作发布按钮* */
								restore_rmtbtn();
								return;
							}

							/** 将文件名称拆分为数组进行判断处理* */
							var filename_array = file_name.split(".");
							var array_length = filename_array.length;

							/* 获取镜像的类型 */
							// var image_type =
							// $('input[name="rmt_image_type"]:checked').val();
							// var image_type = 'APP';
							switch (remote_image_type) {
							case ("BASIC"):
								if ("tar" != filename_array[array_length - 1]) {
									$('#showRemoteInfo').html(
											"<font color=\"red\"><b>"
													+ "基础镜像，只能上传后缀名为tar格式的文件！"
													+ "&nbsp;</b></font>");
									$('#showInfo').show();
									/** 还原制作和远程制作发布按钮* */
									restore_rmtbtn();
									return;
								}
								break;
							case ("APP"):
								/* 在选择发布应用类型的情况下，判断用户是否选择应用 */
								if (app_id == 0 || app_id == undefined
										|| app_id == null) {
									$('#showRemoteInfo')
											.html(
													"<font color=\"red\"><b>"
															+ "请在【所属应用】中选择待制作和发布的镜像所属的应用。"
															+ "&nbsp;</b></font>");
									$('#showInfo').show();
									/** 还原制作和远程制作发布按钮* */
									restore_rmtbtn();
									return;
								}

								if (("tar" != filename_array[array_length - 1])
										&& ("zip" != filename_array[array_length - 1])
										&& ("tar.gz" != (filename_array[array_length - 2]
												+ "." + filename_array[array_length - 1]))) {
									$('#showRemoteInfo')
											.html(
													"<font color=\"red\"><b>"
															+ "创建应用版本，只能上传后缀名为zip或者tar.gz格式的文件！"
															+ "&nbsp;</b></font>");
									$('#showInfo').show();
									/** 还原制作和远程制作发布按钮* */
									restore_rmtbtn();
									return;
								}
								break;
							}
							/** @bug166_begin，做仓库主机检查，防止误删，获取上传仓库主机的信息 */
							var check_url = base + "registry/queryRegistryById";
							var check_data = {
								registryId : registry_id
							};

							var activeRmtEnvs = $('#activeRmtEnv input[name="btn-env"]');
							var envIds = '';
							for (var i = 0; i < activeRmtEnvs.length; i++) {
								envIds += $(activeRmtEnvs[i]).val()
										+ (i + 1 == activeRmtEnvs.length ? ''
												: ',');
							}
							$.post(check_url, check_data, function(response) {
								/** 目标仓库如果存在，直接提示并作返回处理 */
								if (!response.success) {
									$('#showRemoteInfo').text(
											"目标仓库的记录不存在，请核对后新建应用版本！");
									$('#showInfo').show();
									/** 还原制作和远程制作发布按钮* */
									restore_rmtbtn();
									return;
								} else {
									// if (validateImageName(image_name)&&
									// validateVersion(image_tag)) {
									if (validateVersion(image_tag)) {
										/** 发送制作镜像的请求* */
										var img_url = "";
										var titleType = $('#titleType').val();
										if (titleType == "create") {
											img_url = base + 'image/rmtMkPsh';
										} else if (titleType == "import") {
											img_url = base
													+ 'image/importImage';
										}
										var img_data = {
											hostIP : host_ip,
											hostUser : host_user,
											hostPasswd : host_passwd,
											appId : app_id,
											envIds : envIds,
											imageTag : image_tag,
											imageName : image_name,
											registryId : registry_id,
											fileFolder : file_folder,
											fileName : file_name,
											imageType : remote_image_type
										};
										$.post(img_url, img_data, function(
												response) {
										});
										$("#rmt_pid").show();
										if (titleType == "create") {
											$("#rmt_showId").text(
													"远程制作并发布镜像(主机:" + host_ip
															+ ")任务已提交！");
										} else if (titleType == "import") {
											$("#rmt_showId").text(
													"版本导入(主机:" + host_ip
															+ ")任务已提交！");
										}

									} else {
										restore_rmtbtn();
										return;
									}
								}
							});
						}
					});
	/**
	 * 导出镜像
	 */
	$('#export_img')
			.click(
					function() {
						// 1.选择镜像
						var ids = $(grid_selector).jqGrid("getGridParam",
								"selarrrow");
						var message = "";
						var idList = "";
						if (ids.length == 0) {
							showMessage("请先选择需要导出的应用版本!");
							return;
						}
						for (var i = 0; i < ids.length; i++) {
							var rowData = $(grid_selector).jqGrid("getRowData",
									ids[i]);
							idList += i == ids.length - 1 ? rowData.imageId
									: rowData.imageId + ',';
							message += rowData.imageName + ":"
									+ rowData.imageTag + " ";
						}
						var imgInfo = message.split(":")
						var imgName = imgInfo[1].split("/")[2];
						var imgTag = imgInfo[2];
						$('#export_img_name').val(imgName);
						$('#export_img_tag').val(imgTag);
						// 2.弹出导出界面
						$('#exportImgModal').modal('show');
					});

	/**
	 * 导出镜像取消
	 */
	$('#export_cancel').click(function(event) {
		event.preventDefault();
		$('#exportImgModal').modal('hide');
		$('#export_img_frm')[0].reset();
		$('label.error').remove();
	});

	/**
	 * 导出镜像提交
	 */
	$('#export_submit')
			.click(
					function() {
						var ids = $(grid_selector).jqGrid("getGridParam",
								"selarrrow");
						var message = "";
						var idList = "";
						if (ids.length == 0) {
							showMessage("请先选择需要导出的应用版本!");
							return;
						}
						for (var i = 0; i < ids.length; i++) {
							var rowData = $(grid_selector).jqGrid("getRowData",
									ids[i]);
							idList += i == ids.length - 1 ? rowData.imageId
									: rowData.imageId + ',';
							message += rowData.imageName + ":"
									+ rowData.imageTag + " ";
						}
						var imgName = $('#export_img_name').val();
						var imgTag = $('#export_img_tag').val();
						var exportIp = $('#export_ip').val();
						var userName = $('#export_user').val();
						var password = $('#export_passwd').val();
						var path = $('#export_path').val();
						var url = base + "image/exportImage";
						console.log("-----" + message + "-----");
						var data = {
							imageId : idList,
							hostIp : exportIp,
							hostUser : userName,
							hostPwd : password,
							imageInfo : message,
							imageName : imgName,
							imageTag : imgTag,
							savePath : path
						}
						$('#exportImgModal').modal('hide');
						// 显示遮罩层
						showMask();
						$.post(url, data, function(response) {
							// 隐藏遮罩层
							hideMask();
							if (response == "") {
								showMessage("删除应用版本异常！");
							} else {
								showMessage(response.message);
							}
							$(grid_selector).trigger("reloadGrid");
						});
					});

	// 远程创建应用版本取消
	$("#rmt_cancel").on('click', function(event) {
		event.preventDefault();
		/** 隐藏应用部分相关的显示 */
		$('#RmtCrtImgModal').modal('hide');
		$('#remote_crtimg_frm')[0].reset();
		$('label.error').remove();
	});

	/**
	 * 验证远程制作/制作发布镜像表格。
	 */
	$('#remote_crtimg_frm').validate({
		rules : {
			// image_name : {
			// required : true,
			// isImageName : true,
			// maxlength : 64,
			// isNotNull : false
			// },
			image_tag : {
				required : true,
				isURLString : true,
				maxlength : 64
			},
			source_ip : {
				required : true,
				ip : true,
				maxlength : 15
			},
			source_user : {
				required : true,
				maxlength : 64
			},
			source_passwd : {
				required : true,
				maxlength : 128
			}
		},
		messages : {
			// image_name : {
			// required : "镜像名称不能为空值，请输入。",
			// isImageName : "只能以小写字母、数字起始，其后包含小写字母、数字、下划线和冒号的字符串",
			// maxlength : $.validator.format("镜像名称不能大于64个字符"),
			// isNotNull : "镜像名称不能为空值，请输入。"
			// },
			image_tag : {
				required : "镜像的标签版本内容不能为空",
				isURLString : "只能包含(字母、数字、下划线、破折号、斜线)合法字符",
				maxlength : $.validator.format("标签版本不能大于64个字符")
			},
			source_ip : {
				required : "请输入远程主机的IP地址",
				ip : "请输入正确的IP地址，例如：192.168.123.123",
				maxlength : $.validator.format("IP地址不能大于15个字符")
			},
			source_user : {
				required : "请输入用户名称",
				maxlength : $.validator.format("用户名称不能大于64个字符")
			},
			source_passwd : {
				required : "请输入登陆密码",
				maxlength : $.validator.format("登陆密码不能大于128个字符")
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
							url : base + 'image/advancedSearch?params='
									+ column_array + '&values=' + value_array
						}).trigger("reloadGrid");

				/** @bug152_begin 清空用户多选的参数 */
				while ($("#params li").length > 1) {
					$("#remove-param").parent().remove();
				}
				/** @bug152_finish */
				$('#advancedSearchImageModal').modal('hide');
				$('#advanced_search_frm')[0].reset();
			});

	/**
	 * 向高级搜索的取消按钮添加重置隐藏
	 */
	$("#advanced_cancel").on('click', function(event) {
		event.preventDefault();
		$('#advancedSearchImageModal').modal('hide');
		$('#advanced_search_frm')[0].reset();
		$('label.error').remove();
	});

	$(".close").click(function() {
		$('#remote_crtimg_frm')[0].reset();
		$('label.error').remove();
	});

	/** @bug78_begin 向制作镜像的表格添加校验校验部分 */
	// $('#templatename').validate({
	// rules : {
	// templatename : {
	// required : true,
	// //isRightfulString : true,
	// maxlength : 64
	// }
	// },
	// messages : {
	// templatename : {
	// required : "请输入应用名称",
	// maxlength : $.validator.format("应用名称不能大于64个字符")
	// }
	// }
	// });
	$('#templateversion').validate({
		rules : {
			templateversion : {
				required : true,
				isVersionString : true,
				maxlength : 32
			}
		},
		messages : {
			templateversion : {
				required : "请输入应用版本",
				maxlength : $.validator.format("应用版本不能大于32个字符")
			}
		}
	});
	/** @bug78_finish 结束对于制作的镜像名称的校验** */
});

/**
 * @author yangqinglin
 * @datetime 2015年10月29日 11:23
 * @description 添加高级查询函数
 */
function AdvancedSearchImage() {
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
	$('#advancedSearchImageModal').modal('show');
}

/* 2016年1月26日 还原远程制作和远程制作发布的button */
function restore_rmtbtn() {
	/** 还原制作和远程制作发布按钮* */
	// $('#rmt_make_submit').prop('disabled', false);
	$('#rmt_mkps_submit').prop('disabled', false);
}

/*
 * 2016年1月26日 隐藏远程制作和远程发布的button function hide_rmtbtn() {
 *//** 隐藏制作和远程制作发布按钮* */
/*
 * //$('#rmt_make_submit').prop('disabled', true);
 * $('#rmt_mkps_submit').prop('disabled', true); }
 */

// 删除单个镜像
function removeImage(id, image_name, instanceNum) {
	if (instanceNum > 0) {
		showMessage('应用版本中存在应用实例，不允许删除！');
		return false;
	}
	bootbox
			.dialog({
				message : '<div class="alert alert-info" style="margin:10px"><span class="glyphicon glyphicon-info-sign"></span>&nbsp;确定要删除镜像&nbsp;'
						+ image_name + '?</div>',
				title : "删除应用版本",
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
							url = base + "image/remove/" + id;
							data = {};
							// 显示遮罩层
							showMask();
							// 提示信息显示
							$('#spinner-message font').html("应用版本删除中,请稍等....");
							$.post(url, data, function(response) {
								// 隐藏遮罩层
								hideMask();
								if (response == "") {
									showMessage("删除该版本应用异常！");
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
// 镜像修正
function modImg(id,type) {
	if(type=='null'){
		type="BASIC";
	}
	$(':radio[name="mod_image_type"][value="'+type+'"]').click();
	changeModSelect()
	
	$('#mod_image_modal').off('show.bs.modal');
	$('#mod_image_modal').on(
			'show.bs.modal',
			function() {
				$('#mod_image_id').val(id);
				$.post(base + 'app/all?page=1&rows=65536', null, function(
						response) {
					$('#update_activateEnv').html('');
					$('#update_blockEnv').html('');
					$('#belong_app_select').html(
							'<option value="0">请选择应用</option>');
					if (response != "") {
						$.each(response.rows, function(index, obj) {
							var appid = obj.appId;
							var appname = decodeURIComponent(obj.appName);
							$('#belong_app_select').append(
									'<option value="' + appid + '">' + appname
											+ '</option>');
						});
					}
					// 初始化应用版本的环境列表
					getEnvByImgId('update_blockEnv', 'update_activateEnv');
				}, 'json');
			});
	$('#mod_image_modal').modal("show");
}

function modImgFun() {
	var app = null;
	var id = $('#mod_image_id').val();
	var envids = '';
	category = $('input[name="mod_image_type"]:checked').val();
	if ('APP' == category) {
		app = $("#belong_app_select").val();
		if (0 == app) {
			showMessage("请选择镜像所属应用！");
			return;
		}
		var activeEnvs = $('#update_activateEnv input[name="btn-env"]');
		if (activeEnvs.length > 0) {
			for (var i = 0; i < activeEnvs.length; i++) {
				envids += $(activeEnvs[i]).val()
						+ (i + 1 == activeEnvs.length ? '' : ',');
			}
		}
	}
	data = {
		image : id,
		app : app,
		type : category,
		envids : envids
	};
	$('#mod_image_modal').modal("hide");
	$.post(base + 'image/mod', data, function(response) {
		if (response == "") {
			showMessage("当前版本应用发布到其他环境异常！");
			$(grid_selector).trigger("reloadGrid");
		} else {
			showMessage(response.message, function() {
				$(grid_selector).trigger("reloadGrid");
			});
		}

	});
}
/*
 * // 镜像发布 function pushImg(id) { data = { image : id }; $.post(base +
 * 'image/fastpush', data, function(response) {
 *//** 暂不显示提交消息，会遮盖后续服务器端返回的处理结果 */
/*
 * // successNotice("操作提示", "镜像快速发布任务已提交！"); showMessage(response.message); }); }
 */

function hideModal(id) {
	$('#belong_app_select').empty();
	$('#update_activateEnv').html('');
	$('#update_blockEnv').html('');
	$('#belong_app_select').html('<option value="0">请选择应用</option>');
	closeModal(id);
}
// 关闭modal
function closeModal(id) {
	$('#' + id).modal('hide');
}
// 取消按钮点击时触发的函数
function cancelImageCreate() {
	$('#modal-wizard').modal("hide");
	var label = $('#upload-image_btn-text').text();
	if ("取消" == label) {
		file = document.getElementById("image_file").files[0];
		sendCancelMessage(file.name);
		$('#upload-image_btn-text').text("上传");
		cancelUpload = true;
		return;
	}
	current = 0;
}
// 窗口显示时调用的函数
function showCreateImageModal() {
	$('#modal-wizard').empty();
	$('#modal-wizard').html(wizardContent);
	$('#modal-wizard').modal('show');
	$.post(base + 'registry/list?page=1&rows=65536', null, function(response) {
		if (response != "") {
			$.each(response.rows, function(index, obj) {
				var registryId = obj.registryId;
				var hostId = obj.hostId;
				var comp = registryId + ":" + hostId;
				var registryname = decodeURIComponent(obj.registryName);
				$('#registry_select').append(
						'<option value="' + comp + '">' + registryname
								+ '</option>');
			});
		}
	}, 'json');

	/* 保存第一个加载的应用ID值和判断是否取到的布尔值 */
	$('#app_select').html('<option value="0">请选择应用</option>');
	$.post(base + 'app/all?page=1&rows=65536', null, function(response) {
		if (response != "") {
			$.each(response.rows, function(index, obj) {
				var appid = obj.appId;
				var appname = decodeURIComponent(obj.appName);
				$('#app_select').append(
						'<option value="' + appid + '">' + appname
								+ '</option>');
			});

			/** 根据应用的ID获取对应的环境ID列表 */
			// console.log(firstAppid + ":" + getFirstAppid);
			imageLoadEnvs($('#app_select').val(), "lcl", "Lcl");
		}
	}, 'json');

	$('#modal-wizard .modal-header').ace_wizard({
		step : 1
	}).on('change', function(e, info) {
		switch (info.step) {
		case 1:
			var appsel=Number($('#app_select').val());
			if(appsel==0){
				e.preventDefault();
				showMessage('请选择应用！');
			}else{
				if($.trim($('#activeLclEnv').html())==''){
					e.preventDefault();
					showMessage('请选择应用环境！');
				}
				//上传镜像校验
				if (current < 1) {
					e.preventDefault();
					showMessage("请先上传文件！");
				} else {
					$('#make_image_btn').removeClass("hide");
					/** **********@bug169_begin:制作镜像任务提交后,临时将"制作"button置灰(不可点击),当制作失败将button释放(可用)************* */
					$('#make_image_btn').prop('disabled', false);
					/** **********@bug169_finish************* */
					$('#upload_image_btn').addClass("hide");
				}
			}
			break;
		case 2:
			if (info.direction == 'next') {
				if (current < 2) {
					e.preventDefault();
					showMessage("请先制作镜像！");
				}
			} else if (info.direction = 'previous') {
				$('#make_image_btn').addClass("hide");
				$('#upload_image_btn').removeClass("hide");
			}
			break;
		case 3:
			if (info.direction = 'previous') {
				$('#make_image_btn').removeClass("hide");
				/** **********@bug169_begin:制作镜像任务提交后,临时将"制作"button置灰(不可点击),当制作失败将button释放(可用)************* */
				$('#make_image_btn').prop('disabled', false);
				/** **********@bug169_finish************* */
				$('#upload_image_btn').addClass("hide");
			}
		default:
			break;
		}
	}).on('finished', function(e) { // 最后一步需要执行的操作
		e.preventDefault();
		showMessage("点击确定提交创建应用版本任务！", function() {
			/** @bug166_begin 获取上传仓库主机的信息 */
			var comp = $("#registry_select").val();
			var ids = comp.split(":");
			var registryId = ids[0];
			var url = base + "registry/queryRegistryById";
			data = {
				registryId : registryId
			};

			$.post(url, data, function(response) {
				/** 目标仓库存在，且能使用 */
				if (response.success) {
					submitPublishImage();
				} else {
					showMessage("目标仓库不存在，请核对后发布！");
					return;
				}
			});
			/** **@bug166_finish*************************** */
		});
	}).on('stepclick', function(e) {
	});
	$('#modal-wizard .wizard-actions .btn[data-dismiss=modal]').removeAttr(
			'disabled');
}

/** 执行镜像发布任务 */
function submitPublishImage() {
	var appId = $("#app_select").val();
	var imageId = $('#image_id').val();
	var imageName = $.trim($("#app_select option:selected").text());
	var imageTag = $('#templateversion').val();
	var comp = $("#registry_select").val();
	var ids = comp.split(":");
	var registryId = ids[0];
	var hostId = ids[1];
	data = {
		imageId : imageId,
		appId : appId,
		imageTag : imageTag,
		imageName : imageName,
		hostId : hostId,
		registryId : registryId
	};
	$.post(base + 'image/push', data, function(resp) {
	});
	$('#modal-wizard').modal('hide');
	successNotice("应用版本创建", "任务已经提交！");
	setTimeout(function() {
		$(grid_selector).trigger("reloadGrid");
	}, 3000);
}

// 全局的websocket变量
var paragraph = 4 * 1024 * 1024; // 64*1024
var file;
var startSize, endSize = 0;
var i = 0;
var j = 0;
var count = 0;

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
				if (message.title == "制作镜像") {
					if (message.success) {
						$('#imagemessage').text('应用版本创建成功！');
						current = 2;
						$('#make_image_btn').hide();
						$(grid_selector).trigger("reloadGrid");
					} else {
						$('#imagemessage').text('应用版本创建失败！');
						/** **********@bug169_begin:制作镜像任务提交后,临时将"制作"button置灰(不可点击),当制作失败将button释放(可用)************* */
						$('#make_image_btn').prop('disabled', false);
						/** **********@bug169_finish************* */
						current = 0;
					}
					showNotice(message);
				} else if (message.title == "远程制作镜像") {// 添加远程制作镜像处理部分
					// if (message.success) {
					// successNotice(message.title, message.message);
					/** message的消息由进度值#提示内容组成* */
					var prog_msg = message.message.split('#');
					if (!isNaN(parseInt(prog_msg[0]))) {
						var progress = Number(prog_msg[0]);
						var show_msg = prog_msg[1];

						$("#prog_span").html(
								"<font color=\"black\"><b>" + progress
										+ "&nbsp;%</b></font>");
						$("#rmt_progressid").width(progress + "%");
						$("#rmt_showId").html(
								"<font color=\"blue\">" + show_msg + "</font>");

						/** 镜像已经处理完成* */
						if (progress == 100) {
							$(grid_selector).trigger("reloadGrid");
							/** 延迟三秒隐去远程制作窗口* */
							tailinWork();
						}
					} else {
						var progress = prog_msg[0];
						var show_msg = prog_msg[1];
						if (progress == "fail") {
							$("#prog_span")
									.html(
											"<font color=\"black\"><b>0&nbsp;%</b></font>");
							$("#rmt_progressid").width("0%");
							$("#rmt_showId").html(
									"<font color=\"red\"><b>" + show_msg
											+ "</b></font>");
						} else if (progress == "success") {
							$("#prog_span")
									.html(
											"<font color=\"black\"><b>100&nbsp;%</b></font>");
							$("#rmt_showId").html(
									"<font color=\"blue\"><b>" + show_msg
											+ "</b></font>");
						}
						/** 后台制作或者制作发布完毕镜像后，还原之前置灰的按钮* */
						// $('#rmt_make_submit').prop('disabled', false);
						$('#rmt_mkps_submit').prop('disabled', false);
						/** 延迟三秒隐去远程制作窗口* */
						tailinWork();
					}
				} else if (message.title == "镜像发布") {
					current = 0;
					showNotice(message);
					setTimeout(function() {
						location.reload(true);
					}, 3000);
					/** @bug171_begin:[镜像管理]当"镜像不存在！或者仓库主机不存在"发布镜像失败,没有将失败信息返回给终端用户 */
				} else if (message.title == "镜像快速发布") {
					showNotice(message);
					/** @bug171_finish */
				} else if (message.title == "IMAGEID" && message.success) {
					$('#image_id').val(message.message);
				} else if (message.title == "版本导入") {// 添加远程制作镜像处理部分
					// if (message.success) {
					// successNotice(message.title, message.message);
					/** message的消息由进度值#提示内容组成* */
					var prog_msg = message.message.split('#');
					if (!isNaN(parseInt(prog_msg[0]))) {
						var progress = Number(prog_msg[0]);
						var show_msg = prog_msg[1];

						$("#prog_span").html(
								"<font color=\"black\"><b>" + progress
										+ "&nbsp;%</b></font>");
						$("#rmt_progressid").width(progress + "%");
						$("#rmt_showId").html(
								"<font color=\"blue\">" + show_msg + "</font>");

						/** 镜像已经处理完成* */
						if (progress == 100) {
							$(grid_selector).trigger("reloadGrid");
							/** 延迟三秒隐去远程制作窗口* */
							tailinWork();
						}
					} else {
						var progress = prog_msg[0];
						var show_msg = prog_msg[1];
						if (progress == "fail") {
							$("#prog_span")
									.html(
											"<font color=\"black\"><b>0&nbsp;%</b></font>");
							$("#rmt_progressid").width("0%");
							$("#rmt_showId").html(
									"<font color=\"red\"><b>" + show_msg
											+ "</b></font>");
						} else if (progress == "success") {
							$("#prog_span")
									.html(
											"<font color=\"black\"><b>100&nbsp;%</b></font>");
							$("#rmt_showId").html(
									"<font color=\"blue\"><b>" + show_msg
											+ "</b></font>");
						}
						/** 后台制作或者制作发布完毕镜像后，还原之前置灰的按钮* */
						// $('#rmt_make_submit').prop('disabled', false);
						$('#rmt_mkps_submit').prop('disabled', false);
						/** 延迟三秒隐去远程制作窗口* */
						tailinWork();
					}
				}
			}
		};
		socket2 = new WebSocket('ws://' + host + '/uploadService');
		socket2.onopen = function() {
		};
		socket2.onclose = function() {
		};
		socket2.onerror = function(e) {
		};
		socket2.onmessage = function(event) {
			var obj = JSON.parse(event.data);
			if (obj.messageType == "ws_up_file") {
				sendArraybuffer(obj);
			}
		};
	} else {
		console.log('Websocket not supported');
	}
}

function disconnect() {
	socket.close();
	socket2.close();
	console.log("Disconnected");
}
// 发送文件开始消息
function sendfileStart(name, appId, hostId) {
	socket2.send(JSON.stringify({
		'fileStartName' : name,
		'appId' : appId,
		'hostId' : hostId
	}));
}
// 发送取消上传文件的消息
function sendCancelMessage(file) {
	socket2.send(JSON.stringify({
		'cancel' : 'cancel'
	}));
}
// 发送文件上传消息
function sendfileEnd() {
	socket2.send(JSON.stringify({
		'sendover' : 'sendover'
	}));
}
// 显示通知函数
function showNotice(notice) {
	try {
		if (notice.success) {
			successNotice(notice.title, notice.message);
		} else {
			errorNotice(notice.title, notice.message);
		}
	} catch (e) {
	}
}

/* 远程制作镜像的收尾工作 */
function tailinWork() {
	setTimeout(function() {
		$('#RmtCrtImgModal').modal('hide');
		$('#remote_crtimg_frm')[0].reset();
	}, 5000);
}
// 发送数据包函数
function sendArraybuffer(obj) {
	if (obj.content == "OK") {
		if (endSize < file.size) {
			var blob;
			startSize = endSize;
			endSize += paragraph;
			if (file.webkitSlice) {
				blob = file.webkitSlice(startSize, endSize);
			} else if (file.mozSlice) {
				blob = file.mozSlice(startSize, endSize);
			} else {
				blob = file.slice(startSize, endSize);
			}
			var reader = new FileReader();
			reader.readAsArrayBuffer(blob);
			reader.onload = function loaded(evt) {
				var ArrayBuffer = evt.target.result;
				i++;
				var isok = (i / count) * 100;
				$("#showId").text("已经上传" + isok.toFixed(2) + "%");
				$("#progressid").width(isok.toFixed(2) + "%");
				if (cancelUpload) {
					$("#showId").text("上传文件已取消");
					startSize = 0;
					endSize = 0;
					i = 0;
					$("#progressid").width("0.00%");
					/** **2015年12月24日添加************************ */
					$('#upload-image_btn-text').text("上传");
					$('#upload_image_btn').removeClass("hide");
					/** *************************************** */
				} else {
					socket2.send(ArrayBuffer);
				}
			};
		} else {
			startSize = endSize = 0;
			i = 0;
			$("#showId").text("上传完成！");
			$("#progressid").width("100%");
			$(".btn-next").prop("disabled", false);
			sendfileEnd();
			$("#showId").text("已经上传完成，执行第二步：转存到仓库主机临时目录；请稍等...");
		}
	} else if (obj.content.indexOf("TRUE") >= 0) {
		var true_uuid = obj.content.split(",");
		folder_uuid = true_uuid[1];
		$("#showId").text("文件上传并转存完成！");
		current = 1;
		$('#upload_image_btn').addClass("hide");
	} else if (obj.content == "FALSE") {
		$("#showId").text("转存失败！");
	} else if (obj.content == "NOHOST") {
		$("#showId").text("仓库主机在数据库中不存在！");
	} else if (obj.content == "DBERROR") {
		$("#showId").text("数据保存异常！");
	} else if (obj.content == "UNLOADERROR") {
		/** @bug224: [镜像管理]上传镜像,当转存失败,请将失败的原因返回 * */
		$("#showId").text("转存失败，仓库主机网络连接异常！");
	} else if (obj.content == "CONNERROR") {
		$("#showId").text("仓库主机连接异常！");
	} else if (obj.content == "ISEXIT") {
		$("#showId").text("该文件已经存在！");
	} else if (obj.content == "DELTMPSUC") {
		$("#showId").text("删除仓库主机的暂存文件夹成功！");
	} else if (obj.content == "DELTMPEXP") {
		$("#showId").text("删除文件转存目录结果异常！");
	} else if (obj.content == "CANCEL") {
		/** 添加取消的处理函数部分，当远程仓库主机删除临时文件的情况下* */
		current = 0;
		$("#showId").text("仓库主机暂存文件已删除！");
		startSize = 0;
		endSize = 0;
		i = 0;
		$("#progressid").width("0.00%");
		/** **2015年12月24日添加************************ */
		$('#upload-image_btn-text').text("上传");
		$('#upload_image_btn').removeClass("hide");
		/** *************************************** */
	}
}
// 上传文件函数
function uploadImageFile() {
	// event.preventDefault();
	/** @bug77_begin 用户如果重新上传文件时，清空message的消息 */
	$('#imagemessage').text('');
	/** @bug77_finish 修复结束 */
	// var category = $('input[name="image_type"]:checked').val();
	var category = 'APP';
	var label = $('#upload-image_btn-text').text();
	if ("取消" == label) {
		/** 用户点击取消和上传为同一按钮，进行的处理 */
		file = document.getElementById("image_file").files[0];
		sendCancelMessage(file.name);
		$('#upload-image_btn-text').text("上传");
		cancelUpload = true;
		/** @bug170_begin 当上传成功后点击"取消上传",将前一次上传成功的结果及进度隐去 */
		$("#showId").text("上传文件已取消");
		$("#progressid").width("0.00%");
		/** @bug170_finish */
		return;
	}

	cancelUpload = false;
	upload = false;
	if ("BASIC" == category) {
		if ($("#registry_select").val() != null) {
			upload = true;
		}
	} else if ("APP" == category) {
		if ($("#app_select").val() != null
				&& $("#registry_select").val() != null) {
			upload = true;
		}
	}

	/** @bug166_begin 获取上传仓库主机的信息 */
	var comp = $("#registry_select").val();
	/** 判断用户是否选择合法的目标仓库 */
	if (comp == null || comp.indexOf(":") == -1) {
		showMessage("请选择需要推送的目标仓库！");
		return;
	}
	var ids = comp.split(":");
	var registryId = ids[0];
	var url = base + "registry/queryRegistryById";
	data = {
		registryId : registryId
	};

	$.post(url, data, function(response) {
		/** 目标仓库如果存在，直接提示并作返回处理 */
		if (!response.success) {
			showMessage("上传的目标仓库的不存在，请核对后上传文件！");
			return;
		} else {
			if (upload) {
				file = document.getElementById("image_file").files[0];
				if (undefined == file || null == file) {
					showMessage("请选择需要上传的文件");
					return;
				}
				var filename = file.name.split(".");
				/**
				 * @校验 上传文件名称校验
				 */
				if (!validateUploadFile(filename[0])) {
					return;
				}

				/**
				 * @bug76_begin
				 * @author yangqinglin
				 * @date 2015年11月5日
				 * @description 针对基础镜像和应用镜像分别进行文件名的后缀判断处理，基础镜像：tar格式；应用镜像：zip和tar.gz格式
				 */
				switch (category) {
				case ("BASIC"):
					if ("tar" != filename[filename.length - 1]) {
						showMessage("创建基础镜像，只能上传后缀名为tar格式的文件！");
						return;
					}
					break;
				/* 应用镜像只支持tar.gz和zip文件格式，不支持tar格式 */
				case ("APP"):
					if (("zip" != filename[filename.length - 1])
							&& ("tar.gz" != (filename[filename.length - 2]
									+ "." + filename[filename.length - 1]))) {
						showMessage("创建应用镜像，只能上传后缀名为zip或者tar.gz格式的文件！");
						return;
					}
					break;
				}
				/** @bug76_finish 针对镜像处理的分类处理修复结束* */

				if (file != null) {
					count = parseInt(file.size / paragraph) + 1;
					$("#pid").show();
					$("#showId").text("开始上传！");
					sendfileStart(file.name, $("#app_select").val(), $(
							"#registry_select").val().split(":")[1]);
					$('#upload-image_btn-text').text("取消");
				} else {
					showMessage("请先选择需要上传的文件！");
				}
			} else {
				/**
				 * @bug172_begin
				 * @bug173_begin 用户没有选择仓库或者所属的应用选项，提示相应信息*
				 */
				if ("BASIC" == category) {
					if ($("#registry_select").val() == null) {
						showMessage("请选择目标仓库主机！");
						return;
					}
				} else if ("APP" == category) {
					if ($("#registry_select").val() == null) {
						showMessage("请选择目标仓库主机！");
						return;
					} else if ($("#app_select").val() == null) {
						showMessage("请选择所属应用！");
						return;
					}
				}
				/**
				 * @bug172_finish
				 * @bug173_finish**
				 */
			}
		}
	});
	/** **@bug166_finish*************************** */
}

// 制作镜像函数
function makeImageFun() {
	file = $('#image_file').val().split('\\');
	filename = file[file.length - 1];
	var appId = $("#app_select").val();
	/** 获取应用相应的环境Id信息 */
	var activeLclEnvs = $('#activeLclEnv input[name="btn-env"]');
	var envIds = '';
	for (var envCount = 0, envLen = activeLclEnvs.length; envCount < envLen; envCount++) {
		envIds += $(activeLclEnvs[envCount]).val()
				+ (envCount + 1 == activeLclEnvs.length ? '' : ',');
	}

	// var imageName = $('#templatename').val();
	var imageName = $('#app_select>option:selected').text();
	var imageTag = $('#templateversion').val();
	var comp = $("#registry_select").val();
	// var category = $('input[name="image_type"]:checked').val();
	var category = "APP";
	var ids = comp.split(":");
	var registryId = ids[0];
	var hostId = ids[1];
	var imageId = $('#image_id').val();

	/** @bug166_begin 获取上传仓库主机的信息 */
	var check_url = base + "registry/queryRegistryById";
	var check_data = {
		registryId : registryId
	};

	$.post(check_url, check_data, function(response) {
		/** 目标仓库如果存在，直接提示并作返回处理 */
		if (!response.success) {
			showMessage("镜像的目标仓库的记录不存在，请核对后制作镜像！");
			return;
		} else {
			// if (validateImageName(imageName) && validateVersion(imageTag)) {
			if (validateVersion(imageTag)) {
				/** **********@bug169_begin:制作镜像任务提交后,临时将"制作"button置灰(不可点击),当制作失败将button释放(可用)************* */
				$('#make_image_btn').prop('disabled', true);
				/** **********@bug169_finish************* */
				$('#imagestatus').show();
				data = {
					appId : appId,
					envIds : envIds,
					imageTag : imageTag,
					imageName : imageName,
					hostId : hostId,
					registryId : registryId,
					fileName : filename,
					imageId : imageId,
					imageType : category,
					imageUuid : folder_uuid
				};
				$.post(base + 'image/make', data, function(response) {
					$('#imagemessage').removeClass("alert-warning");
					$('#imagemessage').addClass("alert-success");
				});
				$('#imagemessage').text('镜像制作任务已提交！');
			}
		}
	});
	/** **@bug166_finish*************************** */
}

// 上传文件名称校验
function validateUploadFile(filename) {
	var regex = /^[A-Za-z0-9_\-]+$/;
	if (!filename.match(regex)) {
		bootbox.alert("【上传文件】只能包含(字母、数字和下划线、破折号)合法字符");
		return false;
	}
	/* 判断镜像名称不能为空 */
	if (!filename || filename.length == 0) {
		bootbox.alert("【上传文件】不能为空值。");
		return false;
	}
	/* 判断文件名长度是否超长 */
	if (filename.length > 64) {
		bootbox.alert("【上传文件】名称长度不大于64个字符");
		return false;
	} else {
		return true;
	}
}

// 镜像名称校验函数
// function validateImageName(imagename) {
// /* 判断镜像名称不能为空 */
// /** @bug122_begin 镜像名称和镜像版本加入对非空的校验提示信息 */
// if (!imagename || imagename.length == 0) {
// /** @bug122_finish */
// bootbox.alert("【镜像名称】不能为空值。");
// return false;
// }
// var regex = /^[a-z0-9][a-z0-9_\.\-]+$/;
// if (!imagename.match(regex)) {
// bootbox.alert("【镜像名称】必须是以小写字母或数字起始，其后包含小写字母、数字、下划线、减号和小数点的字符串。");
// return false;
// }
// /* 判断文件名长度是否超长 */
// if (imagename.length > 64) {
// bootbox.alert("【镜像名称】长度不大于64个字符");
// return false;
// } else {
// return true;
// }
// }

// 版本校验函数
function validateVersion(version) {
	/* 判断镜像名称不能为空 */
	/** @bug122_begin 镜像名称和镜像版本加入对非空的校验提示信息 */
	if (!version || version.length == 0) {
		/** @bug122_finish */
		bootbox.alert("【镜像版本】不能为空值。");
		return false;
	}
	var regex = /^[A-Za-z0-9_\-\.]+$/;
	if (!version.match(regex)) {
		bootbox.alert("【镜像版本】只能包含(字母、数字、小数点和下划线、破折号)合法字符");
		return false;
	}
	/* 判断文件名称是否超长 */
	if (version.length > 64) {
		bootbox.alert("【镜像版本】长度不大于64个字符");
		return false;
	} else {
		return true;
	}
}

function changeModSelect() {
	category = $('input[name="mod_image_type"]:checked').val();
	switch (category) {
	case "BASIC":
		$('#mod_belong_app_div').css('display', 'none');
		$('#mod_belong_env_div').css('display', 'none');
		break;
	case "APP":
		$('#mod_belong_app_div').css('display', 'block');
		$('#mod_belong_env_div').css('display', 'block');
		break;
	default:
		break;
	}
}

/** 远程制作镜像的情况下，用户点击基础镜像或应用镜像的情况下，隐藏或者显示应用选择列表* */
// function changeRmtModSelect() {
// category = $('input[name="rmt_image_type"]:checked').val();
// switch (category) {
// case "BASIC":
// $('#rmtbelong_app_div').css('display', 'none');
// $('#rmtbelong_appenv').css('display', 'none');
// break;
// case "APP":
// $('#rmtbelong_app_div').css('display', 'block');
// $('#rmtbelong_appenv').css('display', 'block');
// break;
// default:
// break;
// }
// }
// function changeSelect() {
// category = $('input[name="image_type"]:checked').val();
// switch (category) {
// case "BASIC":
// /** 基础镜像情况下，隐藏应用和环境显示栏 */
// $('#make_image_app_id_div').css('display', 'none');
// $('#make_image_appenv_div').css('display', 'none');
// break;
// case "APP":
// /** 应用镜像情况下，显示应用和环境显示栏 */
// $('#make_image_app_id_div').css('display', 'block');
// $('#make_image_appenv_div').css('display', 'block');
// break;
// default:
// break;
// }
// }
$(document).ready(function() {
	if (window.File && window.FileReader && window.FileList && window.Blob) {
	} else {
		bootbox.alert('您的浏览器不支持html5 Web socket 上传功能.');
	}
});
function searchImage() {
	var imageName = $('#search_text').val();
	jQuery(grid_selector).jqGrid('setGridParam', {
		url : base + 'image/list?imageName=' + imageName
	}).trigger("reloadGrid");
}

/** 显示远程制作发布镜像窗口* */
function showRmtCrtImgModal(type) {
	// 设置标示 create：远程创建镜像 import：导入镜像
	$('#titleType').val(type);
	if (type == 'import') {
		$('#RmtCrtImgModal .rmtTitle').html('<b>版本导入</b>');
		remote_image_type = 'BASIC';
		$('#top_show').text('请选择tar文件。');
	} else {
		$('#RmtCrtImgModal .rmtTitle').html('<b>远程创建</b>')
		remote_image_type = 'APP';
		$('#top_show').text('请选择tar.gz或zip文件。');
	}
	/** 清空各个选择栏中的内容* */
	$('#rmt_reg_select').empty();
	$('#target_app').empty();
	// 注释掉显示选择仓库和应用的提示部分，直接插入包含的内容
	// $('#rmt_reg_select').append('<option value=\"0\">请选择镜像推送的目标仓库</option>');
	// $('#target_app').append('<option value=\"0\">请选择镜像所属的应用。</option>');
	/** 清空原始显示文件列表内容* */
	$('#showFolderFile').empty();
	/* 清空进度条及其提示信息 */
	$("#rmt_pid").hide();
	$("#rmt_showId").text("");
	/** 清空错误提示信息部分内容* */
	/** 首先隐藏通知栏进行操作* */

	$('#showRemoteInfo').html("");
	$('#showInfo').hide();
	$.post(base + 'registry/list?page=1&rows=65536', null, function(response) {
		if (response != "") {
			$.each(response.rows, function(index, obj) {
				var registryId = obj.registryId;
				var registryname = decodeURIComponent(obj.registryName);
				$('#rmt_reg_select').append(
						'<option value="' + registryId + '">' + registryname
								+ '</option>');
			});
		}
	}, 'json');

	/** 保存第一个应用Id和是否加载的布尔值 */
	var getFirstAppid = false;
	/** 获取应用全部类型 */
	$.ajax({
		type : 'post',
		url : base + 'app/all?page=1&rows=65536',
		data : {},
		dataType : 'json',
		success : function(array) {
			$('#target_app').html('<option value="0">请选择应用</option>');
			$.each(array.rows, function(index, obj) {
				var app_id = obj.appId;
				var app_name = decodeURIComponent(obj.appName);
				$('#target_app').append(
						'<option value="' + app_id + '">' + app_name
								+ '</option>');
			});
			/** 根据应用的ID获取对应的环境ID列表 */
			// console.log(firstAppid + ":" + getFirstAppid);
			imageLoadEnvs($('#target_app').val(), "rmt", "Rmt");
		}
	});
}

/** 本地和远程加载环境参数，应用ID，lcl|rmt,Lcl|Rmt */
function imageLoadEnvs(appId, lower, upper) {
	$('#blockRmtEnv').html('');
	$('#activeRmtEnv').html('');

	/** 请求应用下的所有环境列表 */
	$
			.ajax({
				type : 'post',
				url : base + 'app/getEnvsByAppId',
				data : {
					appid : appId
				},
				dataType : 'json',
				success : function(array) {
					$
							.each(
									array,
									function(index, obj) {
										var envId = obj.envId;
										var envName = decodeURIComponent(obj.envName);
										$('#block' + upper + 'Env')
												.append(
														'<label class="btn btn-round btn-white btn-primary">'
																+ '<input type="checkbox" name="btn-env" text="'
																+ envName
																+ '" value="'
																+ envId
																+ '"/>&nbsp;&nbsp;'
																+ envName
																+ '</label>');
									});
				}
			});
}

/** 查询远程文件夹的内容* */
function queryRemoteFolder() {
	/* 首先禁用查询远程文件夹内容按钮 */
	$('#show_rmt_files').prop('disabled', true);

	/** 清空原始显示文件列表内容* */
	$('#showFolderFile').empty();
	/** 隐藏向用户提示的信息* */
	$('#showInfo').hide();

	/** 分别获取主机ID和文件夹路径的值* */
	var host_ip = $("#source_ip").val();
	var host_user = $("#source_user").val();
	var host_passwd = $("#source_passwd").val();
	var folder_path = $("#source_path").val();

	/** 判断用户是否输入了正确的主机地址* */
	if (host_ip == undefined || host_ip == null || host_ip == "") {
		$('#showRemoteInfo')
				.html(
						"<font color=\"red\"><b>" + '请输入主机的IP地址。'
								+ "&nbsp;</b></font>");
		$('#showInfo').show();
		/** 查询远程文件夹按钮* */
		$('#show_rmt_files').prop('disabled', false);
		return;
	} else if (!isIp(host_ip)) {
		$('#showRemoteInfo').html(
				"<font color=\"red\"><b>" + '请按照正确的格式输入IP地址！'
						+ "&nbsp;</b></font>");
		$('#showInfo').show();
		/** 查询远程文件夹按钮* */
		$('#show_rmt_files').prop('disabled', false);
		return;
	}

	/** 判断用户是否输入了用户名称* */
	if (host_user == undefined || host_user == null || host_user == "") {
		$('#showRemoteInfo').html(
				"<font color=\"red\"><b>" + '请输入登录远程主机的用户名称！'
						+ "&nbsp;</b></font>");
		$('#showInfo').show();
		/** 查询远程文件夹按钮* */
		$('#show_rmt_files').prop('disabled', false);
		return;
	}

	/** 判断用户是否输入登陆密码* */
	if (host_passwd == undefined || host_passwd == null || host_passwd == "") {
		$('#showRemoteInfo').html(
				"<font color=\"red\"><b>" + '请输入登录远程主机的登陆密码！'
						+ "&nbsp;</b></font>");
		$('#showInfo').show();
		/** 查询远程文件夹按钮* */
		$('#show_rmt_files').prop('disabled', false);
		return;
	}

	/* 从远程机器获取文件夹路径下的内容 */
	/** image.js中制作镜像函数makeImageFun() */
	$
			.ajax({
				type : 'post',
				url : base + 'image/getFilelist',
				data : {
					hostIP : host_ip,
					hostUser : host_user,
					hostPasswd : host_passwd,
					filePath : folder_path
				},
				dataType : 'json',
				success : function(ret_json) {
					/** 返回array中只包含一个元素，获取返回码信息* */
					var ret_code = ret_json.retCode;
					var ret_message = ret_json.retMessage;

					/** 当请求文件列表成功的情况下* */
					if (ret_code == 0) {
						var file_array = ret_json.rfList;
						var ins_html = "<ul>";
						for (var count = 0, length = file_array.length; count < length; count++) {
							var fileName = file_array[count].fileName;
							var fileSuffix = file_array[count].fileSuffix;
							ins_html = ins_html
									+ "<li><input type=\"radio\" name=\"src_project\" value=\""
									+ fileName
									+ "\"/>&nbsp;<img src=\"..\/img\/icons\/"
									+ fileSuffix
									+ ".png\" width=\"16px\" height=\"16px\">&nbsp;"
									+ fileName + "<\/li>";
						}
						ins_html = ins_html + "</ul>";
						$('#showFolderFile').append(ins_html);
						/* 还原查询远程文件夹内容按钮 */
						$('#show_rmt_files').prop('disabled', false);
					} else {
						$('#showRemoteInfo').html(
								"<font color=\"red\"><b>" + ret_message
										+ "&nbsp;</b></font>");
						$('#showInfo').show();
						/* 还原查询远程文件夹内容按钮 */
						$('#show_rmt_files').prop('disabled', false);
					}
				}
			});
}

$(function() {
	// 移入或移除环境选择框（移入按钮id,移除按钮id，已选环境框id，未选环境框id）
	addOrRemoveEnv('add-rmtenv', 'remove-rmtenv', 'activeRmtEnv', 'blockRmtEnv');

	$('#target_app').change(function() {
		var appid = $('#target_app').val();
		// appid ,未选环境框id,已选环境框id
		getAppEnvs(appid, 'blockRmtEnv', 'activeRmtEnv')
	});

	$('#belong_app_select').change(function() {
		var appid = $('#belong_app_select').val();
		// appid ,未选环境框id,已选环境框id
		getAppEnvs(appid, 'update_blockEnv', 'update_activateEnv');
		getImageEnv(appid, 'update_activateEnv', 'update_blockEnv');

	});
	// ------------镜像修正部分---------------------------------------------
	// 移入或移除环境选择框（移入按钮id,移除按钮id，已选环境框id，备选环境框id）
	addOrRemoveEnv('update_add-env', 'update_remove-env', 'update_activateEnv',
			'update_blockEnv');
})

/** 本地制作镜像，添加环境处理 */
function localAddEnvs() {
	var blockEnvs = $('#blockLclEnv' + ' input[name="btn-env"]');
	var envs = new Array();
	for ( var i in blockEnvs) {
		if (blockEnvs[i].checked) {
			var env = $(blockEnvs[i]).parent('label');
			envs.push(env);
		}
	}
	if (envs.length <= 0) {
		showMessage("请选择要增加的环境");
		return;
	}
	for ( var i in blockEnvs) {
		if (blockEnvs[i].checked) {
			$(blockEnvs[i]).parent('label').remove();
		}
	}
	for ( var i in envs) {
		$('#activeLclEnv').append(envs[i]);
	}
	blockEnvs = document.getElementsByName("btn-env");
	for (i in blockEnvs) {
		blockEnvs[i].checked = false;
	}

}

/** 本地制作镜像移除出环境处理 */
function localRemoveEnvs() {
	// 获取某个固定div下的
	var activeEnvs = $('#activeLclEnv' + ' input[name="btn-env"]');
	var envs = new Array();
	for ( var i in activeEnvs) {
		if (activeEnvs[i].checked) {
			var env = $(activeEnvs[i]).parent('label');
			envs.push(env);
		}
	}
	if (envs.length <= 0) {
		showMessage("请选择要移除的环境");
		return;
	}
	for ( var i in activeEnvs) {
		if (activeEnvs[i].checked) {
			$(activeEnvs[i]).parent('label').remove();
		}
	}
	for ( var i in envs) {
		$('#blockLclEnv').append(envs[i]);
	}
	activeEnvs = document.getElementsByName("btn-env");
	for ( var i in activeEnvs) {
		activeEnvs[i].checked = false;
	}
}

/** 当本地的应用发生变化时，动态加载所包含的环境列表 */
function localAppGetEnvs() {
	$('#blockLclEnv').html('');
	$('#activeLclEnv').html('');
	var appid = $("#app_select").val();
	$
			.ajax({
				type : 'post',
				url : base + 'app/getEnvsByAppId',
				data : {
					appid : appid
				},
				dataType : 'json',
				success : function(array) {
					$
							.each(
									array,
									function(index, obj) {
										var envId = obj.envId;
										var envName = decodeURIComponent(obj.envName);
										$('#blockLclEnv')
												.append(
														'<label class="btn btn-round btn-white btn-primary">'
																+ '<input type="checkbox" name="btn-env" text="'
																+ envName
																+ '" value="'
																+ envId
																+ '">&nbsp;&nbsp;'
																+ envName
																+ '</label>');
									});
				}
			});
}

// add-env remove-env activateEnv blockEnv
function addOrRemoveEnv(addid, removeid, activeEnvid, blockEnvid) {
	// 环境约束
	$('#' + addid).click(function() {
		var blockEnvs = $('#' + blockEnvid + ' input[name="btn-env"]');
		var envs = new Array();
		for ( var i in blockEnvs) {
			if (blockEnvs[i].checked) {
				var env = $(blockEnvs[i]).parent('label');
				envs.push(env);
			}
		}
		if (envs.length <= 0) {
			showMessage("请选择要增加的环境");
			return;
		}
		for ( var i in blockEnvs) {
			if (blockEnvs[i].checked) {
				$(blockEnvs[i]).parent('label').remove();
			}
		}
		for ( var i in envs) {
			$('#' + activeEnvid).append(envs[i]);
		}
		blockEnvs = document.getElementsByName("btn-env");
		for (i in blockEnvs) {
			blockEnvs[i].checked = false;
		}
	});

	// 解除环境约束
	$('#' + removeid).click(function() {
		// 获取某个固定div下的
		var activeEnvs = $('#' + activeEnvid + ' input[name="btn-env"]');
		var envs = new Array();
		for ( var i in activeEnvs) {
			if (activeEnvs[i].checked) {
				var env = $(activeEnvs[i]).parent('label');
				envs.push(env);
			}
		}
		if (envs.length <= 0) {
			showMessage("请选择要移除的环境");
			return;
		}
		for ( var i in activeEnvs) {
			if (activeEnvs[i].checked) {
				$(activeEnvs[i]).parent('label').remove();
			}
		}
		for ( var i in envs) {
			$('#' + blockEnvid).append(envs[i]);
		}
		activeEnvs = document.getElementsByName("btn-env");
		for ( var i in activeEnvs) {
			activeEnvs[i].checked = false;
		}
	});
}

function getAppEnvs(appid, blockEnv,activeEnv) {
	$('#' + blockEnv).html('');
	$('#' + activeEnv).html('');
	// 获取备选环境列表
	$
			.ajax({
				type : 'post',
				url : base + 'app/getEnvsByAppId',
				data : {
					appid : appid
				},
				dataType : 'json',
				async : false,
				success : function(array) {
					$
							.each(
									array,
									function(index, obj) {
										var envId = obj.envId;
										var envName = decodeURIComponent(obj.envName);
										$('#' + blockEnv)
												.append(
														'<label class="btn btn-round btn-white btn-primary"><input type="checkbox" name="btn-env" text="'
																+ envName
																+ '" value="'
																+ envId
																+ '">&nbsp;&nbsp;'
																+ envName
																+ '</label>');
									});
				}
			});
}

function getImageEnv(appid, activeEnv, blockEnv) {
	$('#' + activeEnv).html('');
	// 获取已选环境列表
	var id = $('#mod_image_id').val();
	$
			.ajax({
				type : 'post',
				url : base + 'app/getEnvsByImageId',
				data : {
					appid : appid,
					imageid : id
				},
				dataType : 'json',
				async : false,
				success : function(array) {
					$
							.each(
									array,
									function(index, obj) {
										var envId = obj.envId;
										var envName = decodeURIComponent(obj.envName);
										var str = '<label class="btn btn-round btn-white btn-primary"><input type="checkbox" name="btn-env" text="'
												+ envName
												+ '" value="'
												+ envId
												+ '">&nbsp;&nbsp;'
												+ envName
												+ '</label>';
										$(
												'#'
														+ blockEnv
														+ ' input[name="btn-env"][value='
														+ envId + ']').parent()
												.remove();
										$('#' + activeEnv).append(str);
									});
				}
			});
}

// 修正初始化数据
function getEnvByImgId(blockEnv, activeEnv) {
	var id = $('#mod_image_id').val();
	var appid;
	$
			.ajax({
				type : 'post',
				url : base + 'app/getEnvByImgId',
				data : {
					imageid : id
				},
				dataType : 'json',
				async : false,
				success : function(array) {
					if (array.length > 0) {
						$
								.each(
										array,
										function(index, obj) {
											if (index == 0) {
												appid = obj.appId;
												getAppEnvs(appid, blockEnv,activeEnv);
											}
											var envId = obj.envId;
											var envName = decodeURIComponent(obj.envName);
											var str = '<label class="btn btn-round btn-white btn-primary"><input type="checkbox" name="btn-env" text="'
													+ envName
													+ '" value="'
													+ envId
													+ '">&nbsp;&nbsp;'
													+ envName + '</label>';
											$(
													'#'
															+ blockEnv
															+ ' input[name="btn-env"][value='
															+ envId + ']')
													.parent().remove();
											$('#' + activeEnv).append(str);
										});
						$('#belong_app_select').val(appid);
					}
				}
			});
}
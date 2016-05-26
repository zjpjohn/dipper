var grid_selector = "#lb_list";
var page_selector = "#lb_page";
/** 保存当前高级查询条件的列数量 */
var advanceColNum = 0;

jQuery(function($) {
    $(window).on('resize.jqGrid',
    function() {
        $(grid_selector).jqGrid('setGridWidth', $(".page-content").width());
        $(grid_selector).closest(".ui-jqgrid-bdiv").css({
            'overflow-x': 'hidden'
        });
    });
    var parent_column = $(grid_selector).closest('[class*="col-"]');
    $(document).on('settings.ace.jqGrid',
    function(ev, event_name, collapsed) {
        if (event_name === 'sidebar_collapsed' || event_name === 'main_container_fixed') {
            setTimeout(function() {
                $(grid_selector).jqGrid('setGridWidth', parent_column.width());
            },
            0);
        }
    });
    jQuery(grid_selector).jqGrid({
        url: base + 'loadbalance/list',
        datatype: "json",
        height: '100%',
        autowidth: true,
        colNames: ['ID', '名称', '主服务器', '主服务器IP', '配置文件位置', '备用服务器', '备用主机IP', '配置文件位置', '描述信息', '创建人', '创建时间', '快捷操作'],
        colModel: [{
            name: 'lbId',
            index: 'lbId',
            width: 10,
            hidden: true
        },
        {
            name: 'lbName',
            index: 'lbName',
            width: 11,
            formatter: function(cellvalue, options, rowObject) {
                return '<a href="' + base + 'loadbalance/detail/' + rowObject.lbId + '.html">' + cellvalue + '</a>';
            }
        },
        {
            name: 'lbMainHost',
            index: 'lbMainHost',
            width: 8,
            hidden: true
        },
        {
            name: 'lbMainHostIP',
            index: 'lbMainHostIP',
            width: 11
        },
        {
            name: 'lbMainConf',
            index: 'lbMainConf',
            width: 15
        },
        {
            name: 'lbBackupHost',
            index: 'lbBackupHost',
            width: 8,
            hidden: true
        },
        {
            name: 'lbBackupHostIP',
            index: 'lbBackupHostIP',
            width: 11
        },
        {
            name: 'lbBackupConf',
            index: 'lbBackupConf',
            width: 15
        },
        {
            name: 'lbDesc',
            index: 'lbDesc',
            width: 18
        },
        {
            name: 'lbCreatorName',
            index: 'lbCreatorName',
            width: 6
        },
        {
            name: 'lbCreatetime',
            index: 'lbCreatetime',
            width: 14
        },
        {
            name: '',
            title:false,
            index: '',
            width: 140,
            fixed: true,
            sortable: false,
            resize: false,
            formatter: function(cellvalue, options, rowObject) {
                var strHtml = "";

                var upda = $("#update_loadbalance").val();
                if (typeof(upda) != "undefined") {
                    strHtml += "<button class=\"btn btn-round btn-xs btn-primary\" onclick=\"modifyBalance('" + rowObject.lbId + "','" + rowObject.lbName + "','" + rowObject.lbMainHost + "','" + rowObject.lbMainConf + "','" + rowObject.lbBackupHost + "','" + rowObject.lbBackupConf + "','" + rowObject.lbDesc + "')\"><span class=\"glyphicon glyphicon-edit\"></span><b>编辑</b></button> &nbsp;";
                }
                var dele = $("#delete_loadbalance").val();
                if (typeof(dele) != "undefined") {
                    strHtml += "<button class=\"btn btn-xs btn-round btn-inverse\" onclick=\"removeSingleBalance('" + rowObject.lbId + "','" + rowObject.lbName + "')\"><span class=\"glyphicon glyphicon-remove\"></span><b>删除</b></button> &nbsp;";
                }
                return strHtml;
            }
        }],
        viewrecords: true,
        rowNum: 10,
        rowList: [10, 20, 50, 100, 1000],
        pager: page_selector,
        altRows: true,
        multiselect: true,
        jsonReader: {
            root: "rows",
            total: "total",
            page: "page",
            records: "records",
            repeatitems: false
        },
        loadComplete: function() {
            var table = this;
            setTimeout(function() {
                styleCheckbox(table);
                updateActionIcons(table);
                updatePagerIcons(table);
                enableTooltips(table);
            },
            0);
        }
    });
    $(window).triggerHandler('resize.jqGrid'); // 窗口resize时重新resize表格，使其变成合适的大小
    jQuery(grid_selector).jqGrid( // 分页栏按钮
    'navGrid', page_selector, { // navbar options
        edit: false,
        add: false,
        del: false,
        search: false,
        refresh: true,
        refreshstate: 'current',
        refreshicon: 'ace-icon fa fa-refresh',
        view: false
    },
    {},
    {},
    {},
    {},
    {});

    function updateActionIcons(table) {}
    function updatePagerIcons(table) {
        var replacement = {
            'ui-icon-seek-first': 'ace-icon fa fa-angle-double-left bigger-140',
            'ui-icon-seek-prev': 'ace-icon fa fa-angle-left bigger-140',
            'ui-icon-seek-next': 'ace-icon fa fa-angle-right bigger-140',
            'ui-icon-seek-end': 'ace-icon fa fa-angle-double-right bigger-140'
        };
        $('.ui-pg-table:not(.navtable) > tbody > tr > .ui-pg-button > .ui-icon').each(function() {
            var icon = $(this);
            var $class = $.trim(icon.attr('class').replace('ui-icon', ''));
            if ($class in replacement) icon.attr('class', 'ui-icon ' + replacement[$class]);
        })
    }

    function enableTooltips(table) {
        $('.navtable .ui-pg-button').tooltip({
            container: 'body'
        });
        $(table).find('.ui-pg-div').tooltip({
            container: 'body'
        });
    }

    function styleCheckbox(table) {}

    /**
	 * Get application list
	 * 
	 */
    getApplist();

    

    /**
	 * Search balance by name
	 */
    $("#search").click(function() {
        var searchName = $('#search_name').val();
        jQuery(grid_selector).jqGrid('setGridParam', {
            url: base + 'loadbalance/list?lbName=' + searchName
        }).trigger("reloadGrid");
    });

    /**
	 * Reload balance
	 */
    $("#reload").on('click',
    function(event) {
        var ids = $(grid_selector).jqGrid("getGridParam", "selarrrow");
        var lbIds = new Array();
        for (var i = 0; i < ids.length; i++) {
            var rowData = $(grid_selector).jqGrid("getRowData", ids[i]);
            lbIds[i] = rowData.lbId;
        }
        if (lbIds.length == 0) {
            showMessage("请选择要更新的负载！");
            return;
        }
        url = base + 'loadbalance/reload';
        $("#lbIds").val(lbIds);
        data = {
            lbIds: lbIds.join(","),
            fileFlag: 0
        };
        $.post(url, data,function(response) {
        	$('#showLBConfModal').modal('show');
            if (response != "") {
                var fileNames = response.message.split("#");
                for (var i = 0; i < fileNames.length; i++) {
                    if (fileNames[i] != "") {
                        $('#confList').append('<a style="font-size:14px;cursor:pointer;" onclick="readConfFile(' + "'" + fileNames[i] + "'" + ')">' + fileNames[i] + '</a>').append('&nbsp;&nbsp;&nbsp;<a style="font-size:14px;cursor:pointer;" onclick="readConfFile(' + "'" + fileNames[i] + "'" + ')">显示</a>').append('&nbsp;&nbsp;&nbsp;<a style="font-size:14px;cursor:pointer;" onclick="packup()">收起</a>').append('<br/>');
                    }
                }
            }
        });
    });

    /**
	 * cancel show nginx conf
	 */
    $('#conf_cancel').click(function(event) {
        $('#showLBConfModal').modal('hide');
        $('#confContent').empty();
        $('#confList').empty();
        $(grid_selector).trigger("reloadGrid");
        $("#appIds").val("");
    });

    /**
	 * reload nginx
	 */
    $('#conf_submit').click(function(event) {
        lbIds = $("#lbIds").val();
        appIds = $("#appIds").val();
        $('#showLBConfModal').modal('hide');
        if (appIds == "") {
            url = base + 'loadbalance/reload';
            data = {
                lbIds: lbIds,
                fileFlag: 1
            };
            //显示遮罩层
            showMask();
            //提示信息显示
            $('#spinner-message font').html("负载更新中,请稍等....");
            $.post(url, data, function(response) {
            	//隐藏遮罩层
            	hideMask();
            	if (response == "") {
                    showMessage("重新加载负载均衡异常！");
                } else {
                    showMessage(response.message);
                }
            	$(grid_selector).trigger("reloadGrid");
                $("#appIds").val("");
            });
        } else {
            url = base + 'loadbalance/addApp';
            data = {
                appIds: appIds,
                lbId: lbIds,
                fileFlag: 1
            };
            //显示遮罩层
            showMask();
            //提示信息显示
            $('#spinner-message font').html("应用加入负载中,请稍等....");
            $.post(url, data,function(response) {
            	//隐藏遮罩层
            	hideMask();
                if (response == "") {
                    showMessage("应用加入负载异常！");
                } else {
                    showMessage(response.message);
                }
                $(grid_selector).trigger("reloadGrid");
                $("#appIds").val("");
            });
        }
        $('#confContent').empty();
        $('#confList').empty();
    });

    /**
	 * cancel show nginx conf
	 */
    $('#remove_conf_cancel').click(function(event) {
        $('#removeLBConfModal').modal('hide');
        $('#removeconfContent').empty();
        $('#removeconfList').empty();
        $(grid_selector).trigger("reloadGrid");
        $("#appIds").val("");
    });

    /**
	 * reload nginx
	 */
    $('#remove_conf_submit').click(function(event) {
        lbIds = $("#lbIds").val();
        appIds = $("#appIds").val();
        $('#removeLBConfModal').modal('hide');
        if (appIds == "") {
            url = base + 'loadbalance/reload';
            data = {
                lbIds: lbIds,
                fileFlag: 1
            };
            //显示遮罩层
            showMask();
            //提示信息显示
            $('#spinner-message font').html("负载更新中,请稍等....");
            $.post(url, data,
            function(response) {
            	//隐藏遮罩层
            	hideMask();
            	if (response == "") {
                    showMessage("重新加载负载均衡异常！");
                    $(grid_selector).trigger("reloadGrid");
                } else {
                    showMessage(response.message,
                    function() {
                        $(grid_selector).trigger("reloadGrid");
                    });
                }
                $("#appIds").val("");
            });
        } else {
            url = base + 'loadbalance/removeApp';
            data = {
                appIds: appIds,
                lbId: lbIds,
                fileFlag: 1
            };
            //显示遮罩层
            showMask();
            //提示信息显示
            $('#spinner-message font').html("应用移出负载中,请稍等....");
            $.post(url, data, function(response) {
            	//隐藏遮罩层
            	hideMask();
                if (response == "") {
                    showMessage("应用移出负载异常！");
                } else {
                    showMessage(response.message);
                }
                $(grid_selector).trigger("reloadGrid");
                $("#appIds").val("");
            });
        }
        
        $('#removeconfContent').empty();
        $('#removeconfList').empty();
    });

    /**
	 * Remove load balance
	 */
    $("#remove").on('click',
    function(event) {
        var ids = $(grid_selector).jqGrid("getGridParam", "selarrrow");
        var infoList = "";
        var lbIds = new Array();
        for (var i = 0; i < ids.length; i++) {
            var rowData = $(grid_selector).jqGrid("getRowData", ids[i]);
            //lbIds[i] = (lbIds + rowData.lbId) + (((i + 1)== ids.length) ? '':',');
            lbIds[i] = rowData.lbId;
            infoList = (infoList + "[lb-" + rowData.lbName + "]") + (((i + 1) == ids.length) ? '': ',');
        }
        if (lbIds.length == 0) {
            showMessage("请选择要删除的负载！");
            return;
        }
        bootbox.dialog({
            message: '<div class="alert alert-info" style="margin:10px"><span class="glyphicon glyphicon-info-sign"></span>&nbsp;删除负载&nbsp;' + infoList + '?</div>',
            title: "提示",
            buttons: {
            	cancel: {
                    label: "<i class='icon-info'></i> <b>取消</b>",
                    className: "btn-sm btn-danger btn-round",
                    callback: function() {}
                },
                main: {
                    label: "<i class='icon-info'></i><b>确定</b>",
                    className: "btn-sm btn-success btn-round",
                    callback: function() {
                        removeLB(lbIds.join(','));
                    }
                }
            }
        });
    });

    /**
	 * Create load balance
	 */
    $('#submit').click(function(e) {
        e.preventDefault();
        if ($("#add_balance_form").valid()) {
            url = base + 'loadbalance/create';
            id = $('#balance_id').val();
            name = $('#balance_name').val();
            mainhost = $('#main_host').val();
            mainconf = $('#main_conf').val();
            backuphost = $('#backup_host').val();
            backupconf = $('#backup_conf').val();
            desc = $('#balance_desc').val();
            if (mainhost == 0) {
                showMessage("主服务器不能为空！");
                return;
            }
            if (mainhost == backuphost) {
                showMessage("主服务器和备用服务器不能是同一台服务器！");
                return;
            }
            if (backuphost == 0 && backupconf != "") {
                showMessage("备用服务器及其文件不能为空！");
                return;
            }else if(backuphost != 0 && backupconf == ""){
            	showMessage("备用服务器或者其文件不能为空！");
                return;
            }

            if (backuphost == 0) {
                backuphost = null;
            }
            $('#addLBModal').modal('hide');
            data = {
                lbId: id,
                lbName: name,
                lbMainHost: mainhost,
                lbMainConf: mainconf,
                lbBackupHost: backuphost,
                lbBackupConf: backupconf,
                lbDesc: desc
            };
            $.post(url, data,
            function(response) {
                if (response == "") {
                    showMessage("创建负载均衡异常！");
                } else {
                    showMessage(response.message);
                }
                $('#add_balance_form')[0].reset();
                $(grid_selector).trigger("reloadGrid");
            });
        }
    });

    /**
	 * Cancel add LB
	 */
    $('#cancel').click(function() {
        $('#addLBModal').modal('hide');
        $('#add_balance_form')[0].reset();
        $('label.error').remove();
    });

    $(".close").click(function() {
        $('#add_balance_form')[0].reset();
        $('label.error').remove();
    });

    /**
	 * Modify load balance
	 */
    $('#modify').click(function() {
        if ($("#modify_balance_frm").valid()) {
            url = base + 'loadbalance/modify';
            id = $('#balance_id_edit').val();
            name = $('#balance_name_edit').val();
            mainhost = $('#main_host_edit').val();
            mainconf = $('#main_conf_edit').val();
            backuphost = $('#backup_host_edit').val();
            backupconf = $('#backup_conf_edit').val();
            desc = $('#balance_desc_edit').val();
            if (mainhost == backuphost) {
                showMessage("主服务器和备用服务器不能是同一台服务器！");
                return;
            }
            if (backuphost == 0 && backupconf != "") {
                showMessage("备用服务器或者其文件不能为空！");
                return;
            }else if(backuphost != 0 && backupconf == ""){
            	showMessage("备用服务器或者其文件不能为空！");
                return;
            }
            $('#modifyLBModal').modal('hide');
            data = {
                lbId: id,
                lbName: name,
                lbMainHost: mainhost,
                lbMainConf: mainconf,
                lbBackupHost: backuphost,
                lbBackupConf: backupconf,
                lbDesc: desc
            };
            $.post(url, data, function(response) {
                if (response == "") {
                    showMessage("更新负载均衡异常！");
                } else {
                    showMessage(response.message);
                }
                $("#modify_balance_frm")[0].reset();
                $(grid_selector).trigger("reloadGrid");
            });
        }
    });

    /**
	 * Cancel modify LB
	 */
    $('#modifyLBModal .close,#modify_cancel').click(function() {
        $('#modifyLBModal').modal('hide');
        $('label.error').remove();
        $('#modify_balance_frm')[0].reset();
    });

    /**
	 * Validate create balance form
	 */
    $('#add_balance_form').validate({
        rules: {
            balance_name: {
                required: true,
                stringNameCheck: true,
                maxlength: 64,
                remote: {
                    url: base + "loadbalance/checkName",
                    type: "post",
                    dataType: "json",
                    data: {
                        lbName: function() {
                            return $("#balance_name").val();
                        }
                    },
                    dataFilter: function(data) { // 判断控制器返回的内容
                        if (data == "true") {
                            return true;
                        } else {
                            return false;
                        }
                    }
                }
            },
            main_host: {
                isInteger: true
            },
            main_conf: {
                required: true
            },
            balance_desc: {
                maxlength: 200,
                stringCheck: true
            }

        },
        messages: {
            balance_name: {
                required: "负载均衡名称不能为空",
                maxlength: $.validator.format("负载均衡名称不能大于64个字符"),
                remote: "负载名称已经存在，请重新输入！"
            },
            main_host: {
                isInteger: "请选择主服务器"
            },
            main_conf: {
                required: "配置文件不能为空"
            },
            balance_desc: {
                maxlength: $.validator.format("描述信息不能大于200个字符")
            }
        }
    });

    /**
	 * Validate modify balance form
	 */
    $("#modify_balance_frm").validate({
        rules: {
            balance_name_edit: {
                required: true,
                stringNameCheck: true,
                maxlength: 64,
                remote: {
                    url: base + "loadbalance/checkName",
                    type: "post",
                    dataType: "json",
                    data: {
                    	lbName: function() {
                            return $("#balance_name_edit").val();
                        }
                    },
                    dataFilter: function(data) { // 判断控制器返回的内容
                        if (data == "true") {
                            return true;
                        } else {
                        	var name=$("#balance_name_edit").val();
                        	var oldname=$('#balance_oldname_edit').val();
                        	if(oldname==name){
                        		return true;
                        	}else{
                        		return false;
                        	}
                        }
                    }
                }
            },
            main_host_edit: {
                isInteger: true
            },
            main_conf_edit: {
                required: true
            },
            balance_desc_edit: {
                maxlength: 200,
                stringCheck: true
            }

        },
        messages: {
            balance_name_edit: {
                required: "负载均衡名称不能为空",
                maxlength: $.validator.format("负载均衡名称不能大于64个字符"),
                remote: "负载名称已经存在，请重新输入！"
            },
            main_host_edit: {
                isInteger: "请选择主服务器"
            },
            main_conf_edit: {
                required: "配置文件不能为空"
            },
            balance_desc_edit: {
                maxlength: $.validator.format("描述信息不能大于200个字符")
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
    $("#advanced_search").on('click',
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
        jQuery(grid_selector).jqGrid('setGridParam', {
            url: base + 'loadbalance/advancedSearch?params=' + column_array + '&values=' + value_array
        }).trigger("reloadGrid");

				/** @bug152_begin 清空用户多选的参数 */
				while ($("#params li").length > 1) {
					$("#remove-param").parent().remove();
				}
				/** @bug152_finish */
				
				$('#advancedSearchLoadBalanceModal').modal('hide');
				$('#advanced_search_frm')[0].reset();
			});


    /**
	 * 向高级搜索的取消按钮添加重置隐藏
	 */
    $("#advanced_cancel").on('click',
    function(event) {
        event.preventDefault();
        $('#advancedSearchLoadBalanceModal').modal('hide');
        $('#advanced_search_frm')[0].reset();
    });
});

/**
 * @author yangqinglin
 * @datetime 2015年11月3日 11:23
 * @description 添加高级查询函数
 */
function AdvancedSearchLoadbalance() {
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
	$('#advancedSearchLoadBalanceModal').modal('show');
}

/**
 * Read nginx conf file name
 */
function readConfFile(fileName) {
    url = base + 'loadbalance/readConfFile';
    data = {
        fileName: fileName
    };
    $.post(url, data,
    function(response) {
        $('#confContent').html(response);
    });

}

/**
 * Read nginx conf file name
 */
function readRemoveConfFile(fileName) {
    url = base + 'loadbalance/readConfFile';
    data = {
        fileName: fileName
    };
    $.post(url, data, function(response) {
        $('#removeconfContent').html(response);
    });

}

/**
 * Add application to load balance
 */
function addApp() {
    var ids = $(grid_selector).jqGrid("getGridParam", "selarrrow");
    var lbIds = new Array();
    for (var i = 0; i < ids.length; i++) {
        var rowData = $(grid_selector).jqGrid("getRowData", ids[i]);
        lbIds[i] = rowData.lbId;
    }
    if (lbIds.length == 0) {
        showMessage("请选中负载，再添加应用");
        return;
    } else if (lbIds.length > 1) {
        showMessage("应用只能添加到一个负载");
        return;
    }
    getApplist();
    var message = $('#add_application').html();
    $('#add_application').empty();
    url = base + 'loadbalance/addApp';
    title = '<i class="ace-icon fa fa-pencil-square-o bigger-125"></i>&nbsp;<b>添加应用</b>';
    bootbox.dialog({
        title: title,
        message: message,
        buttons: {
        	"cancel": {
                "label": "<i class='icon-info'></i> <b>取消</b>",
                "className": "btn-sm btn-danger btn-round",
                "callback": function() {
                    $('#add_application').html(message);
                }
            },
            "success": {
                "label": "<i class='icon-ok'></i> <b>确定</b>",
                "className": "btn-sm btn-success btn-round",
                "callback": function() {
                    var appId = $('#app_select option:selected').val();
                    if (appId == 0) {
                        showMessage("请选择添加的应用！");
                    } else {
                        $('#appIds').val(appId);
                        $("#lbIds").val(lbIds);
                        data = {
                            appIds: appId,
                            lbId: lbIds.join(","),
                            fileFlag: 0
                        };
                        $.post(url, data,function(response) {
                            $('#showLBConfModal').modal('show');
                            if (response != "") {
                                var fileNames = response.message.split("#");
                                for (var i = 0; i < fileNames.length; i++) {
                                    if (fileNames[i] != "") {
                                        $('#confList').append('<a style="font-size:14px;cursor:pointer;" onclick="readConfFile(' + "'" + fileNames[i] + "'" + ')">' + fileNames[i] + '</a>').append('&nbsp;&nbsp;&nbsp;<a style="font-size:14px;cursor:pointer;" onclick="readConfFile(' + "'" + fileNames[i] + "'" + ')">显示</a>').append('&nbsp;&nbsp;&nbsp;<a style="font-size:14px;cursor:pointer;" onclick="packup()">收起</a>').append('<br/>');
                                    }
                                }
                            } else {
                                $('#confList').append('<span>未生成配置文件！</span>')
                            }
                            //getRemoveApplist();
                            $('#add_application').html(message);
                        });
                    }
                }
            }
        },
        onEscape: function() {
            $('#add_application').html(message);
        }
    });
}

/**
 * Add application to load balance
 */
function delApp() {
    var ids = $(grid_selector).jqGrid("getGridParam", "selarrrow");
    var lbIds = new Array();
    for (var i = 0; i < ids.length; i++) {
        var rowData = $(grid_selector).jqGrid("getRowData", ids[i]);
        lbIds[i] = rowData.lbId;
    }
    if (lbIds.length == 0) {
        showMessage("请选中负载，再移除应用");
        return;
    } else if (lbIds.length > 1) {
        showMessage("应用只能从一个负载中移除！");
        return;
    }

    var message = $('#remove_application').html();
    getRemoveApplist(lbIds.join(','));
    $('#remove_application').empty();
    url = base + 'loadbalance/removeApp';
    title = '<i class="ace-icon fa fa-pencil-square-o bigger-125"></i>&nbsp;<b>移除应用</b>';
    bootbox.dialog({
        title: title,
        message: message,
        buttons: {
        	 "cancel": {
                 "label": "<i class='icon-info'></i> <b>取消</b>",
                 "className": "btn-sm btn-danger btn-round",
                 "callback": function() {
                     $('#remove_application').html(message);
                 }
             },
            "success": {
                "label": "<i class='icon-ok'></i> <b>确定</b>",
                "className": "btn-sm btn-success btn-round",
                "callback": function() {
                    var appId = $('#remove_app_select option:selected').val();
                    if (appId == 0) {
                        showMessage("请选择移除的应用！");
                    } else {
                        $('#appIds').val(appId);
                        $("#lbIds").val(lbIds);
                        data = {
                            appIds: appId,
                            lbId: lbIds.join(","),
                            fileFlag: 0
                        };
                        $.post(url, data,
                        function(response) {
                            $('#removeLBConfModal').modal('show');
                            if (response != "") {
                                var fileNames = response.message.split("#");
                                for (var i = 0; i < fileNames.length; i++) {
                                    if (fileNames[i] != "") {
                                        $('#removeconfList').append('<a style="font-size:14px;cursor:pointer;" onclick="readRemoveConfFile(' + "'" + fileNames[i] + "'" + ')">' + fileNames[i] + '</a>').append('&nbsp;&nbsp;&nbsp;<a style="font-size:14px;cursor:pointer;" onclick="readRemoveConfFile(' + "'" + fileNames[i] + "'" + ')">显示</a>').append('&nbsp;&nbsp;&nbsp;<a style="font-size:14px;cursor:pointer;" onclick="packupRemove()">收起</a>').append('<br/>');
                                    }
                                }
                            } else {
                                $('#removeconfList').append('<span>未生成配置文件！</span>')
                            }
                            getApplist();
                            $('#remove_application').html(message);
                        });
                    }
                }
            }
        },
        onEscape: function() {
            $('#remove_application').html(message);
        }
    });
}

/**
 * show modify balance form
 */
function modifyBalance(id, name, mainHost, mainConf, backupHost, backupConf, desc) {
	//获取主机列表
	getHostlist();
	//获取该负载的主服务器和备份服务器的信息
	 $.ajax({
	        type: 'get',
	        url: base + 'loadbalance/hostOfLBId',
	        data: {
	            id: id
	        },
	        dataType: 'json',
	        success: function(data) {
	        	if(data==""){
	        		$("#main_host_edit").html("");
	        		$('#backup_conf_edit').html('');
	        	}else{
	        		if(data.mainhostId!=undefined){
	        			$("#main_host_edit").html('<option value="'+data.mainhostId+ '" selected>' + data.mainhostName + '</option>');
	        		}else{
	        			$("#main_host_edit").prepend('<option value="0" selected>选择服务器</option>');
	        		}
	        		if(data.backhostId!=undefined){
	        			$("#backup_host_edit").prepend('<option value="'+data.backhostId+ '" selected>' + data.backhostName+ '</option>');
	        		}else{
	        			$("#backup_host_edit").prepend('<option value="0" selected>选择服务器</option>');
	        		}
	        	}
	        }
	    });
	//编辑负载中DOM元素赋值
    $('#balance_id_edit').val(id);
    $('#balance_name_edit').val(name);
    $('#balance_oldname_edit').val(name);
    $("#main_host_edit").find("option[value='" + mainHost + "']").attr("selected", true);
    $('#main_conf_edit').val(mainConf);
    $('#main_host_edit').attr("disabled", true);
    $('#main_conf_edit').attr("disabled", true);
    $("#backup_host_edit").find("option[value='" + backupHost + "']").attr("selected", true);
    $('#backup_conf_edit').attr("value", backupConf == "null" ? "": backupConf);
    $('#balance_desc_edit').text(desc == "null" ? "": desc);
    $('#modifyLBModal').modal('show');
}

/**
 * Remove single load balance
 */
function removeSingleBalance(lbId, lbName) {
    bootbox.dialog({
        message: '<div class="alert alert-info" style="margin:10px"><span class="glyphicon glyphicon-info-sign"></span>&nbsp;删除负载&nbsp;lb-' + lbName + '?</div>',
        title: "提示",
        buttons: {
        	cancel: {
                label: "<i class='icon-info'></i> <b>取消</b>",
                className: "btn-sm btn-danger btn-round",
                callback: function() {}
            },
            main: {
                label: "<i class='icon-info'></i><b>确定</b>",
                className: "btn-sm btn-success btn-round",
                callback: function() {
                    removeLB(lbId);
                }
            }
        }
    });
}

/**
 * Remove load balance
 */
function removeLB(lbIds) {
    url = base + 'loadbalance/remove';
    data = {
        lbIds: lbIds
    };
    $.post(url, data, function(response) {
        showMessage(response.message);
        $(grid_selector).trigger("reloadGrid");
    });
}

function getRemoveApplist(balanceId) {
    $('#remove_app_select').empty();
    $('#remove_app_select').append('<option value="0">请选择应用</option>');
    $.ajax({
        type: 'get',
        url: base + 'app/appInLb',
        data: {
            balanceId: balanceId
        },
        dataType: 'json',
        success: function(array) {
            $.each(array,
            function(index, obj) {
                var appid = obj.appId;
                var appname = decodeURIComponent(obj.appName);
                $('#remove_app_select').append('<option value="' + appid + '">' + appname + '</option>');
            });
        }
    });
}

function getApplist() {
    $('#app_select').empty();
    $('#app_select').append('<option value="0">请选择应用</option>');
    $.ajax({
        type: 'get',
        url: base + 'app/appNotInLb',
        dataType: 'json',
        success: function(array) {
            $.each(array,
            function(index, obj) {
                var appid = obj.appId;
                var appname = decodeURIComponent(obj.appName);
                $('#app_select').append('<option value="' + appid + '">' + appname + '</option>');
            });
        }
    });
}

function packup() {
    $('#confContent').empty();
}

function packupRemove() {
    $('#removeconfContent').empty();
}

//新增负载
function show_addLBModal(){
	$('#addLBModal').modal("show");
	getHostlist();
}
/**
 * Get nginx list
 */
function getHostlist() {
	$('#main_host').html('<option value="0">选择服务器</option>');
	$('#backup_host').html('<option value="0">选择服务器</option>');
	 $('#main_host_edit').html("");
	 $('#backup_host_edit').html("");
    $.ajax({
        type: 'get',
        url: base + 'loadbalance/listHost',
        dataType: 'json',
        async:false,
        success: function(array) {
            $.each(array,
            function(index, obj) {
                var hostId = obj.hostId;
                var hostName = decodeURIComponent(obj.hostName);
                $('#main_host').append('<option value="' + hostId + '">' + hostName + '</option>');
                $('#main_host_edit').append('<option value="' + hostId + '">' + hostName + '</option>');
                $('#backup_host').append('<option value="' + hostId + '">' + hostName + '</option>');
                $('#backup_host_edit').append('<option value="' + hostId + '">' + hostName + '</option>');
            });
        }
    });
}
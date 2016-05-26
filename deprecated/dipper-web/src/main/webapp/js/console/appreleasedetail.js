var grid_selector = "#container_list";
var page_selector = "#container_page";
jQuery(function($) {
    var options = {
        bootstrapMajorVersion: 3,
        currentPage: 1,
        totalPages: 1,
        numberOfPages: 0,
        onPageClicked: function(e, originalEvent, page) {
            if (page == "next") {
                page = parseInt($('#currentPage').val()) + 1;
            } else {
                page = parseInt($('#currentPage').val()) - 1;
            }
            var appId = $('#app_select option:selected').val();
            getImageList(page, 5, appId);
        },
        shouldShowPage: function(type, page, current) {
            switch (type) {
            case "first":
            case "last":
                return false;
            default:
                return true;
            }
        }
    };
    $('#tplpage').bootstrapPaginator(options);
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
    var appId = $('#appId').val();
    var imgid = $('#imageId').val();
    var balanceId = $('#balanceId').val();
    /** 根据应用的ID查询应用的名称信息 */
    $.ajax({
        type: 'get',
        url: base + 'app/getOne',
        data: {
            appid: appId
        },
        dataType: 'json',
        success: function(response) {
            // $('#application_name').prepend(response.appName);
            $('#application_name').text(response.appName);
        }
    });

    jQuery(grid_selector).jqGrid({
        url: base + 'container/listConInfoByAppid',
        data: {
            appId: appId,
            imageId: imgid
        },
        datatype: "json",
        height: '100%',
        autowidth: true,
        colNames: ['ID', '应用名称', '应用版本', '实例标识', '', '运行状态', '健康状态', '', '监控状态', '主机IP','端口', '备注', '创建时间', '操作'],
        colModel: [{
            name: 'conId',
            index: 'conId',
            width: 10,
            hidden: true
        },
        {
            name: 'appName',
            index: 'appName',
            width: 8
        },
        {
            name: 'imageTag',
            index: 'imageTag',
            width: 8
        },
        {
            name: 'conUuid',
            index: 'conUuid',
            width: 10,
            formatter: function(cellvalue, options, rowObject) {
                return '<a href="' + base + 'container/detail/'+appId+'/'+imgid+'/'+balanceId+'/'+ rowObject.conId + '.html"> c-' + cellvalue.substr(0, 8) + '</a>';
            }
        },
        {
            name: 'conPower',
            index: 'conPower',
            width: 8,
            hidden: true
        },
        {
            name: 'power',
            index: 'power',
            width: 8,
            formatter: function(cellvalue, options, rowObject) {
                switch (rowObject.conPower) {
                case 0:
                    return '<i class="fa fa-stop text-danger">&nbsp; 已停止</i>';
                case 1:
                    return '<i class="fa fa-play-circle text-success"><b> &nbsp;运行中<b></i>';
                }
            }
        },
        {
            name: 'appstatus',
            index: 'appstatus',
            width: 8,
            formatter: function(cellvalue, options, rowObject) {
                switch (rowObject.appStatus) {
                case 0:
                    return '<i class="fa fa-stop text-danger">&nbsp; 关闭</i>';
                case 1:
                    return '<i class="fa fa-play-circle text-success"><b> &nbsp;健康<b></i>';
                case 2:
                    return '<i class="fa fa-stop text-warning">&nbsp; 异常</i>';
                case 3:
                	return '<i class="fa fa-stop text-warning">&nbsp; 未检测</i>';
                }
            }
        },
        {
            name: 'appStatus',
            index: 'appStatus',
            width: 10,
            hidden: true
        },
        {
            name: 'monitorstatus',
            index: 'monitorstatus',
            width: 8,
            formatter: function(cellvalue, options, rowObject) {
                switch (rowObject.monitorStatus) {
                case 0:
                    return '<i class="fa fa-stop text-danger">&nbsp; 未监控</i>';
                case 1:
                    return '<i class="fa fa-play-circle text-success"><b> &nbsp;监控中<b></i>';
                case 2:
                	return '<i class="fa fa-stop text-warning">&nbsp; 未添加</i>';
                }
            }
        },
        {
            name: 'conIp',
            index: 'conIp',
            width: 10
        },
        {
            name: 'conPortInfo',
            index: 'conPortInfo',
            width: 10
        },
        {
            name: 'conDesc',
            index: 'conDesc',
            width: 10
        },
        {
            name: 'conCreatetime',
            index: 'conCreatetime',
            width: 10
        },
        {
            name: '',
            title: false,
            index: '',
            width: 130,
            fixed: true,
            sortable: false,
            resize: false,
            formatter: function(cellvalue, options, rowObject) {
                var button = '';
                var strHtml = "";
                var stop = $("#stop_container").val();
                var start = $("#start_container").val();

                if (typeof(stop) != "undefined" && rowObject.conPower == 1) {
                    button = "&nbsp;<button class=\"btn btn-round btn-warning btn-xs\" onclick=\"stopSingleContainer('" + rowObject.conId + "','" + rowObject.conUuid + "',this)\"><span class=\"glyphicon glyphicon-stop\"></span>停止</button> &nbsp;";
                } else if (typeof(start) != "undefined" && rowObject.conPower == 0) {
                    button = "&nbsp;<button class=\"btn btn-round btn-xs btn-success\" onclick=\"startSingleContainer('" + rowObject.conId + "','" + rowObject.conUuid + "','" + rowObject.appStatus + "',this)\"><span class=\"glyphicon glyphicon-play\"></span>启动</button> &nbsp;";
                }
                var dele = $("#delete_container").val();
                if (typeof(dele) != "undefined") {
                    strHtml += "<button class=\"btn btn-xs btn-round btn-danger\" onclick=\"removeSingleContainer('" + rowObject.conId + "','" + rowObject.conUuid + "','" + rowObject.conPower + "',this)\"><span class=\"glyphicon glyphicon-remove\"></span>删除</button> &nbsp;";
                }
                return button + strHtml;
            }
        }],
        viewrecords: true,
        postData: {
            appId: appId,
            imageId:imgid
        },
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
    jQuery(grid_selector).jqGrid('navGrid', page_selector, {
        edit: false,
        add: false,
        del: false,
        search: false,
        searchicon: 'ace-icon fa fa-search',
        refresh: true,
        refreshstate: 'current',
        refreshicon: 'ace-icon fa fa-refresh',
        view: false
    },
    {
        // search form
        recreateForm: true,
        afterShowSearch: function(e) {
            var form = $(e[0]);
            form.closest('.ui-jqdialog').find('.ui-jqdialog-title').wrap('<div class="widget-header" />');
            style_search_form(form);
        },
        afterRedraw: function() {
            style_search_filters($(this));
        },
        multipleSearch: true
    });

    function style_search_filters(form) {
        form.find('.delete-rule').val('X');
        form.find('.add-rule').addClass('btn btn-xs btn-primary');
        form.find('.add-group').addClass('btn btn-xs btn-success');
        form.find('.delete-group').addClass('btn btn-xs btn-danger');
    }
    function style_search_form(form) {
        var dialog = form.closest('.ui-jqdialog');
        var buttons = dialog.find('.EditTable');
        buttons.find('.EditButton a[id*="_reset"]').addClass('btn btn-sm btn-round btn-info').find('.ui-icon').attr('class', 'ace-icon fa fa-retweet');
        buttons.find('.EditButton a[id*="_query"]').addClass('btn btn-sm btn-round btn-inverse').find('.ui-icon').attr('class', 'ace-icon fa fa-comment-o');
        buttons.find('.EditButton a[id*="_search"]').addClass('btn btn-sm btn-round btn-purple').find('.ui-icon').attr('class', 'ace-icon fa fa-search');
    }

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
        });
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
    


//    $("#search").click(function() {
//        var searchName = $('#search_name').val();
//        jQuery(grid_selector).jqGrid('setGridParam', {
//            url: base + 'container/list?conName=' + searchName
//        }).trigger("reloadGrid");
//    });

    /**
	 * Start container
	 */
    $("#start").on('click',
    function(event) {
        var ids = $(grid_selector).jqGrid("getGridParam", "selarrrow");
        var infoList = "";
        for (var i = 0; i < ids.length; i++) {
            var rowData = $(grid_selector).jqGrid("getRowData", ids[i]);
            if (rowData.conPower != 0) {
                showMessage("容器 " + rowData.conUuid + "已经启动, 请重新选择！");
                return;
            }
            infoList += rowData.conUuid + " ";
        }
        if (infoList != "") {
            bootbox.dialog({
                message: '<div class="alert alert-info" style="margin:10px"><span class="glyphicon glyphicon-info-sign"></span>&nbsp;启动容器&nbsp;' + infoList + '?</div>',
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
                            var conids = new Array();
                            for (var i = 0; i < ids.length; i++) {
                                var rowData = $(grid_selector).jqGrid("getRowData", ids[i]);
                                conids = (conids + rowData.conId) + (((i + 1) == ids.length) ? '': ',');
                            }
                            startContainer(conids);
                        }
                    }
                }
            });
        } else {
            showMessage("请选中要启动的容器！");
            return;
        }
    });

    /**
	 * stop container
	 */
    $("#stop").on('click',
    function(event) {
        var ids = $(grid_selector).jqGrid("getGridParam", "selarrrow");
        var infoList = "";
        for (var i = 0; i < ids.length; i++) {
            var rowData = $(grid_selector).jqGrid("getRowData", ids[i]);
            if (rowData.conPower != 1) {
                showMessage("容器 " + rowData.conUuid + "已经停止, 请重新选择！");
                return;
            }
            infoList += rowData.conUuid + " ";
        }
        var balanceId = $('#balanceId').val();
        var stopMessage = '';
        if(parseInt(balanceId) == -1){
        		stopMessage = '&nbsp;停止容器&nbsp;' + infoList + '？';
        }else{
        	   stopMessage = '&nbsp;停止容器&nbsp;' + infoList + ',是否更新负载？';
           }
        if (infoList != "") {
            bootbox.dialog({
                message: '<div class="alert alert-info" style="margin:10px"><span class="glyphicon glyphicon-info-sign"></span>'+stopMessage+'</div>',
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
                            var conids = new Array();
                            for (var i = 0; i < ids.length; i++) {
                                var rowData = $(grid_selector).jqGrid("getRowData", ids[i]);
                                conids = (conids + rowData.conId) + (((i + 1) == ids.length) ? '': ',');
                            		 }
                            if(parseInt(balanceId) == -1){
                            	stopContainer(conids);
                    		    }else{
                    			   reloadBalance(conids,1);
                            		 }
                        }
                    }
                }
            });
        } else {
            showMessage("请选中要停止的容器！");
            return;
        }
    });

    /**
	 * remove container
	 */
    $("#trash").on('click',
    function(event) {
        var ids = $(grid_selector).jqGrid("getGridParam", "selarrrow");
        var infoList = "";
        for (var i = 0; i < ids.length; i++) {
            var rowData = $(grid_selector).jqGrid("getRowData", ids[i]);
            if (rowData.conPower == 1) {
                showMessage("容器 " + rowData.conUuid + "没有停止, 不能删除！");
                return;
            }
            infoList += rowData.conUuid + " ";
        }
        if (infoList != "") {
            bootbox.dialog({
                message: '<div class="alert alert-info" style="margin:10px"><span class="glyphicon glyphicon-info-sign"></span>&nbsp;删除容器&nbsp;' + infoList + '?</div>',
                title: "提示",
                buttons: {
                	cancel: {
                        label: "<i class='icon-info'></i> <b>取消</b>",
                        className: "btn-sm btn-danger btn-round",
                        callback: function() {
                            location.reload();
                        }
                    },
                    main: {
                        label: "<i class='icon-info'></i><b>确定</b>",
                        className: "btn-sm btn-success btn-round",
                        callback: function() {
                            var conids = new Array();
                            for (var i = 0; i < ids.length; i++) {
                                var rowData = $(grid_selector).jqGrid("getRowData", ids[i]);
                                conids = (conids + rowData.conId) + (((i + 1) == ids.length) ? '': ',');
                            }
                            trashContainer(conids);
                        }
                    }
                }
            });
        } else {
            showMessage("请选中要删除的容器！");
        }
    });

    /**
	 * sync container
	 */
    $('#sync').click(function() {
        url = base + 'container/sync';
        $('.well').append('<div class="icon-spinner">' + '<i id = "spinner" class="ace-icon fa fa-spinner fa-spin blue bigger-225"></i>' + '</div>');
        $.get(url,
        function(response) {
            $('div.icon-spinner').remove();
            if (response == "") {
                showMessage("容器同步异常！");
                $(grid_selector).trigger("reloadGrid");
            } else {
                showMessage(response.message,
                function() {
                    $(grid_selector).trigger("reloadGrid");
                });
            }
        });
    })

    /**
	 * reload nginx
	 */
    $('#conf_submit').click(function(event) {
        conIds = $("#conIds").val();
        url = base + 'loadbalance/reloadApp';
        data = {
            conIds: conIds,
            fileFlag: 1
        };
        $('div.well').append('<div class="icon-spinner" style="float:right;">' + '<i id = "spinner" class="ace-icon fa fa-spinner fa-spin blue bigger-225"></i>' + '</div>');
        $.post(url, data,
        function(response) {
            $('#showLBConfModal').modal('hide');
            if (response == "") {
                showMessage("更新负载均衡异常！");
                $('div.icon-spinner').remove();
                location.reload();
            } else {
                showMessage(response.message,
                function() {
                    $('div.icon-spinner').remove();
                    location.reload();
                });
            }
        });
        $('#confContent').empty();
        $('#confList').empty();
    });
    
    /**
	 * 添加高级搜索的参数项
	 */
	$("#ci_add-param").on('click', function(event) {
		event.preventDefault();
		$("#ci_params li:first").clone(true).appendTo("#ci_params");
		$("#ci_params li").not(":first").find("#ci_remove-param").show();
		$("#ci_params li:first").find("#ci_remove-param").hide();
		var str = $("#ci_params li:last").find("#ci_meter").val();
		/** @bug152_begin 新增查询参数时，新增栏参数内容置空 */
		$("#ci_params li:last").find("#ci_param_value").val("");
		/** @bug152_finish */
	});


    /**
	 * 删除高级搜索的参数项
	 */
    $("#ci_remove-param").on('click', function(event) {
        event.preventDefault();
		if ($("#ci_params li").length > 1) {
			$(this).parent().remove();
		}
    });

    /**
	 * 向查询按钮添加请求提交操作 删除高级索索的参数项
	 */
    $("#ci_advanced_search").on('click',
    function(event) {
        /* 保存各项栏目的名称数组 */
        var column_array = new Array();
        /* 保存用户填写的各项信息数组 */
        var value_array = new Array();
        /*获取应用的ID值*/
        var appId = $('#appId').val();

        /* 获取选择栏目的名称 */
        $("select[name=ci_meter]").each(function() {
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
        $("input[name=ci_param_value]").each(function() {
            value_array.push($(this).val());
        });
        
        /* 查询是否存在关键词相关的结果 */
        jQuery(grid_selector).jqGrid('setGridParam', {
            url: base + 'container/advancedSearch?params=' + column_array + '&values=' + value_array + "&app_id=" + appId
        }).trigger("reloadGrid");

				/** @bug152_begin 清空用户多选的参数 */
				while ($("#ci_params li").length > 1) {
					$("#ci_remove-param").parent().remove();
				}
				/** @bug152_finish */

				$('#advanSearchCIModal').modal('hide');
				$('#advanced_search_ci_frm')[0].reset();
			});


    /**
	 * 向高级搜索的取消按钮添加重置隐藏
	 */

    $("#ci_advanced_cancel").on('click',
    function(event) {
        event.preventDefault();
        $('#advanSearchCIModal').modal('hide');
        $('#advanced_search_ci_frm')[0].reset();
    });

});

/**
 * Start single container
 */
function startSingleContainer(conid, conUuid, appstatus, obj) {
    if (appstatus == 2) {
        showMessage('容器中应用状态异常，不允许启动！');
        return;
    }
    bootbox.dialog({
        message: '<div class="alert alert-info" style="margin:10px"><span class="glyphicon glyphicon-info-sign"></span>&nbsp;启动容器&nbsp;c-' + conUuid.substr(0, 8) + '?</div>',
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
                    $(obj).attr("disabled", true);
                    startContainer(conid);
                }
            }
        }
    });
}

/**
 * Start container
 */
function startContainer(conidArray) {
	//显示遮罩层
    showMask();
    //提示信息显示
    $('#spinner-message font').html("应用实例启动中,请稍等....");
    $('#conIds').val(conidArray);
    var balanceId = $('#balanceId').val();
    url = base + 'container/start';
    data = {
        containerids: conidArray
    };
    $.get(url, data, function(response){
    	//隐藏遮罩层
    	hideMask();
    	if (response == "") {
            showMessage("应用实例启动异常！");
        } else {
            if (response.success) {
        			if(balanceId != -1){
        				bootbox.dialog({
                            message: '<div class="alert alert-info" style="margin:10px"><span class="glyphicon glyphicon-info-sign"></span>&nbsp;启动成功，是否更新负载均衡&nbsp;' + '?</div>',
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
                                        reloadBalance(conidArray,0);
                                    }
                                }
                            }
                        });
        			}else{
            			showMessage("应用启动成功！");
            		}
                
            } else {
                showMessage(response.message);
            }
        }
    	$(grid_selector).trigger("reloadGrid");
    });
}

/**
 * Stop single container
 */
function stopSingleContainer(conid, conUuid, obj) {
	var balanceId = $('#balanceId').val();
    var stopMessage = '';
    if(parseInt(balanceId) == -1){
    		stopMessage = '&nbsp;停止容器&nbsp;' + conUuid.substr(0, 8) + '？';
    }else{
    	   stopMessage = '&nbsp;停止容器&nbsp;' + conUuid.substr(0, 8) + ',是否更新负载？';
       }
    bootbox.dialog({
        message: '<div class="alert alert-info" style="margin:10px"><span class="glyphicon glyphicon-info-sign"></span>'+stopMessage+'</div>',
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
                    $(obj).attr("disabled", true);
                    if(parseInt(balanceId) == -1){
                    	  stopContainer(conid);
		              }else{
	                	  reloadBalance(conid,1);
		                   }
                    //
                }
            }
        }
    });
}

/**
 * Stop container
 */
function stopContainer(conidArray) {
	url = base + 'container/stop';
	data = {containerids: conidArray};
	var balanceId = $('#balanceId').val();
	//显示遮罩层
    showMask();
    //提示信息显示
    $('#spinner-message font').html("应用实例停止中,请稍等....");
	$.get(url, data, function(response){
		//隐藏遮罩层
    	hideMask();
    	if (response == "") {
            showMessage("应用实例停止异常！");
        } else {
            if (response.success) {
            	 if(balanceId != -1){
            		 bootbox.dialog({
                         message: '<div class="alert alert-info" style="margin:10px"><span class="glyphicon glyphicon-info-sign"></span>&nbsp;停止成功，是否更新负载均衡&nbsp;' + '?</div>',
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
	                                     reloadBalance(conidArray,1);
	                                 }
	                             }
	                         }
	                     });
            	 }else{
            	 		 showMessage("应用停止成功");
            	 	 }
                
            } else {
                showMessage(response.message);
            }
        }
        $(grid_selector).trigger("reloadGrid");
	});
}

/**
 * Remove single container
 */
function removeSingleContainer(conid, conUuid, conPower, obj) {
    if (conPower == 1) {
        showMessage("容器 c-" + conUuid.substr(0, 8) + "没有停止, 不能删除！");
        return;
    }
    bootbox.dialog({
        message: '<div class="alert alert-info" style="margin:10px"><span class="glyphicon glyphicon-info-sign"></span>&nbsp;删除容器&nbsp;c-' + conUuid.substr(0, 8) + '?</div>',
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
                    $(obj).attr("disabled", true);
                    trashContainer(conid);
                }
            }
        }
    });
}

/**
 * Trash container
 */
function trashContainer(conidArray) {
	url = base + 'container/trash';
	data = {containerids: conidArray};
	//显示遮罩层
    showMask();
    //提示信息显示
    $('#spinner-message font').html("应用实例删除中,请稍等....");
	$.get(url, data, function (response) {
		//隐藏遮罩层
    	hideMask();
    	if (response == "") {
            showMessage("应用实例删除异常！");
        } else {
            showMessage(response.message);
        }
    	$(grid_selector).trigger("reloadGrid");
	});
    /*$.ajax({
        type: 'get',
        url: base + 'container/trash',
        data: {
            containerids: conidArray
        },
        dataType: 'json',
        success: function(response) {
            $('.icon-spinner').remove();
            if (response == "") {
                showMessage("删除容器异常！");
                $(grid_selector).trigger("reloadGrid");
            } else {
                showMessage(response.message,
                function() {
                    $(grid_selector).trigger("reloadGrid");
                });
            }
        },
        error: function(response) {
            $('.icon-spinner').remove();
            if (response == "") {
                showMessage("删除容器异常！");
                $(grid_selector).trigger("reloadGrid");
            } else {
                showMessage(response.message,
                function() {
                    $(grid_selector).trigger("reloadGrid");
                });
            }
        }
    });*/
}

/**
 * Modify page status
 * 
 * @param current
 * @param total
 */
function modalPageUpdate(current, total) {
    $('#currentPtpl').html(current);
    $('#totalPtpl').html(total);
}

function reloadBalance(conIds, actionFlag) {
    $.ajax({
        type: 'post',
        url: base + 'loadbalance/reloadApp',
        data: {
            conIds: conIds,
            fileFlag: 0,
            actionFlag:actionFlag
        },
        dataType: 'json',
        success: function(response) {
            if (response == "") {
                showMessage("重新加载负载均衡异常！");
                $(grid_selector).trigger("reloadGrid");
            } else {
                var fileNames = response.message.split("#");
                var confList = '<div class="col-sm-9" style="margin-left:-20px;" id="confList">'
                for (var i = 0; i < fileNames.length; i++) {
                    if (fileNames[i] != "") {
                        confList += '<a style="font-size:14px;cursor:pointer;">' + fileNames[i] + '</a>' + '&nbsp;&nbsp;&nbsp;<a style="font-size:14px;cursor:pointer;" onclick="readConfFile(' + "'" + fileNames[i] + "'" + ')">显示</a>' + '&nbsp;&nbsp;&nbsp;<a style="font-size:14px;cursor:pointer;" onclick="packup()">收起</a>';
                    }
                }
                confList += '</div>';
                bootbox.dialog({
                    message: '<div class="alert alert-info" style="margin:10px"><span class="glyphicon glyphicon-info-sign"></span>&nbsp;查看配置文件，是否继续更新负载均衡&nbsp;' + '?' + '<div class="well" id="content" style="margin-top:1px;">' + '<form class="form-horizontal" role="form" id="modify_balance_frm">' + '<div class="form-group" >' + '<label class="col-sm-3" style="text-align:right;"><b>配置文件：</b></label>' + confList + '</div>' + '<div class="form-group">' + '<label class="col-sm-3" style="text-align:right;"><b>文件内容：</b></label>' + '<div class="col-sm-9" style="margin-left:-20px;">' + '<p id="confContent"></p>' + '</div>' + '</div>' + '</form>' + '</div>' + '</div>',
                    title: "提示",
                    buttons: {
                    	cancel: {
                            label: "<i class='icon-info'></i> <b>取消</b>",
                            className: "btn-sm btn-danger btn-round",
                            callback: function() {
                                $(grid_selector).trigger("reloadGrid");
                            }
                        },
                        main: {
                            label: "<i class='icon-info'></i><b>继续</b>",
                            className: "btn-sm btn-success btn-round",
                            callback: function() {
                                url = base + 'loadbalance/reloadApp';
                                data = {
                                    conIds: conIds,
                                    fileFlag: 1,
                                    actionFlag:actionFlag
                                };
                                //显示遮罩层
                        showMask();
                                //提示信息显示
                    	   if(actionFlag == 1){
                    		   $('#spinner-message font').html("负载更新，停止容器进行中，请稍等....");
                    	    }else{
                    	    	 $('#spinner-message font').html("负载更新中，请稍等....");
                        	    }
                        $.post(url, data, function(response) {
                            	//隐藏遮罩层
                        hideMask();
                        if (response == "") {
                            showMessage("重新加载负载均衡异常！");
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

        }
    });
}

/**
 * @author yangqinglin
 * @datetime 2016年3月7日
 * @description 添加查询容器实例
 */
function SearchContainerIns() {
	/*获取搜索的内容和容器所属的应用ID*/
	var searchAppName = $('#search_name').val();
	var appId = $('#appId').val();
	
	/* 查询是否存在关键词相关的结果 */
	jQuery(grid_selector).jqGrid('setGridParam', {
		url : base + 'container/listSearch?search_name=' + searchAppName+"&appId="+appId
	}).trigger("reloadGrid");
}

/**
 * @author yangqinglin
 * @datetime 2016年3月7日
 * @description 添加对于容器实例的高级查询
 */
function ASContainerIns(){
	/** @bug152_begin 清空用户多选的参数 */
	while ($("#ci_params li").length > 1) {
		$("#ci_remove-param").parent().remove();
	}
	/* 隐藏高级查询第一行的删除打叉按钮 */
	$("#ci_params li:first").find("#ci_remove-param").hide();
	/** 打开高级搜索窗口，之前输入全部清空 */
	$("#ci_params li:first").find("#param_value").val("");
	$("#ci_params li:first").find("#ci_meter").val("0");
	/** @bug152_finish */
	$('#advanSearchCIModal').modal('show');
}

/*刷新本页，重新获取全部容器列表*/
function showAllContainers(){
	self.location.reload(); 
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

function packup() {
    $('#confContent').empty();
}
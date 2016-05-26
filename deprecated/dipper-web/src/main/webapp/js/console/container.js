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

    $('#app_select').on('change',
    function() {
        var appId = $('#app_select option:selected').val();
        if (appId == '请选择应用') {
            appId = 0;
        }
        getImageList(1, 5, appId);
    });

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
        url: base + 'container/list',
        datatype: "json",
        height: '100%',
        autowidth: true,
        colNames: ['ID', 'UUID', '名称', '依赖镜像', '', '容器状态','应用状态','','监控状态', '应用', '端口', '备注', '创建时间', '操作'],
        colModel: [{
            name: 'conId',
            index: 'conId',
            width: 10,
            hidden: true
        },
        {
            name: 'conUuid',
            index: 'conUuid',
            width: 10,
            formatter: function(cellvalue, options, rowObject) {
                return '<a href="' + base + 'container/detail/' + rowObject.conId + '.html"> c-' + cellvalue.substr(0, 8) + '</a>';
            }
        },
        {
            name: 'conName',
            index: 'conName',
            width: 15
        },
        {
            name: 'conImgid',
            index: 'conImgid',
            width: 15,
            hidden: true
        },
        {
            name: 'conPower',
            index: 'conPower',
            width: 10,
            hidden: true
        },
        {
            name: 'power',
            index: 'power',
            width: 10,
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
            width: 10,
            formatter: function(cellvalue, options, rowObject) {
                switch (rowObject.appStatus) {
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
        	name: 'appStatus',
        	index: 'appStatus',
        	width: 10,
        	hidden:true
        },
        {
            name: 'monitorstatus',
            index: 'monitorstatus',
            width: 10,
            formatter: function(cellvalue, options, rowObject) {
                switch (rowObject.monitorStatus) {
                case 0:
                    return '<i class="fa fa-stop text-danger">&nbsp; 未监控</i>';
                case 1:
                    return '<i class="fa fa-play-circle text-success"><b> &nbsp;监控中<b></i>';
                }
            }
        },
        {
            name: 'appId',
            index: 'appId',
            width: 15,
            hidden: true
        },
        {
            name: 'port',
            index: 'port',
            width: 20,
            formatter: function(cellvalue, options, rowObject) {
                return getPortInfos(rowObject.conId);
            }
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
            title:false,
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
    current = 0;

    function getPortInfos(conId) {
        var ports = "";
        $.ajax({
            type: 'get',
            url: base + 'container/listPort',
            data: {
                conId: conId
            },
            async: false,
            dataType: 'json',
            success: function(array) {
                $.each(array,
                function(index, obj) {
                    portInfo = obj.conIp + ":" + obj.pubPort + "-->" + obj.priPort + (((index + 1) == array.length) ? '': '\n');
                    ports += portInfo;
                });
            }
        });
        return ports;
    }

    /**
	 * Next page, Pre page
	 */
    currentStep = 0;
    $('#modal-wizard .modal-header').ace_wizard().on('change',
    function(e, info) {
        if (info.step == 1) { // 第一步需要执行的操作
            var appId = $('#app_select option:selected').val();
            var imageid = $('.imagelist').find('.selected').attr("imageid");
            if (appId == 0) {
                e.preventDefault();
                showMessage("请选择应用, 再进行下一步操作！");
                return;
            }
            if (imageid == undefined) {
                e.preventDefault();
                showMessage("请选择镜像，再进行下一步操作！");
                return;
            }
            getClusters(appId);
        } else if (info.step == 2) { // 第二步需要执行的操作
            if (info.direction == 'next') {
                if ($('#cluster_select option:selected').val() == 0) {
                    e.preventDefault();
                    showMessage("请选择集群，再进行下一步操作！");
                    return;
                }
                conNum = $('#container_num').val().trim();
                if (conNum == "") {
                    e.preventDefault();
                    showMessage("容器数量不能为空！");
                    return;
                } else if (conNum == 0) {
                    e.preventDefault();
                    showMessage("容器数量不能为0");
                    return;
                } else if (!isInteger(conNum)) {
                    e.preventDefault();
                    showMessage("容器数量不是正整数，请重新输入！");
                    return;
                }
                conName = $('#container_name').val().trim();
                if (conName == "") {
                    e.preventDefault();
                    showMessage("容器名称不能为空！");
                    return;
                } else if (!isSpecialChar(conName)) {
                    e.preventDefault();
                    showMessage("容器名称中不能包含特殊字符");
                    return;
                }
                //获取配置模板信息
                getTemplates();
            } else if (info.direction = 'previous') {
            }
        }else if(info.step == 3){
        	//添加模板信息
        	getParams();
        }
    }).on('finished',
    function(e) { // 最后一步需要执行的操作
        var appId = $('#app_select option:selected').val();
        var clusterId = $('#app_select option:selected').val();
        var imageid = $('.imagelist').find('.selected').attr("imageid");
        var imageName = $('.imagelist').find('.selected').attr("imagename");
        var clusterId = $('#cluster_select option:selected').val();
        var createModel = $('#create_mode option:selected').val();
        var conNumber = $('#container_num').val();
        var conName = $('#container_name').val();
        var conDesc = $('#container_desc').val();
        var createParams = "";
        $.each($("#temp_params li"), function(){
        	var pk = $(this).find("#tp_key").attr("paramkey");
        	var pConn = $(this).find("#tp_key").attr("paramConnector");
        	var pv = $(this).find("#tp_value").val();
        });
        return;
        $.each($("#params li"),
        function() {
            var paramName = $(this).find("#meter option:selected").val();
            var paramValue = $(this).find("#param_value").val();
            if (paramName != 0) {
                if (paramName.indexOf("-v") != -1) {
                    var tempParam = paramName + " " + paramValue;
                    var temParams = new Array();
                    temParams = tempParam.split(":");
                    if (temParams.length > 1) {
                        temParams[0] = temParams[0] + "/tempDir" + ":";
                    } else {
                        showMessage("日志挂载参数不合法,请检查参数，参数格式 -v 主机目录：容器目录");
                        return;
                    }
                    createParams += temParams[0] + temParams[1] + " ";
                } else {
                    createParams += paramName + " " + paramValue + " ";
                }
            }
        });
        $('.well').append('<div class="icon-spinner">' + '<i id = "spinner" class="ace-icon fa fa-spinner fa-spin blue bigger-225"></i>' + '</div>');
        $.ajax({
            type: 'post',
            url: base + 'container/create',
            dataType: 'json',
            data: {
                appId: appId,
                imageId: imageid,
                imageName: imageName,
                clusterId: clusterId,
                createModel: createModel,
                conNumber: conNumber,
                conName: conName,
                conDesc: conDesc,
                createParams: createParams
            },
            success: function(response) {
                $('.icon-spinner').remove();
                if (response == "") {
                    showMessage("创建容器失败：服务器异常",
                    function() {
                        $(grid_selector).trigger("reloadGrid");
                        location.reload();
                    });
                } else {
                    if (response.success) {
                        $(grid_selector).trigger("reloadGrid");
                        bootbox.dialog({
                            message: '<div class="alert alert-info" style="margin:10px"><span class="glyphicon glyphicon-info-sign"></span>&nbsp;创建成功，是否更新负载均衡&nbsp;' + '?</div>',
                            title: "提示",
                            buttons: {
                                main: {
                                    label: "确定",
                                    className: "btn-success btn-round",
                                    callback: function() {
                                        reloadBalance(response.message);
                                    }
                                },
                                cancel: {
                                    label: "取消",
                                    className: "btn-danger btn-round",
                                    callback: function() {
                                        location.reload();
                                    }
                                }
                            }
                        });
                    } else {
                        showMessage(response.message,
                        function() {
                            location.reload();
                        });
                    }
                }
            },
            error: function(response) {
                $('.icon-spinner').remove();
                showMessage("创建容器失败：服务器异常",
                function() {
                    $(grid_selector).trigger("reloadGrid");
                    location.reload();
                });
            }
        });
        $('#modal-wizard').hide();
        $('.modal-backdrop').hide();
        $('#modal-wizard .modal-header').ace_wizard({
            step: 1
        });
        $('#modal-wizard .wizard-actions').show();
    });
    $('#modal-wizard .wizard-actions .btn[data-dismiss=modal]').removeAttr('disabled');

    /**
	 * Get application list
	 * 
	 */
    getApplist();
    function getApplist() {
        $.ajax({
            type: 'get',
            url: base + 'application/all',
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

    /**
	 * Get cluster list
	 * 
	 */
    function getClusters(appId) {
        $('#cluster_select').html("<option value='0'>请选择集群</option>");
        $.ajax({
            type: 'get',
            url: base + 'cluster/clusterInApp',
            data: {
                appId: appId
            },
            dataType: 'json',
            success: function(array) {
                $.each(array,
                function(index, obj) {
                    var clusterId = obj.clusterId;
                    var clustername = decodeURIComponent(obj.clusterName);
                    $('#cluster_select').append('<option value="' + clusterId + '">' + clustername + '</option>');
                });
            }
        });
    }
    
    /**
     * Get template list 
     */
    function getTemplates() {
        $('#temp_select').html("<option tempId='0' tempVersion='0'>请选择启动模板</option>");
        $.ajax({
            type: 'post',
            url: base + 'template/queryTemplateKV',
            dataType: 'json',
            success: function(array) {
                $.each(array,
                function(index, obj) {
                    var tempId = obj.tplId;
                    var tempVersion = obj.tplVersion;
                    var tempName = decodeURIComponent(obj.tplName);
                    $('#temp_select').append('<option tempId="' + tempId + '" tempVersion="'+tempVersion+'">' + tempName + '</option>');
                });
            }
        });
    }
    
    /**
	 * Get parameters list
	 * 
	 */
    function getParams() {
    	$('#meter').html("<option value='0'>启动参数</option>");
        $.ajax({
            type: 'get',
            url: base + 'param/allValue',
            dataType: 'json',
            success: function(array) {
            	var tempId = $('#temp_select').children('option:selected').attr('tempId');
            	var existParam = new Array();
            	$.each($("#temp_params li"), function(){
            		var pk = $(this).find("#tp_key").attr("paramkey");
            		existParam.push(pk);
            	});
                $.each(array,
                function(index, obj) {
                    var pv = obj.paramValue;
                    var pn = decodeURIComponent(obj.paramName);
                    var pr = obj.paramReusable;
                    var pc = obj.paramConnector;
                    var pt = obj.paramType;
                    var comment = obj.paramComment;
                    if(tempId != "0"){
                    	if(pr==0 && existParam.indexOf(pv) >= 0){
                    		
                    	}else{
                    		$('#meter').append('<option type="'+pt+'" value="'+pv+'" conn="'+pc+'" comment="'+comment+'">' + pn + '</option>');
                    	}
                    }else{
                    	$('#meter').append('<option type="'+pt+'" value="'+pv+'" conn="'+pc+'" comment="'+comment+'">' + pn + '</option>');
                    }
                });
            }
        });
    }

    /*
	 * Get selected image from imageList
	 * 
	 */
    $('.imagelist').on('click', '.image-item',
    function(event) {
        event.preventDefault();
        $('div', $('#imagelist')).removeClass('selected');
        $(this).addClass('selected');
    });
    
    /**
     * Get params of templates 
     */
    $('#temp_select').change(function(){
    	var tempId = $(this).children('option:selected').attr('tempId'); 
    	var tempVersion= $(this).children('option:selected').attr('tempVersion');
    	if(tempId != 0 || !tempVersion != 0){
    		var url = base + "template/getPElistViaTemplateIV";
    		var data = {
				tplId : tempId,
		        tplVersion : tempVersion
    		}
    		$.post(url, data, function(array) {
    			if(array.length > 0){
    				var temp_params = "";
    				$.each(array, function(index, obj) {
    					var paramName = decodeURIComponent(obj.paramName);
    					var paramKey = obj.paramKey;
    					var paramConnector = obj.paramConnector;
    					var paramValue = obj.paramValue.trim();
    					var paramRemark = obj.paramRemark.trim();
    					if(paramValue!=""){
    						temp_params += '<li class="tp" style="margin-top: 10px;">'+
    						'<input class="short-input" type="text" name="tp_key" paramConnector="'+paramConnector+'" paramkey = "'+ paramKey +'" id="tp_key" value="'+paramName+'" style="width: 100px; border: 1px solid #ccc; height: 30px"  disabled>'+
    						'&nbsp;&nbsp;<input class="short-input" type="text" name="tp_value" id="tp_value" value="'+paramValue+'" style="width: 73%; border: 1px solid #ccc; height: 30px">'+
    						'</li>'

    					}else{
    						temp_params += '<li class="tp" style="margin-top: 10px;">'+
    						'<input class="short-input" type="text" name="tp_key" paramConnector="'+paramConnector+'" paramkey = "'+ paramKey +'" id="tp_key" value="'+paramName+'" style="width: 100px; border: 1px solid #ccc; height: 30px"  disabled>'+
    						'&nbsp;&nbsp;<input class="short-input" type="text" name="tp_value" id="tp_value" placeholder="'+paramRemark+'" style="width: 73%; border: 1px solid #ccc; height: 30px">'+
    						'</li>'
    					}
    				});
    				$('#temp_param_label').css('display',"");
    				$('#temp_param').css('display',"");
    				$('#temp_params').html(temp_params);
    			}
		    });
    	}else{
    		$('#temp_param_label').css('display',"none");
			$('#temp_param').css('display',"none");
			$('#temp_params').empty();
    	}
    });

    /*
	 * Add container start param
	 */
	$("#add-param").on('click', function(event) {
		event.preventDefault();
		$("#params li:first").clone(true).appendTo("#params");
		$("#params li").not(":first").find("#remove-param").show();
		$("#params li:first").find("#remove-param").hide();
		var str = $("#params li:last").find("#meter").val();
		/** @bug152_begin 新增查询参数时，新增栏参数内容置空 */
		$("#params li:last").find("#param_value").val("");
		/** @bug152_finish */
	});
	
    /*
	 * Remove container start param
	 */
    $("#remove-param").on('click',
    function(event) {
        event.preventDefault();
        if ($("#params li").length > 1) {
            $(this).parent().remove();
        }
    });
    
    //根据选择提示输入信息
    $("#meter").change(function(){
    	var comment = $(this).children('option:selected').attr('comment');
    	var type = $(this).children('option:selected').attr('type');
    	if(type == 0){
    		$("#param_value").val('参数不需要输入值');
    		$("#param_value").attr("disabled",true);
    	}else{
    		$("#param_value").val(comment);
    		$("#param_value").attr("disabled",false);
    	}
    });

    $("#cancel").click(function() {
        location.reload();
    });

    $("#search").click(function() {
        var searchName = $('#search_name').val();
        jQuery(grid_selector).jqGrid('setGridParam', {
            url: base + 'container/list?conName=' + searchName
        }).trigger("reloadGrid");
    });

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
            if (rowData.appStatus == 2) {
                showMessage("容器 " + rowData.conUuid + "应用状态异常，不允许启动, 请重新选择！");
                return;
            }
            infoList += rowData.conUuid + " ";
        }
        return;
        if (infoList != "") {
            bootbox.dialog({
                message: '<div class="alert alert-info" style="margin:10px"><span class="glyphicon glyphicon-info-sign"></span>&nbsp;启动容器&nbsp;' + infoList + '?</div>',
                title: "提示",
                buttons: {
                    main: {
                        label: "<i class='icon-info'></i><b>确定</b>",
                        className: "btn-sm btn-success btn-round",
                        callback: function() {
                            $('.well').append('<div class="icon-spinner">' + '<i id = "spinner" class="ace-icon fa fa-spinner fa-spin blue bigger-225"></i>' + '</div>');
                            var conids = new Array();
                            for (var i = 0; i < ids.length; i++) {
                                var rowData = $(grid_selector).jqGrid("getRowData", ids[i]);
                                conids = (conids + rowData.conId) + (((i + 1) == ids.length) ? '': ',');
                            }
                            startContainer(conids);
                        }
                    },
                    cancel: {
                        label: "<i class='icon-info'></i> <b>取消</b>",
                        className: "btn-sm btn-danger btn-round",
                        callback: function() {}
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
        if (infoList != "") {
            bootbox.dialog({
                message: '<div class="alert alert-info" style="margin:10px"><span class="glyphicon glyphicon-info-sign"></span>&nbsp;停止容器&nbsp;' + infoList + '?</div>',
                title: "提示",
                buttons: {
                    main: {
                        label: "<i class='icon-info'></i><b>确定</b>",
                        className: "btn-sm btn-success btn-round",
                        callback: function() {
                            $('.well').append('<div class="icon-spinner">' + '<i id = "spinner" class="ace-icon fa fa-spinner fa-spin blue bigger-225"></i>' + '</div>');
                            var conids = new Array();
                            for (var i = 0; i < ids.length; i++) {
                                var rowData = $(grid_selector).jqGrid("getRowData", ids[i]);
                                conids = (conids + rowData.conId) + (((i + 1) == ids.length) ? '': ',');
                            }
                            stopContainer(conids);
                        }
                    },
                    cancel: {
                        label: "<i class='icon-info'></i> <b>取消</b>",
                        className: "btn-sm btn-danger btn-round",
                        callback: function() {}
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
                    main: {
                        label: "<i class='icon-info'></i><b>确定</b>",
                        className: "btn-sm btn-success btn-round",
                        callback: function() {
                            $('.well').append('<div class="icon-spinner">' + '<i id = "spinner" class="ace-icon fa fa-spinner fa-spin blue bigger-225"></i>' + '</div>');
                            var conids = new Array();
                            for (var i = 0; i < ids.length; i++) {
                                var rowData = $(grid_selector).jqGrid("getRowData", ids[i]);
                                conids = (conids + rowData.conId) + (((i + 1) == ids.length) ? '': ',');
                            }
                            trashContainer(conids);
                        }
                    },
                    cancel: {
                        label: "<i class='icon-info'></i> <b>取消</b>",
                        className: "btn-sm btn-danger btn-round",
                        callback: function() {
                            location.reload();
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
	$("#con_add-param").on('click', function(event) {
		event.preventDefault();
		$("#con_params li:first").clone(true).appendTo("#con_params");
		$("#con_params li").not(":first").find("#con_remove-param").show();
		$("#con_params li:first").find("#con_remove-param").hide();
		var str = $("#con_params li:last").find("#con_meter").val();
		/** @bug152_begin 新增查询参数时，新增栏参数内容置空 */
		$("#con_params li:last").find("#con_param_value").val("");
		/** @bug152_finish */
	});


    /**
	 * 删除高级索索的参数项
	 */
    $("#add-search").on('click',
    function(event) {
        event.preventDefault();
        $("#search_params li:first").clone(true).appendTo("#search_params");
        $("#search_params li").not(":first").find("#remove-search").show();
        $("#search_params li:first").find("#remove-search").hide();
        var str = $("#search_params li:last").find("#search_meter").val();
        if (str == 0 || str == 1 || str == 2) {
            $("#search_params li:last").find("#unit").text("%");
        } else {
            $("#parasearch_paramsms li:last").find("#unit").text("Mbps");
        }
    });

    /**
	 * 向查询按钮添加请求提交操作 删除高级索索的参数项
	 */
    $("#con_advanced_search").on('click',
    function(event) {
        /* 保存各项栏目的名称数组 */
        var column_array = new Array();
        /* 保存用户填写的各项信息数组 */
        var value_array = new Array();

        /* 获取选择栏目的名称 */
        $("select[name=con_meter]").each(function() {
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
        $("input[name=con_param_value]").each(function() {
            value_array.push($(this).val());
        });
        /* 查询是否存在关键词相关的结果 */
        jQuery(grid_selector).jqGrid('setGridParam', {
            url: base + 'container/advancedSearch?params=' + column_array + '&values=' + value_array
        }).trigger("reloadGrid");

				/** @bug152_begin 清空用户多选的参数 */
				while ($("#con_params li").length > 1) {
					$("#con_remove-param").parent().remove();
				}
				/** @bug152_finish */

				$('#advanSearchContainerModal').modal('hide');
				$('#advanced_search_container_frm')[0].reset();
			});


    /**
	 * 向高级搜索的取消按钮添加重置隐藏
	 */

    $("#con_advanced_cancel").on('click',
    function(event) {
        event.preventDefault();
        $('#advanSearchContainerModal').modal('hide');
        $('#advanced_search_container_frm')[0].reset();
    });

});

/**
 * @author yangqinglin
 * @datetime 2015年11月3日 11:23
 * @description 添加高级查询函数
 */
function AdvancedSearchContainer() {
	/** @bug152_begin 清空用户多选的参数 */
	while ($("#con_params li").length > 1) {
		$("#con_remove-param").parent().remove();
	}
	/* 隐藏高级查询第一行的删除打叉按钮 */
	$("#con_params li:first").find("#con_remove-param").hide();
	/** 打开高级搜索窗口，之前输入全部清空 */
	$("#con_params li:first").find("#con_param_value").val("");
	$("#con_params li:first").find("#con_meter").val("0");
	/** @bug152_finish */

	$('#advanSearchContainerModal').modal('show');

}

/**
 * Get image list
 */
function getImageList(page, limit, appId) {
    $('#imagelist').html("");
    var appmessage = {
        page: page,
        rows: limit,
        appId: appId
    };
    $.ajax({
        type: 'get',
        url: base + 'image/listByappId',
        data: appmessage,
        dataType: 'json',
        success: function(array) {
            var tableStr = "";
            if (array.rows.length >= 1) {
                var totalnum = array.records;
                var totalp = 1;
                if (totalnum != 0) {
                    totalp = Math.ceil(totalnum / limit);
                }
                options = {
                    totalPages: totalp
                };
                $('#tplpage').bootstrapPaginator(options);
                modalPageUpdate(page, totalp);
                $('#currentPage').val(page);
                var rowData = array.rows;
                for (var i = 0; i < rowData.length; i++) {
                    var obj = rowData[i];
                    var imageId = obj.imageId;
                    var imageUuid = obj.imageUuid;
                    var imageName = obj.imageName;
                    var tag = obj.imageTag;
                    var imgUrl = imageName + ":" + tag;
                    if (tag != null) {
                        tableStr = tableStr + '<div class="image-item" imageid="' + imageId + '" imagename="' + imgUrl + '">' + imgUrl + '</div>';
                    } else {
                        tableStr = tableStr + '<div class="image-item" imageid="' + imageId + '" imagename="' + imgUrl + '">' + imgUrl + '</div>';
                    }
                }
                $('#imagelist').html(tableStr);
            }
        }
    });
}

/**
 * Start single container
 */
function startSingleContainer(conid, conUuid, appstatus, obj) {
	if(appstatus==2){
		showMessage('容器中应用状态异常，不允许启动！');
		return;
	}
    bootbox.dialog({
        message: '<div class="alert alert-info" style="margin:10px"><span class="glyphicon glyphicon-info-sign"></span>&nbsp;启动容器&nbsp;c-' + conUuid.substr(0, 8) + '?</div>',
        title: "提示",
        buttons: {
            main: {
                label: "<i class='icon-info'></i><b>确定</b>",
                className: "btn-sm btn-success btn-round",
                callback: function() {
                	$(obj).attr("disabled",true);
                    $('.well').append('<div class="icon-spinner">' + '<i id = "spinner" class="ace-icon fa fa-spinner fa-spin blue bigger-225"></i>' + '</div>');
                    startContainer(conid);
                }
            },
            cancel: {
                label: "<i class='icon-info'></i> <b>取消</b>",
                className: "btn-sm btn-danger btn-round",
                callback: function() {}
            }
        }
    });
}

/**
 * Start container
 */
function startContainer(conidArray) {
    $('#conIds').val(conidArray);
    $.ajax({
        type: 'get',
        url: base + 'container/start',
        data: {
            containerids: conidArray
        },
        dataType: 'json',
        success: function(response) {
            $('.icon-spinner').remove();
            if (response == "") {
                showMessage("启动容器异常！");
            } else {
                if (response.success) {
                    bootbox.dialog({
                        message: '<div class="alert alert-info" style="margin:10px"><span class="glyphicon glyphicon-info-sign"></span>&nbsp;启动成功，是否更新负载均衡&nbsp;' + '?</div>',
                        title: "提示",
                        buttons: {
                            main: {
                                label: "<i class='icon-info'></i><b>确定</b>",
                                className: "btn-sm btn-success btn-round",
                                callback: function() {
                                    reloadBalance(conidArray);
                                }
                            },
                            cancel: {
                                label: "<i class='icon-info'></i> <b>取消</b>",
                                className: "btn-sm btn-danger btn-round",
                                callback: function() {}
                            }
                        }
                    });
                } else {
                    showMessage(response.message);
                }
            }
            $(grid_selector).trigger("reloadGrid");
        },
        error: function(response) {
            $('.icon-spinner').remove();
            if (response == "") {
                showMessage("启动容器异常！");
                $(grid_selector).trigger("reloadGrid");
            } else {
                showMessage(response.message,
                function() {
                    $(grid_selector).trigger("reloadGrid");
                });
            }
        }
    });
}

/**
 * Stop single container
 */
function stopSingleContainer(conid, conUuid,obj) {
    bootbox.dialog({
        message: '<div class="alert alert-info" style="margin:10px"><span class="glyphicon glyphicon-info-sign"></span>&nbsp;停止容器&nbsp;c-' + conUuid.substr(0, 8) + '?</div>',
        title: "提示",
        buttons: {
            main: {
                label: "<i class='icon-info'></i><b>确定</b>",
                className: "btn-sm btn-success btn-round",
                callback: function() {
                	$(obj).attr("disabled",true);
                    Loading.show();
                	stopContainer(conid);
                }
            },
            cancel: {
                label: "<i class='icon-info'></i> <b>取消</b>",
                className: "btn-sm btn-danger btn-round",
                callback: function() {}
            }
        }
    });
}

/**
 * Stop container
 */
function stopContainer(conidArray) {
    $.ajax({
        type: 'get',
        url: base + 'container/stop',
        data: {
            containerids: conidArray
        },
        dataType: 'json',
        success: function(response) {
        	Loading.hide();
            if (response == "") {
                showMessage("停止容器出现异常！");
            } else {
                if (response.success) {
                    bootbox.dialog({
                        message: '<div class="alert alert-info" style="margin:10px"><span class="glyphicon glyphicon-info-sign"></span>&nbsp;停止成功，是否更新负载均衡&nbsp;' + '?</div>',
                        title: "提示",
                        buttons: {
                            main: {
                                label: "<i class='icon-info'></i><b>确定</b>",
                                className: "btn-sm btn-success btn-round",
                                callback: function() {
                                    reloadBalance(conidArray);
                                }
                            },
                            cancel: {
                                label: "<i class='icon-info'></i> <b>取消</b>",
                                className: "btn-sm btn-danger btn-round",
                                callback: function() {}
                            }
                        }
                    });
                } else {
                    showMessage(response.message);
                }
            }
            $(grid_selector).trigger("reloadGrid");
        },
        error: function(response) {
            $('.icon-spinner').remove();
            if (response == "") {
                showMessage("停止容器出现异常！");
                $(grid_selector).trigger("reloadGrid");
            } else {
                showMessage(response.message,
                function() {
                    $(grid_selector).trigger("reloadGrid");
                });
            }
        }
    });
}

/**
 * Remove single container
 */
function removeSingleContainer(conid, conUuid, conPower,obj) {
    if (conPower == 1) {
        showMessage("容器 c-" + conUuid.substr(0, 8) + "没有停止, 不能删除！");
        return;
    }
    bootbox.dialog({
        message: '<div class="alert alert-info" style="margin:10px"><span class="glyphicon glyphicon-info-sign"></span>&nbsp;删除容器&nbsp;c-' + conUuid.substr(0, 8) + '?</div>',
        title: "提示",
        buttons: {
            main: {
                label: "<i class='icon-info'></i><b>确定</b>",
                className: "btn-sm btn-success btn-round",
                callback: function() {
                	$(obj).attr("disabled",true);
                    $('.well').append('<div class="icon-spinner">' + '<i id = "spinner" class="ace-icon fa fa-spinner fa-spin blue bigger-225"></i>' + '</div>');
                    trashContainer(conid);
                }
            },
            cancel: {
                label: "<i class='icon-info'></i> <b>取消</b>",
                className: "btn-sm btn-danger btn-round",
                callback: function() {}
            }
        }
    });
}

/**
 * Trash container
 */
function trashContainer(conidArray) {
    $.ajax({
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
    });
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

/**
 * @param conIds
 */
/*
 * function reloadBalance(conIds){ $('#showLBConfModal').modal('show'); return;
 * 
 * url = base+'loadbalance/reloadApp'; data = { conIds:conIds, fileFlag:0 };
 * $.post(url,data,function(response){ $('#showLBConfModal').modal('show'); var
 * fileNames = response.message.split("#"); for(var i=0;i<fileNames.length;i++){
 * if(fileNames[i]!=""){ $('#confList').append('<a
 * style="font-size:14px;cursor:pointer;"
 * onclick="readConfFile('+"'"+fileNames[i]+"'"+')">'+fileNames[i]+'</a>'); } }
 * }); }
 */

function reloadBalance(conIds) {
    $('.well').append('<div class="icon-spinner">' + '<i id = "spinner" class="ace-icon fa fa-spinner fa-spin blue bigger-225"></i>' + '</div>');
    $.ajax({
        type: 'post',
        url: base + 'loadbalance/reloadApp',
        data: {
            conIds: conIds,
            fileFlag: 0
        },
        dataType: 'json',
        success: function(response) {
            if (response == "") {
                showMessage("重新加载负载均衡异常！");
                $('.icon-spinner').remove();
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
                        main: {
                            label: "<i class='icon-info'></i><b>继续</b>",
                            className: "btn-sm btn-success btn-round",
                            callback: function() {
                                url = base + 'loadbalance/reloadApp';
                                data = {
                                    conIds: conIds,
                                    fileFlag: 1
                                };
                                $.post(url, data,
                                function(response) {
                                    if (response == "") {
                                        showMessage("重新加载负载均衡异常！");
                                        $('.icon-spinner').remove();
                                        $(grid_selector).trigger("reloadGrid");
                                    } else {
                                        showMessage(response.message,
                                        function() {
                                            $('.icon-spinner').remove();
                                            location.reload();
                                        });
                                    }
                                });
                            }
                        },
                        cancel: {
                            label: "<i class='icon-info'></i> <b>取消</b>",
                            className: "btn-sm btn-danger btn-round",
                            callback: function() {
                                $('.icon-spinner').remove();
                                $(grid_selector).trigger("reloadGrid");
                            }
                        }
                    }
                });
            }

        }
    });
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
var grid_selector = "#role_list";
var page_selector = "#role_page";

/** 保存当前高级查询条件的列数量 */
var advanceColNum = 0;

var authTreeData = [];
var setting = {
    check: {
        enable: true
    },
    data: {
        simpleData: {
            enable: true
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
        url: base + 'role/all',
        datatype: "json",
        height: '100%',
        autowidth: true,
        colNames: ['角色ID', '角色名', '角色描述', '角色标记', '角色状态', '快捷操作'],
        colModel: [{
            name: 'roleId',
            index: 'roleId',
            width: 10,
            hidden: true
        },
        {
            name: 'roleName',
            index: 'roleName',
            width: 10,
            formatter: function(cell, opt, obj) {
                return '<i class="fa fa-cubes"></i><a href="' + base + 'role/detail/' + obj.roleId + '.html">' + cell + '</a>';
            }
        },
        {
            name: 'roleDesc',
            index: 'roleDesc',
            width: 10
        },
        {
            name: 'roleRemarks',
            index: 'roleRemarks',
            width: 10,
            hidden: true
        },
        {
            name: 'roleStatus',
            index: 'roleStatus',
            width: 10,
            formatter: function(cellvalue, options, rowObject) {
                switch (rowObject.roleStatus) {
                case 1:
                    return '正常';
                default:
                    return '注销';
                }
            }
        },
        {
            name: '',
            title:false,
            index: '',
            width: 120,
            fixed: true,
            sortable: false,
            resize: false,
            formatter: function(cellvalue, options, rowObject) {
                var strHtml = "";

                var upda = $("#update_role").val();
                if (typeof(upda) != "undefined") {
                    strHtml += "<button class=\"btn btn-xs btn-primary btn-round\" onclick=\"modifyRoleWin('" + rowObject.roleId + "','" + rowObject.roleName + "','" + rowObject.roleDesc + "','" + rowObject.roleRemarks + "')\"><b>编辑</b></button> &nbsp;";
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
                updatePagerIcons(table);
                enableTooltips(table);
            },
            0);
        }
    });
    $(window).triggerHandler('resize.jqGrid'); // 窗口resize时重新resize表格，使其变成合适的大小
    jQuery(grid_selector).jqGrid( //分页栏按钮
    'navGrid', page_selector, { // navbar options
        edit: false,
        add: false,
        del: false,
        search: false,
        refresh: true,
        refreshstate: 'current',
        refreshicon: 'ace-icon fa fa-refresh',
        view: false
    },{},{},{},{},{});

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

    //创建角色提交按钮
    $('#createRole').on('click',function(){
    	$('#createRoleModal').modal('show');
    })
    $('#submit').click(function(){
    	if($('#create_role_form').valid()){
    		var roleName=$('#role_name').val();
    		var roleDesc=$('#role_desc').val();
    		var data={
    			roleName:roleName,
    			roleDesc:roleDesc
    		}
    		
    		var url=base+'role/create';
    		$('#createRoleModal').modal('hide');
    		$.post(url,data,function(response){
    			if(response==''){
    				showMessage('创建角色异常！');
    			}else{
    				showMessage(response.message);
    			}
    			$(grid_selector).trigger('reloadGrid');
    			$('div.icon-spinner').remove();
    			$('#create_role_form')[0].reset();
    		})
    	}
    })
    $('#cancel').click(function(){
    	$(grid_selector).trigger('reloadGrid');
    	$('#createRoleModal').modal('hide');
    	$('#create_role_form')[0].reset();
    	$('label.error').remove();
    	
    })
    
     //创建角色信息校验
    $("#create_role_form").validate({
        rules: {
            role_name: {
                required: true,
                stringCheck: true,
                maxlength: 20,
                remote:{
                	url : base + "role/checkRoleName",
					type : "get",
					dataType : "json",
					data : {
						roleName : function() {
							return $.trim($("#role_name").val());
						}
					},
					dataFilter : function(data) {
				 		return data;
					}
                }
            },
            role_desc: {
                maxlength: 200,
                stringCheck: true
            }
        },
        messages: {
            role_name: {
                required: "角色名不能为空",
                maxlength: $.validator.format("角色名不能大于20个字符"),
                remote:"该角色名称已经存在，请重新填写。"
            },
            role_desc: {
                maxlength: $.validator.format("角色描述不能大于200个字符")
            }
        }
    });
    //修改角色提交按钮
    $('#modify_submit').click(function() {
        if ($("#modify_role_form").valid()) {
           var url = base + 'role/update';
           var id = $('#role_id_edit').val();
           var name = $('#role_name_edit').val();
           var desc = $('#role_desc_edit').val();
           var roleRemarks = $('#role_remarks_edit').val();

           var data = {
                roleId: id,
                roleName: name,
                roleDesc: desc,
                roleRemarks: roleRemarks
            };
            $('#modifyRoleModal').modal('hide');
            $.post(url, data,
            function(response) {
                if (response == "") {
                    showMessage("修改角色异常！");
                } else {
                    showMessage(response.message);
                }
                $(grid_selector).trigger("reloadGrid");
            });
        }
    });

    //编辑角色取消按钮
    $('#modify_cancel').click(function() {
        $(grid_selector).trigger("reloadGrid");
        $('#modifyRoleModal').modal('hide');
        $('#modify_role_form')[0].reset();
        $('label.error').remove();
    });

    //编辑角色信息校验
    $("#modify_role_form").validate({
        rules: {
            role_name_edit: {
                required: true,
                stringCheck: true,
                maxlength: 20,
                remote:{
                	url : base + "role/checkRoleName",
					type : "get",
					dataType : "json",
					data : {
						roleName : function() {
							return $.trim($("#role_name_edit").val());
						}
					},
					dataFilter : function(data) {
						var oldName=$.trim($('#old_role_name_edit').val());
						var newName=$.trim($("#role_name_edit").val())
						if(oldName==newName){
							return true;
						}else{
							return data;
						}
					}
                }
            },
            role_desc_edit: {
                maxlength: 200,
                stringCheck: true
            }
        },
        messages: {
            role_name_edit: {
                required: "角色名不能为空",
                maxlength: $.validator.format("角色名不能大于20个字符"),
                remote:"该角色名称已经存在，请重新填写。"
            },
            role_desc_edit: {
                maxlength: $.validator.format("角色描述不能大于200个字符")
            }
        }
    });

    //角色授权按钮
    $('#authToRole').click(function() {
        var rowData = "";
        var ids = $(grid_selector).jqGrid("getGridParam", "selarrrow");
        var roles=new Array();
        for (var i = 0; i < ids.length; i++) {
            rowData = $(grid_selector).jqGrid("getRowData", ids[i]);
            roles[i]= rowData.roleId;
        }
        if (roles.length > 1) {
            $(grid_selector).trigger("reloadGrid");
            showMessage("一次只能给一个角色授权！");
            return;
        }
        if (ids.length > 0) {
            if (rowData.roleRemarks == 0) {
                $(grid_selector).trigger("reloadGrid");
                showMessage("不能给管理员授权！");
            } else {
                var treeStr = "";
                $.ajax({
                    url: base + "role/roleAuth",
                    data: {
                        roleId: roles[0]
                    },
                    type: 'get',
                    dataType: 'json',
                    success: function(obj) {
                        $.each(obj,function(index, json) {
                            treeStr += json.actionId + ",";
                        });
                        createZTreeFun("#roleAuthTree");
                        //替换为ztree
                        var tree = $.fn.zTree.getZTreeObj('roleAuthTree');
                        var treeStrSplit = treeStr.split(",");
                        var treeIds = [];
                        for (var i = 0; i < treeStrSplit.length; i++) {
                            var node = tree.getNodeByParam('id', parseInt(treeStrSplit[i]), null);
                            if (node) tree.checkNode(node, true);
                        }
                        $('#authToRoleModal').modal('show');
                    }
                });
            }
        } else {
            $(grid_selector).trigger("reloadGrid");
            showMessage("请先选中角色，再授权！");
        }
    });

    //
    $('#authAuth_submit').click(function() {
        var ids = $(grid_selector).jqGrid("getGridParam", "selarrrow");
        var roles = "";
        for (var i = 0; i < ids.length; i++) {
            var rowData = $(grid_selector).jqGrid("getRowData", ids[i]);
            roles += (i == ids.length - 1 ? rowData.roleId: rowData.roleId + ",");
        }
        var tree = $.fn.zTree.getZTreeObj('roleAuthTree'),
        nodeId = '',
        items = tree.getCheckedNodes();
        if(items.length==0){
        	showMessage("至少选择一个权限进行授权！");
        }else{
        	for (var i in items) if (items.hasOwnProperty(i)) {
        		var item = items[i];
        		nodeId += item.id + ',';
        	}
        	nodeId = nodeId.substring(0, nodeId.length - 1);
        	authToRoles(roles, nodeId);
        }
    });

    //角色授权隐藏按钮
    $('#authAuth_cancel').click(function() {
        $(grid_selector).trigger("reloadGrid");
        $('#authToRoleModal').modal('hide');
    });

    //替换ztree
    $.ajax({
        url: base + "auth/tree",
        //要加载数据的地址
        type: 'get',
        //加载方式
        dataType: 'json',
        //返回数据格式
        success: function(response) {
            if (response == "") {
                showMessage("权限数据加载异常！");
            } else {
                authTreeData = response.data;
            }
        },
        error: function(response) {
            console.log(response);
        }
    });

    /**
	 * 向查询按钮添加请求提交操作
	 */
    $("#advanced_role_search").on('click',
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
		$("input[name=search_role_value]").each(function() {
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
					url : base + 'role/advancedSearch?params='
							+ column_array + '&values=' + value_array
				}).trigger("reloadGrid");
		//
		$('#advancedSearchRoleModal').modal('hide');
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
    $("#advanced_role_cancel").on('click',
    function(event) {
        event.preventDefault();
        $('#advancedSearchRoleModal').modal('hide');
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
		$("#params li:last").find("#search_role_value").val("");
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


/**
 * 修改角色信息
 * @param id	角色id
 * @param name	角色名称
 * @param desc	角色描述
 * @param remarks	角色标记
 */
function modifyRoleWin(id, name, desc, remarks) {
    $("#role_id_edit").val(id);
    $("#old_role_name_edit").val(name);
    $("#role_name_edit").val(name);
    $("#role_desc_edit").val((desc == null || desc == "null") ? "": desc);
    $("#role_remarks_edit").val(remarks);
    $("#modifyRoleModal").modal('show');
}

/**
 * 根据输入的角色名进行模糊查询
 */
function searchRoles() {
    var roleName = $('#search_role').val();
    jQuery(grid_selector).jqGrid('setGridParam', {
        url: base + 'role/all?roleName=' + roleName
    }).trigger("reloadGrid");
}

/**
 * 角色授权
 * @param roles		角色
 * @param auths		权限
 */
function authToRoles(roles, auths) {
    if (roles == null) {
        showMessage("请选择角色！");
        $(grid_selector).trigger("reloadGrid");
    } else {
        var url = base + 'role/authToRole';
        data = {
            roles: roles,
            auths: auths
        };
        $('#authToRoleModal').modal('hide');
        $.post(url, data,
        function(response) {
            showMessage("角色授权成功！");
            $(grid_selector).trigger("reloadGrid");
        });
    }
}

//获取权限列表
function getAuthlist() {
    $("#authbody").html("");
    $.ajax({
        url: base + 'auth/list',
        data: {},
        dataType: "json",
        success: function(obj) {
            var tableStr = "";
            $.each(obj,
            function(index, json) {
                var thistr = '<tr rowid="' + json.actionId + '">';
                thistr += '<td class="rcheck"><input type="checkbox" name="checkbox_auth"></td><td name="id">' + json.actionId + '</td><td name="name">' + json.actionName + '</td></tr>';
                tableStr += thistr;
            });
            $('#authbody').html(tableStr);
        }
    });
}

function AdvancedSearchRoles() {
	/** @bug152_begin 清空用户多选的参数 */
	while ($("#params li").length > 1) {
		$("#remove-param").parent().remove();
	}
	/* 隐藏高级查询第一行的删除打叉按钮 */
	$("#params li:first").find("#remove-param").hide();
	/** 打开高级搜索窗口，之前输入全部清空 */
	$("#params li:first").find("#search_role_value").val("");
	$("#params li:first").find("#meter").val("0");
	/** @bug152_finish */

	$('#advancedSearchRoleModal').modal('show');
}

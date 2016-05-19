var grid_selector = "#user_list";
var page_selector = "#user_page";
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
        url: base + 'user/all',
        datatype: "json",
        height: '100%',
        autowidth: true,
        colNames: ['用户ID', '用户名', '角色','密码', '邮箱', '电话', '公司', '级别', '状态', '用户角色', '创建人', '创建时间', '快捷操作'],
        colModel: [{
            name: 'userId',
            index: 'userId',
            width: 5,
            hidden: true
        },
        {
            name: 'userName',
            index: 'userName',
            width: 5,
            formatter: function(cell, opt, obj) {
                return '<i class="fa fa-cubes"></i><a href="' + base + 'user/detail/' + obj.userId + '.html">' + cell + '</a>';
            }
        },
        {
            name: 'roleName',
            index: 'roleName',
            width: 5
        },
        {
            name: 'userPass',
            index: 'userPass',
            width: 10,
            hidden: true
        },
        {
            name: 'userMail',
            index: 'userMail',
            width: 15,
            hidden: true
        },
        {
            name: 'userPhone',
            index: 'userPhone',
            width: 10
        },
        {
            name: 'userCompany',
            index: 'userCompany',
            width: 5
        },
        {
            name: 'userLevel',
            index: 'userLevel',
            width: 5,
            hidden: true
        },
        {
            name: 'userStatus',
            index: 'userStatus',
            width: 5,
            formatter: function(cellvalue, options, rowObject) {
                switch (rowObject.userStatus) {
                case 1:
                    return '激活';
                default:
                    return '冻结';
                }
            }
        },
        {
            name: 'userRoleid',
            index: 'userRoleid',
            width: 10,
            hidden: true
        },
        {
            name: 'createUserName',
            index: 'createUserName',
            width: 10
        },
        {
            name: 'userCreatedate',
            index: 'userCreatedate',
            width: 15,
            hidden: true
        },
        {
            name: '',
            index: '',
            width: 120,
            fixed: true,
            sortable: false,
            resize: false,
            formatter: function(cellvalue, options, rowObject) {
                var strHtml = "";
                var upda = $("#update_user").val();
                if (typeof(upda) != "undefined") {
                    strHtml += "<button class=\"btn btn-xs btn-primary btn-round\" onclick=\"modifyUserWin('" + rowObject.userId + "','" + rowObject.userName + "','" + rowObject.userMail + "','" + rowObject.userPhone + "','" + rowObject.userCompany + "')\"><b>编辑</b></button> &nbsp;";
                }
                var dele = $("#delete_user").val();
                if (typeof(dele) != "undefined" && rowObject.userStatus == 1) {
                    strHtml += "<button class=\"btn btn-xs btn-danger btn-round\" onclick=\"removeUser('" + rowObject.userId + "')\"><b>冻结</b></button> &nbsp;";
                }
                var act = $("#active_user").val();
                if (typeof(act) != "undefined" && rowObject.userStatus == 0) {
                    strHtml += "<button class=\"btn btn-xs btn-primary btn-round\" onclick=\"activeUser('" + rowObject.userId + "')\"><b>激活</b></button> &nbsp;";
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
            },0);
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
    },{},{},{},{},{});

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
    //
    $('#submit').click(function() {
        if ($("#create_user_form").valid()) {
            var userName = $('#user_name').val();
            var userMail = $('#user_mail').val();
            var userPhone = $('#user_phone').val();
            var userCompany = $('#user_company').val();
            data = {
                userName: userName,
                userMail: userMail,
                userPhone: userPhone,
                userCompany: userCompany
            };
            url = base + "user/create";
            $('#createUserModal').modal('hide');
            $.post(url, data,
            function(response) {
                if (response == "") {
                    showMessage("创建用户异常！");
                } else {
                    showMessage(response.message);
                }
                $(grid_selector).trigger("reloadGrid");
                $('#create_user_form')[0].reset();
            });
        }
    });

    //创建用户div 【取消按钮】
    $('#cancel').click(function() {
        $('#createUserModal').modal('hide');
        $('#create_user_form')[0].reset();
        $('label.error').remove();
        $(grid_selector).trigger("reloadGrid");
    });

    //修改用户信息   【提交按钮】
    $('#modify_submit').click(function() {
        if ($("#modify_user_form").valid()) {
            url = base + 'user/update';
            id = $('#user_id_edit').val();
            name = $('#user_name_edit').val();
            mail = $('#user_mail_edit').val();
            phone = $('#user_phone_edit').val();
            company = $('#user_company_edit').val();
            data = {
                userId: id,
                userName: name,
                userMail: mail,
                userPhone: phone,
                userCompany: company
            };
            $('#modifyUserModal').modal('hide');
            $.post(url, data,
            function(response) {
                if (response == "") {
                    showMessage("修改用户信息异常！");
                } else {
                    showMessage(response.message);
                }
                $(grid_selector).trigger("reloadGrid");
            });
        }
    });

    //修改用户信息 【取消】
    $('#modify_cancel').click(function() {
        $('#modifyUserModal').modal('hide');
        $('#modify_user_form')[0].reset();
        $('label.error').remove();
        $(grid_selector).trigger("reloadGrid");
    });

    //添加用户（校验）
    $("#create_user_form").validate({
        rules: {
            user_name: {
                required: true,
                stringNameCheck: true,
                maxlength: 32,
                remote: {
                    url: base + "user/checkName",
                    type: "post",
                    dataType: "json",
                    data: {
                        userName: function() {
                            return $("#user_name").val();
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
            user_mail: {
                required: true,
                maxlength: 64,
                isMail: true
            },
            user_phone: {
                required: true,
                maxlength: 11,
                isPhone: true
            },
            user_company: {
                stringCheck: true,
                maxlength: 256
            }
        },
        messages: {
            user_name: {
                required: "用户名不能为空",
                maxlength: $.validator.format("用户名不能大于32个字符"),
                remote: "用户名已经存在，请重新填写"
            },
            user_mail: {
                required: "邮箱不能为空",
                maxlength: $.validator.format("邮箱不能大于64个字符")
            },
            user_phone: {
                required: "电话不能为空",
                maxlength: $.validator.format("电话不能大于11个字符")
            },
            user_company: {
                maxlength: $.validator.format("公司名称不能大于256个字符")
            }
        }
    });

    //修改用户（校验）
    $("#modify_user_form").validate({
        rules: {
            user_name_edit: {
                required: true,
                stringNameCheck: true,
                maxlength: 32,
                remote: {
                    url: base + "user/checkName",
                    type: "post",
                    dataType: "json",
                    data: {
                        userName: function() {
                            return $("#user_name_edit").val();
                        }
                    },
                    dataFilter: function(data) { // 判断控制器返回的内容
                        if (data=="true") {
                            return true;
                        } else {
                        	var oldname=$('#user_oldname_edit').val();
                        	if( $("#user_name_edit").val()==oldname){
                        		return true;
                        	}else{
                        		return false;
                        	}
                        }
                    }
                }
            },
            user_pass_edit: {
                required: true,
                maxlength: 20,
                isPwd: true
            },
            user_mail_edit: {
                required: true,
                maxlength: 64,
                isMail: true
            },
            user_phone_edit: {
                required: true,
                maxlength: 11,
                isPhone: true
            },
            user_company_edit: {
                stringCheck: true,
                maxlength: 256
            }
        },
        messages: {
            user_name_edit: {
                required: "用户名不能为空",
                maxlength: $.validator.format("用户名不能大于32个字符"),
                remote: "用户名已经存在，请重新填写"
            },
            user_pass_edit: {
                required: "密码不能为空",
                maxlength: $.validator.format("密码不能大于20个字符")
            },
            user_mail_edit: {
                required: "邮箱不能为空",
                maxlength: $.validator.format("邮箱不能大于64个字符")
            },
            user_phone_edit: {
                required: "电话不能为空",
                maxlength: $.validator.format("电话不能大于11个字符")
            },
            user_company_edit: {
                maxlength: $.validator.format("公司名称不能大于256个字符")
            }
        }
    });

    //用户授权
    $('#authToUser').click(function() {
        var ids = $(grid_selector).jqGrid("getGridParam", "selarrrow");
        var users = new Array();
        if (ids.length > 0) {
            for (var i = 0; i < ids.length; i++) {
                var rowData = $(grid_selector).jqGrid("getRowData", ids[i]);
                users[i]=  rowData.userId;
            }
            if (users.length > 2) {
                $(grid_selector).trigger("reloadGrid");
                showMessage("一次只能给一个用户赋予角色！");
            } else {
                getRolelist(users[0]);
                $('#authToUserModal').modal('show');
            }
        } else {
            showMessage("请先选中用户，再授权！");
        }
    });


    //
    $('#authUser_submit').click(function() {
        var ids = $(grid_selector).jqGrid("getGridParam", "selarrrow");
        var users = "";
        for (var i = 0; i < ids.length; i++) {
            var rowData = $(grid_selector).jqGrid("getRowData", ids[i]);
            users += (i == ids.length - 1 ? rowData.userId: rowData.userId + ",");
        }
        var roles = "";
        $("input[name='checkbox_role']:checked").each(function() {
            roles += $(this).parent().parent().attr('rowid').trim() + ",";
        })

        if(roles==""){
        	showMessage("至少选择一个角色进行授权！");
        	return false;
        }
        authToUsers(users, roles);
    });
    //
    $('#authUser_cancel').click(function() {
        $('#authToUserModal').modal('hide');
        $(grid_selector).trigger("reloadGrid");
    });
    /**
	 * 向查询按钮添加请求提交操作
	 */
    $("#advanced_user_search").on('click',
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
				$("input[name=search_user_value]").each(function() {
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
							url : base + 'user/advancedSearch?params='
									+ column_array + '&values=' + value_array
						}).trigger("reloadGrid");
				//
				$('#advancedSearchUserModal').modal('hide');
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
    $("#advanced_user_cancel").on('click',
    function(event) {
        event.preventDefault();
        $('#advancedSearchUserModal').modal('hide');
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
});

function removeUser(id) {
    url = base + "user/delete/" + id;
    data = {};
    bootbox.dialog({
        message: '<div class="alert alert-info" style="margin:10px"><span class="glyphicon glyphicon-info-sign"></span>&nbsp;你确定要冻结此用户&nbsp;' + '?</div>',
        title: "冻结用户",
        buttons: {
        	cancel: {
                label: "取消",
                className: "btn-danger btn-round",
                callback: function() {
                	$(grid_selector).trigger("reloadGrid");
                }
            },
            main: {
                label: "确定",
                className: "btn-success btn-round",
                callback: function() {
                	$.post(url, data,function(response) {
                        if (response == "") {
                            showMessage("冻结用户异常！");
                        } else {
                            if (response.success) {
                                showMessage("冻结用户成功！");
                            } else {
                                showMessage(response.message);
                            }
                        }
                        $(grid_selector).trigger("reloadGrid");
                    });
                }
            }
        }
    });
}

function activeUser(id) {
    url = base + "user/active/" + id;
    data = {};
    
    bootbox.dialog({
        message: '<div class="alert alert-info" style="margin:10px"><span class="glyphicon glyphicon-info-sign"></span>&nbsp;你确定要激活此用户&nbsp;' + '?</div>',
        title: "激活用户",
        buttons: {
        	cancel: {
                label: "取消",
                className: "btn-danger btn-round",
                callback: function() {
                	$(grid_selector).trigger("reloadGrid");
                }
            },
            main: {
                label: "确定",
                className: "btn-success btn-round",
                callback: function() {
                	 $.post(url, data,function(response) {
    	                if (response == "") {
    	                    showMessage("激活用户异常！");
    	                } else {
    	                    if (response.success) {
    	                        showMessage("激活用户成功！");
    	                    } else {
    	                        showMessage(response.message);
    	                    }
    	                }
    	                $(grid_selector).trigger("reloadGrid");
    	            });
                }
            }
        }
    });
}

//
function modifyUserWin(id, name, mail, phone, company) {

	$('#user_oldname_edit').val(name);
    $("#user_id_edit").val(id);
    $("#user_name_edit").val(name);
    //$("#user_pass_edit").val( pwd);
    $("#user_mail_edit").val(mail);
    $("#user_phone_edit").val(phone);
    $("#user_company_edit").val(company);

    $("#modifyUserModal").modal('show');
}

//
function searchUsers() {
    var userName = $('#search_user').val();
    jQuery(grid_selector).jqGrid('setGridParam', {
        url: base + 'user/all?userName=' + userName
    }).trigger("reloadGrid");
}

//
function authToUsers(users, roles) {
    if (users == null) {
        showMessage("请选择用户！");
        $(grid_selector).trigger("reloadGrid");
    } else {
        var url = base + 'user/authToUser';
        data = {
            users: users,
            roles: roles
        };
        $('#authToUserModal').modal('hide');
        $.post(url, data,
        function(response) {
            $(grid_selector).trigger("reloadGrid");
            if (response == "") {
                showMessage("用户赋予角色异常！");
            } else {
                if (response.success) {
                    showMessage("用户赋予角色成功！");
                } else {
                    showMessage("用户赋予角色失败！");
                }
            }
        });
    }
}

//
function getRolelist(userId) {
    $("#rolebody").html("");
    $.ajax({
        url: base + 'role/userRoleList',
        data: {
            userId: userId
        },
        dataType: "json",
        success: function(obj) {
            var tableStr = "";
            $.each(obj,
            function(index, json) {
                var isused = json.isUsed;
                var box = "";
                if (isused == 1) {
                    box = '<input type="checkbox" name="checkbox_role" checked>';
                } else {
                    box = '<input type="checkbox" name="checkbox_role">';
                }
                var thistr = '<tr rowid="' + json.roleId + '">';
                thistr += '<td class="rcheck">' + box + '</td><td name="id">' + json.roleId + '</td><td name="name">' + json.roleName + '</td></tr>';
                tableStr += thistr;
            });
            $('#rolebody').html(tableStr);
        }
    });
}

function AdvancedSearchUsers() {
	/** @bug152_begin 清空用户多选的参数 */
	while ($("#params li").length > 1) {
		$("#remove-param").parent().remove();
	}
	/* 隐藏高级查询第一行的删除打叉按钮 */
	$("#params li:first").find("#remove-param").hide();
	/** 打开高级搜索窗口，之前输入全部清空 */
	$("#params li:first").find("#search_user_value").val("");
	$("#params li:first").find("#meter").val("0");
	/** @bug152_finish */

	$('#advancedSearchUserModal').modal('show');
}
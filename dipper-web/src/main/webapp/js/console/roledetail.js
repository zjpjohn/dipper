window.onload = createZTreeFun(); //初始化加载权限树
function createZTreeFun() {
    var authTreeData = [];
    var setting = {
        data: {
            simpleData: {
                idKey: "id",
                pIdKey: "pId",
                enable: true
            }
        }
    };
    var roles = $("#roleInfo_roleId").val();
    $.ajax({
        url: base + "auth/treeByRoleId",
        //要加载数据的地址
        data: {
            roleId: roles
        },
        type: 'get',
        //加载方式
        dataType: 'json',
        //返回数据格式
        async: false,
        success: function(response) {
            if (response == "") {
                return false;
            } else {
                authTreeData = response.data;
            }
        },
        error: function(response) {
            console.log(response);
        }
    });
    var tree = $.fn.zTree.init($("#roleAuthTree"), setting, authTreeData);
    tree.expandAll(true);
}
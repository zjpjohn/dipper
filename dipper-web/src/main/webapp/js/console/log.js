var grid_selector = "#log_list";
var page_selector = "#log_page";
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
        url: base + 'log/list',
        datatype: "json",
        height: '100%',
        autowidth: true,
        colNames: ['操作对象', '实际动作', '用户ID', '用户名称', '用户IP', '操作结果', '添加日期'],
        colModel: [{
            name: 'logObject',
            index: 'logObject',
            width: 10
        },
        {
            name: 'logAction',
            index: 'logAction',
            width: 15
        },
        {
            name: 'userId',
            index: 'userId',
            width: 5,
            hidden: true
        },
        {
            name: 'userName',
            index: 'userName',
            width: 5
        },
        {
            name: 'userIp',
            index: 'userIp',
            width: 10
        },
        {
            name: 'logResult',
            index: 'logResult',
            width: 10
        },
        {
            name: 'logCreatetime',
            index: 'logCreatetime',
            width: 15
        }],
        viewrecords: true,
        rowNum: 10,
        rowList: [10, 20, 50, 100, 1000],
        pager: page_selector,
        altRows: true,
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
    $('#start_time').datetimepicker({
        format: 'YYYY-MM-DD HH:mm:00'
    }).next().on(ace.click_event,
    function() {
        $(this).prev().focus();
    });
    $('#end_time').datetimepicker({
        format: 'YYYY-MM-DD HH:mm:00'
    }).next().on(ace.click_event,
    function() {
        $(this).prev().focus();
    });
});

function searchLogs() {
    start = $('#start_time').val();
    end = $('#end_time').val();
    target = $('#target').val();
    var imageName = $('#search_text').val();
    jQuery(grid_selector).jqGrid('setGridParam', {
        url: base + 'log/list?from=' + start + '&to=' + end + '&target=' + target
    }).trigger("reloadGrid");
}
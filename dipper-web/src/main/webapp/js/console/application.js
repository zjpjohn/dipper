var grid_selector = "#app_list";
var page_selector = "#app_page";
/* 保存原始的应用名称信息 */
var original_app_name = "";
/* 保存原始的应用URL信息 */
var original_app_url = "";
var appInfos = "";

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
	$(document).on('settings.ace.jqGrid', function(ev, event_name, collapsed) {
		if (event_name === 'sidebar_collapsed'
				|| event_name === 'main_container_fixed') {
			setTimeout(function() {
				$(grid_selector).jqGrid('setGridWidth',
						parent_column.width());
			}, 0);
		}
	});
	jQuery(grid_selector).jqGrid({
		url : base + 'app/list',
		datatype : "json",
		height : '100%',
		autowidth : true,
		colNames : [ 'APP_ID', '应用名称', '应用状态', '访问路径', '应用描述', '创建日期', '快捷操作' ],
		colModel : [
				{
					name : 'appId',
					index : 'appId',
					width : 10,
					hidden : true
				},
				{
					name : 'appName',
					index : 'appName',
					width : 10,
					formatter : function(cell, opt, obj) {
						return '<i class="fa fa-cubes"></i><a href="'
								+ base
								+ 'app/detail/'
								+ obj.appId
								+ '.html">'
								+ cell
								+ '</a>';
					}
				},
				{
					name : 'appStatus',
					index : 'appStatus',
					width : 5,
					formatter : function(cellvalue, options,
							rowObject) {
						switch (cellvalue) {
						case 0:
							return '<i class="fa fa-stop text-danger">&nbsp;删除</i>';
						case 1:
							return '<i class="fa fa-play-circle text-success"><b>&nbsp;正常</b></i>';
						default:
							return '未知';
						}
					}
				},
				{
					name : 'appUrl',
					index : 'appUrl',
					width : 15
				},
				{
					name : 'appDesc',
					index : 'appDesc',
					width : 15
				},
				{
					name : 'appCreatetime',
					index : 'appCreatetime',
					width : 10
				},
				{
					name : '',
					title : false,
					index : '',
					width : 150,
					fixed : true,/* 固定像素长度 */
					sortable : false,
					resize : false,
					formatter : function(cellvalue, options,
							rowObject) {
						var strHtml = "";
//						var upda = $("#update_app").val();
//						if (typeof (upda) != "undefined") {
							strHtml += "<button class=\"btn btn-primary btn-xs btn-round\" onclick=\"edit('"
									+ rowObject.appId
									+ "')\"><i class=\"ace-icon fa fa-pencil align-top\"></i>&nbsp;<b>编辑</b></button>&nbsp;"
//						}
						var dele = $("#delete_app").val();
						if (typeof (dele) != "undefined") {
							strHtml += "<button class=\"btn btn-inverse btn-xs btn-round\" onclick=\"deleteApp('"
									+ rowObject.appId
									+ "','"
									+ rowObject.appName
									+ "')\"><i class=\"ace-icon fa fa-trash-o\"></i>&nbsp;<b>删除</b></button>&nbsp;";
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
	$(window).triggerHandler('resize.jqGrid');// 窗口resize时重新resize表格，使其变成合适的大小
	jQuery(grid_selector).jqGrid(// 分页栏按钮
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
		$('.ui-pg-table:not(.navtable) > tbody > tr > .ui-pg-button > .ui-icon').each(function(){
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

	/* （创建）应用取消操作 */
	$('#add_cancel').click(function(e) {
		e.preventDefault();
		create_app_cancel();
		location.reload();
	});
	
	//构建应用的信息
	$('#modal-wizard .modal-header').ace_wizard().on('change',function(e, info) {
	    if (info.step == 1) { // 第一步需要执行的操作
	    	if(info.direction == 'next'){
	    		if(!$("#app_basic_form").valid()){
	        		return false;
	        	}
	    		
	    		var appinfo_step1='';
	        	//名称
	     		var name = $('#app_name').val();
	     		if(name.trim() !== ''){
	     			appinfo_step1 += '<div class="item"><label>应用名称：</label> <label>'+name+'</label></div>';
	     		}
	     		
	     		//应用负载
	     		var rb = $('#app_balance option:selected').text();
	     		var rv=Number($('#app_balance option:selected').val());
	     		if(rb.trim() !== ''){
	     			if(rv!=-1){
	     				if(!(checkAppurl()&&checkAppport())){
		        			return false;
		        		}
	     				
	     				appinfo_step1 += '<div class="item"><label>应用负载：</label> <label>'+rb+'</label></div>';
	     				
	     				//应用端口
	    	     		var port = $('#app_port').val();
	    	     		if(port.trim() !== ''){
	    	     			appinfo_step1 += '<div class="item"><label>应用端口：</label> <label>'+port+'</label></div>';
	    	     		}
	    	     		
	    	     		//访问路径
	    	     		var url = $('#app_url').val();
	    	     		if(url.trim() !== ''){
	    	     			appinfo_step1 += '<div class="item"><label>访问路径：</label> <label>'+url+'</label></div>';
	    	     		}
	     			}
	     		}
	     		
	    		
	     		
	     		//环境变量
	     		var appenv = '';
	     		$.each($('#env_params li'), function(){
	     			appenv += $(this).find('#app_env').val()+' ';
	     		});
	     		if(appenv.trim() !== ''){
	     			appinfo_step1 += '<div class="item"><label>环境变量：</label> <label>'+appenv+'</label></div>';
	     		}
	    		//挂载点
	     		var volume='';
	     		$.each($('#vol_params li'), function(){
	     			var hostVolume = $(this).find('#host_volume').val();
	     			var containerVolume = $(this).find('#container_volume').val();
	     			volume += hostVolume + ":" + containerVolume + " ";
	     		});
	     		if(volume.trim() !== ':'){
	     			appinfo_step1 += '<div class="item"><label>挂载点：</label> <label>'+volume+'</label></div>';
	     		}
	    		//其他参数
	     		var param='';
	     		$.each($("#params li"), function(){
	            	var pk = $(this).find("#meter option:selected").val();
	            	if(pk!=0){
	            		var pConn = $(this).find("#meter option:selected").attr("conn");
	            		pConn = pConn==0?' ':'=';
	            		var pv = $(this).find("#param_value").val();
	            		param += (pk+pConn+pv+",");
	            	}
	            	
	            });
	     		if(param.trim() !== ''){
	     			appinfo_step1 += '<div class="item"><label>其他参数：</label> <label>'+param+'</label></div>';
	     		}
	     		//启动命令
	     		var command = $('#app_command').val();
	     		if(command.trim() !== ''){
	     			appinfo_step1 += '<div class="item"><label>启动命令：</label> <label>'+command+'</label></div>';
	     		}
	     		
	    		//描述信息
	     		var desc = $('#app_desc').val();
	     		if(desc.trim() !== ''){
	     			appinfo_step1 += '<div class="item"><label>描述信息：</label> <label>'+desc+'</label></div>';
	     		}
	     		
	     		//信息展示
	     		$('#app_info>#appinfo_step1').remove();
	     		$('#app_info').append('<div id="appinfo_step1">'+appinfo_step1+'</div>');
	    	}
	    } else if (info.step == 2) { // 第二步需要执行的操作
	        if (info.direction == 'next') {
	        	if(!valid_app_env_form()){
	    			return false;
	    		}
	        	var appinfo_step2="";
	        	
	    		//环境
	    		var activeEnvs = $('#activateEnv input[name="btn-env"]');
	    		var env = '';
	    		for(var i=0; i<activeEnvs.length; i++){
     				env += $(activeEnvs[i]).attr('text') + (i+1==activeEnvs.length ? '' : ',');
	     		}
	    		if(env.trim() !== ''){
	    			appinfo_step2 += '<div class="item"><label>环境约束：</label> <label>'+env+'</label></div>';
	    		}
	    		//集群
	     		var activeClusters = $('#activeCluster input[name="btn-cluster"]');
	     		var cluster = '';
	     		for(var i=0; i<activeClusters.length; i++){
     				cluster += $(activeClusters[i]).attr('text') + (i+1==activeEnvs.length ? '' : ',');
	     		}
	     		if(cluster.trim() !== ''){
	     			appinfo_step2 += '<div class="item"><label>集群约束：</label> <label>'+cluster+'</label></div>';
	    		}
	    		//资源
	     		//$('input:radio[name="cpu-radio"]:checked').val();
	     		//var resource = $('#fixed input[name="app_resource"]:checked');
	     		var cpu = $('input:radio[name="cpu-radio"]:checked').val();
	     		var mem = $('input:radio[name="mem-radio"]:checked').val();
	     		if(cpu !== undefined && mem !== undefined){
	     			appinfo_step2 += '<div class="item"><label>资源约束：</label> <label>cpu：'+cpu+'核 , 内存：'+mem+'G</label></div>';
	     		}
	        	
	     		//信息展示
	        	$('#app_info>#appinfo_step2').remove();
	     		$('#app_info').append('<div id="appinfo_step2">'+appinfo_step2+'</div>');
	        } else if (info.direction = 'previous') {
	        }
	    } else if(info.step ==3) {// 第三步执行操作
	    	if (info.direction == 'next') {
	    		if(!$("#app_other_form").valid()){
	    			return false;
	    		}
	    		
	    		var appinfo_step3='';
	    		//监测代理
	     		var monitorProxy = $('#app_proxy_ip').val();
	     		if(monitorProxy.trim() !== '0'){
	     			appinfo_step3 += '<div class="item"><label>监测代理：</label> <label>'+monitorProxy+'</label></div>';
	     		}
	     		if($('#app_health').is(':checked')){
	     			appinfo_step3 += '<div class="item"><label>健康检查：</label> <label>是</label></div>';
	     		}
	     		if($('#app_monitor').is(':checked')){
	     			appinfo_step3 += '<div class="item"><label>监控开关：</label> <label>是</label></div>';
	     		}
	     		
	     		//信息展示
	     		$('#app_info>#appinfo_step3').remove();
	     		$('#app_info').append('<div id="appinfo_step3">'+appinfo_step3+'</div>');
	    	} else if (info.direction = 'previous') {
	    	}
	    } 
	}).on('finished',function(e) { // 最后一步需要执行的操作
		//提交操作
		e.preventDefault();
		var activeEnvs = $('#activateEnv input[name="btn-env"]');
		var env = '';
		for(var i=0; i<activeEnvs.length; i++){
			env += $(activeEnvs[i]).val() + (i+1==activeEnvs.length ? '' : ',');
 		}
		//集群
 		var activeClusters = $('#activeCluster input[name="btn-cluster"]');
 		var cluster = '';
 		for(var i=0; i<activeClusters.length; i++){
			cluster += $(activeClusters[i]).val() + (i+1==activeClusters.length ? '' : ',');
 		}
 		console.log(cluster);
		//资源
/* 		var resource = $('#fixed input[name="app_resource"]:checked');
 		var cpu = resource.attr('cpu');
 		var mem = resource.attr('mem');
*/ 		var cpu = $('input:radio[name="cpu-radio"]:checked').val();
 		var mem = $('input:radio[name="mem-radio"]:checked').val();
 		mem = parseInt(mem)*1024;
 		//名称
 		var name = $('#app_name').val();
 		//应用端口
 		var port = $('#app_port').val();
 		//环境变量
 		var appenv = '';
 		$.each($('#env_params li'), function(){
 			var env_value = $(this).find('#app_env').val();
 			if(env_value !== ""){
 				appenv += "-e "+env_value+' ';
 			}
 			
 		});
 		//挂载点
 		var volume='';
 		$.each($('#vol_params li'), function(){
 			var hostVolume = $.trim($(this).find('#host_volume').val());
 			var containerVolume =$.trim($(this).find('#container_volume').val());
 			if(hostVolume!=''&&containerVolume!=''){
 				volume += hostVolume + ":" + containerVolume + " ";
 			}
 		});
 		//其他参数
 		var param='';
 		$.each($("#params li"), function(){
        	var pk = $(this).find("#meter option:selected").val();
        	if(pk!=0){
        		var pConn = $(this).find("#meter option:selected").attr("conn");
        		pConn = pConn==0?' ':'=';
        		var pv = $(this).find("#param_value").val();
        		param += (pk+pConn+pv+";");
        	}
        	
        });
 		param=param.substring(0,param.length-1);
 		//启动命令
 		var command = $('#app_command').val();
 		//访问路径
 		var url = $('#app_url').val();
 		//应用负载
 		var rb = $('#app_balance option:selected').val();
 		//描述信息
 		var desc = $('#app_desc').val();
 		//监测代理
 		var monitorProxy = $('#app_proxy_ip').val();
 		//健康检查开关
 		var appHealth = $('#app_health').is(':checked')==true ? 1 : 0;
 		//添加监控开关
 		var appMonitor = $('#app_monitor').is(':checked')==true ? 1 : 0;
 		var data = {
			envIds:env,
			clusterIds:cluster,
 			appCpu:cpu,
 			appMem:mem,
 			appName:name,
 			appPriPort:port,
 			appEnv:appenv,
 			appVolumn:volume,
 			appParams:param,
 			appHealth:appHealth,
 			appMonitor:appMonitor,
 			appCommand:command,
 			appUrl:url,
 			balanceId:rb,
 			appDesc:desc,
 			appProxy:monitorProxy
 		};
 		var appid=$('#cre_app_id').val();
 		if(appid==0){
 			url = base + 'app/add';
 		}else{
 			url = base + 'app/modify';
 			data.appId=appid;
 		}
		
		$.post(url, data, function(response){
			$('#modal-wizard').modal('hide');
			if (response == "") {
				if(url == base+'app/modify'){
					showMessage("修改应用信息异常！");
				}else{
					showMessage("创建应用异常！");
				}
				location.reload();
			} else {
				showMessage(response.message, function(){
					location.reload();
				});
			}
		});
		$('.modal-backdrop').hide();
        $('#modal-wizard .modal-header').ace_wizard({
            step: 1
        });
        $('#modal-wizard .wizard-actions').show();
	});
 	$('#modal-wizard .wizard-actions .btn[data-dismiss=modal]').removeAttr('disabled');
	
 	//环境约束
 	$('#add-env').click(function(){
 		var blockEnvs = $('#blockEnv input[name="btn-env"]');
 		var envs = new Array();
 		for (i in blockEnvs) {
 			if (blockEnvs[i].checked) {
 				var env = $(blockEnvs[i]).parent('label');
 				envs.push(env);
 			}
 		}
 		if(envs.length <= 0){
 			showMessage("请选择要增加的环境");
 			return;
 		}
 		for (i in blockEnvs) {
 			if (blockEnvs[i].checked) {
 				$(blockEnvs[i]).parent('label').remove();
 			}
 		}
 		for(i in envs){
 			$('#activateEnv').append(envs[i]);
 		}
 		blockEnvs = document.getElementsByName("btn-env");
 		for (i in blockEnvs) {
			blockEnvs[i].checked = false;
 		}
 	});
 	
 	//解除环境约束
 	$('#remove-env').click(function(){
 		//获取某个固定div下的
 		var activeEnvs = $('#activateEnv input[name="btn-env"]');
 		var envs = new Array();
 		var appId=$('#cre_app_id').val();
 		for (i in activeEnvs) {
 			if (activeEnvs[i].checked) {
 				var env = $(activeEnvs[i]).parent('label');
 				//检测环境依赖关系
 				var envId = $(activeEnvs[i]).val();
 				if(!checkAppInEnv(appId, envId)){
 					showMessage($(activeEnvs[i]).attr('text')+"存在应用的实例，不允许移出");
 					return;
 				}
 				envs.push(env);
 			}
 		}
 		if(envs.length <= 0){
 			showMessage("请选择要移除的环境");
 			return;
 		}
 		for (i in activeEnvs) {
 			if (activeEnvs[i].checked) {
 				$(activeEnvs[i]).parent('label').remove();
 			}
 		}
 		for(i in envs){
 			$('#blockEnv').append(envs[i]);
 		}
 		activeEnvs = document.getElementsByName("btn-env");
 		for (i in activeEnvs) {
 			activeEnvs[i].checked = false;
 		}
 	});
 	
 	//集群约束
 	$('#add-cluster').click(function(){
 		var blockClusters = $('#blockCluster input[name="btn-cluster"]');
 		var clusters = new Array();
 		for (i in blockClusters) {
 			if (blockClusters[i].checked) {
 				var cluster = $(blockClusters[i]).parent('label');
 				clusters.push(cluster);
 			}
 		}
 		if(clusters.length <= 0){
 			showMessage("请选择要增加的集群");
 			return;
 		}
 		for (i in blockClusters) {
 			if (blockClusters[i].checked) {
 				$(blockClusters[i]).parent('label').remove();
 			}
 		}
 		for(i in clusters){
 			$('#activeCluster').append(clusters[i]);
 		}
 		blockClusters = document.getElementsByName("btn-cluster");
 		for (i in blockClusters) {
 			blockClusters[i].checked = false;
 		}
 	});
 	
 	//解除集群约束
 	$('#remove-cluster').click(function(){
 		//获取某个固定div下的
 		var activeClusters = $('#activeCluster input[name="btn-cluster"]');
 		var clusters = new Array();
 		var appId=$('#cre_app_id').val();
 		for (i in activeClusters) {
 			if (activeClusters[i].checked) {
 				var cluster = $(activeClusters[i]).parent('label');
 				//检测环境依赖关系
 				var clusterPort = $(activeClusters[i]).attr('port');
 				if(!checkAppInCluster(clusterPort, appId)){
 					
 					showMessage($(activeClusters[i]).attr('text')+"存在应用的实例，不允许移出");
 					return;
 				}
 				clusters.push(cluster);
 			}
 		}
 		if(clusters.length <= 0){
 			showMessage("请选择要移除的集群");
 			return;
 		}
 		for (i in activeClusters) {
 			if (activeClusters[i].checked) {
 				$(activeClusters[i]).parent('label').remove();
 			}
 		}
 		for(i in clusters){
 			$('#blockCluster').append(clusters[i]);
 		}
 		activeClusters = document.getElementsByName("btn-cluster");
 		for (i in activeClusters) {
 			activeClusters[i].checked = false;
 		}
 	});
 	
 	//资源约束
 	$(".resource").change(function() {
 		$("input[name='app_resource']").each(function(){
 		    if($(this).is(':checked')){
 		    	$(this).parent("label").removeClass("btn-primary");
 		    	$(this).parent("label").removeClass("active");
 		    	$(this).parent("label").addClass("btn-success");
 		    }else{
 		    	$(this).parent("label").addClass("btn-primary");
 		    	$(this).parent("label").removeClass("btn-success");
 		    }
 		 });
	});
 	
 	//添加环境变量
 	$("#env-plus").on('click', function(event) {
		$("#env_params li:first").clone(true).appendTo("#env_params");
		$("#env_params li").not(":first").find("#env-minus").show();
		$("#env_params li").not(":last").find("#env-plus").hide();
		$("#env_params li:first").find("#env-minus").hide();
		$("#env_params li:last").find("#env-plus").show();
		$("#env_params li:last").find("#app_env").val("");
	});
 	
 	//删除一个环境变量
 	$("#env-minus").click(function(){
 		if ($("#env_params li").length > 1) {
 			$(this).parent().remove();
 		}
 		$("#env_params li:last").find("#env-plus").css('display','');
 	});
 	//添加挂载点
 	$("#vol-plus").on('click', function(event) {
		$("#vol_params li:first").clone(true).appendTo("#vol_params");
		$("#vol_params li").not(":first").find("#vol-minus").show();
		$("#vol_params li").not(":last").find("#vol-plus").hide();
		$("#vol_params li:first").find("#vol-minus").hide();
		$("#vol_params li:last").find("#vol-plus").show();
		$("#vol_params li:last").find("#host_volume").val("");
		$("#vol_params li:last").find("#container_volume").val("");
	});
 	
 	//删除一个挂载点
 	$("#vol-minus").click(function(){
 		if ($("#vol_params li").length > 1) {
 			$(this).parent().remove();
 		}
 		$("#vol_params li:last").find("#vol-plus").css('display','');
 	});
 	
 	//添加其他参数
 	$("#param-plus").on('click', function(event) {
		$("#params li:first").clone(true).appendTo("#params");
		$("#params li").not(":first").find("#param-minus").show();
		$("#params li").not(":last").find("#param-plus").hide();
		$("#params li:first").find("#param-minus").hide();
		$("#params li:last").find("#param-plus").show();
		$("#params li:last").find("#param_value").val("");
		$("#params li:last").find("#param_value").attr("placeholder","");
	});
 	
 	//删除一个其他参数
 	$("#param-minus").click(function(){
 		if ($("#params li").length > 1) {
 			$(this).parent().remove();
 		}
 		$("#params li:last").find("#param-plus").css('display','');
 	});
 	
 	//根据选择提示输入信息
    $("#meter").change(function(){
    	var comment = $(this).children('option:selected').attr('comment');
    	var type = $(this).children('option:selected').attr('type');
    	if(type == 0){
    		$(this).parent().next().attr('placeholder','参数不需要输入值');
    		$("#param_value").attr("disabled",true);
    	}else{
    		$("#param_value").attr("disabled",false);
    		$(this).parent().next().attr("placeholder", comment);
    		
    	}
    });
 	
 	//取消操作
 	$('#add_cancel').click(function(){
 		$("input[name='app_resource']").parent("label").addClass("btn-primary");
 		$("input[name='app_resource']").parent("label").removeClass("active");
    	$("input[name='app_resource']").parent("label").removeClass("btn-success");
 		$('#app_basic_form')[0].reset();
        $('label.error').remove();
 	});
 	
 	//替换实例
 	$('#replace_num').ace_spinner({
    	value:1,
    	min:1,
    	max:100,
    	step:1, 
    	on_sides: true, 
    	icon_up:'ace-icon fa fa-plus smaller-75', 
    	icon_down:'ace-icon fa fa-minus smaller-75', 
    	btn_up_class:'btn-success' , 
    	btn_down_class:'btn-danger'
    });

 	//百分比替换
 	$('#replace_percent').ace_spinner({
    	value:1,
    	min:1,
    	max:50,
    	step:1, 
    	on_sides: true, 
    	icon_up:'ace-icon fa fa-plus smaller-75', 
    	icon_down:'ace-icon fa fa-minus smaller-75', 
    	btn_up_class:'btn-success' , 
    	btn_down_class:'btn-danger'
    });
 	
 	//构建应用选择负载后的操作
 	$('#app_balance').change(function(){
 		if(Number($(this).val())==-1){
 			$('.imp_input').hide();
 			$('#app_url,#app_port').off('keyup').val('');
 		}else{
 			$('.imp_input').show();
 			$('#app_url').on('keyup',function(){
 				checkAppurl();
 			})
 			$('#app_port').on('keyup',function(){
 				checkAppport();
 			})
 		}
 	})
 	
 	/**
	 * 添加高级搜索的参数项
	 */
	/** @bug68594:高级查询功能增加条列数目没有限制 */
	/* 获取查询条件的总数 */
	advanceColNum = $('#app_meter option').length - 1;

	$("#app_add-param").on('click', function(event) {
		/* 获取当前已经添加的列数量 */
		var selNum = $('#app_params li').length + 1;
		event.preventDefault();
		$("#app_params li:first").clone(true).appendTo("#app_params");
		$("#app_params li").not(":first").find("#app_remove-param").show();
		$("#app_params li:first").find("#app_remove-param").hide();
		var str = $("#app_params li:last").find("#app_meter").val();
		/** @bug152_begin 新增查询参数时，新增栏参数内容置空 */
		$("#app_params li:last").find("#app_param_value").val("");
		/** @bug152_finish */
		/* 当添加的列数量与参数数量相等数，隐藏添加条件按钮 */
		if (selNum >= advanceColNum) {
			$("#app_add-param").hide();
		}
	});

	/**
	 * 删除高级索索的参数项
	 */
	$("#app_remove-param").on('click', function(event) {
		/* 获取当前已经添加的列数量 */
		var selNum = $('#app_params li').length;
		event.preventDefault();
		/* 判断当列数量小于参数数量的时候，显示添加条件按钮 */
		if (selNum <= advanceColNum) {
			$("#app_add-param").show();
		}
		if ($("#app_params li").length > 1) {
			$(this).parent().remove();
		}
	});
 	
    /**
	 * 向查询按钮添加请求提交操作 删除高级索索的参数项
	 */
    $("#app_advanced_search").on('click',
    function(event) {
        /* 保存各项栏目的名称数组 */
        var column_array = new Array();
        /* 保存用户填写的各项信息数组 */
        var value_array = new Array();

        /* 获取选择栏目的名称 */
        $("select[name=app_meter]").each(function() {
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
		$("input[name=app_param_value]").each(function() {
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
            url: base + 'app/advancedSearch?params=' + column_array + '&values=' + value_array
        }).trigger("reloadGrid");

				/** @bug152_begin 清空用户多选的参数 */
				while ($("#app_params li").length > 1) {
					$("#app_remove-param").parent().remove();
				}
				/** @bug152_finish */

				$('#advanSearchApplicationModal').modal('hide');
				$('#advanced_search_application_frm')[0].reset();
			});


    /**
	 * 向高级搜索的取消按钮添加重置隐藏
	 */

    $("#app_advanced_cancel").on('click',
    function(event) {
        event.preventDefault();
        $('#advanSearchApplicationModal').modal('hide');
        $('#advanced_search_application_frm')[0].reset();
    });
    
});


function checkAppurl(){
	$('#app_url').next().remove();
	
	var appurl=$.trim($('#app_url').val());
	if(appurl==""){
		$('#app_url').after('<label class="error" for="app_url">应用访问路径不能为空</label>');
		return false;
	}else{
		var reg=/^[A-Za-z0-9_\-\.\/]+$/;
		if(!reg.test(appurl)){
			$('#app_url').after('<label class="error" for="app_url">只能包含(字母、数字、下划线、破折号)合法字符</label>');
			return false;
		}else{
			var rb = $('#app_balance option:selected').text();
     		var rv=Number($('#app_balance option:selected').val());
     		var flag=false;
			$.ajax({
		        type: 'post',
		        url: base + 'app/checkAppUrl',
		        data:{
		        	appUrl:appurl,
		        	balanceId:rv
		        },
		        dataType: 'json',
		        async:false,
		        success: function(data) {
		            if(!data){
		            	$('#app_url').after('<label class="error" for="app_url">负载'+rb+'中已存在该访问url，请重新填写。</label>');
		            }else{
		            	flag=true;
		            }
		        }
		    });
			return flag;
		}
	}
}

function checkAppport(){
	$('#app_port').next().remove();
	var appport=$.trim($('#app_port').val());
	
	if(appport==""){
		$('#app_port').after('<label class="error" for="app_url">应用端口不能为空</label>');
		return false;
	}else{
		var reg=/^[1-9]$|(^[1-9][0-9]$)|(^[1-9][0-9][0-9]$)|(^[1-9][0-9][0-9][0-9]$)|(^[1-5][0-9][0-9][0-9][0-9]$)|(^[6][0-5][0-5][0-3][0-5]$)/;
		if(!reg.test(appport)){
			$('#app_port').after('<label class="error" for="app_url">端口输入错误，请参考1~65535之间的数字</label>');
			return false;
		}else{
			return true;
		}
	}
}
//创建应用取消按钮操作
function create_app_cancel(){
	$('#modal-wizard').modal('hide');
	$('#app_env_form')[0].reset();
	$('#app_basic_form')[0].reset();
	$('#app_other_form')[0].reset();
	$('label.error').remove();
}
//创建应用弹出框
function showCreateModal(){
	//创建应用appid设置为0，更改则大于0
	$('#cre_app_id').val(0);
	$(':button[class="btn btn-success btn-round btn-sm btn-next"]').attr('data-last','创建');
	//获取应用的环境
 	getEnvs();
 	//构建应用的集群
 	getClusters();
 	//获取负载列表
	getBalance();
	//获取监控代理列表
	getAppProxy();
	
	create_app_cancel();
	$('#activateEnv').html('');
	$('#activeCluster').html('');
	$('#app_balance').val(0);
	$('#app_proxy_ip').val(0);
	
	//资源约束
	$("input[name='app_resource']").parent("label").addClass("btn-primary");
	$("input[name='app_resource']").parent("label").removeClass("active");
	$("input[name='app_resource']").parent("label").removeClass("btn-success");
	
	//参数配置
	getParams();
	$('#modal-wizard').modal('show');
}


function getEnvs(){
	$('#blockEnv').html('');
	$.ajax({
        type: 'get',
        url: base + 'env/listAll',
        dataType: 'json',
        success: function(array) {
            $.each(array, function(index, obj) {
                var envId = obj.envId;
                var envName = decodeURIComponent(obj.envName);
                $('#blockEnv').append('<label class="btn btn-round btn-white btn-primary">'+
                		'<input type="checkbox" name="btn-env" text="'+envName+'" value="'+envId+'">&nbsp;&nbsp;'+envName+'</label>');
            });
        }
    });
}

function getClusters() {
	$('#blockCluster').html('');
    $.ajax({
        type: 'get',
        url: base + 'cluster/all',
        dataType: 'json',
        success: function(array) {
            $.each(array,
            function(index, obj) {
                var clusterId = obj.clusterId;
                var clustername = decodeURIComponent(obj.clusterName);
                $('#blockCluster').append('<label class="btn btn-round btn-white btn-primary">'+
                		'<input type="checkbox" name="btn-cluster" port="'+obj.clusterPort+'" text="'+clustername+'" value="'+clusterId+'">&nbsp;&nbsp;'+clustername+'</label>');
            });
        }
    });
}

function getBalance() {
	$('#app_balance').html("<option value='-1' checked>请选择负载</option>");
	$.ajax({
        type: 'get',
        url: base + 'loadbalance/listAll',
        dataType: 'json',
        success: function(array) {
            $.each(array, function(index, obj) {
                var lbId = obj.lbId;
                var lbName = decodeURIComponent(obj.lbName);
                $('#app_balance').append('<option value="' + lbId + '">' + lbName + '</option>');
            });
        }
    });
}

/**
 * Get parameters list
 * 
 */
function getParams() {
	$('.param-meter[name="meter"]').html("<option value='0'>启动参数</option>");
    $.ajax({
        type: 'get',
        url: base + 'param/allValue',
        dataType: 'json',
        async:false,
        success: function(array) {
            $.each(array,function(index, obj) {
                var pv = obj.paramValue;
                var pn = decodeURIComponent(obj.paramName);
                var pr = obj.paramReusable;
                var pc = obj.paramConnector;
                var pt = obj.paramType;
                var comment = obj.paramComment;
                $('.param-meter[name="meter"]').append('<option type="'+pt+'" value="'+pv+'" conn="'+pc+'" comment="'+comment+'">' + pn + '</option>');
            	
            });
        }
    });
}

//获取监控代理列表
function getAppProxy() {
	$('#app_proxy_ip').html("<option value='0' checked>请选择监控代理</option>");
	$.ajax({
        type: 'get',
        url: base + 'mntrproxy/all',
        dataType: 'json',
        success: function(array) {
            $.each(array, function(index, obj) {
                var mpName = decodeURIComponent(obj.mpName);
                var mp=obj.mpIP+':'+obj.mpPort;
                $('#app_proxy_ip').append('<option value='+mp+'>' + mpName+'&nbsp;&nbsp;&nbsp;&nbsp;('+mp+')'+ '</option>');
            });
        }
    });
}

function deleteApp(app_id, app_name) {
	bootbox.dialog({
        message: '<div class="alert alert-info" style="margin:10px"><span class="glyphicon glyphicon-info-sign"></span>&nbsp;删除应用&nbsp;' + app_name + '?</div>',
        title: "删除应用",
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
                	url = base + "app/remove";
                	data = {appId : app_id};
                	$.post(url, data, function(response) {
        				if (response == "") {
        					showMessage('删除应用异常');
        				}else{
        					showMessage(response.message, function(){
        						$(grid_selector).trigger("reloadGrid");
        					});
        				}
        			});
                }
            }
        }
    });
}
//编辑应用信息
function edit(appid){
	$('#old_app_name').val('');
	$('#cre_app_id').val(appid);
	$(':button[class="btn btn-success btn-round btn-sm btn-next"]').attr('data-last','确定');
	//获取应用的环境
 	getEnvs();
 	//构建应用的集群
 	getClusters();
 	//获取负载列表
	getBalance();
	//获取监控代理列表
	getAppProxy();
	//参数配置
	getParams();
	$.ajax({
        type: 'get',
        url: base + 'app/getOne',
        data:{
        	appid:appid
        },
        dataType: 'json',
        success: function(data) {
        	if(data==""){
        		showMessage("服务器异常！");
        	}else{
        		$("#modal-wizard").modal('show');
        		load_updata_data(data);
        	}
        }
    });
}

function load_updata_data(data){
	var app=data.app,
		clusters=data.clusterList,
		envs=data.envList;
	
	//环境约束
	 $.each(envs, function(index, obj) {
         var envId = obj.envId;
         var $lable=$('#blockEnv :checkbox[value='+envId+']').parent();
         $('#activateEnv').append($lable);
     });
	 //集群约束
	 $.each(clusters,function(index, obj) {
            var clusterId = obj.clusterId;
            var $lable=$('#blockCluster :checkbox[value='+clusterId+']').parent();
            $('#activeCluster').append($lable);
        });
	 //资源约束
	 var $resources = $('#fixed input[name="app_resource"]');
	 var cpu=app.appCpu;
	 $("input[name=cpu-radio][value="+cpu+"]").attr("checked",true);
	 var mem=parseInt(app.appMem)/1024;
	 $("input[name=mem-radio][value="+mem+"]").attr("checked",true);
	 
	 //基础配置信息
	 $('#app_name').val(app.appName);
	 $('#old_app_name').val(app.appName);
	 $('#app_port').val(app.appPriPort);
	 //环境变量
	 var appEnvs=$.trim(app.appEnv).split(' ');
	 var envs=new Array();
	 //过滤不必要的内容
	 for(var i=0;i<appEnvs.length/2;i++){
		 envs[i] = appEnvs[2*i+1];
	 }
	 for (var i = 0; i < envs.length; i++) {
		 if(i>0){
			 $('#env-plus').click();
		 }
		 $(':input[name="app_env"]').eq(i).val(envs[i]);
	 }
	 //挂载点
	 var appVolumn=$.trim(app.appVolumn).split(' ');
	 $(':input[name="host_volume"]:gt(0)').parent().remove();
	 for (var i = 0; i < appVolumn.length; i++) {
		 if(i>0){
			 $('#vol-plus').click();
		 }
		 var arr=appVolumn[i].split(":");
		 $(':input[name="host_volume"]').eq(i).val(arr[0]);
		 $(':input[name="container_volume"]').eq(i).val(arr[1]);
	 }
	
	 //其他参数显示有问题
	 if($.trim(app.appParams)!=''){
		 var appParms=$.trim(app.appParams).split(';');
		 $(':input[name="param_key"]:gt(0)').parent().remove();
		 for (var i = 0; i < appParms.length; i++) {
			 if(i>0){
				 $('#param-plus').click();
			 }
			 var arr=appParms[i].replace(new RegExp(/\s+/g),'=').split("=");
			 $('.param-meter[name="meter"]').eq(i).val(arr[0]);
			 $(':input[name="param_value"]').eq(i).val(arr[1]);
		 }
	 }
	
	 //启动命令
	 $('#app_command').val(app.appCommand);
	 //访问路径
	 $('#app_url').val(app.appUrl);
	 //应用负载
	 $('#app_balance').val(app.balanceId);
	 //描述信息
	 $('#app_desc').val(app.appDesc);
	 //检测代理
	 $('#app_proxy_ip').val(app.appProxy);
	 if(app.appMonitor==1){
		 $("[name = switch-monitor]:checkbox").attr("checked",true);
	 }
	 if(app.appHealth==1){
		 $("[name = switch-health]:checkbox").attr("checked",true);
	 }
	 
}

/**************************表单验证**************************/
//运行环境验证
function valid_app_env_form(){
	//环境约束
	 if($.trim($('#activateEnv').text())==""){
		 showMessage("请选择环境约束！");
		 return false;
	 }
	 //集群约束
	 if($.trim($('#activeCluster').text())==""){
		 showMessage("请选择集群约束！");
		 return false;
	 }
	 return true;
}
$(function(){
		//表单验证
//基础配置验证
	$("#app_basic_form").validate({
	    rules: {
	    	app_name: {
				required : true,
				isImageName : true,
				maxlength : 64,
				isNotNull : false,
				remote : {
					url : base + "app/checkAppName",
					type : "post",
					dataType : "json",
					data : {
						appName : function() {
							return $.trim($("#app_name").val());
						}
					},
					dataFilter : function(data) {
						var appid=$('#cre_app_id').val();
				 		if(appid==0){
				 			return data;
				 		}else{
				 			var oldName=$('#old_app_name').val();
				 			var newName=$.trim($("#app_name").val());
				 			if(oldName==newName){
				 				return true;
				 			}else{
				 				return data;
				 			}
				 		}
						
					}
				}
	        },
//	        app_port: {
//	            required: true,
//	            isValidServerPort:true
//	        },
	        app_env: {
	            //isEnv:true
	        },
	        host_volume: {
	            isCommand:true
	        },
	        container_volume:{
	        	isCommand:true
	        },
//	        app_url:{
//	        	required:true,
//	        	isUrlString:true
//	        },
	        app_desc: {
	            maxlength: 200,
	            stringCheck: true
	        }
	    },
	    messages: {
	    	app_name: {
	            required: "应用名称不能为空",
	            maxlength : $.validator.format("应用名称不能大于64个字符"),
				isImageName : "只能以小写字母、数字起始，其后包含小写字母、数字、下划线和合法的字符串",
				isNotNull : "应用名称不能为空值，请输入。",
				remote : "应用名称已存在，请重新填写。"
	        },
//	        app_port: {
//	            required: "端口不能为空"
//	        },
//	        app_url:{
//	        	required:"应用访问路径不能为空"
//	        },
	        app_desc: {
	            maxlength: $.validator.format("描述信息不能大于200个字符")
	        }
	    },
	    errorPlacement : function(error, element) {
	    	error.appendTo(element.parent());
	    }
	});
	
	//基础配置验证
	$("#app_other_form").validate({
	    /*rules: {
	    	app_proxy_ip:{
	    		isCheckedProxy:true
	        }
	    },*/
	    /*errorPlacement : function(error, element) {
	    	error.appendTo(element.parent());
	    }*/
	});
})

/**
 * @author yangqinglin
 * @datetime 2016年3月4日
 * @description 添加查询应用功能
 */
function SearchApplications() {
	var searchAppName = $('#searchAppName').val();
	
	 /*校验不符合条件的输入结果*/
    if(!isValidateValidName(searchAppName)){
    	showMessage("请输入正确的应用名称(包含中文、英文和数字)！");
    	$('#searchAppName').val("").focus();
    	return;
    }

	/* 查询是否存在关键词相关的结果 */
	jQuery(grid_selector).jqGrid('setGridParam', {
		url : base + 'app/listSearch?search_name=' + searchAppName
	}).trigger("reloadGrid");

}

/**
 * @author yangqinglin
 * @datetime 2016年3月4日
 * @description 添加对于应用高级查询
 */
function AdvancedSearchApp() {
	/** @bug152_begin 清空用户多选的参数 */
	while ($("#app_params li").length > 1) {
		$("#app_remove-param").parent().remove();
	}
	/* 隐藏高级查询第一行的删除打叉按钮 */
	$("#app_params li:first").find("#app_remove-param").hide();
	/** 打开高级搜索窗口，之前输入全部清空 */
	$("#app_params li:first").find("#app_param_value").val("");
	$("#app_params li:first").find("#app_meter").val("0");
	/** @bug152_finish */

	$('#advanSearchApplicationModal').modal('show');
}

/*刷新本页，重新获取全部应用列表*/
function showAllApplications(){
	self.location.reload(); 
}

/**
 * 移除环境时检查该环境下是否存在应用实例
 */
function checkAppInEnv(appId, envId){
	url = base + 'app/checkAppInEnv';
	var data = {
			appId:appId,
			envId:envId
		}
	flag = true;
	$.post(url, data, function(response){
		if(response <=0 ){
			flag = false;
		}
	});
	return flag
}

/**
 * 移除集群时检查该集群下是否存在应用实例
 */
function checkAppInCluster(clusterPort, appId){
	url = base + 'app/checkAppInCluster';
	var data = {
			clusterPort:clusterPort,
			appId:appId
		}
	flag = true;
	$.post(url, data, function(response){
		if(response <=0 ){
			flag = false;
		}
	});
	return flag
}


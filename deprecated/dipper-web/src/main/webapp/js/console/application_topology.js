/*建立保存从server端返回数据的对象信息*/
var temp_response;
var APP_TYPE="application";
var CLU_TYPE="cluster";
var IMG_TYPE="image";
var CTN_TYPE="container";
/*有可能查看详细过程中，镜像显示多个窗口，因此设置标志位*/
var isShown = false;

$('#application_id').ready(function() {
	// 创建节点
	var canvas = document.getElementById('canvas');
	var currentNode = null;
	var stage = new JTopo.Stage(canvas);
	stage.wheelZoom = 0.90; // 创建一个舞台对象（支持滚轮缩放操作）
	// stage.setCenter(100, 200);
	var scene = new JTopo.Scene(stage); // 创建一个场景对象
	scene.alpha = 1;
	getTopology();

	stage.click(function(event) {
		if (event.button == 0) {// 右键
			// 关闭弹出菜单（div）
			$("#contextmenu").hide();
		}
	});

	$("#contextmenu a").click(function() {
		var text = $(this).text();
		/*尝试获取img名称*/
		
		if (text == '删除节点') {
			scene.remove(currentNode);
			currentNode = null;
		}else if(text == '放大视图'){
			//stage.centerAndZoom(1);
			stage.zoomOut(0.618);
		}else if(text == '缩小视图'){
			//stage.centerAndZoom(1);
			stage.zoomIn(0.618);
		}else if (text == '详细信息') {
			/*获取节点的类型和节点的ID信息*/
			var node_type = currentNode.node_type;
			var node_id =  currentNode.node_id;
			var toponode_name = currentNode.text;
			
			switch(node_type){
			case(APP_TYPE):{
				bootbox.dialog({
					title : "应用信息",
					message : "<div><table class=\"table table-striped table-bordered table-hover\" border=\"1\"><tr><td><b>应用ID</b></td><td>"
							+ temp_response.appId
							+ "</td></tr><tr><td><b>应用名称</b></td><td>"
							+ temp_response.appName
							+ "</td></tr><tr><td><b>访问路径</b></td><td>"
							+ temp_response.appUrl
							+ "</td></tr><tr><td><b>CPU数量</b></td><td>"
							+ temp_response.appCpu
							+ "核</td></tr><tr><td><b>内存容量</b></td><td>"
							+ temp_response.appMem
							+ "MB</td></tr><tr><td><b>监控代理</b></td><td>"
							+ temp_response.appProxy
							+ "</td></tr><tr><td><b>描述信息</b></td><td>"
							+ temp_response.appDesc
							+ "</td></tr></table></div>",
					buttons : {"success" : {
							"label" : "<i class=\"fa fa-floppy-o\"></i>&nbsp;<b>确定</b>",
							"className" : "btn-sm btn-round btn-success",
							"callback" : function(e) {
							}
						}
					}
				});
				break;
			}
			case(CLU_TYPE):{
				var cluster_list = temp_response.cluster_list;
				/*遍历集群树状结构数组，添加到页面中*/
				for(var clu_count=0,clu_length=cluster_list.length;clu_count<clu_length;clu_count++){
					if(cluster_list[clu_count].clusterId==node_id){
						bootbox.dialog({
							title : "集群信息",
							message : "<div><table class=\"table table-striped table-bordered table-hover\" border=\"1\"><tr><td><b>集群ID</b></td><td>"
									+ cluster_list[clu_count].clusterId
									+ "</td></tr><tr><td><b>集群名称</b></td><td>"
									+ cluster_list[clu_count].clusterName
									+ "</td></tr><tr><td><b>集群UUID</b></td><td>"
									+ cluster_list[clu_count].clusterUuid
									+ "</td></tr><tr><td><b>集群端口</b></td><td>"
									+ cluster_list[clu_count].clusterPort
									+ "</td></tr><tr><td><b>集群日志</b></td><td>"
									+ cluster_list[clu_count].clusterLogFile
									+ "</td></tr><tr><td><b>集群描述</b></td><td>"
									+ cluster_list[clu_count].clusterDesc
									+ "</td></tr></table></div>",
							buttons : {"success" : {
									"label" : "<i class=\"fa fa-floppy-o\"></i>&nbsp;<b>确定</b>",
									"className" : "btn-sm btn-round btn-success",
									"callback" : function(e) {
									}
								}
							}
						});
					}
				}
				break;
			}
			case(IMG_TYPE):{
				var cluster_list = temp_response.cluster_list;
				for(var clu_count=0,clu_length=cluster_list.length;clu_count<clu_length;clu_count++){
					var image_list = cluster_list[clu_count].image_list;
					for(var img_count=0,img_length=image_list.length;img_count<img_length;img_count++){
						if((image_list[img_count].imageId==node_id)&&(!isShown)){
							bootbox.dialog({
								title : "镜像信息",
								message : "<div><table class=\"table table-striped table-bordered table-hover\" border=\"1\"><tr><td><b>镜像ID</b></td><td>"
										+ image_list[img_count].imageId
										+ "</td></tr><tr><td><b>镜像名称</b></td><td>"
										+ image_list[img_count].imageName
										+ "</td></tr><tr><td><b>镜像标签</b></td><td>"
										+ image_list[img_count].imageTag
										+ "</td></tr><tr><td><b>镜像UUID</b></td><td>"
										+ image_list[img_count].imageUuid
										+ "</td></tr><tr><td><b>镜像类型</b></td><td>"
										+ image_list[img_count].imageType
										+ "</td></tr><tr><td><b>镜像描述</b></td><td>"
										+ image_list[img_count].imageDesc
										+ "</td></tr></table></div>",
								buttons : {"success" : {
										"label" : "<i class=\"fa fa-floppy-o\"></i>&nbsp;<b>确定</b>",
										"className" : "btn-sm btn-round btn-success",
										"callback" : function(e) {
										}
									}
								}
							});
							/*修改显示标志位*/
							isShown = true;
						}
					}
				}
				break;
			}
			case(CTN_TYPE):{
				var cluster_list = temp_response.cluster_list;
				for(var clu_count=0,clu_length=cluster_list.length;clu_count<clu_length;clu_count++){
					var image_list = cluster_list[clu_count].image_list;
					for(var img_count=0,img_length=image_list.length;img_count<img_length;img_count++){
						var container_list = image_list[img_count].container_list;
						for(var ctn_count=0,ctn_length=container_list.length;ctn_count<ctn_length;ctn_count++){
							if(container_list[ctn_count].conId==node_id){
							bootbox.dialog({
								title : "容器信息",
								message : "<div><table class=\"table table-striped table-bordered table-hover\" border=\"1\"><tr><td><b>容器ID</b></td><td>"
										+ container_list[ctn_count].conId
										+ "</td></tr><tr><td><b>容器名称</b></td><td>"
										+ container_list[ctn_count].conName
										+ "</td></tr><tr><td><b>容器UUID</b></td><td>"
										+ container_list[ctn_count].conUuid
										+ "</td></tr><tr><td><b>CPU数量</b></td><td>"
										+ container_list[ctn_count].conCpu
										+ "</td></tr><tr><td><b>内存容量</b></td><td>"
										+ container_list[ctn_count].conMem
										+ "</td></tr><tr><td><b>容器描述</b></td><td>"
										+ container_list[ctn_count].conDesc
										+ "</td></tr></table></div>",
								buttons : {"success" : {
										"label" : "<i class=\"fa fa-floppy-o\"></i>&nbsp;<b>确定</b>",
										"className" : "btn-sm btn-round btn-success",
										"callback" : function(e) {
										}
									}
								}
							});
							}
						}
					}
				}
				break;
			}
			}
		}
		$("#contextmenu").hide();
});

	function getTopology() {
		var app_id = $("#application_id").text();
		url = base + "app/topology";
		data = {
			App_Id : app_id
		};
		$.ajax({
			type : "post",// 请求方式
			url : url,// 发送请求地址
			dataType : "json",
			data : data,// 发送给数据库的数据
			// 请求成功后的回调函数有两个参数
			success : function(response, textStatus) {
				/* （1）保存response对象到缓存中，便于之后的查询处理 */
				temp_response = response;
				/* （2）然后绘制拓扑图 */
				if(response==""){
					showMessage("获取数据异常！");
				}else{
					/*绘制应用树状结构拓扑图*/
					paintTopology(response);
				}
			},
			error : function(response) {
			}
		});
	}

	function handler(event) {
		if (event.button == 2) {// 右键
			// 当前位置弹出菜单（div）
			$("#contextmenu").css({
				top : event.y,
				left : event.x
			}).show();
		}
	}

	// 添加连线
	function addLink(nodeA, nodeZ) {
		var link = new JTopo.FlexionalLink(nodeA, nodeZ);
		link.direction = 'vertical' || 'horizontal';
		link.strokeColor = '125,125,125';
		//link.dashedPattern = 1; //虚线点距离
		link.arrowsRadius = 5;
		link.lineWidth = 2; // 线宽
		link.offsetGap = 35;
		link.bundleGap = 0; // 线条之间的间隔
		scene.add(link);
		return link;
	}

	// 添加节点
	/**
	 * @text: 节点显示的文本名称
	 * @image: 调用显示的图片名
	 * @type：节点的类型包含：application，cluster，image，container
	 * @eleid：节点本身的ID信息
	 * @isShowText：是否初始的时候就显示名称
	 * */
	function addNode(text, image, type, eleid, isShowText) {
		/*添加保存节点类型和ID值的属性*/
		var ExtendObj = {
				node_type : type,
				node_id   : eleid
		};
		
		/*新建JTopo节点类型*/
		var node = new JTopo.Node();// 圆形节点
		
		/*合并ExtendObj对象属性到node中*/
		$.extend(true,node,ExtendObj);
		
		node.setImage(base + '/img/' + image + '.png', true);
		node.fontColor = '0,0,0';
		
		/*判断是否显示节点的Text内容*/
		if(isShowText){
			node.text = text;
		}

		// 添加事件
		node.addEventListener('mouseup', function(event) {
			currentNode = this;
			handler(event);
		});

		node.setSize(30, 30); // 尺寸
		node.borderRadius = 2; // 圆角
		node.borderWidth = 1; // 边框的宽度

		if(isShowText){
			node.mouseover(function() {
				this.text = text;
			});
		}else{
			node.mouseover(function() {
				this.text = text;
			});
			node.mouseout(function() {
				this.text = null;
			});
		}
		
		scene.add(node);
		return node;
	}

	/*绘制应用树状拓扑图*/
	function paintTopology(response) {
		var app_name = response.appName;

		/* （1）创建应用初始节点 */
		var applicationNode = addNode(app_name, "application", APP_TYPE, response.appId, true);
		applicationNode.setLocation(500, 100);

		/*获取应用下的集群列表*/
		var cluster_list = response.cluster_list;
		/*遍历集群树状结构数组，添加到页面中*/
		for(var clu_count=0,clu_length=cluster_list.length;clu_count<clu_length;clu_count++){
			/*（2）插入集群节点*/
			var clusterNode = addNode(cluster_list[clu_count].clusterName, "cluster", CLU_TYPE, cluster_list[clu_count].clusterId,true);
			/*添加应用节点与集群节点的连接线*/
			addLink(applicationNode,clusterNode);
			
			var image_list = cluster_list[clu_count].image_list;
			for(var img_count=0,img_length=image_list.length;img_count<img_length;img_count++){
				/*（3）插入镜像节点*/
				var imageNode = addNode(image_list[img_count].imageName+":"+image_list[img_count].imageTag,
						"image", IMG_TYPE, image_list[img_count].imageId, false);
				/*添加集群节点与镜像的连接线*/
				addLink(clusterNode,imageNode);
				
				var container_list = image_list[img_count].container_list;
				for(var ctn_count=0,ctn_length=container_list.length;ctn_count<ctn_length;ctn_count++){
					/*（4）插入容器节点*/
					var containerNode = addNode(
							/* "[" + container_list[ctn_count].conId + "]"+ */
							container_list[ctn_count].conName, "container",  CTN_TYPE, container_list[ctn_count].conId, false);
					//containerNode.alarm='异常';
					/*添加镜像节点与容器的连接线*/
					addLink(imageNode,containerNode);
				}
			}
		}
		// 树形布局
		scene.doLayout(JTopo.layout.TreeLayout('down', 30,107));
	}
});

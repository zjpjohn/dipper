/*建立保存从server端返回数据的对象信息*/
var temp_response;

$('#detail_cluster_id').ready(function() {
	// 创建节点
	var canvas = document.getElementById('canvas');
	var currentNode = null;
	var stage = new JTopo.Stage(canvas);
	stage.wheelZoom = 0.9; // 创建一个舞台对象（支持滚轮缩放操作）
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
		if (text == '删除节点') {
			scene.remove(currentNode);
			currentNode = null;
		}
		if (text == '详细信息') {
			var toponode_name = currentNode.text;
	
			/* 用[id]标注的为主机节点，用(id)标注的为容器节点 */
			/* 为主机类型的节点处理 */
			if (toponode_name.indexOf("[") == 0) {
				var hostid_ip = toponode_name.split("]");
				/* 截取出主机的ID信息 */
				var host_id = hostid_ip[0].substring(1);
				/* 循环遍历刚刚返回的集群>主机>容器信息，取得匹配的主机详细信息 */
				for ( var hostNode in temp_response) {
					var host_info = temp_response[hostNode];
	
					/* 遍历集群中的子元素中的主机类型，并进行IP地址的匹配判断 */
					if (host_info != null&& host_info.hostId == host_id) {
						bootbox.dialog({
							title : "主机信息",
							message : "<div><table  class=\"table table-striped table-bordered table-hover\"  border=\"1\"><tr><td><b>主机IP</b></td><td>"
									+ host_info.hostIp
									+ "</td></tr><tr><td><b>主机描述</b></td><td>"
									+ host_info.hostDesc
									+ "</td></tr><tr><td><b>CPU数量</b></td><td>"
									+ host_info.hostCpu
									+ "核</td></tr><tr><td><b>内存容量</b></td><td>"
									+ host_info.hostMem
									+ "MB</td></tr><tr><td><b>内核版本</b></td><td>"
									+ host_info.hostKernelVersion
									+ "</td></tr><tr><td><b>容器数量</b></td><td>"
									+ host_info.hostConCounter
									+ "个</td></tr></table></div>",
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
			/* 为容器类型的节点处理 */
			else if (toponode_name.indexOf("(") == 0) {
				var conid_name = toponode_name.split(")");
				/* 截取出容器的ID信息 */
				var container_id = conid_name[0].substring(1);
	
				/* 循环遍历刚刚返回的集群>主机>容器信息，取得匹配的主机详细信息 */
				for ( var hostNode in temp_response) {
					var host_info = temp_response[hostNode];
	
					/* 循环遍历主机内的所有容器节点 */
					for ( var containers in host_info) {
						var container_info = host_info[containers];
	
						/* 遍历集群中的子元素中的主机类型，并进行IP地址的匹配判断 */
						if (container_info != null&& container_info.conId == container_id) {
							bootbox.dialog({
								title : "容器信息",
								message : "<div><table  class=\"table table-striped table-bordered table-hover\"  border=\"1\"><tr><td><b>容器ID</b></td><td>"
										+ container_info.conId
										+ "</td></tr><tr><td><b>容器名称</b></td><td>"
										+ container_info.conName
										+ "</td></tr><tr><td><b>容器描述</b></td><td>"
										+ container_info.conDesc
										+ "</td></tr><tr><td><b>启动命令</b></td><td>"
										+ container_info.conStartCommand
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
		}
		$("#contextmenu").hide();
});

	function getTopology() {
		var cluster_id = $("#detail_cluster_id").text();
		url = base + "cluster/topology";
		data = {
			Cluster_Id : cluster_id
		};
		$.ajax({
			type : "post",// 请求方式
			url : url,// 发送请求地址
			dataType : "json",
			data : data,// 发送给数据库的数据
			// 请求成功后的回调函数有两个参数
			success : function(response, textStatus) {
				/* 保存response对象到缓存中，便于之后的查询处理 */
				temp_response = response;
				if(response==""){
					showMessage("获取数据异常！");
				}else{
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
		link.strokeColor = '6,6,6';
		//link.dashedPattern = 1; //虚线点距离
		link.arrowsRadius = 5;
		link.lineWidth = 0.5; // 线宽
		link.offsetGap = 39;
		link.bundleGap = 0; // 线条之间的间隔
		scene.add(link);
		return link;
	}

	// 添加节点
	function addNode(text, image) {
		var node = new JTopo.Node();
		node.setImage(base + 'img/' + image + '.gif', true);
		node.fontColor = '0,0,0';
		if(image=="cluster"){
			node.text = text;
		}
		node.textPosition="Bottom_Center";
		//node.alarm = '2 W';/*提示报警信息*/

		if (image != "cluster") {
			node.addEventListener('mouseup', function(event) {
				currentNode = this;
				handler(event, currentNode);
			});
		}

		node.setSize(30, 30); // 尺寸
		node.borderRadius = 2; // 圆角
		node.borderWidth = 1; // 边框的宽度

		node.mouseover(function() {
			 this.text = text;
		});
		node.mouseout(function() {
			 this.text = null;
		});
		scene.add(node);
		return node;
	}

	// 添加节点
	function addContainerNode(text, image) {
		var node = new JTopo.Node();// 圆形节点
		node.setImage(base + '/img/' + image + '.gif', true);
		node.fontColor = '0,0,0';

		// 添加事件
		node.addEventListener('mouseup', function(event) {
			currentNode = this;
			handler(event);
		});

		node.setSize(30, 30); // 尺寸
		node.borderRadius = 2; // 圆角
		node.borderWidth = 1; // 边框的宽度

		node.mouseover(function() {
			this.text = text;
		});
		node.mouseout(function() {
			this.text = null;
		});
		scene.add(node);
		return node;
	}

	function paintTopology(response) {
		var cluster = response.clusterName;

		/* 创建应用初始节点 */
		var clusterNode = addNode(cluster, "cluster");
		//clusterNode.setLocation(500, 100);

		for ( var hostNode in response) {
			if (hostNode.indexOf("host") == 0) {
				var host = response[hostNode];
				var host_node = addNode(host.hostName, "host_node");
				addLink(clusterNode, host_node);

				for ( var containers in response[hostNode]){
					if (containers.indexOf("container") == 0) {
						// container节点绘制
						var container = response[hostNode][containers];
						var containerNode = addContainerNode("(" + container.conId + ")"
								+ container.conName,"container");
						addLink(host_node, containerNode);
					}
				}
			}
		}
		// 树形布局
		scene.doLayout(JTopo.layout.TreeLayout('down', 60,107));

	}

});

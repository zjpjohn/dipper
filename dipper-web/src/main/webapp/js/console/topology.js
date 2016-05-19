$('#detail_app_id').ready(function() {
    // 创建节点
    var canvas = document.getElementById('canvas');
    var currentNode = null;
    var stage = new JTopo.Stage(canvas);
    stage.wheelZoom = 0.90; // 创建一个舞台对象(支持缩放)
    stage.setCenter(100, 200);

    var scene = new JTopo.Scene(stage); // 创建一个场景对象
    scene.alpha = 1;

    getTopology();

    stage.click(function(event) {
        if (event.button == 0) { // 右键
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
            alert("应用节点：" + currentNode.text);

        }
        $("#contextmenu").hide();
    });

    function getTopology() {
        var App_Id = $("#detail_app_id").text();
        url = base + "application/topology";
        data = {
            App_Id: App_Id
        };
        $.ajax({
            type: "post",
            // 请求方式
            url: url,
            // 发送请求地址
            dataType: "json",
            data: data,
            // 发送给数据库的数据
            // 请求成功后的回调函数有两个参数
            success: function(response, textStatus) {
                if (response == "") {
                    showMessage("应用响应数据异常！");
                } else {
                    paintTopology(response);
                }
            },
            error: function(response) {
                showMessage("应用响应数据错误：" + response.statusText);
            }
        });
    }

    function handler(event) {
        if (event.button == 2) { // 右键
            // 当前位置弹出菜单（div）
            $("#contextmenu").css({
                top: event.y,
                left: event.x
            }).show();
        }
    }

    // 添加连线
    function addLink(nodeA, nodeZ) {
        var link = new JTopo.FlexionalLink(nodeA, nodeZ);
        link.direction = 'vertical' || 'horizontal';
        link.strokeColor = '125,125,125';
        // link.dashedPattern = 1;
        link.arrowsRadius = 5;
        link.lineWidth = 2; // 线宽
        link.offsetGap = 35;
        link.bundleGap = 0; // 线条之间的间隔
        scene.add(link);
        return link;
    }

    // 添加节点
    function addNode(text, image) {
        var node = new JTopo.Node();
        node.setImage(base + 'img/' + image + '.png', true);
        node.fontColor = '0,0,0';
        node.addEventListener('mouseup',
        function(event) {
            currentNode = this;
            handler(event, currentNode);
        });

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
        var node = new JTopo.Node(); // 圆形节点
        node.setImage(base + '/img/' + image + '.png', true);
        node.fontColor = '0,0,0';

        // 添加事件
        node.addEventListener('mouseup',
        function(event) {
            currentNode = this;
            handler(event);
        });

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
        var application = response.appName;

        /* 创建应用初始节点 */
        var appNode = addNode(application, "application");
        appNode.setLocation(500, 100);

        /* 定义负载均衡节点 */
        var loadNode = null;

        for (var loadBalanceNode in response) {
            if (loadBalanceNode.indexOf("loadbalance") == 0) {
                loadNode = addNode("{" + response[loadBalanceNode].lbId + "}" + response[loadBalanceNode].lbName, "nginx");
                addLink(appNode, loadNode);
            }
        }

        for (var hostNode in response) {
            if (hostNode.indexOf("host") == 0) {
                var host_node = addNode("[" + response[hostNode].hostId + "]" + response[hostNode].hostIp, "host_node");

                /* 判断loadbalance的节点是否为空 */
                if (loadNode != null) {
                    addLink(loadNode, host_node);
                } else {
                    addLink(appNode, host_node);
                }

                for (var containers in response[hostNode]) if (containers.indexOf("container") == 0) {
                    {
                        // container节点绘制
                        var container = response[hostNode][containers];
                        var containerNode = addContainerNode("(" + container.conId + ")" + container.conName, "container");
                        addLink(host_node, containerNode);
                    }
                }
            }
        }
        // 树形布局
        scene.doLayout(JTopo.layout.TreeLayout('down', 30, 107));
    }
});
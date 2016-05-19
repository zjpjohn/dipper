$(function () {
    connect();
});

$(window).unload(function() {
	disconnect();
});
socket=null;
function connect() {
    if ('WebSocket' in window) {
        console.log('Websocket supported');
        var host = window.location.host;
        socket = new WebSocket('ws://' + host + '/messagingService');
        console.log('Connection attempted');

        socket.onopen = function () {
            console.log('Connection open');
        };

        socket.onclose = function () {
            console.log('Disconnecting connection');
        };
        socket.onmessage = function (event) {
        	var obj = JSON.parse(event.data);
        	message = JSON.parse(obj.content);
        	if(obj.messageType == 'ws_sticky'){
        		showNotice(message);
        	}
        };

    } else {
        console.log('Websocket not supported');
    }
}

function disconnect() {
    socket.close();
    console.log("Disconnected");
}

function send(message) {
    socket.send(JSON.stringify({
        'message': message
    }));
}

function showNotice(notice){
	try{
		if(notice.success){
			successNotice(notice.title,notice.message);
		}else{
			errorNotice(notice.title,notice.message);
		}
	}catch (e) {
	}
}
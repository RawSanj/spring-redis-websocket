$(document).ready(function () {

	// define selectors to avoid duplication
	let $alert = $('.alert');
	let $connect = $("#connect");
	let $disconnect = $("#disconnect");
	let $chatMessage = $("#chat-message");

	$alert.hide();

	let messageCount = 0;
	let rowCount = 0;

	let websocket = null;

	function setConnected(connected) {
		$connect.prop("disabled", connected);
		$disconnect.prop("disabled", !connected);
		if (connected) {
			$("#chatMessage").show();
		} else {
			$("#chatMessage").hide();
		}
		$("#messages").html("");
	}

	function connect(callback) {
		$alert.hide();

		let host = location.hostname + (location.port ? ':' + location.port : '');
		websocket = new WebSocket("ws://" + host + "/redis-chat");

		websocket.onopen = openEvent => {
			setConnected(true);
			callback();
		};

		websocket.onmessage = messageEvent => {
			console.log("Message: ", messageEvent);
			let chatMessage = JSON.parse(messageEvent.data);
			setMessageCount(chatMessage.id);
			showChatMessage(chatMessage);
		};

		websocket.onerror = errorEvent => {
			console.log("Error Occured.", errorEvent);
			disconnect();
		};

		websocket.onclose = closeEvent => {
			console.log("WebSocket Session Closed.", closeEvent);
			disconnect();
		};

		$("#messageCount").text(messageCount);
	}

	function disconnect() {
		if (websocket !== null && websocket.readyState === websocket.OPEN) {
			websocket.close();
		}
		setConnected(false);
		console.log("Session Closed. WebSocket Disconnected.");
		messageCount = 0;
		rowCount = 0;
	}

	function setMessageCount(totalCount) {
		$("#message-count").text(totalCount);
	}

	function showChatMessage(chatMessage) {
		rowCount++;
		$("#messages").prepend("<tr" + "><td scope='row'> <h6><b>  " + chatMessage.id + ".</b></h6> </td> <td> " + chatMessage.message + "</td><td> " + chatMessage.hostname + "</td></tr>");
	}

	$disconnect.click(function () {
		console.log("Disconnect");
		disconnect();
	});

	$("#close-alert").click(function () {
		$alert.hide();
	});

	$connect.click(function () {
		if (websocket == null) {
			connect(function () {
				console.log("Connected");
			});
		}
	});


	$("#send-ws-message").click(function () {
		sendMessage();
	});

	$("#send-http-message").click(function () {
		var chatMessage = $chatMessage.val();
		$.ajax({
			url: "/message",
			type: "POST",
			data: JSON.stringify({"message": chatMessage}),
			dataType: "json",
			contentType: "application/json",
			success: function (response) {
				console.log(response);
				$chatMessage.val("");
			},
			error: function (err) {
				console.log(err);
			}
		})
	});

	$('#chat-message').keypress(function (e) {
		var key = e.which;
		if (key === 13) {
			sendMessage();
		}
	});

	function sendMessage() {

		console.log('websocket', websocket);

		if (websocket != null && websocket.readyState === websocket.OPEN) {
			let chatMessage = $chatMessage.val();
			websocket.send(chatMessage);
			$chatMessage.val("");
		} else {
			connect(function () {
				let chatMessage = $chatMessage.val();
				websocket.send(chatMessage);
				$chatMessage.val("");
			})
		}
	}

});


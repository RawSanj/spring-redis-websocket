$(document).ready(function () {

	// define selectors to avoid duplication
	let $alert = $('#websocket-disconnected');
	let $userConnected = $("#connect-alert");
	let $userDisconnect = $("#disconnect-alert");
	let $connect = $("#connect");
	let $disconnect = $("#disconnect");
	let $chatMessage = $("#chat-message");

	$alert.hide();
	$userConnected.hide();
	$userDisconnect.hide();

	function showUserConnectedAlert() {
		$userConnected.fadeTo(2000, 500).slideUp(500, function() {
			$userConnected.slideUp(500);
		});
	}

	function showUserDisconnectedAlert() {
		$userDisconnect.fadeTo(2000, 500).slideUp(500, function() {
			$userDisconnect.slideUp(500);
		});
	}

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
		let wsProtocol = location.protocol === "https:" ? "wss://" : "ws://";

		websocket = new WebSocket(wsProtocol + host + "/redis-chat");

		websocket.onopen = openEvent => {
			setConnected(true);
			callback();
		};

		websocket.onmessage = messageEvent => {
			console.log("Message: ", messageEvent);
			let chatMessage = JSON.parse(messageEvent.data);
			if (chatMessage.id !== 0) {
				setMessageCount(chatMessage.id);
				setUsersOnlineCount(chatMessage.usersOnline);
				showChatMessage(chatMessage);
			} else {
				setUsersOnlineCount(chatMessage.usersOnline);
				if (chatMessage.message === "CONNECTED") {
					showUserConnectedAlert();
				} else {
					showUserDisconnectedAlert();
				}
			}
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

		$alert.fadeTo(5000, 500).slideUp(500, function() {
			$alert.slideUp(500);
		});
	}

	function setMessageCount(totalCount) {
		$("#message-count").text(totalCount);
	}

	function setUsersOnlineCount(userOnline) {
		$("#users-online").text(userOnline);
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


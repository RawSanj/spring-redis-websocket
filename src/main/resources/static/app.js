$(document).ready(function () {

	$('.alert').hide();

	var stompClient = null;
	var messageCount = 0;
	var rowCount = 0;

	function setConnected(connected) {
		$("#connect").prop("disabled", connected);
		$("#disconnect").prop("disabled", !connected);
		if (connected) {
			$("#chatMessage").show();
		} else {
			$("#chatMessage").hide();
		}
		$("#messages").html("");
	}

	function connect(callback) {
		$('.alert').hide();
		var socket = new SockJS('/redis-chat');
		stompClient = Stomp.over(socket);

		stompClient.connect({}, function (frame) {
			setConnected(true);
			console.log('Connected: ' + frame);
			stompClient.subscribe('/topic/messages', function (chatMessage) {
				console.log("Message: ", chatMessage);
				showChatMessage(JSON.parse(chatMessage.body));
			});
			stompClient.subscribe('/topic/count', function (totalCount) {
				console.log("Total: ", totalCount.body);
				setMessageCount(totalCount.body);
			});
			callback();
		}, function (message) {
			disconnect();
			$('.alert').show();
		});
		$("#messageCount").text(messageCount);
	}

	function disconnect() {
		if (stompClient !== null) {
			stompClient.disconnect();
		}
		setConnected(false);
		console.log("Disconnected");
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

	$("#disconnect").click(function () {
		console.log("Disconnect");
		disconnect();
	});

	$("#close-alert").click(function () {
		$('.alert').hide();
	});

	$("#connect").click(function () {
		if (stompClient == null) {
			connect(function () {
				console.log("Connected");
			});
		} else if (!stompClient.connected) {
			connect(function () {
				console.log("Connected");
			});
		}
	});


	$("#send-ws-message").click(function () {
		sendMessage();
	});

	$("#send-http-message").click(function () {
		var chatMessage = $("#chat-message").val();
		$.ajax({
			url: "/message",
			type: "POST",
			data: JSON.stringify({"message": chatMessage}),
			dataType: "json",
			contentType: "application/json",
			success: function (response) {
				console.log(response);
			},
			error: function (err) {
				console.log(err);
			}
		})
	});

	$('#chat-message').keypress(function (e) {
		var key = e.which;
		if (key == 13) {
			sendMessage();
		}
	});

	function sendMessage() {
		if (stompClient !== null && stompClient.connected) {
			var chatMessage = $("#chat-message").val();
			stompClient.send("/app/message", {}, chatMessage);
			$("#chat-message").val("");
		} else {
			connect(function () {
				var chatMessage = $("#chat-message").val();
				stompClient.send("/app/message", {}, chatMessage);
				$("#chat-message").val("");
			});
		}
	}

});


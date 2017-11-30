angular.module("TradeInjectorApp.services").service("TradeInjectorService", function($q, $timeout){
	// Register the listeners in here
	
	var service= {},
	listenerTrades = $q.defer(),
	listenerTradeInjectMessage = $q.defer(), 
	socket={
		client:null,
		stomp:null
	},
	messageIds=[];
	
	service.RECONNECT_TIMEOUT=30000;
	service.SOCKET_URL="/injectorUI";
	service.TRADE_TOPIC="/topic/tradeAck";
	service.TRADE_MESSAGE_INJECT_TOPIC="/topic/tradeMessageInject";
	service.TRADE_BROKER="/tradeMessageInject";
	
	service.receive = function(){
		return listenerTrades.promise;
	};
	
	service.receiveTradeInjectMessage = function(){
		return listenerTradeInjectMessage.promise;
	};
	
	service.send=function(injectMessage){
		var id = Math.floor(Math.random() * 1000000);
		socket.stomp.send(service.TRADE_BROKER, 
				{priority: 9},
				JSON.stringify(
					injectMessage
					
				)
		);
		messageIds.push(id);
	};
	
	var reconnect = function(){
		$timeout(function(){
			initialize();
		}, 
		this.RECONNECT_TIMEOUT				
		);
	};
	
	var getTradeAcks = function(acks){
		var trades = JSON.parse(acks)
		return trades;
	};
	
	var getTradeInjectMessages = function(injectMessages){
		var tradesInjectMessages = JSON.parse(injectMessages)
		return tradesInjectMessages;
	};
	
	var startListener = function(){
		socket.stomp.subscribe(service.TRADE_TOPIC, function(data){
			listenerTrades.notify(getTradeAcks(data.body));
		});
	};
	
	var startListenerTradeMessageInject = function(){
		socket.stomp.subscribe(service.TRADE_MESSAGE_INJECT_TOPIC, function(data){
			listenerTradeInjectMessage.notify(getTradeInjectMessages(data.body));
		});
	};
	
	var initialize = function(){
		socket.client = new SockJS(service.SOCKET_URL);
		socket.stomp = Stomp.over(socket.client);
		socket.stomp.connect({}, startListener);
		socket.stomp.onclose = reconnect;
	};
	
	var initializeTradeInject = function(){
		socket.client = new SockJS(service.SOCKET_URL);
		socket.stomp = Stomp.over(socket.client);
		socket.stomp.connect({}, startListenerTradeMessageInject);
		socket.stomp.onclose = reconnect;
	};
	
	var disconnect = function disconnect() {
	    if (socket.stomp.connect != null) {
	    	socket.stomp.connect.disconnect();
	    }
	    //setConnected(false);
	    console.log("Disconnected");
	}
	
	initialize();
	initializeTradeInject();
	return service;
});
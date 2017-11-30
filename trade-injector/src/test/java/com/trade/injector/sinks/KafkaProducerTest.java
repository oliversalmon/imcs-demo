package com.trade.injector.sinks;

import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import static org.junit.Assert.*;

import com.trade.injector.controller.TradeInjectorController;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TradeInjectorController.class)
@TestPropertySource(properties = {"kafka.bootstrap-servers=138.68.168.237:9092", "spring.data.mongodb.host=localhost"})
public class KafkaProducerTest {
	
	@Value("${kafka.topic.marketData}")
	private String topic;
	
	@Value("${kafka.topic.trade}")
	private String tradeTopic;
	
	
	
	
	
	@Autowired
	private KafkaSink sender;
	@Autowired
	private MarketDataReceiver receive;
	
	@Autowired
	private TradeReceiver tradeReceiver;
	
	@Test
	public void loadStringTest() throws InterruptedException {

		/*ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send("maket_data", "testpricemessage");
		future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
			@Override
			public void onSuccess(SendResult<String, String> result) {
				System.out.println("success");
			}

			@Override
			public void onFailure(Throwable ex) {
				System.out.println("failed");
			}
		});
		System.out.println(Thread.currentThread().getId());*/
		
		
		sender.send(topic, "Hi",  "Hi there");
		assertTrue(this.receive.getLatch().await(60, TimeUnit.SECONDS));
		
		sender.send(tradeTopic, "89", "a trade message");
		assertTrue(this.tradeReceiver.getLatch().await(60, TimeUnit.SECONDS));
		
		//receive.receive(message);

	}

}

package com.trade.injector.sinks;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;

import java.util.concurrent.CountDownLatch;

public class Listener {

	public final CountDownLatch countDownLatch1 = new CountDownLatch(1);

	@KafkaListener(id = "priceListener", topics = "maket_data", group = "demo")
	public void listen(ConsumerRecord<?, ?> record) {
		System.out.println(record);
		countDownLatch1.countDown();
	}

}

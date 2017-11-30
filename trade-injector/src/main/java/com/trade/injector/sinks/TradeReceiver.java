package com.trade.injector.sinks;

import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;

public class TradeReceiver {
	

	final Logger LOG = LoggerFactory.getLogger(TradeReceiver.class);

	  private CountDownLatch latch = new CountDownLatch(1);

	  public CountDownLatch getLatch() {
	    return latch;
	  }

	  @KafkaListener(topics = "${kafka.topic.trade}")
	  public void receive(String message) {
	    LOG.info("received message='{}'", message);
	    latch.countDown();
	  }

}

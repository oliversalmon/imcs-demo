package com.trade.injector.sinks;

import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;

public class MarketDataReceiver {

	final Logger LOG = LoggerFactory.getLogger(MarketDataReceiver.class);

	  private CountDownLatch latch = new CountDownLatch(1);

	  public CountDownLatch getLatch() {
	    return latch;
	  }

	  @KafkaListener(topics = "${kafka.topic.marketData}")
	  public void receive(String message) {
	    LOG.info("received message='{}'", message);
	    latch.countDown();
	  }
	  

}

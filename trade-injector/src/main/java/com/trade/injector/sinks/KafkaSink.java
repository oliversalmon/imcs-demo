package com.trade.injector.sinks;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;



@Configuration
@EnableKafka
public class KafkaSink implements ISink {

	final Logger LOG = LoggerFactory.getLogger(KafkaSink.class);

	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;

	public void send(String topic, String key, String message) {
		// the KafkaTemplate provides asynchronous send methods returning a
		// Future
		ListenableFuture<SendResult<String, String>> future = kafkaTemplate
				.send(topic, key,  message);

		// register a callback with the listener to receive the result of the
		// send asynchronously
		future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {

			@Override
			public void onSuccess(SendResult<String, String> result) {
				//LOG.info("sent message='{}' with offset={}", message, result
						//.getRecordMetadata().offset());
			}

			@Override
			public void onFailure(Throwable ex) {
				LOG.error("unable to send message='{}'", message, ex);
			}
		});

		// or, to block the sending thread to await the result, invoke the
		// future's get() method
	}

	@Override
	public void writeTo(String url, String key,  String message) throws Exception {
		
		send(url, key, message);

	}

}

package com.trade.injector.sinks;

import static org.junit.Assert.*;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.listener.config.ContainerProperties;
import org.springframework.kafka.test.rule.KafkaEmbedded;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.kafka.test.utils.ContainerTestUtils;

import org.springframework.test.context.junit4.SpringRunner;

import com.trade.injector.controller.TradeInjectorController;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringKafkaSenderTest {

	final Logger LOG = LoggerFactory.getLogger(SpringKafkaSenderTest.class);


  private KafkaMessageListenerContainer<String, String> container;

  private BlockingQueue<ConsumerRecord<String, String>> records;

  @Autowired
  private KafkaSink sender;

  @Before
  public void setUp() throws Exception {
    // set up the Kafka consumer properties
    Map<String, Object> consumerProperties =
        KafkaTestUtils.consumerProps("sender_group", "false", KafkaEmbeddedTest.embeddedKafka);

    // create a Kafka consumer factory
    DefaultKafkaConsumerFactory<String, String> consumerFactory =
        new DefaultKafkaConsumerFactory<String, String>(consumerProperties);

    // set the topic that needs to be consumed
    ContainerProperties containerProperties =
        new ContainerProperties(KafkaEmbeddedTest.SENDER_TOPIC);

    // create a Kafka MessageListenerContainer
    container = new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);

    // create a thread safe queue to store the received message
    records = new LinkedBlockingQueue<>();

    // setup a Kafka message listener
    container.setupMessageListener(new MessageListener<String, String>() {
      @Override
      public void onMessage(ConsumerRecord<String, String> record) {
        LOG.debug("test-listener received message='{}'", record.toString());
        records.add(record);
      }
    });

    // start the container and underlying message listener
    container.start();
    // wait until the container has the required number of assigned partitions
    ContainerTestUtils.waitForAssignment(container,
        KafkaEmbeddedTest.embeddedKafka.getPartitionsPerTopic());
  }

  @After
  public void tearDown() {
    // stop the container
    container.stop();
  }

  @Test
  public void testSend() throws Exception {
    // send the message
    String greeting = "Hello Spring Kafka Sender!";
    sender.send(KafkaEmbeddedTest.SENDER_TOPIC, greeting, greeting);

    // check that the message was received
    assertEquals(records.poll(10, TimeUnit.SECONDS), greeting);
  }
}
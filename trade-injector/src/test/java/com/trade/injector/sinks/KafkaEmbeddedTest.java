package com.trade.injector.sinks;

import org.junit.runners.Suite;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.kafka.test.rule.KafkaEmbedded;


@RunWith(Suite.class)
@SuiteClasses({SpringKafkaApplicationTest.class, SpringKafkaSenderTest.class,
    SpringKafkaReceiverTest.class})

public class KafkaEmbeddedTest {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(KafkaEmbeddedTest.class);

	  public static final String SENDER_TOPIC = "sender.t";
	  public static final String RECEIVER_TOPIC = "receiver.t";

	  @ClassRule
	  public static KafkaEmbedded embeddedKafka =
	      new KafkaEmbedded(1, true, SENDER_TOPIC, RECEIVER_TOPIC);

	  @BeforeClass
	  public static void setUpBeforeClass() throws Exception {
	    String kafkaBootstrapServers = embeddedKafka.getBrokersAsString();

	    LOGGER.debug("kafkaServers='{}'", kafkaBootstrapServers);
	    // override the property in application.properties
	    System.setProperty("kafka.bootstrap-servers", kafkaBootstrapServers);
	  }


}

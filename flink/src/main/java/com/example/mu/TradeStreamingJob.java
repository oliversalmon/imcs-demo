package com.example.mu;

/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.example.mu.domain.Trade;
import com.example.mu.kafka.schemas.TradeSchema;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer082;

import java.util.Properties;
import java.util.concurrent.TimeUnit;


/**
 * Skeleton for a Flink Streaming Job.
 * <p>
 * For a full example of a Flink Streaming Job, see the SocketTextStreamWordCount.java
 * file in the same package/directory or have a look at the website.
 * <p>
 * You can also generate a .jar file that you can submit on your Flink
 * cluster.
 * Just type
 * mvn clean package
 * in the projects root directory.
 * You will find the jar in
 * target/flink-1.0-SNAPSHOT.jar
 * From the CLI you can then run
 * ./bin/flink run -c com.example.mu.TradeStreamingJob target/flink-1.0-SNAPSHOT.jar
 * <p>
 * For more information on the CLI see:
 * <p>
 * http://flink.apache.org/docs/latest/apis/cli.html
 */
public class TradeStreamingJob {

    public static void main(String[] args) throws Exception {
        final String ZOOKEEPER_HOST;
        final String KAFKA_BROKER;
        final String MU_GROUP;
        final String INPUT_TOPIC;
        final String OUTPUT_TOPIC;

        // set up the streaming execution environment
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        // Set up the environment
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        env.getConfig().setAutoWatermarkInterval(1000);
        env.enableCheckpointing(5000, CheckpointingMode.EXACTLY_ONCE);
        env.setRestartStrategy(RestartStrategies.fixedDelayRestart(10, Time.of(30, TimeUnit.SECONDS)));
        // Set up the environment variables
        ParameterTool pt = ParameterTool.fromPropertiesFile("classpath:mu-tade-processor.properties");
        ZOOKEEPER_HOST = pt.get("zookeeper.host");
        KAFKA_BROKER = pt.get("kafka.broker");
        MU_GROUP = pt.get("kafka.group");
        INPUT_TOPIC = pt.get("kafka.topic.trade");

        // Set up Kafka properties
        Properties kafkaProps = new Properties();
        kafkaProps.setProperty("zookeeper.connect", ZOOKEEPER_HOST);
        kafkaProps.setProperty("bootstrap.servers", KAFKA_BROKER);
        kafkaProps.setProperty("group.id", MU_GROUP);

        //Create the stream source
        DataStream<Trade> tradeStream = env.addSource(new FlinkKafkaConsumer082<Trade>(
                INPUT_TOPIC,
                new TradeSchema(),
                kafkaProps
        ));


        // execute program
        env.execute("Mu Trade Processor");
    }
}

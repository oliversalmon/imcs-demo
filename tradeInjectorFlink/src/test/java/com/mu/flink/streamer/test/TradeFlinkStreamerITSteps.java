package com.mu.flink.streamer.test;

import com.example.mu.domain.Trade;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.mu.flink.streamer.HzTradeSink;
import com.mu.flink.streamer.TradeProcess;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertEquals;

//import utilities.SingletonTestManager;

public class TradeFlinkStreamerITSteps {

    private List<Trade> listOfTrades;
    HazelcastInstance hz = Hazelcast.newHazelcastInstance();

    @Before
    public void before() {


        //SingletonTestManager.startUpServices(null);


    }

    @Given("the list of trades")
    public void the_list_of_trades(List<Trade> inputTrades) {

        listOfTrades = inputTrades;
        assert listOfTrades.size() == 2;


    }

    @When("the streamer reads from source")
    public void the_streamer_reads_from_source() throws Exception {
        // Write code here that turns the phrase above into concrete actions
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // configure your test environment
        env.setParallelism(1);

        DataStream<String> stream;
        Iterator<Trade> iter = listOfTrades.iterator();

        stream = env.fromElements(iter.next().toJSON(), iter.next().toJSON());


        SingleOutputStreamOperator<Trade> mainDataStream   = stream.process(new TradeProcess());
        mainDataStream.addSink(new CollectSink());

        // execute
        env.execute();

        assert listOfTrades.size() == CollectSink.values.size();

    }

    @Then("the streamer pushes the following to Hz")
    public void the_streamer_can_read_all_trades_successfully(List<Trade> output) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // configure your test environment
        env.setParallelism(1);

        DataStream<String> stream;
        Iterator<Trade> iter = listOfTrades.iterator();

        stream = env.fromElements(iter.next().toJSON(), iter.next().toJSON());


        SingleOutputStreamOperator<Trade> mainDataStream   = stream.process(new TradeProcess());
        mainDataStream.addSink(new HzTradeSink());

        // execute
        env.execute();

        assertEquals(output.size(), hz.getMap(HzTradeSink.getMapName()).size());




    }

    private static class CollectSink implements SinkFunction<Trade> {

        // must be static
        public static final List<Trade> values = new ArrayList<>();

        @Override
        public synchronized void invoke(Trade value) {
            values.add(value);
        }
    }

}

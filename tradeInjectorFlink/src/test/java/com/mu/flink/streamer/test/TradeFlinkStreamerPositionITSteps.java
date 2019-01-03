package com.mu.flink.streamer.test;

import com.example.mu.domain.PositionAccount;
import com.example.mu.domain.Price;
import com.example.mu.domain.Trade;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.mu.flink.streamer.HzPositionSink;
import com.mu.flink.streamer.HzPositionWindowSink;
import com.mu.flink.streamer.HzTradeSink;
import com.mu.flink.streamer.TradeFlinkStreamer;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.cucumber.datatable.DataTable;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.junit.After;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class TradeFlinkStreamerPositionITSteps {

    private Price spotPx = null;
    private List<Trade> listOfInputTrades;
    public static final HazelcastInstance hz = Hazelcast.newHazelcastInstance();
    private static final HazelcastInstance hzClient = HazelcastClient.newHazelcastClient();

    @Before
    public void before() {





    }

    @After
    public void after() {
        hz.shutdown();

    }


    @Given("the list of trades to aggregate into Positions")
    public void the_list_of_trades_to_aggregate_into_Positions(List<Trade> trades) {

        listOfInputTrades = trades;
        assert listOfInputTrades.size() == 2;
    }

    @When("the streamer reads from trade source for positions")
    public void the_streamer_reads_from_trade_source() throws Exception {
        // Write code here that turns the phrase above into concrete actions
         StreamExecutionEnvironment env = org.apache.flink.streaming.api.environment.StreamExecutionEnvironment.getExecutionEnvironment();

        // configure your test environment
        env.setParallelism(1);

        DataStream<String> stream;
        Iterator<Trade> iter = listOfInputTrades.iterator();

        stream = env.fromElements(iter.next().toJSON(), iter.next().toJSON());


        TradeFlinkStreamer streamer = new TradeFlinkStreamer();
        streamer.streamAndSink(stream, new CollectSink(), new PositionCollectSink());

        // execute
        env.execute();

        assert listOfInputTrades.size() == TradeFlinkStreamerPositionITSteps.CollectSink.values.size();
        //assert PositionCollectSink.values.size()==1;
    }

    @When("the spot price for the instruments are as follows")
    public void the_spot_price_for_the_instruments_are_as_follows(DataTable spotPrice) {
        List<Map<String, String>> px = spotPrice.asMaps(String.class, String.class);
        spotPx = new Price();
        spotPx.setTimeStamp(System.currentTimeMillis());
        spotPx.setInstrumentId(px.get(0).get("instrumentid"));
        spotPx.setPriceId(px.get(0).get("priceId"));
        spotPx.setPrice(Double.parseDouble(px.get(0).get("price")));





    }

    @Then("the streamer aggregates the following positions and calculates the pnl as follows")
    public void the_streamer_aggregates_the_following_positions_and_calculates_the_pnl_as_follows(DataTable posTable) {

        List<Map<String, String>> posDataToCompare = posTable.asMaps(String.class, String.class);
        PositionAccount posAccControl = new PositionAccount();
        posAccControl.setAccountId(posDataToCompare.get(0).get("accountId"));
        posAccControl.setInstrumentid(posDataToCompare.get(0).get("instrumentid"));
        posAccControl.setPnl(Double.parseDouble(posDataToCompare.get(0).get("pnl")));
        posAccControl.setSize(Long.parseLong(posDataToCompare.get(0).get("size")));


        assert spotPx != null;
        assert listOfInputTrades.size() == 2;
        TradeFlinkStreamer streamer = new TradeFlinkStreamer();


        DataStream<String> stream;
        Iterator<Trade> iter = listOfInputTrades.iterator();

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // configure your test environment
        env.setParallelism(1);

        stream = env.fromElements(iter.next().toJSON(), iter.next().toJSON());

        streamer.streamAndSink(stream, new CollectSink(), new PositionCollectSink());


        PositionAccount posAccountActual = PositionCollectSink.values.get(0);
        assert posAccountActual != null;
        assert posAccountActual.getPnl()==0;
        assert posAccountActual.getSize()==100;



    }

    @Then("pushes the above position into Position Map in Hz")
    public void pushes_the_above_position_into_Position_Map_in_Hz(DataTable posTable) throws Exception {

        List<Map<String, String>> posDataToCompare = posTable.asMaps(String.class, String.class);
        PositionAccount posAccControl = new PositionAccount();
        posAccControl.setAccountId(posDataToCompare.get(0).get("accountId"));
        posAccControl.setInstrumentid(posDataToCompare.get(0).get("instrumentid"));
        posAccControl.setPnl(Double.parseDouble(posDataToCompare.get(0).get("pnl")));
        posAccControl.setSize(Long.parseLong(posDataToCompare.get(0).get("size")));

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // configure your test environment
        env.setParallelism(1);

        DataStream<String> stream;
        Iterator<Trade> iter = listOfInputTrades.iterator();

        stream = env.fromElements(iter.next().toJSON(), iter.next().toJSON());


        TradeFlinkStreamer streamer = new TradeFlinkStreamer();

        //Feed in the spot px

        IMap<String, Price> mapPrice = TradeFlinkStreamer.getHzClient().getMap("price");
        mapPrice.put(spotPx.getInstrumentId(), spotPx);

        //and clear the caches
        TradeFlinkStreamer.getHzClient().getMap(HzTradeSink.getMapName()).clear();
        TradeFlinkStreamer.getHzClient().getMap(HzPositionWindowSink.getMapName()).clear();

        streamer.streamAndSink(stream, new HzTradeSink(), new HzPositionSink());

        // execute
        env.execute();


        assertEquals(2, TradeFlinkStreamer.getHzClient().getMap(HzTradeSink.getMapName()).size());
        assertEquals(1, TradeFlinkStreamer.getHzClient().getMap(HzPositionWindowSink.getMapName()).size());

        String posKey = posAccControl.getAccountId()+posAccControl.getInstrumentid();
        IMap<String, PositionAccount> posMap = TradeFlinkStreamer.getHzClient().getMap(HzPositionWindowSink.getMapName());
        PositionAccount posAccountActual = posMap.get(posKey);

        assert posAccountActual != null;
        assert posAccountActual.getPnl()==posAccControl.getPnl();
        assert posAccountActual.getSize()==posAccControl.getSize();



    }

    private static class CollectSink implements SinkFunction<Trade> {

        // must be static
        public static final List<Trade> values = new ArrayList<>();

        @Override
        public synchronized void invoke(Trade value) {
            values.add(value);
        }
    }

    private static class PositionCollectSink implements SinkFunction<PositionAccount> {

        // must be static
        public static final List<PositionAccount> values = new ArrayList<>();

        @Override
        public synchronized void invoke(PositionAccount value) {
            values.add(value);
        }
    }

}

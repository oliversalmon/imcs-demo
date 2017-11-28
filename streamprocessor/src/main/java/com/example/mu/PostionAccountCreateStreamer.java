package com.example.mu;

import static com.hazelcast.jet.AggregateOperations.allOf;
import static com.hazelcast.jet.AggregateOperations.mapping;
import static com.hazelcast.jet.AggregateOperations.summingLong;
import static com.hazelcast.jet.AggregateOperations.toSet;
import static com.hazelcast.jet.Edge.between;
import static com.hazelcast.jet.WatermarkEmissionPolicy.emitByFrame;
import static com.hazelcast.jet.WatermarkPolicies.limitingLagAndDelay;
import static com.hazelcast.jet.WindowDefinition.slidingWindowDef;
import static com.hazelcast.jet.impl.connector.ReadWithPartitionIteratorP.readMap;
import static com.hazelcast.jet.processor.DiagnosticProcessors.writeLogger;
import static com.hazelcast.jet.processor.Processors.aggregateToSessionWindow;
import static com.hazelcast.jet.processor.Processors.insertWatermarks;
import static java.util.concurrent.TimeUnit.NANOSECONDS;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.AbstractMap;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;


import com.example.mu.domain.Trade;
import com.hazelcast.jet.AggregateOperation;
import com.hazelcast.jet.DAG;
import com.hazelcast.jet.Jet;
import com.hazelcast.jet.JetInstance;
import com.hazelcast.jet.Job;
import com.hazelcast.jet.Session;
import com.hazelcast.jet.Vertex;
import com.hazelcast.jet.WindowDefinition;
import com.hazelcast.jet.processor.Processors;
import com.hazelcast.jet.processor.Sinks;
import com.hazelcast.jet.stream.IStreamMap;

public class PostionAccountCreateStreamer {

	private final static String TRADE_MAP = "trade";
	public static final int MAX_LAG = 1000;
	private static final int SLIDING_WINDOW_LENGTH_MILLIS = 1000;
	private static final int SLIDE_STEP_MILLIS = 10;
	private static final int SESSION_TIMEOUT = 5000;

	
	public static void connectAndStreamToMap(String url) throws Exception {

		try {

			//load the map first
			
			JetInstance jet = Jet.newJetInstance();
			//JetInstance jet = Jet.newJetClient();
			Job job = jet.newJob(getDAG(url));
			long start = System.nanoTime();
			job.execute();
			// Thread.sleep(SECONDS.toMillis(JOB_DURATION));
			
			while (true) {
				
				
				Thread.sleep(10000);
			}
			

		}

		finally {

		}
	}
	
	
	private static DAG getDAG(String url) throws Exception {

		AggregateOperation<Trade, List<Object>, List<Object>> aggrOp = allOf(summingLong(e -> e.getQuantity()),
				mapping(e -> e.getPositionAccountId(), toSet()), mapping(e -> e.getInstrumentId(), toSet()));

		DAG dag = new DAG();
		WindowDefinition windowDef = slidingWindowDef(SLIDING_WINDOW_LENGTH_MILLIS, SLIDE_STEP_MILLIS);

		// source is the Trade Map from remote Hz cluster
		Vertex source = dag.newVertex("trade-source", readMap(TRADE_MAP, HzClientConfig.getClientConfig()));
		//Vertex source = dag.newVertex("trade-source", readMap(TRADE_MAP));

		// emit each trade
		Vertex tradeProcessor = dag.newVertex("trade-processor",
				Processors.map((Entry<String, Trade> f) -> f.getValue()));

		// create a watermark based on the Trade Timestamp (trade date)
		Vertex insertWatermarks = dag.newVertex("insert-watermarks",
				insertWatermarks(Trade::getTradeTime, limitingLagAndDelay(MAX_LAG, 100), emitByFrame(windowDef)));

		// aggregate
		Vertex aggregateSessions = dag.newVertex("aggregateSessions",
				aggregateToSessionWindow(SESSION_TIMEOUT, Trade::getTradeTime, Trade::getPositionAccountInstrumentKey, aggrOp));

		// print out
		Vertex sink = dag.newVertex("sink", writeLogger(PostionAccountCreateStreamer::sessionToString)).localParallelism(1);

		source.localParallelism(1);

		// connect the vertices

        dag.edge(between(source, tradeProcessor))
        .edge(between(tradeProcessor, insertWatermarks).isolated())
            //This edge needs to be partitioned+distributed. It is not possible
           // to calculate session windows in a two-stage fashion.
           .edge(between(insertWatermarks, aggregateSessions)
                   .partitioned(Trade::getPositionAccountInstrumentKey)
                   .distributed())
           .edge(between(aggregateSessions, sink));
		 //dag.edge(between(tradeMapper, sink));

		return dag;
	}

	private static String sessionToString(Session<String, List<Long>> s) {
		String ret= String.format("Session{userId=%s, start=%s, duration=%2ds, value={quantity=%2d, positionAccountId=%s, InstrumentId=%s}", s.getKey(), // userId
				Instant.ofEpochMilli(s.getStart()).atZone(ZoneId.systemDefault()).toLocalTime(), // session start
				Duration.ofMillis(s.getEnd() - s.getStart()).getSeconds(), // session duration
				s.getResult().get(0), // position aty
				s.getResult().get(1), // Position account id
				s.getResult().get(2)); // Instrument id
		
		System.out.println("Final result is "+ret);
		
		return ret;
		
	}

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		PostionAccountCreateStreamer.connectAndStreamToMap(null);
	}

}

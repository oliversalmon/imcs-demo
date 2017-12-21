package com.mu.flink.streamer;

import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.mu.domain.PositionAccount;
import com.example.mu.domain.Price;
import com.example.mu.domain.Trade;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

public class PositionAggregator extends RichFlatMapFunction<Tuple2<String, Trade>, PositionAccount> {

	/**
	* 
	*/
	private static final long serialVersionUID = 1L;
	private transient ValueState<Tuple2<String, PositionAccount>> sumOfQty;
	private transient ValueState<Tuple2<String, Long>> startTime;

	final static Logger LOG = LoggerFactory.getLogger(PositionAggregator.class);

	@Override
	public void flatMap(Tuple2<String, Trade> arg0, Collector<PositionAccount> arg1) throws Exception {

		LOG.info("Starting to calculate Positions...");

		// get the particulars of the trade to set the position correctly
		String accountId = arg0.f1.getPositionAccountInstrumentKey().split("&")[0];
		String instrumnetId = arg0.f1.getPositionAccountInstrumentKey().split("&")[1];

		LOG.info("for the following instrument and position " + accountId + " " + instrumnetId);

		// get the time
		Tuple2<String, Long> timer = startTime.value();
		Long starttime = timer.f1;

		if (timer.f0.equals(""))
			timer.f0 = arg0.f0;

		// first set the quantity
		Tuple2<String, PositionAccount> positionTuple = sumOfQty.value();
		PositionAccount currentPosition = positionTuple.f1;

		// set the key to be the position Instrument key
		if (positionTuple.f0.equals(""))
			positionTuple.f0 = arg0.f0;

		// make sure the Position is properly created before setting the quantity
		if (currentPosition.getAccountId() == null)
			currentPosition.setAccountId(accountId);
		if (currentPosition.getInstrumentid() == null)
			currentPosition.setInstrumentid(instrumnetId);

		long qty = currentPosition.getSize();
		currentPosition.setSize(qty += arg0.f1.getQuantity());

		LOG.info("position quantity is " + currentPosition.getSize());

		// now set the pnl
		// get the spot Px
		HazelcastInstance hzClient = TradeFlinkStreamer.getHzClient();
		IMap<String, Price> mapPrice = hzClient.getMap("price");
		Price spotPx = mapPrice.get(instrumnetId);
		if (spotPx == null)
			LOG.warn("NO Spot PX available for the following instrument id, not calculating PnL " + instrumnetId);
		else {
			LOG.info("Spot px used =" + spotPx.getPrice());
			LOG.info("traded value is " + arg0.f1.getTradeValue());
			double pnl = spotPx.getPrice() * arg0.f1.getQuantity() - arg0.f1.getTradeValue();
		
			LOG.info("Pnl calculated is " + pnl);
			double currentPnl = currentPosition.getPnl();
			currentPosition.setPnl( currentPnl+= pnl);
		}

		// update the state
		sumOfQty.update(positionTuple);

		// emit position state every 200 millis
		double elapsed = System.currentTimeMillis() - starttime;
		LOG.info("time elapsed "+elapsed);
		if (elapsed > 200) {
			arg1.collect(currentPosition);
			startTime.clear();
			sumOfQty.clear();
			LOG.info("emitting current Position");
		}

	}

	@Override
	public void open(Configuration config) {
		@SuppressWarnings("deprecation")
		ValueStateDescriptor<Tuple2<String, PositionAccount>> descriptor = new ValueStateDescriptor<Tuple2<String, PositionAccount>>(
				"positionQty", // the
				// state
				// name
				TypeInformation.of(new TypeHint<Tuple2<String, PositionAccount>>() {
				}), // type information
				Tuple2.of("", new PositionAccount())); // default value of the state, if nothing was set
		sumOfQty = getRuntimeContext().getState(descriptor);

		// start the clock

		@SuppressWarnings("deprecation")
		ValueStateDescriptor<Tuple2<String, Long>> time = new ValueStateDescriptor<Tuple2<String, Long>>("time", // the
																													// state
																													// name
				TypeInformation.of(new TypeHint<Tuple2<String, Long>>() {
				}), // type information
				Tuple2.of("", System.currentTimeMillis())); // default value of the state, if nothing was set
		startTime = getRuntimeContext().getState(time);
	}

}

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

public class PositionAggregator extends RichFlatMapFunction<Trade, PositionAccount> {

	/**
	* 
	*/
	private static final long serialVersionUID = 1L;
	private transient ValueState<PositionAccount> sumOfQty;
	private transient ValueState<Long> startTime;
	

	final static Logger LOG = LoggerFactory.getLogger(PositionAggregator.class);

	@Override
	public void flatMap(Trade arg0, Collector<PositionAccount> arg1) throws Exception {

		LOG.info("Starting to calculate Positions...");

		// get the particulars of the trade to set the position correctly
		String accountId = arg0.getPositionAccountInstrumentKey().split("&")[0];
		String instrumnetId = arg0.getPositionAccountInstrumentKey().split("&")[1];

		LOG.info("for the following instrument and position " + accountId + " " + instrumnetId);

		// first set the quantity
		PositionAccount currentPosition = sumOfQty.value();

		// make sure the Position is properly created before setting the quantity
		if (currentPosition.getAccountId() == null)
			currentPosition.setAccountId(accountId);
		if (currentPosition.getInstrumentid() == null)
			currentPosition.setInstrumentid(instrumnetId);

		long qty = currentPosition.getSize();
		currentPosition.setSize(qty += arg0.getQuantity());

		LOG.info("position quantity is " + currentPosition.getSize());

		// now set the pnl
		//get the spot Px
		HazelcastInstance hzClient = TradeFlinkStreamer.getHzClient();
		IMap<String, Price> mapPrice = hzClient.getMap("price");
		Price spotPx = mapPrice.get(instrumnetId);
		if(spotPx == null)
			LOG.warn("NO Spot PX available for the following instrument id, not calculating PnL "+instrumnetId);
		else {
			LOG.info("Spot px used ="+spotPx.getPrice());
			LOG.info("traded value is "+arg0.getTradeValue());
			double pnl = spotPx.getPrice()*arg0.getQuantity() - arg0.getTradeValue();
			pnl+= currentPosition.getPnl();
			LOG.info("Pnl calculated is "+pnl);
			currentPosition.setPnl(pnl);
		}

		// update the state
		sumOfQty.update(currentPosition);

		// emit for every 1 minute of trades (needs to be configurable)
		double timepassed = System.currentTimeMillis()-startTime.value();
		LOG.info("Number of millis passed "+timepassed);
		if (System.currentTimeMillis()-startTime.value() > 60000) {
			arg1.collect(currentPosition);
			sumOfQty.clear();
			startTime.clear();

			LOG.info("emitting current Position");

		}

	}

	@Override
	public void open(Configuration config) {
		@SuppressWarnings("deprecation")
		ValueStateDescriptor<PositionAccount> descriptor = new ValueStateDescriptor<PositionAccount>("positionQty", // the
																													// state
																													// name
				TypeInformation.of(new TypeHint<PositionAccount>() {
				}), // type information
				new PositionAccount()); // default value of the state, if nothing was set
		sumOfQty = getRuntimeContext().getState(descriptor);

		// start the clock

		@SuppressWarnings("deprecation")
		ValueStateDescriptor<Long> time = new ValueStateDescriptor<Long>("time", // the state name
				TypeInformation.of(new TypeHint<Long>() {
				}), // type information
				System.currentTimeMillis()); // default value of the state, if nothing was set
		startTime = getRuntimeContext().getState(time);
	}

}

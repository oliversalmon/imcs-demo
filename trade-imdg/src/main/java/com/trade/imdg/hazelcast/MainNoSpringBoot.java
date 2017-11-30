package com.trade.imdg.hazelcast;

import static com.example.mu.database.MuSchemaConstants.HBASE_HOST;
import static com.example.mu.database.MuSchemaConstants.ZK_HOST;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.mu.database.Schema;
import com.example.mu.domain.Instrument;
import com.example.mu.domain.Party;
import com.example.mu.domain.Price;
import com.example.mu.domain.Trade;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.jet.Jet;
import com.hazelcast.jet.JetInstance;

public class MainNoSpringBoot {
	public static final String MAP_INSTRUMENTS = "instrument";
	private final static String PRICE_MAP = "price";
	public static final Logger LOG = LoggerFactory.getLogger(MainNoSpringBoot.class);
	public static HazelcastInstance hz = Hazelcast.newHazelcastInstance();

	public static void startUpHz() throws Exception {

		// load up prices as well
		IMap<String, Price> prices = hz.getMap(PRICE_MAP);
		LOG.info("Size of prices is " + prices.size());
		LOG.info("Loading prices ...");

		IMap<String, Instrument> instruments = hz.getMap(MAP_INSTRUMENTS);
		LOG.info("Size of Instruments is " + instruments.size());
		LOG.info("Loading instruments ...");

		// instruments.loadAll(true);

		if (instruments.size() > 3000) {

			LOG.info("Not loading from file " + instruments.size());
			return;
		}
		
		// Seed the instruments
		//Group, Inc. - Common Stock|Q|N|N|100|N|N
		
			LOG.info("Loading from file nasdaqlisted.txt");
					try (BufferedReader reader = new BufferedReader(
							new InputStreamReader(Main.class
									.getResourceAsStream("/nasdaqlisted.txt"),
									StandardCharsets.UTF_8))) {

						reader.lines()
								.skip(1)
								// .limit(100)
								.map(l -> Arrays.asList(l.split("\\|")))
								.forEach(
										t -> instruments.put(t.get(0),
												constructInstrument(t)));
					} catch (IOException e) {
						throw new RuntimeException(e);
					}

					LOG.info("Size of Instruments is " + instruments.size());

	}
	
	private static Instrument constructInstrument(List<String> instrumentData) {
		Instrument aInstrument = new Instrument();
		aInstrument.setAssetClass(instrumentData.get(0));
		aInstrument.setInstrumentId(instrumentData.get(0));
		aInstrument.setIssuer(instrumentData.get(1));
		aInstrument.setSymbol(instrumentData.get(0));
		
		//Added as part of this defect
		aInstrument.setProduct(instrumentData.get(0));

		return aInstrument;
	}

	public static void main(String[] args) throws Exception {

		MainNoSpringBoot.startUpHz();

	}

}

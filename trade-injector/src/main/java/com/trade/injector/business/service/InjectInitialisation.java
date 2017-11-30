package com.trade.injector.business.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.mu.domain.Instrument;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;


@Component
public class InjectInitialisation {
	
	final Logger LOG = LoggerFactory.getLogger(InjectInitialisation.class);
	public static final String MAP_INSTRUMENTS = "instrument";
	
	@Autowired
	private HazelcastInstance hazelcastInstance;

	@PostConstruct
	public void init() {
		
		LOG.info("Initialising instrument data...");
		IMap<String, Instrument> instruments = hazelcastInstance
				.getMap(MAP_INSTRUMENTS);
		
		//do not do anything is the map is already populated
		if(instruments.size() > 3000) return;
		
		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(InjectInitialisation.class
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
		// instruments.loadAll(false);
		
		
		LOG.info("Done.");
	}
	
	@PreDestroy
	public void shutDownCache() {
		LOG.info("Shutting down the Instrumment and Party Cache ...");
		//jet.shutdown();
		LOG.info("Completed shutdown");
	}
	
	private Instrument constructInstrument(List<String> instrumentData) {
		Instrument aInstrument = new Instrument();
		aInstrument.setAssetClass(instrumentData.get(0));
		aInstrument.setInstrumentId(instrumentData.get(0));
		aInstrument.setIssuer(instrumentData.get(1));
		aInstrument.setSymbol(instrumentData.get(0));
		aInstrument.setProduct(instrumentData.get(0));

		return aInstrument;
	}

}

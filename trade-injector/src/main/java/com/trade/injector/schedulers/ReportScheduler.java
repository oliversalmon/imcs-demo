package com.trade.injector.schedulers;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.mu.domain.Price;
import com.example.mu.domain.Trade;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.trade.injector.business.service.BusinessServiceCacheNames;
import com.trade.injector.controller.TradeInjectorController;
import com.trade.injector.jto.InstrumentReport;
import com.trade.injector.jto.TradeInjectorMessage;
import com.trade.injector.jto.TradeInjectorProfile;
import com.trade.injector.jto.TradeReport;
import com.trade.injector.jto.repository.TradeInjectorMessageRepository;
import com.trade.injector.jto.repository.TradeInjectorProfileRepository;
import com.trade.injector.jto.repository.TradeReportRepository;

@Component
public class ReportScheduler {

	private static final String REPORT_NAME = "MASTER_REPORT";
	final Logger LOG = LoggerFactory.getLogger(ReportScheduler.class);

	@Autowired
	private SimpMessagingTemplate messageSender;

	@Autowired(required = true)
	private TradeInjectorMessageRepository repo;

	@Autowired
	private TradeReportRepository reportRepo;

	@Autowired
	private TradeInjectorProfileRepository profileRepo;

	@Autowired
	HazelcastInstance hazelcastInstance;

	@Autowired
	private MongoTemplate coreTemplate;

	//@Value("${webservices.priceservice.baseURL}")
	//private String webServicesPriceBaseURL;

	//@Bean
	//WebClient priceQueryClient() {
		//return WebClient.create(webServicesPriceBaseURL);
	//}

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

	@Scheduled(fixedDelay = 1000)
	public void publishInjectProfiles() {
		List<TradeInjectorProfile> listOfProfiles = profileRepo.findAll();
		messageSender.convertAndSend("/topic/tradeMessageInject", listOfProfiles);
	}

	@Scheduled(fixedDelay = 3000)
	public void pushCountStatistics() throws Exception {

		TradeReport tradeReport = coreTemplate.findOne(Query.query(Criteria.where("injectorProfileId").is(REPORT_NAME)),
				TradeReport.class);

		if (tradeReport == null)
			tradeReport = createTradeReport();

		// first generate trade stats
		generateTradeStats(tradeReport);

		// next generate instrument stats
		generateInstrumentReport(tradeReport);

		List<TradeReport> listOfReports = reportRepo.findAll();
		messageSender.convertAndSend("/topic/tradeAck", listOfReports);

	}

	private void generateTradeStats(TradeReport report) {

		if (report == null)
			return;

		LOG.info("Starting to generate report stats...");
		// first get all the prices from the cache
		IMap<String, Trade> mapTrades = hazelcastInstance.getMap("trade");

		mapTrades.loadAll(false);
		// no data do not generate
		if (mapTrades.size() == 0) {
			LOG.warn("No trades found");
			return;
		}

		LOG.info("trade count is " + mapTrades.size());
		report.setTradeCount(mapTrades.size());
		reportRepo.save(report);

	}

	private void generateInstrumentReport(TradeReport report) {

		if (report == null)
			return;

		LOG.info("Starting to obtain prices...");
		// first get all the prices from the cache
		IMap<String, Price> mapPrices = hazelcastInstance.getMap(BusinessServiceCacheNames.PRICE_CACHE);
		// mapPrices.loadAll(false);
		// no data do not generate
		if (mapPrices.size() == 0) {
			LOG.warn("No prices found");
			return;
		}

		List<InstrumentReport> instrumentReport = report.getInstruments();

		if (instrumentReport.size() == 0)
			report.setInstruments(
					mapPrices.values().stream().map(p -> createNewInstrumentReport(p)).collect(Collectors.toList()));
		else {
			if(mapPrices.size() !=0)
			instrumentReport.stream().forEach(x -> updateInstrumentReportPrice(x, mapPrices.get(x.getId())));
		}
		// generate and sink to Kafka
		Collection<Price> px = mapPrices.values();
		LOG.info("Number of prices " + px.size());
		// ins.stream().forEach(a->sender.send(marketDataTopic,GeneratePriceData.generateRandomDataOnInstruments(a).toJSON()));

		reportRepo.save(report);
		LOG.info("Prices obtained done");
	}

	private void updateInstrumentReportPrice(InstrumentReport report, Price aPrice) {

		report.setPrevPrice(report.getPrice());
		report.setPrice(aPrice.getPrice());

	}

	private TradeReport createTradeReport() throws Exception {

		// initialise the report if not found and create the Instrument report
		// for each price

		// create a new one
		TradeReport tradeReport = new TradeReport();
		tradeReport.setCurrentTradeProgress(1);
		tradeReport.setInjectorProfileId(REPORT_NAME);
		tradeReport.setName("Report_" + REPORT_NAME);
		tradeReport.setReportDate(new Date(System.currentTimeMillis()));
		tradeReport.setTradeCount(1);
		// tradeReport.setUserId(username);

		// //tradeReport.setInstruments(instruments);
		List<InstrumentReport> instrumentList = new ArrayList<InstrumentReport>();
		// instrumentList =
		// prices.stream().map(p->createNewInstrumentReport(p)).collect(Collectors.toList());
		tradeReport.setInstruments(instrumentList);

		// now go through each price and update the Instrument report with the
		// current and previous price
		return tradeReport;

	}

	private InstrumentReport createNewInstrumentReport(Price aPrice) {
		InstrumentReport report = new InstrumentReport();
		report.setId(aPrice.getInstrumentId());
		report.setPrice(aPrice.getPrice());
		report.setPrevPrice(aPrice.getPrice());
		return report;
	}

	private void createOrUpdateInstrumentReport(Price aPrice, TradeReport report) {

		//initialise the list first
		if(report.getInstruments().size() <1) {
			report.getInstruments().add(createNewInstrumentReport(aPrice));
			return;
		}
			
		
		report.getInstruments().stream().forEach(x -> {
			if (x.getId().equals(aPrice.getInstrumentId()))
				updateInstrumentReportPrice(x, aPrice);
			else
				report.getInstruments().add(createNewInstrumentReport(aPrice));
		});

		
	}
}

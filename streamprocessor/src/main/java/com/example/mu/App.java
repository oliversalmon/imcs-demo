package com.example.mu;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The main class that starts up the cluster nodes based on the profile that is
 * passed in
 *
 */
public class App {
	final static Logger LOG = LoggerFactory.getLogger(App.class);

	public static void main(String[] args) throws Exception {

		//String clusterProfile = System.getProperty("hzJetClusterProfile");
		if(args ==  null || args.length < 5)
			throw new Exception("No arguments passed into this program");
		
		String clusterProfile = args[0].substring(args[0].indexOf('=')+1, args[0].length());
		
		try {
			MuCuratorClient.initiate(null);
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		if (clusterProfile == null)
			throw new Exception(
					"hzJetClusterProfile cannot be null, please set to sensible values like tradeStreamer=1,priceStreamer=0,positionStreamer=1 where each value denotes the number of nodes to run for Trade, Price and Position streamers respectively");

		LOG.info("Cluster profile is " + clusterProfile);
		Arrays.stream(clusterProfile.split(",")).parallel().forEach(x -> run(x, args));

	}

	private static void run(String clusterNodes, String[] args) {

		String kafkaUrl = args[1].substring(args[1].indexOf('=')+1, args[1].length());
		String hzHost = args[2].substring(args[2].indexOf('=')+1, args[2].length());
		String runs = args[3].substring(args[3].indexOf('=')+1, args[3].length());
		String delay = args[4].substring(args[4].indexOf('=')+1, args[4].length());
		
		
		String[] clusterNodeDef = clusterNodes.split("=");
		if (clusterNodeDef[0].equals("tradeStreamer")) {

			int tradeStreamer = new Integer(clusterNodeDef[1]).intValue();
			if(tradeStreamer < 1) return;
			ExecutorService executor = Executors.newFixedThreadPool(tradeStreamer);
			for (int i = 0; i < tradeStreamer; i++)
				try {

					executor.submit(() -> {
						try {
							TradeStreamer.connectAndStreamToMap(kafkaUrl);
						} catch (Exception e) {
							e.printStackTrace();
							LOG.error(e.getMessage());
						}

					});

				} catch (Exception e) {
					e.printStackTrace();
					LOG.error(e.getMessage());
				}

		}

		if (clusterNodeDef[0].equals("priceStreamer")) {
			int priceStreamer = new Integer(clusterNodeDef[1]).intValue();
			if(priceStreamer < 1) return;
			ExecutorService executor = Executors.newFixedThreadPool(priceStreamer);
			for (int i = 0; i < priceStreamer; i++)
				try {
					executor.submit(() -> {
						try {
							PriceStreamer.connectAndStreamToMap(kafkaUrl);
						} catch (Exception e) {
							e.printStackTrace();
							LOG.error(e.getMessage());
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
					LOG.error(e.getMessage());
				}

		}

		if (clusterNodeDef[0].equals("positionStreamer")) {
			int positionStreamer = new Integer(clusterNodeDef[1]).intValue();
			
			if(positionStreamer < 1) return;
			ExecutorService executor = Executors.newFixedThreadPool(positionStreamer);
			
			for (int i = 0; i < positionStreamer; i++)
				try {
					executor.submit(() -> {
						try {
							PositionStreamer.connectAndUpdatePositions(hzHost,
									runs, delay);
						} catch (Exception e) {
							e.printStackTrace();
							LOG.error(e.getMessage());
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
					LOG.error(e.getMessage());
				}

		}

	}
}

package com.example.mu;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.mu.cachefactory.MuCacheFactory;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.ClientNetworkConfig;
import com.hazelcast.client.config.XmlClientConfigBuilder;
import com.hazelcast.config.DiscoveryConfig;
import com.hazelcast.config.DiscoveryStrategyConfig;
import com.hazelcast.config.GroupConfig;
import com.hazelcast.config.SerializationConfig;
import com.hazelcast.nio.serialization.PortableFactory;

public class HzClientConfig {

	private static ClientConfig clientConfig;
	final static Logger LOG = LoggerFactory.getLogger(HzClientConfig.class);

	public static ClientConfig getClientConfig() {

		if (clientConfig == null) {
			InputStream configInputStream = HzClientConfig.class.getResourceAsStream("/hazelcast-client.xml");
			clientConfig = new XmlClientConfigBuilder(configInputStream).build();
			// clientConfig.setSerializationConfig(serializationConfig)
			SerializationConfig szConfig = clientConfig.getSerializationConfig();
			if (szConfig != null) {
				PortableFactory mufactory = new MuCacheFactory();
				Map<Integer, PortableFactory> mapFactories = new HashMap<Integer, PortableFactory>();
				mapFactories.put(1, mufactory);
				szConfig.setPortableFactories(mapFactories);
			}

		}
		
		//log to see if Zookeeper is set
		//LOG.info("ZOOKEEPER for hzClient is "+clientConfig.ge);

		return clientConfig;

	}

	public static ClientConfig buildClientConfig(String hzZooHost) {
		if (clientConfig == null) {
			
			LOG.info("BUILDING CLIENT CONFIG FOR HZ");
			clientConfig = new ClientConfig();
			clientConfig.setProperty("hazelcast.discovery.enabled", "true");

			ClientNetworkConfig cnc = new ClientNetworkConfig();
			cnc.setSmartRouting(false);
			cnc.setRedoOperation(false);

			DiscoveryConfig dcfg = new DiscoveryConfig();
			List<DiscoveryStrategyConfig> discoveryStrategyConfigs = new ArrayList<DiscoveryStrategyConfig>();
			DiscoveryStrategyConfig aConfig = new DiscoveryStrategyConfig(
					"com.hazelcast.zookeeper.ZookeeperDiscoveryStrategy");
			aConfig.addProperty("zookeeper_url", hzZooHost);
			aConfig.addProperty("zookeeper_path", "/discovery/hazelcast");
			aConfig.addProperty("group", "kappa-serving-layer");

			discoveryStrategyConfigs.add(aConfig);
			dcfg.setDiscoveryStrategyConfigs(discoveryStrategyConfigs);
			cnc.setDiscoveryConfig(dcfg);

			GroupConfig gpcfg = new GroupConfig();
			gpcfg.setName("kappa-serving-layer");
			gpcfg.setPassword("kappa-password");

			SerializationConfig serializationConfig = new SerializationConfig();
			PortableFactory mufactory = new MuCacheFactory();
			Map<Integer, PortableFactory> mapFactories = new HashMap<Integer, PortableFactory>();
			mapFactories.put(1, mufactory);
			serializationConfig.setPortableFactories(mapFactories);

			clientConfig.setGroupConfig(gpcfg);
			clientConfig.setNetworkConfig(cnc);
			clientConfig.setSerializationConfig(serializationConfig);
			LOG.info("DONE");
			
		}

		return clientConfig;

	}

}

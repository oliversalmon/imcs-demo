package com.example.mu;

import java.io.InputStream;

import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.XmlClientConfigBuilder;

public class HzClientConfig {
	
	public static ClientConfig getClientConfig() {
		
		  InputStream configInputStream =
				  HzClientConfig.class.getResourceAsStream("/hazelcast-client.xml");
			    ClientConfig clientConfig = new XmlClientConfigBuilder(configInputStream).build();
			    return clientConfig;
		
	}

}

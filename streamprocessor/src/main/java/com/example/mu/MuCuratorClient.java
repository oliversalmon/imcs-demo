package com.example.mu;

import org.apache.commons.lang.ObjectUtils.Null;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceProvider;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.spi.discovery.integration.DiscoveryServiceProvider;



public class MuCuratorClient {

	static ServiceProvider serviceProvider;

	public static void initiate(String[] args) throws Exception {

		
		//ClientConfig cfg = HzClientConfig.buildClientConfig("192.168.1.176:2181");
	  //cfg.getNetworkConfig().getAddresses().forEach(a->System.out.println(a));
	  
	  //HazelcastInstance hzClient = HazelcastClient.newHazelcastClient(HzClientConfig.getClientConfig());
		//System.out.println(hzClient.getConfig().getNetworkConfig().getPublicAddress());
		
		CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient("zookeeper-1.vnet:2181",
				new RetryNTimes(5, 1000));
		curatorFramework.start();
		

		ServiceDiscovery<Void> serviceDiscovery = ServiceDiscoveryBuilder.builder(Void.class)
				.basePath("/discovery").client(curatorFramework).build();
		serviceDiscovery.start();

		serviceProvider = serviceDiscovery.serviceProviderBuilder().serviceName("hazelcast").build();
		serviceProvider.start();

	}

	public static void main(String[] args) throws Exception{
		
		MuCuratorClient.initiate(null);

	}

}

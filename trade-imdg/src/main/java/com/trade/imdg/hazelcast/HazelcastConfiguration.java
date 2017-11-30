package com.trade.imdg.hazelcast;

import java.util.List;
import java.util.Collection;
import java.util.stream.Collectors;

import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceInstance;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.MulticastConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.config.TcpIpConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

//@Configuration
public class HazelcastConfiguration {

	@Bean(destroyMethod = "shutdown")
	HazelcastInstance hazelcast(Config config) {
		return Hazelcast.newHazelcastInstance(config);
	}

	@Bean
	Config config(ApplicationContext applicationContext,
			NetworkConfig networkConfig) {
		final Config config = new Config();
		config.setNetworkConfig(networkConfig);
		config.getGroupConfig().setName(applicationContext.getId());
		return config;
	}

	@Bean
	NetworkConfig networkConfig(@Value("${hazelcast.port:5701}") int port,
			JoinConfig joinConfig) {
		final NetworkConfig networkConfig = new NetworkConfig();
		networkConfig.setJoin(joinConfig);
		networkConfig.setPort(port);
		return networkConfig;
	}

	@Bean
	JoinConfig joinConfig(TcpIpConfig tcpIpConfig) {
		final JoinConfig joinConfig = disabledMulticast();
		joinConfig.setTcpIpConfig(tcpIpConfig);
		return joinConfig;
	}

	private JoinConfig disabledMulticast() {
		JoinConfig join = new JoinConfig();
		final MulticastConfig multicastConfig = new MulticastConfig();
		multicastConfig.setEnabled(false);
		join.setMulticastConfig(multicastConfig);
		return join;
	}

	@Bean
	TcpIpConfig tcpIpConfig(ApplicationContext applicationContext,
			ServiceDiscovery<Void> serviceDiscovery) throws Exception {
		final TcpIpConfig tcpIpConfig = new TcpIpConfig();
		final List<String> instances = queryOtherInstancesInZk(
				applicationContext.getId(), serviceDiscovery);
		tcpIpConfig.setMembers(instances);
		tcpIpConfig.setEnabled(true);
		return tcpIpConfig;
	}

	private List<String> queryOtherInstancesInZk(String name, ServiceDiscovery<Void> serviceDiscovery) throws Exception {
	return serviceDiscovery
		.queryForInstances(name)
		.stream()
		.map(ServiceInstance::buildUriSpec)
		.collect(Collectors.toList());
	}
}

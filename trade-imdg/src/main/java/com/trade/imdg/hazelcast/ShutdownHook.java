package com.trade.imdg.hazelcast;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.jet.JetInstance;

@Component
public class ShutdownHook implements DisposableBean {

	//@Autowired
	//private JetInstance jet;

	@Autowired
	private HazelcastInstance hzInstance;

	@Override
	public void destroy() throws Exception {
		
		//jet.shutdown();
		hzInstance.shutdown();
	}

}

package com.trade.imdg.jet.test;

import com.hazelcast.jet.Jet;
import com.hazelcast.jet.JetInstance;

public class TestJetStartUp {

	
	public static void main(String[] args) {
        // Will clean up in case of an exception:
        Runtime.getRuntime().addShutdownHook(new Thread(Jet::shutdownAll));

        JetInstance instance1 = Jet.newJetInstance();
        JetInstance instance2 = Jet.newJetInstance();

        

        // Will clean up when there's no exception:
        Jet.shutdownAll();
    }

}

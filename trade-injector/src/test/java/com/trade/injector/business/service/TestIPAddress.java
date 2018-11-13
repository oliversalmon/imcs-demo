package com.trade.injector.business.service;

import org.junit.Test;

import java.net.InetAddress;

public class TestIPAddress {

    @Test
    public void testIPAddress() throws Exception{

        System.out.println("Address is "+ InetAddress.getLocalHost().getHostAddress());

    }
}

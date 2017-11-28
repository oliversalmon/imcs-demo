package com.example.mu.domain;

import junit.framework.TestCase;

/**
 * Created by oliverbuckley-salmon on 05/05/2017.
 */
public class InstrumentTest extends TestCase {
    public void testToJSON() throws Exception {
        Instrument inst = new Instrument();
        inst.setInstrumentId("1");
        inst.setIssuer("Bank of Oliver");
        inst.setAssetClass("Equities");
        inst.setProduct("Cash Equity");
        System.out.println(inst.toJSON());
    }

}
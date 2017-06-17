package com.example.mu.maplisteners;

import com.example.mu.domain.Price;
import com.hazelcast.core.EntryEvent;
import com.hazelcast.map.listener.EntryAddedListener;

/**
 * Created by oliverbuckley-salmon on 18/05/2017.
 */
public class PriceListener implements EntryAddedListener<String, Price> {


    public PriceListener() {
    }

    @Override
    public void entryAdded(EntryEvent<String, Price> entryEvent) {

    }
}

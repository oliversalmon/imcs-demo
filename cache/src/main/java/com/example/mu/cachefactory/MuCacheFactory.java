package com.example.mu.cachefactory;

import com.example.mu.domain.*;
import com.hazelcast.nio.serialization.Portable;
import com.hazelcast.nio.serialization.PortableFactory;

/**
 * Created by oliverbuckley-salmon on 02/05/2017.
 */
public class MuCacheFactory implements PortableFactory{


    public Portable create(int classId ) {
        if ( Trade.CLASS_ID == classId )
            return new Trade();
        else if(Party.CLASS_ID == classId)
            return new Party();
        else if (Instrument.CLASS_ID == classId)
            return new Instrument();
        else if(Price.CLASS_ID == classId)
            return new Price();
        else if(PositionAccount.CLASS_ID == classId)
            return new PositionAccount();
        else
            return null;
    }
}

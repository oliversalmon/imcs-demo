package com.example.mu.domain;

import com.google.gson.Gson;
import com.hazelcast.nio.serialization.Portable;
import com.hazelcast.nio.serialization.PortableReader;
import com.hazelcast.nio.serialization.PortableWriter;

import java.io.IOException;

/**
 * Created by oliverbuckley-salmon on 28/04/2017.
 */
public class Price implements Portable{

    private String  priceId
    ,               instrumentId;
    private double  price;
    private long    timeStamp;
    public static final int FACTORY_ID = 0
    ,                       CLASS_ID = 4;


    public Price() {
    }

    public Price(String priceId, String instrumentId, double price, long timeStamp) {
        this.priceId = priceId;
        this.instrumentId = instrumentId;
        this.price = price;
        this.timeStamp = timeStamp;
    }

    public String getPriceId() {
        return priceId;
    }

    public void setPriceId(String priceId) {
        this.priceId = priceId;
    }

    public String getInstrumentId() {
        return instrumentId;
    }

    public void setInstrumentId(String instrumentId) {
        this.instrumentId = instrumentId;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getFactoryId() {
        return FACTORY_ID;
    }

    public int getClassId() {
        return CLASS_ID;
    }

    public void writePortable(PortableWriter out) throws IOException {
        out.writeUTF("priceId", priceId);
        out.writeUTF("instrumentId", instrumentId);
        out.writeDouble("price", price);
        out.writeLong("timeStamp", timeStamp);
    }


    public void readPortable(PortableReader in) throws IOException {
        this.priceId = in.readUTF("priceId");
        this.instrumentId = in.readUTF("instrumentId");
        this.price = in.readDouble("price");
        this.timeStamp = in.readLong("timeStamp");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Price price1 = (Price) o;

        if (Double.compare(price1.price, price) != 0) return false;
        if (timeStamp != price1.timeStamp) return false;
        if (!priceId.equals(price1.priceId)) return false;
        return instrumentId.equals(price1.instrumentId);
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = priceId.hashCode();
        result = 31 * result + instrumentId.hashCode();
        temp = Double.doubleToLongBits(price);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (int) (timeStamp ^ (timeStamp >>> 32));
        return result;
    }

    public String toJSON(){
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}

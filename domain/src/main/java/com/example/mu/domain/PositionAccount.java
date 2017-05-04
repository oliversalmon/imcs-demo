package com.example.mu.domain;

import com.google.gson.Gson;
import com.hazelcast.nio.serialization.Portable;
import com.hazelcast.nio.serialization.PortableReader;
import com.hazelcast.nio.serialization.PortableWriter;

import java.io.IOException;

/**
 * Created by oliverbuckley-salmon on 03/05/2017.
 */
public class PositionAccount implements Portable{

    private String  accountId
    ,               instrumentid;
    private long    size;
    private double  pnl;

    public static final int FACTORY_ID = 1
    ,                       CLASS_ID = 5;

    public PositionAccount() {
    }

    public PositionAccount(String accountId, String instrumentid, long size, double pnl) {
        this.accountId = accountId;
        this.instrumentid = instrumentid;
        this.size = size;
        this.pnl = pnl;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getInstrumentid() {
        return instrumentid;
    }

    public void setInstrumentid(String instrumentid) {
        this.instrumentid = instrumentid;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public double getPnl() {
        return pnl;
    }

    public void setPnl(double pnl) {
        this.pnl = pnl;
    }

    @Override
    public int getFactoryId() {
        return FACTORY_ID;
    }

    @Override
    public int getClassId() {
        return CLASS_ID;
    }

    public void writePortable(PortableWriter out) throws IOException {
        out.writeUTF("accountId" , accountId);
        out.writeUTF("instrumentid" , instrumentid);
        out.writeLong("size" , size);
        out.writeDouble("pnl" , pnl);
    }


    public void readPortable(PortableReader in) throws IOException {
        this.accountId = in.readUTF("accountId");
        this.instrumentid = in.readUTF("instrumentid");
        this.size = in.readLong("size");
        this.pnl = in.readDouble("pnl");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PositionAccount that = (PositionAccount) o;

        if (size != that.size) return false;
        if (Double.compare(that.pnl, pnl) != 0) return false;
        if (!accountId.equals(that.accountId)) return false;
        return instrumentid.equals(that.instrumentid);
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = accountId.hashCode();
        result = 31 * result + instrumentid.hashCode();
        result = 31 * result + (int) (size ^ (size >>> 32));
        temp = Double.doubleToLongBits(pnl);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    public String toJSON(){
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}

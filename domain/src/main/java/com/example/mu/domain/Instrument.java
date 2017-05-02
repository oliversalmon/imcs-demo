package com.example.mu.domain;

import com.google.gson.Gson;
import com.hazelcast.nio.serialization.Portable;
import com.hazelcast.nio.serialization.PortableReader;
import com.hazelcast.nio.serialization.PortableWriter;

import java.io.IOException;

/**
 * Created by oliverbuckley-salmon on 28/04/2017.
 */
public class Instrument implements Portable {

    private String  instrumentId
    ,               symbol
    ,               product
    ,               assetClass
    ,               issuer;
    public static final int FACTORY_ID =0
    ,                       CLASS_ID = 2;


    public Instrument() {
    }

    public Instrument(String instrumentId, String symbol, String product, String assetClass, String issuer) {
        this.instrumentId = instrumentId;
        this.symbol = symbol;
        this.product = product;
        this.assetClass = assetClass;
        this.issuer = issuer;
    }

    public String getInstrumentId() {
        return instrumentId;
    }

    public void setInstrumentId(String instrumentId) {
        this.instrumentId = instrumentId;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getAssetClass() {
        return assetClass;
    }

    public void setAssetClass(String assetClass) {
        this.assetClass = assetClass;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public int getFactoryId() {
        return FACTORY_ID;
    }

    public int getClassId() {
        return CLASS_ID;
    }

    public void writePortable(PortableWriter out) throws IOException {
        out.writeUTF("instrumentId", instrumentId);
        out.writeUTF("symbol", symbol);
        out.writeUTF("product", product);
        out.writeUTF("assetClass", assetClass);
        out.writeUTF("issuer", issuer);
    }


    public void readPortable(PortableReader in) throws IOException {
        this.instrumentId = in.readUTF("instrumentId");
        this.symbol = in.readUTF("symbol");
        this.product = in.readUTF("product");
        this.assetClass = in.readUTF("assetClass");
        this.issuer = in.readUTF("issuer");
    }

    public String toJSON(){
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Instrument that = (Instrument) o;

        if (!instrumentId.equals(that.instrumentId)) return false;
        if (!symbol.equals(that.symbol)) return false;
        if (product != null ? !product.equals(that.product) : that.product != null) return false;
        if (assetClass != null ? !assetClass.equals(that.assetClass) : that.assetClass != null) return false;
        return issuer != null ? issuer.equals(that.issuer) : that.issuer == null;
    }

    @Override
    public int hashCode() {
        int result = instrumentId.hashCode();
        result = 31 * result + symbol.hashCode();
        result = 31 * result + (product != null ? product.hashCode() : 0);
        result = 31 * result + (assetClass != null ? assetClass.hashCode() : 0);
        result = 31 * result + (issuer != null ? issuer.hashCode() : 0);
        return result;
    }
}

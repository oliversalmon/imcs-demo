package com.example.mu.domain;

import com.google.gson.Gson;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Portable;
import com.hazelcast.nio.serialization.PortableReader;
import com.hazelcast.nio.serialization.PortableWriter;

import java.io.IOException;
import java.util.Date;

/**
 * Created by oliverbuckley-salmon on 28/04/2017.
 */
public class Trade implements Portable {

    private String  tradeId
    ,               secondaryTradeId
    ,               firmTradeId
    ,               secondaryFirmTradeId
    ,               tradeType
    ,               secondaryTradeType
    ,               executionId;
    private Date    originalTradeDate;
    private String  executingFirmId
    ,               clientId
    ,               executionVenueId
    ,               executingTraderId
    ,               positionAccountId
    ,               instrumentId;
    private double  price;
    private int     quantity;
    private String  currency;
    private Date    tradeDate
    ,               settlementDate;
   
    public static final int FACTORY_ID = 1
    ,                       CLASS_ID =1;


    public Trade() {
    }

    public Trade(String tradeId, String secondaryTradeId, String firmTradeId, String secondaryFirmTradeId, String tradeType, String secondaryTradeType, String executionId, Date originalTradeDate, String executingFirmId, String clientId, String executionVenueId, String executingTraderId, String positionAccountId, String instrumentId, double price, int quantity, String currency, Date tradeDate, Date settlementDate) {
        this.tradeId = tradeId;
        this.secondaryTradeId = secondaryTradeId;
        this.firmTradeId = firmTradeId;
        this.secondaryFirmTradeId = secondaryFirmTradeId;
        this.tradeType = tradeType;
        this.secondaryTradeType = secondaryTradeType;
        this.executionId = executionId;
        this.originalTradeDate = originalTradeDate;
        this.executingFirmId = executingFirmId;
        this.clientId = clientId;
        this.executionVenueId = executionVenueId;
        this.executingTraderId = executingTraderId;
        this.positionAccountId = positionAccountId;
        this.instrumentId = instrumentId;
        this.price = price;
        this.quantity = quantity;
        this.currency = currency;
        this.tradeDate = tradeDate;
        this.settlementDate = settlementDate;
    }

    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

    public String getSecondaryTradeId() {
        return secondaryTradeId;
    }

    public void setSecondaryTradeId(String secondaryTradeId) {
        this.secondaryTradeId = secondaryTradeId;
    }

    public String getFirmTradeId() {
        return firmTradeId;
    }

    public void setFirmTradeId(String firmTradeId) {
        this.firmTradeId = firmTradeId;
    }

    public String getSecondaryFirmTradeId() {
        return secondaryFirmTradeId;
    }

    public void setSecondaryFirmTradeId(String secondaryFirmTradeId) {
        this.secondaryFirmTradeId = secondaryFirmTradeId;
    }

    public String getTradeType() {
        return tradeType;
    }

    public void setTradeType(String tradeType) {
        this.tradeType = tradeType;
    }

    public String getSecondaryTradeType() {
        return secondaryTradeType;
    }

    public void setSecondaryTradeType(String secondaryTradeType) {
        this.secondaryTradeType = secondaryTradeType;
    }

    public String getExecutionId() {
        return executionId;
    }

    public void setExecutionId(String executionId) {
        this.executionId = executionId;
    }

    public Date getOriginalTradeDate() {
        return originalTradeDate;
    }

    public void setOriginalTradeDate(Date originalTradeDate) {
        this.originalTradeDate = originalTradeDate;
    }

    public String getExecutingFirmId() {
        return executingFirmId;
    }

    public void setExecutingFirmId(String executingFirmId) {
        this.executingFirmId = executingFirmId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getExecutionVenueId() {
        return executionVenueId;
    }

    public void setExecutionVenueId(String executionVenueId) {
        this.executionVenueId = executionVenueId;
    }

    public String getExecutingTraderId() {
        return executingTraderId;
    }

    public void setExecutingTraderId(String executingTraderId) {
        this.executingTraderId = executingTraderId;
    }

    public String getPositionAccountId() {
        return positionAccountId;
    }
    
    public String getPositionAccountInstrumentKey() {
        return positionAccountId+"&"+instrumentId;
    }

    public void setPositionAccountId(String positionAccountId) {
        this.positionAccountId = positionAccountId;
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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Date getTradeDate() {
        return tradeDate;
    }

    public long getTradeTime() {
        return tradeDate.getTime();
    }
    public void setTradeDate(Date tradeDate) {
        this.tradeDate = tradeDate;
    }

    public Date getSettlementDate() {
        return settlementDate;
    }

    public void setSettlementDate(Date settlementDate) {
        this.settlementDate = settlementDate;
    }


    public int getFactoryId() {
        return FACTORY_ID;
    }

    public int getClassId() {
        return CLASS_ID;
    }


	public void writePortable(PortableWriter out) throws IOException {
        out.writeUTF("tradeId", tradeId);
        out.writeUTF("secondaryTradeId", secondaryTradeId);
        out.writeUTF("firmTradeId", firmTradeId);
        out.writeUTF("secondaryFirmTradeId", secondaryFirmTradeId);
        out.writeUTF("tradeType", tradeType);
        out.writeUTF("secondaryTradeType", secondaryTradeType);
        out.writeUTF("executionId", executionId);
        out.writeUTF("executingFirmId", executingFirmId);
        out.writeUTF("clientId", clientId);
        out.writeUTF("executionVenueId", executionVenueId);
        out.writeUTF("executingTraderId", executingTraderId);
        out.writeUTF("positionAccountId", positionAccountId);
        out.writeUTF("instrumentId", instrumentId);
        out.writeDouble("price", price);
        out.writeInt("quantity", quantity);
        out.writeUTF("currency", currency);
        ObjectDataOutput rawDataOutput = out.getRawDataOutput();
        rawDataOutput.writeObject(originalTradeDate);
        rawDataOutput.writeObject(tradeDate);
        rawDataOutput.writeObject(settlementDate);
    }


    public void readPortable(PortableReader in) throws IOException {
        this.tradeId = in.readUTF("tradeId");
        this.secondaryTradeId = in.readUTF("secondaryTradeId");
        this.firmTradeId = in.readUTF("firmTradeId");
        this.secondaryFirmTradeId = in.readUTF("secondaryFirmTradeId");
        this.tradeType = in.readUTF("tradeType");
        this.secondaryTradeType = in.readUTF("secondaryTradeType");
        this.executionId = in.readUTF("executionId");
        this.executingFirmId = in.readUTF("executingFirmId");
        this.clientId = in.readUTF("clientId");
        this.executionVenueId = in.readUTF("executionVenueId");
        this.executingTraderId = in.readUTF("executingTraderId");
        this.positionAccountId = in.readUTF("positionAccountId");
        this.instrumentId = in.readUTF("instrumentId");
        this.price = in.readDouble("price");
        this.quantity = in.readInt("quantity");
        this.currency = in.readUTF("currency");
        ObjectDataInput rawDataInput = in.getRawDataInput();
        this.originalTradeDate = rawDataInput.readObject();
        this.tradeDate = rawDataInput.readObject();
        this.settlementDate = rawDataInput.readObject();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Trade trade = (Trade) o;

        if (Double.compare(trade.price, price) != 0) return false;
        if (quantity != trade.quantity) return false;
        if (tradeId != null ? !tradeId.equals(trade.tradeId) : trade.tradeId != null) return false;
        if (secondaryTradeId != null ? !secondaryTradeId.equals(trade.secondaryTradeId) : trade.secondaryTradeId != null)
            return false;
        if (firmTradeId != null ? !firmTradeId.equals(trade.firmTradeId) : trade.firmTradeId != null) return false;
        if (secondaryFirmTradeId != null ? !secondaryFirmTradeId.equals(trade.secondaryFirmTradeId) : trade.secondaryFirmTradeId != null)
            return false;
        if (tradeType != null ? !tradeType.equals(trade.tradeType) : trade.tradeType != null) return false;
        if (secondaryTradeType != null ? !secondaryTradeType.equals(trade.secondaryTradeType) : trade.secondaryTradeType != null)
            return false;
        if (executionId != null ? !executionId.equals(trade.executionId) : trade.executionId != null) return false;
        if (originalTradeDate != null ? !originalTradeDate.equals(trade.originalTradeDate) : trade.originalTradeDate != null)
            return false;
        if (executingFirmId != null ? !executingFirmId.equals(trade.executingFirmId) : trade.executingFirmId != null)
            return false;
        if (clientId != null ? !clientId.equals(trade.clientId) : trade.clientId != null) return false;
        if (executionVenueId != null ? !executionVenueId.equals(trade.executionVenueId) : trade.executionVenueId != null)
            return false;
        if (executingTraderId != null ? !executingTraderId.equals(trade.executingTraderId) : trade.executingTraderId != null)
            return false;
        if (positionAccountId != null ? !positionAccountId.equals(trade.positionAccountId) : trade.positionAccountId != null)
            return false;
        if (instrumentId != null ? !instrumentId.equals(trade.instrumentId) : trade.instrumentId != null) return false;
        if (currency != null ? !currency.equals(trade.currency) : trade.currency != null) return false;
        if (tradeDate != null ? !tradeDate.equals(trade.tradeDate) : trade.tradeDate != null) return false;
        return settlementDate != null ? settlementDate.equals(trade.settlementDate) : trade.settlementDate == null;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = tradeId != null ? tradeId.hashCode() : 0;
        result = 31 * result + (secondaryTradeId != null ? secondaryTradeId.hashCode() : 0);
        result = 31 * result + (firmTradeId != null ? firmTradeId.hashCode() : 0);
        result = 31 * result + (secondaryFirmTradeId != null ? secondaryFirmTradeId.hashCode() : 0);
        result = 31 * result + (tradeType != null ? tradeType.hashCode() : 0);
        result = 31 * result + (secondaryTradeType != null ? secondaryTradeType.hashCode() : 0);
        result = 31 * result + (executionId != null ? executionId.hashCode() : 0);
        result = 31 * result + (originalTradeDate != null ? originalTradeDate.hashCode() : 0);
        result = 31 * result + (executingFirmId != null ? executingFirmId.hashCode() : 0);
        result = 31 * result + (clientId != null ? clientId.hashCode() : 0);
        result = 31 * result + (executionVenueId != null ? executionVenueId.hashCode() : 0);
        result = 31 * result + (executingTraderId != null ? executingTraderId.hashCode() : 0);
        result = 31 * result + (positionAccountId != null ? positionAccountId.hashCode() : 0);
        result = 31 * result + (instrumentId != null ? instrumentId.hashCode() : 0);
        temp = Double.doubleToLongBits(price);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + quantity;
        result = 31 * result + (currency != null ? currency.hashCode() : 0);
        result = 31 * result + (tradeDate != null ? tradeDate.hashCode() : 0);
        result = 31 * result + (settlementDate != null ? settlementDate.hashCode() : 0);
        return result;
    }

    public String toJSON(){
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}

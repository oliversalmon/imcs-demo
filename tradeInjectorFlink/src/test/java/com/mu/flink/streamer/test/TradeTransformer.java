package com.mu.flink.streamer.test;

import com.example.mu.domain.Trade;
import cucumber.api.TypeRegistry;
import cucumber.api.TypeRegistryConfigurer;
import io.cucumber.datatable.DataTableType;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class TradeTransformer implements TypeRegistryConfigurer {
    @Override
    public Locale locale() {
        return Locale.ENGLISH;
    }

    @Override
    public void configureTypeRegistry(TypeRegistry typeRegistry) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

        typeRegistry.defineDataTableType(new DataTableType(Trade.class,



                (Map<String, String> row) -> {
                            String tradeId = row.get("tradeId");
                            String firmTradeId = row.get("firmTradeId");
                            String tradeType = row.get("tradeType");
                            String executionId = row.get("executionId");
                            Date originalTradeDate = sdf.parse(row.get("originalTradeDate"));
                            String clientId = row.get("clientId");
                            String executionVenueId = row.get("executionVenueId");
                            String executingTraderId = row.get("executingTraderId");
                            String positionAccountId = row.get("positionAccountId");
                            String instrumentId = row.get("instrumentId");
                            Integer quantity = Integer.parseInt(row.get("quantity"));
                            Double price = Double.parseDouble(row.get("price"));
                            String currency = row.get("currency");
                            Date tradeDate = sdf.parse(row.get("tradeDate"));
                            Date settlementDate = sdf.parse(row.get("settlementDate"));

                            Trade atrade = new Trade();
                            atrade.setClientId(clientId);
                            atrade.setCurrency(currency);
                            atrade.setExecutingFirmId(firmTradeId);
                            atrade.setCurrency(currency);
                            atrade.setExecutingTraderId(tradeId);
                            atrade.setExecutionId(executionId);
                            atrade.setExecutionVenueId(executionVenueId);
                            atrade.setInstrumentId(instrumentId);
                            atrade.setOriginalTradeDate(originalTradeDate);
                            atrade.setPositionAccountId(positionAccountId);
                            atrade.setPrice(price);
                            atrade.setQuantity(quantity);
                            atrade.setSettlementDate(settlementDate);
                            atrade.setTradeDate(tradeDate);
                            atrade.setTradeType(tradeType);
                            atrade.setExecutingTraderId(executingTraderId);
                            atrade.setTradeId(tradeId);

                            return atrade;
                        }
                )
        );

    }
}

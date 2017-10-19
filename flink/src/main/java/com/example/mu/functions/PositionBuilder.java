package com.example.mu.functions;

import com.example.mu.domain.PositionAccount;
import com.example.mu.domain.Trade;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.configuration.Configuration;

public class PositionBuilder extends RichMapFunction<Trade, PositionAccount> {

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
    }

    @Override
    public void close() throws Exception {
        super.close();
    }

    @Override
    public PositionAccount map(Trade value) throws Exception {
        return null;
    }
}

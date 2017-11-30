package com.trade.injector.jto.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.trade.injector.jto.TradeReport;

public interface TradeReportRepository extends MongoRepository<TradeReport, String> {

}

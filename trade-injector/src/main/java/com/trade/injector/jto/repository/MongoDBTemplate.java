package com.trade.injector.jto.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.stereotype.Component;

@Component
public class MongoDBTemplate {


	private final MongoDbFactory dbFactory;
	
	@Autowired
	public MongoDBTemplate(MongoDbFactory mongo){
		this.dbFactory = mongo;
	}

	public MongoDbFactory getDbFactory() {
		return dbFactory;
	}
	
	
}

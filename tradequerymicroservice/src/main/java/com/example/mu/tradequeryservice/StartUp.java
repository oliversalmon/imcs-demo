package com.example.mu.tradequeryservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "com.example.mu.tradequeryservice")
@EnableDiscoveryClient
public class StartUp {

	public static void main(String[] args) {
		SpringApplication.run(StartUp.class, args);

	}

}

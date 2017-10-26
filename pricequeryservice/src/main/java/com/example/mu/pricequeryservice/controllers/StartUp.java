package com.example.mu.pricequeryservice.controllers;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "com.example.mu.pricequeryservice")
@EnableScheduling
public class StartUp {

	public static void main(String[] args) {
		SpringApplication.run(StartUp.class, args);

	}

}

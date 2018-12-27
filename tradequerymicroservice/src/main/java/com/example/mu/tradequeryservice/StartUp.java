package com.example.mu.tradequeryservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.example.mu.tradequeryservice")
public class StartUp {

	public static void main(String[] args) {
		SpringApplication.run(StartUp.class, args);

	}

}

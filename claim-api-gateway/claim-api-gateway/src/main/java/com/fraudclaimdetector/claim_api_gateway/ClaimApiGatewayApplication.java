package com.fraudclaimdetector.claim_api_gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@EnableDiscoveryClient
@SpringBootApplication
@ComponentScan(basePackages = "com.fraudclaimdetector.claim_api_gateway")  // ← add this line
public class ClaimApiGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClaimApiGatewayApplication.class, args);
	}

}

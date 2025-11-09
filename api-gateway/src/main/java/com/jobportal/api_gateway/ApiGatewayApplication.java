package com.jobportal.api_gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class ApiGatewayApplication {
	public static void main(String[] args) {
		//hello
		SpringApplication.run(ApiGatewayApplication.class, args);
		System.out.println("Started Api-gateway");
	}
}

package com.example.scg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;

import reactor.core.publisher.Mono;

@SpringBootApplication
public class ScgApplication {

	public static void main(String[] args) {
		SpringApplication.run(ScgApplication.class, args);
	}

	@Bean
	public KeyResolver keyResolver() {
		return exchange -> Mono.just(exchange.getRequest().getRemoteAddress().getAddress().getHostAddress());
	}

}

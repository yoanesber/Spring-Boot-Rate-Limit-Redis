package com.yoanesber.rate_limit_with_redis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RateLimitWithRedisApplication {

	public static void main(String[] args) {
		SpringApplication.run(RateLimitWithRedisApplication.class, args);
	}

}

package ru.akozadaev.user_finance_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableCaching
@EnableScheduling
@SpringBootApplication
public class UserFinanceServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserFinanceServiceApplication.class, args);
	}
}

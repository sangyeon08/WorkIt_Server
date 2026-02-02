package com.jubilee.workit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication(scanBasePackages = {"com.jubilee.workit"})
public class WorkitApplication {

	public static void main(String[] args) {
		SpringApplication.run(WorkitApplication.class, args);
		log.info("\n\n============= started =============\n\n");
	}
}